package com.syj.eplus.module.dms.controller.admin.shipment.vo;

import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.module.dms.dal.dataobject.forwarderfee.ForwarderFeeDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - 出运单新增/修改 Request VO")
@Data
public class ShipmentForwarderFeeSaveReqVO {

    @Schema(description = "出运明细主键")
    private Long shipmentId;

    @Schema(description = "船代费用列表")
    private List<ForwarderFeeDO> forwarderFeeList;

    @Schema(description = "采购员")
    private List<UserDept> purchaseUserList;

    @Schema(description = "业务员")
    private List<UserDept> managerList;
}