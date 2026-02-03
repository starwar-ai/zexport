package com.syj.eplus.module.crm.dal.mysql.cust;


import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.framework.common.enums.ChangeFlagEnum;
import com.syj.eplus.module.crm.api.cust.dto.SimpleCustResp;
import com.syj.eplus.module.crm.api.cust.dto.SimpleCustRespDTO;
import com.syj.eplus.module.crm.controller.admin.cust.vo.CustPageReqVO;
import com.syj.eplus.module.crm.dal.dataobject.cust.CustDO;
import com.syj.eplus.module.crm.enums.cust.DeletedEnum;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 客户资料 Mapper
 *
 * @author du
 */
@Mapper
public interface CustMapper extends BaseMapperX<CustDO> {

    default PageResult<CustDO> selectPage(CustPageReqVO reqVO, List<String> managerIds) {
        LambdaQueryWrapperX<CustDO> custDOLambdaQueryWrapperX = new LambdaQueryWrapperX<CustDO>()
                .eqIfPresent(CustDO::getVer, reqVO.getVer())
                .likeIfPresent(CustDO::getName, reqVO.getName())
                .likeIfPresent(CustDO::getShortname, reqVO.getShortname())
                .likeIfPresent(CustDO::getCode, reqVO.getCode())
                .eqIfPresent(CustDO::getCountryId, reqVO.getCountryId())
                .eqIfPresent(CustDO::getHomepage, reqVO.getHomepage())
                .eqIfPresent(CustDO::getEmail, reqVO.getEmail())
                .eqIfPresent(CustDO::getCustomerTypes, reqVO.getCustomerTypes())
                .eqIfPresent(CustDO::getStageType, reqVO.getStageType())
                .eqIfPresent(CustDO::getCurrencyList, reqVO.getCurrencyList())
                .eqIfPresent(CustDO::getTransportType, reqVO.getTransportType())
                .likeIfPresent(CustDO::getAddress, reqVO.getAddress())
                .eqIfPresent(CustDO::getPhone, reqVO.getPhone())
                .eqIfPresent(CustDO::getAbroadFlag, reqVO.getAbroadFlag())
                .eqIfPresent(CustDO::getSourceType, reqVO.getSourceType())
                .eqIfPresent(CustDO::getCreditFlag, reqVO.getCreditFlag())
                .eqIfPresent(CustDO::getCreditLimit, reqVO.getCreditLimit())
                .eqIfPresent(CustDO::getZxbquotaFlag, reqVO.getZxbquotaFlag())
                .eqIfPresent(CustDO::getSettlementTermType, reqVO.getSettlementTermType())
                .eqIfPresent(CustDO::getInvoiceHeader, reqVO.getInvoiceHeader())
                .eqIfPresent(CustDO::getTaxRate, reqVO.getTaxRate())
                .eqIfPresent(CustDO::getAgentFlag, reqVO.getAgentFlag())
                .betweenIfPresent(CustDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(CustDO::getAuditStatus, reqVO.getAuditStatus())
                .eqIfPresent(CustDO::getEnableFlag, reqVO.getEnableFlag())
                .eqIfPresent(CustDO::getChangeFlag, ChangeFlagEnum.NO.getValue())
                .orderByDesc(CustDO::getId);
        //处理多业务员过滤项逻辑
        if (CollUtil.isNotEmpty(managerIds)) {
            String condition = managerIds.stream()
                    .map(id -> "FIND_IN_SET(" + id + ", manager_ids)")
                    .collect(Collectors.joining(" OR "));
            custDOLambdaQueryWrapperX.apply("(" + condition + ")");
        }

        if (Objects.nonNull(reqVO.getManagerDeptId())) {
            custDOLambdaQueryWrapperX.apply("JSON_CONTAINS(JSON_EXTRACT(manager_list, '$[*].deptId'), CAST({0} AS JSON))", reqVO.getManagerDeptId());
        }
        return selectPage(reqVO, custDOLambdaQueryWrapperX);
    }

    default PageResult<CustDO> selectChangePage(CustPageReqVO reqVO, List<String> managerIds) {
        LambdaQueryWrapperX<CustDO> custDOLambdaQueryWrapperX = new LambdaQueryWrapperX<CustDO>()
                .eqIfPresent(CustDO::getVer, reqVO.getVer())
                .likeIfPresent(CustDO::getName, reqVO.getName())
                .likeIfPresent(CustDO::getShortname, reqVO.getShortname())
                .eqIfPresent(CustDO::getCode, reqVO.getCode())
                .eqIfPresent(CustDO::getCountryId, reqVO.getCountryId())
                .eqIfPresent(CustDO::getHomepage, reqVO.getHomepage())
                .eqIfPresent(CustDO::getEmail, reqVO.getEmail())
                .eqIfPresent(CustDO::getCustomerTypes, reqVO.getCustomerTypes())
                .eqIfPresent(CustDO::getStageType, reqVO.getStageType())
                .eqIfPresent(CustDO::getCurrencyList, reqVO.getCurrencyList())
                .eqIfPresent(CustDO::getTransportType, reqVO.getTransportType())
                .eqIfPresent(CustDO::getAddress, reqVO.getAddress())
                .eqIfPresent(CustDO::getPhone, reqVO.getPhone())
                .eqIfPresent(CustDO::getAbroadFlag, reqVO.getAbroadFlag())
                .eqIfPresent(CustDO::getSourceType, reqVO.getSourceType())
                .eqIfPresent(CustDO::getCreditFlag, reqVO.getCreditFlag())
                .eqIfPresent(CustDO::getCreditLimit, reqVO.getCreditLimit())
                .eqIfPresent(CustDO::getZxbquotaFlag, reqVO.getZxbquotaFlag())
                .eqIfPresent(CustDO::getSettlementTermType, reqVO.getSettlementTermType())
                .eqIfPresent(CustDO::getInvoiceHeader, reqVO.getInvoiceHeader())
                .eqIfPresent(CustDO::getTaxRate, reqVO.getTaxRate())
                .eqIfPresent(CustDO::getAgentFlag, reqVO.getAgentFlag())
                .betweenIfPresent(CustDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(CustDO::getAuditStatus, reqVO.getAuditStatus())
                .eqIfPresent(CustDO::getEnableFlag, reqVO.getEnableFlag())
                .eqIfPresent(CustDO::getChangeDeleted, DeletedEnum.NO.getValue())
                .gtIfPresent(CustDO::getVer, 1)
                .inIfPresent(CustDO::getDeleted, new ArrayList<>(Arrays.asList(0, 1)))
                .orderByDesc(CustDO::getId);
        //处理多业务员过滤项逻辑
        if (CollUtil.isNotEmpty(managerIds)) {
            String condition = managerIds.stream()
                    .map(id -> "FIND_IN_SET(" + id + ", manager_ids)")
                    .collect(Collectors.joining(" OR "));
            custDOLambdaQueryWrapperX.apply("(" + condition + ")");
        }
        return selectPage(reqVO, custDOLambdaQueryWrapperX);
    }

    @ResultMap(value = "ResultMapWithJson")
    @Select("""
             <script>SELECT
                                                   	a.id AS id,
                                                   	a.CODE AS CODE,
                                                   	a.NAME,
                                                   	a.abroad_flag AS abroadFlag,
                                                   	a.create_time AS createTime,
                                                   	a.settlement_term_type AS settlementTermType,
                                                   	a.customer_types AS customerTypes,
                                                   	a.country_id AS countryId,
                                                   	a.address_shipping AS addressShippingList,
                                                   	a.currency_list AS currencyList,
                                                   	b.NAME AS countryName,
                                                   	b.region_name AS regionName,
                                                   	a.cust_link_code AS custLinkCode,
                                                   	a.manager_ids AS managerIds,
                                                   	a.internal_flag AS internalFlag,
                                                   	a.agent_flag AS agentFlag,
                                                   	a.credit_flag AS creditFlag,
                                                   	a.stage_type AS stageType,
                                                   	a.manager_list AS managerList
                                                   FROM
                                                   	crm_cust a
                                                   	LEFT JOIN system_country_info b ON a.country_id = b.id
                                                   WHERE
                                                   	a.deleted = 0
                                                   	AND a.enable_flag = 1
                                                   	AND a.change_flag = 0
             <when test='startTime!=null and endTime!=null'>\s
             AND a.create_time BETWEEN #{startTime,jdbcType=TIMESTAMP} AND #{endTime,jdbcType=TIMESTAMP}\s
             </when>\s
             <when test='custCode!="" and custCode!=null'>
             AND a.code like CONCAT("%",#{custCode,jdbcType=VARCHAR},"%")\s
             </when>
            <when test='internalFlag!=null'>
             AND a.internal_flag = #{internalFlag}
             </when>
             <when test='stageTypeFlag!=null and stageTypeFlag==1'>
             AND ((a.stage_type = 1 and a.audit_status = 0) or (a.stage_type =2 and a.audit_status = 2))
             </when>
           <when test='stageTypeFlag==null'>
             AND a.audit_status = 2
             </when>
             <when test='customerTypes != null and customerTypes.size()>0'>
             <foreach collection="customerTypes" index="index" open="AND (" close=")" item="customerType" separator="OR" >\s
                  FIND_IN_SET(#{customerType,jdbcType=BIGINT}, a.customer_types) > 0\s
             </foreach>\s
             </when>
              <when test='managerIds != null and managerIds.size()>0'>
              <foreach collection="managerIds" index="index" open="AND (" close=")" item="managerId" separator="OR" >\s
                   FIND_IN_SET(#{managerId,jdbcType=BIGINT}, a.manager_ids) > 0\s
              </foreach>\s
              </when>
            <when test='custName!="" and custName!=null and custNameRegex!="" and custNameRegex!=null'>\s
            AND a.name like CONCAT("%",#{custNameRegex,jdbcType=VARCHAR},"%")\s
            </when>
             <when test='custName!="" and custName!=null and (custNameRegex=="" or custNameRegex==null)'>\s
                 AND a.name like CONCAT("%",#{custName,jdbcType=VARCHAR},"%")\s
             </when>
               <when test='nameCode!="" and nameCode!=null'>
                       AND (a.code like CONCAT("%",#{nameCode,jdbcType=VARCHAR},"%") OR a.name REGEXP #{nameCode,jdbcType=VARCHAR})\s
                           ORDER BY length(REPLACE(a.name, #{nameCode,jdbcType=VARCHAR}, ''))/length(a.name)\s
             </when>
             LIMIT #{skip,jdbcType=INTEGER} , #{limit,jdbcType=INTEGER}\s
             </script>""")

//            <when test='custName!="" and custName!=null and custNameRegex!="" and custNameRegex!=null'>s
//              AND a.name REGEXP #{custNameRegex,jdbcType=VARCHAR}s
//              ORDER BY length(REPLACE(a.name, #{custName ,jdbcType=VARCHAR}, ''))/length(a.name)s
//            </when>s
    List<SimpleCustRespDTO> selectSimpleList(@Param("startTime") LocalDateTime startTime,
                                             @Param("endTime") LocalDateTime endTime,
                                             @Param("customerTypes") List<Long> customerTypes,
                                             @Param("managerIds") List<Long> managerIds,
                                             @Param("custCode") String custCode,
                                             @Param("custName") String custName,
                                             @Param("custNameRegex") String custNameRegex,
                                             @Param("internalFlag")Integer internalFlag,
                                             @Param("nameCode") String nameCode,
                                             @Param("stageTypeFlag") Integer stageTypeFlag,
                                             @Param("skip") int skip,
                                             @Param("limit") int limit);

    @Select("""
            <script>select count(1) from crm_cust where deleted =0 and enable_flag = 1 AND change_flag = 0\s
            <when test='custName!="" and custName!=null and custNameRegex!="" and custNameRegex!=null'>\s
            AND name REGEXP #{custNameRegex,jdbcType=VARCHAR}\s
            </when>\s
            <when test='custName!="" and custName!=null and (custNameRegex=="" or custNameRegex==null)'>\s
            AND name like CONCAT("%",#{custName,jdbcType=VARCHAR},"%")\s
            </when>\s
            <when test='internalFlag!=null'>
            AND internal_flag = #{internalFlag}
            </when>
           <when test='stageTypeFlag!=null and stageTypeFlag==1'>
             AND ((stage_type = 1 and audit_status = 0) or (stage_type =2 and audit_status = 2))
           </when>
           <when test='stageTypeFlag==null'>
             AND audit_status = 2
             </when>
            <when test='startTime!=null and endTime!=null'>\s
            AND create_time BETWEEN #{startTime,jdbcType=TIMESTAMP} AND #{endTime,jdbcType=TIMESTAMP}\s
            </when>\s
            <when test='custCode!="" and custCode!=null'>
            AND code like CONCAT("%",#{custCode,jdbcType=VARCHAR},"%")\s
            </when>
            <when test='customerTypes!=null and customerTypes.size()>0'>\s
             AND \s
             <foreach collection="customerTypes" open="(" close=")" item="customerType" separator="OR" >\s
                 FIND_IN_SET( #{customerType,jdbcType=BIGINT}, customer_types)\s
             </foreach>\s
             </when>\s
              <when test='managerIds != null and managerIds.size()>0'>
              <foreach collection="managerIds" index="index" open="AND (" close=")" item="managerId" separator="OR" >\s
                   FIND_IN_SET(#{managerId,jdbcType=BIGINT}, manager_ids) > 0\s
              </foreach>\s
              </when>
            <when test='nameCode!="" and nameCode!=null'>
            AND (code like CONCAT("%",#{nameCode,jdbcType=VARCHAR},"%") OR name REGEXP #{nameCode,jdbcType=VARCHAR})\s
                ORDER BY length(REPLACE(name, #{nameCode ,jdbcType=VARCHAR}, ''))/length(name)\s
            </when>
            </script>""")
    Long getCount(@Param("startTime") LocalDateTime startTime,
                  @Param("endTime") LocalDateTime endTime,
                  @Param("customerTypes") List<Long> customerTypes,
                  @Param("managerIds") List<Long> managerIds,
                  @Param("custCode") String custCode,
                  @Param("custName") String custName,
                  @Param("nameCode") String nameCode,
                  @Param("stageTypeFlag") Integer stageTypeFlag,
                  @Param("internalFlag")Integer internalFlag,
                  @Param("custNameRegex") String custNameRegex);

    static Consumer<LambdaQueryWrapperX<CustDO>> buildCustQueryConsumer(CustPageReqVO reqVO) {
        Consumer<LambdaQueryWrapperX<CustDO>> custConsumer = null;
        return custConsumer;
    }

    @Select("""
            <script>SELECT t.id,t.code, t.name, t.abroad_flag FROM crm_cust t \s
             WHERE t.Deleted = '0' and enable_flag = 1 and change_flag = 0\s
            <when test='custName!="" and custName!=null and custNameRegex!="" and custNameRegex!=null'>\s
            AND t.name REGEXP #{custNameRegex}\s
            ORDER BY length(REPLACE(t.name, #{custName}, ''))/length(t.name)\s
            </when>\s
            <when test='custName!="" and custName!=null and (custNameRegex=="" or custNameRegex==null)'>\s
            AND t.name like CONCAT("%",#{custName,jdbcType=VARCHAR},"%")\s
            </when>\s
            LIMIT #{skip,jdbcType=INTEGER} , #{limit,jdbcType=INTEGER}\s
            </script>""")
    List<SimpleCustRespDTO> queryMatchCustName(@Param("custNameRegex") String custNameRegex, @Param("custName") String custName, @Param("skip") int skip,
                                               @Param("limit") int limit);

    @Select(value = """
            <script>SELECT count(1) FROM crm_cust t \s
             WHERE t.Deleted = '0' and enable_flag = 1 and change_flag = 0\s
            <when test='custName!="" and custName!=null and custNameRegex!="" and custNameRegex!=null'>\s
            AND t.name REGEXP #{custNameRegex}\s
            ORDER BY length(REPLACE(t.name, #{custName}, ''))/length(t.name)\s
            </when>\s
            <when test='custName!="" and custName!=null and (custNameRegex=="" or custNameRegex==null)'>\s
            AND t.name like CONCAT("%",#{custName,jdbcType=VARCHAR},"%")\s
            </when>\s
            LIMIT #{skip,jdbcType=INTEGER} , #{limit,jdbcType=INTEGER}\s
            </script>""")
    Long getMatchCustCount(@Param("custNameRegex") String custNameRegex, @Param("custName") String custName, @Param("skip") int skip,
                           @Param("limit") int limit);

    @ResultMap(value = "BatchResultMapWithJson")
    @Select("""
            <script>SELECT t.id as id,t.code as code, t.name as name, t.abroad_flag as abroadFlag,t.customer_types as customerTypes,t.create_time as createTime FROM crm_cust t\s
             WHERE enable_flag = 1 and change_flag = 0\s
            <when test='custIds!=null and custIds.size()>0'>\s
             AND id in\s
             <foreach collection="custIds" open="(" close=")" item="custId" separator="," >\s
                 #{custId}\s
             </foreach>\s
             </when>\s
             </script>
            """)
    List<SimpleCustRespDTO> batchSimpleCustRespDTOList(@Param("custIds") List<Long> custIds);

    @ResultMap(value = "simpleBaseResultMapWithJson")
    @Select("""

             WITH link_result AS (
            	SELECT
            		t3.id,
            		t3.CODE,
            		t3.NAME,
            		t3.shortname,
            		t3.manager_ids\s
            	FROM
            		crm_cust t3\s
            	WHERE
            		EXISTS ( SELECT 1 FROM crm_cust_bankaccount t4 WHERE t3.id = t4.cust_id AND bank_poc = #{bankPoc} )
            		AND t3.change_flag = 0
            		 AND t3.deleted = 0\s
            	) SELECT
            	t1.id AS id,
            	t1.CODE AS code,
            	t1.NAME AS name,
            	t1.shortname AS shortname,
            	t1.manager_ids AS managerIds\s
            FROM
            	crm_cust t1
            	RIGHT JOIN link_result t2 ON FIND_IN_SET( t2.CODE, t1.cust_link_code ) > 0 AND t1.change_flag = 0 AND t1.deleted = 0\s
            UNION
            SELECT
            	t4.id AS id,
            	t4.CODE AS code,
            	t4.NAME AS name,
            	t4.shortname AS shortname,
            	t4.manager_ids AS managerIds\s
            FROM
            	link_result t4
            """)
    List<SimpleCustResp> getSimpleCustResp(@Param("bankPoc") String bankPoc);

    @Select("""
                SELECT t.name FROM crm_cust t \s
                         WHERE SOUNDEX(name) = SOUNDEX(#{name})
            """)
    List<String> checkName(@Param("name") String name);



    @Select("""
            select count(1) from crm_cust a where a.id =#{id} and  a.deleted=0 and (exists( \s
            	select 1 from sms_sale_contract b where a.id = b.cust_id and b.deleted = 0 \s
            )  or exists( \s
            	select 1 from scm_purchase_plan c where a.id = c.cust_id and c.deleted = 0 \s
            )  or exists(\s
              select 1 from scm_purchase_contract d where a.id = d.cust_id and d.deleted = 0\s
            )  or  exists(\s
            	select 1 from dms_shipment e where a.id = e.cust_id and e.deleted = 0\s
            ) or  exists(\s
            	select 1 from oa_payment_app g where a.code = g.business_subject_code and g.deleted = 0 and g.business_subject_type = 3\s
            )  or  exists(\s
            	select 1 from wms_notice_item h where a.id = h.cust_id and h.deleted = 0\s
            ))
            """)
    Long validateAntiAuditStatus(@Param("id") Long id);
}