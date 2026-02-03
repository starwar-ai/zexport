package com.syj.eplus.module.scm.dal.mysql.purchaseregistration;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.scm.controller.admin.purchaseregistration.vo.PurchaseRegistrationPageReqVO;
import com.syj.eplus.module.scm.dal.dataobject.purchaseregistration.PurchaseRegistrationDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 采购跟单登记 Mapper
 *
 * @author du
 */
@Mapper
public interface PurchaseRegistrationMapper extends BaseMapperX<PurchaseRegistrationDO> {
    default PageResult<PurchaseRegistrationDO> selectPage(PurchaseRegistrationPageReqVO reqVO) {
        LambdaQueryWrapperX<PurchaseRegistrationDO> queryWrapperX = new LambdaQueryWrapperX<PurchaseRegistrationDO>()
                .eqIfPresent(PurchaseRegistrationDO::getCompanyId, reqVO.getCompanyId())
                .eqIfPresent(PurchaseRegistrationDO::getStatus, reqVO.getStatus())
                .neIfPresent(PurchaseRegistrationDO::getStatus, reqVO.getNeStatus())
                .likeIfPresent(PurchaseRegistrationDO::getVenderName, reqVO.getVenderName())
                .eqIfPresent(PurchaseRegistrationDO::getInvoiceCode, reqVO.getInvoiceCode())
                .betweenIfPresent(PurchaseRegistrationDO::getCreateTime, reqVO.getCreateTime())
                .inIfPresent(PurchaseRegistrationDO::getId, reqVO.getIdList())
                .orderByDesc(PurchaseRegistrationDO::getId);
        return selectPage(reqVO, queryWrapperX);
    }
}