package com.syj.eplus.module.crm.convert;

import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import com.syj.eplus.framework.common.entity.BankAccount;
import com.syj.eplus.module.crm.api.cust.dto.CustBankAccountDTO;
import com.syj.eplus.module.crm.dal.dataobject.custbankaccount.CustBankaccountDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface CustBankAccountConvert {
    CustBankAccountConvert INSTANCE = Mappers.getMapper(CustBankAccountConvert.class);

    List<BankAccount> convertBankAccount(List<CustBankaccountDO> custBankaccountDO);

    CustBankAccountDTO convertCustBankAccountDTO(CustBankaccountDO custBankaccountDO);

    default List<CustBankAccountDTO> convertCustBankAccountDTOList(List<CustBankaccountDO> custBankaccountDOList){
        return CollectionUtils.convertList(custBankaccountDOList, s->{
            return convertCustBankAccountDTO(s);
        });
    }
}
