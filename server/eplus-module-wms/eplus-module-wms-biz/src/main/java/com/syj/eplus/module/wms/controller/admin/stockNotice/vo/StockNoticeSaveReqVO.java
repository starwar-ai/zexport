package com.syj.eplus.module.wms.controller.admin.stockNotice.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.syj.eplus.framework.common.entity.JsonWeight;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.module.wms.dal.dataobject.stockNotice.StockNoticeItemDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 仓储管理-入(出)库通知单新增/修改 Request VO")
@Data
public class StockNoticeSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "16203")
    private Long id;

    @Schema(description = "单号", requiredMode = Schema.RequiredMode.REQUIRED)
    //@NotEmpty(message = "单号不能为空")
    private String code;

    @Schema(description = "通知类型 1-入库通知单、2-出库通知单", example = "1")
    @NotNull(message = "通知类型不能为空")
    private Integer noticeType;

    @Schema(description = "通知单状态 1-未转 2-已转 3-作废", example = "1")
    private Integer noticeStatus;

    @Schema(description = "通知时间")
    private LocalDateTime noticeTime;

    @Schema(description = "预计到/出货日期")
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern=FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND,timezone = "GMT+8")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime expectDate;

    @Schema(description = "归属公司主键", example = "5879")
    private Long companyId;

    @Schema(description = "归属公司名称", example = "赵六")
    private String companyName;

    @Schema(description = "总体积(cm³)")
    private BigDecimal totalVolume;

    @Schema(description = "总毛重（{数量,单位}）")
    private JsonWeight totalWeight;

    @Schema(description = "打印状态 0-未打印 1-已打印")
    private Integer printFlag;

    @Schema(description = "打印次数")
    private Integer printTimes;

    @Schema(description = "备注", example = "你说的对")
    private String remark;

    @Schema(description = "仓储管理-入(出)库通知单-明细列表")
    private List<StockNoticeItemDO> noticeItems;

    @Schema(description = "进仓日期")
    private LocalDateTime inboundDate;

    @Schema(description = "提单号")
    private String referenceNumber;

    @Schema(description = "是否拉柜通知单")
    private Integer isContainerTransportation;

    @Schema(description = "申请人")
    private UserDept applyer;

    @Schema(description = "销售合同编号列表")
    private List<String> saleContractCodeList;

    @Schema(description = "采购合同编号列表")
    private List<String> purContractCodeList;

    @Schema(description = "链路编号")
    private List<String> linkCodeList;

    @Schema(description = "出运单号")
    private String shipmentCode;

    @Schema(description = "出运发票号")
    private String invoiceCode;

    @Schema(description = "出运方式")
    private Integer shipmentType;

    @Schema(description = "是否工厂出库")
    private Integer factoryOutboundFlag;

    @Schema(description = "是否提交")
    private Integer submitFlag;

    @Schema(description = "手动标识")
    private Integer manualFlag;

    @Schema(description = "审核状态")
    private Integer auditStatus;
}
