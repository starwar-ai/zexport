package com.syj.eplus.module.scm.controller.admin.vender.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Description：供应商详情请求封装实体(为区别不同入口)
 * @Author：chengbo
 * @Date：2024/1/21 19:48
 */
@Data
@Accessors(chain = true)
public class ScmVenderDetailReq {

    @Schema(description = "流程任务名", example = "eplus")
    private String processInstanceId;

    @Schema(description = "供应商id", example = "1")
    private Long venderId;
    @Schema(description = "供应商编号", example = "1")
    private String venderCode;
}
