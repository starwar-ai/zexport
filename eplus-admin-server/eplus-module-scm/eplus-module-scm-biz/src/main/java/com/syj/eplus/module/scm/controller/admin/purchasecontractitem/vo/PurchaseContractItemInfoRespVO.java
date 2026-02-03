package com.syj.eplus.module.scm.controller.admin.purchasecontractitem.vo;

import cn.iocoder.yudao.framework.mybatis.core.exinterface.ChangeExInterface;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.syj.eplus.framework.common.annotations.CompareField;
import com.syj.eplus.framework.common.entity.SimpleData;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Schema(description = "管理后台 - 采购合同明细 Response VO")
@Data
@ExcelIgnoreUnannotated
@Accessors(chain = false)
public class PurchaseContractItemInfoRespVO extends  PurchaseContractItemRespVO implements ChangeExInterface {

    private List<SimpleData> auxiliarySkuList;

    @CompareField(value = "产品删除标记")
    private Integer skuDeletedFlag;
}