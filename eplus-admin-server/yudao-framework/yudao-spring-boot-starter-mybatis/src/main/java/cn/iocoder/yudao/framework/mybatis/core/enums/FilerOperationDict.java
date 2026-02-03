package cn.iocoder.yudao.framework.mybatis.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: du
 * @Date: 2024/08/23/10:44
 * @Description:
 */
@Getter
@AllArgsConstructor
public class FilerOperationDict {
    public static final String EQ = "$eq";
    public static final String NE = "$ne";
    public static final String GT = "$gt";
    public static final String GTE = "$gte";
    public static final String LT = "$lt";
    public static final String LTE = "$lte";
    public static final String LIKE = "like";
    public static final String NLIKE = "nlike";
    public static final String IN = "$in";
    public static final String NIN = "$nin";
    public static final String BETWEEN = "between";
}
