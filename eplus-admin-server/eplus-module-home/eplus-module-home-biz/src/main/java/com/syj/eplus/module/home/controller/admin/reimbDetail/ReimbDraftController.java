package com.syj.eplus.module.home.controller.admin.reimbDetail;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.syj.eplus.module.home.controller.admin.reimbDetail.vo.ReimbDraftPageReqVO;
import com.syj.eplus.module.home.service.ReimbDraft.ReimbDraftService;
import com.syj.eplus.module.oa.api.dto.ReimbDetailDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: du
 * @Date: 2024/09/18/11:16
 * @Description:
 */
@Tag(name = "管理后台 - 报销草稿")
@RestController
@RequestMapping("/home/reimb-draft")
@Validated
public class ReimbDraftController {

    @Resource
    private ReimbDraftService reimbDraftService;
    @PostMapping("/create")
    @Operation(summary = "创建报销草稿")
    public CommonResult<Boolean> createReimbDraft(@Valid @RequestBody ReimbDetailDTO createReqVO) {
        reimbDraftService.createReimbDraft(createReqVO);
        return success(true);
    }


    @GetMapping("/page")
    @Operation(summary = "获得报销草稿分页")
    public CommonResult<PageResult<ReimbDetailDTO>> getReimbDraftPage(@Valid ReimbDraftPageReqVO pageReqVO) {
        PageResult<ReimbDetailDTO> reimbDraft = reimbDraftService.getReimbDraft(pageReqVO);
        return success(reimbDraft);
    }
}
