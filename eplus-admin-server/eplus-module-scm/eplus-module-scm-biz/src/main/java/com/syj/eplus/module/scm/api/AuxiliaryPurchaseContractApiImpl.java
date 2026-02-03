package com.syj.eplus.module.scm.api;

import com.syj.eplus.module.scm.api.AuxiliaryPurchaseContract.AuxiliaryPurchaseContractApi;
import com.syj.eplus.module.scm.service.purchasecontract.PurchaseContractService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class AuxiliaryPurchaseContractApiImpl implements AuxiliaryPurchaseContractApi {

    @Resource
    @Lazy
    PurchaseContractService purchaseContractService;





}
