package com.syj.eplus.framework.common.config.handler;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * JSON类型处理器基类
 * <p>
 * 提供统一的JSON序列化/反序列化能力,简化TypeHandler实现
 * <p>
 * 使用示例:
 * <pre>
 * public class JsonAmountListTypeHandler extends AbstractJsonTypeHandler&lt;List&lt;JsonAmount&gt;&gt; {
 *     public JsonAmountListTypeHandler() {
 *         super(new TypeReference&lt;List&lt;JsonAmount&gt;&gt;() {});
 *     }
 * }
 * </pre>
 *
 * @author eplus-framework
 * @param <T> 要转换的目标类型
 */
@Slf4j
public abstract class AbstractJsonTypeHandler<T> extends BaseTypeHandler<T> {

    private final TypeReference<T> typeReference;

    /**
     * 构造函数
     *
     * @param typeReference 类型引用,用于JSON反序列化
     */
    protected AbstractJsonTypeHandler(TypeReference<T> typeReference) {
        if (typeReference == null) {
            throw new IllegalArgumentException("TypeReference不能为空");
        }
        this.typeReference = typeReference;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType)
            throws SQLException {
        ps.setString(i, toJson(parameter));
    }

    @Override
    public T getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String json = rs.getString(columnName);
        return fromJson(json);
    }

    @Override
    public T getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String json = rs.getString(columnIndex);
        return fromJson(json);
    }

    @Override
    public T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String json = cs.getString(columnIndex);
        return fromJson(json);
    }

    /**
     * 将对象转换为JSON字符串
     *
     * @param object 要转换的对象
     * @return JSON字符串
     */
    protected String toJson(T object) {
        if (object == null) {
            return null;
        }
        try {
            return JsonUtils.toJsonString(object);
        } catch (Exception e) {
            log.error("对象转JSON失败: {}", object, e);
            throw new RuntimeException("对象转JSON失败", e);
        }
    }

    /**
     * 将JSON字符串转换为对象
     *
     * @param json JSON字符串
     * @return 转换后的对象
     */
    protected T fromJson(String json) {
        if (StrUtil.isBlank(json)) {
            return null;
        }
        try {
            return JsonUtils.parseObject(json, typeReference);
        } catch (Exception e) {
            log.error("JSON转对象失败: {}", json, e);
            throw new RuntimeException("JSON转对象失败: " + json, e);
        }
    }

    /**
     * 获取类型引用
     *
     * @return TypeReference
     */
    protected TypeReference<T> getTypeReference() {
        return typeReference;
    }
}
