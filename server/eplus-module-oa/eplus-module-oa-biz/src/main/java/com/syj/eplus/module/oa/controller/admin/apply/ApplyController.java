package com.syj.eplus.module.oa.controller.admin.apply;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;
import com.syj.eplus.module.oa.controller.admin.apply.vo.ApplyPageReqVO;
import com.syj.eplus.module.oa.controller.admin.apply.vo.ApplyRespVO;
import com.syj.eplus.module.oa.service.apply.ApplyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 费用申请")
@RestController
@RequestMapping("/oa/apply")
public class ApplyController {

    @Resource
    private ApplyService applyService;

    @GetMapping("/get-simple-list")
    @Operation(summary = "获得费用报销申请单分页")
    @DataPermission(enable = false)
    public CommonResult<PageResult<ApplyRespVO>> getApplyPageSimple(@Valid ApplyPageReqVO pageReqVO) {
        return success( applyService.getApplyPage(pageReqVO));
    }

}