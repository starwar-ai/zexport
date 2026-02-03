package com.syj.eplus.module.crm.controller.admin.custbankaccount;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import com.syj.eplus.module.crm.controller.admin.custbankaccount.vo.CustBankaccountPageReqVO;
import com.syj.eplus.module.crm.controller.admin.custbankaccount.vo.CustBankaccountRespVO;
import com.syj.eplus.module.crm.controller.admin.custbankaccount.vo.CustBankaccountSaveReqVO;
import com.syj.eplus.module.crm.dal.dataobject.custbankaccount.CustBankaccountDO;
import com.syj.eplus.module.crm.service.custbankaccount.CustBankaccountService;
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


@Tag(name = "管理后台 - 银行账户信息")
@RestController
@RequestMapping("/crm/cust-bankaccount")
@Validated
public class CustBankaccountController {

    @Resource
    private CustBankaccountService custBankaccountService;

    @PostMapping("/create")
    @Operation(summary = "创建银行账户信息")
    @PreAuthorize("@ss.hasPermission('crm:cust-bankaccount:create')")
    public CommonResult<Long> createCustBankaccount(@Valid @RequestBody CustBankaccountSaveReqVO createReqVO) {
        return success(custBankaccountService.createCustBankaccount(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新银行账户信息")
    @PreAuthorize("@ss.hasPermission('crm:cust-bankaccount:update')")
    public CommonResult<Boolean> updateCustBankaccount(@Valid @RequestBody CustBankaccountSaveReqVO updateReqVO) {
        custBankaccountService.updateCustBankaccount(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除银行账户信息")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('crm:cust-bankaccount:delete')")
    public CommonResult<Boolean> deleteCustBankaccount(@RequestParam("id") Long id) {
        custBankaccountService.deleteCustBankaccount(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得银行账户信息")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('crm:cust-bankaccount:query')")
    public CommonResult<CustBankaccountRespVO> getCustBankaccount(@RequestParam("id") Long id) {
        CustBankaccountDO custBankaccount = custBankaccountService.getCustBankaccount(id);
        return success(BeanUtils.toBean(custBankaccount, CustBankaccountRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得银行账户信息分页")
    @PreAuthorize("@ss.hasPermission('crm:cust-bankaccount:query')")
    public CommonResult<PageResult<CustBankaccountRespVO>> getCustBankaccountPage(@Valid CustBankaccountPageReqVO pageReqVO) {
        PageResult<CustBankaccountDO> pageResult = custBankaccountService.getCustBankaccountPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, CustBankaccountRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出银行账户信息 Excel")
    @PreAuthorize("@ss.hasPermission('crm:cust-bankaccount:export')")
    @OperateLog(type = EXPORT)
    public void exportCustBankaccountExcel(@Valid CustBankaccountPageReqVO pageReqVO,
                                           HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<CustBankaccountDO> list = custBankaccountService.getCustBankaccountPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "银行账户信息.xls", "数据", CustBankaccountRespVO.class,
                BeanUtils.toBean(list, CustBankaccountRespVO.class));
    }

}