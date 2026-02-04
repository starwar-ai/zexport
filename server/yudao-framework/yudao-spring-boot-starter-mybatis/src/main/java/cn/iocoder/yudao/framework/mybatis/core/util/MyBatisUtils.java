package cn.iocoder.yudao.framework.mybatis.core.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.dict.CommonDict;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.SortingField;
import cn.iocoder.yudao.framework.common.util.string.StrUtils;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.support.LambdaMeta;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * MyBatis 工具类
 */
public class MyBatisUtils {

    protected static final Logger logger = LoggerFactory.getLogger(MyBatisUtils.class);
    private static final String MYSQL_ESCAPE_CHARACTER = "`";

    private static final ObjectMapper mapper = new ObjectMapper();

    public static <T> Page<T> buildPage(PageParam pageParam) {
        String sortingFieldStr = pageParam.getSortingFields();
        List<SortingField> sortingFields = null;
        if (StrUtil.isEmpty(sortingFieldStr)) {
            return buildPage(pageParam, sortingFields);
        }
        try {
            sortingFields = mapper.readValue(sortingFieldStr, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            logger.error("排序字段json转换异常sortingFieldStr-{}", sortingFieldStr);
        }
        return buildPage(pageParam, sortingFields);
    }

    public static <T> Page<T> buildPage(PageParam pageParam, Collection<SortingField> sortingFields) {
        // 页码 + 数量
        Page<T> page = new Page<>(pageParam.getPageNo(), pageParam.getPageSize());
        // 排序字段
        if (!CollectionUtil.isEmpty(sortingFields)) {
            sortingFields.forEach(s -> {
                OrderItem orderItem = SortingField.ORDER_ASC.equals(s.getOrder()) ? OrderItem.asc(StrUtils.camelToSnake(s.getField())) : OrderItem.desc(StrUtils.camelToSnake(s.getField()));
                String sortFieldStr = s.getSortField();
                if (StrUtil.isEmpty(sortFieldStr)) {
                    page.addOrder(orderItem);
                }
                List<String> sortFields = StrUtils.splitToString(sortFieldStr, CommonDict.COMMA);
                if (CollUtil.isEmpty(sortFields)) {
                    return;
                }
                sortFields.forEach(sortField -> {
                    String[] split = sortField.split(CommonDict.COLON);
                    String subField = split[0];
                    if (split.length == 1) {
                        page.addOrder(OrderItem.asc(String.format("IFNULL(JSON_EXTRACT(%s, '$.%s'), '')", StrUtils.camelToSnake(s.getField()), subField)));
                    } else if (split.length == 2) {
                        String sortMethod = split[1];
                        page.addOrder("desc".equals(sortMethod) ? OrderItem.desc(String.format("IFNULL(JSON_EXTRACT(%s, '$.%s'), '')", StrUtils.camelToSnake(s.getField()), subField)) : OrderItem.asc(String.format("IFNULL(JSON_EXTRACT(%s, '$.%s'), '')", StrUtils.camelToSnake(s.getField()), subField)));
                    }
                });

            });

        }
        return page;
    }

    /**
     * 将拦截器添加到链中
     * 由于 MybatisPlusInterceptor 不支持添加拦截器，所以只能全量设置
     *
     * @param interceptor 链
     * @param inner 拦截器
     * @param index 位置
     */
    public static void addInterceptor(MybatisPlusInterceptor interceptor, InnerInterceptor inner, int index) {
        List<InnerInterceptor> inners = new ArrayList<>(interceptor.getInterceptors());
        inners.add(index, inner);
        interceptor.setInterceptors(inners);
    }

    /**
     * 获得 Table 对应的表名
     *
     * 兼容 MySQL 转义表名 `t_xxx`
     *
     * @param table 表
     * @return 去除转移字符后的表名
     */
    public static String getTableName(Table table) {
        String tableName = table.getName();
        if (tableName.startsWith(MYSQL_ESCAPE_CHARACTER) && tableName.endsWith(MYSQL_ESCAPE_CHARACTER)) {
            tableName = tableName.substring(1, tableName.length() - 1);
        }
        return tableName;
    }

    /**
     * 构建 Column 对象
     *
     * @param tableName 表名
     * @param tableAlias 别名
     * @param column 字段名
     * @return Column 对象
     */
    public static Column buildColumn(String tableName, Alias tableAlias, String column) {
        if (tableAlias != null) {
            tableName = tableAlias.getName();
        }
        return new Column(tableName + StringPool.DOT + column);
    }

    /**
     * 通过lambda表达式获取数据库字段
     * @param func lambda 表达式
     * @return 数据库字段
     * @param <T>
     */
    public static <T> String getFieldNameByFunc(SFunction<T, ?> func){
        LambdaMeta extract = LambdaUtils.extract(func);
        String implMethodName = extract.getImplMethodName();
        // 手动截取get方法名中参数名称
        String substring = implMethodName.substring(3);
        String fieldName = substring.substring(0, 1).toLowerCase() + substring.substring(1);
        return StrUtils.camelToSnake(fieldName);
    }
}
