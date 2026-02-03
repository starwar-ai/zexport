package com.syj.eplus.module.crm.service.mark;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.crm.controller.admin.mark.vo.MarkPageReqVO;
import com.syj.eplus.module.crm.controller.admin.mark.vo.MarkSaveReqVO;
import com.syj.eplus.module.crm.dal.dataobject.mark.MarkDO;
import com.syj.eplus.module.crm.dal.mysql.mark.MarkMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.crm.enums.ErrorCodeConstants.MARK_NOT_EXISTS;

/**
 * 唛头 Service 实现类
 *
 * @author du
 */
@Service
@Validated
public class MarkServiceImpl implements MarkService {

    @Resource
    private MarkMapper markMapper;

    @Override
    public Long createMark(MarkSaveReqVO createReqVO) {
        // 插入
        MarkDO mark = BeanUtils.toBean(createReqVO, MarkDO.class);
        markMapper.insert(mark);
        // 返回
        return mark.getId();
    }

    @Override
    public void updateMark(MarkSaveReqVO updateReqVO) {
        // 校验存在
//        validateMarkExists(updateReqVO.getId());
        // 更新
        MarkDO updateObj = BeanUtils.toBean(updateReqVO, MarkDO.class);
        markMapper.updateById(updateObj);
    }

    @Override
    public void deleteMark(Long id) {
        // 校验存在
        validateMarkExists(id);
        // 删除
        markMapper.deleteById(id);
    }

    @Override
    public void deleteMarkByCustId(Long custId) {
        // 删除
        markMapper.delete(MarkDO::getCustId, custId);
    }

    private void validateMarkExists(Long id) {
        if (markMapper.selectById(id) == null) {
            throw exception(MARK_NOT_EXISTS);
        }
    }

    @Override
    public MarkDO getMark(Long id) {
        return markMapper.selectById(id);
    }

    @Override
    public PageResult<MarkDO> getMarkPage(MarkPageReqVO pageReqVO) {
        return markMapper.selectPage(pageReqVO);
    }

    @Override
    public void batchSaveMark(List<MarkDO> markSaveReqDOList) {
        if (CollUtil.isEmpty(markSaveReqDOList)) {
            return;
        }
        markMapper.insertBatch(markSaveReqDOList);
    }

    @Override
    public List<MarkDO> getMarkListByCustId(Long custId) {
        return markMapper.selectList(new LambdaQueryWrapperX<MarkDO>().eq(MarkDO::getCustId, custId));
    }

    @Override
    public void batchDeleteMarkList(List<Long> idList) {
        markMapper.deleteBatchIds(idList);
    }

}