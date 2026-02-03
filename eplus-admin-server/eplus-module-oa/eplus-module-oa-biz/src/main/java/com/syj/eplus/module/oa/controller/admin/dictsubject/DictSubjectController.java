package com.syj.eplus.module.oa.controller.admin.dictsubject;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import com.syj.eplus.module.oa.controller.admin.dictsubject.vo.DictFeeRespVO;
import com.syj.eplus.module.oa.controller.admin.dictsubject.vo.DictSubjectPageReqVO;
import com.syj.eplus.module.oa.controller.admin.dictsubject.vo.DictSubjectRespVO;
import com.syj.eplus.module.oa.controller.admin.dictsubject.vo.DictSubjectSaveReqVO;
import com.syj.eplus.module.oa.dal.dataobject.dictsubject.DictSubjectDO;
import com.syj.eplus.module.oa.service.dictsubject.DictSubjectService;
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

@Tag(name = "管理后台 - 类别配置")
@RestController
@RequestMapping("/oa/dict-subject")
@Validated
public class DictSubjectController {

    @Resource
    private DictSubjectService dictSubjectService;

    @PostMapping("/create")
    @Operation(summary = "创建类别配置")
    @PreAuthorize("@ss.hasPermission('oa:dict-subject:create')")
    public CommonResult<Long> createDictSubject(@Valid @RequestBody DictSubjectSaveReqVO createReqVO) {
        return success(dictSubjectService.createDictSubject(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新类别配置")
    @PreAuthorize("@ss.hasPermission('oa:dict-subject:update')")
    public CommonResult<Boolean> updateDictSubject(@Valid @RequestBody DictSubjectSaveReqVO updateReqVO) {
        dictSubjectService.updateDictSubject(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除类别配置")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('oa:dict-subject:delete')")
    public CommonResult<Boolean> deleteDictSubject(@RequestParam("id") Long id) {
        dictSubjectService.deleteDictSubject(id);
        return success(true);
    }

    @GetMapping("/detail")
    @Operation(summary = "获得类别配置")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('oa:dict-subject:query')")
    public CommonResult<DictSubjectRespVO> getDictSubject(@RequestParam("id") Long id) {
            DictSubjectRespVO dictSubject = dictSubjectService.getDictSubject (id);
            return success(dictSubject);
    }

    @GetMapping("/getSubject")
    @Operation(summary = "获得科目")
    @Parameter(name = "systemDictType", description = "字典类型", required = true, example = "1024")
    public CommonResult<List<DictSubjectDO>> getSubject(@RequestParam("systemDictType") String systemDictType) {
        List<DictSubjectDO> dictSubject = dictSubjectService.getSubject (systemDictType);
        return success(dictSubject);
    }

    @GetMapping("/page")
    @Operation(summary = "获得类别配置分页")
    @PreAuthorize("@ss.hasPermission('oa:dict-subject:query')")
    public CommonResult<PageResult<DictSubjectRespVO>> getDictSubjectPage(@Valid DictSubjectPageReqVO pageReqVO) {
        PageResult<DictSubjectRespVO> pageResult = dictSubjectService.getDictSubjectPage(pageReqVO);
        return success(pageResult);
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出类别配置 Excel")
    @PreAuthorize("@ss.hasPermission('oa:dict-subject:export')")
    @OperateLog(type = EXPORT)
    public void exportDictSubjectExcel(@Valid DictSubjectPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<DictSubjectRespVO> list = dictSubjectService.getDictSubjectPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "类别配置.xls", "数据", DictSubjectRespVO.class,
                       list);
    }


    @GetMapping("/get-fee-list")
    @Operation(summary = "获取精简列表")
    public CommonResult<List<DictFeeRespVO>> getSimpleList() {
        List<DictFeeRespVO> list = dictSubjectService.getFeeList();
        return success(list);
    }


}