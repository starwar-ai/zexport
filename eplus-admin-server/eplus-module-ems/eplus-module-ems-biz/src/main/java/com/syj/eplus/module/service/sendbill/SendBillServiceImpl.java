package com.syj.eplus.module.service.sendbill;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.infra.api.rate.RateApi;
import com.syj.eplus.framework.common.enums.EmsSendStatusEnum;
import com.syj.eplus.module.controller.admin.sendbill.vo.SendBillImportVO;
import com.syj.eplus.module.controller.admin.sendbill.vo.SendBillPageReqVO;
import com.syj.eplus.module.controller.admin.sendbill.vo.SendBillRespVO;
import com.syj.eplus.module.controller.admin.sendbill.vo.SendBillSaveReqVO;
import com.syj.eplus.module.convert.sendbill.SendBillConvert;
import com.syj.eplus.module.dal.dataobject.send.SendDO;
import com.syj.eplus.module.dal.dataobject.sendbill.SendBillDO;
import com.syj.eplus.module.dal.mysql.send.SendMapper;
import com.syj.eplus.module.dal.mysql.sendbill.SendBillMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.ems.enums.ErrorCodeConstants.SEND_BILL_NOT_EXISTS;


/**
 * 寄件导入单据 Service 实现类
 *
 * @author zhangcm
 */
@Service
@Validated
public class SendBillServiceImpl implements SendBillService {

protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private SendBillMapper sendBillMapper;

    @Resource
    private RateApi rateApi;

    @Resource
    private SendMapper sendMapper;

    @Override
    public Long createSendBill(SendBillSaveReqVO createReqVO) {
SendBillDO sendBill = SendBillConvert.INSTANCE.convertSendBillDO(createReqVO);
        // 插入
        sendBillMapper.insert(sendBill);
        // 返回
        return sendBill.getId();
    }

    @Override
    public void updateSendBill(SendBillSaveReqVO updateReqVO) {
        // 校验存在
        validateSendBillExists(updateReqVO.getId());
        // 更新
SendBillDO updateObj = SendBillConvert.INSTANCE.convertSendBillDO(updateReqVO);
        sendBillMapper.updateById(updateObj);
    }

    @Override
    public void deleteSendBill(Long id) {
        // 校验存在
        validateSendBillExists(id);
        // 删除
        sendBillMapper.deleteById(id);
    }

    private void validateSendBillExists(Long id) {
        if (sendBillMapper.selectById(id) == null) {
            throw exception(SEND_BILL_NOT_EXISTS);
        }
    }
    @Override
public SendBillRespVO getSendBill(Long id) {
    SendBillDO sendBillDO = sendBillMapper.selectById(id);
if (sendBillDO == null) {
return null;
}
return SendBillConvert.INSTANCE.convertSendBillRespVO(sendBillDO);
    }

    @Override
    public PageResult<SendBillDO> getSendBillPage(SendBillPageReqVO pageReqVO) {
        return sendBillMapper.selectPage(pageReqVO);
    }


    @Override
    public List<SendBillRespVO> importExcelNotInsert(List<SendBillImportVO> list ) {
        if(CollUtil.isEmpty(list)){
            return null;
        }
        List<SendBillRespVO> result = BeanUtils.toBean(list,SendBillRespVO.class);
        String batchCode = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        result.forEach(s->{
            s.setBatchCode(batchCode);
        });
        return result;
    }



    @Override
    public void insertBillBatch(List<SendBillDO> billDoList) {
        sendBillMapper.insertBatch(billDoList);
    }

    @Override
    public SendBillRespVO importExcel(List<SendBillImportVO> list) {
        SendBillRespVO sendBillImportVO = new SendBillRespVO();
        if(CollUtil.isEmpty(list)){
            return sendBillImportVO;
        }
        list.forEach(s->s.setCode(s.getSendCode()));
        List<SendBillImportVO> overList = new ArrayList<>();
        List<SendBillImportVO> successList = new ArrayList<>();
        List<SendBillImportVO> finishList = new ArrayList<>();
        List<SendBillImportVO> errorList = new ArrayList<>();
        String batchCode = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        sendBillImportVO.setBatchCode(batchCode);

        List<String> sendCodeList = list.stream().map(SendBillImportVO::getSendCode).distinct().toList();
        List<SendDO> sendDOList = sendMapper.selectList(SendDO::getCode, sendCodeList);
        List<SendBillDO> sendAddArr = new ArrayList<>();
        if(CollUtil.isNotEmpty(sendDOList)){
            //查出的数据状态为已完成，不考虑是否付款，都不能修改
            List<SendDO> doneDoList = sendDOList.stream().filter(s -> Objects.equals(s.getSendStatus(), EmsSendStatusEnum.COMPLETED.getCode())).toList();
            List<SendBillImportVO> doneVoList = BeanUtils.toBean(doneDoList, SendBillImportVO.class);
           if(CollUtil.isNotEmpty(doneVoList)){
               doneVoList.forEach(s->{
                   s.setImportType(2);
                   s.setErrorDesc("单据已转付款申请:" + s.getCode());
               });

               Map<String, SendDO> doMap = sendDOList.stream().collect(Collectors.toMap(SendDO::getCode, s -> s, (s1, s2) -> s1));
               doneVoList.forEach(s->{
                   SendDO sendDO = doMap.get(s.getCode());
                   if(Objects.nonNull(sendDO)){
                       s.setCostStr(sendDO.getCost().getAmount() + " " + sendDO.getCost().getCurrency());
                       s.setCode(sendDO.getCode());
                       s.setSendCode(sendDO.getCode());
                       s.setEmsCode(sendDO.getExpressCode());
                       s.setMonth(sendDO.getSendTime().getMonthValue() + " 月");
                   }
               });
               finishList = doneVoList;
               sendAddArr.addAll(BeanUtils.toBean(doneVoList, SendBillDO.class));
           }

            //不存在的首先单据判断
            List<String> sendDoCodeList = sendDOList.stream().map(SendDO::getCode).distinct().toList();
            List<SendBillImportVO> missList = list.stream().filter(s -> !sendDoCodeList.contains(s.getSendCode())).distinct().toList();
            if(CollUtil.isNotEmpty(missList)){
                missList.forEach(s->{
                    s.setImportType(3);
                    s.setErrorDesc("快递单号在系统中不存在：" + s.getSendCode());
                });
                errorList.addAll(missList);
                sendAddArr.addAll(BeanUtils.toBean(missList,SendBillDO.class));
            }

            Set<String> currencies = rateApi.getAllCurrencyByDate();
            List<SendDO> doCompareList = sendDOList.stream().filter(s -> !Objects.equals(s.getSendStatus(), EmsSendStatusEnum.COMPLETED.getCode())).toList();
            if(CollUtil.isNotEmpty(doCompareList)){
                List<SendBillImportVO> compareList = BeanUtils.toBean(doCompareList, SendBillImportVO.class);
                compareList.forEach(s->s.setSendCode(s.getCode()));
                List<SendBillImportVO> finalErrorList = errorList;
                compareList.forEach(s->{
                    Optional<SendBillImportVO> first = list.stream().filter(v -> Objects.equals(v.getSendCode(), s.getSendCode())).findFirst();
                    if(first.isPresent()){
                        SendBillImportVO importVo = first.get();
                        //校验费用字段信息
                        String cost =  importVo.getCostStr() == null ? "" : importVo.getCostStr().replace(" ","");
                        String currency ="RMB";  //2025.9.11 寄件费用只有RMB-G
                        BigDecimal amount = BigDecimal.ZERO;
                        try{
                            Optional<String> firstMatchedKey = currencies.stream()
                                    .filter(key -> cost.toLowerCase().contains(key.toLowerCase()))
                                    .findFirst();
                            if(firstMatchedKey.isPresent()){
                                currency = cost.substring(cost.length()-3);
                                String amountString = cost.substring(0,cost.length()-3);
                                amount = new BigDecimal(amountString);
                            }else {
                                amount = new BigDecimal(cost);
                            }

                        }catch (Exception ignore){
                            importVo.setImportType(4);
                            importVo.setErrorDesc("费用金额异常,请规范格式,例如：100 RMB");
                            finalErrorList.add(importVo);
                            sendAddArr.add(BeanUtils.toBean(importVo, SendBillDO.class));
                            return;
                        }
                        Optional<SendDO> sendDoFirst = doCompareList.stream().filter(d -> Objects.equals(d.getCode(), s.getCode())).findFirst();
                        if(sendDoFirst.isPresent()){
                            SendDO sendDO = sendDoFirst.get();
                            //如果原先有数据
                            if(Objects.nonNull(sendDO.getCost()) && sendDO.getCost().getAmount().compareTo(BigDecimal.ZERO) > 0){
                                importVo.setImportType(1);
                                s.setCostStr(importVo.getCostStr());
                                s.setCurrency(amount + " " + currency);
                                overList.add(importVo);
                            }else {
                                importVo.setImportType(0);
                                s.setCostStr(importVo.getCostStr());
                                successList.add(importVo);
                            }

                            SendBillDO sendBillDO = BeanUtils.toBean(importVo, SendBillDO.class);
                            sendBillDO.setCost(amount);
                            sendBillDO.setCurrency(currency);
                            sendAddArr.add(sendBillDO);
                        }
                    }
                });
            }
        }else {
            //编号在数据库中查不到，全部为缺失单据,实际上使用导出文件不存在
            errorList = list;
        }

        sendAddArr.forEach(s->s.setBatchCode(batchCode));
        sendBillMapper.insertBatch(sendAddArr);

        sendBillImportVO.setErrorList(errorList);
        sendBillImportVO.setOverList(overList);
        sendBillImportVO.setSuccessList(successList);
        sendBillImportVO.setFinishList(finishList);
        return sendBillImportVO;

    }

    @Override
    public List<SendBillDO> getBYBatchCode(String batchCode) {
        if(Objects.isNull(batchCode)){
            return null;
        }
        return  sendBillMapper.selectList(SendBillDO::getBatchCode,batchCode).stream().toList();
    }

}