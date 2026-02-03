
CREATE TABLE `daily_currencys_rate` (
                                        `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                        `daily_curr_date` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '日期',
                                        `daily_curr_name` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '币种',
                                        `daily_curr_rate` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '汇率',
                                        `daily_curr_source` int DEFAULT NULL COMMENT '来源 1-自动 0-手动',
                                        `daily_curr_mid_rate` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '中间汇率',
                                        `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
                                        `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                        `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
                                        `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                        `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
                                        PRIMARY KEY (`id`) USING BTREE,
                                        KEY `index_id` (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=18123 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='动态汇率表';
/*!40101 SET character_set_client = @saved_cs_client */;