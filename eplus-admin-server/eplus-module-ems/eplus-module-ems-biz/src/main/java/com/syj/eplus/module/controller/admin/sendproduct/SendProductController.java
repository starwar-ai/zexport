package com.syj.eplus.module.controller.admin.sendproduct;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import com.syj.eplus.module.controller.admin.sendproduct.vo.SendProductPageReqVO;
import com.syj.eplus.module.controller.admin.sendproduct.vo.SendProductRespVO;
import com.syj.eplus.module.controller.admin.sendproduct.vo.SendProductSaveReqVO;
import com.syj.eplus.module.dal.dataobject.sendproduct.SendProductDO;
import com.syj.eplus.module.service.sendproduct.SendProductService;
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

@Tag(name = "管理后台 - 寄件产品")
@RestController
@RequestMapping("/ems/send-product")
@Validated
public class SendProductController {


    @Resource
    private SendProductService sendProductService;

    @PostMapping("/create")
    @Operation(summary = "创建寄件产品")
    @PreAuthorize("@ss.hasPermission('ems:send-product:create')")
    public CommonResult<Long> createSendProduct(@Valid @RequestBody SendProductSaveReqVO createReqVO) {
        return success(sendProductService.createSendProduct(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新寄件产品")
    @PreAuthorize("@ss.hasPermission('ems:send-product:update')")
    public CommonResult<Boolean> updateSendProduct(@Valid @RequestBody SendProductSaveReqVO updateReqVO) {
        sendProductService.updateSendProduct(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除寄件产品")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('ems:send-product:delete')")
    public CommonResult<Boolean> deleteSendProduct(@RequestParam("id") Long id) {
        sendProductService.deleteSendProduct(id);
        return success(true);
    }

        @GetMapping("/detail")
    @Operation(summary = "获得寄件产品")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('ems:send-product:query')")
    public CommonResult<SendProductRespVO> getSendProduct(@RequestParam("id") Long id) {
            SendProductRespVO sendProduct = sendProductService.getSendProduct(id);
            return success(sendProduct);
    }
    @GetMapping("/page")
    @Operation(summary = "获得寄件产品分页")
    @PreAuthorize("@ss.hasPermission('ems:send-product:query')")
    public CommonResult<PageResult<SendProductRespVO>> getSendProductPage(@Valid SendProductPageReqVO pageReqVO) {
        PageResult<SendProductDO> pageResult = sendProductService.getSendProductPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, SendProductRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出寄件产品 Excel")
    @PreAuthorize("@ss.hasPermission('ems:send-product:export')")
    @OperateLog(type = EXPORT)
    public void exportSendProductExcel(@Valid SendProductPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<SendProductDO> list = sendProductService.getSendProductPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "寄件产品.xls", "数据", SendProductRespVO.class,
                        BeanUtils.toBean(list, SendProductRespVO.class));
    }


}