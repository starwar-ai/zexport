package com.syj.eplus.module.oa.api.feeshare.dto;

import com.syj.eplus.framework.common.entity.JsonAmount;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 *
 * @author zhangcm
 */
@Data
@Accessors(chain = true)
public class FeeShareDescItemDTO   {

    public FeeShareDescItemDTO(String type, String name, JsonAmount amount,Long dId,String dName){
        feeShareType = type;
        feeShareName =  name;
        feeShareAmount = amount;
        descId = dId;
        descName = dName;
    }
    private String feeShareType;
    private String feeShareName;
    private JsonAmount feeShareAmount;

    private Long descId;
    private String descName;
}
