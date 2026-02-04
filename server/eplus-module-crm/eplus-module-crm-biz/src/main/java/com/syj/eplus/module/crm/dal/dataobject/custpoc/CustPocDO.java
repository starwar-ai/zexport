package com.syj.eplus.module.crm.dal.dataobject.custpoc;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.*;
import com.sun.xml.bind.v2.TODO;
import com.syj.eplus.framework.common.config.handler.JsonFileListTypeHandler;
import com.syj.eplus.framework.common.entity.SimpleFile;
import lombok.*;

import java.util.List;

/**
 * 客户联系人 DO
 *
 * @author du
 */
@TableName("crm_cust_poc")
@KeySequence("crm_cust_poc_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustPocDO extends BaseDO {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 版本号
     */
    private Integer ver;
    /**
     * 客户id
     */
    private Long custId;
    /**
     * 联系人姓名
     */
    private String name;
    /**
     * 联系人职位
     */
    private String pocPosts;
    /**
     * 电子邮件
     */
    private String email;
    /**
     * 手机
     */
    private String mobile;
    /**
     * 住宅地址
     */
    private String address;
    /**
     * 默认联系人
     * <p>
     * 枚举 {@link TODO confirm_type 对应的类}
     */
    private Integer defaultFlag;

    /**
     * 备注
     */
    private String remark;

    /**
     * 座机
     */
    private String telephone;

    /**
     * 微信
     */
    private String wechat;

    /**
     * QQ
     */
    private String qq;

    /**
     * 名片
     */
    @TableField(typeHandler = JsonFileListTypeHandler.class, value = "card")
    private List<SimpleFile> cardList;
}