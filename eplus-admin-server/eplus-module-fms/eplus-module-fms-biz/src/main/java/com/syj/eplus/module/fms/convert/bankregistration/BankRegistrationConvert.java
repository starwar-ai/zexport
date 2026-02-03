package com.syj.eplus.module.fms.convert.bankregistration;

import com.syj.eplus.module.fms.controller.admin.bankregistration.vo.BankRegistrationRespVO;
import com.syj.eplus.module.fms.controller.admin.bankregistration.vo.BankRegistrationSaveReqVO;
import com.syj.eplus.module.fms.dal.dataobject.bankregistration.BankRegistrationDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;


@Mapper
public interface BankRegistrationConvert {

        BankRegistrationConvert INSTANCE = Mappers.getMapper(BankRegistrationConvert.class);

        BankRegistrationRespVO convert(BankRegistrationDO bankRegistrationDO);

        default BankRegistrationRespVO convertBankRegistrationRespVO(BankRegistrationDO bankRegistrationDO){
                BankRegistrationRespVO bankRegistrationRespVO = convert(bankRegistrationDO);
                return bankRegistrationRespVO;
        }

    BankRegistrationDO convertBankRegistrationDO(BankRegistrationSaveReqVO saveReqVO);

    List<BankRegistrationDO> convertBankRegistrationDoList(List<BankRegistrationSaveReqVO> saveReqVOList);
}