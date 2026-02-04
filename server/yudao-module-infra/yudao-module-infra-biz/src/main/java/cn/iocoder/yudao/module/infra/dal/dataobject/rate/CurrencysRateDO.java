package cn.iocoder.yudao.module.infra.dal.dataobject.rate;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;

/**
 * @Description：
 * @Author：du
 * @Date：2024/1/12 14:30
 */
@TableName("daily_currencys_rate")
@KeySequence("daily_currencys_rate_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrencysRateDO extends BaseDO {
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
