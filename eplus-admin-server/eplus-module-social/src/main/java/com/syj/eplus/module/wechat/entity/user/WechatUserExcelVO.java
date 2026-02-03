package com.syj.eplus.module.wechat.entity.user;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = false) // 设置 chain = false，避免用户导入有问题
public class WechatUserExcelVO {
    @ExcelProperty("姓名")
    private String nickname;
    @ExcelProperty("部门")
    private String deptName;
    @ExcelProperty("手机")
    private String mobile;
    @ExcelProperty("账号")
    private String socialId;
    @ExcelProperty("工号")
    private String code;
}
