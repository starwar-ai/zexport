package com.syj.eplus.module.oa.dal.dataobject.dictsubject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.syj.eplus.framework.common.config.handler.StringListTypeHandler;
import lombok.*;

import java.util.List;

/**
 * 类别配置 DO
 *
 * @author ePlus
 */

@TableName(value = "oa_dict_subject",autoResultMap = true)
@KeySequence("oa_dict_subject_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DictSubjectDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 科目id
     */
    private Long subjectId;
    /**
     * 科目名称
     */
    private String subjectName;
    /**
     * 科目描述
     */
    private String subjectDescription;

    /**
     * 字典类型
     */
    @TableField(typeHandler = StringListTypeHandler.class)
    private List<String> systemDictTypeList;

    /**
     * 费用名称
     */
    private String feeName;
    /**
     * 费用描述
     */
    private String feeDesc;

    /**
     * 是否在描述展示
     */
    private Integer showDescFlag;

    /**
     * 是否在费用实际展示
     */
    private Integer showFeeFlag;
}