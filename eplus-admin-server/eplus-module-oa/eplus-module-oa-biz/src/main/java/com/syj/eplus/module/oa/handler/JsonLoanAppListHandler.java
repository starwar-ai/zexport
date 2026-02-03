package com.syj.eplus.module.oa.handler;

import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.syj.eplus.module.oa.api.dto.SimpleLoanAppRespDTO;
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
 * Created with IntelliJ IDEA.
 *
 * @Author: du
 * @Date: 2024/09/09/11:33
 * @Description:
 */
public class JsonLoanAppListHandler extends BaseTypeHandler<List<SimpleLoanAppRespDTO>> {

    protected static final Logger logger = LoggerFactory.getLogger(JsonLoanAppListHandler.class);


    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<SimpleLoanAppRespDTO> parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, toJson(parameter));
    }

    @Override
    public List<SimpleLoanAppRespDTO> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return this.toObject(rs.getString(columnName));
    }

    @Override
    public List<SimpleLoanAppRespDTO> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return this.toObject(rs.getString(columnIndex));
    }

    @Override
    public List<SimpleLoanAppRespDTO> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return this.toObject(cs.getString(columnIndex));
    }

    private String toJson(List<SimpleLoanAppRespDTO> params) {
        try {
            return JsonUtils.toJsonString(params);
        } catch (Exception e) {
            logger.warn("[出差报销借款单-insert]JSON转换异常-{}", e.getMessage());
        }
        return "[]";
    }

    private List<SimpleLoanAppRespDTO> toObject(String content) {
        if (content != null && !content.isEmpty()) {
            try {
                return JsonUtils.parseObject(content,new TypeReference<>() {
                });
            } catch (Exception e) {
                logger.warn("[出差报销借款单-select]JSON转换异常-{}", e.getMessage());
            }
        }
        return null;
    }
}