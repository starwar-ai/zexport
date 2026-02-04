package com.syj.eplus.module.infra.dal.dataobject.company;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.syj.eplus.framework.common.config.handler.JsonDictFileListTypeHandler;
import com.syj.eplus.framework.common.config.handler.JsonFileTypeHandler;
import com.syj.eplus.framework.common.config.handler.JsonUserDeptTypeHandler;
import com.syj.eplus.framework.common.config.handler.StringListTypeHandler;
import com.syj.eplus.framework.common.entity.DictSimpleFileList;
import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.framework.common.entity.UserDept;
import lombok.*;

import java.util.List;

/**
 * 内部法人单位 DO
 *
 * @author du
 */
@TableName(value = "system_company", autoResultMap = true)
@KeySequence("system_company_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 单位名称
     */
    private String name;

    /**
     * 公司性质
     */
    private Integer companyNature;

    /**
     * 企业名称
     */
    private String companyName;

    /**
     * 企业英文名称
     */
    private String companyNameEng;

    /**
     * 营业执照
     */
    @TableField(typeHandler = JsonFileTypeHandler.class)
    private SimpleFile license;

    /**
     * 营业执照号
     */
    private String licenseNo;

    /**
     * 企业地址
     */
    private String companyAddress;

    /**
     * 企业英文地址
     */
    private String companyAddressEng;

    /**
     * 管理员
     */
    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    private UserDept manager;

    /**
     * 管理员手机号
     */
    private String managerMobile;

    /**
     * 管理员邮箱
     */
    private String managerMail;

    /**
     * 企业电话
     */
    private String companyTel;

    /**
     * 企业传真
     */
    private String companyFax;

    /**
     * 法人
     */
    private String legalPerson;

    /**
     * 启用标识
     */
    private Integer enableFlag;

    /**
     * 简称
     */
    private String shortname;

    /**
     * 税号
     */
    private String taxNumb;

    /**
     * 海关编号
     */
    private String customsNumber;

    /**
     * 图片
     */
    @TableField(typeHandler = JsonDictFileListTypeHandler.class)
    private List<DictSimpleFileList> picture;


    /**
     * 可用币种
     */
    @TableField(typeHandler = StringListTypeHandler.class)
    private List<String> availableCurrencyList;
}
