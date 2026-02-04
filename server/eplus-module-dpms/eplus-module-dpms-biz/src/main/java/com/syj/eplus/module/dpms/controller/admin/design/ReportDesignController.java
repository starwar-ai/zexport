package com.syj.eplus.module.dpms.controller.admin.design;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import com.syj.eplus.framework.common.enums.BooleanEnum;
import com.syj.eplus.module.dpms.controller.admin.design.vo.DesignStatisticRespVO;
import com.syj.eplus.module.dpms.service.design.DesignReportService;
import com.syj.eplus.module.dtms.api.design.dto.DesignPageReqDTO;
import com.syj.eplus.module.dtms.api.design.dto.DesignRespDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: du
 * @Date: 2024/08/27/16:15
 * @Description:
 */
@Tag(name = "管理后台 - 设计报表")
@RestController
@RequestMapping("/dpms/report/design")
@Validated
public class ReportDesignController {
    @Resource
    private DesignReportService designReportService;

    @GetMapping("/list")
    @Operation(summary = "获得列表")
    @PreAuthorize("@ss.hasPermission('dtms:design:query')")
    public CommonResult<List<DesignRespDTO>> getDesignPage(@Valid DesignPageReqDTO pageReqDTO) {
        pageReqDTO.setClaimFlag(BooleanEnum.NO.getValue());
        List<DesignRespDTO> designPage = designReportService.getDesignPage(pageReqDTO);
        return success(designPage);
    }


    @GetMapping("/count")
    @Operation(summary = "获得列表")
    @PreAuthorize("@ss.hasPermission('dtms:design:query')")
    public CommonResult<DesignStatisticRespVO> getdesignStatisticResp() {
        DesignStatisticRespVO designStatisticResp = designReportService.getDesignStatisticResp();
        return success(designStatisticResp);
    }
}
