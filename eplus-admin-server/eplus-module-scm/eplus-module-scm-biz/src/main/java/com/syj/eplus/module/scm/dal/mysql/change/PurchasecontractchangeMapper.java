package com.syj.eplus.module.scm.dal.mysql.change;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.scm.controller.admin.purchasecontract.vo.ChangePurchasePageReq;
import com.syj.eplus.module.scm.entity.PurchaseContractChange;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Description：
 * @Author：du
 * @Date：2024/5/23 15:37
 */
@Mapper
public interface PurchasecontractchangeMapper extends BaseMapperX<PurchaseContractChange> {

    default PageResult<PurchaseContractChange> selectPage(ChangePurchasePageReq reqVO) {
        LambdaQueryWrapperX<PurchaseContractChange> queryWrapperX = new LambdaQueryWrapperX<PurchaseContractChange>();
        
        // 基础查询条件
        queryWrapperX.likeIfPresent(PurchaseContractChange::getCode, reqVO.getCode());
        queryWrapperX.likeIfPresent(PurchaseContractChange::getContractCode, reqVO.getContractCode());
        queryWrapperX.eqIfPresent(PurchaseContractChange::getAuditStatus, reqVO.getAuditStatus());
        queryWrapperX.betweenIfPresent(PurchaseContractChange::getCreateTime, reqVO.getCreateTime());
        queryWrapperX.eqIfPresent(PurchaseContractChange::getCreator, reqVO.getCreator());
        queryWrapperX.eqIfPresent(PurchaseContractChange::getAuxiliaryFlag, reqVO.getAuxiliaryFlag());
        // 供应商相关查询
        queryWrapperX.likeIfPresent(PurchaseContractChange::getVenderName, reqVO.getVenderName());
        // 采购员相关查询
        queryWrapperX.eqIfPresent(PurchaseContractChange::getPurchaseUserId, reqVO.getPurchaseUserId());
        queryWrapperX.likeIfPresent(PurchaseContractChange::getPurchaseUserName, reqVO.getPurchaseUserName());
        queryWrapperX.eqIfPresent(PurchaseContractChange::getPurchaseUserDeptId, reqVO.getPurchaseDeptId());
        queryWrapperX.likeIfPresent(PurchaseContractChange::getPurchaseUserDeptName, reqVO.getPurchaseDeptName());
        // 关联合同查询
        queryWrapperX.likeIfPresent(PurchaseContractChange::getSaleContractCode, reqVO.getSaleContractCode());
        queryWrapperX.likeIfPresent(PurchaseContractChange::getSourceCode, reqVO.getSourceCode());

        // 产品编号
        if (StrUtil.isNotEmpty(reqVO.getSkuCode())) {
            queryWrapperX.apply("JSON_SEARCH(children, 'one', CONCAT('%', {0}, '%'), NULL, '$[*].skuCode') IS NOT NULL", reqVO.getSkuCode());
        }

        // 关联销售合同号
        if (StrUtil.isNotEmpty(reqVO.getAuxiliarySaleContractCode())) {
            queryWrapperX.apply("JSON_SEARCH(children, 'one', CONCAT('%', {0}, '%'), NULL, '$[*].auxiliarySaleContractCode') IS NOT NULL", reqVO.getAuxiliarySaleContractCode());
        }

        // 关联采购合同号
        if (StrUtil.isNotEmpty(reqVO.getAuxiliaryPurchaseContractCode())) {
            queryWrapperX.apply("JSON_SEARCH(children, 'one', CONCAT('%', {0}, '%'), NULL, '$[*].auxiliaryPurchaseContractCode') IS NOT NULL", reqVO.getAuxiliaryPurchaseContractCode());
        }

        queryWrapperX.orderByDesc(PurchaseContractChange::getId);
        return selectPage(reqVO, queryWrapperX);
    }
}
