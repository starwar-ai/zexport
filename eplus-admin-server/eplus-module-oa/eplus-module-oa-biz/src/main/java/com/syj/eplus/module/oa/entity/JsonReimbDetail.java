package com.syj.eplus.module.oa.entity;

import com.sun.xml.bind.v2.TODO;
import com.syj.eplus.framework.common.entity.SimpleFile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Description：报销明细json实体
 * @Author：du
 * @Date：2024/4/24 10:02
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class JsonReimbDetail {
    /**
     * 主键
     */
    private String id;
    /**
     * 出差费用类型
     * <p>
     * 枚举 {@link TODO expense_type 对应的类}
     */
    private Integer expenseType;

    /**
     * 发票金额
     */
    private BigDecimal invoiceAmount;
    /**
     * 小项金额
     */
    private BigDecimal expenseAmount;
    /**
     * 币种
     */
    private String expenseCurrency;

    /**
     * 交通工具类型
     * <p>
     * 枚举 {@link TODO transportation_type 对应的类}
     */
    private Integer transportationType;
    /**
     * 出行日期
     */
    private LocalDateTime transportationDate;
    /**
     * 出发地
     */
    private String transportationFrom;
    /**
     * 目的地
     */
    private String transportationTo;
    /**
     * 出发里程
     */
    private BigDecimal mileageStart;
    /**
     * 结束里程
     */
    private BigDecimal mileageEnd;
    /**
     * 总公里数
     */
    private BigDecimal mileage;
    /**
     * 线路说明
     */
    private String routeDesc;
    /**
     * 住宿城市
     */
    private Integer lodgingCity;
    /**
     * 住宿标准
     */
    private BigDecimal lodgingStandard;
    /**
     * 住宿天数
     */
    private Integer stayLength;
    /**
     * 出差补贴天数
     */
    private Double allowanceDay;
    /**
     * 出差补贴标准
     */
    private BigDecimal allowanceStandard;

    /**
     * 其他
     */
    private String otherDesc;

    /**
     * 科目主键
     */
    private Long financialSubjectId;

    /**
     * 科目名称
     */
    private String financialSubjectName;

    /**
     * 过路费
     */
    private BigDecimal tollFee;

    /**
     * 发票号
     */
    private String invoiceCode;

    /**
     * 类别主键
     */
    private Long dictSubjectId;

    /**
     * 费用名称
     */
    private String feeName;

    /**
     * 发票
     */
    private List<SimpleFile> invoice;

    /**
     * 图片
     */
    private List<SimpleFile> picture;

    /**
     * 费用用途主键
     */
    private Long expenseUseToFormalId;

    /**
     * 费用用途（正式）
     */
    private String expenseUseToFormal;

    /**
     * 费用用途(发生)
     */
    private String expenseUseToOccur;
    /**
     * 备注
     */
    private String remark;
}
