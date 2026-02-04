package com.syj.eplus.module.crm.service.custbankaccount;


import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.syj.eplus.module.crm.api.cust.dto.CustBankAccountDTO;
import com.syj.eplus.module.crm.controller.admin.custbankaccount.vo.CustBankaccountPageReqVO;
import com.syj.eplus.module.crm.controller.admin.custbankaccount.vo.CustBankaccountSaveReqVO;
import com.syj.eplus.module.crm.dal.dataobject.custbankaccount.CustBankaccountDO;

import javax.validation.Valid;
import java.util.List;

/**
 * 银行账户信息 Service 接口
 *
 * @author 芋道源码
 */
public interface CustBankaccountService {

    /**
     * 创建银行账户信息
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createCustBankaccount(@Valid CustBankaccountSaveReqVO createReqVO);

    /**
     * 更新银行账户信息
     *
     * @param updateReqVO 更新信息
     */
    void updateCustBankaccount(@Valid CustBankaccountSaveReqVO updateReqVO);

    /**
     * 删除银行账户信息
     *
     * @param id
     */
    void deleteCustBankaccount(Long id);

    /**
     * 根据客户id删除银行账户信息
     *
     * @param custId 客户id
     *
     */
    void deleteCustBankaccountByCustId(Long custId);

    /**
     * 获得银行账户信息
     *
     * @param id 编号
     * @return 银行账户信息
     */
    CustBankaccountDO getCustBankaccount(Long id);

    /**
     * 获得银行账户信息分页
     *
     * @param pageReqVO 分页查询
     * @return 银行账户信息分页
     */
    PageResult<CustBankaccountDO> getCustBankaccountPage(CustBankaccountPageReqVO pageReqVO);

    /**
     * 批量新增银行账户信息
     *
     * @param custBankaccountDOList
     */
    void batchSaveBankAccount(List<CustBankaccountDO> custBankaccountDOList);

    /**
     * 通过客户id获得银行账户信息列表(此时不需要关注版本号，版本变更id必定会变更)
     *
     * @param custId
     * @return
     */
    List<CustBankaccountDO> getBankAccountListByCustId(Long custId);

    /**
     * 批量删除银行账户信息
     *
     * @param idList
     */
    void batchDeleteBankAccountList(List<Long> idList);

    /**
     * 根据客户编号批量获取客户银行账户信息
     *
     * @param custIdList 客户id列表
     * @return 客户银行账户信息
     */
    List<CustBankaccountDO> getBankAccountListByCustCodeList(List<Long> custIdList);

    /**
     * 根据银行抬头获取客户id
     *
     * @param bankPoc 银行抬头
     * @return 客户id
     */
    Long getCustIdByBankPoc(String bankPoc);

    /**
     * 根据抬头模糊查询银行联系人
     * @param title 抬头
     * @return 银行联系人列表
     */
    List<CustBankAccountDTO> getBankPocListByTitle(String title);
}