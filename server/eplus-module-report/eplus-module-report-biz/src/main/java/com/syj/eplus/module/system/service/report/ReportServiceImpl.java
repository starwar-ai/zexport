package com.syj.eplus.module.system.service.report;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.StrUtil;
import cn.hutool.system.OsInfo;
import cn.hutool.system.SystemUtil;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.operatelog.core.util.ChangeCompareUtil;
import cn.iocoder.yudao.framework.operatelog.core.util.OperateLogUtils;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.bpm.api.task.BpmProcessInstanceApi;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import cn.iocoder.yudao.module.bpm.enums.task.BpmProcessInstanceResultEnum;
import cn.iocoder.yudao.module.infra.api.file.FileApi;
import com.syj.eplus.module.infra.api.formchange.FormChangeApi;
import com.syj.eplus.module.infra.api.formchange.dto.FormChangeDTO;
import com.aspose.words.Document;
import com.aspose.words.FontSettings;
import com.aspose.words.License;
import com.aspose.words.SaveFormat;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.syj.eplus.framework.common.enums.SubmitFlagEnum;
import com.syj.eplus.module.infra.api.CodeGeneratorApi;
import com.syj.eplus.module.system.api.report.dto.ReportDTO;
import com.syj.eplus.module.system.controller.admin.report.vo.*;
import com.syj.eplus.module.system.controller.admin.report.vo.change.ChangeReportRespVO;
import com.syj.eplus.module.system.controller.admin.report.vo.change.ChangeReportSaveReq;
import com.syj.eplus.module.system.convert.report.ReportConvert;
import com.syj.eplus.module.system.dal.dataobject.report.ReportChangeDO;
import com.syj.eplus.module.system.dal.dataobject.report.ReportDO;
import com.syj.eplus.module.system.dal.mysql.report.ReportMapper;
import com.syj.eplus.module.system.dal.mysql.reportchange.ReportChangeMapper;
import com.syj.eplus.module.system.enums.report.ReportTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.system.enums.ErrorCodeConstants.*;

/**
 * 打印模板 Service 实现类
 *
 * @author ePlus
 */
@Service
@Validated
public class ReportServiceImpl implements ReportService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private ReportMapper reportMapper;

    @Resource
    private ReportChangeMapper reportChangeMapper;
    @Resource
    private FileApi fileApi;
    @Resource
    private FormChangeApi formChangeApi;
    @Resource
    private BpmProcessInstanceApi bpmProcessInstanceApi;
    @Resource
    private CodeGeneratorApi codeGeneratorApi;

    private static final String PROCESS_DEFINITION_KEY = "system_poi_report";
    private static final String CHANGE_SN_TYPE = "system_poi_report_change";
    private static final String CHANGE_CODE_PREFIX = "RPC";
    private static final String PROCESS_DEFINITION_CHANGE_KEY = "system_poi_report_change";
    public static final String CHANGE_OPERATOR_EXTS_KEY = "changeReportCode";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createReport(ReportSaveReqVO createReqVO) {
        //同一类型模板不可重复创建
        ReportDO reportDO = null;
        HashMap map = new HashMap<String, Object>();
        map.put("code", createReqVO.getCode());
        map.put("report_type", createReqVO.getReportType());
        if (createReqVO.getReportType() == ReportTypeEnum.CUSTOM.getValue()) {
            map.put("company_id", createReqVO.getCompanyId());
            map.put("source_code", createReqVO.getSourceCode());
            map.put("source_type", createReqVO.getSourceType());
        } else if (createReqVO.getReportType() == ReportTypeEnum.SPECIFIC.getValue()) {
            map.put("company_id", createReqVO.getCompanyId());
            map.put("name", createReqVO.getName());
        } else if (createReqVO.getReportType() == ReportTypeEnum.COMPANY.getValue()) {
            map.put("company_id", createReqVO.getCompanyId());
        }
        reportDO = reportMapper.selectOneByMap(map);
        if (reportDO != null) {
            throw exception(REPORT_MUL);
        }
        // 只要新增进来则初始审核状态为未提交
        createReqVO.setAuditStatus(BpmProcessInstanceResultEnum.UNSUBMITTED.getResult());
        ReportDO report = ReportConvert.INSTANCE.convertReportDO(createReqVO);
        reportMapper.insert(report);
        Long reportId = report.getId();
        if (SubmitFlagEnum.SUBMIT.getStatus().equals(createReqVO.getSubmitFlag())) {
            submitTask(reportId, WebFrameworkUtils.getLoginUserId());
        }
        // 返回
        return reportId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateReport(ReportSaveReqVO updateReqVO) {
        // 校验存在
        validateReportExists(updateReqVO.getId());
        // 更新
        ReportDO updateObj = ReportConvert.INSTANCE.convertReportDO(updateReqVO);
        reportMapper.updateById(updateObj);
        Long reportId = updateObj.getId();
        if (SubmitFlagEnum.SUBMIT.getStatus().equals(updateReqVO.getSubmitFlag())) {
            submitTask(reportId, WebFrameworkUtils.getLoginUserId());
        }
    }

    @Override
    public void deleteReport(Long id) {
        // 校验存在
        validateReportExists(id);
        // 删除
        reportMapper.deleteById(id);
    }

    private ReportDO validateReportExists(Long id) {
        ReportDO reportDO = reportMapper.selectById(id);
        if (reportDO == null) {
            throw exception(REPORT_NOT_EXISTS);
        }
        return reportDO;
    }

    @Override
    public ReportInfoRespVO getReport(SystemReportDetailReq req) {
        try {
            Document nodes = new Document();
            nodes.updatePageLayout();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //将不同入口的参数转换为reportId
        Long reportId = Objects.nonNull(req.getProcessInstanceId()) ? bpmProcessInstanceApi.selectAuditAbleIdByProcessInstanceId(req.getProcessInstanceId()) : req.getReportId();
        if (Objects.isNull(reportId)) {
            logger.error("[模板详情]未获取到模板id");
            return null;
        }
        ReportDO reportDO = reportMapper.selectById(reportId);
        if (reportDO == null) {
            return null;
        }
        ReportInfoRespVO reportInfoRespVO = ReportConvert.INSTANCE.convertReportInfoRespVO(reportDO);
        String bpmProcessInstanceId = bpmProcessInstanceApi.getBpmProcessInstanceId(reportId, PROCESS_DEFINITION_KEY);
        if (StrUtil.isNotEmpty(bpmProcessInstanceId)) {
            reportInfoRespVO.setProcessInstanceId(bpmProcessInstanceId);
        }
        return reportInfoRespVO;
    }


    @Override
    public List<ReportDO> getCompanySpecificReport(Long companyId, String reportCode) {
        LambdaQueryWrapper<ReportDO> queryWrapper = new LambdaQueryWrapperX<ReportDO>()
                .eq(ReportDO::getCompanyId, companyId)
                .eq(ReportDO::getCode, reportCode)
                .eq(ReportDO::getReportType, ReportTypeEnum.SPECIFIC.getValue());
        List<ReportDO> list = reportMapper.selectList(queryWrapper).stream().toList();
        return list;
    }

    @Override
    public ReportDTO getReportByCode(String code) {
        ReportDO reportDO = reportMapper.selectOne(ReportDO::getCode, code, ReportDO::getReportType, ReportTypeEnum.BASE.getValue());
        if (reportDO == null) {
            return null;
        }
        return ReportConvert.INSTANCE.convertReportDTO(reportDO);
    }

    @Override
    public ReportDTO getReportByCodeAndCompanyId(String code, Long companyId) {
        ReportDO reportDO = reportMapper.selectOne(ReportDO::getCode, code, ReportDO::getCompanyId, companyId, ReportDO::getReportType, ReportTypeEnum.COMPANY.getValue());
        if (reportDO == null) {
            reportDO = reportMapper.selectOne(ReportDO::getCode, code, ReportDO::getReportType, ReportTypeEnum.BASE.getValue());
        }
        if (reportDO == null) {
            return null;
        }
        return ReportConvert.INSTANCE.convertReportDTO(reportDO);
    }

    @Override
    public ReportDTO getSourceReport(String code, String sourceCode, Integer sourceType, Long companyId) {
        ReportDO reportDO = null;
        if (sourceType == null && sourceCode == null) {
            reportDO = reportMapper.selectOne(ReportDO::getCode, code, ReportDO::getReportType, ReportTypeEnum.BASE.getValue());
        }
        if (sourceType != null && sourceCode != null) {
            HashMap map = new HashMap<String, Object>();
            map.put("code", code);
            map.put("report_type", ReportTypeEnum.CUSTOM.getValue());
            map.put("source_code", sourceCode);
            map.put("source_type", sourceType);
            map.put("company_id", companyId);
            reportDO = reportMapper.selectOneByMap(map);
            if (reportDO == null) {
                return getReportByCodeAndCompanyId(code, companyId);
            }
        }
        if (reportDO == null) {
            return null;
        }
        return ReportConvert.INSTANCE.convertReportDTO(reportDO);
    }

    @Override
    public ReportDTO getReportById(Long reportId) {
        ReportDO reportDO = reportMapper.selectById(reportId);
        if (reportDO == null) {
            return null;
        }
        return ReportConvert.INSTANCE.convertReportDTO(reportDO);
    }

    @Override
    public PageResult<ReportDO> getReportPage(ReportPageReqVO pageReqVO) {
        PageResult<ReportDO> reportDOPageResult = reportMapper.selectPage(pageReqVO);
        List<ReportDO> reportDOList = reportDOPageResult.getList();
        if (CollUtil.isEmpty(reportDOList)) {
            return null;
        }
        return reportDOPageResult;
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
    public void submitTask(Long reportId, Long userId) {
        bpmProcessInstanceApi.createProcessInstance(userId, PROCESS_DEFINITION_KEY, reportId);
        updateAuditStatus(reportId, BpmProcessInstanceResultEnum.PROCESS.getResult());
    }

    @Override
    public void updateAuditStatus(Long auditableId, Integer auditStatus) {
        reportMapper.updateById(ReportDO.builder().id(auditableId).auditStatus(auditStatus).build());
    }

    @Override
    public String getProcessDefinitionKey() {
        return PROCESS_DEFINITION_KEY;
    }

    @Override
    public String print(String inputPath, String outputPath, HashMap<String, Object> params,String outputName) {
        StopWatch print = new StopWatch("总耗时");
        print.start();
        try {
            StopWatch getFileContent = new StopWatch("下载模板");
            getFileContent.start();
            byte[] content = fileApi.getFileContent(inputPath.substring(inputPath.lastIndexOf("get/") + 4));
            getFileContent.stop();
            logger.info("下载模板耗时{}ms", getFileContent.getTotalTimeSeconds());
            ByteArrayInputStream inputStream = new ByteArrayInputStream(content);
//            File inputFile = new File(inputDownloadPath);
//            if (!inputFile.exists()) {
//                throw exception(INPUT_PAth_NOT_EXISTS);
//            }
            StopWatch filePath = new StopWatch("替换路径");
            filePath.start();
            File wordFile = new File(outputPath);
            if (!wordFile.exists()) {
                File parentDir = wordFile.getParentFile();
                if (parentDir == null || !parentDir.exists()) {
                    parentDir.mkdirs();
                }
            }
            filePath.stop();
            logger.info("替换路径耗时{}ms", filePath.getTotalTimeSeconds());

            StopWatch changeField = new StopWatch("替换变量");
            changeField.start();
            XWPFTemplate template;
            if (params.get("configure") != null) {
                Configure configure = (Configure) params.get("configure");
                template = XWPFTemplate.compile(inputStream, configure).render(params);
            } else {
                template = XWPFTemplate.compile(inputStream).render(params);
            }
            FileOutputStream fileOutputStream = new FileOutputStream(wordFile);
            template.writeAndClose(fileOutputStream);
            changeField.stop();
            logger.info("替换变量耗时{}ms", changeField.getTotalTimeMillis());

            StopWatch pdfWatch = new StopWatch("转换PDF");
            pdfWatch.start();
            String pdfPath = wordFile.getParentFile() + "/" + wordFile.getName().substring(0, wordFile.getName().lastIndexOf(".")) + ".pdf";
            wordToPdf(outputPath, pdfPath);
            pdfWatch.stop();
            logger.info("转换PDF耗时{}ms", pdfWatch.getTotalTimeMillis());

            StopWatch uploadWatch = new StopWatch("文件上传");
            uploadWatch.start();
            File pdfFile = new File(pdfPath);
            byte[] pdfContent = new byte[(int) pdfFile.length()];
            FileInputStream fis = new FileInputStream(pdfFile);
            fis.read(pdfContent);
            fis.close();
            String result = fileApi.createFile(pdfContent,outputName);
            uploadWatch.stop();
            logger.info("文件上传耗时{}ms", uploadWatch.getTotalTimeMillis());
            //删除中间文件
            wordFile.delete();
            pdfFile.delete();
            print.stop();
            logger.info("总耗时{}ms", print.getTotalTimeMillis());
            return result;
        } catch (Exception e) {
            logger.error(REPORT_ERROR.getMsg() + ":" + e.getMessage());
            throw exception(new ErrorCode(1_010_001_004, REPORT_ERROR.getMsg() + ":" + e.getMessage()));
        }
    }

    @Override
    public void exportWord(HttpServletResponse response, String inputPath, String outputPath, HashMap<String, Object> params) {
        try {
            System.out.println("开始导出PDF：模板路径：" + inputPath);
            byte[] content = fileApi.getFileContent(inputPath.substring(inputPath.lastIndexOf("get/") + 4));
            System.out.println("获取输入文件byte");
            ByteArrayInputStream inputStream = new ByteArrayInputStream(content);
            File wordFile = new File(outputPath);
            if (!wordFile.exists()) {
                File parentDir = wordFile.getParentFile();
                if (parentDir == null || !parentDir.exists()) {
                    parentDir.mkdirs();
                }
            }
            System.out.println("开始替换变量");
            XWPFTemplate template;
            if (params.get("configure") != null) {
                Configure configure = (Configure) params.get("configure");
                template = XWPFTemplate.compile(inputStream, configure).render(params);
            } else {
                template = XWPFTemplate.compile(inputStream).render(params);
            }
            FileOutputStream fileOutputStream = new FileOutputStream(wordFile);
            template.writeAndClose(fileOutputStream);
            // 设置响应头
            response.setContentType("application/msword");
            response.setHeader("Content-Disposition", "inline; filename=\"example.docx\"");
            try (InputStream inputStream2 = new FileInputStream(wordFile);
                 OutputStream outputStream = response.getOutputStream()) {
                // 设置 Content-Length（可选但建议）
                response.setContentLength((int) wordFile.length());
                // 将文件内容写入响应流
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream2.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
            //删除中间文件
            wordFile.delete();
        } catch (Exception e) {
            logger.error(REPORT_ERROR.getMsg() + ":" + e.getMessage());
            throw exception(REPORT_ERROR);
        }
    }

    public void wordToPdf(String outputFile, String outputPdf) {
        FileOutputStream os = null;
        try {
            //凭证 不然切换后有水印
            InputStream is = new ClassPathResource("/license.xml").getStream();
            License aposeLic = new License();
            aposeLic.setLicense(is);
            if (!aposeLic.getIsLicensed()) {
                System.out.println("License验证不通过...");
            }
            
            // 设置Java系统属性，指定默认字体
            // 这会影响Aspose.Words在找不到字体时的行为
//            System.setProperty("awt.useSystemAAFontSettings", "on");
//            System.setProperty("swing.aatext", "true");
            OsInfo osInfo = SystemUtil.getOsInfo();
            if(osInfo.isLinux()){
                FontSettings.setFontsFolder("/usr/share/fonts/windows", true);
            }
            
            // 在Linux服务器上需要安装中文字体
            // 可以通过以下命令安装：
            // CentOS/RHEL: sudo yum install -y wqy-microhei-fonts wqy-zenhei-fonts
            // Ubuntu/Debian: sudo apt-get install -y fonts-wqy-microhei fonts-wqy-zenhei
            StopWatch pdfWatch = new StopWatch("转换PDF文件");
            pdfWatch.start();
            //生成一个空的PDF文件
            File file = new File(outputPdf);
            os = new FileOutputStream(file);
            //要转换的word文件
            Document doc = new Document(outputFile);

            // 使用SaveFormat.PDF保存为PDF
            doc.save(os, SaveFormat.PDF);
            pdfWatch.stop();
            logger.info("转换PDF耗时{}ms", pdfWatch.getTotalTimeMillis());
            System.out.println("PDF转换完成: " + outputPdf);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("PDF转换失败: " + e.getMessage());
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /***-----------------------变更-----------*/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeReport(ChangeReportSaveReq changeReq) {

        ReportInfoRespVO oldReport = changeReq.getOld_shipment();
        ReportInfoRespVO report = changeReq.getShipment();
        // 提交流程标识 默认使用前端传入
        AtomicReference<Integer> submitFlag = new AtomicReference<>(0);
        Set<String> changeFields = new ChangeCompareUtil<ReportInfoRespVO>().transformObject(oldReport, report);
        // 变更字段名称
        Map<String, FormChangeDTO> formChangeDTOList = formChangeApi.getFormChangeDTOList(List.of("system_report"));
        if (CollUtil.isEmpty(formChangeDTOList)) {
            throw exception(REPORT_FIELD_CHANGE_CONFIG_NOT_EXISTS);
        }
        String code = codeGeneratorApi.getCodeGenerator(CHANGE_SN_TYPE, CHANGE_CODE_PREFIX);
        ReportChangeDO reportChangeDO = ReportConvert.INSTANCE.convertReportChange(report);
        reportChangeDO.setOldData(oldReport);
        reportChangeDO.setCode(code);
        // 比对变更配置字段
        submitFlag = formChangeApi.dealShipmentTable(changeFields, formChangeDTOList, reportChangeDO, "system_report", submitFlag, true);

        // 记录操作日志
        OperateLogUtils.setContent(String.format("创建采购合同变更记录【%s】", code));
        OperateLogUtils.addExt(CHANGE_OPERATOR_EXTS_KEY, reportChangeDO.getCode());
        reportChangeMapper.insert(reportChangeDO);

    }

    @Override
    public ChangeReportRespVO getChangeReport(Long id) {
        ReportChangeDO reportChangeDO = reportChangeMapper.selectById(id);
        if (Objects.isNull(reportChangeDO)) {
            return null;
        }
        return ReportConvert.INSTANCE.convertChangeReportRespVO(reportChangeDO);
    }

    @Override
    public PageResult<ChangeReportRespVO> getReportChangePage(ReportPageReqVO pageReqVO) {
        PageResult<ReportChangeDO> reportChangeDOPageResult = reportChangeMapper.selectPage(pageReqVO);
        if (CollUtil.isEmpty(reportChangeDOPageResult.getList())) {
            return PageResult.empty();
        }
        List<ChangeReportRespVO> changeShipmentRespVOList = ReportConvert.INSTANCE.convertChangeReportRespVOList(reportChangeDOPageResult.getList());
        return new PageResult<ChangeReportRespVO>().setTotal(reportChangeDOPageResult.getTotal()).setList(changeShipmentRespVOList);
    }


    @Override
    public void approveChangeTask(Long userId, BpmTaskApproveReqDTO reqVO) {
        bpmProcessInstanceApi.approveTask(userId, BeanUtils.toBean(reqVO, BpmTaskApproveReqDTO.class));
    }

    @Override
    public void rejectChangeTask(Long userId, BpmTaskRejectReqDTO reqVO) {
        bpmProcessInstanceApi.rejectTask(userId, BeanUtils.toBean(reqVO, BpmTaskRejectReqDTO.class));
    }

    @Override
    public void submitChangeTask(Long reportId, Long userId) {
        bpmProcessInstanceApi.createProcessInstance(userId, PROCESS_DEFINITION_CHANGE_KEY, reportId);
        updateAuditStatus(reportId, BpmProcessInstanceResultEnum.PROCESS.getResult());
    }

    @Override
    public void updateChangeAuditStatus(Long auditableId, Integer auditStatus) {
        reportMapper.updateById(ReportDO.builder().id(auditableId).auditStatus(auditStatus).build());
    }

    @Override
    public String getChangeProcessDefinitionKey() {
        return PROCESS_DEFINITION_CHANGE_KEY;
    }

    @Override
    public void directlyUpdateReport(ReportChangeSaveReqVO updateReqVO) {
       if(Objects.isNull(updateReqVO)){
           throw exception(REPORT_PARA_ERROR);
       }
        ReportDO reportDO = validateReportExists(updateReqVO.getId());
        reportMapper.updateById(new ReportDO()
                .setId(reportDO.getId())
                .setName(updateReqVO.getName())
                .setCode(updateReqVO.getCode())
                .setAnnex(updateReqVO.getAnnex())
                .setBusinessType(updateReqVO.getBusinessType()));
    }

}