package com.syj.eplus.module.scm.dal.mysql.purchasecontractitem;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.syj.eplus.module.scm.api.purchasecontract.dto.PurchaseContractGetItemPageReqDTO;
import com.syj.eplus.module.scm.controller.admin.purchasecontract.vo.PurchaseContractPageReqVO;
import com.syj.eplus.module.scm.controller.admin.purchasecontract.vo.PurchaseContractProductModeRespVO;
import com.syj.eplus.module.scm.dal.dataobject.purchasecontract.PurchaseContractProductModeSummaryDO;
import com.syj.eplus.module.scm.dal.dataobject.purchasecontract.PurchaseItemAmountSummaryDO;
import com.syj.eplus.module.scm.dal.dataobject.purchasecontractitem.PurchaseContractItemDO;
import com.syj.eplus.module.scm.dal.dataobject.purchaseplanitem.PurchaseItemSummaryDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

/**
 * 采购合同明细 Mapper
 *
 * @author zhangcm
 */
@Mapper
public interface PurchaseContractItemMapper extends BaseMapperX<PurchaseContractItemDO> {

    default PageResult<PurchaseContractItemDO> selectPage(PurchaseContractGetItemPageReqDTO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<PurchaseContractItemDO>()
                .inIfPresent(PurchaseContractItemDO::getVenderCode,reqVO.getVenderCodeList())
                .eqIfPresent(PurchaseContractItemDO::getAuxiliaryPurchaseContractCode,reqVO.getAuxiliaryPurchaseContractCode())
                .eqIfPresent(PurchaseContractItemDO::getSaleContractCode,reqVO.getSaleContractCode())
                .eqIfPresent(PurchaseContractItemDO::getPurchaseContractCode,reqVO.getPurchaseContractCode())
                .likeIfPresent(PurchaseContractItemDO::getSpecRemark,reqVO.getSpecDesc())
                .eq(PurchaseContractItemDO::getSkuCode,reqVO.getSkuCode())
                .orderByDesc(PurchaseContractItemDO::getId));
    }
    @Select("""     
            <script>
            SELECT
            	JSON_EXTRACT( i.with_tax_price, '$.currency' ) AS currency,
            	SUM( i.quantity ) AS sumQty,
            	SUM(JSON_EXTRACT( i.with_tax_price, '$.amount' ) * i.quantity) AS sumTotal
           FROM
                scm_purchase_contract_item i
           WHERE
                i.deleted = 0
           <when test= 'skuCode != null and skuCode !="" '>
                AND i.sku_code like CONCAT('%', #{skuCode}, '%')
            </when>
            <when test= 'cskuCode != null and cskuCode !="" '>
                AND i.csku_code like CONCAT('%', #{cskuCode}, '%')
            </when>
            <when test= 'basicSkuCode != null and basicSkuCode !="" '>
                AND i.basic_sku_code like CONCAT('%', #{basicSkuCode}, '%')
            </when>
            AND EXISTS(select 1 from scm_purchase_contract c where c.deleted =0 and c.contract_status != 7 and c.code = i.purchase_contract_code
            <when test= 'companyId != null'>
                AND c.company_id = #{companyId}
            </when>
            <when test= 'venderName != null and venderName !="" '>
                AND c.vender_name like CONCAT('%', #{venderName}, '%')
            </when>
            <when test= 'managerId != null'>
                AND JSON_EXTRACT(c.manager, '$.userId') = #{managerId}
            </when>
            <when test= 'code != null and code !="" '>
                AND c.code like CONCAT('%', #{code}, '%')
            </when>
             <when test='custIdList!=null and custIdList.size()>0'>\s
                 AND c.cust_id in
                 <foreach collection="custIdList" open="(" close=")" item="custId" separator="," >\s
                     #{custId}\s
                 </foreach>\s
            </when>\s
            <when test= 'currency != null and currency !="" '>
                AND c.currency = #{currency}
            </when>
            <when test= 'paymentId != null'>
                AND c.payment_id = #{paymentId}
            </when>
            <when test= 'purchaseUserId != null'>
                AND c.purchase_user_id = #{purchaseUserId}
            </when>
            <when test= 'purchaseUserDeptId != null'>
                AND c.purchase_user_dept_id = #{purchaseUserDeptId}
            </when>
            <when test= 'autoFlag != null'>
                AND c.auto_flag = #{autoFlag}
            </when>
             <when test= 'createTimeStart != null and createTimeEnd != null' >
                   and (
                       c.create_time  <![CDATA[>=]]> #{createTimeStart,javaType=DATE, jdbcType=VARCHAR}
                       and c.create_time  <![CDATA[<=]]> #{createTimeEnd,javaType=DATE, jdbcType=VARCHAR}
                       )
            </when>
          
            <when test= 'salesUserId != null'>
                AND JSON_EXTRACT(c.sales, '$.userId') = #{salesUserId}
            </when>
            )
           GROUP BY
            	JSON_EXTRACT(i.with_tax_price,'$.currency')
            </script> 
           """)
    List<PurchaseItemSummaryDO> getPurchaseItemSummary(@Param("companyId")Long companyId,
                                                       @Param("venderName")String venderName,
                                                       @Param("managerId") Long managerId,
                                                       @Param("code")String code,
                                                       @Param("custIdList")List<Long> custIdList,
                                                       @Param("currency")String currency,
                                                       @Param("paymentId")Long paymentId,
                                                       @Param("purchaseUserId")Long purchaseUserId,
                                                       @Param("purchaseUserDeptId")Long purchaseUserDeptId,
                                                       @Param("autoFlag")Integer autoFlag,
                                                       @Param("createTimeStart")Date createTimeStart,
                                                       @Param("createTimeEnd")Date createTimeEnd,
                                                       @Param("skuCode")String skuCode,
                                                       @Param("cskuCode")String cskuCode,
                                                       @Param("basicSkuCode")String basicSkuCode,
                                                       @Param("salesUserId")Long salesUserId);

    @Select("""     
            <script>
            SELECT \s
              JSON_EXTRACT(i.receivable_amount, '$.currency') AS currency,
              SUM(JSON_EXTRACT(i.receivable_amount, '$.amount')) AS amount \s
            FROM scm_payment_plan i 
            WHERE \s
              JSON_EXTRACT(i.receivable_amount, '$.currency') IS NOT NULL \s
              AND i.deleted = 0 \s
            AND EXISTS(select 1 from scm_purchase_contract_item a where a.deleted =0 and a.purchase_contract_code = i.contract_code
             <when test= 'skuCode != null and skuCode !="" '>
                AND a.sku_code like CONCAT('%', #{skuCode}, '%')
            </when>
            <when test= 'cskuCode != null and cskuCode !="" '>
                AND a.csku_code like CONCAT('%', #{cskuCode}, '%')
            </when>
            <when test= 'basicSkuCode != null and basicSkuCode !="" '>
                AND a.basic_sku_code like CONCAT('%', #{basicSkuCode}, '%')
            </when>                                            
            )
            AND EXISTS(select 1 from scm_purchase_contract c where c.deleted =0 and c.contract_status !=7 and c.code = i.contract_code
                  <when test= 'companyId != null'>
                AND c.company_id = #{companyId}
            </when>
              <when test= 'venderName != null and venderName !="" '>
                AND c.vender_name like CONCAT('%', #{venderName}, '%')
            </when>
                     <when test= 'managerId != null'>
                AND JSON_EXTRACT(c.manager, '$.userId') = #{managerId}
            </when>
            <when test= 'code != null and code !="" '>
                 AND c.code like CONCAT('%', #{code}, '%')
            </when>
             <when test='custIdList!=null and custIdList.size()>0'>\s
                 AND c.cust_id in
                 <foreach collection="custIdList" open="(" close=")" item="custId" separator="," >\s
                     #{custId}\s
                 </foreach>\s
            </when>\s
            <when test= 'currency != null and currency !="" '>
                AND c.currency = #{currency}
            </when>
            <when test= 'paymentId != null'>
                AND c.payment_id = #{paymentId}
            </when>
            <when test= 'purchaseUserId != null'>
                AND c.purchase_user_id = #{purchaseUserId}
            </when>
            <when test= 'purchaseUserDeptId != null'>
                AND c.purchase_user_dept_id = #{purchaseUserDeptId}
            </when>
            <when test= 'autoFlag != null'>
                AND c.auto_flag = #{autoFlag}
            </when>
             <when test= 'createTimeStart != null and createTimeEnd != null' >
                   and (
                       c.create_time  <![CDATA[>=]]> #{createTimeStart,javaType=DATE, jdbcType=VARCHAR}
                       and c.create_time  <![CDATA[<=]]> #{createTimeEnd,javaType=DATE, jdbcType=VARCHAR}
                       )
            </when>
           <when test= 'salesUserId != null'>
                AND JSON_EXTRACT(c.sales, '$.userId') = #{salesUserId}
            </when>
                )
           GROUP BY
            	JSON_EXTRACT(i.receivable_amount, '$.currency')
            </script> 
           """)
    List<PurchaseItemAmountSummaryDO> getPaymentAmountSummary(@Param("companyId")Long companyId,
                                                              @Param("venderName")String venderName,
                                                              @Param("managerId") Long managerId,
                                                              @Param("code")String code,
                                                              @Param("custIdList")List<Long> custIdList,
                                                              @Param("currency")String currency,
                                                              @Param("paymentId")Long paymentId,
                                                              @Param("purchaseUserId")Long purchaseUserId,
                                                              @Param("purchaseUserDeptId")Long purchaseUserDeptId,
                                                              @Param("autoFlag")Integer autoFlag,
                                                              @Param("createTimeStart")Date createTimeStart,
                                                              @Param("createTimeEnd")Date createTimeEnd,
                                                              @Param("skuCode")String skuCode,
                                                              @Param("cskuCode")String cskuCode,
                                                              @Param("basicSkuCode")String basicSkuCode,
                                                              @Param("salesUserId")Long salesUserId);
    @Select("""     
            <script>
            SELECT \s
              JSON_EXTRACT(i.received_amount, '$.currency') AS currency,
              SUM(JSON_EXTRACT(i.received_amount, '$.amount')) AS amount \s
            FROM \s
              scm_purchase_contract c \s
              LEFT JOIN scm_payment_plan i ON i.contract_code = c.CODE \s
              LEFT JOIN scm_purchase_contract_item a ON a.purchase_contract_code = c.code \s
            WHERE \s
              JSON_EXTRACT(i.received_amount, '$.currency') IS NOT NULL \s
              AND i.deleted = 0 \s
            AND EXISTS(select 1 from scm_purchase_contract_item a where a.deleted =0 and a.purchase_contract_code = i.contract_code
             <when test= 'skuCode != null and skuCode !="" '>
                AND a.sku_code like CONCAT('%', #{skuCode}, '%')
            </when>
            <when test= 'cskuCode != null and cskuCode !="" '>
                AND a.csku_code like CONCAT('%', #{cskuCode}, '%')
            </when>
            <when test= 'basicSkuCode != null and basicSkuCode !="" '>
                AND a.basic_sku_code like CONCAT('%', #{basicSkuCode}, '%')
            </when>                                            
            )
            AND EXISTS(select 1 from scm_purchase_contract c where c.deleted =0 and c.contract_status !=7 and c.code = i.contract_code
                  <when test= 'companyId != null'>
                AND c.company_id = #{companyId}
            </when>
              <when test= 'venderName != null and venderName !="" '>
                AND c.vender_name like CONCAT('%', #{venderName}, '%')
            </when>
                     <when test= 'managerId != null'>
                AND JSON_EXTRACT(c.manager, '$.userId') = #{managerId}
            </when>
            <when test= 'code != null and code !="" '>
               AND c.code like CONCAT('%', #{code}, '%')
            </when>
             <when test='custIdList!=null and custIdList.size()>0'>\s
                 AND c.cust_id in
                 <foreach collection="custIdList" open="(" close=")" item="custId" separator="," >\s
                     #{custId}\s
                 </foreach>\s
            </when>\s
            <when test= 'currency != null and currency !="" '>
                AND c.currency = #{currency}
            </when>
            <when test= 'paymentId != null'>
                AND c.payment_id = #{paymentId}
            </when>
            <when test= 'purchaseUserId != null'>
                AND c.purchase_user_id = #{purchaseUserId}
            </when>
            <when test= 'purchaseUserDeptId != null'>
                AND c.purchase_user_dept_id = #{purchaseUserDeptId}
            </when>
            <when test= 'autoFlag != null'>
                AND c.auto_flag = #{autoFlag}
            </when>
             <when test= 'createTimeStart != null and createTimeEnd != null' >
                   and (
                       c.create_time  <![CDATA[>=]]> #{createTimeStart,javaType=DATE, jdbcType=VARCHAR}
                       and c.create_time  <![CDATA[<=]]> #{createTimeEnd,javaType=DATE, jdbcType=VARCHAR}
                       )
            </when>
                       <when test= 'salesUserId != null'>
                AND JSON_EXTRACT(c.sales, '$.userId') = #{salesUserId}
            </when>
                )
           GROUP BY
            	JSON_EXTRACT(i.received_amount, '$.currency')
            </script> 
           """)
    List<PurchaseItemAmountSummaryDO> getPayedAmountSummary(@Param("companyId")Long companyId,
                                             @Param("venderName")String venderName,
                                             @Param("managerId") Long managerId,
                                             @Param("code")String code,
                                             @Param("custIdList")List<Long> custIdList,
                                             @Param("currency")String currency,
                                             @Param("paymentId")Long paymentId,
                                             @Param("purchaseUserId")Long purchaseUserId,
                                             @Param("purchaseUserDeptId")Long purchaseUserDeptId,
                                             @Param("autoFlag")Integer autoFlag,
                                             @Param("createTimeStart")Date createTimeStart,
                                             @Param("createTimeEnd")Date createTimeEnd,
                                             @Param("skuCode")String skuCode,
                                             @Param("cskuCode")String cskuCode,
                                             @Param("basicSkuCode")String basicSkuCode,
                                             @Param("salesUserId")Long salesUserId);

    @Select("""     
            <script>
             select JSON_EXTRACT(c.total_amount_rmb, '$.currency') AS currency,
                          SUM(JSON_EXTRACT(c.total_amount_rmb, '$.amount')) AS amount
                          from  scm_purchase_contract c\s
            WHERE \s
              JSON_EXTRACT(c.total_amount_rmb, '$.currency') IS NOT NULL \s
              AND c.deleted = 0 \s
              AND c.contract_status != 7 \s
               <when test= 'companyId != null'>
                AND c.company_id = #{companyId}
            </when>
            <when test= 'venderName != null and venderName !="" '>
               AND c.vender_name like CONCAT('%', #{venderName}, '%')
            </when>
            <when test= 'managerId != null'>
                AND JSON_EXTRACT(c.manager, '$.userId') = #{managerId}
            </when>
            <when test= 'code != null and code !="" '>
               AND c.code like CONCAT('%', #{code}, '%')
            </when>
             <when test='custIdList!=null and custIdList.size()>0'>\s
                 AND c.cust_id in
                 <foreach collection="custIdList" open="(" close=")" item="custId" separator="," >\s
                     #{custId}\s
                 </foreach>\s
            </when>\s
            <when test= 'currency != null and currency !="" '>
                AND c.currency = #{currency}
            </when>
            <when test= 'paymentId != null'>
                AND c.payment_id = #{paymentId}
            </when>
            <when test= 'purchaseUserId != null'>
                AND c.purchase_user_id = #{purchaseUserId}
            </when>
            <when test= 'purchaseUserDeptId != null'>
                AND c.purchase_user_dept_id = #{purchaseUserDeptId}
            </when>
            <when test= 'autoFlag != null'>
                AND c.auto_flag = #{autoFlag}
            </when>
             <when test= 'createTimeStart != null and createTimeEnd != null' >
                   and (
                       c.create_time  <![CDATA[>=]]> #{createTimeStart,javaType=DATE, jdbcType=VARCHAR}
                       and c.create_time  <![CDATA[<=]]> #{createTimeEnd,javaType=DATE, jdbcType=VARCHAR}
                       )
            </when>
           AND EXISTS(select 1 from scm_purchase_contract_item i where i.deleted =0 and i.purchase_contract_code = c.code
           <when test= 'skuCode != null and skuCode !="" '>
                AND i.sku_code like CONCAT('%', #{skuCode}, '%')
            </when>
            <when test= 'cskuCode != null and cskuCode !="" '>
                AND i.csku_code like CONCAT('%', #{cskuCode}, '%')
            </when>
            <when test= 'basicSkuCode != null and basicSkuCode !="" '>
                AND i.basic_sku_code like CONCAT('%', #{basicSkuCode}, '%')
            </when>
           )
            <when test= 'salesUserId != null'>
              AND JSON_EXTRACT(c.sales, '$.userId') = #{salesUserId}
            </when>
           GROUP BY
            	JSON_EXTRACT(c.total_amount_rmb, '$.currency')
            </script> 
           """)
    List<PurchaseItemAmountSummaryDO> getTotalAmountRmbSummary(@Param("companyId")Long companyId,
                                                               @Param("venderName")String venderName,
                                                            @Param("managerId") Long managerId,
                                                            @Param("code")String code,
                                                            @Param("custIdList")List<Long> custIdList,
                                                            @Param("currency")String currency,
                                                            @Param("paymentId")Long paymentId,
                                                            @Param("purchaseUserId")Long purchaseUserId,
                                                            @Param("purchaseUserDeptId")Long purchaseUserDeptId,
                                                            @Param("autoFlag")Integer autoFlag,
                                                            @Param("createTimeStart")Date createTimeStart,
                                                            @Param("createTimeEnd")Date createTimeEnd,
                                                            @Param("skuCode")String skuCode,
                                                            @Param("cskuCode")String cskuCode,
                                                            @Param("basicSkuCode")String basicSkuCode,
                                                            @Param("salesUserId")Long salesUserId);

    default List<PurchaseContractItemDO> getPurchaseContractItemListByVenderCode(String venderCode) {
        LambdaQueryWrapperX<PurchaseContractItemDO> queryWrapperX = new LambdaQueryWrapperX<PurchaseContractItemDO>();
        queryWrapperX.eq(PurchaseContractItemDO::getVenderCode, venderCode);
        List<PurchaseContractItemDO> purchaseContractItemList = this.selectList(queryWrapperX);
        if (CollUtil.isEmpty(purchaseContractItemList)) {
            return null;
        }
        return purchaseContractItemList;
    }

    default List<PurchaseContractItemDO> getPurchaseContractItemListBySkuCodeList(List<String> skuCodeList) {
        LambdaQueryWrapperX<PurchaseContractItemDO> queryWrapperX = new LambdaQueryWrapperX<PurchaseContractItemDO>();
        queryWrapperX.in(PurchaseContractItemDO::getSkuCode, skuCodeList);
        List<PurchaseContractItemDO> purchaseContractItemList = this.selectList(queryWrapperX);
        if (CollUtil.isEmpty(purchaseContractItemList)) {
            return null;
        }
        return purchaseContractItemList;
    }

    default List<PurchaseContractItemDO> getPurchaseContractItemListByContractId(Long purchaseContractId) {
        LambdaQueryWrapperX<PurchaseContractItemDO> queryWrapperX = new LambdaQueryWrapperX<PurchaseContractItemDO>();
        queryWrapperX.eq(PurchaseContractItemDO::getPurchaseContractId, purchaseContractId);
        List<PurchaseContractItemDO> purchaseContractItemList = this.selectList(queryWrapperX);
        if (CollUtil.isEmpty(purchaseContractItemList)) {
            return null;
        }
        return purchaseContractItemList;
    }

    /**
     * 产品模式分页查询（按明细分页，关联主表条件）
     * 用于产品视图的分页查询，将采购合同明细展开为扁平化列表，每条明细作为一行
     *
     * @param page      分页参数
     * @param pageReqVO 查询参数（包含主表和明细表的筛选条件）
     * @return 分页结果
     */
    IPage<PurchaseContractProductModeRespVO> selectProductModePage(
            IPage<?> page,
            @Param("req") PurchaseContractPageReqVO pageReqVO
    );

    /**
     * 产品模式汇总查询（基于筛选后的明细）
     * 用于产品视图的汇总统计，按币种分组统计数量和金额
     *
     * @param pageReqVO 查询参数（包含主表和明细表的筛选条件）
     * @return 汇总数据列表（按币种分组）
     */
    List<PurchaseContractProductModeSummaryDO> selectProductModeSummary(
            @Param("req") PurchaseContractPageReqVO pageReqVO
    );

}
