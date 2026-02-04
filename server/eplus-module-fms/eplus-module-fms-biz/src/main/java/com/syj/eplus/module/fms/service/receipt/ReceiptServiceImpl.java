package com.syj.eplus.module.fms.service.receipt;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.bpm.api.task.BpmProcessInstanceApi;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRespDTO;
import cn.iocoder.yudao.module.bpm.enums.task.BpmProcessInstanceResultEnum;
import com.syj.eplus.module.infra.api.company.CompanyApi;
import com.syj.eplus.module.infra.api.company.dto.SimpleCompanyDTO;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import com.syj.eplus.framework.common.dict.BusinessSubjectTypeDict;
import com.syj.eplus.framework.common.dict.ReceiptBusinessSubjectTypeDict;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.framework.common.enums.ReceiptBusinessType;
import com.syj.eplus.framework.common.enums.ReceiptStatusEnum;
import com.syj.eplus.framework.common.enums.SubmitFlagEnum;
import com.syj.eplus.module.crm.api.cust.CustApi;
import com.syj.eplus.module.fms.controller.admin.receipt.vo.ReceiptDetailReq;
import com.syj.eplus.module.fms.controller.admin.receipt.vo.ReceiptPageReqVO;
import com.syj.eplus.module.fms.controller.admin.receipt.vo.ReceiptRespVO;
import com.syj.eplus.module.fms.controller.admin.receipt.vo.ReceiptSaveReqVO;
import com.syj.eplus.module.fms.convert.receipt.ReceiptConvert;
import com.syj.eplus.module.fms.dal.dataobject.receipt.ReceiptDO;
import com.syj.eplus.module.fms.dal.mysql.receipt.ReceiptMapper;
import com.syj.eplus.module.infra.api.CodeGeneratorApi;
import com.syj.eplus.module.oa.api.RepayAppApi;
import com.syj.eplus.module.scm.api.vender.VenderApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;
import static com.syj.eplus.module.fms.api.payment.enums.ErrorCodeConstants.RECEIPT_NOT_EXISTS;

/**
 * 财务收款单 Service 实现类
 *
 * @author ePlus
 */
@Service
@Validated
public class ReceiptServiceImpl implements ReceiptService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private ReceiptMapper receiptMapper;
    @Resource
    private CodeGeneratorApi codeGeneratorApi;
    @Resource
    private BpmProcessInstanceApi bpmProcessInstanceApi;
    private static final String SN_TYPE = "sn_receipt";
    private static final String CODE_PREFIX = "SK";
    private static final String PROCESS_DEFINITION_KEY = "fms_receipt";

    @Resource
    private CompanyApi companyApi;

    @Resource
    @Lazy
    private RepayAppApi repayAppApi;
    @Resource
    private CustApi custApi;
    @Resource
    private VenderApi venderApi;
    @Resource
    private AdminUserApi adminUserApi;
    @Override
    public Long createReceipt(ReceiptSaveReqVO createReqVO) {
        ReceiptDO receipt = ReceiptConvert.INSTANCE.convertReceiptDO(createReqVO);
        // 生成 财务收款单 编号
        receipt.setCode(codeGeneratorApi.getCodeGenerator(SN_TYPE, CODE_PREFIX));
        // 插入
        receiptMapper.insert(receipt);
        Long startUserId = Objects.isNull(createReqVO.getStartUserId()) ? WebFrameworkUtils.getLoginUserId() : createReqVO.getStartUserId();
        if (SubmitFlagEnum.SUBMIT.getStatus().equals(createReqVO.getSubmitFlag())) {
            submitTask(receipt.getId(), startUserId);
        }
        // 返回
        return receipt.getId();
    }

    @Override
    public List<ReceiptDO> batchCreateReceipt(List<ReceiptSaveReqVO> createReqVOList) {
        List<ReceiptDO> receipts = ReceiptConvert.INSTANCE.convertReceiptDOList(createReqVOList);
        receipts.forEach(s->{
            s.setCode(codeGeneratorApi.getCodeGenerator(SN_TYPE, CODE_PREFIX));
        });
        receiptMapper.insertBatch(receipts);
        return receipts;
    }

    @Override
    public void updateReceipt(ReceiptSaveReqVO updateReqVO) {
        // 校验存在
        validateReceiptExists(updateReqVO.getId());
        // 更新
        ReceiptDO updateObj = ReceiptConvert.INSTANCE.convertReceiptDO(updateReqVO);
        receiptMapper.updateById(updateObj);
    }

    @Override
    public void deleteReceipt(Long id) {
        // 校验存在
        validateReceiptExists(id);
        // 删除
        receiptMapper.deleteById(id);
    }

    private void validateReceiptExists(Long id) {
        if (receiptMapper.selectById(id) == null) {
            throw exception(RECEIPT_NOT_EXISTS);
        }
    }

    @Override
    public void approveTask(Long userId, BpmTaskApproveReqDTO reqDTO) {
        bpmProcessInstanceApi.approveTask(userId, BeanUtils.toBean(reqDTO, BpmTaskApproveReqDTO.class));
    }

    @Override
    public void rejectTask(Long userId, BpmTaskRejectReqDTO reqVO) {
        bpmProcessInstanceApi.rejectTask(userId, BeanUtils.toBean(reqVO, BpmTaskRejectReqDTO.class));
    }

    @Override
    public void submitTask(Long receiptId, Long userId) {
        bpmProcessInstanceApi.createProcessInstance(userId, PROCESS_DEFINITION_KEY, receiptId);
        updateAuditStatus(receiptId, BpmProcessInstanceResultEnum.PROCESS.getResult());
    }

    @Override
    public void updateAuditStatus(Long auditableId, Integer auditStatus) {
        receiptMapper.updateById(ReceiptDO.builder().id(auditableId).auditStatus(auditStatus).build());
    }

    @Override
    public void updateApprovalMessage(Long id, String processInstanceId) {
        BpmTaskRespDTO bpmTask = bpmProcessInstanceApi.getBpmTask(processInstanceId);
        receiptMapper.updateById(ReceiptDO.builder().id(id).approver(bpmTask.getApprover()).approvalTime(bpmTask.getEndTime()).build());
    }

    @Override
    public void confirmReceipt(Long receiptId) {
        ReceiptDO receiptDO = receiptMapper.selectById(receiptId);
        if (Objects.isNull(receiptDO)) {
            return;
        }
        receiptDO = ReceiptDO.builder().id(receiptId).amount(receiptDO.getAmount()).businessType(receiptDO.getBusinessType()).businessCode(receiptDO.getBusinessCode()).status(ReceiptStatusEnum.RECEIVED.getValue()).receiptTime(LocalDateTime.now()).build();
        UserDept userDept = adminUserApi.getUserDeptByUserId(getLoginUserId());
        if (Objects.nonNull(userDept)) {
            receiptDO.setReceiptUser(userDept);
        }
        receiptMapper.updateById(receiptDO);
        if (ReceiptBusinessType.REPAY.getValue().equals(receiptDO.getBusinessType())) {
            repayAppApi.updateRepayStatus(receiptDO.getBusinessCode());
        }

    }

    @Override
    public Long getOrderNumByBusinessCode(Integer businessType, String businessCode) {
        return receiptMapper.selectCount(new LambdaQueryWrapperX<ReceiptDO>().eq(ReceiptDO::getBusinessType, businessType).eq(ReceiptDO::getBusinessCode, businessCode));
    }

    @Override
    public void batchDeletedReceipt(Integer businessType, String businessCode, Integer businessSubjectType, String businessSubjectCode) {
        receiptMapper.delete(new LambdaQueryWrapperX<ReceiptDO>().eq(ReceiptDO::getBusinessType, businessType)
                .eq(ReceiptDO::getBusinessCode, businessCode)
                .eq(ReceiptDO::getBusinessSubjectType, businessSubjectType)
                .eq(ReceiptDO::getBusinessSubjectCode, businessSubjectCode));
    }

    @Override
    public String getProcessDefinitionKey() {
        return PROCESS_DEFINITION_KEY;
    }

    @Override
    public ReceiptRespVO getReceipt(ReceiptDetailReq receiptDetailReq) {
        Long receiptId = Objects.nonNull(receiptDetailReq.getProcessInstanceId()) ? bpmProcessInstanceApi.selectAuditAbleIdByProcessInstanceId(receiptDetailReq.getProcessInstanceId()) : receiptDetailReq.getReceiptId();
        if (Objects.isNull(receiptId)) {
            logger.error("[财务收款单]未获取到财务收款单id");
            return null;
        }
        ReceiptDO receiptDO = receiptMapper.selectById(receiptId);
        if (receiptDO == null) {
            return null;
        }

        Map<Long, SimpleCompanyDTO> simpleCompanyDTO = companyApi.getSimpleCompanyDTO();
        ReceiptRespVO receiptRespVO = ReceiptConvert.INSTANCE.convertReceiptRespVO(receiptDO, simpleCompanyDTO);
        if (Objects.nonNull(receiptRespVO)) {
            Integer businessSubjectType = receiptRespVO.getBusinessSubjectType();
            String businessSubjectCode = receiptRespVO.getBusinessSubjectCode();
            String nameByBusinessCode = getNameByBusinessCode(businessSubjectType, businessSubjectCode);
            receiptRespVO.setBusinessSubjectName(nameByBusinessCode);
        }
        // 获取申请人信息
        Integer businessType = receiptDO.getBusinessType();
        String businessCode = receiptDO.getBusinessCode();
        switch (businessType) {
            case ReceiptBusinessSubjectTypeDict.REPAYMENT -> {
                receiptRespVO.setApplyer(repayAppApi.getApplyerByCode(businessCode));
                break;
            }
            default -> {
                break;
            }
        }
        // 若配置不需要审核则直接返回
        if (BpmProcessInstanceResultEnum.UNSUBMITTED.getResult().equals(receiptDO.getAuditStatus())) {
            return receiptRespVO;
        }
        String bpmProcessInstanceId = bpmProcessInstanceApi.getBpmProcessInstanceId(receiptId, PROCESS_DEFINITION_KEY);
        if (StrUtil.isNotEmpty(bpmProcessInstanceId)) {
            receiptRespVO.setProcessInstanceId(bpmProcessInstanceId);
        }
        return receiptRespVO;
    }

    @Override
    public PageResult<ReceiptRespVO> getReceiptPage(ReceiptPageReqVO pageReqVO) {
        Map<Long, SimpleCompanyDTO> simpleCompanyDTOList = companyApi.getSimpleCompanyDTO();
        PageResult<ReceiptDO> receiptDOPageResult = receiptMapper.selectPage(pageReqVO);
        PageResult<ReceiptRespVO> receiptRespVOPageResult = ReceiptConvert.INSTANCE.convertReceiptPageResult(receiptDOPageResult, simpleCompanyDTOList);
        List<ReceiptRespVO> receiptRespVOList = receiptRespVOPageResult.getList();
        if (CollUtil.isNotEmpty(receiptRespVOList)) {
            receiptRespVOList.forEach(s -> {
                String businessSubjectCode = s.getBusinessSubjectCode();
                Integer businessSubjectType = s.getBusinessSubjectType();
                String nameByBusinessCode = getNameByBusinessCode(businessSubjectType, businessSubjectCode);
                s.setBusinessSubjectName(nameByBusinessCode);
            });
        }
        return receiptRespVOPageResult.setList(receiptRespVOList);
    }

    private String getNameByBusinessCode(Integer businessSubjectType, String businessSubjectCode) {
        if (Objects.isNull(businessSubjectType)) {
            return null;
        }
        switch (businessSubjectType) {
            case BusinessSubjectTypeDict.CUST:
                return custApi.getCustNameCache(businessSubjectCode).get(businessSubjectCode);
            case BusinessSubjectTypeDict.BUSINESS_VENDER:
            case BusinessSubjectTypeDict.ADMINISTRATION_VENDER:
                return venderApi.getVenderNameCache(businessSubjectCode).get(businessSubjectCode);
            case BusinessSubjectTypeDict.PERSON:
                return dealUserDept(Long.parseLong(businessSubjectCode));
            default: {
                logger.warn("[财务收款单]根据编号:{}未获取到{}对应名称", businessSubjectCode, businessSubjectType);
                return null;
            }
        }
    }

    private String dealUserDept(Long userId) {
        UserDept userDept = adminUserApi.getUserDeptCache(userId).get(userId);
        if (Objects.isNull(userDept)) {
            return null;
        }
        return JsonUtils.toJsonString(BeanUtils.toBean(userDept, UserDept.class));
    }
}