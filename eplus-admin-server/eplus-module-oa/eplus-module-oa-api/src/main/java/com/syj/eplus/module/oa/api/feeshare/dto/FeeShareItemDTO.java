package com.syj.eplus.module.oa.api.feeshare.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import com.syj.eplus.framework.common.entity.JsonAmount;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 *
 * @author zhangcm
 */
@Data
@Accessors(chain = true)
public class FeeShareItemDTO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 费用归属id
     */
    private Long shareFeeId;
    /**
     * 费用归属对象类型
     */
    private Integer businessSubjectType;
    /**
     * 费用归属对象编号
     */
    private String businessSubjectCode;

    /**
     * 编号
     */
    private String code;

    /**
     * 金额
     */
    private JsonAmount amount;

    /**
     * 费用标签
     */
    private Integer descId;

}
