package com.syj.eplus.module.infra.controller.admin.card;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import com.syj.eplus.module.infra.controller.admin.card.vo.CardPageReqVO;
import com.syj.eplus.module.infra.controller.admin.card.vo.CardPositionUpdateReqVO;
import com.syj.eplus.module.infra.controller.admin.card.vo.CardRespVO;
import com.syj.eplus.module.infra.controller.admin.card.vo.CardSaveReqVO;
import com.syj.eplus.module.infra.dal.dataobject.card.CardDO;
import com.syj.eplus.module.infra.service.card.CardService;
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

@Tag(name = "管理后台 - 首页卡片")
@RestController
@RequestMapping("/home/card")
@Validated
public class CardController {

    @Resource
    private CardService cardService;

    @PostMapping("/create")
    @Operation(summary = "创建首页卡片")
//    @PreAuthorize("@ss.hasPermission('home:card:create')")
    public CommonResult<Long> createCard(@Valid @RequestBody CardSaveReqVO createReqVO) {
        return success(cardService.createCard(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新首页卡片")
//    @PreAuthorize("@ss.hasPermission('home:card:update')")
    public CommonResult<Boolean> updateCard(@Valid @RequestBody CardSaveReqVO updateReqVO) {
        cardService.updateCard(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除首页卡片")
    @Parameter(name = "id", description = "编号", required = true)
//    @PreAuthorize("@ss.hasPermission('home:card:delete')")
    public CommonResult<Boolean> deleteCard(@RequestParam("id") Long id) {
        cardService.deleteCard(id);
        return success(true);
    }

    @GetMapping("/detail")
    @Operation(summary = "获得首页卡片")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
//    @PreAuthorize("@ss.hasPermission('home:card:query')")
    public CommonResult<CardRespVO> getCard(@RequestParam("id") Long id) {
        CardRespVO card = cardService.getCard(id);
        return success(card);
    }

    @GetMapping("/page")
    @Operation(summary = "获得首页卡片分页")
//    @PreAuthorize("@ss.hasPermission('home:card:query')")
    public CommonResult<PageResult<CardRespVO>> getCardPage(@Valid CardPageReqVO pageReqVO) {
        PageResult<CardDO> pageResult = cardService.getCardPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, CardRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出首页卡片 Excel")
//    @PreAuthorize("@ss.hasPermission('home:card:export')")
    @OperateLog(type = EXPORT)
    public void exportCardExcel(@Valid CardPageReqVO pageReqVO,
                                HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<CardDO> list = cardService.getCardPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "首页卡片.xls", "数据", CardRespVO.class,
                BeanUtils.toBean(list, CardRespVO.class));
    }

    @PutMapping("/enable")
    @Operation(summary = "启用")
//    @PreAuthorize("@ss.hasPermission('home:card:enable')")
    public CommonResult<Boolean> enableCard(@RequestParam Long id) {
        cardService.enableCard(id);
        return success(true);
    }

    @PutMapping("/disable")
    @Operation(summary = "禁用")
//    @PreAuthorize("@ss.hasPermission('home:card:disable')")
    public CommonResult<Boolean> disableCard(@RequestParam Long id) {
        cardService.disEnableCard(id);
        return success(true);
    }

    @GetMapping("/base-list")
    @Operation(summary = "获得首页卡片列表")
//    @PreAuthorize("@ss.hasPermission('home:card:query')")
    public CommonResult<List<CardRespVO>> getCardList(@RequestParam("ids") List<Long> ids) {
        List<CardRespVO> cardList = cardService.getCardList(ids);
        return success(cardList);
    }

    @PutMapping("/update/position")
    @Operation(summary = "卡片位置变更")
//    @PreAuthorize("@ss.hasPermission('home:card:update')")
    public CommonResult<Boolean> updateCardPosition(@RequestBody CardPositionUpdateReqVO updateReqVO) {
        cardService.updateCardPosition(updateReqVO);
        return success(true);
    }

    @GetMapping("/list")
    @Operation(summary = "获得首页卡片列表")
//    @PreAuthorize("@ss.hasPermission('home:card:query')")
    public CommonResult<List<CardRespVO>> getCardInfoList(@RequestParam Long id) {
        List<CardRespVO> cardInfoList = cardService.getCardInfoList(id);
        return success(cardInfoList);
    }

}