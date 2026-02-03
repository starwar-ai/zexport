package com.syj.eplus.module.pms.controller.admin.sku;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;
import com.syj.eplus.framework.common.entity.ChangeEffectRespVO;
import com.syj.eplus.module.pms.api.sku.dto.SimpleSkuDTO;
import com.syj.eplus.module.pms.controller.admin.sku.vo.*;
import com.syj.eplus.module.pms.service.sku.SkuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - sku")
@RestController
@RequestMapping("/pms/sku")
@Validated
public class SkuController {

    @Resource
    private SkuService skuService;

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @GetMapping("/simple-sku-name")
    @Operation(summary = "获得sku精简名称列表")
    public CommonResult<List<String>> getSkuFuzzySearchName(@RequestParam String name) {
        List<String> skuNameFuzzySearchByName = skuService.getSkuNameFuzzySearchByName(name);
        return success(skuNameFuzzySearchByName);
    }


    @GetMapping("/simple-sku")
    @Operation(summary = "获得精简sku列表")
    public CommonResult<PageResult<SimpleSkuDTO>> getSimpleSkuDTO(SimpleSkuPageReqVO simpleSkuPageReqVO) {
        PageResult<SimpleSkuDTO> simpleSkuDTO = skuService.getSimpleSkuDTO(simpleSkuPageReqVO);
        return success(simpleSkuDTO);
    }

    @GetMapping("/simple-own-sku")
    @Operation(summary = "获得自营产品精简sku列表")
    public CommonResult<PageResult<SimpleSkuDTO>> getSimpleOwnBrandSkuDTO(SimpleOwnBrandSkuPageReqVO reqVO) {
        PageResult<SimpleSkuDTO> simpleSkuDTO = skuService.getSimpleOwnBrandSkuDTO(reqVO);
        return success(simpleSkuDTO);
    }

    @PostMapping("/change-effect")
    @Operation(summary = "获得变更影响列表")
    @PreAuthorize("@ss.hasPermission('pms:sku:change','pms:csku:change','pms:own-brand-sku:change','pms:auxiliary-sku:change')")
    @DataPermission(enable = false)
    public CommonResult<ChangeEffectRespVO> getChangeEffect(@Valid @RequestBody SkuInfoSaveReqVO changeReqVO) {
        SkuInfoRespVO sku = BeanUtils.toBean(changeReqVO, SkuInfoRespVO.class);
        sku.setVer(sku.getVer() + 1);
        return success(skuService.getChangeEffect(sku));
    }


    @GetMapping("/generate-code")
    @Operation(summary = "生成编号")
    @Parameter(name = "categoryId", description = "编号", required = true, example = "uuid")
    public CommonResult<String> getChangeSkuByProcessId(@RequestParam("categoryId") Long categoryId) {
        return success(skuService.generateCode(categoryId,0));
    }


    @PostMapping("/check-cust")
    @Operation(summary = "判断是否可以修改客户")
    public CommonResult<Boolean> toCheckCustChange(@RequestBody SkuCheckCustReqVO reqVO) {
        return success(skuService.toCheckCustChange(reqVO));
    }


    @GetMapping("/get-simple-detail")
    @Operation(summary = "获取产品信息")
    @Parameter(name = "skuCode", description = "编号", required = true)
    public CommonResult<SkuInfoRespVO> getSku(@RequestParam("skuCode") String skuCode) {
        SkuInfoRespVO sku = skuService.getSku(new SkuDetailReq().setSkuCode(skuCode));
        return success(sku);
    }

    @PutMapping("/update-thumbnail")
    @Operation(summary = "更新缩略图")
    public CommonResult<Boolean> updateThumbnail(@RequestParam Long limit) {
        skuService.updateThumbnail(limit);
        return success(true);
    }
}