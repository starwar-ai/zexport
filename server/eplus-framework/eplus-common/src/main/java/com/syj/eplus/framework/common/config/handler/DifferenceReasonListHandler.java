package com.syj.eplus.framework.common.config.handler;

import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.syj.eplus.framework.common.entity.DifferenceReason;
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
 * @Date：2024/7/24 11:24
 */
public class DifferenceReasonListHandler extends BaseTypeHandler<List<DifferenceReason>> {

        protected static final Logger logger = LoggerFactory.getLogger(DifferenceReasonListHandler.class);

        @Override
        public void setNonNullParameter(PreparedStatement ps, int i, List<DifferenceReason> parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, toJson(parameter));
        }

        @Override
        public List<DifferenceReason> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return this.toObject(rs.getString(columnName));
        }

        @Override
        public List<DifferenceReason> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return this.toObject(rs.getString(columnIndex));
        }

        @Override
        public List<DifferenceReason> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return this.toObject(cs.getString(columnIndex));
        }

        private String toJson(List<DifferenceReason> params) {
        try {
                return JsonUtils.toJsonString(params);
        } catch (Exception e) {
                logger.warn("[认领差异-insert]JSON转换异常params-{},exception-{}", params, e.getMessage());
        }
        return "[]";
        }

        private List<DifferenceReason> toObject(String content) {
        if (content != null && !content.isEmpty()) {
                try {
                        return JsonUtils.parseObject(content, new TypeReference<>() {
                        });
                } catch (Exception e) {
                        logger.warn("[认领差异-select]JSON转换异常content-{},exception-{}", content, e.getMessage());
                }
        }
        return null;
        }
}