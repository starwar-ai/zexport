package com.syj.eplus.module.scm.controller.admin.venderbankaccount;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import com.syj.eplus.module.scm.controller.admin.venderbankaccount.vo.VenderBankaccountPageReqVO;
import com.syj.eplus.module.scm.controller.admin.venderbankaccount.vo.VenderBankaccountRespVO;
import com.syj.eplus.module.scm.controller.admin.venderbankaccount.vo.VenderBankaccountSaveReqVO;
import com.syj.eplus.module.scm.dal.dataobject.venderbankaccount.VenderBankaccountDO;
import com.syj.eplus.module.scm.service.venderbankaccount.VenderBankaccountService;
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


@Tag(name = "管理后台 - 供应商银行账户")
@RestController
@RequestMapping("/scm/vender-bankaccount")
@Validated
public class VenderBankaccountController {

    @Resource
    private VenderBankaccountService venderBankaccountService;

    @PostMapping("/create")
    @Operation(summary = "创建供应商银行账户")
    @PreAuthorize("@ss.hasPermission('scm:vender-bankaccount:create')")
    public CommonResult<Long> createVenderBankaccount(@Valid @RequestBody VenderBankaccountSaveReqVO createReqVO) {
        return success(venderBankaccountService.createVenderBankaccount(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新供应商银行账户")
    @PreAuthorize("@ss.hasPermission('scm:vender-bankaccount:update')")
    public CommonResult<Boolean> updateVenderBankaccount(@Valid @RequestBody VenderBankaccountSaveReqVO updateReqVO) {
        venderBankaccountService.updateVenderBankaccount(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除供应商银行账户")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('scm:vender-bankaccount:delete')")
    public CommonResult<Boolean> deleteVenderBankaccount(@RequestParam("id") Long id) {
        venderBankaccountService.deleteVenderBankaccount(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得供应商银行账户")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('scm:vender-bankaccount:query')")
    public CommonResult<VenderBankaccountRespVO> getVenderBankaccount(@RequestParam("id") Long id) {
        VenderBankaccountDO venderBankaccount = venderBankaccountService.getVenderBankaccount(id);
        return success(BeanUtils.toBean(venderBankaccount, VenderBankaccountRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得供应商银行账户分页")
    @PreAuthorize("@ss.hasPermission('scm:vender-bankaccount:query')")
    public CommonResult<PageResult<VenderBankaccountRespVO>> getVenderBankaccountPage(@Valid VenderBankaccountPageReqVO pageReqVO) {
        PageResult<VenderBankaccountDO> pageResult = venderBankaccountService.getVenderBankaccountPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, VenderBankaccountRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出供应商银行账户 Excel")
    @PreAuthorize("@ss.hasPermission('scm:vender-bankaccount:export')")
    @OperateLog(type = EXPORT)
    public void exportVenderBankaccountExcel(@Valid VenderBankaccountPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<VenderBankaccountDO> list = venderBankaccountService.getVenderBankaccountPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "供应商银行账户.xls", "数据", VenderBankaccountRespVO.class,
                        BeanUtils.toBean(list, VenderBankaccountRespVO.class));
    }

}