package com.syj.eplus.module.scm.dal.dataobject.vender;

import cn.iocoder.yudao.framework.common.pojo.JsonEffectRange;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.framework.mybatis.core.exinterface.ChangeExInterface;
import cn.iocoder.yudao.module.system.inface.ModelKeyHolder;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sun.xml.bind.v2.TODO;
import com.syj.eplus.framework.common.config.handler.*;
import com.syj.eplus.framework.common.entity.BaseValue;
import com.syj.eplus.framework.common.entity.JsonVenderTax;
import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.module.scm.controller.admin.vender.vo.VenderRespVO;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 供应商信息 DO
 *
 * @author zhangcm
 */
@TableName(value = "scm_vender", autoResultMap = true)
@KeySequence("scm_vender_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = false)
public class VenderDO extends BaseDO implements ModelKeyHolder, ChangeExInterface {

    /**
     * 主键
     */
    @TableId
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
    @TableField(typeHandler = LongListTypeHandler.class)
    private List<Long> businessScope;
    /**
     * 应付供应商
     */
    @TableField(typeHandler = StringListTypeHandler.class)
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
     * 审核状态
     * <p>
     * 枚举 {@link TODO auditStatus 对应的类}
     */
    private Integer auditStatus;
    /**
     * 采购员
     */
    @TableField(typeHandler = LongListTypeHandler.class)
    private List<Long> buyerIds;
    /**
     * 采购员
     */
    @TableField(typeHandler = BaseValueListTypeHandler.class)
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
    @TableField(typeHandler = JsonFileListTypeHandler.class)
    private List<SimpleFile> annex;

    /**
     * 传真
     */
    private String fax;

    /**
     *资质 id
     */
    @TableField(typeHandler = LongListTypeHandler.class)
    private List<Long> qualificationIds;

    /**
     * 变更是否删除
     */
    private Integer changeDeleted;

    /**
     * 行政供应商类型
     */
    private Integer administrationVenderType;

    @TableField(exist = false)
    private VenderRespVO oldVender;


    /**
     * 内部企业标识 0-否 1-是
     */
    private Integer internalFlag;

    /**
     * 内部企业主键
     */
    private Long internalCompanyId;

    private String modelKey;

    @TableField(typeHandler = JsonEffectRangeTypeHandler.class)
    private List<JsonEffectRange> effectRangeList;

    @TableField(typeHandler = JsonVenderTaxListTypeHandler.class)
    private List<JsonVenderTax> taxMsg;
}
