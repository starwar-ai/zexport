package com.syj.eplus.module.wechat.entity.approval;

import com.syj.eplus.module.wechat.entity.common.WechatResp;
import lombok.Data;

import java.util.List;

/**
 * @Description：
 * @Author：du
 * @Date：2024/3/13 19:10
 */
@Data
public class ApprovalResp extends WechatResp {
    private List<String> sp_no_list;
}
