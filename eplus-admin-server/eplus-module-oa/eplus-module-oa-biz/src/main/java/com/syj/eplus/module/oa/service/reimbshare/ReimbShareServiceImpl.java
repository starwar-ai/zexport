package com.syj.eplus.module.oa.service.reimbshare;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.framework.common.enums.FeeShareSourceTypeEnum;
import com.syj.eplus.module.crm.api.cust.CustApi;
import com.syj.eplus.module.crm.api.cust.dto.SimpleCustRespDTO;
import com.syj.eplus.module.oa.controller.admin.reimbshare.vo.ReimbSharePageReqVO;
import com.syj.eplus.module.oa.controller.admin.reimbshare.vo.ReimbShareRespVO;
import com.syj.eplus.module.oa.controller.admin.reimbshare.vo.ReimbShareSaveReqVO;
import com.syj.eplus.module.oa.convert.reimbshare.ReimbShareConvert;
import com.syj.eplus.module.oa.dal.dataobject.reimbshare.ReimbShareDO;
import com.syj.eplus.module.oa.dal.mysql.reimbshare.ReimbShareMapper;
import com.syj.eplus.module.oa.dict.AuxiliaryTypeDict;
import com.syj.eplus.module.oa.service.feeshare.FeeShareService;
import com.syj.eplus.module.scm.api.vender.VenderApi;
import com.syj.eplus.module.scm.api.vender.dto.SimpleVenderRespDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.oa.enums.ErrorCodeConstants.CUST_INFO_NOT_FIND;
import static com.syj.eplus.module.oa.enums.ErrorCodeConstants.REIMBER_SHARE_NOT_NULL;


/**
 * 报销分摊 Service 实现类
 *
 * @author ePlus
 */
@Service
@Validated
public class ReimbShareServiceImpl implements ReimbShareService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private ReimbShareMapper reimbShareMapper;

    @Lazy
    @Resource
    private FeeShareService feeShareService;

    @Resource
    private CustApi custApi;
    @Resource
    private VenderApi venderApi;

    @Override
    public Long createReimbShare(ReimbShareSaveReqVO createReqVO) {
        ReimbShareDO reimbShare = ReimbShareConvert.INSTANCE.convertReimbShareDO(createReqVO);
        // 插入
        reimbShareMapper.insert(reimbShare);
        // 返回
        return reimbShare.getReimbShareId();
    }

    @Override
    public void updateReimbShare(ReimbShareSaveReqVO updateReqVO) {
        // 校验存在
        validateReimbShareExists(updateReqVO.getReimbShareId());
        // 更新
        ReimbShareDO updateObj = ReimbShareConvert.INSTANCE.convertReimbShareDO(updateReqVO);
        reimbShareMapper.updateById(updateObj);
    }

    @Override
    public void deleteReimbShare(Long id) {
        // 校验存在
        validateReimbShareExists(id);
        // 删除
        reimbShareMapper.deleteById(id);
    }

    private void validateReimbShareExists(Long id) {
        if (reimbShareMapper.selectById(id) == null) {
            throw exception(REIMBER_SHARE_NOT_NULL);
        }
    }

    @Override
    public ReimbShareRespVO getReimbShare(Long id) {
        ReimbShareDO reimbShareDO = reimbShareMapper.selectById(id);
        if (reimbShareDO == null) {
            return null;
        }
        return ReimbShareConvert.INSTANCE.convertReimbShareRespVO(reimbShareDO);
    }

    @Override
    public PageResult<ReimbShareDO> getReimbSharePage(ReimbSharePageReqVO pageReqVO) {
        return reimbShareMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ReimbShareRespVO> getReimbShareByReimbId(Long reimbId, Integer auxiliaryType) {
        List<ReimbShareDO> reimbShareDOList = reimbShareMapper.selectList(new LambdaQueryWrapperX<ReimbShareDO>().eq(ReimbShareDO::getReimbId, reimbId));
        if (CollUtil.isEmpty(reimbShareDOList)) {
            return Collections.emptyList();
        }
        return dealAuxiliary(reimbShareDOList.get(0).getAuxiliaryType(), reimbShareDOList);
    }

    @Override
    public void batchSaveReimbShare(List<ReimbShareSaveReqVO> shareSaveReqVOList) {
        List<ReimbShareDO> reimbShareDOList = ReimbShareConvert.INSTANCE.convertReimbShareDOList(shareSaveReqVOList);
        if (CollUtil.isEmpty(reimbShareDOList)) {
            return;
        }
        // 插入
        reimbShareMapper.insertBatch(reimbShareDOList);
        // 返回
    }

    @Override
    public void deleteReimbShareByReimbId(Long reimbId, FeeShareSourceTypeEnum feeShareSourceTypeEnum) {
        reimbShareMapper.delete(ReimbShareDO::getReimbId, reimbId);
        feeShareService.deleteById(feeShareSourceTypeEnum.getValue(),reimbId);
    }

    @Override
    public void batchUpdateReimbShare(List<ReimbShareSaveReqVO> updateReqVO) {
        if (CollUtil.isEmpty(updateReqVO)) {
            return;
        }
        updateReqVO.forEach(s -> {
            validateReimbShareExists(s.getReimbShareId());
        });
        List<ReimbShareDO> reimbShareDOList = ReimbShareConvert.INSTANCE.convertReimbShareDOList(updateReqVO);
        reimbShareMapper.updateBatch(reimbShareDOList);
    }

    @Override
    public void dealReimbShare(List<ReimbShareSaveReqVO> auxiliaryList, Long reimbId) {
        reimbShareMapper.delete(ReimbShareDO::getReimbId, reimbId);
        if (CollUtil.isEmpty(auxiliaryList)) {
            return;
        }
        auxiliaryList.forEach(s -> {
            s.setReimbId(reimbId);
        });
        batchSaveReimbShare(auxiliaryList);
    }

    /**
     * 处理费用归属
     *
     * @param auxiliaryType 费用归属类型
     * @param auxiliaryList 费用归属对象列表
     */
    private List<ReimbShareRespVO> dealAuxiliary(Integer auxiliaryType, List<ReimbShareDO> auxiliaryList) {
        if (Objects.isNull(auxiliaryType) || CollUtil.isEmpty(auxiliaryList)) {
            return Collections.emptyList();
        }
        List<ReimbShareRespVO> ReimbShareRespVOList = BeanUtils.toBean(auxiliaryList, ReimbShareRespVO.class);
        switch (auxiliaryType) {
            case AuxiliaryTypeDict.CUST -> {
                ReimbShareRespVOList = dealCustAuxiliary(auxiliaryList);
            }
            case AuxiliaryTypeDict.VENDER -> {
                ReimbShareRespVOList = dealVenderAuxiliary(auxiliaryList);

            }
            default -> {
            }
        }
        return ReimbShareRespVOList;
    }

    /**
     * 处理客户费用归属
     *
     * @return 客户精简列表
     */
    private List<ReimbShareRespVO> dealCustAuxiliary(List<ReimbShareDO> auxiliaryList) {
        List<Long> custIdList = auxiliaryList.stream().map(ReimbShareDO::getAuxiliaryId).toList();
        List<SimpleCustRespDTO> simpleCustRespDTOList = custApi.getSimpleCustRespDTO(custIdList);
        if (CollUtil.isEmpty(simpleCustRespDTOList)) {
            throw exception(CUST_INFO_NOT_FIND);
        }
        Map<Long, SimpleCustRespDTO> simpleCustRespDTOMap = simpleCustRespDTOList.stream().collect(Collectors.toMap(SimpleCustRespDTO::getId, simpleCustRespDTO -> simpleCustRespDTO));
        List<ReimbShareRespVO> ReimbShareRespVOList = new ArrayList<>();
        auxiliaryList.forEach(reimbShareDO -> {
            SimpleCustRespDTO simpleCustRespDTO = simpleCustRespDTOMap.get(reimbShareDO.getAuxiliaryId());
            ReimbShareRespVOList.add(BeanUtils.toBean(reimbShareDO, ReimbShareRespVO.class).setCustRespDTO(simpleCustRespDTO));
        });
        return ReimbShareRespVOList;
    }

    private List<ReimbShareRespVO> dealVenderAuxiliary(List<ReimbShareDO> auxiliaryList) {
        List<Long> venderIdList = auxiliaryList.stream().map(ReimbShareDO::getAuxiliaryId).toList();
        List<SimpleVenderRespDTO> simpleVenderRespDTOList = venderApi.getSimpleVenderRespDTO(venderIdList);
        if (CollUtil.isEmpty(simpleVenderRespDTOList)) {
            return null;
        }
        Map<Long, SimpleVenderRespDTO> simpleVenderRespDTOMap = simpleVenderRespDTOList.stream().collect(Collectors.toMap(SimpleVenderRespDTO::getId, simpleVenderRespDTO -> simpleVenderRespDTO));
        List<ReimbShareRespVO> ReimbShareRespVOList = new ArrayList<>();
        auxiliaryList.forEach(reimbShareDO -> {
            SimpleVenderRespDTO simpleVenderRespDTO = simpleVenderRespDTOMap.get(reimbShareDO.getAuxiliaryId());
            ReimbShareRespVOList.add(BeanUtils.toBean(reimbShareDO, ReimbShareRespVO.class).setVenderRespDTO(simpleVenderRespDTO));
        });
        return ReimbShareRespVOList;
    }
}