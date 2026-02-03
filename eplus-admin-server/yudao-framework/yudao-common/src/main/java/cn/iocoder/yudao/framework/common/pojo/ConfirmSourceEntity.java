package cn.iocoder.yudao.framework.common.pojo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Description：返回确认来源列表
 * @Author：chengbo
 * @Date：2024/1/20 19:48
 */
@Data
@Accessors(chain = true)
public class ConfirmSourceEntity {
    /**
     * 确认来源类型
     */
    private Integer confirmSourceType;
    /**
     * 确认来源编号
     */
    private String code;
    /**
     * 确认来源id
     */
    private Long id;
}

