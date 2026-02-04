package com.syj.eplus.module.wms.service.adjustment;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.syj.eplus.module.wms.controller.admin.adjustment.vo.AdjustmentDetailReq;
import com.syj.eplus.module.wms.controller.admin.adjustment.vo.AdjustmentPageReqVO;
import com.syj.eplus.module.wms.controller.admin.adjustment.vo.AdjustmentRespVO;
import com.syj.eplus.module.wms.controller.admin.adjustment.vo.AdjustmentSaveReqVO;
import com.syj.eplus.module.wms.controller.admin.adjustmentitem.vo.AdjustmentItemRespVO;
import com.syj.eplus.module.wms.controller.admin.adjustmentitem.vo.AdjustmentItemSaveReqVO;
import com.syj.eplus.module.wms.convert.adjustment.AdjustmentConvert;
import com.syj.eplus.module.wms.dal.dataobject.adjustment.AdjustmentDO;
import com.syj.eplus.module.wms.dal.dataobject.adjustment.AdjustmentItemDO;
import com.syj.eplus.module.wms.dal.mysql.adjustment.AdjustmentItemMapper;
import com.syj.eplus.module.wms.dal.mysql.adjustment.AdjustmentMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.wms.enums.ErrorCodeConstants.ADJUSTMENT_ITEM_NOT_EXISTS;
import static com.syj.eplus.module.wms.enums.ErrorCodeConstants.ADJUSTMENT_NOT_EXISTS;

/**
 * 仓储管理-盘库调整单 Service 实现类
 *
 * @author ePlus
 */
@Service
@Validated
public class AdjustmentServiceImpl implements AdjustmentService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private AdjustmentMapper adjustmentMapper;
    @Resource
    private AdjustmentItemMapper adjustmentItemMapper;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createAdjustment(AdjustmentSaveReqVO createReqVO) {
        AdjustmentDO adjustment = AdjustmentConvert.INSTANCE.convertAdjustmentDO(createReqVO);
        // 插入
        adjustmentMapper.insert(adjustment);
        // 插入子表
        createAdjustmentItemList(adjustment.getId(), createReqVO.getAdjustmentItemList());
        // 返回
        return adjustment.getId();
    }

    private boolean createAdjustmentItemList(Long adjustmentId, List<AdjustmentItemSaveReqVO> list) {
        list.forEach(o -> o.setId(null).setAdjustmentId(adjustmentId));
        List<AdjustmentItemDO> itemDOList = BeanUtils.toBean(list, AdjustmentItemDO.class);
        return adjustmentItemMapper.insertBatch(itemDOList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAdjustment(AdjustmentSaveReqVO updateReqVO) {
        // 校验存在
        validateAdjustmentExists(updateReqVO.getId());
        // 更新
        AdjustmentDO updateObj = AdjustmentConvert.INSTANCE.convertAdjustmentDO(updateReqVO);
        adjustmentMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAdjustment(Long id) {
        // 校验存在
        validateAdjustmentExists(id);
        // 删除
        adjustmentMapper.deleteById(id);

        // 删除子表
        deleteAdjustmentItemByAdjustmentId(id);
    }

    private void validateAdjustmentExists(Long id) {
        if (adjustmentMapper.selectById(id) == null) {
            throw exception(ADJUSTMENT_NOT_EXISTS);
        }
    }

    @Override
    public AdjustmentRespVO getAdjustment(AdjustmentDetailReq adjustmentDetailReq) {
        Long adjustmentId = adjustmentDetailReq.getAdjustmentId();
        if (Objects.isNull(adjustmentId)) {
            logger.error("[仓储管理-盘库调整单]未获取到仓储管理-盘库调整单id");
            return null;
        }
        AdjustmentDO adjustmentDO = adjustmentMapper.selectById(adjustmentId);
        if (adjustmentDO == null) {
            return null;
        }
        AdjustmentRespVO respVO = BeanUtils.toBean(adjustmentDO, AdjustmentRespVO.class);
        QueryWrapper<AdjustmentItemDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("adjustment_id", adjustmentId);
        List<AdjustmentItemDO> itemDOList = adjustmentItemMapper.selectList(queryWrapper);
        if (!CollectionUtils.isEmpty(itemDOList)) {
            List<AdjustmentItemRespVO> itemRespVOS = BeanUtils.toBean(itemDOList, AdjustmentItemRespVO.class);
            respVO.setAdjustmentItems(itemRespVOS);
        }
        return respVO;
    }

    @Override
    public PageResult<AdjustmentDO> getAdjustmentPage(AdjustmentPageReqVO pageReqVO) {
        return adjustmentMapper.selectPage(pageReqVO);
    }

    // ==================== 子表（仓储管理-盘库调整单-明细） ====================

    @Override
    public PageResult<AdjustmentItemDO> getAdjustmentItemPage(PageParam pageReqVO, Long adjustmentId) {
        return adjustmentItemMapper.selectPage(pageReqVO, adjustmentId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createAdjustmentItem(AdjustmentItemDO adjustmentItem) {
        adjustmentItemMapper.insert(adjustmentItem);
        return adjustmentItem.getId();
    }

    @Override
    public void updateAdjustmentItem(AdjustmentItemDO adjustmentItem) {
        // 校验存在
        validateAdjustmentItemExists(adjustmentItem.getId());
        // 更新
        adjustmentItemMapper.updateById(adjustmentItem);
    }

    @Override
    public void deleteAdjustmentItem(Long id) {
        // 校验存在
        validateAdjustmentItemExists(id);
        // 删除
        adjustmentItemMapper.deleteById(id);
    }

    @Override
    public AdjustmentItemDO getAdjustmentItem(Long id) {
        return adjustmentItemMapper.selectById(id);
    }

    private void validateAdjustmentItemExists(Long id) {
        if (adjustmentItemMapper.selectById(id) == null) {
            throw exception(ADJUSTMENT_ITEM_NOT_EXISTS);
        }
    }

    private void deleteAdjustmentItemByAdjustmentId(Long adjustmentId) {
        adjustmentItemMapper.deleteByAdjustmentId(adjustmentId);
    }
}
