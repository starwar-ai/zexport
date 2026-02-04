package com.syj.eplus.module.exms.service.exhibition;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.operatelog.core.util.OperateCompareUtil;
import cn.iocoder.yudao.framework.operatelog.core.util.OperateLogUtils;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.bpm.api.task.BpmProcessInstanceApi;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import cn.iocoder.yudao.module.bpm.enums.task.BpmProcessInstanceResultEnum;
import cn.iocoder.yudao.module.system.api.dept.DeptApi;
import cn.iocoder.yudao.module.system.api.dept.dto.DeptRespDTO;
import com.syj.eplus.framework.common.entity.ChangeRecord;
import com.syj.eplus.framework.common.enums.ChangeRecordTypeEnum;
import com.syj.eplus.framework.common.enums.ExmsExpoStatusEnum;
import com.syj.eplus.framework.common.enums.SubmitFlagEnum;
import com.syj.eplus.module.exms.controller.admin.exhibition.vo.*;
import com.syj.eplus.module.exms.convert.exhibition.ExhibitionConvert;
import com.syj.eplus.module.exms.dal.dataobject.exhibition.ExhibitionDO;
import com.syj.eplus.module.exms.dal.mysql.exhibition.ExhibitionMapper;
import com.syj.eplus.module.infra.api.CodeGeneratorApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.exms.enums.ErrorCodeConstants.EXHIBITION_DATE_NOT_EXISTS;
import static com.syj.eplus.module.exms.enums.ErrorCodeConstants.EXHIBITION_NOT_EXISTS;

/**
 * 展会 Service 实现类
 *
 * @author zhangcm
 */
@Service
@Validated
public class ExhibitionServiceImpl implements ExhibitionService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    @Resource
    private CodeGeneratorApi codeGeneratorApi;
    @Resource
    private ExhibitionMapper exhibitionMapper;
    @Resource
    private BpmProcessInstanceApi bpmProcessInstanceApi;

    @Resource
    private DeptApi deptApi;

    private static final String PROCESS_DEFINITION_KEY = "exms_exhibition";

    private static final String CODE_PREFIX = "EXE";
    public static final String SN_TYPE = "SN_EXMS_EXHIBITION";
    public static final String OPERATOR_EXTS_KEY = "ExmsExhibitionCode";
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createExhibition(ExhibitionSaveReqVO createReqVO) {
        if(Objects.nonNull(createReqVO.getPlanDate()) && createReqVO.getPlanDate().length>=2){
            createReqVO.setPlanStartDate(createReqVO.getPlanDate()[0]);
            createReqVO.setPlanEndDate(createReqVO.getPlanDate()[1]);
        }

        ExhibitionDO exhibition = ExhibitionConvert.INSTANCE.convertExhibitionDO(createReqVO);
        exhibition.setId(null);
        exhibition.setExpoStatus(ExmsExpoStatusEnum.PENDING_SUBMIT.getCode());
        exhibition.setAuditStatus(BpmProcessInstanceResultEnum.UNSUBMITTED.getResult());
        String categoryCode = codeGeneratorApi.getCodeGenerator(SN_TYPE, CODE_PREFIX);
        exhibition.setCode(categoryCode);
        // 设置排序值为最大值+10
        Integer maxOrderNum = exhibitionMapper.selectMaxOrderNum();
        exhibition.setOrderNum(maxOrderNum + 10);
        exhibitionMapper.insert(exhibition);
        if (SubmitFlagEnum.SUBMIT.getStatus().equals(createReqVO.getSubmitFlag())) {
            submitTask(exhibition.getId(), WebFrameworkUtils.getLoginUserId());
        }

        // 补充操作日志明细
        OperateLogUtils.setContent(String.format("【新增展会】%s", categoryCode));
        OperateLogUtils.addExt(OPERATOR_EXTS_KEY, categoryCode);
        // 返回
        return exhibition.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateExhibition(ExhibitionSaveReqVO updateReqVO) {
        if(Objects.nonNull(updateReqVO.getPlanDate()) && updateReqVO.getPlanDate().length>=2){
            updateReqVO.setPlanStartDate(updateReqVO.getPlanDate()[0]);
            updateReqVO.setPlanEndDate(updateReqVO.getPlanDate()[1]);
        }
        // 校验存在
        ExhibitionDO exhibitionDO = validateExhibitionExists(updateReqVO.getId());
        // 更新
        ExhibitionDO updateObj = ExhibitionConvert.INSTANCE.convertExhibitionDO(updateReqVO);


        exhibitionMapper.updateById(updateObj);
        if (SubmitFlagEnum.SUBMIT.getStatus().equals(updateReqVO.getSubmitFlag())) {
            submitTask(updateReqVO.getId(), WebFrameworkUtils.getLoginUserId());
        }

        //操作日志
        {
            List<ChangeRecord> changeRecords = new OperateCompareUtil<ExhibitionDO>().compare(exhibitionDO, updateObj);

            if (CollUtil.isNotEmpty(changeRecords)) {
                OperateCompareUtil<Object> operateCompareUtil = new OperateCompareUtil<>();
                AtomicReference<String> content = new AtomicReference<>();
                changeRecords.forEach(s -> {
                    content.set(operateCompareUtil.apply(content.get(), s.getFieldName(), s.getOldValue(), s.getValue(), ChangeRecordTypeEnum.GENERAL_CHANGE.getType()));
                });
                // 插入操作日志
                OperateLogUtils.setContent(content.get());
            }
            OperateLogUtils.addExt(OPERATOR_EXTS_KEY, updateReqVO.getCode());
        }
    }

    @Override
    public void deleteExhibition(Long id) {
        // 校验存在
        validateExhibitionExists(id);
        // 删除
        exhibitionMapper.deleteById(id);
    }

    private ExhibitionDO validateExhibitionExists(Long id) {
        ExhibitionDO exhibitionDO = exhibitionMapper.selectById(id);
        if ( exhibitionDO == null) {
            throw exception(EXHIBITION_NOT_EXISTS);
        }
        return  exhibitionDO;
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
    public void submitTask(Long exhibitionId, Long userId) {
        bpmProcessInstanceApi.createProcessInstance(userId, PROCESS_DEFINITION_KEY, exhibitionId);
        updateAuditStatus(exhibitionId, BpmProcessInstanceResultEnum.PROCESS.getResult());

    }

    @Override
    public void updateAuditStatus(Long auditableId, Integer auditStatus) {
        exhibitionMapper.updateById(ExhibitionDO.builder().id(auditableId).auditStatus(auditStatus).build());
        if(auditStatus.equals(BpmProcessInstanceResultEnum.PROCESS.getResult())){
            exhibitionMapper.updateById(
                    ExhibitionDO.builder().id(auditableId)
                            .expoStatus(ExmsExpoStatusEnum.PENDING_APPROVE.getCode())
                            .build());
        }
        if(auditStatus.equals(BpmProcessInstanceResultEnum.APPROVE.getResult())){
            exhibitionMapper.updateById(
                    ExhibitionDO.builder().id(auditableId)
                            .expoStatus(ExmsExpoStatusEnum.PROJECT.getCode())
                            .build());
        }
        if(auditStatus.equals(BpmProcessInstanceResultEnum.REJECT.getResult())){
            exhibitionMapper.updateById(
                    ExhibitionDO.builder().id(auditableId)
                            .expoStatus(ExmsExpoStatusEnum.REJECT.getCode())
                            .build());
        }
        if(auditStatus.equals(BpmProcessInstanceResultEnum.CANCEL.getResult())){
            exhibitionMapper.updateById(
                    ExhibitionDO.builder().id(auditableId)
                            .expoStatus(ExmsExpoStatusEnum.PENDING_SUBMIT.getCode())
                            .build());
        }
    }



    @Override
    public void finishManufacture(Long id ) {
        ExhibitionDO exhibitionDO = exhibitionMapper.selectOne(ExhibitionDO::getId,id);
        if(exhibitionDO == null){
            throw exception(EXHIBITION_NOT_EXISTS);
        }
        exhibitionDO.setExpoStatus(ExmsExpoStatusEnum.FINISH.getCode()).setFinishTime(LocalDateTime.now());
        exhibitionMapper.updateById(exhibitionDO);
    }

    @Override
    public void rollbackFinishManufacture(Long id) {
        ExhibitionDO exhibitionDO = exhibitionMapper.selectOne(ExhibitionDO::getId,id);
        if(exhibitionDO == null){
            throw exception(EXHIBITION_NOT_EXISTS);
        }
        exhibitionDO.setExpoStatus(ExmsExpoStatusEnum.PENDING_SUBMIT.getCode()).setFinishTime(null);
        exhibitionMapper.updateById(exhibitionDO);
    }

    @Override
    public void doneManufacture(ExhibitionDoneReqVO reqVo ) {
        ExhibitionDO exhibitionDO = exhibitionMapper.selectOne(ExhibitionDO::getId,reqVo.getId());
        if(exhibitionDO == null){
            throw exception(EXHIBITION_NOT_EXISTS);
        }
        if(reqVo.getDate() == null || reqVo.getDate().length < 2){
            throw exception(EXHIBITION_DATE_NOT_EXISTS);
        }
        exhibitionDO.setStartDate(reqVo.getDate()[0]).setEndDate(reqVo.getDate()[1]);
        exhibitionDO.setExpoStatus(ExmsExpoStatusEnum.DONE.getCode()).setDoneTime(LocalDateTime.now());
        exhibitionMapper.updateById(exhibitionDO);
    }

    @Override
    public PageResult<ExhibitionSimpleRespVO> getSimpleExhibitionPage(ExhibitionPageReqVO pageReqVO) {
        PageResult<ExhibitionDO> exhibitionDOPageResult = exhibitionMapper.selectPage(pageReqVO);
        List<ExhibitionDO> list = exhibitionDOPageResult.getList();
        if(CollUtil.isEmpty(list)){
            return new PageResult<ExhibitionSimpleRespVO>().setList(null).setTotal((long)0);
        }
        List<ExhibitionSimpleRespVO> voList = BeanUtils.toBean(list, ExhibitionSimpleRespVO.class);
        return new PageResult<ExhibitionSimpleRespVO>().setList(voList).setTotal((long)voList.size());
    }

    @Override
    public String getProcessDefinitionKey() {
        return PROCESS_DEFINITION_KEY;
    }
    @Override
    public ExhibitionRespVO getExhibition( ExhibitionDetailReq exhibitionDetailReq) {
        Long exhibitionId = Objects.nonNull(exhibitionDetailReq.getProcessInstanceId()) ? bpmProcessInstanceApi.selectAuditAbleIdByProcessInstanceId(exhibitionDetailReq.getProcessInstanceId()) : exhibitionDetailReq.getExhibitionId();
        if (Objects.isNull(exhibitionId)) {
            logger.error("[展会]未获取到展会id");
            return null;
        }
        ExhibitionDO exhibitionDO = exhibitionMapper.selectById(exhibitionId);
        if (exhibitionDO == null) {
            return null;
        }
        ExhibitionRespVO exhibitionRespVO = ExhibitionConvert.INSTANCE.convertExhibitionRespVO(exhibitionDO);
        exhibitionRespVO.setPlanDate(new LocalDateTime[]{
                exhibitionRespVO.getPlanStartDate(),
                exhibitionRespVO.getPlanEndDate()
        });
        if(CollUtil.isNotEmpty(exhibitionRespVO.getDeptList())){
            List<DeptRespDTO> deptList = deptApi.getDeptList(exhibitionRespVO.getDeptList());
            exhibitionRespVO.setDeptMsgList(deptList);
        }

        String bpmProcessInstanceId = bpmProcessInstanceApi.getBpmProcessInstanceId(exhibitionDO.getId(), PROCESS_DEFINITION_KEY);
        if (StrUtil.isNotEmpty(bpmProcessInstanceId)) {
            exhibitionRespVO.setProcessInstanceId(bpmProcessInstanceId);
        }

        return exhibitionRespVO;
    }

    @Override
    public PageResult<ExhibitionRespVO> getExhibitionPage(ExhibitionPageReqVO pageReqVO) {
        PageResult<ExhibitionDO> exhibitionDOPageResult = exhibitionMapper.selectPage(pageReqVO);
        List<ExhibitionDO> list = exhibitionDOPageResult.getList();
        if(CollUtil.isEmpty(list)){
            return new PageResult<ExhibitionRespVO>().setList(null).setTotal((long)0);
        }
        List<ExhibitionRespVO> voList = BeanUtils.toBean(list, ExhibitionRespVO.class);
        voList.forEach(s->{
            s.setPlanDate(new LocalDateTime[]{
                    s.getPlanStartDate(),
                    s.getPlanEndDate()
            });
            if(CollUtil.isNotEmpty(s.getDeptList())){
                List<DeptRespDTO> deptList = deptApi.getDeptList(s.getDeptList());
                s.setDeptMsgList(deptList);
            }
        });

        return new PageResult<ExhibitionRespVO>().setList(voList).setTotal((long)voList.size());
    }

}