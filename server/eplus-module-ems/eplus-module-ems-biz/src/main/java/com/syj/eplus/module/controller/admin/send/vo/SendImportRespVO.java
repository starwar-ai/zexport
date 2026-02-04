package com.syj.eplus.module.controller.admin.send.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.syj.eplus.module.controller.admin.sendbill.vo.SendBillRespVO;
import com.syj.eplus.module.dal.dataobject.send.SendDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - 寄件 Response VO")
@Data
@ExcelIgnoreUnannotated
public class SendImportRespVO  {

     @Schema(description = "完成数量")
     private Integer quantity;

     @Schema(description = "完成数据")
     private List<SendDO> list;

     @Schema(description = "已录入待确认数量")
     private Integer CompareQuantity;

     @Schema(description = "已录入待确认列表")
     private List<SendCompareRespVO> CompareList;

     @Schema(description = "已结算不允许操作的数量")
     private Integer doneQuantity;

     @Schema(description = "已结算不允许操作的数据")
     private List<SendDO> doneList;

     @Schema(description = "系统缺少订单的数量")
     private Integer missQuantity;

     @Schema(description = "系统缺少订单的数据")
     private List<SendBillRespVO> missList;

}