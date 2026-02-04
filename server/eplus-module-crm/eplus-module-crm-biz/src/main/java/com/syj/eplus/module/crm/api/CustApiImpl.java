package com.syj.eplus.module.crm.api;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.ConfirmSourceEntity;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.syj.eplus.framework.common.entity.BaseData;
import com.syj.eplus.framework.common.entity.BaseValue;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.framework.common.enums.BooleanEnum;
import com.syj.eplus.module.crm.api.cust.CustApi;
import com.syj.eplus.module.crm.api.cust.dto.*;
import com.syj.eplus.module.crm.controller.admin.collectionaccount.vo.CollectionAccountRespVO;
import com.syj.eplus.module.crm.dal.dataobject.cust.CustDO;
import com.syj.eplus.module.crm.dal.dataobject.mark.MarkDO;
import com.syj.eplus.module.crm.service.collectionaccount.CollectionAccountService;
import com.syj.eplus.module.crm.service.cust.CustService;
import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Description：
 * @Author：du
 * @Date：2024/2/22 15:41
 */
@Service
public class CustApiImpl implements CustApi {
    @Resource
    @Lazy
    private CustService custService;

    @Resource
    @Lazy
    private CollectionAccountService collectionAccountService;

    @Override
    public Map<String, String> getCustNameCache(String code) {
        return custService.getCustNameCache(code);
    }

    @Override
    public List<SimpleCustRespDTO> getSimpleCustRespDTO(List<Long> idList) {
        return custService.getSimpleCustRespDTOList(idList);
    }

    @Override
    public Map<Long, SimpleCustRespDTO> getSimpleCustRespDTOMap(List<Long> idList) {
        List<SimpleCustRespDTO> simpleCustRespDTOList = custService.getSimpleCustRespDTOList(idList);
        return simpleCustRespDTOList.stream().collect(Collectors.toMap(SimpleCustRespDTO::getId, Function.identity()));
    }

    @Override
    @DataPermission(enable = false)
    public SimpleCustRespDTO getSimpleCustByInternalCompanyId(Long internalCompanyId) {
        LambdaQueryWrapper<CustDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CustDO::getInternalCompanyId, internalCompanyId).last("limit 1");
        CustDO custDO = custService.getOne(queryWrapper);
        if(Objects.isNull(custDO)){
            return null;
        }
        List<BaseValue> currencyList = custDO.getCurrencyList();
        SimpleCustRespDTO result = BeanUtils.toBean(custDO, SimpleCustRespDTO.class);
        currencyList.stream().filter(s-> Objects.equals(s.getDefaultFlag(), BooleanEnum.YES.getValue())).findFirst().ifPresent(s->result.setCurrency(s.getValue()));
        return result;
    }

    @Override
    public List<SimpleCustResp> getSimpleCustRespByBankPoc(String bankPoc) {
        return custService.getSimpleCustRespByBankPoc(bankPoc);
    }

    @Override
    public void createCustTitle(Map<String,Long> notExistCustCodeMap, String custTitle) {
        custService.createCustTitle(notExistCustCodeMap, custTitle);
    }

    @Override
    @DataPermission(enable = false)
    public Map<String, CustAllDTO> getCustByCodeList(List<String> codeList) {
        if (CollUtil.isEmpty(codeList)){
            return Map.of();
        }
        List<CustDO> custDos = custService.getCustByCodeList(codeList);
        List<CustAllDTO> list = BeanUtils.toBean(custDos, CustAllDTO.class);

        if (list == null) {
           return  null;
        }
        Map<String, CustDO> doMap = custDos.stream().collect(Collectors.toMap(CustDO::getCode, s -> s, (s1, s2) -> s1));
        list.forEach(s->{
            CustDO custDO = doMap.get(s.getCode());
            if(Objects.isNull(custDO) || CollUtil.isEmpty(custDO.getManagerList()))
                return;
            List<UserDept> userDepts = custDO.getManagerList().stream()
                    .map(baseValue -> {
                        UserDept userDept = new UserDept();
                        try {
                            userDept.setUserId(Long.parseLong(baseValue.getValue()));
                        } catch (NumberFormatException ignored) {}
                        userDept.setNickname(baseValue.getName());
                        userDept.setDeptId(baseValue.getDeptId());
                        userDept.setDeptName(baseValue.getDeptName());
                        return userDept;
                    })
                    .toList();
            s.setManagerList(userDepts);
        });
        return  list.stream().collect(Collectors.toMap(CustAllDTO::getCode, s -> s , (o,n)->n));
    }

    @Override
    public List<String> getCodeListByName(String custName) {
        return custService.getCodeListByName(custName);

    }

    /**
     * 根据客户编号获得最新的客户变更资料
     *
     * @param code
     * @return 客户资料
     */
    @Override
    public CustAllDTO getCustChangeByCode(String code) {
        return BeanUtils.toBean(custService.getCustChangeByCode(code), CustAllDTO.class);
    }

    @Override
    public List<ConfirmSourceEntity> getConfirmSourceList(String targetCode, Integer effectRangeType) {
        return custService.getConfirmSourceList(targetCode, effectRangeType);
    }

    @Override
    public List<String> getCodeListByCountryCode(Long countryId, String regionCode) {

        return custService.getCodeListByCountryCode(countryId,regionCode);
    }

    @Override
    public CustAllDTO getCustByCode(String custCode) {
        return custService.getCustByCode(custCode);
    }

    @Override
    public List<CustMarkDTO> getMarkListByCustCode(Long custId) {
        List<MarkDO> voList = custService.getMarkListByCustCode(custId);
        return BeanUtils.toBean(voList,CustMarkDTO.class);
    }

    @Override
    public CollectionAccountDTO getCollectionAccountByCust(String custCode, Long companyId) {

        CollectionAccountRespVO collectionAccountByCust = collectionAccountService.getCollectionAccountByCust(custCode, companyId);
        if(Objects.isNull(collectionAccountByCust)){
            return null;
        }
        return BeanUtils.toBean(collectionAccountByCust,CollectionAccountDTO.class);
    }

    @Override
    public List<String> getCodeListByNameList(List<String> nameList) {

        return custService.getCodeListByNameList(nameList);
    }

    @Override
    public List<CustBankAccountDTO> getBankPocListByTitle(String title) {
        return custService.getBankPocListByTitle(title);
    }

    @Override
    public List<SimpleCustResp> getSimpleCustRespByCode(List<String> cutCodeList) {
        return custService.getSimpleCustRespByCode(cutCodeList);
    }

    @Override
    public List<SimpleCustResp> getSimpleCustRespByName(String custName) {
        return custService.getSimpleCustRespByName(custName);
    }
    
    @Override
    public void deleteCustTitle(List<Long> payeeEntityIds) {
       custService.deleteCustTitle(payeeEntityIds);
    }

    @Override
    public boolean checkIsInternalCust(String custCode) {
        if (StrUtil.isEmpty(custCode)){
            return false;
        }
        return custService.checkIsInternalCust(custCode);
    }

    @Override
    public Long getInternalCompany(String custCode) {
        return custService.getInternalCompany(custCode);
    }

    @Override
    public List<SystemCollectionPlanDTO> getCollectionPlanDTOByCustCode(String custCode) {
        return custService.getCollectionPlanDTOByCustCode(custCode);
    }

    @Override
    public String getBankPocByCustCode(String custCode) {
        return custService.getBankPocByCustCode(custCode);
    }

    @Override
    public String getCustPocByCustCode(String custCode) {
        return custService.getCustPocByCustCode(custCode);
    }

    @Override
    public Map<Long, CustDTO> getCustByIds(List<Long> custIds) {
        Map<Long, CustDO> custDOMap = custService.getCustByIds(custIds);
        if(CollUtil.isEmpty(custDOMap)){
            return null;
        }
        return custDOMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> BeanUtils.toBean(entry.getValue(), CustDTO.class)
                ));
    }

    @Override
    public Map<Long, BaseData> getAllInnerCustCache() {
        return custService.getAllInnerCustCache();
    }
}
