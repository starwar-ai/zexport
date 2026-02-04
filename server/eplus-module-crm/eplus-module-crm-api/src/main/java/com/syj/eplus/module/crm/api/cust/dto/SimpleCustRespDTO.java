package com.syj.eplus.module.crm.api.cust.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.syj.eplus.framework.common.config.handler.LongListTypeHandler;
import com.syj.eplus.framework.common.entity.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName(value = "crm_cust", autoResultMap = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimpleCustRespDTO {
    /**
     * 主键
     */
    private Long id;
    /**
     * 企业名称
     */
    private String name;
    /**
     * 客户编号
     */
    private String code;
    /**
     * 国外客户标志
     */
    private Integer abroadFlag;

    /**
     * 币种
     */
    private String currency;
    /**
     * 价格条款
     */
    private String settlementTermType;
    /**
     * 币种
     */
    private List<BaseValue> currencyList;
    /**
     * 客户类型
     */
    @TableField(typeHandler = LongListTypeHandler.class)
    private List<Long> customerTypes;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 银行信息列表
     */
    private List<BankAccount> bankAccountList;

    /**
     * 联系人
     */
    private List<CustPocDTO> custPocList;

    /**
     * 公司路径
     */
    @TableField(exist = false)
    private List<CompanyPath> companyPath;

    /**
     * 国家
     */
    private Long countryId;

    /**
     * 国家名称
     */
    private String countryName;

    /**
     * 区域名称
     */
    private String regionName;

    /**
     * 关联客户编号
     */
    private List<String> custLinkCode;
    /**
     * 关联客户信息
     */
    @TableField(exist = false)
    private List<SimpleData> custLink;

    private List<Long> managerIds;

    private List<UserDept> managerList;

    @TableField(exist = false)
    private List<SettlementDTO> settlementList;

    /**
     * 是否内部企业
     */
    private Integer internalFlag;


    private List<AddressShippingDTO> addressShippingList;

    private CollectionAccountDTO collectionAccount;

    /**
     * 是否代理  0:否， 1：是
     */
    private Integer agentFlag;

    /**
     * 客户分类名称
     */
    private String customerTypesName;


    /**
     * 启用信用额度：0：不启用，1：启用
     */
    private Integer creditFlag;

    /**
     * 阶段类型
     */
    private Integer stageType;
}