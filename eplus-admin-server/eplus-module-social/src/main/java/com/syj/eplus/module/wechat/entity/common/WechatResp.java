package com.syj.eplus.module.wechat.entity.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description：
 * @Author：du
 * @Date：2024/3/10 18:07
 */
@Data
public class WechatResp implements Serializable {
    private Integer errcode;

    private String errmsg;
}
