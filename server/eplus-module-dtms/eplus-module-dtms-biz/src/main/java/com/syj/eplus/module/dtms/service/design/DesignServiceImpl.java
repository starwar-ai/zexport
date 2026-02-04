package com.syj.eplus.module.dtms.service.design;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;
import cn.iocoder.yudao.framework.operatelog.core.util.OperateLogUtils;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.bpm.api.task.BpmProcessInstanceApi;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRespDTO;
import cn.iocoder.yudao.module.bpm.enums.task.BpmProcessInstanceResultEnum;
import cn.iocoder.yudao.module.system.api.logger.OperateLogApi;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.framework.common.enums.BooleanEnum;
import com.syj.eplus.framework.common.enums.SpecialPermissionFlagEnum;
import com.syj.eplus.framework.common.enums.SubmitFlagEnum;
import com.syj.eplus.module.dtms.controller.admin.design.vo.DesignDetailReq;
import com.syj.eplus.module.dtms.controller.admin.design.vo.DesignPageReqVO;
import com.syj.eplus.module.dtms.controller.admin.design.vo.DesignRespVO;
import com.syj.eplus.module.dtms.controller.admin.design.vo.DesignSaveReqVO;
import com.syj.eplus.module.dtms.controller.admin.designitem.vo.DesignItemRespVO;
import com.syj.eplus.module.dtms.controller.admin.designitem.vo.DesignItemSaveReqVO;
import com.syj.eplus.module.dtms.controller.admin.designsummary.vo.DesignSummaryRespVO;
import com.syj.eplus.module.dtms.convert.design.DesignConvert;
import com.syj.eplus.module.dtms.dal.dataobject.design.DesignDO;
import com.syj.eplus.module.dtms.dal.dataobject.designitem.DesignItemDO;
import com.syj.eplus.module.dtms.dal.dataobject.designsummary.DesignSummaryDO;
import com.syj.eplus.module.dtms.dal.mysql.design.DesignMapper;
import com.syj.eplus.module.dtms.dal.mysql.designitem.DesignItemMapper;
import com.syj.eplus.module.dtms.enums.DesignStatusEnum;
import com.syj.eplus.module.dtms.service.designitem.DesignItemService;
import com.syj.eplus.module.dtms.service.designsummary.DesignSummaryService;
import com.syj.eplus.module.infra.api.CodeGeneratorApi;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.dtms.api.ErrorCodeConstants.*;

/**
 * 设计-任务单 Service 实现类
 *
 * @author ePlus
 */
@Service
@Validated
public class DesignServiceImpl extends ServiceImpl<DesignMapper, DesignDO> implements DesignService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private DesignMapper designMapper;

    @Resource
    private DesignItemMapper designItemMapper;
    @Resource
    private CodeGeneratorApi codeGeneratorApi;
    @Resource
    private BpmProcessInstanceApi bpmProcessInstanceApi;

    @Resource
    @Lazy
    private DesignItemService designItemService;
    @Resource
    @Lazy
    private DesignSummaryService designSummaryService;
    @Resource
    private AdminUserApi adminUserApi;
    @Resource
    private OperateLogApi operateLogApi;

    private static final String SN_TYPE = "dtms_design";
    private static final String CODE_PREFIX = "DES";
    private static final String PROCESS_DEFINITION_KEY = "dtms_design";

    public static final String OPERATOR_EXTS_KEY = "designCode";


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createDesign(DesignSaveReqVO createReqVO) {
        AdminUserRespDTO currentUser = adminUserApi.getUser(WebFrameworkUtils.getLoginUserId());
        DesignDO design = DesignConvert.INSTANCE.convertDesignDO(createReqVO);
        // 生成 设计-任务单 编号
        String code = codeGeneratorApi.getCodeGenerator(SN_TYPE, CODE_PREFIX);
        design.setCode(code);
        design.setAuditStatus(BpmProcessInstanceResultEnum.UNSUBMITTED.getResult());
        design.setDesignStatus(DesignStatusEnum.UN_SUBMIT.getValue());
        // 插入
        designMapper.insert(design);
        Long id = design.getId();
        //是否补单为是
        if(Objects.nonNull(createReqVO.getIsSupplementOrder()) && createReqVO.getIsSupplementOrder().equals(BooleanEnum.YES.getValue())){
            if(CollectionUtils.isEmpty(createReqVO.getSpecificDesigners())){
                throw exception(SPECIFIC_DESIGNER_NOT_EXISTS);
            }
            List<DesignItemSaveReqVO> designItemSaveReqVOList = new ArrayList<>();
            createReqVO.getSpecificDesigners().stream().forEach(item->{
                DesignItemSaveReqVO designItemSaveReqVO = new DesignItemSaveReqVO();
                designItemSaveReqVO.setDesignId(id);
                designItemSaveReqVO.setDesignerId(item.getUserId());
                designItemSaveReqVO.setDesignerName(item.getNickname());
                designItemSaveReqVO.setDesignerDeptName(item.getDeptName());
                designItemSaveReqVO.setDesignerDeptId(item.getDeptId());
                designItemSaveReqVO.setPlanStartDate(LocalDateTime.now());
                designItemSaveReqVO.setPlanCompleteDate(createReqVO.getExpectCompleteDate());
                designItemSaveReqVOList.add(designItemSaveReqVO);
            });
            designItemService.createDesignItem(designItemSaveReqVOList);
            List<Long> designerIdList = createReqVO.getSpecificDesigners().stream().map(UserDept::getUserId).toList();
            design.setDesignerIds(StringUtils.join(designerIdList,","));
        }else{
            design.setSpecificDesigners(new ArrayList<>());
            design.setDesignerIds(null);
        }
        if (SubmitFlagEnum.SUBMIT.getStatus().equals(createReqVO.getSubmitFlag())) {
            submitTask(id, WebFrameworkUtils.getLoginUserId());
        }
        // 补充操作日志明细
        OperateLogUtils.setContent(String.format("【设计任务】%s 新增任务 %s", currentUser.getNickname(),design.getName()));
        OperateLogUtils.addExt(OPERATOR_EXTS_KEY, code);
        // 返回
        return id;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDesign(DesignSaveReqVO updateReqVO) {
        // 校验存在
        validateDesignExists(updateReqVO.getId());
        // 更新
        DesignDO updateObj = DesignConvert.INSTANCE.convertDesignDO(updateReqVO);
        //是否补单为是
        if(Objects.nonNull(updateReqVO.getIsSupplementOrder()) && updateReqVO.getIsSupplementOrder().equals(BooleanEnum.YES.getValue())){
            if(CollectionUtils.isEmpty(updateReqVO.getSpecificDesigners())){
                throw exception(SPECIFIC_DESIGNER_NOT_EXISTS);
            }
            List<DesignItemSaveReqVO> designItemSaveReqVOList = new ArrayList<>();
            updateReqVO.getSpecificDesigners().stream().forEach(item->{
                DesignItemSaveReqVO designItemSaveReqVO = new DesignItemSaveReqVO();
                designItemSaveReqVO.setDesignId(updateReqVO.getId());
                designItemSaveReqVO.setDesignerId(item.getUserId());
                designItemSaveReqVO.setDesignerName(item.getNickname());
                designItemSaveReqVO.setDesignerDeptName(item.getDeptName());
                designItemSaveReqVO.setDesignerDeptId(item.getDeptId());
                designItemSaveReqVO.setPlanStartDate(LocalDateTime.now());
                designItemSaveReqVO.setPlanCompleteDate(updateReqVO.getExpectCompleteDate());
                designItemSaveReqVOList.add(designItemSaveReqVO);
            });
            designItemService.createDesignItem(designItemSaveReqVOList);
            List<Long> designerIdList = updateReqVO.getSpecificDesigners().stream().map(UserDept::getUserId).toList();
            updateObj.setDesignerIds(StringUtils.join(designerIdList,","));
        }else{
            updateObj.setSpecificDesigners(new ArrayList<>());
            updateObj.setDesignerIds(null);
        }
        designMapper.updateById(updateObj);
        if (SubmitFlagEnum.SUBMIT.getStatus().equals(updateReqVO.getSubmitFlag())) {
            submitTask(updateObj.getId(), WebFrameworkUtils.getLoginUserId());
        }
    }

    @Override
    public void deleteDesign(Long id) {
        // 校验存在
        validateDesignExists(id);
        // 删除
        designMapper.deleteById(id);
    }

    private void validateDesignExists(Long id) {
        if (designMapper.selectById(id) == null) {
            throw exception(DESIGN_NOT_EXISTS);
        }
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
    public void submitTask(Long designId, Long userId) {
        AdminUserRespDTO currentUser = adminUserApi.getUser(WebFrameworkUtils.getLoginUserId());
        DesignDO designDO = super.getById(designId);
        Integer specialPermissionFlag = designDO.getSpecialPermissionFlag();
        if (SpecialPermissionFlagEnum.SPECIAL_PERMISSION.getStatus().equals(specialPermissionFlag)) {
            String processInstanceId = bpmProcessInstanceApi.createProcessInstance(userId, PROCESS_DEFINITION_KEY, designId);
            BpmTaskRespDTO bpmTask = bpmProcessInstanceApi.getBpmTask(processInstanceId);
            Long assigneeUserId = bpmTask.getApprover() != null ? bpmTask.getApprover().getUserId() : null;
            // 更新设计任务单状态
            designMapper.updateById(DesignDO.builder().id(designId).auditId(assigneeUserId).auditStatus(BpmProcessInstanceResultEnum.PROCESS.getResult()).designStatus(DesignStatusEnum.IN_AUDIT.getValue()).build());

            OperateLogUtils.setContent(String.format("【设计任务】%s 提交了任务", currentUser.getNickname()));
            OperateLogUtils.addExt(OPERATOR_EXTS_KEY, designDO.getCode());
        } else {
            designMapper.updateById(DesignDO.builder().id(designId).auditStatus(BpmProcessInstanceResultEnum.APPROVE.getResult()).designStatus(DesignStatusEnum.IN_PROCESS.getValue()).build());
        }
    }

    @Override
    public List<DesignDO> getAllList() {
        return designMapper.selectList();
    }

    @Override
    public String getProcessDefinitionKey() {
        return PROCESS_DEFINITION_KEY;
    }

    @Override
    @DataPermission(enable = false)
    public DesignRespVO getDesign(DesignDetailReq designDetailReq) {
        Long designId = Objects.nonNull(designDetailReq.getProcessInstanceId()) ? bpmProcessInstanceApi.selectAuditAbleIdByProcessInstanceId(designDetailReq.getProcessInstanceId()) : designDetailReq.getDesignId();
        if (Objects.isNull(designId)) {
            logger.error("[设计-任务单]未获取到设计-任务单id");
            return null;
        }
        DesignDO designDO = designMapper.selectById(designId);
        if (designDO == null) {
            return null;
        }
        DesignRespVO designRespVO = DesignConvert.INSTANCE.convertDesignRespVO(designDO);
        // 任务id
        String bpmProcessInstanceId = bpmProcessInstanceApi.getBpmProcessInstanceId(designId, PROCESS_DEFINITION_KEY);
        if (StrUtil.isNotEmpty(bpmProcessInstanceId)) {
            designRespVO.setProcessInstanceId(bpmProcessInstanceId);
        }
        // 认领记录
        LambdaQueryWrapper<DesignItemDO> itemQueryWrapper = new LambdaQueryWrapper<>();
        itemQueryWrapper.eq(DesignItemDO::getDesignId, designId);
        List<DesignItemDO> designItemDOList = designItemService.list(itemQueryWrapper);
        designRespVO.setDesignItemRespVOList(BeanUtils.toBean(designItemDOList, DesignItemRespVO.class));
        // 工作总结
        LambdaQueryWrapper<DesignSummaryDO> summaryQueryWrapper = new LambdaQueryWrapper<>();
        summaryQueryWrapper.eq(DesignSummaryDO::getDesignId, designId);
        List<DesignSummaryDO> summaryDOList = designSummaryService.list(summaryQueryWrapper);
        designRespVO.setSummaryRespVOList(BeanUtils.toBean(summaryDOList, DesignSummaryRespVO.class));

        // 获得操作日志信息
        designRespVO.setOperateLogRespDTOList(operateLogApi.getOperateLogRespDTOList(OPERATOR_EXTS_KEY, designDO.getCode()));
        return designRespVO;
    }

    @Override
    public PageResult<DesignRespVO> getDesignPage(DesignPageReqVO pageReqVO) {
        PageResult<DesignDO> designDOPageResult = designMapper.selectPage(pageReqVO);
        List<DesignDO> designDOList = designDOPageResult.getList();
        if (CollectionUtils.isEmpty(designDOList)) {
            return new PageResult<DesignRespVO>().setList(new ArrayList<>()).setTotal(designDOPageResult.getTotal());
        }
        List<DesignRespVO> designRespVOList = BeanUtils.toBean(designDOList, DesignRespVO.class);
        designRespVOList.stream().forEach(x -> {
            String designerIds = x.getDesignerIds();
            if (StringUtils.isNoneBlank(designerIds)) {
                List<String> userIdStrList = Arrays.asList(designerIds.split(","));
                List<Long> userIdList = userIdStrList.stream().map(Long::parseLong).toList();
                List<AdminUserRespDTO> userList = adminUserApi.getUserList(userIdList);
                x.setDesignerNames(userList.stream().map(AdminUserRespDTO::getNickname).collect(Collectors.joining(",")));
            }
        });
        return new PageResult<DesignRespVO>().setList(designRespVOList).setTotal(designDOPageResult.getTotal());
    }

    @Override
    public void cancleClaim(Long id) {
        //更新认领人
        DesignDO designDO =  designMapper.selectById(id);
        if (ObjectUtil.isEmpty(designDO)) {
            throw exception(DESIGN_NOT_EXISTS);
        }
        String designerIds = designDO.getDesignerIds();
        if(StringUtils.isEmpty(designerIds)){
            throw exception(DESIGNERID_NOT_EXISTS);
        }
        Long loginUserId = WebFrameworkUtils.getLoginUserId();
        if(designerIds.indexOf(loginUserId.toString())<0){
            throw exception(DESIGNERID_NOT_CONTAINS);
        }
        List<String> designerIdArrs = new ArrayList<String>(List.of(designerIds.split(",")));
        designerIdArrs.remove(loginUserId.toString());
        designDO.setDesignerIds(StringUtils.join(designerIdArrs,","));
        designMapper.updateById(designDO);
        //删除认领明细
        LambdaQueryWrapper<DesignItemDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DesignItemDO::getDesignId, id);
        queryWrapper.eq(DesignItemDO::getDesignerId, loginUserId);
        List<DesignItemDO> designItemDOList =  designItemMapper.selectList(queryWrapper).stream().toList();
        if(Objects.nonNull(designItemDOList)){
            List<Long> designItemIds= designItemDOList.stream().map(DesignItemDO::getId).toList();
            designItemMapper.deleteBatchIds(designItemIds);
        }else{
            throw exception(DESIGN_ITEM_NOT_EXISTS);
        }
    }

    @Override
    @DataPermission(enable = false)
    public PageResult<DesignRespVO> getClaimDesignPage(DesignPageReqVO pageReqVO) {
        PageResult<DesignDO> designDOPageResult = designMapper.selectPage(pageReqVO);
        List<DesignDO> designDOList = designDOPageResult.getList();
        if (CollectionUtils.isEmpty(designDOList)) {
            return new PageResult<DesignRespVO>().setList(new ArrayList<>()).setTotal(designDOPageResult.getTotal());
        }
        List<DesignRespVO> designRespVOList = BeanUtils.toBean(designDOList, DesignRespVO.class);
        designRespVOList.stream().forEach(x -> {
            String designerIds = x.getDesignerIds();
            if (StringUtils.isNoneBlank(designerIds)) {
                List<String> userIdStrList = Arrays.asList(designerIds.split(","));
                List<Long> userIdList = userIdStrList.stream().map(Long::parseLong).toList();
                List<AdminUserRespDTO> userList = adminUserApi.getUserList(userIdList);
                x.setDesignerNames(userList.stream().map(AdminUserRespDTO::getNickname).collect(Collectors.joining(",")));
            }
        });
        return new PageResult<DesignRespVO>().setList(designRespVOList).setTotal(designDOPageResult.getTotal());
    }

    @Override
    @DataPermission(enable = false)
    public DesignDO getDOById(Long designId) {
         return designMapper.selectById(designId);

    }

    @Override
    @DataPermission(enable = false)
    public void updateDOById(DesignDO designDO) {
        designMapper.updateById(designDO);
    }
}
