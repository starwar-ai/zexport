package com.syj.eplus.module.oa.controller.admin.travelreimb.vo;

import cn.iocoder.yudao.module.system.api.logger.dto.OperateLogRespDTO;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.module.oa.api.dto.SimpleLoanAppRespDTO;
import com.syj.eplus.module.oa.api.feeshare.dto.FeeShareRespDTO;
import com.syj.eplus.module.oa.controller.admin.apply.vo.ApplyRespVO;
import com.syj.eplus.module.oa.controller.admin.reimb.vo.ReimbRespVO;
import com.syj.eplus.module.oa.entity.JsonReimbDetail;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @Description：
 * @Author：du
 * @Date：2024/4/23 9:51
 */
@Schema(description = "管理后台 - 出差报销详情 Response VO")
@Data
@ExcelIgnoreUnannotated
public class TravelReimbRespVO extends ReimbRespVO {
    @Schema(description = "交通费")
    private List<JsonReimbDetail> trafficFeeList;

    @Schema(description = "住宿费")
    private List<JsonReimbDetail> accommodationFeeList;

    @Schema(description = "自驾")
    private List<JsonReimbDetail> selfDrivingList;

    @Schema(description = "其他")
    private List<JsonReimbDetail> otherDescList;

    @Schema(description = "出差补贴")
    private List<JsonReimbDetail> travelAllowanceList;

    @Schema(description = "发票")
    private List<SimpleFile> invoiceList;

    @Schema(description = "出差申请单")
    private List<ApplyRespVO> applyList;

    @Schema(description = "借款单列表")
    private List<SimpleLoanAppRespDTO> app;

    @Schema(description = "费用归属")
    private List<FeeShareRespDTO> feeShare;

    @Schema(description = "费用归属状态")
    private Integer auxiliaryStatus;

    @Schema(description = "费用用途（正式）")
    private String expenseUseToFormal;

    @Schema(description = "费用用途（发生）")
    private String expenseUseToOccur;

    @Schema(description = "科目主键")
    private Long financialSubjectId;

    @Schema(description = "科目名称")
    private String financialSubjectName;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "操作日志")
    private List<OperateLogRespDTO> operateLogRespDTOList;
}
