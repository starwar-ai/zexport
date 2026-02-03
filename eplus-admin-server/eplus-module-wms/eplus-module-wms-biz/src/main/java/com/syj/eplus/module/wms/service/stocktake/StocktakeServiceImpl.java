package com.syj.eplus.module.wms.service.stocktake;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.io.FileUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.bpm.api.task.BpmProcessInstanceApi;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import cn.iocoder.yudao.module.bpm.enums.task.BpmProcessInstanceResultEnum;
import cn.iocoder.yudao.module.infra.api.file.FileApi;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.framework.common.enums.BooleanEnum;
import com.syj.eplus.framework.common.enums.SubmitFlagEnum;
import com.syj.eplus.framework.common.util.NumUtil;
import com.syj.eplus.module.pms.api.sku.SkuApi;
import com.syj.eplus.module.pms.api.sku.dto.SimpleSkuDTO;
import com.syj.eplus.module.pms.api.sku.dto.SkuDTO;
import com.syj.eplus.module.scm.api.purchasecontract.PurchaseContractApi;
import com.syj.eplus.module.scm.api.purchasecontract.dto.PurchaseContractItemDTO;
import com.syj.eplus.module.system.api.report.ReportApi;
import com.syj.eplus.module.system.api.report.dto.ReportDTO;
import com.syj.eplus.module.wms.api.stock.IStockApi;
import com.syj.eplus.module.wms.api.stock.dto.StockDTO;
import com.syj.eplus.module.wms.controller.admin.stocktake.vo.*;
import com.syj.eplus.module.wms.controller.admin.stocktakeitem.vo.StocktakeItemRespVO;
import com.syj.eplus.module.wms.controller.admin.stocktakeitem.vo.StocktakeItemSaveReqVO;
import com.syj.eplus.module.wms.convert.stocktake.StocktakeConvert;
import com.syj.eplus.module.wms.dal.dataobject.stocktake.StocktakeDO;
import com.syj.eplus.module.wms.dal.dataobject.stocktake.StocktakeItemDO;
import com.syj.eplus.module.wms.dal.mysql.stocktake.StocktakeItemMapper;
import com.syj.eplus.module.wms.dal.mysql.stocktake.StocktakeMapper;
import com.syj.eplus.module.wms.enums.StocktakeResultEnum;
import com.syj.eplus.module.wms.enums.StocktakeStatusEnum;
import com.syj.eplus.module.wms.util.BatchCodeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.dms.enums.ErrorCodeConstants.DECLARATION_NOT_EXISTS;
import static com.syj.eplus.module.scm.enums.ErrorCodeConstants.*;
import static com.syj.eplus.module.wms.enums.ErrorCodeConstants.STOCKTAKE_ITEM_NOT_EXISTS;
import static com.syj.eplus.module.wms.enums.ErrorCodeConstants.STOCKTAKE_NOT_EXISTS;

/**
 * 仓储管理-盘点单 Service 实现类
 *
 * @author ePlus
 */
@Service
@Validated
public class StocktakeServiceImpl implements StocktakeService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private StocktakeMapper stocktakeMapper;
    @Resource
    private StocktakeItemMapper stocktakeItemMapper;
    @Resource
    private BpmProcessInstanceApi bpmProcessInstanceApi;
    private static final String PROCESS_DEFINITION_KEY = "wms_stocktake";
    @Resource
    private BatchCodeUtil batchCodeUtil;
    @Resource
    private SkuApi skuApi;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private PurchaseContractApi purchaseContractApi;
    @Resource
    private ReportApi reportApi;
    @Resource
    private FileApi fileApi;
    @Resource
    private IStockApi stockApi;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createStocktake(StocktakeSaveReqVO createReqVO) {
        StocktakeDO stocktake = StocktakeConvert.INSTANCE.convertStocktakeDO(createReqVO);
        String code = batchCodeUtil.genBatchCode("ST");
        stocktake.setCode(code);
        stocktake.setPrintFlag(BooleanEnum.NO.getValue());
        stocktake.setPrintTimes(BigDecimal.ZERO.intValue());
        stocktake.setStocktakeStatus(StocktakeStatusEnum.NOT_START.getValue());
        stocktake.setAuditStatus(BpmProcessInstanceResultEnum.UNSUBMITTED.getResult());

        // 插入
        stocktakeMapper.insert(stocktake);
        if (SubmitFlagEnum.SUBMIT.getStatus().equals(createReqVO.getSubmitFlag())) {
            submitTask(stocktake.getId(), WebFrameworkUtils.getLoginUserId());
        }
        // 插入子表
        List<StocktakeItemSaveReqVO> itemSaveReqVOList = createReqVO.getItemSaveReqVOList();
        itemSaveReqVOList.stream().forEach(
                x->{
                    // 设置盘点信息默认值
                    Integer stockQuantity = x.getStockQuantity();
                    Integer stocktakeStockQuantity = x.getStocktakeStockQuantity();
                    x.setStocktakePosition(x.getPosition())
                    .setStocktakeStockQuantity(stockQuantity);
                    // 计算盘点箱数
                    Integer qtyPerOuterbox = x.getQtyPerOuterbox();
                    x.setStockBoxQuantity(NumUtil.div(stockQuantity,qtyPerOuterbox).setScale(2, RoundingMode.UP).intValue());
                    x.setStocktakeStockBoxQuantity(NumUtil.div(stocktakeStockQuantity,qtyPerOuterbox).setScale(2, RoundingMode.UP).intValue());
                });
        createStocktakeItemList(stocktake.getId(), itemSaveReqVOList);
        // 返回
        return stocktake.getId();
    }

    private boolean createStocktakeItemList(Long stocktakeId, List<StocktakeItemSaveReqVO> list) {
        list.forEach(o -> o.setId(null).setStocktakeId(stocktakeId));
        List<StocktakeItemDO> itemDOList = BeanUtils.toBean(list, StocktakeItemDO.class);
        List<String> purchaseCodeList = itemDOList.stream().map(StocktakeItemDO::getPurchaseContractCode).distinct().toList();
        if (CollUtil.isEmpty(purchaseCodeList)){
            return stocktakeItemMapper.insertBatch(itemDOList);
        }
        Map<String, Map<String, PurchaseContractItemDTO>> purchaseContractItemMap = purchaseContractApi.getPurchaseContractItemMap(purchaseCodeList);
        if (CollUtil.isEmpty(purchaseContractItemMap)){
            return stocktakeItemMapper.insertBatch(itemDOList);
        }
        itemDOList.forEach(s->{
            Map<String, PurchaseContractItemDTO> itemMapKeySku = purchaseContractItemMap.get(s.getPurchaseContractCode());
            if (CollUtil.isEmpty(itemMapKeySku)){
                return;
            }
            PurchaseContractItemDTO purchaseContractItemDTO = itemMapKeySku.get(s.getSkuCode());
            if (Objects.isNull(purchaseContractItemDTO)){
                return;
            }
            UserDept purchaseUser = new UserDept().setUserId(purchaseContractItemDTO.getPurchaseUserId()).setNickname(purchaseContractItemDTO.getPurchaseUserName())
                    .setDeptId(purchaseContractItemDTO.getPurchaseUserDeptId()).setDeptName(purchaseContractItemDTO.getPurchaseUserDeptName());
            s.setPurchaseUser(purchaseUser);
        });
        return stocktakeItemMapper.insertBatch(itemDOList);
    }

    @Override
    public void updateStocktake(StocktakeSaveReqVO updateReqVO) {
        // 校验存在
        validateStocktakeExists(updateReqVO.getId());
        // 更新
        StocktakeDO updateObj = StocktakeConvert.INSTANCE.convertStocktakeDO(updateReqVO);
        stocktakeMapper.updateById(updateObj);
        // 更新子表
        updateStocktakeItemList(updateReqVO.getId(), updateReqVO.getItemSaveReqVOList());
    }

    private boolean updateStocktakeItemList(Long stocktakeId, List<StocktakeItemSaveReqVO> list) {
        deleteStocktakeItemByStocktakeId(stocktakeId);
        list.forEach(o -> o.setId(null)); // 解决更新情况下：1）id 冲突；2）updateTime 不更新
        return createStocktakeItemList(stocktakeId, list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteStocktake(Long id) {
        // 校验存在
        validateStocktakeExists(id);
        // 删除
        stocktakeMapper.deleteById(id);

        // 删除子表
        deleteStocktakeItemByStocktakeId(id);
    }

    private void validateStocktakeExists(Long id) {
        if (stocktakeMapper.selectById(id) == null) {
            throw exception(STOCKTAKE_NOT_EXISTS);
        }
    }

    @Override
    public void approveTask(Long userId, BpmTaskApproveReqDTO reqDTO) {
        bpmProcessInstanceApi.approveTask(userId, BeanUtils.toBean(reqDTO, BpmTaskApproveReqDTO.class));
    }

    @Override
    public void rejectTask(Long userId, BpmTaskRejectReqDTO reqDTO) {
        bpmProcessInstanceApi.rejectTask(userId, BeanUtils.toBean(reqDTO, BpmTaskRejectReqDTO.class));
    }

    @Override
    public void submitTask(Long stocktakeId, Long userId) {
        bpmProcessInstanceApi.createProcessInstance(userId, PROCESS_DEFINITION_KEY, stocktakeId);
        updateAuditStatus(stocktakeId, BpmProcessInstanceResultEnum.PROCESS.getResult());
    }

    @Override
    public void updateAuditStatus(Long auditableId, Integer auditStatus) {
        // 释放盘点占用批次库存
        stocktakeMapper.updateById(StocktakeDO.builder().id(auditableId).auditStatus(auditStatus).stocktakeStatus(StocktakeStatusEnum.ENDED.getValue()).build());
    }

    @Override
    public String getProcessDefinitionKey() {
        return PROCESS_DEFINITION_KEY;
    }

    @Override
    public StocktakeRespVO getStocktake(StocktakeDetailReq stocktakeDetailReq) {
        Long stocktakeId = Objects.nonNull(stocktakeDetailReq.getProcessInstanceId()) ? bpmProcessInstanceApi.selectAuditAbleIdByProcessInstanceId(stocktakeDetailReq.getProcessInstanceId()) : stocktakeDetailReq.getStocktakeId();
        if (Objects.isNull(stocktakeId)) {
            logger.error("[仓储管理-盘点单]未获取到仓储管理-盘点单id");
            return null;
        }
        StocktakeDO stocktakeDO = stocktakeMapper.selectById(stocktakeId);
        if (stocktakeDO == null) {
            return null;
        }
        StocktakeRespVO stocktakeRespVO = BeanUtils.toBean(stocktakeDO, StocktakeRespVO.class);

        LambdaQueryWrapper<StocktakeItemDO> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(StocktakeItemDO::getStocktakeId, stocktakeId);
        List<StocktakeItemDO> stocktakeItemDOS = stocktakeItemMapper.selectList(queryWrapper);
        if (!CollectionUtils.isEmpty(stocktakeItemDOS)) {
            List<StocktakeItemRespVO> stockNoticeItemRespVOList = BeanUtils.toBean(stocktakeItemDOS, StocktakeItemRespVO.class);

            List<Long> skuIdSet = stockNoticeItemRespVOList.stream().map(StocktakeItemRespVO::getSkuId).distinct().collect(Collectors.toList());
            Map<Long, SkuDTO> skuDTOMap = skuApi.getSkuDTOMap(skuIdSet);

            stockNoticeItemRespVOList.forEach(x -> {
                SkuDTO skuDTO = skuDTOMap.get(x.getSkuId());
                String skuCode = "";
                SimpleFile simpleFile = new SimpleFile().setFileUrl("");
                if (ObjectUtil.isNotNull(skuDTO)) {
                    skuCode = skuDTO.getCode();
                    List<SimpleFile> pictureList = skuDTO.getPicture();
                    simpleFile = pictureList.stream().filter(picture -> BooleanEnum.YES.getValue().equals(picture.getMainFlag())).findFirst().orElse(null);
                }
                x.setSkuCode(skuCode);
                x.setMainPicture(simpleFile);

                Integer stockQuantity = x.getStockQuantity();
                Integer stocktakeStockQuantity = x.getStocktakeStockQuantity();
                int diffStock = NumberUtil.sub(stocktakeStockQuantity, stockQuantity).intValue();
                x.setDiffQuantity(diffStock);
                Integer stocktakeResult = StocktakeResultEnum.BE_EQUAL.getValue();
                if (diffStock > 0) {
                    stocktakeResult = StocktakeResultEnum.SURPLUS.getValue();
                }else if (diffStock < 0) {
                    stocktakeResult = StocktakeResultEnum.DEFICIT.getValue();
                }
                x.setStocktakeStockResult(stocktakeResult);
            });
            stocktakeRespVO.setStocktakeItemRespVOList(stockNoticeItemRespVOList);
        }
        String bpmProcessInstanceId = bpmProcessInstanceApi.getBpmProcessInstanceId(stocktakeId, PROCESS_DEFINITION_KEY);
        if (StrUtil.isNotEmpty(bpmProcessInstanceId)) {
            stocktakeRespVO.setProcessInstanceId(bpmProcessInstanceId);
        }

        return stocktakeRespVO;
    }

    @Override
    public PageResult<StocktakeRespVO> getStocktakePage(StocktakePageReqVO pageReqVO) {
        if (StrUtil.isNotEmpty(pageReqVO.getBasicSkuCode()) || StrUtil.isNotEmpty(pageReqVO.getCskuCode()) || StrUtil.isNotEmpty(pageReqVO.getSkuCode())){
            List<StocktakeItemDO> stocktakeItemDOList = stocktakeItemMapper.selectList(new LambdaQueryWrapperX<StocktakeItemDO>().likeIfPresent(StocktakeItemDO::getCskuCode, pageReqVO.getCskuCode()).likeIfPresent(StocktakeItemDO::getBasicSkuCode, pageReqVO.getBasicSkuCode()).likeIfPresent(StocktakeItemDO::getSkuCode, pageReqVO.getSkuCode()));
            if (CollUtil.isEmpty(stocktakeItemDOList)) {
                return PageResult.empty();
            }
            pageReqVO.setIdList(stocktakeItemDOList.stream().map(StocktakeItemDO::getStocktakeId).distinct().toList());
        }
        PageResult<StocktakeDO> stocktakeDOPageResult = stocktakeMapper.selectPage(pageReqVO);
        List<StocktakeRespVO> stocktakeRespVOList = new ArrayList<>();

        List<StocktakeDO> list = stocktakeDOPageResult.getList();
        if (!CollectionUtils.isEmpty(list)) {
            stocktakeRespVOList = BeanUtils.toBean(list, StocktakeRespVO.class);
            stocktakeRespVOList.forEach(x->{
                String bpmProcessInstanceId = bpmProcessInstanceApi.getBpmProcessInstanceId(x.getId(), PROCESS_DEFINITION_KEY);
                if (StrUtil.isNotEmpty(bpmProcessInstanceId)) {
                    x.setProcessInstanceId(bpmProcessInstanceId);
                }
            });
        }
        return new PageResult<>(stocktakeRespVOList, stocktakeDOPageResult.getTotal());
    }

    // ==================== 子表（仓储管理-盘点单-明细） ====================

    @Override
    public PageResult<StocktakeItemDO> getStocktakeItemPage(PageParam pageReqVO, Long stocktakeId) {
        return stocktakeItemMapper.selectPage(pageReqVO, stocktakeId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createStocktakeItem(StocktakeItemDO stocktakeItem) {
        stocktakeItemMapper.insert(stocktakeItem);
        return stocktakeItem.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStocktakeItem(StocktakeItemDO stocktakeItem) {
        // 校验存在
        validateStocktakeItemExists(stocktakeItem.getId());
        // 更新
        stocktakeItemMapper.updateById(stocktakeItem);
    }

    @Override
    public void deleteStocktakeItem(Long id) {
        // 校验存在
        validateStocktakeItemExists(id);
        // 删除
        stocktakeItemMapper.deleteById(id);
    }

    @Override
    public StocktakeItemDO getStocktakeItem(Long id) {
        return stocktakeItemMapper.selectById(id);
    }

    @Override
    public boolean counting(StocktakeSaveReqVO updateReqVO) {
        Long id = updateReqVO.getId();
        // 更新
        StocktakeRespVO stocktakeRespVO = this.getStocktake(new StocktakeDetailReq().setStocktakeId(id));

        StocktakeDO stocktakeDO = BeanUtils.toBean(stocktakeRespVO, StocktakeDO.class);
        stocktakeDO.setStocktakeStatus(StocktakeStatusEnum.IN_PROGRESS.getValue());
        boolean stocktakeFlag = stocktakeMapper.updateById(stocktakeDO) > 0;
        // 更新子表
        List<StocktakeItemRespVO> stocktakeItemRespVOList = stocktakeRespVO.getStocktakeItemRespVOList();
        List<StocktakeItemSaveReqVO> saveReqVOList = BeanUtils.toBean(stocktakeItemRespVOList, StocktakeItemSaveReqVO.class);
        boolean stocktakeItemFlag = updateStocktakeItemList(id, saveReqVOList);
        // 锁定批次库存，禁止出库及占用
        return stocktakeFlag && stocktakeItemFlag;
    }

    @Override
    public boolean complete(StocktakeSaveReqVO updateReqVO) {
        // 更新
        StocktakeDO updateObj = BeanUtils.toBean(updateReqVO, StocktakeDO.class);
        boolean stocktakeFlag = stocktakeMapper.updateById(updateObj) > 0;
        // 更新子表
        boolean stocktakeItemFlag = updateStocktakeItemList(updateReqVO.getId(), updateReqVO.getItemSaveReqVOList());
        return stocktakeFlag && stocktakeItemFlag;
    }

    @Override
    public void exportExcel(Long id, String reportCode, HttpServletResponse response) {
        if (id == null) {
            throw exception(DECLARATION_NOT_EXISTS);
        }
        if (reportCode == null) {
            throw exception(REPORTCODE_NULL);
        }
        StocktakeDO stocktakeDO = stocktakeMapper.selectById(id);
        if (stocktakeDO == null) {
            throw exception(STOCKTAKE_ITEM_NOT_EXISTS);
        }
        ReportDTO reportDTO = null;
        if (reportDTO == null) {
            reportDTO = reportApi.getReport(reportCode);
        }
        if (reportDTO == null) {
            throw exception(REPORT_NULL, "reportCode-" + reportCode);
        }
        // 已在审核中的数据不允许修改
        if (!BpmProcessInstanceResultEnum.APPROVE.getResult().equals(reportDTO.getAuditStatus())) {
            throw exception(REPORT_NOT_APPROVE);
        }

        List<StocktakeItemDO> stocktakeItemDOList = stocktakeItemMapper.selectList(new LambdaQueryWrapperX<StocktakeItemDO>().eq(StocktakeItemDO::getStocktakeId, id));
        List<Long> skuIdList = stocktakeItemDOList.stream().map(StocktakeItemDO::getSkuId).distinct().toList();
        Map<Long, SimpleSkuDTO>  simpleSkuDTOMap= skuApi.getSimpleSkuDTOMap(skuIdList);
        Map<Long, List<StockDTO>> StockDTOListMap = stockApi.getStockBySkuIdList(skuIdList);
        List<StockTakeItemExportVO> stockTakeItemExportVOList = new ArrayList<>();
        stocktakeItemDOList.forEach(item -> {
            StockTakeItemExportVO stockTakeItemExportVO = BeanUtils.toBean(item, StockTakeItemExportVO.class);
            if(Objects.nonNull(item.getSkuId())){
                SimpleSkuDTO simpleSkuDTO = simpleSkuDTOMap.get(item.getSkuId());
                if(Objects.nonNull(simpleSkuDTO)){
                    if(Objects.nonNull(simpleSkuDTO.getMainPicture())){
                        String inputPath = simpleSkuDTO.getMainPicture().getFileUrl();
                        try {
                            byte[] content = fileApi.getFileContent(inputPath.substring(inputPath.lastIndexOf("get/") + 4));
                            File inputFile = FileUtils.createTempFile(content);
                            BufferedImage image = ImageIO.read(inputFile);
                            Double width = Double.valueOf(image.getWidth());
                            Double height = Double.valueOf(image.getHeight());
                            WriteCellData<Void> voidWriteCellData = ExcelUtils.imageCells(content,width,height,2.0,2.0,0,0);
                            stockTakeItemExportVO.setCptp(voidWriteCellData);
                            inputFile.delete();
                        } catch (Exception e) {
                            logger.info("盘点单导出图片获取失败"+e.getMessage());
                        }
                    }
                }
                List<StockDTO> StockDTOList =  StockDTOListMap.get(item.getSkuId());
                if(!CollectionUtils.isEmpty(StockDTOList) && StockDTOList.get(0).getReceiptTime()!=null){
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    stockTakeItemExportVO.setReceiptTime(dtf.format(StockDTOList.get(0).getReceiptTime()));
                }
            }
            stockTakeItemExportVOList.add(stockTakeItemExportVO);
        });
        String inputPath = reportDTO.getAnnex().get(0).getFileUrl();
        byte[] content;
        String path = "";
        try {
            path = inputPath.substring(inputPath.lastIndexOf("get/") + 4);
            content = fileApi.getFileContent(path);
        } catch (Exception e) {
            throw exception(TEMPLETE_DOWNLOAD_FAIL, "path-" + path);
        }
        ByteArrayInputStream templateFileInputStream = new ByteArrayInputStream(content);
        try {
            ExcelUtils.writeByTemplate(response, templateFileInputStream, null, "盘点单.xlsx", stockTakeItemExportVOList, null, 600);
        } catch (IOException e) {
            throw exception(EXCEL_EXPORT_FAIL);
        }
    }

    private void validateStocktakeItemExists(Long id) {
        if (stocktakeItemMapper.selectById(id) == null) {
            throw exception(STOCKTAKE_ITEM_NOT_EXISTS);
        }
    }

    private void deleteStocktakeItemByStocktakeId(Long stocktakeId) {
        stocktakeItemMapper.deleteByStocktakeId(stocktakeId);
    }
}
