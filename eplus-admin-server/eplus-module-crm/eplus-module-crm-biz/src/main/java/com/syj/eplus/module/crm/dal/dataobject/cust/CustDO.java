package com.syj.eplus.module.crm.dal.dataobject.cust;

import cn.iocoder.yudao.framework.common.pojo.JsonEffectRange;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.framework.mybatis.core.exinterface.ChangeExInterface;
import cn.iocoder.yudao.module.system.inface.ModelKeyHolder;
import com.baomidou.mybatisplus.annotation.*;
import com.sun.xml.bind.v2.TODO;
import com.syj.eplus.framework.common.config.handler.*;
import com.syj.eplus.framework.common.entity.BaseValue;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.module.crm.controller.admin.cust.vo.CustRespVO;
import com.syj.eplus.module.crm.entity.AddressShipping;
import com.syj.eplus.module.crm.framework.config.handler.JsonAddressListTypeHandler;
import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 客户资料 DO
 *
 * @author du
 */
@TableName(value = "crm_cust", autoResultMap = true)
@KeySequence("crm_cust_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = false)
public class CustDO extends BaseDO implements ModelKeyHolder, ChangeExInterface {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;


    @TableField(exist = false)
    private String sourceCode;

    /**
     * 是否变更
     */
    private Integer changeFlag;

    /**
     * 变更状态
     */
    private Integer changeStatus;
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
    @TableField(typeHandler = LongListTypeHandler.class)
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
    @TableField(typeHandler = BaseValueListTypeHandler.class)
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
    @TableField(typeHandler = JsonAmountTypeHandler.class)
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
    @TableField(typeHandler = JsonAddressListTypeHandler.class)
    private List<AddressShipping> addressShipping;
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
     * 业务员
     */
    @TableField(typeHandler = BaseValueListTypeHandler.class)
    private List<BaseValue> managerList;
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
    @TableField(typeHandler = JsonFileListTypeHandler.class)
    private List<SimpleFile> annex;
    /**
     * 图片
     */
    @TableField(typeHandler = JsonFileListTypeHandler.class)
    private List<SimpleFile> picture;

    /**
     * 关联客户
     */
    @TableField(typeHandler = StringListTypeHandler.class)
    private List<String> custLinkCode;

    /**
     * 变更是否删除
     */
    private Integer changeDeleted;

    @TableField(exist = false)
    private CustRespVO oldCust;

    /**
     * 内部企业标识 0-否 1-是
     */
    private Integer internalFlag;

    /**
     * 内部企业主键
     */
    private Long internalCompanyId;

    /**
     * 正面唛头
     */
    private String mainMark;
    /**
     * 侧面唛头
     */
    private String sideMark;


    private String modelKey;

    @TableField(typeHandler = JsonEffectRangeTypeHandler.class)
    private List<JsonEffectRange> effectRangeList;

    //展会id
    private Long exmsExhibitionId;

    //展会系列
    private Long exmsEventCategoryId;

    /**
     * 收货人
     */
    private String receivePerson;
    /**
     * 通知人
     */
    private String notifyPerson;
}
