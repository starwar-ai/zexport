package com.syj.eplus.module.crm.service.custbankaccount;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.crm.api.cust.CustApi;
import com.syj.eplus.module.crm.api.cust.dto.CustBankAccountDTO;
import com.syj.eplus.module.crm.api.cust.dto.SimpleCustRespDTO;
import com.syj.eplus.module.crm.controller.admin.custbankaccount.vo.CustBankaccountPageReqVO;
import com.syj.eplus.module.crm.controller.admin.custbankaccount.vo.CustBankaccountSaveReqVO;
import com.syj.eplus.module.crm.convert.CustBankAccountConvert;
import com.syj.eplus.module.crm.dal.dataobject.custbankaccount.CustBankaccountDO;
import com.syj.eplus.module.crm.dal.mysql.custbankaccount.CustBankaccountMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.crm.enums.ErrorCodeConstants.BANK_POC_BELONG_CUST;
import static com.syj.eplus.module.crm.enums.ErrorCodeConstants.CUST_BANKACCOUNT_NOT_EXISTS;

/**
 * 银行账户信息 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class CustBankaccountServiceImpl implements CustBankaccountService {

    @Resource
    private CustBankaccountMapper custBankaccountMapper;

    @Resource
    private CustApi custApi;

    @Override
    public Long createCustBankaccount(CustBankaccountSaveReqVO createReqVO) {
        // 插入
        CustBankaccountDO custBankaccount = BeanUtils.toBean(createReqVO, CustBankaccountDO.class);
        custBankaccountMapper.insert(custBankaccount);
        // 返回
        return custBankaccount.getId();
    }

    @Override
    public void updateCustBankaccount(CustBankaccountSaveReqVO updateReqVO) {
        // 校验存在
//        validateCustBankaccountExists(updateReqVO.getId());
        // 更新
        CustBankaccountDO updateObj = BeanUtils.toBean(updateReqVO, CustBankaccountDO.class);
        custBankaccountMapper.updateById(updateObj);
    }

    @Override
    public void deleteCustBankaccount(Long id) {
        // 校验存在
        validateCustBankaccountExists(id);
        // 删除
        custBankaccountMapper.deleteById(id);
    }

    @Override
    public void deleteCustBankaccountByCustId(Long custId) {
        // 删除
        custBankaccountMapper.delete(CustBankaccountDO::getCustId, custId);
    }

    private void validateCustBankaccountExists(Long id) {
        if (custBankaccountMapper.selectById(id) == null) {
            throw exception(CUST_BANKACCOUNT_NOT_EXISTS);
        }
    }

    @Override
    public CustBankaccountDO getCustBankaccount(Long id) {
        return custBankaccountMapper.selectById(id);
    }

    @Override
    public PageResult<CustBankaccountDO> getCustBankaccountPage(CustBankaccountPageReqVO pageReqVO) {
        return custBankaccountMapper.selectPage(pageReqVO);
    }

    @Override
    public void batchSaveBankAccount(List<CustBankaccountDO> custBankaccountDOList) {
        if (CollUtil.isEmpty(custBankaccountDOList)) {
            return;
        }
        custBankaccountMapper.insertBatch(custBankaccountDOList);
    }

    @Override
    public List<CustBankaccountDO> getBankAccountListByCustId(Long custId) {
        return custBankaccountMapper.selectList(new LambdaQueryWrapperX<CustBankaccountDO>().eq(CustBankaccountDO::getCustId, custId));
    }

    @Override
    public void batchDeleteBankAccountList(List<Long> idList) {
        custBankaccountMapper.deleteBatchIds(idList);
    }

    @Override
    public List<CustBankaccountDO> getBankAccountListByCustCodeList(List<Long> custIdList) {
        return custBankaccountMapper.selectList(new LambdaQueryWrapperX<CustBankaccountDO>().in(CustBankaccountDO::getCustId, custIdList));
    }

    @Override
    public Long getCustIdByBankPoc(String bankPoc) {
        List<CustBankaccountDO> custBankaccountDOList = custBankaccountMapper.selectList(CustBankaccountDO::getBankPoc, bankPoc);
        if (CollUtil.isEmpty(custBankaccountDOList)) {
            return null;
        }
        List<Long> custIdList = custBankaccountDOList.stream().map(CustBankaccountDO::getCustId).distinct().toList();
        if (custIdList.size() > 1) {
            throw exception(BANK_POC_BELONG_CUST);
        }
        return custIdList.get(0);
    }

    @Override
    public List<CustBankAccountDTO> getBankPocListByTitle(String title) {
        List<CustBankaccountDO> custBankaccountDOS = custBankaccountMapper.selectList(new LambdaQueryWrapperX<CustBankaccountDO>().likeIfPresent(CustBankaccountDO::getBankPoc, title));
        if (CollUtil.isEmpty(custBankaccountDOS)){
            return List.of();
        }
        List<CustBankAccountDTO> custBankAccountDTOs =  CustBankAccountConvert.INSTANCE.convertCustBankAccountDTOList(custBankaccountDOS);
        List<Long> custIdList = custBankAccountDTOs.stream().map(CustBankAccountDTO::getCustId).distinct().toList();
        List<SimpleCustRespDTO> simpleCustRespDTOList = custApi.getSimpleCustRespDTO(custIdList);
        Map<Long, SimpleCustRespDTO> custMap = simpleCustRespDTOList.stream().collect(Collectors.toMap(SimpleCustRespDTO::getId, s -> s));
        custBankAccountDTOs.forEach(item->{
            SimpleCustRespDTO  simpleCustRespDTO = custMap.get(item.getCustId());
            if(simpleCustRespDTO!=null){
                item.setCustName(simpleCustRespDTO.getName());
            }
        });
        return custBankAccountDTOs;
    }
}