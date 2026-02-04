package com.syj.eplus.module.crm.controller.admin.custsettlement;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import com.syj.eplus.module.crm.controller.admin.custsettlement.vo.CustSettlementPageReqVO;
import com.syj.eplus.module.crm.controller.admin.custsettlement.vo.CustSettlementRespVO;
import com.syj.eplus.module.crm.controller.admin.custsettlement.vo.CustSettlementSaveReqVO;
import com.syj.eplus.module.crm.dal.dataobject.custsettlement.CustSettlementDO;
import com.syj.eplus.module.crm.service.custSettlement.CustSettlementService;
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

@Tag(name = "管理后台 - 客户结汇方式")
@RestController
@RequestMapping("/crm/cust-Settlement")
@Validated
public class CustSettlementController {

    @Resource
    private CustSettlementService custSettlementService;

    @PostMapping("/create")
    @Operation(summary = "创建客户结汇方式")
    @PreAuthorize("@ss.hasPermission('crm:cust-settlement:create')")
    public CommonResult<Long> createCustSettlement(@Valid @RequestBody CustSettlementSaveReqVO createReqVO) {
        return success(custSettlementService.createCustSettlement(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新客户结汇方式")
    @PreAuthorize("@ss.hasPermission('crm:cust-settlement:update')")
    public CommonResult<Boolean> updateCustSettlement(@Valid @RequestBody CustSettlementSaveReqVO updateReqVO) {
        custSettlementService.updateCustSettlement(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除客户结汇方式")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('crm:cust-settlement:delete')")
    public CommonResult<Boolean> deleteCustSettlement(@RequestParam("id") Long id) {
        custSettlementService.deleteCustSettlement(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得客户结汇方式")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('crm:cust-settlement:query')")
    public CommonResult<CustSettlementRespVO> getCustSettlement(@RequestParam("id") Long id) {
        CustSettlementDO custSettlement = custSettlementService.getCustSettlement(id);
        return success(BeanUtils.toBean(custSettlement, CustSettlementRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得客户结汇方式分页")
    @PreAuthorize("@ss.hasPermission('crm:cust-settlement:query')")
    public CommonResult<PageResult<CustSettlementRespVO>> getCustSettlementPage(@Valid CustSettlementPageReqVO pageReqVO) {
        PageResult<CustSettlementDO> pageResult = custSettlementService.getCustSettlementPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, CustSettlementRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出客户结汇方式 Excel")
    @PreAuthorize("@ss.hasPermission('crm:cust-settlement:export')")
    @OperateLog(type = EXPORT)
    public void exportCustSettlementExcel(@Valid CustSettlementPageReqVO pageReqVO,
                                          HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<CustSettlementDO> list = custSettlementService.getCustSettlementPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "客户结汇方式.xls", "数据", CustSettlementRespVO.class,
                BeanUtils.toBean(list, CustSettlementRespVO.class));
    }

}