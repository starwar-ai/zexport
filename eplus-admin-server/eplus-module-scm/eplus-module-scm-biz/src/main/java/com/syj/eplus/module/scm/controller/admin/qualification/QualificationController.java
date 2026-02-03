package com.syj.eplus.module.scm.controller.admin.qualification;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import com.syj.eplus.module.scm.controller.admin.qualification.vo.QualificationPageReqVO;
import com.syj.eplus.module.scm.controller.admin.qualification.vo.QualificationRespVO;
import com.syj.eplus.module.scm.controller.admin.qualification.vo.QualificationSaveReqVO;
import com.syj.eplus.module.scm.dal.dataobject.qualification.QualificationDO;
import com.syj.eplus.module.scm.service.qualification.QualificationService;
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

@Tag(name = "管理后台 - 资质")
@RestController
@RequestMapping("/scm/qualification")
@Validated
public class QualificationController {

    @Resource
    private QualificationService qualificationService;

    @PostMapping("/create")
    @Operation(summary = "创建资质")
    @PreAuthorize("@ss.hasPermission('scm:qualification:create')")
    public CommonResult<Long> createQualification(@Valid @RequestBody QualificationSaveReqVO createReqVO) {
        return success(qualificationService.createQualification(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新资质")
    @PreAuthorize("@ss.hasPermission('scm:qualification:update')")
    public CommonResult<Boolean> updateQualification(@Valid @RequestBody QualificationSaveReqVO updateReqVO) {
        qualificationService.updateQualification(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除资质")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('scm:qualification:delete')")
    public CommonResult<Boolean> deleteQualification(@RequestParam("id") Long id) {
        qualificationService.deleteQualification(id);
        return success(true);
    }

    @GetMapping("/detail")
    @Operation(summary = "获得资质")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('scm:qualification:query')")
    public CommonResult<QualificationRespVO> getQualification(@RequestParam("id") Long id) {
            QualificationRespVO qualification = qualificationService.getQualification (id);
            return success(qualification);
    }
    @GetMapping("/page")
    @Operation(summary = "获得资质分页")
    public CommonResult<PageResult<QualificationRespVO>> getQualificationPage(@Valid QualificationPageReqVO pageReqVO) {
        PageResult<QualificationDO> pageResult = qualificationService.getQualificationPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, QualificationRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出资质 Excel")
    @PreAuthorize("@ss.hasPermission('scm:qualification:export')")
    @OperateLog(type = EXPORT)
    public void exportQualificationExcel(@Valid QualificationPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<QualificationDO> list = qualificationService.getQualificationPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "资质.xls", "数据", QualificationRespVO.class,
                        BeanUtils.toBean(list, QualificationRespVO.class));
    }


}