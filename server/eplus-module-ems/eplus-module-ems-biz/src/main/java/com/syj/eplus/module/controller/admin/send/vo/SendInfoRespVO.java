package com.syj.eplus.module.controller.admin.send.vo;

import cn.iocoder.yudao.module.system.api.logger.dto.OperateLogRespDTO;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.syj.eplus.module.controller.admin.sendproduct.vo.SendProductRespVO;
import com.syj.eplus.module.oa.api.feeshare.dto.FeeShareRespDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - 寄件 Response VO")
@Data
@ExcelIgnoreUnannotated
public class SendInfoRespVO extends  SendRespVO {

     @Schema(description = "费用归集")
     private List<FeeShareRespDTO> feeShare;

     @Schema(description = "产品列表")
     private List<SendProductRespVO> children;

     @Schema(description = "操作日志")
     private List<OperateLogRespDTO> operateLogRespDTOList;

     @Schema(description = "任务id")
     private String processInstanceId;
}