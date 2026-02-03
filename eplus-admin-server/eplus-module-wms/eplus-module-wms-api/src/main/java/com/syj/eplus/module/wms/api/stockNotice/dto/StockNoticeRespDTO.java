package com.syj.eplus.module.wms.api.stockNotice.dto;

import com.syj.eplus.framework.common.entity.JsonWeight;
import com.syj.eplus.framework.common.entity.UserDept;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Desc——采购合同转入库通知单-请求主表
 * Create by Rangers at  2024-06-11 22:13
 */
@Data
public class StockNoticeRespDTO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "16203")
    private Long id;

    @Schema(description = "单号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String code;

    @Schema(description = "通知类型 1-入库通知单、2-出库通知单", example = "1")
    private Integer noticeType;

    @Schema(description = "通知单状态 1-未转 2-已转 3-作废", example = "1")
    private Integer noticeStatus;

    @Schema(description = "通知时间")
    private LocalDateTime noticeTime;

    @Schema(description = "采购合同主键", example = "6613")
    private Long purchaseContractId;

    @Schema(description = "采购合同号")
    private String purchaseContractCode;

    @Schema(description = "销售合同主键", example = "828")
    private Long saleContractId;

    @Schema(description = "销售合同号")
    private String saleContractCode;

    @Schema(description = "仓库主键", example = "10095")
    private Long warehouseId;

    @Schema(description = "仓库名称", example = "王五")
    private String warehouseName;

    @Schema(description = "预计到/出货日期")
    private LocalDateTime expectDate;

    @Schema(description = "采购员主键", example = "14452")
    private Long purchaserId;

    private String purchaseUserName;

    private String purchaseMobile;

    @Schema(description = "采购员部门主键", example = "11021")
    private Long purchaserDeptId;

    private String purchaseDeptName;

    @Schema(description = "销售人员主键", example = "10937")
    private Long salesId;

    private String salesName;

    @Schema(description = "销售员部门主键", example = "31806")
    private Long salesDeptId;

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

    private String createUserName;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    private List<StockNoticeItemRespDTO> children;

    @Schema(description = "进仓日期")
    private LocalDateTime inboundDate;

    @Schema(description = "提单号")
    private String referenceNumber;

    @Schema(description = "是否拉柜通知单")
    private Integer isContainerTransportation;

    @Schema(description = "申请人")
    private UserDept applyer;

}
