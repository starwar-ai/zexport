package com.syj.eplus.module.dtms.convert.designsummary;

import com.syj.eplus.module.dtms.controller.admin.designsummary.vo.DesignSummaryRespVO;
import com.syj.eplus.module.dtms.controller.admin.designsummary.vo.DesignSummarySaveReqVO;
import com.syj.eplus.module.dtms.dal.dataobject.designsummary.DesignSummaryDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper
public interface DesignSummaryConvert {

        DesignSummaryConvert INSTANCE = Mappers.getMapper(DesignSummaryConvert.class);

        DesignSummaryRespVO convert(DesignSummaryDO designSummaryDO);

        default DesignSummaryRespVO convertDesignSummaryRespVO(DesignSummaryDO designSummaryDO){
                DesignSummaryRespVO designSummaryRespVO = convert(designSummaryDO);
                return designSummaryRespVO;
        }

    DesignSummaryDO convertDesignSummaryDO(DesignSummarySaveReqVO saveReqVO);
}
