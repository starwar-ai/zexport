package com.syj.eplus.module.crm.service.cust;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Tuple;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.dict.CommonDict;
import cn.iocoder.yudao.framework.common.pojo.ConfirmSourceEntity;
import cn.iocoder.yudao.framework.common.pojo.JsonEffectRange;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.common.util.string.StrUtils;
import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.operatelog.core.util.ChangeCompareUtil;
import cn.iocoder.yudao.framework.operatelog.core.util.DiffUtil;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.bpm.api.task.BpmProcessInstanceApi;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import cn.iocoder.yudao.module.bpm.enums.task.BpmProcessInstanceResultEnum;
import com.syj.eplus.module.infra.api.company.CompanyApi;
import com.syj.eplus.module.infra.api.company.dto.SimpleCompanyDTO;
import com.syj.eplus.module.infra.api.companypath.CompanyPathApi;
import com.syj.eplus.module.infra.api.companypath.dto.CompanyPathDTO;
import com.syj.eplus.module.infra.api.countryinfo.CountryInfoApi;
import com.syj.eplus.module.infra.api.countryinfo.dto.CountryInfoDTO;
import com.syj.eplus.module.infra.api.formchange.FormChangeApi;
import com.syj.eplus.module.infra.api.formchange.dto.FormChangeDTO;
import com.syj.eplus.module.infra.api.formchange.dto.FormChangeItemDTO;
import com.syj.eplus.module.infra.api.settlement.SettlementApi;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.syj.eplus.framework.common.entity.*;
import com.syj.eplus.framework.common.enums.*;
import com.syj.eplus.module.crm.api.cust.dto.*;
import com.syj.eplus.module.crm.controller.admin.cust.vo.*;
import com.syj.eplus.module.crm.controller.admin.custbankaccount.vo.CustBankaccountRespVO;
import com.syj.eplus.module.crm.controller.admin.custbankaccount.vo.CustBankaccountSaveReqVO;
import com.syj.eplus.module.crm.controller.admin.custpoc.vo.CustPocRespVO;
import com.syj.eplus.module.crm.controller.admin.custpoc.vo.CustPocSaveReqVO;
import com.syj.eplus.module.crm.controller.admin.custsettlement.vo.CustSettlementRespVO;
import com.syj.eplus.module.crm.controller.admin.custsettlement.vo.CustSettlementSaveReqVO;
import com.syj.eplus.module.crm.convert.CustBankAccountConvert;
import com.syj.eplus.module.crm.convert.CustConvert;
import com.syj.eplus.module.crm.dal.dataobject.collectionaccount.CollectionAccountDO;
import com.syj.eplus.module.crm.dal.dataobject.cust.CustDO;
import com.syj.eplus.module.crm.dal.dataobject.custbankaccount.CustBankaccountDO;
import com.syj.eplus.module.crm.dal.dataobject.custcompanypath.CustCompanyPath;
import com.syj.eplus.module.crm.dal.dataobject.custpoc.CustPocDO;
import com.syj.eplus.module.crm.dal.dataobject.custsettlement.CustSettlementDO;
import com.syj.eplus.module.crm.dal.dataobject.mark.MarkDO;
import com.syj.eplus.module.crm.dal.mysql.cust.CustMapper;
import com.syj.eplus.module.crm.dal.mysql.custbankaccount.CustBankaccountMapper;
import com.syj.eplus.module.crm.dal.mysql.custcompanypath.CustCompanyPathMapper;
import com.syj.eplus.module.crm.dal.mysql.custsettlement.CustSettlementMapper;
import com.syj.eplus.module.crm.enums.cust.DeletedEnum;
import com.syj.eplus.module.crm.enums.cust.EffectMainInstanceFlagEnum;
import com.syj.eplus.module.crm.enums.cust.EffectRangeEnum;
import com.syj.eplus.module.crm.service.category.CrmCategoryService;
import com.syj.eplus.module.crm.service.collectionaccount.CollectionAccountService;
import com.syj.eplus.module.crm.service.custSettlement.CustSettlementService;
import com.syj.eplus.module.crm.service.custSettlement.dto.CustSettlementDTO;
import com.syj.eplus.module.crm.service.custbankaccount.CustBankaccountService;
import com.syj.eplus.module.crm.service.custpoc.CustPocService;
import com.syj.eplus.module.crm.service.mark.MarkService;
import com.syj.eplus.module.dms.enums.api.ShipmentApi;
import com.syj.eplus.module.dms.enums.api.dto.ShipmentDTO;
import com.syj.eplus.module.exms.api.exhibition.ExhibitionApi;
import com.syj.eplus.module.exms.api.exhibition.dto.ExhibitionDTO;
import com.syj.eplus.module.infra.api.CodeGeneratorApi;
import com.syj.eplus.module.oa.api.PaymentAppApi;
import com.syj.eplus.module.oa.api.feeshare.FeeShareApi;
import com.syj.eplus.module.sms.api.SaleContractApi;
import com.syj.eplus.module.sms.api.dto.SmsContractAllDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.crm.enums.ErrorCodeConstants.*;


/**
 * 客户资料 Service 实现类
 *
 * @author du
 */
@Service
@Validated
public class CustServiceImpl extends ServiceImpl<CustMapper, CustDO> implements CustService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private CustMapper custMapper;
    @Resource
    private CustBankaccountService custBankaccountService;

    @Resource
    private CustPocService custPocService;

    @Resource
    private MarkService markService;

    @Resource
    private CustSettlementService custSettlementService;

    @Resource
    private CustSettlementMapper custSettlementMapper;




    @Resource
    private AdminUserApi adminUserApi;

    @Resource
    private BpmProcessInstanceApi bpmProcessInstanceApi;

    @Resource
    private CountryInfoApi countryInfoApi;

    @Resource
    private CodeGeneratorApi codeGeneratorApi;
    @Resource
    @Lazy
    private PaymentAppApi paymentAppApi;
    @Resource
    @Lazy
    private FeeShareApi feeShareApi;
    @Resource
    private CustCompanyPathMapper custCompanyPathMapper;
    private static final String CODE_PREFIX = "C";
    public static final String SN_TYPE = "SN_CUST";
    private static final String PROCESS_DEFINITION_KEY = "oa_cust";
    private static final String CHANGE_PROCESS_DEFINITION_KEY = "oa_change_cust";

    @Resource
    private CompanyPathApi companyPathApi;

    @Resource
    private CompanyApi companyApi;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private SettlementApi settlementApi;

    @Resource
    private CollectionAccountService collectionAccountService;

    @Resource
    @Lazy
    private SaleContractApi saleContractApi;

    @Resource
    private ShipmentApi shipmentApi;

    @Resource
    private FormChangeApi formChangeApi;

    @Resource
    private CrmCategoryService categoryService;
    @Autowired
    private CustBankaccountMapper custBankaccountMapper;
    @Resource
    private ExhibitionApi exhibitionApi;

    private Long createCustDetail(CustInfoSaveReqVO createInfoReqVO, boolean isChange) {

        createInfoReqVO.setAuditStatus(BpmProcessInstanceResultEnum.UNSUBMITTED.getResult());

        CustDO cust = BeanUtils.toBean(createInfoReqVO, CustDO.class);
        cust.setEnableFlag(BooleanEnum.YES.getValue());
        cust.setChangeStatus(ChangeStatusEnum.NOT_CHANGED.getStatus());
        if (isChange) {
            cust.setModelKey(CHANGE_PROCESS_DEFINITION_KEY);
        }
        //处理业务员ids，该字段用于权限，前端弃用，后台需要维护
        List<BaseValue> managerList = cust.getManagerList();
        List<Long> ids = new ArrayList<>();
        if (CollUtil.isNotEmpty(managerList)) {
            managerList.forEach(s -> {
                try {
                    ids.add(Long.parseLong(s.getValue()));
                } catch (Exception exception) {
                    throw exception;
                }
            });
        }
        cust.setManagerIds(ids);
        // 插入客户基本信息\
        custMapper.insert(cust);
        //此时id为数据库自增生成id
        Long custId = cust.getId();
        Integer ver = cust.getVer();
        List<CustBankaccountSaveReqVO> bankaccountSaveReqList = createInfoReqVO.getBankaccountList();
        if (CollUtil.isNotEmpty(bankaccountSaveReqList)) {
            List<CustBankaccountDO> custBankaccountDOList = BeanUtils.toBean(bankaccountSaveReqList, CustBankaccountDO.class);
            custBankaccountDOList.stream().forEach(s -> {
                s.setId(null);
                s.setCustId(custId);
                s.setVer(ver);
            });
            custBankaccountService.batchSaveBankAccount(custBankaccountDOList);
        }
        List<CustPocSaveReqVO> custPocSaveReqVOList = createInfoReqVO.getPocList();
        if (CollUtil.isNotEmpty(custPocSaveReqVOList)) {
            custPocSaveReqVOList.forEach(s -> {
                s.setId(null);
                s.setCustId(custId);
                s.setVer(ver);
            });
            custPocService.batchSavePoc(custPocSaveReqVOList);
        }
//        List<MarkSaveReqVO> markSaveReqVOList = createInfoReqVO.getMarkVOList();
//        if (CollUtil.isNotEmpty(markSaveReqVOList)) {
//            List<MarkDO> markDOList = BeanUtils.toBean(markSaveReqVOList, MarkDO.class);
//            markDOList.forEach(s -> {
//                s.setId(null);
//                s.setCustId(custId);
//                s.setVer(ver);
//            });
//            markService.batchSaveMark(markDOList);
//        }
        List<CustSettlementSaveReqVO> settlementList = createInfoReqVO.getSettlementList();
        if (CollUtil.isNotEmpty(settlementList)) {
            List<CustSettlementDO> custSettlementDOList = BeanUtils.toBean(settlementList, CustSettlementDO.class);
            custSettlementDOList.forEach(s -> {
                s.setId(null);
                s.setCustId(custId);
            });
            custSettlementService.batchSaveCustSettlement(custSettlementDOList);
        }
        List<CustCompanyPath> custCompanyPathList = createInfoReqVO.getCustCompanyPathList();
        if (CollUtil.isNotEmpty(custCompanyPathList)) {
            custCompanyPathList.forEach(s -> {
                s.setId(null);
                s.setCustId(cust.getId());
                s.setCustCode(cust.getCode());
            });
            custCompanyPathMapper.insertBatch(custCompanyPathList);
        }
        //收款方式
        List<CollectionAccountDO> collectionAccountList = createInfoReqVO.getCollectionAccountList();
        if(CollUtil.isNotEmpty(collectionAccountList)){
            collectionAccountList.forEach(s->{
                s.setCustId(cust.getId());
                s.setCustCode(cust.getCode());
            });
            collectionAccountService.batchInsert(collectionAccountList);
        }


        if (SubmitFlagEnum.SUBMIT.getStatus().equals(createInfoReqVO.getSubmitFlag())) {
            if (isChange) {
                submitChangeTask(custId, WebFrameworkUtils.getLoginUserId());
            } else {
                submitTask(custId, WebFrameworkUtils.getLoginUserId());
            }
        }
        return custId;
    }

    /**
     * 创建客户资料
     *
     * @param createInfoReqVO 创建信息
     * @return 编号
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createCust(CustInfoSaveReqVO createInfoReqVO) {
        // 校验名称是否存在
        validateCustNameExists(createInfoReqVO.getName());
        //仅提交检验币种
        if (SubmitFlagEnum.SUBMIT.getStatus().equals(createInfoReqVO.getSubmitFlag())) {
            validateCurrencyList(createInfoReqVO.getCurrencyList());
        }
        CountryInfoDTO countryInfoDTO = countryInfoApi.getCountryInfoById(createInfoReqVO.getCountryId());
        String code = codeGeneratorApi.getCodeGenerator(SN_TYPE, countryInfoDTO.getAreaCode(),false,4);
        createInfoReqVO.setCode(code);
        //新增版本默认为1
        createInfoReqVO.setVer(1);
        return createCustDetail(createInfoReqVO, false);
    }

    //校验币种有且仅有一个默认
    private void validateCurrencyList(List<BaseValue> currencyList) {
        long count = currencyList.stream().filter(s -> Objects.equals(s.getDefaultFlag(), BooleanEnum.YES.getValue())).count();
        if (!Objects.equals(count, BigDecimal.ONE.longValue())) {
            throw exception(CURRENCY_WRONG);
        }
    }

    private void validateCustNameExists(String name) {
        boolean exists = custMapper.exists(new LambdaQueryWrapperX<CustDO>().eq(CustDO::getName, name));
        if (exists) {
            throw exception(CUST_NAME_EXISTS);
        }
    }
    /**
     * 更新客户资料
     *
     * @param updateReqVO 更新信息
     */
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public Long updateCust(CustInfoSaveReqVO updateReqVO) {
//        Long custId = updateReqVO.getId();
//        //删除方法中已经校验审核状态 此处直接调用
//        deleteCust(custId, false);
//        //数据库自增 将id置空
//        updateReqVO.setId(null);
//        //版本加1
//        updateReqVO.setVer(updateReqVO.getVer() + 1);
//        return createCust(updateReqVO, true);
//    }

    /**
     * 更新客户资料
     *
     * @param updateReqVO 更新信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCust(CustInfoSaveReqVO updateReqVO) {
        //仅提交检验币种
        if (SubmitFlagEnum.SUBMIT.getStatus().equals(updateReqVO.getSubmitFlag())) {
            validateCurrencyList(updateReqVO.getCurrencyList());
        }
        // 已在审核中的数据不允许修改
        if (BpmProcessInstanceResultEnum.PROCESS.getResult().equals(updateReqVO.getAuditStatus())) {
            throw exception(NOT_UPDATE_PROCESS);
        }
        // 校验存在
        validateCustExists(updateReqVO.getId());

        CustDO cust = BeanUtils.toBean(updateReqVO, CustDO.class);
        //处理业务员ids，该字段用于权限，前端弃用，后台需要维护
        List<BaseValue> managerList = cust.getManagerList();
        List<Long> ids = new ArrayList<>();
        if (CollUtil.isNotEmpty(managerList)) {
            managerList.forEach(s -> {
                try {
                    ids.add(Long.parseLong(s.getValue()));
                } catch (Exception exception) {
                    throw exception;
                }
            });
        }
        cust.setManagerIds(ids);
        custMapper.updateById(cust);

        //此时id为数据库自增生成id
        Long custId = cust.getId();
        Integer ver = cust.getVer();
        List<CustBankaccountSaveReqVO> bankaccountSaveReqList = updateReqVO.getBankaccountList();
        if (CollUtil.isNotEmpty(bankaccountSaveReqList)) {
            List<CustBankaccountDO> custBankaccountDOList = BeanUtils.toBean(bankaccountSaveReqList, CustBankaccountDO.class);
            custBankaccountDOList.stream().forEach(s -> {
                s.setId(null);
                s.setCustId(custId);
                s.setVer(ver);
            });
            custBankaccountService.deleteCustBankaccountByCustId(custId);
            custBankaccountService.batchSaveBankAccount(custBankaccountDOList);
        }
        List<CustPocSaveReqVO> custPocSaveReqVOList = updateReqVO.getPocList();
        if (CollUtil.isNotEmpty(custPocSaveReqVOList)) {
            custPocSaveReqVOList.forEach(s -> {
                s.setId(null);
                s.setCustId(custId);
                s.setVer(ver);
            });
            custPocService.deleteCustPocByCustId(custId);
            custPocService.batchSavePoc(custPocSaveReqVOList);
        }
//        List<MarkSaveReqVO> markSaveReqVOList = updateReqVO.getMarkVOList();
//        if (CollUtil.isNotEmpty(markSaveReqVOList)) {
//            List<MarkDO> markDOList = BeanUtils.toBean(markSaveReqVOList, MarkDO.class);
//            markDOList.forEach(s -> {
//                s.setId(null);
//                s.setCustId(custId);
//                s.setVer(ver);
//            });
//            markService.deleteMarkByCustId(custId);
//            markService.batchSaveMark(markDOList);
//        }
        List<CustSettlementSaveReqVO> settlementList = updateReqVO.getSettlementList();
        if (CollUtil.isNotEmpty(settlementList)) {
            List<CustSettlementDO> custSettlementDOList = BeanUtils.toBean(settlementList, CustSettlementDO.class);
            custSettlementDOList.forEach(s -> {
                s.setId(null);
                s.setCustId(custId);
            });
            custSettlementService.deleteCustSettlementByCustId(custId);
            custSettlementService.batchSaveCustSettlement(custSettlementDOList);
        }
        List<CustCompanyPath> custCompanyPathList = updateReqVO.getCustCompanyPathList();
        if (CollUtil.isNotEmpty(custCompanyPathList)) {
            custCompanyPathList.forEach(s -> {
                s.setId(null);
                s.setCustId(cust.getId());
                s.setCustCode(cust.getCode());
            });
            custCompanyPathMapper.delete(CustCompanyPath::getCustId, cust.getId());
            custCompanyPathMapper.insertBatch(custCompanyPathList);
        }

        //收款方式
        List<CollectionAccountDO> collectionAccountList = updateReqVO.getCollectionAccountList();
        collectionAccountService.deleteCollectionAccountByCustCode(updateReqVO.getId());
        if(CollUtil.isNotEmpty(collectionAccountList)){
            collectionAccountList.forEach(s->{
                s.setCustId(cust.getId());
                s.setCustCode(cust.getCode());
            });
            collectionAccountService.batchInsert(collectionAccountList);
        }

        if (SubmitFlagEnum.SUBMIT.getStatus().equals(updateReqVO.getSubmitFlag())) {
            submitTask(custId, WebFrameworkUtils.getLoginUserId());
        }
    }

    /**
     * 变更客户资料
     *
     * @param changeReqVO 更新信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long changeCust(CustInfoSaveReqVO changeReqVO) {
        //已经有高版本客户信息，无法变更
        List<CustDO> custDOs = custMapper.selectList(new LambdaQueryWrapperX<CustDO>()
                .eqIfPresent(CustDO::getCode, changeReqVO.getCode())
                .eqIfPresent(CustDO::getChangeFlag, ChangeFlagEnum.NO.getValue())
                .eqIfPresent(CustDO::getVer, changeReqVO.getVer() + 1)
        );
        if (CollUtil.isNotEmpty(custDOs)) {
            throw exception(CHANGE_NOT_ALLOWED);
        }
        CustDO custDO = validateCustExists(changeReqVO.getId());
        if (custDO.getChangeStatus() == ChangeStatusEnum.IN_CHANGE.getStatus()) {
            throw exception(IN_CHANGE_NOT_ALLOWED);
        }
        // 已存在不同编号客户名称无法变更
        boolean exists = custMapper.exists(new LambdaQueryWrapperX<CustDO>().eq(CustDO::getName, changeReqVO.getName()).ne(CustDO::getCode, changeReqVO.getCode()));
        if (exists){
            throw exception(CUST_NAME_EXISTS);
        }
        //查询新客户与原客户
        CustInfoRespVo cust = BeanUtils.toBean(changeReqVO, CustInfoRespVo.class);
        cust.setVer(cust.getVer() + 1);
        ChangeEffectRespVO effectRange = getChangeEffect(cust);
        if (Objects.nonNull(effectRange)){
            changeReqVO.setEffectRangeList(effectRange.getEffectRangeList());
        }
        custDO.setChangeStatus(ChangeStatusEnum.IN_CHANGE.getStatus());
        //处理业务员ids，该字段用于权限，前端弃用，后台需要维护
        List<BaseValue> managerList = cust.getManagerList();
        List<Long> ids = new ArrayList<>();
        if (CollUtil.isNotEmpty(managerList)) {
            managerList.forEach(s -> {
                try {
                    ids.add(Long.parseLong(s.getValue()));
                } catch (Exception exception) {
                    throw exception;
                }
            });
        }
        custDO.setManagerIds(ids);
        custMapper.updateById(custDO);

        //数据库自增 将id置空
        changeReqVO.setId(null);
        //版本加1
        changeReqVO.setVer(changeReqVO.getVer() + 1);
        changeReqVO.setChangeFlag(ChangeFlagEnum.YES.getValue());

        // 如果都是普通级直接变更
        if (!getChangeLevelFlag(cust)) {
            changeReqVO.setSubmitFlag(BooleanEnum.NO.getValue());
            Long custId = createCustDetail(changeReqVO, true);
            CustInfoRespVo custInfoRespVo = getCustDetail(new CrmCustDetailReq().setCustId(custId), true);
            //自动审核通过
            updateChangeAuditStatus(custId, BpmProcessInstanceResultEnum.APPROVE.getResult());
            changeSuccess(custInfoRespVo);
            return custId;
        } else {
            changeReqVO.setSubmitFlag(BooleanEnum.YES.getValue());
            Long custId = createCustDetail(changeReqVO, true);
            return custId;
        }
    }

    @Override
    public void changeSuccess(CustInfoRespVo cust) {
        //删除原客户
        deleteOldCust(cust.getId());
        if (CollUtil.isNotEmpty(cust.getEffectRangeList())) {
            cust.getEffectRangeList().forEach(s -> {
                if (EffectRangeEnum.SMS.getValue().equals(s.getEffectRangeType())) {
                    saleContractApi.updateConfirmFlag(ConfirmFlagEnum.NO.getValue(), s.getEffectRangeCode());
                }
                if (EffectRangeEnum.DMS.getValue().equals(s.getEffectRangeType())) {
                    shipmentApi.updateConfirmFlag(ConfirmFlagEnum.NO.getValue(), s.getEffectRangeCode());
                }
            });
        }
        saleContractApi.updateCust(cust.getId(), cust.getName(), cust.getCode());
        shipmentApi.updateCust(cust.getId(), cust.getName(), cust.getCode());
//
//        CustChangeEffectRespVO custChangeEffectRespVO = getChangeEffect(cust);
//        //更新该客户未完成的所有销售合同为未确认
//        if (CollUtil.isNotEmpty(custChangeEffectRespVO.getSmsSaleContractList())) {
//            custChangeEffectRespVO.getSmsSaleContractList().forEach(s -> {
//                saleContractApi.updateConfirmFlag(ConfirmFlagEnum.NO.getValue(), s.getId());
//            });
//        }
//        //更新该客户未完成的所有采购合同为未确认
//        if (CollUtil.isNotEmpty(custChangeEffectRespVO.getScmPurchaseContractList())) {
//            logger.warn("客户变更不影响采购合同，但有的字段选择的影响范围中包含采购！此处跳过！");
//        }
//        //更新该客户未完成的所有出运明细为未确认
//        if (CollUtil.isNotEmpty(custChangeEffectRespVO.getDmsShipmentList())) {
//            custChangeEffectRespVO.getDmsShipmentList().forEach(s -> {
//                shipmentApi.updateConfirmFlag(ConfirmFlagEnum.NO.getValue(), s.getId());
//            });
//        }
    }

    /**
     * 删除客户资料
     * * @param id 编号
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCust(Long id, Boolean forceDeleted) {
        // 校验存在
        CustDO custDO = validateCustExists(id);
        //审核中的数据不允许修改,删除
        if (!forceDeleted && !BpmProcessInstanceResultEnum.UNSUBMITTED.getResult().equals(custDO.getAuditStatus())&&!BpmProcessInstanceResultEnum.CANCEL.getResult().equals(custDO.getAuditStatus())) {
            throw exception(UNSUBMIT_EDIT_DEL_NOT_ALLOWED);
        }
        // 删除
        custMapper.deleteById(id);
        //获取修改前的客户银行信息列表
//        List<CustBankaccountDO> bankAccountList = custBankaccountService.getBankAccountListByCustId(id);
//        //不为空进行删除 为空不做操作减少数据库访问次数
//        if (CollUtil.isNotEmpty(bankAccountList)) {
//            List<Long> idList = bankAccountList.stream().map(CustBankaccountDO::getId).toList();
//            custBankaccountService.batchDeleteBankAccountList(idList);
//        }
//        //获取修改前的客户联系人列表
//        List<CustPocRespVO> custPocRespVOList = custPocService.getPocListByCustId(id);
//        //不为空进行删除 为空不做操作减少数据库访问次数
//        if (CollUtil.isNotEmpty(custPocRespVOList)) {
//            List<Long> idList = custPocRespVOList.stream().map(CustPocRespVO::getId).toList();
//            custPocService.batchDeletePocList(idList);
//        }
//        //获取修改前的客户唛头列表
//        List<MarkDO> markList = markService.getMarkListByCustId(id);
//        //不为空进行删除 为空不做操作减少数据库访问次数
//        if (CollUtil.isNotEmpty(markList)) {
//            List<Long> idList = markList.stream().map(MarkDO::getId).toList();
//            markService.batchDeleteMarkList(idList);
//        }
//        //获取修改前的客户结汇方式列表
//        List<CustSettlementDTO> settlementList = custSettlementService.getSettlementByCustId(id);
//        //不为空进行删除 为空不做操作减少数据库访问次数
//        if (CollUtil.isNotEmpty(settlementList)) {
//            custSettlementService.deleteCustSettlementByCustId(id);
//        }
//        //删除客户公司路径
//        custCompanyPathMapper.delete(CustCompanyPath::getCustId, custDO.getId());
    }

    /**
     * 删除变更前的客户资料
     * * @param id 编号
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteOldCust(Long id) {
        // 校验存在
        CustDO custDO = validateCustExists(id);
        // 查询该id对应编号所有的数据
        List<CustDO> custDOs = custMapper.selectList(new LambdaQueryWrapperX<CustDO>()
                .eqIfPresent(CustDO::getCode, custDO.getCode())
                .orderByDesc(CustDO::getId)
        );

//        //审核中的数据不允许修改,删除
//        if (!BpmProcessInstanceResultEnum.APPROVE.getResult().equals(custDO.getAuditStatus())) {
//            throw exception(UNAPPROVE_EDIT_DEL_NOT_ALLOWED);
//        }

        custDOs.forEach(s -> {
            if (!s.getId().equals(id)) {
                deleteCust(s.getId(), true);
            }
        });
    }

    private CustDO validateCustExists(Long id) {
        CustDO custDO = custMapper.selectById(id);
        //未查到客户基本信息，抛出异常
        if (Objects.isNull(custDO)) {
            throw exception(CUST_NOT_EXISTS);
        }
        return custDO;
    }

    public CustInfoRespVo getCustDetail(CrmCustDetailReq req, Boolean isChange) {
        //将不同入口的参数转换为custId
        Long custId = Objects.nonNull(req.getProcessInstanceId()) ? bpmProcessInstanceApi.selectAuditAbleIdByProcessInstanceId(req.getProcessInstanceId()) : req.getCustId();
        custId =  Objects.nonNull(req.getCustCode()) ? getLastedIdByCode(req.getCustCode()) : custId;
        if (Objects.isNull(custId)) {
            logger.error("[客户详情]未获取到客户id");
            return null;
        }
        LambdaQueryWrapperX<CustDO> custDOLambdaQueryWrapperX = new LambdaQueryWrapperX<CustDO>();
        custDOLambdaQueryWrapperX.eqIfPresent(CustDO::getId, custId);
        custDOLambdaQueryWrapperX.inIfPresent(CustDO::getDeleted, new ArrayList<>(Arrays.asList(0, 1)));
        CustDO custDO = custMapper.selectOne(custDOLambdaQueryWrapperX);
        if (Objects.isNull(custDO)) {
            return null;
        }
        Map<Long, CountryInfoDTO> countryInfoMap = countryInfoApi.getCountryInfoMap();
        CustInfoRespVo custInfoRespVo = CustConvert.INSTANCE.convert(custDO, null, countryInfoMap);
        ExhibitionDTO exhibition = exhibitionApi.getExhibitionById(custDO.getExmsExhibitionId(), custDO.getExmsEventCategoryId());
        if (Objects.nonNull(exhibition)){
            custInfoRespVo.setExmsExhibitionName(exhibition.getExhibitionName());
            custInfoRespVo.setExmsEventCategoryName(exhibition.getExmsEventCategoryName());
        }
        //获取银行信息
        List<CustBankaccountDO> bankAccountListByCustId = custBankaccountService.getBankAccountListByCustId(custId);
        if (CollUtil.isNotEmpty(bankAccountListByCustId)) {
            custInfoRespVo.setBankaccountList(BeanUtils.toBean(bankAccountListByCustId, CustBankaccountRespVO.class));
        }
        //获取联系人列表
        List<CustPocRespVO> custPocRespVOList = custPocService.getPocListByCustId(custId);
        if (CollUtil.isNotEmpty(custPocRespVOList)) {
            custInfoRespVo.setPocList(custPocRespVOList);
        }
        //获取唛头列表
//        List<MarkDO> markListByCustId = markService.getMarkListByCustId(custId);
//        if (CollUtil.isNotEmpty(markListByCustId)) {
//            custInfoRespVo.setMarkVOList(BeanUtils.toBean(markListByCustId, MarkRespVO.class));
//        }
        //获取结汇方式列表
        List<CustSettlementDTO> settlementDTOList = custSettlementService.getSettlementByCustId(custId);
        if (CollUtil.isNotEmpty(settlementDTOList)) {
            custInfoRespVo.setSettlementList(BeanUtils.toBean(settlementDTOList, CustSettlementRespVO.class));
        }
        String bpmProcessInstanceId = bpmProcessInstanceApi.getBpmProcessInstanceId(custId, custDO.getVer() > 1 ? CHANGE_PROCESS_DEFINITION_KEY : PROCESS_DEFINITION_KEY);
        if (StrUtil.isNotEmpty(bpmProcessInstanceId)) {
            custInfoRespVo.setProcessInstanceId(bpmProcessInstanceId);
        }
        List<String> custLinkCode = custDO.getCustLinkCode();
        Map<String, SimpleData> stringSimpleDataMap = transformCustLink(custLinkCode);
        if (CollUtil.isNotEmpty(stringSimpleDataMap) && CollUtil.isNotEmpty(custLinkCode)) {
            List<SimpleData> simpleDataList = new ArrayList<>();
            custLinkCode.forEach(s -> {
                simpleDataList.add(stringSimpleDataMap.get(s));
            });
            custInfoRespVo.setCustLink(simpleDataList);
        }
        List<CustCompanyPath> custCompanyPathList = custCompanyPathMapper.selectList(CustCompanyPath::getCustId, custDO.getId());
        if (CollUtil.isNotEmpty(custCompanyPathList)) {
            custInfoRespVo.setCustCompanyPathList(custCompanyPathList);
            List<Long> companyPathIdList = custCompanyPathList.stream().map(CustCompanyPath::getCompanyPathId).distinct().toList();
            Map<Long, CompanyPathDTO> companyPathDTOMap = companyPathApi.getCompanyPathMap(companyPathIdList);
            if (CollUtil.isNotEmpty(companyPathDTOMap)) {
                custInfoRespVo.setCompanyPath(custCompanyPathList.stream().map(s -> {
                    CompanyPathDTO dto = companyPathDTOMap.get(s.getCompanyPathId());
                    JsonCompanyPath path = convertToJsonCompanyPath(dto);
                    return new CompanyPath().setId(s.getCompanyPathId()).setPath(path).setDefaultFlag(s.getDefaultFlag());
                }).toList());
            }
        }
        //获取内部企业名称
        if (Objects.nonNull(custInfoRespVo.getInternalCompanyId())&&Objects.nonNull(custInfoRespVo.getInternalFlag())&&BooleanEnum.YES.getValue().equals(custInfoRespVo.getInternalFlag())){
            SimpleCompanyDTO companyDTO = companyApi.getCompanyDTO(custInfoRespVo.getInternalCompanyId());
            if(Objects.nonNull(companyDTO)){
                custInfoRespVo.setInternalCompanyName(companyDTO.getName());
            }
        }


        //查询收款方式
        custInfoRespVo.setCollectionAccountList(collectionAccountService.getCollectionAccountListByCustCode(custInfoRespVo.getId()));
        String custTypesName = categoryService.getNameByIdList(custDO.getCustomerTypes());
        custInfoRespVo.setCustomerTypesName(custTypesName);
        return custInfoRespVo;
    }

    private Long getLastedIdByCode(String custCode) {
        LambdaQueryWrapperX<CustDO> custDOLambdaQueryWrapperX = new LambdaQueryWrapperX<CustDO>();
        custDOLambdaQueryWrapperX.eq(CustDO::getCode, custCode);
        custDOLambdaQueryWrapperX.eq(CustDO::getAuditStatus, BpmProcessInstanceResultEnum.APPROVE.getResult());
        custDOLambdaQueryWrapperX.eq(CustDO::getChangeFlag, ChangeFlagEnum.NO.getValue());
        CustDO custDO = custMapper.selectOne(custDOLambdaQueryWrapperX);
        if(Objects.isNull(custDO)){
            return null;
        }
        return custDO.getId();
    }

    /**
     * 获得客户资料
     *
     * @param req 请求实体
     * @return 客户资料
     */
    @Override
    public CustInfoRespVo getCust(CrmCustDetailReq req) {
        return getCustDetail(req, false);
    }

    /**
     * 获得客户资料分页
     *
     * @param pageReqVO 分页查询
     * @return 客户资料分页
     */
    @Override
    public PageResult<CustRespVO> getCustPage(CustPageReqVO pageReqVO) {
        List<Long> managerIds = pageReqVO.getManagerIds();
        List<String> managerIds_str = new ArrayList<>();
        if (CollUtil.isNotEmpty(managerIds)) {
            managerIds_str = managerIds.stream().map(String::valueOf).collect(Collectors.toList());
        }
        pageReqVO.setChangeFlag(ChangeFlagEnum.NO.getValue());
        PageResult<CustDO> custDOPageResult = custMapper.selectPage(pageReqVO, managerIds_str);
        Map<Long, CountryInfoDTO> countryInfoMap = countryInfoApi.getCountryInfoMap();
        // 客户分类名称缓存
        Map<Long, String> categroyNameMap = categoryService.getCategroyNameMap(null);
        return CustConvert.INSTANCE.convertToCustRespPageResult(custDOPageResult, categroyNameMap, countryInfoMap);
    }

    /**
     * 获得客户变更资料分页
     *
     * @param pageReqVO 分页查询
     * @return 客户资料分页
     */
    @Override
    public PageResult<CustRespVO> getCustChangePage(CustPageReqVO pageReqVO) {
        List<Long> managerIds = pageReqVO.getManagerIds();
        List<String> managerIds_str = new ArrayList<>();
        if (CollUtil.isNotEmpty(managerIds)) {
            managerIds_str = managerIds.stream().map(String::valueOf).collect(Collectors.toList());
        }
        PageResult<CustDO> custRespDOPageResult = custMapper.selectChangePage(pageReqVO, managerIds_str);
        Map<Long, CountryInfoDTO> countryInfoMap = countryInfoApi.getCountryInfoMap();
        Map<Long, String> categroyNameMap = categoryService.getCategroyNameMap(null);
        PageResult<CustRespVO> custRespVOPageResult = CustConvert.INSTANCE.convertToCustRespPageResult(custRespDOPageResult, categroyNameMap, countryInfoMap);
        List<CustRespVO> custRespVOList = custRespVOPageResult.getList();
        if (CollUtil.isEmpty(custRespVOList)) {
            return custRespVOPageResult;
        }
        List<String> custCodeList = custRespVOList.stream().map(CustRespVO::getCode).distinct().toList();
        Map<String, Map<Integer, CustInfoRespVo>> simpleOldCustMap = getSimpleOldCustMap(custCodeList);
        custRespVOPageResult.getList().forEach(s -> {
            Map<Integer, CustInfoRespVo> custInfoRespVoMap = simpleOldCustMap.get(s.getCode());
            if (CollUtil.isEmpty(custInfoRespVoMap)){
                return;
            }
            s.setOldCust(custInfoRespVoMap.get(s.getVer() - 1));
        });
        return custRespVOPageResult;
    }

    @Override
    public ChangeEffectRespVO getChangeEffect(CustInfoRespVo cust) {
        //查询新客户与原客户
        CustInfoRespVo oldCust = getOldCust(cust);

        //初始化变更标记
        final boolean[] changeFlag = {false, false, false};
        ChangeEffectRespVO changeEffectRespVO = new ChangeEffectRespVO();
        //查询所有表的配置
        Map<String, FormChangeDTO> formChangeDTOList = formChangeApi.getFormChangeDTOList(List.of("crm_cust", "crm_cust_bankaccount", "crm_cust_poc", "crm_cust_settlement", "crm_mark", "crm_company_path"));
        if (CollUtil.isEmpty(formChangeDTOList)) {
            throw exception(FIELD_CHANGE_CONFIG_NOT_EXISTS);
        }
        Integer submitFlag = BooleanEnum.NO.getValue();
        //客户主表
        Set<String> changeFields = new ChangeCompareUtil<CustInfoRespVo>().transformObject(oldCust, cust);
        compareTableField(changeFields, formChangeDTOList.get("crm_cust"), changeFlag,submitFlag);

        //客户银行信息
        List<CustBankaccountRespVO> oldCustBankaccountList = oldCust.getBankaccountList();
        List<CustBankaccountRespVO> custBankaccountList = cust.getBankaccountList();
        List<DiffRecord<CustBankaccountRespVO>> custBankaccountDiffRecords = DiffUtil.compareLists(oldCustBankaccountList, custBankaccountList);
        Tuple custBankAccountTuple = new ChangeCompareUtil<CustBankaccountRespVO>().transformChildList(custBankaccountDiffRecords, CustBankaccountRespVO.class);
        compareTableField(custBankAccountTuple.get(1), formChangeDTOList.get("crm_cust_bankaccount"), changeFlag,submitFlag);

        //客户联系人
        List<CustPocRespVO> oldCustPocList = oldCust.getPocList();
        List<CustPocRespVO> custPocList = cust.getPocList();
        List<DiffRecord<CustPocRespVO>> custPocDiffRecords = DiffUtil.compareLists(oldCustPocList, custPocList);
        Tuple custPocTuple = new ChangeCompareUtil<CustPocRespVO>().transformChildList(custPocDiffRecords, CustPocRespVO.class);
        compareTableField(custPocTuple.get(1), formChangeDTOList.get("crm_cust_poc"), changeFlag,submitFlag);

        //客户结汇方式
        List<CustSettlementRespVO> oldCustSettlementList = oldCust.getSettlementList();
        List<CustSettlementRespVO> custSettlementList = cust.getSettlementList();
        List<DiffRecord<CustSettlementRespVO>> custSettlementDiffRecords = DiffUtil.compareLists(oldCustSettlementList, custSettlementList);
        Tuple custSettlementTuple = new ChangeCompareUtil<CustSettlementRespVO>().transformChildList(custSettlementDiffRecords, CustSettlementRespVO.class);
        compareTableField(custSettlementTuple.get(1), formChangeDTOList.get("crm_cust_settlement"), changeFlag,submitFlag);

        //客户唛头
//        List<MarkRespVO> oldMarkList = oldCust.getMarkVOList();
//        List<MarkRespVO> markList = cust.getMarkVOList();
//        List<DiffRecord<MarkRespVO>> markDiffRecords = DiffUtil.compareLists(oldMarkList, markList);
//        Tuple markTuple = new ChangeCompareUtil<MarkRespVO>().transformChildList(markDiffRecords, MarkRespVO.class);
//        compareTableField(markTuple.get(1), formChangeDTOList.get("crm_mark"), changeFlag);

        //客户公司路径
//            List<MarkRespVO> oldMarkList = oldCust.getMarkVOList();
//            List<MarkRespVO> markList = cust.getMarkVOList();
//            List<DiffRecord<MarkRespVO>> markDiffRecords = DiffUtil.compareLists(oldMarkList, markList);
//            Tuple markTuple = new ChangeCompareUtil<MarkRespVO>().transformChildList(markDiffRecords, MarkRespVO.class);
//            compareTableField(markTuple.get(1), formChangeDTOList.get("crm_mark"), changeFlag);

        // 处理影响范围
        List<JsonEffectRange> effectRangeList = new ArrayList<>();
        //更新该客户未完成的所有销售合同为未确认
        List<SmsContractAllDTO> smsContracts = new ArrayList<>();
        if (changeFlag[0]) {
            smsContracts = saleContractApi.getUnCompletedSaleContractByCustCode(cust.getCode());
            if (CollUtil.isNotEmpty(smsContracts)) {
                smsContracts.forEach(s -> {
                    effectRangeList.add(new JsonEffectRange().setEffectRangeCode(s.getCode()).setEffectRangeType(EffectRangeEnum.SMS.getValue()).setConfirmFlag(ConfirmFlagEnum.NO.getValue()));
                });
            }
        }
        //更新该客户未完成的所有出运明细为未确认
        List<ShipmentDTO> shipments = new ArrayList<>();
        if (changeFlag[2]) {
            shipments = shipmentApi.getUnShippedDTOByCustCode(cust.getCode());
            if (CollUtil.isNotEmpty(shipments)) {
                shipments.forEach(s -> {
                    effectRangeList.add(new JsonEffectRange().setEffectRangeCode(s.getCode()).setEffectRangeType(EffectRangeEnum.DMS.getValue()).setConfirmFlag(ConfirmFlagEnum.NO.getValue()));
                });
            }
        }
        changeEffectRespVO.setSubmitFlag(submitFlag);
        changeEffectRespVO.setEffectRangeList(effectRangeList);
        return changeEffectRespVO;
    }

    public Boolean getChangeLevelFlag(CustInfoRespVo cust) {
        //查询新客户与原客户
        CustInfoRespVo oldCust = getOldCust(cust);

        //初始化变更标记
        final boolean[] changeFlag = {false};

        //查询所有表的配置
        Map<String, FormChangeDTO> formChangeDTOList = formChangeApi.getFormChangeDTOList(List.of("crm_cust", "crm_cust_bankaccount", "crm_cust_poc", "crm_cust_settlement", "crm_mark", "crm_company_path"));
        if (CollUtil.isEmpty(formChangeDTOList)) {
            throw exception(FIELD_CHANGE_CONFIG_NOT_EXISTS);
        }

        //客户主表
        Set<String> changeFields = new ChangeCompareUtil<CustInfoRespVo>().transformObject(oldCust, cust);
        compareChangeLevelTableField(changeFields, formChangeDTOList.get("crm_cust"), changeFlag);

        //客户银行信息
        List<CustBankaccountRespVO> oldCustBankaccountList = oldCust.getBankaccountList();
        List<CustBankaccountRespVO> custBankaccountList = cust.getBankaccountList();
        List<DiffRecord<CustBankaccountRespVO>> custBankaccountDiffRecords = DiffUtil.compareLists(oldCustBankaccountList, custBankaccountList);
        Tuple custBankAccountTuple = new ChangeCompareUtil<CustBankaccountRespVO>().transformChildList(custBankaccountDiffRecords, CustBankaccountRespVO.class);
        compareChangeLevelTableField(custBankAccountTuple.get(1), formChangeDTOList.get("crm_cust_bankaccount"), changeFlag);

        //客户联系人
        List<CustPocRespVO> oldCustPocList = oldCust.getPocList();
        List<CustPocRespVO> custPocList = cust.getPocList();
        List<DiffRecord<CustPocRespVO>> custPocDiffRecords = DiffUtil.compareLists(oldCustPocList, custPocList);
        Tuple custPocTuple = new ChangeCompareUtil<CustPocRespVO>().transformChildList(custPocDiffRecords, CustPocRespVO.class);
        compareChangeLevelTableField(custPocTuple.get(1), formChangeDTOList.get("crm_cust_poc"), changeFlag);

        //客户结汇方式
        List<CustSettlementRespVO> oldCustSettlementList = oldCust.getSettlementList();
        List<CustSettlementRespVO> custSettlementList = cust.getSettlementList();
        List<DiffRecord<CustSettlementRespVO>> custSettlementDiffRecords = DiffUtil.compareLists(oldCustSettlementList, custSettlementList);
        Tuple custSettlementTuple = new ChangeCompareUtil<CustSettlementRespVO>().transformChildList(custSettlementDiffRecords, CustSettlementRespVO.class);
        compareChangeLevelTableField(custSettlementTuple.get(1), formChangeDTOList.get("crm_cust_settlement"), changeFlag);

        // 订单路径
        List<CustCompanyPath> oldCustCompanyPathList = oldCust.getCustCompanyPathList();
        List<CustCompanyPath> custCompanyPathList = cust.getCustCompanyPathList();
        List<DiffRecord<CustCompanyPath>> custCompanyPathDiffRecords = DiffUtil.compareLists(oldCustCompanyPathList, custCompanyPathList);
        Tuple custCompanyPathTuple = new ChangeCompareUtil<CustCompanyPath>().transformChildList(custCompanyPathDiffRecords, CustCompanyPath.class);
        compareChangeLevelTableField(custCompanyPathTuple.get(1), formChangeDTOList.get("crm_company_path"), changeFlag);
        //客户唛头
//        List<MarkRespVO> oldMarkList = oldCust.getMarkVOList();
//        List<MarkRespVO> markList = cust.getMarkVOList();
//        List<DiffRecord<MarkRespVO>> markDiffRecords = DiffUtil.compareLists(oldMarkList, markList);
//        Tuple markTuple = new ChangeCompareUtil<MarkRespVO>().transformChildList(markDiffRecords, MarkRespVO.class);
//        compareChangeLevelTableField(markTuple.get(1), formChangeDTOList.get("crm_mark"), changeFlag);

        return changeFlag[0];
    }

    private void compareTableField(Set<String> changeFieldNames, FormChangeDTO formChange, boolean[] changeFlag,Integer submitFlag) {
        if (formChange != null) {
            //影响销售的字段
            List<FormChangeItemDTO> smsItems = formChange.getChildren().stream().filter(s -> Objects.equals(s.getEffectMainInstanceFlag(), EffectMainInstanceFlagEnum.YES.getValue()) && s.getEffectRange().contains(EffectRangeEnum.SMS.getValue())).toList();
            //影响采购的字段
            List<FormChangeItemDTO> scmItems = formChange.getChildren().stream().filter(s -> Objects.equals(s.getEffectMainInstanceFlag(), EffectMainInstanceFlagEnum.YES.getValue()) && s.getEffectRange().contains(EffectRangeEnum.SCM.getValue())).toList();
            //影响出运的字段
            List<FormChangeItemDTO> dmsItems = formChange.getChildren().stream().filter(s -> Objects.equals(s.getEffectMainInstanceFlag(), EffectMainInstanceFlagEnum.YES.getValue()) && s.getEffectRange().contains(EffectRangeEnum.DMS.getValue())).toList();
            smsItems.forEach(s -> {
                if (changeFieldNames.contains(s.getNameEng())) {
                    changeFlag[0] = true;
                }
            });
            scmItems.forEach(s -> {
                if (changeFieldNames.contains(s.getNameEng())) {
                    changeFlag[1] = true;
                }
            });
            dmsItems.forEach(s -> {
                if (changeFieldNames.contains(s.getNameEng())) {
                    changeFlag[2] = true;
                }
            });
            boolean isSubmitFlag = formChange.getChildren().stream().anyMatch(s -> ChangeLevelEnum.FORM.getValue().equals(s.getChangeLevel()));
            if (isSubmitFlag){
                submitFlag = BooleanEnum.YES.getValue();
            }
        }
    }

    private void compareChangeLevelTableField(Set<String> changeFieldNames, FormChangeDTO formChange, boolean[] changeFlag) {
        if (formChange != null) {
            List<FormChangeItemDTO> itemDTOS = formChange.getChildren().stream().filter(s -> ChangeLevelEnum.FORM.getValue().equals(s.getChangeLevel())).toList();
            itemDTOS.forEach(s -> {
                if (changeFieldNames.contains(StrUtils.snakeToCamel(s.getNameEng()))) {
                    changeFlag[0] = true;
                }
            });
        }
    }

    /**
     * 获得客户变更资料
     *
     * @param req
     * @return 客户资料
     */
    @Override
    public CustInfoRespVo getCustChange(CrmCustDetailReq req) {
        CustInfoRespVo cust = getCustDetail(req, true);
        if (cust != null && cust.getVer() > 1) {
            cust.setOldCust(getOldCust(cust));
        }
        return cust;
    }

    /**
     * 根据客户编号获得最新的客户变更资料
     *
     * @param code
     * @return 客户资料
     */
    @Override
    @DataPermission(enable = false)
    public CustInfoRespVo getCustChangeByCode(String code) {
        if (StrUtil.isEmpty(code)) {
            return null;
        }
        LambdaQueryWrapperX<CustDO> custDOLambdaQueryWrapperX = new LambdaQueryWrapperX<CustDO>();
        custDOLambdaQueryWrapperX.eq(CustDO::getCode, code);
        custDOLambdaQueryWrapperX.eq(CustDO::getAuditStatus, BpmProcessInstanceResultEnum.APPROVE.getResult());
        custDOLambdaQueryWrapperX.eq(CustDO::getChangeFlag, ChangeFlagEnum.NO.getValue());
        CustDO custDO = custMapper.selectOne(custDOLambdaQueryWrapperX);
        CustInfoRespVo cust = getCustDetail(new CrmCustDetailReq().setCustId(custDO.getId()), true);
        if (cust != null && cust.getVer() > 1) {
            cust.setOldCust(getOldCust(cust));
        }
        return cust;
    }

    /**
     * 获得旧客户资料
     *
     * @param cust
     * @return 旧客户资料
     */
    @Override
    public CustInfoRespVo getOldCust(CustInfoRespVo cust) {
        LambdaQueryWrapperX<CustDO> custDOLambdaQueryWrapperX = new LambdaQueryWrapperX<CustDO>();
        custDOLambdaQueryWrapperX.eq(CustDO::getCode, cust.getCode());
        custDOLambdaQueryWrapperX.eq(CustDO::getVer, cust.getVer() - 1);
        custDOLambdaQueryWrapperX.eq(CustDO::getAuditStatus, BpmProcessInstanceResultEnum.APPROVE.getResult());
        custDOLambdaQueryWrapperX.eq(CustDO::getChangeDeleted, DeletedEnum.NO.getValue());
        custDOLambdaQueryWrapperX.in(CustDO::getDeleted, new ArrayList<>(Arrays.asList(0, 1)));
        CustDO oldCust = custMapper.selectOne(custDOLambdaQueryWrapperX);
        if (oldCust == null) {
            return null;
        }
        CustInfoRespVo oldCustInfo = getCustDetail(new CrmCustDetailReq().setCustId(oldCust.getId()), true);
        return oldCustInfo;
    }

    private Map<String, Map<Integer, CustInfoRespVo>> getSimpleOldCustMap(List<String> custCodeList){
        LambdaQueryWrapperX<CustDO> custDOLambdaQueryWrapperX = new LambdaQueryWrapperX<CustDO>();
        custDOLambdaQueryWrapperX.in(CustDO::getCode, custCodeList);
        custDOLambdaQueryWrapperX.eq(CustDO::getAuditStatus, BpmProcessInstanceResultEnum.APPROVE.getResult());
        custDOLambdaQueryWrapperX.eq(CustDO::getChangeDeleted, DeletedEnum.NO.getValue());
        custDOLambdaQueryWrapperX.in(CustDO::getDeleted, new ArrayList<>(Arrays.asList(0, 1)));
        List<CustDO> oldCustList = custMapper.selectList(custDOLambdaQueryWrapperX);
        if (CollUtil.isEmpty(oldCustList)){
            return Map.of();
        }
        return oldCustList.stream()
                .collect(Collectors.groupingBy(
                        CustDO::getCode, // 第一层分组：根据 code
                        Collectors.toMap(
                                CustDO::getVer, // 第二层分组：根据 ver
                                custDO ->  BeanUtils.toBean(custDO, CustInfoRespVo.class)) // 转换为 CustInfoRespVo
                        )
                );
    }

    @Override
    public void approveTask(Long userId, BpmTaskApproveReqDTO reqDTO) {
        bpmProcessInstanceApi.approveTask(userId, BeanUtils.toBean(reqDTO, BpmTaskApproveReqDTO.class));
    }

    @Override
    public void rejectTask(Long userId, BpmTaskRejectReqDTO reqDTO) {
        bpmProcessInstanceApi.rejectTask(userId, BeanUtils.toBean(reqDTO, BpmTaskRejectReqDTO.class));
    }

    @Override
    public void submitTask(Long custId, Long userId) {
        bpmProcessInstanceApi.createProcessInstance(userId, PROCESS_DEFINITION_KEY, custId);
        updateAuditStatus(custId, BpmProcessInstanceResultEnum.PROCESS.getResult());
    }

    @Override
    @DataPermission(enable = false)
    public void updateAuditStatus(Long auditableId, Integer auditStatus) {
        CustDO.CustDOBuilder builder = CustDO.builder().id(auditableId);
        builder.auditStatus(auditStatus);
        custMapper.updateById(builder.build());
    }

    @Override
    public String getProcessDefinitionKey() {
        return PROCESS_DEFINITION_KEY;
    }

    @Override
    public void approveChangeTask(Long userId, BpmTaskApproveReqDTO reqDTO) {
        bpmProcessInstanceApi.approveTask(userId, reqDTO);
    }

    @Override
    public void rejectChangeTask(Long userId, BpmTaskRejectReqDTO reqDTO) {
        bpmProcessInstanceApi.rejectTask(userId, reqDTO);
    }

    @Override
    public void submitChangeTask(Long custId, Long userId) {
        bpmProcessInstanceApi.createProcessInstance(userId, CHANGE_PROCESS_DEFINITION_KEY, custId);
        updateChangeAuditStatus(custId, BpmProcessInstanceResultEnum.PROCESS.getResult());
    }

    @Override
    public String getChangeProcessDefinitionKey() {
        return CHANGE_PROCESS_DEFINITION_KEY;
    }

    @Override
    public void updateChangeAuditStatus(Long auditableId, Integer auditStatus) {
        CustDO custDO = validateCustExists(auditableId);
        custDO.setAuditStatus(auditStatus);
        if (auditStatus == BpmProcessInstanceResultEnum.APPROVE.getResult()) {
            custDO.setChangeFlag(ChangeFlagEnum.NO.getValue());
            custDO.setChangeStatus(ChangeStatusEnum.CHANGED.getStatus());
            custDO.setAuditStatus(BpmProcessInstanceResultEnum.APPROVE.getResult());
        } else if (auditStatus == BpmProcessInstanceResultEnum.CANCEL.getResult() || auditStatus == BpmProcessInstanceResultEnum.BACK.getResult()) {
            custDO.setChangeFlag(ChangeFlagEnum.YES.getValue());
            custDO.setChangeStatus(ChangeStatusEnum.NOT_CHANGED.getStatus());
            custDO.setAuditStatus(auditStatus);
            CustDO oldCust = custMapper.selectOne(new LambdaQueryWrapperX<CustDO>().eq(CustDO::getCode, custDO.getCode()).eq(CustDO::getVer, custDO.getVer() - 1));
            oldCust.setChangeFlag(ChangeFlagEnum.NO.getValue());
            oldCust.setChangeStatus(ChangeStatusEnum.NOT_CHANGED.getStatus());
            custMapper.updateById(oldCust);
        }
        custMapper.updateById(custDO);
    }

    @Override
    public void deleteChangeCust(Long id) {

        custMapper.updateById(CustDO.builder()
                .id(id)
                .changeDeleted(DeletedEnum.YES.getValue())
                .build());
        deleteCust(id, false);
//        CustInfoRespVo cust = BeanUtils.toBean(changeReqVO, CustInfoRespVo.class);
//        cust.setVer(cust.getVer() + 1);
//        List<JsonEffectRange> effectRangeList = getChangeEffect(cust);
//        changeReqVO.setEffectRangeList(effectRangeList);
//        custDO.setChangeStatus(ChangeStatusEnum.IN_CHANGE.getStatus());
//        custMapper.updateById(custDO);
    }

    @Override
    @DataPermission(enable = false)
    public PageResult<SimpleCustRespDTO> getSimpleCust(CustPageReqVO pageReqVO) {
        String custNameRegex = null;
        
        // 如果custName不为空，对其进行正则特殊字符转义处理
        if (StrUtil.isNotBlank(pageReqVO.getName())) {
            custNameRegex = escapeRegexSpecialChars(pageReqVO.getName());
        }
        String nameCodeRegex = null;
        if (StrUtil.isNotEmpty(pageReqVO.getNameCode())){
            nameCodeRegex = escapeRegexSpecialChars(pageReqVO.getNameCode());
        }
        Integer pageNo = pageReqVO.getPageNo();
        Integer pageSize = pageReqVO.getPageSize();
        int skip = 0;
        if (pageSize > 0) {
            skip = (pageNo - 1) * pageSize;
        }
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        LocalDateTime[] createTime = pageReqVO.getCreateTime();
        if (createTime != null && createTime.length > 1) {
            startTime = pageReqVO.getCreateTime()[0];
            endTime = pageReqVO.getCreateTime()[1];
        }
        String name = pageReqVO.getName();
        if(Objects.isNull(name) && Objects.nonNull(pageReqVO.getCustName())){
            name = pageReqVO.getCustName();
        }
        List<SimpleCustRespDTO> simpleCustRespDTOList = custMapper.selectSimpleList(startTime, endTime, pageReqVO.getCustomerTypes(),pageReqVO.getManagerIds(), pageReqVO.getCode(),name, custNameRegex,pageReqVO.getInternalFlag(),nameCodeRegex,pageReqVO.getStageTypeFlag(),skip, pageSize);
        //精简列表引用过多，保留currency字段，取值list的默认值
        simpleCustRespDTOList.forEach(s -> {
            List<BaseValue> currencyList = s.getCurrencyList();
            if (CollUtil.isNotEmpty(currencyList)) {
                Optional<BaseValue> first = currencyList.stream().filter(c -> Objects.equals(c.getDefaultFlag(), BooleanEnum.YES.getValue())).findFirst();
                first.ifPresent(baseValue -> s.setCurrency(baseValue.getValue()));
            }
        });
        Long count = custMapper.getCount(startTime, endTime, pageReqVO.getCustomerTypes(),pageReqVO.getManagerIds(), pageReqVO.getCode(), name,nameCodeRegex, pageReqVO.getStageTypeFlag(),pageReqVO.getInternalFlag(),custNameRegex);
        if (CollUtil.isEmpty(simpleCustRespDTOList)) {
            logger.warn("[精简客户列表]未查询到客户列表");
            return null;
        }
        transformCustTypeName(simpleCustRespDTOList);
        transformBankAccount(simpleCustRespDTOList);
        transformCustPo(simpleCustRespDTOList);
        transformCompanyPath(simpleCustRespDTOList);
        transformManager(simpleCustRespDTOList);
        transformSettlement(simpleCustRespDTOList);
        if (CollUtil.isNotEmpty(simpleCustRespDTOList)) {
            List<String> custLinkCodeList = simpleCustRespDTOList.stream()
                    .filter(simpleCustRespDTO -> Objects.nonNull(simpleCustRespDTO.getCustLinkCode()))
                    .flatMap(simpleCustRespDTO -> simpleCustRespDTO.getCustLinkCode().stream())
                    .distinct()
                    .toList();
            Map<String, SimpleData> stringSimpleDataMap = transformCustLink(custLinkCodeList);
            if (CollUtil.isEmpty(stringSimpleDataMap)) {
                return new PageResult<>(simpleCustRespDTOList, count);
            }
            simpleCustRespDTOList.forEach(s -> {
                List<String> custLinkCode = s.getCustLinkCode();
                List<SimpleData> simpleDataResult = new ArrayList<>();
                if (CollUtil.isNotEmpty(custLinkCode)) {
                    custLinkCode.forEach(custCode -> {
                        simpleDataResult.add(stringSimpleDataMap.get(custCode));
                    });
                    s.setCustLink(simpleDataResult);
                }
            });
        }
        return new PageResult<>(simpleCustRespDTOList, count);
    }
    
        /**
     * 转义正则表达式中的特殊字符
     * 
     * @param input 输入字符串
     * @return 转义后的字符串
     */
    private String escapeRegexSpecialChars(String input) {
        if (StrUtil.isBlank(input)) {
            return input;
        }
        
        // 需要转义的特殊字符列表
        final String[] REGEX_SPECIAL_CHARS = {"\\", "^", "$", ".", "[", "]", "|", "(", ")", "?", "*", "+", "{"};
        
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            String c = String.valueOf(input.charAt(i));
            boolean isSpecial = false;
            
            for (String special : REGEX_SPECIAL_CHARS) {
                if (special.equals(c)) {
                    result.append("\\").append(c);
                    isSpecial = true;
                    break;
                }
            }
            if (!isSpecial) {
                result.append(c);
            }
        }
        return result.toString();
    }

    /**
     * 转换客户精简列表中的公司路径
     *
     * @param simpleCustRespDTOList 客户精简列表
     */
    private void transformCompanyPath(List<SimpleCustRespDTO> simpleCustRespDTOList) {
        if (CollUtil.isEmpty(simpleCustRespDTOList)) {
            return;
        }
        // 获取客户编号
        List<Long> custIdList = simpleCustRespDTOList.stream().map(SimpleCustRespDTO::getId).distinct().toList();
        // 获取客户公司路径
        List<CustCompanyPath> allCustCompanyPathList = custCompanyPathMapper.selectList(new LambdaQueryWrapperX<CustCompanyPath>().in(CustCompanyPath::getCustId, custIdList));
        if (CollUtil.isEmpty(allCustCompanyPathList)) {
            return;
        }
        // 公司路径map方便取数据
        Map<String, List<CustCompanyPath>> custCompanyPathMap = allCustCompanyPathList.stream().collect(Collectors.groupingBy(CustCompanyPath::getCustCode));
        if (CollUtil.isEmpty(custCompanyPathMap)) {
            return;
        }
        // 先将当前客户列表所需公司路径查出来 避免循环db
        List<Long> companyPathIdList = allCustCompanyPathList.stream().map(CustCompanyPath::getCompanyPathId).distinct().toList();
        if (CollUtil.isEmpty(companyPathIdList)) {
            return;
        }
        Map<Long, CompanyPathDTO> companyPathDTOMap = companyPathApi.getCompanyPathMap(companyPathIdList);
        if (CollUtil.isEmpty(companyPathDTOMap)) {
            return;
        }
        // 将公司路径复制给客户精简列表
        simpleCustRespDTOList.forEach(s -> {
            String custCode = s.getCode();
            List<CompanyPath> companyPathList = new ArrayList<>();
            List<CustCompanyPath> custCompanyPathList = custCompanyPathMap.get(custCode);
            if (CollUtil.isEmpty(custCompanyPathList)) {
                return;
            }
            custCompanyPathList.forEach(a -> {
                CompanyPathDTO dto = companyPathDTOMap.get(a.getCompanyPathId());
                JsonCompanyPath path = convertToJsonCompanyPath(dto);
                companyPathList.add(new CompanyPath().setId(a.getCompanyPathId()).setPath(path).setDefaultFlag(a.getDefaultFlag()));
            });
            s.setCompanyPath(companyPathList);
        });

    }

    private Map<String, SimpleData> transformCustLink(List<String> custLinkCodeList) {
        if (CollUtil.isEmpty(custLinkCodeList)) {
            return null;
        }
        List<CustDO> custDOList = custMapper.selectList(new LambdaQueryWrapperX<CustDO>()
                .in(CustDO::getCode, custLinkCodeList)
                .eq(CustDO::getChangeFlag, ChangeFlagEnum.NO.getValue()));
        if (CollUtil.isEmpty(custDOList)) {
            return null;
        }
        List<SimpleData> simpleDataList = BeanUtils.toBean(custDOList, SimpleData.class);
        return simpleDataList.stream().collect(Collectors.toMap(SimpleData::getCode, s -> s));
    }

    @Override
    @DataPermission(enable = false)
    public Map<String, String> getCustNameCache(String code) {
        LambdaQueryWrapper<CustDO> queryWrapper = new LambdaQueryWrapperX<CustDO>().select(CustDO::getCode, CustDO::getName);
        if (StrUtil.isNotEmpty(code)) {
            queryWrapper.eq(CustDO::getCode, code);
        }
        List<CustDO> custDOList = custMapper.selectList(queryWrapper);
        Map<String, String> custNameCache = new HashMap<>();
        if (CollUtil.isEmpty(custDOList)) {
            logger.warn("[客户名称缓存]未查询到客户列表");
            return custNameCache;
        }
        custDOList.forEach(s -> custNameCache.put(s.getCode(), s.getName()));
        return custNameCache;
    }

    private void transformManager(List<SimpleCustRespDTO> simpleCustRespDTOList) {
        if (CollUtil.isEmpty(simpleCustRespDTOList)) {
            return;
        }
        Map<Long, List<Long>> managerIdMap = simpleCustRespDTOList.stream().collect(Collectors.toMap(SimpleCustRespDTO::getId, SimpleCustRespDTO::getManagerIds));
        List<Long> userIdList = new ArrayList<>();
        managerIdMap.values().forEach(userIdList::addAll);
        Map<Long, UserDept> userDeptListCache = adminUserApi.getUserDeptListCache(userIdList);
        simpleCustRespDTOList.stream().forEach(s -> {
            List<Long> idList = managerIdMap.get(s.getId());
            if (CollUtil.isNotEmpty(idList)) {
                List<UserDept> managers = idList.stream().map(id -> {
                    if (CollUtil.isNotEmpty(userDeptListCache)) {
                        return userDeptListCache.get(id);
                    }
                    return null;
                }).filter(Objects::nonNull).toList();
                s.setManagerList(managers);
            }
        });
    }

    @Override
    @DataPermission(enable = false)
    public List<SimpleCustRespDTO> getSimpleCustRespDTOList(List<Long> idList) {
        if (CollUtil.isEmpty(idList)) {
            return Collections.emptyList();
        }
        List<SimpleCustRespDTO> simpleCustRespDTOList = custMapper.batchSimpleCustRespDTOList(idList);
        if (CollUtil.isEmpty(simpleCustRespDTOList)) {
            return Collections.emptyList();
        }
//        simpleCustRespDTOList.forEach(simpleCustRespDTO -> {
//            String customerTypes = simpleCustRespDTO.getCustomerTypes();
//            if (StrUtil.isEmpty(customerTypes)) {
//                return;
//            }
//            List<Integer> customerTypeList = SplitUtil.splitToLongList(customerTypes);
//            if (CollUtil.isEmpty(customerTypeList)) {
//                return;
//            }
//            String customerType = customerTypeList.stream().map(s -> {
//                return DictFrameworkUtils.getDictDataLabel(DictTypeConstants.CUSTOM_TYPE, String.valueOf(s));
//            }).collect(Collectors.joining(CommonDict.COMMA));
//            simpleCustRespDTO.setCustomerTypes(customerType);
//        });
        return simpleCustRespDTOList;
    }

    @Override
    public PageResult<SimpleCustRespDTO> getSimpleCustRespDTO(SimplePageReqVO simplePageReqVO) {
        String custName = simplePageReqVO.getCustName();
        
        String custNameRegex = null;
        // 如果custName不为空，对其进行正则特殊字符转义处理
        if (StrUtil.isNotBlank(custName)) {
            custNameRegex = escapeRegexSpecialChars(custName);
        }
        
        Integer pageNo = simplePageReqVO.getPageNo();
        Integer pageSize = simplePageReqVO.getPageSize();
        int skip = 0;
        if (pageSize > 0) {
            skip = (pageNo - 1) * pageSize;
        }
        List<SimpleCustRespDTO> SimpleCustRespDTOList = custMapper.queryMatchCustName(custNameRegex, custName, skip, pageSize);
        Long matchCustCount = custMapper.getMatchCustCount(custNameRegex, custName, skip, pageSize);
        if (CollUtil.isEmpty(SimpleCustRespDTOList)) {
            return null;
        }
        transformBankAccount(SimpleCustRespDTOList);
        return new PageResult<SimpleCustRespDTO>().setList(SimpleCustRespDTOList).setTotal(matchCustCount);
    }

    @Override
    public void convertCust(CustInfoSaveReqVO updateReqVO) {
        updateReqVO.setStageType(StageTypeEnum.FORMAL.getValue());
        updateReqVO.setConvertFlag(BooleanEnum.YES.getValue());
        updateReqVO.setConvertTime(LocalDateTime.now());
        updateCust(updateReqVO);
    }

    @Override
    @DataPermission(enable = false)
    public void enableCust(Long custId) {
        CustDO custDO = custMapper.selectById(custId);
        if (Objects.isNull(custDO)) {
            throw exception(CUST_NOT_EXISTS, custId);
        }
        Integer enableFlag = custDO.getEnableFlag();
        if (Integer.valueOf(EnableStatusEnum.TURN_ON.getStatus()).equals(enableFlag)) {
            throw exception(CLUE_TRUN_ON);
        }
        custDO.setEnableFlag(Integer.valueOf(EnableStatusEnum.TURN_ON.getStatus()));
        custMapper.updateById(custDO);
    }

    @Override
    @DataPermission(enable = false)
    public void disableCust(Long custId) {
        CustDO custDO = custMapper.selectById(custId);
        if (Objects.isNull(custDO)) {
            throw exception(CUST_NOT_EXISTS, custId);
        }
        Integer enableFlag = custDO.getEnableFlag();
        if (Integer.valueOf(EnableStatusEnum.TURN_OFF.getStatus()).equals(enableFlag)) {
            throw exception(CLUE_TRUN_OFF);
        }
        custDO.setEnableFlag(Integer.valueOf(EnableStatusEnum.TURN_OFF.getStatus()));
        custMapper.updateById(custDO);
    }

    @Override
    @DataPermission(enable = false)
    public List<SimpleCustResp> getSimpleCustRespByBankPoc(String bankPoc) {
        List<SimpleCustResp> simpleCustResp = custMapper.getSimpleCustResp(bankPoc);
        if (CollUtil.isEmpty(simpleCustResp)) {
            return null;
        }
        simpleCustResp = simpleCustResp.stream().filter(Objects::nonNull).toList();
        if (CollUtil.isEmpty(simpleCustResp)) {
            return null;
        }
        Map<Long, List<Long>> managerIdMap = simpleCustResp.stream().collect(Collectors.toMap(SimpleCustResp::getId, SimpleCustResp::getManagerIds));
        List<Long> userIdList = new ArrayList<>();
        managerIdMap.values().forEach(userIdList::addAll);
        Map<Long, UserDept> userDeptListCache = adminUserApi.getUserDeptListCache(userIdList);
        simpleCustResp.stream().forEach(s -> {
            List<Long> idList = managerIdMap.get(s.getId());
            if (CollUtil.isNotEmpty(idList)) {
                List<UserDept> managers = idList.stream().map(id -> {
                    if (CollUtil.isNotEmpty(userDeptListCache)) {
                        return userDeptListCache.get(id);
                    }
                    return null;
                }).filter(Objects::nonNull).toList();
                s.setManagerList(managers);
            }
        });
        return simpleCustResp;
    }

    @Override
    public void createCustTitle(Map<String,Long> notExistCustCodeMap, String custTitle) {
        if (CollUtil.isEmpty(notExistCustCodeMap)) {
            return;
        }
        List<CustDO> custDOList = custMapper.selectList(new LambdaQueryWrapperX<CustDO>().select(CustDO::getId,CustDO::getCode).in(CustDO::getCode, notExistCustCodeMap.keySet()).eq(CustDO::getChangeFlag,ChangeFlagEnum.NO.getValue()));
        if (CollUtil.isEmpty(custDOList)) {
            return;
        }
        List<CustBankaccountDO> custBankaccountDOList = custDOList.stream().map(s -> new CustBankaccountDO().setCustId(s.getId()).setBankPoc(custTitle).setPayeeEntityId(notExistCustCodeMap.get(s.getCode()))).toList();
        custBankaccountService.batchSaveBankAccount(custBankaccountDOList);
    }

    @Override
    @DataPermission(enable = false)
    public List<CustDO> getCustByCodeList(List<String> codeList) {
        return custMapper.selectList(CustDO::getCode, codeList);
    }

    @Override
    @DataPermission(enable = false)
    public List<String> getCodeListByName(String custName) {
        if (Objects.isNull(custName)) {
            return null;
        }
        List<CustDO> custDOList = custMapper.selectList(new LambdaQueryWrapperX<CustDO>().likeIfPresent(CustDO::getName, custName));
        if (custDOList == null) {
            return null;
        }
        return custDOList.stream().map(CustDO::getCode).distinct().toList();

    }

    @Override
    @DataPermission(enable = false)
    public List<String> checkName(String name) {
        if (StrUtil.isEmpty(name)) {
            return List.of();
        }
        List<String> result = custMapper.checkName(name);
        if (CollUtil.isEmpty(result)) {
            return List.of();
        }
        return result.stream().filter(StrUtil::isNotEmpty).toList();
    }

    private void transformBankAccount(List<SimpleCustRespDTO> SimpleCustRespDTOList) {
        List<Long> custIdList = SimpleCustRespDTOList.stream().map(SimpleCustRespDTO::getId).toList();
        List<CustBankaccountDO> bankAccountListBycustCodeList = custBankaccountService.getBankAccountListByCustCodeList(custIdList);
        if (CollUtil.isNotEmpty(bankAccountListBycustCodeList)) {
            Map<Long, List<CustBankaccountDO>> custBankaccountMap = bankAccountListBycustCodeList.stream().collect(Collectors.groupingBy(CustBankaccountDO::getCustId));
            SimpleCustRespDTOList.forEach(s -> {
                List<CustBankaccountDO> custBankaccountDOList = custBankaccountMap.get(s.getId());
                if (CollUtil.isNotEmpty(custBankaccountDOList)) {
                    s.setBankAccountList(CustBankAccountConvert.INSTANCE.convertBankAccount(custBankaccountDOList));
                }
            });
        }
    }

    private void transformCustTypeName(List<SimpleCustRespDTO> SimpleCustRespDTOList){
        Map<Long, String> categroyNameMap = categoryService.getCategroyNameMap(null);
        if (CollUtil.isEmpty(categroyNameMap)||CollUtil.isEmpty(SimpleCustRespDTOList)){
            return;
        }
        SimpleCustRespDTOList.forEach(s->{
            List<Long> customerTypes = s.getCustomerTypes();
            if (CollUtil.isEmpty(customerTypes)){
                return;
            }
            String categroyName = customerTypes.stream().map(categroyNameMap::get).collect(Collectors.joining(CommonDict.COMMA));
            s.setCustomerTypesName(categroyName);
        });
    }


    private void transformCustPo(List<SimpleCustRespDTO> SimpleCustRespDTOList) {
        List<Long> custIdList = SimpleCustRespDTOList.stream().map(SimpleCustRespDTO::getId).toList();
        List<CustPocDO> custPocDOListBycustCodeList = custPocService.getPocListByCustIdList(custIdList);
        if (CollUtil.isNotEmpty(custPocDOListBycustCodeList)) {
            Map<Long, List<CustPocDO>> custPocDOMap = custPocDOListBycustCodeList.stream().collect(Collectors.groupingBy(CustPocDO::getCustId));
            SimpleCustRespDTOList.forEach(s -> {
                List<CustPocDO> custPocDOList = custPocDOMap.get(s.getId());
                if (CollUtil.isNotEmpty(custPocDOList)) {
                    s.setCustPocList(CustConvert.INSTANCE.convertCustPocDTO(custPocDOList));
                }
            });
        }
    }

    private void transformSettlement(List<SimpleCustRespDTO> SimpleCustRespDTOList) {
        List<Long> custIdList = SimpleCustRespDTOList.stream().map(SimpleCustRespDTO::getId).toList();
        List<CustSettlementDO> custSettlementDOList = custSettlementMapper.selectList(new LambdaQueryWrapperX<CustSettlementDO>().in(CustSettlementDO::getCustId, custIdList));
        //根据custId 分组
        Map<Long, List<CustSettlementDO>> custSettlementDOMap = custSettlementDOList.stream().collect(Collectors.groupingBy(CustSettlementDO::getCustId));
        if (CollUtil.isNotEmpty(custSettlementDOList)) {
            //获取结汇方式
            List<Long> custSettlementIdList = custSettlementDOList.stream().map(CustSettlementDO::getSettlementId).toList();
            List<com.syj.eplus.module.infra.api.settlement.dto.SettlementDTO> allsettlementDTOList = settlementApi.getSettlementWithCollectionPlansById(custSettlementIdList);
            //结汇方式根据id分组
            Map<Long, List<com.syj.eplus.module.infra.api.settlement.dto.SettlementDTO>> allsettlementDTOMap = allsettlementDTOList.stream().collect(Collectors.groupingBy(com.syj.eplus.module.infra.api.settlement.dto.SettlementDTO::getId));
            SimpleCustRespDTOList.forEach(s -> {
                List<CustSettlementDO> custSettlementList = custSettlementDOMap.get(s.getId());
                List<com.syj.eplus.module.crm.api.cust.dto.SettlementDTO> settlementList = new ArrayList<>();
                if (CollUtil.isNotEmpty(custSettlementList)) {
                    custSettlementList.forEach(csl -> {
                        List<com.syj.eplus.module.infra.api.settlement.dto.SettlementDTO> settlementDTOList = allsettlementDTOMap.get(csl.getSettlementId());
                        if (CollUtil.isNotEmpty(settlementDTOList)) {
                            settlementDTOList.forEach(sd -> {
                                com.syj.eplus.module.crm.api.cust.dto.SettlementDTO settlementDTO = CustConvert.INSTANCE.convertSettlementDTO(sd);
                                settlementDTO.setDefaultFlag(csl.getDefaultFlag());
                                settlementList.add((settlementDTO));
                            });
                        }
                    });
                    s.setSettlementList(settlementList);
                }
            });
        }
    }

    @Override
    @DataPermission(enable = false)
    public String getProcessDefinitionKeyByBusinessId(Long id) {
        CustDO custChange = custMapper.selectById(id);
        if (Objects.isNull(custChange)) {
            return null;
        }
        return custChange.getModelKey();
    }

    @Override
    public List<String> getCodeListByCountryCode(Long countryId, String regionCode) {
        //区域不为空，国家为空  查询区域所有国家  再查对应的客户
        if (Objects.isNull(countryId) && StrUtil.isNotEmpty(regionCode)) {
            List<CountryInfoDTO> countryList = countryInfoApi.getCountryListByRegionCode(regionCode);
            if (CollUtil.isNotEmpty(countryList)) {
                List<Long> idList = countryList.stream().map(CountryInfoDTO::getId).toList();
                return custMapper.selectList(CustDO::getCountryId, idList).stream().map(CustDO::getCode).toList();
            }
            return null;
        }
        //区域为空  国家不为空 直接查对应国家客户
        else if (Objects.nonNull(countryId) && StrUtil.isEmpty(regionCode)) {
            return custMapper.selectList(CustDO::getCountryId, countryId).stream().map(CustDO::getCode).toList();
        }
        //区域国家都不为空  校验国家是否属于区域，是返回国家对应的客户   不是返回空
        else if (Objects.nonNull(countryId) && StrUtil.isNotEmpty(regionCode)) {
            Map<Long, CountryInfoDTO> countryInfoMap = countryInfoApi.getCountryInfoMap();
            CountryInfoDTO countryInfoDTO = countryInfoMap.get(countryId);
            if (Objects.equals(countryInfoDTO.getRegionCode(), regionCode)) {
                return custMapper.selectList(CustDO::getCountryId, countryId).stream().map(CustDO::getCode).toList();
            } else {
                return null;
            }

        }
        //传参为空，查询客户信息为空
        else {
            return null;
        }
    }

    @Override
    @DataPermission(enable = false)
    public List<CustDO> getCustomersWithIdGreaterThan(Long lastId) {
        return custMapper.selectList(new LambdaQueryWrapperX<CustDO>().gt(CustDO::getId, lastId));
    }
    @Override
    @DataPermission(enable = false)
    public CustAllDTO getCustByCode(String custCode) {
        Optional<CustDO> first = custMapper.selectList(CustDO::getCode, custCode).stream().findFirst();
        if (first.isPresent()) {
            CustAllDTO dto = BeanUtils.toBean(first.get(), CustAllDTO.class);
            Map<Long, CountryInfoDTO> countryInfoMap = countryInfoApi.getCountryInfoMap();
            if (CollUtil.isNotEmpty(countryInfoMap)) {
                CountryInfoDTO countryInfoDTO = countryInfoMap.get(dto.getCountryId());
                if (Objects.nonNull(countryInfoDTO)) {
                    dto.setCountryName(countryInfoDTO.getName());
                    dto.setRegionName(countryInfoDTO.getRegionName());
                }
            }
            return dto;
        }
        return null;

    }

    @Override
    @DataPermission(enable = false)
    public List<MarkDO> getMarkListByCustCode(Long custId) {
        return markService.getMarkListByCustId(custId);
    }

    @Override
    @DataPermission(enable = false)
    public boolean antiAudit(Long id) {
        // 校验是否存在
        CustDO custDO = validateCustExists(id);
        // 校验反审核状态
        validateAntiAuditStatus(id);
        // 更改单据状态
        custDO.setAuditStatus(BpmProcessInstanceResultEnum.UNSUBMITTED.getResult());
        int i = custMapper.updateById(custDO);
        return i > 0;
    }

    @Override
    public List<String> getCodeListByNameList(List<String> nameList) {

        List<CustDO> doList = custMapper.selectList(CustDO::getName, nameList);
        if(CollUtil.isEmpty(doList)){
            return  null;
        }
        return doList.stream().map(CustDO::getCode).distinct().toList();


    }

    @Override
    public List<CustBankAccountDTO> getBankPocListByTitle(String title) {
        return custBankaccountService.getBankPocListByTitle(title);
    }

    @Override
    public List<SimpleCustResp> getSimpleCustRespByCode(List<String> cutCodeList) {
        List<CustDO> custDOList = custMapper.selectList(new LambdaQueryWrapperX<CustDO>().select(CustDO::getId,CustDO::getCode,CustDO::getName,CustDO::getManagerIds)
                .in(CustDO::getCode,cutCodeList).eq(CustDO::getChangeFlag,BooleanEnum.NO.getValue()).eq(CustDO::getEnableFlag,BooleanEnum.YES.getValue())
                .eq(CustDO::getAuditStatus,BpmProcessInstanceResultEnum.APPROVE.getResult()));
        if (CollUtil.isEmpty(custDOList)){
            return List.of();
        }
        List<Long> managerIdList = custDOList.stream().flatMap(s -> s.getManagerIds().stream()).distinct().toList();
        Map<Long, UserDept> userDeptListCache = adminUserApi.getUserDeptListCache(managerIdList);

        List<SimpleCustResp> simpleCustResps = BeanUtils.toBean(custDOList, SimpleCustResp.class);
        simpleCustResps.forEach(x -> {
            List<UserDept> managerList = managerIdList.stream().map(s -> {
                if (CollUtil.isNotEmpty(userDeptListCache)) {
                    return userDeptListCache.get(s);
                }
                return null;
            }).filter(Objects::nonNull).toList();
            x.setManagerList(managerList);
        });
        return simpleCustResps;
    }

    @Override
    public void deleteCustTitle(List<Long> payeeEntityIds) {
        custBankaccountMapper.delete(new LambdaQueryWrapperX<CustBankaccountDO>().in(CustBankaccountDO::getPayeeEntityId, payeeEntityIds));
    }

    @Override
    public boolean checkIsInternalCust(String custCode) {
        List<CustDO> custDOS = custMapper.selectList(new LambdaQueryWrapperX<CustDO>().eq(CustDO::getCode, custCode).eq(CustDO::getChangeFlag, BooleanEnum.NO.getValue()));
        if (CollUtil.isEmpty(custDOS)){
            return false;
        }
        return custDOS.stream().anyMatch(s->BooleanEnum.YES.getValue().equals(s.getInternalFlag()));
    }

    @Override
    public Long getInternalCompany(String custCode) {
        if (StrUtil.isEmpty(custCode)){
            return null;
        }
        List<CustDO> custDOS = custMapper.selectList(new LambdaQueryWrapperX<CustDO>().eq(CustDO::getCode, custCode).eq(CustDO::getChangeFlag, BooleanEnum.NO.getValue()).eq(CustDO::getInternalFlag, BooleanEnum.YES.getValue()));
        if (CollUtil.isEmpty(custDOS)){
            return null;
        }
        return custDOS.get(0).getInternalCompanyId();
    }

    @Override
    public List<SystemCollectionPlanDTO> getCollectionPlanDTOByCustCode(String custCode) {
        if (StrUtil.isEmpty(custCode)){
            return List.of();
        }
        List<CustDO> custDOS = custMapper.selectList(new LambdaQueryWrapperX<CustDO>().select(CustDO::getId).eq(CustDO::getCode, custCode).eq(CustDO::getChangeFlag, BooleanEnum.NO.getValue()));
        if (CollUtil.isEmpty(custDOS)){
            return List.of();
        }
        CustDO custDO = custDOS.stream().findFirst().get();
        Long custId = custDO.getId();
        List<CustSettlementDO> custSettlementDOS = custSettlementMapper.selectList(new LambdaQueryWrapperX<CustSettlementDO>().eq(CustSettlementDO::getCustId, custId).eq(CustSettlementDO::getDefaultFlag, BooleanEnum.YES.getValue()));
        if (CollUtil.isEmpty(custSettlementDOS)){
            return List.of();
        }
        // Get settlement ID from default customer settlement
        CustSettlementDO custSettlementDO = custSettlementDOS.stream().findFirst().get();
        Long settlementId = custSettlementDO.getSettlementId();
        if (settlementId == null) {
            return List.of();
        }
        
        // Fetch settlement with collection plans from system API
        List<com.syj.eplus.module.infra.api.settlement.dto.SettlementDTO> settlementDTOList = 
            settlementApi.getSettlementWithCollectionPlansById(Collections.singletonList(settlementId));
        if (CollUtil.isEmpty(settlementDTOList)) {
            return List.of();
        }
        
        com.syj.eplus.module.infra.api.settlement.dto.SettlementDTO settlementDTO = settlementDTOList.get(0);
        List<com.syj.eplus.module.infra.api.settlement.dto.SystemCollectionPlanDTO> systemCollectionPlanList = 
            settlementDTO.getCollectionPlanList();
        if (CollUtil.isEmpty(systemCollectionPlanList)) {
            return List.of();
        }
        
        // Convert system DTO to CRM DTO
        return BeanUtils.toBean(systemCollectionPlanList, SystemCollectionPlanDTO.class);
    }

    @Override
    public String getBankPocByCustCode(String custCode) {
        if (StrUtil.isEmpty(custCode)){
            return null;
        }
        List<CustDO> custDOS = custMapper.selectList(new LambdaQueryWrapperX<CustDO>().select(CustDO::getId).eq(CustDO::getCode, custCode).eq(CustDO::getChangeFlag, BooleanEnum.NO.getValue()));
        if (CollUtil.isEmpty(custDOS)){
            return null;
        }
        Long custId = custDOS.stream().findFirst().get().getId();
        List<CustBankaccountDO> bankAccountListByCustId = custBankaccountService.getBankAccountListByCustId(custId);
        if (CollUtil.isEmpty(bankAccountListByCustId)){
            return null;
        }
        Optional<CustBankaccountDO> bankaccountDOOpt = bankAccountListByCustId.stream().filter(s -> s.getDefaultFlag().equals(BooleanEnum.YES.getValue())).findFirst();
        if (bankaccountDOOpt.isPresent()){
            return bankaccountDOOpt.get().getBankPoc();
        }
        return CommonDict.EMPTY_STR;
    }

    @Override
    public String getCustPocByCustCode(String custCode) {
        if (StrUtil.isEmpty(custCode)){
            return null;
        }
        List<CustDO> custDOS = custMapper.selectList(new LambdaQueryWrapperX<CustDO>().select(CustDO::getId).eq(CustDO::getCode, custCode).eq(CustDO::getChangeFlag, BooleanEnum.NO.getValue()));
        if (CollUtil.isEmpty(custDOS)){
            return null;
        }
        Long custId = custDOS.stream().findFirst().get().getId();
        List<CustPocRespVO> pocListByCustId = custPocService.getPocListByCustId(custId);
        if (CollUtil.isEmpty(pocListByCustId)){
            return null;
        }
        Optional<CustPocRespVO> pocDefault = pocListByCustId.stream().filter(s -> s.getDefaultFlag().equals(BooleanEnum.YES.getValue())).findFirst();
        if (pocDefault.isPresent()){
            return pocDefault.get().getName();
        }
        return CommonDict.EMPTY_STR;
    }

    @Override
    public void managerDeleteCust(Long id) {
        CustDO custDO = validateCustExists(id);
        boolean exists = saleContractApi.validateCustExists(custDO.getCode());
        if (exists){
            throw exception(SALE_CONTRACT_IS_EXISTS);
        }
        boolean paymentExists = paymentAppApi.validateCustExists(custDO.getCode());
        if (paymentExists){
            throw exception(PAYMENT_APP_IS_EXISTS);
        }
        boolean freeShareExists = feeShareApi.validateCustExists(custDO.getCode());
        if (freeShareExists){
            throw exception(FREE_SHARE_IS_EXISTS);
        }
        deleteCust( id,true);
    }

    @Override
    @DataPermission(enable = false)
    public PageResult<CustRespVO> custDuplicateCheck(CustPageReqVO pageReqVO) {
        List<Long> managerIds = pageReqVO.getManagerIds();
        List<String> managerIds_str = new ArrayList<>();
        if (CollUtil.isNotEmpty(managerIds)) {
            managerIds_str = managerIds.stream().map(String::valueOf).collect(Collectors.toList());
        }
        pageReqVO.setChangeFlag(ChangeFlagEnum.NO.getValue());
        PageResult<CustDO> custDOPageResult = custMapper.selectPage(pageReqVO, managerIds_str);
        Map<Long, CountryInfoDTO> countryInfoMap = countryInfoApi.getCountryInfoMap();
        // 客户分类名称缓存
        Map<Long, String> categroyNameMap = categoryService.getCategroyNameMap(null);
        return CustConvert.INSTANCE.convertToCustRespPageResult(custDOPageResult, categroyNameMap, countryInfoMap);
    }

    @DataPermission(enable = false)
    @Override
    public Map<Long, CustDO> getCustByIds(List<Long> custIds) {
        if(CollUtil.isEmpty(custIds)){
            return null;
        }
        List<CustDO> custDOS = custMapper.selectList(
                new LambdaQueryWrapper<CustDO>().in(CustDO::getId, custIds).in(CustDO::getDeleted, 0, 1)
        );
        if(CollUtil.isEmpty(custDOS)){
            return  null;
        }
        return custDOS.stream().collect(Collectors.toMap(CustDO::getId, custDO -> custDO));
    }

    @Override
    public Map<Long, BaseData> getAllInnerCustCache() {
        LambdaQueryWrapper<CustDO> queryWrapper = new LambdaQueryWrapperX<CustDO>().select(CustDO::getId, CustDO::getCode, CustDO::getName,CustDO::getInternalCompanyId)
                .eq(CustDO::getInternalFlag, BooleanEnum.YES.getValue())
                .eq(CustDO::getChangeFlag, BooleanEnum.NO.getValue());
        List<CustDO> custDOS = custMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(custDOS)){
            return Map.of();
        }
        return custDOS.stream().collect(Collectors.toMap(CustDO::getInternalCompanyId, s -> new BaseData().setId(s.getId()).setName(s.getName()).setCode(s.getCode()), (s1, s2) -> s1));
    }


    /**
     * 校验反审核状态
     * @param id 主键
     */
    private void validateAntiAuditStatus(Long id) {
        Long count = custMapper.validateAntiAuditStatus(id);
        if (count > 0) {
            throw exception(ANTI_AUDIT_EXCEPT);
        }
    }

    @Override
    public List<ConfirmSourceEntity> getConfirmSourceList(String targetCode, Integer effectRangeType) {
        LambdaQueryWrapper<CustDO> queryWrapper = new LambdaQueryWrapper<CustDO>().select(CustDO::getId, CustDO::getCode)
                .apply("JSON_CONTAINS(effect_range_list,cast({0} as json))", JsonUtils.toJsonString(new JsonEffectRange().setEffectRangeType(effectRangeType).setEffectRangeCode(targetCode).setConfirmFlag(0)));
        List<CustDO> TList = custMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(TList)) {
            return List.of();
        }
        return TList.stream().map(s -> new ConfirmSourceEntity().setId(s.getId()).setCode(s.getCode()).setConfirmSourceType(EffectRangeEnum.CUST.getValue())).toList();
    }

    @Override
    public List<SimpleCustResp> getSimpleCustRespByName(String custName) {
        List<CustDO> custDOList = custMapper.selectList(new LambdaQueryWrapperX<CustDO>().select(CustDO::getId,CustDO::getCode,CustDO::getName,CustDO::getManagerIds)
                .like(CustDO::getName,custName).eq(CustDO::getChangeFlag,BooleanEnum.NO.getValue()).eq(CustDO::getEnableFlag,BooleanEnum.YES.getValue())
                .eq(CustDO::getAuditStatus,BpmProcessInstanceResultEnum.APPROVE.getResult()));
        if (CollUtil.isEmpty(custDOList)){
            return List.of();
        }
        List<Long> managerIdList = custDOList.stream().flatMap(s -> s.getManagerIds().stream()).distinct().toList();
        Map<Long, UserDept> userDeptListCache = adminUserApi.getUserDeptListCache(managerIdList);
        List<SimpleCustResp> simpleCustResps = BeanUtils.toBean(custDOList, SimpleCustResp.class);
        simpleCustResps.forEach(x -> {
            List<UserDept> managerList = managerIdList.stream().map(s -> {
                if (CollUtil.isNotEmpty(userDeptListCache)) {
                    return userDeptListCache.get(s);
                }
                return null;
            }).filter(Objects::nonNull).toList();
            x.setManagerList(managerList);
        });
        return simpleCustResps;
    }

    /**
     * 将 CompanyPathDTO 转换为 JsonCompanyPath
     */
    private JsonCompanyPath convertToJsonCompanyPath(CompanyPathDTO dto) {
        if (dto == null) {
            return null;
        }
        JsonCompanyPath path = new JsonCompanyPath();
        path.setId(dto.getId());
        path.setName(dto.getName());
        path.setAllocateType(dto.getAllocateType());
        path.setRangeMinRatio(dto.getRangeMinRatio());
        path.setRangeMaxRatio(dto.getRangeMaxRatio());
        path.setFixRatio(dto.getFixRatio());
        path.setAllocateConditionType(dto.getAllocateConditionType());
        path.setAllocateConditionValue(dto.getAllocateConditionValue());
        path.setProcessedFlag(dto.getProcessedFlag());
        path.setLevel(dto.getLevel());
        path.setParentId(dto.getParentId());
        // 递归转换下一个节点
        if (dto.getNext() != null) {
            path.setNext(convertToJsonCompanyPath(dto.getNext()));
        }
        return path;
    }
}
