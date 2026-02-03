package com.syj.eplus.module.pms.controller.admin.spu.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @Description：
 * @Author：du
 * @Date：2024/2/7 15:14
 */
@Data
public class SpuInfoSaveReqVO extends SpuSaveReqVO {

    @Schema(description = "客户图片信息列表")
    private List<Long> pictureList;

    @Schema(description = "客户附件信息Id列表")
    private List<Long> fileAnnexList;
}
