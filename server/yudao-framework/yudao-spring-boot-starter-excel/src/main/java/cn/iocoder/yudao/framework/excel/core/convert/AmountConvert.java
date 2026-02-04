package cn.iocoder.yudao.framework.excel.core.convert;

import cn.iocoder.yudao.framework.common.dict.CommonDict;
import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.syj.eplus.framework.common.entity.JsonAmount;

public class AmountConvert implements Converter<JsonAmount> {
    @Override
    public Class<?> supportJavaTypeKey() {
        throw new UnsupportedOperationException("暂不支持，也不需要");
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        throw new UnsupportedOperationException("暂不支持，也不需要");
    }

    @Override
    public WriteCellData<String> convertToExcelData(JsonAmount value, ExcelContentProperty contentProperty,
                                                    GlobalConfiguration globalConfiguration) {
        String result = value.getAmount() + CommonDict.SPACE + value.getCurrency();
        // 生成 Excel 小表格
        return new WriteCellData<>(result);
    }

}
