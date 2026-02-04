package com.syj.eplus.module.sms.handler;

import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.syj.eplus.framework.common.config.handler.JsonFileListTypeHandler;
import com.syj.eplus.module.sms.dal.dataobject.addsubitem.AddSubItem;
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
 * @Date：2024/7/2 17:34
 */
public class JsonAddSubItemHandler extends BaseTypeHandler<List<AddSubItem>> {

    protected static final Logger logger = LoggerFactory.getLogger(JsonFileListTypeHandler.class);


    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<AddSubItem> parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, toJson(parameter));
    }

    @Override
    public List<AddSubItem> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return this.toObject(rs.getString(columnName));
    }

    @Override
    public List<AddSubItem> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return this.toObject(rs.getString(columnIndex));
    }

    @Override
    public List<AddSubItem> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return this.toObject(cs.getString(columnIndex));
    }

    private String toJson(List<AddSubItem> params) {
        try {
            return JsonUtils.toJsonString(params);
        } catch (Exception e) {
            logger.warn("[销售合同加减项变更-insert]JSON转换异常params-{},exception-{}", params, e.getMessage());
        }
        return "[]";
    }

    private List<AddSubItem> toObject(String content) {
        if (content != null && !content.isEmpty()) {
            try {
                return JsonUtils.parseObject(content, new TypeReference<>() {
                });
            } catch (Exception e) {
                logger.warn("[销售合同加减项变更-select]JSON转换异常content-{},exception-{}", content, e.getMessage());
            }
        }
        return null;
    }
}
