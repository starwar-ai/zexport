package com.syj.eplus.module.scm.service.vender;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Tuple;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.dict.CommonDict;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.pojo.ConfirmSourceEntity;
import cn.iocoder.yudao.framework.common.pojo.JsonEffectRange;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.common.util.string.StrUtils;
import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;
import cn.iocoder.yudao.framework.ip.core.Area;
import cn.iocoder.yudao.framework.ip.core.utils.AreaUtils;
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
import cn.iocoder.yudao.module.system.api.dept.DeptApi;
import cn.iocoder.yudao.module.system.api.dept.dto.DeptRespDTO;
import com.syj.eplus.module.infra.api.formchange.FormChangeApi;
import com.syj.eplus.module.infra.api.formchange.dto.FormChangeDTO;
import com.syj.eplus.module.infra.api.formchange.dto.FormChangeItemDTO;
import com.syj.eplus.module.infra.api.paymentitem.PaymentItemApi;
import com.syj.eplus.module.infra.api.paymentitem.dto.PaymentItemDTO;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.syj.eplus.framework.common.entity.*;
import com.syj.eplus.framework.common.enums.*;
import com.syj.eplus.framework.common.util.NumUtil;
import com.syj.eplus.module.crm.enums.cust.DeletedEnum;
import com.syj.eplus.module.crm.enums.cust.EffectMainInstanceFlagEnum;
import com.syj.eplus.module.crm.enums.cust.EffectRangeEnum;
import com.syj.eplus.module.dms.enums.api.ShipmentApi;
import com.syj.eplus.module.dms.enums.api.dto.ShipmentDTO;
import com.syj.eplus.module.infra.api.CodeGeneratorApi;
import com.syj.eplus.module.oa.api.PaymentAppApi;
import com.syj.eplus.module.oa.api.feeshare.FeeShareApi;
import com.syj.eplus.module.pms.api.Category.CategoryApi;
import com.syj.eplus.module.pms.api.Category.dto.SimpleCategoryDTO;
import com.syj.eplus.module.scm.api.purchasecontract.PurchaseContractApi;
import com.syj.eplus.module.scm.api.purchasecontract.dto.PurchaseContractAllDTO;
import com.syj.eplus.module.scm.api.vender.dto.SimpleVenderResp;
import com.syj.eplus.module.scm.api.vender.dto.SimpleVenderRespDTO;
import com.syj.eplus.module.scm.api.vender.dto.VenderPaymentDTO;
import com.syj.eplus.module.scm.api.vender.dto.VenderPocDTO;
import com.syj.eplus.module.scm.controller.admin.vender.vo.*;
import com.syj.eplus.module.scm.controller.admin.venderbankaccount.vo.VenderBankaccountRespVO;
import com.syj.eplus.module.scm.controller.admin.venderbankaccount.vo.VenderBankaccountSaveReqVO;
import com.syj.eplus.module.scm.controller.admin.venderpoc.vo.VenderPocRespVO;
import com.syj.eplus.module.scm.controller.admin.venderpoc.vo.VenderPocSaveReqVO;
import com.syj.eplus.module.scm.convert.VenderBankaccountConvert;
import com.syj.eplus.module.scm.convert.VenderConvert;
import com.syj.eplus.module.scm.dal.dataobject.qualification.QualificationDO;
import com.syj.eplus.module.scm.dal.dataobject.vender.VenderDO;
import com.syj.eplus.module.scm.dal.dataobject.venderbankaccount.VenderBankaccountDO;
import com.syj.eplus.module.scm.dal.dataobject.venderpayment.VenderPayment;
import com.syj.eplus.module.scm.dal.dataobject.venderpoc.VenderPocDO;
import com.syj.eplus.module.scm.dal.mysql.qualification.QualificationMapper;
import com.syj.eplus.module.scm.dal.mysql.vender.VenderMapper;
import com.syj.eplus.module.scm.dal.mysql.venderpayment.VenderPaymentMapper;
import com.syj.eplus.module.scm.service.purchasecontract.PurchaseContractService;
import com.syj.eplus.module.scm.service.venderbankaccount.VenderBankaccountService;
import com.syj.eplus.module.scm.service.venderpoc.VenderPocService;
import com.syj.eplus.module.wms.api.warehouse.IWarehouseApi;
import com.syj.eplus.module.wms.api.warehouse.dto.WarehouseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.crm.enums.ErrorCodeConstants.*;
import static com.syj.eplus.module.crm.enums.ErrorCodeConstants.CHANGE_NOT_ALLOWED;
import static com.syj.eplus.module.crm.enums.ErrorCodeConstants.NOT_UPDATE_PROCESS;
import static com.syj.eplus.module.scm.enums.ErrorCodeConstants.*;
import static com.syj.eplus.module.scm.enums.ErrorCodeConstants.FIELD_CHANGE_CONFIG_NOT_EXISTS;
import static com.syj.eplus.module.scm.enums.ErrorCodeConstants.IN_CHANGE_NOT_ALLOWED;


/**
 * 供应商信息 Service 实现类
 *
 * @author zhangcm
 */
@Service
@Validated
public class VenderServiceImpl extends ServiceImpl<VenderMapper, VenderDO> implements VenderService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private VenderMapper venderMapper;

    @Resource
    private VenderBankaccountService venderBankaccountService;

    @Resource
    private VenderPocService venderPocService;

    @Resource
    private BpmProcessInstanceApi processInstanceApi;

    @Resource
    private AdminUserApi adminUserApi;

    @Resource
    private DeptApi deptApi;

    @Resource
    private PaymentItemApi paymentItemApi;

    @Resource
    private CategoryApi categoryApi;
    @Resource
    private IWarehouseApi warehouseApi;
    @Resource
    @Lazy
    private PurchaseContractService purchaseContractService;
    @Resource
    @Lazy
    private PaymentAppApi paymentAppApi;
    @Resource
    @Lazy
    private FeeShareApi feeShareApi;

    private static final String PROCESS_DEFINITION_KEY = "oa_vender";
    private static final String BUSINESS_PROCESS_DEFINITION_KEY = "oa_business_vender";

    private static final String CHANGE_PROCESS_DEFINITION_KEY = "oa_change_vender";
    private static final String BUSINESS_CHANGE_PROCESS_DEFINITION_KEY = "oa_change_business_vender";

    private static final String CODE_PREFIX = "V";
    public static final String SN_TYPE = "SN_VENDER";

    @Resource
    private BpmProcessInstanceApi bpmProcessInstanceApi;

    @Resource
    private CodeGeneratorApi codeGeneratorApi;

    @Resource
    private VenderPaymentMapper venderPaymentMapper;

    @Resource
    private QualificationMapper qualificationMapper;

    @Resource
    private FormChangeApi formChangeApi;

    @Resource
    private PurchaseContractApi purchaseContractApi;

    @Resource
    private ShipmentApi shipmentApi;

    @Resource
    private CompanyApi companyApi;
    @Transactional(rollbackFor = Exception.class)
    public Long createVenderDetail(VenderInfoSaveReqVO venderInfoSaveReqVO, boolean isChange) {
        venderInfoSaveReqVO.setAuditStatus(BpmProcessInstanceResultEnum.UNSUBMITTED.getResult());

        VenderDO vender = BeanUtils.toBean(venderInfoSaveReqVO, VenderDO.class);
        vender.setEnableFlag(BooleanEnum.YES.getValue());
        vender.setChangeStatus(ChangeStatusEnum.NOT_CHANGED.getStatus());
        if (!isChange) {
            vender.setChangeFlag(ChangeFlagEnum.NO.getValue());
        }
        //处理供应商Ids，该字段用于权限，前端弃用，后台需要维护
        List<BaseValue> buyerList = vender.getBuyerList();
        List<Long> ids = new ArrayList<>();
        if(CollUtil.isNotEmpty(buyerList)){
            buyerList.forEach(s->{
                try{
                    ids.add(Long.parseLong(s.getValue()));
                }catch (Exception exception){
                    throw exception;
                }
            });
        }
        vender.setBuyerIds(ids);
        // 插入供应商基本信息
        venderMapper.insert(vender);
        //此时id为数据库自增生成id
        Long venderId = vender.getId();
        Integer ver = vender.getVer();
        List<VenderBankaccountSaveReqVO> bankaccountList = venderInfoSaveReqVO.getBankaccountList();
        if (CollUtil.isNotEmpty(bankaccountList)) {
            List<VenderBankaccountDO> venderBankaccountDOList = BeanUtils.toBean(bankaccountList, VenderBankaccountDO.class);
            venderBankaccountDOList.forEach(s -> {
                s.setId(null);
                s.setVenderId(venderId);
                s.setVer(ver);
            });
            venderBankaccountService.batchSaveBankAccount(venderBankaccountDOList);
        }
        List<VenderPocSaveReqVO> pocList = venderInfoSaveReqVO.getPocList();
        if (CollUtil.isNotEmpty(pocList)) {
            List<VenderPocDO> venderPocDOList = BeanUtils.toBean(pocList, VenderPocDO.class);
            venderPocDOList.forEach(s -> {
                s.setId(null);
                s.setVenderId(venderId);
                s.setVer(ver);
            });
            venderPocService.batchSavePoc(venderPocDOList);
        }
        // 写入付款方式
        List<VenderPayment> paymentList = venderInfoSaveReqVO.getPaymentList();
        if (CollUtil.isNotEmpty(paymentList)) {
            paymentList.forEach(s -> {
                s.setId(null);
                s.setVenderId(venderId);
            });
            venderPaymentMapper.insertBatch(paymentList);
        }
        if (SubmitFlagEnum.SUBMIT.getStatus().equals(venderInfoSaveReqVO.getSubmitFlag())) {
            if (isChange) {
                submitChangeTask(venderId, WebFrameworkUtils.getLoginUserId());
            } else {
                submitTask(venderId, WebFrameworkUtils.getLoginUserId());
            }
        }
        return venderId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createVender(VenderInfoSaveReqVO venderInfoSaveReqVO, boolean isUpdate) {
        // 新增时校验供应商名称是否已存在
        if (!isUpdate) {
            validateCustNameExists(venderInfoSaveReqVO.getName());
        }
        if(venderInfoSaveReqVO.getVenderType().equals(VenderTypeEnum.ADMINISTRATION_VENDER.getValue())){
            String code = codeGeneratorApi.getCodeGenerator(SN_TYPE, CODE_PREFIX);
            venderInfoSaveReqVO.setCode(code);
        }else{
            Integer factoryAreaId = venderInfoSaveReqVO.getFactoryAreaId();
            if (Objects.isNull(factoryAreaId)){
                throw exception(FACTORY_AREA_NOT_NULL);
            }
            Area area = AreaUtils.getArea(factoryAreaId);
            String codePrefix = NumUtil.padCode(area.getAreaCode(), 4);
            String code = codeGeneratorApi.getCodeGenerator(SN_TYPE,codePrefix,false,4);
            venderInfoSaveReqVO.setCode(code);
        }
        //新增版本默认为1
        venderInfoSaveReqVO.setVer(1);
        return createVenderDetail(venderInfoSaveReqVO, false);
    }

    private void validateCustNameExists(String name) {
        boolean exists = venderMapper.exists(new LambdaQueryWrapperX<VenderDO>().eq(VenderDO::getName, name));
        if (exists) {
            throw exception(VENDER_NAME_EXISTS);
        }
    }
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public Long updateVender(VenderInfoSaveReqVO updateReqVO) {
//        Long venderId = updateReqVO.getId();
//        //删除方法中已经校验审核状态 此处直接调用
//        deleteVender(venderId, false);
//        //数据库自增 将id置空
//        updateReqVO.setId(null);
//        //版本加1
//        updateReqVO.setVer(updateReqVO.getVer() + 1);
//        return createVender(updateReqVO, true);
//    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateVender(VenderInfoSaveReqVO updateReqVO) {
        // 已在审核中的数据不允许修改
        if (BpmProcessInstanceResultEnum.PROCESS.getResult().equals(updateReqVO.getAuditStatus())) {
            throw exception(NOT_UPDATE_PROCESS);
        }
        // 校验存在
        validateVenderExists(updateReqVO.getId());
        VenderDO vender = BeanUtils.toBean(updateReqVO, VenderDO.class);
        //处理供应商Ids，该字段用于权限，前端弃用，后台需要维护
        List<BaseValue> buyerList = vender.getBuyerList();
        List<Long> ids = new ArrayList<>();
        if(CollUtil.isNotEmpty(buyerList)){
            buyerList.forEach(s->{
                try{
                    ids.add(Long.parseLong(s.getValue()));
                }catch (Exception exception){
                    throw exception;
                }
            });
        }
        vender.setBuyerIds(ids);
        venderMapper.updateById(vender);

        //此时id为数据库自增生成id
        Long venderId = vender.getId();
        Integer ver = vender.getVer();

        List<VenderBankaccountSaveReqVO> bankaccountList = updateReqVO.getBankaccountList();
        if (CollUtil.isNotEmpty(bankaccountList)) {
            List<VenderBankaccountDO> venderBankaccountDOList = BeanUtils.toBean(bankaccountList, VenderBankaccountDO.class);
            venderBankaccountDOList.forEach(s -> {
                s.setId(null);
                s.setVenderId(venderId);
                s.setVer(ver);
            });
            //获取修改前的供应商银行信息列表
            List<VenderBankaccountDO> oldBankAccountList = venderBankaccountService.getBankAccountListByVenderId(venderId);
            //不为空进行删除 为空不做操作减少数据库访问次数
            if (CollUtil.isNotEmpty(oldBankAccountList)) {
                List<Long> idList = oldBankAccountList.stream().map(VenderBankaccountDO::getId).toList();
                venderBankaccountService.batchDeleteBankAccountList(idList);
            }
            venderBankaccountService.batchSaveBankAccount(venderBankaccountDOList);
        }
        List<VenderPocSaveReqVO> pocList = updateReqVO.getPocList();
        if (CollUtil.isNotEmpty(pocList)) {
            List<VenderPocDO> venderPocDOList = BeanUtils.toBean(pocList, VenderPocDO.class);
            venderPocDOList.forEach(s -> {
                s.setId(null);
                s.setVenderId(venderId);
                s.setVer(ver);
            });
            //获取修改前的供应商联系人列表
            List<VenderPocDO> oldPocList = venderPocService.getPocListByVenderId(venderId);
            //不为空进行删除 为空不做操作减少数据库访问次数
            if (CollUtil.isNotEmpty(oldPocList)) {
                List<Long> idList = oldPocList.stream().map(VenderPocDO::getId).toList();
                venderPocService.batchDeletePocByIds(idList);
            }
            venderPocService.batchSavePoc(venderPocDOList);
        }
        // 写入付款方式
        List<VenderPayment> paymentList = updateReqVO.getPaymentList();
        if (CollUtil.isNotEmpty(paymentList)) {
            paymentList.forEach(s -> {
                s.setId(null);
                s.setVenderId(venderId);
            });
            //获取修改前的付款方式列表
            List<VenderPayment> venderPaymentList = venderPaymentMapper.selectList(VenderPayment::getVenderId, venderId);
            if (CollUtil.isNotEmpty(venderPaymentList)) {
                venderPaymentMapper.deleteBatchIds(venderPaymentList.stream().map(VenderPayment::getId).toList());
            }
            venderPaymentMapper.insertBatch(paymentList);
        }
        if (SubmitFlagEnum.SUBMIT.getStatus().equals(updateReqVO.getSubmitFlag())) {
            submitTask(venderId, WebFrameworkUtils.getLoginUserId());
        }
    }

    /**
     * 变更供应商
     *
     * @param changeReqVO 更新信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long changeVender(VenderInfoSaveReqVO changeReqVO) {
        //已经有高版本客户信息，无法变更
        List<VenderDO> venderDOs = venderMapper.selectList(new LambdaQueryWrapperX<VenderDO>()
                .eqIfPresent(VenderDO::getCode, changeReqVO.getCode())
                .eqIfPresent(VenderDO::getChangeFlag, ChangeFlagEnum.NO.getValue())
                .eqIfPresent(VenderDO::getVer, changeReqVO.getVer() + 1)
        );
        if (CollUtil.isNotEmpty(venderDOs)) {
            throw exception(CHANGE_NOT_ALLOWED);
        }
        VenderDO venderDO = validateVenderExists(changeReqVO.getId());
        if (Objects.equals(venderDO.getChangeStatus(), ChangeStatusEnum.IN_CHANGE.getStatus())) {
            throw exception(IN_CHANGE_NOT_ALLOWED);
        }
        // 已存在不同编号供应商名称无法变更
        boolean exists = venderMapper.exists(new LambdaQueryWrapperX<VenderDO>().eq(VenderDO::getName, changeReqVO.getName()).ne(VenderDO::getCode, changeReqVO.getCode()));
        if (exists){
            throw exception(VENDER_NAME_EXISTS);
        }
        //查询新供应商与原供应商
        VenderInfoRespVO vender = BeanUtils.toBean(changeReqVO, VenderInfoRespVO.class);
        vender.setVer(vender.getVer() + 1);
        ChangeEffectRespVO changeEffect = getChangeEffect(vender);
        if (Objects.nonNull(changeEffect)){
            changeReqVO.setEffectRangeList(changeEffect.getEffectRangeList());
        }
        venderDO.setChangeStatus(ChangeStatusEnum.IN_CHANGE.getStatus());
        //处理供应商Ids，该字段用于权限，前端弃用，后台需要维护
        List<BaseValue> buyerList = vender.getBuyerList();
        List<Long> ids = new ArrayList<>();
        if(CollUtil.isNotEmpty(buyerList)){
            buyerList.forEach(s->{
                try{
                    ids.add(Long.parseLong(s.getValue()));
                }catch (Exception exception){
                    throw exception;
                }
            });
        }
        venderDO.setBuyerIds(ids);
        venderMapper.updateById(venderDO);

        //数据库自增 将id置空
        changeReqVO.setId(null);
        //版本加1
        changeReqVO.setVer(changeReqVO.getVer() + 1);
        changeReqVO.setChangeFlag(ChangeFlagEnum.YES.getValue());

        // 如果都是普通级直接变更
        if (!getChangeLevelFlag(vender)) {
            changeReqVO.setSubmitFlag(BooleanEnum.NO.getValue());
            Long venderId = createVenderDetail(changeReqVO, true);
            VenderInfoRespVO venderInfoRespVO = getVenderDetail(new ScmVenderDetailReq().setVenderId(venderId), true);
            //自动审核通过
            updateChangeAuditStatus(venderId, BpmProcessInstanceResultEnum.APPROVE.getResult());
            changeSuccess(venderInfoRespVO);
            return venderId;
        } else {
            changeReqVO.setSubmitFlag(BooleanEnum.YES.getValue());
            Long venderId = createVenderDetail(changeReqVO, true);
            return venderId;
        }
    }

    @Override
    public void changeSuccess(VenderInfoRespVO vender) {
        //删除原供应商
        deleteOldVender(vender.getId());
        createVenderWareHouse(vender.getId());
        if (CollUtil.isNotEmpty(vender.getEffectRangeList())) {
            vender.getEffectRangeList().forEach(s -> {
                if (EffectRangeEnum.SCM.getValue().equals(s.getEffectRangeType())) {
                    purchaseContractApi.updateConfirmFlag(ConfirmFlagEnum.NO.getValue(), s.getEffectRangeCode());
                }
                if (EffectRangeEnum.DMS.getValue().equals(s.getEffectRangeType())) {
                    shipmentApi.updateConfirmFlag(ConfirmFlagEnum.NO.getValue(), s.getEffectRangeCode());
                }
            });
        }
        purchaseContractApi.updateVenderId(vender.getId(), vender.getCode());
        shipmentApi.updateVender(vender.getId(), vender.getName(), vender.getCode());
    }

    @Override
    public void deleteVender(Long id, Boolean deleted) {
        // 校验存在
        VenderDO venderDO = validateVenderExists(id);
        // 删除
        venderMapper.deleteById(id);
        // 删除所有虚拟仓库
        warehouseApi.deleteWarehouseByVenderCode(venderDO.getCode());
//        //获取修改前的供应商银行信息列表
//        List<VenderBankaccountDO> bankAccountList = venderBankaccountService.getBankAccountListByVenderId(id);
//        //不为空进行删除 为空不做操作减少数据库访问次数
//        if (CollUtil.isNotEmpty(bankAccountList)) {
//            List<Long> idList = bankAccountList.stream().map(VenderBankaccountDO::getId).toList();
//            venderBankaccountService.batchDeleteBankAccountList(idList);
//        }
//        //获取修改前的供应商联系人列表
//        List<VenderPocDO> pocList = venderPocService.getPocListByVenderId(id);
//        //不为空进行删除 为空不做操作减少数据库访问次数
//        if (CollUtil.isNotEmpty(pocList)) {
//            List<Long> idList = pocList.stream().map(VenderPocDO::getId).toList();
//            venderPocService.batchDeletePocByIds(idList);
//        }
//        List<VenderPayment> venderPaymentList = venderPaymentMapper.selectList(VenderPayment::getVenderId, id);
//        if (CollUtil.isNotEmpty(venderPaymentList)) {
//            venderPaymentMapper.deleteBatchIds(venderPaymentList.stream().map(VenderPayment::getId).toList());
//        }
    }

    /**
     * 删除变更前的供应商
     * * @param id 编号
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteOldVender(Long id) {
        // 校验存在
        VenderDO venderDO = validateVenderExists(id);
        // 查询该id对应编号所有的数据
        List<VenderDO> venderDOs = venderMapper.selectList(new LambdaQueryWrapperX<VenderDO>()
                .eqIfPresent(VenderDO::getCode, venderDO.getCode())
                .orderByDesc(VenderDO::getId)
        );

        venderDOs.forEach(s -> {
            if (!s.getId().equals(id)) {
                deleteVender(s.getId(), true);
            }
        });
        venderDO.setChangeFlag(BooleanEnum.NO.getValue());
        venderMapper.updateById(venderDO);
    }

    private void validateVenderDuplicates(String bankAccount, String bank, String phone, String email) {
        VenderBankaccountDO venderBankaccountDO = venderBankaccountService.getBankAccountByBankAccount(bankAccount, bank);
        if (Objects.nonNull(venderBankaccountDO)) {
            throw exception(VENDER_BANK_ACCOUNT_EXISTS, bankAccount);
        }
//        venderPocService.updateVenderPoc();
    }

    @DataPermission(enable = false)
    private VenderDO validateVenderExists(Long id) {
        VenderDO venderDO = venderMapper.selectById(id);
        if (Objects.isNull(venderDO)) {
            throw exception(VENDER_NOT_EXISTS);
        }
        return venderDO;
    }

    public VenderInfoRespVO getVenderDetail(ScmVenderDetailReq req, Boolean isChange) {
        //将不同入口的参数转换为venderId
        Long venderId = Objects.nonNull(req.getProcessInstanceId()) ? processInstanceApi.selectAuditAbleIdByProcessInstanceId(req.getProcessInstanceId()) : req.getVenderId();
        venderId = Objects.nonNull(req.getVenderCode()) ? getLatestIdByCode(req.getVenderCode()) : venderId;
        if (Objects.isNull(venderId)) {
            logger.error("[供应商详情]未获取到供应商id");
            return null;
        }
        LambdaQueryWrapperX<VenderDO> venderDOLambdaQueryWrapperX = new LambdaQueryWrapperX<VenderDO>();
        venderDOLambdaQueryWrapperX.eqIfPresent(VenderDO::getId, venderId);
        venderDOLambdaQueryWrapperX.inIfPresent(VenderDO::getDeleted, new ArrayList<>(Arrays.asList(0, 1)));
        VenderDO venderDO = venderMapper.selectOne(venderDOLambdaQueryWrapperX);
        if (Objects.isNull(venderDO)) {
            return null;
        }
        VenderInfoRespVO venderInfoRespVO = VenderConvert.INSTANCE.convert(venderDO, adminUserApi.getUserDeptCache(null), venderPocService.getDefaultVenderPocMap());
        //处理应付供应商
        List<String> venderLinkCode = venderDO.getVenderLinkCode();
        Map<String, SimpleData> stringSimpleDataMap = transformCustLink(venderLinkCode);
        if (CollUtil.isNotEmpty(stringSimpleDataMap) && CollUtil.isNotEmpty(venderLinkCode)) {
            List<SimpleData> simpleDataList = new ArrayList<>();
            venderLinkCode.forEach(s -> {
                simpleDataList.add(stringSimpleDataMap.get(s));
            });
            venderInfoRespVO.setVenderlink(simpleDataList);
        }

        //处理资质
        List<Long> qualificationIds = venderDO.getQualificationIds();
        Map<Long, QualificationDO> QualificationDOMap = transQualificationLink(qualificationIds);
        if (CollUtil.isNotEmpty(QualificationDOMap) && CollUtil.isNotEmpty(qualificationIds)) {
            List<QualificationDO> qualificationDOList = new ArrayList<>();
            qualificationIds.forEach(s -> {
                qualificationDOList.add(QualificationDOMap.get(s));
            });
            venderInfoRespVO.setQualificationlink(qualificationDOList);
        }

        //获取银行信息
        List<VenderBankaccountDO> bankAccountListByVenderId = venderBankaccountService.getBankAccountListByVenderId(venderId);
        if (CollUtil.isNotEmpty(bankAccountListByVenderId)) {
            venderInfoRespVO.setBankaccountList(BeanUtils.toBean(bankAccountListByVenderId, VenderBankaccountRespVO.class));
        }
        //获取联系人列表
        List<VenderPocDO> pocListByVenderId = venderPocService.getPocListByVenderId(venderId);
        if (CollUtil.isNotEmpty(pocListByVenderId)) {
            venderInfoRespVO.setPocList(BeanUtils.toBean(pocListByVenderId, VenderPocRespVO.class));
        }
        String definitionKey;
        if (isChange){
            if (VenderTypeEnum.BUSINESS_VENDER.getValue().equals(venderDO.getVenderType())){
                definitionKey = BUSINESS_CHANGE_PROCESS_DEFINITION_KEY;
            }else {
                definitionKey =CHANGE_PROCESS_DEFINITION_KEY;
            }
        }else {
            if (VenderTypeEnum.BUSINESS_VENDER.getValue().equals(venderDO.getVenderType())){
                definitionKey = BUSINESS_PROCESS_DEFINITION_KEY;
            }else {
                definitionKey =PROCESS_DEFINITION_KEY;
            }
        }
        String bpmProcessInstanceId = bpmProcessInstanceApi.getBpmProcessInstanceId(venderId,definitionKey);
        if (StrUtil.isNotEmpty(bpmProcessInstanceId)) {
            venderInfoRespVO.setProcessInstanceId(bpmProcessInstanceId);
        }
        // 获取付款方式
        List<VenderPayment> venderPaymentList = venderPaymentMapper.selectList(VenderPayment::getVenderId, venderId);
        if (CollUtil.isNotEmpty(venderPaymentList)) {
            Map<Long, Integer> venderPaymentMap = venderPaymentList.stream().collect(Collectors.toMap(VenderPayment::getPaymentId, VenderPayment::getDefaultFlag, (o, n) -> o));
            List<PaymentItemDTO> paymentItemDTOList = paymentItemApi.getPaymentDTOList(new ArrayList<>(venderPaymentMap.keySet()));
            if (CollUtil.isNotEmpty(paymentItemDTOList)){
                paymentItemDTOList.forEach(s->{
                    s.setDefaultFlag(venderPaymentMap.get(s.getId()));
                });
                venderInfoRespVO.setPaymentList(paymentItemDTOList);
            }
        }
        //获取主营业务列表
        List<Long> businessScopes = venderInfoRespVO.getBusinessScope();
        if (CollUtil.isNotEmpty(businessScopes)) {
            //businessScope主营业务由前端传参，兼容之前为手写内容，允许不为long
                List<SimpleCategoryDTO> dotList = categoryApi.getListByIdList(businessScopes);
                venderInfoRespVO.setBusinessScopeList(dotList);
        }

        return venderInfoRespVO;
    }

    private Long getLatestIdByCode(String venderCode) {
        LambdaQueryWrapperX<VenderDO> lambdaQueryWrapperX = new LambdaQueryWrapperX<VenderDO>();
        lambdaQueryWrapperX.eq(VenderDO::getCode, venderCode);
        lambdaQueryWrapperX.eq(VenderDO::getAuditStatus, BpmProcessInstanceResultEnum.APPROVE.getResult());
        lambdaQueryWrapperX.eq(VenderDO::getChangeFlag, ChangeFlagEnum.NO.getValue());
        VenderDO venderDO = venderMapper.selectOne(lambdaQueryWrapperX);
        if(Objects.isNull(venderDO)){
            return null;
        }
        return venderDO.getId();
    }

    @Override
    public VenderInfoRespVO getVender(ScmVenderDetailReq req) {
        return getVenderDetail(req, false);
    }

    private Map<String, SimpleData> transformCustLink(List<String> venderLinkCodeList) {
        if (CollUtil.isEmpty(venderLinkCodeList)) {
            return null;
        }
        List<VenderDO> venderDOList = venderMapper.selectList(new LambdaQueryWrapperX<VenderDO>()
                .in(VenderDO::getCode, venderLinkCodeList)
                .eq(VenderDO::getChangeFlag, ChangeFlagEnum.NO.getValue()));
        if (CollUtil.isEmpty(venderDOList)) {
            return null;
        }
        List<SimpleData> simpleDataList = BeanUtils.toBean(venderDOList, SimpleData.class);
        return simpleDataList.stream().collect(Collectors.toMap(SimpleData::getCode, s -> s, (v1, v2) -> v1));
    }

    private Map<Long, QualificationDO> transQualificationLink(List<Long> qualificationIds) {
        if (CollUtil.isEmpty(qualificationIds)) {
            return null;
        }
        List<QualificationDO> qualificationDOList = qualificationMapper.selectList(new LambdaQueryWrapperX<QualificationDO>().in(QualificationDO::getId, qualificationIds));
        if (CollUtil.isEmpty(qualificationDOList)) {
            return null;
        }
        return qualificationDOList.stream().collect(Collectors.toMap(QualificationDO::getId, s -> s));
    }

    @Override
    public PageResult<VenderRespVO> getVenderPage(VenderPageReqVO pageReqVO) {
        List<Long> buyerIds = pageReqVO.getBuyerIds();
        List<String> buyerIds_str = new ArrayList<>();
        if (CollUtil.isNotEmpty(buyerIds)) {
            buyerIds_str = buyerIds.stream().map(String::valueOf).collect(Collectors.toList());
        }
        PageResult<VenderDO> venderDOPageResult = venderMapper.selectPage(pageReqVO, buyerIds_str);
        Map<Long, VenderPocDO> defaultVenderPocMap = venderPocService.getDefaultVenderPocMap();
        return VenderConvert.INSTANCE.convertToVenderRespPageResult(venderDOPageResult,  adminUserApi.getUserDeptCache(null), defaultVenderPocMap);
    }

    /**
     * 获得供应商变更资料分页
     *
     * @param pageReqVO 分页查询
     * @return 供应商资料分页
     */
    @Override
    public PageResult<VenderRespVO> getVenderChangePage(VenderPageReqVO pageReqVO) {
        List<Long> buyerIds = pageReqVO.getBuyerIds();
        List<String> buyerIds_str = new ArrayList<>();
        if (CollUtil.isNotEmpty(buyerIds)) {
            buyerIds_str = buyerIds.stream().map(String::valueOf).collect(Collectors.toList());
        }
        PageResult<VenderDO> venderDOPageResult = venderMapper.selectChangePage(pageReqVO, buyerIds_str);
        Map<Long, VenderPocDO> defaultVenderPocMap = venderPocService.getDefaultVenderPocMap();
        PageResult<VenderRespVO> venderRespVOPageResult = VenderConvert.INSTANCE.convertToVenderRespPageResult(venderDOPageResult, adminUserApi.getUserDeptCache(null), defaultVenderPocMap);
        List<VenderRespVO> list = venderRespVOPageResult.getList();
        if(CollUtil.isEmpty(list)){
            return PageResult.empty();
        }
        List<String> venderCodeList = list.stream().map(VenderRespVO::getCode).distinct().toList();
        Map<String, Map<Integer, VenderRespVO>> simpleOldVenderMap = getSimpleOldVenderMap(venderCodeList);
        venderRespVOPageResult.getList().forEach(s -> {
            Map<Integer, VenderRespVO> venderRespVOMap = simpleOldVenderMap.get(s.getCode());
            if (CollUtil.isEmpty(venderRespVOMap)){
                return;
            }
            s.setOldVender(venderRespVOMap.get(s.getVer() - 1));
        });
        return venderRespVOPageResult;
    }

    private Map<String, Map<Integer, VenderRespVO>> getSimpleOldVenderMap(List<String> venderCodeList){
        LambdaQueryWrapperX<VenderDO> venderDOLambdaQueryWrapperX = new LambdaQueryWrapperX<VenderDO>();
        venderDOLambdaQueryWrapperX.in(VenderDO::getCode, venderCodeList);
        venderDOLambdaQueryWrapperX.eq(VenderDO::getAuditStatus, BpmProcessInstanceResultEnum.APPROVE.getResult());
        venderDOLambdaQueryWrapperX.eq(VenderDO::getChangeDeleted, DeletedEnum.NO.getValue());
        venderDOLambdaQueryWrapperX.in(VenderDO::getDeleted, new ArrayList<>(Arrays.asList(0, 1)));
        List<VenderDO> oldVenderList = venderMapper.selectList(venderDOLambdaQueryWrapperX);
        if (CollUtil.isEmpty(oldVenderList)){
            return Map.of();
        }
        return oldVenderList.stream()
                .collect(Collectors.groupingBy(
                        VenderDO::getCode, // 第一层分组：根据 code
                                Collectors.toMap(
                                        VenderDO::getVer, // 第二层分组：根据 ver
                                        venderDO ->  BeanUtils.toBean(venderDO, VenderRespVO.class)) // 转换为 VenderRespVo
                        )
                );
    }
    @Override
    public ChangeEffectRespVO getChangeEffect(VenderInfoRespVO vender) {
        //查询新供应商与原供应商
        VenderInfoRespVO oldVender = getOldVender(vender);

        //初始化变更标记
        final boolean[] changeFlag = {false, false, false};
        Integer submitFlag = BooleanEnum.NO.getValue();
        ChangeEffectRespVO changeEffectRespVO = new ChangeEffectRespVO();
        //查询所有表的配置
        Map<String, FormChangeDTO> formChangeDTOList = formChangeApi.getFormChangeDTOList(List.of("scm_vender", "scm_vender_bankaccount", "scm_vender_poc", "scm_vender_payment"));
        if (CollUtil.isEmpty(formChangeDTOList)) {
            throw exception(FIELD_CHANGE_CONFIG_NOT_EXISTS);
        }

        //供应商主表
        Set<String> changeFields = new ChangeCompareUtil<VenderInfoRespVO>().transformObject(oldVender, vender);
        compareTableField(changeFields, formChangeDTOList.get("scm_vender"), changeFlag,submitFlag);

        //供应商银行信息
        List<VenderBankaccountRespVO> oldVenderBankaccountList = oldVender.getBankaccountList();
        List<VenderBankaccountRespVO> venderBankaccountList = vender.getBankaccountList();
        List<DiffRecord<VenderBankaccountRespVO>> venderBankaccountDiffRecords = DiffUtil.compareLists(oldVenderBankaccountList, venderBankaccountList);
        Tuple venderBankAccountTuple = new ChangeCompareUtil<VenderBankaccountRespVO>().transformChildList(venderBankaccountDiffRecords, VenderBankaccountRespVO.class);
        compareTableField(venderBankAccountTuple.get(1), formChangeDTOList.get("scm_vender_bankaccount"), changeFlag,submitFlag);

        //供应商联系人
        List<VenderPocRespVO> oldVenderPocList = oldVender.getPocList();
        List<VenderPocRespVO> venderPocList = vender.getPocList();
        List<DiffRecord<VenderPocRespVO>> venderPocDiffRecords = DiffUtil.compareLists(oldVenderPocList, venderPocList);
        Tuple venderPocTuple = new ChangeCompareUtil<VenderPocRespVO>().transformChildList(venderPocDiffRecords, VenderPocRespVO.class);
        compareTableField(venderPocTuple.get(1), formChangeDTOList.get("scm_vender_poc"), changeFlag,submitFlag);

        //供应商付款方式
        List<PaymentItemDTO> oldVenderPaymentList = oldVender.getPaymentList();
        List<PaymentItemDTO> venderPaymentList = vender.getPaymentList();
        List<DiffRecord<PaymentItemDTO>> venderPaymentDiffRecords = DiffUtil.compareLists(oldVenderPaymentList, venderPaymentList);
        Tuple venderPaymentTuple = new ChangeCompareUtil<PaymentItemDTO>().transformChildList(venderPaymentDiffRecords, PaymentItemDTO.class);
        compareTableField(venderPaymentTuple.get(1), formChangeDTOList.get("scm_vender_payment"), changeFlag,submitFlag);

        // 处理影响范围
        List<JsonEffectRange> effectRangeList = new ArrayList<>();
        //更新该供应商未完成的所有采购合同为未确认
        List<PurchaseContractAllDTO> purchaseContracts = new ArrayList<>();
        if (changeFlag[1]) {
            purchaseContracts = purchaseContractApi.getUnCompletedPurchaseContractByVenderCode(vender.getCode());
            if (CollUtil.isNotEmpty(purchaseContracts)) {
                purchaseContracts.forEach(s -> {
                    effectRangeList.add(new JsonEffectRange().setEffectRangeCode(s.getCode()).setEffectRangeType(EffectRangeEnum.SCM.getValue()).setConfirmFlag(ConfirmFlagEnum.NO.getValue()));
                });
            }
        }
        //更新该供应商未完成的所有出运明细为未确认
        List<ShipmentDTO> shipments = new ArrayList<>();
        if (changeFlag[2]) {
            shipments = shipmentApi.getUnShippedDTOByVenderCode(vender.getCode());
            if (CollUtil.isNotEmpty(shipments)) {
                shipments.forEach(s -> {
                    effectRangeList.add(new JsonEffectRange().setEffectRangeCode(s.getCode()).setEffectRangeType(EffectRangeEnum.DMS.getValue()).setConfirmFlag(ConfirmFlagEnum.NO.getValue()));
                });
            }
        }
        changeEffectRespVO.setEffectRangeList(effectRangeList);
        changeEffectRespVO.setSubmitFlag(submitFlag);
        return changeEffectRespVO;
    }

    public Boolean getChangeLevelFlag(VenderInfoRespVO vender) {
        //查询新供应商与原供应商
        VenderInfoRespVO oldVender = getOldVender(vender);

        //初始化变更标记
        final boolean[] changeFlag = {false};

        //查询所有表的配置
        Map<String, FormChangeDTO> formChangeDTOList = formChangeApi.getFormChangeDTOList(List.of("scm_vender", "scm_vender_bankaccount", "scm_vender_poc", "scm_vender_payment"));
        if (CollUtil.isEmpty(formChangeDTOList)) {
            throw exception(FIELD_CHANGE_CONFIG_NOT_EXISTS);
        }

        //供应商主表
        Set<String> changeFields = new ChangeCompareUtil<VenderInfoRespVO>().transformObject(oldVender, vender);
        compareChangeLevelTableField(changeFields, formChangeDTOList.get("scm_vender"), changeFlag);

        //供应商银行信息
        List<VenderBankaccountRespVO> oldVenderBankaccountList = oldVender.getBankaccountList();
        List<VenderBankaccountRespVO> venderBankaccountList = vender.getBankaccountList();
        List<DiffRecord<VenderBankaccountRespVO>> venderBankaccountDiffRecords = DiffUtil.compareLists(oldVenderBankaccountList, venderBankaccountList);
        Tuple venderBankAccountTuple = new ChangeCompareUtil<VenderBankaccountRespVO>().transformChildList(venderBankaccountDiffRecords, VenderBankaccountRespVO.class);
        compareChangeLevelTableField(venderBankAccountTuple.get(1), formChangeDTOList.get("scm_vender_bankaccount"), changeFlag);

        //供应商联系人
        List<VenderPocRespVO> oldVenderPocList = oldVender.getPocList();
        List<VenderPocRespVO> venderPocList = vender.getPocList();
        List<DiffRecord<VenderPocRespVO>> venderPocDiffRecords = DiffUtil.compareLists(oldVenderPocList, venderPocList);
        Tuple venderPocTuple = new ChangeCompareUtil<VenderPocRespVO>().transformChildList(venderPocDiffRecords, VenderPocRespVO.class);
        compareChangeLevelTableField(venderPocTuple.get(1), formChangeDTOList.get("scm_vender_poc"), changeFlag);

        //供应商付款方式
        List<PaymentItemDTO> oldVenderPaymentList = oldVender.getPaymentList();
        List<PaymentItemDTO> venderPaymentList = vender.getPaymentList();
        List<DiffRecord<PaymentItemDTO>> venderPaymentDiffRecords = DiffUtil.compareLists(oldVenderPaymentList, venderPaymentList);
        Tuple venderPaymentTuple = new ChangeCompareUtil<PaymentItemDTO>().transformChildList(venderPaymentDiffRecords, PaymentItemDTO.class);
        compareChangeLevelTableField(venderPaymentTuple.get(1), formChangeDTOList.get("scm_vender_payment"), changeFlag);

        return changeFlag[0];
    }

    private void compareTableField(Set<String> changeFieldNames, FormChangeDTO formChange, boolean[] changeFlag,Integer submitFlag) {
        if (formChange != null) {
            //影响销售的字段
            List<FormChangeItemDTO> smsItems = formChange.getChildren().stream().filter(s -> s.getEffectMainInstanceFlag() == EffectMainInstanceFlagEnum.YES.getValue() && s.getEffectRange().contains(EffectRangeEnum.SMS.getValue())).toList();
            //影响采购的字段
            List<FormChangeItemDTO> scmItems = formChange.getChildren().stream().filter(s -> s.getEffectMainInstanceFlag() == EffectMainInstanceFlagEnum.YES.getValue() && s.getEffectRange().contains(EffectRangeEnum.SCM.getValue())).toList();
            //影响出运的字段
            List<FormChangeItemDTO> dmsItems = formChange.getChildren().stream().filter(s -> s.getEffectMainInstanceFlag() == EffectMainInstanceFlagEnum.YES.getValue() && s.getEffectRange().contains(EffectRangeEnum.DMS.getValue())).toList();
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
     * 获得供应商变更资料
     *
     * @param req
     * @return 供应商资料
     */
    @Override
    public VenderInfoRespVO getVenderChange(ScmVenderDetailReq req) {
        VenderInfoRespVO vender = getVenderDetail(req, true);
        if (vender != null && vender.getVer() > 1) {
            vender.setOldVender(getOldVender(vender));
        }
        return vender;
    }

    /**
     * 根据供应商编号获得最新的供应商变更资料
     *
     * @param code
     * @return 供应商资料
     */
    @Override
    @DataPermission(enable = false)
    public VenderInfoRespVO getVenderChangeByCode(String code) {
        if (StrUtil.isEmpty(code)) {
            return null;
        }
        LambdaQueryWrapperX<VenderDO> venderDOLambdaQueryWrapperX = new LambdaQueryWrapperX<VenderDO>();
        venderDOLambdaQueryWrapperX.eq(VenderDO::getCode, code);
        venderDOLambdaQueryWrapperX.eq(VenderDO::getAuditStatus, BpmProcessInstanceResultEnum.APPROVE.getResult());
        venderDOLambdaQueryWrapperX.eq(VenderDO::getChangeFlag, ChangeFlagEnum.NO.getValue());
        VenderDO venderDO = venderMapper.selectOne(venderDOLambdaQueryWrapperX);
        VenderInfoRespVO vender = getVenderDetail(new ScmVenderDetailReq().setVenderId(venderDO.getId()), true);
        if (vender != null && vender.getVer() > 1) {
            vender.setOldVender(getOldVender(vender));
        }
        return vender;
    }

    /**
     * 获得旧供应商资料
     *
     * @param vender
     * @return 旧供应商资料
     */
    @Override
    public VenderInfoRespVO getOldVender(VenderInfoRespVO vender) {
        LambdaQueryWrapperX<VenderDO> venderDOLambdaQueryWrapperX = new LambdaQueryWrapperX<VenderDO>();
        venderDOLambdaQueryWrapperX.eq(VenderDO::getCode, vender.getCode());
        venderDOLambdaQueryWrapperX.eq(VenderDO::getVer, vender.getVer() - 1);
        venderDOLambdaQueryWrapperX.eq(VenderDO::getAuditStatus, BpmProcessInstanceResultEnum.APPROVE.getResult());
        venderDOLambdaQueryWrapperX.eq(VenderDO::getChangeDeleted, DeletedEnum.NO.getValue());
        venderDOLambdaQueryWrapperX.in(VenderDO::getDeleted, new ArrayList<>(Arrays.asList(0, 1)));
        VenderDO oldVender = venderMapper.selectOne(venderDOLambdaQueryWrapperX);
        if (oldVender == null) {
            return null;
        }
        VenderInfoRespVO oldVenderInfo = getVenderDetail(new ScmVenderDetailReq().setVenderId(oldVender.getId()), true);
        return oldVenderInfo;
    }

    @Override
    public void approveTask(Long userId, BpmTaskApproveReqDTO reqVO) {
        processInstanceApi.approveTask(userId, BeanUtils.toBean(reqVO, BpmTaskApproveReqDTO.class));
    }

    @Override
    public void rejectTask(Long userId, BpmTaskRejectReqDTO reqVO) {
        processInstanceApi.rejectTask(userId, BeanUtils.toBean(reqVO, BpmTaskRejectReqDTO.class));
    }

    @Override
    public void updateVenderAuditStatus(Long venderId, Integer auditStatus) {
        venderMapper.updateById(VenderDO.builder().id(venderId).auditStatus(auditStatus).build());
    }

    @Override
    public PageResult<SimpleVenderRespDTO> getSimpleVender(VenderPageReqVO pageParam) {
//        String venderNameRegex = null;
//        if (StrUtil.isNotEmpty(pageParam.getName())) {
//            venderNameRegex = luceneApi.splitWord(pageParam.getName());
//        }
        Integer pageNo = pageParam.getPageNo();
        Integer pageSize = pageParam.getPageSize();
        int skip = 0;
        if (pageSize > 0) {
            skip = (pageNo - 1) * pageSize;
        }
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        LocalDateTime[] createTime = pageParam.getCreateTime();
        if (createTime != null && createTime.length > 1) {
            startTime = pageParam.getCreateTime()[0];
            endTime = pageParam.getCreateTime()[1];
        }
        List<SimpleVenderRespDTO> simpleVenderRespDTOList = venderMapper.selectSimpleList(
                startTime, endTime, pageParam.getCode(), pageParam.getName(), pageParam.getPurchaseUserId(),pageParam.getPurchaseUserDeptId(), pageParam.getName(), skip,
                pageParam.getPageSize(), pageParam.getRateFlag(), pageParam.getVenderType(), pageParam.getStageTypeFlag(),pageParam.getAdministrationVenderType(),pageParam.getSkuQuoteFlag());
        if (CollUtil.isEmpty(simpleVenderRespDTOList)) {
            logger.warn("[精简客供应商列表]未查询到供应商列表");
            return null;
        }
        dealSimpleVenderLink(simpleVenderRespDTOList);
        transformBankAccount(simpleVenderRespDTOList);
        // 付款方式
        dealVenderPayment(simpleVenderRespDTOList);
        //联系人
        List<Long> idList = simpleVenderRespDTOList.stream().map(SimpleVenderRespDTO::getId).distinct().toList();
        Map<Long, List<VenderPocDTO>> venderPocMap = venderPocService.getVenderPocMap(idList);
        if(CollUtil.isNotEmpty(venderPocMap)){
            simpleVenderRespDTOList.forEach(s->{
               s.setPocList(venderPocMap.get(s.getId()))  ;
            });
        }
        //地址
        simpleVenderRespDTOList.forEach(s->{
            Integer deliveryAreaId = s.getDeliveryAreaId();
            if (Objects.nonNull(deliveryAreaId)) {
                s.setDeliveryAreaIdList(AreaUtils.formatList(deliveryAreaId));
                s.setDeliveryAreaName(AreaUtils.format(deliveryAreaId, CommonDict.SLASH_CHAR));
            }
            List<JsonVenderTax> taxMsg = s.getTaxMsg();
            if (CollUtil.isNotEmpty(taxMsg)){
                taxMsg.stream().filter(x->BooleanEnum.YES.getValue().equals(x.getDefaultFlag())).findFirst().ifPresent(x->{
                    s.setCurrency(x.getCurrency());
                    s.setTaxType(x.getTaxType());
                    s.setTaxRate(x.getTaxRate());
                });
            }
        });

        Long count = venderMapper.getCount(startTime, endTime, pageParam.getCode(), pageParam.getName(),pageParam.getPurchaseUserId(),pageParam.getPurchaseUserDeptId(),
                pageParam.getName(), pageParam.getRateFlag(), pageParam.getVenderType(), pageParam.getStageTypeFlag(),pageParam.getAdministrationVenderType(),pageParam.getSkuQuoteFlag());
        return new PageResult<>(simpleVenderRespDTOList, count);
    }

    @Override
    public PageResult<SimpleVenderRespDTO> getSimpleVenderByBuyerUserId(VenderSimpleReqVO reqVO) {
        List<VenderDO> venderDOList = venderMapper.selectList(
                new LambdaQueryWrapperX<VenderDO>()
                        .likeIfPresent(VenderDO::getName, reqVO.getVenderName())
                        .eqIfPresent(VenderDO::getVenderType,reqVO.getVenderType())
                        .eq(VenderDO::getAuditStatus,BpmProcessInstanceResultEnum.APPROVE.getResult())
                        .apply("FIND_IN_SET({0},buyer_ids)", reqVO.getBuyerId()));
        Long size = (long) venderDOList.size();
        venderDOList = venderDOList.stream().skip((long) (reqVO.getPageNo() - 1) * reqVO.getPageSize()).limit(reqVO.getPageSize()).toList();
        List<SimpleVenderRespDTO> venderRespDTOS = BeanUtils.toBean(venderDOList, SimpleVenderRespDTO.class);
        return new PageResult<>(venderRespDTOS, size);
    }

    private void dealVenderPayment(List<SimpleVenderRespDTO> simpleVenderRespDTOList) {
        if (CollUtil.isEmpty(simpleVenderRespDTOList)) {
            return;
        }
        List<Long> venderIdList = simpleVenderRespDTOList.stream().map(SimpleVenderRespDTO::getId).toList();
        List<VenderPayment> venderPaymentList = venderPaymentMapper.selectList(new LambdaQueryWrapperX<VenderPayment>().in(VenderPayment::getVenderId, venderIdList));
        if (CollUtil.isEmpty(venderPaymentList)) {
            return;
        }
        Map<Long, List<VenderPayment>> venderPaymentMap = venderPaymentList.stream().collect(Collectors.groupingBy(VenderPayment::getVenderId, Collectors.toList()));
        Map<Long, PaymentItemDTO> paymentDTOMap = paymentItemApi.getPaymentDTOMap();
        simpleVenderRespDTOList.forEach(s -> {
            Long venderId = s.getId();
            List<VenderPayment> venderPayments = venderPaymentMap.get(venderId);
            if (CollUtil.isEmpty(venderPayments)) {
                return;
            }
            List<PaymentItemDTO> paymentItemDTOList = venderPayments.stream().map(venderPayment->paymentDTOMap.get(venderPayment.getPaymentId())).filter(Objects::nonNull).toList();
            if (CollUtil.isEmpty(paymentItemDTOList)) {
                return;
            }
            Map<Long, PaymentItemDTO> paymentItemDTOMap = paymentItemDTOList.stream().collect(Collectors.toMap(PaymentItemDTO::getId, item -> item));
            List<VenderPaymentDTO> paymentDTOList = BeanUtils.toBean(venderPayments, VenderPaymentDTO.class);
            paymentDTOList.forEach(p -> {
                PaymentItemDTO paymentItemDTO = paymentItemDTOMap.get(p.getPaymentId());
                if (Objects.nonNull(paymentItemDTO)) {
                    p.setPaymentName(paymentItemDTO.getName());
                }
            });
            s.setPaymentList(paymentDTOList);
        });
    }

    @Override
    public List<VenderPaymentDTO> getVenderPaymentList(Long venderId){
        List<VenderPayment> venderPaymentList = venderPaymentMapper.selectList(new LambdaQueryWrapperX<VenderPayment>().eq(VenderPayment::getVenderId, venderId));
        if (CollUtil.isEmpty(venderPaymentList)){
            return List.of();
        }
        List<Long> paymentIdList = venderPaymentList.stream().map(VenderPayment::getPaymentId).toList();
        List<PaymentItemDTO> paymentItemDTOList = paymentItemApi.getPaymentDTOList(paymentIdList);
        Map<Long, PaymentItemDTO> paymentItemDTOMap = paymentItemDTOList.stream().collect(Collectors.toMap(PaymentItemDTO::getId, item -> item));
        List<VenderPaymentDTO> paymentDTOList = BeanUtils.toBean(venderPaymentList, VenderPaymentDTO.class);
        paymentDTOList.forEach(p -> {
            PaymentItemDTO paymentItemDTO = paymentItemDTOMap.get(p.getPaymentId());
            if (Objects.nonNull(paymentItemDTO)) {
                p.setPaymentName(paymentItemDTO.getName());
            }
        });
        return paymentDTOList;
    }

    @Override
    public Map<String, SimpleVenderRespDTO> getSimpleVenderDTOMapByCodes(Collection<String> venderCodeList,Integer containInnerFlag) {
        if (CollUtil.isEmpty(venderCodeList)){
            return Map.of();
        }
        LambdaQueryWrapperX<VenderDO> queryWrapperX = new LambdaQueryWrapperX<VenderDO>().in(VenderDO::getCode, venderCodeList);
        List<VenderDO> venderDOList = venderMapper.selectList(queryWrapperX);
        if(CollUtil.isEmpty(venderDOList)){
            return null;
        }
        if (BooleanEnum.NO.getValue().equals(containInnerFlag)){
            queryWrapperX.ne(VenderDO::getInternalFlag, BooleanEnum.YES.getValue());
            venderDOList = venderDOList.stream().filter(s->!BooleanEnum.YES.getValue().equals(s.getInternalFlag())).collect(Collectors.toList());
        }
        if(CollUtil.isEmpty(venderDOList)){
            return null;
        }
        List<SimpleVenderRespDTO> dtoList = BeanUtils.toBean(venderDOList, SimpleVenderRespDTO.class);
        List<Long> buyerIdList = dtoList.stream().flatMap(doObj -> doObj.getBuyerIds().stream()).distinct().toList();
        Map<Long, AdminUserRespDTO> userMap = adminUserApi.getUserMap(buyerIdList);
        List<Long> deptIdList = userMap.values().stream().map(AdminUserRespDTO::getDeptId).distinct().toList();
        Map<Long, DeptRespDTO> deptMap = deptApi.getDeptMap(deptIdList);
        if(CollUtil.isNotEmpty(userMap)){
            dtoList.forEach(s->{
                if(CollUtil.isEmpty(s.getBuyerIds())){
                    return;
                }
                List<UserDept> userDeptList = new ArrayList<>();

                s.getBuyerIds().forEach(c->{
                    AdminUserRespDTO adminUserRespDTO = userMap.get(c);
                    Optional<BaseValue> first = s.getBuyerList().stream().filter(b -> Objects.equals(b.getValue(), c.toString())).findFirst();
                    Integer defaultFlag = 0;
                    if(first.isPresent()){
                        defaultFlag =first.get().getDefaultFlag();
                    }
                    if(Objects.nonNull(adminUserRespDTO)){
                        UserDept user = new UserDept()
                                .setDefaultFlag(defaultFlag)
                                .setUserId(adminUserRespDTO.getId())
                                .setUserCode(adminUserRespDTO.getCode())
                                .setNickname(adminUserRespDTO.getNickname())
                                .setDeptId(adminUserRespDTO.getDeptId()) ;
                        if(CollUtil.isNotEmpty(deptMap)){
                            DeptRespDTO deptRespDTO = deptMap.get(adminUserRespDTO.getDeptId());
                            if(Objects.nonNull(deptRespDTO)){
                                user.setDeptName(deptRespDTO.getName());
                            }
                        }
                        userDeptList.add(user);
                    }

                });
                s.setBuyers(userDeptList);
            });
        }

        return dtoList.stream().collect(Collectors.toMap(SimpleVenderRespDTO::getCode,s->s,(s1,s2)->s2));
    }

    @Override
    @DataPermission(enable = false)
    public SimpleCompanyDTO getCompanyByVenderCode(String venderCode) {
        VenderDO venderDO = venderMapper.selectOne(new LambdaQueryWrapperX<VenderDO>().eq(VenderDO::getCode, venderCode).eq(VenderDO::getChangeFlag, BooleanEnum.NO.getValue()));
        if (Objects.isNull(venderDO)){
            throw exception(VENDER_NOT_EXISTS);
        }
        if (!BooleanEnum.YES.getValue().equals(venderDO.getInternalFlag())){
           return null;
        }
        return companyApi.getCompanyDTO(venderDO.getInternalCompanyId());
    }

    @Override
    public boolean checkInnerVender(String venderCode) {
        if (StrUtil.isEmpty(venderCode)){
            return false;
        }
        VenderDO venderDO = venderMapper.selectOne(new LambdaQueryWrapperX<VenderDO>().select(VenderDO::getInternalFlag, VenderDO::getInternalCompanyId).eq(VenderDO::getCode, venderCode).eq(VenderDO::getChangeFlag, BooleanEnum.NO.getValue()));
        if (Objects.isNull(venderDO)){
            return false;
        }
        Integer internalFlag = venderDO.getInternalFlag();
        Long internalCompanyId = venderDO.getInternalCompanyId();
        return BooleanEnum.YES.getValue().equals(internalFlag) || Objects.nonNull(internalCompanyId);
    }

    @Override
    public Tuple getVenderNameAndTelByCode(String venderCode) {
        if (StrUtil.isEmpty(venderCode)){
            return new Tuple(null,null,null);
        }
        VenderDO venderDO = venderMapper.selectOne(new LambdaQueryWrapperX<VenderDO>().select(VenderDO::getId,VenderDO::getName, VenderDO::getPhone,VenderDO::getCompanyAddress).eq(VenderDO::getCode, venderCode).eq(VenderDO::getChangeFlag, BooleanEnum.NO.getValue()));
        if (Objects.isNull(venderDO)){
            return new Tuple(null,null,null);
        }
        VenderBankaccountDO bankAccount = venderBankaccountService.getBankAccountByVenderCode(venderDO.getId());
        if (Objects.isNull(bankAccount)){
            return new Tuple(venderDO.getName(),venderDO.getPhone(),venderDO.getCompanyAddress(),null,null);
        }
        return new Tuple(venderDO.getName(),venderDO.getPhone(),venderDO.getCompanyAddress(),bankAccount.getBank(),bankAccount.getBankAccount());
    }

    @Override
    public Set<String> getAvailableVenderIdSetByVenderIdList(List<String> venderCodeList) {
       if (CollUtil.isEmpty(venderCodeList)){
           return Set.of();
       }
        List<VenderDO> venderDOS = venderMapper.selectList(new LambdaQueryWrapperX<VenderDO>().select(VenderDO::getCode).in(VenderDO::getCode, venderCodeList).eq(VenderDO::getAuditStatus, BpmProcessInstanceResultEnum.APPROVE.getResult()).eq(VenderDO::getStageType, StageTypeEnum.FORMAL.getValue()));
        if (CollUtil.isEmpty(venderDOS)){
            return Set.of();
        }
        return venderDOS.stream().map(VenderDO::getCode).collect(Collectors.toSet());
    }

    @Override
    public Map<Long,PaymentItemDTO> getPaymentItemDTOListByVenderCode(String venderCode) {
        if (StrUtil.isEmpty(venderCode)){
            return Map.of();
        }
        List<VenderDO> venderDOS = venderMapper.selectList(new LambdaQueryWrapperX<VenderDO>().select(VenderDO::getId).eq(VenderDO::getCode, venderCode).eq(VenderDO::getChangeFlag, BooleanEnum.NO.getValue()));
        if (CollUtil.isEmpty(venderDOS)){
            return Map.of();
        }
        Long venderId = venderDOS.stream().findFirst().get().getId();
        List<VenderPayment> venderPayments = venderPaymentMapper.selectList(new LambdaQueryWrapperX<VenderPayment>().eq(VenderPayment::getVenderId, venderId).eq(VenderPayment::getDefaultFlag, BooleanEnum.YES.getValue()));
        if (CollUtil.isEmpty(venderPayments)){
            return Map.of();
        }
        VenderPayment venderPayment = venderPayments.stream().findFirst().get();
        List<PaymentItemDTO> paymentDTOList = paymentItemApi.getPaymentDTOList(List.of(venderPayment.getPaymentId()));
        if (CollUtil.isEmpty(paymentDTOList)){
            return Map.of();
        }
        return Map.of(venderPayment.getPaymentId(),paymentDTOList.stream().findFirst().get());
    }

    @Override
    public void validateVenderAvailable(List<String> codeList) {
        if (CollUtil.isEmpty(codeList)){
            return;
        }
        List<VenderDO> venderDOS = venderMapper.selectList(new LambdaQueryWrapperX<VenderDO>().select(VenderDO::getCode).in(VenderDO::getCode, codeList.stream().distinct().toList()).eq(VenderDO::getVenderType, VenderTypeEnum.BUSINESS_VENDER.getValue()).eq(VenderDO::getAuditStatus, BpmProcessInstanceResultEnum.APPROVE.getResult()).eq(VenderDO::getStageType, StageTypeEnum.FORMAL.getValue()));
        if (CollUtil.isEmpty(venderDOS)){
            throw exception(VENDER_NOT_AVAILABLE);
        }
        Set<String> baseVenderCodeSet = venderDOS.stream().map(VenderDO::getCode).collect(Collectors.toSet());
        List<String> notAvailableList= CollUtil.subtractToList(codeList, baseVenderCodeSet);
        if (CollUtil.isNotEmpty(notAvailableList)){
            throw exception(VENDER_NOT_AVAILABLE);
        }
    }

    @Override
    public String getBankPocByVenderCode(String venderCode) {
        if (StrUtil.isEmpty(venderCode)){
            return null;
        }
        List<VenderDO> venderDOS = venderMapper.selectList(new LambdaQueryWrapperX<VenderDO>().select(VenderDO::getId).eq(VenderDO::getCode, venderCode).eq(VenderDO::getChangeFlag, BooleanEnum.NO.getValue()));
        if (CollUtil.isEmpty(venderDOS)){
            return null;
        }
        Long venderId = venderDOS.stream().findFirst().get().getId();
        VenderBankaccountDO bankAccount = venderBankaccountService.getBankAccountByVenderCode(venderId);
        if (Objects.isNull(bankAccount)){
            return null;
        }
        return bankAccount.getBankPoc();
    }

    @Override
    public void managerDeleteVender(Long id) {
        VenderDO venderDO = validateVenderExists(id);
        boolean exists = purchaseContractService.validateVenderExists(venderDO.getCode());
        if (exists){
            throw exception(PURCHASE_CONTRACT_IS_EXISTS);
        }
        boolean paymentExists = paymentAppApi.validateCustExists(venderDO.getCode());
        if (paymentExists){
            throw exception(PAYMENT_APP_IS_EXISTS);
        }
        boolean freeShareExists = feeShareApi.validateCustExists(venderDO.getCode());
        if (freeShareExists){
            throw exception(FREE_SHARE_IS_EXISTS);
        }
        deleteVender(id,true);
    }

    @Override
    public void rejectChangeVender(Long id) {
        VenderDO venderDO = validateVenderExists(id);
        VenderRespVO oldVender = venderDO.getOldVender();
        venderDO.setAuditStatus(BpmProcessInstanceResultEnum.UNSUBMITTED.getResult());
        if (Objects.isNull(oldVender)){
            return;
        }
        oldVender.setChangeStatus(ChangeStatusEnum.IN_CHANGE.getStatus());
    }

    private void dealSimpleVenderLink(List<SimpleVenderRespDTO> simpleVenderRespDTOList) {
        if (CollUtil.isEmpty(simpleVenderRespDTOList)) {
            return;
        }
        List<String> venderCodeList = simpleVenderRespDTOList.stream().map(SimpleVenderRespDTO::getCode).distinct().toList();
        List<VenderDO> venderDOList = venderMapper.selectList(new LambdaQueryWrapperX<VenderDO>().in(VenderDO::getCode, venderCodeList));
        if (CollUtil.isEmpty(venderDOList)) {
            return;
        }
        Map<String, List<String>> venderCodeLinkMap = venderDOList.stream().collect(Collectors.toMap(VenderDO::getCode, VenderDO::getVenderLinkCode, (v1, v2) -> v2));
        simpleVenderRespDTOList.forEach(s -> {
            List<String> venderLinkCodeList = venderCodeLinkMap.get(s.getCode());
            if (CollUtil.isEmpty(venderLinkCodeList)) {
                return;
            }
            Map<String, SimpleData> stringSimpleDataMap = transformCustLink(venderLinkCodeList);
            if (CollUtil.isNotEmpty(stringSimpleDataMap)) {
                List<SimpleData> simpleDataList = new ArrayList<>();
                venderLinkCodeList.forEach(venderLinkCode -> {
                    simpleDataList.add(stringSimpleDataMap.get(venderLinkCode));
                });
                s.setVenderLink(simpleDataList);
            }
        });
    }

    @Override
    public List<SimpleVenderRespDTO> getSimpleVenderRespDTO(String venderName) {
        String venderNameRegex = null;
        List<SimpleVenderRespDTO> simpleVenderRespDTOList = venderMapper.queryMatchVenderName(venderNameRegex, venderName);
        if (CollUtil.isEmpty(simpleVenderRespDTOList)) {
            return Collections.emptyList();
        }
        transformBankAccount(simpleVenderRespDTOList);
        return simpleVenderRespDTOList;
    }

    @Override
    public String getProcessDefinitionKey() {
        return PROCESS_DEFINITION_KEY;
    }

    @Override
    public String getBusinessProcessDefinitionKey() {
        return BUSINESS_PROCESS_DEFINITION_KEY;
    }

    @Override
    public void updateAuditStatus(Long auditableId, Integer auditStatus) {
        venderMapper.updateById(VenderDO.builder().id(auditableId).auditStatus(auditStatus).build());
    }

    @Override
    public void updateChangeAuditStatus(Long auditableId, Integer auditStatus) {
        VenderDO venderDO = validateVenderExists(auditableId);
        venderDO.getSourceCode();
        venderDO.setAuditStatus(auditStatus);
        if (auditStatus == BpmProcessInstanceResultEnum.APPROVE.getResult()) {
            venderDO.setChangeFlag(ChangeFlagEnum.YES.getValue());
            venderDO.setChangeStatus(ChangeStatusEnum.CHANGED.getStatus());
        } else if (auditStatus == BpmProcessInstanceResultEnum.CANCEL.getResult() || auditStatus == BpmProcessInstanceResultEnum.BACK.getResult()) {
            venderDO.setChangeFlag(ChangeFlagEnum.YES.getValue());
            venderDO.setChangeStatus(ChangeStatusEnum.NOT_CHANGED.getStatus());
            VenderDO oldVender = venderMapper.selectOne(new LambdaQueryWrapperX<VenderDO>().eq(VenderDO::getCode, venderDO.getCode()).eq(VenderDO::getVer, venderDO.getVer() - 1));
            oldVender.setChangeFlag(ChangeFlagEnum.NO.getValue());
            oldVender.setChangeStatus(ChangeStatusEnum.NOT_CHANGED.getStatus());
            venderMapper.updateById(oldVender);
        }
        venderMapper.updateById(venderDO);
        String sourceCode = venderDO.getSourceCode();
    }

    @Override
    @DataPermission(enable = false)
    public List<SimpleVenderRespDTO> getSimpleVenderRespDTOList(List<Long> idList) {
        if (CollUtil.isEmpty(idList)) {
            return Collections.emptyList();
        }
        return venderMapper.batchSimpleVenderRespDTOList(idList);
    }

    @Override
    @DataPermission(enable = false)
    public List<SimpleVenderRespDTO> getSimpleVenderRespDTOListByCodeList(List<String> codes) {
        if (CollUtil.isEmpty(codes)) {
            return Collections.emptyList();
        }
        List<SimpleVenderRespDTO> simpleVenderRespDTOS = venderMapper.batchSimpleVenderRespDTOListByCodeList(codes);
        if (CollUtil.isEmpty(simpleVenderRespDTOS)) {
            return List.of();
        }
        dealVenderPayment(simpleVenderRespDTOS);
        return simpleVenderRespDTOS;
    }

    @Override
    @DataPermission(enable = false)
    public VenderInfoRespVO getVenderByCode(String code) {
        if (StrUtil.isEmpty(code)){
            return null;
        }
        VenderDO venderDO = venderMapper.selectOne(new LambdaQueryWrapperX<VenderDO>().eq(VenderDO::getCode, code).eq(VenderDO::getChangeFlag, BooleanEnum.NO.getValue()));
        VenderInfoRespVO venderInfoRespVO = VenderConvert.INSTANCE.convert(venderDO, adminUserApi.getUserDeptCache(null), venderPocService.getDefaultVenderPocMap());
        return venderInfoRespVO;
    }

    @Override
    @DataPermission(enable = false)
    public Map<String, String> getVenderNameCache(String code) {
        LambdaQueryWrapper<VenderDO> queryWrapper = new LambdaQueryWrapperX<VenderDO>().select(VenderDO::getName, VenderDO::getCode);
        if (StrUtil.isNotEmpty(code)) {
            queryWrapper.eq(VenderDO::getCode, code);
        }
        List<VenderDO> venderDOList = venderMapper.selectList(queryWrapper);
        Map<String, String> venderNameCache = new HashMap<>();
        if (CollUtil.isEmpty(venderDOList)) {
            logger.warn("[供应商名称缓存]未查询到供应商列表");
            return venderNameCache;
        }
        venderDOList.forEach(s -> venderNameCache.put(s.getCode(), s.getName()));
        return venderNameCache;
    }

    @Override
    @DataPermission(enable = false)
    public Map<Long, String> getVenderNameCache(List<Long> idList) {
        LambdaQueryWrapper<VenderDO> queryWrapper = new LambdaQueryWrapperX<VenderDO>().select(VenderDO::getName, VenderDO::getId);
        if (Objects.nonNull(idList)) {
            queryWrapper.in(VenderDO::getId, idList);
        }
        List<VenderDO> venderDOList = venderMapper.selectList(queryWrapper);
        Map<Long, String> venderNameCache = new HashMap<>();
        if (CollUtil.isEmpty(venderDOList)) {
            logger.warn("[供应商名称缓存]未查询到供应商列表");
            return venderNameCache;
        }
        venderDOList.forEach(s -> venderNameCache.put(s.getId(), s.getName()));
        return venderNameCache;
    }

    @Override
    public ServiceException convertVender(VenderInfoSaveReqVO updateReqVO) {
        //判断供应商名称唯一
        VenderDO existingVender = venderMapper.selectOne(
                new LambdaQueryWrapperX<VenderDO>()
                        .eq(VenderDO::getName, updateReqVO.getName())
                        .eq(VenderDO::getStageType, StageTypeEnum.FORMAL.getValue())
        );
        if (existingVender != null) {
            throw exception(VENDER_NAME_EXISTS);
        }

        try{
            updateReqVO.setStageType(StageTypeEnum.FORMAL.getValue());
            updateReqVO.setConvertFlag(BooleanEnum.YES.getValue());
            updateReqVO.setConvertTime(LocalDateTime.now());
            updateVender(updateReqVO);
        }catch (Exception e){
            logger.error(e.getMessage());
            throw exception(CONVERT_VENDER_FAIL);
        }
        return null;
    }

    @Override
    public void enableVender(Long venderId) {
        VenderDO venderDO = venderMapper.selectById(venderId);
        if (Objects.isNull(venderDO)) {
            throw exception(VENDER_NOT_EXISTS);
        }
        Integer enableFlag = venderDO.getEnableFlag();
        if (Integer.valueOf(EnableStatusEnum.TURN_ON.getStatus()).equals(enableFlag)) {
            throw exception(VENDER_TRUN_ON);
        }
        venderDO.setEnableFlag(Integer.valueOf(EnableStatusEnum.TURN_ON.getStatus()));
        venderMapper.updateById(venderDO);
    }

    @Override
    public void disableVender(Long venderId) {
        VenderDO venderDO = venderMapper.selectById(venderId);
        if (Objects.isNull(venderDO)) {
            throw exception(VENDER_NOT_EXISTS);
        }
        Integer enableFlag = venderDO.getEnableFlag();
        if (Integer.valueOf(EnableStatusEnum.TURN_OFF.getStatus()).equals(enableFlag)) {
            throw exception(VENDER_TRUN_OFF);
        }
        venderDO.setEnableFlag(Integer.valueOf(EnableStatusEnum.TURN_OFF.getStatus()));
        venderMapper.updateById(venderDO);
    }

    @Override
    @DataPermission(enable = false)
    public Map<String, List<UserDept>> getVenderManagerByCodeList(List<String> codeList) {
        List<VenderDO> venderDOList = venderMapper.selectList(new LambdaQueryWrapperX<VenderDO>()
                .in(VenderDO::getCode, codeList)
                .eq(VenderDO::getChangeFlag,BooleanEnum.NO.getValue()));
        if (CollUtil.isEmpty(venderDOList)) {
            throw exception(VENDER_NOT_EXISTS);
        }
        Set<Long> buyerIdList = venderDOList.stream()
                .flatMap(s -> s.getBuyerIds().stream())
                .collect(Collectors.toSet());
        if (CollUtil.isEmpty(buyerIdList)) {
            logger.warn("[获取供应商业务员]供应商未查询到业务员venderList-{}", codeList);
            return null;
        }
        Map<Long, UserDept> userDeptCache = adminUserApi.getUserDeptCache(null);
        if (CollUtil.isEmpty(userDeptCache)) {
            logger.warn("[获取供应商业务员]查询到系统用户为空");
            return null;
        }
        return venderDOList.stream().collect(Collectors.toMap(VenderDO::getCode, s -> {
            List<Long> buyerIds = s.getBuyerIds();
            if (CollUtil.isEmpty(buyerIds)) {
                logger.warn("[获取供应商业务员]供应商未查询到业务员venderCode-{}", s.getCode());
                return Collections.emptyList();
            }
            return buyerIds.stream().map(userDeptCache::get).filter(Objects::nonNull).toList();
        }));
    }

    @Override
    @DataPermission(enable = false)
    public List<VenderDO> getSimpleListByCodeAndName(String vender) {
        List<VenderDO> venderDOList = venderMapper.selectList(
                new LambdaQueryWrapperX<VenderDO>().likeIfPresent(VenderDO::getCode, vender)
                        .or().likeIfPresent(VenderDO::getName, vender)
        );
        return venderDOList;
    }

    @Override
    public SimpleVenderResp getSimpleVenderRespByBankPoc(String bankPoc) {
        Long venderId = venderBankaccountService.geVenderIdByBankPoc(bankPoc);
        if (Objects.isNull(venderId)) {
            return null;
        }
        VenderDO venderDO = venderMapper.selectById(venderId);
        if (Objects.isNull(venderDO)) {
            throw exception(INVALID_BANK_ACCOUNT);
        }
        SimpleVenderResp simpleVenderResp = BeanUtils.toBean(venderDO, SimpleVenderResp.class);
        List<Long> managerIds = venderDO.getBuyerIds();
        if (CollUtil.isNotEmpty(managerIds)) {
            Map<Long, UserDept> userDeptListCache = adminUserApi.getUserDeptListCache(managerIds);
            List<UserDept> userDeptList = new ArrayList<>(userDeptListCache.values());
            simpleVenderResp.setManagerList(userDeptList);
        }
        return simpleVenderResp;
    }

    @Override
    @DataPermission(enable = false)
    public Map<String, SimpleVenderResp> getSimpleVenderMapByCodes(Collection<String> codeList) {
        if (CollUtil.isEmpty(codeList)) {
            return null;
        }
        List<VenderDO> venderDOList = venderMapper.selectList(new LambdaQueryWrapperX<VenderDO>().in(VenderDO::getCode, codeList).eq(VenderDO::getChangeFlag, ChangeFlagEnum.NO.getValue()));
        if (CollUtil.isEmpty(venderDOList)) {
            return null;
        }
        return venderDOList.stream().collect(Collectors.toMap(VenderDO::getCode, s -> new SimpleVenderResp().setCode(s.getCode()).setId(s.getId()).setName(s.getName()).setBuyerIds(s.getBuyerIds()), (s1, s2) -> s1));
    }

    @Override
    @DataPermission(enable = false)
    public List<VenderDO> getVenderByCodeList(Collection<String> codeList) {
        return venderMapper.selectList(VenderDO::getCode, codeList).stream().filter(s-> Objects.equals(s.getChangeFlag(), BooleanEnum.NO.getValue())).toList();

    }

    @Override
    @DataPermission(enable = false)
    public List<String> getCodeListByName(String venderName) {
        if (Objects.isNull(venderName)) {
            return null;
        }
        List<VenderDO> venderDOList = venderMapper.selectList(new LambdaQueryWrapperX<VenderDO>().like(VenderDO::getName, venderName));
        if (venderDOList == null) {
            return null;
        }
        return venderDOList.stream().map(VenderDO::getCode).distinct().toList();
    }

    @Override
    public void submitTask(Long venderId, Long userId) {
        VenderDO venderDO = validateVenderExists(venderId);
        Integer venderType = venderDO.getVenderType();
        if (VenderTypeEnum.BUSINESS_VENDER.getValue().equals(venderType)){
            bpmProcessInstanceApi.createProcessInstance(userId, BUSINESS_PROCESS_DEFINITION_KEY, venderId);
        }else {
            bpmProcessInstanceApi.createProcessInstance(userId, PROCESS_DEFINITION_KEY, venderId);
        }
        updateVenderAuditStatus(venderId, BpmProcessInstanceResultEnum.PROCESS.getResult());
    }

    private void transformBankAccount(List<SimpleVenderRespDTO> simpleVenderRespDTOList) {
        List<Long> venderIdList = simpleVenderRespDTOList.stream().map(SimpleVenderRespDTO::getId).toList();
        List<VenderBankaccountDO> bankAccountListByVenderCodeList = venderBankaccountService.getBankAccountListByVenderCodeList(venderIdList);
        if (CollUtil.isNotEmpty(bankAccountListByVenderCodeList)) {
            Map<Long, List<VenderBankaccountDO>> venderBankaccountMap = bankAccountListByVenderCodeList.stream().collect(Collectors.groupingBy(VenderBankaccountDO::getVenderId));
            simpleVenderRespDTOList.forEach(s -> {
                List<VenderBankaccountDO> venderBankaccountDOList = venderBankaccountMap.get(s.getId());
                if (CollUtil.isNotEmpty(venderBankaccountDOList)) {
                    s.setBankAccountList(VenderBankaccountConvert.INSTANCE.convertVenderBankAccountDTO(venderBankaccountDOList));
                }
            });
        }
    }

    @Override
    public void approveChangeTask(Long userId, BpmTaskApproveReqDTO reqVO) {
        bpmProcessInstanceApi.approveTask(userId, BeanUtils.toBean(reqVO, BpmTaskApproveReqDTO.class));
    }

    @Override
    public void rejectChangeTask(Long userId, BpmTaskRejectReqDTO reqVO) {
        bpmProcessInstanceApi.rejectTask(userId, BeanUtils.toBean(reqVO, BpmTaskRejectReqDTO.class));
    }

    @Override
    public void submitChangeTask(Long venderId, Long userId) {
        VenderDO venderDO = validateVenderExists(venderId);
        Integer venderType = venderDO.getVenderType();
        if (VenderTypeEnum.BUSINESS_VENDER.getValue().equals(venderType)){
            bpmProcessInstanceApi.createProcessInstance(userId, BUSINESS_CHANGE_PROCESS_DEFINITION_KEY, venderId);
        }else {
            bpmProcessInstanceApi.createProcessInstance(userId, CHANGE_PROCESS_DEFINITION_KEY, venderId);
        }
        updateVenderAuditStatus(venderId, BpmProcessInstanceResultEnum.PROCESS.getResult());
    }

    @Override
    public String getChangeProcessDefinitionKey() {
        return CHANGE_PROCESS_DEFINITION_KEY;
    }

    @Override
    public String getBusinessChangeProcessDefinitionKey() {
        return BUSINESS_CHANGE_PROCESS_DEFINITION_KEY;
    }

    @Override
    public void deleteChangeVender(Long id) {
        venderMapper.updateById(VenderDO.builder()
                .id(id)
                .changeDeleted(DeletedEnum.YES.getValue())
                .build());
        deleteVender(id, false);
    }

    @Override
    public void createVenderWareHouse(Long venderId) {
        VenderInfoRespVO vender = getVender(new ScmVenderDetailReq().setVenderId(venderId));
        logger.info("供应商审核通过，自动创建仓库，供应商信息：{}", vender.getName());
        WarehouseDTO warehouseDTO = new WarehouseDTO();
        warehouseDTO.setAddress(vender.getFactoryAddress());
        warehouseDTO.setEnableFlag(BooleanEnum.YES.getValue());
        warehouseDTO.setVenderFlag(BooleanEnum.YES.getValue());
        warehouseDTO.setVenderCode(vender.getCode());
        warehouseDTO.setVenderName(vender.getName());
        warehouseDTO.setRemark(DateUtil.now() + ",供应商审核通过，自动创建");
        // 创建供应商在制仓
        warehouseDTO.setName("泛太外仓-" + vender.getName());
        warehouseApi.createVenderWareHouse(warehouseDTO);
    }

    @Override
    public List<ConfirmSourceEntity> getConfirmSourceList(String targetCode, Integer effectRangeType) {
        LambdaQueryWrapper<VenderDO> queryWrapper = new LambdaQueryWrapper<VenderDO>().select(VenderDO::getId, VenderDO::getCode)
                .apply("JSON_CONTAINS(effect_range_list,cast({0} as json))", JsonUtils.toJsonString(new JsonEffectRange().setEffectRangeType(effectRangeType).setEffectRangeCode(targetCode).setConfirmFlag(0)));
        List<VenderDO> TList = venderMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(TList)) {
            return List.of();
        }
        return TList.stream().map(s -> new ConfirmSourceEntity().setId(s.getId()).setCode(s.getCode()).setConfirmSourceType(EffectRangeEnum.VENDER.getValue())).toList();
    }

    @Override
    @DataPermission(enable = false)
    public List<String> checkName(String name) {
        if (StrUtil.isEmpty(name)) {
            return List.of();
        }
        List<String> result = venderMapper.checkName(name);
        if (CollUtil.isEmpty(result)) {
            return List.of();
        }
        return result.stream().filter(StrUtil::isNotEmpty).toList();
    }

    @Override
    public boolean antiAudit(Long id) {
        // 校验是否存在
        VenderDO venderDO = validateVenderExists(id);
        // 校验反审核状态
        validateAntiAuditStatus(id);
        // 更改单据状态
        venderDO.setAuditStatus(BpmProcessInstanceResultEnum.UNSUBMITTED.getResult());
        int i = venderMapper.updateById(venderDO);
        return i>0;
    }

    @Override
    @DataPermission(enable = false)
    public SimpleVenderRespDTO getSimpleVenderRespDTOById(Long venderId) {
        List<VenderDO> venderDOs = venderMapper.selectList(new LambdaQueryWrapperX<VenderDO>().eq(VenderDO::getId, venderId).in(VenderDO::getDeleted,List.of(0,1)));
        if (CollUtil.isEmpty(venderDOs)){
            throw exception(VENDER_NOT_EXISTS);
        }
        Optional<VenderDO> venderOpt = venderDOs.stream().findFirst();
        if (venderOpt.isEmpty()){
            throw exception(VENDER_NOT_EXISTS);
        }
        VenderDO venderDO = venderOpt.get();
        //应付供应商
        SimpleVenderRespDTO  simpleVenderRespDTO = BeanUtils.toBean(venderDO,SimpleVenderRespDTO.class);
        List <String> venderLinkCodeList = venderDO.getVenderLinkCode();
        if(!venderLinkCodeList.isEmpty()){
            List<VenderDO> venderDOList = venderMapper.selectList(new LambdaQueryWrapperX<VenderDO>()
                    .in(VenderDO::getCode, venderLinkCodeList)
                    .eq(VenderDO::getChangeFlag, ChangeFlagEnum.NO.getValue()));
            if(!venderDOList.isEmpty()){
                List<SimpleData> simpleDataList = BeanUtils.toBean(venderDOList, SimpleData.class);
                simpleVenderRespDTO.setVenderLink(simpleDataList);
            }
        }
        //获取联系人列表
        List<VenderPocDO> pocListByVenderId = venderPocService.getPocListByVenderId(venderId);
        if (CollUtil.isNotEmpty(pocListByVenderId)) {
            simpleVenderRespDTO.setPocList(BeanUtils.toBean(pocListByVenderId, VenderPocDTO.class));
        }
        return simpleVenderRespDTO;
    }

    /**
     * 校验反审核状态
     * @param id 主键
     */
    private void validateAntiAuditStatus(Long id){
        Long count = venderMapper.validateAntiAuditStatus(id);
        if (count > 0){
            throw exception(ANTI_AUDIT_EXCEPT);
        }
    }

}
