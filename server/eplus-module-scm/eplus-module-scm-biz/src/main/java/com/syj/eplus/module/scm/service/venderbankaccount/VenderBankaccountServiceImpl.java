package com.syj.eplus.module.scm.service.venderbankaccount;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.framework.common.enums.BooleanEnum;
import com.syj.eplus.module.scm.controller.admin.venderbankaccount.vo.VenderBankaccountPageReqVO;
import com.syj.eplus.module.scm.controller.admin.venderbankaccount.vo.VenderBankaccountSaveReqVO;
import com.syj.eplus.module.scm.dal.dataobject.venderbankaccount.VenderBankaccountDO;
import com.syj.eplus.module.scm.dal.mysql.venderbankaccount.VenderBankaccountMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.scm.enums.ErrorCodeConstants.BANK_POC_BELONG_VENDER;
import static com.syj.eplus.module.scm.enums.ErrorCodeConstants.VENDER_BANKACCOUNT_NOT_EXISTS;

/**
 * 供应商银行账户 Service 实现类
 *
 * @author zhangcm
 */
@Service
@Validated
public class VenderBankaccountServiceImpl implements VenderBankaccountService {

    @Resource
    private VenderBankaccountMapper venderBankaccountMapper;

    @Override
    public Long createVenderBankaccount(VenderBankaccountSaveReqVO createReqVO) {
        // 插入
        VenderBankaccountDO venderBankaccount = BeanUtils.toBean(createReqVO, VenderBankaccountDO.class);
        venderBankaccountMapper.insert(venderBankaccount);
        // 返回
        return venderBankaccount.getId();
    }

    @Override
    public void updateVenderBankaccount(VenderBankaccountSaveReqVO updateReqVO) {
        // 校验存在
//        validateVenderBankaccountExists(updateReqVO.getId());
        // 更新
        VenderBankaccountDO updateObj = BeanUtils.toBean(updateReqVO, VenderBankaccountDO.class);
        venderBankaccountMapper.updateById(updateObj);
    }

    @Override
    public void deleteVenderBankaccount(Long id) {
        // 校验存在
        validateVenderBankaccountExists(id);
        // 删除
        venderBankaccountMapper.deleteById(id);
    }

    private void validateVenderBankaccountExists(Long id) {
        if (venderBankaccountMapper.selectById(id) == null) {
            throw exception(VENDER_BANKACCOUNT_NOT_EXISTS);
        }
    }

    @Override
    public VenderBankaccountDO getVenderBankaccount(Long id) {
        return venderBankaccountMapper.selectById(id);
    }

    @Override
    public PageResult<VenderBankaccountDO> getVenderBankaccountPage(VenderBankaccountPageReqVO pageReqVO) {
        return venderBankaccountMapper.selectPage(pageReqVO);
    }

    @Override
    public void batchSaveBankAccount(List<VenderBankaccountDO> venderBankaccountDOList) {
        if (CollUtil.isEmpty(venderBankaccountDOList)) {
            return;
        }
        venderBankaccountMapper.insertBatch(venderBankaccountDOList);
    }

    @Override
    public List<VenderBankaccountDO> getBankAccountListByVenderId(Long venderId) {
        return venderBankaccountMapper.selectList(new LambdaQueryWrapperX<VenderBankaccountDO>().eq(VenderBankaccountDO::getVenderId, venderId));
    }

    @Override
    public void batchDeleteBankAccountList(List<Long> idList) {
        venderBankaccountMapper.deleteBatchIds(idList);
    }

    @Override
    public List<VenderBankaccountDO> getBankAccountListByVenderCodeList(List<Long> venderIdList) {
        return venderBankaccountMapper.selectList(new LambdaQueryWrapperX<VenderBankaccountDO>().in(VenderBankaccountDO::getVenderId, venderIdList));
    }

    @Override
    public Long geVenderIdByBankPoc(String bankPoc) {
        List<VenderBankaccountDO> venderBankaccountDOList = venderBankaccountMapper.selectList(VenderBankaccountDO::getBankPoc, bankPoc);
        if (CollUtil.isEmpty(venderBankaccountDOList)) {
            return null;
        }
        List<Long> custIdList = venderBankaccountDOList.stream().map(VenderBankaccountDO::getVenderId).distinct().toList();
        if (custIdList.size() > 1) {
            throw exception(BANK_POC_BELONG_VENDER);
        }
        return custIdList.get(0);
    }

    @Override
    public VenderBankaccountDO getBankAccountByBankAccount(String bankAccount,String bank) {
        return venderBankaccountMapper.selectOne(VenderBankaccountDO::getBankAccount, bankAccount, VenderBankaccountDO::getBank, bank);
    }

    @Override
    public VenderBankaccountDO getBankAccountByVenderCode(Long venderId) {
        if (Objects.isNull(venderId)){
            return null;
        }
        return venderBankaccountMapper.selectOne(new LambdaQueryWrapperX<VenderBankaccountDO>().eq(VenderBankaccountDO::getVenderId, venderId).eq(VenderBankaccountDO::getDefaultFlag, BooleanEnum.YES.getValue()));
    }

}