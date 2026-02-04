package com.syj.eplus.module.dms.controller.admin.forwardercompanyinfo.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - 船代公司分页精简列表 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SimpleForwarderCompanyInfoPageReqVO extends PageParam {

    @Schema(description = "名称", example = "赵六")
    private String name;
}