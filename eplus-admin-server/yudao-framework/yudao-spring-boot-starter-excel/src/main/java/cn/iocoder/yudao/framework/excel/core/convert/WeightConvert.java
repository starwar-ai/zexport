package cn.iocoder.yudao.framework.excel.core.convert;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.syj.eplus.framework.common.entity.JsonWeight;
import com.syj.eplus.framework.common.util.NumberFormatUtil;

/**
 * @Description：
 * @Author：du
 * @Date：2024/5/16 15:34
 */
public class WeightConvert implements Converter<JsonWeight> {
    @Override
    public Class<?> supportJavaTypeKey() {
        throw new UnsupportedOperationException("暂不支持，也不需要");
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        throw new UnsupportedOperationException("暂不支持，也不需要");
    }

    @Override
    public WriteCellData<String> convertToExcelData(JsonWeight value, ExcelContentProperty contentProperty,
                                                    GlobalConfiguration globalConfiguration) {
        String result = NumberFormatUtil.formatWeight(value.getWeight()) + value.getUnit();
        // 生成 Excel 小表格
        return new WriteCellData<>(result);
    }
}