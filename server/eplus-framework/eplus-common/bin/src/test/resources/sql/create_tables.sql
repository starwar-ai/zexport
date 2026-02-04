/*
 Navicat Premium Data Transfer

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80035
 Source Host           : localhost:3306
 Source Schema         : foreign_trade

 Target Server Type    : MySQL
 Target Server Version : 80035
 File Encoding         : 65001

 Date: 03/02/2024 20:39:02
*/

SET NAMES utf8mb4;
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

SET FOREIGN_KEY_CHECKS = 1;
