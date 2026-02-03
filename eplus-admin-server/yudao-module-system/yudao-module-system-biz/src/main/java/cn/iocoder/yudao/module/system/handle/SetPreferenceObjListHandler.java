package cn.iocoder.yudao.module.system.handle;

import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.syj.eplus.framework.common.entity.SetPreferenceObj;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SetPreferenceObjListHandler extends BaseTypeHandler<SetPreferenceObj> {

    protected static final Logger logger = LoggerFactory.getLogger(SetPreferenceObjListHandler.class);


    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, SetPreferenceObj parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, toJson(parameter));
    }

    @Override
    public SetPreferenceObj getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return this.toObject(rs.getString(columnName));
    }

    @Override
    public SetPreferenceObj getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return this.toObject(rs.getString(columnIndex));
    }

    @Override
    public SetPreferenceObj getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return this.toObject(cs.getString(columnIndex));
    }

    private String toJson(SetPreferenceObj params) {
        try {
            return JsonUtils.toJsonString(params);
        } catch (Exception e) {
            logger.warn("[偏好设置JSON转换异常params-{},-{}", params, e.getMessage());
        }
        return "[]";
    }

    private SetPreferenceObj toObject(String content) {
        if (content != null && !content.isEmpty()) {
            try {
                return JsonUtils.parseObject(content, new TypeReference<>() {
                });
            } catch (Exception e) {
                logger.warn("[偏好设置JSON转换异常content-{},exception-{}", content, e.getMessage());
            }
        }
        return null;
    }
}
