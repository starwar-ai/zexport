package com.syj.eplus.framework.common.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode
public class CreatedResponse {

    public CreatedResponse(Long _id,String _code){
        id = _id;
        code = _code;
    }

    /**
     * 主键
     */
    private Long id;

    /**
     * 编号
     */
    private String code;

}
