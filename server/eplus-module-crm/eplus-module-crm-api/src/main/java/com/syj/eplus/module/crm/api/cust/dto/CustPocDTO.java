package com.syj.eplus.module.crm.api.cust.dto;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.sun.xml.bind.v2.TODO;
import com.syj.eplus.framework.common.config.handler.JsonFileListTypeHandler;
import com.syj.eplus.framework.common.entity.SimpleFile;
import lombok.Data;

import java.util.List;
@Data
public class CustPocDTO extends BaseDO {

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