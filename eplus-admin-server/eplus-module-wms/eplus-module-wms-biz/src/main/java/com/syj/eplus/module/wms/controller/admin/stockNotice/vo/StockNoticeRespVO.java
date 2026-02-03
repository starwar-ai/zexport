package com.syj.eplus.module.wms.controller.admin.stockNotice.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.syj.eplus.framework.common.entity.JsonWeight;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.module.wms.controller.admin.stockNoticeItem.vo.StockNoticeItemRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 仓储管理-入(出)库通知单 Response VO")
@Data
@ExcelIgnoreUnannotated
public class StockNoticeRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "16203")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "单号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("单号")
    private String code;

    @Schema(description = "通知类型 1-入库通知单、2-出库通知单", example = "1")
    @ExcelProperty("通知类型 1-入库通知单、2-出库通知单")
    private Integer noticeType;

    @Schema(description = "通知单状态 1-未转 2-已转 3-作废", example = "1")
    @ExcelProperty("通知单状态 1-未转 2-已转 3-作废")
    private Integer noticeStatus;

    @Schema(description = "通知时间")
    @ExcelProperty("通知时间")
    private LocalDateTime noticeTime;

    @Schema(description = "销售合同主键", example = "828")
    @ExcelProperty("销售合同主键")
    private Long saleContractId;

    @ExcelProperty("销售合同号")
    @Schema(description = "销售合同编号列表")
    private List<String> saleContractCodeList;

    @Schema(description = "预计到/出货日期")
    @ExcelProperty("预计到/出货日期")
    private LocalDateTime expectDate;

    @Schema(description = "归属公司主键", example = "5879")
    @ExcelProperty("归属公司主键")
    private Long companyId;

    @Schema(description = "归属公司名称", example = "赵六")
    @ExcelProperty("归属公司名称")
    private String companyName;

    @Schema(description = "总体积(cm³)")
    @ExcelProperty("总体积(cm³)")
    private BigDecimal totalVolume;

    @Schema(description = "总毛重（{数量,单位}）")
    @ExcelProperty("总毛重（{数量,单位}）")
    private JsonWeight totalWeight;

    @Schema(description = "打印状态 0-未打印 1-已打印")
    @ExcelProperty("打印状态 0-未打印 1-已打印")
    private Integer printFlag;

    @Schema(description = "打印次数")
    @ExcelProperty("打印次数")
    private Integer printTimes;

    @Schema(description = "备注", example = "你说的对")
    @ExcelProperty("备注")
    private String remark;

    private UserDept createUser;

    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    private List<StockNoticeItemRespVO> stockNoticeItemRespVOList;

    @Schema(description = "进仓日期")
    private LocalDateTime inboundDate;

    @Schema(description = "提单号")
    private String referenceNumber;

    @Schema(description = "是否拉柜通知单")
    private Integer isContainerTransportation;

    @Schema(description = "待入库数量")
    private Integer pendingStockQuantity;

    @Schema(description = "申请人")
    private UserDept applyer;

    private List<StockNoticeItemRespVO> children;

    @Schema(description = "出运发票号")
    private String invoiceCode;

    @Schema(description = "已转入库单数量")
    private Integer transformStockQuantity;

    @Schema(description = "出运方式")
    private Integer shipmentType;

    @Schema(description = "审核状态")
    private Integer auditStatus;

    @Schema(description = "流程实例id")
    private String processInstanceId;

    @Schema(description = "流程定义id")
    private String processDefinitionId;
}
