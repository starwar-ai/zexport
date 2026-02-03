package cn.iocoder.yudao.framework.excel.core.util;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;

import java.util.List;
import java.util.stream.Collectors;

public class ListConverter implements Converter<List<?>> {
    @Override
    public Class<?> supportJavaTypeKey() {
        return List.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING; // Convert List to String in Excel
    }

    @Override
    public WriteCellData<?> convertToExcelData(List<?> list, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        // Convert List to a comma-separated String
        String joinedString = list.stream()
                .map(Object::toString)
                .collect(Collectors.joining(", "));
        return new WriteCellData<>(joinedString);
    }
}