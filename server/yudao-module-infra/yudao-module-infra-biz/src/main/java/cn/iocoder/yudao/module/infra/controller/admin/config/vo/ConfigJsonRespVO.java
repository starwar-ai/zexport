package cn.iocoder.yudao.module.infra.controller.admin.config.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

@Schema(description = "管理后台")
@Data
@ToString(callSuper = true)
public class ConfigJsonRespVO {

    private Integer key;
    private Integer value;
    private String name;
    private String label;
    private String type;

}
