package com.syj.eplus.module.oa.controller.admin.reimbrepaydetail;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import com.syj.eplus.module.oa.controller.admin.reimbrepaydetail.vo.ReimbRepayDetailPageReqVO;
import com.syj.eplus.module.oa.controller.admin.reimbrepaydetail.vo.ReimbRepayDetailRespVO;
import com.syj.eplus.module.oa.controller.admin.reimbrepaydetail.vo.ReimbRepayDetailSaveReqVO;
import com.syj.eplus.module.oa.dal.dataobject.reimbrepaydetail.ReimbRepayDetailDO;
import com.syj.eplus.module.oa.service.reimbrepaydetail.ReimbRepayDetailService;
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

@Tag(name = "管理后台 - 报销还款详情")
@RestController
@RequestMapping("/oa/reimb-repay-detail")
@Validated
public class ReimbRepayDetailController {

    @Resource
    private ReimbRepayDetailService reimbRepayDetailService;

    @PostMapping("/create")
    @Operation(summary = "创建报销还款详情")
    @PreAuthorize("@ss.hasPermission('oa:reimb-repay-detail:create')")
    public CommonResult<Long> createReimbRepayDetail(@Valid @RequestBody ReimbRepayDetailSaveReqVO createReqVO) {
        return success(reimbRepayDetailService.createReimbRepayDetail(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新报销还款详情")
    @PreAuthorize("@ss.hasPermission('oa:reimb-repay-detail:update')")
    public CommonResult<Boolean> updateReimbRepayDetail(@Valid @RequestBody ReimbRepayDetailSaveReqVO updateReqVO) {
        reimbRepayDetailService.updateReimbRepayDetail(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除报销还款详情")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('oa:reimb-repay-detail:delete')")
    public CommonResult<Boolean> deleteReimbRepayDetail(@RequestParam("id") Long id) {
        reimbRepayDetailService.deleteReimbRepayDetail(id);
        return success(true);
    }

    @GetMapping("/detail")
    @Operation(summary = "获得报销还款详情")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('oa:reimb-repay-detail:query')")
    public CommonResult<ReimbRepayDetailRespVO> getReimbRepayDetail(@RequestParam("id") Long id) {
        ReimbRepayDetailRespVO reimbRepayDetail = reimbRepayDetailService.getReimbRepayDetail(id);
        return success(reimbRepayDetail);
    }

    @GetMapping("/page")
    @Operation(summary = "获得报销还款详情分页")
    @PreAuthorize("@ss.hasPermission('oa:reimb-repay-detail:query')")
    public CommonResult<PageResult<ReimbRepayDetailRespVO>> getReimbRepayDetailPage(@Valid ReimbRepayDetailPageReqVO pageReqVO) {
        PageResult<ReimbRepayDetailDO> pageResult = reimbRepayDetailService.getReimbRepayDetailPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ReimbRepayDetailRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出报销还款详情 Excel")
    @PreAuthorize("@ss.hasPermission('oa:reimb-repay-detail:export')")
    @OperateLog(type = EXPORT)
    public void exportReimbRepayDetailExcel(@Valid ReimbRepayDetailPageReqVO pageReqVO,
                                            HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ReimbRepayDetailDO> list = reimbRepayDetailService.getReimbRepayDetailPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "报销还款详情.xls", "数据", ReimbRepayDetailRespVO.class,
                BeanUtils.toBean(list, ReimbRepayDetailRespVO.class));
    }


}