package com.syj.eplus.module.fms.service.bankregistration;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import com.syj.eplus.framework.common.entity.SimpleData;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.framework.common.enums.BooleanEnum;
import com.syj.eplus.module.crm.api.cust.CustApi;
import com.syj.eplus.module.crm.api.cust.dto.CustBankAccountDTO;
import com.syj.eplus.module.crm.api.cust.dto.SimpleCustResp;
import com.syj.eplus.module.fms.controller.admin.bankregistration.vo.*;
import com.syj.eplus.module.fms.convert.bankregistration.BankRegistrationConvert;
import com.syj.eplus.module.fms.dal.dataobject.bankregistration.BankRegistrationDO;
import com.syj.eplus.module.fms.dal.mysql.bankregistration.BankRegistrationMapper;
import com.syj.eplus.module.infra.api.CodeGeneratorApi;
import com.syj.eplus.module.scm.api.vender.VenderApi;
import com.syj.eplus.module.scm.api.vender.dto.SimpleVenderResp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.fms.api.payment.enums.ErrorCodeConstants.BANK_REGISTRATION_NOT_EXISTS;

/**
 * 银行登记 Service 实现类
 *
 * @author du
 */
@Service
@Validated
public class BankRegistrationServiceImpl implements BankRegistrationService {

protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private BankRegistrationMapper bankRegistrationMapper;
    @Resource
    private CodeGeneratorApi codeGeneratorApi;
    @Resource
    private CustApi custApi;

    @Resource
    private VenderApi venderApi;

    @Resource
    private AdminUserApi adminUserApi;
private static final String SN_TYPE = "${table.snType}";
private static final String CODE_PREFIX = "SK";


    @Override
    public void createBankRegistration(BankRegistrationCreateReq createReqVO) {
        List<BankRegistrationSaveReqVO> bankRegistrationList = createReqVO.getBankRegistrationList();
        Integer submitFlag = createReqVO.getSubmitFlag();
        List<BankRegistrationDO> bankRegistrationDOList = BankRegistrationConvert.INSTANCE.convertBankRegistrationDoList(bankRegistrationList);
        bankRegistrationDOList.forEach(s -> {
            // 生成 银行登记 编号
            s.setCode(codeGeneratorApi.getCodeGenerator(SN_TYPE, CODE_PREFIX));
            if (BooleanEnum.YES.getValue().equals(submitFlag)){
                s.setStatus(BooleanEnum.YES.getValue());
            }else {
                s.setStatus(BooleanEnum.NO.getValue());
            }
            List<SimpleCustResp> simpleCustRespByBankPoc = custApi.getSimpleCustRespByBankPoc(s.getCompanyTitle());
            if (CollUtil.isEmpty(simpleCustRespByBankPoc)){
                return;
            }
            // 业务员
            List<UserDept> managers = new ArrayList<>(simpleCustRespByBankPoc.stream()
                    .map(SimpleCustResp::getManagerList)
                    .flatMap(List::stream)
                    .collect(Collectors.toMap(
                            UserDept::getUserId,
                            userDept -> userDept,
                            (o, n) -> o
                    )).values());
            // 客户
            List<SimpleData> custList = BeanUtils.toBean(simpleCustRespByBankPoc, SimpleData.class);
            s.setManagerList(managers);
            s.setCustList(custList);
        });
        if (BooleanEnum.YES.getValue().equals(submitFlag)){
            // 回款认领增加数据
        }
        // 插入
        bankRegistrationMapper.insertBatch(bankRegistrationDOList);
    }

    @Override
    public void updateBankRegistration(BankRegistrationSaveReqVO updateReqVO) {
        // 校验存在
        validateBankRegistrationExists(updateReqVO.getId());
        // 更新
BankRegistrationDO updateObj = BankRegistrationConvert.INSTANCE.convertBankRegistrationDO(updateReqVO);
        bankRegistrationMapper.updateById(updateObj);
    }

    @Override
    public void deleteBankRegistration(Long id) {
        // 校验存在
        validateBankRegistrationExists(id);
        // 删除
        bankRegistrationMapper.deleteById(id);
    }

    private void validateBankRegistrationExists(Long id) {
        if (bankRegistrationMapper.selectById(id) == null) {
            throw exception(BANK_REGISTRATION_NOT_EXISTS);
        }
    }
    @Override
public BankRegistrationRespVO getBankRegistration(Long id) {
    BankRegistrationDO bankRegistrationDO = bankRegistrationMapper.selectById(id);
if (bankRegistrationDO == null) {
return null;
}
return BankRegistrationConvert.INSTANCE.convertBankRegistrationRespVO(bankRegistrationDO);
    }

    @Override
    public PageResult<BankRegistrationDO> getBankRegistrationPage(BankRegistrationPageReqVO pageReqVO) {
        return bankRegistrationMapper.selectPage(pageReqVO);
    }

    @Override
    public SimpleRegistrationResp getSimpleRegistrationRespByBankPoc(String bankPoc) {
//        List<SimpleCustResp> simpleCustRespByBankPoc = custApi.getSimpleCustRespByBankPoc(bankPoc);
//        if (Objects.nonNull(simpleCustRespByBankPoc)){
//            return BeanUtils.toBean(simpleCustRespByBankPoc,SimpleRegistrationResp.class);
//        }
        SimpleVenderResp simpleVenderRespByBankPoc = venderApi.getSimpleVenderRespByBankPoc(bankPoc);
        if (Objects.nonNull(simpleVenderRespByBankPoc)){
            return BeanUtils.toBean(simpleVenderRespByBankPoc,SimpleRegistrationResp.class);
        }
        return null;
    }

    @Override
    public List<CustBankAccountDTO> getBankPocListByTitle(String title) {
        return custApi.getBankPocListByTitle(title);
    }

}