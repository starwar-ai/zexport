package com.syj.eplus.module.infra.controller.admin.orderlink;


import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import com.syj.eplus.module.infra.api.orderlink.dto.OrderLinkDTO;
import com.syj.eplus.module.infra.service.orderlink.OrderLinkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 订单链路")
@RestController
@RequestMapping("/system/orderlink")
@Validated
public class OrderLinkController {

    @Resource
    private OrderLinkService orderLinkService;
    @GetMapping("/get")
    @Operation(summary = "批量校验明细链路")
    @PreAuthorize("@ss.hasPermission('system:orderlink:query')")
    public CommonResult<Boolean> getSn(@RequestParam("codes") List<String> codes) {
        boolean b = orderLinkService.validateOrderLinkExists(codes);
        return success(b);
    }

    @GetMapping("/get-single-node")
    @Operation(summary = "获取链路单节点")
    public CommonResult<List<OrderLinkDTO>> getSn(@RequestParam("code")String code,
                                                  @RequestParam("name")String name,
                                                  @RequestParam("linkCode")String linkCode,
                                                  @RequestParam("orderType")Integer orderType) {
        return success(orderLinkService.getOrderLinkDTOByCodeAndName(code, name,linkCode,orderType));
    }

    @GetMapping("/get-tree-by-link-code")
    @Operation(summary = "获取节点树")
    public CommonResult<OrderLinkDTO> getTree(@RequestParam("linkCode")String linkCode) {
        List<OrderLinkDTO> orderLinkDTO = orderLinkService.getOrderLinkDTO(List.of(linkCode));
        if (CollUtil.isEmpty(orderLinkDTO)){
            return success(null);
        }
        Optional<OrderLinkDTO> optional = orderLinkDTO.stream().findFirst();
        return optional.map(CommonResult::success).orElseGet(() -> success(null));
    }

    @GetMapping("/get-order-link-list")
    @Operation(summary = "获取链路列表")
    public CommonResult<List<OrderLinkDTO>> getOrderLinkList(@RequestParam("linkCodeList")List<String> linkCodeList) {
        List<OrderLinkDTO> result = orderLinkService.getOrderLinkListByLinkCode(linkCodeList);
        return success(result);
    }
}
