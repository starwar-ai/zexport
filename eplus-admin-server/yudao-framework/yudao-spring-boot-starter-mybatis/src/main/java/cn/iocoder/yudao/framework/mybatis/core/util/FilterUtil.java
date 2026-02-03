package cn.iocoder.yudao.framework.mybatis.core.util;

import cn.iocoder.yudao.framework.mybatis.core.enums.FilerOperationDict;
import cn.iocoder.yudao.framework.mybatis.core.query.QueryWrapperX;

import java.util.Objects;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: du
 * @Date: 2024/08/26/11:17
 * @Description:
 */
public class FilterUtil {
    public static <T> void filterSql(QueryWrapperX<T> queryWrapperX,String condFlag,String fileName,Object value) {
        if (Objects.isNull(condFlag)){
            throw new RuntimeException("查询条件不可为空");
        }
        switch (condFlag){
            case FilerOperationDict.EQ:
                queryWrapperX.eqIfPresent(fileName,value);
                break;
            case FilerOperationDict.LIKE:
                queryWrapperX.likeIfPresent(fileName, (String) value);
                break;
            case FilerOperationDict.GT:
                queryWrapperX.gtIfPresent(fileName,value);
                break;
            case FilerOperationDict.LT:
                queryWrapperX.ltIfPresent(fileName,value);
                break;
            case FilerOperationDict.IN:
                queryWrapperX.inIfPresent(fileName,value);
            case FilerOperationDict.BETWEEN:
                queryWrapperX.betweenIfPresent(fileName, (Object[]) value);
            default:
                throw new RuntimeException("未知查询条件");
        }
    }

    public static <T> void filterJsonSql(QueryWrapperX<T> queryWrapperX,String condFlag,String fileName,Object value) {
        if (Objects.isNull(condFlag)){
            throw new RuntimeException("查询条件不可为空");
        }
        switch (condFlag){
            case FilerOperationDict.EQ:
                queryWrapperX.eqIfPresent(fileName,value);
                break;
            case FilerOperationDict.LIKE:
                queryWrapperX.likeIfPresent(fileName, (String) value);
                break;
            case FilerOperationDict.GT:
                queryWrapperX.gtIfPresent(fileName,value);
                break;
            case FilerOperationDict.LT:
                queryWrapperX.ltIfPresent(fileName,value);
                break;
            case FilerOperationDict.IN:
                queryWrapperX.inIfPresent(fileName,value);
            case FilerOperationDict.BETWEEN:
                queryWrapperX.betweenIfPresent(fileName, (Object[]) value);
            default:
                throw new RuntimeException("未知查询条件");
        }
    }
}
