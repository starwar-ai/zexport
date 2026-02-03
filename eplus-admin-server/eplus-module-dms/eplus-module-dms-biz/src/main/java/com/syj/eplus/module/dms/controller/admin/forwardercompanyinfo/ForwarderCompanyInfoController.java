package com.syj.eplus.module.dms.controller.admin.forwardercompanyinfo;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import com.syj.eplus.module.dms.controller.admin.forwardercompanyinfo.vo.ForwarderCompanyInfoPageReqVO;
import com.syj.eplus.module.dms.controller.admin.forwardercompanyinfo.vo.ForwarderCompanyInfoRespVO;
import com.syj.eplus.module.dms.controller.admin.forwardercompanyinfo.vo.ForwarderCompanyInfoSaveReqVO;
import com.syj.eplus.module.dms.controller.admin.forwardercompanyinfo.vo.SimpleForwarderCompanyInfoPageReqVO;
import com.syj.eplus.module.dms.dal.dataobject.forwardercompanyinfo.ForwarderCompanyInfoDO;
import com.syj.eplus.module.dms.service.forwardercompanyinfo.ForwarderCompanyInfoService;
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

@Tag(name = "管理后台 - 船代公司")
@RestController
@RequestMapping("/dms/forwarder-company-info")
@Validated
public class ForwarderCompanyInfoController {

    @Resource
    private ForwarderCompanyInfoService forwarderCompanyInfoService;

    @PostMapping("/create")
    @Operation(summary = "创建船代公司")
    @PreAuthorize("@ss.hasPermission('dms:forwarder-company-info:create')")
    public CommonResult<Long> createForwarderCompanyInfo(@Valid @RequestBody ForwarderCompanyInfoSaveReqVO createReqVO) {
        return success(forwarderCompanyInfoService.createForwarderCompanyInfo(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新船代公司")
    @PreAuthorize("@ss.hasPermission('dms:forwarder-company-info:update')")
    public CommonResult<Boolean> updateForwarderCompanyInfo(@Valid @RequestBody ForwarderCompanyInfoSaveReqVO updateReqVO) {
        forwarderCompanyInfoService.updateForwarderCompanyInfo(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除船代公司")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('dms:forwarder-company-info:delete')")
    public CommonResult<Boolean> deleteForwarderCompanyInfo(@RequestParam("id") Long id) {
        forwarderCompanyInfoService.deleteForwarderCompanyInfo(id);
        return success(true);
    }

    @GetMapping("/detail")
    @Operation(summary = "获得船代公司")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('dms:forwarder-company-info:query')")
    public CommonResult<ForwarderCompanyInfoRespVO> getForwarderCompanyInfo(@RequestParam("id") Long id) {
        ForwarderCompanyInfoRespVO forwarderCompanyInfo = forwarderCompanyInfoService.getForwarderCompanyInfo(id);
        return success(forwarderCompanyInfo);
    }

    @GetMapping("/page")
    @Operation(summary = "获得船代公司分页")
    @PreAuthorize("@ss.hasPermission('dms:forwarder-company-info:query')")
    public CommonResult<PageResult<ForwarderCompanyInfoRespVO>> getForwarderCompanyInfoPage(@Valid ForwarderCompanyInfoPageReqVO pageReqVO) {
        PageResult<ForwarderCompanyInfoDO> pageResult = forwarderCompanyInfoService.getForwarderCompanyInfoPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ForwarderCompanyInfoRespVO.class));
    }

    @GetMapping("/get-simple-list")
    @Operation(summary = "获得船代公司精简分页")
    public CommonResult<PageResult<ForwarderCompanyInfoRespVO>> getSimpleForwarderCompanyInfoPage(@Valid SimpleForwarderCompanyInfoPageReqVO pageReqVO) {
        PageResult<ForwarderCompanyInfoDO> pageResult = forwarderCompanyInfoService.getSimpleForwarderCompanyInfoPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ForwarderCompanyInfoRespVO.class));
    }


    @GetMapping("/export-excel")
    @Operation(summary = "导出船代公司 Excel")
    @PreAuthorize("@ss.hasPermission('dms:forwarder-company-info:export')")
    @OperateLog(type = EXPORT)
    public void exportForwarderCompanyInfoExcel(@Valid ForwarderCompanyInfoPageReqVO pageReqVO,
                                                HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ForwarderCompanyInfoDO> list = forwarderCompanyInfoService.getForwarderCompanyInfoPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "船代公司.xls", "数据", ForwarderCompanyInfoRespVO.class,
                BeanUtils.toBean(list, ForwarderCompanyInfoRespVO.class));
    }


}