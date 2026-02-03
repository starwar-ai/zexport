package com.syj.eplus.module.pms.service.convent;

import com.syj.eplus.module.pms.controller.admin.hsdata.vo.HsdataRespVO;
import com.syj.eplus.module.pms.controller.admin.hsdata.vo.HsdataSimpleRespVO;
import com.syj.eplus.module.pms.dal.dataobject.hsdata.HsdataDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @Description：
 * @Author：du
 * @Date：2024/2/7 14:17
 */
@Mapper
public interface HsdataConvent {
    HsdataConvent INSTANCE = Mappers.getMapper(HsdataConvent.class);


    HsdataRespVO convert(HsdataDO hsdataDO);

    HsdataSimpleRespVO convertToSimple(HsdataDO hsdataDO);


    List<HsdataSimpleRespVO> convertToSimpleList(List<HsdataDO> hsdataDOList);

}
