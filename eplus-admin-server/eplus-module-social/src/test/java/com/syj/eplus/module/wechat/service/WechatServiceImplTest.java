package com.syj.eplus.module.wechat.service;

import com.syj.eplus.module.wechat.entity.approval.ApprovalDetailResp;
import com.syj.eplus.module.wechat.entity.approval.ApprovalResp;
import com.syj.eplus.module.wechat.entity.common.WeChatEntity;
import com.syj.eplus.module.wechat.entity.token.AccessToken;
import com.syj.eplus.module.wechat.entity.user.WechatUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WechatServiceImplTest {

    private WeChatEntity mockWeChatEntity;
    private WechatServiceImpl wechatServiceImplUnderTest;

    @BeforeEach
    void setUp() {
        mockWeChatEntity = new WeChatEntity()
                .setAgentid("1000005")
                .setAppid("ww6c36dbda65f99ac0")
                .setTokenurl("https://qyapi.weixin.qq.com/cgi-bin/gettoken")
                .setCropsecret("F2rUYkyqMHE055GnurshA0BdC5IW7WhzvZkHzb7HyTI")
                .setUser_code_url("https://qyapi.weixin.qq.com/cgi-bin/auth/getuserinfo")
                .setUser_mobile_url("https://qyapi.weixin.qq.com/cgi-bin/user/getuserid?access_token=")
                .setApproval_id_url("https://qyapi.weixin.qq.com/cgi-bin/oa/getapprovalinfo?access_token=")
                .setApproval_detail_url("https://qyapi.weixin.qq.com/cgi-bin/oa/getapprovaldetail?access_token=")
                .setMedia_url("https://qyapi.weixin.qq.com/cgi-bin/media/get");
        wechatServiceImplUnderTest = new WechatServiceImpl(mockWeChatEntity);
    }

    @Test
    void testGetAccessToken() {


        final AccessToken result = wechatServiceImplUnderTest.getAccessToken();

        assertNotNull(result);
        assertThat(result.getErrcode()).isEqualTo(0);
    }

    @Test
    void testGetUserIdByCode() {
        final WechatUser expectedResult = new WechatUser();
        expectedResult.setUserid("userid");

        when(mockWeChatEntity.getUser_code_url()).thenReturn("result");

        final WechatUser result = wechatServiceImplUnderTest.getUserIdByCode("access_token", "code");

        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testGetUserIdByMobile() {
        final WechatUser expectedResult = new WechatUser();
        expectedResult.setUserid("userid");

        when(mockWeChatEntity.getUser_mobile_url()).thenReturn("result");

        final WechatUser result = wechatServiceImplUnderTest.getUserIdByMobile("access_token", "mobile");

        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testGetApprovalList() {

        long endtime = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();
        long startTime = LocalDateTime.now().minusDays(3).atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();
        final ApprovalResp result = wechatServiceImplUnderTest.getApprovalList(String.valueOf(startTime), String.valueOf(endtime), "3",
                "2",null);
        assertNotNull(result);
        assertThat(result.getErrcode()).isEqualTo(0);
    }

    @Test
    void testGetApprovalDetail() {
        final ApprovalDetailResp result = wechatServiceImplUnderTest.getApprovalDetail("202403130001");
        assertNotNull(result);
        assertThat(result.getErrcode()).isEqualTo(0);
    }

    @Test
    void testGetMedia() {
//        Map<String, Object> media = wechatServiceImplUnderTest.getMedia("WWME_F1DTWQAAsxvkJT8Msppcv2vw5OjmXA");
//        assertNotNull(media);
        String s = "attachment; filename*=utf-8''9e27da785375474d8e8abe740b65dfde.jpg; filename=\"9e27da785375474d8e8abe740b65dfde.jpg\"";
        Pattern pattern = Pattern.compile("(?<=filename\\*=utf-8''|filename=\")(.+?\\.\\w+)(?=;|$)");
        Matcher matcher = pattern.matcher(s);

        if (matcher.find()) {
            String fullName = matcher.group();
            String[] parts = fullName.split("\\.");
            String fileName = parts[0];
            String fileType = parts[1];

            System.out.println("Filename: " + fileName);
            System.out.println("Filetype: " + fileType);
        }
    }
}
