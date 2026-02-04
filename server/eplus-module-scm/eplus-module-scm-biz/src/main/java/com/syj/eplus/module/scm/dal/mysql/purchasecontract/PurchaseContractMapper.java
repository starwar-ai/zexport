package com.syj.eplus.module.scm.dal.mysql.purchasecontract;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.MPJLambdaWrapperX;
import com.syj.eplus.module.scm.controller.admin.purchasecontract.vo.PurchaseContractPageReqVO;
import com.syj.eplus.module.scm.dal.dataobject.purchasecontract.PurchaseContractDO;
import com.syj.eplus.module.scm.dal.dataobject.purchasecontractitem.PurchaseContractItemDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Objects;

/**
 * 采购合同 Mapper
 *
 * @author zhangcm
 */
@Mapper
public interface PurchaseContractMapper extends BaseMapperX<PurchaseContractDO> {

    default PageResult<PurchaseContractDO> selectPage(PurchaseContractPageReqVO reqVO) {

        MPJLambdaWrapperX<PurchaseContractDO> purchaseContractDOLambdaQueryWrapperX = new MPJLambdaWrapperX<PurchaseContractDO>()
                .selectAll(PurchaseContractDO.class)
                .eqIfPresent(PurchaseContractDO::getVer, reqVO.getVer())
                .likeIfPresent(PurchaseContractDO::getCode, reqVO.getCode())
                .likeIfPresent(PurchaseContractDO::getCode, reqVO.getPurchaseContractCode())
                .eqIfPresent(PurchaseContractDO::getConfirmFlag, reqVO.getConfirmFlag())
                .eqIfPresent(PurchaseContractDO::getRepeatFlag, reqVO.getRepeatFlag())
                .inIfPresent(PurchaseContractDO::getVenderCode, reqVO.getVenderCodeList())
                .neIfPresent(PurchaseContractDO::getCode,reqVO.getExcludePurchaseContract())
                .inIfPresent(PurchaseContractDO::getCustCode, reqVO.getCustCodeList())
                .eqIfPresent(PurchaseContractDO::getAuditStatus, reqVO.getAuditStatus())
                .eqIfPresent(PurchaseContractDO::getContractStatus, reqVO.getContractStatus())
                .eqIfPresent(PurchaseContractDO::getTotalAmount, reqVO.getTotalAmount())
                .eqIfPresent(PurchaseContractDO::getTotalQuantity, reqVO.getTotalQuantity())
                .eqIfPresent(PurchaseContractDO::getPrintFlag, reqVO.getPrintFlag())
                .eqIfPresent(PurchaseContractDO::getPrintTimes, reqVO.getPrintTimes())
                .eqIfPresent(PurchaseContractDO::getPrepayStatus, reqVO.getPrepayStatus())
                .eqIfPresent(PurchaseContractDO::getPrepayAmount, reqVO.getPrepayAmount())
                .eqIfPresent(PurchaseContractDO::getPayStatus, reqVO.getPayStatus())
                .eqIfPresent(PurchaseContractDO::getPayedAmount, reqVO.getPayedAmount())
                .eqIfPresent(PurchaseContractDO::getInvoiceStatus, reqVO.getInvoiceStatus())
                .eqIfPresent(PurchaseContractDO::getInvoicedAmount, reqVO.getInvoicedAmount())
                .eqIfPresent(PurchaseContractDO::getPurchaseUserId, reqVO.getPurchaseUserId())
                .likeIfPresent(PurchaseContractDO::getPurchaseUserName, reqVO.getPurchaseUserName())
                .eqIfPresent(PurchaseContractDO::getPurchaseUserDeptId, reqVO.getPurchaseUserDeptId())
                .likeIfPresent(PurchaseContractDO::getPurchaseUserDeptName, reqVO.getPurchaseUserDeptName())
                .eqIfPresent(PurchaseContractDO::getCustId, reqVO.getCustId())
                .eqIfPresent(PurchaseContractDO::getVenderId, reqVO.getVenderId())
                .eqIfPresent(PurchaseContractDO::getStockId, reqVO.getStockId())
                .likeIfPresent(PurchaseContractDO::getStockCode, reqVO.getStockCode())
                .likeIfPresent(PurchaseContractDO::getVenderCode, reqVO.getVenderCode())
                .likeIfPresent(PurchaseContractDO::getVenderName, reqVO.getVenderName())
                .likeIfPresent(PurchaseContractDO::getCustCode, reqVO.getCustCode())
                .eqIfPresent(PurchaseContractDO::getSignBackFlag, reqVO.getSignBackFlag())
                .likeIfPresent(PurchaseContractDO::getStockName, reqVO.getStockName())
                .eqIfPresent(PurchaseContractDO::getPurchasePlanId, reqVO.getPurchasePlanId())
                .likeIfPresent(PurchaseContractDO::getPurchasePlanCode, reqVO.getPurchasePlanCode())
                .eqIfPresent(PurchaseContractDO::getSaleContractId, reqVO.getSaleContractId())
                .likeIfPresent(PurchaseContractDO::getSaleContractCode, reqVO.getSaleContractCode())
                .likeIfPresent(PurchaseContractDO::getRemark, reqVO.getRemark())
                .betweenIfPresent(PurchaseContractDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(PurchaseContractDO::getAnnex, reqVO.getAnnex())
                .eqIfPresent(PurchaseContractDO::getCompanyId, reqVO.getCompanyId())
                .betweenIfPresent(PurchaseContractDO::getPurchaseTime, reqVO.getPurchaseTime())
                .eqIfPresent(PurchaseContractDO::getPaymentId, reqVO.getPaymentId())
                .likeIfPresent(PurchaseContractDO::getPaymentName, reqVO.getPaymentName())
                .eqIfPresent(PurchaseContractDO::getPortId, reqVO.getPortId())
                .eqIfPresent(PurchaseContractDO::getFreight, reqVO.getFreight())
                .eqIfPresent(PurchaseContractDO::getOtherCost, reqVO.getOtherCost())
                .eqIfPresent(PurchaseContractDO::getTaxType, reqVO.getTaxType())
                .eqIfPresent(PurchaseContractDO::getEquallyType, reqVO.getEquallyType())
                .inIfPresent(PurchaseContractDO::getDeleted,reqVO.getDeleteFlag())
                .inIfPresent(PurchaseContractDO::getAuxiliaryFlag,reqVO. getAuxiliaryFlag())
                .eqIfPresent(PurchaseContractDO::getAutoFlag,reqVO. getAutoFlag())
                .betweenIfPresent(PurchaseContractDO::getDeliveryDate, reqVO.getDeliveryDate())
                .inIfPresent(PurchaseContractDO::getContractStatus, reqVO.getContractStatusList())
                .neIfPresent(PurchaseContractDO::getContractStatus, reqVO.getNeStatus())
                .inIfPresent(PurchaseContractDO::getId, reqVO.getIdList())
                .inIfPresent(PurchaseContractDO::getCustId, reqVO.getCustIdList())
                .eqIfPresent(PurchaseContractDO::getCurrency, reqVO.getCurrency())
                .eqIfPresent(PurchaseContractDO::getPaymentId, reqVO.getPaymentId())
                .eqIfPresent(PurchaseContractDO::getBoxWithColor, reqVO.getBoxWithColor())
                .eqIfPresent(PurchaseContractDO::getSampleCount, reqVO.getSampleCount())
                .orderByDesc(PurchaseContractDO::getPurchaseTime);
                if (Objects.nonNull(reqVO.getTotalAmountRangeMin())) {
                    purchaseContractDOLambdaQueryWrapperX.apply(
                            "JSON_EXTRACT(total_amount, '$.amount') >= {0}", reqVO.getTotalAmountRangeMin()
                    );
                }
                if (Objects.nonNull(reqVO.getTotalAmountRangeMax())) {
                    purchaseContractDOLambdaQueryWrapperX.apply(
                            "JSON_EXTRACT(total_amount, '$.amount') <= {0}", reqVO.getTotalAmountRangeMax()
                    );
                }
                if (Objects.nonNull(reqVO.getTotalAmountRmbRangeMin())) {
                    purchaseContractDOLambdaQueryWrapperX.apply(
                            "JSON_EXTRACT(total_amount_rmb, '$.amount') >= {0}", reqVO.getTotalAmountRmbRangeMin()
                    );
                }
                if (Objects.nonNull(reqVO.getTotalAmountRmbRangeMax())) {
                    purchaseContractDOLambdaQueryWrapperX.apply(
                            "JSON_EXTRACT(total_amount_rmb, '$.amount') <= {0}", reqVO.getTotalAmountRmbRangeMax()
                    );
                }
                if(Objects.nonNull(reqVO.getManagerId())){
                    purchaseContractDOLambdaQueryWrapperX.apply("JSON_EXTRACT(manager, '$.userId') = {0}", reqVO.getManagerId());
                }
                if(Objects.nonNull(reqVO.getSalesUserId())){
                    purchaseContractDOLambdaQueryWrapperX.apply("JSON_EXTRACT(sales, '$.userId') = {0}", reqVO.getSalesUserId());
                }
                purchaseContractDOLambdaQueryWrapperX.leftJoin(PurchaseContractItemDO.class,PurchaseContractItemDO::getPurchaseContractId,PurchaseContractDO::getId);
                if (StrUtil.isNotEmpty(reqVO.getBasicSkuCode())){
                    purchaseContractDOLambdaQueryWrapperX.like(PurchaseContractItemDO::getBasicSkuCode, reqVO.getBasicSkuCode());
                }
                if (StrUtil.isNotEmpty(reqVO.getCskuCode())){
                    purchaseContractDOLambdaQueryWrapperX.like(PurchaseContractItemDO::getCskuCode, reqVO.getCskuCode());
                }
                if (StrUtil.isNotEmpty(reqVO.getSkuCode())){
                    purchaseContractDOLambdaQueryWrapperX.like(PurchaseContractItemDO::getSkuCode, reqVO.getSkuCode());
                }
                if (StrUtil.isNotEmpty(reqVO.getName())){
                    purchaseContractDOLambdaQueryWrapperX.like(PurchaseContractItemDO::getSkuName, reqVO.getName());
                }
                if (Objects.nonNull(reqVO.getWithTaxPriceMin())) {
                    purchaseContractDOLambdaQueryWrapperX.apply(
                            "JSON_EXTRACT(with_tax_price, '$.amount') >= {0}", reqVO.getWithTaxPriceMin()
                    );
                }
                if (Objects.nonNull(reqVO.getWithTaxPriceMax())) {
                    purchaseContractDOLambdaQueryWrapperX.apply(
                            "JSON_EXTRACT(with_tax_price, '$.amount') <= {0}", reqVO.getWithTaxPriceMax()
                    );
                }
                if (Objects.nonNull(reqVO.getWithTaxPriceRmbMin())) {
                    purchaseContractDOLambdaQueryWrapperX.apply(
                            "JSON_EXTRACT(total_price_rmb, '$.amount') / NULLIF(quantity, 0) >= {0}",
                            reqVO.getWithTaxPriceRmbMin()
                    );
                }
                if (Objects.nonNull(reqVO.getWithTaxPriceRmbMax())) {
                    purchaseContractDOLambdaQueryWrapperX.apply(
                            "JSON_EXTRACT(total_price_rmb, '$.amount') / NULLIF(quantity, 0) <= {0}",
                            reqVO.getWithTaxPriceRmbMax()
                    );
                }
                if (StrUtil.isNotEmpty(reqVO.getAuxiliaryPurchaseContractCode())){
                    purchaseContractDOLambdaQueryWrapperX.like(PurchaseContractItemDO::getAuxiliaryPurchaseContractCode, reqVO.getAuxiliaryPurchaseContractCode());
                }
                if (StrUtil.isNotEmpty(reqVO.getAuxiliarySaleContractCode())){
                    purchaseContractDOLambdaQueryWrapperX.like(PurchaseContractItemDO::getAuxiliarySaleContractCode, reqVO.getAuxiliarySaleContractCode());
                }
                purchaseContractDOLambdaQueryWrapperX.groupBy(PurchaseContractDO::getId);
        return selectJoinPage(reqVO,PurchaseContractDO.class,purchaseContractDOLambdaQueryWrapperX);
    }

    @Select("SELECT id FROM scm_purchase_contract WHERE purchase_plan_id =  #{planId} ")
    List<Long> getIdListByPlanId(@Param("planId") Long planId);


    @Select("""
            select count(1) from scm_purchase_contract a where a.id =#{id} and  a.deleted=0 and( exists(
            	select 1 from qms_quality_inspection e where a.id = e.purchase_contract_id and e.deleted = 0 and e.quality_inspection_status !=8
             ) or  exists(
            	select 1 from scm_invoicing_notices e where a.code COLLATE utf8mb4_unicode_ci = e.pur_order_code COLLATE utf8mb4_unicode_ci and e.deleted = 0 and e.audit_status !=10
            ) or  exists(
                select 1 from scm_payment_apply e where FIND_IN_SET(a.code COLLATE utf8mb4_unicode_ci, e.purchase_contract_code_list COLLATE utf8mb4_unicode_ci) > 0 and e.deleted = 0 AND e.audit_status !=10
            ))
            """)
    Long validateAntiAuditStatus(@Param("id") Long id);

}