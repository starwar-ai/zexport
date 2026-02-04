package com.syj.eplus.module.dms.service.forwarderfee;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.bpm.api.task.BpmProcessInstanceApi;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.PaymentAppDTO;
import com.syj.eplus.framework.common.enums.FeeShareSourceTypeEnum;
import com.syj.eplus.framework.common.enums.PayStatusEnum;
import com.syj.eplus.module.dms.controller.admin.forwarderfee.vo.ForwarderFeePageReqVO;
import com.syj.eplus.module.dms.controller.admin.forwarderfee.vo.ForwarderFeeRespVO;
import com.syj.eplus.module.dms.controller.admin.forwarderfee.vo.ForwarderFeeSaveReqVO;
import com.syj.eplus.module.dms.convert.forwarderfee.ForwarderFeeConvert;
import com.syj.eplus.module.dms.dal.dataobject.forwarderfee.ForwarderFeeDO;
import com.syj.eplus.module.dms.dal.mysql.forwarderfee.ForwarderFeeMapper;
import com.syj.eplus.module.dms.enums.api.dto.DmsForwarderFeeDTO;
import com.syj.eplus.module.infra.api.CodeGeneratorApi;
import com.syj.eplus.module.oa.api.PaymentAppApi;
import com.syj.eplus.module.oa.api.feeshare.FeeShareApi;
import com.syj.eplus.module.oa.api.feeshare.dto.FeeShareRespDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.dms.enums.ErrorCodeConstants.FORWARDER_FEE_CODE_MORE;
import static com.syj.eplus.module.dms.enums.ErrorCodeConstants.FORWARDER_FEE_NOT_EXISTS;

/**
 * 船代费用 Service 实现类
 *
 * @author zhangcm
 */
@Service
@Validated
public class ForwarderFeeServiceImpl implements ForwarderFeeService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    @Resource
    private ForwarderFeeMapper forwarderFeeMapper;
    @Resource
    @Lazy
    private FeeShareApi feeShareApi;
    @Resource
    private BpmProcessInstanceApi bpmProcessInstanceApi;
    @Resource
    private CodeGeneratorApi codeGeneratorApi;
    @Resource
    @Lazy
    private PaymentAppApi paymentAppApi;
    private static final String SN_TYPE = "forwarder_fee";
    private static final String PROCESS_DEFINITION_KEY = "oa_fee_share";
    private static final String CODE_PREFIX = "FWF";

    @Override
    public Long createForwarderFee(ForwarderFeeSaveReqVO createReqVO) {
        ForwarderFeeDO forwarderFee = ForwarderFeeConvert.INSTANCE.convertForwarderFeeDO(createReqVO);
        // 插入
        forwarderFeeMapper.insert(forwarderFee);
        // 返回
        return forwarderFee.getId();
    }

    @Override
    public void updateForwarderFee(ForwarderFeeSaveReqVO updateReqVO) {
        // 校验存在
        validateForwarderFeeExists(updateReqVO.getId());
        // 更新
        ForwarderFeeDO updateObj = ForwarderFeeConvert.INSTANCE.convertForwarderFeeDO(updateReqVO);
        forwarderFeeMapper.updateById(updateObj);
    }

    @Override
    public void deleteForwarderFee(Long id) {
        // 校验存在
        validateForwarderFeeExists(id);
        // 删除
        forwarderFeeMapper.deleteById(id);
    }

    private void validateForwarderFeeExists(Long id) {
        if (forwarderFeeMapper.selectById(id) == null) {
            throw exception(FORWARDER_FEE_NOT_EXISTS);
        }
    }
    @Override
    public ForwarderFeeRespVO getForwarderFee(Long id) {
        ForwarderFeeDO forwarderFeeDO = forwarderFeeMapper.selectById(id);
        if (forwarderFeeDO == null) {
            return null;
        }
        ForwarderFeeRespVO forwarderFeeRespVO = ForwarderFeeConvert.INSTANCE.convertForwarderFeeRespVO(forwarderFeeDO);
        List<FeeShareRespDTO> feeShare = feeShareApi.getFeeShareDTO(FeeShareSourceTypeEnum.SHIPMENT_FORWARDER_FEE,forwarderFeeDO.getCode());
        forwarderFeeRespVO.setFeeShare(feeShare);

        String bpmProcessInstanceId;
        bpmProcessInstanceId = bpmProcessInstanceApi.getBpmProcessInstanceId( id, PROCESS_DEFINITION_KEY);
        if (StrUtil.isNotEmpty(bpmProcessInstanceId)) {
            forwarderFeeRespVO.setProcessInstanceId(bpmProcessInstanceId);
        }

        return  forwarderFeeRespVO;
    }

    @Override
    public PageResult<ForwarderFeeDO> getForwarderFeePage(ForwarderFeePageReqVO pageReqVO) {
        return forwarderFeeMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ForwarderFeeDO> getListByShipmentId(Long shipmentId) {
        List<ForwarderFeeDO> forwarderFeeDOS = forwarderFeeMapper.selectList(ForwarderFeeDO::getShipmentId, shipmentId);
        if (CollUtil.isEmpty(forwarderFeeDOS)){
            return List.of();
        }
        Set<String> paymentAppCodeSet = forwarderFeeDOS.stream().map(ForwarderFeeDO::getPaymentAppCode).filter(StrUtil::isNotEmpty).collect(Collectors.toSet());
        if (CollUtil.isEmpty(paymentAppCodeSet)){
            return forwarderFeeDOS;
        }
        Map<String, PaymentAppDTO> simplePaymentMsgByCodes = paymentAppApi.getSimplePaymentMsgByCodes(paymentAppCodeSet);
        if (CollUtil.isEmpty(simplePaymentMsgByCodes)){
            return forwarderFeeDOS;
        }
        forwarderFeeDOS.forEach(s->{
            PaymentAppDTO paymentAppDTO = simplePaymentMsgByCodes.get(s.getPaymentAppCode());
            if (paymentAppDTO != null){
                s.setPaymentDate(paymentAppDTO.getPaymentDate());
                s.setPaymentStatus(paymentAppDTO.getPaymentStatus());
            }
        });
        return forwarderFeeDOS;
    }

    @Override
    public void deleteByShipmentId(Long shipmentId) {
        forwarderFeeMapper.delete(ForwarderFeeDO::getShipmentId,shipmentId);
    }

    @Override
    public void insertBatch(List<ForwarderFeeDO> feeList) {
        feeList.forEach(s->{
            String codeGenerator = codeGeneratorApi.getCodeGenerator(SN_TYPE, CODE_PREFIX);
            s.setCode(codeGenerator);
        });
        forwarderFeeMapper.insertBatch(feeList);
    }

    @Override
    public PageResult<ForwarderFeeDO> getForwarderFeeApplyPage(ForwarderFeePageReqVO pageReqVO) {
        //根据筛选条件查询所有有效数据
        LambdaQueryWrapperX<ForwarderFeeDO> queryWrapperX = new LambdaQueryWrapperX<ForwarderFeeDO>()
                .eqIfPresent(ForwarderFeeDO::getCompanyId, pageReqVO.getCompanyId())
                .likeIfPresent(ForwarderFeeDO::getCompanyName, pageReqVO.getCompanyName())
                .eqIfPresent(ForwarderFeeDO::getVenderId, pageReqVO.getVenderId())
                .eqIfPresent(ForwarderFeeDO::getVenderCode, pageReqVO.getVenderCode())
                .likeIfPresent(ForwarderFeeDO::getVenderName, pageReqVO.getVenderName());
        if(Objects.nonNull(pageReqVO.getApplyerId())){
            queryWrapperX.apply("JSON_EXTRACT(applyer,'$.userId') = {0}", pageReqVO.getApplyerId());
        }
        List<ForwarderFeeDO> doList = forwarderFeeMapper.selectList(queryWrapperX);
        if (CollUtil.isEmpty(doList)){
            return PageResult.empty();
        }
        //将聚合后的单条code使用逗号分隔存在codeList
        doList.forEach(s->{
            s.setCodeList(s.getCode());
        });

        doList.sort(Comparator.comparingInt(ForwarderFeeDO::getPayStatus).thenComparingLong(ForwarderFeeDO::getId).reversed());
        List<ForwarderFeeDO> resultList = new ArrayList<>();
        List<ForwarderFeeDO> notPaymentList = doList.stream().filter(s -> StrUtil.isEmpty(s.getPaymentAppCode())).collect(
                Collectors.groupingBy(item ->
                                Arrays.asList(item.getPayStatus(),item.getCompanyId(), item.getVenderId(), item.getShipmentId(), item.getInvoiceCode(),  item.getAmount().getCurrency()),
                        Collectors.reducing((item1, item2) -> {
                            item1.setId(item1.getId());
                            item1.getAmount().setAmount(item1.getAmount().getAmount().add(item2.getAmount().getAmount()));
                            item1.setCodeList(item1.getCodeList() + "," + item2.getCodeList());
                            return item1;
                        })
                )
        ).values().stream().map(Optional::get).toList();
        if (CollUtil.isNotEmpty(notPaymentList)){
            resultList.addAll(notPaymentList);
        }
        //需求：同一个出运明细的单证费用，如果是同一个船代同一币种，付款状态为未支付的，可以合并
        List<ForwarderFeeDO> list = doList.stream().filter(s -> StrUtil.isNotEmpty(s.getPaymentAppCode())).collect(Collectors.groupingBy(item ->
                        Arrays.asList(item.getPaymentAppCode(),item.getPayStatus(),item.getCompanyId(), item.getVenderId(), item.getShipmentId(), item.getInvoiceCode(),  item.getAmount().getCurrency()),
                Collectors.reducing((item1, item2) -> {
                    item1.setId(item1.getId());
                    item1.getAmount().setAmount(item1.getAmount().getAmount().add(item2.getAmount().getAmount()));
                    item1.setCodeList(item1.getCodeList() + "," + item2.getCodeList());
                    return item1;
                })
        )).values().stream().map(Optional::get).toList();
        if (CollUtil.isNotEmpty(list)){
            resultList.addAll(list);
        }

        List<ForwarderFeeDO> pageList = resultList.stream().sorted(Comparator.comparing(ForwarderFeeDO::getPayStatus).reversed().thenComparing(ForwarderFeeDO::getId).reversed())
                .toList();

        //手动分页
        Integer size = pageReqVO.getPageSize();
        Integer no = pageReqVO.getPageNo();
        int startIndex = size * no - size;
        int endIndex = size * no;
        if(startIndex > pageList.size()){
            return PageResult.empty();
        }
        if(endIndex > pageList.size()){
            endIndex = pageList.size();
        }
        List<ForwarderFeeDO> result = pageList.subList(startIndex,endIndex);

        return new PageResult<ForwarderFeeDO>().setList(result).setTotal((long)pageList.size());
    }

    @Override
    public void updateStatusByCodeList(DmsForwarderFeeDTO dmsForwarderFeeDTO) {
        List<String> codeList = dmsForwarderFeeDTO.getCodeList();
        if(codeList.isEmpty()){
            return;
        }

        codeList.forEach(s->{
            try {
                updateSendStatusByCode(s,dmsForwarderFeeDTO.getStatus(),dmsForwarderFeeDTO.getPaymentAppCode(),dmsForwarderFeeDTO.getPaymentAppId());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public List<ForwarderFeeDO> getListByCodeList(List<String> list) {
        return forwarderFeeMapper.selectList(ForwarderFeeDO::getCode,list);
    }

    @Override
    public String getNewCode() {
        return   codeGeneratorApi.getCodeGenerator(SN_TYPE, CODE_PREFIX);

    }

    @Override
    @DataPermission(enable = false)
    public JsonAmount getFeeShareAmountByCode(String businessCode) {
        List<ForwarderFeeDO> forwarderFeeDOS = forwarderFeeMapper.selectList(ForwarderFeeDO::getCode, businessCode);
        if(Objects.isNull(forwarderFeeDOS) || forwarderFeeDOS.size() < 1){
            throw exception(FORWARDER_FEE_NOT_EXISTS);
        }
        ForwarderFeeDO forwarderFeeDO = forwarderFeeDOS.get(0);
       return  forwarderFeeDO.getAmount();
    }

    @Override
    public void updateBelongFlagByCode(String businessKey, Integer value) {
        List<ForwarderFeeDO>  forwarderFeeDOS = forwarderFeeMapper.selectList(ForwarderFeeDO::getCode, businessKey);
        if(CollUtil.isEmpty(forwarderFeeDOS)){
            throw exception(FORWARDER_FEE_NOT_EXISTS);
        }
        if((long) forwarderFeeDOS.size() > 1){
            throw exception(FORWARDER_FEE_CODE_MORE);
        }
        ForwarderFeeDO forwarderFeeDO = forwarderFeeDOS.get(0);
        //暂无归集字段，因为船代费用后台自动归集，如果需求需要修改归集状态，增加归集字段，使用该方法
//        forwarderFeeDO.setBelongFlag(value);
        forwarderFeeMapper.updateById(forwarderFeeDO);
    }

    @Override
    public List<ForwarderFeeDO> getListByPaymentCode(String code) {

        return forwarderFeeMapper.selectList(ForwarderFeeDO::getPaymentAppCode, code);

    }

    @Override
    public Map<String, String> getShipmentCodeByCodes(List<String> relationCodes) {
        List<ForwarderFeeDO> forwarderFeeDOS = forwarderFeeMapper.selectList(ForwarderFeeDO::getCode, relationCodes);
        if(forwarderFeeDOS.isEmpty()){
            return null;
        }
        return forwarderFeeDOS.stream().collect(Collectors.toMap(ForwarderFeeDO::getCode,ForwarderFeeDO::getShipmentCode));
    }

    @Override
    public Map<String, List<JsonAmount>> getAmountByShipmentCodes(List<String> relationCodes) {
        if (CollUtil.isEmpty(relationCodes)) {
            return Collections.emptyMap();
        }

        List<ForwarderFeeDO> forwarderFeeDOS = forwarderFeeMapper.selectList(ForwarderFeeDO::getShipmentCode, relationCodes);
        if (CollUtil.isEmpty(forwarderFeeDOS)) {
            return Collections.emptyMap();
        }

        // 根据shipmentCode分组，然后按币种求和
        return forwarderFeeDOS.stream()
                .collect(Collectors.groupingBy(ForwarderFeeDO::getShipmentCode))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> {
                    // 按币种分组并求和，返回 Map<币种, 金额>
                    Map<String, BigDecimal> currencyAmountMap = entry.getValue().stream()
                            .map(ForwarderFeeDO::getAmount)
                            .filter(Objects::nonNull)
                            .collect(Collectors.groupingBy(
                                    JsonAmount::getCurrency,
                                    Collectors.reducing(
                                            BigDecimal.ZERO,
                                            amount -> Optional.ofNullable(amount.getAmount()).orElse(BigDecimal.ZERO),
                                            BigDecimal::add)));
                    
                    // 将 Map<币种, 金额> 转换为 List<JsonAmount>
                    if (currencyAmountMap.isEmpty()) {
                        return Collections.emptyList();
                    }
                    
                    return currencyAmountMap.entrySet().stream()
                            .map(e -> new JsonAmount(e.getValue(), e.getKey()))
                            .collect(Collectors.toList());
                }));
    }


    public void updateSendStatusByCode(String code,Integer status,String paymentAppCode,Long paymentAppId) throws Exception {
        if(Objects.isNull(code)){
            throw  new Exception("编号不能为空");
        }
        Optional<ForwarderFeeDO> first = forwarderFeeMapper.selectList(ForwarderFeeDO::getCode, code).stream().findFirst();
        if(first.isEmpty()){
            throw  new Exception("船代费用不存在");
        }
        ForwarderFeeDO forwarderFeeDO = first.get();
        forwarderFeeDO.setPayStatus(status);
        //如果修改为未申请状态，删除单据编号
        if(Objects.equals(PayStatusEnum.NOT_APPLY.getValue(), status)){
            forwarderFeeDO.setPaymentAppCode("");
            forwarderFeeDO.setPaymentAppId((long)0);
        }else {
            forwarderFeeDO.setPaymentAppCode(paymentAppCode);
            forwarderFeeDO.setPaymentAppId(paymentAppId);
        }

        forwarderFeeMapper.updateById(forwarderFeeDO);
    }

}