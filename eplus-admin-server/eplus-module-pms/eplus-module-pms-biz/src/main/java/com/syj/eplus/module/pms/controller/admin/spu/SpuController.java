package com.syj.eplus.module.pms.controller.admin.spu;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import com.syj.eplus.module.pms.controller.admin.spu.vo.SpuInfoSaveReqVO;
import com.syj.eplus.module.pms.controller.admin.spu.vo.SpuPageReqVO;
import com.syj.eplus.module.pms.controller.admin.spu.vo.SpuRespVO;
import com.syj.eplus.module.pms.dal.dataobject.spu.SpuDO;
import com.syj.eplus.module.pms.service.spu.SpuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.error;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.operatelog.core.enums.OperateTypeEnum.EXPORT;


@Tag(name = "管理后台 - 商品")
@RestController
@RequestMapping("/pms/spu")
@Validated
public class SpuController {

    @Resource
    private SpuService spuService;

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @PostMapping("/create")
    @Operation(summary = "创建商品")
    @PreAuthorize("@ss.hasPermission('pms:spu:create')")
    public CommonResult<Long> createSpu(@Valid @RequestBody SpuInfoSaveReqVO createReqVO) {
        return success(spuService.createSpu(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新商品")
    @PreAuthorize("@ss.hasPermission('pms:spu:update')")
    public CommonResult<Boolean> updateSpu(@Valid @RequestBody SpuInfoSaveReqVO updateReqVO) {
        try {
            spuService.updateSpu(updateReqVO);
        } catch (Exception e) {
            logger.warn(e.getMessage());
            return error(INTERNAL_SERVER_ERROR);
        }
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除商品")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('pms:spu:delete')")
    public CommonResult<Boolean> deleteSpu(@RequestParam("id") Long id) {
        spuService.deleteSpu(id, true);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得商品")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('pms:spu:query')")
    public CommonResult<SpuRespVO> getSpu(@RequestParam("id") Long id) {
        SpuDO spu = spuService.getSpu(id);
        return success(BeanUtils.toBean(spu, SpuRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得商品分页")
    @PreAuthorize("@ss.hasPermission('pms:spu:query')")
    public CommonResult<PageResult<SpuRespVO>> getSpuPage(@Valid SpuPageReqVO pageReqVO) {
        PageResult<SpuDO> pageResult = spuService.getSpuPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, SpuRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出商品 Excel")
    @PreAuthorize("@ss.hasPermission('pms:spu:export')")
    @OperateLog(type = EXPORT)
    public void exportSpuExcel(@Valid SpuPageReqVO pageReqVO,
                               HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<SpuDO> list = spuService.getSpuPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "商品.xls", "数据", SpuRespVO.class,
                BeanUtils.toBean(list, SpuRespVO.class));
    }

}