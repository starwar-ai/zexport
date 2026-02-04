package cn.iocoder.yudao.framework.excel.core.convert;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.syj.eplus.framework.common.util.VolumeUtil;

import java.math.BigDecimal;

/**
 * 体积转换器
 * 使用VolumeUtil.processVolume格式化体积数据
 * 
 * @author 波波
 * @date 2024/12/19
 */
public class VolumeConvert implements Converter<BigDecimal> {
    @Override
    public Class<?> supportJavaTypeKey() {
        return BigDecimal.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public WriteCellData<String> convertToExcelData(BigDecimal value, ExcelContentProperty contentProperty,
                                                    GlobalConfiguration globalConfiguration) {
        if (value == null) {
            return new WriteCellData<>("");
        }
        // 使用VolumeUtil.processVolume格式化体积数据
        BigDecimal processedVolume = VolumeUtil.processVolume(value);
        return new WriteCellData<>(processedVolume.toString());
    }
}
