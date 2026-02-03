package com.syj.eplus.module.wms.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

/**
 * wms 错误码枚举类
 * <p>
 * wms 系统，使用 1-006-001-001 段
 */
public interface ErrorCodeConstants {
    // ========== 仓储管理 仓库信息==========
    ErrorCode WAREHOUSE_NOT_EXISTS = new ErrorCode(1_006_001_001, "仓库信息不存在");

    ErrorCode WAREHOUSE_NAME_ALREADY_EXISTS = new ErrorCode(1_006_001_002, "仓库名称已存在");

    ErrorCode WAREHOUSE_NOT_DELETE = new ErrorCode(1_006_001_003, "供应商仓库不允许删除");
    ErrorCode WAREHOUSE_NOT_DEFAULT = new ErrorCode(1_006_001_004, "仓库没有默认值");
    ErrorCode WAREHOUSE_MORE_DEFAULT = new ErrorCode(1_006_001_005, "仓库有多个默认值");
    ErrorCode BATCKCODE_MUL = new ErrorCode(1_006_016_002, "批次号重复");

    // ========== 仓储管理 入库通知单==========
    ErrorCode RENOTICE_NOT_EXISTS = new ErrorCode(1_006_002_001, "入库通知单不存在");

    ErrorCode RENOTICE_ITEM_NOT_EXISTS = new ErrorCode(1_006_003_001, "入库通知单-明细不存在");
    ErrorCode RENOTICE_ITEM_NOT_EMPTY = new ErrorCode(1_006_003_002, "入库通知单-明细不可为空");


    // ========== 仓储管理 出库通知单==========
    ErrorCode TTNOTICE_NOT_EXISTS = new ErrorCode(1_006_004_001, "出库通知单不存在");

    ErrorCode BILL_IN_CONVERT = new ErrorCode(1_006_004_002, "通知单据正在转单中，请检查");

    ErrorCode TTNOTICE_ITEM_NOT_EXISTS = new ErrorCode(1_006_005_001, "出库通知单-明细不存在");

    ErrorCode BILL_ITEM_ALL_OUT = new ErrorCode(1_006_005_002, "单据明细均已出库");

    ErrorCode QTY_PER_OUTERBOX_INVALID = new ErrorCode(1_006_005_003, "外箱装量无效，不能为空或0");

    // ========== 仓储管理 入库单==========
    ErrorCode YSBILL_NOT_EXISTS = new ErrorCode(1_006_006_001, "入库单不存在");


    ErrorCode YSBILL_ITEM_NOT_EXISTS = new ErrorCode(1_007_003_001, "入库单-明细不存在");

    ErrorCode YSBILL_PARENT_MANUFACTURE_BILL_FAIL = new ErrorCode(1_007_003_002, "加工单-父产品入库失败");

    ErrorCode YSBILL_PARENT_MANUFACTURE_BILL_ITEM_FAIL = new ErrorCode(1_007_003_003, "加工单-父产品明细入库失败");

    ErrorCode YSBILL_PARENT_MANUFACTURE_BILL_NOT_ENOUGH = new ErrorCode(1_007_003_004, "加工单-父产品数据不全");



    // ========== 仓储管理 出库单==========
    ErrorCode CKBILL_NOT_EXISTS = new ErrorCode(1_006_008_001, "出库单不存在");

    ErrorCode CKBILL_ITEM_NOT_EXISTS = new ErrorCode(1_006_009_001, "出库单-明细不存在");

    ErrorCode CKBILL_CHILDREN_MANUFACTURE_BILL_FAIL = new ErrorCode(1_006_009_002, "加工单-子产品出库失败");

    ErrorCode CKBILL_CHILDREN_MANUFACTURE_BILL_ITEM_FAIL = new ErrorCode(1_006_009_003, "加工单-子产品明细出库失败");

    ErrorCode CKBILL_CHILDREN_MANUFACTURE_BILL_NOT_ENOUGH = new ErrorCode(1_006_009_004, "加工单-子产品数据不全");

    // ========== 仓储管理-库存明细 ==========
    ErrorCode STOCK_NOT_EXISTS = new ErrorCode(1_006_010_001, "库存明细不存在");
    ErrorCode STOCK_EXCEED = new ErrorCode(1_006_010_002, "库存明细不足，请检查");
    ErrorCode SALE_CONTRACT_NOT_EXITS = new ErrorCode(1_006_010_003, "销售合同不存在");
    ErrorCode SALE_CONTRACT_SHIPMENT_TOTAL_QUANTITY_LESS = new ErrorCode(1_006_010_005, "销售合同已记录已出运总数小于当前出运数量，作废回写失败");

    ErrorCode STOCK_IMPORT_NULL = new ErrorCode(1_006_010_006, "导入编号异常，请关闭页面，重新导入");

    ErrorCode STOCK_NOT_DONE = new ErrorCode(1_006_010_007, "库存数据已经导入，请勿重复操作");

    // ========== 仓储管理-库存明细 锁定信息 ==========
    ErrorCode STOCK_LOCK_NOT_EXISTS = new ErrorCode(1_006_011_001, "库存明细-占用信息不存在");
    ErrorCode STOCK_LOCK_EXCEED = new ErrorCode(1_006_011_002, "可锁定数量不足，请检查");
    ErrorCode STOCK_LOCK_EXISTS = new ErrorCode(1_006_011_003, "锁库数据已经存在，不允许删除库存");


    // ========== 仓储管理-盘点单 ==========
    ErrorCode STOCKTAKE_NOT_EXISTS = new ErrorCode(1_006_012_001, "盘点单不存在");

    ErrorCode STOCKTAKE_ITEM_NOT_EXISTS = new ErrorCode(1_006_013_001, "盘点单-明细不存在");

    // ========== 仓储管理-盘库调整单 ==========
    ErrorCode ADJUSTMENT_NOT_EXISTS = new ErrorCode(1_006_014_001, "盘库调整单不存在");

    ErrorCode ADJUSTMENT_ITEM_NOT_EXISTS = new ErrorCode(1_006_015_001, "盘库调整单-明细不存在");

    // =========== 调拨单 ================
    ErrorCode TRANSFER_ORDER_NOT_EXISTS = new ErrorCode(1_006_016_001, "调拨单-调拨单不存在");
    ErrorCode STOCK_NOT_EXISTS_BY_BATCH_CODE = new ErrorCode(1_006_016_002, "调拨单-根据批次号{}未查询到对应库存");
    ErrorCode STOCK_NOT_ENOUGH = new ErrorCode(1_006_016_003, "调拨单-库存不足调拨当前数量");
    ErrorCode TRANSFER_ORDER_ITEM_NOT_EXISTS = new ErrorCode(1_006_016_003, "调拨单-调拨单{}明细未找到");


    ErrorCode STOCK_IMPORT_NOT_EXISTS = new ErrorCode(1_007_001_001, "仓储管理-库存明细导入不存在");
}
