package com.syj.eplus.module.fms.service.bankregistration;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.syj.eplus.module.crm.api.cust.dto.CustBankAccountDTO;
import com.syj.eplus.module.fms.controller.admin.bankregistration.vo.*;
import com.syj.eplus.module.fms.dal.dataobject.bankregistration.BankRegistrationDO;

import javax.validation.Valid;
import java.util.List;

/**
 * 银行登记 Service 接口
 *
 * @author du
 */
public interface BankRegistrationService {

    /**
     * 创建银行登记
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    void createBankRegistration(BankRegistrationCreateReq createReqVO);

    /**
     * 更新银行登记
     *
     * @param updateReqVO 更新信息
     */
    void updateBankRegistration(@Valid BankRegistrationSaveReqVO updateReqVO);

    /**
     * 删除银行登记
     *
     * @param id 编号
     */
    void deleteBankRegistration(Long id);

    /**
     * 获得银行登记
     *
* @param  id 编号 
     * @return 银行登记
     */
BankRegistrationRespVO getBankRegistration(Long id);

    /**
     * 获得银行登记分页
     *
     * @param pageReqVO 分页查询
     * @return 银行登记分页
     */
    PageResult<BankRegistrationDO> getBankRegistrationPage(BankRegistrationPageReqVO pageReqVO);


    /**
     * 根据银行抬头获取对应业务单元精简信息
     * @param bankPoc 银行抬头
     * @return 业务单元精简信息
     */
    SimpleRegistrationResp getSimpleRegistrationRespByBankPoc(String bankPoc);

    /**
     * 根据抬头模糊查询银行联系人
     * @param title 抬头
     * @return 银行联系人列表
     */
    List<CustBankAccountDTO> getBankPocListByTitle(String title);

}