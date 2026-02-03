package com.syj.eplus.module.crm.controller.admin.cust;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import com.syj.eplus.framework.common.entity.ChangeEffectRespVO;
import com.syj.eplus.module.crm.api.cust.dto.SimpleCustRespDTO;
import com.syj.eplus.module.crm.controller.admin.cust.vo.*;
import com.syj.eplus.module.crm.indexing.CustomerNameIndexService;
import com.syj.eplus.module.crm.service.cust.CustService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.error;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.operatelog.core.enums.OperateTypeEnum.EXPORT;
import static com.syj.eplus.module.crm.enums.ErrorCodeConstants.*;


@Tag(name = "管理后台 - 客户资料")
@RestController
@RequestMapping("/crm/cust")
@Validated
public class CustController {

    @Resource
    private CustService custService;

    @Resource
    private CustomerNameIndexService  customerNameIndexService;

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @PostMapping("/create")
    @Operation(summary = "创建客户资料")
    @PreAuthorize("@ss.hasPermission('crm:cust:create')")
    public CommonResult<Long> createCust(@Valid @RequestBody CustInfoSaveReqVO createReqVO) {
        Long custId;
        try {
            custId = custService.createCust(createReqVO);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return error(new ErrorCode(CREATE_CUST_FAIL.getCode(), CREATE_CUST_FAIL.getMsg() + ":" + e.getMessage()));
        }
        return success(custId);
    }

    @PutMapping("/update")
    @Operation(summary = "更新客户资料")
    @PreAuthorize("@ss.hasPermission('crm:cust:update')")
    public CommonResult<Boolean> updateCust(@Valid @RequestBody CustInfoSaveReqVO updateReqVO) {
        try {
            custService.updateCust(updateReqVO);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return error(new ErrorCode(UPDATE_CUST_FAIL.getCode(), UPDATE_CUST_FAIL.getMsg() + ":" + e.getMessage()));
        }
        return success(true);
    }


    @PutMapping("/convert")
    @Operation(summary = "转正式客户")
    @PreAuthorize("@ss.hasPermission('crm:cust:convert')")
    public CommonResult<Boolean> convertCust(@Valid @RequestBody CustInfoSaveReqVO updateReqVO) {
        try {
            custService.convertCust(updateReqVO);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return error(new ErrorCode(CONVERT_CUST_FAIL.getCode(), CONVERT_CUST_FAIL.getMsg() + ":" + e.getMessage()));
        }
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除客户资料")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('crm:cust:delete')")
    public CommonResult<Boolean> deleteCust(@RequestParam("id") Long id) {
        custService.deleteCust(id, false);
        return success(true);
    }

    @GetMapping("/detail")
    @Operation(summary = "获得客户详情")
    @PreAuthorize("@ss.hasPermission('crm:cust:query')")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<CustInfoRespVo> getCustById(@RequestParam("id") Long id) {
        CustInfoRespVo custInfoRespVo = custService.getCust(new CrmCustDetailReq().setCustId(id));
        return success(custInfoRespVo);
    }
    @GetMapping("/detail-by-code")
    @Operation(summary = "根据编号获得客户详情")
    @PreAuthorize("@ss.hasPermission('crm:cust:query')")
    @Parameter(name = "code", description = "编号", required = true, example = "1024")
    public CommonResult<CustInfoRespVo> getCustByCode(@RequestParam("code") String code) {
        CustInfoRespVo custInfoRespVo = custService.getCust(new CrmCustDetailReq().setCustCode(code));
        return success(custInfoRespVo);
    }
    @GetMapping("/audit-detail")
    @Operation(summary = "获得客户详情")
    @Parameter(name = "id", description = "编号", required = true, example = "uuid")
    public CommonResult<CustInfoRespVo> getCustByProcessId(@RequestParam("id") String id) {
        CustInfoRespVo custInfoRespVo = custService.getCust(new CrmCustDetailReq().setProcessInstanceId(id));
        return success(custInfoRespVo);
    }

    @GetMapping("/get-simple-list")
    @Operation(summary = "获得客户精简列表")
    @DataPermission(enable = false)
    public CommonResult<PageResult<SimpleCustRespDTO>> getSimpleCust(CustPageReqVO pageReqVO) {
        PageResult<SimpleCustRespDTO> simpleCust = custService.getSimpleCust(pageReqVO);
        return success(simpleCust);
    }

    @GetMapping("/page")
    @Operation(summary = "获得客户资料分页")
    @PreAuthorize("@ss.hasPermission('crm:cust:query')")
    public CommonResult<PageResult<CustRespVO>> getCustPage(@Valid CustPageReqVO pageReqVO) {
        PageResult<CustRespVO> pageResult = custService.getCustPage(pageReqVO);
        List<CustRespVO> custList = pageResult.getList();
        if (CollUtil.isEmpty(custList)) {
            return success(PageResult.empty(pageResult.getTotal()));
        }
        return success(pageResult);
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出客户资料 Excel")
    @PreAuthorize("@ss.hasPermission('crm:cust:export')")
    @OperateLog(type = EXPORT)
    public void exportCustExcel(@Valid CustPageReqVO pageReqVO,
                                HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<CustRespVO> custRespVOList = custService.getCustPage(pageReqVO).getList();
        if (CollUtil.isNotEmpty(custRespVOList)) {
            // 导出 Excel
            ExcelUtils.write(response, "客户资料.xls", "数据", CustRespVO.class,
                    custRespVOList);
        }

    }

    @PutMapping("/approve")
    @Operation(summary = "通过任务")
    @PreAuthorize("@ss.hasPermission('crm:cust:audit')")
    public CommonResult<Boolean> approveTask(@Valid @RequestBody BpmTaskApproveReqDTO reqDTO) {
        custService.approveTask(WebFrameworkUtils.getLoginUserId(), reqDTO);
        return success(true);
    }

    @PutMapping("/reject")
    @Operation(summary = "不通过任务")
    @PreAuthorize("@ss.hasPermission('crm:cust:audit')")
    public CommonResult<Boolean> rejectTask(@Valid @RequestBody BpmTaskRejectReqDTO reqDTO) {
        custService.rejectTask(WebFrameworkUtils.getLoginUserId(), reqDTO);
        return success(true);
    }
    @GetMapping("/find-similar-names")
    @Operation(summary = "查找相似名称")
    public CommonResult<List<String>> findSimilarNames(@RequestParam String name) {
        List<String> result = customerNameIndexService.findSimilarNames(name);
        return success(result);
    }


    @PutMapping("/submit")
    @Operation(summary = "提交任务")
    @PreAuthorize("@ss.hasPermission('crm:cust:submit')")
    public CommonResult<Boolean> submitTask(@RequestParam Long custId) {
        custService.submitTask(custId, WebFrameworkUtils.getLoginUserId());
        return success(true);
    }

    @GetMapping("/get-cust-bank-list")
    @Operation(summary = "获得客户银行信息")
    @DataPermission(enable = false)
    public CommonResult<PageResult<SimpleCustRespDTO>> getPayableVender(SimplePageReqVO simplePageReqVO) {
        PageResult<SimpleCustRespDTO> simpleVenderRespDTO = custService.getSimpleCustRespDTO(simplePageReqVO);
        return success(simpleVenderRespDTO);
    }

    @PutMapping("/enable")
    @Operation(summary = "启用")
    @PreAuthorize("@ss.hasPermission('crm:cust:enable')")
    public CommonResult<Boolean> enableCust(@RequestParam Long custId) {
        custService.enableCust(custId);
        return success(true);
    }

    @PutMapping("/disable")
    @Operation(summary = "禁用")
    @PreAuthorize("@ss.hasPermission('crm:cust:disable')")
    public CommonResult<Boolean> disableCust(@RequestParam Long custId) {
        custService.disableCust(custId);
        return success(true);
    }

    @GetMapping("/check-name")
    @Operation(summary = "校验名称")
    public CommonResult<List<String>> getSimpleCustName(@RequestParam("name") String name) {
        List<String> nameList = custService.checkName(name);
        return success(nameList);
    }

    @PutMapping("/change")
    @Operation(summary = "变更客户资料")
    @PreAuthorize("@ss.hasPermission('crm:cust:change')")
    public CommonResult<Boolean> changeCust(@Valid @RequestBody CustInfoSaveReqVO changeReqVO) {
        Long custId;
        try {
            custId = custService.changeCust(changeReqVO);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return error(new ErrorCode(UPDATE_CUST_FAIL.getCode(), UPDATE_CUST_FAIL.getMsg() + ":" + e.getMessage()));
        }
        return success(true);
    }

    @GetMapping("/change-page")
    @Operation(summary = "获得变更客户资料分页")
    @PreAuthorize("@ss.hasPermission('crm:cust-change:query')")
    public CommonResult<PageResult<CustRespVO>> getCustChangePage(@Valid CustPageReqVO pageReqVO) {
        PageResult<CustRespVO> pageResult = custService.getCustChangePage(pageReqVO);
        List<CustRespVO> custList = pageResult == null ? null : pageResult.getList();
        if (CollUtil.isEmpty(custList)) {
            return success(PageResult.empty());
        }
        return success(pageResult);
    }

    @PostMapping("/change-effect")
    @Operation(summary = "获得变更影响列表")
    @PreAuthorize("@ss.hasPermission('crm:cust:change')")
    public CommonResult<ChangeEffectRespVO> getChangeEffect(@Valid @RequestBody CustInfoSaveReqVO changeReqVO) {
        CustInfoRespVo cust = BeanUtils.toBean(changeReqVO, CustInfoRespVo.class);
        cust.setVer(cust.getVer() + 1);
        return success(custService.getChangeEffect(cust));
    }

    @PutMapping("/change-update")
    @Operation(summary = "更新变更客户资料")
    @PreAuthorize("@ss.hasPermission('crm:cust-change:update')")
    public CommonResult<Boolean> updateChangeCust(@Valid @RequestBody CustInfoSaveReqVO updateReqVO) {
        return updateCust(updateReqVO);
    }

    @DeleteMapping("/change-delete")
    @Operation(summary = "删除变更客户资料")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('crm:cust-change:delete')")
    public CommonResult<Boolean> deleteChangeCust(@RequestParam("id") Long id) {
        custService.deleteChangeCust(id);
        return success(true);
    }

    @GetMapping("/change-detail")
    @Operation(summary = "获得变更客户详情")
    @PreAuthorize("@ss.hasPermission('crm:cust-change:query')")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<CustInfoRespVo> getChangeCustById(@RequestParam("id") Long id) {
        CustInfoRespVo custInfoRespVo = custService.getCustChange(new CrmCustDetailReq().setCustId(id));
        return success(custInfoRespVo);
    }

    @GetMapping("/audit-change-detail")
    @Operation(summary = "获得变更客户详情")
    @Parameter(name = "id", description = "编号", required = true, example = "uuid")
    public CommonResult<CustInfoRespVo> getChangeCustByProcessId(@RequestParam("id") String id) {
        CustInfoRespVo custInfoRespVo = custService.getCustChange(new CrmCustDetailReq().setProcessInstanceId(id));
        return success(custInfoRespVo);
    }

    @PutMapping("/change-approve")
    @Operation(summary = "通过变更任务")
    @PreAuthorize("@ss.hasPermission('crm:cust-change:audit')")
    public CommonResult<Boolean> approveChangeTask(@Valid @RequestBody BpmTaskApproveReqDTO reqDTO) {
        custService.approveChangeTask(WebFrameworkUtils.getLoginUserId(), reqDTO);
        return success(true);
    }

    @PutMapping("/change-reject")
    @Operation(summary = "不通过变更任务")
    @PreAuthorize("@ss.hasPermission('crm:cust-change:audit')")
    public CommonResult<Boolean> rejectChangeTask(@Valid @RequestBody BpmTaskRejectReqDTO reqDTO) {
        custService.rejectChangeTask(WebFrameworkUtils.getLoginUserId(), reqDTO);
        return success(true);
    }

    @PutMapping("/change-submit")
    @Operation(summary = "提交变更任务")
    @PreAuthorize("@ss.hasPermission('crm:cust-change:submit')")
    public CommonResult<Boolean> submitChangeTask(@RequestParam Long custId) {
        custService.submitChangeTask(custId, WebFrameworkUtils.getLoginUserId());
        return success(true);
    }

    @PutMapping("/anti-audit")
    @Operation(summary = "反审核")
    @PreAuthorize("@ss.hasPermission('crm:cust:anti-audit')")
    public CommonResult<Boolean> antiAudit(@RequestParam Long custId) {
        return success(custService.antiAudit(custId));
    }

    @DeleteMapping("/manager-delete")
    @Operation(summary = "管理员删除")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('crm:cust:manager-delete')")
    public CommonResult<Boolean> managerDeleteCust(@RequestParam("id") Long id) {
        custService.managerDeleteCust(id);
        return success(true);
    }

    @GetMapping("/cust-duplicate-check")
    @Operation(summary = "客户查重分页")
    @PreAuthorize("@ss.hasPermission('crm:cust:query')")
    public CommonResult<PageResult<CustRespVO>> custDuplicateCheck(@Valid CustPageReqVO pageReqVO) {
        PageResult<CustRespVO> pageResult = custService.custDuplicateCheck(pageReqVO);
        List<CustRespVO> custList = pageResult.getList();
        if (CollUtil.isEmpty(custList)) {
            return success(PageResult.empty(pageResult.getTotal()));
        }
        return success(pageResult);
    }
}