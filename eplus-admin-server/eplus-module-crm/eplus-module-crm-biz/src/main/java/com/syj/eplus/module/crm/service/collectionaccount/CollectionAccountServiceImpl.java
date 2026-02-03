package com.syj.eplus.module.crm.service.collectionaccount;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.infra.api.company.CompanyApi;
import com.syj.eplus.module.infra.api.company.dto.CompanyBankRespDTO;
import com.syj.eplus.module.crm.api.cust.CustApi;
import com.syj.eplus.module.crm.api.cust.dto.CollectionAccountDTO;
import com.syj.eplus.module.crm.api.cust.dto.CustAllDTO;
import com.syj.eplus.module.crm.controller.admin.collectionaccount.vo.CollectionAccountPageReqVO;
import com.syj.eplus.module.crm.controller.admin.collectionaccount.vo.CollectionAccountRespVO;
import com.syj.eplus.module.crm.controller.admin.collectionaccount.vo.CollectionAccountSaveReqVO;
import com.syj.eplus.module.crm.convert.collectionaccount.CollectionAccountConvert;
import com.syj.eplus.module.crm.dal.dataobject.collectionaccount.CollectionAccountDO;
import com.syj.eplus.module.crm.dal.mysql.collectionaccount.CollectionAccountMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.infra.enums.ErrorCodeConstants.COMPANY_NOT_EXISTS;
import static com.syj.eplus.module.crm.enums.ErrorCodeConstants.COLLECTION_ACCOUNT_NOT_EXISTS;
import static com.syj.eplus.module.crm.enums.ErrorCodeConstants.CUST_NOT_EXISTS;

/**
 * 收款账号 Service 实现类
 *
 * @author zhangcm
 */
@Service
@Validated
public class CollectionAccountServiceImpl implements CollectionAccountService {

protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private CollectionAccountMapper collectionAccountMapper;
    @Resource
    private CustApi custApi;
    @Resource
    private CompanyApi companyApi;

    @Override
    public Long createCollectionAccount(CollectionAccountSaveReqVO createReqVO) {
CollectionAccountDO collectionAccount = CollectionAccountConvert.INSTANCE.convertCollectionAccountDO(createReqVO);
        // 插入
        collectionAccountMapper.insert(collectionAccount);
        // 返回
        return collectionAccount.getId();
    }

    @Override
    public void updateCollectionAccount(CollectionAccountSaveReqVO updateReqVO) {
        // 校验存在
        validateCollectionAccountExists(updateReqVO.getId());
        // 更新
CollectionAccountDO updateObj = CollectionAccountConvert.INSTANCE.convertCollectionAccountDO(updateReqVO);
        collectionAccountMapper.updateById(updateObj);
    }

    @Override
    public void deleteCollectionAccount(Long id) {
        // 校验存在
        validateCollectionAccountExists(id);
        // 删除
        collectionAccountMapper.deleteById(id);
    }

    private void validateCollectionAccountExists(Long id) {
        if (collectionAccountMapper.selectById(id) == null) {
            throw exception(COLLECTION_ACCOUNT_NOT_EXISTS);
        }
    }
    @Override
public CollectionAccountRespVO getCollectionAccount(Long id) {
    CollectionAccountDO collectionAccountDO = collectionAccountMapper.selectById(id);
if (collectionAccountDO == null) {
return null;
}
return CollectionAccountConvert.INSTANCE.convertCollectionAccountRespVO(collectionAccountDO);
    }

    @Override
    public PageResult<CollectionAccountDO> getCollectionAccountPage(CollectionAccountPageReqVO pageReqVO) {
        return collectionAccountMapper.selectPage(pageReqVO);
    }

    @Override
    public void batchInsert(List<CollectionAccountDO> collectionAccountList) {
        if(CollUtil.isEmpty(collectionAccountList)){
            return;
        }
        collectionAccountList.forEach(s->{
            s.setId(null);
        });
        collectionAccountMapper.insertBatch(collectionAccountList);
    }

    @Override
    public void deleteCollectionAccountByCustCode(Long custId) {
        collectionAccountMapper.delete(CollectionAccountDO::getCustId,custId);
    }

    @Override
    public List<CollectionAccountRespVO> getCollectionAccountListByCustCode(Long custId) {
        List<CollectionAccountDO> doList = collectionAccountMapper.selectList(CollectionAccountDO::getCustId, custId);
        if(CollUtil.isEmpty(doList)){
            return null;
        }
       Map<Long, CompanyBankRespDTO> dtoMap =  companyApi.getCompanyBankDTOMap();

        List<CollectionAccountRespVO> voList = BeanUtils.toBean(doList, CollectionAccountRespVO.class);
        voList.forEach(s->{
            if(CollUtil.isNotEmpty(dtoMap)){
                CompanyBankRespDTO companyBankRespDTO = dtoMap.get(s.getCompanyBankId());
                if(Objects.nonNull(companyBankRespDTO)){
                    s.setCompanyName(companyBankRespDTO.getCompanyName());
                    s.setCompanyNameEng(companyBankRespDTO.getCompanyNameEng());
                    s.setBankName(companyBankRespDTO.getBankName());
                    s.setBankNameEng(companyBankRespDTO.getBankNameEng());
                    s.setBankAddress(companyBankRespDTO.getBankAddress());
                    s.setBankAddressEng(companyBankRespDTO.getBankAddressEng());
                    s.setBankCode(companyBankRespDTO.getBankCode());
                    s.setSwiftCode(companyBankRespDTO.getSwiftCode());
                }
            }
        });
        return voList;
    }

    @Override
    public Map<String, CollectionAccountDTO> getMapByCodeList(List<String> codeList,Long companyId) {
        if(CollUtil.isEmpty(codeList)){
            return null;
        }
        List<CollectionAccountDO> doList = collectionAccountMapper.selectList(CollectionAccountDO::getCustCode, codeList);
        if(CollUtil.isEmpty(doList)){
            return null;
        }
        Map<Long, CompanyBankRespDTO> companyBankDTOMap =  companyApi.getCompanyBankDTOMap();
        List<CollectionAccountDTO> voList = BeanUtils.toBean(doList, CollectionAccountDTO.class);
        if (CollUtil.isNotEmpty(companyBankDTOMap)) {
            return null;
        }

        Map<String, List<CollectionAccountDTO>> listMap = voList.stream().collect(Collectors.groupingBy(CollectionAccountDTO::getCustCode));
        if(CollUtil.isEmpty(listMap)){
            return null;
        }
        Map<String, CollectionAccountDTO> result = new HashMap<>();
        listMap.forEach((k,v)->{
            //取客户设置的默认值
            Optional<CollectionAccountDTO> first = v.stream().filter(s -> s.getDefaultFlag() == 1 && Objects.equals(s.getCompanyId(), companyId)).findFirst();
            if(first.isPresent()){
                CollectionAccountDTO collectionAccountDTO = first.get();
                CompanyBankRespDTO companyBankRespDTO = companyBankDTOMap.get(collectionAccountDTO.getCompanyBankId());
                if(Objects.nonNull(companyBankRespDTO)){
                    collectionAccountDTO.setCompanyName(companyBankRespDTO.getCompanyName());
                    collectionAccountDTO.setCompanyNameEng(companyBankRespDTO.getCompanyNameEng());
                    collectionAccountDTO.setBankName(companyBankRespDTO.getBankName());
                    collectionAccountDTO.setBankNameEng(companyBankRespDTO.getBankNameEng());
                    collectionAccountDTO.setBankAddress(companyBankRespDTO.getBankAddress());
                    collectionAccountDTO.setBankAddressEng(companyBankRespDTO.getBankAddressEng());
                    collectionAccountDTO.setBankCode(companyBankRespDTO.getBankCode());
                    collectionAccountDTO.setSwiftCode(companyBankRespDTO.getSwiftCode());
                    collectionAccountDTO.setDefaultFlag(companyBankRespDTO.getDefaultFlag());
                }
                result.put(k,collectionAccountDTO);
            }else { //没有默认值，取主体的默认值
                Optional<CompanyBankRespDTO> deFaultFirst = companyBankDTOMap.values().stream().filter(s -> Objects.equals(s.getCompanyId(), companyId) && s.getDefaultFlag() == 1).findFirst();
                if(deFaultFirst.isPresent()){
                    CollectionAccountDTO dto = BeanUtils.toBean(deFaultFirst.get(), CollectionAccountDTO.class);
                    dto.setCustCode(k);
                    dto.setCompanyId(companyId).setCompanyBankId(dto.getId());
                    dto.setId(null);
                    result.put(k,dto);
                }
            }
        });

        return result;
    }

    @Override
    public CollectionAccountRespVO getCollectionAccountByCust(String custCode, Long companyId) {
        if (Objects.isNull(companyId)) {
            throw exception(COMPANY_NOT_EXISTS);
        }
        if(StrUtil.isEmpty(custCode)){
            throw exception(CUST_NOT_EXISTS);
        }
        Optional<CollectionAccountDO> first = collectionAccountMapper.selectList(new LambdaQueryWrapperX<CollectionAccountDO>()
                .eq(CollectionAccountDO::getCompanyId, companyId)
                .eq(CollectionAccountDO::getCustCode, custCode)).stream().findFirst();
        Map<Long, CompanyBankRespDTO> companyBankDTOMap =  companyApi.getCompanyBankDTOMap();
        CustAllDTO custDTO = custApi.getCustByCode(custCode);
        if(first.isPresent()){   //存在返回
            CollectionAccountRespVO vo = BeanUtils.toBean(first.get(), CollectionAccountRespVO.class);
            CompanyBankRespDTO companyBankRespDTO = companyBankDTOMap.get(vo.getCompanyBankId());
            if(Objects.nonNull(companyBankRespDTO)){
                vo.setCompanyName(companyBankRespDTO.getCompanyName());
                vo.setCompanyNameEng(companyBankRespDTO.getCompanyNameEng());
                vo.setBankName(companyBankRespDTO.getBankName());
                vo.setBankNameEng(companyBankRespDTO.getBankNameEng());
                vo.setBankAddress(companyBankRespDTO.getBankAddress());
                vo.setBankAddressEng(companyBankRespDTO.getBankAddressEng());
                vo.setBankCode(companyBankRespDTO.getBankCode());
                vo.setSwiftCode(companyBankRespDTO.getSwiftCode());
                if (Objects.nonNull(custDTO)) {
                    vo.setCurrency(custDTO.getCurrency()).setSettlementTermType(custDTO.getSettlementTermType());
                }
            }
            return vo;
        }else {  //不存在返回主体默认账号
            Optional<CompanyBankRespDTO> deFaultFirst = companyBankDTOMap.values().stream().filter(s -> Objects.equals(s.getCompanyId(), companyId) && s.getDefaultFlag() == 1).findFirst();
            if(deFaultFirst.isPresent()){
                CollectionAccountRespVO vo = BeanUtils.toBean(deFaultFirst.get(), CollectionAccountRespVO.class);
                vo.setCustCode(custCode);
                vo.setCompanyId(companyId).setCompanyBankId(vo.getId());
                vo.setId(null);
                if (Objects.nonNull(custDTO)) {
                    vo.setCurrency(custDTO.getCurrency()).setSettlementTermType(custDTO.getSettlementTermType());
                }
                return vo;
            }
          return null;
        }
    }
}