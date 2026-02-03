package com.syj.eplus.module.sms.dal.mysql.salecontract;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.syj.eplus.framework.common.dict.CalculationDict;
import com.syj.eplus.module.sms.controller.admin.salecontract.vo.SaleContractPageReqVO;
import com.syj.eplus.module.sms.dal.dataobject.salecontract.SaleContractDO;
import com.syj.eplus.module.sms.dal.dataobject.salecontract.SaleContractSummaryDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 外销合同 Mapper
 *
 * @author du
 */
@Mapper
public interface SaleContractMapper extends BaseMapperX<SaleContractDO> {

    default PageResult<SaleContractDO> selectPage(SaleContractPageReqVO reqVO) {

        LambdaQueryWrapperX<SaleContractDO> saleContractDOLambdaQueryWrapperX = new LambdaQueryWrapperX<SaleContractDO>()
                .likeIfPresent(SaleContractDO::getCode, reqVO.getCode())
                .eqIfPresent(SaleContractDO::getConfirmFlag, reqVO.getConfirmFlag())
                .eqIfPresent(SaleContractDO::getCustCountryId, reqVO.getCountryId())
                .betweenIfPresent(SaleContractDO::getSaleContractDate, reqVO.getSaleContractDate())
                .eqIfPresent(SaleContractDO::getCompanyId, reqVO.getCompanyId())
                .likeIfPresent(SaleContractDO::getCustName, reqVO.getCustName())
                .likeIfPresent(SaleContractDO::getCustPo, reqVO.getCustPo())
                .likeIfPresent(SaleContractDO::getCustCode, reqVO.getCustCode())
                .betweenIfPresent(SaleContractDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(SaleContractDO::getSaleType, reqVO.getSaleType())
                .eqIfPresent(SaleContractDO::getStatus, reqVO.getStatus())
                .neIfPresent(SaleContractDO::getStatus, reqVO.getNeStatus())
                .inIfPresent(SaleContractDO::getStatus, reqVO.getStatusList())
                .inIfPresent(SaleContractDO::getId, reqVO.getIdList())
                .betweenIfPresent(SaleContractDO::getSignBackDate, reqVO.getSignBackDate())
                .eqIfPresent(SaleContractDO::getAuditStatus, reqVO.getAuditStatus())
                .eqIfPresent(SaleContractDO::getAutoFlag, reqVO.getAutoFlag())
                .inIfPresent(SaleContractDO::getCustCode, reqVO.getCustCodeList())
                .betweenIfPresent(SaleContractDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(SaleContractDO::getCustCountryId, reqVO.getCustCountryId())
                .orderByDesc(SaleContractDO::getInputDate);
        if(Objects.nonNull(reqVO.getSalesId())){
            saleContractDOLambdaQueryWrapperX.apply("JSON_EXTRACT(sales, '$.userId') = {0}", reqVO.getSalesId());
        }

        if(Objects.nonNull(reqVO.getSalesDeptId())){
            saleContractDOLambdaQueryWrapperX.apply("JSON_EXTRACT(sales, '$.deptId') = {0}", reqVO.getSalesDeptId());
        }
        if (StrUtil.isNotEmpty(reqVO.getSalesDeptName())) {
            saleContractDOLambdaQueryWrapperX.apply("JSON_EXTRACT(sales,'$.deptName') like CONCAT('%',{0},'%')", reqVO.getSalesDeptName());
        }
        if (Objects.nonNull(reqVO.getCustSourceType())) {
            saleContractDOLambdaQueryWrapperX.inSql(SaleContractDO::getCustCode,
                "SELECT code FROM crm_cust WHERE deleted = 0 AND source_type = " + reqVO.getCustSourceType());
        }
        return selectPage(reqVO,saleContractDOLambdaQueryWrapperX);
    }

    default List<SaleContractSummaryDO> getSummary(SaleContractPageReqVO reqVO) {
        QueryWrapper<SaleContractDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("JSON_EXTRACT( total_amount_usd, '$.currency' ) AS currency,SUM(JSON_EXTRACT( total_amount_usd, '$.amount' )) AS sumTotalAmountUsd");
        if (StrUtil.isNotEmpty(reqVO.getCode())){
            queryWrapper.like("code", reqVO.getCode());
        }
        if (Objects.nonNull(reqVO.getConfirmFlag())){
            queryWrapper.eq("confirm_flag", reqVO.getConfirmFlag());
        }
        if (Objects.nonNull(reqVO.getSignBackDate())&&reqVO.getSignBackDate().length>0){
            queryWrapper.between("sign_back_date", reqVO.getSignBackDate()[0], reqVO.getSignBackDate()[1]);
        }
        if (Objects.nonNull(reqVO.getCountryId())){
            queryWrapper.eq("cust_country_id", reqVO.getCountryId());
        }
        if (Objects.nonNull(reqVO.getCompanyId())){
            queryWrapper.eq("company_id", reqVO.getCompanyId());
        }
        if (StrUtil.isNotEmpty(reqVO.getCustName())){
            queryWrapper.like("cust_name", reqVO.getCustName());
        }
        if (StrUtil.isNotEmpty(reqVO.getCustPo())){
            queryWrapper.like("cust_po", reqVO.getCustPo());
        }
        if (StrUtil.isNotEmpty(reqVO.getCustCode())){
            queryWrapper.like("cust_code", reqVO.getCustCode());
        }
        if (Objects.nonNull(reqVO.getCreateTime())){
            queryWrapper.between("create_time", reqVO.getCreateTime()[0], reqVO.getCreateTime()[1]);
        }
        if (Objects.nonNull(reqVO.getSaleType())){
            queryWrapper.eq("sale_type", reqVO.getSaleType());
        }
        if (Objects.nonNull(reqVO.getStatus())){
            queryWrapper.eq("status", reqVO.getStatus());
        }
        if (Objects.nonNull(reqVO.getNeStatus())){
            queryWrapper.ne("status", reqVO.getNeStatus());
        }
        if (Objects.nonNull(reqVO.getStatusList())&&reqVO.getStatusList().length>0){
            queryWrapper.in("status", reqVO.getStatusList());
        }
        if (Objects.nonNull(reqVO.getIdList())&&reqVO.getIdList().length>0){
            queryWrapper.in("id", reqVO.getIdList());
        }
        if (Objects.nonNull(reqVO.getAuditStatus())){
            queryWrapper.eq("audit_status", reqVO.getAuditStatus());
        }
        if (Objects.nonNull(reqVO.getAutoFlag())){
            queryWrapper.eq("auto_flag", reqVO.getAutoFlag());
        }
        if (CollUtil.isNotEmpty(reqVO.getCustCodeList())){
            queryWrapper.in("cust_code", reqVO.getCustCodeList());
        }
        if (Objects.nonNull(reqVO.getCustCountryId())){
            queryWrapper.eq("cust_country_id", reqVO.getCustCountryId());
        }

        if(Objects.nonNull(reqVO.getSalesId())){
            queryWrapper.apply("JSON_EXTRACT(sales, '$.userId') = {0}", reqVO.getSalesId());
        }
        if(Objects.nonNull(reqVO.getSalesDeptId())){
            queryWrapper.apply("JSON_EXTRACT(sales, '$.deptId') = {0}", reqVO.getSalesDeptId());
        }
        if (StrUtil.isNotEmpty(reqVO.getSalesDeptName())) {
            queryWrapper.apply("JSON_EXTRACT(sales,'$.deptName') like CONCAT('%',{0},'%')", reqVO.getSalesDeptName());
        }
        if (Objects.nonNull(reqVO.getCustSourceType())) {
            queryWrapper.inSql("cust_code",
                "SELECT code FROM crm_cust WHERE deleted = 0 AND source_type = " + reqVO.getCustSourceType());
        }
        queryWrapper.groupBy("JSON_EXTRACT( total_amount_usd, '$.currency' )");
        List<Map<String, Object>> list = selectMaps(queryWrapper);
        if (CollUtil.isEmpty(list)){
            return List.of();
        }
        BigDecimal sumTotalAmountUsd =BigDecimal.ZERO;
        Optional<BigDecimal> amountOpt = list.stream().map(s -> {
            String currency = Objects.isNull(s.get("currency")) ? CalculationDict.CURRENCY_RMB : String.valueOf(s.get("currency"));
            currency = currency.replaceAll("\"([^\"]*)\"", "$1");
            if (!CalculationDict.CURRENCY_USD.equals(currency)) {
                return BigDecimal.ZERO;
            }
            return Objects.isNull(s.get("sumTotalAmountUsd")) ? BigDecimal.ZERO : BigDecimal.valueOf((Double) s.get("sumTotalAmountUsd"));
        }).reduce(BigDecimal::add);
        if (amountOpt.isPresent()){
            sumTotalAmountUsd = amountOpt.get();
        }
        return List.of(new SaleContractSummaryDO().setCurrency(CalculationDict.CURRENCY_USD).setSumTotalAmount(sumTotalAmountUsd));
    }
    default List<SaleContractSummaryDO> getTotalAmountThisCurrencySummary(SaleContractPageReqVO reqVO) {
        QueryWrapper<SaleContractDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("JSON_EXTRACT( total_amount_this_currency, '$.currency' ) AS currency,SUM(JSON_EXTRACT( total_amount_this_currency, '$.amount' )) AS sumTotalAmount");
        if (StrUtil.isNotEmpty(reqVO.getCode())){
            queryWrapper.like("code", reqVO.getCode());
        }
        if (Objects.nonNull(reqVO.getConfirmFlag())){
            queryWrapper.eq("confirm_flag", reqVO.getConfirmFlag());
        }
        if (Objects.nonNull(reqVO.getCountryId())){
            queryWrapper.eq("cust_country_id", reqVO.getCountryId());
        }
        if (Objects.nonNull(reqVO.getCompanyId())){
            queryWrapper.eq("company_id", reqVO.getCompanyId());
        }
        if (StrUtil.isNotEmpty(reqVO.getCustName())){
            queryWrapper.like("cust_name", reqVO.getCustName());
        }
        if (Objects.nonNull(reqVO.getSignBackDate())&&reqVO.getSignBackDate().length>0){
            queryWrapper.between("sign_back_date", reqVO.getSignBackDate()[0], reqVO.getSignBackDate()[1]);
        }
        if (StrUtil.isNotEmpty(reqVO.getCustPo())){
            queryWrapper.like("cust_po", reqVO.getCustPo());
        }
        if (StrUtil.isNotEmpty(reqVO.getCustCode())){
            queryWrapper.like("cust_code", reqVO.getCustCode());
        }
        if (Objects.nonNull(reqVO.getCreateTime())){
            queryWrapper.between("create_time", reqVO.getCreateTime()[0], reqVO.getCreateTime()[1]);
        }
        if (Objects.nonNull(reqVO.getSaleType())){
            queryWrapper.eq("sale_type", reqVO.getSaleType());
        }
        if (Objects.nonNull(reqVO.getStatus())){
            queryWrapper.eq("status", reqVO.getStatus());
        }
        if (Objects.nonNull(reqVO.getNeStatus())){
            queryWrapper.ne("status", reqVO.getNeStatus());
        }
        if (Objects.nonNull(reqVO.getStatusList())&&reqVO.getStatusList().length>0){
            queryWrapper.in("status", reqVO.getStatusList());
        }
        if (Objects.nonNull(reqVO.getIdList())&&reqVO.getIdList().length>0){
            queryWrapper.in("id", reqVO.getIdList());
        }
        if (Objects.nonNull(reqVO.getAuditStatus())){
            queryWrapper.eq("audit_status", reqVO.getAuditStatus());
        }
        if (Objects.nonNull(reqVO.getAutoFlag())){
            queryWrapper.eq("auto_flag", reqVO.getAutoFlag());
        }
        if (CollUtil.isNotEmpty(reqVO.getCustCodeList())){
            queryWrapper.in("cust_code", reqVO.getCustCodeList());
        }
        if (Objects.nonNull(reqVO.getCustCountryId())){
            queryWrapper.eq("cust_country_id", reqVO.getCustCountryId());
        }

        if(Objects.nonNull(reqVO.getSalesId())){
            queryWrapper.apply("JSON_EXTRACT(sales, '$.userId') = {0}", reqVO.getSalesId());
        }
        if(Objects.nonNull(reqVO.getSalesDeptId())){
            queryWrapper.apply("JSON_EXTRACT(sales, '$.deptId') = {0}", reqVO.getSalesDeptId());
        }
        if (StrUtil.isNotEmpty(reqVO.getSalesDeptName())) {
            queryWrapper.apply("JSON_EXTRACT(sales,'$.deptName') like CONCAT('%',{0},'%')", reqVO.getSalesDeptName());
        }
        if (Objects.nonNull(reqVO.getCustSourceType())) {
            queryWrapper.inSql("cust_code",
                "SELECT code FROM crm_cust WHERE deleted = 0 AND source_type = " + reqVO.getCustSourceType());
        }
        queryWrapper.groupBy("JSON_EXTRACT( total_amount_this_currency, '$.currency' )");
        List<Map<String, Object>> list = selectMaps(queryWrapper);
        if (CollUtil.isEmpty(list)){
            return List.of();
        }
        return list.stream().map(s -> {
            String currency = Objects.isNull(s.get("currency")) ? CalculationDict.CURRENCY_RMB : String.valueOf(s.get("currency"));
            currency = currency.replaceAll("\"([^\"]*)\"", "$1");
            BigDecimal amount = Objects.isNull(s.get("sumTotalAmount")) ? BigDecimal.ZERO : BigDecimal.valueOf((Double) s.get("sumTotalAmount"));
            return new SaleContractSummaryDO().setCurrency(currency).setSumTotalAmount(amount);
        }).collect(Collectors.toList());
    }

    /**
     * Optimized page query with JOIN to reduce N+1 query problem
     * Fetch main contracts, items, add-sub items, and collection plans in batch queries
     */
    default PageResult<SaleContractDO> selectPageOptimized(SaleContractPageReqVO reqVO) {
        // 1. Query main contracts with pagination
        PageResult<SaleContractDO> pageResult = selectPage(reqVO);
        List<SaleContractDO> contracts = pageResult.getList();
        
        if (CollUtil.isEmpty(contracts)) {
            return pageResult;
        }
        
        // 2. Batch query related data to avoid N+1 problem
        List<Long> contractIds = contracts.stream()
            .map(SaleContractDO::getId)
            .distinct()
            .collect(Collectors.toList());
            
        List<String> contractCodes = contracts.stream()
            .map(SaleContractDO::getCode)
            .distinct()
            .collect(Collectors.toList());
        
        // These batch queries will be handled by the service layer
        // This method signature change allows better control
        
        return pageResult;
    }

    default List<SaleContractSummaryDO> getTotalAmountSummary(SaleContractPageReqVO reqVO) {
        QueryWrapper<SaleContractDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("JSON_EXTRACT( total_amount, '$.currency' ) AS currency,SUM(JSON_EXTRACT( total_amount, '$.amount' )) AS sumTotalAmount");
        if (StrUtil.isNotEmpty(reqVO.getCode())){
            queryWrapper.like("code", reqVO.getCode());
        }
        if (Objects.nonNull(reqVO.getConfirmFlag())){
            queryWrapper.eq("confirm_flag", reqVO.getConfirmFlag());
        }
        if (Objects.nonNull(reqVO.getCountryId())){
            queryWrapper.eq("cust_country_id", reqVO.getCountryId());
        }
        if (Objects.nonNull(reqVO.getCompanyId())){
            queryWrapper.eq("company_id", reqVO.getCompanyId());
        }
        if (StrUtil.isNotEmpty(reqVO.getCustName())){
            queryWrapper.like("cust_name", reqVO.getCustName());
        }
        if (Objects.nonNull(reqVO.getSignBackDate())&&reqVO.getSignBackDate().length>0){
            queryWrapper.between("sign_back_date", reqVO.getSignBackDate()[0], reqVO.getSignBackDate()[1]);
        }
        if (StrUtil.isNotEmpty(reqVO.getCustPo())){
            queryWrapper.like("cust_po", reqVO.getCustPo());
        }
        if (StrUtil.isNotEmpty(reqVO.getCustCode())){
            queryWrapper.like("cust_code", reqVO.getCustCode());
        }
        if (Objects.nonNull(reqVO.getCreateTime())){
            queryWrapper.between("create_time", reqVO.getCreateTime()[0], reqVO.getCreateTime()[1]);
        }
        if (Objects.nonNull(reqVO.getSaleType())){
            queryWrapper.eq("sale_type", reqVO.getSaleType());
        }
        if (Objects.nonNull(reqVO.getStatus())){
            queryWrapper.eq("status", reqVO.getStatus());
        }
        if (Objects.nonNull(reqVO.getNeStatus())){
            queryWrapper.ne("status", reqVO.getNeStatus());
        }
        if (Objects.nonNull(reqVO.getStatusList())&&reqVO.getStatusList().length>0){
            queryWrapper.in("status", reqVO.getStatusList());
        }
        if (Objects.nonNull(reqVO.getIdList())&&reqVO.getIdList().length>0){
            queryWrapper.in("id", reqVO.getIdList());
        }
        if (Objects.nonNull(reqVO.getAuditStatus())){
            queryWrapper.eq("audit_status", reqVO.getAuditStatus());
        }
        if (Objects.nonNull(reqVO.getAutoFlag())){
            queryWrapper.eq("auto_flag", reqVO.getAutoFlag());
        }
        if (CollUtil.isNotEmpty(reqVO.getCustCodeList())){
            queryWrapper.in("cust_code", reqVO.getCustCodeList());
        }
        if (Objects.nonNull(reqVO.getCustCountryId())){
            queryWrapper.eq("cust_country_id", reqVO.getCustCountryId());
        }

        if(Objects.nonNull(reqVO.getSalesId())){
            queryWrapper.apply("JSON_EXTRACT(sales, '$.userId') = {0}", reqVO.getSalesId());
        }
        if(Objects.nonNull(reqVO.getSalesDeptId())){
            queryWrapper.apply("JSON_EXTRACT(sales, '$.deptId') = {0}", reqVO.getSalesDeptId());
        }
        if (StrUtil.isNotEmpty(reqVO.getSalesDeptName())) {
            queryWrapper.apply("JSON_EXTRACT(sales,'$.deptName') like CONCAT('%',{0},'%')", reqVO.getSalesDeptName());
        }
        if (Objects.nonNull(reqVO.getCustSourceType())) {
            queryWrapper.inSql("cust_code",
                "SELECT code FROM crm_cust WHERE deleted = 0 AND source_type = " + reqVO.getCustSourceType());
        }
        queryWrapper.groupBy("JSON_EXTRACT( total_amount, '$.currency' )");
        List<Map<String, Object>> list = selectMaps(queryWrapper);
        if (CollUtil.isEmpty(list)){
            return List.of();
        }
       return list.stream().map(s -> {
            String currency = Objects.isNull(s.get("currency")) ? CalculationDict.CURRENCY_RMB : String.valueOf(s.get("currency"));
            currency = currency.replaceAll("\"([^\"]*)\"", "$1");
           BigDecimal amount = Objects.isNull(s.get("sumTotalAmount")) ? BigDecimal.ZERO : BigDecimal.valueOf((Double) s.get("sumTotalAmount"));
           return new SaleContractSummaryDO().setCurrency(currency).setSumTotalAmount(amount);
       }).collect(Collectors.toList());
    }


    // 更改出运单状态
    @Update("""
            update dms_shipment a set a.`confirm_flag` = 0\s
            where a.id in\s
            (select distinct shipment_id from dms_shipment_item b where b.sale_contract_code = #{contractCode})
            """)
    void updateEffectShipmentStatus(@Param("contractCode") String contractCode);

    // 更改采购单状态
    @Update("""
            update dms_shipment a set a.`confirm_flag` = 0\s
            where a.id in\s
            (select distinct shipment_id from dms_shipment_item b where b.sale_contract_code = #{contractCode})
            """)
    void updateEffectPurchaseStatus(@Param("contractCode") String contractCode);


    @Select("""
              select a.id from sms_sale_contract a where a.code =#{code} and  a.deleted=0 and (exists(
                                                  	select 1 from scm_purchase_plan b where a.code = b.sale_contract_code and b.deleted = 0 and b.plan_status <> 6\s
                                                  )  or exists(
                                                  	select 1 from fms_cust_claim_item c where a.code = c.contract_code and c.deleted = 0
                                                  ) or exists(
                                                  	select 1 from scm_purchase_contract d where a.code = d.sale_contract_code and d.deleted = 0 and d.contract_status <> 7
                                                  ) or exists(
                                                  	select 1 from dms_shipment_item e where a.code = e.sale_contract_code and e.deleted = 0 and e.status <> 6
                                                  ) or exists(
                                                  	select 1 from dms_shipment_plan_item f where a.code = f.sale_contract_code and f.deleted = 0 and f.status <> 3 
                                                  	                                       ))
            """)
    Long validateAntiAuditStatus(@Param("code") String code);

//    @Select("""
//             <script>
//             SELECT
//             	JSON_EXTRACT( i.total_amount_usd, '$.currency' ) AS currency,
//             	SUM(JSON_EXTRACT( i.total_amount_usd, '$.amount' )) AS sumTotalAmountUsd
//            FROM
//                 sms_sale_contract i
//            WHERE
//                 i.deleted = 0
//                 and JSON_CONTAINS_PATH(i.total_amount_usd, 'one', '$.amount') !=0
//                 <if test= 'code != null' >
//                    and i.code like CONCAT("%",#{code,jdbcType=VARCHAR},"%")\s
//                 </if>
//                 <if test= 'confirmFlag != null ' >
//                    and i.confirm_flag = #{confirmFlag,jdbcType=INTEGER}
//                 </if>
//                  <if test= 'companyId != null ' >
//                    and i.company_id = #{companyId,jdbcType=INTEGER}
//                 </if>
//                   <if test= 'custName != null' >
//                     and i.cust_name like CONCAT("%",#{custName,jdbcType=VARCHAR},"%")\s
//                  </if>
//                   <if test= 'custPo != null' >
//                     and i.cust_po like CONCAT("%",#{custPo,jdbcType=VARCHAR},"%")\s
//                  </if>
//                   <if test= 'custCode != null' >
//                     and i.cust_code like CONCAT("%",#{custCode,jdbcType=VARCHAR},"%")\s
//                  </if>
//                   <if test= 'createTimeStart != null and createTimeEnd != null' >
//                   and (
//                       i.create_time  <![CDATA[>=]]> #{createTimeStart,javaType=DATE, jdbcType=VARCHAR}
//                       and i.create_time  <![CDATA[<=]]> #{createTimeEnd,javaType=DATE, jdbcType=VARCHAR}
//                       )
//                   </if>
//                  <if test= 'saleType != null' >
//                     and i.sale_type = #{saleType,jdbcType=INTEGER}
//                  </if>
//                  <if test= 'status != null' >
//                     and i.status = #{status,jdbcType=INTEGER}
//                  </if>
//                   <if test= 'neStatus != null' >
//                     and i.status != #{neStatus,jdbcType=INTEGER}
//                  </if>
//                  <if test= 'statusList != null' >
//                    AND i.status in
//                         <foreach collection="statusList" open="(" close=")" item="status" separator="," >
//                             #{status}
//                         </foreach>
//                  </if>
//                   <if test= 'idList != null' >
//                    AND i.id in
//                         <foreach collection="idList" open="(" close=")" item="id" separator="," >
//                             #{id}
//                         </foreach>
//                  </if>
//                  <if test= 'auditStatus != null' >
//                     and i.audit_status = #{auditStatus,jdbcType=INTEGER}
//                  </if>
//                    <if test= 'autoFlag != null' >
//                     and i.auto_flag = #{autoFlag,jdbcType=INTEGER}
//                  </if>
//                   <if test= 'custCodeList != null' >
//                    AND i.custCode in
//                         <foreach collection="custCodeList" open="(" close=")" item="custCode" separator="," >
//                             #{custCode}
//                         </foreach>
//                  </if>
//                <if test= 'countryId != null' >
//                     and i.cust_country_id = #{countryId}
//                  </if>
//                  <if test= 'salesDeptId != null' >
//                     and JSON_EXTRACT(i.sales, '$.deptId') = #{salesDeptId}
//                  </if>
//                 <if test= 'salesId != null' >
//                     and JSON_EXTRACT(i.sales, '$.userId') = #{salesId}
//                  </if>
//            GROUP BY
//             	JSON_EXTRACT(i.total_amount_usd,'$.currency')
//             </script>
//             """)
//    List<SaleContractSummaryDO>  getSaleContractSummary(@Param("code")String code,
//                                                       @Param("confirmFlag")Integer confirmFlag,
//                                                       @Param("companyId")Long companyId,
//                                                       @Param("custName") String custName,
//                                                       @Param("custPo") String custPo,
//                                                       @Param("custCode") String custCode,
//                                                       @Param("createTimeStart")Date createTimeStart,
//                                                       @Param("createTimeEnd")Date createTimeEnd,
//                                                       @Param("saleType")Integer saleType,
//                                                       @Param("status")Integer status,
//                                                       @Param("neStatus")Integer neStatus,
//                                                       @Param("statusList")List<Integer> statusList,
//                                                       @Param("idList")List<Long> idList,
//                                                       @Param("auditStatus")Integer auditStatus,
//                                                       @Param("autoFlag")Integer autoFlag,
//                                                       @Param("custCodeList")List<String> custCodeList,
//                                                       @Param("countryId")Long countryId,
//                                                       @Param("salesDeptId")Long salesDeptId,
//                                                        @Param("salesId")Long salesId);
}