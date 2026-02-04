package com.syj.eplus.framework.common.config.handler;

import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.syj.eplus.framework.common.entity.SplitPurchase;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class JsonSplitPurchaseListTypeHandler extends BaseTypeHandler<List<SplitPurchase>> {

    protected static final Logger logger = LoggerFactory.getLogger(JsonSplitPurchaseListTypeHandler.class);

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<SplitPurchase> parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, toJson(parameter));
    }

    @Override
    public List<SplitPurchase> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return this.toObject(rs.getString(columnName), columnName);
    }

    @Override
    public List<SplitPurchase> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return this.toObject(rs.getString(columnIndex), columnIndex);
    }

    @Override
    public List<SplitPurchase> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return this.toObject(cs.getString(columnIndex), columnIndex);
    }

    private String toJson(List<SplitPurchase> params) {
        try {
            return JsonUtils.toJsonString(params);
        } catch (Exception e) {
            logger.warn("[拆分采购信息-insert]JSON转换异常params-{},exception-{}", params, e.getMessage());
        }
        return "[]";
    }

    private List<SplitPurchase> toObject(String content, String columnName) {
        if (content != null && !content.isEmpty()) {
            try {
                return JsonUtils.parseObject(content, new TypeReference<>() {
                });
            } catch (Exception e) {
                logger.warn("[拆分采购信息-select]字段名：{}-JSON转换异常content-{},exception-{}", columnName, content, e.getMessage());
            }
        }
        return null;
    }

    private List<SplitPurchase> toObject(String content, int columnIndex) {
        if (content != null && !content.isEmpty()) {
            try {
                return JsonUtils.parseObject(content, new TypeReference<>() {
                });
            } catch (Exception e) {
                logger.warn("[拆分采购信息-select]字段index：{}-JSON转换异常content-{},exception-{}", columnIndex, content, e.getMessage());
            }
        }
        return null;
    }
}