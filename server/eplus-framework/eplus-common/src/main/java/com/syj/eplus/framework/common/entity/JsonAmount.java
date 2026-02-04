package com.syj.eplus.framework.common.entity;

import com.syj.eplus.framework.common.dict.CalculationDict;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;


/**
 * @Description：带币种金额json映射实体
 * @Author：du
 * @Date：2024/2/21 17:37
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class JsonAmount {
    /**
     * 金额
     */
    private BigDecimal amount = BigDecimal.ZERO;
    /**
     * 币种
     */
    private String currency = "";

    public String showAmount() {
        if (Objects.isNull(amount)){
            return BigDecimal.ZERO.toString();
        }
        return amount.setScale(2, RoundingMode.HALF_UP) + currency;
    }

    /**
     * 获取bigDecimal类型的保留两位小数值
     * @return
     */
    public BigDecimal getCheckAmount(){
        if (Objects.isNull(amount)){
            return BigDecimal.ZERO;
        }
        return amount.setScale(CalculationDict.DECIMAL_CHECK, RoundingMode.HALF_UP);
    }

    /**
     * jsonAmount类型的加法 (忽略币种情况下使用)
     * @param other 加项
     * @return 结果
     */
    public JsonAmount add(JsonAmount other) {
        return new JsonAmount(Objects.isNull(this.getAmount())?BigDecimal.ZERO:this.amount.add(Objects.isNull(other.getAmount())?BigDecimal.ZERO:other.amount), this.currency);
    }

    /**
     * jsonAmount类型的乘法
     * @param quantity 乘数
     * @return 乘法结果
     */
    public JsonAmount mul(BigDecimal quantity){
        return new JsonAmount(Objects.isNull(this.getAmount())?BigDecimal.ZERO:this.amount.multiply(quantity), this.currency);
    }

    /**
     * jsonAmount类型的减法 (忽略币种情况下使用)
     * @param other 加项
     * @return 结果
     */
    public JsonAmount sub(JsonAmount other) {
        return new JsonAmount(Objects.isNull(this.getAmount())?BigDecimal.ZERO:this.amount.subtract(Objects.isNull(other.getAmount())?BigDecimal.ZERO:other.amount), this.currency);
    }
}