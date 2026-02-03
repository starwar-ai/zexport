package com.syj.eplus.module.pms.controller.admin.sku.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import lombok.Data;

import java.util.List;

/**
 * @Description：
 * @Author：du
 * @Date：2024/5/10 17:41
 */
@Data
public class SimpleOwnBrandSkuPageReqVO extends PageParam {

    private Long categoryId;

    private String skuCode;

    private String skuName;

    private String cskuCode;

    private String oskuCode;

    private String custCode;

    private Integer allProduct;

    private Integer skuType;

    private List<String> skuCodeList;

    private String basicSkuCode;

    private Integer agentFlag;

    /**
     * 传参为1时，只查询自营产品，不管客户编号
     * 传参不为1或者不传参时，会根据客户产品替换对应的自营产品
     */
//    private Integer ownBrandonOnlyFlag;
}
