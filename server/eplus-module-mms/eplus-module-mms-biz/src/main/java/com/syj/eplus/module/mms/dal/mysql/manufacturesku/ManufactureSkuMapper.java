package com.syj.eplus.module.mms.dal.mysql.manufacturesku;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.mms.controller.admin.manufacturesku.vo.ManufactureSkuPageReqVO;
import com.syj.eplus.module.mms.dal.dataobject.manufacturesku.ManufactureSkuDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 加工单产品 Mapper
 *
 * @author zhangcm
 */
@Mapper
public interface ManufactureSkuMapper extends BaseMapperX<ManufactureSkuDO> {

    default PageResult<ManufactureSkuDO> selectPage(ManufactureSkuPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ManufactureSkuDO>()
                .eqIfPresent(ManufactureSkuDO::getManufactureId, reqVO.getManufactureId())
                .eqIfPresent(ManufactureSkuDO::getSkuId, reqVO.getSkuId())
                .eqIfPresent(ManufactureSkuDO::getSkuCode, reqVO.getSkuCode())
                .eqIfPresent(ManufactureSkuDO::getCskuCode, reqVO.getCskuCode())
                .likeIfPresent(ManufactureSkuDO::getSkuName, reqVO.getSkuName())
                .eqIfPresent(ManufactureSkuDO::getQuantity, reqVO.getQuantity())
                .eqIfPresent(ManufactureSkuDO::getMainPicture, reqVO.getMainPicture())
                .eqIfPresent(ManufactureSkuDO::getSmsContractId, reqVO.getSmsContractId())
                .eqIfPresent(ManufactureSkuDO::getSmsContractCode, reqVO.getSmsContractCode())
                .betweenIfPresent(ManufactureSkuDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ManufactureSkuDO::getId));
    }

}