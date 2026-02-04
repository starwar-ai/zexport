package com.syj.eplus.module.service.send;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.operatelog.core.util.OperateCompareUtil;
import cn.iocoder.yudao.framework.operatelog.core.util.OperateLogUtils;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.bpm.api.task.BpmProcessInstanceApi;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import cn.iocoder.yudao.module.bpm.enums.task.BpmProcessInstanceResultEnum;
import cn.iocoder.yudao.module.system.api.dept.DeptApi;
import cn.iocoder.yudao.module.system.api.dept.dto.DeptRespDTO;
import cn.iocoder.yudao.module.system.api.logger.OperateLogApi;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import com.syj.eplus.framework.common.entity.ChangeRecord;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.framework.common.enums.*;
import com.syj.eplus.module.controller.admin.send.vo.*;
import com.syj.eplus.module.controller.admin.send.vo.feeshare.FeeShareReqVO;
import com.syj.eplus.module.controller.admin.sendproduct.vo.SendProductRespVO;
import com.syj.eplus.module.controller.admin.sendproduct.vo.SendProductSaveReqVO;
import com.syj.eplus.module.convert.send.SendConvert;
import com.syj.eplus.module.dal.dataobject.send.SendDO;
import com.syj.eplus.module.dal.dataobject.sendbill.SendBillDO;
import com.syj.eplus.module.dal.dataobject.sendproduct.SendProductDO;
import com.syj.eplus.module.dal.mysql.send.SendMapper;
import com.syj.eplus.module.dal.mysql.sendproduct.SendProductMapper;
import com.syj.eplus.module.ems.api.send.dto.EmsSendDetailDTO;
import com.syj.eplus.module.ems.api.send.dto.EmsSendUpdateDTO;
import com.syj.eplus.module.infra.api.CodeGeneratorApi;
import com.syj.eplus.module.oa.api.feeshare.FeeShareApi;
import com.syj.eplus.module.oa.api.feeshare.dto.FeeShareReqDTO;
import com.syj.eplus.module.oa.api.feeshare.dto.FeeShareRespDTO;
import com.syj.eplus.module.scm.api.vender.VenderApi;
import com.syj.eplus.module.scm.api.vender.dto.SimpleVenderRespDTO;
import com.syj.eplus.module.service.sendbill.SendBillService;
import com.syj.eplus.module.service.sendproduct.SendProductService;
import com.syj.eplus.module.sms.api.dto.SmsContractAllDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.framework.common.dict.EmsSendStatusDict.*;
import static com.syj.eplus.module.ems.enums.ErrorCodeConstants.*;
import static com.syj.eplus.module.scm.enums.ErrorCodeConstants.VENDER_NOT_EXISTS;

/**
 * 寄件 Service 实现类
 *
 * @author zhangcm
 */
@Service
@Validated
public class SendServiceImpl implements SendService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    @Resource
    private CodeGeneratorApi codeGeneratorApi;
    @Resource
    private SendMapper sendMapper;

    @Resource
    private AdminUserApi adminUserApi;
    @Resource
    private VenderApi venderApi;
    @Resource
    private SendProductMapper sendProductMapper ;
    @Resource
    private FeeShareApi feeShareApi;
    @Resource
    private AdminUserApi userApi;
    @Resource
    private OperateLogApi operateLogApi;
    @Resource
    private SendBillService sendBillService;
    @Resource
    private SendProductService sendProductService;
    @Resource
    private BpmProcessInstanceApi bpmProcessInstanceApi;
    @Resource
    private DeptApi deptApi;

    private static final String CODE_PREFIX = "ES";
    public static final String SN_TYPE = "SN_EMS_SEND";
    public static final String OPERATOR_EXTS_KEY = "emsSendCode";
    public static final String PROCESS_DEFINITION_KEY = "ems_send";
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createSend(SendSaveInfoReqVO createReqVO) {
        SendDO send = SendConvert.INSTANCE.convertSendDO(createReqVO);
        String categoryCode = codeGeneratorApi.getCodeGenerator(SN_TYPE, CODE_PREFIX);
        var now = LocalDateTime.now();
        send.setId(null);
        send.setPayStatus(PaymentStatusEnum.UNPAID.getValue());
        send.setCode(categoryCode);
        send.setAuditStatus(BpmProcessInstanceResultEnum.UNSUBMITTED.getResult());
        send.setCreateTime(now);
        send.setSendStatus(EmsSendStatusEnum.PENDING_SUBMIT.getCode());
        send.setSendStatus(EmsSendStatusEnum.PENDING_SUBMIT.getCode());
        if(Objects.nonNull(send.getExpressCode())){
            send.setSendTime(now);
        }
        sendMapper.insert(send);
        Long sendId = send.getId();

        List<SendProductSaveReqVO> children = createReqVO.getChildren();
        if(CollUtil.isNotEmpty(children)){
            children.forEach(s->{
                s.setId(null);
                s.setSendId(send.getId());
                s.setPicture(s.getMainPicture());
            });
            List<SendProductDO> productDOList = BeanUtils.toBean(children, SendProductDO.class);
            sendProductMapper.insertBatch(productDOList);
        }

        //更新录入人
        long createId = Long.parseLong(send.getCreator());
        UserDept userDept = userApi.getUserDeptByUserId(createId);
        if(userDept != null ){
            send.setInputUserId(userDept.getUserId())
                    .setInputUserName(userDept.getNickname())
                    .setInputDeptId(userDept.getDeptId())
                    .setInputDeptName(userDept.getDeptName());
        }

        //更新实际寄件人
        UserDept actualUserDept = userApi.getUserDeptByUserId(createReqVO.getActualUserId());
        if(actualUserDept != null ){
            send.setActualUser(actualUserDept);
        }

        //处理费用归集
        dealFeeShare(createReqVO.getFeeShare(),sendId);

        //跟新标记，录入人信息
        sendMapper.updateById(send);

        //处理提交
        dealSubmit(createReqVO.getSubmitFlag(),send);

        // 补充操作日志明细
        OperateLogUtils.setContent(String.format("【新增寄件】%s", categoryCode));
        OperateLogUtils.addExt(OPERATOR_EXTS_KEY, categoryCode);

        return send.getId();
    }

    private void dealSubmit(Integer submitFlag,SendDO send){
        if (SubmitFlagEnum.SUBMIT.getStatus().equals(submitFlag)) {
            if(Objects.equals(send.getSendRegion(), SendRegionEnum.OUT.getValue())){  //国外快递进审核
                submitTask(send.getId(), WebFrameworkUtils.getLoginUserId());
            }
            else {  //国内快递跳过审核
                if(StrUtil.isNotEmpty(send.getExpressCode())){  //已填写单号，状态为已寄出
                    send.setSendStatus(EmsSendStatusEnum.ALREADY_SEND.getCode());
                }else {  //未填写单号，状态为未寄出
                    send.setSendStatus(EmsSendStatusEnum.PENDING_SEND.getCode());
                }
                sendMapper.updateById(send);
            }
        }
    }


    private void dealFeeShare(List<FeeShareReqDTO> feeShareList,Long sendId){
        SendDO sendDO = sendMapper.selectById(sendId);
        if(Objects.isNull(sendDO)){
            throw exception(SEND_ID_NOT_EXISTS);
        }

        if(feeShareList == null){
           var feeShare = new FeeShareReqDTO();
            feeShare.setOrderType(null);
            feeShare.setAuditStatus(BpmProcessInstanceResultEnum.APPROVE.getResult())
                    .setSourceStatus(BpmProcessInstanceResultEnum.APPROVE.getResult())
                    .setStatus(BooleanEnum.NO.getValue());
            feeShare.setId(null);
            feeShare.setBusinessType(FeeShareSourceTypeEnum.EMS_SEND.getValue())
                    .setBusinessCode(sendDO.getCode())
                    .setBusinessId(sendId);
            feeShare.setInputUser(adminUserApi.getUserDeptByUserId(sendDO.getInputUserId()));
            feeShareApi.updateFeeShareByDTO(feeShare);
            //回写寄件归集标记状态
            sendDO.setBelongFlag(BooleanEnum.NO.getValue());
        }else{
            feeShareList.forEach(s->{
                s.setAuditStatus(BpmProcessInstanceResultEnum.APPROVE.getResult())
                        .setSourceStatus(BpmProcessInstanceResultEnum.APPROVE.getResult())
                        .setStatus(BooleanEnum.YES.getValue());
                s.setId(null);
                s.setBusinessType(FeeShareSourceTypeEnum.EMS_SEND.getValue())
                        .setBusinessCode(sendDO.getCode())
                        .setBusinessId(sendId);
                s.setInputUser(adminUserApi.getUserDeptByUserId(sendDO.getInputUserId()));
            });
            //回写寄件归集标记状态
            sendDO.setBelongFlag(BooleanEnum.YES.getValue());
            feeShareApi.updateFeeShareByDTOList(feeShareList);
        }
        sendMapper.updateById(sendDO);

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSend(SendSaveInfoReqVO updateReqVO) {
        SendDO sendDO = validateSendExists(updateReqVO.getId());
        SendDO updateObj = SendConvert.INSTANCE.convertSendDO(updateReqVO);
        updateObj.setSendStatus(EmsSendStatusEnum.PENDING_SUBMIT.getCode());
        if(Objects.nonNull(updateObj.getExpressCode())){
            updateObj.setSendTime(LocalDateTime.now());
        }
        //更新实际寄件人
        UserDept actualUserDept = userApi.getUserDeptByUserId(updateReqVO.getActualUserId());
        if(actualUserDept != null ){
            updateObj.setActualUser(actualUserDept);
        }
        sendMapper.updateById(updateObj);

        List<SendProductDO> productDOList = sendProductMapper.selectList(SendProductDO::getSendId, updateReqVO.getId());
        if(CollUtil.isNotEmpty(productDOList)){
            List<Long> idList = productDOList.stream().map(SendProductDO::getId).toList();
            sendProductMapper.deleteBatchIds(idList);
        }

        List<SendProductSaveReqVO> children = updateReqVO.getChildren();
        if(CollUtil.isNotEmpty(children)){
            children.forEach(s->{
                        s.setId(null);
                        s.setSendId(updateObj.getId());
                        s.setPicture(s.getMainPicture());
                    }
            );
            sendProductMapper.insertBatch(BeanUtils.toBean(children, SendProductDO.class));
        }


        //处理费用归集
        dealFeeShare(updateReqVO.getFeeShare(),sendDO.getId());

        //处理提交
        dealSubmit(updateReqVO.getSubmitFlag(),updateObj);

        //操作日志
        {
            List<ChangeRecord> changeRecords = new OperateCompareUtil<SendDO>().compare(sendDO, updateObj);

            //采购明细变更修改
            dealEmsSendProductItem(sendDO.getId(), updateReqVO, changeRecords);
            if (CollUtil.isNotEmpty(changeRecords)) {
                OperateCompareUtil<Object> operateCompareUtil = new OperateCompareUtil<>();
                AtomicReference<String> content = new AtomicReference<>();
                changeRecords.forEach(s -> {
                    content.set(operateCompareUtil.apply(content.get(), s.getFieldName(), s.getOldValue(), s.getValue(), ChangeRecordTypeEnum.GENERAL_CHANGE.getType()));
                });
                // 插入操作日志
                OperateLogUtils.setContent(content.get());
            }
            OperateLogUtils.addExt(OPERATOR_EXTS_KEY, updateReqVO.getCode());
        }

    }

    private void dealEmsSendProductItem(Long sendId, SendSaveInfoReqVO updateReqVO, List<ChangeRecord> changeRecords) {
        List<SendProductDO> baseContractItemList = sendProductService.getListBysendCode(sendId);
        List<SendProductDO> reqContractItemList = BeanUtils.toBean(updateReqVO.getChildren(), SendProductDO.class);

        if (CollUtil.isEmpty(reqContractItemList) && CollUtil.isNotEmpty(baseContractItemList)) {     //判断删除
            changeRecords.add(new ChangeRecord().setFieldName("清空寄件产品"));
        } else if (CollUtil.isNotEmpty(reqContractItemList) && CollUtil.isEmpty(baseContractItemList)) {    //判断新增
            reqContractItemList.forEach(s -> {
                changeRecords.add(new ChangeRecord().setFieldName("新增寄件产品"));
            });
        } else if (CollUtil.isNotEmpty(reqContractItemList) && CollUtil.isNotEmpty(baseContractItemList)) {
            Map<Long, SendProductDO> dtoMap = baseContractItemList.stream().collect(Collectors.toMap(SendProductDO::getId, s -> s));
            List<SendProductDO> updateList = reqContractItemList.stream().filter(s -> Objects.nonNull(s.getId())).toList();
            if (CollUtil.isNotEmpty(updateList)) {
                updateList.forEach(s -> {
                    SendProductDO purchaseContractItemDO = dtoMap.get(s.getId());
                    if (Objects.isNull(purchaseContractItemDO)) {
                        return;
                    }
                    List<ChangeRecord> quoteRecords = new OperateCompareUtil<SendProductDO>().compare(purchaseContractItemDO, s);
                    if (CollUtil.isNotEmpty(quoteRecords)) {
                        quoteRecords.forEach(quoteRecord -> {
                            quoteRecord.setFieldName(String.format("修改供寄件产品信息%s:%s %s -> %s", s.getSkuCode(), quoteRecord.getFieldName(), quoteRecord.getOldValue(), quoteRecord.getValue()));
                        });
                        changeRecords.addAll(quoteRecords);
                    }
                });
            }

            List<SendProductDO> insertList = reqContractItemList.stream().filter(s -> Objects.isNull(s.getId())).toList();
            if (CollUtil.isNotEmpty(insertList)) {
                insertList.forEach(insert -> {
                    changeRecords.add(new ChangeRecord().setFieldName("寄件新增产品信息:" + insert.getSkuCode()));
                });
            }

            Set<Long> list = reqContractItemList.stream().map(SendProductDO::getId).collect(Collectors.toSet());
            List<SendProductDO> deleteList = baseContractItemList.stream().filter(s -> !list.contains(s.getId())).toList();
            if (CollUtil.isNotEmpty(deleteList)) {
                deleteList.forEach(delete -> {
                    changeRecords.add(new ChangeRecord().setFieldName("寄件删除产品信息：" + delete.getSkuCode()));
                });
            }
        }
    }

    @Override
    public void deleteSend(Long id) {
        SendDO sendDO = validateSendExists(id);
        feeShareApi.deleteByCodes(FeeShareSourceTypeEnum.EMS_SEND,List.of(sendDO.getCode()));
        sendMapper.deleteById(id);
    }

    private SendDO validateSendExists(Long id) {
        SendDO sendDO = sendMapper.selectById(id);
        if ( sendDO == null) {
            throw exception(SEND_NOT_EXISTS);
        }
        return sendDO;
    }
    @Override
    public SendInfoRespVO getSend(SendDetailReq req) {
        Long sendId = Objects.nonNull(req.getProcessInstanceId()) ?
                bpmProcessInstanceApi.selectAuditAbleIdByProcessInstanceId(req.getProcessInstanceId())
                : req.getSenedId();
        if (Objects.isNull(sendId)) {
            logger.error("[采购合同]未获取到采购合同id");
            return null;
        }
        SendDO sendDO = sendMapper.selectById(sendId);
        if (sendDO == null) {
            return null;
        }
        SendInfoRespVO sendInfoRespVO = SendConvert.INSTANCE.convertSendRespVO(sendDO);
        sendInfoRespVO.setDealUserName(sendDO.getDealUser().getNickname());
        //产品
        List<SendProductDO> sendProductDOList = sendProductMapper.selectList(SendProductDO::getSendId, sendId);
        if(CollUtil.isNotEmpty(sendProductDOList)){
            List<SendProductRespVO> sendProductVOList = BeanUtils.toBean(sendProductDOList, SendProductRespVO.class);
            sendProductVOList.forEach(s->{
                s.setMainPicture(s.getPicture());
            });
            sendInfoRespVO.setChildren(sendProductVOList);
        }
        //费用归集
        List<FeeShareRespDTO> feeShare = feeShareApi.getFeeShareDTO(FeeShareSourceTypeEnum.EMS_SEND,sendDO.getCode());

        if(feeShare.size() == 1 && feeShare.get(0).getFeeShareDetail() == "暂不归集"){
            sendInfoRespVO.setFeeShare(null);
        }else {
            sendInfoRespVO.setFeeShare(feeShare);
        }

        //处理供应商编号，版本向下兼容
        if(Objects.isNull(sendInfoRespVO.getVenderCode())){
            SimpleVenderRespDTO simpleVenderRespDTO = venderApi.getSimpleVenderRespDTOById(sendInfoRespVO.getVenderId());
            if(Objects.nonNull(simpleVenderRespDTO)){
                sendInfoRespVO.setVenderCode(simpleVenderRespDTO.getCode());
            }
        }

        sendInfoRespVO.setOperateLogRespDTOList(operateLogApi.getOperateLogRespDTOList(OPERATOR_EXTS_KEY, sendInfoRespVO.getCode()));

        String bpmProcessInstanceId = bpmProcessInstanceApi.getBpmProcessInstanceId(sendDO.getId(), PROCESS_DEFINITION_KEY);
        if (StrUtil.isNotEmpty(bpmProcessInstanceId)) {
            sendInfoRespVO.setProcessInstanceId(bpmProcessInstanceId);
        }

        return sendInfoRespVO;
    }

    @Override
    public PageResult<SendInfoRespVO> getSendPage(SendPageReqVO pageReqVO) {
        PageResult<SendDO> sendDoPageResult = sendMapper.selectPage(pageReqVO);
        List<SendDO> doList = sendDoPageResult.getList();
        if(CollUtil.isNotEmpty(doList)){
            List<SendInfoRespVO> sendInfoRespVOList = BeanUtils.toBean(doList, SendInfoRespVO.class);

            if(CollUtil.isNotEmpty(sendInfoRespVOList)){

                List<Long> idList = sendInfoRespVOList.stream().map(SendInfoRespVO::getId).toList();
                List<SendProductDO> productDOList = sendProductMapper.selectList(SendProductDO::getSendId, idList);
                if(CollUtil.isNotEmpty(productDOList)){
                    List<SendProductRespVO> productRespVOList = BeanUtils.toBean(productDOList, SendProductRespVO.class);
                    Map<Long, List<SendProductRespVO>> productRespVoMap = productRespVOList.stream().collect(Collectors.groupingBy(SendProductRespVO::getSendId));
                    if(CollUtil.isNotEmpty(productRespVoMap)){
                        sendInfoRespVOList.forEach(s->{
                            Long id = s.getId();
                            List<SendProductRespVO> respVOList = productRespVoMap.get(id);
                            if(CollUtil.isNotEmpty(respVOList)){
                                respVOList.forEach(p->{
                                    p.setMainPicture(p.getPicture());
                                });
                                s.setChildren(respVOList);
                            }
                        });
                    }
                }

                List<String> codeList = sendInfoRespVOList.stream().map(SendInfoRespVO::getCode).distinct().toList();
                Map<String,List<FeeShareRespDTO>> feeShareMap =  feeShareApi.getFeeShareByCodeList(FeeShareSourceTypeEnum.EMS_SEND.getValue(),codeList);
                List<Long> venderIdList = sendInfoRespVOList.stream().map(SendInfoRespVO::getVenderId).distinct().toList();
                Map<Long, SimpleVenderRespDTO> simpleVenderRespDTOMap = venderApi.getSimpleVenderRespDTOMap(venderIdList);

                sendInfoRespVOList.forEach(s->{
                    if(CollUtil.isNotEmpty(feeShareMap)){
                        List<FeeShareRespDTO> feeShareRespDTO = feeShareMap.get(s.getCode());
                        s.setFeeShare(feeShareRespDTO);
                        if(Objects.nonNull(s.getSendTime())){
                            s.setSendMonth(s.getSendTime().getMonthValue() + "月");
                        }
                        if(CollUtil.isNotEmpty(feeShareRespDTO)){
                            s.setFeeShareDesc(feeShareRespDTO.stream().map(FeeShareRespDTO::getFeeShareDetail).collect(Collectors.joining(", ")));
                        }
                    }
                    //处理编号，版本向下兼容
                    if(CollUtil.isNotEmpty(simpleVenderRespDTOMap)){
                        SimpleVenderRespDTO simpleVenderRespDTO = simpleVenderRespDTOMap.get(s.getVenderId());
                        if(Objects.nonNull(simpleVenderRespDTO)){
                            s.setVenderCode(simpleVenderRespDTO.getCode());
                        }
                    }
                });
            }

            Map<String, Object> summary = new HashMap<>();
            pageReqVO.setPageSize(-1);
            PageResult<SendDO> sendDOPageResult = sendMapper.selectPage(pageReqVO);
            if(Objects.nonNull(sendDOPageResult) && CollUtil.isNotEmpty(sendDOPageResult.getList())){
                Map<String, BigDecimal> costSumMap = sendDOPageResult.getList().stream().map(SendDO::getCost).filter(Objects::nonNull)
                        .filter(cost -> cost.getCurrency() != null && !cost.getCurrency().isEmpty())
                        .collect(Collectors.groupingBy(JsonAmount::getCurrency,Collectors.reducing(BigDecimal.ZERO,JsonAmount::getAmount,BigDecimal::add)));
                if (!costSumMap.isEmpty()) {
                    List<JsonAmount> aggregatedAmountList = costSumMap.entrySet().stream().map(entry ->
                                    new JsonAmount().setCurrency(entry.getKey()).setAmount(entry.getValue())).toList();
                    summary.put("costSum", aggregatedAmountList);
                }
                Map<String, BigDecimal> estCostSumMap = sendDOPageResult.getList().stream().map(SendDO::getEstCost).filter(Objects::nonNull)
                        .filter(estCost -> estCost.getCurrency() != null && !estCost.getCurrency().isEmpty())
                        .collect(Collectors.groupingBy(JsonAmount::getCurrency,Collectors.reducing(BigDecimal.ZERO,JsonAmount::getAmount,BigDecimal::add)));
                if (!estCostSumMap.isEmpty()) {
                    List<JsonAmount> aggregatedAmountList = estCostSumMap.entrySet().stream().map(entry ->
                            new JsonAmount().setCurrency(entry.getKey()).setAmount(entry.getValue())).toList();
                    summary.put("estCostSum", aggregatedAmountList);
                }
            }
            return new PageResult<SendInfoRespVO>().setList(sendInfoRespVOList).setTotal(sendDoPageResult.getTotal()).setSummary(summary);
        }

       return  new PageResult<>(null);

    }

    @Override
    public Boolean submitSend(Long id) {
        SendDO sendDO = sendMapper.selectById(id);
        if(sendDO == null ){
            return  false;
        }
        sendDO.setSendStatus(EmsSendStatusEnum.PENDING_SEND.getCode()).setSubmitTime(LocalDateTime.now());
        sendMapper.updateById(sendDO);
        return true;
    }

    @Override
    public Boolean uploadNumberSend(Long id, String number,Long venderId) {
        SendDO sendDO = sendMapper.selectById(id);
        if(sendDO == null ){
            return  false;
        }
        sendDO.setExpressCode(number);
        sendDO.setSendStatus(EmsSendStatusEnum.ALREADY_SEND.getCode()).setSendTime(LocalDateTime.now());
        Long loginUserId = WebFrameworkUtils.getLoginUserId();
        UserDept userDept = userApi.getUserDeptByUserId(loginUserId);
        sendDO.setDealUser(userDept);
        SimpleVenderRespDTO simpleVenderRespDTOById = venderApi.getSimpleVenderRespDTOById(venderId);
        if(Objects.isNull(simpleVenderRespDTOById)){
            throw exception(VENDER_NOT_EXISTS);
        }
        sendDO.setVenderId(simpleVenderRespDTOById.getId())
                .setVenderCode(simpleVenderRespDTOById.getCode())
                .setVenderShortName(simpleVenderRespDTOById.getNameShort())
                .setVenderName(simpleVenderRespDTOById.getName());
        sendMapper.updateById(sendDO);
        return true;
    }

    @Override
    public Boolean importSend(String batchCode, Integer overFlag){
       List<SendBillDO> doList = sendBillService.getBYBatchCode(batchCode);
       if(overFlag == 1){  //覆盖已有数据的列表
           doList = doList.stream().filter(s->s.getImportType() == 1).toList();  //1-表示之前已经录入了真实费用，可以覆盖录入
       }else { //修改第一次填写费用数据的列表
           doList = doList.stream().filter(s->s.getImportType() == 0).toList(); //0-表示之前没有录入过数据，现在可以正常录入
       }
       if(doList.size() == 0){
           return false;
       }
        List<String> sendCodes = doList.stream().map(SendBillDO::getCode).toList();
        List<SendDO> sendDOS = sendMapper.selectList(SendDO::getCode, sendCodes);
        Map<String, SendBillDO> importMap = doList.stream().collect(Collectors.toMap(SendBillDO::getCode, s -> s, (s1, s2) -> s1));
        sendDOS.forEach(s->{
            SendBillDO sendBillDO = importMap.get(s.getCode());
            s.setCost(new JsonAmount().setAmount(sendBillDO.getCost()).setCurrency(sendBillDO.getCurrency()));
            SendUpdateCostReqVO req = new SendUpdateCostReqVO();
            req.setId(s.getId());
            req.setCost(s.getCost());
            updateCost(req);  //循环更新价格
        });
        return  true;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateFeeShare(FeeShareReqVO updateReqVO)  {
        SendDO sendDO = sendMapper.selectById(updateReqVO.getSendId());
        if(sendDO == null){
            List<SendDO> sends = sendMapper.selectList(SendDO::getCode,updateReqVO.getBusinessCode());
            if(CollUtil.isEmpty(sends)){
                throw exception(SEND_CODE_NOT_EXISTS);
            }
            sendDO = sends.get(0);
        }

        List<FeeShareReqDTO> list = BeanUtils.toBean(updateReqVO.getFeeShare(), FeeShareReqDTO.class);
        SendDO finalSendDO = sendDO;
        list.forEach(s->{
            s.setBusinessType(FeeShareSourceTypeEnum.EMS_SEND.getValue());
            s.setBusinessCode(finalSendDO.getCode());
            s.setAuditStatus(BpmProcessInstanceResultEnum.APPROVE.getResult());
            s.setSourceStatus(BpmProcessInstanceResultEnum.APPROVE.getResult());
            s.setStatus(BooleanEnum.YES.getValue());
            s.setBusinessId(finalSendDO.getId());
        });

        feeShareApi.updateFeeShareByDTOList(list);
        sendDO.setBelongFlag(BooleanEnum.YES.getValue());
        sendMapper.updateById(sendDO);
   }


    @Override
    public void updateSendStatusByCodeList(EmsSendUpdateDTO emsSendUpdateDTO) {
        List<String> codeList = emsSendUpdateDTO.getCodeList();
        if(codeList.isEmpty()){
            return;
        }

        codeList.forEach(s->{
            try {
                updateSendStatusByCode(s,emsSendUpdateDTO.getStatus());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

    }

    @Override
    public void updatePayStatusByCodeList(EmsSendUpdateDTO emsSendUpdateDTO) {
        List<String> codeList = emsSendUpdateDTO.getCodeList().stream().distinct().toList();
        if(codeList.isEmpty()){
            return;
        }
        List<SendDO> doList = sendMapper.selectList(SendDO::getCode, codeList).stream().toList();
        doList.forEach(s->{
           s.setPayStatus(emsSendUpdateDTO.getStatus());
        });
        sendMapper.updateBatch(doList);
    }

    @Override
    public void updateBelongFlagById(Long id, Integer status) {
        validateSendExists(id);
        sendMapper.updateById(new SendDO().setId(id).setBelongFlag(status));
    }

    @Override
    public List<SendInfoRespVO> getListByCodeList(String codeListString) {
        List<String> list = Arrays.stream(codeListString.split(",")).toList();
        List<SendDO> sendDOList = sendMapper.selectList(new LambdaQueryWrapperX<SendDO>().inIfPresent(SendDO::getCode, list)).stream().toList();

        if(CollUtil.isNotEmpty(sendDOList)){
            List<SendInfoRespVO> sendInfoRespVOList = BeanUtils.toBean(sendDOList, SendInfoRespVO.class);

            if(CollUtil.isNotEmpty(sendInfoRespVOList)){
                List<Long> idList = sendInfoRespVOList.stream().map(SendInfoRespVO::getId).toList();
                List<SendProductDO> productDOList = sendProductMapper.selectList(SendProductDO::getSendId, idList);
                if(CollUtil.isNotEmpty(productDOList)){
                    List<SendProductRespVO> productRespVOList = BeanUtils.toBean(productDOList, SendProductRespVO.class);
                    Map<Long, List<SendProductRespVO>> productRespVoMap = productRespVOList.stream().collect(Collectors.groupingBy(SendProductRespVO::getSendId));
                    if(CollUtil.isNotEmpty(productRespVoMap)){
                        sendInfoRespVOList.forEach(s->{
                            Long id = s.getId();
                            List<SendProductRespVO> respVOList = productRespVoMap.get(id);
                            if(CollUtil.isNotEmpty(respVOList)){
                                respVOList.forEach(p->{
                                    p.setMainPicture(p.getPicture());
                                });
                                s.setChildren(respVOList);
                            }
                        });
                    }
                }

                List<String> codeList = sendInfoRespVOList.stream().map(SendInfoRespVO::getCode).distinct().toList();
                Map<String,List<FeeShareRespDTO>> feeShareMap =  feeShareApi.getFeeShareByCodeList(FeeShareSourceTypeEnum.EMS_SEND.getValue(),codeList);
                if(CollUtil.isNotEmpty(feeShareMap)){
                    sendInfoRespVOList.forEach(s->{
                        List<FeeShareRespDTO> feeShareRespDTO = feeShareMap.get(s.getCode());
                        s.setFeeShare(feeShareRespDTO);
                    });
                }
            }
            return sendInfoRespVOList;
        }
        return  null;
    }

    @Override
    public void rejectTask(Long userId, BpmTaskRejectReqDTO reqVO) {
        bpmProcessInstanceApi.rejectTask(userId, BeanUtils.toBean(reqVO, BpmTaskRejectReqDTO.class));
    }

    @Override
    public void submitTask(Long id, Long userId) {
        SendDO sendDO = sendMapper.selectById(id);
        if(Objects.equals(sendDO.getSendRegion(), SendRegionEnum.OUT.getValue())){
            sendDO.setSendStatus(EmsSendStatusEnum.PENDING_APPROVE.getCode()).setAuditStatus(BpmProcessInstanceResultEnum.PROCESS.getResult());
            sendMapper.updateById(sendDO);
            if (Objects.isNull(sendDO.getInputDeptId())){
                throw exception(SEND_DEPT_IS_EMPTY);
            }
            DeptRespDTO dept = deptApi.getDept(sendDO.getInputDeptId());
            if (Objects.isNull(dept)||StrUtil.isEmpty(dept.getCode())){
                throw exception(SEND_DEPT_IS_EMPTY);
            }
            Map<String, Object> variables = new HashMap<>();
            variables.put("inputDeptCode", dept.getCode());
            bpmProcessInstanceApi.createProcessInstance(userId, PROCESS_DEFINITION_KEY, id,variables,Map.of());
        }else {
            sendDO.setSendStatus(EmsSendStatusEnum.PENDING_SEND.getCode());
            sendMapper.updateById(sendDO);
        }


    }

    @Override
    public void approveTask(Long userId, BpmTaskApproveReqDTO reqVO) {
        bpmProcessInstanceApi.approveTask(userId, BeanUtils.toBean(reqVO, BpmTaskApproveReqDTO.class));
    }

    @Override
    public String getProcessDefinitionKey() {
        return PROCESS_DEFINITION_KEY;
    }



    public void updateSendStatusByCode(String code,Integer status) throws Exception {
        if(Objects.isNull(code)){
            throw  new Exception("编号不能为空");
        }
        Optional<SendDO> first = sendMapper.selectList(SendDO::getCode, code).stream().findFirst();
        if(first.isEmpty()){
            throw  new Exception("寄件编号不存在");
        }
        SendDO sendDO = first.get();
        sendDO.setSendStatus(status);
        switch (status){
            case PENDING_SUBMIT: sendDO.setSubmitTime(LocalDateTime.now());break;
            case PENDING_SEND: sendDO.setSendTime(LocalDateTime.now());break;
            case ALREADY_SEND: sendDO.setCostTime(LocalDateTime.now());break;
            case PENGDING_PAY: sendDO.setDoneTime(LocalDateTime.now());break;
            default:break;
        }
        sendMapper.updateById(sendDO);
    }

    @Override
    public Boolean updateCost(SendUpdateCostReqVO sendUpdateCostReqVO) {
        Optional<SendDO> first = sendMapper.selectList(SendDO::getId, sendUpdateCostReqVO.getId()).stream().findFirst();
        if(first.isEmpty()){
            return false;
        }
        SendDO sendDO = first.get();
        sendDO.setCost(sendUpdateCostReqVO.getCost());
        sendDO.setSendStatus(EmsSendStatusEnum.PENGDING_PAY.getCode()).setCostTime(LocalDateTime.now());
        Long loginUserId = WebFrameworkUtils.getLoginUserId();
        UserDept userDept = userApi.getUserDeptByUserId(loginUserId);
        sendDO.setDealUser(userDept);
        //判断金额按比例更换实际金额
        if(sendUpdateCostReqVO.getCost() != sendDO.getEstCost() && Objects.nonNull(sendUpdateCostReqVO.getCost())){
            dealCostReplaceEstCost(sendUpdateCostReqVO.getId(),sendUpdateCostReqVO.getCost());
        }
        sendMapper.updateById(sendDO);

        return true;
    }

    private void dealCostReplaceEstCost(Long id,JsonAmount cost){
        SendDO sendDO = sendMapper.selectById(id);
        if(Objects.isNull(sendDO)){
            throw exception(SEND_ID_NOT_EXISTS);
        }
        if(Objects.isNull(sendDO.getEstCost()) || Objects.isNull(sendDO.getEstCost().getAmount()) || 
           Objects.equals(sendDO.getEstCost().getAmount(), BigDecimal.ZERO)){
            return;
        }
            BigDecimal total =  sendDO.getEstCost().getAmount();
        if(Objects.nonNull(sendDO.getCost()) && Objects.nonNull(sendDO.getCost().getAmount()) && sendDO.getCost().getAmount().compareTo(BigDecimal.ZERO) != 0){
            total =  sendDO.getCost().getAmount();
        }
        BigDecimal newTotal = cost.getAmount();
        if(total.compareTo(newTotal) == 0){
            return;
        }
        List<FeeShareRespDTO> feeShareDTOList = feeShareApi.getFeeShareDTO(FeeShareSourceTypeEnum.EMS_SEND, sendDO.getCode());
        List<FeeShareReqDTO> list = new ArrayList<>();
        BigDecimal sumTotal = BigDecimal.ZERO;
        for (int i = 0; i < feeShareDTOList.size(); i++) {
            FeeShareRespDTO feeShareRespDTO = feeShareDTOList.get(i);
            if(CollUtil.isNotEmpty(feeShareRespDTO.getCrmChildren())){
                for(int j=0;j<feeShareRespDTO.getCrmChildren().size();j++){
                    var ss = feeShareRespDTO.getCrmChildren().get(j);
                    BigDecimal amount = ss.getAmount().getAmount().multiply(newTotal).divide(total, 2, RoundingMode.HALF_UP);
                    sumTotal = sumTotal.add(amount);
                    if(i == feeShareDTOList.size() - 1 &&  j ==  feeShareRespDTO.getCrmChildren().size() - 1 && sumTotal.compareTo(newTotal) != 0){
                        amount = amount.add(newTotal.subtract(sumTotal));
                    }
                    ss.setAmount(ss.getAmount().setAmount(amount));
                }
            }

            if(CollUtil.isNotEmpty(feeShareRespDTO.getVenderChildren())){
                for(int j=0;j<feeShareRespDTO.getVenderChildren().size();j++){
                    var ss = feeShareRespDTO.getVenderChildren().get(j);
                    BigDecimal amount = ss.getAmount().getAmount().multiply(newTotal).divide(total, 2, RoundingMode.HALF_UP);
                    sumTotal = sumTotal.add(amount);
                    if(i == feeShareDTOList.size() - 1 &&  j ==  feeShareRespDTO.getVenderChildren().size() - 1 && sumTotal.compareTo(newTotal) != 0){
                        amount = amount.add(newTotal.subtract(sumTotal));
                    }
                    ss.setAmount(ss.getAmount().setAmount(amount));
                }
            }

            if(CollUtil.isNotEmpty(feeShareRespDTO.getPurchaseChildren())){
                for(int j=0;j<feeShareRespDTO.getPurchaseChildren().size();j++){
                    var ss = feeShareRespDTO.getPurchaseChildren().get(j);
                    BigDecimal amount = ss.getAmount().getAmount().multiply(newTotal).divide(total, 2, RoundingMode.HALF_UP);
                    sumTotal = sumTotal.add(amount);
                    if(i == feeShareDTOList.size() - 1 &&  j ==  feeShareRespDTO.getPurchaseChildren().size() - 1 && sumTotal.compareTo(newTotal) != 0){
                        amount = amount.add(newTotal.subtract(sumTotal));
                    }
                    ss.setAmount(ss.getAmount().setAmount(amount));
                }
            }

            if(CollUtil.isNotEmpty(feeShareRespDTO.getSmsChildren())){
                for(int j=0;j<feeShareRespDTO.getSmsChildren().size();j++){
                    var ss = feeShareRespDTO.getSmsChildren().get(j);
                    BigDecimal amount = ss.getAmount().getAmount().multiply(newTotal).divide(total, 2, RoundingMode.HALF_UP);
                    sumTotal = sumTotal.add(amount);
                    if(i == feeShareDTOList.size() - 1 &&  j ==  feeShareRespDTO.getSmsChildren().size() - 1 && sumTotal.compareTo(newTotal) != 0){
                        amount = amount.add(newTotal.subtract(sumTotal));
                    }
                    ss.setAmount(ss.getAmount().setAmount(amount));
                }
            }

            if(CollUtil.isNotEmpty(feeShareRespDTO.getUserChildren())){
                for(int j=0;j<feeShareRespDTO.getUserChildren().size();j++){
                    var ss = feeShareRespDTO.getUserChildren().get(j);
                    BigDecimal amount = ss.getAmount().getAmount().multiply(newTotal).divide(total, 2, RoundingMode.HALF_UP);
                    sumTotal = sumTotal.add(amount);
                    if(i == feeShareDTOList.size() - 1 &&  j ==  feeShareRespDTO.getUserChildren().size() - 1 && sumTotal.compareTo(newTotal) != 0){
                        amount =amount.add(newTotal.subtract(sumTotal));
                    }
                    ss.setAmount(ss.getAmount().setAmount(amount));
                }
            }

            if(CollUtil.isNotEmpty(feeShareRespDTO.getDeptChildren())){
                for(int j=0;j<feeShareRespDTO.getDeptChildren().size();j++){
                    var ss = feeShareRespDTO.getDeptChildren().get(j);
                    BigDecimal amount = ss.getAmount().getAmount().multiply(newTotal).divide(total, 2, RoundingMode.HALF_UP);
                    sumTotal = sumTotal.add(amount);
                    if(i == feeShareDTOList.size() - 1 &&  j ==  feeShareRespDTO.getDeptChildren().size() - 1 && sumTotal.compareTo(newTotal) != 0){
                        amount =amount.add(newTotal.subtract(sumTotal));
                    }
                    ss.setAmount(ss.getAmount().setAmount(amount));
                }
            }

            //所有子项都没有进行主表金额转换
            if(CollUtil.isEmpty(feeShareRespDTO.getSmsChildren()) && CollUtil.isEmpty(feeShareRespDTO.getPurchaseChildren())
                    && CollUtil.isEmpty(feeShareRespDTO.getCrmChildren()) && CollUtil.isEmpty(feeShareRespDTO.getVenderChildren())
                    && CollUtil.isEmpty(feeShareRespDTO.getDeptChildren()) && CollUtil.isEmpty(feeShareRespDTO.getUserChildren())){
                if(Objects.nonNull(feeShareRespDTO.getAmount()) && Objects.nonNull(feeShareRespDTO.getAmount().getAmount())){
                    BigDecimal amount = feeShareRespDTO.getAmount().getAmount().multiply(newTotal).divide(total, 2, RoundingMode.HALF_UP);
                    sumTotal = sumTotal.add(amount);
                    if(i == feeShareDTOList.size() - 1  && sumTotal.compareTo(newTotal) != 0){
                        amount =amount.add(newTotal.subtract(sumTotal));
                    }
                    feeShareRespDTO.setAmount(feeShareRespDTO.getAmount().setAmount(amount));
                }
            }

            FeeShareReqDTO dto = BeanUtils.toBean(feeShareRespDTO,FeeShareReqDTO.class);
            //BeanUtils对子项为list转换不兼容，手动赋值
            dto.setSmsChildren(feeShareRespDTO.getSmsChildren())
                    .setPurchaseChildren(feeShareRespDTO.getPurchaseChildren())
                    .setCrmChildren(feeShareRespDTO.getCrmChildren())
                    .setUserChildren(feeShareRespDTO.getUserChildren())
                    .setVenderChildren(feeShareRespDTO.getVenderChildren());
            dto.setInputUser(adminUserApi.getUserDeptByUserId(sendDO.getInputUserId()));
            list.add(dto);
        }
        feeShareApi.updateFeeShareByDTOList(list);

    }


    @Override
    public void updateAuditStatus(Long auditableId, Integer auditStatus) {
        Integer sendStatus = 0;
        SendDO sendDO = sendMapper.selectById(auditableId);
        if(Objects.equals(auditStatus,BpmProcessInstanceResultEnum.PROCESS.getResult())){
            sendStatus = EmsSendStatusEnum.PENDING_APPROVE.getCode();
        }else if(Objects.equals(auditStatus,BpmProcessInstanceResultEnum.UNSUBMITTED.getResult())||Objects.equals(auditStatus,BpmProcessInstanceResultEnum.CANCEL.getResult())){
            sendStatus = EmsSendStatusEnum.PENDING_SUBMIT.getCode();
        }else if(Objects.equals(auditStatus,BpmProcessInstanceResultEnum.APPROVE.getResult())){
            if(StrUtil.isNotEmpty(sendDO.getExpressCode())){   //有单号，状态为已寄出
                sendStatus = EmsSendStatusEnum.ALREADY_SEND.getCode();
            }else {//没有单号，状态为未寄出
                sendStatus = EmsSendStatusEnum.PENDING_SEND.getCode();
            }
        }else if(Objects.equals(auditStatus,BpmProcessInstanceResultEnum.REJECT.getResult())){
            sendStatus = EmsSendStatusEnum.REJECT.getCode();
        }
        sendDO.setAuditStatus(auditStatus).setSendStatus(sendStatus);
        sendMapper.updateById(sendDO);
    }

    @Override
    @DataPermission(enable = false)
    public JsonAmount getFeeShareAmountByCode(String businessCode) {
        List<SendDO> sendDOS = sendMapper.selectList(SendDO::getCode, businessCode);
        if(Objects.isNull(sendDOS) || sendDOS.size() < 1){
            throw exception(SEND_NOT_EXISTS);
        }
        SendDO sendDO = sendDOS.get(0);
        if(Objects.nonNull(sendDO.getCost()) && Objects.nonNull(sendDO.getCost().getAmount()) && sendDO.getCost().getAmount().compareTo(BigDecimal.ZERO) > 0){
            return sendDO.getCost();
        }else {
            return sendDO.getEstCost();
        }
    }

    @Override
    public void updateBelongFlagByCode(String businessKey, Integer status) {

        List<SendDO> sendDOS = sendMapper.selectList(SendDO::getCode, businessKey);
        if(CollUtil.isEmpty(sendDOS)){
            throw exception(SEND_NOT_EXISTS);
        }
        if((long) sendDOS.size() > 1){
            throw exception(SEND_CODE_MORE);
        }
        SendDO sendDO = sendDOS.get(0);
        sendDO.setBelongFlag(status);
        sendMapper.updateById(sendDO);
    }

    @Override
    public List<EmsSendDetailDTO> getDetailListByCodes(List<String> relationCode) {
        List<EmsSendDetailDTO> res  = new ArrayList<>();
        List<SendDO> sendDOS = sendMapper.selectList(SendDO::getCode, relationCode);
        if(CollUtil.isEmpty(sendDOS)){
            throw exception(SEND_NOT_EXISTS);
        }
        // No need to collect userIds as we're getting user info directly in the loop below
        Map<String, SendDO> sendDOMap = sendDOS.stream().collect(Collectors.toMap(SendDO::getCode, s -> s, (s1, s2) -> s2));
        Map<String, List<FeeShareRespDTO>> feeShareMap = feeShareApi.getFeeShareByCodeList(FeeShareSourceTypeEnum.EMS_SEND.getValue(), relationCode);
        relationCode.forEach(s->{
            EmsSendDetailDTO dto = new EmsSendDetailDTO();
            dto.setExpressCode(s);
            SendDO sendDO = sendDOMap.get(s);
            if(Objects.nonNull(sendDO)){
                dto.setExpressCompany(sendDO.getVenderName());
                dto.setExpressCode(sendDO.getExpressCode());
                dto.setAmount(sendDO.getCost().getAmount().toString());
                dto.setUserName(sendDO.getInputUserName());
                dto.setReceiveName(sendDO.getReceiveName());
                if(Objects.nonNull(sendDO.getActualUser())){
                    dto.setActualUserName(sendDO.getActualUser().getNickname());
                }
                UserDept user = adminUserApi.getUserDeptByUserId(sendDO.getInputUserId());
                if(Objects.nonNull(user)){
                    dto.setDeptName(user.getDeptName());
                }
                dto.setMonth(sendDO.getCreateTime().getMonthValue() + "月");

                if(CollUtil.isNotEmpty(feeShareMap)){
                    List<FeeShareRespDTO> feeShareRespDTOS = feeShareMap.get(s);
                    if(Objects.nonNull(feeShareRespDTOS)){
                        List<String> allCodes = feeShareRespDTOS.stream().map(FeeShareRespDTO::getSmsChildren).filter(Objects::nonNull)
                                .flatMap(List::stream).map(SmsContractAllDTO::getCode).filter(Objects::nonNull).toList();
                        dto.setContractCodes(String.join(",",allCodes));
                        List<String> details = feeShareRespDTOS.stream().map(FeeShareRespDTO::getFeeShareDetail).toList();
                        dto.setRemark(String.join(",",details));
                    }
                }

            }
            res.add(dto);
        });
        return  res;
    }

    @Override
    public void closeSend(Long id) {
        SendDO sendDO = validateSendExists(id);
        sendDO.setSendStatus(EmsSendStatusEnum.CLOSE.getCode());
        sendMapper.updateById(sendDO);
        // 删除费用归集
        feeShareApi.deleteByCodes(FeeShareSourceTypeEnum.EMS_SEND,List.of(sendDO.getCode()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateVender(Long id, Long venderId) {
        SendDO sendDO = validateSendExists(id);
        
        // Validate status - only 已完成 and 已作废 cannot be modified
        if (EmsSendStatusEnum.COMPLETED.getCode().equals(sendDO.getSendStatus()) || 
            EmsSendStatusEnum.CLOSE.getCode().equals(sendDO.getSendStatus())) {
            throw exception(new cn.iocoder.yudao.framework.common.exception.ErrorCode(500, "已完成或已作废状态不允许修改快递公司"));
        }
        
        SimpleVenderRespDTO vender = venderApi.getSimpleVenderRespDTOById(venderId);
        if (Objects.isNull(vender)) {
            throw exception(VENDER_NOT_EXISTS);
        }
        
        // Record old values for operation log
        String oldVenderName = sendDO.getVenderName();
        
        // Update express company
        SendDO updateObj = new SendDO();
        updateObj.setId(id);
        updateObj.setVenderId(vender.getId());
        updateObj.setVenderCode(vender.getCode());
        updateObj.setVenderName(vender.getName());
        updateObj.setVenderShortName(vender.getNameShort());
        
        sendMapper.updateById(updateObj);
        
        // Add operation log
        String content = "修改快递公司: " + oldVenderName + " → " + vender.getName();
        OperateLogUtils.addOperateLog("寄件管理", sendDO.getCode(), content);
    }
}