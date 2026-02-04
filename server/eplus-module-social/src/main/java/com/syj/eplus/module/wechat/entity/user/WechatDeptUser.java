package com.syj.eplus.module.wechat.entity.user;

import com.syj.eplus.module.wechat.entity.common.WechatResp;
import lombok.Data;

import java.util.List;

@Data
public class WechatDeptUser extends WechatResp {
    private List<WechatUser> dept_user;
}
