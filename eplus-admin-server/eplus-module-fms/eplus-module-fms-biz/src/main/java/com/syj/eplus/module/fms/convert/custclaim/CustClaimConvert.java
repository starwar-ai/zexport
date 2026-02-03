package com.syj.eplus.module.fms.convert.custclaim;

import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import com.syj.eplus.framework.common.enums.PayeeTypeEnum;
import com.syj.eplus.module.crm.api.cust.dto.SimpleCustResp;
import com.syj.eplus.module.fms.dal.dataobject.custclaim.PayeeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @Description：
 * @Author：du
 * @Date：2024/7/24 17:46
 */
@Mapper
public interface CustClaimConvert {
    CustClaimConvert INSTANCE = Mappers.getMapper(CustClaimConvert.class);


    @Mapping(target = "payeeCode",source = "code")
    @Mapping(target = "payeeName",source = "name")
    @Mapping(target = "manager",source = "managerList")
    @Mapping(target = "claimTotalAmount",ignore = true)
    @Mapping(target = "currency",ignore = true)
    PayeeEntity convertPayeeEntity(SimpleCustResp simpleCustResp);


    default List<PayeeEntity> convertPayeeEntityList(List<SimpleCustResp> simpleCustRespList,Long registrationId){
        return CollectionUtils.convertList(simpleCustRespList,s->{
            PayeeEntity payeeEntity = convertPayeeEntity(s);
            payeeEntity.setRegistrationId(registrationId);
            payeeEntity.setPayeeType(PayeeTypeEnum.CUST.getType());
            return payeeEntity;
        });
    }
}
