package com.syj.eplus.module.oa.service.reimbrepaydetail;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.framework.common.enums.RepayStatus;
import com.syj.eplus.module.oa.controller.admin.reimbrepaydetail.vo.ReimbRepayDetailPageReqVO;
import com.syj.eplus.module.oa.controller.admin.reimbrepaydetail.vo.ReimbRepayDetailRespVO;
import com.syj.eplus.module.oa.controller.admin.reimbrepaydetail.vo.ReimbRepayDetailSaveReqVO;
import com.syj.eplus.module.oa.convert.reimbrepaydetail.ReimbRepayDetailConvert;
import com.syj.eplus.module.oa.dal.dataobject.reimbrepaydetail.ReimbRepayDetailDO;
import com.syj.eplus.module.oa.dal.mysql.reimbrepaydetail.ReimbRepayDetailMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.oa.enums.ErrorCodeConstants.REIMB_REPAY_DETAIL_NOT_EXISTS;

/**
 * 报销还款详情 Service 实现类
 *
 * @author ePlus
 */
@Service
@Validated
public class ReimbRepayDetailServiceImpl implements ReimbRepayDetailService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private ReimbRepayDetailMapper reimbRepayDetailMapper;


    @Override
    public Long createReimbRepayDetail(ReimbRepayDetailSaveReqVO createReqVO) {
        ReimbRepayDetailDO reimbRepayDetail = ReimbRepayDetailConvert.INSTANCE.convertReimbRepayDetailDO(createReqVO);
        // 插入
        reimbRepayDetailMapper.insert(reimbRepayDetail);
        // 返回
        return reimbRepayDetail.getId();
    }

    @Override
    public void batchCreateReimbRepayDetail(List<ReimbRepayDetailSaveReqVO> createReqVOList) {
        List<ReimbRepayDetailDO> reimbRepayDetailDOS = ReimbRepayDetailConvert.INSTANCE.convertReimbRepayDetailDOList(createReqVOList);
        reimbRepayDetailMapper.insertBatch(reimbRepayDetailDOS);
    }

    @Override
    public void updateReimbRepayDetail(ReimbRepayDetailSaveReqVO updateReqVO) {
        // 校验存在
        validateReimbRepayDetailExists(updateReqVO.getId());
        // 更新
        ReimbRepayDetailDO updateObj = ReimbRepayDetailConvert.INSTANCE.convertReimbRepayDetailDO(updateReqVO);
        reimbRepayDetailMapper.updateById(updateObj);
    }

    @Override
    public void deleteReimbRepayDetail(Long id) {
        // 校验存在
        validateReimbRepayDetailExists(id);
        // 删除
        reimbRepayDetailMapper.deleteById(id);
    }

    private void validateReimbRepayDetailExists(Long id) {
        if (reimbRepayDetailMapper.selectById(id) == null) {
            throw exception(REIMB_REPAY_DETAIL_NOT_EXISTS);
        }
    }

    @Override
    public ReimbRepayDetailRespVO getReimbRepayDetail(Long id) {
        ReimbRepayDetailDO reimbRepayDetailDO = reimbRepayDetailMapper.selectById(id);
        if (reimbRepayDetailDO == null) {
            return null;
        }
        return ReimbRepayDetailConvert.INSTANCE.convertReimbRepayDetailRespVO(reimbRepayDetailDO);
    }

    @Override
    public PageResult<ReimbRepayDetailDO> getReimbRepayDetailPage(ReimbRepayDetailPageReqVO pageReqVO) {
        return reimbRepayDetailMapper.selectPage(pageReqVO);
    }

    @Override
    public Map<Long, ReimbRepayDetailRespVO> getReimbRepayDetailMap(List<Long> ids, Long travelReimbId) {
        LambdaQueryWrapperX<ReimbRepayDetailDO> queryWrapperX = new LambdaQueryWrapperX<ReimbRepayDetailDO>().in(ReimbRepayDetailDO::getLoanId, ids)
                .eq(ReimbRepayDetailDO::getRepayStatus, RepayStatus.NOT_REPAID.getStatus());
        if (Objects.nonNull(travelReimbId)) {
            queryWrapperX.ne(ReimbRepayDetailDO::getReimbId, travelReimbId);
        }
        List<ReimbRepayDetailDO> reimbRepayDetailDOList = reimbRepayDetailMapper.selectList(queryWrapperX);
        if (CollUtil.isEmpty(reimbRepayDetailDOList)) {
            return Collections.emptyMap();
        }
        // TODO 考虑是否一条借款单对应多个报销还款详情  进行分组
        return reimbRepayDetailDOList.stream().collect(Collectors.toMap(ReimbRepayDetailDO::getLoanId, ReimbRepayDetailConvert.INSTANCE::convert));
    }

    @Override
    public List<ReimbRepayDetailDO> getReimbRepayDetailListWithOutCancel(Long id) {
        return reimbRepayDetailMapper.selectList(new LambdaQueryWrapperX<ReimbRepayDetailDO>().eq(ReimbRepayDetailDO::getLoanId, id).ne(ReimbRepayDetailDO::getRepayStatus, RepayStatus.CANCEL.getStatus()));
    }

    @Override
    public List<ReimbRepayDetailDO> getReimbRepayDetailListByReimbId(Long id) {
        return reimbRepayDetailMapper.selectList(new LambdaQueryWrapperX<ReimbRepayDetailDO>().eq(ReimbRepayDetailDO::getReimbId, id));
    }

    @Override
    public void batchDeleteReimbRepayDetailByReimbId(Long reimbId) {
        if (Objects.isNull(reimbId)){
            return;
        }
        reimbRepayDetailMapper.delete(ReimbRepayDetailDO::getReimbId, reimbId);
    }

    @Override
    public Map<Long, List<ReimbRepayDetailDO>> batchGetReimbRepayDetailMap(Collection<Long> reimbIdList) {
        if (CollUtil.isEmpty(reimbIdList)){
            return Map.of();
        }
        List<ReimbRepayDetailDO> reimbRepayDetailDOS = reimbRepayDetailMapper.selectList(new LambdaQueryWrapperX<ReimbRepayDetailDO>().in(ReimbRepayDetailDO::getReimbId, reimbIdList));
        if (CollUtil.isEmpty(reimbRepayDetailDOS)){
            return Map.of();
        }
         return reimbRepayDetailDOS.stream().collect(Collectors.groupingBy(ReimbRepayDetailDO::getReimbId));
    }

    @Override
    public void batchUpdateReimbRepayDetail(List<ReimbRepayDetailDO> reimbRepayDetailDOList) {
        if (CollUtil.isEmpty(reimbRepayDetailDOList)){
            return;
        }
        reimbRepayDetailMapper.updateBatch(reimbRepayDetailDOList);
    }
}