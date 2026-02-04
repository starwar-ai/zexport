package com.syj.eplus.module.dms.controller.admin.shipment.vo.change;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 出运变更分页查询请求VO
 *
 * @Author: du
 * @Date: 2024/08/30/18:35
 * @Description: 出运变更列表页面搜索条件
 */
@Data
public class ChangeShipmentPageReq extends PageParam {
    
    @Schema(description = "客户货号")
    private String cskuCode;

    @Schema(description = "基础产品编号")
    private String basicSkuCode;

    @Schema(description = "产品编号")
    private String skuCode;
    
    @Schema(description = "发票号")
    private String invoiceCode;
    
    @Schema(description = "客户名称")
    private String custName;
    
    @Schema(description = "单证员ID")
    private Long orderManagerId;
    
    @Schema(description = "单证员姓名")
    private String orderManagerName;
    
    @Schema(description = "产品名称")
    private String skuName;
}
