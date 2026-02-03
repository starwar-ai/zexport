package com.syj.eplus.module.scm.dal.mysql.purchaseregistrationitem;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.scm.dal.dataobject.purchaseregistrationitem.PurchaseRegistrationItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 采购跟单登记明细 Mapper
 *
 * @author du
 */
@Mapper
public interface PurchaseRegistrationItemMapper extends BaseMapperX<PurchaseRegistrationItem> {
    default Map<Long, List<PurchaseRegistrationItem>> getPurchaseRegistrationItemMapByRegistrationIdList(List<Long> registrationIdList) {
        LambdaQueryWrapperX<PurchaseRegistrationItem> queryWrapperX = new LambdaQueryWrapperX<PurchaseRegistrationItem>().in(PurchaseRegistrationItem::getRegistrationId, registrationIdList);
        List<PurchaseRegistrationItem> saleContractItemList = this.selectList(queryWrapperX);
        if (CollUtil.isEmpty(saleContractItemList)) {
            return null;
        }
        return saleContractItemList.stream().collect(Collectors.groupingBy(PurchaseRegistrationItem::getRegistrationId));
    }

    @Select("""
    SELECT SUM(b.invoic_this_quantity) as invoicThisQuantityTotal,SUM(b.shipping_quantity) as shippingQuantityTotal,SUM(b.inveic_registered_quantity) as inveicRegisteredTotal,SUM(b.invoic_price * b.invoic_this_quantity) as invoicPriceTotal,SUM(b.invoic_notices_quantity) as invoicNoticesQuantity
    FROM scm_purchase_registration_item b
    WHERE EXISTS (
        SELECT 1
        FROM scm_purchase_registration a
        WHERE a.id = b.registration_id)
    """)
    Map<String, Object> getTotalMsg();

    /**
     * 根据筛选条件获取汇总数据
     */
    @Select("""
    <script>
    SELECT SUM(b.invoic_this_quantity) as invoicThisQuantityTotal,
           SUM(b.shipping_quantity) as shippingQuantityTotal,
           SUM(b.inveic_registered_quantity) as inveicRegisteredTotal,
           SUM(b.invoic_price * b.invoic_this_quantity) as invoicPriceTotal,
           SUM(b.invoic_notices_quantity) as invoicNoticesQuantity
    FROM scm_purchase_registration_item b
    WHERE EXISTS (
        SELECT 1
        FROM scm_purchase_registration a
        WHERE a.id = b.registration_id
        <if test="companyId != null">
            AND a.company_id = #{companyId}
        </if>
        <if test="venderName != null and venderName != ''">
            AND a.vender_name LIKE CONCAT('%', #{venderName}, '%')
        </if>
        <if test="invoiceCode != null and invoiceCode != ''">
            AND a.invoice_code = #{invoiceCode}
        </if>
        <if test='startTime!=null and endTime!=null'>
            AND a.create_time BETWEEN #{startTime,jdbcType=TIMESTAMP} AND #{endTime,jdbcType=TIMESTAMP}\s
        </if>
        <if test="status != null">
            AND a.status = #{status}
        </if>
        <if test="neStatus != null">
            AND a.status != #{neStatus}
        </if>
        <if test="idList != null and idList.length > 0">
            AND a.id IN
            <foreach collection="idList" item="id" open="(" separator="," close=")">
                #{id}
            </foreach>
        </if>
    )
    <if test="shipInvoiceCode != null and shipInvoiceCode != ''">
        AND b.ship_invoice_code LIKE CONCAT('%', #{shipInvoiceCode}, '%')
    </if>
    <if test="invoicSkuName != null and invoicSkuName != ''">
        AND b.invoic_sku_name LIKE CONCAT('%', #{invoicSkuName}, '%')
    </if>
    <if test="hsCode != null and hsCode != ''">
        AND b.hs_code LIKE CONCAT('%', #{hsCode}, '%')
    </if>
    <if test="saleContractCode != null and saleContractCode != ''">
        AND b.sale_contract_code LIKE CONCAT('%', #{saleContractCode}, '%')
    </if>
    <if test="purchaseContractCode != null and purchaseContractCode != ''">
        AND b.purchase_contract_code LIKE CONCAT('%', #{purchaseContractCode}, '%')
    </if>
    <if test="hsMeasureUnit != null and hsMeasureUnit != ''">
        AND b.hs_measure_unit = #{hsMeasureUnit}
    </if>
    <if test="basicSkuCode != null and basicSkuCode != ''">
        AND b.basic_sku_code LIKE CONCAT('%', #{basicSkuCode}, '%')
    </if>
    <if test="managerId != null">
        AND JSON_EXTRACT(b.manager, '$.userId') = #{managerId}
    </if>
    </script>
    """)
    Map<String, Object> getTotalMsgByConditions(@Param("venderName")String venderName,
                                                @Param("companyId") Long companyId,
                                                @Param("invoiceCode")String invoiceCode,
                                                @Param("startTime") LocalDateTime startTime,
                                                @Param("endTime") LocalDateTime endTime,
                                                @Param("status") Integer status,
                                                @Param("neStatus") Integer neStatus,
                                                @Param("idList") Long[] idList,
                                                @Param("shipInvoiceCode") String shipInvoiceCode,
                                                @Param("invoicSkuName") String invoicSkuName,
                                                @Param("hsCode") String hsCode,
                                                @Param("saleContractCode") String saleContractCode,
                                                @Param("purchaseContractCode") String purchaseContractCode,
                                                @Param("hsMeasureUnit") String hsMeasureUnit,
                                                @Param("basicSkuCode") String basicSkuCode,
                                                @Param("managerId") Long managerId
                                                );
}