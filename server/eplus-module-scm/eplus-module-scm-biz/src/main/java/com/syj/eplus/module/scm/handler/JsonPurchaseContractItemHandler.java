package com.syj.eplus.module.scm.handler;

import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.syj.eplus.framework.common.config.handler.JsonFileListTypeHandler;
import com.syj.eplus.module.scm.controller.admin.purchasecontractitem.vo.PurchaseContractItemInfoRespVO;
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
 * @Date：2024/5/23 15:34
 */
public class JsonPurchaseContractItemHandler extends BaseTypeHandler<List<PurchaseContractItemInfoRespVO>> {

    protected static final Logger logger = LoggerFactory.getLogger(JsonFileListTypeHandler.class);


    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<PurchaseContractItemInfoRespVO> parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, toJson(parameter));
    }

    @Override
    public List<PurchaseContractItemInfoRespVO> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return this.toObject(rs.getString(columnName));
    }

    @Override
    public List<PurchaseContractItemInfoRespVO> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return this.toObject(rs.getString(columnIndex));
    }

    @Override
    public List<PurchaseContractItemInfoRespVO> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return this.toObject(cs.getString(columnIndex));
    }

    private String toJson(List<PurchaseContractItemInfoRespVO> params) {
        try {
            return JsonUtils.toJsonString(params);
        } catch (Exception e) {
            logger.warn("[采购合同明细变更-insert]JSON转换异常params-{},exception-{}", params, e.getMessage());
        }
        return "[]";
    }

    private List<PurchaseContractItemInfoRespVO> toObject(String content) {
        if (content != null && !content.isEmpty()) {
            try {
                return JsonUtils.parseObject(content, new TypeReference<>() {
                });
            } catch (Exception e) {
                logger.warn("[采购合同明细变更-select]JSON转换异常content-{},exception-{}", content, e.getMessage());
            }
        }
        return null;
    }
}
