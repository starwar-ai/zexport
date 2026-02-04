package com.syj.eplus.module.crm.controller.admin.collectionaccount;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import com.syj.eplus.module.crm.controller.admin.collectionaccount.vo.CollectionAccountPageReqVO;
import com.syj.eplus.module.crm.controller.admin.collectionaccount.vo.CollectionAccountRespVO;
import com.syj.eplus.module.crm.controller.admin.collectionaccount.vo.CollectionAccountSaveReqVO;
import com.syj.eplus.module.crm.dal.dataobject.collectionaccount.CollectionAccountDO;
import com.syj.eplus.module.crm.service.collectionaccount.CollectionAccountService;
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

@Tag(name = "管理后台 - 收款账号")
@RestController
@RequestMapping("/crm/collection-account")
@Validated
public class CollectionAccountController {

    @Resource
    private CollectionAccountService collectionAccountService;

    @PostMapping("/create")
    @Operation(summary = "创建收款账号")
    @PreAuthorize("@ss.hasPermission('crm:collection-account:create')")
    public CommonResult<Long> createCollectionAccount(@Valid @RequestBody CollectionAccountSaveReqVO createReqVO) {
        return success(collectionAccountService.createCollectionAccount(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新收款账号")
    @PreAuthorize("@ss.hasPermission('crm:collection-account:update')")
    public CommonResult<Boolean> updateCollectionAccount(@Valid @RequestBody CollectionAccountSaveReqVO updateReqVO) {
        collectionAccountService.updateCollectionAccount(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除收款账号")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('crm:collection-account:delete')")
    public CommonResult<Boolean> deleteCollectionAccount(@RequestParam("id") Long id) {
        collectionAccountService.deleteCollectionAccount(id);
        return success(true);
    }

    @GetMapping("/detail")
    @Operation(summary = "获得收款账号")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('crm:collection-account:query')")
    public CommonResult<CollectionAccountRespVO> getCollectionAccount(@RequestParam("id") Long id) {
        CollectionAccountRespVO collectionAccount = collectionAccountService.getCollectionAccount( id);
        return success(collectionAccount);
    }
    @GetMapping("/page")
    @Operation(summary = "获得收款账号分页")
    @PreAuthorize("@ss.hasPermission('crm:collection-account:query')")
    public CommonResult<PageResult<CollectionAccountRespVO>> getCollectionAccountPage(@Valid CollectionAccountPageReqVO pageReqVO) {
        PageResult<CollectionAccountDO> pageResult = collectionAccountService.getCollectionAccountPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, CollectionAccountRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出收款账号 Excel")
    @PreAuthorize("@ss.hasPermission('crm:collection-account:export')")
    @OperateLog(type = EXPORT)
    public void exportCollectionAccountExcel(@Valid CollectionAccountPageReqVO pageReqVO,
                                             HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<CollectionAccountDO> list = collectionAccountService.getCollectionAccountPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "收款账号.xls", "数据", CollectionAccountRespVO.class,
                BeanUtils.toBean(list, CollectionAccountRespVO.class));
    }


    @GetMapping("/get-by-cust")
    @Operation(summary = "获得收款账号")
    @Parameter(name = "custCode", description = "客户编号", required = true, example = "1024")
    @Parameter(name = "companyId", description = "主体id", required = true, example = "1024")
    public CommonResult<CollectionAccountRespVO> getCollectionAccountByCust(@RequestParam("custCode") String custCode,@RequestParam("companyId") Long companyId) {
        CollectionAccountRespVO collectionAccount = collectionAccountService.getCollectionAccountByCust( custCode,companyId);
        return success(collectionAccount);
    }


}