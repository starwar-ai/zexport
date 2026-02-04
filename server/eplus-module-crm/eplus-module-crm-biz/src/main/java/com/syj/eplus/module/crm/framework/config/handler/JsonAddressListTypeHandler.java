package com.syj.eplus.module.crm.framework.config.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.syj.eplus.module.crm.entity.AddressShipping;
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
 * @Description：客户寄件地址JSON处理器(仅支持mybatis_plus语境)
 * @Author：du
 * @Date：2024/3/26 15:43
 */
@MappedJdbcTypes({JdbcType.VARCHAR})
public class JsonAddressListTypeHandler extends BaseTypeHandler<List<AddressShipping>> {

    protected static final Logger logger = LoggerFactory.getLogger(JsonAddressListTypeHandler.class);

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<AddressShipping> parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, toJson(parameter));
    }

    @Override
    public List<AddressShipping> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return this.toObject(rs.getString(columnName));
    }

    @Override
    public List<AddressShipping> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return this.toObject(rs.getString(columnIndex));
    }

    @Override
    public List<AddressShipping> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return this.toObject(cs.getString(columnIndex));
    }

    private String toJson(List<AddressShipping> params) {
        try {
            return mapper.writeValueAsString(params);
        } catch (Exception e) {
            logger.warn("[寄件地址-insert]params-{},JSON转换异常-{}", params, e.getMessage());
        }
        return "[]";
    }

    private List<AddressShipping> toObject(String content) {
        if (content != null && !content.isEmpty()) {
            try {
                return mapper.readValue(content, new TypeReference<>() {
                });
            } catch (Exception e) {
                logger.warn("[寄件地址-select]JSON转换异常content-{},exception-{}", content, e.getMessage());
            }
        }
        return null;
    }
}