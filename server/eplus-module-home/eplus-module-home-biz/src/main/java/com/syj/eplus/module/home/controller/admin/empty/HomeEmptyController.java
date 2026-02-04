package com.syj.eplus.module.home.controller.admin.empty;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;


@Tag(name = "管理后台 - 无意义请求")
@RestController
@RequestMapping("/system")
@Validated
public class HomeEmptyController {

    /**
     * 主要为了前端请求后，可以刷新refreshToken
     * @return
     */
    @GetMapping("/empty")
    @Operation(summary = "无意义请求")
    public CommonResult<Boolean> emptyRequest() {
        return success(true);
    }
}