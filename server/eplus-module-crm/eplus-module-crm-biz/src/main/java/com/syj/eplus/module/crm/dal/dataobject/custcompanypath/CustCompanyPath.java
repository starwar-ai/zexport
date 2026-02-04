package com.syj.eplus.module.crm.dal.dataobject.custcompanypath;

import cn.iocoder.yudao.framework.common.pojo.JsonEffectRange;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.framework.mybatis.core.exinterface.ChangeExInterface;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @Description：
 * @Author：du
 * @Date：2024/6/15 17:07
 */
@TableName(value = "crm_company_path", autoResultMap = true)
@KeySequence("crm_cust_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = false)
public class CustCompanyPath extends BaseDO implements ChangeExInterface {
    /**
     * 主键
     */
    private Long id;

    /**
     * 公司路径主键
     */
    private Long companyPathId;
    /**
     * 客户id
     */
    private Long custId;
    /**
     * 客户编号
     */
    private String custCode;
    /**
     * 是否默认
     */
    private Integer defaultFlag;

    @TableField(exist = false)
    private Integer changeFlag;

    @TableField(exist = false)
    private String sourceCode;

    @TableField(exist = false)
    private List<JsonEffectRange> effectRangeList;
}
