package com.syj.eplus.module.scm.service.concessionrelease;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.bpm.api.task.BpmProcessInstanceApi;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import cn.iocoder.yudao.module.bpm.enums.task.BpmProcessInstanceResultEnum;
import cn.iocoder.yudao.module.system.api.dept.DeptApi;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import com.syj.eplus.framework.common.entity.CreatedResponse;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.framework.common.enums.BooleanEnum;
import com.syj.eplus.framework.common.enums.SubmitFlagEnum;
import com.syj.eplus.module.pms.api.sku.SkuApi;
import com.syj.eplus.module.pms.api.sku.dto.SimpleSkuDTO;
import com.syj.eplus.module.qms.api.QualityInspectionApi;
import com.syj.eplus.module.qms.api.dto.QualityInspectionItemRespDTO;
import com.syj.eplus.module.qms.api.dto.QualityInspectionReqDTO;
import com.syj.eplus.module.qms.api.dto.QualityInspectionRespDTO;
import com.syj.eplus.module.scm.controller.admin.concessionrelease.vo.ConcessionReleaseDetailReq;
import com.syj.eplus.module.scm.controller.admin.concessionrelease.vo.ConcessionReleasePageReqVO;
import com.syj.eplus.module.scm.controller.admin.concessionrelease.vo.ConcessionReleaseRespVO;
import com.syj.eplus.module.scm.controller.admin.concessionrelease.vo.ConcessionReleaseSaveReqVO;
import com.syj.eplus.module.scm.convert.concessionrelease.ConcessionReleaseConvert;
import com.syj.eplus.module.scm.dal.dataobject.concessionrelease.ConcessionReleaseDO;
import com.syj.eplus.module.scm.dal.dataobject.purchasecontract.PurchaseContractDO;
import com.syj.eplus.module.scm.dal.mysql.concessionrelease.ConcessionReleaseMapper;
import com.syj.eplus.module.scm.dal.mysql.purchasecontract.PurchaseContractMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.scm.enums.ErrorCodeConstants.*;

/**
 * 让步放行 Service 实现类
 *
 * @author zhangcm
 */
@Service
@Validated
public class ConcessionReleaseServiceImpl implements ConcessionReleaseService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private ConcessionReleaseMapper concessionReleaseMapper;
    @Resource
    private BpmProcessInstanceApi bpmProcessInstanceApi;
    @Resource
    private QualityInspectionApi qualityInspectionApi;
    @Resource
    private SkuApi skuApi;

    private static final String PROCESS_DEFINITION_KEY = "scm_concession_release";

    private static final String CONDITION_KEY = "annexFlag";
    @Resource
    private DeptApi deptApi;
    @Resource
    private PurchaseContractMapper purchaseContractMapper;
    @Resource
    private AdminUserApi adminUserApi;

    @Override
    public List<CreatedResponse> createConcessionRelease(ConcessionReleaseSaveReqVO createReqVO) {
        List<Long> qualityInspectionItemIdList = createReqVO.getQualityInspectionItemIdList();
        Long qualityId = qualityInspectionApi.checkItemList(qualityInspectionItemIdList);
        if(Objects.isNull(qualityId)){
            throw exception(QUALIFICATION_NOT_EXISTS);
        }

        createReqVO.setId(null);
        ConcessionReleaseDO concessionRelease = ConcessionReleaseConvert.INSTANCE.convertConcessionReleaseDO(createReqVO);
        concessionRelease.setAnnex(createReqVO.getFactoryFile());
        concessionRelease.setPicture(createReqVO.getPicture());
        concessionRelease.setAuditStatus(BpmProcessInstanceResultEnum.UNSUBMITTED.getResult());
        if(Objects.equals(createReqVO.getSubmitFlag(), BooleanEnum.YES.getValue())){
            concessionRelease.setAuditStatus(BpmProcessInstanceResultEnum.PROCESS.getResult());
        }
        concessionRelease.setQualityInspectionId(qualityId);
        concessionRelease.setQualityInspectionItemIds(createReqVO.getQualityInspectionItemIdList());
        // 插入
        concessionReleaseMapper.insert(concessionRelease);
        if (SubmitFlagEnum.SUBMIT.getStatus().equals(createReqVO.getSubmitFlag())) {
            Map<String, Object> variable = new HashMap<>(Map.of(CONDITION_KEY, 0));
            if(Objects.equals(createReqVO.getAnnexFlag(), BooleanEnum.YES.getValue())){
                variable.put(CONDITION_KEY, 1);
            }

            submitTask(concessionRelease.getId(), WebFrameworkUtils.getLoginUserId(),variable);
        }

        //回写验货单让步放行状态
        qualityInspectionApi.setConcessionReleaseStatus(concessionRelease.getQualityInspectionItemIds(),BooleanEnum.YES.getValue(),createReqVO.getFactoryFile());

        // 返回
//        return concessionRelease.getId();
        List<CreatedResponse> response = new ArrayList<>();
        response.add(new CreatedResponse(concessionRelease.getId(),""));  //TODO:让步放行没有编号
        return  response;
    }

    @Override
    public void updateConcessionRelease(ConcessionReleaseSaveReqVO updateReqVO) {
        // 校验存在
        validateConcessionReleaseExists(updateReqVO.getId());
        // 更新
        ConcessionReleaseDO updateObj = ConcessionReleaseConvert.INSTANCE.convertConcessionReleaseDO(updateReqVO);
        concessionReleaseMapper.updateById(updateObj);
        if (SubmitFlagEnum.SUBMIT.getStatus().equals(updateReqVO.getSubmitFlag())) {
            Map<String, Object> variable = new HashMap<>(Map.of(CONDITION_KEY, 0));
            if(Objects.equals(updateReqVO.getAnnexFlag(), BooleanEnum.YES.getValue())){
                variable.put(CONDITION_KEY, 1);
            }
            submitTask(updateReqVO.getId(), WebFrameworkUtils.getLoginUserId(),variable);
        }
    }

    @Override
    public void deleteConcessionRelease(Long id) {
        // 校验存在
        validateConcessionReleaseExists(id);
        // 删除
        concessionReleaseMapper.deleteById(id);
    }

    private ConcessionReleaseDO validateConcessionReleaseExists(Long id) {
        ConcessionReleaseDO concessionReleaseDO = concessionReleaseMapper.selectById(id);
        if ( concessionReleaseDO== null) {
            throw exception(CONCESSION_RELEASE_NOT_EXISTS);
        }
        return concessionReleaseDO;
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
    public void submitTask(Long concessionReleaseId, Long userId, Map<String, Object> variable) {
        // 增加验货单是否存在销售员标识
        ConcessionReleaseDO concessionReleaseDO = validateConcessionReleaseExists(concessionReleaseId);
        Integer saleFlag = 0;
        Long qualityInspectionId = concessionReleaseDO.getQualityInspectionId();
        if (Objects.nonNull(qualityInspectionId)){
            QualityInspectionRespDTO qualityInspectionRespDTO = qualityInspectionApi.getDetailById(qualityInspectionId);
            if (Objects.nonNull(qualityInspectionRespDTO)){
                UserDept sales = qualityInspectionRespDTO.getSales();
                if (Objects.nonNull(sales)&&Objects.nonNull(sales.getUserId())){
                    saleFlag =1;
                }
            }
        }
        variable.put("saleFlag", saleFlag);
        Set<Long> assigneeIds = getPurchaseManagerAssigneeByInstanceId(concessionReleaseDO.getQualityInspectionId());
        Map<String,List<Long>> assignee = new HashMap<>();
        assigneeIds.stream().findFirst().ifPresent(s->{
            assignee.put("Activity_0n2dx3d", List.of(s));
        });
        String processInstanceId = bpmProcessInstanceApi.createProcessInstance(userId, PROCESS_DEFINITION_KEY, concessionReleaseId, variable, assignee);
        updateAuditStatus(concessionReleaseId, BpmProcessInstanceResultEnum.PROCESS.getResult(),processInstanceId);
    }

    @Override
    public void updateAuditStatus(Long auditableId, Integer auditStatus,String processInstanceId) {
        ConcessionReleaseDO concessionReleaseDO = validateConcessionReleaseExists(auditableId);
        concessionReleaseDO.setAuditStatus(auditStatus);
        if (StrUtil.isNotEmpty(processInstanceId)){
            concessionReleaseDO.setProcessInstanceId(processInstanceId);
        }
        concessionReleaseMapper.updateById(concessionReleaseDO);
        //审核通过回写验货单状态
        if(Objects.equals(BpmProcessInstanceResultEnum.APPROVE.getResult(), auditStatus)){
            qualityInspectionApi.setStatus(concessionReleaseDO.getQualityInspectionItemIds(),concessionReleaseDO.getAnnex(),concessionReleaseDO.getDescription(),concessionReleaseDO.getPicture());
        }
        //审核驳回回写验货单让步放行
        if(Objects.equals(BpmProcessInstanceResultEnum.REJECT.getResult(), auditStatus) || Objects.equals(BpmProcessInstanceResultEnum.CANCEL.getResult(), auditStatus)){
            qualityInspectionApi.setConcessionReleaseStatus(concessionReleaseDO.getQualityInspectionItemIds(), BooleanEnum.NO.getValue(),null);
        }
    }

    @Override
    public ConcessionReleaseDO getConcessionReleaseById(Long id) {
        return concessionReleaseMapper.selectById(id);
    }

    @Override
    public UserDept getAssigneeByInstanceId(String instanceId) {
        if (StrUtil.isEmpty(instanceId)){
            return null;
        }
        List<ConcessionReleaseDO> concessionReleaseDOS = concessionReleaseMapper.selectList(ConcessionReleaseDO::getProcessInstanceId, instanceId);
        if (CollUtil.isEmpty(concessionReleaseDOS)){
            return null;
        }
        Optional<ConcessionReleaseDO> releaseDOOptional = concessionReleaseDOS.stream().findFirst();
        if (!releaseDOOptional.isPresent()){
            return null;
        }
        ConcessionReleaseDO releaseDO = releaseDOOptional.get();
        Long qualityInspectionId = releaseDO.getQualityInspectionId();
        if (Objects.isNull(qualityInspectionId)){
            return null;
        }
        QualityInspectionRespDTO qualityInspectionRespDTO = qualityInspectionApi.getDetailById(qualityInspectionId);
        if (Objects.isNull(qualityInspectionRespDTO)){
            return null;
        }
        return qualityInspectionRespDTO.getSales();
    }

    @Override
    public Set<Long> getManagerAssigneeByInstanceId(String instanceId) {
        UserDept sales = getAssigneeByInstanceId(instanceId);
        if (Objects.isNull(sales)){
            return null;
        }
        Long deptId = sales.getDeptId();
        if (Objects.isNull(deptId)){
            return null;
        }
        return deptApi.getParentLeaderUserIds(deptId);
    }

    @Override
    @DataPermission(enable = false)
    public Set<Long> getPurchaseManagerAssigneeByInstanceId(Long qualityInspectionId) {
        Set<Long> result = new HashSet<>();
        if (Objects.isNull(qualityInspectionId)){
            return result;
        }
        QualityInspectionRespDTO qualityInspectionRespDTO = qualityInspectionApi.getDetailById(qualityInspectionId);
        if (Objects.isNull(qualityInspectionRespDTO)){
            return result;
        }
        Long purchaseContractId = qualityInspectionRespDTO.getPurchaseContractId();
        if (Objects.isNull(purchaseContractId)){
            return result;
        }
        List<PurchaseContractDO> purchaseContractDOS = purchaseContractMapper.selectList(new LambdaQueryWrapperX<PurchaseContractDO>().select(PurchaseContractDO::getPurchaseUserId).eq(PurchaseContractDO::getId, purchaseContractId));
        if (CollUtil.isEmpty(purchaseContractDOS)){
            return result;
        }
        Optional<PurchaseContractDO> first = purchaseContractDOS.stream().findFirst();
        first.ifPresent(s->{
            Long leaderId = adminUserApi.getNextDeptLeaderIdByUserId(s.getPurchaseUserId());
            if (Objects.nonNull(leaderId)){
                result.add(leaderId);
            }
        });
        return result;
    }
    @Override
    public String getProcessDefinitionKey() {
        return PROCESS_DEFINITION_KEY;
    }
    @Override
    public ConcessionReleaseRespVO getConcessionRelease( ConcessionReleaseDetailReq concessionReleaseDetailReq) {
        Long concessionReleaseId = Objects.nonNull(concessionReleaseDetailReq.getProcessInstanceId()) ? bpmProcessInstanceApi.selectAuditAbleIdByProcessInstanceId(concessionReleaseDetailReq.getProcessInstanceId()) : concessionReleaseDetailReq.getConcessionReleaseId();
        if (Objects.isNull(concessionReleaseId)) {
            logger.error("[让步放行]未获取到让步放行id");
            return null;
        }
        ConcessionReleaseDO concessionReleaseDO = concessionReleaseMapper.selectById(concessionReleaseId);
        if (concessionReleaseDO == null) {
            return null;
        }
        QualityInspectionRespDTO qualityInspectionRespDTO = qualityInspectionApi.getDetailById(concessionReleaseDO.getQualityInspectionId());
        if(Objects.isNull(qualityInspectionRespDTO)){
            qualityInspectionRespDTO = new QualityInspectionRespDTO();
        }
        Long id = qualityInspectionRespDTO.getId();
        ConcessionReleaseRespVO concessionReleaseRespVO =  BeanUtils.toBean(qualityInspectionRespDTO,ConcessionReleaseRespVO.class);
        concessionReleaseRespVO.setId(id);  //对象转换id覆盖
        concessionReleaseRespVO.setOperateLogRespDTOList(qualityInspectionRespDTO.getOperateLogRespDTOList());
        concessionReleaseRespVO.setReworkPicture(qualityInspectionRespDTO.getReworkPicture());
        List<QualityInspectionItemRespDTO> children = qualityInspectionRespDTO.getChildren();
        if(CollUtil.isEmpty(children)){
            throw exception(QUOTE_ITEM_NOT_EXISTS_DEFAULT);
        }
        List<QualityInspectionItemRespDTO> itemList = qualityInspectionRespDTO.getChildren().stream().filter(s -> concessionReleaseDO.getQualityInspectionItemIds().contains(s.getId())).toList();
        concessionReleaseRespVO.setChildren(itemList);
        concessionReleaseRespVO.setQualityInspectionId(concessionReleaseDO.getQualityInspectionId());
        concessionReleaseRespVO.setAnnexFlag(concessionReleaseDO.getAnnexFlag());
        concessionReleaseRespVO.setConcessionReleaseAnnex(concessionReleaseDO.getAnnex());
        concessionReleaseRespVO.setAuditStatus(concessionReleaseDO.getAuditStatus());
        concessionReleaseRespVO.setDescription(concessionReleaseDO.getDescription());
        concessionReleaseRespVO.setProcessInstanceId(concessionReleaseDO.getProcessInstanceId());
        concessionReleaseRespVO.setPicture(concessionReleaseDO.getPicture());
        return concessionReleaseRespVO;
    }

    @Override
    public PageResult<ConcessionReleaseRespVO> getConcessionReleasePage(ConcessionReleasePageReqVO pageReqVO) {
        List<QualityInspectionRespDTO> dtoList  = qualityInspectionApi.getList(BeanUtils.toBean(pageReqVO,QualityInspectionReqDTO.class));
        if(CollUtil.isEmpty(dtoList)){
            return PageResult.empty();
        }
        // 提取所有 DB 对象中的 m 字段
        List<Long> skuIdList = dtoList.stream().flatMap(doItem -> doItem.getChildren().stream()).map(QualityInspectionItemRespDTO::getSkuId).toList();
        Map<Long, SimpleSkuDTO> simpleSkuDTOMap = skuApi.getSimpleSkuDTOMap(skuIdList);
        dtoList.forEach(x->{
            x.getChildren().forEach(i -> {
                if (CollUtil.isNotEmpty(simpleSkuDTOMap)) {
                    SimpleSkuDTO simpleSkuDTO = simpleSkuDTOMap.get(i.getSkuId());
                    if (Objects.nonNull(simpleSkuDTO)) {
                        i.setMainPicture(simpleSkuDTO.getMainPicture());
                    }
                }
            });
        });

        pageReqVO.setQualityInspectionIdList(dtoList.stream().map(QualityInspectionRespDTO::getId).distinct().toList());
        PageResult<ConcessionReleaseDO> concessionReleaseDOPageResult = concessionReleaseMapper.selectPage(pageReqVO);
        List<ConcessionReleaseDO> list = concessionReleaseDOPageResult.getList();
        if(CollUtil.isEmpty(list)){
            return PageResult.empty();
        }
        List<ConcessionReleaseRespVO> voList = new ArrayList<>();
        Map<Long, QualityInspectionRespDTO> dtoMap = dtoList.stream().collect(Collectors.toMap(QualityInspectionRespDTO::getId, s -> s, (s1, s2) -> s1));
        if(CollUtil.isNotEmpty(dtoMap)){
            list.forEach(s->{
                QualityInspectionRespDTO qualityInspectionRespDTO = dtoMap.get(s.getQualityInspectionId());
                if(Objects.isNull(qualityInspectionRespDTO)){
                    qualityInspectionRespDTO = new QualityInspectionRespDTO();
                }
                Long id = s.getId();
                ConcessionReleaseRespVO concessionReleaseRespVO =  BeanUtils.toBean(qualityInspectionRespDTO,ConcessionReleaseRespVO.class);
                concessionReleaseRespVO.setId(id);  //对象转换id覆盖
                concessionReleaseRespVO.setOperateLogRespDTOList(qualityInspectionRespDTO.getOperateLogRespDTOList());
                concessionReleaseRespVO.setReworkPicture(qualityInspectionRespDTO.getReworkPicture());
                List<QualityInspectionItemRespDTO> children = qualityInspectionRespDTO.getChildren();
                if(CollUtil.isEmpty(children)){
                    throw exception(QUOTE_ITEM_NOT_EXISTS_DEFAULT);
                }
                List<QualityInspectionItemRespDTO> itemList = qualityInspectionRespDTO.getChildren().stream().filter(si ->Objects.nonNull( s.getQualityInspectionItemIds()) && s.getQualityInspectionItemIds().contains(si.getId())).toList();
                concessionReleaseRespVO.setChildren(itemList);
                concessionReleaseRespVO.setQualityInspectionId(s.getQualityInspectionId());
                concessionReleaseRespVO.setAnnexFlag(s.getAnnexFlag());
                concessionReleaseRespVO.setConcessionReleaseAnnex(s.getAnnex());
                concessionReleaseRespVO.setAuditStatus(s.getAuditStatus());
                concessionReleaseRespVO.setDescription(s.getDescription());
                //处理流程id
                String bpmProcessInstanceId = bpmProcessInstanceApi.getBpmProcessInstanceId(s.getId(),  PROCESS_DEFINITION_KEY);
                if (StrUtil.isNotEmpty(bpmProcessInstanceId)) {
                    concessionReleaseRespVO.setProcessInstanceId(bpmProcessInstanceId);
                }
                voList.add(concessionReleaseRespVO);
            });
        }
        return new PageResult<ConcessionReleaseRespVO>().setList(voList).setTotal((long)voList.size());
    }

}
