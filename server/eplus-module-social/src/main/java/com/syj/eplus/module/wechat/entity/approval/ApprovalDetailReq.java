package com.syj.eplus.module.wechat.entity.approval;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Description：获取审批单号详情请求实体
 * @Author：du
 * @Date：2024/3/13 21:40
 */
@Data
@Accessors(chain = true)
public class ApprovalDetailReq {
    @JsonProperty("sp_no")
    private String spNo;
}
