package com.syj.eplus.module.crm.controller.admin.cust.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import lombok.Data;

/**
 * @Description：
 * @Author：du
 * @Date：2024/5/6 14:15
 */
@Data
public class SimplePageReqVO extends PageParam {
    private String custName;
}
