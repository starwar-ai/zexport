package com.syj.eplus.module.oa.controller.admin.feeshare.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.module.oa.dal.dataobject.feeshare.FeeShareDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - 费用归集 Response VO")
@Data
@ExcelIgnoreUnannotated
public class FeeSharePageRespVO extends FeeShareDO {

    private List<String> feeShareDetailList;

    private  JsonAmount amount;
}