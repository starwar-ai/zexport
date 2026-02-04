package cn.iocoder.yudao.framework.common.pojo;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: du
 * @Date: 2024/08/23/9:57
 * @Description:
 */
@Data
public class FilterCondition {

    /**
     * 字段名
     */
    private String field;

    /**
     * 条件
     */
    private String cond;

    /**
     * 字段值
     */
    private Object value;
}
