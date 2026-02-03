package com.syj.eplus.module.wms.api.stockNotice.dto;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.sun.xml.bind.v2.TODO;
import com.syj.eplus.framework.common.config.handler.JsonWeightTypeHandler;
import com.syj.eplus.framework.common.entity.JsonSpecificationEntity;
import com.syj.eplus.framework.common.entity.JsonWeight;
import com.syj.eplus.framework.common.entity.UserDept;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 仓储管理-入(出)库通知单-明细 DTO
 *
 * @author ePlus
 */
@Data
public class StockNoticeItemDTO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 入/出库通知单主键
     */
    private Long noticeId;

    /**
     * 来源单据类型  采购入/出库、盘点入/出库、调拨入/出库、组套件入/出库
     */
    private Integer sourceType;
    /**
     * 序号
     */
    private Integer sortNum;
    /**
     * 来源单据明细序号
     */
    private Integer sourceSortNum;
    /**
     * 入/出库状态（未收/出货、部分收/出货、完全收/出货）
     *
     * 枚举 {@link TODO in_out_status 对应的类}
     */
    private Integer noticeItemStatus;
    /**
     * 产品主键
     */
    private Long skuId;
    /**
     * 产品编码
     */
    private String skuCode;
    /**
     * 产品中文名称
     */
    private String skuName;
    /**
     * 客户货号
     */
    private String cskuCode;

    /**
     * 客户货号
     */
    private String basicSkuCode;
    /**
     * 自主品牌标识
     */
    private Integer ownBrandFlag;
    /**
     * 客户产品标识
     */
    private Integer custProFlag;
    /**
     * 供应商主键
     */
    private Long venderId;
    /**
     * 供应商编码
     */
    private String venderCode;
    /**
     * 供应商名称
     */
    private String venderName;
    /**
     * 客户主键
     */
    private Long custId;
    /**
     * 客户编码
     */
    private String custCode;
    /**
     * 客户名称
     */
    private String custName;
    /**
     * 客户PO号
     */
    private String custPo;
    /**
     * 应收/出数量
     */
    private Integer orderQuantity;
    /**
     * 应收/出箱数
     */
    private Integer orderBoxQuantity;
    /**
     * 外箱装量
     */
    private Integer qtyPerOuterbox;
    /**
     * 内盒装量
     */
    private Integer qtyPerInnerbox;
    /**
     * 总体积(单箱体积x箱数cm³)
     */
    private BigDecimal totalVolume;
    /**
     * 总毛重（单箱毛重x箱数 {数量,单位}）
     */
    @TableField(typeHandler = JsonWeightTypeHandler.class)
    private JsonWeight totalWeight;
    /**
     * 单托体积（cm³）
     */
    private BigDecimal palletVolume;
    /**
     * 单托毛重（{数量,单位}）
     */
    @TableField(typeHandler = JsonWeightTypeHandler.class)
    private JsonWeight palletWeight;
    /**
     * 备注
     */
    private String remark;

    /**
     * 批次号
     */
    private String batchCode;

    /**
     * 销售合同编号
     */
    private String saleContractCode;

    /**
     * 采购员主键
     */
    private Long purchaserId;
    /**
     * 采购员部门主键
     */
    private Long purchaserDeptId;

    /**
     * 采购员名称
     */
    @Schema(description = "采购员名称")
    private String purchaseUserName;
    /**
     * 采购员部门名称
     */
    @Schema(description = "采购员部门名称")
    private String purchaseUserDeptName;

    /**
     * 仓库主键
     */
    private Long warehouseId;
    /**
     * 仓库名称
     */
    private String warehouseName;

    /**
     * 采购合同号
     */
    private String purchaseContractCode;

    /**
     *  外销员
     */
    private UserDept sales;

    /**
     * 来源编号
     */
    private String sourceUniqueCode;

    /**
     * 跟单员
     */
    private UserDept manager;

    /**
     *销售合同明细编号
     */
    private String saleItemUniqueCode;

    /**
     * 出运明细id
     */
    private Long shipmentItemId;

    /**
     * 规格
     */
    private List<JsonSpecificationEntity> specificationList;

    /**
     * 是否分箱
     */
    private Integer splitBoxFlag;

    /**
     * 发货地点
     */
    private Integer shippedAddress;

    /**
     * 待入库数量
     */
    private Integer pendingStockQuantity;

}
