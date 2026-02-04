package com.syj.eplus.module.infra.controller.admin.company;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import com.syj.eplus.module.infra.api.company.dto.SimpleCompanyDTO;
import com.syj.eplus.module.infra.controller.admin.company.vo.CompanyBankRespVO;
import com.syj.eplus.module.infra.controller.admin.company.vo.CompanyPageReqVO;
import com.syj.eplus.module.infra.controller.admin.company.vo.CompanyRespVO;
import com.syj.eplus.module.infra.controller.admin.company.vo.CompanySaveReqVO;
import com.syj.eplus.module.infra.dal.dataobject.company.CompanyDO;
import com.syj.eplus.module.infra.enums.company.CompanyNatureEnum;
import com.syj.eplus.module.infra.service.company.CompanyService;
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
import java.util.ArrayList;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.operatelog.core.enums.OperateTypeEnum.EXPORT;

@Tag(name = "管理后台 - 内部法人单位")
@RestController
@RequestMapping("/system/company")
@Validated
public class CompanyController {

    @Resource
    private CompanyService companyService;

    @PostMapping("/create")
    @Operation(summary = "创建内部法人单位")
    @PreAuthorize("@ss.hasPermission('system:company:create')")
    public CommonResult<Long> createCompany(@Valid @RequestBody CompanySaveReqVO createReqVO) {
        return success(companyService.createCompany(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新内部法人单位")
    @PreAuthorize("@ss.hasPermission('system:company:update')")
    public CommonResult<Boolean> updateCompany(@Valid @RequestBody CompanySaveReqVO updateReqVO) {
        companyService.updateCompany(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除内部法人单位")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('system:company:delete')")
    public CommonResult<Boolean> deleteCompany(@RequestParam("id") Long id) {
        companyService.deleteCompany(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得内部法人单位")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:company:query')")
    public CommonResult<CompanyRespVO> getCompany(@RequestParam("id") Long id) {
        CompanyRespVO company = companyService.getCompany(id);
        return success(company);
    }

    @GetMapping("/get-simple-list")
    @Operation(summary = "获得内部法人单位精简列表")
    public CommonResult<List<CompanyRespVO>> getSimpleCompany() {
        List<CompanyRespVO> companyList = companyService.getCompanyList(null,null);
        return success(companyList);
    }


    @GetMapping("/get-simple-list-not-inner")
    @Operation(summary = "获得不包含内部公司类型的精简列表")
    public CommonResult<List<CompanyRespVO>> getSimpleCompanyNotInner() {
        List<Integer> notContainList = new ArrayList<>();
        notContainList.add(CompanyNatureEnum.INTERNAL_CUST.getValue());
        List<CompanyRespVO> companyList = companyService.getCompanyList(notContainList,null);
        return success(companyList);
    }


    @GetMapping("/get-process-list")
    @Operation(summary = "仅获得包含组装工厂的精简列表")
    public CommonResult<List<SimpleCompanyDTO>> getProcessCompany() {
        List<SimpleCompanyDTO> simpleCompanyDTOList = companyService.getProcessCompanyDTO();
        return success(simpleCompanyDTOList);
    }

    @GetMapping("/get-unprocess-list")
    @Operation(summary = "仅获得不包含组装工厂的精简列表")
    public CommonResult<List<SimpleCompanyDTO>> getUnProcessableCompany() {
        List<SimpleCompanyDTO> simpleCompanyDTOList = companyService.getUnProcessCompanyDTO();
        return success(simpleCompanyDTOList);
    }

    @GetMapping("/page")
    @Operation(summary = "获得内部法人单位分页")
    @PreAuthorize("@ss.hasPermission('system:company:query')")
    public CommonResult<PageResult<CompanyRespVO>> getCompanyPage(@Valid CompanyPageReqVO pageReqVO) {
        PageResult<CompanyDO> pageResult = companyService.getCompanyPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, CompanyRespVO.class));
    }

    @GetMapping("/bank-list")
    @Operation(summary = "获得内部法人银行账号分页")
    public CommonResult<List<CompanyBankRespVO>> getCompanyBankLit() {
        List<CompanyBankRespVO> pageResult = companyService.getCompanyBankList();
        return success(pageResult);
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出内部法人单位 Excel")
    @PreAuthorize("@ss.hasPermission('system:company:export')")
    @OperateLog(type = EXPORT)
    public void exportCompanyExcel(@Valid CompanyPageReqVO pageReqVO,
                                   HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<CompanyDO> list = companyService.getCompanyPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "内部法人单位.xls", "数据", CompanyRespVO.class,
                BeanUtils.toBean(list, CompanyRespVO.class));
    }

    @PutMapping("/enable")
    @Operation(summary = "启用")
    @PreAuthorize("@ss.hasPermission('system:company:enable')")
    public CommonResult<Boolean> enableCust(@RequestParam Long custId) {
        companyService.enableCompany(custId);
        return success(true);
    }

    @PutMapping("/disable")
    @Operation(summary = "禁用")
    @PreAuthorize("@ss.hasPermission('system:company:disable')")
    public CommonResult<Boolean> disableCust(@RequestParam Long custId) {
        companyService.disableCompany(custId);
        return success(true);
    }



}