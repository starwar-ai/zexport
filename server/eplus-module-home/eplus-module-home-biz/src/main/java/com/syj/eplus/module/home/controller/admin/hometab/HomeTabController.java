package com.syj.eplus.module.home.controller.admin.hometab;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import com.syj.eplus.module.infra.api.card.dto.CardRespDTO;
import com.syj.eplus.module.home.controller.admin.hometab.vo.*;
import com.syj.eplus.module.home.dal.dataobject.hometab.HomeTabDO;
import com.syj.eplus.module.home.service.hometab.HomeTabService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.operatelog.core.enums.OperateTypeEnum.EXPORT;


@Tag(name = "管理后台 - 系统首页")
@RestController
@RequestMapping("/system/home-tab")
@Validated
public class HomeTabController {

    @Resource
    private HomeTabService homeTabService;

    @PostMapping("/create")
    @Operation(summary = "创建系统首页")
    public CommonResult<Long> createHomeTab(@Valid @RequestBody HomeTabSaveReqVO createReqVO) {
        return success(homeTabService.createHomeTab(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新系统首页")
    public CommonResult<Boolean> updateHomeTab(@Valid @RequestBody HomeTabSaveReqVO updateReqVO) {
        homeTabService.updateHomeTab(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除系统首页")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> deleteHomeTab(@RequestParam("id") Long id) {
        homeTabService.deleteHomeTab(id);
        return success(true);
    }

    @GetMapping("/detail")
    @Operation(summary = "获得系统首页")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<HomeTabRespVO> getHomeTab(@RequestParam("id") Long id) {
        HomeTabRespVO homeTab = homeTabService.getHomeTab(id);
        return success(homeTab);
    }

    @GetMapping("/page")
    @Operation(summary = "获得系统首页分页")
    public CommonResult<PageResult<HomeTabRespVO>> getHomeTabPage(@Valid HomeTabPageReqVO pageReqVO) {
        PageResult<HomeTabDO> pageResult = homeTabService.getHomeTabPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, HomeTabRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出系统首页 Excel")
    @OperateLog(type = EXPORT)
    public void exportHomeTabExcel(@Valid HomeTabPageReqVO pageReqVO,
                                   HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<HomeTabDO> list = homeTabService.getHomeTabPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "系统首页.xls", "数据", HomeTabRespVO.class,
                BeanUtils.toBean(list, HomeTabRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得系统首页列表")
    public CommonResult<List<HomeTabRespVO>> getHomeTabList(HomeTabListReq homeTabListReq) {
        List<HomeTabRespVO> homeTabPage = homeTabService.getHomeTabList(homeTabListReq.getUserId(),homeTabListReq.getRoleId());
        return success(homeTabPage);
    }


    @PutMapping("/sort")
    @Operation(summary = "页签排序")
    public CommonResult<Boolean> sortHomeTab(@Valid @RequestBody HomeTabSortReqVO sortReqVO) {
        homeTabService.sort(sortReqVO);
        return success(true);
    }

    @GetMapping("/get-card")
    @Operation(summary = "获得页签内卡片信息")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<List<CardRespDTO>> getHomeTabCardList(@RequestParam Long id) {
        List<CardRespDTO> homeTabCardList = homeTabService.getHomeTabCardList(id);
        return success(homeTabCardList);
    }


    @GetMapping("/get-card-by-role")
    @Operation(summary = "根据角色查询卡片id")
    public CommonResult<List<Long>> getCardListByRole(@RequestParam Long id) {
        return success( homeTabService.getCardListByRole(id));
    }
}