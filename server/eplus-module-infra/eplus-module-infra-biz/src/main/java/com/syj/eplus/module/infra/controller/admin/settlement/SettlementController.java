package com.syj.eplus.module.infra.controller.admin.settlement;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import com.syj.eplus.module.infra.controller.admin.settlement.vo.SettlementPageReqVO;
import com.syj.eplus.module.infra.controller.admin.settlement.vo.SettlementRespVO;
import com.syj.eplus.module.infra.controller.admin.settlement.vo.SettlementSaveReqVO;
import com.syj.eplus.module.infra.dal.dataobject.settlement.SettlementDO;
import com.syj.eplus.module.infra.service.settlement.SettlementService;
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

@Tag(name = "管理后台 - 结汇方式")
@RestController
@RequestMapping("/infra/settlement")
@Validated
public class SettlementController {

    @Resource
    private SettlementService settlementService;

    @PostMapping("/create")
    @Operation(summary = "创建结汇方式")
    @PreAuthorize("@ss.hasPermission('system:settlement:create')")
    public CommonResult<Long> createPaymentTerm(@Valid @RequestBody SettlementSaveReqVO createReqVO) {
        return success(settlementService.createSettlement(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新结汇方式")
    @PreAuthorize("@ss.hasPermission('system:settlement:update')")
    public CommonResult<Boolean> updatePaymentTerm(@Valid @RequestBody SettlementSaveReqVO updateReqVO) {
        settlementService.updateSettlement(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除结汇方式")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('system:settlement:delete')")
    public CommonResult<Boolean> deletePaymentTerm(@RequestParam("id") Long id) {
        settlementService.deleteSettlement(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得结汇方式")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:settlement:query')")
    public CommonResult<SettlementRespVO> getPaymentTerm(@RequestParam("id") Long id) {
        SettlementRespVO settlementRespVO = settlementService.getSettlement(id);
        return success(settlementRespVO);
    }

    @GetMapping("/page")
    @Operation(summary = "获得结汇方式分页")
    @PreAuthorize("@ss.hasPermission('system:settlement:query')")
    public CommonResult<PageResult<SettlementRespVO>> getPaymentTermPage(@Valid SettlementPageReqVO pageReqVO) {
        PageResult<SettlementDO> pageResult = settlementService.getSettlementPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, SettlementRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得结汇方式列表")
    public CommonResult<List<SettlementRespVO>> getPaymentTermPage() {
        List<SettlementDO> paymentTermList = settlementService.getSettlementList();
        return success(BeanUtils.toBean(paymentTermList, SettlementRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出结汇方式 Excel")
    @PreAuthorize("@ss.hasPermission('system:settlement:export')")
    @OperateLog(type = EXPORT)
    public void exportPaymentTermExcel(@Valid SettlementPageReqVO pageReqVO,
                                       HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<SettlementDO> list = settlementService.getSettlementPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "结汇方式.xls", "数据", SettlementRespVO.class,
                BeanUtils.toBean(list, SettlementRespVO.class));
    }

}