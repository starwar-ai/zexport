package com.syj.eplus.module.crm.api.cust.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.sun.xml.bind.v2.TODO;
import com.syj.eplus.framework.common.config.handler.LongListTypeHandler;
import com.syj.eplus.framework.common.entity.BaseValue;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.SimpleFile;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CustAllDTO extends SimpleCustRespDTO {
    /**
     * 主键
     */
    private Long id;
    /**
     * 版本号
     */
    private Integer ver;
    /**
     * 企业名称
     */
    private String name;
    /**
     * 简称
     */
    private String shortname;
    /**
     * 客户编号
     */
    private String code;
    /**
     * 国家编码
     */
    private Long countryId;
    /**
     * 官网
     */
    private String homepage;
    /**
     * 电子邮件
     */
    private String email;
    /**
     * 客户类型（电商,进口商,零售商,贸易商,批发商,售后公司,邮购商）
     * <p>
     * 枚举 {@link com.syj.eplus.module.crm.enums.cust.CustomTypeEnum}
     */
    private List<Long> customerTypes;
    /**
     * 客户阶段（潜在客户，正式客户，退休客户）
     * <p>
     * 枚举 {@link com.syj.eplus.module.crm.enums.cust.CustomerStageEnum}
     */
    private Integer stageType;
    /**
     * 币种
     */
    private List<BaseValue> currencyList;
    /**
     * 运输方式(海运、陆运、空运、供应商送货)
     * <p>
     * 枚举 {@link com.syj.eplus.module.crm.enums.cust.TransportTypeEnum}
     */
    private Integer transportType;
    /**
     * 营业地址
     */
    private String address;
    /**
     * 联系电话
     */
    private String phone;
    /**
     * 国外客户标志
     */
    private Integer abroadFlag;
    /**
     * 客户来源
     */
    private Integer sourceType;
    /**
     * 启用信用额度：0：不启用，1：启用
     */
    private Integer creditFlag;
    /**
     * 信用额度
     */
    private JsonAmount creditLimit;
    /**
     * 是否是中信保：0：否，1：是
     */
    private Integer zxbquotaFlag;
    /**
     * 价格条款  CIF,FOB,CIP,DDP,DAP,DAT,CFR
     */
    private String settlementTermType;
    /**
     * 开票抬头
     */
    private String invoiceHeader;
    /**
     * 税率
     */
    private BigDecimal taxRate;
    /**
     * 是否代理  0:否， 1：是
     */
    private Integer agentFlag;
    /**
     * 处理状态
     * <p>
     * 枚举 {@link TODO auditStatus 对应的类}
     */
    private Integer auditStatus;
    /**
     * 寄件地址
     */

    private List<Object> addressShipping;
    /**
     * 备用信息
     */
    private String remark;
    /**
     * 业务员
     */
    @TableField(typeHandler = LongListTypeHandler.class)
    private List<Long> managerIds;

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
     * 图片
     */

    private List<SimpleFile> picture;

    /**
     * 关联客户
     */

    private List<String> custLinkCode;

    /**
     * 创建时间
     */

    private  LocalDateTime createTime;
    /**
     * 变更前客户信息
     */
    private CustAllDTO oldCust;

    /**
     * 费用归属金额
     */
    private JsonAmount amount;
    /**
     * 正面唛头
     */
    private String mainMark;
    /**
     * 侧面唛头
     */
    private String sideMark;

    private String countryName;
    private String regionName;
    /**
     * 费用标签
     */
    private Integer descId;

    /**
     * 收货人
     */
    private String receivePerson;
    /**
     * 通知人
     */
    private String notifyPerson;
}