package com.syj.eplus.module.dpms.controller.admin.report.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: du
 * @Date: 2024/08/26/16:18
 * @Description:
 */
@Data
@Schema(description = "批量创建报表")
public class BatchReportSaveReqVO {

    @Schema(description = "报表列表")
    private List<Long> reportIds;
}
