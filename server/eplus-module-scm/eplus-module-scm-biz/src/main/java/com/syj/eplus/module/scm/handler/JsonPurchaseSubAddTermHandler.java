package com.syj.eplus.module.scm.handler;

import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.syj.eplus.module.scm.dal.dataobject.addsubterm.PurchaseAddSubTerm;
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
 * @Date: 2024/08/05/16:49
 * @Description:
 */
public class JsonPurchaseSubAddTermHandler extends BaseTypeHandler<List<PurchaseAddSubTerm>> {

    protected static final Logger logger = LoggerFactory.getLogger(JsonPurchaseSubAddTermHandler.class);


    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<PurchaseAddSubTerm> parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, toJson(parameter));
    }

    @Override
    public List<PurchaseAddSubTerm> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return this.toObject(rs.getString(columnName));
    }

    @Override
    public List<PurchaseAddSubTerm> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return this.toObject(rs.getString(columnIndex));
    }

    @Override
    public List<PurchaseAddSubTerm> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return this.toObject(cs.getString(columnIndex));
    }

    private String toJson(List<PurchaseAddSubTerm> params) {
        try {
            return JsonUtils.toJsonString(params);
        } catch (Exception e) {
            logger.warn("[采购合同加减项变更-insert]JSON转换异常params-{},exception-{}", params, e.getMessage());
        }
        return "[]";
    }

    private List<PurchaseAddSubTerm> toObject(String content) {
        if (content != null && !content.isEmpty()) {
            try {
                return JsonUtils.parseObject(content, new TypeReference<>() {
                });
            } catch (Exception e) {
                logger.warn("[采购合同加减项变更-select]JSON转换异常content-{},exception-{}", content, e.getMessage());
            }
        }
        return null;
    }
}
