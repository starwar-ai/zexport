package com.syj.eplus.module.pms.dal.dataobject.sku;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.syj.eplus.framework.common.config.handler.JsonAmountTypeHandler;
import com.syj.eplus.framework.common.config.handler.JsonFileListTypeHandler;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.framework.common.entity.UserDept;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Description：
 * @Author：du
 * @Date：2024/5/10 17:25
 */
@Data
@TableName(value = "pms_sku", autoResultMap = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimpleSkuDO {
    /**
     * 主键
     */
    private Long id;
    /**
     * 编号
     */
    private String code;
    private String sourceCode;
    /**
     * 图片
     */
    @TableField(typeHandler = JsonFileListTypeHandler.class)
    private List<SimpleFile> pictures;
    /**
     * 缩略图
     */
    private String thumbnail;
    /**
     * 品名
     */
    private String skuName;
    /**
     * 产品材质
     */
    private String material;
    /**
     * 分类
     */
    private String categoryName;
    /**
     * 品牌
     */
    private String brandName;
    /**
     * 状态
     */
    private Integer onshelfFlag;
    /**
     * 客户货号
     */
    private String cskuCode;
    /**
     * 自营产品货号
     */
    private String oskuCode;
    /**
     * 销售单价
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount price;

    /**
     * 产品类型
     */
    private Integer skuType;

    /**
     * 英文名称
     */
    private String nameEng;
    /**
     * 海关编码
     */
    private String hsCode;
    /**
     * 退税率
     */
    private BigDecimal taxRefundRate;
    /**
     * 公司定价
     */
    private JsonAmount companyPrice;
    /**
     * 自动创建标识
     */
    private Integer autoCreateFlag;
    /**
     * 是否通用辅料
     */
    private Integer auxiliarySkuFlag;
    /**
     * 是否自营
     */
    private Integer ownBrandFlag;
    /**
     * 客户产品标识
     */
    private Integer custProFlag;

    /**
     * 计量单位
     */
    private Integer measureUnit;

    /**
     * 客户名称
     */
    private String custName;

    /**
     * 是否商检
     */
    private Integer commodityInspectionFlag;

    /**
     * 变更标识
     */
    private Integer changeFlag;
    /**
     * 采购员
     */
    private List<UserDept> buyerList;

    private List<Long> buyerIds;
    /**
     * 中文说明
     */
    private String description;
    /**
     * 英文说明
     */
    private String descriptionEng;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 基础产品编号
     */
    private String basicSkuCode;
    private String barcode;

    /**
     * 海关编码id
     */
    private Long hsCodeId;
}
