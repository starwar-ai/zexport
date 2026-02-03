package com.syj.eplus.module.wechat.entity.user;

import com.syj.eplus.module.wechat.entity.common.WechatResp;
import lombok.Data;

/**
 * @Description：
 * @Author：du
 * @Date：2024/3/10 18:08
 */
@Data
public class WechatUser extends WechatResp {

    private String userid;

    private String mobile;

    private String position;

}
