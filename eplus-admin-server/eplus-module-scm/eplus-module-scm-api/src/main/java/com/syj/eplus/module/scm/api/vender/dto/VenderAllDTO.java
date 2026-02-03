package com.syj.eplus.module.scm.api.vender.dto;

import com.sun.xml.bind.v2.TODO;
import com.syj.eplus.framework.common.entity.BaseValue;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.SimpleFile;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class VenderAllDTO {

    /**
     * 主键
     */ 
    private Long id;
    /**
     * 版本
     */
    private Integer ver;
    /**
     * 供应商编码
     */
    private String code;
    /**
     * 供应商名称
     */
    private String name;
    /**
     * 供应商用英文名称
     */
    private String nameEng;
    /**
     * 供应商简称
     */
    private String nameShort;
    /**
     * 注册资本
     */
    private String registeredCapital;
    /**
     * 法定代表人
     */
    private String legalPerson;
    /**
     * 主营业务
     */
    private List<Long> businessScope;
    /**
     * 应付供应商
     */
    private List<String> venderLinkCode;
    /**
     * 公司所在地区
     */
    private Integer companyAreaId;
    /**
     * 工厂所在地区
     */
    private Integer factoryAreaId;
    /**
     * 工厂地址
     */
    private String factoryAddress;
    /**
     * 供应商类型
     */
    private Integer venderType;
    /**
     * 供应商级别
     */
    private Integer venderLevel;

    /**
     * 公司地址
     */
    private String companyAddress;
    /**
     * 营业执照号
     */
    private String licenseNo;
    /**
     * 企业电话
     */
    private String phone;
    /**
     * 是否境外供应商
     */
    private Integer abroadFlag;
    /**
     * 国家编号
     */
    private Long countryId;
    /**
     * 币种
     */
    private String currency;
    /**
     * 税率
     */
    private BigDecimal taxRate;
    /**
     * 发票类型
     */
    private Integer taxType;
    /**
     * 付款方式
     */
    private Long paymentId;
    /**
     * 审核状态
     * <p>
     * 枚举 {@link TODO auditStatus 对应的类}
     */
    private Integer auditStatus;
    /**
     * 采购员
     */
    private List<Long> buyerIds;
    private List<BaseValue> buyerList;

    /**
     * 备注
     */
    private String remark;

    /**
     * 快递所在城市
     */
    private Integer deliveryAreaId;

    /**
     * 快递地址
     */
    private String deliveryAddress;

    /**
     *供应商阶段
     */
    private Integer stageType;

    /**
     * 转正标识
     */
    private Integer convertFlag;

    /**
     * 转正时间
     */
    private LocalDateTime convertTime;

    /**
     * 是否启用
     */
    private Integer enableFlag;

    /**
     * 附件
     */
    private List<SimpleFile> annex;

    /**
     * 传真
     */
    private String fax;

    /**
     * 创建时间
     */

    private  LocalDateTime createTime;

    /**
     * 费用归属金额
     */
    private JsonAmount amount;
    /**
     * 费用标签
     */
    private Integer descId;
}
