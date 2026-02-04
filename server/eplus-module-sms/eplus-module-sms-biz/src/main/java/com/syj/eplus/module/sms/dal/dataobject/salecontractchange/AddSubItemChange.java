package com.syj.eplus.module.sms.dal.dataobject.salecontractchange;

import cn.iocoder.yudao.framework.common.pojo.JsonEffectRange;
import cn.iocoder.yudao.framework.mybatis.core.exinterface.ChangeExInterface;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.syj.eplus.framework.common.config.handler.JsonAmountTypeHandler;
import com.syj.eplus.framework.common.entity.JsonAmount;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @Description：
 * @Author：du
 * @Date：2024/7/2 17:30
 */
@Data
@Accessors(chain = false)
@Schema(description = "加减项")
public class AddSubItemChange implements ChangeExInterface {
    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 合同id
     */
    private Long contractId;
    /**
     * 加/减项
     */
    private Integer calculationType;
    /**
     * 费用名称
     */
    private String feeName;

    /**
     * 金额
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount amount;

    /**
     * 变更标识
     */
    @TableField(exist = false)
    private Integer changeFlag;

    /**
     * 旧 加/减项
     */
    private Integer old_calculationType;

    /**
     * 旧费用名称
     */
    private String old_feeName;

    /**
     * 旧金额
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount old_amount;

    @TableField(exist = false)
    private String sourceCode;

    @TableField(exist = false)
    private List<JsonEffectRange> effectRangeList;
}
