package com.syj.eplus.module.scm.service.venderbankaccount;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.syj.eplus.module.scm.controller.admin.venderbankaccount.vo.VenderBankaccountPageReqVO;
import com.syj.eplus.module.scm.controller.admin.venderbankaccount.vo.VenderBankaccountSaveReqVO;
import com.syj.eplus.module.scm.dal.dataobject.venderbankaccount.VenderBankaccountDO;

import javax.validation.Valid;
import java.util.List;

/**
 * 供应商银行账户 Service 接口
 *
 * @author zhangcm
 */
public interface VenderBankaccountService {

    /**
     * 创建供应商银行账户
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createVenderBankaccount(@Valid VenderBankaccountSaveReqVO createReqVO);

    /**
     * 更新供应商银行账户
     *
     * @param updateReqVO 更新信息
     */
    void updateVenderBankaccount(@Valid VenderBankaccountSaveReqVO updateReqVO);

    /**
     * 删除供应商银行账户
     *
     * @param id 编号
     */
    void deleteVenderBankaccount(Long id);

    /**
     * 获得供应商银行账户
     *
     * @param id 编号
     * @return 供应商银行账户
     */
    VenderBankaccountDO getVenderBankaccount(Long id);

    /**
     * 获得供应商银行账户分页
     *
     * @param pageReqVO 分页查询
     * @return 供应商银行账户分页
     */
    PageResult<VenderBankaccountDO> getVenderBankaccountPage(VenderBankaccountPageReqVO pageReqVO);

    /**
     * 批量新增供应商银行账号
     *
     * @param venderBankaccountDOList
     */
    void batchSaveBankAccount(List<VenderBankaccountDO> venderBankaccountDOList);

    /**
     * 获取供应商银行账号
     *
     * @param venderId
     * @reeturn
     */
    List<VenderBankaccountDO> getBankAccountListByVenderId(Long venderId);

    /**
     * 批量删除供应商银行账户信息
     *
     * @param idList
     */
    void batchDeleteBankAccountList(List<Long> idList);

    /**
     * 根据供应商编号批量获取供应商银行账户信息
     *
     * @param venderIdList 供应商id列表
     * @return 供应商银行账户信息
     */
    List<VenderBankaccountDO> getBankAccountListByVenderCodeList(List<Long> venderIdList);


    /**
     * 根据银行抬头获取供应商id
     *
     * @param bankPoc 银行抬头
     * @return 供应商id
     */
    Long geVenderIdByBankPoc(String bankPoc);

    VenderBankaccountDO getBankAccountByBankAccount(String bankAccount,String bank);

    /**
     * 根据供应商id获取银行信息
     * @param venderId 供应商id
     * @return 银行信息
     */
    VenderBankaccountDO getBankAccountByVenderCode(Long venderId);
}