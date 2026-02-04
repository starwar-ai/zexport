package com.syj.eplus.framework.common.config.handler;

import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.syj.eplus.framework.common.entity.BaseValue;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class BaseValueListTypeHandler extends BaseTypeHandler<List<BaseValue>> {

    protected static final Logger logger = LoggerFactory.getLogger(BaseValueListTypeHandler.class);


    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<BaseValue> parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, toJson(parameter));
    }

    @Override
    public List<BaseValue> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return this.toObject(rs.getString(columnName), columnName);
    }

    @Override
    public List<BaseValue> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return this.toObject(rs.getString(columnIndex), columnIndex);
    }

    @Override
    public List<BaseValue> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return this.toObject(cs.getString(columnIndex), columnIndex);
    }

    private String toJson(List<BaseValue> params) {
        try {
            return JsonUtils.toJsonString(params);
        } catch (Exception e) {
            logger.warn("[文件精简列表-insert]JSON转换异常params-{},exception-{}", params, e.getMessage());
        }
        return "[]";
    }

    private List<BaseValue> toObject(String content, String columnName) {
        if (content != null && !content.isEmpty()) {
            try {
                return JsonUtils.parseObject(content, new TypeReference<>() {
                });
            } catch (Exception e) {
                logger.warn("[文件精简列表-select]字段名：{}，JSON转换异常content-{},exception-{}", columnName, content, e.getMessage());
            }
        }
        return null;
    }

    private List<BaseValue> toObject(String content, int columnIndex) {
        if (content != null && !content.isEmpty()) {
            try {
                return JsonUtils.parseObject(content, new TypeReference<>() {
                });
            } catch (Exception e) {
                logger.warn("[文件精简列表-select]字段index：{}，JSON转换异常content-{},exception-{}", columnIndex, content, e.getMessage());
            }
        }
        return null;
    }
}
