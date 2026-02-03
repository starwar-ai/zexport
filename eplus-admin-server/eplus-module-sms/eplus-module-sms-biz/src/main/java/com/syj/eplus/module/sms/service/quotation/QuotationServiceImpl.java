package com.syj.eplus.module.sms.service.quotation;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.io.FileUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.operatelog.core.util.OperateLogUtils;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.bpm.api.task.BpmProcessInstanceApi;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import cn.iocoder.yudao.module.bpm.enums.task.BpmProcessInstanceResultEnum;
import cn.iocoder.yudao.module.infra.api.config.ConfigApi;
import cn.iocoder.yudao.module.infra.api.file.FileApi;
import cn.iocoder.yudao.module.infra.api.rate.RateApi;
import com.syj.eplus.module.infra.api.company.CompanyApi;
import com.syj.eplus.module.infra.api.company.dto.SimpleCompanyDTO;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import com.alibaba.excel.metadata.data.ImageData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.deepoove.poi.config.Configure;
import com.deepoove.poi.data.Pictures;
import com.deepoove.poi.plugin.table.LoopRowTableRenderPolicy;
import com.syj.eplus.framework.common.dict.CalculationDict;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.JsonSpecificationEntity;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.framework.common.enums.BooleanEnum;
import com.syj.eplus.framework.common.enums.QuotationEnum;
import com.syj.eplus.framework.common.enums.SubmitFlagEnum;
import com.syj.eplus.framework.common.util.CalcSpecificationUtil;
import com.syj.eplus.framework.common.util.CurrencyUtil;
import com.syj.eplus.framework.common.util.NumUtil;
import com.syj.eplus.framework.common.util.NumberFormatUtil;
import com.syj.eplus.module.crm.api.cust.CustApi;
import com.syj.eplus.module.crm.api.cust.dto.CustAllDTO;
import com.syj.eplus.module.infra.api.CodeGeneratorApi;
import com.syj.eplus.module.pms.api.packageType.PackageTypeApi;
import com.syj.eplus.module.pms.api.packageType.dto.PackageTypeDTO;
import com.syj.eplus.module.pms.api.sku.SkuApi;
import com.syj.eplus.module.pms.api.sku.dto.SkuDTO;
import com.syj.eplus.module.sms.controller.admin.quotation.vo.*;
import com.syj.eplus.module.sms.convert.quotation.QuotationConvert;
import com.syj.eplus.module.sms.dal.dataobject.otherfee.OtherFeeDO;
import com.syj.eplus.module.sms.dal.dataobject.quotation.QuotationDO;
import com.syj.eplus.module.sms.dal.dataobject.quotationitem.QuotationItemDO;
import com.syj.eplus.module.sms.dal.mysql.otherfee.OtherFeeMapper;
import com.syj.eplus.module.sms.dal.mysql.quotation.QuotationMapper;
import com.syj.eplus.module.sms.dal.mysql.quotationitem.QuotationItemMapper;
import com.syj.eplus.module.sms.util.CalcSaleContactUtil;
import com.syj.eplus.module.system.api.report.ReportApi;
import com.syj.eplus.module.system.api.report.dto.ReportDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.framework.common.dict.SaleContractDict.*;
import static com.syj.eplus.module.dms.enums.ErrorCodeConstants.DECLARATION_NOT_EXISTS;
import static com.syj.eplus.module.scm.enums.ErrorCodeConstants.*;
import static com.syj.eplus.module.sms.enums.ErrorCodeConstants.*;

/**
 * 报价单主 Service 实现类
 *
 * @author ePlus
 */
@Service
@Validated
public class QuotationServiceImpl implements QuotationService {

protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private QuotationMapper quotationMapper;

    @Resource
    private QuotationItemMapper quotationItemMapper;

    @Resource
    private OtherFeeMapper otherFeeMapper;

    @Resource
    private AdminUserApi adminUserApi;
    @Resource
    private SkuApi skuApi;
    @Resource
    private PackageTypeApi packageTypeApi;

    @Resource
    private ReportApi reportApi;

    @Resource
    private FileApi fileApi;

    @Resource
    private CustApi custApi;
    @Resource
    private CodeGeneratorApi generatorApi;

    @Resource
    private ConfigApi configApi;
    @Resource
    private CompanyApi companyApi;

    @Resource
    private RateApi rateApi;

    @Resource
    private BpmProcessInstanceApi bpmProcessInstanceApi;
    private static final String PROCESS_DEFINITION_KEY = "sms_quotation";
    private static final String SN_TYPE = "sms_quotation";
    private static final String CODE_PREFIX = "SQ";
    private static final String OPERATE_SIGN = "sms_quotation";


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createQuotation(QuotationSaveReqVO createReqVO) {
        QuotationDO quotation = QuotationConvert.INSTANCE.convertQuotationDO(createReqVO);
        // 插入
        quotation.setStatus(QuotationEnum.PENDING_SUBMIT.getCode());
        String code = generatorApi.getCodeGenerator(SN_TYPE, CODE_PREFIX);
        quotation.setCode(code);
        Long loginUserId = WebFrameworkUtils.getLoginUserId();
        UserDept userDept = adminUserApi.getUserDeptByUserId(loginUserId);
        quotation.setManager(userDept);
        quotationMapper.insert(quotation);
        // 插入明细
        List<QuotationItemDO> quotationItemDOList = createReqVO.getChildren();
        if (CollUtil.isNotEmpty(quotationItemDOList)) {
            quotationItemDOList.forEach(s -> {
                s.setSmsQuotationId(quotation.getId());
                s.setId(null);
            });
            // 校验明细尺柜数量
            validateCabiinetNum(quotationItemDOList);
            quotationItemMapper.insertBatch(quotationItemDOList);
        }

        // 插入其他费用
        List<OtherFeeDO> otherFeeDOList = createReqVO.getOtherFeeList();
        if (CollUtil.isNotEmpty(otherFeeDOList)) {
            otherFeeDOList.forEach(s ->{
                        s.setSmsQuotationId(quotation.getId());
                        s.setId(null);
                    });
            otherFeeMapper.insertBatch(otherFeeDOList);
        }

        if (SubmitFlagEnum.SUBMIT.getStatus().equals(createReqVO.getSubmitFlag())) {
              submitTask(quotation.getId(), WebFrameworkUtils.getLoginUserId());
        }
        // 返回
        return quotation.getId();
    }

    /**
     * 校验明细拉柜数量
     * @param quotationItemDOList 拉柜数量
     */
    private void validateCabiinetNum(List<QuotationItemDO> quotationItemDOList) {
        quotationItemDOList.forEach(s->{
            // 外箱体积
            BigDecimal outerboxVolume = CalcSpecificationUtil.calcSpecificationTotalVolume(s.getSpecificationList());
            // 数量
            Integer boxCount = s.getBoxCount();
            if (Objects.isNull(boxCount) || boxCount <= 0) {
                throw exception(BOX_COUNT_EMPTY);
            }
            if (Objects.isNull(outerboxVolume)){
                throw exception(OUTERBOX_VOLUME_EMPTY);
            }
            // 计算结果缓存
            Map<String, BigDecimal> containerMap = CalcSaleContactUtil.calcCabinetNum(NumUtil.mul(outerboxVolume, NumUtil.parseToBigDecimal(boxCount)),configApi.getConfigMap());
            if (CollUtil.isEmpty(containerMap)){
                throw exception(CONTAINER_QUANTITY_RESULT_EMPTY);
            }
            // 散货体积
            BigDecimal bulkHandlingVolume = containerMap.get(BULK_HANDLING);
            if (Objects.isNull(s.getBulkHandlingVolume()) || !NumUtil.bigDecimalsEqual(bulkHandlingVolume.setScale(CalculationDict.DECIMAL_CHECK,RoundingMode.HALF_UP), s.getBulkHandlingVolume().setScale(CalculationDict.DECIMAL_CHECK,RoundingMode.HALF_UP))) {
                throw exception(CONTRACT_BULK_HANDLING_VOLUME, bulkHandlingVolume);
            }
            // 20尺柜
            BigDecimal twentyFootCabinet = containerMap.get(TWENTY_FOOT_CABINET);
            if (Objects.isNull(s.getTwentyFootCabinetNum()) || !NumUtil.bigDecimalsEqual(twentyFootCabinet.setScale(CalculationDict.DECIMAL_CHECK,RoundingMode.HALF_UP), NumUtil.parseToBigDecimal(s.getTwentyFootCabinetNum()).setScale(CalculationDict.DECIMAL_CHECK,RoundingMode.HALF_UP))) {
                throw exception(CONTRACT_TWENTY_FOOT_CABINET_NUM, twentyFootCabinet);
            }
            // 40尺柜
            BigDecimal fortyFootCabinet = containerMap.get(FORTY_FOOT_CABINET);
            if (Objects.isNull(s.getFortyFootCabinetNum()) || !NumUtil.bigDecimalsEqual(fortyFootCabinet.setScale(CalculationDict.DECIMAL_CHECK,RoundingMode.HALF_UP), NumUtil.parseToBigDecimal(s.getFortyFootCabinetNum()).setScale(CalculationDict.DECIMAL_CHECK,RoundingMode.HALF_UP))) {
                throw exception(CONTRACT_FORTY_FOOT_CABINET_NUM, fortyFootCabinet);
            }
            // 40尺高柜
            BigDecimal fortyFootContainer = containerMap.get(FORTY_FOOT_CONTAINER);
            if (Objects.isNull(s.getFortyFootContainerNum()) || !NumUtil.bigDecimalsEqual(fortyFootContainer.setScale(CalculationDict.DECIMAL_CHECK,RoundingMode.HALF_UP), NumUtil.parseToBigDecimal(s.getFortyFootContainerNum()).setScale(CalculationDict.DECIMAL_CHECK,RoundingMode.HALF_UP))) {
                throw exception(CONTRACT_FORTY_FOOT_CONTAINER_NUM, fortyFootContainer);
            }
        });
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateQuotation(QuotationSaveReqVO updateReqVO) {
        // 校验存在
        QuotationDO quotationDO =  validateQuotationExists(updateReqVO.getId());
        // 更新
        QuotationDO updateObj = QuotationConvert.INSTANCE.convertQuotationDO(updateReqVO);

        // 更新明细
        List<QuotationItemDO> quotationItemDOList = updateReqVO.getChildren();
        quotationItemMapper.delete(QuotationItemDO::getSmsQuotationId, updateReqVO.getId());
        if (CollUtil.isNotEmpty(quotationItemDOList)) {
            quotationItemDOList.forEach(s -> {
                s.setSmsQuotationId(updateReqVO.getId());
                s.setId(null);
            });
            // 校验明细尺柜数量
            validateCabiinetNum(quotationItemDOList);
            quotationItemMapper.insertBatch(quotationItemDOList);
        }

        // 更新其他费用
        List<OtherFeeDO> otherFeeDOList = updateReqVO.getOtherFeeList();
        otherFeeMapper.delete(OtherFeeDO::getSmsQuotationId, quotationDO.getId());
        if (CollUtil.isNotEmpty(otherFeeDOList)) {
            otherFeeDOList.forEach(s -> {
                s.setSmsQuotationId(quotationDO.getId());
                s.setId(null);
            });
            otherFeeMapper.insertBatch(otherFeeDOList);
        }
        quotationMapper.updateById(updateObj);

        if (SubmitFlagEnum.SUBMIT.getStatus().equals(updateReqVO.getSubmitFlag())) {
            submitTask(quotationDO.getId(), WebFrameworkUtils.getLoginUserId());
        }
    }

    @Override
    public void deleteQuotation(Long id) {
        // 校验存在
        validateQuotationExists(id);
        // 删除
        quotationMapper.deleteById(id);
        // 删除明细
        quotationItemMapper.delete(QuotationItemDO::getSmsQuotationId, id);
        // 删除其他费用
        otherFeeMapper.delete(OtherFeeDO::getSmsQuotationId, id);
    }

    private QuotationDO validateQuotationExists(Long id) {
        QuotationDO quotationDO = quotationMapper.selectById(id);
        if (Objects.isNull(quotationDO)) {
            throw exception(QUOTATION_NOT_EXISTS);
        }
        return quotationDO;
    }
    @Override
    public void approveTask(Long userId, BpmTaskApproveReqDTO reqVO) {
        bpmProcessInstanceApi.approveTask(userId, BeanUtils.toBean(reqVO, BpmTaskApproveReqDTO.class));
    }

    @Override
    public void rejectTask(Long userId, BpmTaskRejectReqDTO reqVO) {
         bpmProcessInstanceApi.rejectTask(userId, BeanUtils.toBean(reqVO, BpmTaskRejectReqDTO.class));
    }

    @Override
    public void submitTask(Long quotationId, Long userId) {
        String processInstanceId = bpmProcessInstanceApi.createProcessInstance(userId, PROCESS_DEFINITION_KEY, quotationId);
        updateAuditStatus(quotationId, BpmProcessInstanceResultEnum.PROCESS.getResult(), processInstanceId);
    }

    @Override
    public void updateAuditStatus(Long auditableId, Integer auditStatus, String processInstanceId) {
        Integer status = 0;
        if (BpmProcessInstanceResultEnum.APPROVE.getResult().equals(auditStatus)) {
            status = QuotationEnum.APPROVED.getCode();
        } else if (BpmProcessInstanceResultEnum.REJECT.getResult().equals(auditStatus)) {
            status = QuotationEnum.REJECTED.getCode();
        } else if (BpmProcessInstanceResultEnum.UNSUBMITTED.getResult().equals(auditStatus)) {
            status = QuotationEnum.PENDING_SUBMIT.getCode();
        } else if (BpmProcessInstanceResultEnum.PROCESS.getResult().equals(auditStatus)) {
            status = QuotationEnum.PENDING_APPROVAL.getCode();
        } else if (BpmProcessInstanceResultEnum.CANCEL.getResult().equals(auditStatus)) {
            status = QuotationEnum.PENDING_SUBMIT.getCode();
        }
        QuotationDO quotationDO = QuotationDO.builder().id(auditableId).auditStatus(auditStatus).status(status).build();
        if (StrUtil.isNotEmpty(processInstanceId)) {
            quotationDO.setProcessInstanceId(processInstanceId);
        }
        quotationMapper.updateById(quotationDO);
    }

    @Override
    public String getProcessDefinitionKey() {
    return PROCESS_DEFINITION_KEY;
    }

    @Override
    public QuotationRespVO getQuotation( QuotationDetailReq quotationDetailReq) {
        QuotationDO quotationDO;
        if (Objects.nonNull(quotationDetailReq.getQuotationId())) {
            quotationDO = quotationMapper.selectById(quotationDetailReq.getQuotationId());
        } else if (Objects.nonNull(quotationDetailReq.getProcessInstanceId())) {
            quotationDO = quotationMapper.selectOne(QuotationDO::getProcessInstanceId, quotationDetailReq.getProcessInstanceId());
        } else {
            logger.warn("[报价单详情]获取参数为空req-{}", quotationDetailReq);
            return null;
        }
        if (quotationDO == null) {
            return null;
        }
        QuotationRespVO quotationRespVO = QuotationConvert.INSTANCE.convertQuotationRespVO(quotationDO);
        //获取明细
        List<QuotationItemDO> quotationItemDOList = quotationItemMapper.selectList(QuotationItemDO::getSmsQuotationId, quotationDO.getId());
        if(Objects.nonNull(quotationItemDOList)){
            List<PackageTypeDTO> packageTypeList = packageTypeApi.getList();
            List<String> skuCodeList = quotationItemDOList.stream().map(QuotationItemDO::getSkuCode).distinct().toList();
            Map<String, SkuDTO> skuMap = skuApi.getSkuDTOMapByCodeList(skuCodeList);
            quotationItemDOList.forEach(s->{
                SkuDTO skuDTO = skuMap.get(s.getSkuCode());
                s.setSkuId(skuDTO.getId());
                if(CollUtil.isNotEmpty(packageTypeList) && CollUtil.isNotEmpty(s.getPackageType())){
                    List<Long> distinctPackgeType = s.getPackageType().stream().distinct().toList();
                    List<PackageTypeDTO> tempPackageTypeList = packageTypeList.stream().filter(item->distinctPackgeType.contains(item.getId())).toList();
                    if(CollUtil.isNotEmpty(tempPackageTypeList)){
                        List<String> packageTypeNameList = tempPackageTypeList.stream().map(PackageTypeDTO::getName).distinct().toList();
                        s.setPackageTypeName(String.join(",",packageTypeNameList));
                    }
                }
//                if(CollUtil.isNotEmpty(skuMap)){
//                    SkuDTO skuDTO = skuMap.get(s.getSkuCode());
//                    if(Objects.nonNull(skuDTO)){
//                        s.setCustProFlag(skuDTO.getCustProFlag()).setOwnBrandFlag(skuDTO.getOwnBrandFlag());
//                    }
//                }
            });
        }

        quotationRespVO.setChildren(quotationItemDOList);
        // 获取其他费用
        quotationRespVO.setOtherFeeList(otherFeeMapper.selectList(OtherFeeDO::getSmsQuotationId, quotationDO.getId()));
        return quotationRespVO;
    }

    @Override
    public PageResult<QuotationRespVO> getQuotationPage(QuotationPageReqVO pageReqVO) {
        PageResult<QuotationDO> quotationDOPageResult = quotationMapper.selectPage(pageReqVO);
        List<QuotationDO> quotationDOList = quotationDOPageResult.getList();
        if (CollUtil.isEmpty(quotationDOList)) {
            return null;
        }
        Map<Long, UserDept> userDeptCache = adminUserApi.getUserDeptCache(null);
        List<Long> idList = quotationDOList.stream().map(QuotationDO::getId).distinct().toList();
        Map<Long, List<QuotationItemDO>> quotationItemDOMap = quotationItemMapper.getQuotationItemDOMapByQuotationIdList(idList);
        List<QuotationRespVO> quotationRespVOList = QuotationConvert.INSTANCE.convertQuotationRespVO(quotationDOList, quotationItemDOMap,userDeptCache);
        return new PageResult<QuotationRespVO>().setTotal(quotationDOPageResult.getTotal()).setList(quotationRespVOList);
    }

    @Override
    public void finish(Long id) {
        QuotationDO quotationDO = new QuotationDO();
        quotationDO.setId(id);
        quotationDO.setStatus(QuotationEnum.CASE_CLOSED.getCode());
        quotationMapper.updateById(quotationDO);
    }

    @Override
    public void exportExcel(Long id, Long reportId,String reportCode,String unit, HttpServletResponse response){
        if (id == null) {
            throw exception(DECLARATION_NOT_EXISTS);
        }
        if (reportCode == null) {
            throw exception(REPORTCODE_NULL);
        }
        QuotationDO quotationDO = quotationMapper.selectById(id);
        if (quotationDO == null) {
            throw exception(DECLARATION_NOT_EXISTS);
        }
        ReportDTO reportDTO = null;
        if(reportId!=null){
            reportDTO = reportApi.getReportById(reportId);
        }else{
            if ( quotationDO.getCompanyId() != null ) {
                reportDTO = reportApi.getCompanyReport(reportCode, quotationDO.getCompanyId());
            }
            if(reportDTO == null){
                reportDTO = reportApi.getReport(reportCode);
            }
        }
        if(reportDTO == null ){
            throw exception(REPORT_NULL,"reportCode-"+reportCode);
        }
        // 已在审核中的数据不允许修改
        if (!BpmProcessInstanceResultEnum.APPROVE.getResult().equals(reportDTO.getAuditStatus())) {
            throw exception(REPORT_NOT_APPROVE);
        }

        List<QuotationItemDO> quotationItemDOList = quotationItemMapper.selectList(new LambdaQueryWrapperX<QuotationItemDO>().eq(QuotationItemDO::getSmsQuotationId, id));
        //包装方式
        List<PackageTypeDTO> packageTypeList = packageTypeApi.getList();
        if(CollUtil.isNotEmpty(packageTypeList)){
            quotationItemDOList.forEach(s->{
                if(CollUtil.isNotEmpty(s.getPackageType())){
                    List<Long> distinctPackgeType = s.getPackageType().stream().distinct().toList();
                    List<PackageTypeDTO> tempPackageTypeList = packageTypeList.stream().filter(item->distinctPackgeType.contains(item.getId())).toList();
                    if(CollUtil.isNotEmpty(tempPackageTypeList)){
                        List<String> packageTypeNameList = tempPackageTypeList.stream().map(PackageTypeDTO::getName).distinct().toList();
                        s.setPackageTypeName(String.join(",",packageTypeNameList));
                    }
                }
            });
        }
        List<QuotationItemExportVO> quotationItemExportVOList  = QuotationConvert.INSTANCE.convertQuotationItemExportVO(quotationItemDOList);
        quotationItemExportVOList.forEach(item->{
            //计量单位是英制 根据计量单位比例计算 外箱长，宽，高
            if(unit.equals("eng")){
                if(item.getOuterboxLength()!=null){
                    item.setOuterboxLength(NumUtil.div(item.getOuterboxLength(), "2.54").setScale(CalculationDict.TWO,RoundingMode.HALF_UP).toPlainString());
                }
                if(item.getOuterboxHeight()!=null){
                    item.setOuterboxHeight(NumUtil.div(item.getOuterboxHeight(),"2.54").setScale(CalculationDict.TWO,RoundingMode.HALF_UP).toPlainString());
                }
                if(item.getOuterboxWidth()!=null){
                    item.setOuterboxWidth(NumUtil.div(item.getOuterboxWidth(),"2.54").setScale(CalculationDict.TWO,RoundingMode.HALF_UP).toPlainString());
                }
            }
            if(item.getQuotation()!=null && item.getQuotation().getAmount()!=null){
                // 将报价金额转化为USD后再格式化
                Map<String, BigDecimal> dailyRateMap = rateApi.getDailyRateMap();
                JsonAmount result = CurrencyUtil.changeCurrency(item.getQuotation(), "USD", dailyRateMap);
                item.setQuotationAmount(NumberFormatUtil.formatAmount(result.getAmount()).toPlainString());
            }
            if(item.getQuoteDate()!=null){
                LocalDateTime now = LocalDateTime.now();
                item.setQuoteDays(Duration.between(item.getQuoteDate(),now).toDays());
            }
            if(item.getMainPicture()!=null){
                String inputPath = item.getMainPicture().getFileUrl();
                try {
                    byte[] content = fileApi.getFileContent(inputPath.substring(inputPath.lastIndexOf("get/") + 4));
                   // item.setCptp(content);
                    File inputFile = FileUtils.createTempFile(content);
                    BufferedImage image = ImageIO.read(inputFile);
                    Double width = Double.valueOf(image.getWidth());
                    Double height = Double.valueOf(image.getHeight());
                    //自定义方法，用于处理图片在单元格内大小并达到等比例调整的效果
                    WriteCellData<Void> voidWriteCellData = imageCells(content,width,height,2.0,2.0,0,0);
                    item.setCptp(voidWriteCellData);
                    inputFile.delete();
                } catch (Exception e) {
                    logger.info("报价单导出图片获取失败"+e.getMessage());
                }
            }
        });
        HashMap<String, Object> params = new HashMap();
        params.put("companyName", quotationDO.getCompanyName());
        params.put("custPocName", quotationDO.getCustPocName());
        String inputPath = reportDTO.getAnnex().get(0).getFileUrl();
        byte[] content;
        String path = "";
        try {
            path = inputPath.substring(inputPath.lastIndexOf("get/") + 4);
            content = fileApi.getFileContent(path);
        } catch (Exception e) {
            throw exception(TEMPLETE_DOWNLOAD_FAIL,"path-"+path);
        }
        ByteArrayInputStream templateFileInputStream = new ByteArrayInputStream(content);
        try {
            ExcelUtils.writeByTemplate(response,templateFileInputStream, params,"报价单" + quotationDO.getCode() + ".xlsx",quotationItemExportVOList,null,1500);
        } catch (IOException e) {
            throw exception(EXCEL_EXPORT_FAIL);
        }
    }

    @Override
    public PageResult<QuotationItemRespVO> getQuotationItemPage(QuotationItemPageReqVO pageReqVO) {
        Long custId = pageReqVO.getCustId();
        String custCode = pageReqVO.getCustCode();
        List<QuotationDO> quotationDOS;
        if (StrUtil.isEmpty(custCode)) {
            quotationDOS = quotationMapper.selectList(QuotationDO::getCustCode, custCode);
        }else {
            quotationDOS = quotationMapper.selectList(QuotationDO::getCustId, custId);
        }
        if (CollUtil.isEmpty(quotationDOS)){
            throw exception(QUOTATION_NOT_EXISTS);
        }
        List<Long> parentIdList = quotationDOS.stream().map(QuotationDO::getId).distinct().toList();
        pageReqVO.setSmsQuotationIdList(parentIdList);
        PageResult<QuotationItemDO> itemDOPageResult = quotationItemMapper.selectPage(pageReqVO);
        List<QuotationItemDO> itemDOList = itemDOPageResult.getList();
        if (CollUtil.isEmpty(itemDOList)){
            return PageResult.empty();
        }
        List<Long> parentList = itemDOList.stream().map(QuotationItemDO::getSmsQuotationId).distinct().toList();
        if (CollUtil.isEmpty(parentList)){
            throw exception(QUOTATION_NOT_EXISTS);
        }
        Map<Long, QuotationDO> quotationDOMap = quotationDOS.stream().collect(Collectors.toMap(QuotationDO::getId, s -> s));
        Map<String, String> custNameCache = custApi.getCustNameCache(null);
        List<QuotationItemRespVO> quotationItemRespVOS = QuotationConvert.INSTANCE.convertQuotationItemRespVOList(itemDOList, quotationDOMap, custNameCache);
        return new PageResult<QuotationItemRespVO>().setTotal(itemDOPageResult.getTotal()).setList(quotationItemRespVOS);
    }

    @Override
    public String print(Long id, String reportCode, Long reportId, Long companyId, Integer totalPurchaseFlag, String unit) {
        if (id == null) {
            throw exception(SALE_CONTRACT_ID_NULL);
        }
        if (reportCode == null) {
            throw exception(REPORTCODE_NULL);
        }

        QuotationDO quotationDO = quotationMapper.selectById(id);
        if (quotationDO == null) {
            throw exception(SALECONTRACT_NULL);
        }

        List<QuotationItemDO> quotationItemList = quotationItemMapper.selectList(QuotationItemDO::getSmsQuotationId, id);
        if (CollUtil.isEmpty(quotationItemList)) {
            throw exception(SALECONTRACTITEM_NULL);
        }

        ReportDTO reportDTO;
        if (reportId != null) {
            reportDTO = reportApi.getReportById(reportId);
        } else if (companyId != null) {
            reportDTO = reportApi.getCompanyReport(reportCode, companyId);
        } else {
            reportDTO = reportApi.getReport(reportCode);
        }
        if (reportDTO == null || CollectionUtils.isEmpty(reportDTO.getAnnex())) {
            throw exception(REPORT_NULL);
        }
        // 已在审核中的数据不允许修改
        if (!BpmProcessInstanceResultEnum.APPROVE.getResult().equals(reportDTO.getAuditStatus())) {
            throw exception(REPORT_NOT_APPROVE);
        }
        String projectPath = System.getProperty("user.dir");
        HashMap<String, Object> params = getWordParams(quotationDO, quotationItemList,totalPurchaseFlag, unit);
        String result = reportApi.print(reportDTO.getAnnex().get(0).getFileUrl(), projectPath + "/output.docx", params,quotationDO.getCode());
        if (StrUtil.isNotEmpty(result)) {
            // 更新销售合同打印状态
            QuotationDO updateDO = new QuotationDO();
            updateDO.setId(id);
            updateDO.setPrintFlag(BooleanEnum.YES.getValue());
            quotationMapper.updateById(updateDO);
        }
        OperateLogUtils.addOperateLog(OPERATE_SIGN, quotationDO.getCode(), "打印");
        return result;
    }

    //参数依次为图片字节，图片宽度(像素)，图片高度，行高(厘米，多个单元格合并后的值)，列宽(厘米，多个单元格合并后的值),行偏移量，列偏移量
    public  WriteCellData<Void> imageCells(byte[] bytes,Double imageWidth,Double imageHight,Double rowLength,Double columLength,Integer lastRowIndex,Integer lastColumnIndex) throws IOException {
        //等比例缩小图片，直到图片能放在单元格下，每次缩小20%
        Integer top = 0;
        Integer left = 0;
        //厘米转换成像素，按实际需求转换
        rowLength = rowLength*28;
        columLength = columLength*28;
        while (true){
            if(imageHight <= rowLength && imageWidth <= columLength){
                //计算边框值
                top = Math.toIntExact(Math.round((rowLength - imageHight)/2));
                left = Math.toIntExact(Math.round((columLength - imageWidth)/2));
                break;
            }else {
                imageHight = imageHight*0.8;
                imageWidth = imageWidth*0.8;
            }
        }
        WriteCellData<Void> writeCellData = new WriteCellData<>();
        // 这里可以设置为 EMPTY 则代表不需要其他数据了
        // 可以放入多个图片，目前只放一张
        List<ImageData> imageDataList = new ArrayList<>();
        writeCellData.setImageDataList(imageDataList);
        ImageData imageData = new ImageData();
        imageDataList.add(imageData);
        // 设置图片
        imageData.setImage(bytes);
        // 上右下左需要留空，通过这种方式调整图片大小，单位为像素
        imageData.setTop(top);
        imageData.setRight(left);
        imageData.setBottom(top);
        imageData.setLeft(left);

        //以下四个属性分别为设置单元格偏移量，因为图片可能占据多个单元格（合并单元格）
        // 这里我以左上角单元格为起始，所以FirstRowIndex和FirstColumnIndex默认为0
        // 向右增加一格则设置LastColumnIndex为1，
        // 向下增加一格设置LastRowIndex属性为1，
        imageData.setRelativeFirstRowIndex(0);
        imageData.setRelativeFirstColumnIndex(0);
        imageData.setRelativeLastRowIndex(lastRowIndex);
        imageData.setRelativeLastColumnIndex(lastColumnIndex);
        return writeCellData;
    }
    public HashMap<String, Object> getWordParams(QuotationDO quotationDO, List<QuotationItemDO> quotationItemDOList,Integer totalPurchaseFlag, String unit) {
        HashMap<String, Object> params = new HashMap<>();
        Long companyId = quotationDO.getCompanyId();
        SimpleCompanyDTO companyDTO = companyApi.getCompanyDTO(companyId);
        if (Objects.isNull(companyDTO)) {
            throw exception(COMPANY_NOT_EXITS);
        }
        //日期
        if (quotationDO.getCreateTime() != null) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String inputDate = dtf.format(quotationDO.getCreateTime());
            params.put("quoteDay", inputDate);
        }
        LocalDateTime now = LocalDateTime.now();
        params.put("priceValidity", Duration.between(now, quotationDO.getValidPeriod()).toDays());
        // 编号
        params.put("code", quotationDO.getCode());
        // 客户联系人
        String custPocName = quotationDO.getCustPocName();
        params.put("custPocName", custPocName);
        // 邮箱
        CustAllDTO custByCode = custApi.getCustByCode(quotationDO.getCustCode());
        String email = custByCode.getEmail();
        params.put("email", email);
        // 打印时间
        params.put("printTime", DateUtil.format(now, "yyyy-MM-dd"));
        params.put("companyNameEng", companyDTO.getCompanyNameEng());
        params.put("companyName", companyDTO.getCompanyName());
        params.put("addressEng", companyDTO.getCompanyAddressEng());

        LoopRowTableRenderPolicy policy = new LoopRowTableRenderPolicy();
        Configure configure = Configure.builder()
                .bind("quotations", policy).build();
        // 处理多规格情况,将每个规格拆分为单独一条数据
        List<QuotationItemDO> processedList = new ArrayList<>();
        for (QuotationItemDO item : quotationItemDOList) {
            // 获取规格列表
            List<JsonSpecificationEntity> specificationList = item.getSpecificationList();
            if (CollUtil.isNotEmpty(specificationList) && specificationList.size() > 1) {
                // 有多个规格,需要拆分
                for (JsonSpecificationEntity spec : specificationList) {
                    QuotationItemDO newItem = new QuotationItemDO();
                    org.springframework.beans.BeanUtils.copyProperties(item, newItem);
                    newItem.setSpecificationList(Collections.singletonList(spec));
                    processedList.add(newItem);
                }
            } else {
                // 单个规格,直接添加
                processedList.add(item);
            }
        }
        // 使用处理后的列表替换原列表
        quotationItemDOList = processedList;
        //包装方式
        List<PackageTypeDTO> packageTypeList = packageTypeApi.getList();
        if(CollUtil.isNotEmpty(packageTypeList)){
            quotationItemDOList.forEach(s->{
                if(CollUtil.isNotEmpty(s.getPackageType())){
                    List<Long> distinctPackgeType = s.getPackageType().stream().distinct().toList();
                    List<PackageTypeDTO> tempPackageTypeList = packageTypeList.stream().filter(item->distinctPackgeType.contains(item.getId())).toList();
                    if(CollUtil.isNotEmpty(tempPackageTypeList)){
                        List<String> packageTypeNameList = tempPackageTypeList.stream().map(PackageTypeDTO::getName).distinct().toList();
                        s.setPackageTypeName(String.join(",",packageTypeNameList));
                    }
                }
            });
        }
        List<QuotationItemExportVO> quotationItemExportVOList  = QuotationConvert.INSTANCE.convertQuotationItemExportVO(quotationItemDOList);
        quotationItemExportVOList.forEach(item->{
            //计量单位是英制 根据计量单位比例计算 外箱长，宽，高
//            if(unit.equals("eng")){
//                if(item.getOuterboxLength()!=null){
//                    item.setOuterboxLength(NumUtil.div(item.getOuterboxLength().toString(),"2.54").setScale(CalculationDict.TWO,RoundingMode.HALF_UP));
//                }
//                if(item.getOuterboxHeight()!=null){
//                    item.setOuterboxHeight(NumUtil.div(item.getOuterboxHeight().toString(),"2.54").setScale(CalculationDict.TWO,RoundingMode.HALF_UP));
//                }
//                if(item.getOuterboxWidth()!=null){
//                    item.setOuterboxWidth(NumUtil.div(item.getOuterboxWidth().toString(),"2.54").setScale(CalculationDict.TWO,RoundingMode.HALF_UP));
//                }
//            }else{
//                if(item.getOuterboxLength()!=null){
//                    item.setOuterboxLength(NumberFormatUtil.format(item.getOuterboxLength(),CalculationDict.TWO));
//                }
//                if(item.getOuterboxHeight()!=null){
//                    item.setOuterboxHeight(NumberFormatUtil.format(item.getOuterboxHeight(),CalculationDict.TWO));
//                }
//                if(item.getOuterboxWidth()!=null){
//                    item.setOuterboxWidth(NumberFormatUtil.format(item.getOuterboxWidth(),CalculationDict.TWO));
//                }
//            }

            if(item.getQuotation()!=null && item.getQuotation().getAmount()!=null){
                // 将报价金额转化为USD后再格式化
                Map<String, BigDecimal> dailyRateMap = rateApi.getDailyRateMap();
                JsonAmount result = CurrencyUtil.changeCurrency(item.getQuotation(), "USD", dailyRateMap);
                item.setQuotationAmount(NumberFormatUtil.formatAmount(result.getAmount()).toPlainString());
            }
            if(item.getQuoteDate()!=null){
                item.setQuoteDays(Duration.between(item.getQuoteDate(),now).toDays());
            }
            if(item.getMainPicture()!=null){
                try {
                    String inputPath = item.getMainPicture().getFileUrl();
                    byte[] content = fileApi.getFileContent(inputPath.substring(inputPath.lastIndexOf("get/") + 4));
                    item.setMainPictureRender(Pictures.ofBytes(content).size(120, 120).create());
                } catch (Exception e) {
                    logger.info("报价单导出图片获取失败"+e.getMessage());
                }
            }
        });
        params.put("configure", configure);
        params.put("quotations", quotationItemExportVOList);

//        Configure configure = Configure.createDefault().plugin('%', new PoiPicPolicy());
//        params.put("configure", configure);
//        RowRenderData row0;
//        if (BooleanEnum.YES.getValue().equals(totalPurchaseFlag)){
//            row0 = Rows.of(
//                    "Customer Item No", "Vertak Item No", "Picture", "Description", "采购总价","FOB Cost (USD)",
//                    "Package", "MOQ (pcs)", "QTY/Inner Box", "QTY/Outer carton", "N.W. (KGS)", "G.W. (KGS)",
//                    "Carton Length (CM)", "Carton Width (CM)", "Carton Height (CM)", "Carton CBM",
//                    "20ft Loading (pcs)", "40ft Loading (pcs)", "40ft HC Loading (pcs)", "HS code", "Lead Time (Days)"
//            ).rowExactHeight(1).center().create();
//        }else {
//            row0 = Rows.of(
//                    "Customer Item No", "Vertak Item No", "Picture", "Description", "FOB Cost (USD)",
//                    "Package", "MOQ (pcs)", "QTY/Inner Box", "QTY/Outer carton", "N.W. (KGS)", "G.W. (KGS)",
//                    "Carton Length (CM)", "Carton Width (CM)", "Carton Height (CM)", "Carton CBM",
//                    "20ft Loading (pcs)", "40ft Loading (pcs)", "40ft HC Loading (pcs)", "HS code", "Lead Time (Days)"
//            ).rowExactHeight(1).center().create();
//        }
//        double[] num = BooleanEnum.YES.getValue().equals(totalPurchaseFlag)? new double[]{
//                0.63, 0.63, 0.63, 1.5, 0.63, 0.63, 0.63, 0.63, 0.63, 0.63, 0.63, 0.63, 0.63, 0.63, 0.63, 0.63, 0.63, 0.63, 0.63,0.63,0.63
//        } : new double[]{
//                0.63, 0.63,0.63, 0.63, 1.5, 0.63, 0.63, 0.63, 0.63, 0.63, 0.63, 0.63, 0.63, 0.63, 0.63, 0.63, 0.63, 0.63, 0.63, 0.63
//        };
//        TableRenderData table = Tables.ofA4ExtendWidth().width(9.5, num).create();
//        table.addRow(row0);
//
//        quotationItemDOList.forEach(s -> {
//            List<JsonSpecificationEntity> specificationList = s.getSpecificationList();
//            if (CollUtil.isEmpty(specificationList)){
//                return;
//            }
//            specificationList.forEach(specificationEntity -> {
//                String quotation = NumberFormatUtil.formatUnitAmount(s.getQuotation().getAmount()) + " " + s.getQuotation().getCurrency();
//                String psSizeInner = "";
//                String psSizeOuter = "";
//                Integer qtyPerInnerbox =0;
//                Integer qtyPerOuterbox =0;
//
//                if (Objects.nonNull(s.getQtyPerOuterbox())&&s.getQtyPerOuterbox()>0){
//                    qtyPerOuterbox = s.getQtyPerOuterbox();
//                    psSizeOuter = NumUtil.div(s.getMoq(), qtyPerOuterbox).setScale(CalculationDict.ZERO, RoundingMode.UP).toString();
//                }
//                if (Objects.nonNull(s.getQtyPerInnerbox())&&s.getQtyPerInnerbox()>0){
//                    qtyPerInnerbox = s.getQtyPerInnerbox();
//                    psSizeInner = NumUtil.div(s.getMoq(),qtyPerInnerbox).setScale(CalculationDict.ZERO, RoundingMode.UP).toString();
//                }
//                RowRenderData dataRow;
//                if (BooleanEnum.YES.getValue().equals(totalPurchaseFlag)){
//                    String withTaxPrice = NumberFormatUtil.formatUnitAmount(s.getWithTaxPrice().getAmount()) + " " + s.getWithTaxPrice().getCurrency();
//                    dataRow =Rows.of(
//                            s.getSkuCode(),
//                            s.getCskuCode(),
//                            "",
//                            "",
//                            withTaxPrice,
//                            quotation,
//                            s.getPackageTypeName(),
//                            s.getMoq().toString(),
//                            psSizeInner,
//                            psSizeOuter,
//                            s.getQtyPerInnerbox().toString(),
//                            s.getQtyPerOuterbox().toString(),
//                            specificationEntity.getOuterboxLength().toString(),
//                            specificationEntity.getOuterboxWidth().toString(),
//                            specificationEntity.getOuterboxHeight().toString(),
//                            specificationEntity.getOuterboxVolume().toString(),
//                            s.getTwentyFootCabinetNum().toString(),
//                            s.getFortyFootCabinetNum().toString(),
//                            s.getFortyFootContainerNum().toString(),
//                            s.getHsCode(), s.getQuoteDate().toString()
//                    ).rowExactHeight(2.41).create();
//                }else {
//                    dataRow =Rows.of(
//                            s.getSkuCode(),
//                            s.getCskuCode(),
//                            "",
//                            "",
//                            quotation,
//                            s.getPackageTypeName(),
//                            s.getMoq().toString(),
//                            psSizeInner,
//                            psSizeOuter,
//                            s.getQtyPerInnerbox().toString(),
//                            s.getQtyPerOuterbox().toString(),
//                            specificationEntity.getOuterboxLength().toString(),
//                            specificationEntity.getOuterboxWidth().toString(),
//                            specificationEntity.getOuterboxHeight().toString(),
//                            specificationEntity.getOuterboxVolume().toString(),
//                            s.getTwentyFootCabinetNum().toString(),
//                            s.getFortyFootCabinetNum().toString(),
//                            s.getFortyFootContainerNum().toString(),
//                            s.getHsCode(), s.getQuoteDate().toString()
//                    ).rowExactHeight(2.41).create();
//                }
//                ParagraphRenderData paragraphRenderData = new ParagraphRenderData();
//                SimpleFile mainPicture = s.getMainPicture();
//                if (Objects.nonNull(mainPicture)&&StrUtil.isNotEmpty(mainPicture.getFileUrl())){
//                    try {
//                        byte[] content = fileApi.getFileContent(mainPicture.getFileUrl().substring(mainPicture.getFileUrl().lastIndexOf("get/") + 4));
//                        if (Objects.isNull(content)){
//                            throw new Exception("图片不存在");
//                        }
//                        PictureRenderData pictureRenderData = Pictures.ofBytes(content).center().size(5,5).create();
//                        paragraphRenderData.addPicture(pictureRenderData);
//                        dataRow.getCells().get(2).setParagraphs(List.of(paragraphRenderData));
//                    } catch (Exception e) {
//                        logger.warn("主图加载失败，详情：" + e.getMessage());
//                    }
//                }
//                // 字体样式
//
//                // 段落样式
//                ParagraphStyle paragraphStyle = new ParagraphStyle();
//                Style contentStyle = new Style("微软雅黑", 2);
//                paragraphStyle.setDefaultTextStyle(contentStyle);
//                paragraphStyle.setAlign(ParagraphAlignment.CENTER);
//
//                CellStyle cellStyle = new CellStyle();
//                cellStyle.setVertAlign(XWPFTableCell.XWPFVertAlign.CENTER);
//                cellStyle.setDefaultParagraphStyle(paragraphStyle);
//
//                RowStyle rowStyle = new RowStyle();
//                rowStyle.setDefaultCellStyle(cellStyle);
//                dataRow.setRowStyle(rowStyle);
//
//                table.addRow(dataRow);
//            });
//        });
//        params.put("table", table);
        return params;
    }
}