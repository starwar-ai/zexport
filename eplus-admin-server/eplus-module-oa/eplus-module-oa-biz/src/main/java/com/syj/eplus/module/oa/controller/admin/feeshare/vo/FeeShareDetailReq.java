package com.syj.eplus.module.oa.controller.admin.feeshare.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: du
 * @Date: 2024/08/21/19:46
 * @Description:
 */
@Data
@Accessors(chain = true)
public class FeeShareDetailReq {
    @Schema(description = "流程任务名", example = "eplus")
    private String processInstanceId;

    @Schema(description = "客户id", example = "1")
    private Long id;
}
