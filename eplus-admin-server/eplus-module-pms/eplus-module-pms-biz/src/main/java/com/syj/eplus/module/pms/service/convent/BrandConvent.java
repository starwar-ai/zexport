package com.syj.eplus.module.pms.service.convent;

import com.syj.eplus.module.pms.controller.admin.brand.vo.BrandRespVO;
import com.syj.eplus.module.pms.controller.admin.brand.vo.BrandSimpleRespVO;
import com.syj.eplus.module.pms.dal.dataobject.brand.BrandDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @Description：
 * @Author：du
 * @Date：2024/2/7 14:59
 */
@Mapper
public interface BrandConvent {
    BrandConvent INSTANCE = Mappers.getMapper(BrandConvent.class);

    BrandRespVO convert(BrandDO brandDO);

    BrandSimpleRespVO convertToSimple(BrandDO brandDO);

    List<BrandSimpleRespVO> convertToSimpleList(List<BrandDO> brandDOList);

}
