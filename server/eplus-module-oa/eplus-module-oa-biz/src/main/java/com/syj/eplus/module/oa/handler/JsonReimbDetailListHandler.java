package com.syj.eplus.module.oa.handler;

import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.syj.eplus.module.oa.entity.JsonReimbDetail;
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
 * @Description：
 * @Author：du
 * @Date：2024/4/24 10:09
 */
@MappedJdbcTypes({JdbcType.VARCHAR})
public class JsonReimbDetailListHandler extends BaseTypeHandler<List<JsonReimbDetail>> {

    protected static final Logger logger = LoggerFactory.getLogger(JsonReimbDetailListHandler.class);


    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<JsonReimbDetail> parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, toJson(parameter));
    }

    @Override
    public List<JsonReimbDetail> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return this.toObject(rs.getString(columnName));
    }

    @Override
    public List<JsonReimbDetail> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return this.toObject(rs.getString(columnIndex));
    }

    @Override
    public List<JsonReimbDetail> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return this.toObject(cs.getString(columnIndex));
    }

    private String toJson(List<JsonReimbDetail> params) {
        try {
            return JsonUtils.toJsonString(params);
        } catch (Exception e) {
            logger.warn("[报销-insert]JSON转换异常-{}", e.getMessage());
        }
        return "[]";
    }

    private List<JsonReimbDetail> toObject(String content) {
        if (content != null && !content.isEmpty()) {
            try {
                return JsonUtils.parseObject(content,new TypeReference<>() {
                });
            } catch (Exception e) {
                logger.warn("[报销-select]JSON转换异常-{}", e.getMessage());
            }
        }
        return null;
    }
}