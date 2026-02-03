package com.syj.eplus.module.oa.controller.admin.dictsubject.vo;

import cn.iocoder.yudao.module.system.api.dict.dto.DictTypeRespDTO;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 类别配置 Response VO")
@Data
@ExcelIgnoreUnannotated
public class DictSubjectRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "21605")
    @ExcelProperty("主键")
    private Long id;
    
    @Schema(description = "科目id", example = "9323")
    @ExcelProperty("科目id")
    private Long subjectId;
    
    @Schema(description = "科目名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    @ExcelProperty("科目名称")
    private String subjectName;
    
    @Schema(description = "科目描述", example = "随便")
    @ExcelProperty("科目描述")
    private String subjectDescription;


    @Schema(description = "字典值类型")
    private List<String> systemDictTypeList;

    @Schema(description = "字典值类型描述")
    private List<DictTypeRespDTO> systemDictTypeDescList;

    
    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "费用名称")
    @ExcelProperty("费用名称")
    private String feeName;

    @Schema(description = "费用描述")
    @ExcelProperty("费用描述")
    private String feeDesc;

    @Schema(description = "是否在描述展示")
    private Integer showDescFlag;

    @Schema(description = "是否在费用实际展示")
    private Integer showFeeFlag;
}