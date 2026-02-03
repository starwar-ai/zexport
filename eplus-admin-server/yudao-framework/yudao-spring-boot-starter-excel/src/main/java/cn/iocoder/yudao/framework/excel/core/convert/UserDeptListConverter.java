package cn.iocoder.yudao.framework.excel.core.convert;

import cn.iocoder.yudao.framework.common.dict.CommonDict;
import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.syj.eplus.framework.common.entity.UserDept;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description：
 * @Author：du
 * @Date：2024/4/22 17:27
 */
public class UserDeptListConverter implements Converter<List<UserDept>> {
    @Override
    public Class<?> supportJavaTypeKey() {
        throw new UnsupportedOperationException("暂不支持，也不需要");
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        throw new UnsupportedOperationException("暂不支持，也不需要");
    }

    @Override
    public WriteCellData<String> convertToExcelData(List<UserDept> value, ExcelContentProperty contentProperty,
                                                    GlobalConfiguration globalConfiguration) {
        String result = value.stream().map(UserDept::getNickname).collect(Collectors.joining(CommonDict.COMMA));
        // 生成 Excel 小表格
        return new WriteCellData<>(result);
    }

}
