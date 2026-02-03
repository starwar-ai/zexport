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
public class SimpleSkuPageReqVO extends PageParam {


    private Long categoryId;
    private Integer changeFlag;
    private Integer changeStatus;
    private Integer ver;
    private String skuCode;
    private String skuName;
    private String cskuCode;
    private String oskuCode;

    private Integer custProFlag;

    private  String custCode;

    private String venderCode;

    private Integer ownBrandFlag;
    private Integer onshelfFlag;

    private Integer autoCreateFlag;
    private List<String> skuCodeList;

    private Integer purchaseUserId;
    private Integer skuType;
    private List<Integer> skuTypeInList;
    private List<Integer> skuTypeOutList;
    private Integer auxiliarySkuFlag;
    private String basicSkuCode;
    private Integer agentFlag;
}
