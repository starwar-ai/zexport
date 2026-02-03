package com.syj.eplus.module.pms.dal.mysql.brand;


import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.pms.controller.admin.brand.vo.BrandPageReqVO;
import com.syj.eplus.module.pms.dal.dataobject.brand.BrandDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 品牌 Mapper
 *
 * @author ePlus
 */
@Mapper
public interface BrandMapper extends BaseMapperX<BrandDO> {

    default PageResult<BrandDO> selectPage(BrandPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<BrandDO>()
                .likeIfPresent(BrandDO::getName, reqVO.getName())
                .eqIfPresent(BrandDO::getNameEng, reqVO.getNameEng())
                .eqIfPresent(BrandDO::getBrandType, reqVO.getBrandType())
                .eqIfPresent(BrandDO::getCustId, reqVO.getCustId())
                .eqIfPresent(BrandDO::getCustCode, reqVO.getCustCode())
                .likeIfPresent(BrandDO::getCustName, reqVO.getCustName())
                .eqIfPresent(BrandDO::getLogo, reqVO.getLogo())
                .eqIfPresent(BrandDO::getDescription, reqVO.getDescription())
                .eqIfPresent(BrandDO::getDescriptionEng, reqVO.getDescriptionEng())
                .eqIfPresent(BrandDO::getOwnBrandFlag, reqVO.getOwnBrandFlag())
                .betweenIfPresent(BrandDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(BrandDO::getId));
    }

}