package cn.iocoder.yudao.module.system.controller.admin.port.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
public class PortDetailReq {

    @Schema(description = "口岸id", example = "1")
    private Long portId;

}