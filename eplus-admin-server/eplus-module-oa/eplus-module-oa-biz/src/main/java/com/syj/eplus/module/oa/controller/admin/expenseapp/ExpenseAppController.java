package com.syj.eplus.module.oa.controller.admin.expenseapp;


import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.syj.eplus.module.oa.controller.admin.expenseapp.vo.ExpenseAppPageReqVO;
import com.syj.eplus.module.oa.dal.dataobject.expenseapp.ExpenseApp;
import com.syj.eplus.module.oa.service.expenseapp.ExpenseAppService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 招待费申请")
@RestController
@RequestMapping("/oa/expense")
@Validated
public class ExpenseAppController {

    @Resource
    private ExpenseAppService expenseAppService;
    @GetMapping("/page")
    @Operation(summary = "获得招待费申请分页")
//    @PreAuthorize("@ss.hasPermission('oa:expense:query')")
    public CommonResult<PageResult<ExpenseApp>> getExpenseAppPage(@Valid ExpenseAppPageReqVO pageReqVO) {
        PageResult<ExpenseApp> expenseAppPage = expenseAppService.getExpenseAppPage(pageReqVO);
        return success(expenseAppPage);
    }

}
