package com.syj.eplus.module.wechat.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import com.syj.eplus.framework.common.dict.SocialWechatDict;
import com.syj.eplus.module.wechat.entity.approval.ApprovalDetailReq;
import com.syj.eplus.module.wechat.entity.approval.ApprovalDetailResp;
import com.syj.eplus.module.wechat.entity.approval.ApprovalReq;
import com.syj.eplus.module.wechat.entity.approval.ApprovalResp;
import com.syj.eplus.module.wechat.entity.common.WeChatEntity;
import com.syj.eplus.module.wechat.entity.token.AccessToken;
import com.syj.eplus.module.wechat.entity.user.WechatDeptUser;
import com.syj.eplus.module.wechat.entity.user.WechatUser;
import com.syj.eplus.module.wechat.enums.ApprovalFilterEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.TOKEN_NOT_EXISTS;
import static java.util.Objects.isNull;

/**
 * @Description：
 * @Author：du
 * @Date：2024/3/13 18:51
 */
@Service
@Validated
public class WechatServiceImpl implements WechatService {
    @Resource
    private final WeChatEntity weChatEntity;

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    private final AccessToken ACCESS_TOKEN_CACHE = null;


    public WechatServiceImpl(WeChatEntity weChatEntity) {
        this.weChatEntity = weChatEntity;
    }

    @Override
    public AccessToken getAccessToken() {
        // 先从缓存中获取未过期token
        if (Objects.nonNull(ACCESS_TOKEN_CACHE) && checkExpire()) {
            return ACCESS_TOKEN_CACHE;
        }
        HashMap<String, Object> tokenParams = new HashMap<>();
        tokenParams.put(SocialWechatDict.CORP_ID, weChatEntity.getAppid());
        tokenParams.put(SocialWechatDict.CORPSECRET, weChatEntity.getCropsecret());
        String tokenResult = HttpUtil.get(weChatEntity.getTokenurl(), tokenParams);
        if (StrUtil.isEmpty(tokenResult)) {
            logger.warn("[企业微信]通讯录token接口返回为空corpid-{},corpsecret-{}", weChatEntity.getAppid(), weChatEntity.getCropsecret());
            return null;
        }
        AccessToken accessToken;
        try {
            accessToken = JsonUtils.parseObject(tokenResult, AccessToken.class);
        } catch (Exception e) {
            logger.warn("[企业微信]token响应结果JSON转换失败 tokenResult-{}", tokenResult);
            return null;
        }
        // 重写过期时间 保留一分钟作为缓冲
        if (Objects.nonNull(accessToken)) {
            accessToken.setExpireTime(LocalDateTime.now().plusSeconds(accessToken.getExpires_in() - 60));
        }
        return accessToken;
    }

    @Override
    public WechatUser getUserIdByCode(String access_token, String code) {
        HashMap<String, Object> userParams = new HashMap<>();
        userParams.put(SocialWechatDict.ACCESS_TOKEN, access_token);
        userParams.put(SocialWechatDict.CODE, code);
        String userResult = HttpUtil.get(weChatEntity.getUser_code_url(), userParams);
        if (StrUtil.isEmpty(userResult)) {
            logger.warn("[企业微信]通过code获取登录用户id接口返回为空access_token-{},code-{}", access_token, code);
            return null;
        }
        WechatUser wechatUser;
        try {
            wechatUser = JsonUtils.parseObject(userResult, WechatUser.class);
        } catch (Exception e) {
            logger.warn("[企业微信]通过code获取登录用户id接口JSON转换异常userResult-{}", userResult);
            return null;
        }
        return wechatUser;
    }

    @Override
    public WechatUser getUserIdByMobile(String access_token, String mobile) {
//        HashMap<String, String> heads = new HashMap<>();
//        heads.put("access_token", access_token);
        var request = HttpUtil.createPost(weChatEntity.getUser_mobile_url() + access_token);
//        request.addHeaders(heads);
        HashMap<String, String> body = new HashMap<>();
        body.put(SocialWechatDict.MOBILE, mobile);
        request.body(JsonUtils.toJsonString(body));
        var response = request.execute();
        String userResult = response.body();
//        String userResult = HttpUtil.get("https://qyapi.weixin.qq.com/cgi-bin/user/getuserid", userParams);
        if (StrUtil.isEmpty(userResult)) {
            logger.warn("[企业微信]通过手机号获取用户id接口返回为空access_token-{},mobile-{}", access_token, mobile);
            return null;
        }
        WechatUser wechatUser;
        try {
            wechatUser = JsonUtils.parseObject(userResult, WechatUser.class);
        } catch (Exception e) {
            logger.warn("[企业微信]通过手机号获取用户id接口JSON转换异常userResult-{}", userResult);
            return null;
        }
        return wechatUser;
    }

    @Override
    public ApprovalResp getApprovalList(String startTime, String endTime, String record_type, String sp_status,String template_id) {
        ApprovalReq approvalReq = new ApprovalReq();
        approvalReq.setStarttime(startTime);
        approvalReq.setEndtime(endTime);
        List<ApprovalReq.Filters> approvalFilters = new ArrayList<>();
        if (StrUtil.isNotEmpty(record_type)){
            approvalFilters.add(new ApprovalReq.Filters().setKey(ApprovalFilterEnum.RECORD_TYPE.getKey()).setValue(record_type));
        }
        if (StrUtil.isNotEmpty(sp_status)){
            approvalFilters.add(new ApprovalReq.Filters().setKey(ApprovalFilterEnum.SP_STATUS.getKey()).setValue(sp_status));
        }
        if (StrUtil.isNotEmpty(template_id)){
            approvalFilters.add(new ApprovalReq.Filters().setKey(ApprovalFilterEnum.TEMPLATE_ID.getKey()).setValue(template_id));
        }
        approvalReq.setFilters(approvalFilters);
        AccessToken accessToken = getAccessToken();
        if (isNull(accessToken)) {
            logger.warn("[企业微信]审批接口获取到的accesstoken为空");
            return null;
        }
        HttpRequest request = HttpUtil.createPost(weChatEntity.getApproval_id_url() + accessToken.getAccess_token());
        request.body(JsonUtils.toJsonString(approvalReq));
        String respose = request.execute().body();
        if (StrUtil.isEmpty(respose)) {
            logger.warn("[企业微信]获取审批单号接口返回为空,url-{},access_token-{},startTime-{},endTime-{},record_type-{},sp_status-{}",
                    weChatEntity.getApproval_id_url(), accessToken.getAccess_token(), startTime, endTime, record_type, sp_status);
            return null;
        }
        ApprovalResp approvalResp;
        try {
            approvalResp = JsonUtils.parseObject(respose, ApprovalResp.class);
        } catch (Exception e) {
            logger.warn("[企业微信]获取审批单号接口JSON转换异常approvalResp-{}", respose);
            return null;
        }
        return approvalResp;
    }

    @Override
    public ApprovalDetailResp getApprovalDetail(String spNo) {
        AccessToken accessToken = getAccessToken();
        if (isNull(accessToken)) {
            logger.warn("[企业微信]审批接口获取到的accesstoken为空");
            return null;
        }
        HttpRequest request = HttpUtil.createPost(weChatEntity.getApproval_detail_url() + accessToken.getAccess_token());
        ApprovalDetailReq approvalDetailReq = new ApprovalDetailReq().setSpNo(spNo);
        request.body(JsonUtils.toJsonString(approvalDetailReq));
        String respose = request.execute().body();
        if (StrUtil.isEmpty(respose)) {
            logger.warn("[企业微信]获取审批单号接口返回为空,url-{},access_token-{},spNo-{}",
                    weChatEntity.getApproval_id_url(), accessToken.getAccess_token(), spNo);
            return null;
        }
        ApprovalDetailResp approvalDetailResp;
        try {
            approvalDetailResp = JsonUtils.parseObject(respose, ApprovalDetailResp.class);
        } catch (Exception e) {
            logger.warn("[企业微信]获取审批详情接口JSON转换异常approvalDetailResp-{}", respose);
            return null;
        }
        return approvalDetailResp;
    }

    @Override
    public Map<String, Object> getMedia(String mediaId) {
        AccessToken accessToken = getAccessToken();
        if (isNull(accessToken)) {
            logger.warn("[企业微信]审批接口获取到的accesstoken为空");
            return null;
        }
        Map<String, Object> mediaMap = new HashMap<>(4);
        HashMap<String, Object> userParams = new HashMap<>();
        userParams.put(SocialWechatDict.ACCESS_TOKEN, accessToken.getAccess_token());
        userParams.put(SocialWechatDict.MEDIA_ID, mediaId);
        HttpRequest request = HttpUtil.createGet(weChatEntity.getMedia_url());
        request.form(userParams);
        HttpResponse response = request.execute();
        try (InputStream inputStream = response.bodyStream()) {
            //试图读取一行或几个字节，如果输入流为空则会立即返回-1
            if (inputStream.read() == -1) {
                logger.warn("[企业微信]附件接口获取到的文件流为空");
                return null;
            }
            String contentDisposition = response.header(SocialWechatDict.CONT_DISPOSITION);
            Map<String, String> fileNameMap = getFileNameByUrl(contentDisposition);
            String fileName = fileNameMap.get(SocialWechatDict.FILE_NAME);
            mediaMap.put(SocialWechatDict.FILE_NAME, fileName);
            mediaMap.put(SocialWechatDict.FILE_TYPE, fileNameMap.get(SocialWechatDict.FILE_TYPE));
            mediaMap.put(SocialWechatDict.FILE_CONTENT, IoUtil.readBytes(inputStream));
            mediaMap.put(SocialWechatDict.FILE_PATH, getMediaPath(fileName));
        } catch (Exception e) {
            logger.error("[企业微信]获取文件异常-{}", e.getMessage());
        }

        return mediaMap;
    }

    @Override
    public List<WechatUser> getDeptUserList() {
        AccessToken accessToken = getAccessToken();
        if (isNull(accessToken)) {
            throw exception(TOKEN_NOT_EXISTS);
        }
        HttpRequest request = HttpUtil.createPost(weChatEntity.getDept_user_url() + accessToken.getAccess_token());
        HashMap<String, String> body = new HashMap<>();
        body.put(SocialWechatDict.CURSOR, null);
        body.put(SocialWechatDict.LIMIT, null);
        request.body(JsonUtils.toJsonString(body));
        String respose = request.execute().body();
        if (StrUtil.isEmpty(respose)) {
            logger.warn("[企业微信]获取审批单号接口返回为空,url-{},respose-{},access_token-{}",
                    weChatEntity.getApproval_id_url(), respose, accessToken.getAccess_token());
            return null;
        }
        WechatDeptUser wechatDeptUser;
        try {
            wechatDeptUser = JsonUtils.parseObject(respose, WechatDeptUser.class);
        } catch (Exception e) {
            logger.warn("[企业微信]获取获取成员ID列表接口JSON转换异常wechatDeptUser-{}", respose);
            return null;
        }
        if (Objects.isNull(wechatDeptUser)) {
            logger.warn("[企业微信]获取获取成员ID列表接口为空wechatDeptUser-{}", wechatDeptUser);
            return null;
        }
        List<WechatUser> deptUserList = wechatDeptUser.getDept_user();
        if (CollUtil.isEmpty(deptUserList)) {
            logger.warn("[企业微信]获取获取成员ID列表接口为空deptUser-{}", deptUserList);
            return null;
        }
        deptUserList.forEach(deptUser -> {
            WechatUser wechatUser = getUserInfo(deptUser.getUserid());
            if (Objects.nonNull(wechatUser)) {
                deptUser.setMobile(wechatUser.getMobile());
            }
        });
        return deptUserList;
    }

    @Override
    public WechatUser getUserInfo(String userId) {
        AccessToken accessToken = getAccessToken();
        if (isNull(accessToken)) {
            logger.warn("[企业微信]获取用户详情接口获取到的accesstoken为空");
            return null;
        }
        HashMap<String, Object> userParams = new HashMap<>();
        userParams.put(SocialWechatDict.ACCESS_TOKEN, accessToken.getAccess_token());
        userParams.put(SocialWechatDict.USER_ID, userId);
        String userResult = HttpUtil.get(weChatEntity.getUser_url(), userParams);
        if (StrUtil.isEmpty(userResult)) {
            logger.warn("[企业微信]获取用户详情接口返回为空access_token-{}", accessToken.getAccess_token());
            return null;
        }
        WechatUser wechatUser;
        try {
            wechatUser = JsonUtils.parseObject(userResult, WechatUser.class);
        } catch (Exception e) {
            logger.warn("[企业微信]获取用户详情接口JSON转换异常userResult-{}", userResult);
            return null;
        }
        return wechatUser;
    }

    /**
     * 校验企微access_token是否过期
     *
     * @return true-未过期 false-过期
     */
    private boolean checkExpire() {
        //过期缓存为空则返回false重新获取
        if (isNull(ACCESS_TOKEN_CACHE) || isNull(ACCESS_TOKEN_CACHE.getExpireTime())) {
            return false;
        }
        return LocalDateTime.now().isBefore(ACCESS_TOKEN_CACHE.getExpireTime());
    }

    private Map<String, String> getFileNameByUrl(String contentDisposition) {
        String fileName = null;
        String fileType = null;
        Map<String, String> fileNameMap = new HashMap<>();
        if (StrUtil.isEmpty(contentDisposition)) {
            return new HashMap<>();
        }
        Pattern pattern = Pattern.compile("(?<=filename\\*=utf-8''|filename=\")(.+?\\.\\w+)(?=;|$)");
        Matcher matcher = pattern.matcher(contentDisposition);
        if (matcher.find()) {
            fileName = matcher.group();
            String[] parts = fileName.split("\\.");
            fileType = parts[1];
        }
        fileNameMap.put(SocialWechatDict.FILE_NAME, fileName);
        fileNameMap.put(SocialWechatDict.FILE_TYPE, fileType);
        return fileNameMap;
    }

    private String getMediaPath(String fileName) {
        return "social" + SocialWechatDict.SLASH + "wechat" + SocialWechatDict.SLASH + fileName;
    }
}
