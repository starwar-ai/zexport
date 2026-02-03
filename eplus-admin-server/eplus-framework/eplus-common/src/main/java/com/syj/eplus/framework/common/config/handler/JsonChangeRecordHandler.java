package com.syj.eplus.framework.common.config.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.syj.eplus.framework.common.entity.ChangeRecord;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @Description：
 * @Author：du
 * @Date：2024/5/9 11:17
 */
public class JsonChangeRecordHandler extends BaseTypeHandler<ChangeRecord> {

    protected static final Logger logger = LoggerFactory.getLogger(JsonChangeRecordHandler.class);

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, ChangeRecord parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, toJson(parameter));
    }

    @Override
    public ChangeRecord getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return this.toObject(rs.getString(columnName));
    }

    @Override
    public ChangeRecord getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return this.toObject(rs.getString(columnIndex));
    }

    @Override
    public ChangeRecord getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return this.toObject(cs.getString(columnIndex));
    }

    private String toJson(ChangeRecord params) {
        try {
            return mapper.writeValueAsString(params);
        } catch (Exception e) {
            logger.warn("[操作详情-insert]JSON转换异常params-{},exception-{}", params, e.getMessage());
        }
        return "[]";
    }

    private ChangeRecord toObject(String content) {
        if (content != null && !content.isEmpty()) {
            try {
                return mapper.readValue(content, new TypeReference<>() {
                });
            } catch (Exception e) {
                logger.warn("[操作详情-select]JSON转换异常content-{},exception-{}", content, e.getMessage());
            }
        }
        return null;
    }
}
