package com.syj.eplus.module.oa.job.job;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.quartz.core.handler.JobHandler;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import com.syj.eplus.framework.common.dict.SocialWechatDict;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.module.oa.dal.dataobject.expenseapp.ExpenseApp;
import com.syj.eplus.module.oa.dal.mysql.expenseapp.ExpenseAppMapper;
import com.syj.eplus.module.oa.entity.JsonExpenseAppItem;
import com.syj.eplus.module.oa.service.expenseapp.ExpenseAppService;
import com.syj.eplus.module.wechat.entity.approval.ApprovalDetailResp;
import com.syj.eplus.module.wechat.entity.approval.ApprovalResp;
import com.syj.eplus.module.wechat.enums.SpStatusEnum;
import com.syj.eplus.module.wechat.service.WechatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class EntertainAppJob implements JobHandler {

    @Resource
    private WechatService wechatService;

    @Resource
    private ExpenseAppMapper expenseAppMapper;

    @Resource
    private AdminUserApi adminUserApi;

    @Resource
    private ExpenseAppService expenseAppService;

    @Override
    public String execute(String param) throws Exception {
        return dealExpenseAppIdList().toString();
    }

    /**
     * 处理近30天内招待费审批记录
     *
     * @return
     */
    private List<String> dealExpenseAppIdList() {
        long endtime = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();
        long startTime = LocalDateTime.now().minusDays(30).atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();
        List<String> resultList = new ArrayList<>();
        List<ExpenseApp> insertList = new ArrayList<>();
        ApprovalResp approvalResp = wechatService.getApprovalList(String.valueOf(startTime), String.valueOf(endtime), null, SpStatusEnum.APPROVED.getStrStatus(), "C4UE5q4bgv9hkrQ8UFEH1P6hEKqhdb4m7LwN6X43z");
        if (Objects.isNull(approvalResp)) {
            resultList.add(String.format("未获取到近30天内出差审批记录startTime-%s,endTime-%s", startTime, endtime));
        }
        approvalResp.getSp_no_list().forEach(spNo -> {
            //过滤已经同步过的企微出差申请单
            if (expenseAppService.validateExpenseAppExistsBySpNo(spNo)) {
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
            ExpenseApp expenseApp = new ExpenseApp().setApplyerId(adminUserId).setSpNo(spNo);
            buildTravelAppData(info.getApplyData(), expenseApp);
            insertList.add(expenseApp);
        });
        if (CollUtil.isNotEmpty(insertList)) {
            expenseAppMapper.insertBatch(insertList);
        }
        return resultList;
    }

    private void buildTravelAppData(ApprovalDetailResp.Info.ApplyData applyData, ExpenseApp expenseApp) {
        List<ApprovalDetailResp.Info.ApplyData.Content> contents = applyData.getContents();
        if (CollUtil.isEmpty(contents)) {
            return;
        }
        contents.forEach(content -> {
            dealContent(content, expenseApp);
        });
    }

    private void dealContent(ApprovalDetailResp.Info.ApplyData.Content content, ExpenseApp expenseApp) {
        switch (content.getControl()) {
            case SocialWechatDict.DATE -> expenseApp.setExpenseDate(dealDate(content));
            case SocialWechatDict.TEXT -> {
                dealCustName(content,expenseApp);
                dealOrderNo(content,expenseApp);
            }
            case SocialWechatDict.CONTACT -> expenseApp.setCompanions(dealCompanions(content));
            case SocialWechatDict.ENTERTAIN_NUM_NG -> expenseApp.setEntertainNum(dealEntertainNumber(content));
            case SocialWechatDict.SELECTOR -> expenseApp.setCustLevel(dealCustLevel(content));
            case SocialWechatDict.TABLE -> expenseApp.setChildren(dealJsonExpenseAppItem(content));
            case SocialWechatDict.TEXTAREA -> expenseApp.setEntertainEffect(dealReceptionEffect(content));
            case SocialWechatDict.REPATED_APPROVAL -> expenseApp.setRelateAppCode(dealRelatedApprovel(content));
        }
    }

    private List<String> dealRelatedApprovel(ApprovalDetailResp.Info.ApplyData.Content content) {
        long count = content.getTitle().stream().filter(titleItem -> SocialWechatDict.REPATED_APPROVAL_CH.equals(titleItem.getText())).count();
        if (count > 0) {
            List<ApprovalDetailResp.Info.ApplyData.Content.Value.RelatedApproval> relatedApproval = content.getValue().getRelatedApproval();
            if (CollUtil.isEmpty(relatedApproval)){
                return List.of();
            }
            return relatedApproval.stream().map(ApprovalDetailResp.Info.ApplyData.Content.Value.RelatedApproval::getSpNo).toList();
        }
        return List.of();
    }

    /**
     * 处理本次招待成效
     * @param content 审批详情内容
     * @return  本次招待成效
     */
    private String dealReceptionEffect(ApprovalDetailResp.Info.ApplyData.Content content){
        long count = content.getTitle().stream().filter(titleItem -> SocialWechatDict.RECEPTIOM_EFFECT.equals(titleItem.getText())).count();
        if (count > 0) {
            return content.getValue().getText();
        }
        return null;
    }
    /**
     * 处理招待费申请明细
     * @param content 审批详情内容
     * @return 招待费申请明细
     */

    private List<JsonExpenseAppItem> dealJsonExpenseAppItem(ApprovalDetailResp.Info.ApplyData.Content content) {
        long count = content.getTitle().stream().filter(titleItem -> SocialWechatDict.EXPENSE_CONTENT.equals(titleItem.getText())).count();
        if (count == 0) {
            return List.of();
        }
        List<ApprovalDetailResp.Info.ApplyData.Content.Value.Children> children = content.getValue().getChildren();
        if (CollUtil.isEmpty(children)) {
            return List.of();
        }
        List<JsonExpenseAppItem> result = new ArrayList<>();
        children.forEach(s -> {
            List<ApprovalDetailResp.Info.ApplyData.Content> childList = s.getChildList();
            if (CollUtil.isEmpty(childList)) {
                return;
            }
            JsonExpenseAppItem jsonExpenseAppItem = new JsonExpenseAppItem();
            childList.forEach(childContent -> {
                String control = childContent.getControl();
                switch (control) {
                    case SocialWechatDict.MONEY -> {
                        dealExpense(childContent, SocialWechatDict.FOOD_EXPENSE,jsonExpenseAppItem);
                        dealExpense(childContent, SocialWechatDict.FRUIT_EXPENSE,jsonExpenseAppItem);
                        dealExpense(childContent, SocialWechatDict.GIFT_EXPENSE,jsonExpenseAppItem);
                        dealExpense(childContent, SocialWechatDict.TRAFFIC_EXPENSE,jsonExpenseAppItem);
                        dealExpense(childContent, SocialWechatDict.OTHER_EXPENSE,jsonExpenseAppItem);
                        dealExpense(childContent, SocialWechatDict.TOTAL_EXPENSE,jsonExpenseAppItem);
                    }
                    case SocialWechatDict.TEXT ->
                            jsonExpenseAppItem.setOtherExpenseDesc(dealOtherExpenseDesc(childContent));
                    case SocialWechatDict.FORMULA -> jsonExpenseAppItem.setTotalExpense(dealTotalExpense(childContent));
                }
            });
            result.add(jsonExpenseAppItem);
        });
        return result;
    }

    private BigDecimal dealTotalExpense(ApprovalDetailResp.Info.ApplyData.Content content) {
        long count = content.getTitle().stream().filter(titleItem -> SocialWechatDict.TOTAL_EXPENSE.equals(titleItem.getText())).count();
        if (count > 0) {
            return new BigDecimal(content.getValue().getFormula().getValue());
        }
        return BigDecimal.ZERO;
    }


    /**
     * 处理其他费用描述
     * @param content 审批详情内容
     * @return 其他费用描述
     */
    private String dealOtherExpenseDesc(ApprovalDetailResp.Info.ApplyData.Content content) {
        long count = content.getTitle().stream().filter(titleItem -> SocialWechatDict.OTHER_EXPENSE_DESC.equals(titleItem.getText())).count();
        if (count > 0) {
            return content.getValue().getText();
        }
        return null;
    }

    /**
     * 处理招待费用
     * @param content 审批详情内容
     * @return 餐饮费
     */
    private void dealExpense(ApprovalDetailResp.Info.ApplyData.Content content, String type,JsonExpenseAppItem jsonExpenseAppItem) {
        long count = content.getTitle().stream().filter(titleItem -> type.equals(titleItem.getText())).count();
        if (count > 0) {
            BigDecimal money =  new BigDecimal(content.getValue().getMoney());
            switch (type){
                case SocialWechatDict.FOOD_EXPENSE -> jsonExpenseAppItem.setFoodExpense(money);
                case SocialWechatDict.FRUIT_EXPENSE -> jsonExpenseAppItem.setFruitExpense(money);
                case SocialWechatDict.GIFT_EXPENSE -> jsonExpenseAppItem.setGiftExpense(money);
                case SocialWechatDict.TRAFFIC_EXPENSE -> jsonExpenseAppItem.setTrafficExpense(money);
                case SocialWechatDict.OTHER_EXPENSE -> jsonExpenseAppItem.setOtherExpense(money);
            }
        }
    }

    /**
     * 处理客户等级
     * @param content 审批详情内容
     * @return 客户等级
     */
    private String dealCustLevel(ApprovalDetailResp.Info.ApplyData.Content content) {
        long count = content.getTitle().stream().filter(titleItem -> SocialWechatDict.CUST_LEVEL_CH.equals(titleItem.getText())).count();
        if (count > 0) {
            List<ApprovalDetailResp.Info.ApplyData.Content.Value.Selector.Options> options = content.getValue().getSelector().getOptions();
            if (CollUtil.isEmpty(options)){
                return null;
            }
            List<ApprovalDetailResp.Info.ApplyData.Content.Value.Selector.Options.OptValue> value = options.get(0).getValue();
            if (CollUtil.isEmpty(value)){
                return null;
            }
            return value.get(0).getText();
        }
        return null;

    }

    /**
     * 处理费用归属订单
     * @param content 审批详情内容
     * @return 费用归属订单
     */
    private void dealOrderNo(ApprovalDetailResp.Info.ApplyData.Content content,ExpenseApp expenseApp) {
        long count = content.getTitle().stream().filter(titleItem -> SocialWechatDict.ORDER_DEPT.equals(titleItem.getText())).count();
        if (count > 0) {
            expenseApp.setOrderNo(content.getValue().getText());
        }
    }

    /**
     * 处理招待人数
     * @param content 审批详情内容
     * @return 招待人数
     */
    private Integer dealEntertainNumber(ApprovalDetailResp.Info.ApplyData.Content content) {
        long count = content.getTitle().stream().filter(titleItem -> SocialWechatDict.ENTERTAIN_NUM_CH.equals(titleItem.getText())).count();
        if (count > 0) {
            return content.getValue().getNumber();
        }
        return null;

    }

    /**
     * 处理同行人员
     * @param content 审批详情内容
     * @return 同行人员
     */
    private List<UserDept> dealCompanions(ApprovalDetailResp.Info.ApplyData.Content content) {
        long count = content.getTitle().stream().filter(titleItem -> SocialWechatDict.ENTERTAIN_COMPANIONS.equals(titleItem.getText())).count();
        if (count > 0) {
            List<ApprovalDetailResp.Info.ApplyData.Content.Value.member> members = content.getValue().getMembers();
            if (CollUtil.isEmpty(members)) {
                return List.of();
            }
            return members.stream().map(s -> {
                AdminUserRespDTO dto = adminUserApi.getUserMapByStringIdList(java.util.List.of(s.getUserid())).get(s.getUserid());
                return dto == null ? null : adminUserApi.getUserDeptByUserId(dto.getId());
            }).filter(java.util.Objects::nonNull).toList();
        }
        return List.of();
    }

    /**
     * 处理客户名称
     * @param content 审批详情内容
     * @return 客户名称
     */
    private void dealCustName(ApprovalDetailResp.Info.ApplyData.Content content,ExpenseApp expenseApp) {
        long count = content.getTitle().stream().filter(titleItem -> SocialWechatDict.CUST_NAME.equals(titleItem.getText())).count();
        if (count > 0) {
            expenseApp.setCustName(content.getValue().getText());
        }
    }

    /**
     * 处理招待日期
     * @param content 审批详情内容
     * @return 招待日期
     */
    private LocalDateTime dealDate(ApprovalDetailResp.Info.ApplyData.Content content) {
        long count = content.getTitle().stream().filter(titleItem -> SocialWechatDict.DATE_CH.equals(titleItem.getText())).count();
        if (count > 0) {
            Long timeStamp = content.getValue().getDate().getTimeStamp();
            return transferTime(timeStamp);
        }
        return null;
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
