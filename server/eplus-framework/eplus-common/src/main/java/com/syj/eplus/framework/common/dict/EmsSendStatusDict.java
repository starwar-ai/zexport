package com.syj.eplus.framework.common.dict;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: du
 * @Date: 2024/08/22/11:56
 * @Description:
 */
@Data
public class EmsSendStatusDict {
    public static final int PENDING_SUBMIT = 1; //待提交
    public static final int PENDING_SEND = 2;   //待寄出
    public static final int ALREADY_SEND = 3;   //已寄出
    public static final int PENGDING_PAY = 4;   //待请款
    public static final int COMPLETED = 5;  //已完成
}
