package com.syj.eplus.module.oa.config.hendler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.syj.eplus.module.oa.entity.JsonLoanapp;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @Description：出差报销中借款单JSON处理器(仅支持mybatis_plus语境)
 * @Author：du
 * @Date：2024/2/21 17:28
 */
@MappedJdbcTypes({JdbcType.VARCHAR})
public class JsonLoanListTypeHandler extends BaseTypeHandler<List<JsonLoanapp>> {

    protected static final Logger logger = LoggerFactory.getLogger(JsonLoanListTypeHandler.class);

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<JsonLoanapp> parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, toJson(parameter));
    }

    @Override
    public List<JsonLoanapp> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return this.toObject(rs.getString(columnName));
    }

    @Override
    public List<JsonLoanapp> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return this.toObject(rs.getString(columnIndex));
    }

    @Override
    public List<JsonLoanapp> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return this.toObject(cs.getString(columnIndex));
    }

    private String toJson(List<JsonLoanapp> params) {
        try {
            return mapper.writeValueAsString(params);
        } catch (Exception e) {
            logger.warn("[出差报销-insert]JSON转换异常-{}", e.getMessage());
        }
        return "[]";
    }

    private List<JsonLoanapp> toObject(String content) {
        if (content != null && !content.isEmpty()) {
            try {
                return mapper.readValue(content, new TypeReference<>() {
                });
            } catch (Exception e) {
                logger.warn("[出差报销-select]JSON转换异常-{}", e.getMessage());
            }
        }
        return null;
    }
}
