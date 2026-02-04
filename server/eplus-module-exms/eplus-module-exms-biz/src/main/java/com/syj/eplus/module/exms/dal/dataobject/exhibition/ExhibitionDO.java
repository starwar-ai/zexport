package com.syj.eplus.module.exms.dal.dataobject.exhibition;


import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.syj.eplus.framework.common.annotations.CompareField;
import com.syj.eplus.framework.common.config.handler.IntegerListTypeHandler;
import com.syj.eplus.framework.common.config.handler.JsonAmountTypeHandler;
import com.syj.eplus.framework.common.config.handler.LongListTypeHandler;
import com.syj.eplus.framework.common.entity.JsonAmount;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 展会 DO
 *
 * @author zhangcm
 */

@TableName(value = "exms_exhibition",autoResultMap = true)
@KeySequence("exms_exhibition_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExhibitionDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 编号
     */
    private String code;
    /**
     * 名称
     */
    @CompareField(value = "名称")
    private String name;
    /**
     * 项目状态
     */
    private Integer expoStatus;
    /**
     * 审核状态
     */
    private Integer auditStatus;
    /**
     * 主体id
     */
    @CompareField(value = "主体id")
    private Long companyId;
    /**
     * 主体名称
     */
    @CompareField(value = "主体名称")
    private String companyName;
    /**
     * 承担费用部门列表
     */
    @CompareField(value = "承担费用部门列表")
    @TableField(typeHandler = LongListTypeHandler.class)
    private List<Long> deptList;
    /**
     * 申请日期
     */
    private LocalDateTime applyDate;
    /**
     * 申请人id
     */
    private Long applyUserId;
    /**
     * 申请人姓名
     */
    private String applyUserName;
    /**
     * 申请人部门id
     */
    private Long applyDeptId;
    /**
     * 申请人部门名称
     */
    private String applyDeptName;
    /**
     * 项目预算
     */
    @CompareField(value = "项目预算")
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount budget;
    /**
     * 展会主题
     */
    @CompareField(value = "展会主题")
    private Integer theme;
    /**
     * 摊位主题
     */
    @CompareField(value = "摊位主题")
    @TableField(typeHandler = IntegerListTypeHandler.class)
    private List<Integer> stallThemeList;
    /**
     * 国家id
     */
    @CompareField(value = "国家id")
    private Long countryId;
    /**
     * 国家名称
     */
    @CompareField(value = "国家名称")
    private String countryName;
    /**
     * 城市id
     */
    @CompareField(value = "城市id")
    private Long cityId;
    /**
     * 城市名称
     */
    @CompareField(value = "城市名称")
    private String cityName;
    /**
     * 计划开始日期
     */
    @CompareField(value = "计划开始日期")
    private LocalDateTime planStartDate;
    /**
     * 计划结束日期
     */
    @CompareField(value = "计划结束日期")
    private LocalDateTime planEndDate;
    /**
     * 实际开始日期
     */
    @CompareField(value = "实际开始日期")
    private LocalDateTime startDate;
    /**
     * 实际结束日期
     */
    @CompareField(value = "实际结束日期")
    private LocalDateTime endDate;
    /**
     * 摊位面积（平米）
     */
    @CompareField(value = "摊位面积")
    private String stallArea;
    /**
     * 负责人id
     */
    @CompareField(value = "负责人id")
    private Long ownerUserId;
    /**
     * 负责人姓名
     */
    @CompareField(value = "负责人姓名")
    private String ownerUserName;
    /**
     * 负责人部门id
     */
    @CompareField(value = "负责人部门id")
    private Long ownerDeptId;
    /**
     * 负责人部门名称
     */
    @CompareField(value = "负责人部门名称")
    private String ownerDeptName;
    /**
     * 备注
     */
    @CompareField(value = "备注")
    private String remark;
    /**
     * 完成时间
     */
    @CompareField(value = "完成时间")
    private LocalDateTime doneTime;
    /**
     * 结案时间
     */
    @CompareField(value = "结案时间")
    private LocalDateTime finishTime;

    @CompareField(value = "展会系列id")
    private Long exmsEventCategoryId;

    @CompareField(value = "展会系列名称")
    private String exmsEventCategoryName;

    /**
     * 排序
     */
    private Integer orderNum;

}