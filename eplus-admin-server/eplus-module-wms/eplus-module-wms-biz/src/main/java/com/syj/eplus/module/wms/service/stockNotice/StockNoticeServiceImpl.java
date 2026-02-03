package com.syj.eplus.module.wms.service.stockNotice;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.operatelog.core.util.OperateLogUtils;
import cn.iocoder.yudao.module.bpm.api.task.BpmProcessInstanceApi;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import cn.iocoder.yudao.module.bpm.enums.task.BpmProcessInstanceResultEnum;
import cn.iocoder.yudao.module.infra.api.file.FileApi;
import com.syj.eplus.module.infra.api.company.CompanyApi;
import com.syj.eplus.module.infra.api.company.dto.SimpleCompanyDTO;
import cn.iocoder.yudao.module.system.api.dept.DeptApi;
import cn.iocoder.yudao.module.system.api.dept.dto.DeptRespDTO;
import cn.iocoder.yudao.module.system.api.notify.NotifyMessageSendApi;
import cn.iocoder.yudao.module.system.api.notify.dto.NotifySendSingleToUserReqDTO;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.deepoove.poi.data.*;
import com.syj.eplus.framework.common.dict.BusinessNameDict;
import com.syj.eplus.framework.common.dict.CalculationDict;
import com.syj.eplus.framework.common.entity.CreatedResponse;
import com.syj.eplus.framework.common.entity.JsonWeight;
import com.syj.eplus.framework.common.entity.StockLockSaveReqVO;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.framework.common.enums.*;
import com.syj.eplus.framework.common.util.NumberFormatUtil;
import com.syj.eplus.framework.common.util.VolumeUtil;
import com.syj.eplus.module.dms.enums.api.ShipmentApi;
import com.syj.eplus.module.dms.enums.api.dto.ShipmentDTO;
import com.syj.eplus.module.infra.api.CodeGeneratorApi;
import com.syj.eplus.module.infra.api.orderlink.OrderLinkApi;
import com.syj.eplus.module.infra.api.orderlink.dto.OrderLinkDTO;
import com.syj.eplus.module.pms.api.sku.SkuApi;
import com.syj.eplus.module.scm.api.purchasecontract.PurchaseContractApi;
import com.syj.eplus.module.scm.api.vender.VenderApi;
import com.syj.eplus.module.scm.api.vender.dto.SimpleVenderRespDTO;
import com.syj.eplus.module.scm.api.vender.dto.VenderPocDTO;
import com.syj.eplus.module.sms.api.SaleContractApi;
import com.syj.eplus.module.sms.api.dto.SaleContractItemDTO;
import com.syj.eplus.module.system.api.report.ReportApi;
import com.syj.eplus.module.system.api.report.dto.ReportDTO;
import com.syj.eplus.module.wms.api.stock.dto.BillItemSaveReqVO;
import com.syj.eplus.module.wms.api.stock.dto.BillSaveReqVO;
import com.syj.eplus.module.wms.api.warehouse.IWarehouseApi;
import com.syj.eplus.module.wms.api.warehouse.dto.WarehouseDTO;
import com.syj.eplus.module.wms.controller.admin.stockNotice.vo.StockNoticeItemExportVO;
import com.syj.eplus.module.wms.controller.admin.stockNotice.vo.StockNoticePageReqVO;
import com.syj.eplus.module.wms.controller.admin.stockNotice.vo.StockNoticeRespVO;
import com.syj.eplus.module.wms.controller.admin.stockNotice.vo.StockNoticeSaveReqVO;
import com.syj.eplus.module.wms.controller.admin.stockNoticeItem.vo.StockNoticeItemRespVO;
import com.syj.eplus.module.wms.convert.stockNotice.StockNoticeConvert;
import com.syj.eplus.module.wms.dal.dataobject.bill.BillDO;
import com.syj.eplus.module.wms.dal.dataobject.bill.BillItemDO;
import com.syj.eplus.module.wms.dal.dataobject.stock.StockDO;
import com.syj.eplus.module.wms.dal.dataobject.stockNotice.StockNoticeDO;
import com.syj.eplus.module.wms.dal.dataobject.stockNotice.StockNoticeItemDO;
import com.syj.eplus.module.wms.dal.mysql.stock.StockMapper;
import com.syj.eplus.module.wms.dal.mysql.stockNotice.StockNoticeItemMapper;
import com.syj.eplus.module.wms.dal.mysql.stockNotice.StockNoticeMapper;
import com.syj.eplus.module.wms.enums.*;
import com.syj.eplus.module.wms.service.bill.BillService;
import com.syj.eplus.module.wms.service.stock.StockService;
import com.syj.eplus.module.wms.service.stocklock.StockLockService;
import com.syj.eplus.module.wms.util.BatchCodeUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;
import static com.syj.eplus.module.crm.enums.ErrorCodeConstants.ANTI_AUDIT_EXCEPT;
import static com.syj.eplus.module.scm.enums.ErrorCodeConstants.*;
import static com.syj.eplus.module.wms.enums.ErrorCodeConstants.*;

/**
 * 仓储管理-入(出)库通知单 Service 实现类
 *
 * @author ePlus
 */
@Service
@Validated
public class StockNoticeServiceImpl extends ServiceImpl<StockNoticeMapper, StockNoticeDO> implements StockNoticeService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private StockNoticeMapper stockNoticeMapper;
    @Resource
    private StockNoticeItemMapper stockNoticeItemMapper;
    @Resource
    private CodeGeneratorApi codeGeneratorApi;
    @Resource
    private AdminUserApi adminUserApi;
    @Resource
    private SkuApi skuApi;
    @Resource
    private BillService billService;
    @Resource
    private OrderLinkApi orderLinkApi;

    @Resource
    private ReportApi reportApi;

    @Resource
    private FileApi fileApi;

    @Resource
    private SaleContractApi saleContractApi;

    @Resource
    private ShipmentApi shipmentApi;

    @Resource
    private VenderApi venderApi;

    @Resource
    private IWarehouseApi warehouseApi;

    @Resource
    private NotifyMessageSendApi notifyMessageSendApi;

    @Resource
    private CompanyApi companyApi;
    @Resource
    private DeptApi deptApi;
    @Resource
    private StockLockService  stockLockService;
    @Resource
    private StockService stockService;
    @Resource
    private BatchCodeUtil batchCodeUtil;
    @Resource
    @Lazy
    private PurchaseContractApi purchaseContractApi;

    private static final String SN_TYPE = "SN_NOTICE";

    private static final String IN_PROCESS_DEFINITION_KEY = "wms_in_stock_notice";
    private static final String OUT_PROCESS_DEFINITION_KEY = "wms_out_stock_notice";
    // 入库通知单单号规则
    private static final String RE_CODE_PREFIX = "RE";
    // 出库通知单单号规则
    private static final String TT_CODE_PREFIX = "TT";
    private static final String OPERATOR_EXTS_KEY = "stockNotices";
    @Autowired
    private StockMapper stockMapper;
    @Resource
    private BpmProcessInstanceApi bpmProcessInstanceApi;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public CreatedResponse createNotice(StockNoticeSaveReqVO createReqVO) {
        List<StockNoticeItemDO> noticeItems = createReqVO.getNoticeItems();
        if (CollectionUtils.isEmpty(noticeItems)) {
            throw exception(RENOTICE_ITEM_NOT_EMPTY);
        }
        noticeItems.forEach(s -> s.setUniqueCode(IdUtil.fastSimpleUUID()));
        StockNoticeDO notice = StockNoticeConvert.INSTANCE.convertNoticeDO(createReqVO);
        // 生成 仓储管理-入(出)库通知单 编号
        String CODE_PREFIX = "";
        if (createReqVO.getNoticeType() == StockTypeEnum.IN_STOCK.getValue()) {
            CODE_PREFIX = RE_CODE_PREFIX;
        } else {
            CODE_PREFIX = TT_CODE_PREFIX;
        }
        notice.setCode(codeGeneratorApi.getCodeGenerator(SN_TYPE, CODE_PREFIX));
        // 默认未转  出运明细转工厂出库自动生成出库单
        if (BooleanEnum.YES.getValue().equals(createReqVO.getFactoryOutboundFlag())){
            notice.setNoticeStatus(NoticeStatusEnum.CONVERTED.getValue());
        }else {
            notice.setNoticeStatus(NoticeStatusEnum.UN_CONVERT.getValue());
        }
        // 通知时间默认当前时间
        notice.setNoticeTime(LocalDateTime.now());
        // 打印信息
        notice.setPrintFlag(BooleanEnum.NO.getValue());
        notice.setPrintTimes(BigDecimal.ZERO.intValue());
        // 写入链路编号
        if (StockTypeEnum.IN_STOCK.getValue().equals(notice.getNoticeType())) {
            List<String> linkCodeList = noticeItems.stream().map(StockNoticeItemDO::getLinkCodeList).filter(CollUtil::isNotEmpty).flatMap(List::stream).toList();
            notice.setLinkCodeList(linkCodeList);
        }

        // 插入
        stockNoticeMapper.insert(notice);
        // 插入子表
        createNoticeItemList(notice.getId(), noticeItems);
        // 锁定库存
        stockLockOutNotice(notice,noticeItems);
        if (BooleanEnum.YES.getValue().equals(createReqVO.getFactoryOutboundFlag())){
            toBill(notice.getId(),ShippedAddressEnum.FACTORY.getValue());
        }
        if (BooleanEnum.YES.getValue().equals(createReqVO.getSubmitFlag())&&BooleanEnum.YES.getValue().equals(createReqVO.getManualFlag())){
            submitTask(notice.getId(),getLoginUserId());
        }
        // 插入订单链路
        boolean inStockFlag = StockTypeEnum.IN_STOCK.getValue().equals(notice.getNoticeType());
        createOrderLink(noticeItems, notice, inStockFlag);
        //发送站内信给采购员和跟单员
        List<Long> purchaserIds = noticeItems.stream().filter(s -> s.getPurchaserId() != null).map(StockNoticeItemDO::getPurchaserId).distinct().toList();
        List<Long> sendUserIds = new ArrayList<>(purchaserIds);
        List<UserDept> manager = noticeItems.stream().filter(s -> s.getManager() != null && s.getManager().getUserId() != null).map(StockNoticeItemDO::getManager).distinct().toList();
        if (CollUtil.isNotEmpty(manager)) {
            manager.forEach(m -> {
                if (!sendUserIds.contains(m.getUserId())) {
                    sendUserIds.add(m.getUserId());
                }
            });
        }
        if (CollUtil.isNotEmpty(sendUserIds)) {
            sendUserIds.forEach(s -> {
                NotifySendSingleToUserReqDTO sendDto = new NotifySendSingleToUserReqDTO();
                sendDto.setTemplateCode("001");
                sendDto.setUserId(s);
                Map<String, Object> templateParams = new HashMap<>();
                templateParams.put("code", notice.getCode());
                List<StockNoticeItemDO> noticeItemsTmp = noticeItems.stream().filter(ni -> s.equals(ni.getPurchaserId()) || (Objects.nonNull(ni.getManager()) && s.equals(ni.getManager().getUserId()))).distinct().toList();
                if (CollUtil.isNotEmpty(noticeItemsTmp)) {
                    List<String> skuNameList = noticeItemsTmp.stream().map(StockNoticeItemDO::getSkuName).distinct().toList();
                    templateParams.put("skuNames", String.join(",", skuNameList));
                    sendDto.setTemplateParams(templateParams);
                    notifyMessageSendApi.sendSingleMessageToAdmin(sendDto);
                }
            });
        }
        return new CreatedResponse(notice.getId(), notice.getCode());
    }

    /**
     * 出库通知单锁定库存
     * @param notice 出库通知单
     * @param noticeItems 出库通知单子表
     */
    public void stockLockOutNotice(StockNoticeDO notice,List<StockNoticeItemDO> noticeItems){
        // 手动创建的出库通知单锁定库存
        if (BooleanEnum.YES.getValue().equals(notice.getManualFlag())&&StockTypeEnum.OUT_STOCK.getValue().equals(notice.getNoticeType())){
            List<StockLockSaveReqVO> stockLockSaveReqVOList = noticeItems.stream().map(s -> {
                StockLockSaveReqVO stockLockSaveReqVO = new StockLockSaveReqVO();
                stockLockSaveReqVO.setCompanyId(notice.getCompanyId());
                stockLockSaveReqVO.setCompanyName(notice.getCompanyName());
                stockLockSaveReqVO.setLockQuantity(s.getOrderQuantity());
                stockLockSaveReqVO.setBatchCode(s.getBatchCode());
                stockLockSaveReqVO.setSourceOrderType(StockLockSourceTypeEnum.OUT_STOCK.getValue());
                stockLockSaveReqVO.setSourceOrderLockedQuantity(s.getOrderQuantity());
                stockLockSaveReqVO.setSourceOrderCode(notice.getCode());
                stockLockSaveReqVO.setSkuCode(s.getSkuCode());
                stockLockSaveReqVO.setStockId(s.getStockId());
                return stockLockSaveReqVO;
            }).collect(Collectors.toList());
            // 锁定库存
            stockLockService.batchCreateStockLock(stockLockSaveReqVOList);
        }
    }

    /**
     * 写入订单链路
     *
     * @param noticeItems 通知单明细
     * @param notice      通知单
     */
    private void createOrderLink(List<StockNoticeItemDO> noticeItems, StockNoticeDO notice, boolean inStockFlag) {
        List<OrderLinkDTO> orderLinkDTOList = new ArrayList<>();
        if (inStockFlag) {
            orderLinkDTOList = noticeItems.stream().map(s -> {
                List<String> linkCodeList = s.getLinkCodeList();
                if (CollUtil.isNotEmpty(linkCodeList)) {
                    return linkCodeList.stream().map(x -> new OrderLinkDTO()
                            .setBusinessId(notice.getId())
                            .setCode(notice.getCode())
                            .setName(BusinessNameDict.STOCK_IN_NOTICE_NAME)
                            .setType(OrderLinkTypeEnum.STOCK_IN_NOTICE_NAME.getValue())
                            .setLinkCode(x)
                            .setOrderMsg(notice)
                            .setItem(noticeItems)
                            .setParentCode(s.getPurchaseContractCode())
                            .setStatus(notice.getNoticeStatus())
                            .setCompanyId(notice.getCompanyId())).toList();
                }
                return null;
            }).filter(CollUtil::isNotEmpty).flatMap(List::stream).collect(Collectors.toList());
        } else {
            List<String> linkCodeList = notice.getLinkCodeList();
            if (CollUtil.isNotEmpty(linkCodeList)) {
                orderLinkDTOList = linkCodeList.stream().map(s -> new OrderLinkDTO()
                        .setBusinessId(notice.getId())
                        .setCode(notice.getCode())
                        .setName(BusinessNameDict.STOCK_OUT_NOTICE_NAME)
                        .setType(OrderLinkTypeEnum.CABINET_NOTICE.getValue())
                        .setLinkCode(s)
                        .setOrderMsg(notice)
                        .setItem(noticeItems)
                        .setParentCode(notice.getShipmentCode())
                        .setStatus(notice.getNoticeStatus())
                        .setCompanyId(notice.getCompanyId())
                        .setOrderMsg(notice)).toList();
            }
        }

        if (CollUtil.isNotEmpty(orderLinkDTOList)) {
            orderLinkApi.batchCreateOrderLink(orderLinkDTOList);
        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateNotice(StockNoticeSaveReqVO updateReqVO) {
        // 校验存在
        validateNoticeExists(updateReqVO.getId());
        // 更新
        StockNoticeDO updateObj = StockNoticeConvert.INSTANCE.convertNoticeDO(updateReqVO);
        stockNoticeMapper.updateById(updateObj);
        List<StockNoticeItemDO> noticeItems = updateReqVO.getNoticeItems();
        // 释放锁定库存
        stockLockService.cancelOutNoticeLockQuantity(StockLockSourceTypeEnum.OUT_STOCK.getValue(), updateReqVO.getCode());
        // 重新锁定库存
        stockLockOutNotice(updateObj, noticeItems);
        // 更新子表
        updateNoticeItemList(updateReqVO.getId(), noticeItems);
        if (BooleanEnum.YES.getValue().equals(updateObj.getManualFlag())&&BooleanEnum.YES.getValue().equals(updateReqVO.getSubmitFlag())){
            submitTask(updateReqVO.getId(), getLoginUserId());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteNotice(Long id) {
        // 校验存在
        validateNoticeExists(id);
        // 删除
        stockNoticeMapper.deleteById(id);

        // 删除子表
        deleteNoticeItemByNoticeId(id);
    }

    private StockNoticeDO validateNoticeExists(Long id) {
        StockNoticeDO stockNoticeDO = stockNoticeMapper.selectById(id);
        if (stockNoticeDO == null) {
            throw exception(RENOTICE_NOT_EXISTS);
        }
        return stockNoticeDO;
    }

    @Override
    public StockNoticeRespVO getNotice(Long id,String processInstanceId) {
        StockNoticeDO stockNoticeDO;
        if (Objects.nonNull(id)){
            stockNoticeDO= stockNoticeMapper.selectById(id);
        }else if (StrUtil.isNotEmpty(processInstanceId)){
            stockNoticeDO= stockNoticeMapper.selectOne(new LambdaQueryWrapper<StockNoticeDO>().eq(StockNoticeDO::getProcessInstanceId, processInstanceId));
        }else {
            return null;
        }

        if (stockNoticeDO == null) {
            return null;
        }
        StockNoticeRespVO stockNoticeRespVO = StockNoticeConvert.INSTANCE.convertNoticeRespVO(stockNoticeDO);
        UserDept creatUserInfo = adminUserApi.getUserDeptByUserId(Long.parseLong(stockNoticeDO.getCreator()));
        if (Objects.nonNull(creatUserInfo)) {
            stockNoticeRespVO.setCreateUser(creatUserInfo);
        }
        LambdaQueryWrapper<StockNoticeItemDO> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(StockNoticeItemDO::getNoticeId, id);
        List<StockNoticeItemDO> stockNoticeItemDOList = stockNoticeItemMapper.selectList(queryWrapper);
        if (!CollectionUtils.isEmpty(stockNoticeItemDOList)) {
            List<StockNoticeItemRespVO> stockNoticeItemRespVOList = BeanUtils.toBean(stockNoticeItemDOList, StockNoticeItemRespVO.class);
            stockNoticeRespVO.setStockNoticeItemRespVOList(stockNoticeItemRespVOList);
            Set<String> batchCodeSet = stockNoticeItemDOList.stream().map(StockNoticeItemDO::getBatchCode).collect(Collectors.toSet());
            List<StockDO> stockDOS = stockMapper.selectList(StockDO::getBatchCode, batchCodeSet);
            if (CollUtil.isNotEmpty(stockDOS)){
                Map<String, Integer> availableQuantityMap = stockDOS.stream().collect(Collectors.toMap(StockDO::getBatchCode, s -> Objects.isNull(s.getAvailableQuantity()) ? 0 : s.getAvailableQuantity()-(Objects.isNull(s.getLockQuantity())?0:s.getLockQuantity())));
                stockNoticeItemRespVOList.forEach(s->s.setOutQuantity(availableQuantityMap.get(s.getBatchCode())));
            }
        }
        return stockNoticeRespVO;
    }

    @Override
    public PageResult<StockNoticeRespVO> getNoticePage(StockNoticePageReqVO pageReqVO) {
        if (Objects.nonNull(pageReqVO.getSaleContractCodeList()) || StringUtils.isNotEmpty(pageReqVO.getSaleContractCode()) || StringUtils.isNotEmpty(pageReqVO.getPurchaseContractCode()) || StringUtils.isNotEmpty(pageReqVO.getVenderName()) || Objects.nonNull(pageReqVO.getPurchaserId()) || Objects.nonNull(pageReqVO.getPurchaserDeptId())) {
            LambdaQueryWrapper<StockNoticeItemDO> queryWrapper = new LambdaQueryWrapper();
            if (Objects.nonNull(pageReqVO.getSaleContractCode())) {
                queryWrapper.like(StockNoticeItemDO::getSaleContractCode, pageReqVO.getSaleContractCode());
            }
            if (Objects.nonNull(pageReqVO.getSaleContractCodeList())) {
                queryWrapper.in(StockNoticeItemDO::getSaleContractCode, pageReqVO.getSaleContractCodeList());
            }
            if (Objects.nonNull(pageReqVO.getPurchaseContractCode())) {
                queryWrapper.like(StockNoticeItemDO::getPurchaseContractCode, pageReqVO.getPurchaseContractCode());
            }
            if (Objects.nonNull(pageReqVO.getVenderName())) {
                queryWrapper.like(StockNoticeItemDO::getVenderName, pageReqVO.getVenderName());
            }
            if (Objects.nonNull(pageReqVO.getBasicSkuCode())) {
                queryWrapper.like(StockNoticeItemDO::getBasicSkuCode, pageReqVO.getBasicSkuCode());
            }
            if (Objects.nonNull(pageReqVO.getSkuCode())) {
                queryWrapper.like(StockNoticeItemDO::getSkuCode, pageReqVO.getSkuCode());
            }
            if (Objects.nonNull(pageReqVO.getCskuCode())) {
                queryWrapper.like(StockNoticeItemDO::getCskuCode, pageReqVO.getCskuCode());
            }
            if (Objects.nonNull(pageReqVO.getPurchaserId())) {
                queryWrapper.eq(StockNoticeItemDO::getPurchaserId, pageReqVO.getPurchaserId());
            }
            if (Objects.nonNull(pageReqVO.getPurchaserDeptId())) {
                queryWrapper.eq(StockNoticeItemDO::getPurchaserDeptId, pageReqVO.getPurchaserDeptId());
            }
            List<StockNoticeItemDO> stockNoticeItemDOList = stockNoticeItemMapper.selectList(queryWrapper);
            pageReqVO.setIdList(stockNoticeItemDOList.stream().map(StockNoticeItemDO::getNoticeId).distinct().toList());
        }
        if ((Objects.nonNull(pageReqVO.getSaleContractCodeList()) || StringUtils.isNotEmpty(pageReqVO.getSaleContractCode()) || StringUtils.isNotEmpty(pageReqVO.getPurchaseContractCode()) || StringUtils.isNotEmpty(pageReqVO.getVenderName()) || Objects.nonNull(pageReqVO.getPurchaserId()) || Objects.nonNull(pageReqVO.getPurchaserDeptId())) && CollectionUtils.isEmpty(pageReqVO.getIdList())) {
            return PageResult.empty();
        }

        PageResult<StockNoticeDO> stockNoticeDOPageResult = stockNoticeMapper.selectPage(pageReqVO);

        List<StockNoticeRespVO> stockNoticeRespVOList = new ArrayList<>();
        List<StockNoticeDO> noticeDOPageResultList = stockNoticeDOPageResult.getList();
        if (!CollectionUtils.isEmpty(noticeDOPageResultList)) {
            noticeDOPageResultList.forEach(x -> {
                StockNoticeRespVO stockNoticeRespVO = BeanUtils.toBean(x, StockNoticeRespVO.class);
                LambdaQueryWrapper<StockNoticeItemDO> queryWrapper = new LambdaQueryWrapper();
                queryWrapper.eq(StockNoticeItemDO::getNoticeId, x.getId());
                List<StockNoticeItemDO> stockNoticeItemDOList = stockNoticeItemMapper.selectList(queryWrapper);
                if (!CollectionUtils.isEmpty(stockNoticeItemDOList)) {
                    List<StockNoticeItemRespVO> stockNoticeItemRespVOList = BeanUtils.toBean(stockNoticeItemDOList, StockNoticeItemRespVO.class);
                    List<Long> purchaserIdList = stockNoticeItemRespVOList.stream().map(StockNoticeItemRespVO::getPurchaserId).distinct().toList();
                    Map<Long, AdminUserRespDTO> purchaserMap = adminUserApi.getUserMap(purchaserIdList);
                    List<Long> purchaserDeptIdList = stockNoticeItemRespVOList.stream().map(StockNoticeItemRespVO::getPurchaserDeptId).distinct().toList();
                    Map<Long, DeptRespDTO> purchaserDeptMap = deptApi.getDeptMap(purchaserDeptIdList);
                    AtomicReference<Integer> pendingStockQuantity = new AtomicReference<>(0);
                    AtomicReference<Integer> transformStockQuantity = new AtomicReference<>(0);
                    stockNoticeItemRespVOList.forEach(y -> {
                        if (Objects.nonNull(y.getPurchaserId())) {
                            AdminUserRespDTO purchaseUserInfo = purchaserMap.get(y.getPurchaserId());
                            if (Objects.nonNull(purchaseUserInfo)) {
                                String purchaserName = purchaseUserInfo.getNickname();
                                y.setPurchaseUserName(purchaserName);
                                y.setPurchaseMobile(purchaseUserInfo.getMobile());
                            }
                        }
                        Long purchaserDeptId = y.getPurchaserDeptId();
                        if (Objects.nonNull(purchaserDeptId)) {
                            DeptRespDTO purchaserDept = purchaserDeptMap.get(purchaserDeptId);
                            y.setPurchaseUserDeptName(purchaserDept != null ? purchaserDept.getName() : "");
                        }
                        Integer tmpQuantity = ObjectUtil.isNotNull(y.getPendingStockQuantity()) ? y.getPendingStockQuantity() : y.getOrderQuantity();
                        pendingStockQuantity.updateAndGet(v -> v + tmpQuantity);
                        int transformQuantity = ObjectUtil.isNotNull(y.getPendingStockQuantity()) ? y.getOrderQuantity() - y.getPendingStockQuantity() : 0;
                        transformStockQuantity.updateAndGet(v -> v + transformQuantity);
                    });
                    stockNoticeRespVO.setPendingStockQuantity(pendingStockQuantity.get());
                    stockNoticeRespVO.setTransformStockQuantity(transformStockQuantity.get());
                    stockNoticeRespVO.setStockNoticeItemRespVOList(stockNoticeItemRespVOList);
                    stockNoticeRespVO.setChildren(stockNoticeItemRespVOList);
                }
                stockNoticeRespVOList.add(stockNoticeRespVO);
            });
        }
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalVolumeSum", stockNoticeRespVOList.stream().map(StockNoticeRespVO::getTotalVolume).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));
        return new PageResult<StockNoticeRespVO>().setList(stockNoticeRespVOList).setTotal(stockNoticeDOPageResult.getTotal()).setSummary(summary);
    }

    // ==================== 子表（仓储管理-入(出)库通知单-明细） ====================

    @Override
    public List<StockNoticeItemDO> getNoticeItemListByNoticeId(Long noticeId) {
        LambdaQueryWrapper<StockNoticeItemDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StockNoticeItemDO::getNoticeId, noticeId);
        return stockNoticeItemMapper.selectList(queryWrapper);
    }

    private List<StockNoticeItemDO> createNoticeItemList(Long noticeId, List<StockNoticeItemDO> list) {
        list.forEach(o -> {
            o.setId(null);
            o.setNoticeId(noticeId);
            o.setUniqueCode(IdUtil.fastSimpleUUID());
            o.setNoticeItemStatus(StockStatusEnum.NOT.getValue());
            o.setPendingStockQuantity(o.getOrderQuantity());
        });
        stockNoticeItemMapper.insertBatch(list);
        return list;
    }

    private void updateNoticeItemList(Long noticeId, List<StockNoticeItemDO> list) {
        deleteNoticeItemByNoticeId(noticeId);
        list.forEach(o -> o.setId(null).setUpdater(null).setUpdateTime(null)); // 解决更新情况下：1）id 冲突；2）updateTime 不更新
        createNoticeItemList(noticeId, list);
    }

    private void deleteNoticeItemByNoticeId(Long noticeId) {
        LambdaQueryWrapper<StockNoticeItemDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StockNoticeItemDO::getNoticeId, noticeId);
        stockNoticeItemMapper.delete(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<CreatedResponse> toBill(Long id,Integer shippingAddress) {
        StockNoticeRespVO stockNoticeRespVO = this.getNotice(id,null);
        if (stockNoticeRespVO.getNoticeStatus().intValue() == NoticeStatusEnum.IN_CONVERT.getValue()&&ShippedAddressEnum.BILL.getValue().equals(shippingAddress)) {
            throw exception(BILL_IN_CONVERT);
        }
        //计算已出运数量对应的金额
        calcShipmentPrice(stockNoticeRespVO);
        BillSaveReqVO billSaveReqVO = BeanUtils.toBean(stockNoticeRespVO, BillSaveReqVO.class);
        if (ShippedAddressEnum.FACTORY.getValue().equals(shippingAddress)){
            billSaveReqVO.setFactoryFlag(BooleanEnum.YES.getValue());
        }
        Integer billType = stockNoticeRespVO.getNoticeType();
        billSaveReqVO.setId(null);
        billSaveReqVO.setCode(null);
        billSaveReqVO.setNoticeCode(stockNoticeRespVO.getCode());
        billSaveReqVO.setBillType(billType);
        if (CollUtil.isNotEmpty(stockNoticeRespVO.getSaleContractCodeList())&&stockNoticeRespVO.getSaleContractCodeList().size()==1){
            billSaveReqVO.setSaleContractCode(stockNoticeRespVO.getSaleContractCodeList().get(0));
        }
        billSaveReqVO.setBillTime(LocalDateTime.now());
        if (StockTypeEnum.OUT_STOCK.getValue().equals(billType)) {
            Integer sourceType = BooleanEnum.YES.getValue().equals(stockNoticeRespVO.getIsContainerTransportation()) ? OutStockSourceTypeEnum.CONTAINER_TRANSPORTATION.getType() : OutStockSourceTypeEnum.OUT_NOTICE.getType();
            billSaveReqVO.setSourceType(sourceType);
            billSaveReqVO.setSourceId(stockNoticeRespVO.getId());
            billSaveReqVO.setSourceCode(stockNoticeRespVO.getCode());
        }
        List<StockNoticeItemRespVO> stockNoticeItemRespVOList = stockNoticeRespVO.getStockNoticeItemRespVOList();
        stockNoticeItemRespVOList = stockNoticeItemRespVOList.stream().filter(x ->
                ObjectUtil.isNull(x.getPendingStockQuantity()) ||
                        (ObjectUtil.isNotNull(x.getPendingStockQuantity()) && x.getPendingStockQuantity() > 0)).toList();
        if (CollectionUtils.isEmpty(stockNoticeItemRespVOList)) {
            throw exception(BILL_ITEM_ALL_OUT);
        }
        List<BillItemSaveReqVO> billItemSaveReqVOList = new ArrayList<>();
        AtomicReference<Boolean> notConvertFlag = new AtomicReference<Boolean>(false);
        for (int i = 0; i < stockNoticeItemRespVOList.size(); i++) {
            StockNoticeItemRespVO source = stockNoticeItemRespVOList.get(i);
            // 如果是出库，并且发货地址和源发货地址互为工厂和仓库，则跳过
            if (StockTypeEnum.OUT_STOCK.getValue().equals(billType)
                    && ((ShippedAddressEnum.BILL.getValue().equals(shippingAddress) && ShippedAddressEnum.FACTORY.getValue().equals(source.getShippedAddress()))
                    || (ShippedAddressEnum.FACTORY.getValue().equals(shippingAddress) && ShippedAddressEnum.BILL.getValue().equals(source.getShippedAddress())))) {
                if (BooleanEnum.NO.getValue().equals(source.getConvertBillFlag())){
                    notConvertFlag.set(true);
                }
                continue;
            }
            BillItemSaveReqVO billItemSaveReqVO = BeanUtils.toBean(source, BillItemSaveReqVO.class);
            billItemSaveReqVO.setId(null);
            billItemSaveReqVO.setSortNum(i + 1);
            if (BooleanEnum.YES.getValue().equals(stockNoticeRespVO.getIsContainerTransportation())){
                billItemSaveReqVO.setSourceType(StockSourceTypeEnum.PURCHASE.getValue());
            }
            billItemSaveReqVO.setCompanyId(stockNoticeRespVO.getCompanyId());
            billItemSaveReqVO.setCompanyName(stockNoticeRespVO.getCompanyName());
            Integer pendingStockQuantity = source.getPendingStockQuantity();
            if (ObjectUtil.isNull(source.getQtyPerOuterbox()) || source.getQtyPerOuterbox() == 0) {
                throw exception(ErrorCodeConstants.QTY_PER_OUTERBOX_INVALID);
            }
            Integer pendingStockBoxQuantity = NumberUtil.div(pendingStockQuantity, source.getQtyPerOuterbox(), 0, RoundingMode.UP).intValue();
            if (Objects.nonNull(pendingStockQuantity) && pendingStockQuantity > 0) {
                billItemSaveReqVO.setOrderQuantity(pendingStockQuantity);
                billItemSaveReqVO.setOrderBoxQuantity(pendingStockBoxQuantity);
            }
            if (ShippedAddressEnum.FACTORY.getValue().equals(shippingAddress)){
                billItemSaveReqVO.setActQuantity(pendingStockQuantity);
                billItemSaveReqVO.setActBoxQuantity(pendingStockBoxQuantity);
            }
            if (ObjectUtil.isNull(pendingStockQuantity)) {
                billItemSaveReqVO.setPendingStockQuantity(billItemSaveReqVO.getOrderQuantity());
            }
            billItemSaveReqVO.setBillType(stockNoticeRespVO.getNoticeType());
            billItemSaveReqVO.setSourceUniqueCode(source.getUniqueCode());
            billItemSaveReqVO.setUniqueCode(IdUtil.fastSimpleUUID());
            String batchCode = source.getBatchCode();
            if (StringUtils.isNotBlank(batchCode)) {
                billItemSaveReqVO.setBatchCode(batchCode);
                List<StockDO> stockDOList = stockMapper.selectList(StockDO::getBatchCode, batchCode).stream().toList();
                if (!CollectionUtils.isEmpty(stockDOList)) {
                    if (stockDOList.size() > 1) {
                        throw exception(BATCKCODE_MUL);
                    }
                    billItemSaveReqVO.setPosition(stockDOList.get(0).getPosition());
                }
            }
            billItemSaveReqVOList.add(billItemSaveReqVO);
        }
        billSaveReqVO.setBillItemSaveReqVOList(billItemSaveReqVOList);
        StockNoticeDO stockNoticeDO = StockNoticeDO.builder().id(id).noticeStatus(NoticeStatusEnum.IN_CONVERT.getValue()).build();
        // 更新通知单状态为转单中
        if (ShippedAddressEnum.FACTORY.getValue().equals(shippingAddress)){
            if (notConvertFlag.get()){
                stockNoticeDO.setNoticeStatus(NoticeStatusEnum.PART_CONVERT.getValue());
            }else {
                stockNoticeDO.setNoticeStatus(NoticeStatusEnum.CONVERTED.getValue());
            }
        }

        stockNoticeMapper.updateById(stockNoticeDO);
//        orderLinkApi.updateOrderLinkStatus(stockNoticeDO.getCode(),BusinessNameDict.STOCK_NOTICE_NAME,stockNoticeDO.getLinkCode(),NoticeStatusEnum.CONVERTED.getValue());
        // 创建出（入）库单
        List<CreatedResponse> responses = new ArrayList<>();
        responses.add(billService.createBill(billSaveReqVO));
        return responses;
    }

    /**
     * 计算出运明细对应的金额
     *
     * @param stockNoticeRespVO 出库通知单
     */
    private void calcShipmentPrice(StockNoticeRespVO stockNoticeRespVO) {
        // 仅处理出库通知单
        if (!StockTypeEnum.OUT_STOCK.getValue().equals(stockNoticeRespVO.getNoticeType())||!BooleanEnum.YES.getValue().equals(stockNoticeRespVO.getIsContainerTransportation())) {
            return;
        }
        List<StockNoticeItemRespVO> children = stockNoticeRespVO.getStockNoticeItemRespVOList();
        List<String> saleUniqueCodeList = children.stream().map(StockNoticeItemRespVO::getSaleItemUniqueCode).filter(s -> !StringUtils.isWhitespace(s)).toList();
        Map<String, SaleContractItemDTO> itemMap = saleContractApi.getItemListByUniqueCodes(saleUniqueCodeList);
        if (CollUtil.isEmpty(itemMap)) {
            throw exception(SALE_CONTRACT_ITEM_NOT_EXISTS);
        }
        Map<String, List<StockNoticeItemRespVO>> saleContractMap = children.stream().collect(Collectors.groupingBy(StockNoticeItemRespVO::getSaleContractCode));
        if (CollUtil.isNotEmpty(saleContractMap)) {
            saleContractMap.forEach((k, v) -> {
                BigDecimal sum = BigDecimal.ZERO;
                for (StockNoticeItemRespVO child : v) {
                    SaleContractItemDTO saleContractItemDTO = itemMap.get(child.getSaleItemUniqueCode());
                    if (Objects.isNull(saleContractItemDTO)) {
                        throw exception(SALE_CONTRACT_ITEM_NOT_EXISTS);
                    }
                    Integer shippedQuantity = saleContractItemDTO.getShipmentTotalQuantity();
                    if (Objects.isNull(shippedQuantity)) {
                        shippedQuantity = 0;
                    }
                    sum = sum.add(saleContractItemDTO.getUnitPrice().getAmount().multiply(BigDecimal.valueOf(shippedQuantity)));
                }
                saleContractApi.checkCollectionPlan(k, false, sum);
            });
        }
    }

    @Override
    public Long getBillNumByPurchaseCode(Long id) {
        return billService.getBillNumByPurchaseId(id);
    }

    @Override
    public PageResult<StockNoticeRespVO> getTransportationNoticePage(StockNoticePageReqVO pageReqVO) {
        if (StrUtil.isNotEmpty(pageReqVO.getBasicSkuCode())
                || StrUtil.isNotEmpty(pageReqVO.getCskuCode())
                || StrUtil.isNotEmpty(pageReqVO.getSkuCode())
                || StrUtil.isNotEmpty(pageReqVO.getSaleContractCode())
                || StrUtil.isNotEmpty(pageReqVO.getPurchaseContractCode())
                || StrUtil.isNotEmpty(pageReqVO.getCustName())
                || StrUtil.isNotEmpty(pageReqVO.getCustPo())
                || Objects.nonNull(pageReqVO.getPurchaserDeptId())
                || Objects.nonNull(pageReqVO.getSalesDeptId())
                || CollUtil.isNotEmpty(pageReqVO.getSaleContractCodeList())) {
            LambdaQueryWrapperX<StockNoticeItemDO> queryWrapper = new LambdaQueryWrapperX<StockNoticeItemDO>()
                    .likeIfPresent(StockNoticeItemDO::getCskuCode, pageReqVO.getCskuCode())
                    .likeIfPresent(StockNoticeItemDO::getBasicSkuCode, pageReqVO.getBasicSkuCode())
                    .likeIfPresent(StockNoticeItemDO::getSkuCode, pageReqVO.getSkuCode())
                    .likeIfPresent(StockNoticeItemDO::getPurchaseContractCode, pageReqVO.getPurchaseContractCode())
                    .likeIfPresent(StockNoticeItemDO::getCustName, pageReqVO.getCustName())
                    .likeIfPresent(StockNoticeItemDO::getCustPo, pageReqVO.getCustPo())
                    .likeIfPresent(StockNoticeItemDO::getSaleContractCode, pageReqVO.getSaleContractCode())
                    .inIfPresent(StockNoticeItemDO::getSaleContractCode, pageReqVO.getSaleContractCodeList())
                    .eqIfPresent(StockNoticeItemDO::getPurchaserDeptId, pageReqVO.getPurchaserDeptId());
            
            // Filter by sales department - extract deptId from sales JSON field
            if (Objects.nonNull(pageReqVO.getSalesDeptId())) {
                queryWrapper.apply("JSON_EXTRACT(sales, '$.deptId') = {0}", pageReqVO.getSalesDeptId());
            }
            
            List<StockNoticeItemDO> stockNoticeItemDOList = stockNoticeItemMapper.selectList(queryWrapper);
            if (CollUtil.isEmpty(stockNoticeItemDOList)) {
                return PageResult.empty();
            }
            pageReqVO.setIdList(stockNoticeItemDOList.stream().map(StockNoticeItemDO::getNoticeId).distinct().toList());
        }
        PageResult<StockNoticeDO> stockNoticeDOPageResult = stockNoticeMapper.selectPage(pageReqVO);
        List<StockNoticeRespVO> stockNoticeRespVOList = new ArrayList<>();
        List<StockNoticeDO> noticeDOPageResultList = stockNoticeDOPageResult.getList();
        if (!CollectionUtils.isEmpty(noticeDOPageResultList)) {
            noticeDOPageResultList.forEach(x -> {
                StockNoticeRespVO stockNoticeRespVO = BeanUtils.toBean(x, StockNoticeRespVO.class);
                stockNoticeRespVOList.add(stockNoticeRespVO);
            });
            List<Long> idList = noticeDOPageResultList.stream().map(StockNoticeDO::getId).toList();
            List<StockNoticeItemDO> stockNoticeItemDOS = stockNoticeItemMapper.selectList(new LambdaQueryWrapper<StockNoticeItemDO>().in(StockNoticeItemDO::getNoticeId, idList));
            if (CollUtil.isEmpty(stockNoticeItemDOS)) {
                return new PageResult<StockNoticeRespVO>().setList(stockNoticeRespVOList).setTotal(stockNoticeDOPageResult.getTotal());
            }
            Map<Long, List<StockNoticeItemDO>> stockNoticeItemMap = stockNoticeItemDOS.stream().collect(Collectors.groupingBy(StockNoticeItemDO::getNoticeId));
            if (CollUtil.isEmpty(stockNoticeItemMap)) {
                return new PageResult<StockNoticeRespVO>().setList(stockNoticeRespVOList).setTotal(stockNoticeDOPageResult.getTotal());
            }
            stockNoticeRespVOList.forEach(s -> {
                Long id = s.getId();
                List<StockNoticeItemDO> items = stockNoticeItemMap.get(id);
                if (CollUtil.isNotEmpty(items)) {
                    s.setChildren(StockNoticeConvert.INSTANCE.convertStockNoticeItemByDO(items));
                    AtomicReference<Integer> pendingStockQuantityAto = new AtomicReference<>(0);
                    stockNoticeItemDOS.stream().forEach(x -> pendingStockQuantityAto.updateAndGet(v -> v + (ObjectUtil.isNotNull(x.getPendingStockQuantity()) ? x.getPendingStockQuantity() : x.getOrderQuantity())));
                    s.setPendingStockQuantity(pendingStockQuantityAto.get());
                }
            });
        }
        return new PageResult<StockNoticeRespVO>().setList(stockNoticeRespVOList).setTotal(stockNoticeDOPageResult.getTotal());
    }

    @Override
    public void containerTransportationExportExcel(Long id, String reportCode, HttpServletResponse response) {
        if (reportCode == null) {
            throw exception(REPORTCODE_NULL);
        }
        StockNoticeDO stockNoticeDO = stockNoticeMapper.selectById(id);
        if (stockNoticeDO == null) {
            throw exception(STOCKNOTICE_NOT_EXISTS);
        }
        ReportDTO reportDTO = null;
        if (stockNoticeDO.getCompanyId() != null) {
            reportDTO = reportApi.getCompanyReport(reportCode, stockNoticeDO.getCompanyId());
        }
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
        List<StockNoticeItemDO> stockNoticeItemDOList = stockNoticeItemMapper.selectList(new LambdaQueryWrapperX<StockNoticeItemDO>().eq(StockNoticeItemDO::getNoticeId, id));
        List<StockNoticeItemExportVO> StockNoticeItemExportVOList = StockNoticeConvert.INSTANCE.convertDeclarationItemExportVOList(stockNoticeItemDOList);
        StockNoticeItemExportVOList.forEach(item -> {
            if (item.getOuterboxGrossweight() != null && item.getOuterboxGrossweight().getWeight() != null) {
                item.setOuterboxGrossweightweight(NumberFormatUtil.formatWeight(item.getOuterboxGrossweight().getWeight()));
            }
            if (item.getTotalWeight() != null && item.getTotalWeight().getWeight() != null) {
                item.setTotalWeightWeight(NumberFormatUtil.formatWeight(item.getTotalWeight().getWeight()));
            }
            if (item.getOuterboxLength() != null) {
                item.setOuterboxLength(NumberFormatUtil.format(item.getOuterboxLength(),CalculationDict.TWO));
            }
            if (item.getOuterboxHeight() != null) {
                item.setOuterboxHeight(NumberFormatUtil.format(item.getOuterboxHeight(),CalculationDict.TWO));
            }
            if (item.getOuterboxWidth() != null) {
                item.setOuterboxWidth(NumberFormatUtil.format(item.getOuterboxWidth(),CalculationDict.TWO));
            }
            if (item.getTotalVolume() != null) {
                item.setTotalVolume(VolumeUtil.processVolume(item.getTotalVolume()));
            }
            if (item.getManager() != null) {
                item.setManagerName(item.getManager().getNickname());
            }
        });
        HashMap<String, Object> params = new HashMap();
        List<String> saleContractCodeList = stockNoticeDO.getSaleContractCodeList();
        if (!saleContractCodeList.isEmpty()) {
            // 去重并保持顺序
            List<String> distinctSaleContractCodeList = new ArrayList<>(new LinkedHashSet<>(saleContractCodeList));
            params.put("saleContractCode", String.join(",", distinctSaleContractCodeList));
        }
        params.put("referenceNumber", stockNoticeDO.getReferenceNumber());
        params.put("invoiceCode", stockNoticeDO.getInvoiceCode());

        //获取目的口岸
        ShipmentDTO shipmentDTO = shipmentApi.getByShipmentCode(stockNoticeDO.getShipmentCode());
        if (shipmentDTO != null) {
            params.put("destinationPortName", shipmentDTO.getDestinationPortName());
        }
        // 新增：获取公司信息并put到params
        Long companyId = stockNoticeDO.getCompanyId();
        if (companyId != null) {
            SimpleCompanyDTO company = companyApi.getCompanyDTO(companyId);
            if (company != null) {
                params.put("companyName", company.getCompanyName());
                params.put("companyNameEng", company.getCompanyNameEng());
                params.put("companyAddress", company.getCompanyAddress());
                params.put("companyAddressEng", company.getCompanyAddressEng());
                params.put("companyTel", company.getCompanyTel());
                params.put("companyFax", company.getCompanyFax());
            }
        }
        Integer orderBoxQuantitySum = StockNoticeItemExportVOList.stream()
                .map(StockNoticeItemExportVO::getOrderBoxQuantity).reduce(0, Integer::sum);
        BigDecimal totalWeightSum = StockNoticeItemExportVOList.stream()
                .map(s -> {
                    JsonWeight totalWeight = s.getTotalWeight();
                    if (Objects.isNull(totalWeight)) {
                        return null;
                    }
                    return totalWeight.getWeight();
                })
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(CalculationDict.DECIMAL_CHECK, RoundingMode.HALF_UP);
        BigDecimal totalVolumeSum = StockNoticeItemExportVOList.stream()
                .map(s -> s.getTotalVolume())
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(CalculationDict.THREE, RoundingMode.HALF_UP);
        params.put("orderBoxQuantitySum", orderBoxQuantitySum);
        params.put("totalWeightSum", totalWeightSum);
        params.put("totalVolumeSum", totalVolumeSum);
        if (ShipmentTypeEnum.SIGN_BACK_DATE.getValue().equals(stockNoticeDO.getShipmentType())) {
            params.put("dmsNoticeTitle", "进仓明细");
        }else{
            params.put("dmsNoticeTitle", "拉柜通知");
        }
        if (ShipmentTypeEnum.SIGN_BACK_DATE.getValue().equals(stockNoticeDO.getShipmentType())) {
            params.put("referenceNumberTitle", "进仓编号");
        }else{
            params.put("referenceNumberTitle", "提单号");
        }


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
            ExcelUtils.writeByTemplate(response, templateFileInputStream, params, "拉柜通知单.xlsx", StockNoticeItemExportVOList, null, 600);
        } catch (IOException e) {
            throw exception(EXCEL_EXPORT_FAIL);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void closeStockNotice(Long id) {
        StockNoticeDO stockNoticeDO = validateNoticeExists(id);
        boolean isBill = billService.checkBillByNoticeCode(stockNoticeDO.getCode());
        if (isBill) {
            throw exception(ANTI_AUDIT_EXCEPT);
        }
        if (StrUtil.isNotEmpty(stockNoticeDO.getShipmentCode())) {
            List<StockNoticeItemDO> noticeItemDOList = stockNoticeItemMapper.selectList(new LambdaQueryWrapperX<StockNoticeItemDO>().eq(StockNoticeItemDO::getNoticeId, id));
            if (CollUtil.isEmpty(noticeItemDOList)) {
                return;
            }
            Map<String, Integer> stockQuantityMap = noticeItemDOList.stream().collect(Collectors.groupingBy(StockNoticeItemDO::getBatchCode, Collectors.summingInt(StockNoticeItemDO::getOrderQuantity)));
            List<StockDO> stockDOS = stockMapper.selectList(new LambdaQueryWrapperX<StockDO>().select(StockDO::getId,StockDO::getBatchCode,StockDO::getCabinetQuantity).in(StockDO::getBatchCode, stockQuantityMap.keySet()));
            stockDOS.forEach(s->{
                if (Objects.isNull(s.getCabinetQuantity())||s.getCabinetQuantity() == 0){
                    return;
                }
                s.setCabinetQuantity(s.getCabinetQuantity() - stockQuantityMap.get(s.getBatchCode()));
            });
            stockMapper.updateBatch(stockDOS);
            List<String> uniqueCodeList = noticeItemDOList.stream().map(StockNoticeItemDO::getSourceUniqueCode).toList();
            if (CollUtil.isEmpty(uniqueCodeList)) {
                return;
            }
            // 修改出运明细加工标识
            shipmentApi.cancelConvertContainerFlag(uniqueCodeList);
            stockNoticeDO.setNoticeStatus(NoticeStatusEnum.CANCEL.getValue());
            stockNoticeMapper.updateById(stockNoticeDO);
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String print(Long id, String reportCode, Long reportId, Long companyId) {
        if (id == null) {
            throw exception(STOCKNOTICE_NOT_EXISTS);
        }
        if (reportCode == null) {
            throw exception(REPORTCODE_NULL);
        }
        StockNoticeDO stockNoticeDO = stockNoticeMapper.selectById(id);
        if (stockNoticeDO == null) {
            throw exception(STOCKNOTICE_NOT_EXISTS);
        }
        List<StockNoticeItemDO> stockNoticeItemDOList = stockNoticeItemMapper.selectList(StockNoticeItemDO::getNoticeId, id);
        if (CollUtil.isEmpty(stockNoticeItemDOList)) {
            throw exception(STOCKNOTICE_ITEM_NOT_EXISTS);
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
        HashMap<String, Object> params = new HashMap<>();
        params.put("code", stockNoticeDO.getCode());
        params.put("companyName", stockNoticeDO.getCompanyName());
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        if (Objects.nonNull(stockNoticeDO.getExpectDate())) {
            String expectDate = dtf.format(stockNoticeDO.getExpectDate());
            params.put("expectDate", expectDate);
        }
        List<Long> venderIds = stockNoticeItemDOList.stream().map(StockNoticeItemDO::getVenderId).distinct().toList();
        SimpleVenderRespDTO simpleVenderRespDTO = null;
        if (!CollectionUtils.isEmpty(venderIds)) {
            simpleVenderRespDTO = venderApi.getSimpleVenderRespDTOById(venderIds.get(0));
            if (Objects.nonNull(simpleVenderRespDTO)) {
                params.put("venderName", simpleVenderRespDTO.getName());
                List<VenderPocDTO> venderPocDTO = simpleVenderRespDTO.getPocList();
                if (!CollectionUtils.isEmpty(venderPocDTO)) {
                    List<VenderPocDTO> defaultvenderPocDTO = venderPocDTO.stream().filter(item -> item.getDefaultFlag() == 1).distinct().toList();
                    if (!CollectionUtils.isEmpty(defaultvenderPocDTO)) {
                        params.put("venderPocName", defaultvenderPocDTO.get(0).getName());
                        params.put("venderPocMobile", defaultvenderPocDTO.get(0).getMobile());
                    }
                }
            }
        }
        List<Long> warehouseIds = stockNoticeItemDOList.stream().map(StockNoticeItemDO::getWarehouseId).distinct().toList();
        if (!CollectionUtils.isEmpty(warehouseIds)) {
            List<WarehouseDTO> warehouseDTO = warehouseApi.selectBatchIds(warehouseIds);
            if (!CollectionUtils.isEmpty(warehouseDTO)) {
                params.put("warehouseAddress", warehouseDTO.get(0).getAddress());
                if(Objects.nonNull(warehouseDTO.get(0).getManagerIds())){
                    List<AdminUserRespDTO> creatUserInfoList = adminUserApi.getUserList(warehouseDTO.get(0).getManagerIds());
                    if (Objects.nonNull(creatUserInfoList) && !creatUserInfoList.isEmpty()) {
                        List<String> warehousePocNames = new ArrayList<>();
                        for (AdminUserRespDTO creatUserInfo : creatUserInfoList) {
                            StringBuilder warehousePocName = new StringBuilder();
                            if (Objects.nonNull(creatUserInfo.getNickname())) {
                                warehousePocName.append(creatUserInfo.getNickname());
                            }
                            if (Objects.nonNull(creatUserInfo.getMobile())) {
                                warehousePocName.append(" ").append(creatUserInfo.getMobile());
                            }
                            if (Objects.nonNull(creatUserInfo.getTel())) {
                                warehousePocName.append(" ").append(creatUserInfo.getTel());
                            }
                            warehousePocNames.add(warehousePocName.toString());
                        }
                        params.put("warehousePocName", String.join("  ", warehousePocNames));
                    }
                }
            }
        }

        List<UserDept> sales = stockNoticeItemDOList.stream().map(StockNoticeItemDO::getSales).distinct().toList();
        if (!CollectionUtils.isEmpty(sales)) {
            params.put("salesName", sales.get(0).getNickname());
        }

        //货物明细
        RowRenderData row0 = Rows.of("采购订单号", "客户PO号", "客户货号", "中文品名", "数量", "箱数", "实收箱数", "立方数", "位置").rowExactHeight(1).center().create();
        TableRenderData table = Tables.ofA4ExtendWidth().percentWidth("100%", new int[]{16, 11, 17, 15, 6, 6, 9, 10, 10}).create();
        table.addRow(row0);
        final int[] currentRow = {Integer.parseInt(Long.toString(table.getRows().stream().count()))};
        stockNoticeItemDOList.forEach(it -> {
            RowRenderData dataRow = Rows.of(
                    it.getPurchaseContractCode(),
                    it.getCustPo(),
                    it.getCskuCode(),
                    it.getSkuName(),
                    it.getOrderQuantity().toString(),
                    it.getOrderBoxQuantity().toString(),
                    null,
                    VolumeUtil.processVolume(it.getTotalVolume()).toString(),
                    null
            ).rowExactHeight(1).center().create();
            table.addRow(dataRow);
        });

        //体积合计
        BigDecimal totalVolume = stockNoticeItemDOList.stream()
                .map(s -> NumberUtil.div(s.getTotalVolume(), CalculationDict.ONE_MILLION).stripTrailingZeros())
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(CalculationDict.THREE, RoundingMode.HALF_UP);
        RowRenderData totalRow = Rows.of(
                "备注",
                null,
                null,
                null,
                null,
                null,
                "合计",
                totalVolume.toString(),
                null
        ).rowExactHeight(1).center().create();
        table.addRow(totalRow);
        MergeCellRule.MergeCellRuleBuilder ruleBuilder = MergeCellRule.builder();
        currentRow[0] = Integer.parseInt(Long.toString(table.getRows().stream().count()));
        ruleBuilder.map(MergeCellRule.Grid.of(currentRow[0] - 1, 1), MergeCellRule.Grid.of(currentRow[0] - 1, 5));
        ruleBuilder.map(MergeCellRule.Grid.of(currentRow[0] - 1, 7), MergeCellRule.Grid.of(currentRow[0] - 1, 8));
        table.setMergeRule(ruleBuilder.build());
        params.put("table", table);
        String result = reportApi.print(reportDTO.getAnnex().get(0).getFileUrl(), projectPath + "/output.docx", params,stockNoticeDO.getCode());
        //首次打印更新打印状态和打印时间
        // 增加操作日志
        OperateLogUtils.addOperateLog(OPERATOR_EXTS_KEY, stockNoticeDO.getCode(), "打印");
        return result;
    }

    @Override
    public Map<String, Integer> getStockNoticeMapByPurchaseContractCode(String purchaseContractCode) {
        List<StockNoticeItemDO> stockNoticeItemDOS = stockNoticeItemMapper.selectList(StockNoticeItemDO::getPurchaseContractCode, purchaseContractCode);
        if (CollUtil.isEmpty(stockNoticeItemDOS)) {
            return Map.of();
        }
        stockNoticeItemDOS = stockNoticeItemDOS.stream().filter(s->StrUtil.isNotEmpty(s.getPurchaseItemUniqueCode())).toList();
        if (CollUtil.isEmpty(stockNoticeItemDOS)) {
            return Map.of();
        }
        List<Long> noticeIdList = stockNoticeItemDOS.stream().map(StockNoticeItemDO::getNoticeId).distinct().toList();
        List<StockNoticeDO> stockNoticeDOS = stockNoticeMapper.selectBatchIds(noticeIdList);
        if (CollUtil.isEmpty(stockNoticeDOS)) {
            return Map.of();
        }
        Map<String, Integer> result = new HashMap<>();
        Set<Long> unConvertNoticeIds = stockNoticeDOS.stream().filter(s -> NoticeStatusEnum.UN_CONVERT.getValue().equals(s.getNoticeStatus())).map(StockNoticeDO::getId).collect(Collectors.toSet());
        Set<Long> convertNoticeIds = stockNoticeDOS.stream().filter(s -> NoticeStatusEnum.CONVERTED.getValue().equals(s.getNoticeStatus())||NoticeStatusEnum.PART_CONVERT.getValue().equals(s.getNoticeStatus())).map(StockNoticeDO::getId).collect(Collectors.toSet());
        stockNoticeItemDOS.forEach(item -> {
            if (unConvertNoticeIds.contains(item.getNoticeId())) {
                result.merge(item.getPurchaseItemUniqueCode(), item.getOrderQuantity(), Integer::sum);
            } else if (convertNoticeIds.contains(item.getNoticeId())){
                result.merge(item.getPurchaseItemUniqueCode(), item.getRealBillQuantity(), Integer::sum);
            }
        });
        List<String> stockNoticeCodeList = stockNoticeDOS.stream().filter(s -> NoticeStatusEnum.IN_CONVERT.getValue().equals(s.getNoticeStatus())).map(StockNoticeDO::getCode).distinct().toList();
        List<BillItemDO> billItemList = billService.getBillItemListByNoticeCodeList(stockNoticeCodeList);
        if (CollUtil.isNotEmpty(billItemList)){
            billItemList.forEach(s->{
                result.merge(s.getPurchaseItemUniqueCode(), s.getOrderQuantity(), Integer::sum);
            });
        }
        return result;
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
    public void submitTask(Long noticeId, Long userId) {
        StockNoticeDO stockNoticeDO = validateNoticeExists(noticeId);
        String processDefinitionKey = StockTypeEnum.IN_STOCK.getValue().equals(stockNoticeDO.getNoticeType())?IN_PROCESS_DEFINITION_KEY:OUT_PROCESS_DEFINITION_KEY;
        String processInstanceId = bpmProcessInstanceApi.createProcessInstance(userId, processDefinitionKey, noticeId);
        stockNoticeDO.setAuditStatus(BpmProcessInstanceResultEnum.PROCESS.getResult());
        stockNoticeDO.setProcessInstanceId(processInstanceId);
        stockNoticeMapper.updateById(stockNoticeDO);
    }

    @Override
    public String getInProcessDefinitionKey() {
        return IN_PROCESS_DEFINITION_KEY;
    }

    @Override
    public String getOutProcessDefinitionKey() {
        return OUT_PROCESS_DEFINITION_KEY;
    }

    @Override
    public void updateAuditStatus(Long auditableId, Integer auditStatus, String processInstanceId) {
        StockNoticeDO stockNoticeDO = validateNoticeExists(auditableId);
        stockNoticeDO.setAuditStatus(auditStatus);
        if (StrUtil.isNotEmpty(processInstanceId)) {
            stockNoticeDO.setProcessInstanceId(processInstanceId);
        }
        stockNoticeMapper.updateById(stockNoticeDO);
    }

    @Override
    public void closeNotice(Long id) {
        StockNoticeDO stockNoticeDO = validateNoticeExists(id);
        boolean exists = billService.exists(new LambdaQueryWrapperX<BillDO>().eq(BillDO::getNoticeCode, stockNoticeDO.getCode()));
        if (exists){
            throw exception(STOCK_NOTICE_EXISTS_BILL);
        }
         // 下推的出入库通知单不可作废
        if (!BooleanEnum.YES.getValue().equals(stockNoticeDO.getManualFlag())){
            throw exception(STOCK_NOTICE_NOT_ALLOW_CANCEL);
        }
        stockNoticeDO.setNoticeStatus(NoticeStatusEnum.CANCEL.getValue());
        stockNoticeMapper.updateById(stockNoticeDO);
        // 出库通知单释放锁定库存
        if (StockTypeEnum.OUT_STOCK.getValue().equals(stockNoticeDO.getNoticeType())){
            stockLockService.cancelOutNoticeLockQuantity(StockLockSourceTypeEnum.OUT_STOCK.getValue(),stockNoticeDO.getCode());
        }else { // 入库通知单回退采购转入库数量
            List<StockNoticeItemDO> stockNoticeItemDOS = stockNoticeItemMapper.selectList(new LambdaQueryWrapperX<StockNoticeItemDO>().select(StockNoticeItemDO::getNoticeId, StockNoticeItemDO::getPurchaseItemUniqueCode, StockNoticeItemDO::getPurchaseContractCode,StockNoticeItemDO::getOrderQuantity).eq(StockNoticeItemDO::getNoticeId, stockNoticeDO.getId()));
            if (CollUtil.isNotEmpty(stockNoticeItemDOS)){
                Map<String, Map<String, Integer>> updateMap = stockNoticeItemDOS.stream().filter(s -> StrUtil.isNotEmpty(s.getPurchaseContractCode())).collect(Collectors.groupingBy(StockNoticeItemDO::getPurchaseContractCode, Collectors.groupingBy(StockNoticeItemDO::getPurchaseItemUniqueCode, Collectors.summingInt(StockNoticeItemDO::getOrderQuantity))));
                purchaseContractApi.rollBackPurchaseNoticeQuantity(updateMap);
            }
        }
    }

    @Override
    public boolean validateManualStockNotice(String code) {
        if (StrUtil.isEmpty(code)){
            return false;
        }
        return stockNoticeMapper.exists(new LambdaQueryWrapperX<StockNoticeDO>().eq(StockNoticeDO::getCode, code).eq(StockNoticeDO::getManualFlag, BooleanEnum.YES.getValue()));
    }

    @Override
    public void updateConvertStatusByCode(String code) {
        if (StrUtil.isEmpty(code)){
            return;
        }
        List<StockNoticeDO> stockNoticeDOS = stockNoticeMapper.selectList(new LambdaQueryWrapperX<StockNoticeDO>().eq(StockNoticeDO::getCode, code));
        if (CollUtil.isEmpty(stockNoticeDOS)){
            return;
        }
        StockNoticeDO stockNoticeDO = stockNoticeDOS.stream().findFirst().get();
        List<StockNoticeItemDO> stockNoticeItemDOS = stockNoticeItemMapper.selectList(new LambdaQueryWrapperX<StockNoticeItemDO>().eq(StockNoticeItemDO::getNoticeId, stockNoticeDO.getId()));
        boolean allConvertFlag = stockNoticeItemDOS.stream().allMatch(s -> {
            int realBillQuantity = Objects.isNull(s.getRealBillQuantity()) ? 0 : s.getRealBillQuantity();
            int orderQuantity = Objects.isNull(s.getOrderQuantity()) ? 0 : s.getOrderQuantity();
            return realBillQuantity == orderQuantity;
        });
        if (allConvertFlag){
            stockNoticeDO.setNoticeStatus(NoticeStatusEnum.CONVERTED.getValue());
        }else {
            stockNoticeDO.setNoticeStatus(NoticeStatusEnum.PART_CONVERT.getValue());
        }
        stockNoticeMapper.updateById(stockNoticeDO);
    }
}