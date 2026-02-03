package com.syj.eplus.module.oa.controller.admin.subject;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import com.syj.eplus.module.oa.controller.admin.subject.vo.SubjectPageReqVO;
import com.syj.eplus.module.oa.controller.admin.subject.vo.SubjectRespVO;
import com.syj.eplus.module.oa.controller.admin.subject.vo.SubjectSaveReqVO;
import com.syj.eplus.module.oa.controller.admin.subject.vo.SubjectSimpleRespVO;
import com.syj.eplus.module.oa.dal.dataobject.subject.SubjectDO;
import com.syj.eplus.module.oa.service.subject.SubjectService;
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

@Tag(name = "管理后台 - 科目")
@RestController
@RequestMapping("/oa/subject")
@Validated
public class SubjectController {

    @Resource
    private SubjectService subjectService;

    @PostMapping("/create")
    @Operation(summary = "创建科目")
    @PreAuthorize("@ss.hasPermission('oa:subject:create')")
    public CommonResult<Long> createSubject(@Valid @RequestBody SubjectSaveReqVO createReqVO) {
        return success(subjectService.createSubject(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新科目")
    @PreAuthorize("@ss.hasPermission('oa:subject:update')")
    public CommonResult<Boolean> updateSubject(@Valid @RequestBody SubjectSaveReqVO updateReqVO) {
        subjectService.updateSubject(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除科目")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('oa:subject:delete')")
    public CommonResult<Boolean> deleteSubject(@RequestParam("id") Long id) {
        subjectService.deleteSubject(id);
        return success(true);
    }

    @GetMapping("/detail")
    @Operation(summary = "获得科目")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('oa:subject:query')")
    public CommonResult<SubjectRespVO> getSubject(@RequestParam("id") Long id) {
            SubjectRespVO subject = subjectService.getSubject (id);
            return success(subject);
    }

    @GetMapping("/page")
    @Operation(summary = "获得科目分页")
    @PreAuthorize("@ss.hasPermission('oa:subject:query')")
    public CommonResult<PageResult<SubjectRespVO>> getSubjectPage(@Valid SubjectPageReqVO pageReqVO) {
        PageResult<SubjectDO> pageResult = subjectService.getSubjectPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, SubjectRespVO.class));
    }


    @GetMapping("/get-simple-list")
    @Operation(summary = "获得科目分类树")
    public CommonResult<List<SubjectSimpleRespVO>> getSubjectList() {
        List<SubjectSimpleRespVO> simpleList = subjectService.getSimpleList();
        return success(simpleList);
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出科目 Excel")
    @PreAuthorize("@ss.hasPermission('oa:subject:export')")
    @OperateLog(type = EXPORT)
    public void exportSubjectExcel(@Valid SubjectPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<SubjectDO> list = subjectService.getSubjectPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "科目.xls", "数据", SubjectRespVO.class,
                        BeanUtils.toBean(list, SubjectRespVO.class));
    }


}