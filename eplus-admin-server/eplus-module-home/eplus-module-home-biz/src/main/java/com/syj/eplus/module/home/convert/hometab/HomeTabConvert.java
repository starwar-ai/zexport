package com.syj.eplus.module.home.convert.hometab;

import com.syj.eplus.module.home.controller.admin.hometab.vo.HomeTabRespVO;
import com.syj.eplus.module.home.controller.admin.hometab.vo.HomeTabSaveReqVO;
import com.syj.eplus.module.home.dal.dataobject.hometab.HomeTabDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface HomeTabConvert {

    HomeTabConvert INSTANCE = Mappers.getMapper(HomeTabConvert.class);

    HomeTabRespVO convert(HomeTabDO homeTabDO);

    HomeTabDO convert(HomeTabSaveReqVO saveReqVO);

    List<HomeTabRespVO> convertList(List<HomeTabDO> homeTabDOList);

}