package com.syj.eplus.framework.common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * @Description：
 * @Author：du
 * @Date：2024/5/11 11:20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JsonWeight {

    /**
     * 重量
     */
    private BigDecimal weight;
    /**
     * 单位
     */
    private String unit;

    /**
     * JsonWeight类型的加法
     * @param other 加项
     * @return 结果
     */
    public JsonWeight add(JsonWeight other) {
        return new JsonWeight(Objects.isNull(this.getWeight())?BigDecimal.ZERO:this.weight.add(Objects.isNull(other.getWeight())?BigDecimal.ZERO:other.getWeight()), this.unit);
    }
}
