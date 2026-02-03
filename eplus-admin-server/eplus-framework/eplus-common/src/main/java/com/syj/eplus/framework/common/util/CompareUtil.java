package com.syj.eplus.framework.common.util;

import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.JsonWeight;
import com.syj.eplus.framework.common.entity.UserDept;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class CompareUtil {
    public static boolean compare(Object oldValue, Object newValue) {
        if (Objects.isNull(oldValue) && Objects.isNull(newValue)) {
            return true;
        }
        if (Objects.isNull(oldValue) || Objects.isNull(newValue)) {
            return false;
        }
        if (!oldValue.getClass().equals(newValue.getClass())) {
            return false;
        }
        if (oldValue instanceof String) {
            return oldValue.equals(newValue);
        } else if (oldValue instanceof BigDecimal bigOldValue) {
            BigDecimal bigNewValue = (BigDecimal) newValue;
            return NumUtil.bigDecimalsEqual(bigOldValue.setScale(6, RoundingMode.HALF_UP), bigNewValue.setScale(6, RoundingMode.HALF_UP));
        } else if (oldValue instanceof JsonAmount oldJsonAmount) {
            JsonAmount newJsonAmount = (JsonAmount) newValue;
            return compareJsonAmount(oldJsonAmount,newJsonAmount);
        } else if (oldValue instanceof JsonWeight) {
            return compareJsonWight((JsonWeight)oldValue,(JsonWeight)newValue);
        } else if (oldValue instanceof UserDept) {
            return compareUserDept((UserDept)oldValue,(UserDept)newValue);
        }else {
            return Objects.equals(oldValue, newValue);
        }
    }

    private static boolean compareJsonAmount(JsonAmount oldJsonAmount, JsonAmount newJsonAmount) {
        BigDecimal oldAmount = Objects.isNull(oldJsonAmount.getAmount())?BigDecimal.ZERO:oldJsonAmount.getAmount();
        BigDecimal newAmount = Objects.isNull(newJsonAmount.getAmount())?BigDecimal.ZERO:newJsonAmount.getAmount();
        boolean amountFlag;
        boolean currencyFlag;
        amountFlag = oldAmount.setScale(6, RoundingMode.HALF_UP).compareTo(newAmount.setScale(6, RoundingMode.HALF_UP)) == 0;
        currencyFlag = Objects.equals(oldJsonAmount.getCurrency(), newJsonAmount.getCurrency());
        return amountFlag && currencyFlag;
    }

    private static boolean compareJsonWight(JsonWeight oldJsonWight, JsonWeight newJsonWight) {
        BigDecimal oldWight = Objects.isNull(oldJsonWight.getWeight())?BigDecimal.ZERO:oldJsonWight.getWeight();
        BigDecimal newWight = Objects.isNull(newJsonWight.getWeight())?BigDecimal.ZERO:newJsonWight.getWeight();
        boolean wightFlag;
        boolean currencyFlag;
        wightFlag = oldWight.setScale(6, RoundingMode.HALF_UP).compareTo(newWight.setScale(6, RoundingMode.HALF_UP)) == 0;

        currencyFlag = Objects.equals(oldJsonWight.getUnit(), newJsonWight.getUnit());
        return wightFlag && currencyFlag;
    }

    private static boolean compareUserDept(UserDept oldUser,UserDept newUser){
        Long oldUserId = oldUser.getUserId();
        Long newUserId = newUser.getUserId();
        if (Objects.isNull(oldUserId) && Objects.isNull(newUserId)){
            return true;
        }else if (Objects.isNull(oldUserId) || Objects.isNull(newUserId)){
            return false;
        }else {
            return oldUserId.equals(newUserId);
        }
    }
}
