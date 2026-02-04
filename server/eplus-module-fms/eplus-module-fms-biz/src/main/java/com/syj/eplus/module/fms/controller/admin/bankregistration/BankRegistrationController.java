package com.syj.eplus.module.fms.controller.admin.bankregistration;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import com.syj.eplus.module.crm.api.cust.dto.CustBankAccountDTO;
import com.syj.eplus.module.fms.controller.admin.bankregistration.vo.*;
import com.syj.eplus.module.fms.dal.dataobject.bankregistration.BankRegistrationDO;
import com.syj.eplus.module.fms.service.bankregistration.BankRegistrationService;
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

@Tag(name = "管理后台 - 银行登记")
@RestController
@RequestMapping("/fms/bank-registration")
@Validated
public class BankRegistrationController {

    @Resource
    private BankRegistrationService bankRegistrationService;

    @PostMapping("/create")
    @Operation(summary = "创建银行登记")
    @PreAuthorize("@ss.hasPermission('fms:bank-registration:create')")
    public CommonResult<Boolean> createBankRegistration(@Valid @RequestBody BankRegistrationCreateReq createReqVO) {
        bankRegistrationService.createBankRegistration(createReqVO);
        return success(true);
    }

    @PutMapping("/update")
    @Operation(summary = "更新银行登记")
    @PreAuthorize("@ss.hasPermission('fms:bank-registration:update')")
    public CommonResult<Boolean> updateBankRegistration(@Valid @RequestBody BankRegistrationSaveReqVO updateReqVO) {
        bankRegistrationService.updateBankRegistration(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除银行登记")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('fms:bank-registration:delete')")
    public CommonResult<Boolean> deleteBankRegistration(@RequestParam("id") Long id) {
        bankRegistrationService.deleteBankRegistration(id);
        return success(true);
    }

        @GetMapping("/detail")
    @Operation(summary = "获得银行登记")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('fms:bank-registration:query')")
    public CommonResult<BankRegistrationRespVO> getBankRegistration(@RequestParam("id") Long id) {
            BankRegistrationRespVO bankRegistration = bankRegistrationService.getBankRegistration(id);
            return success(bankRegistration);
    }
    @GetMapping("/page")
    @Operation(summary = "获得银行登记分页")
    @PreAuthorize("@ss.hasPermission('fms:bank-registration:query')")
    public CommonResult<PageResult<BankRegistrationRespVO>> getBankRegistrationPage(@Valid BankRegistrationPageReqVO pageReqVO) {
        PageResult<BankRegistrationDO> pageResult = bankRegistrationService.getBankRegistrationPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, BankRegistrationRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出银行登记 Excel")
    @PreAuthorize("@ss.hasPermission('fms:bank-registration:export')")
    @OperateLog(type = EXPORT)
    public void exportBankRegistrationExcel(@Valid BankRegistrationPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<BankRegistrationDO> list = bankRegistrationService.getBankRegistrationPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "银行登记.xls", "数据", BankRegistrationRespVO.class,
                        BeanUtils.toBean(list, BankRegistrationRespVO.class));
    }


    @GetMapping("/get-simple-msg")
    @Operation(summary = "获得银行归属")
    public CommonResult<SimpleRegistrationResp> getBankRegistration(@RequestParam("bankPoc") String bankPoc) {
        SimpleRegistrationResp simpleRegistrationRespByBankPoc = bankRegistrationService.getSimpleRegistrationRespByBankPoc(bankPoc);
        return success(simpleRegistrationRespByBankPoc);
    }

    @GetMapping("/get-bank-poc")
    @Operation(summary = "获得银行抬头")
    public CommonResult<List<CustBankAccountDTO>> getBankPocList(@RequestParam("bankPoc") String bankPoc) {
        return success(bankRegistrationService.getBankPocListByTitle(bankPoc));
    }

}