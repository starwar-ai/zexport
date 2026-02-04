package com.syj.eplus.module.scm.entity;

import cn.iocoder.yudao.framework.common.pojo.JsonEffectRange;
import cn.iocoder.yudao.framework.mybatis.core.exinterface.ChangeExInterface;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.syj.eplus.framework.common.entity.JsonAmount;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: du
 * @Date: 2024/08/05/16:41
 * @Description:
 */
@Data
@Accessors(chain = false)
@Schema(description = "加减项")
public class PurchaseAddSubTermChange implements ChangeExInterface {

    @Schema(description = "主键")
    @TableId
    private Long id;

    @Schema(description = "合同编号")
    private String contractCode;

    @Schema(description = "加/减项")
    private Integer calculationType;

    @Schema(description = "费用名称")
    private String feeName;

    @Schema(description = "金额")
    private JsonAmount amount;

    @Schema(description = "旧加/减项")
    private Integer old_calculationType;

    @Schema(description = "旧费用名称")
    private String old_feeName;

    @Schema(description = "旧金额")
    private String old_amount;

    private Integer changeFlag;

    @TableField(exist = false)
    private String sourceCode;

    @TableField(exist = false)
    private List<JsonEffectRange> effectRangeList;
}
