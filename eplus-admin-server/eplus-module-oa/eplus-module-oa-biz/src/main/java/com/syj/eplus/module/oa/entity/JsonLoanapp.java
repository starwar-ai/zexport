package com.syj.eplus.module.oa.entity;

import com.syj.eplus.framework.common.entity.JsonAmount;
import lombok.Data;

/**
 * @Description：出差报销明细中借款json映射实体
 * @Author：du
 * @Date：2024/2/21 17:22
 */
@Data
public class JsonLoanapp extends JsonAmount {

    /**
     * 借款单id
     */
    private Long id;
}
