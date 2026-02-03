package com.syj.eplus.module.oa.handler;

import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.syj.eplus.module.oa.entity.JsonExpenseAppItem;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class JsonExpenseAppItemListHandler extends BaseTypeHandler<List<JsonExpenseAppItem>> {

    protected static final Logger logger = LoggerFactory.getLogger(JsonExpenseAppItemListHandler.class);


    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<JsonExpenseAppItem> parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, toJson(parameter));
    }

    @Override
    public List<JsonExpenseAppItem> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return this.toObject(rs.getString(columnName));
    }

    @Override
    public List<JsonExpenseAppItem> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return this.toObject(rs.getString(columnIndex));
    }

    @Override
    public List<JsonExpenseAppItem> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return this.toObject(cs.getString(columnIndex));
    }

    private String toJson(List<JsonExpenseAppItem> params) {
        try {
            return JsonUtils.toJsonString(params);
        } catch (Exception e) {
            logger.warn("[招待费申请单-insert]JSON转换异常-{}", e.getMessage());
        }
        return "[]";
    }

    private List<JsonExpenseAppItem> toObject(String content) {
        if (content != null && !content.isEmpty()) {
            try {
                return JsonUtils.parseObject(content,new TypeReference<>() {
                });
            } catch (Exception e) {
                logger.warn("[招待费申请单-select]JSON转换异常-{}", e.getMessage());
            }
        }
        return null;
    }
}