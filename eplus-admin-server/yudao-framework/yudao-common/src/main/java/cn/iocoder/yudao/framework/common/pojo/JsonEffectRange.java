package cn.iocoder.yudao.framework.common.pojo;

import lombok.Data;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: du
 * @Date: 2024/09/04/16:01
 * @Description:
 */
@Data
public class JsonEffectRange {

    /**
     * 影响范围类型
     */
    private Integer effectRangeType;

    /**
     * 影响范围编号列表
     */
    private String effectRangeCode;

    /**
     * 确认状态
     */
    private Integer confirmFlag;

    /**
     * 明细id
     */
    private List<Long> itemIdList;
}
