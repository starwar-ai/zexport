package com.syj.eplus.module.scm.convert.purchaseregistration;

import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import com.syj.eplus.module.scm.controller.admin.purchaseregistration.vo.PurchaseRegistrationItemResp;
import com.syj.eplus.module.scm.controller.admin.purchaseregistration.vo.PurchaseRegistrationRespVO;
import com.syj.eplus.module.scm.controller.admin.purchaseregistration.vo.PurchaseRegistrationSaveReqVO;
import com.syj.eplus.module.scm.dal.dataobject.purchaseregistration.PurchaseRegistrationDO;
import com.syj.eplus.module.scm.dal.dataobject.purchaseregistrationitem.PurchaseRegistrationItem;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;
import java.util.Objects;


@Mapper
public interface PurchaseRegistrationConvert {

    PurchaseRegistrationConvert INSTANCE = Mappers.getMapper(PurchaseRegistrationConvert.class);

    PurchaseRegistrationRespVO convert(PurchaseRegistrationDO purchaseRegistrationDO);

    default PurchaseRegistrationRespVO convertPurchaseRegistrationRespVO(PurchaseRegistrationDO purchaseRegistrationDO) {
        PurchaseRegistrationRespVO purchaseRegistrationRespVO = convert(purchaseRegistrationDO);
        return purchaseRegistrationRespVO;
    }

    PurchaseRegistrationDO convertPurchaseRegistrationDO(PurchaseRegistrationSaveReqVO saveReqVO);

    PurchaseRegistrationItemResp convertRegistrationItemResp(PurchaseRegistrationItem purchaseRegistrationItem);

    default List<PurchaseRegistrationItemResp> convertRegistrationItemRespList(List<PurchaseRegistrationItem> purchaseRegistrationItemList, Map<Long, PurchaseRegistrationDO> registrationDOMap) {
        return CollectionUtils.convertList(purchaseRegistrationItemList, s -> {
            PurchaseRegistrationItemResp purchaseRegistrationItemResp = convertRegistrationItemResp(s);
            PurchaseRegistrationDO purchaseRegistrationDO = registrationDOMap.get(s.getRegistrationId());
            if (Objects.isNull(purchaseRegistrationDO)) {
                return purchaseRegistrationItemResp;
            }
            purchaseRegistrationItemResp.setCode(purchaseRegistrationDO.getCode());
            purchaseRegistrationItemResp.setInvoiceCode(purchaseRegistrationDO.getInvoiceCode());
            purchaseRegistrationItemResp.setCompanyId(purchaseRegistrationDO.getCompanyId());
            purchaseRegistrationItemResp.setCompanyName(purchaseRegistrationDO.getCompanyName());
            purchaseRegistrationItemResp.setVenderCode(purchaseRegistrationDO.getVenderCode());
            purchaseRegistrationItemResp.setVenderName(purchaseRegistrationDO.getVenderName());
            purchaseRegistrationItemResp.setInputDate(purchaseRegistrationDO.getInputDate());
            purchaseRegistrationItemResp.setInvoicedDate(purchaseRegistrationDO.getInvoicedDate());
            purchaseRegistrationItemResp.setInputUser(purchaseRegistrationDO.getInputUser());
            purchaseRegistrationItemResp.setInvoiceAmount(purchaseRegistrationDO.getInvoiceAmount());
            purchaseRegistrationItemResp.setAuditStatus(purchaseRegistrationDO.getAuditStatus());
            return purchaseRegistrationItemResp;
        });
    }

}