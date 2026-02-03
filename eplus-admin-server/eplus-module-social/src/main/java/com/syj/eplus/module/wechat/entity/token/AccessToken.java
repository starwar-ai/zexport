package com.syj.eplus.module.wechat.entity.token;

import com.syj.eplus.module.wechat.entity.common.WechatResp;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Description：
 * @Author：du
 * @Date：2024/3/10 17:58
 */
@Data
public class AccessToken extends WechatResp {
    private String access_token;

    private Long expires_in;

    private LocalDateTime expireTime;
}
