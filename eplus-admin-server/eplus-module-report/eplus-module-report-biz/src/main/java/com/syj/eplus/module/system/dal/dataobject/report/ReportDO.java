package com.syj.eplus.module.system.dal.dataobject.report;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sun.xml.bind.v2.TODO;
import com.syj.eplus.framework.common.config.handler.JsonFileListTypeHandler;
import com.syj.eplus.framework.common.entity.SimpleFile;
import lombok.*;

import java.util.List;

/**
 * 打印模板 DO
 *
 * @author ePlus
 */

@TableName(value = "system_report", autoResultMap = true)
@KeySequence("system_report_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportDO extends BaseDO {

    /**
     * id
     */
    @TableId
    private Long id;
    /**
     * 模板编码
     */
    private String code;
    /**
     * 模板名称
     */
    private String name;

    /**
     * 附件
     */
    @TableField(typeHandler = JsonFileListTypeHandler.class)
    private List<SimpleFile> annex;
    /**
     * 模板类型：1：基础模板，2：外部模板
     *
     * 枚举 {@link TODO report_type 对应的类}
     */
    private Integer reportType;
    /**
     * 外部模板类型：1：客户，2：供应商
     *
     * 枚举 {@link TODO report_source_type 对应的类}
     */
    private Integer sourceType;
    /**
     * 来源编号
     */
    private String sourceCode;
    /**
     * 来源名称
     */
    private String sourceName;
    /**
     * 处理状态
     * <p>
     * 枚举 {@link TODO auditStatus 对应的类}
     */
    private Integer auditStatus;

    /**
     * 归属公司主键
     */
    private Long companyId;


    /**
     * 归属公司
     */
    private String companyName;


    /**
     * 模版业务类型 1：打印 2：导出
     */
    private Integer businessType;

}