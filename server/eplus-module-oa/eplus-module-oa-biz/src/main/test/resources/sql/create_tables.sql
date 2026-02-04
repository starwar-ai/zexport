SET FOREIGN_KEY_CHECKS = 0;
-- ----------------------------
-- Table structure for system_sn
-- ----------------------------
DROP TABLE IF EXISTS `system_sn`;
CREATE TABLE `system_sn`  (
                              `id` bigint NOT NULL AUTO_INCREMENT  COMMENT '编号',
                              `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '类型',
                              `sn` int NOT NULL DEFAULT 0 COMMENT '序列号',
                              `length` int NULL DEFAULT NULL COMMENT '序列号长度',
                              `creator` int UNSIGNED NULL DEFAULT NULL COMMENT '创建者',
                              `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              `updater` int UNSIGNED NULL DEFAULT NULL COMMENT '更新者',
                              `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                              `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
                              PRIMARY KEY (`id`) USING BTREE,
                              UNIQUE INDEX `type`(`type` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '序列号记录表' ROW_FORMAT = Dynamic;



-- ----------------------------
-- Table structure for oa_loan_app
-- ----------------------------
DROP TABLE IF EXISTS `oa_loan_app`;
CREATE TABLE `oa_loan_app`  (
-- 表单信息
                                `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
                                `code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '单据编号',
                                `print_flag` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '打印状态',
                                `print_times` INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '打印次数',
                                `audit_status` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '审核状态',

-- 借款信息
                                `purpose` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '借款事由',
                                `loan_dept_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '申请部门',
                                `applyer_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '申请人编号',
                                `amount` decimal(19, 6) NULL DEFAULT NULL COMMENT '借款金额',
                                `currency` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '币种',
                                `loan_date` datetime NULL DEFAULT NULL COMMENT '借款申请日期',
                                `loan_type` tinyint UNSIGNED NOT NULL  COMMENT '借款方式',

-- 支付信息
                                `bank` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '开户行',
                                `bankaccount` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '银行账户',
                                `accountname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '账户名称',
                                `transfer_status` TINYINT UNSIGNED DEFAULT NULL COMMENT '支付状态',
                                `transfer_amount` decimal(19, 6) NULL DEFAULT NULL COMMENT '已转金额',
                                `transfer_date` datetime NULL DEFAULT NULL COMMENT '支付日期',
                                `transfer_cashier_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '出纳编号',
                                `transfer_company_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '支付公司',
                                `transfer_remarks` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '支付备注',
-- 还款信息

                                `repay_status` TINYINT UNSIGNED DEFAULT NULL COMMENT '还款状态',
                                `repay_amount` decimal(19, 6) NULL DEFAULT NULL COMMENT '已还金额',
                                `repay_date` datetime NULL DEFAULT NULL COMMENT '实际还款日期',
                                `outstanding_amount` decimal(19, 6) NULL DEFAULT NULL COMMENT '剩余未还款金额',

-- 系统字段
                                `creator` int UNSIGNED NULL DEFAULT NULL COMMENT '创建人',
                                `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                `updater` int UNSIGNED NULL DEFAULT NULL COMMENT '修改人',
                                `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除  0：有效 1：删除',
                                PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '借款申请单' ROW_FORMAT = DYNAMIC;

SET FOREIGN_KEY_CHECKS = 1;