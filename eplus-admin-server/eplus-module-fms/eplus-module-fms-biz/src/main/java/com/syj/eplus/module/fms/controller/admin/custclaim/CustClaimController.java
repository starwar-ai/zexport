package com.syj.eplus.module.fms.controller.admin.custclaim;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.syj.eplus.module.fms.controller.admin.bankregistration.vo.SimpleRegistrationResp;
import com.syj.eplus.module.fms.controller.admin.custclaim.vo.CustClaimPageReq;
import com.syj.eplus.module.fms.controller.admin.custclaim.vo.CustClaimSaveReqVO;
import com.syj.eplus.module.fms.dal.dataobject.custclaim.CustClaimItem;
import com.syj.eplus.module.fms.service.custclaim.CustClaimService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * @Description：
 * @Author：du
 * @Date：2024/7/23 15:32
 */
@Tag(name = "管理后台 - 客户认领")
@RestController
@RequestMapping("/fms/cust-claim")
@Validated
public class CustClaimController {

    @Resource
    private CustClaimService custClaimService;

    @GetMapping("/list")
    @Operation(summary = "客户认领列表")
    @PreAuthorize("@ss.hasPermission('fms:cust-claim:query')")
    public CommonResult<PageResult<SimpleRegistrationResp>> getReceipt(CustClaimPageReq custClaimPageReq) {
        PageResult<SimpleRegistrationResp> custClaimList = custClaimService.getCustClaimList(custClaimPageReq);
        return success(custClaimList);
    }

    @GetMapping("/claim")
    @Operation(summary = "获得认领信息")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('fms:cust-claim:query')")
    public CommonResult<SimpleRegistrationResp> getMidCustClaim(@RequestParam("id") Long id) {
        SimpleRegistrationResp custClaim = custClaimService.getCustClaim(id);
        return success(custClaim);
    }

    @PostMapping("/save")
    @Operation(summary = "创建认领明细")
    @PreAuthorize("@ss.hasPermission('fms:cust-claim:create')")
    public CommonResult<Boolean> createBankRegistration(@Valid @RequestBody CustClaimSaveReqVO createReqVO) {
        custClaimService.createCustClaim(createReqVO);
        return success(true);
    }

    @PutMapping("/cancel")
    @Operation(summary = "取消认领")
    @PreAuthorize("@ss.hasPermission('fms:cust-claim:cancel')")
    public CommonResult<Boolean> cancelBankRegistration(@RequestParam Long id) {
        custClaimService.cancelCustClaim(id);
        return success(true);
    }

    @GetMapping("/detail")
    @Operation(summary = "获得认领详情")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('fms:cust-claim:query')")
    public CommonResult<SimpleRegistrationResp> getMidCustClaimDetail(@RequestParam("id") Long id) {
        SimpleRegistrationResp custClaim = custClaimService.getCustClaimDetail(id);
        return success(custClaim);
    }

    @GetMapping("/claim-item")
    @Operation(summary = "获得客户认领明细")
    @PreAuthorize("@ss.hasPermission('fms:cust-claim:query')")
    public CommonResult<List<CustClaimItem>> getMidCustClaimItem(@RequestParam("custCodeList") List<String> custCodeList,@RequestParam("companyId")Long companyId) {
        List<CustClaimItem> custClaimItemList = custClaimService.getCustClaimItemList(custCodeList,companyId);
        return success(custClaimItemList);
    }
}
