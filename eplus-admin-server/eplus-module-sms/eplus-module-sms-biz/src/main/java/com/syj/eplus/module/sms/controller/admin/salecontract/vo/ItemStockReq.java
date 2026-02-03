package com.syj.eplus.module.sms.controller.admin.salecontract.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: du
 * @Date: 2024/08/12/21:16
 * @Description:
 */
@Data
public class ItemStockReq extends PageParam {
    private String saleContractCode;

    private String skuCode;

    private String cskuCode;

}
