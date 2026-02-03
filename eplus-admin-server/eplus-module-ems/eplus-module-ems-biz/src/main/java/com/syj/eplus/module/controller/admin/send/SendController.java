package com.syj.eplus.module.controller.admin.send;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import com.syj.eplus.module.controller.admin.send.vo.*;
import com.syj.eplus.module.controller.admin.send.vo.feeshare.FeeShareReqVO;
import com.syj.eplus.module.service.send.SendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.operatelog.core.enums.OperateTypeEnum.EXPORT;


@Tag(name = "管理后台 - 寄件")
@RestController
@RequestMapping("/ems/send")
@Validated
public class SendController {

    @Resource
    private SendService sendService;

    @PostMapping("/create")
    @Operation(summary = "创建寄件")
    @PreAuthorize("@ss.hasPermission('ems:send:create')")
    public CommonResult<Long> createSend(@Valid @RequestBody SendSaveInfoReqVO createReqVO) {
        return success(sendService.createSend(createReqVO));
    }


    @PutMapping("/update")
    @Operation(summary = "更新寄件")
    @PreAuthorize("@ss.hasPermission('ems:send:update')")
    public CommonResult<Boolean> updateSend(@Valid @RequestBody SendSaveInfoReqVO updateReqVO) {
        sendService.updateSend(updateReqVO);
        return success(true);
    }

    @PostMapping("/upload-number")
    @Operation(summary = "处理")
    @PreAuthorize("@ss.hasPermission('ems:send:upload-number')")
    public CommonResult<Boolean> uploadNumberSend( @RequestParam("id")Long id,@RequestParam("number")String number,@RequestParam("venderId")Long venderId) {
        return success(sendService.uploadNumberSend(id,number,venderId));
    }

//    @PostMapping("/submit")
//    @Operation(summary = "提交")
//    @PreAuthorize("@ss.hasPermission('ems:send:submit')")
//    public CommonResult<Boolean> submitSend( @RequestParam("id")Long id) {
//        return success(sendService.submitSend(id));
//    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除寄件")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('ems:send:delete')")
    public CommonResult<Boolean> deleteSend(@RequestParam("id") Long id) {
        sendService.deleteSend(id);
        return success(true);
    }

    @GetMapping("/detail")
    @Operation(summary = "获得寄件")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('ems:send:query')")
    public CommonResult<SendInfoRespVO> getSend(@RequestParam("id") Long id) {
        SendInfoRespVO send = sendService.getSend(new SendDetailReq().setSenedId(id));
        return success(send);
    }

    @GetMapping("/audit_detail")
    @Operation(summary = "获得寄件")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<SendInfoRespVO> getSendByProcessInstanceId(@RequestParam("id") String id) {
        SendInfoRespVO send = sendService.getSend(new SendDetailReq().setProcessInstanceId(id));
        return success(send);
    }

    @GetMapping("/page")
    @Operation(summary = "获得寄件分页")
    @PreAuthorize("@ss.hasPermission('ems:send:query')")
    public CommonResult<PageResult<SendInfoRespVO>> getSendPage(@Valid SendPageReqVO pageReqVO) {
        PageResult<SendInfoRespVO> pageResult = sendService.getSendPage(pageReqVO);
        return success(pageResult);
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出寄件 Excel")
    @PreAuthorize("@ss.hasPermission('ems:send:export')")
    @OperateLog(type = EXPORT)
    public void exportSendExcel(@Valid SendPageReqVO pageReqVO,
                                HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<SendInfoRespVO> list = sendService.getSendPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "寄件.xls", "数据", SendInfoRespVO.class,list);
    }


    @PostMapping("/import")
    @Operation(summary = "回填费用")
    @Parameter(name = "sendBillRespVOList", description = "账单列表", required = true, example = "[]")
    @PreAuthorize("@ss.hasPermission('ems:send:import')")
    @Parameter(name = "batchCode", description = "编号", required = true)
    @Parameter(name = "overFlag", description = "覆盖标记", required = true)
    public CommonResult<Boolean> importSend(@RequestParam("batchCode") String batchCode,@RequestParam("overFlag") Integer overFlag) throws Exception {
        return success(sendService.importSend(batchCode,overFlag));
    }

    @PostMapping("/update-cost")
    @Operation(summary = "单个回填费用")
    @PreAuthorize("@ss.hasPermission('ems:send:import')")
    public CommonResult<Boolean> importSend(@Valid @RequestBody SendUpdateCostReqVO sendUpdateCostReqVO) throws Exception {
        return success(sendService.updateCost(sendUpdateCostReqVO));
    }

    @PutMapping("/fee-share-update")
    @Operation(summary = "更新费用归集")
    @PreAuthorize("@ss.hasPermission('ems:send:fee-share-update')")
    public CommonResult<Boolean> updateFeeShare(@Valid @RequestBody FeeShareReqVO updateReqVO) {
        sendService.updateFeeShare(updateReqVO);
        return success(true);
    }

    @GetMapping("/get-list-by-code")
    @Operation(summary = "根据编号字符串获得寄件列表")
    public CommonResult<List<SendInfoRespVO>> getListByCodeList(@Valid String codeList) {
        List<SendInfoRespVO> pageResult = sendService.getListByCodeList(codeList);
        return success(pageResult);
    }


    @PutMapping("/approve")
    @Operation(summary = "通过任务")
    @PreAuthorize("@ss.hasPermission('ems:send:audit')")
    public CommonResult<Boolean> approveTask(@Valid @RequestBody BpmTaskApproveReqDTO reqVO) {
        sendService.approveTask(WebFrameworkUtils.getLoginUserId(), reqVO);
        return success(true);
    }

    @PutMapping("/reject")
    @Operation(summary = "不通过任务")
    @PreAuthorize("@ss.hasPermission('ems:send:audit')")
    public CommonResult<Boolean> rejectTask(@Valid @RequestBody BpmTaskRejectReqDTO reqVO) {
        sendService.rejectTask(WebFrameworkUtils.getLoginUserId(), reqVO);
        return success(true);
    }

    @PutMapping("/submit")
    @Operation(summary = "提交任务")
    @PreAuthorize("@ss.hasPermission('ems:send:submit')")
    public CommonResult<Boolean> submitTask(@RequestParam Long id) {
        sendService.submitTask(id, WebFrameworkUtils.getLoginUserId());
        return success(true);
    }

    @PutMapping("/close")
    @Operation(summary = "寄件作废",description = "作废")
    @PreAuthorize("@ss.hasPermission('ems:send:close')")
    public CommonResult<Boolean> closeSend(@RequestParam Long id) {
        sendService.closeSend(id);
        return success(true);
    }

    @PutMapping("/update-vender")
    @Operation(summary = "修改快递公司")
    @PreAuthorize("@ss.hasPermission('ems:send:update-vender')")
    public CommonResult<Boolean> updateVender(@RequestParam("id") Long id, @RequestParam("venderId") Long venderId) {
        sendService.updateVender(id, venderId);
        return success(true);
    }
}