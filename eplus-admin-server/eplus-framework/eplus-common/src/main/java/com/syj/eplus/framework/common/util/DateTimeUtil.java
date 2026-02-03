package com.syj.eplus.framework.common.util;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

public class DateTimeUtil {
    /**
     * 获取离指定日期最近的付款日
     * @param inputDate 日期
     * @return 最近付款日
     */
    public static LocalDateTime getNearestPaymentDay(LocalDateTime inputDate) {
        int dayOfMonth = inputDate.getDayOfMonth();
        if (dayOfMonth <= 5) {
            // 返回当月5号
            return inputDate.with(TemporalAdjusters.firstDayOfMonth()).plusDays(4);
        } else if (dayOfMonth <= 15) {
            // 返回当月15号
            return inputDate.with(TemporalAdjusters.firstDayOfMonth()).plusDays(14);
        } else if (dayOfMonth <= 25) {
            // 返回当月25号
            return inputDate.with(TemporalAdjusters.firstDayOfMonth()).plusDays(24);
        } else {
            // 返回下个月5号
            return inputDate.with(TemporalAdjusters.firstDayOfNextMonth()).plusDays(4);
        }
    }
}
