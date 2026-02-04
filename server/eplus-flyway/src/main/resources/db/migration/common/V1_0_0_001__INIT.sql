-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: localhost    Database: foreign_trade
-- ------------------------------------------------------
-- Server version	8.0.44

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `act_evt_log`
--

DROP TABLE IF EXISTS `act_evt_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `act_evt_log` (
                               `LOG_NR_` bigint NOT NULL AUTO_INCREMENT,
                               `TYPE_` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                               `PROC_DEF_ID_` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                               `PROC_INST_ID_` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                               `EXECUTION_ID_` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                               `TASK_ID_` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                               `TIME_STAMP_` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
                               `USER_ID_` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                               `DATA_` longblob,
                               `LOCK_OWNER_` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                               `LOCK_TIME_` timestamp(3) NULL DEFAULT NULL,
                               `IS_PROCESSED_` tinyint DEFAULT '0',
                               PRIMARY KEY (`LOG_NR_`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `act_ge_bytearray`
--

DROP TABLE IF EXISTS `act_ge_bytearray`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `act_ge_bytearray` (
                                    `ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
                                    `REV_` int DEFAULT NULL,
                                    `NAME_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                    `DEPLOYMENT_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                    `BYTES_` longblob,
                                    `GENERATED_` tinyint DEFAULT NULL,
                                    PRIMARY KEY (`ID_`) USING BTREE,
                                    KEY `ACT_FK_BYTEARR_DEPL` (`DEPLOYMENT_ID_`) USING BTREE,
                                    CONSTRAINT `ACT_FK_BYTEARR_DEPL` FOREIGN KEY (`DEPLOYMENT_ID_`) REFERENCES `act_re_deployment` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `act_ge_property`
--

DROP TABLE IF EXISTS `act_ge_property`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `act_ge_property` (
                                   `NAME_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
                                   `VALUE_` varchar(300) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                   `REV_` int DEFAULT NULL,
                                   PRIMARY KEY (`NAME_`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `act_hi_actinst`
--

DROP TABLE IF EXISTS `act_hi_actinst`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `act_hi_actinst` (
                                  `ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
                                  `REV_` int DEFAULT '1',
                                  `PROC_DEF_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
                                  `PROC_INST_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
                                  `EXECUTION_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
                                  `ACT_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
                                  `TASK_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                  `CALL_PROC_INST_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                  `ACT_NAME_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                  `ACT_TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
                                  `ASSIGNEE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                  `START_TIME_` datetime(3) NOT NULL,
                                  `END_TIME_` datetime(3) DEFAULT NULL,
                                  `TRANSACTION_ORDER_` int DEFAULT NULL,
                                  `DURATION_` bigint DEFAULT NULL,
                                  `DELETE_REASON_` varchar(4000) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                  `TENANT_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT '',
                                  PRIMARY KEY (`ID_`) USING BTREE,
                                  KEY `ACT_IDX_HI_ACT_INST_START` (`START_TIME_`) USING BTREE,
                                  KEY `ACT_IDX_HI_ACT_INST_END` (`END_TIME_`) USING BTREE,
                                  KEY `ACT_IDX_HI_ACT_INST_PROCINST` (`PROC_INST_ID_`,`ACT_ID_`) USING BTREE,
                                  KEY `ACT_IDX_HI_ACT_INST_EXEC` (`EXECUTION_ID_`,`ACT_ID_`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `act_hi_attachment`
--

DROP TABLE IF EXISTS `act_hi_attachment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `act_hi_attachment` (
                                     `ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
                                     `REV_` int DEFAULT NULL,
                                     `USER_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                     `NAME_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                     `DESCRIPTION_` varchar(4000) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                     `TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                     `TASK_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                     `PROC_INST_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                     `URL_` varchar(4000) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                     `CONTENT_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                     `TIME_` datetime(3) DEFAULT NULL,
                                     PRIMARY KEY (`ID_`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `act_hi_comment`
--

DROP TABLE IF EXISTS `act_hi_comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `act_hi_comment` (
                                  `ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
                                  `TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                  `TIME_` datetime(3) NOT NULL,
                                  `USER_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                  `TASK_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                  `PROC_INST_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                  `ACTION_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                  `MESSAGE_` varchar(4000) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                  `FULL_MSG_` longblob,
                                  PRIMARY KEY (`ID_`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `act_hi_detail`
--

DROP TABLE IF EXISTS `act_hi_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `act_hi_detail` (
                                 `ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
                                 `TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
                                 `PROC_INST_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                 `EXECUTION_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                 `TASK_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                 `ACT_INST_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                 `NAME_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
                                 `VAR_TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                 `REV_` int DEFAULT NULL,
                                 `TIME_` datetime(3) NOT NULL,
                                 `BYTEARRAY_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                 `DOUBLE_` double DEFAULT NULL,
                                 `LONG_` bigint DEFAULT NULL,
                                 `TEXT_` varchar(4000) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                 `TEXT2_` varchar(4000) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                 PRIMARY KEY (`ID_`) USING BTREE,
                                 KEY `ACT_IDX_HI_DETAIL_PROC_INST` (`PROC_INST_ID_`) USING BTREE,
                                 KEY `ACT_IDX_HI_DETAIL_ACT_INST` (`ACT_INST_ID_`) USING BTREE,
                                 KEY `ACT_IDX_HI_DETAIL_TIME` (`TIME_`) USING BTREE,
                                 KEY `ACT_IDX_HI_DETAIL_NAME` (`NAME_`) USING BTREE,
                                 KEY `ACT_IDX_HI_DETAIL_TASK_ID` (`TASK_ID_`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `act_hi_entitylink`
--

DROP TABLE IF EXISTS `act_hi_entitylink`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `act_hi_entitylink` (
                                     `ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
                                     `LINK_TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                     `CREATE_TIME_` datetime(3) DEFAULT NULL,
                                     `SCOPE_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                     `SUB_SCOPE_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                     `SCOPE_TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                     `SCOPE_DEFINITION_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                     `PARENT_ELEMENT_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                     `REF_SCOPE_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                     `REF_SCOPE_TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                     `REF_SCOPE_DEFINITION_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                     `ROOT_SCOPE_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                     `ROOT_SCOPE_TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                     `HIERARCHY_TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                     PRIMARY KEY (`ID_`) USING BTREE,
                                     KEY `ACT_IDX_HI_ENT_LNK_SCOPE` (`SCOPE_ID_`,`SCOPE_TYPE_`,`LINK_TYPE_`) USING BTREE,
                                     KEY `ACT_IDX_HI_ENT_LNK_REF_SCOPE` (`REF_SCOPE_ID_`,`REF_SCOPE_TYPE_`,`LINK_TYPE_`) USING BTREE,
                                     KEY `ACT_IDX_HI_ENT_LNK_ROOT_SCOPE` (`ROOT_SCOPE_ID_`,`ROOT_SCOPE_TYPE_`,`LINK_TYPE_`) USING BTREE,
                                     KEY `ACT_IDX_HI_ENT_LNK_SCOPE_DEF` (`SCOPE_DEFINITION_ID_`,`SCOPE_TYPE_`,`LINK_TYPE_`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `act_hi_identitylink`
--

DROP TABLE IF EXISTS `act_hi_identitylink`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `act_hi_identitylink` (
                                       `ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
                                       `GROUP_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                       `TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                       `USER_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                       `TASK_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                       `CREATE_TIME_` datetime(3) DEFAULT NULL,
                                       `PROC_INST_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                       `SCOPE_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                       `SUB_SCOPE_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                       `SCOPE_TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                       `SCOPE_DEFINITION_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                       PRIMARY KEY (`ID_`) USING BTREE,
                                       KEY `ACT_IDX_HI_IDENT_LNK_USER` (`USER_ID_`) USING BTREE,
                                       KEY `ACT_IDX_HI_IDENT_LNK_SCOPE` (`SCOPE_ID_`,`SCOPE_TYPE_`) USING BTREE,
                                       KEY `ACT_IDX_HI_IDENT_LNK_SUB_SCOPE` (`SUB_SCOPE_ID_`,`SCOPE_TYPE_`) USING BTREE,
                                       KEY `ACT_IDX_HI_IDENT_LNK_SCOPE_DEF` (`SCOPE_DEFINITION_ID_`,`SCOPE_TYPE_`) USING BTREE,
                                       KEY `ACT_IDX_HI_IDENT_LNK_TASK` (`TASK_ID_`) USING BTREE,
                                       KEY `ACT_IDX_HI_IDENT_LNK_PROCINST` (`PROC_INST_ID_`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `act_hi_procinst`
--

DROP TABLE IF EXISTS `act_hi_procinst`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `act_hi_procinst` (
                                   `ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
                                   `REV_` int DEFAULT '1',
                                   `PROC_INST_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
                                   `BUSINESS_KEY_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                   `PROC_DEF_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
                                   `START_TIME_` datetime(3) NOT NULL,
                                   `END_TIME_` datetime(3) DEFAULT NULL,
                                   `DURATION_` bigint DEFAULT NULL,
                                   `START_USER_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                   `START_ACT_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                   `END_ACT_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                   `SUPER_PROCESS_INSTANCE_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                   `DELETE_REASON_` varchar(4000) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                   `TENANT_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT '',
                                   `NAME_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                   `CALLBACK_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                   `CALLBACK_TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                   `REFERENCE_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                   `REFERENCE_TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                   `PROPAGATED_STAGE_INST_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                   `BUSINESS_STATUS_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                   PRIMARY KEY (`ID_`) USING BTREE,
                                   UNIQUE KEY `PROC_INST_ID_` (`PROC_INST_ID_`) USING BTREE,
                                   KEY `ACT_IDX_HI_PRO_INST_END` (`END_TIME_`) USING BTREE,
                                   KEY `ACT_IDX_HI_PRO_I_BUSKEY` (`BUSINESS_KEY_`) USING BTREE,
                                   KEY `ACT_IDX_HI_PRO_SUPER_PROCINST` (`SUPER_PROCESS_INSTANCE_ID_`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `act_hi_taskinst`
--

DROP TABLE IF EXISTS `act_hi_taskinst`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `act_hi_taskinst` (
                                   `ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
                                   `REV_` int DEFAULT '1',
                                   `PROC_DEF_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                   `TASK_DEF_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                   `TASK_DEF_KEY_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                   `PROC_INST_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                   `EXECUTION_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                   `SCOPE_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                   `SUB_SCOPE_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                   `SCOPE_TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                   `SCOPE_DEFINITION_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                   `PROPAGATED_STAGE_INST_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                   `NAME_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                   `PARENT_TASK_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                   `DESCRIPTION_` varchar(4000) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                   `OWNER_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                   `ASSIGNEE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                   `START_TIME_` datetime(3) NOT NULL,
                                   `CLAIM_TIME_` datetime(3) DEFAULT NULL,
                                   `END_TIME_` datetime(3) DEFAULT NULL,
                                   `DURATION_` bigint DEFAULT NULL,
                                   `DELETE_REASON_` varchar(4000) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                   `PRIORITY_` int DEFAULT NULL,
                                   `DUE_DATE_` datetime(3) DEFAULT NULL,
                                   `FORM_KEY_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                   `CATEGORY_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                   `TENANT_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT '',
                                   `LAST_UPDATED_TIME_` datetime(3) DEFAULT NULL,
                                   PRIMARY KEY (`ID_`) USING BTREE,
                                   KEY `ACT_IDX_HI_TASK_SCOPE` (`SCOPE_ID_`,`SCOPE_TYPE_`) USING BTREE,
                                   KEY `ACT_IDX_HI_TASK_SUB_SCOPE` (`SUB_SCOPE_ID_`,`SCOPE_TYPE_`) USING BTREE,
                                   KEY `ACT_IDX_HI_TASK_SCOPE_DEF` (`SCOPE_DEFINITION_ID_`,`SCOPE_TYPE_`) USING BTREE,
                                   KEY `ACT_IDX_HI_TASK_INST_PROCINST` (`PROC_INST_ID_`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `act_hi_tsk_log`
--

DROP TABLE IF EXISTS `act_hi_tsk_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `act_hi_tsk_log` (
                                  `ID_` bigint NOT NULL AUTO_INCREMENT,
                                  `TYPE_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                  `TASK_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
                                  `TIME_STAMP_` timestamp(3) NOT NULL,
                                  `USER_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                  `DATA_` varchar(4000) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                  `EXECUTION_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                  `PROC_INST_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                  `PROC_DEF_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                  `SCOPE_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                  `SCOPE_DEFINITION_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                  `SUB_SCOPE_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                  `SCOPE_TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                  `TENANT_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT '',
                                  PRIMARY KEY (`ID_`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `act_hi_varinst`
--

DROP TABLE IF EXISTS `act_hi_varinst`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `act_hi_varinst` (
                                  `ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
                                  `REV_` int DEFAULT '1',
                                  `PROC_INST_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                  `EXECUTION_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                  `TASK_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                  `NAME_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
                                  `VAR_TYPE_` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                  `SCOPE_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                  `SUB_SCOPE_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                  `SCOPE_TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                  `BYTEARRAY_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                  `DOUBLE_` double DEFAULT NULL,
                                  `LONG_` bigint DEFAULT NULL,
                                  `TEXT_` varchar(4000) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                  `TEXT2_` varchar(4000) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                  `CREATE_TIME_` datetime(3) DEFAULT NULL,
                                  `LAST_UPDATED_TIME_` datetime(3) DEFAULT NULL,
                                  PRIMARY KEY (`ID_`) USING BTREE,
                                  KEY `ACT_IDX_HI_PROCVAR_NAME_TYPE` (`NAME_`,`VAR_TYPE_`) USING BTREE,
                                  KEY `ACT_IDX_HI_VAR_SCOPE_ID_TYPE` (`SCOPE_ID_`,`SCOPE_TYPE_`) USING BTREE,
                                  KEY `ACT_IDX_HI_VAR_SUB_ID_TYPE` (`SUB_SCOPE_ID_`,`SCOPE_TYPE_`) USING BTREE,
                                  KEY `ACT_IDX_HI_PROCVAR_PROC_INST` (`PROC_INST_ID_`) USING BTREE,
                                  KEY `ACT_IDX_HI_PROCVAR_TASK_ID` (`TASK_ID_`) USING BTREE,
                                  KEY `ACT_IDX_HI_PROCVAR_EXE` (`EXECUTION_ID_`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `act_id_bytearray`
--

DROP TABLE IF EXISTS `act_id_bytearray`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `act_id_bytearray` (
                                    `ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
                                    `REV_` int DEFAULT NULL,
                                    `NAME_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                    `BYTES_` longblob,
                                    PRIMARY KEY (`ID_`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `act_id_group`
--

DROP TABLE IF EXISTS `act_id_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `act_id_group` (
                                `ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
                                `REV_` int DEFAULT NULL,
                                `NAME_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                `TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                PRIMARY KEY (`ID_`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `act_id_info`
--

DROP TABLE IF EXISTS `act_id_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `act_id_info` (
                               `ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
                               `REV_` int DEFAULT NULL,
                               `USER_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                               `TYPE_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                               `KEY_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                               `VALUE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                               `PASSWORD_` longblob,
                               `PARENT_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                               PRIMARY KEY (`ID_`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `act_id_membership`
--

DROP TABLE IF EXISTS `act_id_membership`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `act_id_membership` (
                                     `USER_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
                                     `GROUP_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
                                     PRIMARY KEY (`USER_ID_`,`GROUP_ID_`) USING BTREE,
                                     KEY `ACT_FK_MEMB_GROUP` (`GROUP_ID_`) USING BTREE,
                                     CONSTRAINT `ACT_FK_MEMB_GROUP` FOREIGN KEY (`GROUP_ID_`) REFERENCES `act_id_group` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                                     CONSTRAINT `ACT_FK_MEMB_USER` FOREIGN KEY (`USER_ID_`) REFERENCES `act_id_user` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `act_id_priv`
--

DROP TABLE IF EXISTS `act_id_priv`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `act_id_priv` (
                               `ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
                               `NAME_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
                               PRIMARY KEY (`ID_`) USING BTREE,
                               UNIQUE KEY `ACT_UNIQ_PRIV_NAME` (`NAME_`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `act_id_priv_mapping`
--

DROP TABLE IF EXISTS `act_id_priv_mapping`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `act_id_priv_mapping` (
                                       `ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
                                       `PRIV_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
                                       `USER_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                       `GROUP_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                       PRIMARY KEY (`ID_`) USING BTREE,
                                       KEY `ACT_FK_PRIV_MAPPING` (`PRIV_ID_`) USING BTREE,
                                       KEY `ACT_IDX_PRIV_USER` (`USER_ID_`) USING BTREE,
                                       KEY `ACT_IDX_PRIV_GROUP` (`GROUP_ID_`) USING BTREE,
                                       CONSTRAINT `ACT_FK_PRIV_MAPPING` FOREIGN KEY (`PRIV_ID_`) REFERENCES `act_id_priv` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `act_id_property`
--

DROP TABLE IF EXISTS `act_id_property`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `act_id_property` (
                                   `NAME_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
                                   `VALUE_` varchar(300) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                   `REV_` int DEFAULT NULL,
                                   PRIMARY KEY (`NAME_`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `act_id_token`
--

DROP TABLE IF EXISTS `act_id_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `act_id_token` (
                                `ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
                                `REV_` int DEFAULT NULL,
                                `TOKEN_VALUE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                `TOKEN_DATE_` timestamp(3) NULL DEFAULT NULL,
                                `IP_ADDRESS_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                `USER_AGENT_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                `USER_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                `TOKEN_DATA_` varchar(2000) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                PRIMARY KEY (`ID_`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `act_id_user`
--

DROP TABLE IF EXISTS `act_id_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `act_id_user` (
                               `ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
                               `REV_` int DEFAULT NULL,
                               `FIRST_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                               `LAST_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                               `DISPLAY_NAME_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                               `EMAIL_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                               `PWD_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                               `PICTURE_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                               `TENANT_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT '',
                               PRIMARY KEY (`ID_`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `act_procdef_info`
--

DROP TABLE IF EXISTS `act_procdef_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `act_procdef_info` (
                                    `ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
                                    `PROC_DEF_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
                                    `REV_` int DEFAULT NULL,
                                    `INFO_JSON_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                    PRIMARY KEY (`ID_`) USING BTREE,
                                    UNIQUE KEY `ACT_UNIQ_INFO_PROCDEF` (`PROC_DEF_ID_`) USING BTREE,
                                    KEY `ACT_IDX_INFO_PROCDEF` (`PROC_DEF_ID_`) USING BTREE,
                                    KEY `ACT_FK_INFO_JSON_BA` (`INFO_JSON_ID_`) USING BTREE,
                                    CONSTRAINT `ACT_FK_INFO_JSON_BA` FOREIGN KEY (`INFO_JSON_ID_`) REFERENCES `act_ge_bytearray` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                                    CONSTRAINT `ACT_FK_INFO_PROCDEF` FOREIGN KEY (`PROC_DEF_ID_`) REFERENCES `act_re_procdef` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `act_re_deployment`
--

DROP TABLE IF EXISTS `act_re_deployment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `act_re_deployment` (
                                     `ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
                                     `NAME_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                     `CATEGORY_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                     `KEY_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                     `TENANT_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT '',
                                     `DEPLOY_TIME_` timestamp(3) NULL DEFAULT NULL,
                                     `DERIVED_FROM_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                     `DERIVED_FROM_ROOT_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                     `PARENT_DEPLOYMENT_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                     `ENGINE_VERSION_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                     PRIMARY KEY (`ID_`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `act_re_model`
--

DROP TABLE IF EXISTS `act_re_model`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `act_re_model` (
                                `ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
                                `REV_` int DEFAULT NULL,
                                `NAME_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                `KEY_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                `CATEGORY_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                `CREATE_TIME_` timestamp(3) NULL DEFAULT NULL,
                                `LAST_UPDATE_TIME_` timestamp(3) NULL DEFAULT NULL,
                                `VERSION_` int DEFAULT NULL,
                                `META_INFO_` varchar(4000) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                `DEPLOYMENT_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                `EDITOR_SOURCE_VALUE_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                `EDITOR_SOURCE_EXTRA_VALUE_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                `TENANT_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT '',
                                PRIMARY KEY (`ID_`) USING BTREE,
                                KEY `ACT_FK_MODEL_SOURCE` (`EDITOR_SOURCE_VALUE_ID_`) USING BTREE,
                                KEY `ACT_FK_MODEL_SOURCE_EXTRA` (`EDITOR_SOURCE_EXTRA_VALUE_ID_`) USING BTREE,
                                KEY `ACT_FK_MODEL_DEPLOYMENT` (`DEPLOYMENT_ID_`) USING BTREE,
                                CONSTRAINT `ACT_FK_MODEL_DEPLOYMENT` FOREIGN KEY (`DEPLOYMENT_ID_`) REFERENCES `act_re_deployment` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                                CONSTRAINT `ACT_FK_MODEL_SOURCE` FOREIGN KEY (`EDITOR_SOURCE_VALUE_ID_`) REFERENCES `act_ge_bytearray` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                                CONSTRAINT `ACT_FK_MODEL_SOURCE_EXTRA` FOREIGN KEY (`EDITOR_SOURCE_EXTRA_VALUE_ID_`) REFERENCES `act_ge_bytearray` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `act_re_procdef`
--

DROP TABLE IF EXISTS `act_re_procdef`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `act_re_procdef` (
                                  `ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
                                  `REV_` int DEFAULT NULL,
                                  `CATEGORY_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                  `NAME_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                  `KEY_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
                                  `VERSION_` int NOT NULL,
                                  `DEPLOYMENT_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                  `RESOURCE_NAME_` varchar(4000) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                  `DGRM_RESOURCE_NAME_` varchar(4000) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                  `DESCRIPTION_` varchar(4000) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                  `HAS_START_FORM_KEY_` tinyint DEFAULT NULL,
                                  `HAS_GRAPHICAL_NOTATION_` tinyint DEFAULT NULL,
                                  `SUSPENSION_STATE_` int DEFAULT NULL,
                                  `TENANT_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT '',
                                  `ENGINE_VERSION_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                  `DERIVED_FROM_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                  `DERIVED_FROM_ROOT_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                  `DERIVED_VERSION_` int NOT NULL DEFAULT '0',
                                  PRIMARY KEY (`ID_`) USING BTREE,
                                  UNIQUE KEY `ACT_UNIQ_PROCDEF` (`KEY_`,`VERSION_`,`DERIVED_VERSION_`,`TENANT_ID_`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `act_ru_actinst`
--

DROP TABLE IF EXISTS `act_ru_actinst`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `act_ru_actinst` (
                                  `ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
                                  `REV_` int DEFAULT '1',
                                  `PROC_DEF_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
                                  `PROC_INST_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
                                  `EXECUTION_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
                                  `ACT_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
                                  `TASK_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                  `CALL_PROC_INST_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                  `ACT_NAME_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                  `ACT_TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
                                  `ASSIGNEE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                  `START_TIME_` datetime(3) NOT NULL,
                                  `END_TIME_` datetime(3) DEFAULT NULL,
                                  `DURATION_` bigint DEFAULT NULL,
                                  `TRANSACTION_ORDER_` int DEFAULT NULL,
                                  `DELETE_REASON_` varchar(4000) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                  `TENANT_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT '',
                                  PRIMARY KEY (`ID_`) USING BTREE,
                                  KEY `ACT_IDX_RU_ACTI_START` (`START_TIME_`) USING BTREE,
                                  KEY `ACT_IDX_RU_ACTI_END` (`END_TIME_`) USING BTREE,
                                  KEY `ACT_IDX_RU_ACTI_PROC` (`PROC_INST_ID_`) USING BTREE,
                                  KEY `ACT_IDX_RU_ACTI_PROC_ACT` (`PROC_INST_ID_`,`ACT_ID_`) USING BTREE,
                                  KEY `ACT_IDX_RU_ACTI_EXEC` (`EXECUTION_ID_`) USING BTREE,
                                  KEY `ACT_IDX_RU_ACTI_EXEC_ACT` (`EXECUTION_ID_`,`ACT_ID_`) USING BTREE,
                                  KEY `ACT_IDX_RU_ACTI_TASK` (`TASK_ID_`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `act_ru_deadletter_job`
--

DROP TABLE IF EXISTS `act_ru_deadletter_job`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `act_ru_deadletter_job` (
                                         `ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
                                         `REV_` int DEFAULT NULL,
                                         `CATEGORY_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                         `TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
                                         `EXCLUSIVE_` tinyint(1) DEFAULT NULL,
                                         `EXECUTION_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                         `PROCESS_INSTANCE_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                         `PROC_DEF_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                         `ELEMENT_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                         `ELEMENT_NAME_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                         `SCOPE_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                         `SUB_SCOPE_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                         `SCOPE_TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                         `SCOPE_DEFINITION_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                         `CORRELATION_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                         `EXCEPTION_STACK_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                         `EXCEPTION_MSG_` varchar(4000) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                         `DUEDATE_` timestamp(3) NULL DEFAULT NULL,
                                         `REPEAT_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                         `HANDLER_TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                         `HANDLER_CFG_` varchar(4000) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                         `CUSTOM_VALUES_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                         `CREATE_TIME_` timestamp(3) NULL DEFAULT NULL,
                                         `TENANT_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT '',
                                         PRIMARY KEY (`ID_`) USING BTREE,
                                         KEY `ACT_IDX_DEADLETTER_JOB_EXCEPTION_STACK_ID` (`EXCEPTION_STACK_ID_`) USING BTREE,
                                         KEY `ACT_IDX_DEADLETTER_JOB_CUSTOM_VALUES_ID` (`CUSTOM_VALUES_ID_`) USING BTREE,
                                         KEY `ACT_IDX_DEADLETTER_JOB_CORRELATION_ID` (`CORRELATION_ID_`) USING BTREE,
                                         KEY `ACT_IDX_DJOB_SCOPE` (`SCOPE_ID_`,`SCOPE_TYPE_`) USING BTREE,
                                         KEY `ACT_IDX_DJOB_SUB_SCOPE` (`SUB_SCOPE_ID_`,`SCOPE_TYPE_`) USING BTREE,
                                         KEY `ACT_IDX_DJOB_SCOPE_DEF` (`SCOPE_DEFINITION_ID_`,`SCOPE_TYPE_`) USING BTREE,
                                         KEY `ACT_FK_DEADLETTER_JOB_EXECUTION` (`EXECUTION_ID_`) USING BTREE,
                                         KEY `ACT_FK_DEADLETTER_JOB_PROCESS_INSTANCE` (`PROCESS_INSTANCE_ID_`) USING BTREE,
                                         KEY `ACT_FK_DEADLETTER_JOB_PROC_DEF` (`PROC_DEF_ID_`) USING BTREE,
                                         CONSTRAINT `ACT_FK_DEADLETTER_JOB_CUSTOM_VALUES` FOREIGN KEY (`CUSTOM_VALUES_ID_`) REFERENCES `act_ge_bytearray` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                                         CONSTRAINT `ACT_FK_DEADLETTER_JOB_EXCEPTION` FOREIGN KEY (`EXCEPTION_STACK_ID_`) REFERENCES `act_ge_bytearray` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                                         CONSTRAINT `ACT_FK_DEADLETTER_JOB_EXECUTION` FOREIGN KEY (`EXECUTION_ID_`) REFERENCES `act_ru_execution` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                                         CONSTRAINT `ACT_FK_DEADLETTER_JOB_PROC_DEF` FOREIGN KEY (`PROC_DEF_ID_`) REFERENCES `act_re_procdef` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                                         CONSTRAINT `ACT_FK_DEADLETTER_JOB_PROCESS_INSTANCE` FOREIGN KEY (`PROCESS_INSTANCE_ID_`) REFERENCES `act_ru_execution` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `act_ru_entitylink`
--

DROP TABLE IF EXISTS `act_ru_entitylink`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `act_ru_entitylink` (
                                     `ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
                                     `REV_` int DEFAULT NULL,
                                     `CREATE_TIME_` datetime(3) DEFAULT NULL,
                                     `LINK_TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                     `SCOPE_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                     `SUB_SCOPE_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                     `SCOPE_TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                     `SCOPE_DEFINITION_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                     `PARENT_ELEMENT_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                     `REF_SCOPE_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                     `REF_SCOPE_TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                     `REF_SCOPE_DEFINITION_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                     `ROOT_SCOPE_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                     `ROOT_SCOPE_TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                     `HIERARCHY_TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                     PRIMARY KEY (`ID_`) USING BTREE,
                                     KEY `ACT_IDX_ENT_LNK_SCOPE` (`SCOPE_ID_`,`SCOPE_TYPE_`,`LINK_TYPE_`) USING BTREE,
                                     KEY `ACT_IDX_ENT_LNK_REF_SCOPE` (`REF_SCOPE_ID_`,`REF_SCOPE_TYPE_`,`LINK_TYPE_`) USING BTREE,
                                     KEY `ACT_IDX_ENT_LNK_ROOT_SCOPE` (`ROOT_SCOPE_ID_`,`ROOT_SCOPE_TYPE_`,`LINK_TYPE_`) USING BTREE,
                                     KEY `ACT_IDX_ENT_LNK_SCOPE_DEF` (`SCOPE_DEFINITION_ID_`,`SCOPE_TYPE_`,`LINK_TYPE_`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `act_ru_event_subscr`
--

DROP TABLE IF EXISTS `act_ru_event_subscr`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `act_ru_event_subscr` (
                                       `ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
                                       `REV_` int DEFAULT NULL,
                                       `EVENT_TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
                                       `EVENT_NAME_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                       `EXECUTION_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                       `PROC_INST_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                       `ACTIVITY_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                       `CONFIGURATION_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                       `CREATED_` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
                                       `PROC_DEF_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                       `SUB_SCOPE_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                       `SCOPE_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                       `SCOPE_DEFINITION_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                       `SCOPE_TYPE_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                       `LOCK_TIME_` timestamp(3) NULL DEFAULT NULL,
                                       `LOCK_OWNER_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                       `TENANT_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT '',
                                       PRIMARY KEY (`ID_`) USING BTREE,
                                       KEY `ACT_IDX_EVENT_SUBSCR_CONFIG_` (`CONFIGURATION_`) USING BTREE,
                                       KEY `ACT_IDX_EVENT_SUBSCR_SCOPEREF_` (`SCOPE_ID_`,`SCOPE_TYPE_`) USING BTREE,
                                       KEY `ACT_FK_EVENT_EXEC` (`EXECUTION_ID_`) USING BTREE,
                                       CONSTRAINT `ACT_FK_EVENT_EXEC` FOREIGN KEY (`EXECUTION_ID_`) REFERENCES `act_ru_execution` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `act_ru_execution`
--

DROP TABLE IF EXISTS `act_ru_execution`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `act_ru_execution` (
                                    `ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
                                    `REV_` int DEFAULT NULL,
                                    `PROC_INST_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                    `BUSINESS_KEY_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                    `PARENT_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                    `PROC_DEF_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                    `SUPER_EXEC_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                    `ROOT_PROC_INST_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                    `ACT_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                    `IS_ACTIVE_` tinyint DEFAULT NULL,
                                    `IS_CONCURRENT_` tinyint DEFAULT NULL,
                                    `IS_SCOPE_` tinyint DEFAULT NULL,
                                    `IS_EVENT_SCOPE_` tinyint DEFAULT NULL,
                                    `IS_MI_ROOT_` tinyint DEFAULT NULL,
                                    `SUSPENSION_STATE_` int DEFAULT NULL,
                                    `CACHED_ENT_STATE_` int DEFAULT NULL,
                                    `TENANT_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT '',
                                    `NAME_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                    `START_ACT_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                    `START_TIME_` datetime(3) DEFAULT NULL,
                                    `START_USER_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                    `LOCK_TIME_` timestamp(3) NULL DEFAULT NULL,
                                    `LOCK_OWNER_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                    `IS_COUNT_ENABLED_` tinyint DEFAULT NULL,
                                    `EVT_SUBSCR_COUNT_` int DEFAULT NULL,
                                    `TASK_COUNT_` int DEFAULT NULL,
                                    `JOB_COUNT_` int DEFAULT NULL,
                                    `TIMER_JOB_COUNT_` int DEFAULT NULL,
                                    `SUSP_JOB_COUNT_` int DEFAULT NULL,
                                    `DEADLETTER_JOB_COUNT_` int DEFAULT NULL,
                                    `EXTERNAL_WORKER_JOB_COUNT_` int DEFAULT NULL,
                                    `VAR_COUNT_` int DEFAULT NULL,
                                    `ID_LINK_COUNT_` int DEFAULT NULL,
                                    `CALLBACK_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                    `CALLBACK_TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                    `REFERENCE_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                    `REFERENCE_TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                    `PROPAGATED_STAGE_INST_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                    `BUSINESS_STATUS_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                    PRIMARY KEY (`ID_`) USING BTREE,
                                    KEY `ACT_IDX_EXEC_BUSKEY` (`BUSINESS_KEY_`) USING BTREE,
                                    KEY `ACT_IDC_EXEC_ROOT` (`ROOT_PROC_INST_ID_`) USING BTREE,
                                    KEY `ACT_IDX_EXEC_REF_ID_` (`REFERENCE_ID_`) USING BTREE,
                                    KEY `ACT_FK_EXE_PROCINST` (`PROC_INST_ID_`) USING BTREE,
                                    KEY `ACT_FK_EXE_PARENT` (`PARENT_ID_`) USING BTREE,
                                    KEY `ACT_FK_EXE_SUPER` (`SUPER_EXEC_`) USING BTREE,
                                    KEY `ACT_FK_EXE_PROCDEF` (`PROC_DEF_ID_`) USING BTREE,
                                    CONSTRAINT `ACT_FK_EXE_PARENT` FOREIGN KEY (`PARENT_ID_`) REFERENCES `act_ru_execution` (`ID_`) ON DELETE CASCADE ON UPDATE RESTRICT,
                                    CONSTRAINT `ACT_FK_EXE_PROCDEF` FOREIGN KEY (`PROC_DEF_ID_`) REFERENCES `act_re_procdef` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                                    CONSTRAINT `ACT_FK_EXE_PROCINST` FOREIGN KEY (`PROC_INST_ID_`) REFERENCES `act_ru_execution` (`ID_`) ON DELETE CASCADE ON UPDATE CASCADE,
                                    CONSTRAINT `ACT_FK_EXE_SUPER` FOREIGN KEY (`SUPER_EXEC_`) REFERENCES `act_ru_execution` (`ID_`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `act_ru_external_job`
--

DROP TABLE IF EXISTS `act_ru_external_job`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `act_ru_external_job` (
                                       `ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
                                       `REV_` int DEFAULT NULL,
                                       `CATEGORY_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                       `TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
                                       `LOCK_EXP_TIME_` timestamp(3) NULL DEFAULT NULL,
                                       `LOCK_OWNER_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                       `EXCLUSIVE_` tinyint(1) DEFAULT NULL,
                                       `EXECUTION_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                       `PROCESS_INSTANCE_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                       `PROC_DEF_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                       `ELEMENT_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                       `ELEMENT_NAME_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                       `SCOPE_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                       `SUB_SCOPE_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                       `SCOPE_TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                       `SCOPE_DEFINITION_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                       `CORRELATION_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                       `RETRIES_` int DEFAULT NULL,
                                       `EXCEPTION_STACK_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                       `EXCEPTION_MSG_` varchar(4000) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                       `DUEDATE_` timestamp(3) NULL DEFAULT NULL,
                                       `REPEAT_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                       `HANDLER_TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                       `HANDLER_CFG_` varchar(4000) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                       `CUSTOM_VALUES_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                       `CREATE_TIME_` timestamp(3) NULL DEFAULT NULL,
                                       `TENANT_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT '',
                                       PRIMARY KEY (`ID_`) USING BTREE,
                                       KEY `ACT_IDX_EXTERNAL_JOB_EXCEPTION_STACK_ID` (`EXCEPTION_STACK_ID_`) USING BTREE,
                                       KEY `ACT_IDX_EXTERNAL_JOB_CUSTOM_VALUES_ID` (`CUSTOM_VALUES_ID_`) USING BTREE,
                                       KEY `ACT_IDX_EXTERNAL_JOB_CORRELATION_ID` (`CORRELATION_ID_`) USING BTREE,
                                       KEY `ACT_IDX_EJOB_SCOPE` (`SCOPE_ID_`,`SCOPE_TYPE_`) USING BTREE,
                                       KEY `ACT_IDX_EJOB_SUB_SCOPE` (`SUB_SCOPE_ID_`,`SCOPE_TYPE_`) USING BTREE,
                                       KEY `ACT_IDX_EJOB_SCOPE_DEF` (`SCOPE_DEFINITION_ID_`,`SCOPE_TYPE_`) USING BTREE,
                                       CONSTRAINT `ACT_FK_EXTERNAL_JOB_CUSTOM_VALUES` FOREIGN KEY (`CUSTOM_VALUES_ID_`) REFERENCES `act_ge_bytearray` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                                       CONSTRAINT `ACT_FK_EXTERNAL_JOB_EXCEPTION` FOREIGN KEY (`EXCEPTION_STACK_ID_`) REFERENCES `act_ge_bytearray` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `act_ru_history_job`
--

DROP TABLE IF EXISTS `act_ru_history_job`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `act_ru_history_job` (
                                      `ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
                                      `REV_` int DEFAULT NULL,
                                      `LOCK_EXP_TIME_` timestamp(3) NULL DEFAULT NULL,
                                      `LOCK_OWNER_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                      `RETRIES_` int DEFAULT NULL,
                                      `EXCEPTION_STACK_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                      `EXCEPTION_MSG_` varchar(4000) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                      `HANDLER_TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                      `HANDLER_CFG_` varchar(4000) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                      `CUSTOM_VALUES_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                      `ADV_HANDLER_CFG_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                      `CREATE_TIME_` timestamp(3) NULL DEFAULT NULL,
                                      `SCOPE_TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                      `TENANT_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT '',
                                      PRIMARY KEY (`ID_`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `act_ru_identitylink`
--

DROP TABLE IF EXISTS `act_ru_identitylink`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `act_ru_identitylink` (
                                       `ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
                                       `REV_` int DEFAULT NULL,
                                       `GROUP_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                       `TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                       `USER_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                       `TASK_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                       `PROC_INST_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                       `PROC_DEF_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                       `SCOPE_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                       `SUB_SCOPE_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                       `SCOPE_TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                       `SCOPE_DEFINITION_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                       PRIMARY KEY (`ID_`) USING BTREE,
                                       KEY `ACT_IDX_IDENT_LNK_USER` (`USER_ID_`) USING BTREE,
                                       KEY `ACT_IDX_IDENT_LNK_GROUP` (`GROUP_ID_`) USING BTREE,
                                       KEY `ACT_IDX_IDENT_LNK_SCOPE` (`SCOPE_ID_`,`SCOPE_TYPE_`) USING BTREE,
                                       KEY `ACT_IDX_IDENT_LNK_SUB_SCOPE` (`SUB_SCOPE_ID_`,`SCOPE_TYPE_`) USING BTREE,
                                       KEY `ACT_IDX_IDENT_LNK_SCOPE_DEF` (`SCOPE_DEFINITION_ID_`,`SCOPE_TYPE_`) USING BTREE,
                                       KEY `ACT_IDX_ATHRZ_PROCEDEF` (`PROC_DEF_ID_`) USING BTREE,
                                       KEY `ACT_FK_TSKASS_TASK` (`TASK_ID_`) USING BTREE,
                                       KEY `ACT_FK_IDL_PROCINST` (`PROC_INST_ID_`) USING BTREE,
                                       CONSTRAINT `ACT_FK_ATHRZ_PROCEDEF` FOREIGN KEY (`PROC_DEF_ID_`) REFERENCES `act_re_procdef` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                                       CONSTRAINT `ACT_FK_IDL_PROCINST` FOREIGN KEY (`PROC_INST_ID_`) REFERENCES `act_ru_execution` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                                       CONSTRAINT `ACT_FK_TSKASS_TASK` FOREIGN KEY (`TASK_ID_`) REFERENCES `act_ru_task` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `act_ru_job`
--

DROP TABLE IF EXISTS `act_ru_job`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `act_ru_job` (
                              `ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
                              `REV_` int DEFAULT NULL,
                              `CATEGORY_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                              `TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
                              `LOCK_EXP_TIME_` timestamp(3) NULL DEFAULT NULL,
                              `LOCK_OWNER_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                              `EXCLUSIVE_` tinyint(1) DEFAULT NULL,
                              `EXECUTION_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                              `PROCESS_INSTANCE_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                              `PROC_DEF_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                              `ELEMENT_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                              `ELEMENT_NAME_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                              `SCOPE_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                              `SUB_SCOPE_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                              `SCOPE_TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                              `SCOPE_DEFINITION_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                              `CORRELATION_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                              `RETRIES_` int DEFAULT NULL,
                              `EXCEPTION_STACK_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                              `EXCEPTION_MSG_` varchar(4000) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                              `DUEDATE_` timestamp(3) NULL DEFAULT NULL,
                              `REPEAT_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                              `HANDLER_TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                              `HANDLER_CFG_` varchar(4000) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                              `CUSTOM_VALUES_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                              `CREATE_TIME_` timestamp(3) NULL DEFAULT NULL,
                              `TENANT_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT '',
                              PRIMARY KEY (`ID_`) USING BTREE,
                              KEY `ACT_IDX_JOB_EXCEPTION_STACK_ID` (`EXCEPTION_STACK_ID_`) USING BTREE,
                              KEY `ACT_IDX_JOB_CUSTOM_VALUES_ID` (`CUSTOM_VALUES_ID_`) USING BTREE,
                              KEY `ACT_IDX_JOB_CORRELATION_ID` (`CORRELATION_ID_`) USING BTREE,
                              KEY `ACT_IDX_JOB_SCOPE` (`SCOPE_ID_`,`SCOPE_TYPE_`) USING BTREE,
                              KEY `ACT_IDX_JOB_SUB_SCOPE` (`SUB_SCOPE_ID_`,`SCOPE_TYPE_`) USING BTREE,
                              KEY `ACT_IDX_JOB_SCOPE_DEF` (`SCOPE_DEFINITION_ID_`,`SCOPE_TYPE_`) USING BTREE,
                              KEY `ACT_FK_JOB_EXECUTION` (`EXECUTION_ID_`) USING BTREE,
                              KEY `ACT_FK_JOB_PROCESS_INSTANCE` (`PROCESS_INSTANCE_ID_`) USING BTREE,
                              KEY `ACT_FK_JOB_PROC_DEF` (`PROC_DEF_ID_`) USING BTREE,
                              CONSTRAINT `ACT_FK_JOB_CUSTOM_VALUES` FOREIGN KEY (`CUSTOM_VALUES_ID_`) REFERENCES `act_ge_bytearray` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                              CONSTRAINT `ACT_FK_JOB_EXCEPTION` FOREIGN KEY (`EXCEPTION_STACK_ID_`) REFERENCES `act_ge_bytearray` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                              CONSTRAINT `ACT_FK_JOB_EXECUTION` FOREIGN KEY (`EXECUTION_ID_`) REFERENCES `act_ru_execution` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                              CONSTRAINT `ACT_FK_JOB_PROC_DEF` FOREIGN KEY (`PROC_DEF_ID_`) REFERENCES `act_re_procdef` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                              CONSTRAINT `ACT_FK_JOB_PROCESS_INSTANCE` FOREIGN KEY (`PROCESS_INSTANCE_ID_`) REFERENCES `act_ru_execution` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `act_ru_suspended_job`
--

DROP TABLE IF EXISTS `act_ru_suspended_job`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `act_ru_suspended_job` (
                                        `ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
                                        `REV_` int DEFAULT NULL,
                                        `CATEGORY_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                        `TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
                                        `EXCLUSIVE_` tinyint(1) DEFAULT NULL,
                                        `EXECUTION_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                        `PROCESS_INSTANCE_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                        `PROC_DEF_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                        `ELEMENT_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                        `ELEMENT_NAME_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                        `SCOPE_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                        `SUB_SCOPE_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                        `SCOPE_TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                        `SCOPE_DEFINITION_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                        `CORRELATION_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                        `RETRIES_` int DEFAULT NULL,
                                        `EXCEPTION_STACK_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                        `EXCEPTION_MSG_` varchar(4000) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                        `DUEDATE_` timestamp(3) NULL DEFAULT NULL,
                                        `REPEAT_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                        `HANDLER_TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                        `HANDLER_CFG_` varchar(4000) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                        `CUSTOM_VALUES_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                        `CREATE_TIME_` timestamp(3) NULL DEFAULT NULL,
                                        `TENANT_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT '',
                                        PRIMARY KEY (`ID_`) USING BTREE,
                                        KEY `ACT_IDX_SUSPENDED_JOB_EXCEPTION_STACK_ID` (`EXCEPTION_STACK_ID_`) USING BTREE,
                                        KEY `ACT_IDX_SUSPENDED_JOB_CUSTOM_VALUES_ID` (`CUSTOM_VALUES_ID_`) USING BTREE,
                                        KEY `ACT_IDX_SUSPENDED_JOB_CORRELATION_ID` (`CORRELATION_ID_`) USING BTREE,
                                        KEY `ACT_IDX_SJOB_SCOPE` (`SCOPE_ID_`,`SCOPE_TYPE_`) USING BTREE,
                                        KEY `ACT_IDX_SJOB_SUB_SCOPE` (`SUB_SCOPE_ID_`,`SCOPE_TYPE_`) USING BTREE,
                                        KEY `ACT_IDX_SJOB_SCOPE_DEF` (`SCOPE_DEFINITION_ID_`,`SCOPE_TYPE_`) USING BTREE,
                                        KEY `ACT_FK_SUSPENDED_JOB_EXECUTION` (`EXECUTION_ID_`) USING BTREE,
                                        KEY `ACT_FK_SUSPENDED_JOB_PROCESS_INSTANCE` (`PROCESS_INSTANCE_ID_`) USING BTREE,
                                        KEY `ACT_FK_SUSPENDED_JOB_PROC_DEF` (`PROC_DEF_ID_`) USING BTREE,
                                        CONSTRAINT `ACT_FK_SUSPENDED_JOB_CUSTOM_VALUES` FOREIGN KEY (`CUSTOM_VALUES_ID_`) REFERENCES `act_ge_bytearray` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                                        CONSTRAINT `ACT_FK_SUSPENDED_JOB_EXCEPTION` FOREIGN KEY (`EXCEPTION_STACK_ID_`) REFERENCES `act_ge_bytearray` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                                        CONSTRAINT `ACT_FK_SUSPENDED_JOB_EXECUTION` FOREIGN KEY (`EXECUTION_ID_`) REFERENCES `act_ru_execution` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                                        CONSTRAINT `ACT_FK_SUSPENDED_JOB_PROC_DEF` FOREIGN KEY (`PROC_DEF_ID_`) REFERENCES `act_re_procdef` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                                        CONSTRAINT `ACT_FK_SUSPENDED_JOB_PROCESS_INSTANCE` FOREIGN KEY (`PROCESS_INSTANCE_ID_`) REFERENCES `act_ru_execution` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `act_ru_task`
--

DROP TABLE IF EXISTS `act_ru_task`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `act_ru_task` (
                               `ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
                               `REV_` int DEFAULT NULL,
                               `EXECUTION_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                               `PROC_INST_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                               `PROC_DEF_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                               `TASK_DEF_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                               `SCOPE_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                               `SUB_SCOPE_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                               `SCOPE_TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                               `SCOPE_DEFINITION_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                               `PROPAGATED_STAGE_INST_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                               `NAME_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                               `PARENT_TASK_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                               `DESCRIPTION_` varchar(4000) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                               `TASK_DEF_KEY_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                               `OWNER_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                               `ASSIGNEE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                               `DELEGATION_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                               `PRIORITY_` int DEFAULT NULL,
                               `CREATE_TIME_` timestamp(3) NULL DEFAULT NULL,
                               `DUE_DATE_` datetime(3) DEFAULT NULL,
                               `CATEGORY_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                               `SUSPENSION_STATE_` int DEFAULT NULL,
                               `TENANT_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT '',
                               `FORM_KEY_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                               `CLAIM_TIME_` datetime(3) DEFAULT NULL,
                               `IS_COUNT_ENABLED_` tinyint DEFAULT NULL,
                               `VAR_COUNT_` int DEFAULT NULL,
                               `ID_LINK_COUNT_` int DEFAULT NULL,
                               `SUB_TASK_COUNT_` int DEFAULT NULL,
                               PRIMARY KEY (`ID_`) USING BTREE,
                               KEY `ACT_IDX_TASK_CREATE` (`CREATE_TIME_`) USING BTREE,
                               KEY `ACT_IDX_TASK_SCOPE` (`SCOPE_ID_`,`SCOPE_TYPE_`) USING BTREE,
                               KEY `ACT_IDX_TASK_SUB_SCOPE` (`SUB_SCOPE_ID_`,`SCOPE_TYPE_`) USING BTREE,
                               KEY `ACT_IDX_TASK_SCOPE_DEF` (`SCOPE_DEFINITION_ID_`,`SCOPE_TYPE_`) USING BTREE,
                               KEY `ACT_FK_TASK_EXE` (`EXECUTION_ID_`) USING BTREE,
                               KEY `ACT_FK_TASK_PROCINST` (`PROC_INST_ID_`) USING BTREE,
                               KEY `ACT_FK_TASK_PROCDEF` (`PROC_DEF_ID_`) USING BTREE,
                               CONSTRAINT `ACT_FK_TASK_EXE` FOREIGN KEY (`EXECUTION_ID_`) REFERENCES `act_ru_execution` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                               CONSTRAINT `ACT_FK_TASK_PROCDEF` FOREIGN KEY (`PROC_DEF_ID_`) REFERENCES `act_re_procdef` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                               CONSTRAINT `ACT_FK_TASK_PROCINST` FOREIGN KEY (`PROC_INST_ID_`) REFERENCES `act_ru_execution` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `act_ru_timer_job`
--

DROP TABLE IF EXISTS `act_ru_timer_job`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `act_ru_timer_job` (
                                    `ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
                                    `REV_` int DEFAULT NULL,
                                    `CATEGORY_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                    `TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
                                    `LOCK_EXP_TIME_` timestamp(3) NULL DEFAULT NULL,
                                    `LOCK_OWNER_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                    `EXCLUSIVE_` tinyint(1) DEFAULT NULL,
                                    `EXECUTION_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                    `PROCESS_INSTANCE_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                    `PROC_DEF_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                    `ELEMENT_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                    `ELEMENT_NAME_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                    `SCOPE_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                    `SUB_SCOPE_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                    `SCOPE_TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                    `SCOPE_DEFINITION_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                    `CORRELATION_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                    `RETRIES_` int DEFAULT NULL,
                                    `EXCEPTION_STACK_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                    `EXCEPTION_MSG_` varchar(4000) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                    `DUEDATE_` timestamp(3) NULL DEFAULT NULL,
                                    `REPEAT_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                    `HANDLER_TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                    `HANDLER_CFG_` varchar(4000) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                    `CUSTOM_VALUES_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                    `CREATE_TIME_` timestamp(3) NULL DEFAULT NULL,
                                    `TENANT_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT '',
                                    PRIMARY KEY (`ID_`) USING BTREE,
                                    KEY `ACT_IDX_TIMER_JOB_EXCEPTION_STACK_ID` (`EXCEPTION_STACK_ID_`) USING BTREE,
                                    KEY `ACT_IDX_TIMER_JOB_CUSTOM_VALUES_ID` (`CUSTOM_VALUES_ID_`) USING BTREE,
                                    KEY `ACT_IDX_TIMER_JOB_CORRELATION_ID` (`CORRELATION_ID_`) USING BTREE,
                                    KEY `ACT_IDX_TIMER_JOB_DUEDATE` (`DUEDATE_`) USING BTREE,
                                    KEY `ACT_IDX_TJOB_SCOPE` (`SCOPE_ID_`,`SCOPE_TYPE_`) USING BTREE,
                                    KEY `ACT_IDX_TJOB_SUB_SCOPE` (`SUB_SCOPE_ID_`,`SCOPE_TYPE_`) USING BTREE,
                                    KEY `ACT_IDX_TJOB_SCOPE_DEF` (`SCOPE_DEFINITION_ID_`,`SCOPE_TYPE_`) USING BTREE,
                                    KEY `ACT_FK_TIMER_JOB_EXECUTION` (`EXECUTION_ID_`) USING BTREE,
                                    KEY `ACT_FK_TIMER_JOB_PROCESS_INSTANCE` (`PROCESS_INSTANCE_ID_`) USING BTREE,
                                    KEY `ACT_FK_TIMER_JOB_PROC_DEF` (`PROC_DEF_ID_`) USING BTREE,
                                    CONSTRAINT `ACT_FK_TIMER_JOB_CUSTOM_VALUES` FOREIGN KEY (`CUSTOM_VALUES_ID_`) REFERENCES `act_ge_bytearray` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                                    CONSTRAINT `ACT_FK_TIMER_JOB_EXCEPTION` FOREIGN KEY (`EXCEPTION_STACK_ID_`) REFERENCES `act_ge_bytearray` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                                    CONSTRAINT `ACT_FK_TIMER_JOB_EXECUTION` FOREIGN KEY (`EXECUTION_ID_`) REFERENCES `act_ru_execution` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                                    CONSTRAINT `ACT_FK_TIMER_JOB_PROC_DEF` FOREIGN KEY (`PROC_DEF_ID_`) REFERENCES `act_re_procdef` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                                    CONSTRAINT `ACT_FK_TIMER_JOB_PROCESS_INSTANCE` FOREIGN KEY (`PROCESS_INSTANCE_ID_`) REFERENCES `act_ru_execution` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `act_ru_variable`
--

DROP TABLE IF EXISTS `act_ru_variable`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `act_ru_variable` (
                                   `ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
                                   `REV_` int DEFAULT NULL,
                                   `TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
                                   `NAME_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
                                   `EXECUTION_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                   `PROC_INST_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                   `TASK_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                   `SCOPE_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                   `SUB_SCOPE_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                   `SCOPE_TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                   `BYTEARRAY_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                   `DOUBLE_` double DEFAULT NULL,
                                   `LONG_` bigint DEFAULT NULL,
                                   `TEXT_` varchar(4000) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                   `TEXT2_` varchar(4000) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                   PRIMARY KEY (`ID_`) USING BTREE,
                                   KEY `ACT_IDX_RU_VAR_SCOPE_ID_TYPE` (`SCOPE_ID_`,`SCOPE_TYPE_`) USING BTREE,
                                   KEY `ACT_IDX_RU_VAR_SUB_ID_TYPE` (`SUB_SCOPE_ID_`,`SCOPE_TYPE_`) USING BTREE,
                                   KEY `ACT_FK_VAR_BYTEARRAY` (`BYTEARRAY_ID_`) USING BTREE,
                                   KEY `ACT_IDX_VARIABLE_TASK_ID` (`TASK_ID_`) USING BTREE,
                                   KEY `ACT_FK_VAR_EXE` (`EXECUTION_ID_`) USING BTREE,
                                   KEY `ACT_FK_VAR_PROCINST` (`PROC_INST_ID_`) USING BTREE,
                                   CONSTRAINT `ACT_FK_VAR_BYTEARRAY` FOREIGN KEY (`BYTEARRAY_ID_`) REFERENCES `act_ge_bytearray` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                                   CONSTRAINT `ACT_FK_VAR_EXE` FOREIGN KEY (`EXECUTION_ID_`) REFERENCES `act_ru_execution` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                                   CONSTRAINT `ACT_FK_VAR_PROCINST` FOREIGN KEY (`PROC_INST_ID_`) REFERENCES `act_ru_execution` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `bpm_form`
--

DROP TABLE IF EXISTS `bpm_form`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bpm_form` (
                            `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
                            `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '表单名',
                            `status` tinyint NOT NULL COMMENT '开启状态',
                            `conf` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '表单的配置',
                            `fields` varchar(5000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '表单项的数组',
                            `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
                            `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
                            `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
                            `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                            `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
                            `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
                            PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工作流的表单定义';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `bpm_model_ext`
--

DROP TABLE IF EXISTS `bpm_model_ext`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bpm_model_ext` (
                                 `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                                 `model_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '流程定义key',
                                 `transfer_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否转给上级领导审批',
                                 `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                 `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                 `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                 `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                 `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除  0：有效 1：删除',
                                 PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=55 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='流程模型拓展表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `bpm_oa_leave`
--

DROP TABLE IF EXISTS `bpm_oa_leave`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bpm_oa_leave` (
                                `id` bigint NOT NULL AUTO_INCREMENT COMMENT '请假表单主键',
                                `user_id` bigint NOT NULL COMMENT '申请人的用户编号',
                                `type` tinyint NOT NULL COMMENT '请假类型',
                                `reason` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '请假原因',
                                `start_time` datetime NOT NULL COMMENT '开始时间',
                                `end_time` datetime NOT NULL COMMENT '结束时间',
                                `day` tinyint NOT NULL COMMENT '请假天数',
                                `result` tinyint NOT NULL COMMENT '请假结果',
                                `process_instance_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '流程实例的编号',
                                `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
                                `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
                                `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
                                `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
                                PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='OA 请假申请表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `bpm_process_definition_ext`
--

DROP TABLE IF EXISTS `bpm_process_definition_ext`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bpm_process_definition_ext` (
                                              `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
                                              `process_definition_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '流程定义的编号',
                                              `model_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '流程模型的编号',
                                              `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '描述',
                                              `form_type` tinyint NOT NULL COMMENT '表单类型',
                                              `form_id` bigint DEFAULT NULL COMMENT '表单编号',
                                              `form_conf` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '表单的配置',
                                              `form_fields` varchar(5000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '表单项的数组',
                                              `form_custom_create_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '自定义表单的提交路径',
                                              `form_custom_view_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '自定义表单的查看路径',
                                              `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
                                              `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                              `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
                                              `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                              `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
                                              `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
                                              PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=428 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='Bpm 流程定义的拓展表\n';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `bpm_process_instance_ext`
--

DROP TABLE IF EXISTS `bpm_process_instance_ext`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bpm_process_instance_ext` (
                                            `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
                                            `start_user_id` bigint NOT NULL COMMENT '发起流程的用户编号',
                                            `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '流程实例的名字',
                                            `process_instance_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '流程实例的编号',
                                            `process_definition_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '流程定义的编号',
                                            `category` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '流程分类',
                                            `status` tinyint NOT NULL COMMENT '流程实例的状态',
                                            `result` tinyint NOT NULL COMMENT '流程实例的结果',
                                            `end_time` datetime DEFAULT NULL COMMENT '结束时间',
                                            `form_variables` varchar(5000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '表单值',
                                            `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
                                            `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                            `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
                                            `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                            `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
                                            `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
                                            `audit_able_id` bigint DEFAULT NULL COMMENT '流程业务id',
                                            PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=14817 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工作流的流程实例的拓展';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `bpm_process_instance_ext_bk`
--

DROP TABLE IF EXISTS `bpm_process_instance_ext_bk`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bpm_process_instance_ext_bk` (
                                               `id` bigint NOT NULL DEFAULT '0' COMMENT '编号',
                                               `start_user_id` bigint NOT NULL COMMENT '发起流程的用户编号',
                                               `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '流程实例的名字',
                                               `process_instance_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '流程实例的编号',
                                               `process_definition_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '流程定义的编号',
                                               `category` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '流程分类',
                                               `status` tinyint NOT NULL COMMENT '流程实例的状态',
                                               `result` tinyint NOT NULL COMMENT '流程实例的结果',
                                               `end_time` datetime DEFAULT NULL COMMENT '结束时间',
                                               `form_variables` varchar(5000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '表单值',
                                               `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
                                               `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                               `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
                                               `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                               `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
                                               `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
                                               `audit_able_id` bigint DEFAULT NULL COMMENT '流程业务id'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `bpm_task_assign_rule`
--

DROP TABLE IF EXISTS `bpm_task_assign_rule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bpm_task_assign_rule` (
                                        `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
                                        `model_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '流程模型的编号',
                                        `process_definition_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '流程定义的编号',
                                        `task_definition_key` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '流程任务定义的 key',
                                        `type` tinyint NOT NULL COMMENT '规则类型',
                                        `options` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '规则值，JSON 数组',
                                        `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
                                        `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                        `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
                                        `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                        `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
                                        `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
                                        PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1337 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='Bpm 任务规则表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `bpm_task_ext`
--

DROP TABLE IF EXISTS `bpm_task_ext`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bpm_task_ext` (
                                `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
                                `assignee_user_id` bigint DEFAULT NULL COMMENT '任务的审批人',
                                `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '任务的名字',
                                `task_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '任务的编号',
                                `result` tinyint NOT NULL COMMENT '任务的结果',
                                `reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '审批建议',
                                `end_time` datetime DEFAULT NULL COMMENT '任务的结束时间',
                                `process_instance_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '流程实例的编号',
                                `process_definition_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '流程定义的编号',
                                `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
                                `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
                                `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
                                `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
                                PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=20910 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工作流的流程任务的拓展表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `bpm_user_group`
--

DROP TABLE IF EXISTS `bpm_user_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bpm_user_group` (
                                  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
                                  `name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '组名',
                                  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '描述',
                                  `member_user_ids` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '成员编号数组',
                                  `status` tinyint NOT NULL COMMENT '状态（0正常 1停用）',
                                  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
                                  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
                                  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
                                  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
                                  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户组';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `company_path`
--

DROP TABLE IF EXISTS `company_path`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `company_path` (
                                `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                `path` json NOT NULL DEFAULT (json_object()) COMMENT '路径',
                                `status` tinyint NOT NULL COMMENT '状态',
                                `description` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '描述',
                                `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除',
                                `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='公司路径';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `crm_category`
--

DROP TABLE IF EXISTS `crm_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `crm_category` (
                                `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                                `code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '分类编号',
                                `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '名称',
                                `code_len` int DEFAULT NULL COMMENT '流水号长度',
                                `parent_id` bigint unsigned DEFAULT NULL COMMENT '父节点编号',
                                `category_type` tinyint DEFAULT NULL COMMENT '种类',
                                `grade` int DEFAULT NULL COMMENT '级别',
                                `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除  0：有效 1：删除',
                                PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='产品分类表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `crm_collection_account`
--

DROP TABLE IF EXISTS `crm_collection_account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `crm_collection_account` (
                                          `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                          `cust_id` bigint DEFAULT NULL COMMENT '客户id',
                                          `default_flag` int NOT NULL DEFAULT '0' COMMENT '默认标记',
                                          `cust_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '客户编号',
                                          `company_id` bigint NOT NULL DEFAULT '0' COMMENT '内部公司主键',
                                          `company_bank_id` bigint NOT NULL DEFAULT '0' COMMENT '内部公司银行账号主键',
                                          `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                          `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                          `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                          `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除',
                                          `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                          PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='收款账号';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `crm_company_path`
--

DROP TABLE IF EXISTS `crm_company_path`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `crm_company_path` (
                                    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                    `cust_id` bigint DEFAULT NULL COMMENT '客户id',
                                    `company_path_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '公司路径主键',
                                    `cust_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '客户编号',
                                    `default_flag` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '是否默认',
                                    `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                    `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除',
                                    `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=19339 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='客户公司路径关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `crm_cust`
--

DROP TABLE IF EXISTS `crm_cust`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `crm_cust` (
                            `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                            `receive_person` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '收货人',
                            `notify_person` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '通知人',
                            `exms_event_category_id` bigint DEFAULT NULL COMMENT '展会系列id',
                            `manager_list` json NOT NULL DEFAULT (json_array()) COMMENT '业务员列表',
                            `exms_exhibition_id` bigint unsigned DEFAULT NULL COMMENT '展会id',
                            `side_mark` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '侧面唛头',
                            `main_mark` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '正面唛头',
                            `currency_list` json NOT NULL DEFAULT (json_array()) COMMENT '币种',
                            `effect_range_list` json NOT NULL DEFAULT (json_array()) COMMENT '影响范围',
                            `model_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '流程模型标识',
                            `change_status` tinyint DEFAULT NULL COMMENT '变更状态',
                            `internal_company_id` bigint DEFAULT NULL COMMENT '内部企业主键',
                            `internal_flag` int NOT NULL DEFAULT '0' COMMENT '内部企业标识 0-否 1-是',
                            `change_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '变更是否删除',
                            `change_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否变更',
                            `ver` int NOT NULL COMMENT '版本号',
                            `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '企业名称',
                            `shortname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '简称',
                            `code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '客户编号',
                            `country_id` bigint unsigned DEFAULT NULL COMMENT '国家编码',
                            `homepage` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '官网',
                            `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '电子邮件',
                            `customer_types` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '客户类型（电商,进口商,零售商,贸易商,批发商,售后公司,邮购商）',
                            `stage_type` tinyint DEFAULT NULL COMMENT '客户阶段（潜在客户，正式客户，退休客户）',
                            `settle_code` bigint DEFAULT NULL COMMENT '收款方式id',
                            `transport_type` tinyint DEFAULT NULL COMMENT '运输方式(海运、陆运、空运、供应商送货)',
                            `address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '营业地址',
                            `address_shipping` json NOT NULL DEFAULT (json_array()) COMMENT '寄件地址',
                            `phone` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '联系电话',
                            `abroad_flag` tinyint DEFAULT NULL COMMENT '国外客户标志',
                            `source_type` tinyint DEFAULT NULL COMMENT '客户来源',
                            `credit_flag` tinyint DEFAULT '0' COMMENT '启用信用额度',
                            `credit_limit` json DEFAULT NULL COMMENT '信用额度',
                            `zxbquota_flag` tinyint DEFAULT '0' COMMENT '是否是中信保',
                            `settlement_term_type` varchar(3) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '收款类型',
                            `invoice_header` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '开票抬头',
                            `remark` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注信息',
                            `tax_rate` decimal(19,6) DEFAULT NULL COMMENT '税率',
                            `audit_status` tinyint NOT NULL COMMENT '审核状态',
                            `agent_flag` tinyint DEFAULT '0' COMMENT '是否代理',
                            `manager_ids` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '业务员',
                            `convert_flag` tinyint DEFAULT '0' COMMENT '转正标识',
                            `convert_time` datetime DEFAULT NULL COMMENT '转正时间',
                            `enable_flag` tinyint NOT NULL DEFAULT '1' COMMENT '是否启用',
                            `annex` json NOT NULL DEFAULT (json_array()) COMMENT '附件',
                            `picture` json NOT NULL DEFAULT (json_array()) COMMENT '图片',
                            `cust_link_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '关联客户编号',
                            `creator` int unsigned DEFAULT NULL COMMENT '创建者',
                            `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            `updater` int unsigned DEFAULT NULL COMMENT '更新者',
                            `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                            `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
                            PRIMARY KEY (`id`) USING BTREE,
                            KEY `index_code` (`code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4136 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='客户资料表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `crm_cust_bankaccount`
--

DROP TABLE IF EXISTS `crm_cust_bankaccount`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `crm_cust_bankaccount` (
                                        `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                        `payee_entity_id` bigint DEFAULT NULL COMMENT '认领明细主键',
                                        `ver` int DEFAULT NULL COMMENT '版本号',
                                        `cust_id` bigint DEFAULT NULL COMMENT '客户id',
                                        `bank` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '开户行',
                                        `bank_account` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '银行账户',
                                        `bank_address` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '开户行地址',
                                        `bank_poc` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '开户行联系人',
                                        `bank_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '银行账号',
                                        `default_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否默认账户0-否，1-是',
                                        `creator` int unsigned DEFAULT NULL COMMENT '创建者',
                                        `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                        `updater` int unsigned DEFAULT NULL COMMENT '更新者',
                                        `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                        `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
                                        PRIMARY KEY (`id`) USING BTREE,
                                        KEY `index_cust_id` (`cust_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=243 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='银行账户信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `crm_cust_manager`
--

DROP TABLE IF EXISTS `crm_cust_manager`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `crm_cust_manager` (
                                    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                    `ver` int NOT NULL COMMENT '版本号',
                                    `cust_id` bigint NOT NULL COMMENT '客户id',
                                    `manager_id` bigint NOT NULL COMMENT '客户经理id',
                                    `default_flag` tinyint NOT NULL DEFAULT '0' COMMENT '默认联系人',
                                    `dept_id` bigint NOT NULL COMMENT '部门id',
                                    `creator` int unsigned DEFAULT NULL COMMENT '创建者',
                                    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `updater` int unsigned DEFAULT NULL COMMENT '更新者',
                                    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                    `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
                                    PRIMARY KEY (`id`) USING BTREE,
                                    KEY `index_cust_id` (`cust_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='客户移交跟进表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `crm_cust_poc`
--

DROP TABLE IF EXISTS `crm_cust_poc`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `crm_cust_poc` (
                                `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                `ver` int NOT NULL COMMENT '版本号',
                                `cust_id` bigint NOT NULL COMMENT '客户id',
                                `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '联系人姓名',
                                `poc_posts` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '联系人职位',
                                `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT ' ' COMMENT '电子邮件',
                                `mobile` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT ' ' COMMENT '手机',
                                `address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT ' ' COMMENT '住宅地址',
                                `default_flag` tinyint DEFAULT '0' COMMENT '默认联系人',
                                `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT ' ' COMMENT '备注',
                                `telephone` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT ' ' COMMENT '座机',
                                `wechat` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT ' ' COMMENT '微信',
                                `qq` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT ' ' COMMENT 'QQ',
                                `card` json NOT NULL DEFAULT (json_object()) COMMENT '名片',
                                `creator` int unsigned DEFAULT NULL COMMENT '创建者',
                                `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                `updater` int unsigned DEFAULT NULL COMMENT '更新者',
                                `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
                                PRIMARY KEY (`id`) USING BTREE,
                                KEY `index_cust_id` (`cust_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6869 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='客户联系人表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `crm_cust_settlement`
--

DROP TABLE IF EXISTS `crm_cust_settlement`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `crm_cust_settlement` (
                                       `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                                       `settlement_id` bigint unsigned DEFAULT NULL COMMENT '结汇方式编号',
                                       `cust_id` bigint unsigned DEFAULT NULL COMMENT '客户id',
                                       `default_flag` bigint unsigned DEFAULT NULL COMMENT '是否缺省',
                                       `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                       `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                       `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                       `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                       `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除  0：有效 1：删除',
                                       PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3954 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='客户结汇方式表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `crm_mark`
--

DROP TABLE IF EXISTS `crm_mark`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `crm_mark` (
                            `id` bigint NOT NULL AUTO_INCREMENT COMMENT '唛头id',
                            `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '唛头名称',
                            `ver` int NOT NULL COMMENT '版本号',
                            `cust_id` bigint NOT NULL COMMENT '客户id',
                            `eng_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '唛头英文名称',
                            `main_mark_text` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '主文字唛',
                            `main_mark_pic` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '主图形唛',
                            `main_mark_text_side` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '主侧文字唛',
                            `main_mark_pic_side` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '主侧图形唛',
                            `main_mark_text_in` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '内主文字唛',
                            `main_mark_pic_in` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '内主图形唛',
                            `main_mark_text_side_in` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '内侧文字唛',
                            `main_mark_pic_side_in` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '内侧图形唛',
                            `creator` int unsigned DEFAULT NULL COMMENT '创建者',
                            `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            `updater` int unsigned DEFAULT NULL COMMENT '更新者',
                            `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                            `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
                            PRIMARY KEY (`id`) USING BTREE,
                            KEY `index_cust_id` (`cust_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='唛头表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `csku_creater`
--

DROP TABLE IF EXISTS `csku_creater`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `csku_creater` (
                                `CP_CODE` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                `CP_INMAN` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                KEY `CP_CODE` (`CP_CODE`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `daily_currencys_rate`
--

DROP TABLE IF EXISTS `daily_currencys_rate`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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

--
-- Table structure for table `dms_commodity_inspection`
--

DROP TABLE IF EXISTS `dms_commodity_inspection`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dms_commodity_inspection` (
                                            `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                            `total_decliaration` json NOT NULL DEFAULT (json_array()) COMMENT '报关合计',
                                            `invoice_date` datetime DEFAULT NULL COMMENT '发票日期',
                                            `est_departure_time` datetime DEFAULT NULL COMMENT '预计结港日期',
                                            `front_shipping_mark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '正面唛头',
                                            `side_shipping_mark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '侧面唛头',
                                            `receive_person` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '收货人',
                                            `notify_person` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '通知人',
                                            `manager_list` json NOT NULL DEFAULT (json_array()) COMMENT '业务员',
                                            `purchase_user_list` json NOT NULL DEFAULT (json_array()) COMMENT '采购员',
                                            `input_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '制单日期',
                                            `link_code_list` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '订单链路编号',
                                            `departure_country_area` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '出运国区域',
                                            `departure_country_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '出运国名称',
                                            `departure_country_id` bigint DEFAULT NULL COMMENT '出运国主键',
                                            `bulk_handling_volume` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '散货',
                                            `forty_foot_container_num` int NOT NULL DEFAULT '0' COMMENT '40尺高柜',
                                            `forty_foot_cabinet_num` int NOT NULL DEFAULT '0' COMMENT '40尺柜',
                                            `twenty_foot_cabinet_num` int NOT NULL DEFAULT '0' COMMENT '20尺柜',
                                            `foreign_trade_company_id` bigint DEFAULT NULL COMMENT '外贸公司主体主键',
                                            `foreign_trade_company_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '外贸公司主体名称',
                                            `code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '编号',
                                            `shipment_plan_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '计划编号',
                                            `status` tinyint unsigned DEFAULT NULL COMMENT '状态',
                                            `invoice_code` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '发票号',
                                            `ship_date` datetime DEFAULT NULL COMMENT '出运日期',
                                            `trade_country_id` bigint DEFAULT NULL COMMENT '贸易国别主键',
                                            `trade_country_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '贸易国别名称',
                                            `trade_country_area` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '贸易国别区域',
                                            `trade_type` tinyint DEFAULT NULL COMMENT '贸易方式',
                                            `settlement_term_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '价格条款',
                                            `forwarder_company_id` bigint DEFAULT NULL COMMENT '船代公司主键',
                                            `forwarder_company_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '船代公司名称',
                                            `container_quantity` int unsigned DEFAULT NULL COMMENT '集装箱数量',
                                            `transport_type` tinyint DEFAULT NULL COMMENT '运输方式',
                                            `departure_port_id` bigint DEFAULT NULL COMMENT '出运口岸主键',
                                            `departure_port_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '出运口岸名称',
                                            `destination_port_id` bigint DEFAULT NULL COMMENT '目的口岸主键',
                                            `destination_port_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '目的口岸名称',
                                            `shipment_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '出运单号',
                                            `total_quantity` int NOT NULL DEFAULT '0' COMMENT '数量合计',
                                            `total_boxes` int NOT NULL DEFAULT '0' COMMENT '箱数合计',
                                            `total_grossweight` json NOT NULL DEFAULT (json_object()) COMMENT '毛重合计',
                                            `total_weight` json NOT NULL DEFAULT (json_object()) COMMENT '净重合计',
                                            `total_volume` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '体积合计',
                                            `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                            `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                            `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                            `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除',
                                            `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                            PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=41 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='商检单';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `dms_commodity_inspection_item`
--

DROP TABLE IF EXISTS `dms_commodity_inspection_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dms_commodity_inspection_item` (
                                                 `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                                 `barcode` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '条形码',
                                                 `basic_sku_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '基础产品编号',
                                                 `free_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否包含赠品',
                                                 `split_box_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否分箱',
                                                 `specification_list` json NOT NULL DEFAULT (json_array()) COMMENT '规格',
                                                 `shipped_address` tinyint NOT NULL DEFAULT '1' COMMENT '发货地点',
                                                 `customs_declare_elements` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '报关要素',
                                                 `cust_po` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '客户合同号',
                                                 `purchase_with_tax_price` json NOT NULL DEFAULT (json_object()) COMMENT '采购含税价',
                                                 `shipment_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '出运明细编号',
                                                 `commodity_inspection_id` bigint DEFAULT NULL COMMENT '商检单主键',
                                                 `deliver_date` datetime DEFAULT NULL COMMENT '交货日期',
                                                 `settlement_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '收款方式名称',
                                                 `settlement_id` bigint DEFAULT NULL COMMENT '收款方式主键',
                                                 `currency` varchar(3) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '交易币别',
                                                 `vender_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '供应商编号',
                                                 `vender_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '供应商名称',
                                                 `vender_id` bigint unsigned NOT NULL DEFAULT '0' COMMENT '供应商id',
                                                 `delivery_date` datetime DEFAULT NULL COMMENT '交货日期',
                                                 `shipping_quantity` int DEFAULT NULL COMMENT '出运数量',
                                                 `commodity_inspection_flag` tinyint DEFAULT NULL COMMENT '是否商检',
                                                 `manager` json NOT NULL DEFAULT (json_object()) COMMENT '业务员',
                                                 `insurance_fee` json NOT NULL DEFAULT (json_object()) COMMENT '保险费',
                                                 `commodity_inspection_date` datetime DEFAULT NULL COMMENT '转商检单时间',
                                                 `commodity_inspection_user` json NOT NULL DEFAULT (json_object()) COMMENT '转商检单人',
                                                 `is_to_commodity_inspection` int NOT NULL DEFAULT '0' COMMENT '是否转商检单',
                                                 `settlement_form_date` datetime DEFAULT NULL COMMENT '转结汇单时间',
                                                 `settlement_form_user` json NOT NULL DEFAULT (json_object()) COMMENT '转结汇单人',
                                                 `is_to_settlement_form` int NOT NULL DEFAULT '0' COMMENT '是否转结汇单',
                                                 `declaration_date` datetime DEFAULT NULL COMMENT '转报关单时间',
                                                 `declaration_user` json NOT NULL DEFAULT (json_object()) COMMENT '转报关单人',
                                                 `is_to_declaration` int NOT NULL DEFAULT '0' COMMENT '是否转报关单',
                                                 `unreceived_num` int NOT NULL DEFAULT '0' COMMENT '未收货值',
                                                 `received_num` int NOT NULL DEFAULT '0' COMMENT '已收货值',
                                                 `cust_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '客户名称',
                                                 `cust_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '客户编号',
                                                 `cust_id` bigint DEFAULT NULL COMMENT '客户主键',
                                                 `sale_contract_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '外销合同号',
                                                 `est_pickup_time` datetime DEFAULT NULL COMMENT '预计拉柜时间',
                                                 `sku_id` bigint DEFAULT NULL COMMENT '产品主键',
                                                 `sku_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT 'SKU编号',
                                                 `csku_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '客户货号',
                                                 `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '中文名称',
                                                 `name_eng` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '英文名称',
                                                 `declaration_quantity` int unsigned NOT NULL DEFAULT '0' COMMENT '报关数量',
                                                 `purchase_total_quantity` int NOT NULL DEFAULT '0' COMMENT '总采购数量',
                                                 `sale_unit_price` json NOT NULL DEFAULT (json_object()) COMMENT '销售单价',
                                                 `sale_amount` json NOT NULL DEFAULT (json_object()) COMMENT '销售金额',
                                                 `declaration_unit_price` json NOT NULL DEFAULT (json_object()) COMMENT '报关单价',
                                                 `declaration_amount` json NOT NULL DEFAULT (json_object()) COMMENT '报关金额',
                                                 `commodity_inspection_type` tinyint DEFAULT NULL COMMENT '商检类型',
                                                 `hs_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '海关编码',
                                                 `declaration_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '报关中文品名',
                                                 `customs_declaration_name_eng` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '报关英文品名',
                                                 `hs_measure_unit` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '海关计量单位',
                                                 `tax_refund_rate` decimal(19,6) DEFAULT NULL COMMENT '退税率',
                                                 `tax_refund_price` json NOT NULL DEFAULT (json_object()) COMMENT '退税金额',
                                                 `purchase_contract_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '采购合同编号',
                                                 `purchase_user_id` bigint DEFAULT NULL COMMENT '采购员id',
                                                 `purchase_user_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '采购员姓名',
                                                 `purchase_user_dept_id` bigint DEFAULT NULL COMMENT '采购员部门id',
                                                 `purchase_user_dept_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '采购员部门名称',
                                                 `purchase_currency` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '采购币种',
                                                 `purchase_unit_price` json NOT NULL DEFAULT (json_object()) COMMENT '采购单价',
                                                 `package_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '包装方式',
                                                 `qty_per_outerbox` int NOT NULL DEFAULT '0' COMMENT '外箱装量',
                                                 `qty_per_innerbox` int NOT NULL DEFAULT '0' COMMENT '内盒装量',
                                                 `box_count` int NOT NULL DEFAULT '0' COMMENT '箱数',
                                                 `unit_per_outerbox` int NOT NULL DEFAULT '0' COMMENT '外箱单位',
                                                 `outerbox_length` decimal(19,6) unsigned DEFAULT NULL COMMENT '外箱长度',
                                                 `outerbox_width` decimal(19,6) unsigned DEFAULT NULL COMMENT '外箱宽度',
                                                 `outerbox_height` decimal(19,6) unsigned DEFAULT NULL COMMENT '外箱高度',
                                                 `outerbox_volume` decimal(19,6) unsigned DEFAULT NULL COMMENT '外箱体积',
                                                 `total_volume` decimal(19,6) unsigned DEFAULT NULL COMMENT '总体积',
                                                 `outerbox_netweight` json NOT NULL DEFAULT (json_object()) COMMENT '外箱净重',
                                                 `total_netweight` json NOT NULL DEFAULT (json_object()) COMMENT '总净重',
                                                 `outerbox_grossweight` json NOT NULL DEFAULT (json_object()) COMMENT '外箱毛重',
                                                 `total_grossweight` json DEFAULT NULL COMMENT '总毛重',
                                                 `description` varchar(5000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '产品描述',
                                                 `description_eng` varchar(5000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '产品英文描述',
                                                 `stock_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '仓库编号',
                                                 `stock_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '仓库名称',
                                                 `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '备注',
                                                 `commission_amount` json NOT NULL DEFAULT (json_object()) COMMENT '佣金金额',
                                                 `order_gross_profit_rate` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '毛利率',
                                                 `outbound_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否出仓',
                                                 `outbound_date` datetime DEFAULT NULL COMMENT '出仓日期',
                                                 `declaration_unit` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '报关单位',
                                                 `declared_quantity` int NOT NULL DEFAULT '0' COMMENT '已报关数',
                                                 `settle_order_flag` tinyint NOT NULL DEFAULT '1' COMMENT '是否转结汇单',
                                                 `settlement_term_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '价格条款',
                                                 `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                                 `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                                 `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                                 `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除',
                                                 `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                                 PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=71 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='商检单明细';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `dms_declaration`
--

DROP TABLE IF EXISTS `dms_declaration`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dms_declaration` (
                                   `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                   `invoice_date` datetime DEFAULT NULL COMMENT '发票日期',
                                   `est_departure_time` datetime DEFAULT NULL COMMENT '预计结港日期',
                                   `protocol_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '合同数量',
                                   `front_shipping_mark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '正面唛头',
                                   `side_shipping_mark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '侧面唛头',
                                   `receive_person` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '收货人',
                                   `notify_person` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '通知人',
                                   `manager_list` json NOT NULL DEFAULT (json_array()) COMMENT '业务员',
                                   `purchase_user_list` json NOT NULL DEFAULT (json_array()) COMMENT '采购员',
                                   `departure_country_area` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '出运地区',
                                   `departure_country_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '出运国名称',
                                   `departure_country_id` bigint DEFAULT NULL COMMENT '出运国主键',
                                   `shipment_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '是否基础卡片',
                                   `link_code_list` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '订单链路编号',
                                   `foreign_trade_company_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '外贸公司主体名称',
                                   `bulk_handling_volume` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '散货',
                                   `forty_foot_container_num` int NOT NULL DEFAULT '0' COMMENT '40尺高柜',
                                   `forty_foot_cabinet_num` int NOT NULL DEFAULT '0' COMMENT '40尺柜',
                                   `twenty_foot_cabinet_num` int NOT NULL DEFAULT '0' COMMENT '20尺柜',
                                   `foreign_trade_company_id` bigint DEFAULT NULL COMMENT '外贸公司主体主键',
                                   `code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '编号',
                                   `shipment_plan_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '计划编号',
                                   `invoice_code` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '发票号',
                                   `input_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '制单日期',
                                   `status` tinyint DEFAULT NULL COMMENT '订单状态',
                                   `ship_date` datetime DEFAULT NULL COMMENT '出运日期',
                                   `currency` varchar(3) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '外销币种',
                                   `trade_country_id` bigint DEFAULT NULL COMMENT '贸易国别主键',
                                   `trade_country_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '贸易国别名称',
                                   `trade_country_area` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '贸易国别区域',
                                   `trade_type` tinyint DEFAULT NULL COMMENT '贸易方式',
                                   `settlement_term_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '价格条款',
                                   `forwarder_company_id` bigint DEFAULT NULL COMMENT '船代公司主键',
                                   `forwarder_company_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '船代公司名称',
                                   `container_quantity` int unsigned DEFAULT NULL COMMENT '集装箱数量',
                                   `transport_type` tinyint DEFAULT NULL COMMENT '运输方式',
                                   `departure_port_id` bigint DEFAULT NULL COMMENT '出运口岸主键',
                                   `departure_port_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '出运口岸名称',
                                   `destination_port_id` bigint DEFAULT NULL COMMENT '目的口岸主键',
                                   `destination_port_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '目的口岸名称',
                                   `total_quantity` int NOT NULL DEFAULT '0' COMMENT '数量合计',
                                   `total_boxes` int NOT NULL DEFAULT '0' COMMENT '箱数合计',
                                   `total_grossweight` json NOT NULL DEFAULT (json_object()) COMMENT '毛重合计',
                                   `total_weight` json NOT NULL DEFAULT (json_object()) COMMENT '净重合计',
                                   `total_volume` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '体积合计',
                                   `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                   `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                   `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                   `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除',
                                   `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                   PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=502 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='报关单';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `dms_declaration_item`
--

DROP TABLE IF EXISTS `dms_declaration_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dms_declaration_item` (
                                        `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                        `barcode` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '条形码',
                                        `free_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否包含赠品',
                                        `split_box_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否分箱',
                                        `specification_list` json NOT NULL DEFAULT (json_array()) COMMENT '规格',
                                        `shipped_address` tinyint NOT NULL DEFAULT '1' COMMENT '发货地点',
                                        `customs_declare_elements` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '报关要素',
                                        `cust_po` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '客户合同号',
                                        `shipment_item_id` bigint DEFAULT NULL COMMENT '出运明细明细主键',
                                        `declaration_element` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '报关要素',
                                        `declaration_invoices` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '报关单据',
                                        `pricing_method` tinyint unsigned DEFAULT NULL COMMENT '计价方式',
                                        `settlement_id` bigint DEFAULT NULL COMMENT '收款方式主键',
                                        `settlement_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '收款方式名称',
                                        `currency` varchar(3) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '交易币别',
                                        `vender_id` bigint unsigned NOT NULL DEFAULT '0' COMMENT '供应商id',
                                        `vender_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '供应商编号',
                                        `vender_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '供应商名称',
                                        `delivery_date` datetime DEFAULT NULL COMMENT '交货日期',
                                        `shipping_quantity` int DEFAULT NULL COMMENT '出运数量',
                                        `commodity_inspection_flag` tinyint DEFAULT NULL COMMENT '是否商检',
                                        `manager` json NOT NULL DEFAULT (json_object()) COMMENT '业务员',
                                        `insurance_fee` json NOT NULL DEFAULT (json_object()) COMMENT '保险费',
                                        `commodity_inspection_date` datetime DEFAULT NULL COMMENT '转商检单时间',
                                        `commodity_inspection_user` json NOT NULL DEFAULT (json_object()) COMMENT '转商检单人',
                                        `is_to_commodity_inspection` int NOT NULL DEFAULT '0' COMMENT '是否转商检单',
                                        `settlement_form_date` datetime DEFAULT NULL COMMENT '转结汇单时间',
                                        `settlement_form_user` json NOT NULL DEFAULT (json_object()) COMMENT '转结汇单人',
                                        `is_to_settlement_form` int NOT NULL DEFAULT '0' COMMENT '是否转结汇单',
                                        `declaration_date` datetime DEFAULT NULL COMMENT '转报关单时间',
                                        `declaration_user` json NOT NULL DEFAULT (json_object()) COMMENT '转报关单人',
                                        `is_to_declaration` int NOT NULL DEFAULT '0' COMMENT '是否转报关单',
                                        `unreceived_num` int NOT NULL DEFAULT '0' COMMENT '未收货值',
                                        `received_num` int NOT NULL DEFAULT '0' COMMENT '已收货值',
                                        `cust_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '客户名称',
                                        `cust_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '客户编号',
                                        `cust_id` bigint DEFAULT NULL COMMENT '客户主键',
                                        `sale_contract_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '外销合同号',
                                        `est_pickup_time` datetime DEFAULT NULL COMMENT '预计拉柜时间',
                                        `sku_id` bigint DEFAULT NULL COMMENT '产品主键',
                                        `sku_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT 'SKU编号',
                                        `csku_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '客户货号',
                                        `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '中文名称',
                                        `name_eng` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '英文名称',
                                        `declaration_quantity` int unsigned NOT NULL DEFAULT '0' COMMENT '报关数量',
                                        `purchase_total_quantity` int NOT NULL DEFAULT '0' COMMENT '总采购数量',
                                        `sale_unit_price` json NOT NULL DEFAULT (json_object()) COMMENT '销售单价',
                                        `sale_amount` json NOT NULL DEFAULT (json_object()) COMMENT '销售金额',
                                        `declaration_unit_price` json NOT NULL DEFAULT (json_object()) COMMENT '报关单价',
                                        `declaration_amount` json NOT NULL DEFAULT (json_object()) COMMENT '报关金额',
                                        `commodity_inspection_type` tinyint DEFAULT NULL COMMENT '商检类型',
                                        `hs_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '海关编码',
                                        `declaration_id` bigint unsigned DEFAULT NULL COMMENT '报关单id',
                                        `declaration_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '报关中文品名',
                                        `customs_declaration_name_eng` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '报关英文品名',
                                        `hs_measure_unit` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '海关计量单位',
                                        `tax_refund_rate` decimal(19,6) DEFAULT NULL COMMENT '退税率',
                                        `tax_refund_price` json NOT NULL DEFAULT (json_object()) COMMENT '退税金额',
                                        `purchase_contract_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '采购合同编号',
                                        `purchase_user_id` bigint DEFAULT NULL COMMENT '采购员id',
                                        `purchase_user_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '采购员姓名',
                                        `purchase_user_dept_id` bigint DEFAULT NULL COMMENT '采购员部门id',
                                        `purchase_user_dept_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '采购员部门名称',
                                        `purchase_currency` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '采购币种',
                                        `purchase_with_tax_price` json NOT NULL DEFAULT (json_object()) COMMENT '采购单价',
                                        `package_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '包装方式',
                                        `qty_per_outerbox` int NOT NULL DEFAULT '0' COMMENT '外箱装量',
                                        `qty_per_innerbox` int NOT NULL DEFAULT '0' COMMENT '内盒装量',
                                        `box_count` int NOT NULL DEFAULT '0' COMMENT '箱数',
                                        `unit_per_outerbox` int NOT NULL DEFAULT '0' COMMENT '外箱单位',
                                        `outerbox_length` decimal(19,6) unsigned DEFAULT NULL COMMENT '外箱长度',
                                        `outerbox_width` decimal(19,6) unsigned DEFAULT NULL COMMENT '外箱宽度',
                                        `outerbox_height` decimal(19,6) unsigned DEFAULT NULL COMMENT '外箱高度',
                                        `outerbox_volume` decimal(19,6) unsigned DEFAULT NULL COMMENT '外箱体积',
                                        `total_volume` decimal(19,6) unsigned DEFAULT NULL COMMENT '总体积',
                                        `outerbox_netweight` json NOT NULL DEFAULT (json_object()) COMMENT '外箱净重',
                                        `total_netweight` json NOT NULL DEFAULT (json_object()) COMMENT '总净重',
                                        `outerbox_grossweight` json NOT NULL DEFAULT (json_object()) COMMENT '外箱毛重',
                                        `total_grossweight` json DEFAULT NULL COMMENT '总毛重',
                                        `description` varchar(5000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '产品描述',
                                        `description_eng` varchar(5000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '产品英文描述',
                                        `stock_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '仓库编号',
                                        `stock_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '仓库名称',
                                        `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '备注',
                                        `commission_amount` json NOT NULL DEFAULT (json_object()) COMMENT '佣金金额',
                                        `order_gross_profit_rate` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '毛利率',
                                        `outbound_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否出仓',
                                        `outbound_date` datetime DEFAULT NULL COMMENT '出仓日期',
                                        `declaration_unit` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '报关单位',
                                        `declared_quantity` int NOT NULL DEFAULT '0' COMMENT '已报关数',
                                        `settle_order_flag` tinyint NOT NULL DEFAULT '1' COMMENT '是否转结汇单',
                                        `settlement_term_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '价格条款',
                                        `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                        `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                        `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                        `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除',
                                        `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                        PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2594 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='报关单明细';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `dms_expense_type`
--

DROP TABLE IF EXISTS `dms_expense_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dms_expense_type` (
                                    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                    `shipment_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '出运明细编号',
                                    `payment_count` int unsigned NOT NULL DEFAULT '0' COMMENT '付款次数',
                                    `forwarder_company_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '船代公司编号',
                                    `forwarder_company_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '船代公司名称',
                                    `expense_type` tinyint DEFAULT NULL COMMENT '费用类型',
                                    `amount` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '费用金额',
                                    `currency` varchar(3) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '币别',
                                    `reimbursed_amount` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '已报销金额',
                                    `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                    `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除',
                                    `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='费用类型';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `dms_forwarder_company_info`
--

DROP TABLE IF EXISTS `dms_forwarder_company_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dms_forwarder_company_info` (
                                              `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                              `code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '编号',
                                              `company_id` bigint DEFAULT NULL COMMENT '归属公司主键',
                                              `company_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '归属公司名称',
                                              `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '名称',
                                              `contact_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '联系人',
                                              `contact_phone_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '联系电话',
                                              `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态',
                                              `input_user` json NOT NULL DEFAULT (json_object()) COMMENT '录入人',
                                              `input_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '录入日期',
                                              `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT ' ' COMMENT '备注',
                                              `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                              `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                              `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                              `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除',
                                              `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                              PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='船代公司';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `dms_forwarder_fee`
--

DROP TABLE IF EXISTS `dms_forwarder_fee`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dms_forwarder_fee` (
                                     `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                     `payment_app_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '对公申请编号',
                                     `payment_app_id` bigint DEFAULT NULL COMMENT '对公申请主键',
                                     `manager_list` json NOT NULL DEFAULT (json_array()) COMMENT '业务员',
                                     `purchase_user_list` json NOT NULL DEFAULT (json_array()) COMMENT '采购员',
                                     `code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '编号',
                                     `shipment_id` bigint DEFAULT NULL COMMENT '出运费用主键',
                                     `shipment_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '出运费用编号',
                                     `invoice_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '发票号',
                                     `company_id` bigint DEFAULT NULL COMMENT '主体主键',
                                     `company_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '主体名称',
                                     `sort_num` int DEFAULT NULL COMMENT '序号',
                                     `vender_id` bigint DEFAULT NULL COMMENT '供应商主键',
                                     `vender_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '供应商编号',
                                     `vender_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '供应商名称',
                                     `dict_subject_id` bigint DEFAULT NULL COMMENT '费用名称主键',
                                     `dict_subject_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '费用名称',
                                     `fee_type` int DEFAULT NULL COMMENT '费用类型',
                                     `amount` json NOT NULL DEFAULT (json_object()) COMMENT '金额',
                                     `pay_status` int DEFAULT NULL COMMENT '付款状态',
                                     `applyer` json NOT NULL DEFAULT (json_object()) COMMENT '申请人',
                                     `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                     `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                     `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                     `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                     `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除',
                                     PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=216 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='船代费用表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `dms_settlement_form`
--

DROP TABLE IF EXISTS `dms_settlement_form`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dms_settlement_form` (
                                       `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                       `invoice_packing_list` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '发票箱单',
                                       `invoice_date` datetime DEFAULT NULL COMMENT '发票日期',
                                       `est_departure_time` datetime DEFAULT NULL COMMENT '预计结港日期',
                                       `front_shipping_mark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '正面唛头',
                                       `side_shipping_mark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '侧面唛头',
                                       `receive_person` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '收货人',
                                       `notify_person` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '通知人',
                                       `manager_list` json NOT NULL DEFAULT (json_array()) COMMENT '业务员',
                                       `purchase_user_list` json NOT NULL DEFAULT (json_array()) COMMENT '采购员',
                                       `total_goods_value` json NOT NULL DEFAULT (json_array()) COMMENT '货值合计',
                                       `link_code_list` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '订单链路编号',
                                       `departure_country_area` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '出运国区域',
                                       `departure_country_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '出运国名称',
                                       `departure_country_id` bigint DEFAULT NULL COMMENT '出运国主键',
                                       `destination_port_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '目的口岸名称',
                                       `destination_port_id` bigint DEFAULT NULL COMMENT '目的口岸主键',
                                       `bulk_handling_volume` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '散货',
                                       `forty_foot_container_num` int NOT NULL DEFAULT '0' COMMENT '40尺高柜',
                                       `forty_foot_cabinet_num` int NOT NULL DEFAULT '0' COMMENT '40尺柜',
                                       `twenty_foot_cabinet_num` int NOT NULL DEFAULT '0' COMMENT '20尺柜',
                                       `code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '编号',
                                       `foreign_trade_company_id` bigint DEFAULT NULL COMMENT '外贸公司主体主键',
                                       `foreign_trade_company_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '外贸公司主体名称',
                                       `invoice_code` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '发票号',
                                       `input_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '制单日期',
                                       `ship_date` datetime DEFAULT NULL COMMENT '出运日期',
                                       `status` tinyint DEFAULT NULL COMMENT '状态',
                                       `currency` varchar(3) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '外销币种',
                                       `trade_country_id` bigint DEFAULT NULL COMMENT '贸易国别主键',
                                       `trade_country_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '贸易国别名称',
                                       `trade_country_area` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '贸易国别区域',
                                       `trade_type` tinyint DEFAULT NULL COMMENT '贸易方式',
                                       `settlement_term_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '价格条款',
                                       `settlement_id` bigint unsigned DEFAULT NULL COMMENT '结汇方式',
                                       `settlement_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '结汇名称',
                                       `forwarder_company_id` bigint DEFAULT NULL COMMENT '船代公司主键',
                                       `forwarder_company_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '船代公司名称',
                                       `container_quantity` int unsigned DEFAULT NULL COMMENT '集装箱数量',
                                       `transport_type` tinyint DEFAULT NULL COMMENT '运输方式',
                                       `departure_port_id` bigint DEFAULT NULL COMMENT '出运口岸主键',
                                       `departure_port_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '出运口岸名称',
                                       `shipment_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '出运单号',
                                       `total_quantity` int NOT NULL DEFAULT '0' COMMENT '数量合计',
                                       `total_boxes` int NOT NULL DEFAULT '0' COMMENT '箱数合计',
                                       `total_grossweight` json NOT NULL DEFAULT (json_object()) COMMENT '毛重合计',
                                       `total_weight` json NOT NULL DEFAULT (json_object()) COMMENT '净重合计',
                                       `total_volume` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '体积合计',
                                       `total_amount` json NOT NULL DEFAULT (json_object()) COMMENT '金额合计',
                                       `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                       `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                       `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                       `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除',
                                       `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                       PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=338 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='结汇单';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `dms_settlement_form_item`
--

DROP TABLE IF EXISTS `dms_settlement_form_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dms_settlement_form_item` (
                                            `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                            `barcode` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '条形码',
                                            `free_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否包含赠品',
                                            `shipment_item_id` bigint DEFAULT NULL COMMENT '出运明细id',
                                            `split_box_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否分箱',
                                            `specification_list` json NOT NULL DEFAULT (json_array()) COMMENT '规格',
                                            `shipped_address` tinyint NOT NULL DEFAULT '1' COMMENT '发货地点',
                                            `customs_declare_elements` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '报关要素',
                                            `cust_po` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '客户合同号',
                                            `purchase_with_tax_price` json NOT NULL DEFAULT (json_object()) COMMENT '采购含税价',
                                            `settlement_quantity` int DEFAULT NULL COMMENT '结汇数量',
                                            `settlement_form_id` bigint DEFAULT NULL COMMENT '结汇单主键',
                                            `deliver_date` datetime DEFAULT NULL COMMENT '交货日期',
                                            `settlement_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '收款方式名称',
                                            `settlement_id` bigint DEFAULT NULL COMMENT '收款方式主键',
                                            `currency` varchar(3) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '交易币别',
                                            `vender_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '供应商编号',
                                            `vender_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '供应商名称',
                                            `vender_id` bigint unsigned NOT NULL DEFAULT '0' COMMENT '供应商id',
                                            `delivery_date` datetime DEFAULT NULL COMMENT '交货日期',
                                            `shipping_quantity` int DEFAULT NULL COMMENT '出运数量',
                                            `commodity_inspection_flag` tinyint DEFAULT NULL COMMENT '是否商检',
                                            `manager` json NOT NULL DEFAULT (json_object()) COMMENT '业务员',
                                            `insurance_fee` json NOT NULL DEFAULT (json_object()) COMMENT '保险费',
                                            `commodity_inspection_date` datetime DEFAULT NULL COMMENT '转商检单时间',
                                            `commodity_inspection_user` json NOT NULL DEFAULT (json_object()) COMMENT '转商检单人',
                                            `is_to_commodity_inspection` int NOT NULL DEFAULT '0' COMMENT '是否转商检单',
                                            `settlement_form_date` datetime DEFAULT NULL COMMENT '转结汇单时间',
                                            `settlement_form_user` json NOT NULL DEFAULT (json_object()) COMMENT '转结汇单人',
                                            `is_to_settlement_form` int NOT NULL DEFAULT '0' COMMENT '是否转结汇单',
                                            `declaration_date` datetime DEFAULT NULL COMMENT '转报关单时间',
                                            `declaration_user` json NOT NULL DEFAULT (json_object()) COMMENT '转报关单人',
                                            `is_to_declaration` int NOT NULL DEFAULT '0' COMMENT '是否转报关单',
                                            `unreceived_num` int NOT NULL DEFAULT '0' COMMENT '未收货值',
                                            `received_num` int NOT NULL DEFAULT '0' COMMENT '已收货值',
                                            `cust_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '客户名称',
                                            `cust_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '客户编号',
                                            `cust_id` bigint DEFAULT NULL COMMENT '客户主键',
                                            `sale_contract_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '外销合同号',
                                            `est_pickup_time` datetime DEFAULT NULL COMMENT '预计拉柜时间',
                                            `sku_id` bigint DEFAULT NULL COMMENT '产品主键',
                                            `sku_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT 'SKU编号',
                                            `csku_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '客户货号',
                                            `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '中文名称',
                                            `name_eng` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '英文名称',
                                            `declaration_quantity` int unsigned NOT NULL DEFAULT '0' COMMENT '报关数量',
                                            `purchase_total_quantity` int NOT NULL DEFAULT '0' COMMENT '总采购数量',
                                            `sale_unit_price` json NOT NULL DEFAULT (json_object()) COMMENT '销售单价',
                                            `sale_amount` json NOT NULL DEFAULT (json_object()) COMMENT '销售金额',
                                            `declaration_unit_price` json NOT NULL DEFAULT (json_object()) COMMENT '报关单价',
                                            `declaration_amount` json NOT NULL DEFAULT (json_object()) COMMENT '报关金额',
                                            `commodity_inspection_type` tinyint DEFAULT NULL COMMENT '商检类型',
                                            `hs_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '海关编码',
                                            `declaration_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '报关中文品名',
                                            `customs_declaration_name_eng` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '报关英文品名',
                                            `hs_measure_unit` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '海关计量单位',
                                            `tax_refund_rate` decimal(19,6) DEFAULT NULL COMMENT '退税率',
                                            `tax_refund_price` json NOT NULL DEFAULT (json_object()) COMMENT '退税金额',
                                            `purchase_contract_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '采购合同编号',
                                            `purchase_user_id` bigint DEFAULT NULL COMMENT '采购员id',
                                            `purchase_user_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '采购员姓名',
                                            `purchase_user_dept_id` bigint DEFAULT NULL COMMENT '采购员部门id',
                                            `purchase_user_dept_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '采购员部门名称',
                                            `purchase_currency` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '采购币种',
                                            `purchase_unit_price` json NOT NULL DEFAULT (json_object()) COMMENT '采购单价',
                                            `package_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '包装方式',
                                            `qty_per_outerbox` int NOT NULL DEFAULT '0' COMMENT '外箱装量',
                                            `qty_per_innerbox` int NOT NULL DEFAULT '0' COMMENT '内盒装量',
                                            `box_count` int NOT NULL DEFAULT '0' COMMENT '箱数',
                                            `unit_per_outerbox` int NOT NULL DEFAULT '0' COMMENT '外箱单位',
                                            `outerbox_length` decimal(19,6) unsigned DEFAULT NULL COMMENT '外箱长度',
                                            `outerbox_width` decimal(19,6) unsigned DEFAULT NULL COMMENT '外箱宽度',
                                            `outerbox_height` decimal(19,6) unsigned DEFAULT NULL COMMENT '外箱高度',
                                            `outerbox_volume` decimal(19,6) unsigned DEFAULT NULL COMMENT '外箱体积',
                                            `total_volume` decimal(19,6) unsigned DEFAULT NULL COMMENT '总体积',
                                            `outerbox_netweight` json NOT NULL DEFAULT (json_object()) COMMENT '外箱净重',
                                            `total_netweight` json NOT NULL DEFAULT (json_object()) COMMENT '总净重',
                                            `outerbox_grossweight` json NOT NULL DEFAULT (json_object()) COMMENT '外箱毛重',
                                            `total_grossweight` json DEFAULT NULL COMMENT '总毛重',
                                            `description` varchar(5000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '产品描述',
                                            `description_eng` varchar(5000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '产品英文描述',
                                            `stock_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '仓库编号',
                                            `stock_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '仓库名称',
                                            `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '备注',
                                            `commission_amount` json NOT NULL DEFAULT (json_object()) COMMENT '佣金金额',
                                            `order_gross_profit_rate` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '毛利率',
                                            `outbound_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否出仓',
                                            `outbound_date` datetime DEFAULT NULL COMMENT '出仓日期',
                                            `declaration_unit` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '报关单位',
                                            `declared_quantity` int NOT NULL DEFAULT '0' COMMENT '已报关数',
                                            `settle_order_flag` tinyint NOT NULL DEFAULT '1' COMMENT '是否转结汇单',
                                            `settlement_term_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '价格条款',
                                            `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                            `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                            `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                            `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除',
                                            `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                            PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1865 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='结汇单明细';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `dms_shipment`
--

DROP TABLE IF EXISTS `dms_shipment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dms_shipment` (
                                `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                `source_id` bigint DEFAULT NULL COMMENT '来源明细id',
                                `batch_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否分批出运',
                                `invoice_date` datetime DEFAULT NULL COMMENT '发票日期',
                                `cust_delivery_date` datetime DEFAULT NULL COMMENT '客户交期',
                                `gen_contract_unique_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '内部生成编号',
                                `front_shipping_mark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '正面唛头',
                                `side_shipping_mark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '侧面唛头',
                                `receive_person` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '收货人',
                                `notify_person` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '通知人',
                                `inbound_date` datetime DEFAULT NULL COMMENT '进仓日期',
                                `manager_list` json NOT NULL DEFAULT (json_array()) COMMENT '业务员',
                                `purchase_user_list` json NOT NULL DEFAULT (json_array()) COMMENT '采购员',
                                `documenter` json NOT NULL DEFAULT (json_object()) COMMENT '单证员',
                                `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '备注',
                                `total_declaration` json NOT NULL DEFAULT (json_array()) COMMENT '报关合计',
                                `total_goods_value` json NOT NULL DEFAULT (json_array()) COMMENT '货值合计',
                                `auto_flag` tinyint NOT NULL DEFAULT '0' COMMENT '自动生成标识',
                                `source_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '来源编号',
                                `link_code_list` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '订单链路编号',
                                `contaner_no` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '发票标识',
                                `invoice_notice_flag` tinyint NOT NULL DEFAULT '0' COMMENT '开票标识',
                                `change_status` tinyint NOT NULL DEFAULT '0' COMMENT '变更状态',
                                `confirm_flag` tinyint NOT NULL DEFAULT '1' COMMENT '确认状态',
                                `code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '编号',
                                `shipment_plan_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '计划编号',
                                `status` tinyint NOT NULL DEFAULT '1' COMMENT '单据状态',
                                `cust_id` bigint DEFAULT NULL COMMENT '客户主键',
                                `cust_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '客户编号',
                                `cust_contract_code` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '客户合同',
                                `cust_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                                `invoice_code` varchar(50) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '发票号',
                                `settlement_term_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '价格条款',
                                `est_ship_date` datetime DEFAULT NULL COMMENT '预计出运',
                                `ship_date` datetime DEFAULT NULL COMMENT '出运日期',
                                `order_manager` json NOT NULL DEFAULT (json_object()) COMMENT '单据员',
                                `transport_type` tinyint DEFAULT NULL COMMENT '运输方式',
                                `departure_country_id` bigint DEFAULT NULL COMMENT '出运国主键',
                                `departure_country_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '出运国名称',
                                `departure_country_area` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '出运国区域',
                                `departure_port_id` bigint DEFAULT NULL COMMENT '出运口岸主键',
                                `departure_port_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '出运口岸名称',
                                `trade_country_id` bigint DEFAULT NULL COMMENT '贸易国别主键',
                                `trade_country_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '贸易国别名称',
                                `trade_country_area` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '贸易国别区域',
                                `destination_port_id` bigint DEFAULT NULL COMMENT '目的口岸主键',
                                `destination_port_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '目的口岸名称',
                                `trade_type` tinyint DEFAULT NULL COMMENT '贸易方式',
                                `outbound_flag` tinyint DEFAULT '0' COMMENT '是否出仓',
                                `outbound_date` datetime DEFAULT NULL COMMENT '出仓日期',
                                `shipment_flag` tinyint DEFAULT '0' COMMENT '是否出运',
                                `settle_order_flag` tinyint DEFAULT '0' COMMENT '转结汇单',
                                `declaration_flag` tinyint DEFAULT '0' COMMENT '报关状态',
                                `inovice_noti_flag` tinyint DEFAULT '0' COMMENT '已转开票通知',
                                `foreign_trade_company_id` bigint DEFAULT NULL COMMENT '外贸公司主体主键',
                                `foreign_trade_company_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '外贸公司主体名称',
                                `forwarder_company_id` bigint DEFAULT NULL COMMENT '船代公司主键',
                                `forwarder_company_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '船代公司名称',
                                `ship_num` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '船次',
                                `bill_lading_num` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '提单号',
                                `est_closing_time` datetime DEFAULT NULL COMMENT '预计结单时间',
                                `est_clearance_time` datetime DEFAULT NULL COMMENT '预计结关时间',
                                `est_departure_time` datetime DEFAULT NULL COMMENT '预计结港时间',
                                `twenty_foot_cabinet_num` int NOT NULL DEFAULT '0' COMMENT '20尺柜',
                                `forty_foot_cabinet_num` int NOT NULL DEFAULT '0' COMMENT '40尺柜',
                                `forty_foot_container_num` int NOT NULL DEFAULT '0' COMMENT '40尺高柜',
                                `bulk_handling_volume` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '散货',
                                `total_quantity` int NOT NULL DEFAULT '0' COMMENT '数量合计',
                                `total_boxes` int NOT NULL DEFAULT '0' COMMENT '箱数合计',
                                `total_grossweight` json NOT NULL DEFAULT (json_object()) COMMENT '毛重合计',
                                `total_weight` json NOT NULL DEFAULT (json_object()) COMMENT '净重合计',
                                `total_volume` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '体积合计',
                                `total_purchase` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '采购总金额',
                                `total_tax_refund_price` json NOT NULL DEFAULT (json_object()) COMMENT '退税总额',
                                `commission_amount` json NOT NULL DEFAULT (json_object()) COMMENT '佣金金额',
                                `insurance_fee` json NOT NULL DEFAULT (json_object()) COMMENT '保险费用',
                                `addition_amount` json NOT NULL DEFAULT (json_object()) COMMENT '加项金额',
                                `total_addition_amount` json NOT NULL DEFAULT (json_object()) COMMENT '加项总额',
                                `deduction_amount` json NOT NULL DEFAULT (json_object()) COMMENT '减项金额',
                                `total_deduction_amount` json NOT NULL DEFAULT (json_object()) COMMENT '减项总额',
                                `received_num` int NOT NULL DEFAULT '0' COMMENT '已收货值',
                                `unreceived_num` int NOT NULL DEFAULT '0' COMMENT '未收货值',
                                `input_user` json NOT NULL DEFAULT (json_object()) COMMENT '录入人',
                                `input_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '录入日期',
                                `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除',
                                `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=577 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='出运单';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `dms_shipment_change`
--

DROP TABLE IF EXISTS `dms_shipment_change`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dms_shipment_change` (
                                       `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                       `documenter` json NOT NULL DEFAULT (json_object()) COMMENT '单证员',
                                       `invoice_date` datetime DEFAULT NULL COMMENT '发票日期',
                                       `cust_delivery_date` datetime DEFAULT NULL COMMENT '客户交期',
                                       `front_shipping_mark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '正面唛头',
                                       `side_shipping_mark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '侧面唛头',
                                       `receive_person` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '收货人',
                                       `notify_person` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '通知人',
                                       `inbound_date` datetime DEFAULT NULL COMMENT '进仓日期',
                                       `manager_list` json NOT NULL DEFAULT (json_array()) COMMENT '业务员',
                                       `purchase_user_list` json NOT NULL DEFAULT (json_array()) COMMENT '采购员',
                                       `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '备注',
                                       `contaner_no` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '柜号',
                                       `total_declaration` json NOT NULL DEFAULT (json_array()) COMMENT '报关合计',
                                       `total_goods_value` json NOT NULL DEFAULT (json_array()) COMMENT '货值合计',
                                       `forwarder_fee_list` json NOT NULL DEFAULT (json_array()) COMMENT '单证费用',
                                       `old_data` json NOT NULL DEFAULT (json_object()) COMMENT '旧数据',
                                       `change_status` tinyint NOT NULL DEFAULT '0' COMMENT '变更状态',
                                       `company_title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '公司抬头',
                                       `code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '编号',
                                       `shipment_plan_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '计划编号',
                                       `status` tinyint NOT NULL DEFAULT '1' COMMENT '单据状态',
                                       `cust_id` bigint DEFAULT NULL COMMENT '客户主键',
                                       `cust_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '客户编号',
                                       `cust_contract_code` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '客户合同',
                                       `cust_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '客户名称',
                                       `invoice_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '发票号',
                                       `settlement_term_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '价格条款',
                                       `est_ship_date` datetime DEFAULT NULL COMMENT '预计出运',
                                       `ship_date` datetime DEFAULT NULL COMMENT '出运日期',
                                       `order_manager` json DEFAULT NULL COMMENT '单据员',
                                       `transport_type` tinyint DEFAULT NULL COMMENT '运输方式',
                                       `sales` json NOT NULL DEFAULT (json_object()) COMMENT '销售人员',
                                       `departure_country_id` bigint DEFAULT NULL COMMENT '出运国主键',
                                       `departure_country_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '出运国名称',
                                       `departure_country_area` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '出运国区域',
                                       `departure_port_id` bigint DEFAULT NULL COMMENT '出运口岸主键',
                                       `departure_port_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '出运口岸名称',
                                       `trade_country_id` bigint DEFAULT NULL COMMENT '贸易国别主键',
                                       `trade_country_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '贸易国别名称',
                                       `trade_country_area` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '贸易国别区域',
                                       `destination_port_id` bigint DEFAULT NULL COMMENT '目的口岸主键',
                                       `destination_port_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '目的口岸名称',
                                       `process_instance_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '流程实例编号',
                                       `model_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '流程模型标识',
                                       `trade_type` tinyint DEFAULT NULL COMMENT '贸易方式',
                                       `outbound_flag` tinyint DEFAULT '0' COMMENT '是否出仓',
                                       `outbound_date` datetime DEFAULT NULL COMMENT '出仓日期',
                                       `shipment_flag` tinyint DEFAULT '0' COMMENT '是否出运',
                                       `settle_order_flag` tinyint DEFAULT '0' COMMENT '转结汇单',
                                       `declaration_flag` tinyint DEFAULT '0' COMMENT '报关状态',
                                       `inovice_noti_flag` tinyint DEFAULT '0' COMMENT '已转开票通知',
                                       `audit_status` tinyint DEFAULT NULL COMMENT '审核状态',
                                       `company_id` bigint DEFAULT NULL COMMENT '内部法人单位主键',
                                       `company_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '内部法人单位名称',
                                       `forwarder_company_id` bigint DEFAULT NULL COMMENT '船代公司主键',
                                       `forwarder_company_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '船代公司名称',
                                       `ship_num` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '船次',
                                       `bill_lading_num` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '提单号',
                                       `est_closing_time` datetime DEFAULT NULL COMMENT '预计结单时间',
                                       `est_clearance_time` datetime DEFAULT NULL COMMENT '预计结关时间',
                                       `est_departure_time` datetime DEFAULT NULL COMMENT '预计结港时间',
                                       `twenty_foot_cabinet_num` int NOT NULL DEFAULT '0' COMMENT '20尺柜',
                                       `forty_foot_cabinet_num` int NOT NULL DEFAULT '0' COMMENT '40尺柜',
                                       `forty_foot_container_num` int NOT NULL DEFAULT '0' COMMENT '40尺高柜',
                                       `bulk_handling_volume` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '散货',
                                       `total_quantity` int NOT NULL DEFAULT '0' COMMENT '数量合计',
                                       `total_boxes` int NOT NULL DEFAULT '0' COMMENT '箱数合计',
                                       `total_grossweight` json NOT NULL DEFAULT (json_object()) COMMENT '毛重合计',
                                       `total_weight` json NOT NULL DEFAULT (json_object()) COMMENT '净重合计',
                                       `total_volume` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '体积合计',
                                       `total_purchase` json NOT NULL DEFAULT (json_array()) COMMENT '采购总金额',
                                       `total_tax_refund_price` json NOT NULL DEFAULT (json_object()) COMMENT '退税总额',
                                       `commission_amount` json NOT NULL DEFAULT (json_object()) COMMENT '佣金金额',
                                       `insurance_fee` json NOT NULL DEFAULT (json_object()) COMMENT '保险费用',
                                       `addition_amount` json NOT NULL DEFAULT (json_object()) COMMENT '加项金额',
                                       `total_addition_amount` json NOT NULL DEFAULT (json_object()) COMMENT '加项总额',
                                       `deduction_amount` json NOT NULL DEFAULT (json_object()) COMMENT '减项金额',
                                       `total_deduction_amount` json NOT NULL DEFAULT (json_object()) COMMENT '减项总额',
                                       `create_user` json NOT NULL DEFAULT (json_object()) COMMENT '变更人',
                                       `received_num` int NOT NULL DEFAULT '0' COMMENT '已收货值',
                                       `unreceived_num` int NOT NULL DEFAULT '0' COMMENT '未收货值',
                                       `input_user` json NOT NULL COMMENT '录入人',
                                       `input_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '录入日期',
                                       `children` json NOT NULL DEFAULT (json_array()) COMMENT '出运单明细',
                                       `add_sub_item_list` json NOT NULL DEFAULT (json_array()) COMMENT '加减项列表',
                                       `temporary_sku_list` json NOT NULL DEFAULT (json_array()) COMMENT '临时产品',
                                       `shipment_cust_list` json NOT NULL DEFAULT (json_array()) COMMENT '出货客户信息',
                                       `shipment_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '出运单编号',
                                       `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                       `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                       `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                       `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除',
                                       `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                       PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1085 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='出运单变更表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `dms_shipment_item`
--

DROP TABLE IF EXISTS `dms_shipment_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dms_shipment_item` (
                                     `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                     `barcode` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '条形码',
                                     `stock_purchase_contract_codes` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '库存采购合同号',
                                     `process_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否需要加工',
                                     `free_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否包含赠品',
                                     `source_item_id` bigint DEFAULT NULL COMMENT '来源明细id',
                                     `split_quantity` int NOT NULL DEFAULT '0' COMMENT '拆分数量',
                                     `thumbnail` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '缩略图',
                                     `main_picture` json NOT NULL DEFAULT (json_object()) COMMENT '主图',
                                     `split_box_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否分箱',
                                     `specification_list` json NOT NULL DEFAULT (json_array()) COMMENT '规格',
                                     `declaration_quantity_current` int DEFAULT NULL COMMENT '本次报关数量',
                                     `basic_sku_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '基础产品编号',
                                     `shipped_address` tinyint NOT NULL DEFAULT '1' COMMENT '发货地点',
                                     `check_status` tinyint NOT NULL DEFAULT '0' COMMENT '验货状态',
                                     `stock_cost` json NOT NULL DEFAULT (json_object()) COMMENT '库存成本',
                                     `bill_status` tinyint NOT NULL DEFAULT '1' COMMENT '入库状态',
                                     `measure_unit` int DEFAULT NULL COMMENT '计量单位',
                                     `cust_po` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '客户合同号',
                                     `real_lock_quantity` int NOT NULL DEFAULT '0' COMMENT '真实锁定数量',
                                     `out_date` datetime DEFAULT NULL COMMENT '出库日期',
                                     `out_quantity` int NOT NULL DEFAULT '0' COMMENT '出库数量',
                                     `purchase_model` int NOT NULL DEFAULT '0' COMMENT '采购拆分标记',
                                     `this_purchase_quantity` int NOT NULL DEFAULT '0' COMMENT '本次出运使用的采购数量',
                                     `pay_vender_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '应付供应商名称',
                                     `pay_vender_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '应付供应商编号',
                                     `pay_vender_id` bigint DEFAULT NULL COMMENT '应付供应商',
                                     `sales` json NOT NULL DEFAULT (json_object()) COMMENT '销售人员',
                                     `source_sale_contract_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '来源销售合同编号',
                                     `hs_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT 'HS编码',
                                     `purchase_with_tax_price` json NOT NULL DEFAULT (json_object()) COMMENT '采购含税价',
                                     `conver_notice_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '转拉柜标识：0-否 1-是',
                                     `invoice_status` tinyint NOT NULL DEFAULT '0' COMMENT '开票状态',
                                     `invoiced_quantity` int DEFAULT NULL COMMENT '已开票数量',
                                     `settlement_quantity` int DEFAULT NULL COMMENT '结汇数量',
                                     `source_unique_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '来源编号',
                                     `unique_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '唯一编号',
                                     `forwarder_share_amount` json NOT NULL DEFAULT (json_object()) COMMENT '船代费用均摊金额',
                                     `sale_contract_item_id` bigint DEFAULT NULL COMMENT '销售明细主键',
                                     `declaration_quantity_old` int NOT NULL DEFAULT '0' COMMENT '已报关数量',
                                     `status` tinyint NOT NULL COMMENT '状态',
                                     `settlement_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '收款方式名称',
                                     `settlement_id` bigint DEFAULT NULL COMMENT '收款方式主键',
                                     `currency` varchar(3) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '交易币别',
                                     `vender_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '供应商编号',
                                     `vender_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '供应商名称',
                                     `vender_id` bigint unsigned NOT NULL DEFAULT '0' COMMENT '供应商id',
                                     `delivery_date` datetime DEFAULT NULL COMMENT '交货日期',
                                     `shipping_quantity` int DEFAULT NULL COMMENT '出运数量',
                                     `commodity_inspection_flag` tinyint DEFAULT NULL COMMENT '是否商检',
                                     `manager` json NOT NULL DEFAULT (json_object()) COMMENT '业务员',
                                     `insurance_fee` json NOT NULL DEFAULT (json_object()) COMMENT '保险费',
                                     `commodity_inspection_date` datetime DEFAULT NULL COMMENT '转商检单时间',
                                     `commodity_inspection_user` json NOT NULL DEFAULT (json_object()) COMMENT '转商检单人',
                                     `is_to_commodity_inspection` int NOT NULL DEFAULT '0' COMMENT '是否转商检单',
                                     `settlement_form_date` datetime DEFAULT NULL COMMENT '转结汇单时间',
                                     `settlement_form_user` json NOT NULL DEFAULT (json_object()) COMMENT '转结汇单人',
                                     `is_to_settlement_form` int NOT NULL DEFAULT '1' COMMENT '是否转结汇单',
                                     `declaration_date` datetime DEFAULT NULL COMMENT '转报关单时间',
                                     `declaration_user` json NOT NULL DEFAULT (json_object()) COMMENT '转报关单人',
                                     `is_to_declaration` int NOT NULL DEFAULT '0' COMMENT '是否转报关单',
                                     `unreceived_num` int NOT NULL DEFAULT '0' COMMENT '未收货值',
                                     `received_num` int NOT NULL DEFAULT '0' COMMENT '已收货值',
                                     `sale_contract_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '外销合同号',
                                     `cust_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '客户名称',
                                     `cust_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '客户编号',
                                     `cust_id` bigint DEFAULT NULL COMMENT '客户主键',
                                     `shipment_id` bigint DEFAULT NULL COMMENT '出运单主键',
                                     `est_pickup_time` datetime DEFAULT NULL COMMENT '预计拉柜时间',
                                     `sku_id` bigint DEFAULT NULL COMMENT '产品主键',
                                     `sku_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT 'SKU编号',
                                     `csku_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '客户货号',
                                     `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '中文名称',
                                     `name_eng` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '英文名称',
                                     `declaration_quantity` int unsigned NOT NULL DEFAULT '0' COMMENT '报关数量',
                                     `purchase_total_quantity` int NOT NULL DEFAULT '0' COMMENT '采购总数量',
                                     `sale_unit_price` json NOT NULL DEFAULT (json_object()) COMMENT '销售单价',
                                     `sale_amount` json NOT NULL DEFAULT (json_object()) COMMENT '销售金额',
                                     `declaration_unit_price` json NOT NULL DEFAULT (json_object()) COMMENT '报关单价',
                                     `declaration_amount` json NOT NULL DEFAULT (json_object()) COMMENT '报关金额',
                                     `commodity_inspection_type` tinyint DEFAULT NULL COMMENT '商检类型',
                                     `declaration_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '报关中文品名',
                                     `customs_declaration_name_eng` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '报关英文品名',
                                     `hs_measure_unit` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '海关计量单位',
                                     `tax_refund_rate` decimal(19,6) DEFAULT NULL COMMENT '退税率',
                                     `tax_refund_price` json NOT NULL DEFAULT (json_object()) COMMENT '退税金额',
                                     `purchase_contract_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '采购合同编号',
                                     `purchase_user_id` bigint DEFAULT NULL COMMENT '采购员id',
                                     `purchase_user_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '采购员姓名',
                                     `purchase_user_dept_id` bigint DEFAULT NULL COMMENT '采购员部门id',
                                     `purchase_user_dept_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '采购员部门名称',
                                     `purchase_currency` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '采购币种',
                                     `purchase_unit_price` json NOT NULL DEFAULT (json_object()) COMMENT '采购单价',
                                     `package_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '包装方式',
                                     `qty_per_outerbox` int NOT NULL DEFAULT '0' COMMENT '外箱装量',
                                     `qty_per_innerbox` int NOT NULL DEFAULT '0' COMMENT '内盒装量',
                                     `box_count` int NOT NULL DEFAULT '0' COMMENT '箱数',
                                     `unit_per_outerbox` int NOT NULL DEFAULT '0' COMMENT '外箱单位',
                                     `outerbox_length` decimal(19,6) unsigned DEFAULT NULL COMMENT '外箱长度',
                                     `outerbox_width` decimal(19,6) unsigned DEFAULT NULL COMMENT '外箱宽度',
                                     `outerbox_height` decimal(19,6) unsigned DEFAULT NULL COMMENT '外箱高度',
                                     `outerbox_volume` decimal(19,6) unsigned DEFAULT NULL COMMENT '外箱体积',
                                     `total_volume` decimal(19,6) unsigned DEFAULT NULL COMMENT '总体积',
                                     `outerbox_netweight` json NOT NULL DEFAULT (json_object()) COMMENT '外箱净重',
                                     `total_netweight` json NOT NULL DEFAULT (json_object()) COMMENT '总净重',
                                     `outerbox_grossweight` json NOT NULL DEFAULT (json_object()) COMMENT '外箱毛重',
                                     `total_grossweight` json NOT NULL DEFAULT (json_object()) COMMENT '总毛重',
                                     `description` varchar(5000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '产品描述',
                                     `description_eng` varchar(5000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '产品英文描述',
                                     `stock_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '仓库编号',
                                     `stock_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '仓库名称',
                                     `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '备注',
                                     `commission_amount` json NOT NULL DEFAULT (json_object()) COMMENT '佣金金额',
                                     `order_gross_profit_rate` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '毛利率',
                                     `outbound_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否出仓',
                                     `outbound_date` datetime DEFAULT NULL COMMENT '出仓日期',
                                     `declaration_unit` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '报关单位',
                                     `declared_quantity` int NOT NULL DEFAULT '0' COMMENT '已报关数',
                                     `settle_order_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否转结汇单',
                                     `settlement_term_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '价格条款',
                                     `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                     `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                     `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                     `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除',
                                     `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                     PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5605 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='出运单明细';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `dms_shipment_plan`
--

DROP TABLE IF EXISTS `dms_shipment_plan`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dms_shipment_plan` (
                                     `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                     `front_shipping_mark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '正面唛头',
                                     `side_shipping_mark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '侧面唛头',
                                     `receive_person` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '收货人',
                                     `notify_person` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '通知人',
                                     `est_delivery_date` datetime DEFAULT NULL COMMENT '预计交货日期',
                                     `settlement_term_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '价格条款',
                                     `manager_list` json NOT NULL DEFAULT (json_array()) COMMENT '业务员',
                                     `purchase_user_list` json NOT NULL DEFAULT (json_array()) COMMENT '采购员',
                                     `purchase_total_price` json NOT NULL DEFAULT (json_array()) COMMENT '采购合计',
                                     `total_goods_value` json NOT NULL DEFAULT (json_array()) COMMENT '货值合计',
                                     `link_code_list` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '订单链路编号',
                                     `code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '编号',
                                     `status` tinyint DEFAULT NULL COMMENT '单据状态',
                                     `input_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '录入日期',
                                     `est_ship_date` datetime DEFAULT NULL COMMENT '预计出运时间',
                                     `sale_contract_code` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '外销合同号',
                                     `departure_country_id` bigint DEFAULT NULL COMMENT '出运国主键',
                                     `departure_country_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '出运国名称',
                                     `departure_country_area` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '出运国区域',
                                     `departure_port_id` bigint DEFAULT NULL COMMENT '出运口岸主键',
                                     `departure_port_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '出运口岸名称',
                                     `trade_country_id` bigint DEFAULT NULL COMMENT '贸易国别主键',
                                     `trade_country_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '贸易国别名称',
                                     `trade_country_area` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '贸易国别区域',
                                     `destination_port_id` bigint DEFAULT NULL COMMENT '目的口岸主键',
                                     `destination_port_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '目的口岸名称',
                                     `cust_po` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '客户PO号',
                                     `collected_cust_id` bigint DEFAULT NULL COMMENT '应收客户主键',
                                     `collected_cust_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '应收客户编号',
                                     `collected_cust_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                                     `receive_cust_id` bigint DEFAULT NULL COMMENT '收货客户主键',
                                     `cust_delivery_date` datetime DEFAULT NULL COMMENT '客户交期',
                                     `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '备注',
                                     `annex` json DEFAULT NULL COMMENT '附件',
                                     `foreign_trade_company_id` bigint DEFAULT NULL COMMENT '外贸公司主体主键',
                                     `foreign_trade_company_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '外贸公司主体名称',
                                     `twenty_foot_cabinet_num` int NOT NULL DEFAULT '0' COMMENT '20尺柜',
                                     `forty_foot_cabinet_num` int NOT NULL DEFAULT '0' COMMENT '40尺柜',
                                     `forty_foot_container_num` int NOT NULL DEFAULT '0' COMMENT '40尺高柜',
                                     `bulk_handling_volume` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '散货',
                                     `total_quantity` int NOT NULL DEFAULT '0' COMMENT '数量合计',
                                     `total_boxes` int NOT NULL DEFAULT '0' COMMENT '箱数合计',
                                     `total_grossweight` json NOT NULL DEFAULT (json_object()) COMMENT '毛重合计',
                                     `total_weight` json NOT NULL DEFAULT (json_object()) COMMENT '净重合计',
                                     `total_volume` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '体积合计',
                                     `transport_type` tinyint DEFAULT NULL COMMENT '运输方式',
                                     `input_user` json NOT NULL DEFAULT (json_object()) COMMENT '录入人',
                                     `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                     `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                     `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                     `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除',
                                     `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                     PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=643 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='出运计划单';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `dms_shipment_plan_item`
--

DROP TABLE IF EXISTS `dms_shipment_plan_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dms_shipment_plan_item` (
                                          `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                          `barcode` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '条形码',
                                          `stock_purchase_contract_codes` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '库存采购合同号',
                                          `free_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否包含赠品',
                                          `company_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '下单主体名称',
                                          `company_id` bigint DEFAULT NULL COMMENT '下单主体主键',
                                          `thumbnail` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '缩略图',
                                          `main_picture` json NOT NULL DEFAULT (json_object()) COMMENT '主图',
                                          `split_box_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否分箱',
                                          `specification_list` json NOT NULL DEFAULT (json_array()) COMMENT '规格',
                                          `basic_sku_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '基础产品编号',
                                          `shipped_address` tinyint NOT NULL DEFAULT '1' COMMENT '发货地点',
                                          `measure_unit` int DEFAULT NULL COMMENT '计量单位',
                                          `real_lock_quantity` int NOT NULL DEFAULT '0' COMMENT '真实锁定数量',
                                          `purchase_shipping_price` json NOT NULL DEFAULT (json_object()) COMMENT '运费',
                                          `pay_vender_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '应付供应商名称',
                                          `pay_vender_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '应付供应商编号',
                                          `pay_vender_id` bigint DEFAULT NULL COMMENT '应付供应商',
                                          `sales` json NOT NULL DEFAULT (json_object()) COMMENT '销售人员',
                                          `hs_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT 'HS编码',
                                          `source_unique_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '来源编号',
                                          `unique_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '唯一编号',
                                          `hs_measure_unit` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '海关计量单位',
                                          `sale_contract_item_id` bigint DEFAULT NULL COMMENT '销售明细主键',
                                          `vender_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '供应商编号',
                                          `vender_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '供应商名称',
                                          `vender_id` bigint unsigned NOT NULL DEFAULT '0' COMMENT '供应商id',
                                          `currency` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '币种',
                                          `manager` json NOT NULL DEFAULT (json_object()) COMMENT '业务员',
                                          `shipping_quantity` int DEFAULT NULL COMMENT '出运数量',
                                          `transform_shipment_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否转出运明细',
                                          `status` tinyint DEFAULT NULL COMMENT '状态',
                                          `shipment_plan_id` bigint DEFAULT NULL COMMENT '出运单主键',
                                          `sku_id` bigint DEFAULT NULL COMMENT '产品主键',
                                          `sku_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT 'SKU编号',
                                          `csku_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '客户货号',
                                          `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '中文品名',
                                          `name_eng` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '英文品名',
                                          `purchase_quantity` int NOT NULL DEFAULT '0' COMMENT '采购数量',
                                          `sale_unit_price` json NOT NULL DEFAULT (json_object()) COMMENT '销售单价',
                                          `sale_amount` json NOT NULL DEFAULT (json_object()) COMMENT '销售金额',
                                          `commodity_inspection_flag` tinyint DEFAULT NULL COMMENT '商检类型',
                                          `tax_refund_rate` decimal(19,6) DEFAULT NULL COMMENT '退税率',
                                          `tax_refund_price` json NOT NULL DEFAULT (json_object()) COMMENT '退税金额',
                                          `purchase_contract_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '采购合同编号',
                                          `purchase_user_id` bigint DEFAULT NULL COMMENT '采购员id',
                                          `purchase_user_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '采购员姓名',
                                          `purchase_user_dept_id` bigint DEFAULT NULL COMMENT '采购员部门id',
                                          `purchase_user_dept_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '采购员部门名称',
                                          `purchase_currency` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '采购币种',
                                          `purchase_unit_price` json NOT NULL DEFAULT (json_object()) COMMENT '采购单价',
                                          `package_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '包装方式',
                                          `qty_per_outerbox` int NOT NULL DEFAULT '0' COMMENT '外箱装量',
                                          `qty_per_innerbox` int NOT NULL DEFAULT '0' COMMENT '内盒装量',
                                          `box_count` int NOT NULL DEFAULT '0' COMMENT '箱数',
                                          `unit_per_outerbox` int NOT NULL DEFAULT '0' COMMENT '外箱单位',
                                          `outerbox_length` decimal(19,6) unsigned DEFAULT NULL COMMENT '外箱长度',
                                          `outerbox_width` decimal(19,6) unsigned DEFAULT NULL COMMENT '外箱宽度',
                                          `outerbox_height` decimal(19,6) unsigned DEFAULT NULL COMMENT '外箱高度',
                                          `outerbox_volume` decimal(19,6) unsigned DEFAULT NULL COMMENT '外箱体积',
                                          `total_volume` decimal(19,6) unsigned DEFAULT NULL COMMENT '总体积',
                                          `outerbox_netweight` json NOT NULL DEFAULT (json_object()) COMMENT '外箱净重',
                                          `total_netweight` json NOT NULL DEFAULT (json_object()) COMMENT '总净重',
                                          `outerbox_grossweight` json NOT NULL DEFAULT (json_object()) COMMENT '外箱毛重',
                                          `total_grossweight` json NOT NULL DEFAULT (json_object()) COMMENT '总毛重',
                                          `description` varchar(5000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '产品描述',
                                          `description_eng` varchar(5000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '产品英文描述',
                                          `stock_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '仓库编号',
                                          `stock_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '仓库名称',
                                          `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '备注',
                                          `commission_amount` json NOT NULL DEFAULT (json_object()) COMMENT '佣金金额',
                                          `order_gross_profit_rate` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '毛利率',
                                          `sale_contract_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '外销合同编号',
                                          `cust_po` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '客户PO号',
                                          `cust_id` bigint DEFAULT NULL COMMENT '客户主键',
                                          `cust_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '客户编号',
                                          `cust_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                                          `settlement_id` bigint DEFAULT NULL COMMENT '收款方式主键',
                                          `settlement_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '收款方式名称',
                                          `settlement_term_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '价格条款',
                                          `delivery_date` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '客户交期',
                                          `commission_type` tinyint DEFAULT NULL COMMENT '佣金类型',
                                          `commission_rate` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '佣金比例',
                                          `purchase_packaging_price` json NOT NULL DEFAULT (json_object()) COMMENT '包装价',
                                          `purchase_total_price` json NOT NULL DEFAULT (json_object()) COMMENT '总价',
                                          `purchase_with_tax_price` json NOT NULL DEFAULT (json_object()) COMMENT '含税总价',
                                          `sale_quantity` int NOT NULL DEFAULT '0' COMMENT '销售数量',
                                          `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                          `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                          `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                          `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除',
                                          `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                          PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2533 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='出运计划单明细';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `dms_temporary_sku`
--

DROP TABLE IF EXISTS `dms_temporary_sku`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dms_temporary_sku` (
                                     `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                     `split_box_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否分箱',
                                     `specification_list` json NOT NULL DEFAULT (json_array()) COMMENT '规格',
                                     `shipment_id` bigint DEFAULT NULL COMMENT '出运单主键',
                                     `cust_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '客户',
                                     `hs_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '海关编码',
                                     `declaration_element` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '报关要素',
                                     `declaration_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '报关品名',
                                     `declaration_name_eng` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '报关英文名',
                                     `expect_count` int DEFAULT NULL COMMENT '出货数量',
                                     `hs_measure_unit` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '海关计量单位',
                                     `declaration_count` int DEFAULT NULL COMMENT '报关数量',
                                     `box_count` int DEFAULT NULL COMMENT '箱数',
                                     `unit_per_outerbox` int DEFAULT NULL COMMENT '外箱单位',
                                     `pricing_method` tinyint DEFAULT NULL COMMENT '计价方式',
                                     `declaration_unit` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '报关单位',
                                     `declaration_total_price` json DEFAULT (json_object()) COMMENT '报关总价',
                                     `outerbox_volume` decimal(19,6) unsigned DEFAULT NULL COMMENT '外箱体积',
                                     `total_volume` decimal(19,6) unsigned DEFAULT NULL COMMENT '总体积',
                                     `outerbox_netweight` json DEFAULT (json_object()) COMMENT '外箱净重',
                                     `total_netweight` json DEFAULT (json_object()) COMMENT '总净重',
                                     `outerbox_grossweight` json DEFAULT (json_object()) COMMENT '外箱毛重',
                                     `total_grossweight` json DEFAULT (json_object()) COMMENT '总毛重',
                                     `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                     `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                     `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                     `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除',
                                     `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                     PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='出运临时客户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `doc_change_impact_field`
--

DROP TABLE IF EXISTS `doc_change_impact_field`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `doc_change_impact_field` (
                                           `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                           `doc_code` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '单据类型编码',
                                           `field_name` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '字段名(英文)',
                                           `field_label` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '字段标签(中文)',
                                           `impact_level` tinyint NOT NULL DEFAULT '1' COMMENT '影响级别 1-无影响 2-需通知 3-需审批 4-禁止变更',
                                           `impact_doc_codes` json DEFAULT NULL COMMENT '影响的下游单据类型列表',
                                           `notify_required` tinyint DEFAULT '0' COMMENT '变更时是否需要通知 0-否 1-是',
                                           `approval_required` tinyint DEFAULT '0' COMMENT '变更时是否需要审批 0-否 1-是',
                                           `description` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '描述',
                                           `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
                                           `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                           `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
                                           `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                           `deleted` bit(1) DEFAULT b'0' COMMENT '是否删除',
                                           `tenant_id` bigint DEFAULT '0' COMMENT '租户编号',
                                           PRIMARY KEY (`id`),
                                           UNIQUE KEY `uk_doc_field` (`doc_code`,`field_name`),
                                           KEY `idx_doc_code` (`doc_code`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='变更影响字段配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `doc_document_definition`
--

DROP TABLE IF EXISTS `doc_document_definition`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `doc_document_definition` (
                                           `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                           `code` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '单据类型编码',
                                           `name` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '单据类型名称',
                                           `table_name` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '关联的数据库表名',
                                           `module_code` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '所属模块编码',
                                           `sort` int DEFAULT '0' COMMENT '排序(用于流程顺序)',
                                           `can_be_source` tinyint DEFAULT '1' COMMENT '是否可作为源单据 0-否 1-是',
                                           `can_be_target` tinyint DEFAULT '1' COMMENT '是否可作为目标单据 0-否 1-是',
                                           `state_config` json DEFAULT NULL COMMENT '状态机配置(JSON格式)',
                                           `description` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '描述',
                                           `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
                                           `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                           `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
                                           `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                           `deleted` bit(1) DEFAULT b'0' COMMENT '是否删除',
                                           `tenant_id` bigint DEFAULT '0' COMMENT '租户编号',
                                           PRIMARY KEY (`id`),
                                           UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='单据类型定义表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `doc_document_link`
--

DROP TABLE IF EXISTS `doc_document_link`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `doc_document_link` (
                                     `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                     `link_code` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '链路编号(同一业务链共享)',
                                     `source_doc_code` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '源单据类型编码',
                                     `source_doc_id` bigint NOT NULL COMMENT '源单据ID',
                                     `source_doc_no` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '源单据编号',
                                     `source_item_id` bigint DEFAULT NULL COMMENT '源单据明细ID(可选)',
                                     `target_doc_code` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '目标单据类型编码',
                                     `target_doc_id` bigint NOT NULL COMMENT '目标单据ID',
                                     `target_doc_no` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '目标单据编号',
                                     `target_item_id` bigint DEFAULT NULL COMMENT '目标单据明细ID(可选)',
                                     `relation_type` tinyint NOT NULL DEFAULT '1' COMMENT '关系类型 1-引用 2-转换 3-拆分 4-合并',
                                     `quantity` decimal(18,4) DEFAULT NULL COMMENT '引用数量(用于部分引用场景)',
                                     `status` tinyint DEFAULT '1' COMMENT '状态 1-有效 0-无效',
                                     `extra_data` json DEFAULT NULL COMMENT '扩展数据',
                                     `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
                                     `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                     `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
                                     `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                     `deleted` bit(1) DEFAULT b'0' COMMENT '是否删除',
                                     `tenant_id` bigint DEFAULT '0' COMMENT '租户编号',
                                     PRIMARY KEY (`id`),
                                     KEY `idx_link_code` (`link_code`),
                                     KEY `idx_source` (`source_doc_code`,`source_doc_id`),
                                     KEY `idx_target` (`target_doc_code`,`target_doc_id`),
                                     KEY `idx_source_item` (`source_doc_code`,`source_doc_id`,`source_item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='单据实例关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `doc_flow_log`
--

DROP TABLE IF EXISTS `doc_flow_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `doc_flow_log` (
                                `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                `doc_code` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '单据类型编码',
                                `doc_id` bigint NOT NULL COMMENT '单据ID',
                                `doc_no` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '单据编号',
                                `action` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '操作类型(CREATE/UPDATE/VOID/APPROVE等)',
                                `from_status` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '变更前状态',
                                `to_status` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '变更后状态',
                                `change_fields` json DEFAULT NULL COMMENT '变更的字段(JSON数组)',
                                `change_detail` json DEFAULT NULL COMMENT '变更详情(包含新旧值)',
                                `operator_id` bigint DEFAULT NULL COMMENT '操作人ID',
                                `operator_name` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作人名称',
                                `remark` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
                                `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                `tenant_id` bigint DEFAULT '0' COMMENT '租户编号',
                                PRIMARY KEY (`id`),
                                KEY `idx_doc` (`doc_code`,`doc_id`),
                                KEY `idx_doc_no` (`doc_no`),
                                KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='单据流转日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `doc_relation_rule`
--

DROP TABLE IF EXISTS `doc_relation_rule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `doc_relation_rule` (
                                     `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                     `source_doc_code` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '源单据类型编码',
                                     `target_doc_code` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '目标单据类型编码',
                                     `relation_type` tinyint NOT NULL DEFAULT '1' COMMENT '关系类型 1-引用 2-转换 3-拆分 4-合并',
                                     `is_required` tinyint DEFAULT '0' COMMENT '目标单据是否必须引用源单据 0-否 1-是',
                                     `cascade_void` tinyint DEFAULT '0' COMMENT '源单据废弃时是否级联废弃目标 0-否 1-是',
                                     `cascade_update` tinyint DEFAULT '1' COMMENT '源单据变更时是否通知目标 0-否 1-是',
                                     `void_check_required` tinyint DEFAULT '1' COMMENT '废弃前是否需要检查目标单据 0-否 1-是',
                                     `description` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '描述',
                                     `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
                                     `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                     `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
                                     `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                     `deleted` bit(1) DEFAULT b'0' COMMENT '是否删除',
                                     `tenant_id` bigint DEFAULT '0' COMMENT '租户编号',
                                     PRIMARY KEY (`id`),
                                     UNIQUE KEY `uk_source_target` (`source_doc_code`,`target_doc_code`),
                                     KEY `idx_source` (`source_doc_code`),
                                     KEY `idx_target` (`target_doc_code`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='单据关系规则表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `dpms_report`
--

DROP TABLE IF EXISTS `dpms_report`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dpms_report` (
                               `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                               `component_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '组件编号',
                               `user_id` bigint DEFAULT NULL COMMENT '用户编号',
                               `role_id` bigint DEFAULT NULL COMMENT '角色编号',
                               `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '名称',
                               `type` tinyint DEFAULT NULL COMMENT '类型',
                               `sort` int DEFAULT NULL COMMENT '显示顺序',
                               `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                               `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                               `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                               `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除  0：有效 1：删除',
                               PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='报表配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `dpms_report_role`
--

DROP TABLE IF EXISTS `dpms_report_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dpms_report_role` (
                                    `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                                    `role_id` bigint DEFAULT NULL COMMENT '角色编号',
                                    `report_id` bigint DEFAULT NULL COMMENT '报表编号',
                                    `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                    `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                    `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除  0：有效 1：删除',
                                    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='报表角色关系表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `dtms_design`
--

DROP TABLE IF EXISTS `dtms_design`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dtms_design` (
                               `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                               `complete_date` datetime DEFAULT NULL COMMENT '实际完成日期',
                               `estimated_complete_date` datetime DEFAULT NULL COMMENT '预计完成日期',
                               `specific_designers` json NOT NULL DEFAULT (json_object()) COMMENT '指定设计师',
                               `is_supplement_order` tinyint NOT NULL DEFAULT '0' COMMENT '是否补单',
                               `audit_id` bigint DEFAULT NULL COMMENT '审核人主键',
                               `contract_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '合同编号',
                               `contract_type` tinyint DEFAULT NULL COMMENT '合同类型',
                               `code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '单号',
                               `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '设计任务名称',
                               `audit_status` tinyint DEFAULT NULL COMMENT '审核状态',
                               `design_status` tinyint DEFAULT NULL COMMENT '任务状态：1：待提交，2：待审批，3：待完成，4：待评价，5：已完成，6：已作废，7：已驳回',
                               `special_permission_flag` tinyint DEFAULT '0' COMMENT '是否特批:0-否 1-是',
                               `special_permission_reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '特批原因',
                               `design_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '设计任务类型（多选）1：亚马逊，2：阿里，3：拍照抠图P图，4：包材设计，5：不干胶设计及打印，6：视频拍摄制作，7：效果图设计，8：样本宣传页',
                               `expect_complete_date` datetime DEFAULT NULL COMMENT '期望完成日期',
                               `apply_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
                               `apply_designer_id` bigint DEFAULT NULL COMMENT '申请人主键',
                               `apply_designer_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '申请人姓名',
                               `apply_designer_dept_id` bigint DEFAULT NULL COMMENT '申请人部门主键',
                               `apply_designer_dept_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '申请人部门名称',
                               `design_requirement` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '设计要求',
                               `material_desc` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '素材说明',
                               `designer_ids` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '认领人主键',
                               `annex` json DEFAULT NULL COMMENT '附件',
                               `close_reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '结案原因',
                               `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
                               `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                               `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                               `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                               `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除  0-有效 1-删除',
                               PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=462 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='设计-任务单';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `dtms_design_item`
--

DROP TABLE IF EXISTS `dtms_design_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dtms_design_item` (
                                    `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                                    `design_id` bigint NOT NULL COMMENT '设计任务单主键',
                                    `designer_id` bigint DEFAULT NULL COMMENT '认领人主键',
                                    `designer_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '认领人姓名',
                                    `designer_dept_id` bigint DEFAULT NULL COMMENT '认领人部门主键',
                                    `designer_dept_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '认领人部门名称',
                                    `plan_start_date` datetime DEFAULT NULL COMMENT '计划开始日期',
                                    `plan_complete_date` datetime DEFAULT NULL COMMENT '计划完成日期',
                                    `complete_flag` tinyint DEFAULT NULL COMMENT '完成标识 0-否 1-是',
                                    `item_type` tinyint DEFAULT NULL COMMENT '设计任务类型1：常规任务，2：临时任务',
                                    `design_file_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '设计文件位置',
                                    `accept_date` datetime DEFAULT NULL COMMENT '认领日期',
                                    `complete_date` datetime DEFAULT NULL COMMENT '完成日期',
                                    `evaluate_result` tinyint DEFAULT NULL COMMENT '评价结果；1：投诉，2：优秀，3：点赞',
                                    `evaluate_desc` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '评价描述',
                                    `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
                                    `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                    `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                    `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除  0-有效 1-删除',
                                    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=467 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='设计-认领明细';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `dtms_design_summary`
--

DROP TABLE IF EXISTS `dtms_design_summary`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dtms_design_summary` (
                                       `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                                       `design_id` bigint NOT NULL COMMENT '设计任务单主键',
                                       `design_item_id` bigint NOT NULL COMMENT '设计任务单-明细主键',
                                       `designer_id` bigint DEFAULT NULL COMMENT '认领人主键',
                                       `designer_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '认领人姓名',
                                       `designer_dept_id` bigint DEFAULT NULL COMMENT '认领人部门主键',
                                       `designer_dept_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '认领人部门名称',
                                       `progress` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '当前进度',
                                       `progress_desc` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '进度描述',
                                       `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
                                       `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                       `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                       `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                       `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                       `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除  0-有效 1-删除',
                                       PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=99 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='设计-工作总结';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ems_send`
--

DROP TABLE IF EXISTS `ems_send`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ems_send` (
                            `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                            `vender_short_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '快递公司简称',
                            `actual_user` json NOT NULL DEFAULT (json_object()) COMMENT '实际寄件人',
                            `deal_user` json NOT NULL DEFAULT (json_object()) COMMENT '处理人',
                            `vender_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '快递公司编号',
                            `receive_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '接收人名称',
                            `receive_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '收件人编号',
                            `receive_type` int DEFAULT NULL COMMENT '收件人类型',
                            `input_dept_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '录入人部门名称',
                            `input_dept_id` bigint DEFAULT NULL COMMENT '录入人部门id',
                            `receive_msg` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '收件人信息',
                            `code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '编号',
                            `input_user_id` bigint DEFAULT NULL COMMENT '录入人id',
                            `input_user_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '录入人姓名',
                            `send_region` int DEFAULT NULL COMMENT '寄件区域',
                            `goods_type` int DEFAULT NULL COMMENT '物件类型',
                            `pay_type` int DEFAULT NULL COMMENT '付款方式',
                            `vender_id` bigint DEFAULT NULL COMMENT '快递公司主键',
                            `vender_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '快递公司名称',
                            `est_cost` json DEFAULT (json_object()) COMMENT '预估费用',
                            `remark` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
                            `send_status` int DEFAULT NULL COMMENT '寄件状态',
                            `audit_status` int DEFAULT NULL COMMENT '审核状态',
                            `express_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '物流单号',
                            `cost` json DEFAULT (json_object()) COMMENT '实际费用',
                            `belong_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否归属费用',
                            `pay_status` int DEFAULT NULL COMMENT '付款状态',
                            `submit_time` datetime DEFAULT NULL COMMENT '提交时间',
                            `send_time` datetime DEFAULT NULL COMMENT '寄件时间',
                            `cost_time` datetime DEFAULT NULL COMMENT '费用写入时间',
                            `done_time` datetime DEFAULT NULL COMMENT '完成时间',
                            `pay_time` datetime DEFAULT NULL COMMENT '付款时间',
                            `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                            `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                            `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                            `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除  0：有效 1：删除',
                            PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1405 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='寄件表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ems_send_bill`
--

DROP TABLE IF EXISTS `ems_send_bill`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ems_send_bill` (
                                 `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                                 `error_desc` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '导入类型',
                                 `import_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '异常原因',
                                 `currency` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '币种',
                                 `remark` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
                                 `vender_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '快递公司名称',
                                 `vender_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '快递公司编号',
                                 `vender_id` bigint DEFAULT NULL COMMENT '快递公司id',
                                 `sort_num` int DEFAULT NULL COMMENT '序号',
                                 `batch_code` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '批次编号',
                                 `bill_date` datetime DEFAULT NULL COMMENT '导入时间',
                                 `code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '运单号码',
                                 `cost` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '费用',
                                 `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                 `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                 `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                 `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                 `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除  0：有效 1：删除',
                                 PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4571 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='寄件导入单据表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ems_send_copy1`
--

DROP TABLE IF EXISTS `ems_send_copy1`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ems_send_copy1` (
                                  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                                  `actual_user` json NOT NULL DEFAULT (json_object()) COMMENT '实际寄件人',
                                  `deal_user` json NOT NULL DEFAULT (json_object()) COMMENT '处理人',
                                  `vender_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '快递公司编号',
                                  `receive_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '接收人名称',
                                  `receive_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '收件人编号',
                                  `receive_type` int DEFAULT NULL COMMENT '收件人类型',
                                  `input_dept_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '录入人部门名称',
                                  `input_dept_id` bigint DEFAULT NULL COMMENT '录入人部门id',
                                  `receive_msg` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '收件人信息',
                                  `code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '编号',
                                  `input_user_id` bigint DEFAULT NULL COMMENT '录入人id',
                                  `input_user_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '录入人姓名',
                                  `send_region` int DEFAULT NULL COMMENT '寄件区域',
                                  `goods_type` int DEFAULT NULL COMMENT '物件类型',
                                  `pay_type` int DEFAULT NULL COMMENT '付款方式',
                                  `vender_id` bigint DEFAULT NULL COMMENT '快递公司主键',
                                  `vender_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '快递公司名称',
                                  `est_cost` json DEFAULT (json_object()) COMMENT '预估费用',
                                  `remark` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
                                  `send_status` int DEFAULT NULL COMMENT '寄件状态',
                                  `audit_status` int DEFAULT NULL COMMENT '审核状态',
                                  `express_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '物流单号',
                                  `cost` json DEFAULT (json_object()) COMMENT '实际费用',
                                  `belong_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否归属费用',
                                  `pay_status` int DEFAULT NULL COMMENT '付款状态',
                                  `submit_time` datetime DEFAULT NULL COMMENT '提交时间',
                                  `send_time` datetime DEFAULT NULL COMMENT '寄件时间',
                                  `cost_time` datetime DEFAULT NULL COMMENT '费用写入时间',
                                  `done_time` datetime DEFAULT NULL COMMENT '完成时间',
                                  `pay_time` datetime DEFAULT NULL COMMENT '付款时间',
                                  `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                  `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除  0：有效 1：删除',
                                  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=420 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='寄件表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ems_send_product`
--

DROP TABLE IF EXISTS `ems_send_product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ems_send_product` (
                                    `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                                    `send_id` bigint DEFAULT NULL COMMENT '主键',
                                    `sort_num` int DEFAULT NULL COMMENT '序号',
                                    `goods_source` int DEFAULT NULL COMMENT '物件来源',
                                    `sku_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '产品编号',
                                    `sku_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '产品名称',
                                    `picture` json NOT NULL DEFAULT (json_object()) COMMENT '图片',
                                    `quantity` int DEFAULT '0' COMMENT '数量',
                                    `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                    `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                    `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除  0：有效 1：删除',
                                    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1418 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='寄件产品表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `exms_event_category`
--

DROP TABLE IF EXISTS `exms_event_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `exms_event_category` (
                                       `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                                       `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '名称',
                                       `is_domestic` tinyint DEFAULT '1' COMMENT '是否国内系列 0：否 1：是',
                                       `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                       `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                       `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                       `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                       `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除  0：有效 1：删除',
                                       PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='展会系列表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `exms_exhibition`
--

DROP TABLE IF EXISTS `exms_exhibition`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `exms_exhibition` (
                                   `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                                   `exms_event_category_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '展会系列名称',
                                   `order_num` int DEFAULT '0' COMMENT '排序',
                                   `exms_event_category_id` bigint unsigned DEFAULT NULL COMMENT '展会系列id',
                                   `code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '编号',
                                   `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '名称',
                                   `expo_status` int DEFAULT NULL COMMENT '项目状态',
                                   `audit_status` int DEFAULT NULL COMMENT '审核状态',
                                   `company_id` bigint DEFAULT NULL COMMENT '主体id',
                                   `company_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '主体名称',
                                   `dept_list` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '承担费用部门id列表',
                                   `apply_date` datetime DEFAULT NULL COMMENT '申请日期',
                                   `apply_user_id` bigint DEFAULT NULL COMMENT '申请人id',
                                   `apply_user_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '申请人姓名',
                                   `apply_dept_id` bigint DEFAULT NULL COMMENT '申请人部门id',
                                   `apply_dept_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '申请人部门名称',
                                   `budget` json DEFAULT (json_object()) COMMENT '项目预算',
                                   `theme` int DEFAULT NULL COMMENT '展会主题',
                                   `stall_theme_list` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '摊位主题',
                                   `country_id` bigint DEFAULT NULL COMMENT '国家id',
                                   `country_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '国家名称',
                                   `city_id` bigint DEFAULT NULL COMMENT '城市id',
                                   `city_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '城市名称',
                                   `plan_start_date` datetime DEFAULT NULL COMMENT '计划开始日期',
                                   `plan_end_date` datetime DEFAULT NULL COMMENT '计划结束日期',
                                   `start_date` datetime DEFAULT NULL COMMENT '实际开始日期',
                                   `end_date` datetime DEFAULT NULL COMMENT '实际结束日期',
                                   `stall_area` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '摊位面积',
                                   `owner_user_id` bigint DEFAULT NULL COMMENT '负责人id',
                                   `owner_user_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '负责人姓名',
                                   `owner_dept_id` bigint DEFAULT NULL COMMENT '负责人部门id',
                                   `owner_dept_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '负责人部门名称',
                                   `remark` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
                                   `done_time` datetime DEFAULT NULL COMMENT '完成时间',
                                   `finish_time` datetime DEFAULT NULL COMMENT '结案时间',
                                   `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                   `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                   `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                   `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                   `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除  0：有效 1：删除',
                                   PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=40 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='展会表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `flw_channel_definition`
--

DROP TABLE IF EXISTS `flw_channel_definition`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `flw_channel_definition` (
                                          `ID_` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                                          `NAME_` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
                                          `VERSION_` int DEFAULT NULL,
                                          `KEY_` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
                                          `CATEGORY_` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
                                          `DEPLOYMENT_ID_` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
                                          `CREATE_TIME_` datetime(3) DEFAULT NULL,
                                          `TENANT_ID_` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
                                          `RESOURCE_NAME_` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
                                          `DESCRIPTION_` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
                                          `TYPE_` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
                                          `IMPLEMENTATION_` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
                                          PRIMARY KEY (`ID_`) USING BTREE,
                                          UNIQUE KEY `ACT_IDX_CHANNEL_DEF_UNIQ` (`KEY_`,`VERSION_`,`TENANT_ID_`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `flw_ev_databasechangelog`
--

DROP TABLE IF EXISTS `flw_ev_databasechangelog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `flw_ev_databasechangelog` (
                                            `ID` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                                            `AUTHOR` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                                            `FILENAME` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                                            `DATEEXECUTED` datetime NOT NULL,
                                            `ORDEREXECUTED` int NOT NULL,
                                            `EXECTYPE` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                                            `MD5SUM` varchar(35) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
                                            `DESCRIPTION` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
                                            `COMMENTS` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
                                            `TAG` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
                                            `LIQUIBASE` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
                                            `CONTEXTS` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
                                            `LABELS` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
                                            `DEPLOYMENT_ID` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `flw_ev_databasechangeloglock`
--

DROP TABLE IF EXISTS `flw_ev_databasechangeloglock`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `flw_ev_databasechangeloglock` (
                                                `ID` int NOT NULL,
                                                `LOCKED` bit(1) NOT NULL,
                                                `LOCKGRANTED` datetime DEFAULT NULL,
                                                `LOCKEDBY` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
                                                PRIMARY KEY (`ID`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `flw_event_definition`
--

DROP TABLE IF EXISTS `flw_event_definition`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `flw_event_definition` (
                                        `ID_` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                                        `NAME_` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
                                        `VERSION_` int DEFAULT NULL,
                                        `KEY_` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
                                        `CATEGORY_` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
                                        `DEPLOYMENT_ID_` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
                                        `TENANT_ID_` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
                                        `RESOURCE_NAME_` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
                                        `DESCRIPTION_` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
                                        PRIMARY KEY (`ID_`) USING BTREE,
                                        UNIQUE KEY `ACT_IDX_EVENT_DEF_UNIQ` (`KEY_`,`VERSION_`,`TENANT_ID_`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `flw_event_deployment`
--

DROP TABLE IF EXISTS `flw_event_deployment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `flw_event_deployment` (
                                        `ID_` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                                        `NAME_` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
                                        `CATEGORY_` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
                                        `DEPLOY_TIME_` datetime(3) DEFAULT NULL,
                                        `TENANT_ID_` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
                                        `PARENT_DEPLOYMENT_ID_` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
                                        PRIMARY KEY (`ID_`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `flw_event_resource`
--

DROP TABLE IF EXISTS `flw_event_resource`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `flw_event_resource` (
                                      `ID_` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                                      `NAME_` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
                                      `DEPLOYMENT_ID_` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
                                      `RESOURCE_BYTES_` longblob,
                                      PRIMARY KEY (`ID_`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `flw_ru_batch`
--

DROP TABLE IF EXISTS `flw_ru_batch`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `flw_ru_batch` (
                                `ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
                                `REV_` int DEFAULT NULL,
                                `TYPE_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
                                `SEARCH_KEY_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                `SEARCH_KEY2_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                `CREATE_TIME_` datetime(3) NOT NULL,
                                `COMPLETE_TIME_` datetime(3) DEFAULT NULL,
                                `STATUS_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                `BATCH_DOC_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                `TENANT_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT '',
                                PRIMARY KEY (`ID_`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `flw_ru_batch_part`
--

DROP TABLE IF EXISTS `flw_ru_batch_part`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `flw_ru_batch_part` (
                                     `ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
                                     `REV_` int DEFAULT NULL,
                                     `BATCH_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                     `TYPE_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
                                     `SCOPE_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                     `SUB_SCOPE_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                     `SCOPE_TYPE_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                     `SEARCH_KEY_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                     `SEARCH_KEY2_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                     `CREATE_TIME_` datetime(3) NOT NULL,
                                     `COMPLETE_TIME_` datetime(3) DEFAULT NULL,
                                     `STATUS_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                     `RESULT_DOC_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
                                     `TENANT_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT '',
                                     PRIMARY KEY (`ID_`) USING BTREE,
                                     KEY `FLW_IDX_BATCH_PART` (`BATCH_ID_`) USING BTREE,
                                     CONSTRAINT `FLW_FK_BATCH_PART_PARENT` FOREIGN KEY (`BATCH_ID_`) REFERENCES `flw_ru_batch` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `flyway_schema_history`
--

DROP TABLE IF EXISTS `flyway_schema_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `flyway_schema_history` (
                                         `installed_rank` int NOT NULL,
                                         `version` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                                         `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                         `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                         `script` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                         `checksum` int DEFAULT NULL,
                                         `installed_by` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                         `installed_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                         `execution_time` int NOT NULL,
                                         `success` tinyint(1) NOT NULL,
                                         PRIMARY KEY (`installed_rank`),
                                         KEY `flyway_schema_history_s_idx` (`success`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `flyway_schema_history_async`
--

DROP TABLE IF EXISTS `flyway_schema_history_async`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `flyway_schema_history_async` (
                                               `installed_rank` int NOT NULL,
                                               `version` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                                               `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                               `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                               `script` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                               `checksum` int DEFAULT NULL,
                                               `installed_by` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                               `installed_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                               `execution_time` int NOT NULL,
                                               `success` tinyint(1) NOT NULL,
                                               PRIMARY KEY (`installed_rank`),
                                               KEY `flyway_schema_history_async_s_idx` (`success`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `fms_bank_registration`
--

DROP TABLE IF EXISTS `fms_bank_registration`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fms_bank_registration` (
                                         `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                         `cust_list` json NOT NULL DEFAULT (json_array()) COMMENT '客户列表',
                                         `manager_list` json NOT NULL DEFAULT (json_array()) COMMENT '业务员',
                                         `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态',
                                         `claimed_amount` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '已认领金额',
                                         `code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '编号',
                                         `registered_by` json NOT NULL DEFAULT (json_object()) COMMENT '登记人',
                                         `registration_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '登记日期',
                                         `company_id` bigint DEFAULT NULL COMMENT '入账单位id',
                                         `company_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '入账单位名称',
                                         `company_title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '公司抬头',
                                         `bank_posting_date` datetime DEFAULT NULL COMMENT '银行入账日期',
                                         `bank` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '银行',
                                         `bank_account` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '银行账号',
                                         `bank_address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '开户行地址',
                                         `bank_poc` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '开户行联系人',
                                         `bank_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '银行行号',
                                         `currency` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '入账币别',
                                         `amount` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '入账金额',
                                         `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '备注',
                                         `cust_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '客户编号',
                                         `cust_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '客户名称',
                                         `manager` json NOT NULL DEFAULT (json_object()) COMMENT '业务员',
                                         `claim_manager` json NOT NULL DEFAULT (json_object()) COMMENT '认领业务员',
                                         `claim_status` tinyint NOT NULL DEFAULT '0' COMMENT '认领状态',
                                         `claim_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '认领日期',
                                         `link_sale_contract_code` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '关联外销合同号',
                                         `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                         `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                         `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                         `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除',
                                         `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                         PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=400 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='银行登记表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `fms_cust_claim_item`
--

DROP TABLE IF EXISTS `fms_cust_claim_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fms_cust_claim_item` (
                                       `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                       `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '库存采购合同号',
                                       `sale_type` tinyint DEFAULT NULL COMMENT '销售类型',
                                       `invoice_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '出运发票号',
                                       `finance_amount` json NOT NULL DEFAULT (json_object()) COMMENT '财务费用',
                                       `receipt_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '收款单编号',
                                       `other_fee_type` int DEFAULT NULL COMMENT '其他收费类型 1:证书费  2:模具费  3:样品费  4:快递费 5:验货费 ',
                                       `type` int DEFAULT NULL COMMENT '认领类型 0:回款认领 1：其他收费',
                                       `source` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '来源',
                                       `item_id` bigint DEFAULT NULL COMMENT '发票号',
                                       `registration_id` bigint DEFAULT NULL COMMENT '发票号',
                                       `contract_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '订单合同号',
                                       `cust_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '客户编号',
                                       `cust_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '客户名称',
                                       `currency` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '订单币别',
                                       `settlement_id` bigint DEFAULT NULL COMMENT '收款方式主键',
                                       `settlement_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '收款方式名称',
                                       `source_type` tinyint DEFAULT NULL COMMENT '来源',
                                       `receivable_amount` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '应收金额',
                                       `received_amount` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '已收金额',
                                       `claimed_amount` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '本次入账币种认领金额',
                                       `contract_amount` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '订单币种认领金额',
                                       `difference_amount` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '差异总金额',
                                       `completed_flag` tinyint NOT NULL DEFAULT '0' COMMENT '收款完成标识',
                                       `difference_reason` json DEFAULT NULL COMMENT '认领差异',
                                       `claim_person` json NOT NULL DEFAULT (json_object()) COMMENT '认领员工',
                                       `claim_date` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '认领日期',
                                       `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                       `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                       `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                       `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除',
                                       `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                       PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=185 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='客户认领明细表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `fms_payee`
--

DROP TABLE IF EXISTS `fms_payee`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fms_payee` (
                             `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                             `payee_type` tinyint DEFAULT NULL COMMENT '收款对象类型',
                             `payee_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '编号',
                             `payee_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '名称',
                             `manager` json NOT NULL DEFAULT (json_object()) COMMENT '负责员工',
                             `claim_total_amount` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '认领总金额',
                             `currency` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '币种',
                             `registration_id` bigint DEFAULT NULL COMMENT '登记主键',
                             `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                             `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                             `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                             `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除',
                             `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                             PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=137 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='收款对象表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `fms_payment`
--

DROP TABLE IF EXISTS `fms_payment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fms_payment` (
                               `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                               `payment_bank_account` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '付款银行账户',
                               `payment_bank` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '付款银行',
                               `cancel_reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '作废原因',
                               `cancel_time` datetime DEFAULT NULL COMMENT '作废时间',
                               `cancel_user` json NOT NULL DEFAULT (json_object()) COMMENT '作废人',
                               `acceptance_days` tinyint DEFAULT NULL COMMENT '承兑天数',
                               `annex` json NOT NULL DEFAULT (json_array()) COMMENT '附件',
                               `paid_amount` json NOT NULL DEFAULT (json_object()) COMMENT '已付金额',
                               `link_code_list` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '订单链路编号',
                               `target_bank_poc` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '对方账号',
                               `target_bank_account` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '对方银行',
                               `target_bank` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '对方账户',
                               `payment_apply` json NOT NULL DEFAULT (json_object()) COMMENT '付款申请信息',
                               `payment_method` tinyint DEFAULT NULL COMMENT '支付方式',
                               `apply_payment_date` datetime DEFAULT NULL COMMENT '申请付款日',
                               `apply_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '申请单编号',
                               `code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '支付编码',
                               `print_flag` tinyint NOT NULL DEFAULT '0' COMMENT '打印状态',
                               `print_times` int NOT NULL DEFAULT '0' COMMENT '打印次数',
                               `audit_status` tinyint DEFAULT NULL COMMENT '审核状态',
                               `company_id` bigint DEFAULT NULL COMMENT '内部法人单位',
                               `bank` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '开户行',
                               `bank_account` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '银行账号',
                               `bank_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '开户行地址',
                               `bank_poc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '开户行联系人',
                               `bank_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '银行行号',
                               `status` tinyint NOT NULL DEFAULT '0' COMMENT '支付状态',
                               `amount` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '支付金额',
                               `date` datetime DEFAULT NULL COMMENT '支付日期',
                               `cashier` json DEFAULT NULL COMMENT '出纳员',
                               `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '备注',
                               `apply_amount` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '申请支付金额',
                               `business_type` tinyint NOT NULL DEFAULT '0' COMMENT '业务类型',
                               `business_code` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '业务编号',
                               `business_subject_type` tinyint NOT NULL DEFAULT '0' COMMENT '支付对象类型',
                               `business_subject_code` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '支付对象编号',
                               `applyer` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '申请人',
                               `approver` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '最终审批人',
                               `approval_time` datetime DEFAULT NULL COMMENT '审批时间',
                               `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                               `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                               `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                               `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除  0：有效 1：删除',
                               PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1582 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='财务付款表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `fms_receipt`
--

DROP TABLE IF EXISTS `fms_receipt`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fms_receipt` (
                               `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                               `cust_claim_id` bigint DEFAULT NULL COMMENT '回款认领ID',
                               `code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '申请单号',
                               `print_flag` tinyint NOT NULL DEFAULT '0' COMMENT '打印状态',
                               `print_times` int NOT NULL DEFAULT '0' COMMENT '打印次数',
                               `audit_status` tinyint DEFAULT NULL COMMENT '审核状态',
                               `company_id` bigint DEFAULT NULL COMMENT '内部法人单位',
                               `bank` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '开户行',
                               `bank_address` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '开户行地址',
                               `bank_account` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '银行账号',
                               `bank_poc` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '开户行联系人',
                               `bank_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '银行行号',
                               `amount` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '收款金额',
                               `rate` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '实时汇率',
                               `receipt_time` datetime DEFAULT NULL COMMENT '收款时间',
                               `remark` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '收款备注',
                               `receipt_user` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT (json_object()) COMMENT '收款人',
                               `approver` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '最终审批人',
                               `approval_time` datetime DEFAULT NULL COMMENT '审批时间',
                               `business_type` tinyint NOT NULL DEFAULT '0' COMMENT '业务类型',
                               `business_code` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '业务编号',
                               `business_subject_type` tinyint NOT NULL DEFAULT '0' COMMENT '支付对象类型',
                               `business_subject_code` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '支付对象编号',
                               `status` tinyint NOT NULL DEFAULT '0' COMMENT '确认状态',
                               `receipt_type` tinyint DEFAULT NULL COMMENT '收款方式',
                               `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                               `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                               `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                               `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除  0：有效 1：删除',
                               PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=145 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='财务收款单';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `home_invoice_holder`
--

DROP TABLE IF EXISTS `home_invoice_holder`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `home_invoice_holder` (
                                       `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                                       `invoice_date` datetime DEFAULT NULL COMMENT '发票日期',
                                       `dict_subject_id` tinyint DEFAULT NULL COMMENT '科目类别主键',
                                       `invoice_type` tinyint DEFAULT NULL COMMENT '发票类型',
                                       `invoice_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '发票号',
                                       `description` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '费用说明',
                                       `reimb_type` int unsigned DEFAULT NULL COMMENT '报销类型',
                                       `invoice_amount` decimal(19,6) DEFAULT NULL COMMENT '发票金额',
                                       `reimb_amount` decimal(19,6) DEFAULT NULL COMMENT '报销金额',
                                       `reimb_item` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '报销事项',
                                       `invoice` json NOT NULL DEFAULT (json_object()) COMMENT '报销凭证',
                                       `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态',
                                       `input_user` json NOT NULL DEFAULT (json_object()) COMMENT '录入人',
                                       `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                       `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                       `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                       `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                       `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除  0：有效 1：删除',
                                       PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=470 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='发票夹';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `infra_api_access_log`
--

DROP TABLE IF EXISTS `infra_api_access_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `infra_api_access_log` (
                                        `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志主键',
                                        `trace_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '链路追踪编号',
                                        `user_id` bigint NOT NULL DEFAULT '0' COMMENT '用户编号',
                                        `user_type` tinyint NOT NULL DEFAULT '0' COMMENT '用户类型',
                                        `application_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '应用名',
                                        `request_method` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '请求方法名',
                                        `request_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '请求地址',
                                        `request_params` varchar(8000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '请求参数',
                                        `user_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户 IP',
                                        `user_agent` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '浏览器 UA',
                                        `begin_time` datetime NOT NULL COMMENT '开始请求时间',
                                        `end_time` datetime NOT NULL COMMENT '结束请求时间',
                                        `duration` int NOT NULL COMMENT '执行时长',
                                        `result_code` int NOT NULL DEFAULT '0' COMMENT '结果码',
                                        `result_msg` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '结果提示',
                                        `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
                                        `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                        `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
                                        `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                        `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
                                        `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
                                        PRIMARY KEY (`id`) USING BTREE,
                                        KEY `idx_create_time` (`create_time`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=35832 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='API 访问日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `infra_api_error_log`
--

DROP TABLE IF EXISTS `infra_api_error_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `infra_api_error_log` (
                                       `id` int NOT NULL AUTO_INCREMENT COMMENT '编号',
                                       `trace_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '链路追踪编号\n     *\n     * 一般来说，通过链路追踪编号，可以将访问日志，错误日志，链路追踪日志，logger 打印日志等，结合在一起，从而进行排错。',
                                       `user_id` int NOT NULL DEFAULT '0' COMMENT '用户编号',
                                       `user_type` tinyint NOT NULL DEFAULT '0' COMMENT '用户类型',
                                       `application_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '应用名\n     *\n     * 目前读取 spring.application.name',
                                       `request_method` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '请求方法名',
                                       `request_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '请求地址',
                                       `request_params` varchar(8000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '请求参数',
                                       `user_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户 IP',
                                       `user_agent` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '浏览器 UA',
                                       `exception_time` datetime NOT NULL COMMENT '异常发生时间',
                                       `exception_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '异常名\n     *\n     * {@link Throwable#getClass()} 的类全名',
                                       `exception_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '异常导致的消息\n     *\n     * {@link cn.iocoder.common.framework.util.ExceptionUtil#getMessage(Throwable)}',
                                       `exception_root_cause_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '异常导致的根消息\n     *\n     * {@link cn.iocoder.common.framework.util.ExceptionUtil#getRootCauseMessage(Throwable)}',
                                       `exception_stack_trace` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '异常的栈轨迹\n     *\n     * {@link cn.iocoder.common.framework.util.ExceptionUtil#getServiceException(Exception)}',
                                       `exception_class_name` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '异常发生的类全名\n     *\n     * {@link StackTraceElement#getClassName()}',
                                       `exception_file_name` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '异常发生的类文件\n     *\n     * {@link StackTraceElement#getFileName()}',
                                       `exception_method_name` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '异常发生的方法名\n     *\n     * {@link StackTraceElement#getMethodName()}',
                                       `exception_line_number` int NOT NULL COMMENT '异常发生的方法所在行\n     *\n     * {@link StackTraceElement#getLineNumber()}',
                                       `process_status` tinyint NOT NULL COMMENT '处理状态',
                                       `process_time` datetime DEFAULT NULL COMMENT '处理时间',
                                       `process_user_id` int DEFAULT '0' COMMENT '处理用户编号',
                                       `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
                                       `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                       `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
                                       `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                       `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
                                       `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
                                       PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=17544 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统异常日志';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `infra_codegen_column`
--

DROP TABLE IF EXISTS `infra_codegen_column`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `infra_codegen_column` (
                                        `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
                                        `table_id` bigint NOT NULL COMMENT '表编号',
                                        `column_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '字段名',
                                        `data_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '字段类型',
                                        `column_comment` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '字段描述',
                                        `nullable` bit(1) NOT NULL COMMENT '是否允许为空',
                                        `primary_key` bit(1) NOT NULL COMMENT '是否主键',
                                        `auto_increment` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '是否自增',
                                        `ordinal_position` int NOT NULL COMMENT '排序',
                                        `java_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Java 属性类型',
                                        `java_field` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Java 属性名',
                                        `dict_type` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '字典类型',
                                        `example` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '数据示例',
                                        `create_operation` bit(1) NOT NULL COMMENT '是否为 Create 创建操作的字段',
                                        `update_operation` bit(1) NOT NULL COMMENT '是否为 Update 更新操作的字段',
                                        `list_operation` bit(1) NOT NULL COMMENT '是否为 List 查询操作的字段',
                                        `list_operation_condition` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '=' COMMENT 'List 查询操作的条件类型',
                                        `list_operation_result` bit(1) NOT NULL COMMENT '是否为 List 查询操作的返回字段',
                                        `require_name` bit(1) NOT NULL COMMENT 'resp是否需要返回名称',
                                        `java_name_field` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT 'resp返回名称字段',
                                        `name_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT 'resp返回名称类型',
                                        `convert_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT 'excel转换类型',
                                        `handler_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT 'typeHandler类',
                                        `html_layout` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '页面布局',
                                        `html_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '显示类型',
                                        `required_flag` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否必填字段',
                                        `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
                                        `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                        `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
                                        `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                        `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
                                        PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2000 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='代码生成表字段定义';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `infra_codegen_table`
--

DROP TABLE IF EXISTS `infra_codegen_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `infra_codegen_table` (
                                       `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
                                       `data_source_config_id` bigint NOT NULL COMMENT '数据源配置的编号',
                                       `scene` tinyint NOT NULL DEFAULT '1' COMMENT '生成场景',
                                       `table_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '表名称',
                                       `table_comment` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '表描述',
                                       `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
                                       `module_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模块名',
                                       `business_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '业务名',
                                       `class_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '类名称',
                                       `class_comment` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '类描述',
                                       `author` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '作者',
                                       `template_type` tinyint NOT NULL DEFAULT '1' COMMENT '模板类型',
                                       `front_type` tinyint NOT NULL COMMENT '前端类型',
                                       `parent_menu_id` bigint DEFAULT NULL COMMENT '父菜单编号',
                                       `master_table_id` bigint DEFAULT NULL COMMENT '主表的编号',
                                       `sub_join_column_id` bigint DEFAULT NULL COMMENT '子表关联主表的字段编号',
                                       `sub_join_many` bit(1) DEFAULT NULL COMMENT '主表与子表是否一对多',
                                       `tree_parent_column_id` bigint DEFAULT NULL COMMENT '树表的父字段编号',
                                       `tree_name_column_id` bigint DEFAULT NULL COMMENT '树表的名字字段编号',
                                       `audit_flag` bit(1) DEFAULT NULL COMMENT '是否需要审核流程',
                                       `code_flag` bit(1) DEFAULT NULL COMMENT '是否有编号',
                                       `annex_flag` bit(1) DEFAULT NULL COMMENT '是否需要附件',
                                       `picture_flag` bit(1) DEFAULT NULL COMMENT '是否需要图片',
                                       `sn_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '编号类型',
                                       `code_prefix` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '编号前缀',
                                       `process_key` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '流程标识',
                                       `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
                                       `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                       `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
                                       `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                       `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
                                       PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=155 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='代码生成表定义';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `infra_config`
--

DROP TABLE IF EXISTS `infra_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `infra_config` (
                                `id` int NOT NULL AUTO_INCREMENT COMMENT '参数主键',
                                `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '参数分组',
                                `type` tinyint NOT NULL COMMENT '参数类型',
                                `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '参数名称',
                                `config_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '参数键名',
                                `value` varchar(5000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '参数键值',
                                `visible` bit(1) NOT NULL COMMENT '是否可见',
                                `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
                                `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
                                `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
                                `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
                                PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='参数配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `infra_data_source_config`
--

DROP TABLE IF EXISTS `infra_data_source_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `infra_data_source_config` (
                                            `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键编号',
                                            `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '参数名称',
                                            `url` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '数据源连接',
                                            `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
                                            `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '密码',
                                            `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
                                            `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                            `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
                                            `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                            `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
                                            PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='数据源配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `infra_file`
--

DROP TABLE IF EXISTS `infra_file`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `infra_file` (
                              `id` bigint NOT NULL AUTO_INCREMENT COMMENT '文件编号',
                              `old_name` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '初始文件名',
                              `config_id` bigint DEFAULT NULL COMMENT '配置编号',
                              `name` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '文件名',
                              `path` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件路径',
                              `file_url` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件 URL',
                              `file_type` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件类型',
                              `file_size` int NOT NULL COMMENT '文件大小',
                              `business_type` tinyint DEFAULT NULL COMMENT '业务类型',
                              `business_id` bigint DEFAULT NULL COMMENT '业务id',
                              `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
                              `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
                              `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                              `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
                              PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=90387 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `infra_file_config`
--

DROP TABLE IF EXISTS `infra_file_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `infra_file_config` (
                                     `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
                                     `name` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '配置名',
                                     `storage` tinyint NOT NULL COMMENT '存储器',
                                     `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
                                     `master` bit(1) NOT NULL COMMENT '是否为主配置',
                                     `config` varchar(4096) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '存储配置',
                                     `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
                                     `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                     `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
                                     `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                     `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
                                     PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `infra_file_content`
--

DROP TABLE IF EXISTS `infra_file_content`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `infra_file_content` (
                                      `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
                                      `config_id` bigint NOT NULL COMMENT '配置编号',
                                      `path` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件路径',
                                      `content` mediumblob NOT NULL COMMENT '文件内容',
                                      `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
                                      `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                      `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
                                      `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                      `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
                                      PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=221 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `infra_job`
--

DROP TABLE IF EXISTS `infra_job`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `infra_job` (
                             `id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务编号',
                             `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '任务名称',
                             `status` tinyint NOT NULL COMMENT '任务状态',
                             `handler_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '处理器的名字',
                             `handler_param` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '处理器的参数',
                             `cron_expression` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'CRON 表达式',
                             `retry_count` int NOT NULL DEFAULT '0' COMMENT '重试次数',
                             `retry_interval` int NOT NULL DEFAULT '0' COMMENT '重试间隔',
                             `monitor_timeout` int NOT NULL DEFAULT '0' COMMENT '监控超时时间',
                             `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
                             `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                             `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
                             `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                             `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
                             PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='定时任务表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `infra_job_log`
--

DROP TABLE IF EXISTS `infra_job_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `infra_job_log` (
                                 `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志编号',
                                 `job_id` bigint NOT NULL COMMENT '任务编号',
                                 `handler_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '处理器的名字',
                                 `handler_param` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '处理器的参数',
                                 `execute_index` tinyint NOT NULL DEFAULT '1' COMMENT '第几次执行',
                                 `begin_time` datetime NOT NULL COMMENT '开始执行时间',
                                 `end_time` datetime DEFAULT NULL COMMENT '结束执行时间',
                                 `duration` int DEFAULT NULL COMMENT '执行时长',
                                 `status` tinyint NOT NULL COMMENT '任务状态',
                                 `result` varchar(4000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '结果数据',
                                 `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
                                 `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                 `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
                                 `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                 `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
                                 PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=432 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='定时任务日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `infra_version`
--

DROP TABLE IF EXISTS `infra_version`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `infra_version` (
                                 `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                                 `front_ver` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '前端版本',
                                 `server_ver` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '后端版本',
                                 `publish_ver` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '发布版本',
                                 `publish_name` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '发布更新明细',
                                 `front_desc` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '前端更新明细',
                                 `server_desc` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '后台更新明细',
                                 `publish_desc` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '发布更新明细',
                                 `enabled` int NOT NULL DEFAULT '1' COMMENT '是否显示',
                                 `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                 `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                 `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                 `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                 `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除  0：有效 1：删除',
                                 PRIMARY KEY (`id`) USING BTREE,
                                 KEY `enabled` (`enabled`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='版本记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `member_address`
--

DROP TABLE IF EXISTS `member_address`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `member_address` (
                                  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '收件地址编号',
                                  `user_id` bigint NOT NULL COMMENT '用户编号',
                                  `name` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '收件人名称',
                                  `mobile` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '手机号',
                                  `area_id` bigint NOT NULL COMMENT '地区编码',
                                  `detail_address` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '收件详细地址',
                                  `default_status` bit(1) NOT NULL COMMENT '是否默认',
                                  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
                                  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
                                  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
                                  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
                                  PRIMARY KEY (`id`) USING BTREE,
                                  KEY `idx_userId` (`user_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='用户收件地址';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mms_manufacture`
--

DROP TABLE IF EXISTS `mms_manufacture`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mms_manufacture` (
                                   `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                                   `sale_contract_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '销售合同号',
                                   `code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '编号',
                                   `input_user_id` bigint DEFAULT NULL COMMENT '录入人id',
                                   `input_user_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '录入人姓名',
                                   `input_time` datetime DEFAULT NULL COMMENT '录入时间',
                                   `stock_id` bigint DEFAULT NULL COMMENT '仓库id',
                                   `stock_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '仓库名称',
                                   `company_id` bigint DEFAULT NULL COMMENT '主体id',
                                   `company_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '主体名称',
                                   `cust_id` bigint DEFAULT NULL COMMENT '客户id',
                                   `cust_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '客户编号',
                                   `cust_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '客户名称',
                                   `manufacture_status` tinyint DEFAULT '0' COMMENT '加工单状态',
                                   `auto_flag` tinyint DEFAULT '0' COMMENT '是否自动生成',
                                   `done_time` datetime DEFAULT NULL COMMENT '完成加工时间',
                                   `finish_time` datetime DEFAULT NULL COMMENT '结案时间',
                                   `finish_desc` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '结案原因',
                                   `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                   `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                   `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                   `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                   `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除  0：有效 1：删除',
                                   PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='加工单表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mms_manufacture_sku`
--

DROP TABLE IF EXISTS `mms_manufacture_sku`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mms_manufacture_sku` (
                                       `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                                       `manufacture_id` bigint DEFAULT NULL COMMENT '加工单id',
                                       `sku_id` bigint DEFAULT NULL COMMENT '产品id',
                                       `sku_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '产品编号',
                                       `csku_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '客户产品编号',
                                       `sku_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '产品名称',
                                       `quantity` int DEFAULT NULL COMMENT '产品数量',
                                       `main_picture` json DEFAULT (json_object()) COMMENT '产品图片',
                                       `sms_contract_id` bigint DEFAULT NULL COMMENT '销售合同id',
                                       `sms_contract_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '销售合同编号',
                                       `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                       `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                       `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                       `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                       `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除  0：有效 1：删除',
                                       PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='加工单产品表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mms_manufacture_sku_item`
--

DROP TABLE IF EXISTS `mms_manufacture_sku_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mms_manufacture_sku_item` (
                                            `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                                            `stock_list` json NOT NULL DEFAULT (json_array()) COMMENT '库存信息',
                                            `manufacture_sku_id` bigint DEFAULT NULL COMMENT '加工单产品id',
                                            `manufacture_id` bigint DEFAULT NULL COMMENT '加工单id',
                                            `sku_id` bigint DEFAULT NULL COMMENT '产品id',
                                            `sku_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '产品编号',
                                            `csku_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '客户产品编号',
                                            `sku_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '产品名称',
                                            `quantity` int DEFAULT NULL COMMENT '产品数量',
                                            `ratio` int DEFAULT NULL COMMENT '配比',
                                            `main_picture` json DEFAULT (json_object()) COMMENT '产品图片',
                                            `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                            `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                            `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                            `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                            `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除  0：有效 1：删除',
                                            PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='加工单子产品表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `oa_apply`
--

DROP TABLE IF EXISTS `oa_apply`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `oa_apply` (
                            `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
                            `apply_expense_times` int NOT NULL DEFAULT '0' COMMENT '申请报销次数',
                            `travel_type` tinyint NOT NULL DEFAULT '0' COMMENT '出差类型',
                            `applyer` json NOT NULL DEFAULT (json_object()) COMMENT '申请人',
                            `actual_user` json NOT NULL DEFAULT (json_object()) COMMENT '实际产生费用人员',
                            `company_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '归属公司名称',
                            `company_id` bigint DEFAULT NULL COMMENT '归属公司ID',
                            `intended_objectives` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '拟达成目标',
                            `code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '单据编号',
                            `wecom_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '企微申请单id',
                            `apply_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '申请时间',
                            `purpose` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '事由',
                            `dest` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '出差地点',
                            `start_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '开始时间',
                            `end_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '结束时间',
                            `duration` int DEFAULT NULL COMMENT '出差时长(秒)',
                            `transportation_type` int DEFAULT NULL COMMENT '交通工具',
                            `entertain_entourage` json DEFAULT NULL COMMENT '招待陪同人员',
                            `entertain_level` int DEFAULT NULL COMMENT '招待对象等级',
                            `entertain_num` int DEFAULT NULL COMMENT '招待人数',
                            `entertain_time` datetime DEFAULT NULL COMMENT '招待日期',
                            `entertain_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '招待对象名称',
                            `entertain_type` int DEFAULT NULL COMMENT '招待对象类型',
                            `general_expense` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '一般费用名称',
                            `remarks` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '备注',
                            `is_apply_expense` int DEFAULT NULL COMMENT '是否申请报销',
                            `apply_type` int DEFAULT NULL COMMENT '申请单类型',
                            `amount` json NOT NULL DEFAULT (json_object()) COMMENT '申请金额',
                            `amount_desc` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '金额说明',
                            `audit_status` tinyint DEFAULT NULL COMMENT '审核状态',
                            `annex` json NOT NULL DEFAULT (json_array()) COMMENT '附件',
                            `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                            `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                            `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                            `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除  0：有效 1：删除',
                            PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=667 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='OA申请单';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `oa_budget_app`
--

DROP TABLE IF EXISTS `oa_budget_app`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `oa_budget_app` (
                                 `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '编号',
                                 `description` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '预算简介',
                                 `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '预算名称',
                                 `budget_dept_id` bigint DEFAULT NULL COMMENT '预算部门',
                                 `amount` decimal(19,6) DEFAULT NULL COMMENT '预算金额',
                                 `audit_status` tinyint NOT NULL COMMENT '审核状态',
                                 `start_date` datetime DEFAULT NULL COMMENT '开始日期',
                                 `end_date` datetime DEFAULT NULL COMMENT '结束日期',
                                 `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                 `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                 `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                 `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                 `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除  0：有效 1：删除',
                                 PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='预算申请单';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `oa_dict_subject`
--

DROP TABLE IF EXISTS `oa_dict_subject`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `oa_dict_subject` (
                                   `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                                   `show_fee_flag` tinyint DEFAULT NULL COMMENT '是否在费用实际展示',
                                   `show_desc_flag` tinyint DEFAULT NULL COMMENT '是否在描述展示',
                                   `fee_desc` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '费用描述',
                                   `fee_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '费用名称',
                                   `system_dict_type_list` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '字典类型列表',
                                   `subject_id` bigint DEFAULT NULL COMMENT '科目id',
                                   `subject_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '科目名称',
                                   `subject_description` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '科目描述',
                                   `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                   `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                   `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                   `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                   `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除  0：有效 1：删除',
                                   PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=79 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='类别配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `oa_fee_share`
--

DROP TABLE IF EXISTS `oa_fee_share`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `oa_fee_share` (
                                `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                                `project_id` bigint DEFAULT NULL COMMENT '项目id',
                                `exhibition_id` bigint DEFAULT NULL COMMENT '展会id',
                                `brand_type` tinyint DEFAULT NULL COMMENT '品牌类型',
                                `pre_collection_flag` tinyint NOT NULL DEFAULT '0' COMMENT '预归集标记',
                                `share_user` json NOT NULL DEFAULT (json_object()) COMMENT '归属用户',
                                `order_type` tinyint DEFAULT NULL COMMENT '订单类型',
                                `input_user` json NOT NULL DEFAULT (json_object()) COMMENT '录入人',
                                `amount` json NOT NULL DEFAULT (json_object()) COMMENT '费用',
                                `source_status` tinyint NOT NULL DEFAULT '0' COMMENT '来源单据状态',
                                `company_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '主体名称',
                                `company_id` bigint DEFAULT NULL COMMENT '主体主键',
                                `payment_status` tinyint NOT NULL DEFAULT '0' COMMENT '支付状态',
                                `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态',
                                `business_id` bigint DEFAULT NULL COMMENT '来源单主键',
                                `code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '编号',
                                `process_instance_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '流程实例的编号',
                                `audit_status` tinyint DEFAULT NULL COMMENT '归属状态',
                                `business_type` int DEFAULT NULL COMMENT '来源类型',
                                `business_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '来源单id',
                                `dept_id` bigint DEFAULT NULL COMMENT '业务部门id',
                                `dept_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '业务部门名称',
                                `fee_share_type` int DEFAULT NULL COMMENT '费用归属类别',
                                `relation_type` int DEFAULT NULL COMMENT '相关方类别',
                                `desc_id` bigint DEFAULT NULL COMMENT '具体名称id',
                                `desc_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '具体名称',
                                `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除  0：有效 1：删除',
                                PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=8022 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='费用归集表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `oa_fee_share_desc`
--

DROP TABLE IF EXISTS `oa_fee_share_desc`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `oa_fee_share_desc` (
                                     `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                                     `fee_share_type` int DEFAULT NULL COMMENT '费用归集类型',
                                     `sort_num` int DEFAULT NULL COMMENT '排序',
                                     `relation_type` int DEFAULT NULL COMMENT '相关方类别',
                                     `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '名称',
                                     `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                     `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                     `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                     `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                     `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除  0：有效 1：删除',
                                     PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=75 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='费用归集具体名称表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `oa_fee_share_item`
--

DROP TABLE IF EXISTS `oa_fee_share_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `oa_fee_share_item` (
                                     `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                                     `desc_id` int DEFAULT NULL COMMENT '费用标签',
                                     `amount` json NOT NULL DEFAULT (json_object()) COMMENT '金额',
                                     `share_fee_id` bigint DEFAULT NULL COMMENT '费用归属id',
                                     `business_subject_type` int DEFAULT NULL COMMENT '费用归属对象类型',
                                     `business_subject_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '费用归属对象编号',
                                     `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                     `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                     `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                     `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                     `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除  0：有效 1：删除',
                                     PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6627 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='费用归集明细表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `oa_loan_app`
--

DROP TABLE IF EXISTS `oa_loan_app`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `oa_loan_app` (
                               `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
                               `exchange_rate` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '创建时汇率',
                               `vender_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '供应商名称',
                               `vender_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '供应商编号',
                               `vender_id` bigint DEFAULT NULL COMMENT '供应商id',
                               `loan_source` int NOT NULL DEFAULT '0' COMMENT '借款类型',
                               `purchase_contract_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '采购合同编号',
                               `annex` json DEFAULT NULL COMMENT '附件',
                               `loan_status` int DEFAULT NULL COMMENT '借款状态',
                               `deadline_time` datetime DEFAULT NULL COMMENT '截止时间',
                               `code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '单据编号',
                               `print_flag` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '打印状态',
                               `print_times` int unsigned NOT NULL DEFAULT '0' COMMENT '打印次数',
                               `audit_status` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '审核状态',
                               `purpose` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '借款事由',
                               `company_id` bigint DEFAULT NULL COMMENT '内部法人单位id',
                               `applyer` json DEFAULT NULL COMMENT '申请人',
                               `amount` json DEFAULT NULL COMMENT '借款金额',
                               `loan_date` datetime DEFAULT NULL COMMENT '借款申请日期',
                               `loan_type` tinyint unsigned NOT NULL COMMENT '借款方式',
                               `bank` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '开户行',
                               `bank_address` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '开户行地址',
                               `bank_account` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '银行账号',
                               `bank_poc` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '开户行联系人',
                               `bank_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '银行行号',
                               `payment_status` tinyint unsigned DEFAULT NULL COMMENT '支付状态',
                               `payment_amount` json DEFAULT NULL COMMENT '支付金额',
                               `payment_date` datetime DEFAULT NULL COMMENT '支付日期',
                               `repay_status` tinyint unsigned DEFAULT NULL COMMENT '还款状态',
                               `repay_amount` json DEFAULT NULL COMMENT '已还金额',
                               `repay_date` datetime DEFAULT NULL COMMENT '实际还款日期',
                               `outstanding_amount` json DEFAULT NULL COMMENT '剩余未还款金额',
                               `cashier` json DEFAULT NULL COMMENT '出纳员',
                               `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                               `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                               `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                               `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除  0：有效 1：删除',
                               PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=70 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='借款申请单';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `oa_payment_app`
--

DROP TABLE IF EXISTS `oa_payment_app`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `oa_payment_app` (
                                  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                                  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '备注',
                                  `business_subject_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '业务名称',
                                  `print_date` datetime DEFAULT NULL COMMENT '打印日期 ',
                                  `pay_amount` json NOT NULL DEFAULT (json_object()) COMMENT '支付金额',
                                  `invoice_flag` tinyint NOT NULL DEFAULT '0' COMMENT '发票标识',
                                  `total_invoice_amount` json NOT NULL DEFAULT (json_object()) COMMENT '累计发票金额',
                                  `total_payment_amount` json NOT NULL DEFAULT (json_object()) COMMENT '累计支付金额',
                                  `annex` json NOT NULL DEFAULT (json_array()) COMMENT '发票附件',
                                  `invoice_amount` json NOT NULL DEFAULT (json_object()) COMMENT '发票金额',
                                  `financial_subject_id` bigint DEFAULT NULL COMMENT '科目主键',
                                  `dict_subject_id` bigint DEFAULT NULL COMMENT '类别配置主键',
                                  `link_flag` tinyint DEFAULT NULL COMMENT '关联标识',
                                  `payment_app_list` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '预付申请单编号列表',
                                  `invoice` json NOT NULL DEFAULT (json_array()) COMMENT '发票',
                                  `invoice_amount_list` json NOT NULL DEFAULT (json_array()) COMMENT '发票金额',
                                  `prepaid_flag` tinyint DEFAULT NULL COMMENT '是否预付',
                                  `fee_share_flag` int NOT NULL DEFAULT '1' COMMENT '是否费用归属标记',
                                  `auxiliary_type` int DEFAULT NULL COMMENT '费用归属类型',
                                  `relation_code` varchar(10000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '关联编号',
                                  `relation_type` int DEFAULT NULL COMMENT '关联id',
                                  `code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '申请单号',
                                  `print_flag` tinyint NOT NULL DEFAULT '0' COMMENT '打印状态',
                                  `print_times` int NOT NULL DEFAULT '0' COMMENT '打印次数',
                                  `audit_status` tinyint DEFAULT NULL COMMENT '审核状态',
                                  `reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '事由',
                                  `company_id` bigint DEFAULT NULL COMMENT '内部法人单位',
                                  `business_subject_type` tinyint NOT NULL DEFAULT '0' COMMENT '支付对象',
                                  `business_subject_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '业务编号',
                                  `applyer` json DEFAULT NULL COMMENT '申请人',
                                  `amount` json DEFAULT NULL COMMENT '支付金额',
                                  `bank` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '开户行',
                                  `bank_address` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '开户行地址',
                                  `bank_account` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '银行账号',
                                  `bank_poc` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '开户行联系人',
                                  `bank_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '银行行号',
                                  `cashier` json DEFAULT NULL COMMENT '出纳员',
                                  `payment_amount` json DEFAULT NULL COMMENT '已支付金额',
                                  `payment_status` tinyint NOT NULL DEFAULT '0' COMMENT '支付状态',
                                  `payment_date` datetime DEFAULT NULL COMMENT '支付日期',
                                  `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                  `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除  0：有效 1：删除',
                                  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=553 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='公对公支付申请表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `oa_reception_expense_app`
--

DROP TABLE IF EXISTS `oa_reception_expense_app`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `oa_reception_expense_app` (
                                            `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                            `sp_no` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '编号',
                                            `applyer_id` bigint DEFAULT NULL COMMENT '申请人编号',
                                            `expense_date` datetime DEFAULT NULL COMMENT '日期',
                                            `cust_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '客户名称',
                                            `companions` json NOT NULL DEFAULT (json_array()) COMMENT '我司陪同人员',
                                            `entertain_num` int DEFAULT NULL COMMENT '招待人数',
                                            `order_no` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '费用归属订单',
                                            `cust_level` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '客户等级',
                                            `entertain_effect` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '本次招待成效',
                                            `relate_app_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '关联申请单编号',
                                            `children` json NOT NULL DEFAULT (json_array()) COMMENT '明细',
                                            `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                            `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                            `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                            `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除',
                                            `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                            PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='招待费申请表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `oa_reimb`
--

DROP TABLE IF EXISTS `oa_reimb`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `oa_reimb` (
                            `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '编号',
                            `user_flag` tinyint NOT NULL DEFAULT '1' COMMENT '实际操作人',
                            `cancel_reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '作废原因',
                            `cancel_time` datetime DEFAULT NULL COMMENT '作废时间',
                            `cancel_user` json NOT NULL DEFAULT (json_object()) COMMENT '作废人',
                            `approve_user` json NOT NULL DEFAULT (json_object()) COMMENT '当前审批人',
                            `process_instance_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '流程实例id',
                            `apply_id_list` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '费用申请id列表',
                            `annex` json NOT NULL DEFAULT (json_array()) COMMENT '附件',
                            `print_date` datetime DEFAULT NULL COMMENT '打印日期 ',
                            `draft_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否草稿',
                            `expense_use_to_formal_id` bigint DEFAULT NULL COMMENT '费用用途主键',
                            `loan_app_list` json NOT NULL DEFAULT (json_array()) COMMENT '借款申请列表',
                            `dict_subject_id` bigint DEFAULT NULL COMMENT '类别配置主键',
                            `currency` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '币种',
                            `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '备注',
                            `financial_subject_id` bigint DEFAULT NULL COMMENT '科目主键',
                            `expense_use_to_occur` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '费用用途(发生)',
                            `expense_use_to_formal` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '费用用途(正式)',
                            `code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '表单编码',
                            `print_flag` tinyint NOT NULL DEFAULT '0' COMMENT '打印状态',
                            `print_times` int NOT NULL DEFAULT '0' COMMENT '打印次数',
                            `audit_status` tinyint DEFAULT NULL COMMENT '审核状态',
                            `reimb_type` tinyint DEFAULT NULL COMMENT '报销类型',
                            `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '费用说明',
                            `company_id` bigint DEFAULT NULL COMMENT '报销主体',
                            `reimb_user` json NOT NULL DEFAULT (json_object()) COMMENT '报销人',
                            `actual_user` json NOT NULL DEFAULT (json_object()) COMMENT '实际使用人',
                            `auxiliary_type` tinyint DEFAULT NULL COMMENT '费用归属类型',
                            `contract_list` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '合同列表',
                            `amount_list` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '报销金额列表',
                            `invoice_amount_list` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '发票总金额列表',
                            `total_amount_list` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '本次报销金额列表',
                            `reimb_detail_list` json DEFAULT NULL COMMENT '报销明细',
                            `repay_flag` tinyint DEFAULT NULL COMMENT '是否还款',
                            `repay_amount_list` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '借款单还款金额列表',
                            `payment_amount` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '已支付金额',
                            `payment_status` tinyint DEFAULT NULL COMMENT '支付状态',
                            `payment_date` datetime DEFAULT NULL COMMENT '最后支付日期',
                            `cashier` json DEFAULT NULL COMMENT '出纳员',
                            `invoice` json NOT NULL DEFAULT (json_array()) COMMENT '来源单据状态',
                            `bank` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '开户行',
                            `bank_address` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '开户行地址',
                            `bank_account` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '银行账号',
                            `bank_poc` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '开户行联系人',
                            `bank_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '银行行号',
                            `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                            `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                            `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                            `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除  0：有效 1：删除',
                            PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=478 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='报销表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `oa_reimb_repay_detail`
--

DROP TABLE IF EXISTS `oa_reimb_repay_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `oa_reimb_repay_detail` (
                                         `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '编号',
                                         `repay_source_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '还款单号',
                                         `repay_user` json NOT NULL DEFAULT (json_object()) COMMENT '还款人',
                                         `repay_source_type` tinyint DEFAULT NULL COMMENT '还款来源类型',
                                         `reimb_id` bigint DEFAULT NULL COMMENT '报销单id',
                                         `loan_id` bigint DEFAULT NULL COMMENT '借款单id',
                                         `repay_status` tinyint DEFAULT NULL COMMENT '还款状态',
                                         `repay_amount` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '还款金额',
                                         `audit_status` tinyint DEFAULT NULL COMMENT '审核状态',
                                         `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                         `repay_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '还款时间',
                                         `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                         `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                         `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                         `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除  0：有效 1：删除',
                                         PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=173 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='报销还款详情';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `oa_reimb_share`
--

DROP TABLE IF EXISTS `oa_reimb_share`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `oa_reimb_share` (
                                  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                                  `reimb_id` bigint NOT NULL COMMENT '报销单编号',
                                  `auxiliary_type` tinyint DEFAULT NULL COMMENT '费用归属类型',
                                  `auxiliary_id` bigint NOT NULL DEFAULT '0' COMMENT '费用归属对象id',
                                  `ratio` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '分摊比例',
                                  `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                  `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除  0：有效 1：删除',
                                  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='报销分摊表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `oa_repay_app`
--

DROP TABLE IF EXISTS `oa_repay_app`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `oa_repay_app` (
                                `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                                `annex` json NOT NULL DEFAULT (json_array()) COMMENT '附件',
                                `loan_app_id` bigint DEFAULT NULL COMMENT '借款申请单id',
                                `code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '申请单号',
                                `print_flag` tinyint NOT NULL DEFAULT '0' COMMENT '打印状态',
                                `repay_type` tinyint NOT NULL COMMENT '还款状态',
                                `print_times` int NOT NULL DEFAULT '0' COMMENT '打印次数',
                                `audit_status` tinyint NOT NULL COMMENT '审核状态',
                                `amount` json DEFAULT NULL COMMENT '还款金额',
                                `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '备注',
                                `repay_status` tinyint unsigned DEFAULT NULL COMMENT '还款状态',
                                `bank` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '银行',
                                `bank_account` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '银行账号',
                                `bank_address` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '开户行地址',
                                `bank_poc` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '开户行联系人',
                                `bank_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '银行行号',
                                `repay_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '还款时间',
                                `repayer` json NOT NULL DEFAULT (json_object()) COMMENT '还款人',
                                `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除  0：有效 1：删除',
                                PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='还款申请表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `oa_subject`
--

DROP TABLE IF EXISTS `oa_subject`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `oa_subject` (
                              `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                              `code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '编号',
                              `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '名称',
                              `description` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '描述',
                              `layer` int unsigned DEFAULT NULL COMMENT '层次',
                              `nature` tinyint DEFAULT NULL COMMENT '科目性质',
                              `type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '科目类型',
                              `auxiliary_accounting` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '辅助核算',
                              `accounting_number` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '核算编号',
                              `is_foreign_currency_accounting` tinyint DEFAULT NULL COMMENT '是否外币核算',
                              `currency` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '币种',
                              `is_final_exchange` tinyint DEFAULT NULL COMMENT '是否期末调汇',
                              `is_bank` tinyint DEFAULT NULL COMMENT '是否银行科目',
                              `is_cash` tinyint DEFAULT NULL COMMENT '是否现金科目',
                              `is_cash_bank` tinyint DEFAULT NULL COMMENT '是否现金银行',
                              `parent_id` bigint NOT NULL DEFAULT '0' COMMENT '父级科目id',
                              `parent_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '父级科目名称',
                              `input_date` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '录入日期',
                              `is_cash_flow_related` tinyint DEFAULT NULL COMMENT '是否现金流量相关',
                              `is_last` tinyint DEFAULT NULL COMMENT '是否末级',
                              `cash_flow_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '现金流量编号',
                              `cash_flow_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '现金流量名称',
                              `bank_account` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '银行账户',
                              `bank_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '银行账号',
                              `balance_direction` tinyint DEFAULT NULL COMMENT '余额方向',
                              `balance` bigint unsigned DEFAULT NULL COMMENT '科目余额',
                              `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                              `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                              `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                              `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除  0：有效 1：删除',
                              PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=40 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='科目表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `oa_travel_app`
--

DROP TABLE IF EXISTS `oa_travel_app`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `oa_travel_app` (
                                 `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '编号',
                                 `intended_objectives` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '拟达成目标',
                                 `code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '单据编号',
                                 `wecom_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '企微申请单id',
                                 `applyer_id` bigint DEFAULT NULL COMMENT '申请人',
                                 `apply_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '申请时间',
                                 `purpose` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '出差事由',
                                 `dest` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '出差地点',
                                 `audit_status` tinyint DEFAULT NULL COMMENT '审核状态',
                                 `start_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '开始时间',
                                 `end_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '结束时间',
                                 `duration` int DEFAULT NULL COMMENT '出差时长(秒)',
                                 `transportation_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '交通工具',
                                 `companions` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '同行人员',
                                 `annex` json NOT NULL DEFAULT (json_object()) COMMENT '附件',
                                 `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                 `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                 `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                 `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                 `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除  0：有效 1：删除',
                                 PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='出差申请单';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pjms_project`
--

DROP TABLE IF EXISTS `pjms_project`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pjms_project` (
                                `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                                `cust_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '客户名称',
                                `cust_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '客户编号',
                                `cust_id` bigint DEFAULT NULL COMMENT '客户id',
                                `dept_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '部门名称',
                                `dept_id` bigint DEFAULT NULL COMMENT '部门id',
                                `code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '编号',
                                `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '名称',
                                `project_status` int DEFAULT NULL COMMENT '项目状态',
                                `audit_status` int DEFAULT NULL COMMENT '审核状态',
                                `company_id` bigint DEFAULT NULL COMMENT '主体id',
                                `company_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '主体名称',
                                `develop_type` int DEFAULT NULL COMMENT '研发类型',
                                `plan_start_date` datetime DEFAULT NULL COMMENT '计划开始日期',
                                `plan_end_date` datetime DEFAULT NULL COMMENT '计划结束日期',
                                `start_date` datetime DEFAULT NULL COMMENT '实际开始日期',
                                `end_date` datetime DEFAULT NULL COMMENT '实际结束日期',
                                `owner_user_id` bigint DEFAULT NULL COMMENT '负责人id',
                                `owner_user_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '负责人姓名',
                                `owner_dept_id` bigint DEFAULT NULL COMMENT '负责人部门id',
                                `owner_dept_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '负责人部门名称',
                                `budget` json DEFAULT NULL COMMENT '项目预算',
                                `apply_date` datetime DEFAULT NULL COMMENT '申请日期',
                                `apply_user_id` bigint DEFAULT NULL COMMENT '申请人id',
                                `apply_user_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '申请人姓名',
                                `apply_dept_id` bigint DEFAULT NULL COMMENT '申请人部门id',
                                `apply_dept_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '申请人部门名称',
                                `remark` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
                                `done_time` datetime DEFAULT NULL COMMENT '完成时间',
                                `finish_time` datetime DEFAULT NULL COMMENT '结案时间',
                                `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除  0：有效 1：删除',
                                PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='项目表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pms_brand`
--

DROP TABLE IF EXISTS `pms_brand`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pms_brand` (
                             `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
                             `logo` json NOT NULL DEFAULT (json_object()) COMMENT 'logo',
                             `cust_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '客户名称',
                             `cust_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '客户编号',
                             `cust_id` bigint DEFAULT NULL COMMENT '客户id',
                             `brand_type` int DEFAULT NULL COMMENT '品牌类型',
                             `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '名称',
                             `code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '编号',
                             `name_eng` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '流水号长度',
                             `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '商品说明',
                             `description_eng` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '商品说明英文',
                             `own_brand_flag` tinyint DEFAULT NULL COMMENT '是否自主品牌',
                             `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                             `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                             `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                             `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                             `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除  0：有效 1：删除',
                             PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='品牌表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pms_category`
--

DROP TABLE IF EXISTS `pms_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pms_category` (
                                `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                                `hs_data_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '海关编码编号',
                                `code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '商品分类编号',
                                `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '名称',
                                `code_len` int DEFAULT NULL COMMENT '流水号长度',
                                `hs_code_id` bigint DEFAULT NULL COMMENT '海关编码',
                                `parent_id` bigint unsigned DEFAULT NULL COMMENT '父节点编号',
                                `category_type` tinyint DEFAULT NULL COMMENT '种类',
                                `grade` int DEFAULT NULL COMMENT '级别',
                                `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除  0：有效 1：删除',
                                PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1110 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='产品分类表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pms_hsdata`
--

DROP TABLE IF EXISTS `pms_hsdata`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pms_hsdata` (
                              `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '编号',
                              `ver` int NOT NULL COMMENT '版本号',
                              `code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '编码',
                              `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '商品名称',
                              `unit` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '报关单位',
                              `tax_refund_rate` decimal(19,6) DEFAULT NULL COMMENT '退税率',
                              `rate` decimal(19,6) DEFAULT NULL COMMENT '征税率',
                              `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
                              `chname` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '商品全称',
                              `addrate` decimal(19,6) DEFAULT NULL COMMENT '征收率',
                              `code2` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '第二单位',
                              `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                              `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                              `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                              `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除  0：有效 1：删除',
                              PRIMARY KEY (`id`) USING BTREE,
                              UNIQUE KEY `code` (`code`) USING BTREE,
                              KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=11538 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='海关编码表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pms_hsdata_backup`
--

DROP TABLE IF EXISTS `pms_hsdata_backup`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pms_hsdata_backup` (
                                     `id` bigint unsigned NOT NULL DEFAULT '0' COMMENT '编号',
                                     `ver` int NOT NULL COMMENT '版本号',
                                     `code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '编码',
                                     `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '商品名称',
                                     `unit` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '报关单位',
                                     `tax_refund_rate` decimal(19,6) DEFAULT NULL COMMENT '退税率',
                                     `rate` decimal(19,6) DEFAULT NULL COMMENT '征税率',
                                     `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
                                     `chname` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '商品全称',
                                     `addrate` decimal(19,6) DEFAULT NULL COMMENT '征收率',
                                     `code2` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '第二单位',
                                     `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                     `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                     `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                     `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                     `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除  0：有效 1：删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pms_package_type`
--

DROP TABLE IF EXISTS `pms_package_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pms_package_type` (
                                    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                    `code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '包装方式编号',
                                    `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '包装方式名称',
                                    `name_eng` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '包装方式英文名称',
                                    `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                    `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除',
                                    `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='包装方式';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pms_sku`
--

DROP TABLE IF EXISTS `pms_sku`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pms_sku` (
                           `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                           `belonging_dept_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '归属部门名称',
                           `belonging_dept_id` bigint DEFAULT NULL COMMENT '归属部门ID',
                           `agent_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否代理',
                           `customs_declaration_name_eng` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '报关英文品名',
                           `declaration_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '报关中文品名',
                           `basic_sku_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '基础产品编号',
                           `thumbnail` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '缩略图',
                           `auxiliary_material` int DEFAULT NULL COMMENT '辅料材质',
                           `old_data` json NOT NULL DEFAULT (json_object()) COMMENT '流程实例主键',
                           `process_instance_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '流程实例主键',
                           `ver` int NOT NULL DEFAULT '0' COMMENT '版本号',
                           `effect_range_list` json NOT NULL DEFAULT (json_array()) COMMENT '影响范围',
                           `model_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '流程模型标识',
                           `change_status` tinyint DEFAULT NULL COMMENT '变更状态',
                           `change_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '变更是否删除',
                           `change_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否变更',
                           `commodity_inspection_flag` tinyint DEFAULT NULL COMMENT '是否商检',
                           `audit_status` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '审核状态',
                           `spu_id` bigint unsigned DEFAULT NULL COMMENT 'spuid',
                           `source_id` bigint unsigned DEFAULT NULL COMMENT '来源id',
                           `source_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '来源编号',
                           `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '名称',
                           `name_eng` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '英文名称',
                           `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT 'SKU编号',
                           `barcode` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '条码',
                           `cust_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '客户编号',
                           `csku_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '客户货号',
                           `osku_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '自营产品货号',
                           `onshelf_flag` tinyint unsigned NOT NULL DEFAULT '1' COMMENT '产品状态',
                           `description` varchar(5000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '产品描述',
                           `description_eng` varchar(8000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '产品英文描述',
                           `own_brand_flag` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '是否自主品牌',
                           `advantage_flag` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '是否优势产品',
                           `brand_id` bigint unsigned DEFAULT NULL COMMENT '品牌id',
                           `category_id` bigint unsigned DEFAULT NULL COMMENT '产品分类',
                           `hs_code_id` bigint DEFAULT NULL COMMENT '海关编码',
                           `source_flag` tinyint unsigned DEFAULT NULL COMMENT '产品来源',
                           `sku_type` tinyint unsigned DEFAULT NULL COMMENT '产品类型',
                           `material` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '产品材质',
                           `measure_unit` tinyint unsigned DEFAULT NULL COMMENT '计量单位',
                           `spec_length` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '单品规格长',
                           `spec_width` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '单品规格宽',
                           `spec_height` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '单品规格高',
                           `single_netweight` json NOT NULL DEFAULT (json_object()) COMMENT '单品净重',
                           `single_process_fee` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '单位加工费',
                           `process_remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '加工备注',
                           `price` json NOT NULL DEFAULT (json_object()) COMMENT '销售单价',
                           `company_price` json NOT NULL DEFAULT (json_object()) COMMENT '公司定价',
                           `annex` json DEFAULT NULL COMMENT '附件',
                           `picture` json DEFAULT NULL COMMENT '图片',
                           `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '备注',
                           `cust_pro_flag` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '客户产品标识',
                           `auxiliary_sku_flag` int DEFAULT NULL COMMENT '是否通用辅料',
                           `auto_create_flag` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '自动创建标识',
                           `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                           `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                           `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                           `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                           `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除  0：有效 1：删除',
                           PRIMARY KEY (`id`) USING BTREE,
                           KEY `idx_pms_sku_sku_code` (`code`),
                           KEY `idx_pms_sku_csku_code` (`csku_code`),
                           KEY `idx_pms_sku_source_code` (`source_code`),
                           KEY `idx_pms_sku_source_id` (`source_id`),
                           KEY `change_flag` (`change_flag`),
                           KEY `sku_type` (`sku_type`),
                           KEY `create_time` (`create_time` DESC) USING BTREE,
                           KEY `own_brand_flag` (`own_brand_flag`),
                           KEY `cust_pro_flag` (`cust_pro_flag`),
                           KEY `deleted` (`deleted`) USING BTREE,
                           KEY `osku_code` (`osku_code`) USING BTREE,
                           KEY `idx_sku_create_time` (`create_time` DESC),
                           KEY `idx_sku_query` (`change_flag`,`audit_status`,`sku_type`,`create_time` DESC),
                           KEY `idx_sku_list_query` (`deleted`,`change_flag`,`own_brand_flag`,`cust_pro_flag`,`agent_flag`,`sku_type`,`create_time` DESC),
                           KEY `idx_sku_code` (`code`),
                           KEY `idx_sku_cust_code` (`cust_code`)
) ENGINE=InnoDB AUTO_INCREMENT=57612 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='SKU表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pms_sku_auxiliary`
--

DROP TABLE IF EXISTS `pms_sku_auxiliary`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pms_sku_auxiliary` (
                                     `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                     `enable_flag` int DEFAULT NULL COMMENT '有效标记',
                                     `sku_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '产品编号',
                                     `sku_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '产品名称',
                                     `auxiliary_sku_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '辅料编号',
                                     `auxiliary_sku_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '辅料名称',
                                     `sku_rate` int NOT NULL DEFAULT '1' COMMENT '产品比',
                                     `auxiliary_sku_rate` int NOT NULL DEFAULT '1' COMMENT '辅料比',
                                     `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '描述',
                                     `remark` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '备注',
                                     `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                     `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                     `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                     `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除',
                                     `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                     PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='产品辅料配比表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pms_sku_bom`
--

DROP TABLE IF EXISTS `pms_sku_bom`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pms_sku_bom` (
                               `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                               `sku_id` bigint unsigned DEFAULT NULL COMMENT '子SKU主键',
                               `sku_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '子SKU编号',
                               `sku_ver` int DEFAULT NULL COMMENT 'SKU版本',
                               `qty` int DEFAULT NULL COMMENT '数量',
                               `parent_sku_id` bigint unsigned DEFAULT NULL COMMENT '父SKU编号',
                               `sku_type` tinyint unsigned DEFAULT NULL COMMENT '产品类型',
                               `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                               `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                               `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                               `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除  0：有效 1：删除',
                               PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1468 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='产品SKU BOM表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pms_spu`
--

DROP TABLE IF EXISTS `pms_spu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pms_spu` (
                           `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                           `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '名称',
                           `code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '编号',
                           `barcode` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '产品条码',
                           `spec` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '产品规格',
                           `name_eng` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '英文名称',
                           `brand_id` bigint unsigned DEFAULT NULL COMMENT '品牌编号',
                           `category_id` bigint unsigned DEFAULT NULL COMMENT '商品分类',
                           `unit_type` tinyint DEFAULT NULL COMMENT '计量单位',
                           `onshelf_flag` tinyint DEFAULT NULL COMMENT '商品状态',
                           `audit_status` tinyint NOT NULL COMMENT '审核状态',
                           `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '商品说明',
                           `description_eng` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '商品说明英文',
                           `hscode_id` bigint unsigned DEFAULT NULL COMMENT '海关编码',
                           `hsunit_var` int unsigned DEFAULT NULL COMMENT '海关编码版本号',
                           `own_brand_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否自主品牌',
                           `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                           `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                           `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                           `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                           `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除  0：有效 1：删除',
                           PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='商品表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `qms_quality_inspection`
--

DROP TABLE IF EXISTS `qms_quality_inspection`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qms_quality_inspection` (
                                          `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                                          `purchase_user` json NOT NULL DEFAULT (json_object()) COMMENT '采购员',
                                          `sales` json NOT NULL DEFAULT (json_object()) COMMENT '业务员',
                                          `inspection_node` tinyint DEFAULT NULL COMMENT '验货节点',
                                          `result_annex` json NOT NULL DEFAULT (json_array()) COMMENT '结果附件',
                                          `picture` json NOT NULL DEFAULT (json_array()) COMMENT '图片',
                                          `link_code_list` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '订单链路编号',
                                          `code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '单号',
                                          `audit_status` tinyint DEFAULT NULL COMMENT '审核状态',
                                          `quality_inspection_status` tinyint DEFAULT NULL COMMENT '验货单状态:1：待审批，2：待确认，3：待验货，4：验货不通过，5：已完成（验货已通过或让步放行）',
                                          `reinspection_flag` tinyint DEFAULT NULL COMMENT '是否重验单:0：否，1：是',
                                          `source_id` bigint DEFAULT NULL COMMENT '关联验货单id',
                                          `source_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '关联验货单单号',
                                          `source_type` tinyint DEFAULT NULL COMMENT '单据来源:1：采购合同',
                                          `purchase_contract_id` bigint DEFAULT NULL COMMENT '采购合同主键',
                                          `purchase_contract_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '采购合同编号',
                                          `warehouse_id` bigint DEFAULT NULL COMMENT '仓库主键',
                                          `warehouse_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '仓库名称',
                                          `inspection_type` tinyint DEFAULT NULL COMMENT '验货方式:1： 泛太陪验（工厂）\n2：泛太陪验（公司内） \n3：泛太自验（工厂） \n4：泛太自验（公司内） \n5：客户自检 \n6：客户指定第三方 \n7：远程验货',
                                          `vender_id` bigint DEFAULT NULL COMMENT '供应商id',
                                          `vender_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '供应商编号',
                                          `vender_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '供应商名称',
                                          `expect_inspection_time` datetime DEFAULT NULL COMMENT '期望验货时间',
                                          `apply_inspector_id` bigint DEFAULT NULL COMMENT '申请验货人',
                                          `apply_inspector_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '申请验货人姓名',
                                          `apply_inspector_dept_id` bigint DEFAULT NULL COMMENT '申请验货人部门主键',
                                          `apply_inspector_dept_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '申请验货人部门名称',
                                          `plan_inspection_time` datetime DEFAULT NULL COMMENT '计划验货时间',
                                          `factory_contacter` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '工厂联系人',
                                          `factory_telephone` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '联系电话',
                                          `inspection_address` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '验货地址',
                                          `inspector_id` bigint DEFAULT NULL COMMENT '验货人',
                                          `inspector_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '验货人姓名',
                                          `inspector_dept_id` bigint DEFAULT NULL COMMENT '验货人部门主键',
                                          `inspector_dept_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '验货人部门名称',
                                          `inspection_time` datetime DEFAULT NULL COMMENT '实际验货时间',
                                          `special_attention_notice` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '特别注意事项',
                                          `amount` json DEFAULT NULL COMMENT '验货金额',
                                          `allocation_type` int DEFAULT NULL COMMENT '分摊方式：1-按金额分摊 2-按数量分摊',
                                          `annex` json DEFAULT NULL COMMENT '附件',
                                          `guarantee_letter` json DEFAULT NULL COMMENT '工厂保函',
                                          `accept_desc` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '接受说明',
                                          `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
                                          `company_id` bigint DEFAULT NULL COMMENT '归属公司主键',
                                          `company_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '归属公司名称',
                                          `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                          `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                          `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                          `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                          `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除  0-有效 1-删除',
                                          PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1036 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='验货单';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `qms_quality_inspection_item`
--

DROP TABLE IF EXISTS `qms_quality_inspection_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qms_quality_inspection_item` (
                                               `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                                               `basic_sku_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '基础产品编号',
                                               `main_picture` json NOT NULL DEFAULT (json_object()) COMMENT '主图',
                                               `package_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '包装方式',
                                               `split_box_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否分箱',
                                               `specification_list` json NOT NULL DEFAULT (json_array()) COMMENT '规格',
                                               `purchase_quantity` int NOT NULL DEFAULT '0' COMMENT '采购数量',
                                               `purchase_contract_item_id` bigint DEFAULT NULL COMMENT '采购合同明细主键',
                                               `rework_desc` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '返工说明',
                                               `picture` json NOT NULL DEFAULT (json_array()) COMMENT '图片',
                                               `handle_flag` tinyint DEFAULT '0' COMMENT '处理标识',
                                               `inspection_id` bigint DEFAULT NULL COMMENT '验货单主键',
                                               `sku_id` bigint DEFAULT NULL COMMENT '产品主键',
                                               `sku_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT 'SKU编号',
                                               `sku_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '产品中文名称',
                                               `csku_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '客户货号',
                                               `inspection_status` tinyint DEFAULT NULL COMMENT '验货状态:1：成功，2：失败，3：待定',
                                               `fail_desc` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '失败描述',
                                               `last_fail_desc` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '上次失败描述',
                                               `pending_desc` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '待定描述',
                                               `quantity` int DEFAULT NULL COMMENT '数量',
                                               `amount` json DEFAULT NULL COMMENT '验货金额',
                                               `total_price` json DEFAULT NULL COMMENT '产品总价',
                                               `purchase_contract_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '采购合同编号',
                                               `purchase_user_id` bigint DEFAULT NULL COMMENT '采购员主键',
                                               `purchase_user_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '采购员姓名',
                                               `purchase_user_dept_id` bigint DEFAULT NULL COMMENT '采购员部门主键',
                                               `purchase_user_dept_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '采购员部门名称',
                                               `sale_contract_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '销售合同号',
                                               `sale_user_id` bigint DEFAULT NULL COMMENT '销售员主键',
                                               `sale_user_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '销售员姓名',
                                               `sale_user_dept_id` bigint DEFAULT NULL COMMENT '销售员部门主键',
                                               `sale_user_dept_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '销售员部门名称',
                                               `track_user_id` bigint DEFAULT NULL COMMENT '跟单员主键',
                                               `track_user_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '跟单员姓名',
                                               `track_user_dept_id` bigint DEFAULT NULL COMMENT '跟单员部门主键',
                                               `track_user_dept_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '跟单员部门名称',
                                               `cust_id` bigint DEFAULT NULL COMMENT '客户主键',
                                               `cust_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '客户编码',
                                               `cust_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '客户名称',
                                               `qty_per_outerbox` int DEFAULT NULL COMMENT '外箱装量',
                                               `qty_per_innerbox` int DEFAULT NULL COMMENT '内盒装量',
                                               `box_count` int DEFAULT NULL COMMENT '箱数',
                                               `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
                                               `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                               `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                               `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                               `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                               `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除  0-有效 1-删除',
                                               PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4101 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='验货单-明细';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `qrtz_blob_triggers`
--

DROP TABLE IF EXISTS `qrtz_blob_triggers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_blob_triggers` (
                                      `SCHED_NAME` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                      `TRIGGER_NAME` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                      `TRIGGER_GROUP` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                      `BLOB_DATA` blob,
                                      PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`) USING BTREE,
                                      KEY `SCHED_NAME` (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`) USING BTREE,
                                      CONSTRAINT `qrtz_blob_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `qrtz_triggers` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `qrtz_calendars`
--

DROP TABLE IF EXISTS `qrtz_calendars`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_calendars` (
                                  `SCHED_NAME` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                  `CALENDAR_NAME` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                  `CALENDAR` blob NOT NULL,
                                  PRIMARY KEY (`SCHED_NAME`,`CALENDAR_NAME`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `qrtz_cron_triggers`
--

DROP TABLE IF EXISTS `qrtz_cron_triggers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_cron_triggers` (
                                      `SCHED_NAME` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                      `TRIGGER_NAME` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                      `TRIGGER_GROUP` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                      `CRON_EXPRESSION` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                      `TIME_ZONE_ID` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                      PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`) USING BTREE,
                                      CONSTRAINT `qrtz_cron_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `qrtz_triggers` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `qrtz_fired_triggers`
--

DROP TABLE IF EXISTS `qrtz_fired_triggers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_fired_triggers` (
                                       `SCHED_NAME` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                       `ENTRY_ID` varchar(95) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                       `TRIGGER_NAME` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                       `TRIGGER_GROUP` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                       `INSTANCE_NAME` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                       `FIRED_TIME` bigint NOT NULL,
                                       `SCHED_TIME` bigint NOT NULL,
                                       `PRIORITY` int NOT NULL,
                                       `STATE` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                       `JOB_NAME` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                       `JOB_GROUP` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                       `IS_NONCONCURRENT` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                       `REQUESTS_RECOVERY` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                       PRIMARY KEY (`SCHED_NAME`,`ENTRY_ID`) USING BTREE,
                                       KEY `IDX_QRTZ_FT_TRIG_INST_NAME` (`SCHED_NAME`,`INSTANCE_NAME`) USING BTREE,
                                       KEY `IDX_QRTZ_FT_INST_JOB_REQ_RCVRY` (`SCHED_NAME`,`INSTANCE_NAME`,`REQUESTS_RECOVERY`) USING BTREE,
                                       KEY `IDX_QRTZ_FT_J_G` (`SCHED_NAME`,`JOB_NAME`,`JOB_GROUP`) USING BTREE,
                                       KEY `IDX_QRTZ_FT_JG` (`SCHED_NAME`,`JOB_GROUP`) USING BTREE,
                                       KEY `IDX_QRTZ_FT_T_G` (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`) USING BTREE,
                                       KEY `IDX_QRTZ_FT_TG` (`SCHED_NAME`,`TRIGGER_GROUP`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `qrtz_job_details`
--

DROP TABLE IF EXISTS `qrtz_job_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_job_details` (
                                    `SCHED_NAME` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                    `JOB_NAME` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                    `JOB_GROUP` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                    `DESCRIPTION` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                    `JOB_CLASS_NAME` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                    `IS_DURABLE` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                    `IS_NONCONCURRENT` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                    `IS_UPDATE_DATA` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                    `REQUESTS_RECOVERY` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                    `JOB_DATA` blob,
                                    PRIMARY KEY (`SCHED_NAME`,`JOB_NAME`,`JOB_GROUP`) USING BTREE,
                                    KEY `IDX_QRTZ_J_REQ_RECOVERY` (`SCHED_NAME`,`REQUESTS_RECOVERY`) USING BTREE,
                                    KEY `IDX_QRTZ_J_GRP` (`SCHED_NAME`,`JOB_GROUP`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `qrtz_locks`
--

DROP TABLE IF EXISTS `qrtz_locks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_locks` (
                              `SCHED_NAME` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                              `LOCK_NAME` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                              PRIMARY KEY (`SCHED_NAME`,`LOCK_NAME`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `qrtz_paused_trigger_grps`
--

DROP TABLE IF EXISTS `qrtz_paused_trigger_grps`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_paused_trigger_grps` (
                                            `SCHED_NAME` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                            `TRIGGER_GROUP` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                            PRIMARY KEY (`SCHED_NAME`,`TRIGGER_GROUP`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `qrtz_scheduler_state`
--

DROP TABLE IF EXISTS `qrtz_scheduler_state`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_scheduler_state` (
                                        `SCHED_NAME` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                        `INSTANCE_NAME` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                        `LAST_CHECKIN_TIME` bigint NOT NULL,
                                        `CHECKIN_INTERVAL` bigint NOT NULL,
                                        PRIMARY KEY (`SCHED_NAME`,`INSTANCE_NAME`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `qrtz_simple_triggers`
--

DROP TABLE IF EXISTS `qrtz_simple_triggers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_simple_triggers` (
                                        `SCHED_NAME` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                        `TRIGGER_NAME` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                        `TRIGGER_GROUP` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                        `REPEAT_COUNT` bigint NOT NULL,
                                        `REPEAT_INTERVAL` bigint NOT NULL,
                                        `TIMES_TRIGGERED` bigint NOT NULL,
                                        PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`) USING BTREE,
                                        CONSTRAINT `qrtz_simple_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `qrtz_triggers` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `qrtz_simprop_triggers`
--

DROP TABLE IF EXISTS `qrtz_simprop_triggers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_simprop_triggers` (
                                         `SCHED_NAME` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                         `TRIGGER_NAME` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                         `TRIGGER_GROUP` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                         `STR_PROP_1` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                         `STR_PROP_2` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                         `STR_PROP_3` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                         `INT_PROP_1` int DEFAULT NULL,
                                         `INT_PROP_2` int DEFAULT NULL,
                                         `LONG_PROP_1` bigint DEFAULT NULL,
                                         `LONG_PROP_2` bigint DEFAULT NULL,
                                         `DEC_PROP_1` decimal(13,4) DEFAULT NULL,
                                         `DEC_PROP_2` decimal(13,4) DEFAULT NULL,
                                         `BOOL_PROP_1` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                         `BOOL_PROP_2` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                         PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`) USING BTREE,
                                         CONSTRAINT `qrtz_simprop_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `qrtz_triggers` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `qrtz_triggers`
--

DROP TABLE IF EXISTS `qrtz_triggers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_triggers` (
                                 `SCHED_NAME` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                 `TRIGGER_NAME` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                 `TRIGGER_GROUP` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                 `JOB_NAME` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                 `JOB_GROUP` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                 `DESCRIPTION` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `NEXT_FIRE_TIME` bigint DEFAULT NULL,
                                 `PREV_FIRE_TIME` bigint DEFAULT NULL,
                                 `PRIORITY` int DEFAULT NULL,
                                 `TRIGGER_STATE` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                 `TRIGGER_TYPE` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                 `START_TIME` bigint NOT NULL,
                                 `END_TIME` bigint DEFAULT NULL,
                                 `CALENDAR_NAME` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `MISFIRE_INSTR` smallint DEFAULT NULL,
                                 `JOB_DATA` blob,
                                 PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`) USING BTREE,
                                 KEY `IDX_QRTZ_T_J` (`SCHED_NAME`,`JOB_NAME`,`JOB_GROUP`) USING BTREE,
                                 KEY `IDX_QRTZ_T_JG` (`SCHED_NAME`,`JOB_GROUP`) USING BTREE,
                                 KEY `IDX_QRTZ_T_C` (`SCHED_NAME`,`CALENDAR_NAME`) USING BTREE,
                                 KEY `IDX_QRTZ_T_G` (`SCHED_NAME`,`TRIGGER_GROUP`) USING BTREE,
                                 KEY `IDX_QRTZ_T_STATE` (`SCHED_NAME`,`TRIGGER_STATE`) USING BTREE,
                                 KEY `IDX_QRTZ_T_N_STATE` (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`,`TRIGGER_STATE`) USING BTREE,
                                 KEY `IDX_QRTZ_T_N_G_STATE` (`SCHED_NAME`,`TRIGGER_GROUP`,`TRIGGER_STATE`) USING BTREE,
                                 KEY `IDX_QRTZ_T_NEXT_FIRE_TIME` (`SCHED_NAME`,`NEXT_FIRE_TIME`) USING BTREE,
                                 KEY `IDX_QRTZ_T_NFT_ST` (`SCHED_NAME`,`TRIGGER_STATE`,`NEXT_FIRE_TIME`) USING BTREE,
                                 KEY `IDX_QRTZ_T_NFT_MISFIRE` (`SCHED_NAME`,`MISFIRE_INSTR`,`NEXT_FIRE_TIME`) USING BTREE,
                                 KEY `IDX_QRTZ_T_NFT_ST_MISFIRE` (`SCHED_NAME`,`MISFIRE_INSTR`,`NEXT_FIRE_TIME`,`TRIGGER_STATE`) USING BTREE,
                                 KEY `IDX_QRTZ_T_NFT_ST_MISFIRE_GRP` (`SCHED_NAME`,`MISFIRE_INSTR`,`NEXT_FIRE_TIME`,`TRIGGER_GROUP`,`TRIGGER_STATE`) USING BTREE,
                                 CONSTRAINT `qrtz_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`) REFERENCES `qrtz_job_details` (`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `scm_add_sub_term`
--

DROP TABLE IF EXISTS `scm_add_sub_term`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `scm_add_sub_term` (
                                    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                    `link_contract_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '关联合同号',
                                    `contract_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '合同编号',
                                    `calculation_type` tinyint DEFAULT NULL COMMENT '加/减项',
                                    `fee_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '费用名称',
                                    `amount` json NOT NULL COMMENT '金额',
                                    `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                    `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除',
                                    `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='采购合同加减项';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `scm_concession_release`
--

DROP TABLE IF EXISTS `scm_concession_release`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `scm_concession_release` (
                                          `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                                          `process_instance_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '流程实例id',
                                          `picture` json NOT NULL DEFAULT (json_array()) COMMENT '图片',
                                          `quality_inspection_item_ids` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '验货单明细主键列表',
                                          `quality_inspection_id` bigint DEFAULT NULL COMMENT '验货单主键 ',
                                          `annex_flag` int NOT NULL DEFAULT '0' COMMENT '保函标记',
                                          `annex` json NOT NULL DEFAULT (json_array()) COMMENT '保函',
                                          `audit_status` int DEFAULT NULL COMMENT '审核状态',
                                          `description` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '让步描述',
                                          `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                          `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                          `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                          `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                          `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除  0：有效 1：删除',
                                          PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=94 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='让步放行表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `scm_invoicing_notices`
--

DROP TABLE IF EXISTS `scm_invoicing_notices`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `scm_invoicing_notices` (
                                         `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                                         `registration_date` datetime DEFAULT NULL COMMENT '登票日期',
                                         `process_instance_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '流程实例主键',
                                         `audit_status` tinyint NOT NULL DEFAULT '0' COMMENT '审核状态',
                                         `print_date` datetime DEFAULT NULL COMMENT '打印日期',
                                         `link_code_list` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '订单链路编号',
                                         `source_type` int DEFAULT NULL COMMENT '来源类型',
                                         `ship_invoice_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '出运发票号',
                                         `code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '编号',
                                         `company_id` bigint DEFAULT NULL COMMENT '归属公司主键',
                                         `company_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '归属公司名称',
                                         `input_user` json NOT NULL DEFAULT (json_object()) COMMENT '录入人',
                                         `input_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '录入日期',
                                         `vender_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '供应商编号',
                                         `vender_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '供应商名称',
                                         `manager` json NOT NULL DEFAULT (json_object()) COMMENT '跟单员',
                                         `shipment_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '出运单号',
                                         `invoice_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '发票编号',
                                         `ship_date` datetime DEFAULT NULL COMMENT '出运日期',
                                         `print_flag` tinyint NOT NULL DEFAULT '0' COMMENT '打印状态',
                                         `pur_order_code` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '采购单号',
                                         `status` tinyint NOT NULL COMMENT '状态',
                                         `manually_flag` tinyint unsigned DEFAULT NULL COMMENT '手工开票通知',
                                         `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                         `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                         `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                         `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                         `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除  0：有效 1：删除',
                                         PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=772 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='开票通知表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `scm_invoicing_notices_item`
--

DROP TABLE IF EXISTS `scm_invoicing_notices_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `scm_invoicing_notices_item` (
                                              `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                                              `qty_per_outerbox` int NOT NULL DEFAULT '0' COMMENT '外箱装量',
                                              `split_box_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否分箱',
                                              `specification_list` json NOT NULL DEFAULT (json_array()) COMMENT '规格',
                                              `sku_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '产品名称',
                                              `base_invoice_quantity` int NOT NULL DEFAULT '0' COMMENT '转换前数量',
                                              `basic_sku_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '基础产品编号',
                                              `tax_rate` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '税率',
                                              `purchase_with_tax_price` json NOT NULL DEFAULT (json_object()) COMMENT '采购含税单价',
                                              `shipment_item_id` bigint DEFAULT NULL COMMENT '出运明细明细主键',
                                              `purchase_contract_item_id` bigint DEFAULT NULL COMMENT '采购合同明细主键',
                                              `manager` json NOT NULL DEFAULT (json_object()) COMMENT '跟单员',
                                              `ship_invoice_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '出运发票号',
                                              `source_unique_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '来源编号',
                                              `unique_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '唯一编号',
                                              `invoice_status` tinyint NOT NULL DEFAULT '0' COMMENT '开票状态',
                                              `sale_contract_item_id` bigint DEFAULT NULL COMMENT '销售明细主键',
                                              `invoic_notices_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '开票通知单号',
                                              `purchase_contract_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '采购合同编号',
                                              `purchase_sort_num` int DEFAULT NULL COMMENT '采购序号',
                                              `sku_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT 'SKU编号',
                                              `csku_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '客户货号',
                                              `declaration_quantity` int unsigned DEFAULT '0' COMMENT '报关数量',
                                              `hs_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '海关编码',
                                              `invoic_notices_quantity` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '通知开票数量',
                                              `hs_measure_unit` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '海关计量单位',
                                              `invoic_sku_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '开票品名',
                                              `invoic_price` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '开票单价',
                                              `inveic_registered_status` tinyint unsigned DEFAULT NULL COMMENT '发票登记状态',
                                              `inveic_registered_quantity` int unsigned NOT NULL DEFAULT '0' COMMENT '发票登记数量',
                                              `purchase_total_quantity` int NOT NULL DEFAULT '0' COMMENT '总采购数量',
                                              `purchase_currency` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '采购币种',
                                              `cust_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '客户名称',
                                              `cust_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '客户编号',
                                              `sale_contract_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '销售合同编号',
                                              `manually_flag` tinyint unsigned DEFAULT NULL COMMENT '手工开票通知',
                                              `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                              `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                              `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                              `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                              `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除  0：有效 1：删除',
                                              PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1566 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='开票通知明细表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `scm_payment_apply`
--

DROP TABLE IF EXISTS `scm_payment_apply`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `scm_payment_apply` (
                                     `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                                     `cancel_reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '作废原因',
                                     `cancel_time` datetime DEFAULT NULL COMMENT '作废时间',
                                     `cancel_user` json NOT NULL DEFAULT (json_object()) COMMENT '作废人',
                                     `sub_add_total_amount` json NOT NULL DEFAULT (json_object()) COMMENT '加减项总金额',
                                     `acceptance_days` tinyint DEFAULT NULL COMMENT '承兑天数',
                                     `payment_method` tinyint DEFAULT NULL COMMENT '支付方式',
                                     `annex` json NOT NULL DEFAULT (json_array()) COMMENT '附件',
                                     `payment_mark_type` int DEFAULT NULL COMMENT '付款备注类型',
                                     `purchase_user_list` json NOT NULL DEFAULT (json_array()) COMMENT '采购员',
                                     `link_code_list` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '订单链路编号',
                                     `vender_bank_poc` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '供应商开户行联系人',
                                     `vender_bank_account` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '供应商银行账号',
                                     `vender_bank` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '供应商银行',
                                     `real_payment_amount` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '实际支付金额',
                                     `payment_status` tinyint NOT NULL DEFAULT '0' COMMENT '支付状态',
                                     `payment_user` json DEFAULT NULL COMMENT '付款人',
                                     `payment_date` datetime DEFAULT NULL COMMENT '付款日期',
                                     `code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '编号',
                                     `payment_plan_id` bigint DEFAULT NULL COMMENT '付款计划id',
                                     `company_id` bigint DEFAULT NULL COMMENT '下单主体主键',
                                     `company_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '下单主体',
                                     `applyer` json NOT NULL COMMENT '申请人id',
                                     `apply_date` datetime DEFAULT NULL COMMENT '申请日期',
                                     `print_flag` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '打印状态',
                                     `step` tinyint DEFAULT NULL COMMENT '申请类型',
                                     `apply_total_amount` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '申请总金额',
                                     `goods_total_amount` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '货款总金额',
                                     `apply_payment_date` datetime DEFAULT NULL COMMENT '申请付款日',
                                     `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '申请备注',
                                     `vender_id` bigint DEFAULT NULL COMMENT '应付供应商主键',
                                     `vender_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '应付供应商编码',
                                     `vender_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '应付供应商名称',
                                     `currency` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '应付币种',
                                     `payment_id` bigint DEFAULT NULL COMMENT '付款方式id',
                                     `payment_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '付款方式名称',
                                     `tax_rate` decimal(19,6) DEFAULT '0.000000' COMMENT '税率',
                                     `bank_account` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '银行账号',
                                     `bank` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '开户行',
                                     `process_instance_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '流程实例的编号',
                                     `payment_plan` json DEFAULT NULL COMMENT '流程实例状态',
                                     `apply_payment_plan_list` json DEFAULT NULL COMMENT '付款计划信息',
                                     `applyer_purchase_item_list` json DEFAULT NULL COMMENT '采购明细',
                                     `add_sub_term_list` json DEFAULT NULL COMMENT '加减项',
                                     `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                     `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                     `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                     `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                     `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除  0：有效 1：删除',
                                     `audit_status` tinyint NOT NULL COMMENT '审核状态',
                                     `scm_payment_apply` json DEFAULT NULL,
                                     `purchase_contract_code_list` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                                     PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=812 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='付款申请主表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `scm_payment_apply_item`
--

DROP TABLE IF EXISTS `scm_payment_apply_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `scm_payment_apply_item` (
                                          `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                                          `purchase_contract_item_id` bigint DEFAULT NULL COMMENT '采购合同明细id',
                                          `paid_amount` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '已付金额',
                                          `applied_amount` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '已申请金额',
                                          `apply_amount` bigint unsigned DEFAULT NULL COMMENT '本次请款金额',
                                          `invoice_status` tinyint NOT NULL DEFAULT '0' COMMENT '开票状态',
                                          `step` tinyint NOT NULL DEFAULT '0' COMMENT '付款步骤',
                                          `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                          `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                          `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                          `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                          `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除  0：有效 1：删除',
                                          PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='付款申请子表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `scm_payment_plan`
--

DROP TABLE IF EXISTS `scm_payment_plan`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `scm_payment_plan` (
                                    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                    `control_invoice_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否控制发票',
                                    `annex` json DEFAULT NULL COMMENT '水单',
                                    `payment_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '付款方式名称',
                                    `payment_msg` json NOT NULL DEFAULT (json_array()) COMMENT '付款信息',
                                    `payment_time` datetime DEFAULT NULL COMMENT '付款时间',
                                    `real_payment_ratio` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '实际付款比例',
                                    `contract_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '合同编号',
                                    `step` tinyint DEFAULT NULL COMMENT '步骤',
                                    `payment_method` tinyint DEFAULT NULL COMMENT '支付方式',
                                    `date_type` tinyint DEFAULT NULL COMMENT '起始点',
                                    `start_date` datetime DEFAULT NULL COMMENT '起始日',
                                    `days` int NOT NULL DEFAULT '0' COMMENT '天数',
                                    `expected_receipt_date` datetime DEFAULT NULL COMMENT '预计付款日',
                                    `payment_ratio` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '付款比例',
                                    `receivable_amount` json NOT NULL DEFAULT (json_object()) COMMENT '应付金额',
                                    `received_amount` json NOT NULL DEFAULT (json_object()) COMMENT '实付金额',
                                    `control_purchase_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否控制采购',
                                    `exe_status` tinyint NOT NULL DEFAULT '0' COMMENT '状态',
                                    `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                    `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除',
                                    `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                    `applied_amount` json NOT NULL DEFAULT (json_object()) COMMENT '已申请金额',
                                    `receivable_amount_value` decimal(10,2) GENERATED ALWAYS AS (json_unquote(json_extract(`receivable_amount`,_utf8mb4'$.amount'))) STORED COMMENT '应付金额',
                                    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=12209 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='采购合同付款计划';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `scm_purchase_auxiliary_allocation`
--

DROP TABLE IF EXISTS `scm_purchase_auxiliary_allocation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `scm_purchase_auxiliary_allocation` (
                                                     `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                                     `purchase_contract_id` bigint DEFAULT NULL COMMENT '采购合同主键',
                                                     `purchase_contract_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '采购合同编号',
                                                     `purchase_contract_item_id` bigint DEFAULT NULL COMMENT '采购合同明细主键',
                                                     `sku_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '产品编号',
                                                     `sku_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '产品名称',
                                                     `csku_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '客户货号',
                                                     `auxiliary_purchase_contract_id` bigint DEFAULT NULL COMMENT '辅料采购合同主键',
                                                     `auxiliary_purchase_contract_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '辅料采购合同编号',
                                                     `auxiliary_purchase_contract_item_id` bigint DEFAULT NULL COMMENT '辅料采购合同明细主键',
                                                     `auxiliary_sku_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '辅料产品编号',
                                                     `auxiliary_sku_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '辅料产品名称',
                                                     `quantity` int NOT NULL DEFAULT '0' COMMENT '采购数量',
                                                     `allocation_amount` json NOT NULL DEFAULT (json_object()) COMMENT '分摊金额',
                                                     `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                                     `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                                     `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                                     `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除',
                                                     `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                                     PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=598 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='采购合同辅料分摊表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `scm_purchase_contract`
--

DROP TABLE IF EXISTS `scm_purchase_contract`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `scm_purchase_contract` (
                                         `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                         `init_delivery_date` datetime DEFAULT NULL COMMENT '交货日期',
                                         `tax_rate` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '税率',
                                         `total_amount_rmb` json NOT NULL DEFAULT (json_object()) COMMENT '人名币总额',
                                         `sync_code` bigint NOT NULL DEFAULT '0' COMMENT '同步标记',
                                         `process_instance_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '流程实例id',
                                         `auxiliary_purchase_user` json NOT NULL DEFAULT (json_array()) COMMENT '辅料属于的采购员',
                                         `auxiliary_sales` json NOT NULL DEFAULT (json_array()) COMMENT '辅料属于的销售员',
                                         `auxiliary_manager` json NOT NULL DEFAULT (json_array()) COMMENT '辅料属于的跟单员',
                                         `auxiliary_payment_flag` int NOT NULL DEFAULT '0' COMMENT '下推对公付款标记',
                                         `restocking_deadline` int NOT NULL DEFAULT '0' COMMENT '乙方补货时限',
                                         `minimum_base_quantity` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '最低备品比例',
                                         `delivery_address` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '送货地址',
                                         `gen_contract_unique_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '内部生成编号',
                                         `sample_count` int DEFAULT NULL COMMENT '样品套数',
                                         `box_with_color` int DEFAULT NULL COMMENT '箱带颜色',
                                         `sales` json NOT NULL DEFAULT (json_object()) COMMENT '销售人员',
                                         `vender_poc` json NOT NULL DEFAULT (json_object()) COMMENT '供应商联系人',
                                         `currency` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '币种',
                                         `vender_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '供应商名称',
                                         `repeat_flag` int NOT NULL DEFAULT '0' COMMENT '翻单标记',
                                         `manager` json NOT NULL DEFAULT (json_object()) COMMENT '跟单员',
                                         `check_status` tinyint NOT NULL DEFAULT '0' COMMENT '验货状态',
                                         `plan_source_type` int DEFAULT NULL COMMENT '采购计划来源方式',
                                         `place_order_time` datetime DEFAULT NULL COMMENT '下单时间',
                                         `place_order_flag` int NOT NULL DEFAULT '0' COMMENT '下单标记',
                                         `sign_back_annex_list` json NOT NULL DEFAULT (json_array()) COMMENT '回签附件',
                                         `design_draft_list` json NOT NULL DEFAULT (json_array()) COMMENT '出片文件',
                                         `link_code_list` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '订单链路编号',
                                         `produce_completed` tinyint NOT NULL DEFAULT '0' COMMENT '生产完成标识:0-否 1-是',
                                         `convert_notice_flag` tinyint NOT NULL DEFAULT '1' COMMENT '转通知单状态',
                                         `effect_range_list` json NOT NULL DEFAULT (json_array()) COMMENT '变更影响范围',
                                         `auto_flag` tinyint DEFAULT '0' COMMENT '自动生成标识 0-否 1-是',
                                         `source_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '来源编号',
                                         `change_status` tinyint NOT NULL DEFAULT '1' COMMENT '变更状态',
                                         `sign_back_desc` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '回签描述',
                                         `confirm_flag` tinyint NOT NULL DEFAULT '1' COMMENT '确认状态',
                                         `invoiced_amount` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '已开票金额',
                                         `invoiced_currency` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '登票币种',
                                         `invoiced_quantity` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '已开票数量',
                                         `payment_vender_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '应付供应商名称',
                                         `payment_vender_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '应付供应商编号',
                                         `payment_vender_id` bigint DEFAULT NULL COMMENT '应付供应商主键',
                                         `ver` int DEFAULT NULL COMMENT '版本',
                                         `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '采购合同编号',
                                         `audit_status` tinyint NOT NULL DEFAULT '0' COMMENT '审核状态',
                                         `contract_status` tinyint NOT NULL DEFAULT '0' COMMENT '采购合同状态',
                                         `company_id` bigint DEFAULT NULL COMMENT '采购主体',
                                         `purchase_time` datetime DEFAULT NULL COMMENT '采购时间',
                                         `auxiliary_flag` int NOT NULL DEFAULT '0' COMMENT '是否辅料采购',
                                         `total_amount` json DEFAULT NULL COMMENT '采购总金额',
                                         `total_quantity` int NOT NULL DEFAULT '0' COMMENT '采购总数量',
                                         `print_flag` tinyint NOT NULL DEFAULT '0' COMMENT '打印状态',
                                         `print_times` int NOT NULL DEFAULT '0' COMMENT '打印次数',
                                         `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
                                         `annex` json NOT NULL DEFAULT (json_array()) COMMENT '附件',
                                         `payment_id` bigint DEFAULT NULL COMMENT '付款方式id',
                                         `payment_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '付款方式名称',
                                         `port_id` bigint DEFAULT NULL COMMENT '目的口岸',
                                         `freight` json DEFAULT NULL COMMENT '运费',
                                         `equally_type` tinyint DEFAULT NULL COMMENT '分摊方式',
                                         `other_cost` json DEFAULT NULL COMMENT '其他费用',
                                         `delivery_date` datetime DEFAULT NULL COMMENT '交货日期',
                                         `sign_back_flag` tinyint NOT NULL DEFAULT '0' COMMENT '回签标记',
                                         `tax_type` tinyint DEFAULT NULL COMMENT '发票类型',
                                         `prepay_status` tinyint NOT NULL DEFAULT '0' COMMENT '预付款状态',
                                         `prepay_amount` json DEFAULT NULL COMMENT '预付款金额',
                                         `pay_status` tinyint NOT NULL DEFAULT '0' COMMENT '付款状态',
                                         `payed_amount` json DEFAULT NULL COMMENT '已付款金额',
                                         `invoice_status` tinyint NOT NULL DEFAULT '0' COMMENT '开票状态',
                                         `purchase_user_id` bigint DEFAULT NULL COMMENT '采购员编码',
                                         `purchase_user_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '采购员名称',
                                         `purchase_user_dept_id` bigint DEFAULT NULL COMMENT '采购员部门编码',
                                         `purchase_user_dept_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '采购员部门名称',
                                         `cust_id` bigint DEFAULT NULL COMMENT '客户主键',
                                         `cust_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '客户编码',
                                         `vender_id` bigint DEFAULT NULL COMMENT '供应商主键',
                                         `vender_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '供应商编码',
                                         `stock_id` bigint DEFAULT NULL COMMENT '仓库主键',
                                         `stock_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '仓库编码',
                                         `stock_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '仓库名称',
                                         `purchase_plan_id` bigint DEFAULT NULL COMMENT '采购计划id',
                                         `purchase_plan_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '采购计划编号',
                                         `sale_contract_id` bigint DEFAULT NULL COMMENT '销售合同id',
                                         `sale_contract_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '销售合同编号',
                                         `re_purchase_flag` tinyint(1) DEFAULT '0' COMMENT '重构标记',
                                         `re_purchase_desc` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '重构原因',
                                         `re_purchase_time` datetime DEFAULT NULL COMMENT '重构时间',
                                         `re_purchase_old_flag` tinyint(1) DEFAULT '0' COMMENT '重构旧版本标记',
                                         `deal_time` datetime DEFAULT NULL COMMENT '处理时间',
                                         `finish_time` datetime DEFAULT NULL COMMENT '结案时间',
                                         `done_time` datetime DEFAULT NULL COMMENT '完成时间',
                                         `sign_back_time` datetime DEFAULT NULL COMMENT '回签时间',
                                         `order_time` datetime DEFAULT NULL COMMENT '下单时间',
                                         `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                         `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                         `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                         `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                         `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除',
                                         PRIMARY KEY (`id`) USING BTREE,
                                         KEY `idx_id` (`id`),
                                         KEY `idx_code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=26496 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `scm_purchase_contract_change`
--

DROP TABLE IF EXISTS `scm_purchase_contract_change`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `scm_purchase_contract_change` (
                                                `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                                `tax_rate` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '税率',
                                                `currency` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '币种',
                                                `sales` json NOT NULL DEFAULT (json_object()) COMMENT '业务员',
                                                `sign_back_time` datetime DEFAULT NULL COMMENT '回签日期',
                                                `sample_count` int DEFAULT NULL COMMENT '样品套数',
                                                `box_with_color` int DEFAULT NULL COMMENT '箱带颜色',
                                                `auxiliary_flag` int NOT NULL DEFAULT '0' COMMENT '包材标记',
                                                `design_draft_list` json NOT NULL DEFAULT (json_array()) COMMENT '出片文件',
                                                `manager` json NOT NULL DEFAULT (json_object()) COMMENT '跟单员',
                                                `company_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '采购主体名称',
                                                `source_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '来源编号',
                                                `effect_range_list` json NOT NULL DEFAULT (json_array()) COMMENT '影响范围',
                                                `old_data` json NOT NULL DEFAULT (json_object()) COMMENT '旧数据',
                                                `model_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '流程模型标识',
                                                `purchase_payment_plan_list` json NOT NULL COMMENT '加减项',
                                                `purchase_add_sub_term_list` json NOT NULL COMMENT '付款计划',
                                                `ver` int DEFAULT NULL COMMENT '版本',
                                                `code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '变更单编号',
                                                `contract_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '采购合同编号',
                                                `audit_status` tinyint NOT NULL DEFAULT '0' COMMENT '审核状态',
                                                `contract_status` tinyint NOT NULL DEFAULT '0' COMMENT '采购合同状态',
                                                `total_amount` json DEFAULT NULL COMMENT '采购总金额',
                                                `total_quantity` int DEFAULT NULL COMMENT '采购总数量',
                                                `print_flag` tinyint NOT NULL DEFAULT '0' COMMENT '打印状态',
                                                `print_times` int NOT NULL DEFAULT '0' COMMENT '打印次数',
                                                `prepay_status` tinyint NOT NULL DEFAULT '0' COMMENT '预付款状态',
                                                `prepay_amount` json DEFAULT NULL COMMENT '预付款金额',
                                                `pay_status` tinyint NOT NULL DEFAULT '0' COMMENT '付款状态',
                                                `payed_amount` json DEFAULT NULL COMMENT '已付款金额',
                                                `invoice_status` tinyint NOT NULL DEFAULT '0' COMMENT '开票状态',
                                                `invoiced_amount` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '已开票金额',
                                                `purchase_user_id` bigint DEFAULT NULL COMMENT '采购员编码',
                                                `purchase_user_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '采购员名称',
                                                `purchase_user_dept_id` bigint DEFAULT NULL COMMENT '采购员部门编码',
                                                `purchase_user_dept_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '采购员部门名称',
                                                `cust_id` bigint DEFAULT NULL COMMENT '客户主键',
                                                `vender_id` bigint DEFAULT NULL COMMENT '供应商主键',
                                                `stock_id` bigint DEFAULT NULL COMMENT '仓库主键',
                                                `stock_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '仓库编码',
                                                `vender_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '供应商编码',
                                                `vender_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '供应商名称',
                                                `cust_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '客户编码',
                                                `stock_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '仓库名称',
                                                `purchase_plan_id` bigint DEFAULT NULL COMMENT '采购计划id',
                                                `purchase_plan_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '采购计划编号',
                                                `sale_contract_id` bigint DEFAULT NULL COMMENT '销售合同id',
                                                `sale_contract_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '销售合同编号',
                                                `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
                                                `creator_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建人名称',
                                                `creator` int unsigned DEFAULT NULL COMMENT '创建人id',
                                                `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                                `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                                `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除',
                                                `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                                `annex` json DEFAULT NULL COMMENT '附件',
                                                `company_id` bigint DEFAULT NULL COMMENT '采购主体',
                                                `purchase_time` datetime DEFAULT NULL COMMENT '采购时间',
                                                `payment_id` bigint DEFAULT NULL COMMENT '付款方式id',
                                                `sync_quote_flag` tinyint DEFAULT NULL COMMENT '是否同步供应商',
                                                `free_flag` tinyint DEFAULT NULL COMMENT '是否赠品',
                                                `port_id` bigint DEFAULT NULL COMMENT '目的口岸',
                                                `port_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '目的口岸名称',
                                                `freight` json DEFAULT NULL COMMENT '运费',
                                                `other_cost` json DEFAULT NULL COMMENT '其他费用',
                                                `delivery_date` datetime DEFAULT NULL COMMENT '交货日期',
                                                `planned_arrival_time` datetime DEFAULT NULL COMMENT '计划到料时间',
                                                `signback_flag` tinyint NOT NULL DEFAULT '0' COMMENT '回签',
                                                `item_count_total` int NOT NULL DEFAULT '0' COMMENT '采购产品总数量',
                                                `children` json NOT NULL COMMENT '采购计划明细',
                                                `create_user` json NOT NULL COMMENT '创建人',
                                                `payment_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '付款方式',
                                                `cust_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '客户名称',
                                                `sign_back_flag` tinyint NOT NULL DEFAULT '0' COMMENT '回签',
                                                `tax_type` tinyint DEFAULT NULL COMMENT '发票类型',
                                                `equally_type` tinyint DEFAULT NULL COMMENT '分摊方式',
                                                `process_instance_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '流程实例的编号',
                                                PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=796 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='采购合同变更表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `scm_purchase_contract_copy1`
--

DROP TABLE IF EXISTS `scm_purchase_contract_copy1`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `scm_purchase_contract_copy1` (
                                               `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                               `init_delivery_date` datetime DEFAULT NULL COMMENT '交货日期',
                                               `tax_rate` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '税率',
                                               `total_amount_rmb` json NOT NULL DEFAULT (json_object()) COMMENT '人名币总额',
                                               `sync_code` bigint NOT NULL DEFAULT '0' COMMENT '同步标记',
                                               `process_instance_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '流程实例id',
                                               `auxiliary_purchase_user` json NOT NULL DEFAULT (json_array()) COMMENT '辅料属于的采购员',
                                               `auxiliary_sales` json NOT NULL DEFAULT (json_array()) COMMENT '辅料属于的销售员',
                                               `auxiliary_manager` json NOT NULL DEFAULT (json_array()) COMMENT '辅料属于的跟单员',
                                               `auxiliary_payment_flag` int NOT NULL DEFAULT '0' COMMENT '下推对公付款标记',
                                               `restocking_deadline` int NOT NULL DEFAULT '0' COMMENT '乙方补货时限',
                                               `minimum_base_quantity` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '最低备品比例',
                                               `delivery_address` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '送货地址',
                                               `gen_contract_unique_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '内部生成编号',
                                               `sample_count` int DEFAULT NULL COMMENT '样品套数',
                                               `box_with_color` int DEFAULT NULL COMMENT '箱带颜色',
                                               `sales` json NOT NULL DEFAULT (json_object()) COMMENT '销售人员',
                                               `vender_poc` json NOT NULL DEFAULT (json_object()) COMMENT '供应商联系人',
                                               `currency` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '币种',
                                               `vender_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '供应商名称',
                                               `repeat_flag` int NOT NULL DEFAULT '0' COMMENT '翻单标记',
                                               `manager` json NOT NULL DEFAULT (json_object()) COMMENT '跟单员',
                                               `check_status` tinyint NOT NULL DEFAULT '0' COMMENT '验货状态',
                                               `plan_source_type` int DEFAULT NULL COMMENT '采购计划来源方式',
                                               `place_order_time` datetime DEFAULT NULL COMMENT '下单时间',
                                               `place_order_flag` int NOT NULL DEFAULT '0' COMMENT '下单标记',
                                               `sign_back_annex_list` json NOT NULL DEFAULT (json_array()) COMMENT '回签附件',
                                               `design_draft_list` json NOT NULL DEFAULT (json_array()) COMMENT '出片文件',
                                               `link_code_list` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '订单链路编号',
                                               `produce_completed` tinyint NOT NULL DEFAULT '0' COMMENT '生产完成标识:0-否 1-是',
                                               `convert_notice_flag` tinyint NOT NULL DEFAULT '1' COMMENT '转通知单状态',
                                               `effect_range_list` json NOT NULL DEFAULT (json_array()) COMMENT '变更影响范围',
                                               `auto_flag` tinyint DEFAULT '0' COMMENT '自动生成标识 0-否 1-是',
                                               `source_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '来源编号',
                                               `change_status` tinyint NOT NULL DEFAULT '1' COMMENT '确认状态',
                                               `sign_back_desc` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '回签描述',
                                               `confirm_flag` tinyint NOT NULL DEFAULT '1' COMMENT '确认状态',
                                               `invoiced_amount` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '已开票金额',
                                               `invoiced_currency` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '登票币种',
                                               `invoiced_quantity` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '已开票数量',
                                               `payment_vender_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '应付供应商名称',
                                               `payment_vender_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '应付供应商编号',
                                               `payment_vender_id` bigint DEFAULT NULL COMMENT '应付供应商主键',
                                               `ver` int DEFAULT NULL COMMENT '版本',
                                               `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '采购合同编号',
                                               `audit_status` tinyint NOT NULL DEFAULT '0' COMMENT '审核状态',
                                               `contract_status` tinyint NOT NULL DEFAULT '0' COMMENT '采购合同状态',
                                               `company_id` bigint DEFAULT NULL COMMENT '采购主体',
                                               `purchase_time` datetime DEFAULT NULL COMMENT '采购时间',
                                               `auxiliary_flag` int NOT NULL DEFAULT '0' COMMENT '是否辅料采购',
                                               `total_amount` json DEFAULT NULL COMMENT '采购总金额',
                                               `total_quantity` int NOT NULL DEFAULT '0' COMMENT '采购总数量',
                                               `print_flag` tinyint NOT NULL DEFAULT '0' COMMENT '打印状态',
                                               `print_times` int NOT NULL DEFAULT '0' COMMENT '打印次数',
                                               `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
                                               `annex` json NOT NULL DEFAULT (json_array()) COMMENT '附件',
                                               `payment_id` bigint DEFAULT NULL COMMENT '付款方式id',
                                               `payment_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '付款方式名称',
                                               `port_id` bigint DEFAULT NULL COMMENT '目的口岸',
                                               `freight` json DEFAULT NULL COMMENT '运费',
                                               `equally_type` tinyint DEFAULT NULL COMMENT '分摊方式',
                                               `other_cost` json DEFAULT NULL COMMENT '其他费用',
                                               `delivery_date` datetime DEFAULT NULL COMMENT '交货日期',
                                               `sign_back_flag` tinyint NOT NULL DEFAULT '0' COMMENT '回签标记',
                                               `tax_type` tinyint DEFAULT NULL COMMENT '发票类型',
                                               `prepay_status` tinyint NOT NULL DEFAULT '0' COMMENT '预付款状态',
                                               `prepay_amount` json DEFAULT NULL COMMENT '预付款金额',
                                               `pay_status` tinyint NOT NULL DEFAULT '0' COMMENT '付款状态',
                                               `payed_amount` json DEFAULT NULL COMMENT '已付款金额',
                                               `invoice_status` tinyint NOT NULL DEFAULT '0' COMMENT '开票状态',
                                               `purchase_user_id` bigint DEFAULT NULL COMMENT '采购员编码',
                                               `purchase_user_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '采购员名称',
                                               `purchase_user_dept_id` bigint DEFAULT NULL COMMENT '采购员部门编码',
                                               `purchase_user_dept_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '采购员部门名称',
                                               `cust_id` bigint DEFAULT NULL COMMENT '客户主键',
                                               `cust_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '客户编码',
                                               `vender_id` bigint DEFAULT NULL COMMENT '供应商主键',
                                               `vender_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '供应商编码',
                                               `stock_id` bigint DEFAULT NULL COMMENT '仓库主键',
                                               `stock_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '仓库编码',
                                               `stock_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '仓库名称',
                                               `purchase_plan_id` bigint DEFAULT NULL COMMENT '采购计划id',
                                               `purchase_plan_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '采购计划编号',
                                               `sale_contract_id` bigint DEFAULT NULL COMMENT '销售合同id',
                                               `sale_contract_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '销售合同编号',
                                               `re_purchase_flag` tinyint(1) DEFAULT '0' COMMENT '重构标记',
                                               `re_purchase_desc` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '重构原因',
                                               `re_purchase_time` datetime DEFAULT NULL COMMENT '重构时间',
                                               `re_purchase_old_flag` tinyint(1) DEFAULT '0' COMMENT '重构旧版本标记',
                                               `deal_time` datetime DEFAULT NULL COMMENT '处理时间',
                                               `finish_time` datetime DEFAULT NULL COMMENT '结案时间',
                                               `done_time` datetime DEFAULT NULL COMMENT '完成时间',
                                               `sign_back_time` datetime DEFAULT NULL COMMENT '回签时间',
                                               `order_time` datetime DEFAULT NULL COMMENT '下单时间',
                                               `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                               `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                               `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                               `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                               `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除',
                                               PRIMARY KEY (`id`) USING BTREE,
                                               KEY `idx_id` (`id`),
                                               KEY `idx_code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=25764 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `scm_purchase_contract_item`
--

DROP TABLE IF EXISTS `scm_purchase_contract_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `scm_purchase_contract_item` (
                                              `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                              `barcode` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '条形码',
                                              `osku_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '自营货号',
                                              `sku_unit` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '海关计量单位',
                                              `sync_code` bigint NOT NULL DEFAULT '0' COMMENT '同步标记',
                                              `description` varchar(5000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '描述',
                                              `cust_po` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '客户po号',
                                              `split_flag` tinyint NOT NULL DEFAULT '0' COMMENT '拆分标识',
                                              `free_quantity` int NOT NULL DEFAULT '0' COMMENT '赠品数量',
                                              `total_quantity` int DEFAULT NULL COMMENT '合同数量',
                                              `split_box_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否分箱',
                                              `specification_list` json NOT NULL DEFAULT (json_array()) COMMENT '规格',
                                              `inspection_time` datetime DEFAULT NULL COMMENT '验货时间',
                                              `handle_flag` tinyint DEFAULT NULL COMMENT '处理标识',
                                              `basic_sku_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '基础产品编号',
                                              `package_type_eng_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '包装方式英文名称',
                                              `package_type_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '包装方式名称',
                                              `thumbnail` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '缩略图',
                                              `main_picture` json NOT NULL DEFAULT (json_object()) COMMENT '主图',
                                              `cust_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '客户名称',
                                              `sku_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '产品名称',
                                              `sku_type` tinyint DEFAULT NULL COMMENT '产品类型',
                                              `cancel_flag` tinyint NOT NULL DEFAULT '0' COMMENT '作废标识',
                                              `out_quantity` int DEFAULT '0' COMMENT '出库数量',
                                              `shipping_price` json NOT NULL DEFAULT (json_object()) COMMENT '运费',
                                              `split_company_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '拆分主体名称',
                                              `split_company_id` bigint DEFAULT NULL COMMENT '拆分主体',
                                              `out_flag` int DEFAULT '0' COMMENT '出库标识',
                                              `sale_contract_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '外销合同编号',
                                              `sale_contract_id` bigint DEFAULT NULL COMMENT '外销合同id',
                                              `register_notice_quantity` int NOT NULL DEFAULT '0' COMMENT '已登票数量',
                                              `register_notice_status` int NOT NULL DEFAULT '0' COMMENT '登票状态',
                                              `push_down_auxiliary_flag` int NOT NULL DEFAULT '0' COMMENT '下推辅料采购标记',
                                              `sale_item_unique_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '销售合同明细编号',
                                              `abnormal_remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '异常说明',
                                              `abnormal_status` tinyint DEFAULT NULL COMMENT '异常状态',
                                              `bill_quantity` int NOT NULL DEFAULT '0' COMMENT '入库数量',
                                              `bill_status` tinyint NOT NULL DEFAULT '1' COMMENT '入库状态',
                                              `noticed_quantity` int NOT NULL DEFAULT '0' COMMENT '已转入库通知单数量',
                                              `source_unique_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '来源编号',
                                              `unique_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '唯一编号',
                                              `auxiliary_purchase_contract_item_id` bigint DEFAULT NULL COMMENT '辅料相关的采购合同明细主键',
                                              `applied_amount` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '已申请金额',
                                              `payment_amount` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '已支付金额',
                                              `invoiced_amount` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '已开票金额',
                                              `invoiced_currency` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '登票币种',
                                              `invoiced_quantity` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '已开票数量',
                                              `invoice_status` tinyint NOT NULL DEFAULT '0' COMMENT '开票状态',
                                              `sale_contract_item_id` bigint DEFAULT NULL COMMENT '销售明细主键',
                                              `package_cost` json DEFAULT (json_object()) COMMENT '包装价格',
                                              `sort_num` int NOT NULL DEFAULT '0' COMMENT '序号',
                                              `ver` int DEFAULT NULL COMMENT '版本',
                                              `sync_quote_flag` tinyint DEFAULT NULL COMMENT '是否同步供应商',
                                              `free_flag` tinyint DEFAULT NULL COMMENT '是否赠品',
                                              `plan_arrive_date` datetime DEFAULT NULL COMMENT '预计到料日期',
                                              `warehousing_type` tinyint NOT NULL DEFAULT '0' COMMENT '入库状态',
                                              `purchase_url` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '采购链接',
                                              `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
                                              `check_cost` json DEFAULT NULL COMMENT '验货费用',
                                              `auxiliary_sku_flag` int DEFAULT NULL COMMENT '是否通用辅料',
                                              `auxiliary_sku_type` int DEFAULT NULL COMMENT '辅料采购类型',
                                              `auxiliary_purchase_contract_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '采购合同编号',
                                              `auxiliary_sale_contract_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '辅料属于的采购销售编号',
                                              `auxiliary_sku_id` bigint DEFAULT NULL COMMENT '辅料属于的采购合同产品id',
                                              `auxiliary_sku_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '辅料属于的采购合同产品编号',
                                              `auxiliary_csku_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '辅料属于的采购合同产品客户货号',
                                              `spec_remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '规格描述',
                                              `annex` json DEFAULT NULL COMMENT '附件',
                                              `sku_id` bigint DEFAULT NULL COMMENT '产品id',
                                              `sku_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT 'SKU编号',
                                              `cust_id` bigint DEFAULT NULL COMMENT '客户id',
                                              `cust_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '客户编号',
                                              `csku_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '客户货号',
                                              `own_brand_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否自主品牌',
                                              `cust_pro_flag` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '客户产品标识',
                                              `purchase_contract_id` bigint DEFAULT '0' COMMENT '采购合同单号',
                                              `purchase_contract_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '采购合同编号',
                                              `purchase_type` tinyint NOT NULL COMMENT '采购类型',
                                              `vender_id` bigint DEFAULT NULL COMMENT '供应商id',
                                              `vender_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '供应商编号',
                                              `wms_ids` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '仓库id列表',
                                              `wms_names` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '仓库名称列表',
                                              `purchase_user_id` bigint DEFAULT NULL COMMENT '采购员id',
                                              `purchase_user_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '采购员姓名',
                                              `purchase_user_dept_id` bigint DEFAULT NULL COMMENT '采购员部门id',
                                              `purchase_user_dept_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '采购员部门名称',
                                              `packaging_price` json DEFAULT NULL COMMENT '包装价',
                                              `unit_price` json DEFAULT NULL COMMENT '采购单价',
                                              `total_price` json DEFAULT NULL COMMENT '总价',
                                              `with_tax_price` json DEFAULT NULL COMMENT '含税总价',
                                              `quantity` int NOT NULL DEFAULT '0' COMMENT '采购数量',
                                              `check_status` tinyint NOT NULL DEFAULT '1' COMMENT '验货状态',
                                              `checked_quantity` int NOT NULL DEFAULT '0' COMMENT '已验货数量',
                                              `receive_status` int NOT NULL DEFAULT '0' COMMENT '收货状态',
                                              `received_quantity` int NOT NULL DEFAULT '0' COMMENT '已收货数量',
                                              `exchange_quantity` int NOT NULL DEFAULT '0' COMMENT '换货量',
                                              `return_quantity` int NOT NULL DEFAULT '0' COMMENT '退货量',
                                              `qty_per_innerbox` int DEFAULT '0' COMMENT '内箱装量',
                                              `qty_per_outerbox` int DEFAULT '0' COMMENT '外箱装量',
                                              `package_length` decimal(19,6) DEFAULT '0.000000' COMMENT '包装规格长度',
                                              `package_width` decimal(19,6) DEFAULT '0.000000' COMMENT '包装规格宽度',
                                              `package_height` decimal(19,6) DEFAULT '0.000000' COMMENT '包装规格高度',
                                              `package_unit` tinyint DEFAULT NULL COMMENT '包装规格单位',
                                              `outerbox_length` decimal(19,6) DEFAULT '0.000000' COMMENT '外箱规格长度',
                                              `outerbox_width` decimal(19,6) DEFAULT '0.000000' COMMENT '外箱规格宽度',
                                              `outerbox_height` decimal(19,6) DEFAULT '0.000000' COMMENT '外箱规格高度',
                                              `outerbox_unit` tinyint DEFAULT NULL COMMENT '外箱规格单位',
                                              `outerbox_volume` decimal(19,6) DEFAULT '0.000000' COMMENT '外箱体积',
                                              `outerbox_netweight` json DEFAULT (json_object()) COMMENT '外箱净重',
                                              `single_grossweight` json DEFAULT (json_object()) COMMENT '单品毛重',
                                              `outerbox_grossweight` json DEFAULT (json_object()) COMMENT '外箱毛重',
                                              `tax_rate` decimal(19,6) DEFAULT '0.000000' COMMENT '税率',
                                              `vender_prod_code` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '工厂货号',
                                              `quote_date` datetime DEFAULT NULL COMMENT '报价日期',
                                              `freight_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否含运费',
                                              `package_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否含包装',
                                              `package_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '包装方式',
                                              `currency` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '币种',
                                              `fax_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否含税',
                                              `moq` int NOT NULL DEFAULT '0' COMMENT '最小起购量',
                                              `box_count` int NOT NULL DEFAULT '0' COMMENT '箱数',
                                              `delivery` int DEFAULT NULL COMMENT '到货天数',
                                              `creator` int DEFAULT NULL COMMENT '创建人',
                                              `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                              `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                              `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                              `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除',
                                              PRIMARY KEY (`id`) USING BTREE,
                                              KEY `index_purchase_code` (`purchase_contract_code`) USING BTREE,
                                              KEY `idx_purchase_contract_id` (`purchase_contract_id`),
                                              KEY `idx_purchase_contract_code` (`purchase_contract_code`)
) ENGINE=InnoDB AUTO_INCREMENT=592211 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='采购合同明细表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `scm_purchase_contract_item_copy1`
--

DROP TABLE IF EXISTS `scm_purchase_contract_item_copy1`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `scm_purchase_contract_item_copy1` (
                                                    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                                    `barcode` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '条形码',
                                                    `osku_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '自营货号',
                                                    `sku_unit` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '海关计量单位',
                                                    `sync_code` bigint NOT NULL DEFAULT '0' COMMENT '同步标记',
                                                    `description` varchar(5000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '描述',
                                                    `cust_po` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '客户po号',
                                                    `split_flag` tinyint NOT NULL DEFAULT '0' COMMENT '拆分标识',
                                                    `free_quantity` int NOT NULL DEFAULT '0' COMMENT '赠品数量',
                                                    `total_quantity` int DEFAULT NULL COMMENT '合同数量',
                                                    `split_box_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否分箱',
                                                    `specification_list` json NOT NULL DEFAULT (json_array()) COMMENT '规格',
                                                    `inspection_time` datetime DEFAULT NULL COMMENT '验货时间',
                                                    `handle_flag` tinyint DEFAULT NULL COMMENT '处理标识',
                                                    `basic_sku_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '基础产品编号',
                                                    `package_type_eng_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '包装方式英文名称',
                                                    `package_type_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '包装方式名称',
                                                    `thumbnail` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '缩略图',
                                                    `main_picture` json NOT NULL DEFAULT (json_object()) COMMENT '主图',
                                                    `cust_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '客户名称',
                                                    `sku_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '产品名称',
                                                    `sku_type` tinyint DEFAULT NULL COMMENT '产品类型',
                                                    `cancel_flag` tinyint NOT NULL DEFAULT '0' COMMENT '作废标识',
                                                    `out_quantity` int DEFAULT '0' COMMENT '出库数量',
                                                    `shipping_price` json NOT NULL DEFAULT (json_object()) COMMENT '运费',
                                                    `split_company_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '拆分主体名称',
                                                    `split_company_id` bigint DEFAULT NULL COMMENT '拆分主体',
                                                    `out_flag` int DEFAULT '0' COMMENT '出库标识',
                                                    `sale_contract_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '外销合同编号',
                                                    `sale_contract_id` bigint DEFAULT NULL COMMENT '外销合同id',
                                                    `register_notice_quantity` int NOT NULL DEFAULT '0' COMMENT '已登票数量',
                                                    `register_notice_status` int NOT NULL DEFAULT '0' COMMENT '登票状态',
                                                    `push_down_auxiliary_flag` int NOT NULL DEFAULT '0' COMMENT '下推辅料采购标记',
                                                    `sale_item_unique_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '销售合同明细编号',
                                                    `abnormal_remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '异常说明',
                                                    `abnormal_status` tinyint DEFAULT NULL COMMENT '异常状态',
                                                    `bill_quantity` int NOT NULL DEFAULT '0' COMMENT '入库数量',
                                                    `bill_status` tinyint NOT NULL DEFAULT '1' COMMENT '入库状态',
                                                    `noticed_quantity` int NOT NULL DEFAULT '0' COMMENT '已转入库通知单数量',
                                                    `source_unique_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '来源编号',
                                                    `unique_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '唯一编号',
                                                    `auxiliary_purchase_contract_item_id` bigint DEFAULT NULL COMMENT '辅料相关的采购合同明细主键',
                                                    `applied_amount` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '已申请金额',
                                                    `payment_amount` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '已支付金额',
                                                    `invoiced_amount` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '已开票金额',
                                                    `invoiced_currency` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '登票币种',
                                                    `invoiced_quantity` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '已开票数量',
                                                    `invoice_status` tinyint NOT NULL DEFAULT '0' COMMENT '开票状态',
                                                    `sale_contract_item_id` bigint DEFAULT NULL COMMENT '销售明细主键',
                                                    `package_cost` json DEFAULT (json_object()) COMMENT '包装价格',
                                                    `sort_num` int NOT NULL DEFAULT '0' COMMENT '序号',
                                                    `ver` int DEFAULT NULL COMMENT '版本',
                                                    `sync_quote_flag` tinyint DEFAULT NULL COMMENT '是否同步供应商',
                                                    `free_flag` tinyint DEFAULT NULL COMMENT '是否赠品',
                                                    `plan_arrive_date` datetime DEFAULT NULL COMMENT '预计到料日期',
                                                    `warehousing_type` tinyint NOT NULL DEFAULT '0' COMMENT '入库状态',
                                                    `purchase_url` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '采购链接',
                                                    `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
                                                    `check_cost` json DEFAULT NULL COMMENT '验货费用',
                                                    `auxiliary_sku_flag` int DEFAULT NULL COMMENT '是否通用辅料',
                                                    `auxiliary_sku_type` int DEFAULT NULL COMMENT '辅料采购类型',
                                                    `auxiliary_purchase_contract_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '采购合同编号',
                                                    `auxiliary_sale_contract_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '辅料属于的采购销售编号',
                                                    `auxiliary_sku_id` bigint DEFAULT NULL COMMENT '辅料属于的采购合同产品id',
                                                    `auxiliary_sku_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '辅料属于的采购合同产品编号',
                                                    `auxiliary_csku_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '辅料属于的采购合同产品客户货号',
                                                    `spec_remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '规格描述',
                                                    `annex` json DEFAULT NULL COMMENT '附件',
                                                    `sku_id` bigint DEFAULT NULL COMMENT '产品id',
                                                    `sku_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT 'SKU编号',
                                                    `cust_id` bigint DEFAULT NULL COMMENT '客户id',
                                                    `cust_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '客户编号',
                                                    `csku_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '客户货号',
                                                    `own_brand_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否自主品牌',
                                                    `cust_pro_flag` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '客户产品标识',
                                                    `purchase_contract_id` bigint DEFAULT '0' COMMENT '采购合同单号',
                                                    `purchase_contract_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '采购合同编号',
                                                    `purchase_type` tinyint NOT NULL COMMENT '采购类型',
                                                    `vender_id` bigint DEFAULT NULL COMMENT '供应商id',
                                                    `vender_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '供应商编号',
                                                    `wms_ids` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '仓库id列表',
                                                    `wms_names` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '仓库名称列表',
                                                    `purchase_user_id` bigint DEFAULT NULL COMMENT '采购员id',
                                                    `purchase_user_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '采购员姓名',
                                                    `purchase_user_dept_id` bigint DEFAULT NULL COMMENT '采购员部门id',
                                                    `purchase_user_dept_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '采购员部门名称',
                                                    `packaging_price` json DEFAULT NULL COMMENT '包装价',
                                                    `unit_price` json DEFAULT NULL COMMENT '采购单价',
                                                    `total_price` json DEFAULT NULL COMMENT '总价',
                                                    `with_tax_price` json DEFAULT NULL COMMENT '含税总价',
                                                    `quantity` int NOT NULL DEFAULT '0' COMMENT '采购数量',
                                                    `check_status` tinyint NOT NULL DEFAULT '1' COMMENT '验货状态',
                                                    `checked_quantity` int NOT NULL DEFAULT '0' COMMENT '已验货数量',
                                                    `receive_status` int NOT NULL DEFAULT '0' COMMENT '收货状态',
                                                    `received_quantity` int NOT NULL DEFAULT '0' COMMENT '已收货数量',
                                                    `exchange_quantity` int NOT NULL DEFAULT '0' COMMENT '换货量',
                                                    `return_quantity` int NOT NULL DEFAULT '0' COMMENT '退货量',
                                                    `qty_per_innerbox` int DEFAULT '0' COMMENT '内箱装量',
                                                    `qty_per_outerbox` int DEFAULT '0' COMMENT '外箱装量',
                                                    `package_length` decimal(19,6) DEFAULT '0.000000' COMMENT '包装规格长度',
                                                    `package_width` decimal(19,6) DEFAULT '0.000000' COMMENT '包装规格宽度',
                                                    `package_height` decimal(19,6) DEFAULT '0.000000' COMMENT '包装规格高度',
                                                    `package_unit` tinyint DEFAULT NULL COMMENT '包装规格单位',
                                                    `outerbox_length` decimal(19,6) DEFAULT '0.000000' COMMENT '外箱规格长度',
                                                    `outerbox_width` decimal(19,6) DEFAULT '0.000000' COMMENT '外箱规格宽度',
                                                    `outerbox_height` decimal(19,6) DEFAULT '0.000000' COMMENT '外箱规格高度',
                                                    `outerbox_unit` tinyint DEFAULT NULL COMMENT '外箱规格单位',
                                                    `outerbox_volume` decimal(19,6) DEFAULT '0.000000' COMMENT '外箱体积',
                                                    `outerbox_netweight` json DEFAULT (json_object()) COMMENT '外箱净重',
                                                    `single_grossweight` json DEFAULT (json_object()) COMMENT '单品毛重',
                                                    `outerbox_grossweight` json DEFAULT (json_object()) COMMENT '外箱毛重',
                                                    `tax_rate` decimal(19,6) DEFAULT '0.000000' COMMENT '税率',
                                                    `vender_prod_code` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '工厂货号',
                                                    `quote_date` datetime DEFAULT NULL COMMENT '报价日期',
                                                    `freight_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否含运费',
                                                    `package_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否含包装',
                                                    `package_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '包装方式',
                                                    `currency` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '币种',
                                                    `fax_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否含税',
                                                    `moq` int NOT NULL DEFAULT '0' COMMENT '最小起购量',
                                                    `box_count` int NOT NULL DEFAULT '0' COMMENT '箱数',
                                                    `delivery` int DEFAULT NULL COMMENT '到货天数',
                                                    `creator` int DEFAULT NULL COMMENT '创建人',
                                                    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                                    `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                                    `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                                    `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除',
                                                    PRIMARY KEY (`id`) USING BTREE,
                                                    KEY `index_purchase_code` (`purchase_contract_code`) USING BTREE,
                                                    KEY `idx_purchase_contract_id` (`purchase_contract_id`),
                                                    KEY `idx_purchase_contract_code` (`purchase_contract_code`)
) ENGINE=InnoDB AUTO_INCREMENT=590671 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='采购合同明细表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `scm_purchase_plan`
--

DROP TABLE IF EXISTS `scm_purchase_plan`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `scm_purchase_plan` (
                                     `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                     `sale_type` tinyint DEFAULT NULL COMMENT '销售类型',
                                     `creator_dept_id` bigint DEFAULT NULL COMMENT '创建人部门',
                                     `auxiliary_purchase_user` json NOT NULL DEFAULT (json_array()) COMMENT '辅料属于的采购员',
                                     `auxiliary_sales` json NOT NULL DEFAULT (json_array()) COMMENT '辅料属于的销售员',
                                     `auxiliary_manager` json NOT NULL DEFAULT (json_array()) COMMENT '辅料属于的跟单员',
                                     `split_flag` tinyint NOT NULL DEFAULT '0' COMMENT '拆分标识',
                                     `source_plan_id` bigint DEFAULT NULL COMMENT '来源计划编号',
                                     `manager_list` json NOT NULL DEFAULT (json_array()) COMMENT '业务员',
                                     `purchase_user_list` json NOT NULL DEFAULT (json_array()) COMMENT '采购员',
                                     `sales` json NOT NULL DEFAULT (json_object()) COMMENT '销售人员',
                                     `link_code_list` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '订单链路编号',
                                     `company_path` json NOT NULL DEFAULT (json_object()) COMMENT '订单路径',
                                     `sale_contract_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '销售合同编号',
                                     `sale_contract_id` bigint DEFAULT NULL COMMENT '销售合同主键',
                                     `ver` int DEFAULT NULL COMMENT '版本',
                                     `code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '编号',
                                     `audit_status` tinyint NOT NULL COMMENT '审核状态',
                                     `plan_status` tinyint NOT NULL COMMENT '计划状态',
                                     `company_id` bigint DEFAULT NULL COMMENT '采购主体',
                                     `source_type` tinyint NOT NULL COMMENT '来源单类型',
                                     `plan_date` datetime DEFAULT NULL COMMENT '计划日期',
                                     `est_delivery_date` datetime DEFAULT NULL COMMENT '预计交期',
                                     `cust_id` bigint DEFAULT NULL COMMENT '客户id',
                                     `cust_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '客户编号',
                                     `cust_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                                     `finish_time` datetime DEFAULT NULL COMMENT '结案时间',
                                     `done_time` datetime DEFAULT NULL COMMENT '完成时间',
                                     `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
                                     `annex` json NOT NULL DEFAULT (json_array()) COMMENT '附件',
                                     `auxiliary_flag` int NOT NULL DEFAULT '0' COMMENT '是否辅料采购',
                                     `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                     `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                     `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                     `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                     `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除  0：有效 1：删除',
                                     PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1502 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='采购计划表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `scm_purchase_plan_item`
--

DROP TABLE IF EXISTS `scm_purchase_plan_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `scm_purchase_plan_item` (
                                          `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                          `barcode` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '条形码',
                                          `vender_delivery_date` datetime DEFAULT NULL COMMENT '交货日期',
                                          `vender_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '供应商名称',
                                          `osku_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '自营货号',
                                          `sku_unit` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '海关计量单位',
                                          `thumbnail` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '缩略图',
                                          `cust_po` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '客户po号',
                                          `total_quantity` int DEFAULT NULL COMMENT '合同数量',
                                          `split_box_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否分箱',
                                          `specification_list` json NOT NULL DEFAULT (json_array()) COMMENT '规格',
                                          `source_plan_item_id` bigint DEFAULT NULL COMMENT '来源计划明细编号',
                                          `basic_sku_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '基础产品编号',
                                          `converted_quantity` int NOT NULL DEFAULT '0' COMMENT '已转合同数量',
                                          `purchase_quantity` int NOT NULL DEFAULT '0' COMMENT '采购数量',
                                          `parent_sku_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '处理人',
                                          `parent_sku_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '处理人',
                                          `purchase_contract_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '采购合同编号',
                                          `cancel_flag` tinyint NOT NULL DEFAULT '0' COMMENT '作废标识',
                                          `shipping_price` json NOT NULL DEFAULT (json_object()) COMMENT '运费',
                                          `lock_quantity` int NOT NULL DEFAULT '0' COMMENT '采购计划锁定库存数量',
                                          `sale_lock_quantity` int NOT NULL DEFAULT '0' COMMENT '外销合同锁定库存数量',
                                          `split_company_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '拆分主体名称',
                                          `split_company_id` bigint DEFAULT NULL COMMENT '拆分主体',
                                          `sale_quantity` int NOT NULL DEFAULT '0' COMMENT '销售数量',
                                          `sale_item_unique_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '销售合同明细编号',
                                          `source_unique_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '来源编号',
                                          `unique_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '唯一编号',
                                          `link_code_list` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '订单链路编号',
                                          `source_id` bigint DEFAULT NULL COMMENT '组合产品父级主键',
                                          `main_picture` json NOT NULL DEFAULT (json_object()) COMMENT '图片',
                                          `sku_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '产品名称',
                                          `need_pur_quantity` int DEFAULT NULL COMMENT '待采购数量',
                                          `current_lock_quantity` int DEFAULT NULL COMMENT '锁定库存',
                                          `purchase_company_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '下单主体名称',
                                          `purchase_company_id` int DEFAULT NULL COMMENT '下单主体主键',
                                          `levels` int DEFAULT NULL COMMENT '明细层级',
                                          `three_split_num` int DEFAULT NULL COMMENT '三级拆分序号',
                                          `two_split_num` int DEFAULT NULL COMMENT '二级拆分序号',
                                          `one_split_num` int DEFAULT NULL COMMENT '一级拆分序号',
                                          `sale_contract_item_id` bigint DEFAULT NULL COMMENT '销售明细主键',
                                          `ver` int DEFAULT NULL COMMENT '版本',
                                          `sort_num` int NOT NULL DEFAULT '0' COMMENT '序号',
                                          `sale_contract_id` bigint DEFAULT NULL COMMENT '外销合同id',
                                          `sale_contract_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '外销合同编号',
                                          `purchase_plan_id` bigint DEFAULT NULL COMMENT '采购计划id',
                                          `purchase_plan_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '采购计划编号',
                                          `cust_id` bigint DEFAULT NULL COMMENT '客户id',
                                          `cust_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '客户编号',
                                          `sku_id` bigint DEFAULT NULL COMMENT '产品id',
                                          `sku_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT 'SKU编号',
                                          `csku_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '客户货号',
                                          `own_brand_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否自主品牌',
                                          `cust_pro_flag` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '客户产品标识',
                                          `stock_id` bigint DEFAULT NULL COMMENT '仓库id',
                                          `stock_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '仓库编号',
                                          `converted_flag` int NOT NULL DEFAULT '0' COMMENT '转采购合同标记',
                                          `free_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否赠品',
                                          `purchase_model` int NOT NULL DEFAULT '1' COMMENT '采购模式',
                                          `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
                                          `convert_time` datetime DEFAULT NULL COMMENT '生成采购单时间',
                                          `auxiliary_sku_flag` int DEFAULT NULL COMMENT '是否通用辅料',
                                          `auxiliary_sku_type` int DEFAULT NULL COMMENT '辅料采购类型',
                                          `auxiliary_purchase_contract_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '采购合同编号',
                                          `auxiliary_sale_contract_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '辅料属于的采购销售编号',
                                          `auxiliary_sku_id` bigint DEFAULT NULL COMMENT '辅料属于的采购合同产品id',
                                          `auxiliary_sku_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '辅料属于的采购合同产品编号',
                                          `auxiliary_csku_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '辅料属于的采购合同产品客户货号',
                                          `spec_remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '规格描述',
                                          `annex` json DEFAULT NULL COMMENT '附件',
                                          `vender_id` bigint DEFAULT NULL COMMENT '供应商id',
                                          `vender_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '供应商编号',
                                          `vender_prod_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '工厂货号',
                                          `purchase_user_id` bigint unsigned DEFAULT NULL COMMENT '采购员id',
                                          `purchase_user_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '采购员姓名',
                                          `purchase_user_dept_id` bigint unsigned DEFAULT NULL COMMENT '采购员部门id',
                                          `purchase_user_dept_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '采购员部门名称',
                                          `packaging_price` json DEFAULT NULL COMMENT '包装价',
                                          `unit_price` json DEFAULT NULL COMMENT '采购单价',
                                          `total_price` json DEFAULT NULL COMMENT '总价',
                                          `with_tax_price` json DEFAULT NULL COMMENT '含税总价',
                                          `tax_rate` decimal(19,6) DEFAULT NULL COMMENT '税率',
                                          `purchase_type` tinyint NOT NULL COMMENT '采购类型',
                                          `qty_per_innerbox` int DEFAULT NULL COMMENT '内箱装量',
                                          `qty_per_outerbox` int DEFAULT NULL COMMENT '外箱装量',
                                          `package_length` decimal(19,6) DEFAULT NULL COMMENT '包装规格长度',
                                          `package_width` decimal(19,6) DEFAULT NULL COMMENT '包装规格宽度',
                                          `package_height` decimal(19,6) DEFAULT NULL COMMENT '包装规格高度',
                                          `package_unit` tinyint DEFAULT NULL COMMENT '包装规格单位',
                                          `outerbox_length` decimal(19,6) DEFAULT NULL COMMENT '外箱规格长度',
                                          `outerbox_width` decimal(19,6) DEFAULT NULL COMMENT '外箱规格宽度',
                                          `outerbox_unit` tinyint DEFAULT NULL COMMENT '外箱规格单位',
                                          `outerbox_height` decimal(19,6) DEFAULT NULL COMMENT '外箱规格高度',
                                          `outerbox_volume` decimal(19,6) DEFAULT NULL COMMENT '外箱体积',
                                          `outerbox_netweight` json DEFAULT (json_object()) COMMENT '外箱净重',
                                          `single_grossweight` json DEFAULT (json_object()) COMMENT '单品毛重',
                                          `outerbox_grossweight` json DEFAULT (json_object()) COMMENT '外箱毛重',
                                          `quote_date` datetime DEFAULT NULL COMMENT '报价日期',
                                          `freight_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否含运费',
                                          `package_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否含包装',
                                          `package_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '包装方式',
                                          `currency` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '币种',
                                          `fax_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否含税',
                                          `moq` int NOT NULL DEFAULT '0' COMMENT '最小起购量',
                                          `box_count` int NOT NULL DEFAULT '0' COMMENT '箱数',
                                          `sku_type` int NOT NULL DEFAULT '0' COMMENT '产品类型',
                                          `purchase_url` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '采购链接',
                                          `creator` int DEFAULT NULL COMMENT '创建人',
                                          `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                          `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                          `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                          `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除  0：有效 1：删除',
                                          PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5290 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='采购计划明细表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `scm_purchase_registration`
--

DROP TABLE IF EXISTS `scm_purchase_registration`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `scm_purchase_registration` (
                                             `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                                             `status` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '状态',
                                             `review_user` json NOT NULL DEFAULT (json_object()) COMMENT '复核人',
                                             `review_date` datetime DEFAULT NULL COMMENT '复核日期',
                                             `code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '编号',
                                             `company_id` bigint DEFAULT NULL COMMENT '付款主体主键',
                                             `company_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '付款主题名称',
                                             `vender_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '供应商编号',
                                             `vender_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '供应商名称',
                                             `tax_type` tinyint DEFAULT NULL COMMENT '发票类型',
                                             `invoice_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '税票编号',
                                             `invoiced_date` datetime DEFAULT NULL COMMENT '收票日期',
                                             `invoice_amount` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '发票总金额',
                                             `currency` varchar(3) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '币别',
                                             `tax_rate` decimal(19,6) DEFAULT NULL COMMENT '税率',
                                             `annex` json NOT NULL DEFAULT (json_array()) COMMENT '附件',
                                             `audit_status` tinyint DEFAULT NULL COMMENT '审核状态',
                                             `input_user` json NOT NULL DEFAULT (json_object()) COMMENT '录入人',
                                             `input_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '录入日期',
                                             `process_instance_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '流程实例的编号',
                                             `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                             `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                             `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                             `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                             `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除  0：有效 1：删除',
                                             PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=482 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='采购跟单登记表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `scm_purchase_registration_item`
--

DROP TABLE IF EXISTS `scm_purchase_registration_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `scm_purchase_registration_item` (
                                                  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                                                  `basic_sku_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '基础产品编号',
                                                  `purchase_contract_item_id` bigint DEFAULT NULL COMMENT '采购合同明细主键',
                                                  `inveic_registered_quantity` int unsigned NOT NULL DEFAULT '0' COMMENT '发票登记数量',
                                                  `invoic_sku_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '开票品名',
                                                  `ship_invoice_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '发票号',
                                                  `registration_id` bigint unsigned DEFAULT NULL COMMENT '跟单登记主键',
                                                  `invoicing_notices_item_id` bigint unsigned DEFAULT NULL COMMENT '开票通知主键',
                                                  `sale_contract_item_id` bigint unsigned DEFAULT NULL COMMENT '销售明细主键',
                                                  `tax_rate` decimal(19,6) DEFAULT NULL COMMENT '税率',
                                                  `invoic_this_quantity` int NOT NULL DEFAULT '0' COMMENT '采购合同明细主键',
                                                  `shipping_quantity` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '出运数量',
                                                  `hs_measure_unit` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '海关计量单位',
                                                  `declaration_quantity` int unsigned NOT NULL DEFAULT '0' COMMENT '报关数量',
                                                  `invoic_price` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '开票单价',
                                                  `declaration_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '报关品名',
                                                  `invoice_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '税票编号',
                                                  `purchase_total_quantity` int NOT NULL DEFAULT '0' COMMENT '总采购数量',
                                                  `purchase_with_tax_price` json NOT NULL DEFAULT (json_object()) COMMENT '采购含税单价',
                                                  `purchase_currency` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '采购币种',
                                                  `purchase_contract_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '采购合同编号',
                                                  `manager` json NOT NULL DEFAULT (json_object()) COMMENT '跟单员',
                                                  `sale_contract_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '销售合同号',
                                                  `sku_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '中文品名',
                                                  `sku_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '产品编号',
                                                  `hs_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '海关编码',
                                                  `csku_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '客户货号',
                                                  `purchase_tax_rate` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '采购税率',
                                                  `vender_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '供应商编号',
                                                  `vender_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '供应商名称',
                                                  `invoic_notices_quantity` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '通知开票数量',
                                                  `ship_invoic_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '出运发票号',
                                                  `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                                  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                                  `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                                  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                                  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除  0：有效 1：删除',
                                                  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=922 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='采购跟单登记明细表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `scm_qualification`
--

DROP TABLE IF EXISTS `scm_qualification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `scm_qualification` (
                                     `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                                     `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '表单名称',
                                     `description` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '资质描述',
                                     `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                     `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                     `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                     `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                     `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除  0：有效 1：删除',
                                     PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='资质表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `scm_quote_item`
--

DROP TABLE IF EXISTS `scm_quote_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `scm_quote_item` (
                                  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                                  `split_box_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否分箱',
                                  `specification_list` json NOT NULL DEFAULT (json_array()) COMMENT '规格',
                                  `twenty_foot_container_contain_num` bigint DEFAULT NULL COMMENT '20尺柜装数量',
                                  `forty_foot_high_container_contain_num` bigint DEFAULT NULL COMMENT '40高柜装数量',
                                  `forty_foot_container_contain_num` bigint DEFAULT NULL COMMENT '40尺柜装数量',
                                  `shipping_price` json NOT NULL DEFAULT (json_object()) COMMENT '运费',
                                  `twenty_foot_container_capacity` bigint DEFAULT NULL COMMENT '20尺柜装量',
                                  `forty_foot_high_container_capacity` bigint DEFAULT NULL COMMENT '40高柜装量',
                                  `forty_foot_container_capacity` bigint DEFAULT NULL COMMENT '40尺柜装量',
                                  `invoice_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '开票品名',
                                  `sku_id` bigint DEFAULT NULL COMMENT '产品主键',
                                  `quote_id` bigint unsigned DEFAULT NULL COMMENT '供应商报价单id',
                                  `sku_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT 'SKU编号',
                                  `vender_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '供应商编号',
                                  `vender_id` bigint unsigned DEFAULT NULL COMMENT '供应商id',
                                  `vender_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '供应商名称',
                                  `purchase_user_id` bigint unsigned DEFAULT NULL COMMENT '采购员id',
                                  `purchase_user_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '采购员姓名',
                                  `purchase_user_dept_id` bigint unsigned DEFAULT NULL COMMENT '采购员部门id',
                                  `purchase_user_dept_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '采购员部门名称',
                                  `vender_prod_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '工厂货号',
                                  `quote_date` datetime DEFAULT NULL COMMENT '报价日期',
                                  `freight_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否含运费',
                                  `package_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否含包装',
                                  `package_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '包装方式',
                                  `currency` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '币种',
                                  `fax_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否含税',
                                  `tax_rate` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '税率',
                                  `moq` int NOT NULL DEFAULT '0' COMMENT '最小起购量',
                                  `packaging_price` json NOT NULL DEFAULT (json_object()) COMMENT '包装价',
                                  `unit_price` json NOT NULL DEFAULT (json_object()) COMMENT '采购单价',
                                  `total_price` json NOT NULL DEFAULT (json_object()) COMMENT '含税含包装含运费单价',
                                  `with_tax_price` json NOT NULL DEFAULT (json_object()) COMMENT '含税单价',
                                  `delivery` int DEFAULT NULL COMMENT '交期',
                                  `purchase_url` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '采购链接',
                                  `qty_per_innerbox` int NOT NULL DEFAULT '0' COMMENT '内箱装量',
                                  `qty_per_outerbox` int NOT NULL DEFAULT '0' COMMENT '外箱装量',
                                  `package_length` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '包装规格长度',
                                  `package_width` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '包装规格宽度',
                                  `package_height` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '包装规格高度',
                                  `outerbox_length` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '外箱规格长度',
                                  `outerbox_width` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '外箱规格宽度',
                                  `outerbox_height` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '外箱规格高度',
                                  `outerbox_volume` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '外箱体积',
                                  `outerbox_netweight` json NOT NULL DEFAULT (json_object()) COMMENT '外箱净重',
                                  `single_grossweight` json DEFAULT NULL COMMENT '单品毛重',
                                  `outerbox_grossweight` json NOT NULL DEFAULT (json_object()) COMMENT '外箱毛重',
                                  `default_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否默认',
                                  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
                                  `annex` json NOT NULL DEFAULT (json_object()) COMMENT '附件',
                                  `picture` json NOT NULL DEFAULT (json_object()) COMMENT '图片',
                                  `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                  `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除  0：有效 1：删除',
                                  PRIMARY KEY (`id`) USING BTREE,
                                  KEY `idx_scm_quote_item_sku_id` (`sku_id`),
                                  KEY `vender_id` (`vender_id`),
                                  KEY `purchase_user_id` (`purchase_user_id`),
                                  KEY `default_flag` (`default_flag`),
                                  KEY `idx_scm_quote_item_sku_code` (`sku_code`),
                                  KEY `idx_scm_quote_item_vender_code` (`vender_code`),
                                  KEY `idx_scm_quote_item_create_time` (`create_time`),
                                  KEY `currency` (`currency`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=142899 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='供应商报价明细';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `scm_vender`
--

DROP TABLE IF EXISTS `scm_vender`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `scm_vender` (
                              `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                              `tax_msg` json NOT NULL DEFAULT (json_array()) COMMENT '财务信息',
                              `buyer_list` json NOT NULL DEFAULT (json_array()) COMMENT '采购员列表',
                              `administration_vender_type` int DEFAULT NULL COMMENT '行政供应商类型',
                              `effect_range_list` json NOT NULL DEFAULT (json_array()) COMMENT '影响范围',
                              `model_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '流程模型标识',
                              `change_status` tinyint DEFAULT NULL COMMENT '变更状态',
                              `change_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否变更',
                              `change_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '变更是否删除',
                              `internal_company_id` bigint DEFAULT NULL COMMENT '内部企业主键',
                              `internal_flag` int DEFAULT NULL COMMENT '内部企业标识 0-否 1-是',
                              `ems_flag` int NOT NULL DEFAULT '0' COMMENT '快递公司标记',
                              `qualification_ids` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '关联资质id',
                              `ver` int NOT NULL DEFAULT '0' COMMENT '版本号',
                              `code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '供应商编码',
                              `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '供应商名称',
                              `name_eng` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '供应商英文名称',
                              `name_short` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '供应商简称',
                              `registered_capital` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '注册资本',
                              `legal_person` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '法定代表人',
                              `business_scope` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '主营业务',
                              `vender_link_code` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '应付供应商编号',
                              `company_area_id` int unsigned DEFAULT NULL COMMENT '公司所在城市',
                              `factory_area_id` int unsigned DEFAULT NULL COMMENT '工厂所在城市',
                              `delivery_area_id` int unsigned DEFAULT NULL COMMENT '快递所在城市',
                              `factory_address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '工厂地址',
                              `company_address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '公司地址',
                              `delivery_address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '快递地址',
                              `license_no` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '营业执照号',
                              `phone` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '企业电话',
                              `abroad_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否境外供应商',
                              `vender_type` int unsigned DEFAULT NULL COMMENT '供应商类型',
                              `vender_level` int unsigned DEFAULT NULL COMMENT '供应商等级',
                              `stage_type` tinyint DEFAULT NULL COMMENT '客户阶段（潜在客户，正式客户，退休客户）',
                              `country_id` bigint DEFAULT NULL COMMENT '国家编号',
                              `currency` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '币种',
                              `tax_rate` decimal(19,6) DEFAULT '0.000000' COMMENT '税率',
                              `tax_type` tinyint DEFAULT NULL COMMENT '发票类型',
                              `buyer_ids` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '采购员',
                              `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
                              `audit_status` tinyint NOT NULL COMMENT '审核状态',
                              `convert_flag` tinyint DEFAULT '0' COMMENT '转正标识',
                              `convert_time` datetime DEFAULT NULL COMMENT '转正时间',
                              `enable_flag` tinyint NOT NULL DEFAULT '1' COMMENT '是否启用',
                              `annex` json NOT NULL DEFAULT (json_object()) COMMENT '附件',
                              `fax` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '传真',
                              `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                              `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                              `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                              `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除  0：有效 1：删除',
                              PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6642 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='供应商信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `scm_vender_bankaccount`
--

DROP TABLE IF EXISTS `scm_vender_bankaccount`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `scm_vender_bankaccount` (
                                          `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                                          `ver` int unsigned NOT NULL DEFAULT '0' COMMENT '版本',
                                          `vender_id` bigint unsigned NOT NULL DEFAULT '0' COMMENT '供应商id',
                                          `vender_ver` int unsigned NOT NULL DEFAULT '0' COMMENT '供应商版本',
                                          `bank` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '银行',
                                          `bank_account` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '银行账号',
                                          `bank_address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '开户行地址',
                                          `bank_poc` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '开户行联系人',
                                          `bank_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '银行行号',
                                          `default_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否默认账号',
                                          `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                          `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                          `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                          `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                          `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除  0：有效 1：删除',
                                          PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=8239 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='供应商银行账户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `scm_vender_copy1`
--

DROP TABLE IF EXISTS `scm_vender_copy1`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `scm_vender_copy1` (
                                    `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                                    `tax_msg` json NOT NULL DEFAULT (json_array()) COMMENT '财务信息',
                                    `buyer_list` json NOT NULL DEFAULT (json_array()) COMMENT '采购员列表',
                                    `administration_vender_type` int DEFAULT NULL COMMENT '行政供应商类型',
                                    `effect_range_list` json NOT NULL DEFAULT (json_array()) COMMENT '影响范围',
                                    `model_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '流程模型标识',
                                    `change_status` tinyint DEFAULT NULL COMMENT '变更状态',
                                    `change_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否变更',
                                    `change_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '变更是否删除',
                                    `internal_company_id` bigint DEFAULT NULL COMMENT '内部企业主键',
                                    `internal_flag` int DEFAULT NULL COMMENT '内部企业标识 0-否 1-是',
                                    `ems_flag` int NOT NULL DEFAULT '0' COMMENT '快递公司标记',
                                    `qualification_ids` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '关联资质id',
                                    `ver` int NOT NULL DEFAULT '0' COMMENT '版本号',
                                    `code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '供应商编码',
                                    `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '供应商名称',
                                    `name_eng` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '供应商英文名称',
                                    `name_short` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '供应商简称',
                                    `registered_capital` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '注册资本',
                                    `legal_person` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '法定代表人',
                                    `business_scope` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '主营业务',
                                    `vender_link_code` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '应付供应商编号',
                                    `company_area_id` int unsigned DEFAULT NULL COMMENT '公司所在城市',
                                    `factory_area_id` int unsigned DEFAULT NULL COMMENT '工厂所在城市',
                                    `delivery_area_id` int unsigned DEFAULT NULL COMMENT '快递所在城市',
                                    `factory_address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '工厂地址',
                                    `company_address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '公司地址',
                                    `delivery_address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '快递地址',
                                    `license_no` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '营业执照号',
                                    `phone` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '企业电话',
                                    `abroad_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否境外供应商',
                                    `vender_type` int unsigned DEFAULT NULL COMMENT '供应商类型',
                                    `vender_level` int unsigned DEFAULT NULL COMMENT '供应商等级',
                                    `stage_type` tinyint DEFAULT NULL COMMENT '客户阶段（潜在客户，正式客户，退休客户）',
                                    `country_id` bigint DEFAULT NULL COMMENT '国家编号',
                                    `currency` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '币种',
                                    `tax_rate` decimal(19,6) DEFAULT '0.000000' COMMENT '税率',
                                    `tax_type` tinyint DEFAULT NULL COMMENT '发票类型',
                                    `buyer_ids` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '采购员',
                                    `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
                                    `audit_status` tinyint NOT NULL COMMENT '审核状态',
                                    `convert_flag` tinyint DEFAULT '0' COMMENT '转正标识',
                                    `convert_time` datetime DEFAULT NULL COMMENT '转正时间',
                                    `enable_flag` tinyint NOT NULL DEFAULT '1' COMMENT '是否启用',
                                    `annex` json NOT NULL DEFAULT (json_object()) COMMENT '附件',
                                    `fax` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '传真',
                                    `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                    `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                    `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除  0：有效 1：删除',
                                    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6414 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='供应商信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `scm_vender_manager`
--

DROP TABLE IF EXISTS `scm_vender_manager`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `scm_vender_manager` (
                                      `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                                      `ver` int unsigned NOT NULL DEFAULT '0' COMMENT '版本',
                                      `vender_id` bigint unsigned NOT NULL DEFAULT '0' COMMENT '供应商id',
                                      `vender_ver` int unsigned NOT NULL DEFAULT '0' COMMENT '供应商版本',
                                      `manager_id` bigint unsigned NOT NULL DEFAULT '0' COMMENT '采购员id',
                                      `default_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否默认',
                                      `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                      `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                      `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                      `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                      `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除  0：有效 1：删除',
                                      PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='供应商采购经理表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `scm_vender_payment`
--

DROP TABLE IF EXISTS `scm_vender_payment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `scm_vender_payment` (
                                      `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                                      `payment_id` bigint unsigned DEFAULT NULL COMMENT '结汇方式编号',
                                      `vender_id` bigint unsigned DEFAULT NULL COMMENT '客户id',
                                      `default_flag` bigint unsigned DEFAULT NULL COMMENT '是否缺省',
                                      `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                      `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                      `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                      `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                      `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除  0：有效 1：删除',
                                      PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7564 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='供应商付款方式表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `scm_vender_poc`
--

DROP TABLE IF EXISTS `scm_vender_poc`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `scm_vender_poc` (
                                  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                                  `ver` int unsigned NOT NULL DEFAULT '0' COMMENT '版本',
                                  `vender_id` bigint unsigned NOT NULL DEFAULT '0' COMMENT '供应商id',
                                  `vender_ver` int unsigned NOT NULL DEFAULT '0' COMMENT '供应商版本',
                                  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '名称',
                                  `poc_types` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '职位',
                                  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '电子邮件',
                                  `mobile` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '手机',
                                  `telephone` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '座机',
                                  `wechat` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '微信',
                                  `qq` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT 'QQ',
                                  `address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '地址',
                                  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '备注',
                                  `default_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否默认联系人',
                                  `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                  `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除  0：有效 1：删除',
                                  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=8062 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='供应商联系人表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sku_creator`
--

DROP TABLE IF EXISTS `sku_creator`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sku_creator` (
                               `CP_CODE` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                               `CP_INMAN` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               PRIMARY KEY (`CP_CODE`) USING BTREE,
                               KEY `PR_CODE` (`CP_CODE`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sms_add_sub_term`
--

DROP TABLE IF EXISTS `sms_add_sub_term`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sms_add_sub_term` (
                                    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                    `difference_reason` json DEFAULT NULL COMMENT '认领差异',
                                    `settlement_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否转结汇',
                                    `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态',
                                    `completed_amount` json NOT NULL DEFAULT (json_object()) COMMENT '完成金额',
                                    `contract_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '合同编号',
                                    `calculation_type` tinyint DEFAULT NULL COMMENT '加/减项',
                                    `fee_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '费用名称',
                                    `amount` json NOT NULL DEFAULT (json_object()) COMMENT '金额',
                                    `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                    `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除',
                                    `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=166 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='加减项';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sms_collection_plan`
--

DROP TABLE IF EXISTS `sms_collection_plan`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sms_collection_plan` (
                                       `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                       `real_collection_ratio` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '实际收款比例',
                                       `children` json NOT NULL DEFAULT (json_array()) COMMENT '收款明细',
                                       `control_shipment_flag` int NOT NULL DEFAULT '0' COMMENT '是否控制出运 ',
                                       `difference_reason` json NOT NULL DEFAULT (json_array()) COMMENT '差异原因',
                                       `contract_id` bigint DEFAULT NULL COMMENT '合同id',
                                       `step` tinyint DEFAULT NULL COMMENT '步骤',
                                       `payment_method` tinyint DEFAULT NULL COMMENT '支付方式',
                                       `date_type` tinyint DEFAULT NULL COMMENT '起始点',
                                       `start_date` datetime DEFAULT NULL COMMENT '起始日',
                                       `days` int NOT NULL DEFAULT '0' COMMENT '天数',
                                       `expected_receipt_date` datetime DEFAULT NULL COMMENT '预计收款日',
                                       `collection_ratio` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '收款比例',
                                       `receivable_amount` json NOT NULL DEFAULT (json_object()) COMMENT '应收金额',
                                       `received_amount` json NOT NULL DEFAULT (json_object()) COMMENT '实收金额',
                                       `control_purchase_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否控制采购',
                                       `exe_status` tinyint NOT NULL DEFAULT '0' COMMENT '状态',
                                       `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                       `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                       `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                       `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除',
                                       `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                       PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7025 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='销售合同收款计划';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sms_other_fee`
--

DROP TABLE IF EXISTS `sms_other_fee`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sms_other_fee` (
                                 `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                                 `sms_quotation_id` bigint DEFAULT NULL COMMENT '报价单id',
                                 `fee_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '费用名称',
                                 `amount` json NOT NULL DEFAULT (json_object()) COMMENT '金额',
                                 `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                 `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                 `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                 `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                 `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除  0：有效 1：删除',
                                 PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='其他费用表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sms_quotation`
--

DROP TABLE IF EXISTS `sms_quotation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sms_quotation` (
                                 `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                                 `print_flag` tinyint NOT NULL DEFAULT '0' COMMENT '打印状态',
                                 `code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '报价单号',
                                 `cust_poc_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '客户联系人名称',
                                 `country_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '国家名称',
                                 `currency` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '币种',
                                 `status` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '状态',
                                 `company_id` bigint DEFAULT NULL COMMENT '内部法人单位主键',
                                 `company_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '内部法人单位名称',
                                 `cust_id` bigint DEFAULT NULL COMMENT '客户主键',
                                 `cust_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '客户编号',
                                 `cust_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '客户名称',
                                 `is_new_cust` tinyint NOT NULL DEFAULT '0' COMMENT '是否新客户',
                                 `settlement_term_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '价格条款',
                                 `cust_poc_id` bigint DEFAULT NULL COMMENT '客户联系人主键',
                                 `country_id` bigint DEFAULT NULL COMMENT '国家id',
                                 `departure_port_id` bigint DEFAULT NULL COMMENT '出运口岸主键',
                                 `departure_port_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '出运口岸名称',
                                 `valid_period` datetime DEFAULT NULL COMMENT '有效期止',
                                 `manager` json NOT NULL DEFAULT (json_object()) COMMENT '业务员',
                                 `audit_status` tinyint NOT NULL DEFAULT '0' COMMENT '审核状态',
                                 `process_instance_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '流程实例的编号',
                                 `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                 `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                 `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                 `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                 `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除  0：有效 1：删除',
                                 PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='报价单主表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sms_quotation_item`
--

DROP TABLE IF EXISTS `sms_quotation_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sms_quotation_item` (
                                      `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                                      `split_box_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否分箱',
                                      `specification_list` json NOT NULL DEFAULT (json_array()) COMMENT '规格',
                                      `basic_sku_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '基础产品编号',
                                      `csku_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '客户货号',
                                      `own_brand_flag` int NOT NULL DEFAULT '0' COMMENT '自营产品标记',
                                      `cust_pro_flag` int NOT NULL DEFAULT '0' COMMENT '客户产品标记',
                                      `bulk_handling_volume` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '散货',
                                      `forty_foot_container_num` int NOT NULL DEFAULT '0' COMMENT '40尺高柜',
                                      `forty_foot_cabinet_num` int NOT NULL DEFAULT '0' COMMENT '40尺柜',
                                      `twenty_foot_cabinet_num` int NOT NULL DEFAULT '0' COMMENT '20尺柜',
                                      `profit_rate` decimal(19,6) unsigned DEFAULT NULL COMMENT '佣金比例',
                                      `sms_quotation_id` bigint DEFAULT NULL COMMENT '报价单id',
                                      `sku_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '产品编号',
                                      `main_picture` json NOT NULL DEFAULT (json_object()) COMMENT '图片',
                                      `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '中文名称',
                                      `name_eng` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '英文名称',
                                      `quotation` json NOT NULL DEFAULT (json_object()) COMMENT '报价',
                                      `moq` int NOT NULL DEFAULT '0' COMMENT '起订量',
                                      `vender_id` bigint unsigned NOT NULL DEFAULT '0' COMMENT '供应商id',
                                      `vender_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '供应商编号',
                                      `vender_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '供应商名称',
                                      `with_tax_price` json NOT NULL DEFAULT (json_object()) COMMENT '产品含税单价',
                                      `spec` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '产品规格',
                                      `package_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '包装方式',
                                      `qty_per_innerbox` int DEFAULT '0' COMMENT '内箱装量',
                                      `qty_per_outerbox` int DEFAULT '0' COMMENT '外箱装量',
                                      `box_count` int NOT NULL DEFAULT '0' COMMENT '箱数',
                                      `unit_per_outerbox` int NOT NULL DEFAULT '0' COMMENT '外箱单位',
                                      `outerbox_length` decimal(19,6) unsigned DEFAULT NULL COMMENT '外箱长度',
                                      `outerbox_width` decimal(19,6) unsigned DEFAULT NULL COMMENT '外箱宽度',
                                      `outerbox_height` decimal(19,6) unsigned DEFAULT NULL COMMENT '外箱高度',
                                      `outerbox_volume` decimal(19,6) unsigned DEFAULT NULL COMMENT '外箱体积',
                                      `outerbox_netweight` json NOT NULL DEFAULT (json_object()) COMMENT '外箱净重',
                                      `outerbox_grossweight` json NOT NULL DEFAULT (json_object()) COMMENT '外箱毛重',
                                      `description` varchar(5000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '产品描述',
                                      `description_eng` varchar(5000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '产品英文描述',
                                      `hs_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT 'HS编码',
                                      `quote_date` datetime DEFAULT NULL COMMENT '交货日期',
                                      `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                      `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                      `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                      `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                      `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除  0：有效 1：删除',
                                      PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=74 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='报价单子表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sms_sale_auxiliary_allocation`
--

DROP TABLE IF EXISTS `sms_sale_auxiliary_allocation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sms_sale_auxiliary_allocation` (
                                                 `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                                 `sale_contract_id` bigint DEFAULT NULL COMMENT '销售合同主键',
                                                 `sale_contract_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '销售合同编号',
                                                 `sale_contract_item_id` bigint DEFAULT NULL COMMENT '销售合同明细主键',
                                                 `sku_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '产品编号',
                                                 `sku_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '产品名称',
                                                 `csku_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '客户货号',
                                                 `auxiliary_purchase_contract_id` bigint DEFAULT NULL COMMENT '辅料采购合同主键',
                                                 `auxiliary_purchase_contract_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '辅料采购合同编号',
                                                 `auxiliary_purchase_contract_item_id` bigint DEFAULT NULL COMMENT '辅料采购合同明细主键',
                                                 `auxiliary_sku_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '辅料产品编号',
                                                 `auxiliary_sku_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '辅料产品名称',
                                                 `quantity` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '采购数量',
                                                 `allocation_amount` json NOT NULL DEFAULT (json_object()) COMMENT '分摊金额',
                                                 `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                                 `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                                 `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                                 `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除',
                                                 `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                                 PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='销售合同辅料分摊表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sms_sale_contract`
--

DROP TABLE IF EXISTS `sms_sale_contract`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sms_sale_contract` (
                                     `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                     `total_amount_this_currency` json NOT NULL DEFAULT (json_object()) COMMENT '原币种金额',
                                     `remark` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
                                     `manager` json NOT NULL DEFAULT (json_object()) COMMENT '跟单员',
                                     `collection_total` json NOT NULL DEFAULT (json_object()) COMMENT '收款合计',
                                     `total_stock_cost` json NOT NULL DEFAULT (json_object()) COMMENT '库存成本合计',
                                     `sync_code` bigint NOT NULL DEFAULT '0' COMMENT '同步标记',
                                     `sale_contract_date` datetime DEFAULT NULL,
                                     `usd_rate` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '美元汇率',
                                     `purchase_user_list` json NOT NULL DEFAULT (json_array()) COMMENT '采购员',
                                     `gen_contract_unique_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '内部生成编号',
                                     `total_amount_usd` json NOT NULL DEFAULT (json_object()) COMMENT '销售总金额USD',
                                     `total_amount` json NOT NULL DEFAULT (json_object()) COMMENT '销售总金额',
                                     `print_times` int NOT NULL DEFAULT '0' COMMENT '打印次数',
                                     `print_flag` tinyint NOT NULL DEFAULT '0' COMMENT '打印状态',
                                     `delivery_address` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '送货地址',
                                     `source_contract_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '来源销售合同编号',
                                     `source_contract_id` bigint DEFAULT NULL COMMENT '来源销售合同id',
                                     `collection_account_bank_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '收款账号',
                                     `collection_account_id` bigint DEFAULT NULL COMMENT '收款账号id',
                                     `exchange_rate` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '创建时汇率',
                                     `sign_back_desc` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '回签描述',
                                     `sign_back_flag` int NOT NULL DEFAULT '0' COMMENT '回签标记',
                                     `sign_back_annex` json NOT NULL DEFAULT (json_array()) COMMENT '回签附件',
                                     `design_draft_list` json NOT NULL DEFAULT (json_array()) COMMENT '设计稿',
                                     `link_code_list` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '订单链路编号',
                                     `internal_cust_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '内部客户名称',
                                     `internal_cust_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '内部客户编号',
                                     `internal_cust_Id` bigint DEFAULT NULL COMMENT '内部客户主键',
                                     `auto_flag` tinyint DEFAULT '0' COMMENT '自动生成标识 0-否 1-是',
                                     `source_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '来源编号',
                                     `change_status` tinyint NOT NULL DEFAULT '1' COMMENT '变更状态',
                                     `confirm_flag` tinyint NOT NULL DEFAULT '1' COMMENT '确认状态',
                                     `status` tinyint DEFAULT NULL COMMENT '状态',
                                     `sign_back_date` datetime DEFAULT NULL COMMENT '回签日期',
                                     `sign_back_user` json DEFAULT NULL COMMENT '回签人',
                                     `code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '编号',
                                     `company_id` bigint DEFAULT NULL COMMENT '内部法人单位主键',
                                     `company_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '内部法人单位名称',
                                     `company_path` json NOT NULL DEFAULT (json_object()) COMMENT '订单路径',
                                     `cust_id` bigint DEFAULT NULL COMMENT '客户主键',
                                     `cust_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '客户编号',
                                     `cust_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                                     `currency` varchar(3) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '交易币别',
                                     `settlement_term_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '价格条款',
                                     `settlement_id` bigint DEFAULT NULL COMMENT '收款方式主键',
                                     `settlement_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '收款方式名称',
                                     `cust_country_id` bigint DEFAULT NULL COMMENT '客户国别主键',
                                     `cust_country_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '客户国别名称',
                                     `cust_area_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '客户区域名称',
                                     `cust_po` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '客户PO号',
                                     `agent_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否代理',
                                     `collected_cust_id` bigint DEFAULT NULL COMMENT '应收客户主键',
                                     `collected_cust_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '应收客户编号',
                                     `collected_cust_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                                     `receive_cust_id` bigint DEFAULT NULL COMMENT '收货客户主键',
                                     `receive_cust_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '收货客户编号',
                                     `receive_cust_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                                     `sales` json NOT NULL DEFAULT (json_object()) COMMENT '销售人员',
                                     `annex` json DEFAULT NULL COMMENT '附件',
                                     `input_date` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '录入日期',
                                     `trade_country_id` bigint DEFAULT NULL COMMENT '贸易国别主键',
                                     `trade_country_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '贸易国别名称',
                                     `trade_country_area` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '贸易国别区域',
                                     `departure_country_id` bigint DEFAULT NULL COMMENT '出运国主键',
                                     `departure_country_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '出运国名称',
                                     `departure_country_area` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '出运国区域',
                                     `departure_port_id` bigint DEFAULT NULL COMMENT '出运口岸主键',
                                     `departure_port_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '出运口岸名称',
                                     `destination_port_id` bigint DEFAULT NULL COMMENT '目的口岸主键',
                                     `destination_port_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '目的口岸名称',
                                     `transport_type` tinyint DEFAULT NULL COMMENT '运输方式',
                                     `cust_delivery_date` datetime DEFAULT NULL COMMENT '客户交期',
                                     `twenty_foot_cabinet_num` int NOT NULL DEFAULT '0' COMMENT '20尺柜',
                                     `forty_foot_cabinet_num` int NOT NULL DEFAULT '0' COMMENT '40尺柜',
                                     `forty_foot_container_num` int NOT NULL DEFAULT '0' COMMENT '40尺高柜',
                                     `bulk_handling_volume` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '散货',
                                     `trailer_fee` json NOT NULL DEFAULT (json_object()) COMMENT '拖柜费',
                                     `estimated_total_freight` json NOT NULL DEFAULT (json_object()) COMMENT '预估总运费',
                                     `booking_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否订舱',
                                     `commission` json NOT NULL DEFAULT (json_object()) COMMENT '佣金',
                                     `platform_fee` json NOT NULL DEFAULT (json_object()) COMMENT '平台费',
                                     `insurance_fee` json NOT NULL DEFAULT (json_object()) COMMENT '保险费',
                                     `sinosure_fee` json NOT NULL DEFAULT (json_object()) COMMENT '中信保费用',
                                     `lump_sum_fee` json NOT NULL DEFAULT (json_object()) COMMENT '包干费',
                                     `addition_amount` json NOT NULL DEFAULT (json_object()) COMMENT '加项金额',
                                     `deduction_amount` json NOT NULL DEFAULT (json_object()) COMMENT '减项金额',
                                     `inspection_fee` json NOT NULL DEFAULT (json_object()) COMMENT '验货费用',
                                     `estimated_packing_materials` json NOT NULL DEFAULT (json_object()) COMMENT '预计包材合计',
                                     `accessories_purchase_total` json NOT NULL DEFAULT (json_object()) COMMENT '配件采购合计',
                                     `total_boxes` int NOT NULL DEFAULT '0' COMMENT '箱数合计',
                                     `total_grossweight` json NOT NULL DEFAULT (json_object()) COMMENT '毛重合计',
                                     `total_weight` json NOT NULL DEFAULT (json_object()) COMMENT '净重合计',
                                     `total_volume` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '体积合计',
                                     `total_goods_value` json NOT NULL DEFAULT (json_object()) COMMENT '货值合计',
                                     `total_purchase` json NOT NULL DEFAULT (json_array()) COMMENT '采购总金额',
                                     `total_vat_refund` json NOT NULL DEFAULT (json_object()) COMMENT '退税合计',
                                     `total_quantity` int NOT NULL DEFAULT '0' COMMENT '数量合计',
                                     `order_gross_profit` json NOT NULL DEFAULT (json_object()) COMMENT '订单毛利',
                                     `gross_profit_margin` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '毛利率',
                                     `receivable_exchange` json NOT NULL DEFAULT (json_object()) COMMENT '应收汇款',
                                     `sale_type` tinyint NOT NULL DEFAULT '0' COMMENT '销售合同类型',
                                     `audit_status` tinyint NOT NULL DEFAULT '0' COMMENT '审核状态',
                                     `process_instance_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '流程实例的编号',
                                     `convert_purchase_flag` int NOT NULL DEFAULT '0' COMMENT '转采购计划标记',
                                     `convert_purchase_time` datetime DEFAULT NULL COMMENT '转采购计划时间',
                                     `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                     `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                     `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                     `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除',
                                     `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                     PRIMARY KEY (`id`) USING BTREE,
                                     KEY `index_code` (`code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=13878 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='销售合同';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sms_sale_contract_change`
--

DROP TABLE IF EXISTS `sms_sale_contract_change`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sms_sale_contract_change` (
                                            `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                            `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '备注',
                                            `total_amount_this_currency` json NOT NULL DEFAULT (json_object()) COMMENT '原币种金额',
                                            `manager` json NOT NULL DEFAULT (json_object()) COMMENT '跟单员',
                                            `usd_rate` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '美元汇率',
                                            `total_amount` json NOT NULL DEFAULT (json_object()) COMMENT '销售总额',
                                            `total_amount_usd` json NOT NULL DEFAULT (json_object()) COMMENT '销售总额美金',
                                            `un_received_amount` json NOT NULL DEFAULT (json_object()) COMMENT '剩余未收金额',
                                            `collection_total` json NOT NULL DEFAULT (json_object()) COMMENT '收款合计',
                                            `sale_contract_date` datetime DEFAULT NULL COMMENT '销售合同日日期',
                                            `sign_back_date` datetime DEFAULT NULL COMMENT '回签日期',
                                            `cancel_shipment_plan_item_id_list` json DEFAULT NULL COMMENT '作废出运计划明细id列表',
                                            `cancel_purchase_plan_item_id_list` json DEFAULT NULL COMMENT '作废采购计划明细id列表',
                                            `design_draft_list` json NOT NULL DEFAULT (json_array()) COMMENT '设计稿',
                                            `status` tinyint DEFAULT NULL COMMENT '状态',
                                            `exchange_rate` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '创建时汇率',
                                            `source_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '来源编号',
                                            `old_data` json NOT NULL DEFAULT (json_object()) COMMENT '旧数据',
                                            `code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '编号',
                                            `contract_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '销售合同编号',
                                            `company_id` bigint DEFAULT NULL COMMENT '内部法人单位主键',
                                            `company_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '内部法人单位名称',
                                            `order_path` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '订单路径',
                                            `cust_id` bigint DEFAULT NULL COMMENT '客户主键',
                                            `cust_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '客户编号',
                                            `cust_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '客户名称',
                                            `currency` varchar(3) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '交易币别',
                                            `settlement_term_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '价格条款',
                                            `settlement_id` bigint DEFAULT NULL COMMENT '收款方式主键',
                                            `settlement_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '收款方式名称',
                                            `cust_country_id` bigint DEFAULT NULL COMMENT '客户国别主键',
                                            `cust_country_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '客户国别名称',
                                            `cust_area_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '客户区域名称',
                                            `cust_po` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '客户PO号',
                                            `agent_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否代理',
                                            `collected_cust_id` bigint DEFAULT NULL COMMENT '应收客户主键',
                                            `collected_cust_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '应收客户编号',
                                            `collected_cust_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '应收客户名称',
                                            `receive_cust_id` bigint DEFAULT NULL COMMENT '收货客户主键',
                                            `receive_cust_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '收货客户编号',
                                            `receive_cust_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '收货客户名称',
                                            `sales` json NOT NULL DEFAULT (json_object()) COMMENT '销售人员',
                                            `annex` json DEFAULT NULL COMMENT '附件',
                                            `input_date` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '录入日期',
                                            `trade_country_id` bigint DEFAULT NULL COMMENT '贸易国别主键',
                                            `trade_country_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '贸易国别名称',
                                            `trade_country_area` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '贸易国别区域',
                                            `departure_country_id` bigint DEFAULT NULL COMMENT '出运国主键',
                                            `departure_country_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '出运国名称',
                                            `departure_country_area` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '出运国区域',
                                            `departure_port_id` bigint DEFAULT NULL COMMENT '出运口岸主键',
                                            `departure_port_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '出运口岸名称',
                                            `destination_port_id` bigint DEFAULT NULL COMMENT '目的口岸主键',
                                            `destination_port_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '目的口岸名称',
                                            `transport_type` tinyint DEFAULT NULL COMMENT '运输方式',
                                            `cust_delivery_date` datetime DEFAULT NULL COMMENT '客户交期',
                                            `twenty_foot_cabinet_num` int NOT NULL DEFAULT '0' COMMENT '20尺柜',
                                            `forty_foot_cabinet_num` int NOT NULL DEFAULT '0' COMMENT '40尺柜',
                                            `forty_foot_container_num` int NOT NULL DEFAULT '0' COMMENT '40尺高柜',
                                            `bulk_handling_volume` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '散货',
                                            `trailer_fee` json NOT NULL DEFAULT (json_object()) COMMENT '拖柜费',
                                            `estimated_total_freight` json NOT NULL DEFAULT (json_object()) COMMENT '预估总运费',
                                            `booking_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否订舱',
                                            `commission` json NOT NULL DEFAULT (json_object()) COMMENT '佣金',
                                            `platform_fee` json NOT NULL DEFAULT (json_object()) COMMENT '平台费',
                                            `insurance_fee` json NOT NULL DEFAULT (json_object()) COMMENT '保险费',
                                            `sinosure_fee` json NOT NULL DEFAULT (json_object()) COMMENT '中信保费用',
                                            `lump_sum_fee` json NOT NULL DEFAULT (json_object()) COMMENT '包干费',
                                            `addition_amount` json NOT NULL DEFAULT (json_object()) COMMENT '加项金额',
                                            `deduction_amount` json NOT NULL DEFAULT (json_object()) COMMENT '减项金额',
                                            `inspection_fee` json NOT NULL DEFAULT (json_object()) COMMENT '验货费用',
                                            `estimated_packing_materials` json NOT NULL DEFAULT (json_object()) COMMENT '预计包材合计',
                                            `accessories_purchase_total` json NOT NULL DEFAULT (json_object()) COMMENT '配件采购合计',
                                            `total_boxes` int NOT NULL DEFAULT '0' COMMENT '箱数合计',
                                            `total_grossweight` json NOT NULL DEFAULT (json_object()) COMMENT '毛重合计',
                                            `total_weight` json NOT NULL DEFAULT (json_object()) COMMENT '净重合计',
                                            `total_volume` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '体积合计',
                                            `total_goods_value` json NOT NULL DEFAULT (json_object()) COMMENT '货值合计',
                                            `total_purchase` json NOT NULL DEFAULT (json_object()) COMMENT '采购总金额',
                                            `total_vat_refund` json NOT NULL DEFAULT (json_object()) COMMENT '退税合计',
                                            `total_quantity` int NOT NULL DEFAULT '0' COMMENT '数量合计',
                                            `order_gross_profit` json NOT NULL DEFAULT (json_object()) COMMENT '订单毛利',
                                            `gross_profit_margin` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '毛利率',
                                            `receivable_exchange` json NOT NULL DEFAULT (json_object()) COMMENT '应收汇款',
                                            `sale_type` tinyint DEFAULT NULL COMMENT '销售合同类型',
                                            `audit_status` tinyint NOT NULL DEFAULT '0' COMMENT '审核状态',
                                            `confirm_flag` tinyint NOT NULL DEFAULT '0' COMMENT '确认状态',
                                            `process_instance_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '流程实例的编号',
                                            `children` json NOT NULL DEFAULT (json_array()) COMMENT '销售明细',
                                            `add_sub_item_list` json NOT NULL DEFAULT (json_array()) COMMENT '加减项',
                                            `collection_plan_list` json NOT NULL DEFAULT (json_array()) COMMENT '收款计划',
                                            `create_user` json NOT NULL DEFAULT (json_object()) COMMENT '创建人',
                                            `model_key` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '流程模型标识',
                                            `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                            `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                            `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                            `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除',
                                            `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                            `company_path` json NOT NULL DEFAULT (json_object()) COMMENT '公司路径',
                                            `effect_range_list` json NOT NULL DEFAULT (json_array()) COMMENT '影响范围',
                                            `total_stock_cost` json NOT NULL COMMENT '库存成本合计',
                                            PRIMARY KEY (`id`) USING BTREE,
                                            KEY `audit_status` (`audit_status`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=898 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='销售合同变更表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sms_sale_contract_copy1`
--

DROP TABLE IF EXISTS `sms_sale_contract_copy1`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sms_sale_contract_copy1` (
                                           `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                           `total_amount_this_currency` json NOT NULL DEFAULT (json_object()) COMMENT '原币种金额',
                                           `remark` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
                                           `manager` json NOT NULL DEFAULT (json_object()) COMMENT '跟单员',
                                           `collection_total` json NOT NULL DEFAULT (json_object()) COMMENT '收款合计',
                                           `total_stock_cost` json NOT NULL DEFAULT (json_object()) COMMENT '库存成本合计',
                                           `sync_code` bigint NOT NULL DEFAULT '0' COMMENT '同步标记',
                                           `sale_contract_date` datetime DEFAULT NULL,
                                           `usd_rate` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '美元汇率',
                                           `purchase_user_list` json NOT NULL DEFAULT (json_array()) COMMENT '采购员',
                                           `gen_contract_unique_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '内部生成编号',
                                           `total_amount_usd` json NOT NULL DEFAULT (json_object()) COMMENT '销售总金额USD',
                                           `total_amount` json NOT NULL DEFAULT (json_object()) COMMENT '销售总金额',
                                           `print_times` int NOT NULL DEFAULT '0' COMMENT '打印次数',
                                           `print_flag` tinyint NOT NULL DEFAULT '0' COMMENT '打印状态',
                                           `delivery_address` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '送货地址',
                                           `source_contract_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '来源销售合同编号',
                                           `source_contract_id` bigint DEFAULT NULL COMMENT '来源销售合同id',
                                           `collection_account_bank_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '收款账号',
                                           `collection_account_id` bigint DEFAULT NULL COMMENT '收款账号id',
                                           `exchange_rate` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '创建时汇率',
                                           `sign_back_desc` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '回签描述',
                                           `sign_back_flag` int NOT NULL DEFAULT '0' COMMENT '回签标记',
                                           `sign_back_annex` json NOT NULL DEFAULT (json_array()) COMMENT '回签附件',
                                           `design_draft_list` json NOT NULL DEFAULT (json_array()) COMMENT '设计稿',
                                           `link_code_list` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '订单链路编号',
                                           `internal_cust_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '内部客户名称',
                                           `internal_cust_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '内部客户编号',
                                           `internal_cust_Id` bigint DEFAULT NULL COMMENT '内部客户主键',
                                           `auto_flag` tinyint DEFAULT '0' COMMENT '自动生成标识 0-否 1-是',
                                           `source_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '来源编号',
                                           `change_status` tinyint NOT NULL DEFAULT '1' COMMENT '变更状态',
                                           `confirm_flag` tinyint NOT NULL DEFAULT '1' COMMENT '确认状态',
                                           `status` tinyint DEFAULT NULL COMMENT '状态',
                                           `sign_back_date` datetime DEFAULT NULL COMMENT '回签日期',
                                           `sign_back_user` json DEFAULT NULL COMMENT '回签人',
                                           `code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '编号',
                                           `company_id` bigint DEFAULT NULL COMMENT '内部法人单位主键',
                                           `company_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '内部法人单位名称',
                                           `company_path` json NOT NULL DEFAULT (json_object()) COMMENT '订单路径',
                                           `cust_id` bigint DEFAULT NULL COMMENT '客户主键',
                                           `cust_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '客户编号',
                                           `cust_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                                           `currency` varchar(3) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '交易币别',
                                           `settlement_term_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '价格条款',
                                           `settlement_id` bigint DEFAULT NULL COMMENT '收款方式主键',
                                           `settlement_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '收款方式名称',
                                           `cust_country_id` bigint DEFAULT NULL COMMENT '客户国别主键',
                                           `cust_country_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '客户国别名称',
                                           `cust_area_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '客户区域名称',
                                           `cust_po` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '客户PO号',
                                           `agent_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否代理',
                                           `collected_cust_id` bigint DEFAULT NULL COMMENT '应收客户主键',
                                           `collected_cust_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '应收客户编号',
                                           `collected_cust_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                                           `receive_cust_id` bigint DEFAULT NULL COMMENT '收货客户主键',
                                           `receive_cust_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '收货客户编号',
                                           `receive_cust_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                                           `sales` json NOT NULL DEFAULT (json_object()) COMMENT '销售人员',
                                           `annex` json DEFAULT NULL COMMENT '附件',
                                           `input_date` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '录入日期',
                                           `trade_country_id` bigint DEFAULT NULL COMMENT '贸易国别主键',
                                           `trade_country_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '贸易国别名称',
                                           `trade_country_area` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '贸易国别区域',
                                           `departure_country_id` bigint DEFAULT NULL COMMENT '出运国主键',
                                           `departure_country_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '出运国名称',
                                           `departure_country_area` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '出运国区域',
                                           `departure_port_id` bigint DEFAULT NULL COMMENT '出运口岸主键',
                                           `departure_port_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '出运口岸名称',
                                           `destination_port_id` bigint DEFAULT NULL COMMENT '目的口岸主键',
                                           `destination_port_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '目的口岸名称',
                                           `transport_type` tinyint DEFAULT NULL COMMENT '运输方式',
                                           `cust_delivery_date` datetime DEFAULT NULL COMMENT '客户交期',
                                           `twenty_foot_cabinet_num` int NOT NULL DEFAULT '0' COMMENT '20尺柜',
                                           `forty_foot_cabinet_num` int NOT NULL DEFAULT '0' COMMENT '40尺柜',
                                           `forty_foot_container_num` int NOT NULL DEFAULT '0' COMMENT '40尺高柜',
                                           `bulk_handling_volume` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '散货',
                                           `trailer_fee` json NOT NULL DEFAULT (json_object()) COMMENT '拖柜费',
                                           `estimated_total_freight` json NOT NULL DEFAULT (json_object()) COMMENT '预估总运费',
                                           `booking_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否订舱',
                                           `commission` json NOT NULL DEFAULT (json_object()) COMMENT '佣金',
                                           `platform_fee` json NOT NULL DEFAULT (json_object()) COMMENT '平台费',
                                           `insurance_fee` json NOT NULL DEFAULT (json_object()) COMMENT '保险费',
                                           `sinosure_fee` json NOT NULL DEFAULT (json_object()) COMMENT '中信保费用',
                                           `lump_sum_fee` json NOT NULL DEFAULT (json_object()) COMMENT '包干费',
                                           `addition_amount` json NOT NULL DEFAULT (json_object()) COMMENT '加项金额',
                                           `deduction_amount` json NOT NULL DEFAULT (json_object()) COMMENT '减项金额',
                                           `inspection_fee` json NOT NULL DEFAULT (json_object()) COMMENT '验货费用',
                                           `estimated_packing_materials` json NOT NULL DEFAULT (json_object()) COMMENT '预计包材合计',
                                           `accessories_purchase_total` json NOT NULL DEFAULT (json_object()) COMMENT '配件采购合计',
                                           `total_boxes` int NOT NULL DEFAULT '0' COMMENT '箱数合计',
                                           `total_grossweight` json NOT NULL DEFAULT (json_object()) COMMENT '毛重合计',
                                           `total_weight` json NOT NULL DEFAULT (json_object()) COMMENT '净重合计',
                                           `total_volume` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '体积合计',
                                           `total_goods_value` json NOT NULL DEFAULT (json_object()) COMMENT '货值合计',
                                           `total_purchase` json NOT NULL DEFAULT (json_array()) COMMENT '采购总金额',
                                           `total_vat_refund` json NOT NULL DEFAULT (json_object()) COMMENT '退税合计',
                                           `total_quantity` int NOT NULL DEFAULT '0' COMMENT '数量合计',
                                           `order_gross_profit` json NOT NULL DEFAULT (json_object()) COMMENT '订单毛利',
                                           `gross_profit_margin` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '毛利率',
                                           `receivable_exchange` json NOT NULL DEFAULT (json_object()) COMMENT '应收汇款',
                                           `sale_type` tinyint NOT NULL DEFAULT '0' COMMENT '销售合同类型',
                                           `audit_status` tinyint NOT NULL DEFAULT '0' COMMENT '审核状态',
                                           `process_instance_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '流程实例的编号',
                                           `convert_purchase_flag` int NOT NULL DEFAULT '0' COMMENT '转采购计划标记',
                                           `convert_purchase_time` datetime DEFAULT NULL COMMENT '转采购计划时间',
                                           `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                           `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                           `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                           `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除',
                                           `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                           PRIMARY KEY (`id`) USING BTREE,
                                           KEY `index_code` (`code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=13559 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='销售合同';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sms_sale_contract_item`
--

DROP TABLE IF EXISTS `sms_sale_contract_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sms_sale_contract_item` (
                                          `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                          `total_sale_amount_usd` json NOT NULL DEFAULT (json_object()) COMMENT '美元总金额',
                                          `barcode` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '条形码',
                                          `osku_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '自营货号',
                                          `sync_code` bigint NOT NULL DEFAULT '0' COMMENT '同步标记',
                                          `with_tax_price_remove_free` json NOT NULL DEFAULT (json_object()) COMMENT '不包含赠品金额',
                                          `purchase_free_quantity` int NOT NULL DEFAULT '0' COMMENT '采购赠品数量',
                                          `free_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否包含赠品',
                                          `split_box_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否分箱',
                                          `specification_list` json NOT NULL DEFAULT (json_array()) COMMENT '规格',
                                          `conver_notice_flag` tinyint NOT NULL DEFAULT '0' COMMENT '转出库通知标识',
                                          `basic_sku_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '基础产品编号',
                                          `stock_lock_save_req_v_o_list` json NOT NULL DEFAULT (json_array()) COMMENT '锁库信息',
                                          `split_purchase_list` json NOT NULL DEFAULT (json_array()) COMMENT '拆分采购信息',
                                          `split_purchase_quantity` int NOT NULL DEFAULT '0' COMMENT '拆分采购数量',
                                          `split_purchase_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否拆分采购',
                                          `split_flag` tinyint NOT NULL DEFAULT '0' COMMENT '拆分标识',
                                          `measure_unit` int DEFAULT NULL COMMENT '计量单位',
                                          `transfer_shipped_quantity` int NOT NULL DEFAULT '0' COMMENT '已转出运数量',
                                          `real_tax_refund_rate` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '真实退税率',
                                          `tax_rate` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '税率',
                                          `real_purchase_quantity` int NOT NULL DEFAULT '0' COMMENT '真实采购数量',
                                          `real_lock_quantity` int NOT NULL DEFAULT '0' COMMENT '真实锁库数量',
                                          `lock_msg` json NOT NULL DEFAULT (json_array()) COMMENT '锁库信息',
                                          `re_lock_flag` int NOT NULL DEFAULT '0' COMMENT '重新分配库存标记',
                                          `commission_sub_total` tinyint DEFAULT NULL COMMENT '佣金是否扣减总金额1:是0：否',
                                          `convert_purchase_flag` tinyint NOT NULL DEFAULT '0' COMMENT '转采购标识',
                                          `purchase_shipping_price` json NOT NULL DEFAULT (json_object()) COMMENT '运费',
                                          `shipment_total_quantity` int NOT NULL DEFAULT '0' COMMENT '已转出运明细总数',
                                          `manager` json NOT NULL DEFAULT (json_object()) COMMENT '跟单员',
                                          `real_purchase_with_tax_price` json NOT NULL DEFAULT (json_object()) COMMENT '真实采购单价',
                                          `hs_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '海关编码',
                                          `convert_shipment_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '转出运标识：0-否 1-是',
                                          `bill_quantity` int NOT NULL DEFAULT '0' COMMENT '入库数量',
                                          `abnormal_remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '异常说明',
                                          `abnormal_status` tinyint DEFAULT NULL COMMENT '异常状态',
                                          `bill_status` tinyint NOT NULL DEFAULT '1' COMMENT '入库状态',
                                          `hs_measure_unit` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '海关计量单位',
                                          `source_unique_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '来源编号',
                                          `unique_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '唯一编号',
                                          `need_pur_quantity` int DEFAULT NULL COMMENT '待采购数量',
                                          `current_lock_quantity` int DEFAULT NULL COMMENT '锁定库存',
                                          `unpush_shipment_quantity` int NOT NULL DEFAULT '0' COMMENT '未下推出运数',
                                          `push_shipment_quantity` int NOT NULL DEFAULT '0' COMMENT '已下推出运数',
                                          `vender_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '供应商编号',
                                          `vender_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '供应商名称',
                                          `vender_id` bigint unsigned NOT NULL DEFAULT '0' COMMENT '供应商id',
                                          `shipped_quantity` int DEFAULT NULL COMMENT '已出运数',
                                          `commodity_inspection_flag` tinyint DEFAULT NULL COMMENT '是否商检',
                                          `sort_num` int NOT NULL DEFAULT '0' COMMENT '序号',
                                          `contract_id` bigint DEFAULT NULL COMMENT '合同id',
                                          `sku_id` bigint DEFAULT NULL COMMENT '产品主键',
                                          `sku_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT 'SKU编号',
                                          `contract_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '合同编号',
                                          `main_picture` json NOT NULL DEFAULT (json_object()) COMMENT '图片',
                                          `thumbnail` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '缩略图',
                                          `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '中文名称',
                                          `name_eng` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '英文名称',
                                          `csku_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '客户货号',
                                          `quantity` int NOT NULL DEFAULT '0' COMMENT '数量',
                                          `unit_price` json NOT NULL DEFAULT (json_object()) COMMENT '销售单价',
                                          `total_sale_amount` json NOT NULL DEFAULT (json_object()) COMMENT '外销总金额',
                                          `purchase_packaging_price` json NOT NULL DEFAULT (json_object()) COMMENT '包装价',
                                          `purchase_unit_price` json NOT NULL DEFAULT (json_object()) COMMENT '采购单价',
                                          `purchase_total_price` json NOT NULL DEFAULT (json_object()) COMMENT '总价',
                                          `purchase_with_tax_price` json NOT NULL DEFAULT (json_object()) COMMENT '含税总价',
                                          `purchase_currency` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '采购币种',
                                          `commission_type` tinyint DEFAULT NULL COMMENT '佣金类型',
                                          `commission_rate` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '佣金比例',
                                          `commission_amount` json NOT NULL DEFAULT (json_object()) COMMENT '佣金金额',
                                          `inventory_quantity` int NOT NULL DEFAULT '0' COMMENT '库存',
                                          `unit` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '单位',
                                          `description` varchar(5000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '产品描述',
                                          `description_eng` varchar(5000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '产品描述',
                                          `order_gross_profit` json NOT NULL DEFAULT (json_object()) COMMENT '订单毛利',
                                          `order_gross_profit_rate` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '毛利率',
                                          `vender_delivery_date` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '工厂交期',
                                          `qty_per_outerbox` int NOT NULL DEFAULT '0' COMMENT '外箱装量',
                                          `qty_per_innerbox` int NOT NULL DEFAULT '0' COMMENT '内盒装量',
                                          `box_count` int NOT NULL DEFAULT '0' COMMENT '箱数',
                                          `outerbox_length` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '外箱长度',
                                          `outerbox_width` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '外箱宽度',
                                          `outerbox_height` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '外箱高度',
                                          `outerbox_volume` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '外箱体积',
                                          `volume` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '体积',
                                          `outerbox_netweight` json NOT NULL DEFAULT (json_object()) COMMENT '外箱净重',
                                          `outerbox_grossweight` json NOT NULL DEFAULT (json_object()) COMMENT '外箱毛重',
                                          `reorder_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否翻单',
                                          `package_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '包装方式',
                                          `tax_refund_rate` decimal(19,6) DEFAULT NULL COMMENT '退税率',
                                          `tax_refund_price` json NOT NULL DEFAULT (json_object()) COMMENT '退税金额',
                                          `shipped_item_quantity` int DEFAULT NULL COMMENT '已出货数',
                                          `unshipped_item_quantity` int DEFAULT NULL COMMENT '未发货数',
                                          `inspection_fee` json NOT NULL DEFAULT (json_object()) COMMENT '验货费用',
                                          `fund_occupancy_fee` json NOT NULL DEFAULT (json_object()) COMMENT '资金占用费',
                                          `trailer_fee` json NOT NULL DEFAULT (json_object()) COMMENT '拖柜费',
                                          `booking_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否订舱',
                                          `insurance_fee` json NOT NULL DEFAULT (json_object()) COMMENT '保险费',
                                          `platform_fee` json NOT NULL DEFAULT (json_object()) COMMENT '平台费',
                                          `forecast_total_cost` json NOT NULL DEFAULT (json_object()) COMMENT '预估总费用',
                                          `inner_calc_price` json NOT NULL DEFAULT (json_object()) COMMENT '内部核算单价',
                                          `sinosure_fee` json NOT NULL DEFAULT (json_object()) COMMENT '中信保费用',
                                          `status` tinyint DEFAULT NULL COMMENT '状态',
                                          `purchase_user` json NOT NULL DEFAULT (json_object()) COMMENT '采购员',
                                          `own_brand_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否自主品牌',
                                          `cust_pro_flag` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '客户产品标识',
                                          `sku_type` tinyint unsigned DEFAULT NULL COMMENT '产品类型',
                                          `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                          `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                          `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                          `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除',
                                          `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                          PRIMARY KEY (`id`) USING BTREE,
                                          KEY `item_contract_id` (`contract_code`) USING BTREE,
                                          KEY `idx_contract_id` (`contract_id`)
) ENGINE=InnoDB AUTO_INCREMENT=585501 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='销售合同明细';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sync_code`
--

DROP TABLE IF EXISTS `sync_code`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sync_code` (
                             `y_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                             `e_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                             `repeat_flag` tinyint NOT NULL DEFAULT '0',
                             `completed` tinyint NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `system_card`
--

DROP TABLE IF EXISTS `system_card`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_card` (
                               `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                               `basic_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否基础卡片',
                               `title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '标题',
                               `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '描述',
                               `type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '类型',
                               `current_component` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '组件',
                               `title_flag` tinyint(1) DEFAULT NULL COMMENT '标识',
                               `picture` json NOT NULL DEFAULT (json_object()) COMMENT '图片',
                               `status` tinyint NOT NULL DEFAULT '1' COMMENT '是否启用',
                               `unique_code` int NOT NULL COMMENT '索引',
                               `tab_id` bigint unsigned DEFAULT NULL COMMENT '页签id',
                               `x` int unsigned DEFAULT NULL COMMENT 'x轴',
                               `y` int unsigned DEFAULT NULL COMMENT 'y轴',
                               `h` int unsigned DEFAULT NULL COMMENT '高度',
                               `w` int unsigned DEFAULT NULL COMMENT '宽度',
                               `filter_condition` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '查询条件',
                               `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                               `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                               `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                               `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除  0：有效 1：删除',
                               PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=121 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='首页卡片表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `system_collection_plan`
--

DROP TABLE IF EXISTS `system_collection_plan`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_collection_plan` (
                                          `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                          `control_shipment_flag` int NOT NULL DEFAULT '0' COMMENT '是否控制出运 ',
                                          `payment_method` tinyint DEFAULT NULL COMMENT '支付方式',
                                          `settlement_id` bigint DEFAULT NULL COMMENT '收款方式主键',
                                          `step` tinyint DEFAULT NULL COMMENT '步骤',
                                          `date_type` tinyint DEFAULT NULL COMMENT '起始点',
                                          `days` int NOT NULL DEFAULT '0' COMMENT '天数',
                                          `collection_ratio` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '收款比例',
                                          `control_purchase_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否控制采购',
                                          `exe_status` tinyint NOT NULL DEFAULT '0' COMMENT '状态',
                                          `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                          `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                          `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                          `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除',
                                          `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                          PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=137 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='收款计划';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `system_company`
--

DROP TABLE IF EXISTS `system_company`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_company` (
                                  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                                  `tax_numb` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '税号',
                                  `customs_number` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '海关编号',
                                  `available_currency_list` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '可用币种',
                                  `is_inner_customer` tinyint NOT NULL DEFAULT '0' COMMENT '是否内部客户',
                                  `picture` json NOT NULL DEFAULT (json_array()) COMMENT '公章图片',
                                  `shortname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '简称',
                                  `enable_flag` tinyint NOT NULL DEFAULT '1' COMMENT '启用标识',
                                  `legal_person` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '法人',
                                  `company_fax` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '企业传真',
                                  `company_tel` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '企业电话',
                                  `manager_mail` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '管理员邮箱',
                                  `manager_mobile` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '管理员手机号',
                                  `manager` json NOT NULL DEFAULT (json_object()) COMMENT '管理员',
                                  `company_address` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '企业地址',
                                  `company_address_eng` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '企业英文地址',
                                  `license_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '营业执照号',
                                  `license` json NOT NULL DEFAULT (json_object()) COMMENT '营业执照',
                                  `company_name_eng` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '企业英文名称',
                                  `company_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '企业名称',
                                  `company_nature` tinyint DEFAULT NULL COMMENT '公司性质',
                                  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '单位名称简称',
                                  `processed_flag` int NOT NULL DEFAULT '0' COMMENT '可加工标记',
                                  `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                  `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除  0：有效 1：删除',
                                  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='内部法人单位表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `system_company_bank`
--

DROP TABLE IF EXISTS `system_company_bank`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_company_bank` (
                                       `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                       `bank_address` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '银行地址',
                                       `company_id` bigint DEFAULT NULL COMMENT '主体主键',
                                       `company_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '企业中文名称',
                                       `company_name_eng` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '企业英文名称',
                                       `bank_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '银行中文名称',
                                       `bank_name_eng` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '银行英文名称',
                                       `bank_address_eng` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '银行英文地址',
                                       `bank_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '银行账号',
                                       `swift_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '银行识别代码',
                                       `default_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否默认账户0-否，1-是',
                                       `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                       `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                       `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                       `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除',
                                       `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                       PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=371 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='公司银行信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `system_country_info`
--

DROP TABLE IF EXISTS `system_country_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_country_info` (
                                       `id` bigint NOT NULL AUTO_INCREMENT COMMENT '国家id',
                                       `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '国家名称',
                                       `code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '国家编码',
                                       `region_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '区域编码',
                                       `region_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '区域名称',
                                       `creator` int DEFAULT NULL COMMENT '创建者',
                                       `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                       `updater` int DEFAULT NULL COMMENT '更新者',
                                       `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                       `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
                                       `area_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                       PRIMARY KEY (`id`) USING BTREE,
                                       KEY `index_id` (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1963 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='国家信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `system_dept`
--

DROP TABLE IF EXISTS `system_dept`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_dept` (
                               `id` bigint NOT NULL AUTO_INCREMENT COMMENT '部门id',
                               `code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '编号',
                               `name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '部门名称',
                               `parent_id` bigint NOT NULL DEFAULT '0' COMMENT '父部门id',
                               `sort` int NOT NULL DEFAULT '0' COMMENT '显示顺序',
                               `leader_user_ids` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '负责人',
                               `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '联系电话',
                               `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '邮箱',
                               `status` tinyint NOT NULL COMMENT '部门状态（0正常 1停用）',
                               `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
                               `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
                               `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                               `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
                               `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
                               PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=166 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='部门表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `system_dict_data`
--

DROP TABLE IF EXISTS `system_dict_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_dict_data` (
                                    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '字典编码',
                                    `sort` int NOT NULL DEFAULT '0' COMMENT '字典排序',
                                    `label` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '字典标签',
                                    `value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '字典键值',
                                    `dict_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '字典类型',
                                    `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
                                    `color_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '颜色类型',
                                    `css_class` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT 'css 样式',
                                    `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
                                    `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
                                    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
                                    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                    `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
                                    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=823 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='字典数据表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `system_dict_type`
--

DROP TABLE IF EXISTS `system_dict_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_dict_type` (
                                    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '字典主键',
                                    `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '字典名称',
                                    `type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '字典类型',
                                    `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
                                    `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
                                    `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
                                    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
                                    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                    `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
                                    PRIMARY KEY (`id`) USING BTREE,
                                    UNIQUE KEY `dict_type` (`type`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=200 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='字典类型表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `system_error_code`
--

DROP TABLE IF EXISTS `system_error_code`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_error_code` (
                                     `id` bigint NOT NULL AUTO_INCREMENT COMMENT '错误码编号',
                                     `type` tinyint NOT NULL DEFAULT '0' COMMENT '错误码类型',
                                     `application_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '应用名',
                                     `code` int NOT NULL DEFAULT '0' COMMENT '错误码编码',
                                     `message` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '错误码错误提示',
                                     `memo` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '备注',
                                     `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
                                     `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                     `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
                                     `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                     `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
                                     PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6039 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='错误码表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `system_field`
--

DROP TABLE IF EXISTS `system_field`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_field` (
                                `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
                                `table_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '表名称',
                                `table_comment` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '表描述',
                                `field_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '字段名称',
                                `field_comment` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '字段注释',
                                `field_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '字段类型',
                                `dict_flag` tinyint DEFAULT NULL COMMENT '是否字典',
                                `dict_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '字典类型',
                                `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除  0：有效 1：删除',
                                PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3916 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='系统字段表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `system_form_change`
--

DROP TABLE IF EXISTS `system_form_change`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_form_change` (
                                      `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                                      `main_instance_flag` tinyint DEFAULT '0' COMMENT '是否参与主流程',
                                      `description` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '描述',
                                      `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '表名称',
                                      `model_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '流程模型标识',
                                      `instance_flag` tinyint DEFAULT '0' COMMENT '流程是否开启',
                                      `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                      `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                      `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                      `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                      `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除  0：有效 1：删除',
                                      PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=98 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='表单字段变更管理表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `system_form_change_item`
--

DROP TABLE IF EXISTS `system_form_change_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_form_change_item` (
                                           `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                                           `show_remark_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否显示帮助',
                                           `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '备注',
                                           `effect_range` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '影响范围 （销售，采购，出运）',
                                           `effect_main_instance_flag` tinyint DEFAULT '0' COMMENT '是否影响主流程',
                                           `table_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '表名',
                                           `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '表名',
                                           `name_eng` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '属性英文名称',
                                           `change_level` tinyint NOT NULL COMMENT '变更级别',
                                           `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                           `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                           `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                           `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                           `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除  0：有效 1：删除',
                                           PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4534 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='表单字段变更管理子表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `system_home_tab`
--

DROP TABLE IF EXISTS `system_home_tab`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_home_tab` (
                                   `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                                   `user_id` bigint DEFAULT NULL COMMENT '用户编号',
                                   `role_id` bigint DEFAULT NULL COMMENT '角色编号',
                                   `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '名称',
                                   `sort` int NOT NULL COMMENT '显示顺序',
                                   `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                   `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                   `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                   `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                   `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除  0：有效 1：删除',
                                   PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=57 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='系统首页表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `system_login_log`
--

DROP TABLE IF EXISTS `system_login_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_login_log` (
                                    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '访问ID',
                                    `log_type` bigint NOT NULL COMMENT '日志类型',
                                    `trace_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '链路追踪编号',
                                    `user_id` bigint NOT NULL DEFAULT '0' COMMENT '用户编号',
                                    `user_type` tinyint NOT NULL DEFAULT '0' COMMENT '用户类型',
                                    `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '用户账号',
                                    `result` tinyint NOT NULL COMMENT '登陆结果',
                                    `user_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户 IP',
                                    `user_agent` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '浏览器 UA',
                                    `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
                                    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
                                    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                    `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
                                    `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
                                    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=22351 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统访问记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `system_mail_account`
--

DROP TABLE IF EXISTS `system_mail_account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_mail_account` (
                                       `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                       `mail` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '邮箱',
                                       `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
                                       `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码',
                                       `host` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'SMTP 服务器域名',
                                       `port` int NOT NULL COMMENT 'SMTP 服务器端口',
                                       `ssl_enable` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否开启 SSL',
                                       `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
                                       `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                       `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
                                       `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                       `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
                                       PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='邮箱账号表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `system_mail_log`
--

DROP TABLE IF EXISTS `system_mail_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_mail_log` (
                                   `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
                                   `user_id` bigint DEFAULT NULL COMMENT '用户编号',
                                   `user_type` tinyint DEFAULT NULL COMMENT '用户类型',
                                   `to_mail` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '接收邮箱地址',
                                   `account_id` bigint NOT NULL COMMENT '邮箱账号编号',
                                   `from_mail` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送邮箱地址',
                                   `template_id` bigint NOT NULL COMMENT '模板编号',
                                   `template_code` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模板编码',
                                   `template_nickname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '模版发送人名称',
                                   `template_title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '邮件标题',
                                   `template_content` varchar(10240) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '邮件内容',
                                   `template_params` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '邮件参数',
                                   `send_status` tinyint NOT NULL DEFAULT '0' COMMENT '发送状态',
                                   `send_time` datetime DEFAULT NULL COMMENT '发送时间',
                                   `send_message_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '发送返回的消息 ID',
                                   `send_exception` varchar(4096) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '发送异常',
                                   `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
                                   `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                   `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
                                   `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                   `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
                                   PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=356 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='邮件日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `system_mail_template`
--

DROP TABLE IF EXISTS `system_mail_template`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_mail_template` (
                                        `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
                                        `name` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模板名称',
                                        `code` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模板编码',
                                        `account_id` bigint NOT NULL COMMENT '发送的邮箱账号编号',
                                        `nickname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '发送人名称',
                                        `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模板标题',
                                        `content` varchar(10240) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模板内容',
                                        `params` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '参数数组',
                                        `status` tinyint NOT NULL COMMENT '开启状态',
                                        `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
                                        `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
                                        `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                        `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
                                        `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                        `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
                                        PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='邮件模版表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `system_menu`
--

DROP TABLE IF EXISTS `system_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_menu` (
                               `id` bigint NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
                               `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '菜单名称',
                               `permission` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '权限标识',
                               `type` tinyint NOT NULL COMMENT '菜单类型',
                               `sort` int NOT NULL DEFAULT '0' COMMENT '显示顺序',
                               `parent_id` bigint NOT NULL DEFAULT '0' COMMENT '父菜单ID',
                               `path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '路由地址',
                               `icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '#' COMMENT '菜单图标',
                               `component` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '组件路径',
                               `component_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '组件名',
                               `status` tinyint NOT NULL DEFAULT '0' COMMENT '菜单状态',
                               `visible` bit(1) NOT NULL DEFAULT b'1' COMMENT '是否可见',
                               `keep_alive` bit(1) NOT NULL DEFAULT b'1' COMMENT '是否缓存',
                               `always_show` bit(1) NOT NULL DEFAULT b'1' COMMENT '是否总是显示',
                               `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
                               `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
                               `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                               `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
                               PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3508 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='菜单权限表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `system_notice`
--

DROP TABLE IF EXISTS `system_notice`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_notice` (
                                 `id` bigint NOT NULL AUTO_INCREMENT COMMENT '公告ID',
                                 `title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '公告标题',
                                 `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '公告内容',
                                 `type` tinyint NOT NULL COMMENT '公告类型（1通知 2公告）',
                                 `status` tinyint NOT NULL DEFAULT '0' COMMENT '公告状态（0正常 1关闭）',
                                 `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
                                 `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                 `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
                                 `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                 `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
                                 `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
                                 PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知公告表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `system_notify_message`
--

DROP TABLE IF EXISTS `system_notify_message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_notify_message` (
                                         `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
                                         `user_id` bigint NOT NULL COMMENT '用户id',
                                         `user_type` tinyint NOT NULL COMMENT '用户类型',
                                         `template_id` bigint NOT NULL COMMENT '模版编号',
                                         `template_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模板编码',
                                         `template_nickname` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模版发送人名称',
                                         `template_content` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模版内容',
                                         `template_type` int NOT NULL COMMENT '模版类型',
                                         `template_params` varchar(2550) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模版参数',
                                         `business_type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务类型',
                                         `business_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务ID',
                                         `business_params` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务参数(JSON格式)',
                                         `read_status` bit(1) NOT NULL COMMENT '是否已读',
                                         `read_time` datetime DEFAULT NULL COMMENT '阅读时间',
                                         `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
                                         `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                         `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
                                         `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                         `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
                                         `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
                                         PRIMARY KEY (`id`) USING BTREE,
                                         KEY `idx_business` (`business_type`,`business_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1985 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='站内信消息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `system_notify_template`
--

DROP TABLE IF EXISTS `system_notify_template`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_notify_template` (
                                          `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                          `name` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模板名称',
                                          `code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模版编码',
                                          `nickname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送人名称',
                                          `content` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模版内容',
                                          `type` tinyint NOT NULL COMMENT '类型',
                                          `params` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '参数数组',
                                          `status` tinyint NOT NULL COMMENT '状态',
                                          `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
                                          `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
                                          `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                          `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
                                          `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                          `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
                                          PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='站内信模板表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `system_oauth2_access_token`
--

DROP TABLE IF EXISTS `system_oauth2_access_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_oauth2_access_token` (
                                              `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
                                              `user_id` bigint NOT NULL COMMENT '用户编号',
                                              `user_type` tinyint NOT NULL COMMENT '用户类型',
                                              `access_token` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '访问令牌',
                                              `refresh_token` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '刷新令牌',
                                              `client_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '客户端编号',
                                              `scopes` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '授权范围',
                                              `expires_time` datetime NOT NULL COMMENT '过期时间',
                                              `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
                                              `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                              `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
                                              `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                              `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
                                              `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
                                              PRIMARY KEY (`id`) USING BTREE,
                                              KEY `idx_access_token` (`access_token`) USING BTREE,
                                              KEY `idx_refresh_token` (`refresh_token`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=40633 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='OAuth2 访问令牌';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `system_oauth2_approve`
--

DROP TABLE IF EXISTS `system_oauth2_approve`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_oauth2_approve` (
                                         `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
                                         `user_id` bigint NOT NULL COMMENT '用户编号',
                                         `user_type` tinyint NOT NULL COMMENT '用户类型',
                                         `client_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '客户端编号',
                                         `scope` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '授权范围',
                                         `approved` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否接受',
                                         `expires_time` datetime NOT NULL COMMENT '过期时间',
                                         `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
                                         `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                         `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
                                         `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                         `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
                                         `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
                                         PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=82 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='OAuth2 批准表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `system_oauth2_client`
--

DROP TABLE IF EXISTS `system_oauth2_client`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_oauth2_client` (
                                        `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
                                        `client_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '客户端编号',
                                        `secret` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '客户端密钥',
                                        `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '应用名',
                                        `logo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '应用图标',
                                        `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '应用描述',
                                        `status` tinyint NOT NULL COMMENT '状态',
                                        `access_token_validity_seconds` int NOT NULL COMMENT '访问令牌的有效期',
                                        `refresh_token_validity_seconds` int NOT NULL COMMENT '刷新令牌的有效期',
                                        `redirect_uris` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '可重定向的 URI 地址',
                                        `authorized_grant_types` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '授权类型',
                                        `scopes` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '授权范围',
                                        `auto_approve_scopes` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '自动通过的授权范围',
                                        `authorities` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '权限',
                                        `resource_ids` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '资源',
                                        `additional_information` varchar(4096) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '附加信息',
                                        `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
                                        `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                        `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
                                        `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                        `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
                                        PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=43 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='OAuth2 客户端表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `system_oauth2_code`
--

DROP TABLE IF EXISTS `system_oauth2_code`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_oauth2_code` (
                                      `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
                                      `user_id` bigint NOT NULL COMMENT '用户编号',
                                      `user_type` tinyint NOT NULL COMMENT '用户类型',
                                      `code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '授权码',
                                      `client_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '客户端编号',
                                      `scopes` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '授权范围',
                                      `expires_time` datetime NOT NULL COMMENT '过期时间',
                                      `redirect_uri` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '可重定向的 URI 地址',
                                      `state` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '状态',
                                      `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
                                      `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                      `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
                                      `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                      `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
                                      `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
                                      PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=147 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='OAuth2 授权码表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `system_oauth2_refresh_token`
--

DROP TABLE IF EXISTS `system_oauth2_refresh_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_oauth2_refresh_token` (
                                               `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
                                               `user_id` bigint NOT NULL COMMENT '用户编号',
                                               `refresh_token` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '刷新令牌',
                                               `user_type` tinyint NOT NULL COMMENT '用户类型',
                                               `client_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '客户端编号',
                                               `scopes` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '授权范围',
                                               `expires_time` datetime NOT NULL COMMENT '过期时间',
                                               `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
                                               `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                               `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
                                               `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                               `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
                                               `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
                                               PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=17362 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='OAuth2 刷新令牌';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `system_operate_log`
--

DROP TABLE IF EXISTS `system_operate_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_operate_log` (
                                      `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志主键',
                                      `trace_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '链路追踪编号',
                                      `user_id` bigint NOT NULL COMMENT '用户编号',
                                      `user_type` tinyint NOT NULL DEFAULT '0' COMMENT '用户类型',
                                      `module` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模块标题',
                                      `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '操作名',
                                      `type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '操作类型',
                                      `content` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '操作内容',
                                      `exts` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '拓展字段',
                                      `request_method` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '请求方法名',
                                      `request_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '请求地址',
                                      `user_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户 IP',
                                      `user_agent` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '浏览器 UA',
                                      `java_method` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT 'Java 方法名',
                                      `java_method_args` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '操作类型',
                                      `start_time` datetime NOT NULL COMMENT '操作时间',
                                      `duration` int NOT NULL COMMENT '执行时长',
                                      `result_code` int NOT NULL DEFAULT '0' COMMENT '结果码',
                                      `result_msg` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '结果提示',
                                      `result_data` varchar(4000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '结果数据',
                                      `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
                                      `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                      `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
                                      `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                      `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
                                      `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
                                      PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=145059 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `system_order_link`
--

DROP TABLE IF EXISTS `system_order_link`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_order_link` (
                                     `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                     `order_msg` json NOT NULL DEFAULT (json_object()) COMMENT '订单信息',
                                     `business_id` bigint DEFAULT NULL COMMENT '业务主键',
                                     `type` tinyint DEFAULT NULL COMMENT '类型',
                                     `business_subject_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '业务对象名称',
                                     `company_id` bigint DEFAULT NULL COMMENT '主体主键',
                                     `code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '业务编号',
                                     `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '业务名称',
                                     `link_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '链路编号',
                                     `status` tinyint NOT NULL DEFAULT '0' COMMENT '订单状态',
                                     `item` json DEFAULT NULL COMMENT '子项',
                                     `parent_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '父节点编号',
                                     `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                     `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                     `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                     `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除',
                                     `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                     PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=14703 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='订单链路表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `system_payment_item`
--

DROP TABLE IF EXISTS `system_payment_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_payment_item` (
                                       `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                                       `name_eng` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '英文名称',
                                       `code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '编号',
                                       `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '名称',
                                       `audit_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '审核状态',
                                       `date_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '起始日类型',
                                       `duration` int unsigned DEFAULT NULL COMMENT '天数',
                                       `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                       `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                       `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                       `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                       `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除  0：有效 1：删除',
                                       PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=51 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='付款方式表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `system_payment_plan`
--

DROP TABLE IF EXISTS `system_payment_plan`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_payment_plan` (
                                       `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                       `control_invoice_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否控制发票',
                                       `payment_id` bigint DEFAULT NULL COMMENT '收款方式主键',
                                       `step` tinyint DEFAULT NULL COMMENT '步骤',
                                       `date_type` tinyint DEFAULT NULL COMMENT '起始点',
                                       `days` int NOT NULL DEFAULT '0' COMMENT '天数',
                                       `payment_ratio` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '付款比例',
                                       `control_purchase_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否控制采购',
                                       `exe_status` tinyint NOT NULL DEFAULT '0' COMMENT '状态',
                                       `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                       `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                       `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                       `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除',
                                       `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                       `payment_method` tinyint DEFAULT NULL,
                                       PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=117 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='付款计划';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `system_port`
--

DROP TABLE IF EXISTS `system_port`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_port` (
                               `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                               `top_flag` int NOT NULL DEFAULT '0' COMMENT '置顶标记',
                               `code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '编号',
                               `name_eng` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '英文名称',
                               `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '名称',
                               `country_id` bigint DEFAULT NULL COMMENT '国家id',
                               `city` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '城市',
                               `address` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '地址',
                               `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态',
                               `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                               `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                               `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                               `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除  0：有效 1：删除',
                               PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1109 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='口岸表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `system_post`
--

DROP TABLE IF EXISTS `system_post`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_post` (
                               `id` bigint NOT NULL AUTO_INCREMENT COMMENT '岗位ID',
                               `code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '岗位编码',
                               `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '岗位名称',
                               `sort` int NOT NULL COMMENT '显示顺序',
                               `status` tinyint NOT NULL COMMENT '状态（0正常 1停用）',
                               `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
                               `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
                               `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
                               `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                               `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
                               `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
                               PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=187 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='岗位信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `system_report`
--

DROP TABLE IF EXISTS `system_report`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_report` (
                                 `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
                                 `business_type` tinyint NOT NULL DEFAULT '1' COMMENT '模版业务类型 1：打印 2：导出',
                                 `company_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '归属公司名称',
                                 `company_id` bigint DEFAULT NULL COMMENT '归属公司主键',
                                 `code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '模板编码',
                                 `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '模板名称',
                                 `annex` json NOT NULL DEFAULT (json_object()) COMMENT '模板',
                                 `path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '模板路径',
                                 `report_type` tinyint(1) NOT NULL COMMENT '模板类型：1：基础模板，2：外部模板，3：特定模版',
                                 `source_type` tinyint(1) DEFAULT NULL COMMENT '外部模板类型：1：客户，2：供应商',
                                 `source_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '来源编号',
                                 `source_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '来源名称',
                                 `audit_status` tinyint NOT NULL COMMENT '审核状态',
                                 `creator` int DEFAULT NULL COMMENT '创建者',
                                 `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                 `updater` int DEFAULT NULL COMMENT '更新者',
                                 `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                 `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
                                 PRIMARY KEY (`id`) USING BTREE,
                                 KEY `index_id` (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1779 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='打印模板表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `system_report_change`
--

DROP TABLE IF EXISTS `system_report_change`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_report_change` (
                                        `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
                                        `business_type` tinyint NOT NULL DEFAULT '1' COMMENT '模版业务类型 1：打印 2：导出',
                                        `company_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '归属公司名称',
                                        `company_id` bigint DEFAULT NULL COMMENT '归属公司主键',
                                        `code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '模板编码',
                                        `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '模板名称',
                                        `annex` json NOT NULL COMMENT '模板',
                                        `path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '模板路径',
                                        `report_type` tinyint(1) NOT NULL COMMENT '模板类型：1：基础模板，2：外部模板，3：特定模版',
                                        `source_type` tinyint(1) DEFAULT NULL COMMENT '外部模板类型：1：客户，2：供应商',
                                        `source_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '来源编号',
                                        `source_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '来源名称',
                                        `audit_status` tinyint NOT NULL COMMENT '审核状态',
                                        `creator` int DEFAULT NULL COMMENT '创建者',
                                        `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                        `updater` int DEFAULT NULL COMMENT '更新者',
                                        `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                        `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
                                        PRIMARY KEY (`id`) USING BTREE,
                                        KEY `index_id` (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1714 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='打印模板表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `system_role`
--

DROP TABLE IF EXISTS `system_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_role` (
                               `id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
                               `name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色名称',
                               `code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色权限字符串',
                               `sort` int NOT NULL COMMENT '显示顺序',
                               `data_scope` tinyint NOT NULL DEFAULT '1' COMMENT '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）',
                               `data_scope_dept_ids` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '数据范围(指定部门数组)',
                               `status` tinyint NOT NULL COMMENT '角色状态（0正常 1停用）',
                               `type` tinyint NOT NULL COMMENT '角色类型',
                               `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
                               `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
                               `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
                               `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                               `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
                               `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
                               PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=51 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `system_role_card`
--

DROP TABLE IF EXISTS `system_role_card`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_role_card` (
                                    `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                                    `role_id` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '角色编号',
                                    `card_id` bigint DEFAULT NULL COMMENT '卡片编号',
                                    `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                    `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                    `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除  0：有效 1：删除',
                                    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=99 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='系统首页表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `system_role_field`
--

DROP TABLE IF EXISTS `system_role_field`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_role_field` (
                                     `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
                                     `field_id` bigint DEFAULT NULL COMMENT '字段id',
                                     `role_id` bigint DEFAULT NULL COMMENT '角色id',
                                     `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                     `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                     `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                     `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                     `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除  0：有效 1：删除',
                                     PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='字段角色关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `system_role_menu`
--

DROP TABLE IF EXISTS `system_role_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_role_menu` (
                                    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增编号',
                                    `role_id` bigint NOT NULL COMMENT '角色ID',
                                    `menu_id` bigint NOT NULL COMMENT '菜单ID',
                                    `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
                                    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
                                    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                    `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
                                    `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
                                    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=25136 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色和菜单关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `system_role_menu_copy1`
--

DROP TABLE IF EXISTS `system_role_menu_copy1`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_role_menu_copy1` (
                                          `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增编号',
                                          `role_id` bigint NOT NULL COMMENT '角色ID',
                                          `menu_id` bigint NOT NULL COMMENT '菜单ID',
                                          `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
                                          `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                          `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
                                          `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                          `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
                                          `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
                                          PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=24565 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色和菜单关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `system_settlement`
--

DROP TABLE IF EXISTS `system_settlement`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_settlement` (
                                     `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                                     `code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '编号',
                                     `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '结汇名称',
                                     `name_eng` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '结汇英文名称',
                                     `date_type` tinyint NOT NULL DEFAULT '0' COMMENT '起始日类型',
                                     `duration` int unsigned DEFAULT NULL COMMENT '天数',
                                     `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                     `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                     `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                     `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                     `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除  0：有效 1：删除',
                                     PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=97 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='结汇方式表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `system_sn`
--

DROP TABLE IF EXISTS `system_sn`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_sn` (
                             `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
                             `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '类型',
                             `code_prefix` varchar(100) DEFAULT NULL COMMENT '编号前缀',
                             `sn` int NOT NULL DEFAULT '0' COMMENT '序列号',
                             `length` int DEFAULT NULL COMMENT '序列号长度',
                             `creator` int unsigned DEFAULT NULL COMMENT '创建者',
                             `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                             `updater` int unsigned DEFAULT NULL COMMENT '更新者',
                             `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                             `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
                             PRIMARY KEY (`id`) USING BTREE,
                             UNIQUE KEY `type` (`type`,`code_prefix`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3074 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='序列号记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `system_social_client`
--

DROP TABLE IF EXISTS `system_social_client`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_social_client` (
                                        `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
                                        `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '应用名',
                                        `social_type` tinyint NOT NULL COMMENT '社交平台的类型',
                                        `user_type` tinyint NOT NULL COMMENT '用户类型',
                                        `client_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '客户端编号',
                                        `client_secret` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '客户端密钥',
                                        `agent_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '代理编号',
                                        `status` tinyint NOT NULL COMMENT '状态',
                                        `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
                                        `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                        `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
                                        `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                        `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
                                        `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
                                        PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=44 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='社交客户端表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `system_social_user`
--

DROP TABLE IF EXISTS `system_social_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_social_user` (
                                      `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键(自增策略)',
                                      `type` tinyint NOT NULL COMMENT '社交平台的类型',
                                      `openid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '社交 openid',
                                      `token` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '社交 token',
                                      `raw_token_info` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '原始 Token 数据，一般是 JSON 格式',
                                      `nickname` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户昵称',
                                      `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户头像',
                                      `raw_user_info` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '原始用户数据，一般是 JSON 格式',
                                      `code` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '最后一次的认证 code',
                                      `state` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '最后一次的认证 state',
                                      `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
                                      `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                      `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
                                      `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                      `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
                                      `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
                                      PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='社交用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `system_social_user_bind`
--

DROP TABLE IF EXISTS `system_social_user_bind`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_social_user_bind` (
                                           `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键(自增策略)',
                                           `user_id` bigint NOT NULL COMMENT '用户编号',
                                           `user_type` tinyint NOT NULL COMMENT '用户类型',
                                           `social_type` tinyint NOT NULL COMMENT '社交平台的类型',
                                           `social_user_id` bigint NOT NULL COMMENT '社交用户的编号',
                                           `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
                                           `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                           `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
                                           `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                           `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
                                           `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
                                           PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=81 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='社交绑定表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `system_user_post`
--

DROP TABLE IF EXISTS `system_user_post`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_user_post` (
                                    `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
                                    `user_id` bigint NOT NULL DEFAULT '0' COMMENT '用户ID',
                                    `post_id` bigint NOT NULL DEFAULT '0' COMMENT '岗位ID',
                                    `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
                                    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
                                    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                    `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
                                    `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
                                    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=534 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户岗位表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `system_user_prefence`
--

DROP TABLE IF EXISTS `system_user_prefence`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_user_prefence` (
                                        `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                        `user_id` bigint DEFAULT NULL COMMENT '用户id',
                                        `user_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '用户编号',
                                        `page_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '页面类型',
                                        `tab_index` int NOT NULL DEFAULT '0' COMMENT '页面tab',
                                        `parent` json NOT NULL DEFAULT (json_array()) COMMENT '主表配置',
                                        `children` json NOT NULL DEFAULT (json_array()) COMMENT '子表配置',
                                        `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                        `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                        `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                        `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除',
                                        `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                        PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=241 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='用户偏好设置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `system_user_role`
--

DROP TABLE IF EXISTS `system_user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_user_role` (
                                    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增编号',
                                    `default_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否默认',
                                    `user_id` bigint NOT NULL COMMENT '用户ID',
                                    `role_id` bigint NOT NULL COMMENT '角色ID',
                                    `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
                                    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
                                    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                    `deleted` bit(1) DEFAULT b'0' COMMENT '是否删除',
                                    `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
                                    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=150 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户和角色关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `system_users`
--

DROP TABLE IF EXISTS `system_users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_users` (
                                `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
                                `tel` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '座机',
                                `time_preferences` json NOT NULL DEFAULT (json_array()) COMMENT '时间设置偏好',
                                `preferences` json NOT NULL DEFAULT (json_array()) COMMENT '偏好',
                                `code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '编号',
                                `username` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户账号',
                                `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '密码',
                                `nickname` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户昵称',
                                `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
                                `dept_id` bigint DEFAULT NULL COMMENT '部门ID',
                                `post_ids` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '岗位编号数组',
                                `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '用户邮箱',
                                `mobile` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '手机号码',
                                `sex` tinyint DEFAULT '0' COMMENT '用户性别',
                                `avatar` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '头像地址',
                                `bank` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '开户行',
                                `bank_address` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '开户行地址',
                                `bank_account` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '银行账号',
                                `bank_poc` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '开户行联系人',
                                `bank_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '银行行号',
                                `status` tinyint NOT NULL DEFAULT '0' COMMENT '帐号状态（0正常 1停用）',
                                `login_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '最后登录IP',
                                `login_date` datetime DEFAULT NULL COMMENT '最后登录时间',
                                `social_user_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '社交用户id',
                                `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
                                `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
                                `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
                                `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
                                PRIMARY KEY (`id`) USING BTREE,
                                UNIQUE KEY `idx_username` (`username`,`update_time`,`tenant_id`) USING BTREE,
                                KEY `idx_nickname` (`nickname`)
) ENGINE=InnoDB AUTO_INCREMENT=366 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `system_users_copy1`
--

DROP TABLE IF EXISTS `system_users_copy1`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_users_copy1` (
                                      `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
                                      `tel` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '座机',
                                      `time_preferences` json NOT NULL DEFAULT (json_array()) COMMENT '时间设置偏好',
                                      `preferences` json NOT NULL DEFAULT (json_array()) COMMENT '偏好',
                                      `code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '编号',
                                      `username` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户账号',
                                      `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '密码',
                                      `nickname` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户昵称',
                                      `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
                                      `dept_id` bigint DEFAULT NULL COMMENT '部门ID',
                                      `post_ids` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '岗位编号数组',
                                      `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '用户邮箱',
                                      `mobile` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '手机号码',
                                      `sex` tinyint DEFAULT '0' COMMENT '用户性别',
                                      `avatar` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '头像地址',
                                      `bank` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '开户行',
                                      `bank_address` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '开户行地址',
                                      `bank_account` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '银行账号',
                                      `bank_poc` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '开户行联系人',
                                      `bank_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '银行行号',
                                      `status` tinyint NOT NULL DEFAULT '0' COMMENT '帐号状态（0正常 1停用）',
                                      `login_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '最后登录IP',
                                      `login_date` datetime DEFAULT NULL COMMENT '最后登录时间',
                                      `social_user_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '社交用户id',
                                      `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
                                      `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                      `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
                                      `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                      `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
                                      `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
                                      PRIMARY KEY (`id`) USING BTREE,
                                      UNIQUE KEY `idx_username` (`username`,`update_time`,`tenant_id`) USING BTREE,
                                      KEY `idx_nickname` (`nickname`)
) ENGINE=InnoDB AUTO_INCREMENT=349 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `wms_adjustment`
--

DROP TABLE IF EXISTS `wms_adjustment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wms_adjustment` (
                                  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                                  `code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '调整单单号',
                                  `stocktake_id` bigint DEFAULT NULL COMMENT '盘点单主键',
                                  `stocktake_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '盘点单单号',
                                  `stocktake_user_id` bigint DEFAULT NULL COMMENT '盘点人主键',
                                  `stocktake_user_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '盘点人姓名',
                                  `adjustment_type` tinyint DEFAULT NULL COMMENT '调整类型  1-盘盈单 2-盘亏单',
                                  `purchase_contract_id` bigint DEFAULT NULL COMMENT '采购合同主键',
                                  `purchase_contract_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '采购合同编号',
                                  `sale_contract_id` bigint DEFAULT NULL COMMENT '销售合同主键',
                                  `sale_contract_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '销售合同号',
                                  `warehouse_id` bigint DEFAULT NULL COMMENT '仓库主键',
                                  `warehouse_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '仓库名称',
                                  `adjustment_date` datetime DEFAULT NULL COMMENT '调整日期',
                                  `print_flag` tinyint DEFAULT NULL COMMENT '打印状态 0-未打印 1-已打印',
                                  `print_times` int NOT NULL DEFAULT '0' COMMENT '打印次数',
                                  `company_id` bigint DEFAULT NULL COMMENT '归属公司主键',
                                  `company_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '归属公司名称',
                                  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
                                  `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                  `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除  0-有效 1-删除',
                                  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='仓储管理-盘库调整单';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `wms_adjustment_item`
--

DROP TABLE IF EXISTS `wms_adjustment_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wms_adjustment_item` (
                                       `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                                       `basic_sku_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '基础产品编号',
                                       `diff_reasons` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '差异原因',
                                       `total_amount` json NOT NULL DEFAULT (json_object()) COMMENT '总价',
                                       `price` json NOT NULL DEFAULT (json_object()) COMMENT '单价',
                                       `sale_contract_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '销售合同编号',
                                       `purchase_contract_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '销售合同编号',
                                       `batch_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '批次号',
                                       `adjustment_id` bigint DEFAULT NULL COMMENT '调整单主键',
                                       `sku_id` bigint DEFAULT NULL COMMENT '产品主键',
                                       `sku_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT 'SKU编号',
                                       `sku_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '产品中文名称',
                                       `csku_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '客户货号',
                                       `own_brand_flag` tinyint DEFAULT NULL COMMENT '自主品牌标识',
                                       `cust_pro_flag` tinyint DEFAULT NULL COMMENT '客户产品标识',
                                       `sort_num` int DEFAULT NULL COMMENT '产品序号',
                                       `source_sort_num` int DEFAULT NULL COMMENT '来源单据明细序号',
                                       `warehouse_id` bigint DEFAULT NULL COMMENT '仓库主键',
                                       `warehouse_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '仓库名称',
                                       `position` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '存放位置',
                                       `stocktake_position` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '盘点位置',
                                       `stock_quantity` int DEFAULT NULL COMMENT '库存数量',
                                       `stocktake_stock_quantity` int DEFAULT NULL COMMENT '盘点数量',
                                       `difference_quantity` int DEFAULT NULL COMMENT '盘盈/盘亏数量',
                                       `qty_per_outerbox` int DEFAULT NULL COMMENT '外箱装量',
                                       `qty_per_innerbox` int DEFAULT NULL COMMENT '内盒装量',
                                       `stock_box_quantity` int DEFAULT NULL COMMENT '库存箱数',
                                       `stocktake_stock_box_quantity` int DEFAULT NULL COMMENT '盘点箱数',
                                       `difference_box_quantity` int DEFAULT NULL COMMENT '盘盈/盘亏箱数',
                                       `vender_id` bigint DEFAULT NULL COMMENT '供应商主键',
                                       `vender_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '供应商编码',
                                       `vender_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '供应商名称',
                                       `cust_id` bigint DEFAULT NULL COMMENT '客户主键',
                                       `cust_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '客户编码',
                                       `cust_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                                       `cust_po` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '客户PO号',
                                       `company_id` bigint DEFAULT NULL COMMENT '归属公司主键',
                                       `company_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '归属公司名称',
                                       `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
                                       `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                       `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                       `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                       `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                       `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除  0-有效 1-删除',
                                       PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='仓储管理-盘库调整单-明细';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `wms_bill`
--

DROP TABLE IF EXISTS `wms_bill`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wms_bill` (
                            `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                            `gen_contract_unique_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '内部生成编号',
                            `invoice_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '出运发票号',
                            `source_id` bigint DEFAULT NULL COMMENT '来源id',
                            `source_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '来源编号',
                            `source_type` tinyint DEFAULT NULL COMMENT '出片文件',
                            `sku_codes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '产品编码',
                            `pictures` json NOT NULL DEFAULT (json_array()) COMMENT '图片',
                            `code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '单号',
                            `bill_type` tinyint DEFAULT NULL COMMENT '入/出库类型 1-入库单、2-出库单',
                            `audit_status` tinyint DEFAULT NULL COMMENT '审核状态',
                            `bill_status` tinyint DEFAULT NULL COMMENT '单据状态1-未确认 2-已确认 3-作废',
                            `notice_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '入/出库通知单号',
                            `sale_contract_id` bigint DEFAULT NULL COMMENT '销售合同主键',
                            `sale_contract_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '销售合同号',
                            `bill_time` datetime DEFAULT NULL COMMENT '入/出库时间',
                            `warehouse_id` bigint DEFAULT NULL COMMENT '仓库主键',
                            `warehouse_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '仓库名称',
                            `print_flag` tinyint DEFAULT NULL COMMENT '打印状态 0-未打印 1-已打印',
                            `print_times` int NOT NULL DEFAULT '0' COMMENT '打印次数',
                            `company_id` bigint DEFAULT NULL COMMENT '归属公司主键',
                            `company_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '归属公司名称',
                            `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
                            `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                            `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                            `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                            `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除  0-有效 1-删除',
                            PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3121 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='仓储管理-入(出)库单';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `wms_bill_item`
--

DROP TABLE IF EXISTS `wms_bill_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wms_bill_item` (
                                 `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                                 `barcode` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '条形码',
                                 `osku_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '自营货号',
                                 `manager` json NOT NULL DEFAULT (json_object()) COMMENT '跟单员',
                                 `split_box_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否分箱',
                                 `specification_list` json NOT NULL DEFAULT (json_array()) COMMENT '规格',
                                 `shipment_item_id` bigint DEFAULT NULL COMMENT '出运明细id',
                                 `basic_sku_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '基础产品编号',
                                 `price` json NOT NULL DEFAULT (json_object()) COMMENT '价格',
                                 `sales` json NOT NULL DEFAULT (json_object()) COMMENT '销售人员',
                                 `purchaser_dept_id` bigint DEFAULT '0' COMMENT '采购员部门主键',
                                 `purchaser_id` bigint DEFAULT '0' COMMENT '采购员主键',
                                 `purchase_contract_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '采购合同编号',
                                 `purchase_contract_id` bigint DEFAULT '0' COMMENT '采购合同单号',
                                 `sale_item_unique_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '销售合同明细编号',
                                 `purchase_item_unique_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '采购合同明细编号',
                                 `abnormal_remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '异常说明',
                                 `abnormal_status` tinyint DEFAULT NULL COMMENT '异常状态',
                                 `source_unique_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '来源编号',
                                 `unique_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '唯一编号',
                                 `source_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '来源单单号',
                                 `batch_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '批次号',
                                 `source_id` bigint DEFAULT NULL COMMENT '来源单主键',
                                 `source_type` tinyint DEFAULT NULL COMMENT '来源单据类型  采购入/出库、盘点入/出库、调拨入/出库、组套件入/出库',
                                 `bill_type` tinyint DEFAULT NULL COMMENT '入/出库类型 1-入库单、2-出库单',
                                 `sort_num` int DEFAULT NULL COMMENT '序号',
                                 `source_sort_num` int DEFAULT NULL COMMENT '来源单据明细序号',
                                 `notice_item_status` tinyint DEFAULT '1' COMMENT '入/出库状态（未收/出货、部分收/出货、完全收/出货）',
                                 `sku_id` bigint DEFAULT NULL COMMENT '产品主键',
                                 `sku_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT 'SKU编号',
                                 `sku_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '产品中文名称',
                                 `csku_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '客户货号',
                                 `own_brand_flag` tinyint DEFAULT NULL COMMENT '自主品牌标识',
                                 `cust_pro_flag` tinyint DEFAULT NULL COMMENT '客户产品标识',
                                 `warehouse_id` bigint DEFAULT NULL COMMENT '仓库主键',
                                 `warehouse_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '仓库名称',
                                 `position` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '存放位置',
                                 `vender_id` bigint DEFAULT NULL COMMENT '供应商主键',
                                 `vender_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '供应商编码',
                                 `vender_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '供应商名称',
                                 `cust_id` bigint DEFAULT NULL COMMENT '客户主键',
                                 `cust_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '客户编码',
                                 `cust_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                                 `cust_po` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '客户PO号',
                                 `order_quantity` int DEFAULT NULL COMMENT '应收/出数量',
                                 `order_box_quantity` int DEFAULT NULL COMMENT '应收/出箱数',
                                 `qty_per_outerbox` int DEFAULT NULL COMMENT '外箱装量',
                                 `qty_per_innerbox` int DEFAULT NULL COMMENT '内盒装量',
                                 `act_quantity` int DEFAULT NULL COMMENT '实收/出数量',
                                 `act_box_quantity` int DEFAULT NULL COMMENT '实收/出箱数',
                                 `outerbox_length` decimal(19,6) DEFAULT NULL COMMENT '外箱规格长度 cm',
                                 `outerbox_width` decimal(19,6) DEFAULT NULL COMMENT '外箱规格宽度 cm',
                                 `outerbox_height` decimal(19,6) DEFAULT NULL COMMENT '外箱规格高度 cm',
                                 `outerbox_volume` decimal(19,6) DEFAULT NULL COMMENT '单箱体积（cm³）',
                                 `outerbox_grossweight` json DEFAULT NULL COMMENT '单箱毛重（{数量,单位}）',
                                 `total_volume` decimal(19,6) DEFAULT NULL COMMENT '总体积(单箱体积x箱数cm³)',
                                 `total_weight` json DEFAULT NULL COMMENT '总毛重（单箱毛重x箱数 {数量,单位}）',
                                 `pallet_volume` decimal(19,6) DEFAULT NULL COMMENT '单托体积（cm³）',
                                 `pallet_weight` json DEFAULT NULL COMMENT '单托毛重（{数量,单位}）',
                                 `company_id` bigint DEFAULT NULL COMMENT '归属公司主键',
                                 `company_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '归属公司名称',
                                 `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
                                 `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                 `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                 `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                 `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                 `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除  0-有效 1-删除',
                                 PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7929 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='仓储管理-入(出)库单-明细';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `wms_notice`
--

DROP TABLE IF EXISTS `wms_notice`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wms_notice` (
                              `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                              `manual_flag` tinyint NOT NULL DEFAULT '0' COMMENT '手动标识',
                              `audit_status` tinyint NOT NULL DEFAULT '0' COMMENT '审核状态',
                              `process_instance_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '流程实例id',
                              `shipment_type` tinyint DEFAULT NULL COMMENT '出运方式',
                              `invoice_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '出运发票号',
                              `link_code_list` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '订单链路编号',
                              `shipment_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '出运明细单号',
                              `pur_contract_code_list` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '采购合同编号',
                              `sale_contract_code_list` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '销售合同编号',
                              `applyer` json DEFAULT NULL COMMENT '申请人',
                              `is_container_transportation` tinyint NOT NULL DEFAULT '0' COMMENT '是否拉柜通知单0-否，1-是',
                              `reference_number` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '提单号',
                              `inbound_date` datetime DEFAULT NULL COMMENT '进仓日期',
                              `code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '单号',
                              `notice_type` tinyint DEFAULT NULL COMMENT '通知类型 1-入库通知单、2-出库通知单',
                              `notice_status` tinyint DEFAULT NULL COMMENT '通知单状态 1-未转 2-已转 3-作废',
                              `notice_time` datetime DEFAULT NULL COMMENT '通知时间',
                              `expect_date` datetime DEFAULT NULL COMMENT '预计到/出货日期',
                              `company_id` bigint DEFAULT NULL COMMENT '归属公司主键',
                              `company_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '归属公司名称',
                              `total_volume` decimal(19,6) DEFAULT NULL COMMENT '总体积(cm³)',
                              `total_weight` json DEFAULT NULL COMMENT '总毛重（{数量,单位}）',
                              `print_flag` tinyint DEFAULT NULL COMMENT '打印状态 0-未打印 1-已打印',
                              `print_times` int NOT NULL DEFAULT '0' COMMENT '打印次数',
                              `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
                              `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                              `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                              `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                              `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除  0-有效 1-删除',
                              PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=918 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='仓储管理-入(出)库通知单';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `wms_notice_item`
--

DROP TABLE IF EXISTS `wms_notice_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wms_notice_item` (
                                   `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                                   `stock_id` bigint DEFAULT NULL COMMENT '库存明细id',
                                   `convert_bill_flag` tinyint NOT NULL DEFAULT '0' COMMENT '转出入库单标识',
                                   `shipped_address` tinyint DEFAULT NULL COMMENT '发货地点',
                                   `source_type` tinyint DEFAULT NULL COMMENT '来源类型',
                                   `split_box_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否分箱',
                                   `specification_list` json NOT NULL DEFAULT (json_array()) COMMENT '规格',
                                   `shipment_item_id` bigint DEFAULT NULL COMMENT '出运明细id',
                                   `basic_sku_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '基础产品编号',
                                   `sale_item_unique_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '销售合同明细编号',
                                   `manager` json NOT NULL DEFAULT (json_object()) COMMENT '跟单员',
                                   `purchase_item_unique_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '采购明细编号',
                                   `sales` json NOT NULL DEFAULT (json_object()) COMMENT '销售人员',
                                   `warehouse_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '仓库名称',
                                   `warehouse_id` bigint DEFAULT NULL COMMENT '仓库主键',
                                   `purchase_user_dept_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '采购员部门名称',
                                   `purchase_user_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '采购员名称',
                                   `pending_stock_quantity` int DEFAULT NULL COMMENT '待入库数量',
                                   `purchaser_dept_id` bigint DEFAULT '0' COMMENT '采购员部门主键',
                                   `purchaser_id` bigint DEFAULT '0' COMMENT '采购员主键',
                                   `purchase_contract_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '采购合同编号',
                                   `purchase_contract_id` bigint DEFAULT '0' COMMENT '采购合同单号',
                                   `source_unique_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '来源编号',
                                   `unique_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '唯一编号',
                                   `in_bill_quantity` int DEFAULT NULL COMMENT '入库中数量',
                                   `real_bill_quantity` int DEFAULT NULL COMMENT '实际入库数量',
                                   `sale_contract_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '销售合同编号',
                                   `batch_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '批次号',
                                   `notice_id` bigint DEFAULT NULL COMMENT '入/出库通知单主键',
                                   `sort_num` int DEFAULT NULL COMMENT '序号',
                                   `source_sort_num` int DEFAULT NULL COMMENT '来源单据明细序号',
                                   `notice_item_status` tinyint DEFAULT NULL COMMENT '入/出库状态（未收/出货、部分收/出货、完全收/出货）',
                                   `sku_id` bigint DEFAULT NULL COMMENT '产品主键',
                                   `sku_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT 'SKU编号',
                                   `sku_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '产品中文名称',
                                   `csku_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '客户货号',
                                   `own_brand_flag` tinyint DEFAULT NULL COMMENT '自主品牌标识',
                                   `cust_pro_flag` tinyint DEFAULT NULL COMMENT '客户产品标识',
                                   `vender_id` bigint DEFAULT NULL COMMENT '供应商主键',
                                   `vender_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '供应商编码',
                                   `vender_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '供应商名称',
                                   `cust_id` bigint DEFAULT NULL COMMENT '客户主键',
                                   `cust_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '客户编码',
                                   `cust_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                                   `cust_po` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '客户PO号',
                                   `order_quantity` int DEFAULT NULL COMMENT '应收/出数量',
                                   `order_box_quantity` int DEFAULT NULL COMMENT '应收/出箱数',
                                   `qty_per_outerbox` int DEFAULT NULL COMMENT '外箱装量',
                                   `qty_per_innerbox` int DEFAULT NULL COMMENT '内盒装量',
                                   `outerbox_length` decimal(19,6) DEFAULT NULL COMMENT '外箱规格长度 cm',
                                   `outerbox_width` decimal(19,6) DEFAULT NULL COMMENT '外箱规格宽度 cm',
                                   `outerbox_height` decimal(19,6) DEFAULT NULL COMMENT '外箱规格高度 cm',
                                   `outerbox_volume` decimal(19,6) DEFAULT NULL COMMENT '单箱体积（cm³）',
                                   `outerbox_grossweight` json DEFAULT NULL COMMENT '单箱毛重（{数量,单位}）',
                                   `total_volume` decimal(19,6) DEFAULT NULL COMMENT '总体积(单箱体积x箱数cm³)',
                                   `total_weight` json DEFAULT NULL COMMENT '总毛重（单箱毛重x箱数 {数量,单位}）',
                                   `pallet_volume` decimal(19,6) DEFAULT NULL COMMENT '单托体积（cm³）',
                                   `pallet_weight` json DEFAULT NULL COMMENT '单托毛重（{数量,单位}）',
                                   `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
                                   `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                   `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                   `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                   `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                   `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除  0-有效 1-删除',
                                   PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2943 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='仓储管理-入(出)库通知单-明细';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `wms_stock`
--

DROP TABLE IF EXISTS `wms_stock`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wms_stock` (
                             `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                             `osku_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '自营货号',
                             `split_box_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否分箱',
                             `specification_list` json NOT NULL DEFAULT (json_array()) COMMENT '规格',
                             `cabinet_quantity` int NOT NULL DEFAULT '0' COMMENT '转拉柜数量',
                             `purchase_user` json NOT NULL DEFAULT (json_object()) COMMENT '采购员',
                             `basic_sku_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '基础产品编号',
                             `diff_quantity` int NOT NULL DEFAULT '0' COMMENT '盘点差异数量',
                             `remain_total_amount` json NOT NULL DEFAULT (json_object()) COMMENT '剩余总金额',
                             `producing_quantity` int DEFAULT '0' COMMENT '在制数量(采购合同下单时库存)',
                             `bill_id` bigint DEFAULT NULL COMMENT '入库单-主键',
                             `batch_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '批次号',
                             `bill_item_id` bigint DEFAULT NULL COMMENT '入库单明细-主键',
                             `sku_id` bigint DEFAULT NULL COMMENT '产品主键',
                             `sku_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT 'SKU编号',
                             `sku_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '产品中文名称',
                             `csku_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '客户货号',
                             `own_brand_flag` tinyint DEFAULT NULL COMMENT '自主品牌标识',
                             `cust_pro_flag` tinyint DEFAULT NULL COMMENT '客户产品标识',
                             `receipt_time` datetime DEFAULT NULL COMMENT '入库时间',
                             `init_quantity` int DEFAULT '0' COMMENT '入库库存（批次入库时的库存）',
                             `used_quantity` int DEFAULT '0' COMMENT '出库数量（已出库库存汇总）',
                             `lock_quantity` int DEFAULT '0' COMMENT '锁定数量（锁定数量汇总）',
                             `available_quantity` int DEFAULT '0' COMMENT '可用数量（仓库中可占用可出库的库存，可用数量= 入库库存-出库数量-锁定数量）',
                             `qty_per_outerbox` int DEFAULT NULL COMMENT '外箱装量',
                             `qty_per_innerbox` int DEFAULT NULL COMMENT '内盒装量',
                             `price` json NOT NULL DEFAULT (json_object()) COMMENT '价格',
                             `total_amount` json DEFAULT NULL COMMENT '总金额',
                             `purchase_contract_id` bigint DEFAULT NULL COMMENT '采购合同主键',
                             `purchase_contract_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '采购合同编号',
                             `sale_contract_id` bigint DEFAULT NULL COMMENT '销售合同主键',
                             `sale_contract_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '销售合同编号',
                             `warehouse_id` bigint DEFAULT NULL COMMENT '仓库主键',
                             `warehouse_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '仓库名称',
                             `position` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '存放位置',
                             `vender_id` bigint DEFAULT NULL COMMENT '供应商主键',
                             `vender_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '供应商编码',
                             `vender_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '供应商名称',
                             `cust_id` bigint DEFAULT NULL COMMENT '客户主键',
                             `cust_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '客户编码',
                             `cust_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                             `cust_po` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '客户PO号',
                             `outerbox_length` decimal(19,6) DEFAULT NULL COMMENT '外箱规格长度 cm',
                             `outerbox_width` decimal(19,6) DEFAULT NULL COMMENT '外箱规格宽度 cm',
                             `outerbox_height` decimal(19,6) DEFAULT NULL COMMENT '外箱规格高度 cm',
                             `outerbox_volume` decimal(19,6) DEFAULT NULL COMMENT '单箱体积（cm³）',
                             `outerbox_grossweight` json DEFAULT NULL COMMENT '单箱毛重（{数量,单位}）',
                             `total_volume` decimal(19,6) DEFAULT NULL COMMENT '总体积(单箱体积x箱数cm³)',
                             `total_weight` json DEFAULT NULL COMMENT '总毛重（单箱毛重x箱数 {数量,单位}）',
                             `pallet_volume` decimal(19,6) DEFAULT NULL COMMENT '单托体积（cm³）',
                             `pallet_weight` json DEFAULT NULL COMMENT '单托毛重（{数量,单位}）',
                             `company_id` bigint DEFAULT NULL COMMENT '归属公司主键',
                             `company_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '归属公司名称',
                             `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
                             `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                             `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                             `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                             `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                             `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除  0-有效 1-删除',
                             PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4421 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='仓储管理-库存明细';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `wms_stock_copy1`
--

DROP TABLE IF EXISTS `wms_stock_copy1`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wms_stock_copy1` (
                                   `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                                   `osku_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '自营货号',
                                   `split_box_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否分箱',
                                   `specification_list` json NOT NULL DEFAULT (json_array()) COMMENT '规格',
                                   `cabinet_quantity` int NOT NULL DEFAULT '0' COMMENT '转拉柜数量',
                                   `purchase_user` json NOT NULL DEFAULT (json_object()) COMMENT '采购员',
                                   `basic_sku_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '基础产品编号',
                                   `diff_quantity` int NOT NULL DEFAULT '0' COMMENT '盘点差异数量',
                                   `remain_total_amount` json NOT NULL DEFAULT (json_object()) COMMENT '剩余总金额',
                                   `producing_quantity` int DEFAULT '0' COMMENT '在制数量(采购合同下单时库存)',
                                   `bill_id` bigint DEFAULT NULL COMMENT '入库单-主键',
                                   `batch_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '批次号',
                                   `bill_item_id` bigint DEFAULT NULL COMMENT '入库单明细-主键',
                                   `sku_id` bigint DEFAULT NULL COMMENT '产品主键',
                                   `sku_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT 'SKU编号',
                                   `sku_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '产品中文名称',
                                   `csku_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '客户货号',
                                   `own_brand_flag` tinyint DEFAULT NULL COMMENT '自主品牌标识',
                                   `cust_pro_flag` tinyint DEFAULT NULL COMMENT '客户产品标识',
                                   `receipt_time` datetime DEFAULT NULL COMMENT '入库时间',
                                   `init_quantity` int DEFAULT '0' COMMENT '入库库存（批次入库时的库存）',
                                   `used_quantity` int DEFAULT '0' COMMENT '出库数量（已出库库存汇总）',
                                   `lock_quantity` int DEFAULT '0' COMMENT '锁定数量（锁定数量汇总）',
                                   `available_quantity` int DEFAULT '0' COMMENT '可用数量（仓库中可占用可出库的库存，可用数量= 入库库存-出库数量-锁定数量）',
                                   `qty_per_outerbox` int DEFAULT NULL COMMENT '外箱装量',
                                   `qty_per_innerbox` int DEFAULT NULL COMMENT '内盒装量',
                                   `price` json NOT NULL DEFAULT (json_object()) COMMENT '价格',
                                   `total_amount` json DEFAULT NULL COMMENT '总金额',
                                   `purchase_contract_id` bigint DEFAULT NULL COMMENT '采购合同主键',
                                   `purchase_contract_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '采购合同编号',
                                   `sale_contract_id` bigint DEFAULT NULL COMMENT '销售合同主键',
                                   `sale_contract_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '销售合同编号',
                                   `warehouse_id` bigint DEFAULT NULL COMMENT '仓库主键',
                                   `warehouse_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '仓库名称',
                                   `position` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '存放位置',
                                   `vender_id` bigint DEFAULT NULL COMMENT '供应商主键',
                                   `vender_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '供应商编码',
                                   `vender_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '供应商名称',
                                   `cust_id` bigint DEFAULT NULL COMMENT '客户主键',
                                   `cust_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '客户编码',
                                   `cust_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                                   `cust_po` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '客户PO号',
                                   `outerbox_length` decimal(19,6) DEFAULT NULL COMMENT '外箱规格长度 cm',
                                   `outerbox_width` decimal(19,6) DEFAULT NULL COMMENT '外箱规格宽度 cm',
                                   `outerbox_height` decimal(19,6) DEFAULT NULL COMMENT '外箱规格高度 cm',
                                   `outerbox_volume` decimal(19,6) DEFAULT NULL COMMENT '单箱体积（cm³）',
                                   `outerbox_grossweight` json DEFAULT NULL COMMENT '单箱毛重（{数量,单位}）',
                                   `total_volume` decimal(19,6) DEFAULT NULL COMMENT '总体积(单箱体积x箱数cm³)',
                                   `total_weight` json DEFAULT NULL COMMENT '总毛重（单箱毛重x箱数 {数量,单位}）',
                                   `pallet_volume` decimal(19,6) DEFAULT NULL COMMENT '单托体积（cm³）',
                                   `pallet_weight` json DEFAULT NULL COMMENT '单托毛重（{数量,单位}）',
                                   `company_id` bigint DEFAULT NULL COMMENT '归属公司主键',
                                   `company_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '归属公司名称',
                                   `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
                                   `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                   `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                   `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                   `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                   `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除  0-有效 1-删除',
                                   PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2963 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='仓储管理-库存明细';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `wms_stock_import`
--

DROP TABLE IF EXISTS `wms_stock_import`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wms_stock_import` (
                                    `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                                    `stock_flag` int NOT NULL DEFAULT '0' COMMENT '入库标记',
                                    `split_box_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否分箱',
                                    `specification_list` json NOT NULL COMMENT '规格',
                                    `cabinet_quantity` int NOT NULL DEFAULT '0' COMMENT '转拉柜数量',
                                    `purchase_user` json NOT NULL COMMENT '采购员',
                                    `basic_sku_code` varchar(50) DEFAULT '' COMMENT '基础产品编号',
                                    `diff_quantity` int NOT NULL DEFAULT '0' COMMENT '盘点差异数量',
                                    `remain_total_amount` json NOT NULL COMMENT '剩余总金额',
                                    `producing_quantity` int DEFAULT '0' COMMENT '在制数量(采购合同下单时库存)',
                                    `bill_id` bigint DEFAULT NULL COMMENT '入库单-主键',
                                    `batch_code` varchar(20) DEFAULT NULL COMMENT '批次号',
                                    `bill_item_id` bigint DEFAULT NULL COMMENT '入库单明细-主键',
                                    `sku_id` bigint DEFAULT NULL COMMENT '产品主键',
                                    `sku_code` varchar(50) DEFAULT '' COMMENT 'SKU编号',
                                    `sku_name` varchar(100) DEFAULT NULL COMMENT '产品中文名称',
                                    `csku_code` varchar(100) DEFAULT NULL COMMENT '客户货号',
                                    `own_brand_flag` tinyint DEFAULT NULL COMMENT '自主品牌标识',
                                    `cust_pro_flag` tinyint DEFAULT NULL COMMENT '客户产品标识',
                                    `receipt_time` datetime DEFAULT NULL COMMENT '入库时间',
                                    `init_quantity` int DEFAULT '0' COMMENT '入库库存（批次入库时的库存）',
                                    `used_quantity` int DEFAULT '0' COMMENT '出库数量（已出库库存汇总）',
                                    `lock_quantity` int DEFAULT '0' COMMENT '锁定数量（锁定数量汇总）',
                                    `available_quantity` int DEFAULT '0' COMMENT '可用数量',
                                    `qty_per_outerbox` int DEFAULT NULL COMMENT '外箱装量',
                                    `qty_per_innerbox` int DEFAULT NULL COMMENT '内盒装量',
                                    `price` json NOT NULL COMMENT '价格',
                                    `total_amount` json DEFAULT NULL COMMENT '总金额',
                                    `purchase_contract_id` bigint DEFAULT NULL COMMENT '采购合同主键',
                                    `purchase_contract_code` varchar(50) NOT NULL DEFAULT '' COMMENT '采购合同编号',
                                    `sale_contract_id` bigint DEFAULT NULL COMMENT '销售合同主键',
                                    `sale_contract_code` varchar(100) DEFAULT NULL COMMENT '销售合同编号',
                                    `warehouse_id` bigint DEFAULT NULL COMMENT '仓库主键',
                                    `warehouse_name` varchar(100) DEFAULT NULL COMMENT '仓库名称',
                                    `position` varchar(500) DEFAULT NULL COMMENT '存放位置',
                                    `vender_id` bigint DEFAULT NULL COMMENT '供应商主键',
                                    `vender_code` varchar(20) DEFAULT NULL COMMENT '供应商编码',
                                    `vender_name` varchar(100) DEFAULT NULL COMMENT '供应商名称',
                                    `cust_id` bigint DEFAULT NULL COMMENT '客户主键',
                                    `cust_code` varchar(20) DEFAULT NULL COMMENT '客户编码',
                                    `cust_name` varchar(200) DEFAULT NULL COMMENT '客户名称',
                                    `cust_po` varchar(100) DEFAULT NULL COMMENT '客户PO号',
                                    `outerbox_length` decimal(19,6) DEFAULT NULL COMMENT '外箱规格长度 cm',
                                    `outerbox_width` decimal(19,6) DEFAULT NULL COMMENT '外箱规格宽度 cm',
                                    `outerbox_height` decimal(19,6) DEFAULT NULL COMMENT '外箱规格高度 cm',
                                    `outerbox_volume` decimal(19,6) DEFAULT NULL COMMENT '单箱体积（cm³）',
                                    `outerbox_grossweight` json DEFAULT NULL COMMENT '单箱毛重（{数量,单位}）',
                                    `total_volume` decimal(19,6) DEFAULT NULL COMMENT '总体积(单箱体积x箱数cm³)',
                                    `total_weight` json DEFAULT NULL COMMENT '总毛重（单箱毛重x箱数 {数量,单位}）',
                                    `pallet_volume` decimal(19,6) DEFAULT NULL COMMENT '单托体积（cm³）',
                                    `pallet_weight` json DEFAULT NULL COMMENT '单托毛重（{数量,单位}）',
                                    `company_id` bigint DEFAULT NULL COMMENT '归属公司主键',
                                    `company_name` varchar(100) DEFAULT NULL COMMENT '归属公司名称',
                                    `remark` varchar(500) DEFAULT NULL COMMENT '备注',
                                    `error_remark` varchar(500) DEFAULT NULL COMMENT '失败原因',
                                    `error_flag` tinyint DEFAULT NULL COMMENT '失败原因',
                                    `import_code` varchar(30) DEFAULT NULL COMMENT '导入批次号',
                                    `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                    `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                    `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除  0-有效 1-删除',
                                    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=308 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='仓储管理-库存明细导入表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `wms_stock_lock`
--

DROP TABLE IF EXISTS `wms_stock_lock`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wms_stock_lock` (
                                  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                                  `source_order_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '来源单据编号',
                                  `cabinet_quantity` int NOT NULL DEFAULT '0' COMMENT '转拉柜数量',
                                  `sale_contract_item_id` bigint DEFAULT NULL COMMENT '销售合同明细主键',
                                  `company_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '归属公司主键名称',
                                  `company_id` bigint DEFAULT NULL COMMENT '归属公司主键',
                                  `sku_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '产品编号',
                                  `sale_contract_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '销售合同编号',
                                  `sale_contract_id` bigint DEFAULT NULL COMMENT '销售合同主键',
                                  `stock_id` bigint DEFAULT NULL COMMENT '产品库存主键',
                                  `batch_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '批次号',
                                  `source_order_type` tinyint DEFAULT NULL COMMENT '原单据类型  1-出库通知单 2-加工单',
                                  `lock_type` tinyint DEFAULT NULL COMMENT '锁定类型  1-可用数量 2-未到货库存',
                                  `lock_quantity` int DEFAULT NULL COMMENT '锁定数量',
                                  `lock_time` datetime DEFAULT NULL COMMENT '锁定时间',
                                  `lock_by_user_id` bigint DEFAULT NULL COMMENT '锁定人主键',
                                  `lock_by_user_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '锁定人名称',
                                  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
                                  `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                  `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除  0-有效 1-删除',
                                  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=475 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='仓储管理-库存明细-锁定库存';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `wms_stocktake`
--

DROP TABLE IF EXISTS `wms_stocktake`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wms_stocktake` (
                                 `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                                 `code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '盘点单号',
                                 `audit_status` tinyint DEFAULT NULL COMMENT '审核状态',
                                 `stocktake_status` tinyint DEFAULT NULL COMMENT '盘库状态  1-未开始、2-盘库中、3-已结束',
                                 `plan_date` datetime DEFAULT NULL COMMENT '预计盘点日期',
                                 `act_start_time` datetime DEFAULT NULL COMMENT '实际开始时间',
                                 `act_end_time` datetime DEFAULT NULL COMMENT '实际结束时间',
                                 `purchase_contract_id` bigint DEFAULT NULL COMMENT '采购合同主键',
                                 `purchase_contract_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '采购合同编号',
                                 `sale_contract_id` bigint DEFAULT NULL COMMENT '销售合同主键',
                                 `sale_contract_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '销售合同号',
                                 `warehouse_id` bigint DEFAULT NULL COMMENT '仓库主键',
                                 `warehouse_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '仓库名称',
                                 `stocktake_user_id` bigint DEFAULT NULL COMMENT '盘点人主键',
                                 `stocktake_user_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '盘点人姓名',
                                 `print_flag` tinyint DEFAULT NULL COMMENT '打印状态 0-未打印 1-已打印',
                                 `print_times` int NOT NULL DEFAULT '0' COMMENT '打印次数',
                                 `company_id` bigint DEFAULT NULL COMMENT '归属公司主键',
                                 `company_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '归属公司名称',
                                 `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
                                 `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                 `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                 `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                 `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                 `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除  0-有效 1-删除',
                                 PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='仓储管理-盘点单';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `wms_stocktake_item`
--

DROP TABLE IF EXISTS `wms_stocktake_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wms_stocktake_item` (
                                      `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                                      `split_box_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否分箱',
                                      `specification_list` json NOT NULL DEFAULT (json_array()) COMMENT '规格',
                                      `basic_sku_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '基础产品编号',
                                      `outerbox_grossweight` json NOT NULL DEFAULT (json_object()) COMMENT '外箱毛重',
                                      `outerbox_volume` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '外箱体积',
                                      `outerbox_height` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '外箱高度',
                                      `outerbox_length` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '外箱长度',
                                      `outerbox_width` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '外箱宽度',
                                      `purchase_user` json NOT NULL DEFAULT (json_object()) COMMENT '采购员',
                                      `diff_reasons` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '差异原因',
                                      `sale_contract_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '销售合同编号',
                                      `purchase_contract_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '采购合同编号',
                                      `total_amount` json NOT NULL DEFAULT (json_object()) COMMENT '总价',
                                      `price` json NOT NULL DEFAULT (json_object()) COMMENT '单价',
                                      `batch_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '批次号',
                                      `stocktake_id` bigint DEFAULT NULL COMMENT '盘点单-主键',
                                      `sku_id` bigint DEFAULT NULL COMMENT '产品主键',
                                      `sku_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT 'SKU编号',
                                      `sku_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '产品中文名称',
                                      `csku_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '客户货号',
                                      `own_brand_flag` tinyint DEFAULT NULL COMMENT '自主品牌标识',
                                      `cust_pro_flag` tinyint DEFAULT NULL COMMENT '客户产品标识',
                                      `warehouse_id` bigint DEFAULT NULL COMMENT '仓库主键',
                                      `warehouse_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '仓库名称',
                                      `position` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '存放位置',
                                      `stock_quantity` int DEFAULT NULL COMMENT '库存数量',
                                      `stock_box_quantity` int DEFAULT NULL COMMENT '库存箱数',
                                      `qty_per_outerbox` int DEFAULT NULL COMMENT '外箱装量',
                                      `qty_per_innerbox` int DEFAULT NULL COMMENT '内盒装量',
                                      `stocktake_position` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '盘点位置',
                                      `stocktake_stock_quantity` int DEFAULT NULL COMMENT '盘点数量',
                                      `stocktake_stock_box_quantity` int DEFAULT NULL COMMENT '盘点箱数',
                                      `vender_id` bigint DEFAULT NULL COMMENT '供应商主键',
                                      `vender_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '供应商编码',
                                      `vender_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '供应商名称',
                                      `cust_id` bigint DEFAULT NULL COMMENT '客户主键',
                                      `cust_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '客户编码',
                                      `cust_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                                      `cust_po` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '客户PO号',
                                      `company_id` bigint DEFAULT NULL COMMENT '归属公司主键',
                                      `company_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '归属公司名称',
                                      `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
                                      `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                      `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                      `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                      `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                      `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除  0-有效 1-删除',
                                      PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='仓储管理-盘点单-明细';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `wms_transfer_order`
--

DROP TABLE IF EXISTS `wms_transfer_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wms_transfer_order` (
                                      `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                                      `status` tinyint DEFAULT NULL COMMENT '状态',
                                      `code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '编号',
                                      `company_id` bigint DEFAULT NULL COMMENT '库存主体主键',
                                      `company_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '库存主体名称',
                                      `transfer_type` tinyint DEFAULT NULL COMMENT '调拨类型',
                                      `sale_contract_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '拨入订单号',
                                      `cust_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '客户编号',
                                      `cust_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '客户名称',
                                      `input_user` json NOT NULL DEFAULT (json_object()) COMMENT '录入人',
                                      `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                      `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                      `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                      `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                      `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除  0：有效 1：删除',
                                      `audit_status` tinyint NOT NULL COMMENT '审核状态',
                                      PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='调拨表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `wms_transfer_order_item`
--

DROP TABLE IF EXISTS `wms_transfer_order_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wms_transfer_order_item` (
                                           `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                                           `split_box_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否分箱',
                                           `specification_list` json NOT NULL DEFAULT (json_array()) COMMENT '规格',
                                           `transfer_order_id` bigint DEFAULT NULL COMMENT '调拨单主键',
                                           `sku_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT 'SKU编号',
                                           `stock_id` bigint DEFAULT NULL COMMENT '库存明细主键',
                                           `csku_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '客户货号',
                                           `sku_name` bigint DEFAULT NULL COMMENT '产品名称',
                                           `batch_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '库存批次号',
                                           `available_quantity` decimal(19,6) DEFAULT NULL COMMENT '当前可用库存',
                                           `transfer_quantity` decimal(19,6) NOT NULL DEFAULT '0.000000' COMMENT '拨出数量',
                                           `sale_contract_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '销售订单号',
                                           `cust_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '客户编号',
                                           `cust_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '客户名称',
                                           `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                           `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                           `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                           `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                           `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除  0：有效 1：删除',
                                           PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='调拨明细表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `wms_warehouse`
--

DROP TABLE IF EXISTS `wms_warehouse`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wms_warehouse` (
                                 `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                                 `manager_ids` json NOT NULL DEFAULT (json_array()) COMMENT '仓管主键数组',
                                 `manager_id` bigint DEFAULT NULL COMMENT '仓管主键',
                                 `default_flag` int NOT NULL DEFAULT '0' COMMENT '默认仓库标记',
                                 `code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '仓库编码',
                                 `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '仓库名称',
                                 `address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '仓库地址',
                                 `enable_flag` tinyint DEFAULT NULL COMMENT '启用标识  0-否 1-是',
                                 `vender_flag` tinyint DEFAULT NULL COMMENT '供应仓标识0-否 1-是',
                                 `vender_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '供应商编码',
                                 `vender_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '供应商名称',
                                 `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
                                 `creator` int unsigned DEFAULT NULL COMMENT '创建人',
                                 `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                 `updater` int unsigned DEFAULT NULL COMMENT '修改人',
                                 `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                 `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除  0-有效 1-删除',
                                 PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6498 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='仓库管理-仓库表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-01-20 14:34:30
