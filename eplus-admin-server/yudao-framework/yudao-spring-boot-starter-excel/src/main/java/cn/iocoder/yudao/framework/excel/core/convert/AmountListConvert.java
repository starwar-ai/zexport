package cn.iocoder.yudao.framework.excel.core.convert;

import cn.iocoder.yudao.framework.common.dict.CommonDict;
import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.syj.eplus.framework.common.entity.JsonAmount;

import java.util.List;
import java.util.stream.Collectors;

public class AmountListConvert implements Converter<List<JsonAmount>> {
    @Override
    public Class<?> supportJavaTypeKey() {
        throw new UnsupportedOperationException("暂不支持，也不需要");
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        throw new UnsupportedOperationException("暂不支持，也不需要");
    }

    @Override
    public WriteCellData<String> convertToExcelData(List<JsonAmount> value, ExcelContentProperty contentProperty,
                                                    GlobalConfiguration globalConfiguration) {
        String result = value.stream().map(jsonAmount -> {
            return jsonAmount.getAmount() + CommonDict.SPACE + jsonAmount.getCurrency();
        }).collect(Collectors.joining(CommonDict.COMMA));
        // 生成 Excel 小表格
        return new WriteCellData<>(result);
    }

}
