package com.syj.eplus.module.crm.controller.admin.custpoc;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import com.syj.eplus.module.crm.controller.admin.custpoc.vo.CustPocPageReqVO;
import com.syj.eplus.module.crm.controller.admin.custpoc.vo.CustPocRespVO;
import com.syj.eplus.module.crm.controller.admin.custpoc.vo.CustPocSaveReqVO;
import com.syj.eplus.module.crm.dal.dataobject.custpoc.CustPocDO;
import com.syj.eplus.module.crm.service.custpoc.CustPocService;
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

@Tag(name = "管理后台 - 客户联系人")
@RestController
@RequestMapping("/crm/cust-poc")
@Validated
public class CustPocController {

    @Resource
    private CustPocService custPocService;

    @PostMapping("/create")
    @Operation(summary = "创建客户联系人")
    @PreAuthorize("@ss.hasPermission('crm:cust-poc:create')")
    public CommonResult<Long> createCustPoc(@Valid @RequestBody CustPocSaveReqVO createReqVO) {
        return success(custPocService.createCustPoc(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新客户联系人")
    @PreAuthorize("@ss.hasPermission('crm:cust-poc:update')")
    public CommonResult<Boolean> updateCustPoc(@Valid @RequestBody CustPocSaveReqVO updateReqVO) {
        custPocService.updateCustPoc(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除客户联系人")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('crm:cust-poc:delete')")
    public CommonResult<Boolean> deleteCustPoc(@RequestParam("id") Long id) {
        custPocService.deleteCustPoc(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得客户联系人")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('crm:cust-poc:query')")
    public CommonResult<CustPocRespVO> getCustPoc(@RequestParam("id") Long id) {
        CustPocDO custPoc = custPocService.getCustPoc(id);
        return success(BeanUtils.toBean(custPoc, CustPocRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得客户联系人分页")
    @PreAuthorize("@ss.hasPermission('crm:cust-poc:query')")
    public CommonResult<PageResult<CustPocRespVO>> getCustPocPage(@Valid CustPocPageReqVO pageReqVO) {
        PageResult<CustPocDO> pageResult = custPocService.getCustPocPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, CustPocRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出客户联系人 Excel")
    @PreAuthorize("@ss.hasPermission('crm:cust-poc:export')")
    @OperateLog(type = EXPORT)
    public void exportCustPocExcel(@Valid CustPocPageReqVO pageReqVO,
                                   HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<CustPocDO> list = custPocService.getCustPocPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "客户联系人.xls", "数据", CustPocRespVO.class,
                BeanUtils.toBean(list, CustPocRespVO.class));
    }

}