package com.syj.eplus.module.fms.dal.mysql.custclaim;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import com.syj.eplus.module.fms.dal.dataobject.custclaim.CustClaimItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Description：
 * @Author：du
 * @Date：2024/7/25 11:51
 */
@Mapper
public interface CustClaimItemMapper extends BaseMapperX<CustClaimItem> {

    @Select("""
            <script>
            SELECT
                t1.id as id,
                t1.CODE as contractCode,
                t1.sale_type as saleType,
                t1.cust_id as custId,
                t1.cust_code as custCode,
                t1.cust_name as custName,
                t1.currency as currency,
                t1.settlement_name as settlementName,
                t2.step AS sourceType,
                JSON_EXTRACT(t2.receivable_amount, '$.amount') as receivableAmount,
                JSON_EXTRACT(t2.received_amount, '$.amount') as receivedAmount,
                t2.exe_status as status,
                t2.difference_reason as differenceReason,
                t2.id as itemId,
                t1.create_time as createTime,
                t2.collection_ratio as collectionRatio,
            CONCAT('收款计划-', t5.label) as source
                        FROM
                            sms_sale_contract t1
                        LEFT JOIN sms_collection_plan t2 ON t1.id = t2.contract_id
            						LEFT JOIN system_dict_data t5\s
            								ON t2.step = t5.value
            								AND t5.dict_type = 'payment_plan_step'
            WHERE
                t1.STATUS IN ( 3, 4, 5, 6 ) 
                AND t2.deleted = 0 AND t2.exe_status != 1
                AND t1.company_id = #{companyId}
            <when test='custCodes!=null and custCodes.size()>0'>\s
             AND t1.cust_code in\s
             <foreach collection="custCodes" open="(" close=")" item="custCode" separator="," >\s
                 #{custCode}\s
             </foreach>\s
             </when>\s
            UNION
            SELECT
                t3.id as id,
                t3.CODE as contractCode,
                t3.sale_type as saleType,
                t3.cust_id as custId,
                t3.cust_code as custCode,
                t3.cust_name as custName,
                t3.currency as currency,
                t3.settlement_name as settlementName,
                0 AS sourceType,
                JSON_EXTRACT(t4.amount, '$.amount') as receivableAmount,
                JSON_EXTRACT(t4.completed_amount, '$.amount') as receivedAmount,
                t4.status as status,
                t4.difference_reason as differenceReason,
                t4.id as itemId,
                 t3.create_time as createTime,
                 0 as collectionRatio,
                '加项' as source
            FROM
                sms_sale_contract t3
            LEFT JOIN sms_add_sub_term t4 ON t3.CODE = t4.contract_code\s
            WHERE
                t3.STATUS IN ( 3, 4, 5, 6 ) and t4.deleted = 0 and t4.status = 0 and t4.calculation_type = 1 and t3.company_id = #{companyId}\s
            AND t4.settlement_flag = 1\s
            and t4.calculation_type = 1
            <when test='custCodes!=null and custCodes.size()>0'>\s
             AND t3.cust_code in\s
             <foreach collection="custCodes" open="(" close=")" item="custCode" separator="," >\s
                 #{custCode}\s
             </foreach>\s
             </when>\s
            </script>
            """)
    List<CustClaimItem> getCustClaimItemList(@Param("custCodes") List<String> custCodes,@Param("companyId")Long companyId);

}
