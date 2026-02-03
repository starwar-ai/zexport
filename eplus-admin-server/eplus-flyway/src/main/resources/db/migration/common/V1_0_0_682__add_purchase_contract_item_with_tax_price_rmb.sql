-- =============================================
-- 采购合同明细表增加含税总价（人民币）字段
-- Version: V1.0.0.002
-- Date: 2026-01-27
-- Author: System
-- Description: 在采购合同明细表中添加 total_price_rmb 字段，用于存储含税总价（人民币）
-- =============================================

-- 添加字段
ALTER TABLE `scm_purchase_contract_item` 
ADD COLUMN `total_price_rmb` VARCHAR(255) NULL COMMENT '含税总价（人民币）' AFTER `with_tax_price`;

-- 说明：
-- 1. 字段类型：VARCHAR(255)，使用 JsonAmountTypeHandler 处理
-- 2. 存储格式：JSON {"amount": xxx, "currency": "RMB"}
-- 3. 计算逻辑：含税总价（人民币）= 含税单价 × 数量，并使用当日汇率转换为 RMB
-- 4. 计算时机：在保存采购合同明细时自动计算并保存
-- 5. 业务用途：用于采购合同详情展示和报表统计
