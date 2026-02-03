package com.syj.eplus.module.crm.api.cust;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.syj.eplus.module.crm.api.cust.dto.AddressShippingDTO;
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

@MappedJdbcTypes({JdbcType.VARCHAR})
public class JsonAddressListTypeDTOHandler extends BaseTypeHandler<List<AddressShippingDTO>> {

    protected static final Logger logger = LoggerFactory.getLogger(JsonAddressListTypeDTOHandler.class);

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<AddressShippingDTO> parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, toJson(parameter));
    }

    @Override
    public List<AddressShippingDTO> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return this.toObject(rs.getString(columnName));
    }

    @Override
    public List<AddressShippingDTO> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return this.toObject(rs.getString(columnIndex));
    }

    @Override
    public List<AddressShippingDTO> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return this.toObject(cs.getString(columnIndex));
    }

    private String toJson(List<AddressShippingDTO> params) {
        try {
            return mapper.writeValueAsString(params);
        } catch (Exception e) {
            logger.warn("[寄件地址-insert]params-{},JSON转换异常-{}", params, e.getMessage());
        }
        return "[]";
    }

    private List<AddressShippingDTO> toObject(String content) {
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