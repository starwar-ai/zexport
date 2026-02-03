package com.syj.eplus.module.pms.dal.mysql.spu;


import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.pms.controller.admin.spu.vo.SpuPageReqVO;
import com.syj.eplus.module.pms.dal.dataobject.spu.SpuDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 产品信息 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface SpuMapper extends BaseMapperX<SpuDO> {

    default PageResult<SpuDO> selectPage(SpuPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<SpuDO>()
                .likeIfPresent(SpuDO::getName, reqVO.getName())
                .eqIfPresent(SpuDO::getCode, reqVO.getCode())
                .eqIfPresent(SpuDO::getBarcode, reqVO.getBarcode())
                .eqIfPresent(SpuDO::getNameEng, reqVO.getNameEng())
                .eqIfPresent(SpuDO::getBrandId, reqVO.getBrandId())
                .eqIfPresent(SpuDO::getCategoryId, reqVO.getCategoryId())
                .eqIfPresent(SpuDO::getUnitType, reqVO.getUnitType())
                .eqIfPresent(SpuDO::getOnshelfFlag, reqVO.getOnshelfFlag())
                .eqIfPresent(SpuDO::getAuditStatus, reqVO.getAuditStatus())
                .eqIfPresent(SpuDO::getDescription, reqVO.getDescription())
                .eqIfPresent(SpuDO::getDescriptionEng, reqVO.getDescriptionEng())
                .eqIfPresent(SpuDO::getHsCodeId, reqVO.getHsCodeId())
                .eqIfPresent(SpuDO::getOwnBrandFlag, reqVO.getOwnBrandFlag())
                .eqIfPresent(SpuDO::getHsCode_var, reqVO.getHsCode_var())
//                .eqIfPresent(SpuDO::get)
                .betweenIfPresent(SpuDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(SpuDO::getId));
    }


}