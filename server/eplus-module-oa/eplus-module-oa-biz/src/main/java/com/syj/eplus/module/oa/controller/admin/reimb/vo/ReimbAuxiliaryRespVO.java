package com.syj.eplus.module.oa.controller.admin.reimb.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: du
 * @Date: 2024/08/21/15:16
 * @Description:
 */
@Data
@Schema(description = "报销费用归集返回体")
public class ReimbAuxiliaryRespVO {

    private String code;

    private String companyName;

    private Integer reimbType;

    private String description;

}
