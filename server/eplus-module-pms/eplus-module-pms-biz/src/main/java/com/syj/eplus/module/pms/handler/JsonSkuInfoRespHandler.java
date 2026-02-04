package com.syj.eplus.module.pms.handler;

import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.syj.eplus.module.pms.controller.admin.sku.vo.SkuInfoRespVO;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: du
 * @Date: 2024/09/09/20:15  
 * @Description:
 */
public class JsonSkuInfoRespHandler extends BaseTypeHandler<SkuInfoRespVO> {

    protected static final Logger logger = LoggerFactory.getLogger(JsonSkuInfoRespHandler.class);


    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, SkuInfoRespVO parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, toJson(parameter));
    }

    @Override
    public SkuInfoRespVO getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return this.toObject(rs.getString(columnName));
    }

    @Override
    public SkuInfoRespVO getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return this.toObject(rs.getString(columnIndex));
    }

    @Override
    public SkuInfoRespVO getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return this.toObject(cs.getString(columnIndex));
    }

    private String toJson(SkuInfoRespVO params) {
        try {
            return JsonUtils.toJsonString(params);
        } catch (Exception e) {
            logger.warn("[sku旧数据-insert]JSON转换异常params-{},-{}", params, e.getMessage());
        }
        return "[]";
    }

    private SkuInfoRespVO toObject(String content) {
        if (content != null && !content.isEmpty()) {
            try {
                return JsonUtils.parseObject(content, new TypeReference<>() {
                });
            } catch (Exception e) {
                logger.warn("[sku旧数据-select]JSON转换异常content-{},exception-{}", content, e.getMessage());
            }
        }
        return null;
    }
}
