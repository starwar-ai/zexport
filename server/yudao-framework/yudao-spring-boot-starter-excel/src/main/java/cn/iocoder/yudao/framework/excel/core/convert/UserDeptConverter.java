package cn.iocoder.yudao.framework.excel.core.convert;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.syj.eplus.framework.common.entity.UserDept;

import java.util.Objects;

/**
 * @Description：
 * @Author：du
 * @Date：2024/3/19 17:10
 */
public class UserDeptConverter implements Converter<UserDept> {
    @Override
    public Class<?> supportJavaTypeKey() {
        throw new UnsupportedOperationException("暂不支持，也不需要");
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        throw new UnsupportedOperationException("暂不支持，也不需要");
    }

    @Override
    public WriteCellData<String> convertToExcelData(UserDept value, ExcelContentProperty contentProperty,
                                                    GlobalConfiguration globalConfiguration) {
        if (Objects.nonNull(value)&& StrUtil.isNotEmpty(value.getNickname())){
            return new WriteCellData<>(value.getNickname());
        }else {
            return new WriteCellData<>("");
        }
        // 生成 Excel 小表格

    }

}
