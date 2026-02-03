package cn.iocoder.yudao.module.infra.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description：
 * @Author：du
 * @Date：2024/2/22 10:08
 */
@Data
public class CurrencysRate {
    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 日期
     * String 是为了查询方便
     */
    private String dailyCurrDate;
    /**
     * 币种
     */
    private String dailyCurrName;
    /**
     * 汇率
     */
    private BigDecimal dailyCurrRate;
    /**
     * 来源 1-自动 0-手动
     */
    private Integer dailyCurrSource;
    /**
     * 中间汇率
     */
    private BigDecimal dailyCurrMidRate;
}
