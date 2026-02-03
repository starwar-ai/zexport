package com.syj.eplus.module.scm.convert;

import com.syj.eplus.framework.common.entity.BankAccount;
import com.syj.eplus.module.scm.dal.dataobject.venderbankaccount.VenderBankaccountDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface VenderBankaccountConvert {

    VenderBankaccountConvert INSTANCE = Mappers.getMapper(VenderBankaccountConvert.class);

    List<BankAccount> convertVenderBankAccountDTO(List<VenderBankaccountDO> venderBankaccountDO);
}
