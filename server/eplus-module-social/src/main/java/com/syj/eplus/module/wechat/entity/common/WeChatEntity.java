package com.syj.eplus.module.wechat.entity.common;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Description：
 * @Author：du
 * @Date：2024/3/7 21:05
 */

@Component
@ConfigurationProperties(prefix = "wechat")
@Setter
@Getter
@ToString
@Accessors(chain = true)
public class WeChatEntity {
    private String login_type;

    private String appid;

    private String agentid;

    private String lang;

    private String cropsecret;

    private String approval_id_url;

    private String approval_detail_url;

    private String media_url;
    private String tokenurl;

    private String user_code_url;

    private String user_mobile_url;

    private String dept_user_url;
    private String user_url;
}
