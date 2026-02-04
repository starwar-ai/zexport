package com.syj.eplus.module.crm.controller.admin.custsettlement.vo;

import cn.iocoder.yudao.framework.common.pojo.JsonEffectRange;
import cn.iocoder.yudao.framework.excel.core.annotations.DictFormat;
import cn.iocoder.yudao.framework.excel.core.convert.DictConvert;
import cn.iocoder.yudao.framework.mybatis.core.exinterface.ChangeExInterface;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Schema(description = "管理后台 - 客户结汇方式 Response VO")
@Data
@ExcelIgnoreUnannotated

@Accessors(chain = false)
public class CustSettlementRespVO implements ChangeExInterface {

    @Schema(description = "结汇方式编号")
    @ExcelProperty("结汇方式编号")
    private Long settlementId;

    @Schema(description = "结汇名称")
    @ExcelProperty("结汇名称")
    private String name;

    @Schema(description = "结汇英文名称")
    @ExcelProperty("结汇英文名称")
    private String nameEng;

    @Schema(description = "是否缺省")
    @ExcelProperty(value = "是否缺省", converter = DictConvert.class)
    @DictFormat("confirm_type")
    private Integer defaultFlag;

    @Schema(description = "id")
    private Long id;

    @Schema(description = "变更标识")
    private Integer changeFlag;

    private String sourceCode;

    private List<JsonEffectRange> effectRangeList;
}