package com.syj.eplus.module.mms.dal.mysql.manufactureskuitem;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.mms.controller.admin.manufactureskuitem.vo.ManufactureSkuItemPageReqVO;
import com.syj.eplus.module.mms.dal.dataobject.manufactureskuitem.ManufactureSkuItemDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 加工单子产品 Mapper
 *
 * @author zhangcm
 */
@Mapper
public interface ManufactureSkuItemMapper extends BaseMapperX<ManufactureSkuItemDO> {

    default PageResult<ManufactureSkuItemDO> selectPage(ManufactureSkuItemPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ManufactureSkuItemDO>()
                .eqIfPresent(ManufactureSkuItemDO::getManufactureSkuId, reqVO.getManufactureSkuId())
                .eqIfPresent(ManufactureSkuItemDO::getManufactureId, reqVO.getManufactureId())
                .eqIfPresent(ManufactureSkuItemDO::getSkuId, reqVO.getSkuId())
                .eqIfPresent(ManufactureSkuItemDO::getSkuCode, reqVO.getSkuCode())
                .eqIfPresent(ManufactureSkuItemDO::getCskuCode, reqVO.getCskuCode())
                .likeIfPresent(ManufactureSkuItemDO::getSkuName, reqVO.getSkuName())
                .eqIfPresent(ManufactureSkuItemDO::getQuantity, reqVO.getQuantity())
                .eqIfPresent(ManufactureSkuItemDO::getRatio, reqVO.getRatio())
                .eqIfPresent(ManufactureSkuItemDO::getMainPicture, reqVO.getMainPicture())
                .betweenIfPresent(ManufactureSkuItemDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ManufactureSkuItemDO::getId));
    }

}