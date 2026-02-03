package com.syj.eplus.module.crm.dal.dataobject.collectionaccount;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 收款账号 DO
 *
 * @author zhangcm
 */

@TableName("crm_collection_account")
@KeySequence("crm_collection_account_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CollectionAccountDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 默认标记
     */
//    private Integer defaultFlag;

    /**
     * 客户id
      */
    private Long custId;
    /**
     * 客户编号
     */
    private String custCode;
    /**
     * 内部公司主键
     */
    private Long companyId;
    /**
     * 内部公司银行账号主键
     */
    private Long companyBankId;

}