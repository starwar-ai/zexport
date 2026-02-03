package cn.iocoder.yudao.module.system.controller.admin.user.vo.user;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.syj.eplus.framework.common.entity.JsonTimePreference;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - 设置偏好 Request VO")
@Data
@ExcelIgnoreUnannotated
public class TimePreferencesReqVO {
    private List<JsonTimePreference> jsonTimePreferenceList;
}
