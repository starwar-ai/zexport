package com.syj.eplus.module.oa.controller.admin.reimb;

import com.syj.eplus.module.oa.service.reimbService.ReimbService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@Tag(name = "管理后台 - 报销")
@RestController
@RequestMapping("/oa/reimb")
@Validated
public class ReimbController { // auxiliary

    @Resource
    private ReimbService reimbService;


//    @GetMapping("/auxiliary-page")
//    @Operation(summary = "获得报销归集分页分页")
//    @PreAuthorize("@ss.hasPermission('oa:reimb-auxiliary:query')")
//    public CommonResult<PageResult<ReimbRespVO>> getReimbAuxiliaryPage(@Valid ReimbAuxiliaryPageReqVO pageReqVO) {
//        PageResult<ReimbRespVO> reimbPage = reimbService.getReimbPage(pageReqVO);
//        return success(reimbPage);
//    }




}