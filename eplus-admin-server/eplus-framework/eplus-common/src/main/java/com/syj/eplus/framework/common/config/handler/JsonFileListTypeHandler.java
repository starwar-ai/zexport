package com.syj.eplus.framework.common.config.handler;

import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.syj.eplus.framework.common.entity.SimpleFile;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
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
 * @Date：2024/5/8 18:37
 */
public class JsonFileListTypeHandler extends BaseTypeHandler<List<SimpleFile>> {

    protected static final Logger logger = LoggerFactory.getLogger(JsonFileListTypeHandler.class);


    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<SimpleFile> parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, toJson(parameter));
    }

    @Override
    public List<SimpleFile> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return this.toObject(rs.getString(columnName), columnName);
    }

    @Override
    public List<SimpleFile> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return this.toObject(rs.getString(columnIndex), columnIndex);
    }

    @Override
    public List<SimpleFile> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return this.toObject(cs.getString(columnIndex), columnIndex);
    }

    private String toJson(List<SimpleFile> params) {
        try {
            return JsonUtils.toJsonString(params);
        } catch (Exception e) {
            logger.warn("[文件精简列表-insert]JSON转换异常params-{},exception-{}", params, e.getMessage());
        }
        return "[]";
    }

    private List<SimpleFile> toObject(String content, String columnName) {
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

    private List<SimpleFile> toObject(String content, int columnIndex) {
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
