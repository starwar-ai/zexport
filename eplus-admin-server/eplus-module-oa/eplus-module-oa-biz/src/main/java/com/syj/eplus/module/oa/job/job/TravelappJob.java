package com.syj.eplus.module.oa.job.job;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.quartz.core.handler.JobHandler;
import cn.iocoder.yudao.module.bpm.enums.task.BpmProcessInstanceResultEnum;
import cn.iocoder.yudao.module.infra.api.file.FileApi;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import com.syj.eplus.framework.common.dict.SocialWechatDict;
import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.module.oa.controller.admin.travelapp.vo.TravelAppSaveReqVO;
import com.syj.eplus.module.oa.service.travelapp.TravelAppService;
import com.syj.eplus.module.wechat.entity.approval.ApprovalDetailResp;
import com.syj.eplus.module.wechat.entity.approval.ApprovalResp;
import com.syj.eplus.module.wechat.enums.RecordTypeEnum;
import com.syj.eplus.module.wechat.enums.SpStatusEnum;
import com.syj.eplus.module.wechat.service.WechatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Description：同步企微出差申请定时任务
 * @Author：du
 * @Date：2024/3/13 15:24
 */
@Slf4j
@Component
public class TravelappJob implements JobHandler {
    @Resource
    private WechatService wechatService;

    @Resource
    private AdminUserApi adminUserApi;

    @Resource
    private TravelAppService travelAppService;

    @Resource
    private FileApi fileApi;

    @Override
    public String execute(String param) throws Exception {
        List<String> err_result = deallTravelappFileIdList();
        return err_result.toString();
    }


    /**
     * 处理近30天内出差审批记录
     *
     * @return
     */
    private List<String> deallTravelappFileIdList() {
        long endtime = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();
        long startTime = LocalDateTime.now().minusDays(30).atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();
        List<String> resultList = new ArrayList<>();
        ApprovalResp approvalResp = wechatService.getApprovalList(String.valueOf(startTime), String.valueOf(endtime), RecordTypeEnum.BUSINESS_TRIP.getValue(), SpStatusEnum.APPROVED.getStrStatus(),null);
        if (Objects.isNull(approvalResp)) {
            resultList.add(String.format("未获取到近30天内出差审批记录startTime-%s,endTime-%s", startTime, endtime));
        }
        approvalResp.getSp_no_list().forEach(spNo -> {
            //过滤已经同步过的企微出差申请单
            boolean exist;
            exist = travelAppService.validateTravelAppExistsByWecomId(spNo);
            if (exist) {
                return;
            }
            ApprovalDetailResp approvalDetailResp = wechatService.getApprovalDetail(spNo);
            if (Objects.isNull(approvalDetailResp)) {
                resultList.add(String.format("未获取到对应审批详情spNo-%s", spNo));
                return;
            }
            ApprovalDetailResp.Info info = approvalDetailResp.getInfo();
            if (Objects.isNull(info)) {
                resultList.add(String.format("获取到对应审批详情为空spNo-%s", spNo));
                return;
            }
            //只同步已通过的出差审批
            if (SpStatusEnum.APPROVED.getStatus() != info.getSpStatus()) {
                return;
            }

            LocalDateTime applyTime = transferTime(info.getApplyTime());
            ApprovalDetailResp.Info.Applyer applyer = info.getApplyer();
            if (Objects.isNull(applyer)) {
                resultList.add(String.format("获取到对应审批详情申请人信息为空spNo-%s", spNo));
                return;
            }
            String userid = applyer.getUserid();
            if (StrUtil.isEmpty(userid)) {
                resultList.add(String.format("获取到对应审批详情申请人id为空spNo-%s", spNo));
                return;
            }
            AdminUserRespDTO adminUser = adminUserApi.getUserMapByStringIdList(java.util.List.of(userid)).get(userid);
            if (Objects.isNull(adminUser)) {
                resultList.add(String.format("根据企微用户id-%s未获取到系统用户信息", userid));
                return;
            }
            Long adminUserId = adminUser.getId();
            TravelAppSaveReqVO travelAppSaveReqVO = new TravelAppSaveReqVO().setApplyerId(adminUserId).setWecomId(spNo).setApplyTime(applyTime).setAuditStatus(BpmProcessInstanceResultEnum.APPROVE.getResult());
            travelAppSaveReqVO = buildTravelAppData(info.getApplyData(), travelAppSaveReqVO);
            //插入出差申请
            Long travelAppId = travelAppService.createTravelApp(travelAppSaveReqVO);
//            fileService.updateFile(travelAppSaveReqVO.getFileAnnexList(), travelAppId, FileBusinessTypeEnum.TRAVELAPP_ANNEX.getType());
        });
        return resultList;
    }

    private TravelAppSaveReqVO buildTravelAppData(ApprovalDetailResp.Info.ApplyData applyData, TravelAppSaveReqVO travelAppSaveReqVO) {
        List<ApprovalDetailResp.Info.ApplyData.Content> contents = applyData.getContents();
        if (CollUtil.isEmpty(contents)) {
            return null;
        }
        contents.forEach(content -> dealContent(content, travelAppSaveReqVO));
        return travelAppSaveReqVO;
    }


    private TravelAppSaveReqVO dealContent(ApprovalDetailResp.Info.ApplyData.Content content, TravelAppSaveReqVO travelAppSaveReqVO) {
        return switch (content.getControl()) {
            case SocialWechatDict.TEXTAREA -> {
                travelAppSaveReqVO.setPurpose(dealTextArea(content));
                travelAppSaveReqVO.setIntendedObjectives(dealIntendedObjectives(content));
                yield travelAppSaveReqVO;
            }
            case SocialWechatDict.TEXT -> {
                travelAppSaveReqVO.setDest(dealText(content));
                yield travelAppSaveReqVO;
            }
            case SocialWechatDict.ATTENDANCE -> {
                dealAttendance(content, travelAppSaveReqVO);
                yield travelAppSaveReqVO;
            }
            case SocialWechatDict.FILE -> {
                dealFile(content, travelAppSaveReqVO);
                yield travelAppSaveReqVO;
            }
            case SocialWechatDict.CONTACT -> {
                dealCompanions(content, travelAppSaveReqVO);
                yield travelAppSaveReqVO;
            }
            default -> travelAppSaveReqVO;
        };
    }

    /**
     * 获取出差事由
     *
     * @param content 审批申请详情控件
     */
    private String dealTextArea(ApprovalDetailResp.Info.ApplyData.Content content) {
        long count = content.getTitle().stream().filter(titleItem -> SocialWechatDict.REASON.equals(titleItem.getText())).count();
        if (count > 0) {
            return content.getValue().getText();
        }
        return null;
    }

    /**
     * 获取拟达成目标
     *
     * @param content 审批申请详情控件
     */
    private String dealIntendedObjectives(ApprovalDetailResp.Info.ApplyData.Content content) {
        long count = content.getTitle().stream().filter(titleItem -> SocialWechatDict.INTENDED_OBJECTIVES.equals(titleItem.getText())).count();
        if (count > 0) {
            return content.getValue().getText();
        }
        return null;
    }

    /**
     * 获取出差地点
     *
     * @param content 审批申请详情控件
     */
    private String dealText(ApprovalDetailResp.Info.ApplyData.Content content) {
        long count = content.getTitle().stream().filter(titleItem -> SocialWechatDict.LOCATION.equals(titleItem.getText())).count();
        if (count > 0) {
            return content.getValue().getText();
        }
        return null;
    }

    /**
     * 获取出差时间
     *
     * @param content
     * @param travelAppSaveReqVO
     */
    private void dealAttendance(ApprovalDetailResp.Info.ApplyData.Content content, TravelAppSaveReqVO travelAppSaveReqVO) {
        long count = content.getTitle().stream().filter(titleItem -> SocialWechatDict.TRAVEL.equals(titleItem.getText())).count();
        if (count > 0) {
            long newBegin = content.getValue().getAttendance().getDateRange().getNewBegin();
            long newEnd = content.getValue().getAttendance().getDateRange().getNewEnd();
            travelAppSaveReqVO.setStartTime(transferTime(newBegin));
            travelAppSaveReqVO.setEndTime(transferTime(newEnd));
            travelAppSaveReqVO.setDuration(content.getValue().getAttendance().getDateRange().getNewDuration());
        }
    }

    private void dealFile(ApprovalDetailResp.Info.ApplyData.Content content, TravelAppSaveReqVO travelAppSaveReqVO) {
        long count = content.getTitle().stream().filter(titleItem -> SocialWechatDict.ANNEX.equals(titleItem.getText())).count();
        if (count > 0) {
            List<ApprovalDetailResp.Info.ApplyData.Content.Value.FileItem> files = content.getValue().getFiles();
            if (CollUtil.isEmpty(files)) {
                return;
            }
            List<String> fileIds = files.stream().map(ApprovalDetailResp.Info.ApplyData.Content.Value.FileItem::getFileId).toList();
            if (CollUtil.isNotEmpty(fileIds)) {
                // 用于存储文件id
                List<SimpleFile> simpleFileList = new ArrayList<>();
                fileIds.forEach(fileId -> {
                    Map<String, Object> media = wechatService.getMedia(fileId);
                    String fileUrl = fileApi.createFile((String) media.get(SocialWechatDict.FILE_TYPE), (String) media.get(SocialWechatDict.FILE_NAME), (String) media.get(SocialWechatDict.FILE_PATH), (byte[]) media.get(SocialWechatDict.FILE_CONTENT));
                    SimpleFile simpleFile = new SimpleFile().setId(null).setFileUrl(fileUrl).setName((String) media.get(SocialWechatDict.FILE_NAME)).setMainFlag(0);
                    simpleFileList.add(simpleFile);
                });
                travelAppSaveReqVO.setAnnex(simpleFileList);
            }
        }
    }

    private void dealCompanions(ApprovalDetailResp.Info.ApplyData.Content content, TravelAppSaveReqVO travelAppSaveReqVO) {
        long count = content.getTitle().stream().filter(titleItem -> SocialWechatDict.COMPANIONS.equals(titleItem.getText())).count();
        if (count > 0) {
            List<ApprovalDetailResp.Info.ApplyData.Content.Value.member> members = content.getValue().getMembers();
            if (CollUtil.isEmpty(members)) {
                return;
            }
            List<UserDept> userDepts = members.stream().map(s -> {
                AdminUserRespDTO dto = adminUserApi.getUserMapByStringIdList(java.util.List.of(s.getUserid())).get(s.getUserid());
                return dto == null ? null : adminUserApi.getUserDeptByUserId(dto.getId());
            }).filter(java.util.Objects::nonNull).toList();
            travelAppSaveReqVO.setCompanions(userDepts);
        }
    }

    /**
     * 时间戳转换为LocalDateTime类型
     *
     * @param time 时间戳
     * @return LocalDateTime
     */
    private LocalDateTime transferTime(Long time) {
        if (Objects.isNull(time)) {
            return null;
        }
        Instant instant = Instant.ofEpochSecond(time);
        ZoneId zoneId = ZoneId.systemDefault();
        return instant.atZone(zoneId).toLocalDateTime();
    }

}
