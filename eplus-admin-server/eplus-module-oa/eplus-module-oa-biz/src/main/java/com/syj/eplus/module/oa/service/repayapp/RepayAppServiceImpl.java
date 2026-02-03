package com.syj.eplus.module.oa.service.repayapp;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.bpm.api.task.BpmProcessInstanceApi;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import cn.iocoder.yudao.module.bpm.enums.task.BpmProcessInstanceResultEnum;
import cn.iocoder.yudao.module.infra.api.config.ConfigApi;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import com.syj.eplus.framework.common.dict.AuditFlagDict;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.framework.common.enums.EnableStatusEnum;
import com.syj.eplus.framework.common.enums.ReceiptBusinessType;
import com.syj.eplus.framework.common.enums.RepayStatus;
import com.syj.eplus.framework.common.enums.SubmitFlagEnum;
import com.syj.eplus.module.fms.api.payment.api.receipt.ReceiptApi;
import com.syj.eplus.module.fms.api.payment.api.receipt.dto.ReceiptCreateDTO;
import com.syj.eplus.module.infra.api.CodeGeneratorApi;
import com.syj.eplus.module.oa.api.LoanAppApi;
import com.syj.eplus.module.oa.api.dto.SimpleLoanAppRespDTO;
import com.syj.eplus.module.oa.api.feeshare.FeeShareApi;
import com.syj.eplus.module.oa.controller.admin.repayapp.vo.RepayAppDetailReq;
import com.syj.eplus.module.oa.controller.admin.repayapp.vo.RepayAppPageReqVO;
import com.syj.eplus.module.oa.controller.admin.repayapp.vo.RepayAppRespVO;
import com.syj.eplus.module.oa.controller.admin.repayapp.vo.RepayAppSaveReqVO;
import com.syj.eplus.module.oa.convert.repayapp.RepayAppConvert;
import com.syj.eplus.module.oa.dal.dataobject.repayapp.RepayAppDO;
import com.syj.eplus.module.oa.dal.mysql.repayapp.RepayAppMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.USER_NOT_EXISTS;
import static com.syj.eplus.module.oa.enums.ErrorCodeConstants.REPAY_AMOUNT_ERROR;
import static com.syj.eplus.module.oa.enums.ErrorCodeConstants.REPAY_APP_NOT_EXISTS;

/**
 * 还款单 Service 实现类
 *
 * @author ePlus
 */
@Service
@Validated
public class RepayAppServiceImpl implements RepayAppService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private FeeShareApi feeShareApi;
    @Resource
    private RepayAppMapper repayAppMapper;
    @Resource
    private CodeGeneratorApi codeGeneratorApi;
    public static final String PROCESS_DEFINITION_KEY = "oa_repay_app";
    private static final String SN_TYPE = "sn_repay_app";
    private static final String CODE_PREFIX = "HK";
    @Resource
    private BpmProcessInstanceApi bpmProcessInstanceApi;
    @Resource
    private LoanAppApi loanAppApi;

    @Resource
    private ConfigApi configApi;

    @Resource
    private ReceiptApi receiptApi;

    @Resource
    private AdminUserApi adminUserApi;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createRepayApp(RepayAppSaveReqVO createReqVO) {
        // 生成 还款单 编号
        createReqVO.setCode(codeGeneratorApi.getCodeGenerator(SN_TYPE, CODE_PREFIX));
        Long loginUserId = WebFrameworkUtils.getLoginUserId();
        Map<Long, UserDept> userDeptCache = adminUserApi.getUserDeptCache(loginUserId);
        if (CollUtil.isEmpty(userDeptCache)) {
            throw exception(USER_NOT_EXISTS);
        }
        createReqVO.setRepayer(userDeptCache.get(loginUserId));
        createReqVO.setRepayStatus(RepayStatus.NOT_REPAID.getStatus());
        createReqVO.setAuditStatus(BpmProcessInstanceResultEnum.UNSUBMITTED.getResult());
        RepayAppDO repayApp = RepayAppConvert.INSTANCE.convertRepayAppDO(createReqVO);
        // 插入
        repayAppMapper.insert(repayApp);
        if (SubmitFlagEnum.SUBMIT.getStatus().equals(createReqVO.getSubmitFlag())) {
            // 若未开启审核 点击提交直接更新审核状态为已通过并创建收款单
            if (Objects.equals(configApi.getValueByConfigKey(AuditFlagDict.REPAY_AUDIT_FLAG), EnableStatusEnum.TURN_OFF.getStatus())) {
                // 创建收款单
                ReceiptCreateDTO receiptCreateDTO = RepayAppConvert.INSTANCE.convertReceiptCreateDTO(repayApp);
                receiptCreateDTO.setBusinessCode(repayApp.getCode());
                receiptCreateDTO.setBusinessType(ReceiptBusinessType.REPAY.getValue());
                receiptApi.createReceipt(receiptCreateDTO);
                // 更新审核状态
                updateAuditStatus(repayApp.getId(), BpmProcessInstanceResultEnum.APPROVE.getResult());
            } else {
                validateRepayAmount(repayApp);
                submitTask(repayApp.getId(), WebFrameworkUtils.getLoginUserId());
            }

        }
        // 返回
        return repayApp.getId();
    }
    private void validateRepayAmount( RepayAppDO repayAppDO) {
        List<SimpleLoanAppRespDTO> simpleLoanAppRespDTOList = loanAppApi.getSimpleLoanAppRespDTOList(Collections.singletonList(repayAppDO.getLoanAppId()), null, repayAppDO.getId());
        if (CollUtil.isEmpty(simpleLoanAppRespDTOList)){
            throw exception(REPAY_APP_NOT_EXISTS);
        }
        Optional<SimpleLoanAppRespDTO> firstOpt = simpleLoanAppRespDTOList.stream().findFirst();
        if (firstOpt.isPresent()){
            SimpleLoanAppRespDTO simpleLoanAppRespDTO = firstOpt.get();
            JsonAmount outstandingAmount = simpleLoanAppRespDTO.getOutstandingAmount();
            JsonAmount amount = repayAppDO.getAmount();
            if (outstandingAmount.getAmount().compareTo(amount.getAmount())<0){
                throw exception(REPAY_AMOUNT_ERROR);
            }
        }
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRepayApp(RepayAppSaveReqVO updateReqVO) {
        // 校验存在
        validateRepayAppExists(updateReqVO.getId());
        // 更新
        RepayAppDO updateObj = RepayAppConvert.INSTANCE.convertRepayAppDO(updateReqVO);
        repayAppMapper.updateById(updateObj);
        if (SubmitFlagEnum.SUBMIT.getStatus().equals(updateReqVO.getSubmitFlag())) {
            // 若未开启审核 点击提交直接更新审核状态为已通过并创建收款单
            if (Objects.equals(configApi.getValueByConfigKey(AuditFlagDict.REPAY_AUDIT_FLAG), EnableStatusEnum.TURN_OFF.getStatus())) {
                // 创建收款单
                ReceiptCreateDTO receiptCreateDTO = RepayAppConvert.INSTANCE.convertReceiptCreateDTO(updateObj);
                receiptCreateDTO.setBusinessCode(updateObj.getCode());
                receiptCreateDTO.setBusinessType(ReceiptBusinessType.REPAY.getValue());
                receiptApi.createReceipt(receiptCreateDTO);
                // 更新审核状态
                updateAuditStatus(updateObj.getId(), BpmProcessInstanceResultEnum.APPROVE.getResult());
            } else {
                validateRepayAmount(updateObj);
                submitTask(updateObj.getId(), WebFrameworkUtils.getLoginUserId());
            }

        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRepayApp(Long id) {
        // 校验存在
        RepayAppDO repayAppDO = validateRepayAppExists(id);
        // 删除
        repayAppMapper.deleteById(id);
    }

    private RepayAppDO validateRepayAppExists(Long id) {
        RepayAppDO repayAppDO = repayAppMapper.selectById(id);
        if (repayAppDO == null) {
            throw exception(REPAY_APP_NOT_EXISTS);
        }
        return repayAppDO;
    }

    private RepayAppDO validateRepayAppExistsByCode(String code) {
        RepayAppDO repayAppDO = repayAppMapper.selectOne(RepayAppDO::getCode, code);
        if (repayAppDO == null) {
            throw exception(REPAY_APP_NOT_EXISTS);
        }
        return repayAppDO;
    }
    @Override
    public RepayAppRespVO getRepayApp(RepayAppDetailReq req) {
        RepayAppDO repayAppDO;
        String code = req.getCode();
        if (StrUtil.isNotEmpty(code)) {
            repayAppDO = repayAppMapper.selectOne(RepayAppDO::getCode, code);
        } else {
            //将不同入口的参数转换为repayAppId
            Long repayAppId = Objects.nonNull(req.getProcessInstanceId()) ? bpmProcessInstanceApi.selectAuditAbleIdByProcessInstanceId(req.getProcessInstanceId()) : req.getRepayAppId();
            if (Objects.isNull(repayAppId)) {
                logger.error("[还款申请单详情]未获取到还款申请id");
                return null;
            }
            repayAppDO = repayAppMapper.selectById(repayAppId);
        }
        if (repayAppDO == null) {
            return null;
        }
        RepayAppRespVO repayAppRespVO = RepayAppConvert.INSTANCE.convertRepayAppRespVO(repayAppDO);
        // 获取借款单编号
        Long loanAppId = repayAppRespVO.getLoanAppId();
        if (Objects.isNull(loanAppId)) {
            return null;
        }
        List<SimpleLoanAppRespDTO> simpleLoanAppRespDTOList = loanAppApi.getSimpleLoanAppRespDTOList(Collections.singletonList(loanAppId), null, repayAppDO.getId());
            if (CollUtil.isNotEmpty(simpleLoanAppRespDTOList)) {
                Optional<SimpleLoanAppRespDTO> LoanAppOptional = simpleLoanAppRespDTOList.stream().findFirst();
                if (LoanAppOptional.isPresent()) {
                    SimpleLoanAppRespDTO simpleLoanAppRespDTO = LoanAppOptional.get();
                    repayAppRespVO.setLoanAppCode(simpleLoanAppRespDTO.getCode());
                    // 待还款金额
                    repayAppRespVO.setOutstandingAmount(simpleLoanAppRespDTO.getOutstandingAmount());
                    // 还款中金额
                    repayAppRespVO.setInRepaymentAmount(simpleLoanAppRespDTO.getInRepaymentAmount());
                    // 已还金额
                    repayAppRespVO.setRepayAmount(simpleLoanAppRespDTO.getRepayAmount());
                    // 借款金额
                    repayAppRespVO.setLoanAmount(simpleLoanAppRespDTO.getAmount());
                    // 主体名称
                    repayAppRespVO.setCompanyName(simpleLoanAppRespDTO.getCompanyName());
                    repayAppRespVO.setCompanyId(simpleLoanAppRespDTO.getCompanyId());
                }
            }
        String bpmProcessInstanceId = bpmProcessInstanceApi.getBpmProcessInstanceId(repayAppRespVO.getId(), PROCESS_DEFINITION_KEY);
        if (StrUtil.isNotEmpty(bpmProcessInstanceId)) {
            repayAppRespVO.setProcessInstanceId(bpmProcessInstanceId);
        }
        return repayAppRespVO;
    }

    @Override
    public PageResult<RepayAppRespVO> getRepayAppPage(RepayAppPageReqVO pageReqVO) {
        String loanAppCode = pageReqVO.getLoanAppCode();

        List<Long> loanAppIds = new ArrayList<>();
        if (StrUtil.isNotEmpty(loanAppCode)) {
            loanAppIds = loanAppApi.getIdListByCode(loanAppCode);
        }
        // 为null则表示前台传入借款编号但是未找对对应借款id，直接返回null即可
        // 为空不为null则说明前台未用借款单号进行过滤
        if (loanAppIds == null) {
            return null;
        }
        PageResult<RepayAppDO> repayAppDOPageResult = repayAppMapper.selectPage(pageReqVO, loanAppIds);
        // 获取所有借款单精简列表
        List<SimpleLoanAppRespDTO> simpleLoanAppRespDTOList = loanAppApi.getSimpleLoanAppRespDTOList(null, null, null);
        return RepayAppConvert.INSTANCE.convertRepayRespPageResult(repayAppDOPageResult, simpleLoanAppRespDTOList);
    }

    @Override
    public void approveTask(Long userId, BpmTaskApproveReqDTO reqVO) {
        bpmProcessInstanceApi.approveTask(userId, BeanUtils.toBean(reqVO, BpmTaskApproveReqDTO.class));
    }

    @Override
    public void rejectTask(Long userId, BpmTaskRejectReqDTO reqVO) {
        bpmProcessInstanceApi.rejectTask(userId, BeanUtils.toBean(reqVO, BpmTaskRejectReqDTO.class));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitTask(Long paymentId, Long userId) {
        RepayAppDO repayAppDO = repayAppMapper.selectById(paymentId);
        validateRepayAmount(repayAppDO);
        bpmProcessInstanceApi.createProcessInstance(userId, PROCESS_DEFINITION_KEY, paymentId);
        updateAuditStatus(paymentId, BpmProcessInstanceResultEnum.PROCESS.getResult());
    }

    @Override
    public void updateAuditStatus(Long auditableId, Integer auditStatus) {
        repayAppMapper.updateById(RepayAppDO.builder().id(auditableId).auditStatus(auditStatus).build());
    }

    @Override
    public String getProcessDefinitionKey() {
        return PROCESS_DEFINITION_KEY;
    }

    @Override
    public Map<Long, List<RepayAppRespVO>> getRepayAppMap(List<Long> ids, Long repayAppId) {
        LambdaQueryWrapperX<RepayAppDO> queryWrapperX = new LambdaQueryWrapperX<RepayAppDO>().in(RepayAppDO::getLoanAppId, ids)
                .eq(RepayAppDO::getRepayStatus, RepayStatus.NOT_REPAID.getStatus())
                .neIfPresent(RepayAppDO::getAuditStatus, BpmProcessInstanceResultEnum.UNSUBMITTED.getResult());
//        if (Objects.nonNull(repayAppId)) {
//            queryWrapperX.ne(RepayAppDO::getId, repayAppId);
//        }
        List<RepayAppDO> repayAppDOList = repayAppMapper.selectList(queryWrapperX);
        if (CollUtil.isEmpty(repayAppDOList)) {
            return Collections.emptyMap();
        }
        List<RepayAppRespVO> repayAppRespVOList = RepayAppConvert.INSTANCE.convertRepayAppRespVOList(repayAppDOList);
        Map<Long, List<RepayAppRespVO>> result = repayAppRespVOList.stream().collect(Collectors.groupingBy(RepayAppRespVO::getLoanAppId));
        if (CollUtil.isEmpty(result)) {
            return Collections.emptyMap();
        }
        return result;
    }

    @Override
    public List<RepayAppDO> getRepayAppListWithOutCancel(Long id) {
        return repayAppMapper.selectList(new LambdaQueryWrapperX<RepayAppDO>().eq(RepayAppDO::getLoanAppId, id).ne(RepayAppDO::getRepayStatus, RepayStatus.CANCEL.getStatus()));
    }

    @Override
    public void updateRepayStatus(String code) {
        RepayAppDO repayAppDO = validateRepayAppExistsByCode(code);
        if (Objects.isNull(repayAppDO)) {
            throw exception(REPAY_APP_NOT_EXISTS);
        }
        repayAppMapper.updateRepayStatus(code);
        loanAppApi.updateRepayAmount(repayAppDO.getAmount(), repayAppDO.getLoanAppId());
    }

    @Override
    public UserDept getApplyerByCode(String code) {
        RepayAppDO repayAppDO = repayAppMapper.selectOne(RepayAppDO::getCode, code);
        if (Objects.isNull(repayAppDO)) {
            return null;
        }
        return repayAppDO.getRepayer();
    }
}