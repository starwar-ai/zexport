package cn.iocoder.yudao.module.system.controller.admin.user.vo.user;

import cn.iocoder.yudao.framework.excel.core.annotations.DictFormat;
import cn.iocoder.yudao.framework.excel.core.convert.DictConvert;
import cn.iocoder.yudao.module.system.enums.DictTypeConstants;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @Description：
 * @Author：du
 * @Date：2024/2/28 17:17
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = false) // 设置 chain = false，避免用户导入有问题
@ExcelIgnoreUnannotated
public class UserBankImportExcelVO {
    @ExcelProperty(value = "员工姓名", index = 1)
    private String nickname;

    @ExcelProperty(value = "部门名称", index = 2)
    private String deptName;

    @ExcelProperty(value = "手机号码", index = 3)
    private String mobile;

    @ExcelProperty(value = "开户行", index = 4)
    private String bank;

    @ExcelProperty(value = "银行账号", index = 5)
    private String bankAccount;

    @ExcelProperty(value = "开户行地址", index = 6)
    private String bankAddress;

    @ExcelProperty(value = "开户行联系人", index = 7)
    private String bankPoc;

    @ExcelProperty(value = "银行行号", index = 8)
    private String bankCode;

    @ExcelProperty(value = "账号状态", converter = DictConvert.class, index = 9)
    @DictFormat(DictTypeConstants.COMMON_STATUS)
    private Integer status;
}
