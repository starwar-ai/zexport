package com.syj.eplus.module.wechat.service;

import com.syj.eplus.module.wechat.entity.approval.ApprovalDetailResp;
import com.syj.eplus.module.wechat.entity.approval.ApprovalResp;
import com.syj.eplus.module.wechat.entity.token.AccessToken;
import com.syj.eplus.module.wechat.entity.user.WechatUser;

import java.util.List;
import java.util.Map;

/**
 * @Description：
 * @Author：du
 * @Date：2024/3/13 18:50
 */
public interface WechatService {
    /**
     * 获取企微通讯录token
     *
     * @return token
     */
    AccessToken getAccessToken();

    /**
     * 获取企微userid
     *
     * @param access_token 通讯录token
     * @param code         回调接口返回的code
     * @return 企微用户
     */
    WechatUser getUserIdByCode(String access_token, String code);

    /**
     * 通过用户手机号获取企微用户id
     *
     * @param access_token token
     * @param mobile       手机号
     * @return 企微用户id
     */
    WechatUser getUserIdByMobile(String access_token, String mobile);

    /**
     * 获取企微审批单号列表
     *
     * @param startTime   起始时间
     * @param endTime     结束时间
     * @param record_type 审批单类型 1-请假 2-打卡补卡 3-出差 4-外出 5-加班 6-调班 7-会议室预定 8-退款审批 9-红包报销审批
     * @param sp_status   审批状态 1-审批中 2-已通过 3-已驳回 4-已撤销 6-通过后撤销 7-已删除 10-已支付
     * @return
     */
    ApprovalResp getApprovalList(String startTime, String endTime, String record_type, String sp_status,String template_id);

    /**
     * 根据审批单编号获取企微审批单详情
     *
     * @param spNo
     * @return
     */
    ApprovalDetailResp getApprovalDetail(String spNo);

    /**
     * 根据文件id获取企微附件
     *
     * @param mediaId 文件id
     * @return 附件信息 tuple(fileName,fileType,filePath,content)
     */
    Map<String, Object> getMedia(String mediaId);

    /**
     * 获取企微成员ID列表
     *
     * @return 成员
     */
    List<WechatUser> getDeptUserList();


    /**
     * 获取企微用户信息
     *
     * @param userId 企微用户id
     * @return 用户信息
     */
    WechatUser getUserInfo(String userId);

}
