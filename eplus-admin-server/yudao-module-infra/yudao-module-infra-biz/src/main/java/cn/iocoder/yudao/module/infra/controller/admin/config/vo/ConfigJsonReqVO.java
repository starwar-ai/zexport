package cn.iocoder.yudao.module.infra.controller.admin.config.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

@Schema(description = "管理后台")
@Data
@ToString(callSuper = true)
public class ConfigJsonReqVO {

    private String configType;
    private String type;


}
