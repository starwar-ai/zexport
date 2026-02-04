package com.syj.eplus.module.scm.dal.mysql.purchaseplanitem;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.framework.common.enums.BooleanEnum;
import com.syj.eplus.module.scm.controller.admin.purchaseplanitem.vo.PurchasePlanItemPageReqVO;
import com.syj.eplus.module.scm.dal.dataobject.purchaseplanitem.PurchaseItemSummaryDO;
import com.syj.eplus.module.scm.dal.dataobject.purchaseplanitem.PurchasePlanItemDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 采购计划明细 Mapper
 *
 * @author zhangcm
 */
@Mapper
public interface PurchasePlanItemMapper extends BaseMapperX<PurchasePlanItemDO> {

    default PageResult<PurchasePlanItemDO> selectPage(PurchasePlanItemPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<PurchasePlanItemDO>()
                .eqIfPresent(PurchasePlanItemDO::getVer, reqVO.getVer())
                .eqIfPresent(PurchasePlanItemDO::getSortNum, reqVO.getSortNum())
                .eqIfPresent(PurchasePlanItemDO::getSkuId, reqVO.getSkuId())
                .likeIfPresent(PurchasePlanItemDO::getSkuCode, reqVO.getSkuCode())
                .eqIfPresent(PurchasePlanItemDO::getCustId, reqVO.getCustId())
                .likeIfPresent(PurchasePlanItemDO::getCustCode, reqVO.getCustCode())
                .likeIfPresent(PurchasePlanItemDO::getCskuCode, reqVO.getCskuCode())
                .eqIfPresent(PurchasePlanItemDO::getVenderId, reqVO.getVenderId())
                .likeIfPresent(PurchasePlanItemDO::getVenderCode, reqVO.getVenderCode())
                .eqIfPresent(PurchasePlanItemDO::getStockId, reqVO.getStockId())
                .likeIfPresent(PurchasePlanItemDO::getStockCode, reqVO.getStockCode())
                .eqIfPresent(PurchasePlanItemDO::getPurchasePlanId, reqVO.getPurchasePlanId())
                .likeIfPresent(PurchasePlanItemDO::getPurchasePlanCode, reqVO.getPurchasePlanCode())
                .eqIfPresent(PurchasePlanItemDO::getPackagingPrice, reqVO.getPackagingPrice())
                .eqIfPresent(PurchasePlanItemDO::getShippingPrice, reqVO.getShippingPrice())
                .eqIfPresent(PurchasePlanItemDO::getUnitPrice, reqVO.getUnitPrice())
                .eqIfPresent(PurchasePlanItemDO::getTotalPrice, reqVO.getTotalPrice())
                .eqIfPresent(PurchasePlanItemDO::getWithTaxPrice, reqVO.getWithTaxPrice())
                .eqIfPresent(PurchasePlanItemDO::getTaxRate, reqVO.getTaxRate())
                .eqIfPresent(PurchasePlanItemDO::getPurchaseType, reqVO.getPurchaseType())
                .eqIfPresent(PurchasePlanItemDO::getQtyPerInnerbox, reqVO.getQtyPerInnerbox())
                .eqIfPresent(PurchasePlanItemDO::getQtyPerOuterbox, reqVO.getQtyPerOuterbox())
                .eqIfPresent(PurchasePlanItemDO::getPackageLength, reqVO.getPackageLength())
                .eqIfPresent(PurchasePlanItemDO::getPackageWidth, reqVO.getPackageWidth())
                .eqIfPresent(PurchasePlanItemDO::getPackageHeight, reqVO.getPackageHeight())
                .eqIfPresent(PurchasePlanItemDO::getPackageUnit, reqVO.getPackageUnit())
                .eqIfPresent(PurchasePlanItemDO::getSingleGrossweight, reqVO.getSingleGrossweight())
                .eqIfPresent(PurchasePlanItemDO::getRemark, reqVO.getRemark())
                .eqIfPresent(PurchasePlanItemDO::getPurchaseUserId, reqVO.getPurchaseUserId())
                .eqIfPresent(PurchasePlanItemDO::getPurchaseUserName, reqVO.getPurchaseUserName())
                .eqIfPresent(PurchasePlanItemDO::getPurchaseUserDeptId, reqVO.getPurchaseUserDeptId())
                .eqIfPresent(PurchasePlanItemDO::getPurchaseUserDeptName, reqVO.getPurchaseUserDeptName())
                .likeIfPresent(PurchasePlanItemDO::getVenderProdCode, reqVO.getVenderProdCode())
                .eqIfPresent(PurchasePlanItemDO::getQuoteDate, reqVO.getQuoteDate())
                .eqIfPresent(PurchasePlanItemDO::getFreightFlag, reqVO.getFreightFlag())
                .eqIfPresent(PurchasePlanItemDO::getPackageFlag, reqVO.getPackageFlag())
                .eqIfPresent(PurchasePlanItemDO::getPackageType, reqVO.getPackageType())
                .eqIfPresent(PurchasePlanItemDO::getCurrency, reqVO.getCurrency())
                .eqIfPresent(PurchasePlanItemDO::getFaxFlag, reqVO.getFaxFlag())
                .eqIfPresent(PurchasePlanItemDO::getMoq, reqVO.getMoq())
                .eqIfPresent(PurchasePlanItemDO::getFreeFlag, reqVO.getFreeFlag())
                .eqIfPresent(PurchasePlanItemDO::getSkuType, reqVO.getSkuType())
                .eqIfPresent(PurchasePlanItemDO::getConvertedFlag, reqVO.getConvertedFlag())
                .betweenIfPresent(PurchasePlanItemDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(PurchasePlanItemDO::getPurchaseModel, reqVO.getPurchaseModel())
                .eqIfPresent(PurchasePlanItemDO::getBoxCount, reqVO.getBoxCount())
                .eqIfPresent(PurchasePlanItemDO::getRemark, reqVO.getRemark())
                .eqIfPresent(PurchasePlanItemDO::getSaleContractId, reqVO.getSaleContractId())
                .eqIfPresent(PurchasePlanItemDO::getSaleContractCode, reqVO.getSaleContractCode())
                .eqIfPresent(PurchasePlanItemDO::getAuxiliarySkuFlag, reqVO.getAuxiliarySkuFlag())
                .eqIfPresent(PurchasePlanItemDO::getAuxiliarySkuType, reqVO.getAuxiliarySkuType())
                .likeIfPresent(PurchasePlanItemDO::getAuxiliaryPurchaseContractCode, reqVO.getAuxiliaryPurchaseContractCode())
                .likeIfPresent(PurchasePlanItemDO::getAuxiliarySaleContractCode, reqVO.getAuxiliarySaleContractCode())
                .eqIfPresent(PurchasePlanItemDO::getAuxiliarySkuId, reqVO.getAuxiliarySkuId())
                .eqIfPresent(PurchasePlanItemDO::getAuxiliarySkuCode, reqVO.getAuxiliarySkuCode())
                .likeIfPresent(PurchasePlanItemDO::getAuxiliaryCskuCode, reqVO.getAuxiliaryCskuCode())
                .likeIfPresent(PurchasePlanItemDO::getSpecRemark, reqVO.getSpecRemark())
                .eqIfPresent(PurchasePlanItemDO::getPurchaseUrl, reqVO.getPurchaseUrl())
                .ne(PurchasePlanItemDO::getCancelFlag, BooleanEnum.YES.getValue())
                .orderByDesc(PurchasePlanItemDO::getId));
    }


    @Select("""     
            <script>
            SELECT
            	JSON_EXTRACT( i.with_tax_price, '$.currency' ) AS currency,
            	SUM( i.need_pur_quantity ) AS sumQty,
            	SUM(JSON_EXTRACT( i.with_tax_price, '$.amount' ) * i.need_pur_quantity) AS sumTotal
           FROM
                scm_purchase_plan_item i
            	LEFT JOIN scm_purchase_plan p ON p.id = i.purchase_plan_id
           WHERE
                i.deleted = 0
                and p.deleted = 0
                <when test= 'planStatus != null and planStatus != 3 and planStatus != 5'>
                   and p.plan_status = #{planStatus,jdbcType=TINYINT}
                </when>
                 <when test= 'planStatus == 3'>
                   and p.plan_status = 3
                   and i.converted_flag = 0
                </when>
               <when test= 'planStatus == 5'>
                   and (p.plan_status = 3   or p.plan_status = 5)
                   and i.converted_flag = 1
                </when>
                 <when test= 'auxiliaryFlag != null'>
                   and p.auxiliary_flag = #{auxiliaryFlag,jdbcType=TINYINT}
                </when>
          
           GROUP BY
            	JSON_EXTRACT(i.with_tax_price,'$.currency')
            </script> 
            """)
    List<PurchaseItemSummaryDO> getPurchaseItemSummary(@Param("planStatus")Integer planStatus,@Param("auxiliaryFlag")Integer auxiliaryFlag);
}