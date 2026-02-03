package com.syj.eplus.module.scm.controller.admin.venderpoc.vo;

import cn.iocoder.yudao.framework.common.annotations.ChangeIgnore;
import cn.iocoder.yudao.framework.common.pojo.JsonEffectRange;
import cn.iocoder.yudao.framework.mybatis.core.exinterface.ChangeExInterface;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 供应商联系人 Response VO")
@Data
@ExcelIgnoreUnannotated
@Accessors(chain = false)
public class VenderPocRespVO implements ChangeExInterface {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "15917")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "版本", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("版本")
    private Integer ver;

    @Schema(description = "供应商id", requiredMode = Schema.RequiredMode.REQUIRED, example = "10693")
    @ExcelProperty("供应商id")
    private Long venderId;

    @Schema(description = "供应商版本", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("供应商版本")
    private Integer venderVer;

    @Schema(description = "名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @ExcelProperty("名称")
    private String name;

    @Schema(description = "职位")
    @ExcelProperty("职位")
    private String pocTypes;

    @Schema(description = "电子邮件")
    @ExcelProperty("电子邮件")
    private String email;

    @Schema(description = "手机")
    @ExcelProperty("手机")
    private String mobile;

    @Schema(description = "地址")
    @ExcelProperty("地址")
    private String address;

    @Schema(description = "是否默认联系人", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("是否默认联系人")
    private Integer defaultFlag;

    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    @ChangeIgnore
    private LocalDateTime createTime;

    @Schema(description = "座机")
    @ExcelProperty("座机")
    private String telephone;

    @Schema(description = "微信")
    @ExcelProperty("微信")
    private String wechat;

    @Schema(description = "QQ")
    @ExcelProperty("QQ")
    private String qq;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "变更标识")
    private Integer changeFlag;

    @TableField(exist = false)
    private String sourceCode;

    @TableField(exist = false)
    private List<JsonEffectRange> effectRangeList;
}