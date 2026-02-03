package com.syj.eplus.module.oa.api.feeshare.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.UserDept;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 *
 * @author zhangcm
 */
@Data
@Accessors(chain = true)
public class FeeShareBaseDTO {
    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 来源类型
     */
    private Integer businessType;
    /**
     * 来源单编号
     */
    private String businessCode;
//    /**
//     * 业务部门id
//     */
//    private Long deptId;
//    /**
//     * 业务部门名称
//     */
//    private String deptName;
    /**
     * 费用归属类别
     */
    private Integer feeShareType;
    /**
     * 相关方类别
     */
    private Integer relationType;
    /**
     * 具体名称id
     */
    private Long descId;
    /**
     * 具体名称
     */
    private String descName;
    /**
     * 主体主键
     */
    private Long companyId;

    /**
     * 主体名称
     */
    private String companyName;

    /**
     * 归集状态
     */
    private Integer AuditStatus;

    /**
     * 来源单据状态
     */
    private Integer sourceStatus;

    /**
     * 来源单据主键
     */
    private Long businessId;

    /**
     * 金额
     */
    private JsonAmount amount;


    /**
     * 订单类型
     */
    private Integer orderType;

    /**
     * 归集状态
     */
    private Integer status;

    private Integer  peCollectionFlag;

    /**
     * 项目id
     */
    private Long projectId;
    /**
     * 展会id
     */
    private Long exhibitionId;
    /**
     * 品牌名称
     */
    private Integer brandType;

    /**
     * 录入人
     */
    private UserDept inputUser;
}
