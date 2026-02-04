package com.syj.eplus.module.oa.service.apply;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.bpm.api.task.BpmProcessInstanceApi;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import cn.iocoder.yudao.module.bpm.enums.task.BpmProcessInstanceResultEnum;
import com.syj.eplus.module.infra.api.company.CompanyApi;
import com.syj.eplus.module.infra.api.company.dto.SimpleCompanyDTO;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.framework.common.enums.*;
import com.syj.eplus.module.infra.api.CodeGeneratorApi;
import com.syj.eplus.module.oa.api.feeshare.FeeShareApi;
import com.syj.eplus.module.oa.api.feeshare.dto.FeeShareReqDTO;
import com.syj.eplus.module.oa.api.feeshare.dto.FeeShareRespDTO;
import com.syj.eplus.module.oa.controller.admin.apply.vo.ApplyDetailReq;
import com.syj.eplus.module.oa.controller.admin.apply.vo.ApplyPageReqVO;
import com.syj.eplus.module.oa.controller.admin.apply.vo.ApplyRespVO;
import com.syj.eplus.module.oa.controller.admin.apply.vo.ApplySaveReqVO;
import com.syj.eplus.module.oa.convert.apply.ApplyConvert;
import com.syj.eplus.module.oa.dal.dataobject.apply.ApplyDO;
import com.syj.eplus.module.oa.dal.mysql.apply.ApplyMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.infra.enums.ErrorCodeConstants.COMPANY_NOT_EXISTS;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.FEE_SHARE_NOT_EXISTS;
import static com.syj.eplus.module.oa.enums.ErrorCodeConstants.APPLY_NOT_EXISTS;
import static com.syj.eplus.module.oa.enums.ErrorCodeConstants.APPLY_TYPE_NOT_EXISTS;

/**
 * OA申请单 Service 实现类
 *
 * @author ePlus
 */
@Service
@Validated
public class ApplyServiceImpl implements ApplyService {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private ApplyMapper applyMapper;
    @Resource
    private CodeGeneratorApi codeGeneratorApi;
    @Resource
    private BpmProcessInstanceApi bpmProcessInstanceApi;
    @Resource
    private CompanyApi companyApi;

    @Resource
    private FeeShareApi feeShareApi;
    private static final String SN_TYPE = "apply";
    private static final String CODE_PREFIX = "app";
    private static final String PROCESS_DEFINITION_ENTERTAIN_KEY = "oa_apply_entertain";
    private static final String PROCESS_DEFINITION_TRAVEL_KEY = "oa_apply_travel";
    private static final String PROCESS_DEFINITION_GENERAL_KEY = "oa_apply_general";
    private static final String PROCESS_DEFINITION_OTHER_KEY = "oa_apply_other";

    @Resource
    private AdminUserApi adminUserApi;

    @Override
    @Transactional
    public Long createApply(ApplySaveReqVO createReqVO,FeeShareSourceTypeEnum typeEnum) {
        //赋值申请人
        if(Objects.isNull(createReqVO.getApplyer()) || Objects.isNull(createReqVO.getApplyer().getUserId())){
            createReqVO.setApplyer(adminUserApi.getUserDeptByUserId(WebFrameworkUtils.getLoginUserId()));
        }
        createReqVO.setAuditStatus(BpmProcessInstanceResultEnum.UNSUBMITTED.getResult());
        createReqVO.setIsApplyExpense(PayFeeStatusEnum.NOT_APPLY.getValue());
        createReqVO.setApplyTime(DateTime.now().toLocalDateTime());

        ApplyDO apply = ApplyConvert.INSTANCE.convertApplyDO(createReqVO);
        // 生成 OA申请单 编号
        apply.setCode(codeGeneratorApi.getCodeGenerator(SN_TYPE, CODE_PREFIX));
        // 插入
        applyMapper.insert(apply);
        //费用归集信息
        updateFeeShareList(createReqVO.getFeeShare(),apply,typeEnum);

        if (SubmitFlagEnum.SUBMIT.getStatus().equals(createReqVO.getSubmitFlag())) {
            submitTask(apply.getId(), WebFrameworkUtils.getLoginUserId(),createReqVO.getApplyType());
        }
        // 返回
        return apply.getId();
    }

    private  void updateFeeShareList( List<FeeShareReqDTO>  feeShareList, ApplyDO apply,FeeShareSourceTypeEnum typeEnum  ){
        if (CollUtil.isEmpty(feeShareList)) {
            throw exception(FEE_SHARE_NOT_EXISTS);
        }
        for (FeeShareReqDTO feeShare : feeShareList) {
            feeShare.setBusinessType(typeEnum.getValue());
            feeShare.setBusinessCode(apply.getCode());
            feeShare.setCompanyId(apply.getCompanyId());
            feeShare.setAuditStatus(BpmProcessInstanceResultEnum.PROCESS.getResult());
            feeShare.setStatus(BooleanEnum.YES.getValue());
            feeShare.setInputUser(apply.getApplyer());
            Map<Long, SimpleCompanyDTO> simpleCompanyDTOMap = companyApi.getSimpleCompanyDTO();
            if (CollUtil.isNotEmpty(simpleCompanyDTOMap)) {
                SimpleCompanyDTO simpleCompanyDTO = simpleCompanyDTOMap.get(apply.getCompanyId());
                if (Objects.isNull(simpleCompanyDTO)) {
                    throw exception(COMPANY_NOT_EXISTS);
                }
                feeShare.setCompanyName(simpleCompanyDTO.getCompanyName());
            }
        }
        feeShareApi.updatePreFeeShareByDTOList(feeShareList);
    }

    @Override
    public void updateApply(ApplySaveReqVO updateReqVO,FeeShareSourceTypeEnum typeEnum) {
        // 校验存在
        validateApplyExists(updateReqVO.getId());
        // 更新
        ApplyDO updateObj = ApplyConvert.INSTANCE.convertApplyDO(updateReqVO);
        applyMapper.updateById(updateObj);
        if (SubmitFlagEnum.SUBMIT.getStatus().equals(updateReqVO.getSubmitFlag())) {
            submitTask(updateObj.getId(), WebFrameworkUtils.getLoginUserId(),updateReqVO.getApplyType());
        }
        updateFeeShareList(updateReqVO.getFeeShare(),updateObj,typeEnum);
    }

    @Override
    public void deleteApply(Long id) {
        // 校验存在
        validateApplyExists(id);
        // 删除
        applyMapper.deleteById(id);
    }

    private ApplyDO validateApplyExists(Long id) {
        ApplyDO applyDO = applyMapper.selectById(id);
        if (applyDO == null) {
            throw exception(APPLY_NOT_EXISTS);
        }
        return applyDO;
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
    public void updateAuditStatus(Long auditableId, Integer auditStatus) {
        ApplyDO applyDO = validateApplyExists(auditableId);
        applyDO.setAuditStatus(auditStatus);
        applyMapper.updateById(applyDO);
    }


    @Override
    public List<ApplyRespVO> getApplyListByIdList(List<Long> idList) {
        if (CollUtil.isEmpty(idList)){
            return List.of();
        }
        List<ApplyDO> applyDOS = applyMapper.selectBatchIds(idList);
        if (CollUtil.isEmpty(applyDOS)){
            return List.of();
        }
        return ApplyConvert.INSTANCE.convertList(applyDOS);
    }


    @Override
    public ApplyRespVO getApply( ApplyDetailReq applyDetailReq,FeeShareSourceTypeEnum type) {
        Long applyId = Objects.nonNull(applyDetailReq.getProcessInstanceId()) ? bpmProcessInstanceApi.selectAuditAbleIdByProcessInstanceId(applyDetailReq.getProcessInstanceId()) : applyDetailReq.getApplyId();
        if (Objects.isNull(applyId)) {
            return null;
        }
        ApplyDO applyDO = applyMapper.selectById(applyId);
        if (applyDO == null) {
           return null;
        }
        ApplyRespVO  applyRespVO = ApplyConvert.INSTANCE.convertApplyRespVO(applyDO);
        List<FeeShareRespDTO> feeShare = feeShareApi.getPreFeeShareDTO(type,applyDO.getCode());
        applyRespVO.setFeeShare(feeShare);
        String bpmProcessInstanceId;
        bpmProcessInstanceId = bpmProcessInstanceApi.getBpmProcessInstanceId( applyDO.getId(), getProcessDefinitionKey(applyDO.getApplyType()));
        if (StrUtil.isNotEmpty(bpmProcessInstanceId)) {
            applyRespVO.setProcessInstanceId(bpmProcessInstanceId);
        }
        return applyRespVO;
    }

    private Integer getFeeShareTypeByApplyType(Integer type){
        Map<Integer, Integer> keymap = Map.of(
                ApplyTypeEnum.TRAVEL.getValue(), FeeShareSourceTypeEnum.TRAVEL_APPLY.getValue(),
                ApplyTypeEnum.GENERAL.getValue(), FeeShareSourceTypeEnum.GENERAL_APPLY.getValue(),
                ApplyTypeEnum.ENTERTAIN.getValue(), FeeShareSourceTypeEnum.ENTERTAIN_APPLY.getValue());
        return keymap.get(type);
    }

    @Override
    public PageResult<ApplyRespVO> getApplyPage(ApplyPageReqVO pageReqVO) {
        if(Objects.isNull(pageReqVO.getApplyType())){
            throw exception(APPLY_TYPE_NOT_EXISTS);
        }
        Integer feeShareType = getFeeShareTypeByApplyType(pageReqVO.getApplyType());
        PageResult<ApplyDO> applyDOPageResult = applyMapper.selectPage(pageReqVO);
        List<ApplyDO> doList = applyDOPageResult.getList();
        if(CollUtil.isEmpty(doList)){
            return PageResult.empty();
        }
        List<ApplyRespVO> voList = BeanUtils.toBean(doList, ApplyRespVO.class);
//        List<String> codes = voList.stream().map(ApplyRespVO::getCode).toList();
//        Map<String, List<FeeShareRespDTO>> feeShareMap = feeShareApi.getFeeShareByCodeList(feeShareType, codes);
//        if(CollUtil.isNotEmpty(feeShareMap)){
//            voList.forEach(s->{
//                s.setFeeShare(feeShareMap.get(s.getCode()));
//            });
//        }
        return new PageResult<ApplyRespVO>().setList(voList).setTotal(applyDOPageResult.getTotal());
    }


    @Override
    public String getProcessDefinitionKey(Integer typeNum) {
        Map<Integer, String> keymap = Map.of(
                ApplyTypeEnum.TRAVEL.getValue(), PROCESS_DEFINITION_TRAVEL_KEY,
                ApplyTypeEnum.GENERAL.getValue(), PROCESS_DEFINITION_GENERAL_KEY,
                ApplyTypeEnum.ENTERTAIN.getValue(), PROCESS_DEFINITION_ENTERTAIN_KEY);
         return  keymap.get(typeNum);
    }
    @Override
    public void submitTask(Long applyId, Long userId,Integer typeNum) {
        ApplyDO applyDO = validateApplyExists(applyId);
        Map<String, Object> variables = new HashMap<>();
        BigDecimal applyAmount = Objects.isNull(applyDO.getAmount()) ? BigDecimal.ZERO : (Objects.isNull(applyDO.getAmount().getAmount()) ? BigDecimal.ZERO : applyDO.getAmount().getAmount());
        variables.put("applyAmount",applyAmount);
        UserDept userDept = adminUserApi.getUserDeptByUserId(userId);
        Optional.ofNullable(userDept).ifPresent(s->variables.put("deptCode",userDept.getDeptCode()));
        bpmProcessInstanceApi.createProcessInstance(userId, getProcessDefinitionKey(typeNum), applyId,variables,Map.of());
        updateAuditStatus(applyId, BpmProcessInstanceResultEnum.PROCESS.getResult());
    }

    @Override



    public void batchUpdateIsApplyExpense(List<Long> applyIdList,Integer applyExpenseFlag){
        if (CollUtil.isEmpty(applyIdList)){
            return;
        }
        List<ApplyDO> applyDOS = applyMapper.selectBatchIds(applyIdList);
        if (CollUtil.isEmpty(applyDOS)){
            return;
        }
        applyDOS.forEach(s->{
            int applyExpenseTimes = Objects.isNull(s.getApplyExpenseTimes()) ? 0 : s.getApplyExpenseTimes();
            if (ApplyTypeEnum.TRAVEL.getValue().equals(s.getApplyType())&&TravelTypeEnum.INTERNATIONAL_BUSINESS_TRIP.getType().equals(s.getTravelType())){
               if (ApplyExpenseEnum.CREATE.getValue().equals(applyExpenseFlag)){
                   s.setApplyExpenseTimes(++applyExpenseTimes);
               }else {
                   s.setApplyExpenseTimes(--applyExpenseTimes<0?0:applyExpenseTimes);
               }
            }else {
                if (ApplyExpenseEnum.CREATE.getValue().equals(applyExpenseFlag)){
                    s.setApplyExpenseTimes(1);
                }else {
                    s.setApplyExpenseTimes(0);
                }
            }
            if (s.getApplyExpenseTimes() >0){
                s.setIsApplyExpense(BooleanEnum.YES.getValue());
            }else {
                s.setIsApplyExpense(BooleanEnum.NO.getValue());
            }
        });
      applyMapper.updateBatch(applyDOS);
    }

    @Override
    public void closepApply(Long id) {
        ApplyDO applyDO = validateApplyExists(id);
        applyDO.setAuditStatus(BpmProcessInstanceResultEnum.CLOSE.getResult());
        applyMapper.updateById(applyDO);
    }
}