package com.syj.eplus.module.qms.api.dto;

import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.SimpleFile;
import lombok.Data;

import java.time.LocalDateTime;


@Data
public class QualityInspectionItemRespDTO {


    private Long id;

    //验货单主键
    private Long inspectionId;

    //产品主键
    private Long skuId;

    //产品编号
    private String skuCode;

    //产品中文名称
    private String skuName;

    //客户货号
    private String cskuCode;

    //图片
    private SimpleFile mainPicture;

    //缩略图
    private String thumbnail;

    //验货状态:1:待定 2：成功，3：失败，4：待验货 5：放行通过
    private Integer inspectionStatus;

    //失败描述
    private String failDesc;

    //上次失败描述
    private String lastFailDesc;

    //待定描述
    private String pendingDesc;

    //数量
    private Integer quantity;

    //验货费用
    private JsonAmount amount;

    //产品总价
    private JsonAmount totalPrice;

    //采购合同号
    private String purchaseContractCode;

    //采购员主键
    private Long purchaseUserId;

    //采购员姓名
    private String purchaseUserName;

    //采购员部门主键
    private Long purchaseUserDeptId;

    //采购员部门名称
    private String purchaseUserDeptName;

    //销售合同号
    private String saleContractCode;

    //销售员主键
    private Long saleUserId;

    //销售员姓名
    private String saleUserName;

    //销售员部门主键
    private Long saleUserDeptId;

    //销售员部门名称
    private String saleUserDeptName;

    //跟单员主键
    private Long trackUserId;

    //跟单员姓名
    private String trackUserName;

    //跟单员部门主键
    private Long trackUserDeptId;

    //跟单员部门名称
    private String trackUserDeptName;

    //客户主键
    private Long custId;

    //客户编码
    private String custCode;

    //客户名称
    private String custName;

    //外箱装量
    private Integer qtyPerOuterbox;

    //内盒装量
    private Integer qtyPerInnerbox;

    //箱数
    private Integer boxCount;

    //备注
    private String remark;

    //处理标识:0-否 1-是
    private Integer handleFlag;

    //创建时间
    private LocalDateTime createTime;

}
