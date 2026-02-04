package com.syj.eplus.module.qms.controller.admin.qualityinspection;

import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import cn.iocoder.yudao.framework.operatelog.core.util.OperateLogUtils;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import com.syj.eplus.framework.common.entity.CreatedResponse;
import com.syj.eplus.module.qms.controller.admin.qualityinspection.vo.*;
import com.syj.eplus.module.qms.dal.dataobject.qualityinspection.QualityInspectionItemDO;
import com.syj.eplus.module.qms.enums.InspectionItemStatusEnum;
import com.syj.eplus.module.qms.service.qualityinspection.QualityInspectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.operatelog.core.enums.OperateTypeEnum.EXPORT;
import static com.syj.eplus.module.qms.service.qualityinspection.QualityInspectionServiceImpl.OPERATOR_EXTS_KEY;

@Slf4j
@Tag(name = "管理后台 - 验货单")
@RestController
@RequestMapping("/qms/quality-inspection")
@Validated
public class QualityInspectionController {

    @Resource
    private QualityInspectionService qualityInspectionService;
    @Resource
    private AdminUserApi adminUserApi;

    @PostMapping("/create")
    @Operation(summary = "创建验货单",description = "创建验货单")
    @PreAuthorize("@ss.hasPermission('qms:quality-inspection:create')")
    public CommonResult<List<CreatedResponse>> createQualityInspection(@Valid @RequestBody QualityInspectionSaveReqVO createReqVO) {
        return success(qualityInspectionService.createQualityInspection(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新验货单")
    @PreAuthorize("@ss.hasPermission('qms:quality-inspection:update')")
    public CommonResult<Boolean> updateQualityInspection(@Valid @RequestBody QualityInspectionSaveReqVO updateReqVO) {
        log.info("更新验货单，请求参数：{}",JSONUtil.toJsonStr(updateReqVO));
        return success(qualityInspectionService.updateQualityInspection(updateReqVO));
    }

    @PutMapping("/update-amount")
    @Operation(summary = "更新验货费用")
    @PreAuthorize("@ss.hasPermission('qms:quality-inspection:update')")
    public CommonResult<Boolean> updateQualityInspectionAmount(@Valid @RequestBody QualityInspectionSaveAmountReqVO updateReqVO) {
        return success(qualityInspectionService.updateQualityInspectionAmount(updateReqVO));
    }

    @PutMapping("/verification")
    @Operation(summary = "确认排验",description = "确认排验")
    @PreAuthorize("@ss.hasPermission('qms:quality-inspection:verification')")
    public CommonResult<Boolean> verification(@Valid @RequestBody QualityInspectionSaveReqVO updateReqVO) {
        log.info("更新验货单，请求参数：{}",JSONUtil.toJsonStr(updateReqVO));

        AdminUserRespDTO currentUser = adminUserApi.getUser(WebFrameworkUtils.getLoginUserId());
        // 补充操作日志明细
        OperateLogUtils.setContent(String.format("【验货单】%s 确认排验 ", currentUser.getNickname()));
        OperateLogUtils.addExt(OPERATOR_EXTS_KEY, updateReqVO.getCode());
        return success(qualityInspectionService.updateQualityInspection(updateReqVO));
    }

    @PutMapping("/uploadInspection")
    @Operation(summary = "验货",description = "验货")
    @PreAuthorize("@ss.hasPermission('qms:quality-inspection:uploadInspection')")
    public CommonResult<Boolean> uploadInspection(@Valid @RequestBody QualityInspectionSaveReqVO updateReqVO) {
        log.info("更新验货单，请求参数：{}",JSONUtil.toJsonStr(updateReqVO));
        // 补充操作日志明细
        StringBuilder stringBuilder = new StringBuilder();
        List<QualityInspectionItemSaveReqVO> itemSaveReqVOList = updateReqVO.getItemSaveReqVOList();
        if (!CollectionUtils.isEmpty(itemSaveReqVOList)) {
            itemSaveReqVOList.forEach(x->{
                String desc = x.getSkuName() + InspectionItemStatusEnum.getDescByValue(x.getInspectionStatus())+" ";
                stringBuilder.append(desc);
            });
        }
        OperateLogUtils.setContent(String.format("【验货单】%s 进行验货，验货结果： %s", updateReqVO.getInspectorName(), stringBuilder));
        OperateLogUtils.addExt(OPERATOR_EXTS_KEY, updateReqVO.getCode());
        return success(qualityInspectionService.updateQualityInspection(updateReqVO));
    }
    @PutMapping("/rework")
    @Operation(summary = "返工重验",description = "返工重验")
    @PreAuthorize("@ss.hasPermission('qms:quality-inspection:rework')")
    public CommonResult<Boolean> rework(@Valid @RequestBody QualityInspectionReworkReqVO reworkReqVO ) {
        return success(qualityInspectionService.reworkQualityInspection(reworkReqVO));
    }

//    @PutMapping("/release")
//    @Operation(summary = "让步放行")
//    @PreAuthorize("@ss.hasPermission('qms:quality-inspection:release')")
//    public CommonResult<Boolean> release(@Valid @RequestBody QualityInspectionSaveReqVO updateReqVO) {
//        log.info("更新验货单，请求参数：{}",JSONUtil.toJsonStr(updateReqVO));
//        updateReqVO.setQualityInspectionStatus(null);
//        // 补充操作日志明细
//        AdminUserRespDTO currentUser = adminUserApi.getUser(WebFrameworkUtils.getLoginUserId());
//        OperateLogUtils.setContent(String.format("【验货单】%s 让步放行 %s", currentUser.getNickname(),updateReqVO.getCode()));
//        OperateLogUtils.addExt(OPERATOR_EXTS_KEY, updateReqVO.getCode());
//
//        return success(qualityInspectionService.updateQualityInspection(updateReqVO));
//    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除验货单",description = "删除验货单")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('qms:quality-inspection:delete')")
    public CommonResult<Boolean> deleteQualityInspection(@RequestParam("id") Long id) {
        qualityInspectionService.deleteQualityInspection(id);
        return success(true);
    }

    @GetMapping("/detail")
    @Operation(summary = "获得验货单")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('qms:quality-inspection:query')")
    public CommonResult<QualityInspectionRespVO> getQualityInspection(@RequestParam("id") Long id) {
        QualityInspectionRespVO qualityInspection = qualityInspectionService.getQualityInspection(new QualityInspectionDetailReq().setQualityInspectionId(id));
        return success(qualityInspection);
    }

    @GetMapping("/audit-detail")
    @Operation(summary = "获得验货单详情")
    @Parameter(name = "id", description = "编号", required = true, example = "uuid")
    public CommonResult <QualityInspectionRespVO> getQualityInspectionByProcessId(@RequestParam("id") String id) {
        QualityInspectionRespVO qualityInspection = qualityInspectionService.getQualityInspection(new QualityInspectionDetailReq().setProcessInstanceId(id));
        return success(qualityInspection);
    }

    @GetMapping("/page")
    @Operation(summary = "获得验货单分页")
    @PreAuthorize("@ss.hasPermission('qms:quality-inspection:query')")
    public CommonResult<PageResult<QualityInspectionRespVO>> getQualityInspectionPage(@Valid QualityInspectionPageReqVO pageReqVO) {
        return success(qualityInspectionService.getQualityInspectionPage(pageReqVO));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出验货单 Excel", description = "导出验货单（支持单据维度和产品维度）")
    @PreAuthorize("@ss.hasPermission('qms:quality-inspection:export')")
    @OperateLog(type = EXPORT)
    public void exportQualityInspectionExcel(@Valid QualityInspectionPageReqVO pageReqVO,
                                             HttpServletResponse response) throws IOException {
        qualityInspectionService.exportQualityInspectionExcel(pageReqVO, response);
    }

    @PutMapping("/approve")
    @Operation(summary = "通过任务",description = "审批")
    @PreAuthorize("@ss.hasPermission('qms:quality-inspection:audit')")
    public CommonResult<Boolean> approveTask(@Valid @RequestBody BpmTaskApproveReqDTO reqVO) {
        qualityInspectionService.approveTask(WebFrameworkUtils.getLoginUserId(), reqVO);
        return success(true);
    }

    @PutMapping("/reject")
    @Operation(summary = "不通过任务",description = "审批")
    @PreAuthorize("@ss.hasPermission('qms:quality-inspection:audit')")
    public CommonResult<Boolean> rejectTask(@Valid @RequestBody BpmTaskRejectReqDTO reqVO) {
        qualityInspectionService.rejectTask(WebFrameworkUtils.getLoginUserId(), reqVO);
        return success(true);
    }

    @PutMapping("/submit")
    @Operation(summary = "提交任务",description = "审批")
    @PreAuthorize("@ss.hasPermission('qms:quality-inspection:submit')")
    public CommonResult<Boolean> submitTask(@RequestParam Long id) {
        qualityInspectionService.submitTask(id, WebFrameworkUtils.getLoginUserId());
        return success(true);
    }


    // ==================== 子表（验货单-明细） ====================

    @GetMapping("/quality-inspection-item/page")
    @Operation(summary = "获得验货单-明细分页")
    @Parameter(name = "inspectionId", description = "验货单主键")
    @PreAuthorize("@ss.hasPermission('qms:quality-inspection:query')")
    public CommonResult<PageResult<QualityInspectionItemRespVO>> getQualityInspectionItemPage(PageParam pageReqVO,
                                                                                          @RequestParam("inspectionId") Long inspectionId) {
        return success(qualityInspectionService.getQualityInspectionItemPage(pageReqVO, inspectionId));
    }

    @PostMapping("/quality-inspection-item/create")
    @Operation(summary = "创建验货单-明细")
    @PreAuthorize("@ss.hasPermission('qms:quality-inspection:create')")
    public CommonResult<Long> createQualityInspectionItem(@Valid @RequestBody QualityInspectionItemDO qualityInspectionItem) {
        return success(qualityInspectionService.createQualityInspectionItem(qualityInspectionItem));
    }

    @PutMapping("/quality-inspection-item/update")
    @Operation(summary = "更新验货单-明细")
    @PreAuthorize("@ss.hasPermission('qms:quality-inspection:update')")
    public CommonResult<Boolean> updateQualityInspectionItem(@Valid @RequestBody QualityInspectionItemDO qualityInspectionItem) {
        qualityInspectionService.updateQualityInspectionItem(qualityInspectionItem);
        return success(true);
    }

    @DeleteMapping("/quality-inspection-item/delete")
    @Parameter(name = "id", description = "编号", required = true)
    @Operation(summary = "删除验货单-明细")
    @PreAuthorize("@ss.hasPermission('qms:quality-inspection:delete')")
    public CommonResult<Boolean> deleteQualityInspectionItem(@RequestParam("id") Long id) {
        qualityInspectionService.deleteQualityInspectionItem(id);
        return success(true);
    }

    @GetMapping("/quality-inspection-item/get")
    @Operation(summary = "获得验货单-明细")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('qms:quality-inspection:query')")
    public CommonResult<QualityInspectionItemDO> getQualityInspectionItem(@RequestParam("id") Long id) {
        return success(qualityInspectionService.getQualityInspectionItem(id));
    }

    @PutMapping("/close")
    @Operation(summary = "验货单作废",description = "验货单作废")
    @PreAuthorize("@ss.hasPermission('qms:quality-inspection:close')")
    public CommonResult<Boolean> closeInspection(@RequestParam Long id) {
        qualityInspectionService.closeInspection(id);
        return success(true);
    }

    @GetMapping("/get-simple-list")
    @Operation(summary = "获得验货单精简列表")
    public CommonResult<List<SimpleQulityInspectionRespVO>> getQualityInspectionList(@RequestParam("saleContractItemId")Long saleContractItemId) {
        return success(qualityInspectionService.getSimpleList(saleContractItemId));
    }

    @GetMapping("/export-word")
    @Operation(summary = "导出验货单 word")
    @Parameters({
            @Parameter(name = "id", required = true, description = "编号", example = "1024"),
            @Parameter(name = "reportCode", required = true, description = "模板编码", example = "tudou"),
            @Parameter(name = "reportId", required = false, description = "模版id", example = "tudou"),
            @Parameter(name = "companyId", required = false, description = "账套id", example = "tudou"),
    })
    @PreAuthorize("@ss.hasPermission('qms:quality-inspection:export')")
    public void exportWord(
            @RequestParam("id") Long id,
            @RequestParam("reportCode") String reportCode,
            @RequestParam(value = "reportId", required = false) Long reportId,
            @RequestParam(value = "companyId", required = false) Long companyId,
            HttpServletResponse response) {
        qualityInspectionService.exportWord(id, reportCode,reportId, companyId,response);
    }


    @GetMapping("/export-excel-detail")
    @Operation(summary = "导出验货单 Excel")
    @Parameters({
            @Parameter(name = "id", required = true, description = "编号", example = "1024"),
            @Parameter(name = "reportCode", required = true, description = "模板编码", example = "tudou"),
    })
    @PreAuthorize("@ss.hasPermission('qms:quality-inspection:export')")
    public void exportExcel(
            @RequestParam("id") Long id,
            @RequestParam("reportCode") String reportCode,
            HttpServletResponse response) {
        qualityInspectionService.exportExcel(id, reportCode,response);
    }
}
