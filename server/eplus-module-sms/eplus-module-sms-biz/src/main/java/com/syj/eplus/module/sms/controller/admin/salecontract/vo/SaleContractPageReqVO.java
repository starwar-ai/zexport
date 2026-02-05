package com.syj.eplus.module.sms.controller.admin.salecontract.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

import com.syj.eplus.framework.common.entity.ExportContext;
import com.syj.eplus.framework.common.enums.SaleContractStatusEnum;

@Schema(description = "管理后台 - 外销合同分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SaleContractPageReqVO extends PageParam implements ExportContext.ExportPageParam {

    @Schema(description = "编号")
    private String code;


    @Schema(description = "产品编号")
    private String skuCode;

    @Schema(description = "客户货号")
    private String cskuCode;

    @Schema(description = "基础产品编号")
    private String basicSkuCode;

    @Schema(description = "中文品名")
    private String name;

    @Schema(description = "英文品名")
    private String nameEng;

    @Schema(description = "确认状态，默认1（已确认）")
    private Integer confirmFlag = 1;

    @Schema(description = "内部法人单位id", example = "12365")
    private Long companyId;

    @Schema(description = "客户名称")
    private String custName;

    @Schema(description = "销售合同类型")
    private Integer saleType;

    @Schema(description = "审核状态")
    private Integer auditStatus;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "自动生成")
    private Integer autoFlag;

    @Schema(description = "id数组")
    private Long[] idList;

    @Schema(description = "状态数组")
    private Integer[] statusList;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "客户编号列表")
    private List<String> custCodeList;

    @Schema(description = "客户编号")
    private String custCode;

    @Schema(description = "客户PO编号")
    private String custPo;

    @Schema(description = "排除状态（不等于），默认排除结案")
    private Integer neStatus = SaleContractStatusEnum.CASE_CLOSED.getValue();

    @Schema(description = "销售员Id", example = "王五")
    private Long salesId;

    @Schema(description = "销售员部门Id", example = "王五")
    private Long salesDeptId;

    private String salesDeptName;

    @Schema(description = "国家编号" )
    private Long countryId;

    @Schema(description = "客户国别", example = "12365")
    private Long custCountryId;

    @Schema(description = "销售合同日期")
    private LocalDateTime[] saleContractDate;

    @Schema(description = "是否树形结构")
    private Boolean isTree;

    @Schema(description = "导出时是否需要子表数据")
    private Boolean needChildren;

    @Schema(description = "回签日期")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] signBackDate;

    @Schema(description = "是否优势产品")
    private Integer advantageFlag;

    @Schema(description = "查询模式：1-单据模式（按合同分页），2-产品模式（按明细分页）")
    private Integer queryMode;

    @Schema(description = "优势产品SKU ID列表（内部使用，由 advantageFlag 查询填充）")
    private List<Long> advantageSkuIds;

    @Schema(description = "优势产品SKU Code列表（内部使用，由 advantageFlag 查询填充）")
    private List<String> advantageSkuCodes;

    @Schema(description = "客户来源")
    private Integer custSourceType;

    /**
     * 自定义 setter：null 值不覆盖默认值
     * 确保前端传入 null 时仍使用默认的已确认状态
     */
    public void setConfirmFlag(Integer confirmFlag) {
        if (confirmFlag != null) {
            this.confirmFlag = confirmFlag;
        }
    }

    /**
     * 自定义 setter：null 值不覆盖默认值
     * 确保前端传入 null 时仍使用默认的排除结案条件
     */
    public void setNeStatus(Integer neStatus) {
        if (neStatus != null) {
            this.neStatus = neStatus;
        }
    }
}