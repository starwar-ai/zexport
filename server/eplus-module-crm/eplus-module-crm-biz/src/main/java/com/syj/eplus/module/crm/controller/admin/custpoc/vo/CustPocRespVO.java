package com.syj.eplus.module.crm.controller.admin.custpoc.vo;

import cn.iocoder.yudao.framework.common.annotations.ChangeIgnore;
import cn.iocoder.yudao.framework.common.pojo.JsonEffectRange;
import cn.iocoder.yudao.framework.excel.core.annotations.DictFormat;
import cn.iocoder.yudao.framework.excel.core.convert.DictConvert;
import cn.iocoder.yudao.framework.mybatis.core.exinterface.ChangeExInterface;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.framework.common.enums.DictTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 客户联系人 Response VO")
@Data
@ExcelIgnoreUnannotated
@Accessors(chain = false)
public class CustPocRespVO implements ChangeExInterface {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @Schema(description = "版本号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("版本号")
    private Integer ver;

    @Schema(description = "联系人姓名", requiredMode = Schema.RequiredMode.REQUIRED, example = "李四")
    @ExcelProperty("联系人姓名")
    private String name;

    @Schema(description = "联系人职位")
    @ExcelProperty("联系人职位")
    private String pocPosts;

    @Schema(description = "邮箱")
    @ExcelProperty("邮箱")
    private String email;

    @Schema(description = "联系人电话")
    @ExcelProperty("联系人电话")
    private String mobile;

    @Schema(description = "住宅地址")
    @ExcelProperty("住宅地址")
    private String address;

    @Schema(description = "客户联系人名片")
    private List<SimpleFile> cardList;

    @Schema(description = "默认联系人")
    @ExcelProperty(value = "默认联系人", converter = DictConvert.class)
    @DictFormat(DictTypeConstants.CONFIRM_TYPE) // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Integer defaultFlag;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    @ChangeIgnore
    private LocalDateTime createTime;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "座机")
    private String telephone;

    @Schema(description = "微信")
    private String wechat;

    @Schema(description = "QQ")
    private String qq;

    @Schema(description = "变更标识")
    private Integer changeFlag;

    private String sourceCode;

    private List<JsonEffectRange> effectRangeList;
}