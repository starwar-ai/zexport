package cn.iocoder.yudao.framework.excel.core.convert;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * LocalDateTime 转 Excel 日期格式转换器（只显示日期，不显示时分秒）
 * 
 * @Description：将 LocalDateTime 格式化为 yyyy-MM-dd 格式
 * @Author：Auto
 * @Date：2024
 */
public class LocalDateOnlyConvert implements Converter<Object> {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public Class<?> supportJavaTypeKey() {
        return null;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return null;
    }

    @Override
    public Object convertToJavaData(ReadCellData readCellData, ExcelContentProperty contentProperty,
                                    GlobalConfiguration globalConfiguration) {
        return null;
    }

    @Override
    public WriteCellData<String> convertToExcelData(Object object, ExcelContentProperty contentProperty,
                                                    GlobalConfiguration globalConfiguration) {
        if (object == null) {
            return new WriteCellData<>("");
        }
        if (object instanceof LocalDateTime dateTime) {
            return new WriteCellData<>(dateTime.format(DATE_FORMATTER));
        }

        // 其他情况，尝试解析字符串并格式化
        String dateTimeStr = object.toString();
        if (dateTimeStr.contains("T")) {
            LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr.replace(" ", "T"));
            return new WriteCellData<>(dateTime.format(DATE_FORMATTER));
        }
        return new WriteCellData<>(dateTimeStr);
    }
}

