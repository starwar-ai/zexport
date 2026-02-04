package com.syj.eplus.module.fms.dal.mysql.bankregistration;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.fms.controller.admin.bankregistration.vo.BankRegistrationPageReqVO;
import com.syj.eplus.module.fms.controller.admin.bankregistration.vo.SimpleRegistrationResp;
import com.syj.eplus.module.fms.dal.dataobject.bankregistration.BankRegistrationDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * 银行登记 Mapper
 *
 * @author du
 */
@Mapper
public interface BankRegistrationMapper extends BaseMapperX<BankRegistrationDO> {

    default PageResult<BankRegistrationDO> selectPage(BankRegistrationPageReqVO reqVO) {
        LambdaQueryWrapperX<BankRegistrationDO> queryWrapperX = new LambdaQueryWrapperX<BankRegistrationDO>()
                .eqIfPresent(BankRegistrationDO::getCode, reqVO.getCode())
                .likeIfPresent(BankRegistrationDO::getCompanyTitle, reqVO.getCompanyTitle())
                .likeIfPresent(BankRegistrationDO::getBank, reqVO.getBank())
                .likeIfPresent(BankRegistrationDO::getCompanyName, reqVO.getCompanyName())
                .likeIfPresent(BankRegistrationDO::getAmount, reqVO.getAmount())
                .eqIfPresent(BankRegistrationDO::getClaimStatus, reqVO.getClaimStatus())
                .betweenIfPresent(BankRegistrationDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(BankRegistrationDO::getId);

        if (Objects.nonNull(reqVO.getManagerId())) {
            queryWrapperX.apply("JSON_CONTAINS(JSON_EXTRACT(manager_list, '$[*].userId'), CAST({0} AS JSON))", reqVO.getManagerId());
        }
        if (Objects.nonNull(reqVO.getCustName())) {
            queryWrapperX.apply("JSON_CONTAINS(JSON_EXTRACT(cust_list, '$[*].name'), CAST({0} AS JSON))", reqVO.getCustName());
        }
        return selectPage(reqVO,queryWrapperX);
    }

    @ResultMap(value = "ResultMapWithJson")
    @Select("""
             <script>
             WITH combined_results AS (
               SELECT
                    t1.id AS id,
                    t1.code AS code,
                    t1.company_title AS companyTitle,
                    t1.company_id AS companyId,
                    t1.company_name AS companyName,
                    t1.bank AS bank,
                    t1.bank_account AS bankAccount,
                    t1.currency AS currency,
                    t1.amount AS amount,
                    t1.claimed_amount AS claimedAmount,
                    t1.registered_by AS registeredBy,
                    t1.manager_list AS managerList,
                    t1.cust_list AS custList,
                    t1.claim_status AS claimStatus
               FROM
                    fms_bank_registration t1
               WHERE
                    t1.status=1 and t1.deleted = 0 and\s
                    EXISTS ( SELECT 1 FROM fms_payee t2 WHERE t1.id = t2.registration_id AND t2.payee_type = 1 \s
                    <when test='custName!="" and custName!=null'>
                        AND t2.payee_name like CONCAT("%",#{custName,jdbcType=VARCHAR},"%")\s
                    </when>
                    )
                    <when test='companyTitle!="" and companyTitle!=null'>
                        AND t1.company_title like CONCAT("%",#{companyTitle,jdbcType=VARCHAR},"%")\s
                    </when>
                     <when test='code!="" and code!=null'>
                        AND t1.code = #{code,jdbcType=VARCHAR}\s
                    </when>
                     <when test='creator!="" and creator!=null'>
                        AND t1.creator = #{creator,jdbcType=VARCHAR}\s
                    </when>
                      <when test='custCode!="" and custCode!=null'>
                        AND JSON_SEARCH(cust_list, 'one', CONCAT('%',#{custCode},'%'), NULL, '$[*].code') IS NOT NULL\s
                    </when>
                       <when test='code!="" and code!=null'>
                        AND JSON_SEARCH(t1.cust_list, 'one', CONCAT('%',#{custCode},'%'), NULL, '$[*].code') IS NOT NULL\s
                    </when>
                       <when test='managerId!=null'>
                        AND JSON_CONTAINS(t1.manager_list, JSON_OBJECT('userId', #{managerId}))\s
                    </when>
                       <when test='companyId!=null'>
                        AND t1.company_id = #{companyId}\s
                    </when>
                        <when test='amountMin!=null'>
                        AND t1.amount <![CDATA[>=]]> #{amountMin}\s
                    </when>
                      <when test='amountMax!=null'>
                        AND t1.amount <![CDATA[<=]]> #{amountMax}\s
                    </when>
                    <when test='claimStatus!=null'>
                        AND t1.claim_status = #{claimStatus}\s
                    </when>
               UNION
               SELECT
                    t3.id AS id,
                    t3.code AS code,
                    t3.company_title AS companyTitle,
                    t3.company_id AS companyId,
                    t3.company_name AS companyName,
                    t3.bank AS bank,
                    t3.bank_account AS bankAccount,
                    t3.currency AS currency,
                    t3.amount AS amount,
                    t3.claimed_amount AS claimedAmount,
                    t3.registered_by AS registeredBy,
                    t3.manager_list AS managerList,
                    t3.cust_list AS custList,
                    t3.claim_status AS claimStatus
               FROM
                    fms_bank_registration t3
               WHERE
                    t3.status=1 and t3.deleted = 0 and\s
                    NOT EXISTS ( SELECT 1 FROM fms_payee t4 WHERE t3.id = t4.registration_id AND t4.payee_type = 2
                      <when test='custName!="" and custName!=null'>
                        AND t2.payee_name like CONCAT("%",#{custName,jdbcType=VARCHAR},"%")\s
                    </when>
                    )
                       <when test='companyTitle!="" and companyTitle!=null'>
                        AND t3.company_title like CONCAT("%",#{companyTitle,jdbcType=VARCHAR},"%")\s
                    </when>
                     <when test='code!="" and code!=null'>
                        AND JSON_SEARCH(cust_list, 'one', CONCAT('%',#{custCode},'%'), NULL, '$[*].code') IS NOT NULL\s
                    </when>
                     <when test='creator!="" and creator!=null'>
                        AND t3.creator = #{creator,jdbcType=VARCHAR}\s
                    </when>
                      <when test='custCode!="" and custCode!=null'>
                        AND JSON_SEARCH(cust_list, 'one', CONCAT('%',#{custCode},'%'), NULL, '$[*].code') IS NOT NULL\s
                    </when>
                    <when test='managerId!=null'>
                        AND JSON_CONTAINS(t3.manager_list, JSON_OBJECT('userId', #{managerId}))\s
                    </when>
                    <when test='companyId!=null'>
                        AND t3.company_id = #{companyId}\s
                    </when>
                    <when test='amountMin!=null'>
                        AND t3.amount <![CDATA[>=]]> #{amountMin}\s
                    </when>
                    <when test='amountMax!=null'>
                        AND t3.amount <![CDATA[<=]]> #{amountMax}\s
                    </when>
                    <when test='claimStatus!=null'>
                        AND t3.claim_status = #{claimStatus}\s
                    </when>
             )
             SELECT *
             FROM combined_results
             ORDER BY id DESC -- 或者根据其他字段排序
             LIMIT #{skip,jdbcType=INTEGER} , #{limit,jdbcType=INTEGER}
             </script>
            """)
    List<SimpleRegistrationResp> getCustRegistration(@Param("limit") int limit,
                                                     @Param("skip") int skip,
                                                     @Param("custName") String custName,
                                                     @Param("code") String code,
                                                     @Param("companyTitle") String companyTitle,
                                                     @Param("creator") String creator,
                                                     @Param("custCode") String custCode,
                                                     @Param("managerId") Long managerId,
                                                     @Param("companyId")Long companyId,
                                                     @Param("amountMin")BigDecimal amountMin,
                                                     @Param("amountMax")BigDecimal amountMax,
                                                     @Param("claimStatus") Integer claimStatus);


    @Select("""
             <script>
             WITH combined_results AS (
               SELECT
                    t1.id AS id,
                    t1.code AS code,
                    t1.company_title AS companyTitle,
                    t1.company_id AS companyId,
                    t1.company_name AS companyName,
                    t1.bank AS bank,
                    t1.bank_account AS bankAccount,
                    t1.currency AS currency,
                    t1.amount AS amount,
                    t1.claimed_amount AS claimedAmount
               FROM
                    fms_bank_registration t1
               WHERE
                    t1.status=1 and t1.deleted = 0 and
                    EXISTS ( SELECT 1 FROM fms_payee t2 WHERE t1.id = t2.registration_id AND t2.payee_type = 1 
                    <when test='custName!="" and custName!=null'>
                        AND t2.payee_name like CONCAT("%",#{custName,jdbcType=VARCHAR},"%")\s
                    </when>
                    )
                    <when test='companyTitle!="" and companyTitle!=null'>
                        AND t1.company_title like CONCAT("%",#{companyTitle,jdbcType=VARCHAR},"%")\s
                    </when>
                     <when test='code!="" and code!=null'>
                        AND t1.code = #{code,jdbcType=VARCHAR}\s
                    </when>
                      <when test='creator!="" and creator!=null'>
                        AND t3.creator = #{creator,jdbcType=VARCHAR}\s
                    </when>
                     <when test='custCode!="" and custCode!=null'>
                        AND JSON_SEARCH(cust_list, 'one', CONCAT('%',#{custCode},'%'), NULL, '$[*].code') IS NOT NULL\s
                    </when>
                       <when test='managerId!=null'>
                        AND JSON_CONTAINS(manager_list, JSON_OBJECT('userId', #{managerId}))\s
                    </when>
                       <when test='companyId!=null'>
                        AND company_id = #{companyId}\s
                    </when>
                        <when test='amountMin!=null'>
                        AND amount <![CDATA[>=]]> #{amountMin}\s
                    </when>
                      <when test='amountMax!=null'>
                        AND amount <![CDATA[<=]]> #{amountMax}\s
                    </when>
                    <when test='claimStatus!=null'>
                        AND claim_status = #{claimStatus}\s
                    </when>
               UNION
               SELECT
                    t3.id AS id,
                    t3.code AS code,
                    t3.company_title AS companyTitle,
                    t3.company_id AS companyId,
                    t3.company_name AS companyName,
                    t3.bank AS bank,
                    t3.bank_account AS bankAccount,
                    t3.currency AS currency,
                    t3.amount AS amount,
                    t3.claimed_amount AS claimedAmount
               FROM
                    fms_bank_registration t3
               WHERE
                    t3.status=1 and t3.deleted = 0 and
                    NOT EXISTS ( SELECT 1 FROM fms_payee t4 WHERE t3.id = t4.registration_id AND t4.payee_type = 2)
                       <when test='companyTitle!="" and companyTitle!=null'>
                        AND t3.company_title like CONCAT("%",#{companyTitle,jdbcType=VARCHAR},"%")\s
                    </when>
                     <when test='code!="" and code!=null'>
                        AND t3.code = #{code,jdbcType=VARCHAR}     
                    </when>
                      <when test='creator!="" and creator!=null'>
                        AND t3.creator = #{creator,jdbcType=VARCHAR}\s
                    </when>
                     <when test='custCode!="" and custCode!=null'>
                        AND JSON_SEARCH(cust_list, 'one', CONCAT('%',#{custCode},'%'), NULL, '$[*].code') IS NOT NULL\s
                    </when>
                       <when test='managerId!=null'>
                        AND JSON_CONTAINS(manager_list, JSON_OBJECT('userId', #{managerId}))\s
                    </when>
                       <when test='companyId!=null'>
                        AND company_id = #{companyId}\s
                    </when>
                        <when test='amountMin!=null'>
                        AND amount <![CDATA[>=]]> #{amountMin}\s
                    </when>
                      <when test='amountMax!=null'>
                        AND amount <![CDATA[<=]]> #{amountMax}\s
                    </when>
                    <when test='claimStatus!=null'>
                        AND claim_status = #{claimStatus}\s
                    </when>
             )
             SELECT count(1)
             FROM combined_results
             </script>
            """)
    Long getCustRegistrationCount(
            @Param("custName") String custName,
            @Param("code") String code,
            @Param("companyTitle") String companyTitle,
            @Param("creator") String creator,
            @Param("custCode") String custCode,
            @Param("managerId") Long managerId,
            @Param("companyId")Long companyId,
            @Param("amountMin")BigDecimal amountMin,
            @Param("amountMax")BigDecimal amountMax,
            @Param("claimStatus") Integer claimStatus);
}