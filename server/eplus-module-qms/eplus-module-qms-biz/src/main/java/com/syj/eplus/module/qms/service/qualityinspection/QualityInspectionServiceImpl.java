package com.syj.eplus.module.qms.service.qualityinspection;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.dict.CommonDict;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.io.FileUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.dict.core.util.DictFrameworkUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.operatelog.core.util.OperateLogUtils;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import cn.iocoder.yudao.module.bpm.enums.task.BpmProcessInstanceResultEnum;
import cn.iocoder.yudao.module.infra.api.file.FileApi;
import cn.iocoder.yudao.module.system.api.logger.OperateLogApi;
import cn.iocoder.yudao.module.system.api.logger.dto.OperateLogRespDTO;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import com.alibaba.excel.metadata.data.ImageData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.deepoove.poi.data.*;
import com.syj.eplus.framework.common.dict.BusinessNameDict;
import com.syj.eplus.framework.common.entity.CreatedResponse;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.framework.common.enums.*;
import com.syj.eplus.module.crm.api.cust.CustApi;
import com.syj.eplus.module.infra.api.CodeGeneratorApi;
import com.syj.eplus.module.infra.api.orderlink.OrderLinkApi;
import com.syj.eplus.module.infra.api.orderlink.dto.OrderLinkDTO;
import com.syj.eplus.module.pms.api.packageType.PackageTypeApi;
import com.syj.eplus.module.pms.api.packageType.dto.PackageTypeDTO;
import com.syj.eplus.module.pms.api.sku.SkuApi;
import com.syj.eplus.module.pms.api.sku.dto.SimpleSkuDTO;
import com.syj.eplus.module.qms.api.dto.QualityInspectionItemRespDTO;
import com.syj.eplus.module.qms.api.dto.QualityInspectionReqDTO;
import com.syj.eplus.module.qms.api.dto.QualityInspectionRespDTO;
import com.syj.eplus.module.qms.controller.admin.qualityinspection.vo.*;
import com.syj.eplus.module.qms.convert.qualityinspection.QualityInspectionConvert;
import com.syj.eplus.module.qms.dal.dataobject.qualityinspection.QualityInspectionDO;
import com.syj.eplus.module.qms.dal.dataobject.qualityinspection.QualityInspectionItemDO;
import com.syj.eplus.module.qms.dal.mysql.qualityinspection.QualityInspectionItemMapper;
import com.syj.eplus.module.qms.dal.mysql.qualityinspection.QualityInspectionMapper;
import com.syj.eplus.module.qms.enums.InspectionItemStatusEnum;
import com.syj.eplus.module.qms.enums.InspectionTypeEnum;
import com.syj.eplus.module.scm.api.purchasecontract.PurchaseContractApi;
import com.syj.eplus.module.scm.api.purchasecontract.dto.PurchaseContractAllDTO;
import com.syj.eplus.module.scm.api.purchasecontract.dto.PurchaseContractItemDTO;
import com.syj.eplus.module.scm.api.purchasecontract.dto.PurchaseContractSimpleDTO;
import com.syj.eplus.module.scm.api.purchasecontract.dto.UpdateInspectionDTO;
import com.syj.eplus.module.scm.enums.PurchaseCheckStatusEmums;
import com.syj.eplus.module.sms.api.SaleContractApi;
import com.syj.eplus.module.system.api.report.ReportApi;
import com.syj.eplus.module.system.api.report.dto.ReportDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.qms.enums.ErrorCodeConstants.*;
import static com.syj.eplus.module.scm.enums.ErrorCodeConstants.*;


/**
 * 验货单 Service 实现类
 *
 * @author ePlus
 */
@Service
@Validated
public class QualityInspectionServiceImpl extends ServiceImpl<QualityInspectionMapper, QualityInspectionDO> implements QualityInspectionService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private QualityInspectionMapper qualityInspectionMapper;
    @Resource
    private QualityInspectionItemMapper inspectionItemMapper;
    @Resource
    private CodeGeneratorApi codeGeneratorApi;
    @Resource
    private AdminUserApi adminUserApi;
    @Resource
    private PurchaseContractApi purchaseContractApi;
    @Resource
    private SaleContractApi saleContractApi;

    @Resource
    private CustApi custApi;
    @Resource
    private SkuApi skuApi;

    @Resource
    private OrderLinkApi orderLinkApi;
    @Resource
    private PackageTypeApi packageTypeApi;

    private static final String SN_TYPE = "qualityInspection";
    private static final String CODE_PREFIX = "QI";
    //    private static final String PROCESS_DEFINITION_KEY = "qms_inspection";
    public static final String OPERATOR_EXTS_KEY = "QICode";

    private static final String PURCHASE_OPERATOR_EXTS_KEY = "purchaseContractCode";
    @Resource
    private OperateLogApi operateLogApi;
    @Resource
    private ReportApi reportApi;
    @Resource
    private FileApi fileApi;
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<CreatedResponse> createQualityInspection(QualityInspectionSaveReqVO createReqVO) {
        List<CreatedResponse> responses = new ArrayList<>();
        QualityInspectionDO qualityInspection = QualityInspectionConvert.INSTANCE.convertQualityInspectionDO(createReqVO);
        Long purchaseUserId = createReqVO.getPurchaseUserId();
        if (Objects.nonNull(purchaseUserId)){
            UserDept purchaseUser = adminUserApi.getUserDeptByUserId(purchaseUserId);
            qualityInspection.setPurchaseUser(purchaseUser);
        }
        // 插入
        String code = codeGeneratorApi.getCodeGenerator(SN_TYPE, CODE_PREFIX);
        qualityInspection.setCode(code).setSourceType(1);
        if (ObjectUtil.isNull(qualityInspection.getReinspectionFlag())) {
            qualityInspection.setReinspectionFlag(BooleanEnum.NO.getValue());
        }
//        qualityInspection.setReworkFlag(BooleanEnum.NO.getValue());
        qualityInspection.setAuditStatus(BpmProcessInstanceResultEnum.APPROVE.getResult());
        Long loginUserId = WebFrameworkUtils.getLoginUserId();
        AdminUserRespDTO userInfo = adminUserApi.getUser(loginUserId);
        qualityInspection.setApplyInspectorId(loginUserId).setApplyInspectorName(ObjectUtil.isNotNull(userInfo) ? userInfo.getNickname() : "");
        UserDept userDept = adminUserApi.getUserDeptByUserId(loginUserId);
        qualityInspection.setApplyInspectorDeptId(userInfo.getDeptId()).setApplyInspectorDeptName(ObjectUtil.isNotNull(userDept) ? userDept.getNickname() : "");
        qualityInspection.setQualityInspectionStatus(InspectionBillStatusEnum.WAITING_FOR_CONFIRMATION.getValue());
        List<QualityInspectionItemSaveReqVO> itemSaveReqVOList = createReqVO.getItemSaveReqVOList();
        if (CollUtil.isEmpty(itemSaveReqVOList)) {
            throw exception(QUALITY_INSPECTION_ITEM_NOT_EXISTS);
        }
        if (ObjectUtil.isNotNull(qualityInspection.getInspectionType()) && qualityInspection.getInspectionType().intValue() == InspectionTypeEnum.REMOTE.getValue()) {
//
            //判断验货状态
            List<Integer> list = itemSaveReqVOList.stream().map(QualityInspectionItemSaveReqVO::getInspectionStatus).distinct().toList();
            qualityInspection.setQualityInspectionStatus(getInspectionStatus(list));

            LocalDateTime inspectionTime = qualityInspection.getInspectionTime() == null ? qualityInspection.getInspectionTime() : LocalDateTime.now();
            qualityInspection.setExpectInspectionTime(inspectionTime);
            qualityInspection.setPlanInspectionTime(inspectionTime);
            qualityInspection.setInspectionTime(inspectionTime);
        }
        // 获取链路编号
        if (CollUtil.isEmpty(createReqVO.getLinkCodeList())) {
            createReqVO.setLinkCodeList(List.of(IdUtil.fastSimpleUUID()));
        }
        qualityInspection.setResultAnnex(createReqVO.getResultAnnex());
        qualityInspectionMapper.insert(qualityInspection);
        // 插入子表

        List<QualityInspectionItemDO> inspectionItemList = createInspectionItemList(qualityInspection.getId(), itemSaveReqVOList);

        // 补充操作日志明细
        AdminUserRespDTO currentUser = adminUserApi.getUser(WebFrameworkUtils.getLoginUserId());
        OperateLogUtils.setContent(String.format("【验货单】%s 新增验货单 %s", currentUser.getNickname(), code));
        OperateLogUtils.addExt(OPERATOR_EXTS_KEY, code);
        OperateLogUtils.addExt(PURCHASE_OPERATOR_EXTS_KEY, createReqVO.getPurchaseContractCode());
        List<String> linkCodeList = qualityInspection.getLinkCodeList();
        // 插入订单链路
        if (CollUtil.isNotEmpty(linkCodeList)) {
            List<OrderLinkDTO> orderLinkDTOList = linkCodeList.stream().map(s -> new OrderLinkDTO()
                    .setBusinessId(qualityInspection.getId())
                    .setCode(qualityInspection.getCode())
                    .setName(BusinessNameDict.QUALITY_INSPECTION_NAME)
                    .setType(OrderLinkTypeEnum.INSPECTION.getValue())
                    .setLinkCode(s)
                    .setItem(inspectionItemList)
                    .setParentCode(StrUtil.isEmpty(qualityInspection.getPurchaseContractCode()) ? CommonDict.HYPHEN : qualityInspection.getPurchaseContractCode())
                    .setStatus(qualityInspection.getQualityInspectionStatus())
                    .setCompanyId(qualityInspection.getCompanyId())
                    .setBusinessSubjectName(qualityInspection.getVenderName())
                    .setOrderMsg(qualityInspection)).toList();
            orderLinkApi.batchCreateOrderLink(orderLinkDTOList);
        }
        // 验货方式为远程验货时，同步验货状态至采购合同
        if (ObjectUtil.isNotNull(qualityInspection.getInspectionType()) && qualityInspection.getInspectionType().intValue() == InspectionTypeEnum.REMOTE.getValue()) {
            Map<Long, Integer> inspectionStatusMap = new HashMap<>();
            itemSaveReqVOList.forEach(o -> {
                inspectionStatusMap.put(o.getPurchaseContractItemId(), o.getInspectionStatus());
            });
            // 同步验货单至采购合同明细
            purchaseContractApi.modifyContractItemStatus(qualityInspection.getPurchaseContractCode(), inspectionStatusMap,qualityInspection.getInspectionTime());
        }
        // 返回
        responses.add(new CreatedResponse(qualityInspection.getId(), qualityInspection.getCode()));
        return responses;
    }


    private Integer getInspectionStatus(List<Integer> list) {
        Integer quantityStatus = null;
        if (list.size() == 1 && Objects.equals(list.get(0), InspectionItemStatusEnum.SUCESS.getValue())) {   //全部通过为通过
            quantityStatus = InspectionBillStatusEnum.INSPECTION_PASS.getValue();
        } else if (list.size() == 1 && !Objects.equals(list.get(0), InspectionItemStatusEnum.SUCESS.getValue())) {  //全部不为通过为失败
            quantityStatus = InspectionBillStatusEnum.INSPECTION_FAIL.getValue();
        } else {//其他为部分通过
            quantityStatus = InspectionBillStatusEnum.INSPECTION_PART.getValue();
        }
        return quantityStatus;
    }


    private Integer getInspectionStatusByDO(List<QualityInspectionItemDO> list) {
        if (CollUtil.isEmpty(list)) {
            return null;
        }
        list.forEach(s -> {
            if (Objects.equals(s.getHandleFlag(), QmsHandleStateEnum.RELEASE.getValue())) {
                s.setInspectionStatus(InspectionItemStatusEnum.SUCESS.getValue());
            }
        });
        Integer quantityStatus = null;
        List<Integer> idList = list.stream().map(QualityInspectionItemDO::getInspectionStatus).filter(Objects::nonNull).distinct().toList();
        if (CollUtil.isEmpty(idList)){
            quantityStatus = InspectionBillStatusEnum.EXPECTING_INSPECTION.getValue();
        }else {
            if (idList.size() == 1 && Objects.equals(idList.get(0), InspectionItemStatusEnum.SUCESS.getValue())) {   //全部通过为通过
                quantityStatus = InspectionBillStatusEnum.INSPECTION_PASS.getValue();
            } else if (idList.size() == 1 && !Objects.equals(idList.get(0), InspectionItemStatusEnum.SUCESS.getValue())) {  //全部不为通过为失败
                quantityStatus = InspectionBillStatusEnum.INSPECTION_FAIL.getValue();
            } else {//其他为部分通过
                quantityStatus = InspectionBillStatusEnum.INSPECTION_PART.getValue();
            }
        }
        return quantityStatus;
    }


    private List<QualityInspectionItemDO> createInspectionItemList(Long inspectionId, List<QualityInspectionItemSaveReqVO> list) {
        list.forEach(o -> {
            if (Objects.isNull(o.getPurchaseContractItemId())) {
                o.setPurchaseContractItemId(o.getId());
            }
            o.setId(null).setInspectionId(inspectionId);
        });
        List<QualityInspectionItemDO> itemDOList = BeanUtils.toBean(list, QualityInspectionItemDO.class);
        inspectionItemMapper.insertBatch(itemDOList);
        return itemDOList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateQualityInspection(QualityInspectionSaveReqVO updateReqVO) {
        // 校验存在
        Long inspectionId = updateReqVO.getId();
        validateQualityInspectionExists(inspectionId);
        // 更新主单
        QualityInspectionDO updateObj = QualityInspectionConvert.INSTANCE.convertQualityInspectionDO(updateReqVO);
//        Integer reworkFlag = updateObj.getReworkFlag();
        List<QualityInspectionItemSaveReqVO> itemSaveReqVOList = updateReqVO.getItemSaveReqVOList();
        List<QualityInspectionItemDO> itemDOList = BeanUtils.toBean(itemSaveReqVOList, QualityInspectionItemDO.class);
        updateObj.setQualityInspectionStatus(getInspectionStatusByDO(itemDOList));
        boolean inspectionFlag = qualityInspectionMapper.updateById(updateObj) > 0;
        // 更新子表数据
        boolean inspectionItemFlag = false;
        if (!CollectionUtils.isEmpty(itemSaveReqVOList)) {
            inspectionItemFlag = updateInspectionItemList(updateObj.getInspectionTime(),updateObj.getInspectionNode(),updateObj.getId(), updateObj.getPurchaseContractCode(), itemDOList);
        } else {
            inspectionItemFlag = true;
        }

//        // 返工重演重新生成验货单
//        if (ObjectUtil.isNotNull(reworkFlag) && reworkFlag.intValue() == BooleanEnum.YES.getValue()) {
//            QualityInspectionRespVO reworkInspection = this.getQualityInspection(new QualityInspectionDetailReq().setQualityInspectionId(inspectionId));
//            QualityInspectionSaveReqVO createReqVO = BeanUtils.toBean(reworkInspection, QualityInspectionSaveReqVO.class);
//            createReqVO.setId(null).setCode(null);
//            createReqVO.setReworkFlag(BooleanEnum.NO.getValue()).setReinspectionFlag(BooleanEnum.YES.getValue());
//            createReqVO.setPlanInspectionTime(updateReqVO.getReworkInspectionTime());
//            createReqVO.setSourceId(inspectionId).setSourceCode(updateObj.getCode());
//            List<QualityInspectionItemRespVO> children = reworkInspection.getChildren();
//            List<QualityInspectionItemRespVO> filterList = children.stream().filter(x -> x.getInspectionStatus().intValue() == InspectionItemStatusEnum.FAIL.getValue()
//                                                                                            || x.getInspectionStatus().intValue() == InspectionItemStatusEnum.PENDING.getValue()
//                                                                                            || x.getHandleFlag().intValue() == BooleanEnum.NO.getValue()
//                                                                                            ).toList();
//            if (!CollectionUtils.isEmpty(filterList)) {
//                List<QualityInspectionItemSaveReqVO> reworkItemSaveList = BeanUtils.toBean(filterList, QualityInspectionItemSaveReqVO.class);
//                reworkItemSaveList.forEach(x->x.setId(null).setInspectionId(null).setLastFailDesc(x.getFailDesc()).setFailDesc(null));
//                createReqVO.setItemSaveReqVOList(reworkItemSaveList);
//                this.createQualityInspection(createReqVO);
//            }
//        }
        return inspectionFlag && inspectionItemFlag;
    }

    private boolean updateInspectionItemList(LocalDateTime inspectionTime,Integer inspectionNode,Long inspectionId, String purchaseContractCode, List<QualityInspectionItemDO> itemDOList) {
        deleteInspectionItemBySourceId(inspectionId);
        Map<Long, UpdateInspectionDTO> inspectionStatusMap = new HashMap<>();
        itemDOList.forEach(o -> {
            o.setInspectionId(inspectionId);
            // 解决更新情况下：1）id 冲突；2）updateTime 不更新
            o.setId(null).setUpdater(null).setUpdateTime(null);
            Integer inspectionStatus = o.getInspectionStatus();
            if (Objects.nonNull(inspectionStatus)&&Objects.nonNull(inspectionNode)&& InspectionNodeEnum.FINAL.getCode().equals(inspectionNode)) {
                UpdateInspectionDTO updateInspectionDTO = new UpdateInspectionDTO();
                updateInspectionDTO.setQuantity(o.getQuantity());
                updateInspectionDTO.setCheckStatus(o.getInspectionStatus());
                updateInspectionDTO.setHandleFlag(o.getHandleFlag());
                updateInspectionDTO.setInspectionTime(inspectionTime);
                inspectionStatusMap.put(o.getPurchaseContractItemId(), updateInspectionDTO);
            }
        });
        // 同步验货单至采购合同明细
        purchaseContractApi.updatePurchaseInspectionData(inspectionStatusMap);
        return inspectionItemMapper.insertBatch(itemDOList);
    }

    private void deleteInspectionItemBySourceId(Long inspectionId) {
        LambdaQueryWrapper<QualityInspectionItemDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(QualityInspectionItemDO::getInspectionId, inspectionId);
        inspectionItemMapper.delete(queryWrapper);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteQualityInspection(Long id) {
        // 校验存在
        QualityInspectionDO qualityInspectionDO = validateQualityInspectionExists(id);
        // 删除
        qualityInspectionMapper.deleteById(id);

        // 删除子表
        deleteQualityInspectionItemByInspectionId(id);
        orderLinkApi.deleteOrderLink(OrderLinkTypeEnum.INSPECTION.getValue(),qualityInspectionDO.getCode());
    }

    private QualityInspectionDO validateQualityInspectionExists(Long id) {
        QualityInspectionDO qualityInspectionDO = qualityInspectionMapper.selectById(id);
        if (qualityInspectionDO == null) {
            throw exception(QUALITY_INSPECTION_NOT_EXISTS);
        }
        return qualityInspectionDO;
    }

    @Override
    public void approveTask(Long userId, BpmTaskApproveReqDTO reqVO) {
//        bpmProcessInstanceApi.approveTask(userId, BeanUtils.toBean(reqVO, BpmTaskApproveReqDTO.class));
    }

    @Override
    public void rejectTask(Long userId, BpmTaskRejectReqDTO reqVO) {
//        bpmProcessInstanceApi.rejectTask(userId, BeanUtils.toBean(reqVO, BpmTaskRejectReqDTO.class));
    }

    @Override
    public void submitTask(Long qualityInspectionId, Long userId) {
//        bpmProcessInstanceApi.createProcessInstance(userId, PROCESS_DEFINITION_KEY, qualityInspectionId);
        updateAuditStatus(qualityInspectionId, BpmProcessInstanceResultEnum.PROCESS.getResult());
        QualityInspectionDO qualityInspectionDO = super.getById(qualityInspectionId);
        qualityInspectionDO.setQualityInspectionStatus(InspectionBillStatusEnum.AWAITING_APPROVAL.getValue());
        qualityInspectionMapper.updateById(qualityInspectionDO);
    }

    @Override
    public void updateAuditStatus(Long auditableId, Integer auditStatus) {
        qualityInspectionMapper.updateById(QualityInspectionDO.builder().id(auditableId).auditStatus(auditStatus).build());
        //回写采购合同
        if (Objects.equals(auditStatus, InspectionBillStatusEnum.INSPECTION_PASS.getValue())) {
            QualityInspectionDO qualityInspectionDO = qualityInspectionMapper.selectById(auditableId);
            purchaseContractApi.updateCheckStatus(qualityInspectionDO.getPurchaseContractCode(), PurchaseCheckStatusEmums.ALL_PASS.getValue());
        }
        if (Objects.equals(auditStatus, InspectionBillStatusEnum.INSPECTION_PART.getValue())) {
            QualityInspectionDO qualityInspectionDO = qualityInspectionMapper.selectById(auditableId);
            purchaseContractApi.updateCheckStatus(qualityInspectionDO.getPurchaseContractCode(), PurchaseCheckStatusEmums.PART_CHECK.getValue());
        }
    }

    @Override
    public String getProcessDefinitionKey() {
//        return PROCESS_DEFINITION_KEY;
        return null;
    }

    @Override
    public QualityInspectionRespVO getQualityInspection(QualityInspectionDetailReq qualityInspectionDetailReq) {
        Long qualityInspectionId = qualityInspectionDetailReq.getQualityInspectionId();
        if (Objects.isNull(qualityInspectionId)) {
            logger.error("[验货单]未获取到验货单id");
            return null;
        }
        QualityInspectionDO qualityInspectionDO = qualityInspectionMapper.selectById(qualityInspectionId);
        if (qualityInspectionDO == null) {
            return null;
        }
        QualityInspectionRespVO inspectionRespVO = BeanUtils.toBean(qualityInspectionDO, QualityInspectionRespVO.class);

        Long id = inspectionRespVO.getId();
        List<QualityInspectionItemDO> qualityInspectionItemDOS = this.listItemByInspectionIdList(Collections.singletonList(id));
        List<QualityInspectionItemRespVO> itemRespVOList = BeanUtils.toBean(qualityInspectionItemDOS, QualityInspectionItemRespVO.class);

        if (!CollectionUtils.isEmpty(itemRespVOList)) {
            List<Long> skuIdList = itemRespVOList.stream().map(QualityInspectionItemRespVO::getSkuId).toList();
            Map<Long, SimpleSkuDTO> simpleSkuDTOMap = skuApi.getSimpleSkuDTOMap(skuIdList);
            List<PackageTypeDTO> packageTypeList = packageTypeApi.getList();
            itemRespVOList.forEach(i -> {
                if (CollUtil.isNotEmpty(simpleSkuDTOMap)) {
                    SimpleSkuDTO simpleSkuDTO = simpleSkuDTOMap.get(i.getSkuId());
                    if (Objects.nonNull(simpleSkuDTO)) {
                        i.setMainPicture(simpleSkuDTO.getMainPicture());
                        i.setThumbnail(simpleSkuDTO.getThumbnail());
                    }
                }
                if (CollUtil.isNotEmpty(packageTypeList) && CollUtil.isNotEmpty(i.getPackageType())) {
                    List<Long> distinctPackageType = i.getPackageType().stream().distinct().toList();
                    List<PackageTypeDTO> tempPackageTypeList = packageTypeList.stream().filter(pt -> distinctPackageType.contains(pt.getId())).toList();
                    if (CollUtil.isNotEmpty(tempPackageTypeList)) {
                        List<String> packageTypeNameList = tempPackageTypeList.stream().map(PackageTypeDTO::getName).distinct().toList();
                        i.setPackageTypeName(String.join(",", packageTypeNameList));
                        List<String> packageTypeNameEngList = tempPackageTypeList.stream().map(PackageTypeDTO::getNameEng).distinct().toList();
                        i.setPackageTypeEngName(String.join(",", packageTypeNameEngList));
                    }
                }
            });
            inspectionRespVO.setChildren(itemRespVOList);
        }

//        String bpmProcessInstanceId = bpmProcessInstanceApi.getBpmProcessInstanceId(id, PROCESS_DEFINITION_KEY);
//        if (StrUtil.isNotEmpty(bpmProcessInstanceId)) {
//            inspectionRespVO.setProcessInstanceId(bpmProcessInstanceId);
//        }

        List<OperateLogRespDTO> operateLogRespDTOList = operateLogApi.getOperateLogRespDTOList(OPERATOR_EXTS_KEY, inspectionRespVO.getCode());
        inspectionRespVO.setOperateLogRespDTOList(operateLogRespDTOList);
        if (Objects.equals(inspectionRespVO.getReworkFlag(), BooleanEnum.YES.getValue())) {
            inspectionRespVO.setHandleState(QmsHandleStateEnum.REWORK.getValue());
        }
        if (Objects.equals(inspectionRespVO.getConcessionReleaseFlag(), BooleanEnum.YES.getValue())) {
            inspectionRespVO.setHandleState(QmsHandleStateEnum.RELEASE.getValue());
        }
        inspectionRespVO.getChildren().forEach(c -> {
            c.setHandleState(inspectionRespVO.getHandleState());
        });

        PurchaseContractAllDTO purchaseContractByCode = purchaseContractApi.getPurchaseContractByCode(inspectionRespVO.getPurchaseContractCode());
        if (Objects.nonNull(purchaseContractByCode)) {
            inspectionRespVO.setSaleContractCode(purchaseContractByCode.getSaleContractCode());
            inspectionRespVO.setCustName(purchaseContractByCode.getCustName());
        }
        return inspectionRespVO;
    }

    @Override
    public PageResult<QualityInspectionRespVO> getQualityInspectionPage(QualityInspectionPageReqVO pageReqVO) {
        List<Integer> queryQualityInspectionStatuList = pageReqVO.getQualityInspectionStatus();
        PageResult<QualityInspectionDO> pageResult = qualityInspectionMapper.selectPage(pageReqVO);
        List<QualityInspectionDO> inspectionDOList = pageResult.getList();
        if (CollectionUtils.isEmpty(inspectionDOList)) {
            return new PageResult<QualityInspectionRespVO>().setList(new ArrayList<>()).setTotal(pageResult.getTotal());
        }
        List<Long> inspectionIdList = inspectionDOList.stream().map(QualityInspectionDO::getId).toList();
        List<QualityInspectionItemDO> qualityInspectionItemDOS = this.listItemByInspectionIdList(inspectionIdList);
        Map<Long, List<QualityInspectionItemDO>> qualityInspectionItemCollect = qualityInspectionItemDOS.stream().collect(Collectors.groupingBy(QualityInspectionItemDO::getInspectionId));

        List<Long> skuIdList = qualityInspectionItemDOS.stream().map(QualityInspectionItemDO::getSkuId).toList();
        Map<Long, SimpleSkuDTO> simpleSkuDTOMap = skuApi.getSimpleSkuDTOMap(skuIdList);

        List<QualityInspectionRespVO> inspectionRespVOS = BeanUtils.toBean(inspectionDOList, QualityInspectionRespVO.class);

        List<Long> purchaseCoddeList = inspectionRespVOS.stream().map(QualityInspectionRespVO::getPurchaseContractId).distinct().toList();
        Map<Long, PurchaseContractSimpleDTO> contractMap = purchaseContractApi.getContractMap(purchaseCoddeList);

        inspectionRespVOS.forEach(x -> {
            Long id = x.getId();
            if (CollUtil.isNotEmpty(contractMap)) {
                PurchaseContractSimpleDTO purchaseContractSimpleDTO = contractMap.get(x.getPurchaseContractId());
                if (Objects.nonNull(purchaseContractSimpleDTO)) {
                    x.setCustName(purchaseContractSimpleDTO.getCustName());
                }
            }
            List<QualityInspectionItemDO> qualityInspectionItemList = qualityInspectionItemCollect.get(x.getId());
            List<QualityInspectionItemRespVO> itemRespVOList = BeanUtils.toBean(qualityInspectionItemList, QualityInspectionItemRespVO.class);

            List<QualityInspectionItemRespVO> respList = new ArrayList<>();
            itemRespVOList.forEach(i -> {
                if (CollUtil.isNotEmpty(simpleSkuDTOMap)) {
                    SimpleSkuDTO simpleSkuDTO = simpleSkuDTOMap.get(i.getSkuId());
                    if (Objects.nonNull(simpleSkuDTO)) {
                        i.setMainPicture(simpleSkuDTO.getMainPicture());
                        i.setThumbnail(simpleSkuDTO.getThumbnail());
                    }
                }
                if (!CollectionUtils.isEmpty(queryQualityInspectionStatuList)) {
                    Integer itemInspectionStatus = i.getInspectionStatus();
                    if (queryQualityInspectionStatuList.contains(InspectionBillStatusEnum.INSPECTION_PASS.getValue()) &&
                            queryQualityInspectionStatuList.contains(InspectionBillStatusEnum.INSPECTION_PART.getValue()) &&
                            (Objects.equals(itemInspectionStatus, InspectionItemStatusEnum.SUCESS.getValue()))) {
                        respList.add(i);
                    }
                    if (queryQualityInspectionStatuList.contains(InspectionBillStatusEnum.INSPECTION_FAIL.getValue()) &&
                            queryQualityInspectionStatuList.contains(InspectionBillStatusEnum.INSPECTION_PART.getValue()) &&
                            (Objects.equals(itemInspectionStatus, InspectionItemStatusEnum.PENDING.getValue()) || Objects.equals(itemInspectionStatus, InspectionItemStatusEnum.FAIL.getValue()))) {
                        respList.add(i);
                    }
                }
            });
            if (!CollectionUtils.isEmpty(respList)) {
                x.setChildren(respList);
            } else {
                x.setChildren(itemRespVOList);
            }
//            String bpmProcessInstanceId = bpmProcessInstanceApi.getBpmProcessInstanceId(id, PROCESS_DEFINITION_KEY);
//            if (StrUtil.isNotEmpty(bpmProcessInstanceId)) {
//                x.setProcessInstanceId(bpmProcessInstanceId);
//            }
        });
//        //处理验货单处理状态
//        inspectionRespVOS.forEach(s->{
//            if(Objects.equals(s.getReworkFlag(), BooleanEnum.YES.getValue())){
//                s.setHandleState(QmsHandleStateEnum.REWORK.getValue());
//            }
//            if(Objects.equals(s.getConcessionReleaseFlag(),BooleanEnum.YES.getValue())){
//                s.setHandleState(QmsHandleStateEnum.GIVE_IN.getValue());
//            }
//            s.getChildren().forEach(c->{
//                c.setHandleState(s.getHandleState());
//            });
//        });

        return new PageResult<QualityInspectionRespVO>().setList(inspectionRespVOS).setTotal(pageResult.getTotal());
    }

    // ==================== 子表（验货单-明细） ====================

    @Override
    public PageResult<QualityInspectionItemRespVO> getQualityInspectionItemPage(PageParam pageReqVO, Long inspectionId) {
        PageResult<QualityInspectionItemDO> qualityInspectionItemDOPageResult = inspectionItemMapper.selectPage(pageReqVO, inspectionId);
        List<QualityInspectionItemRespVO> qualityInspectionItemRespVOList = new ArrayList<>();
        List<QualityInspectionItemDO> qualityInspectionItemDOList = qualityInspectionItemDOPageResult.getList();
        if (!CollectionUtils.isEmpty(qualityInspectionItemDOList)) {

            List<Long> skuIdList = qualityInspectionItemDOList.stream().map(QualityInspectionItemDO::getSkuId).toList();
            Map<Long, SimpleSkuDTO> simpleSkuDTOMap = skuApi.getSimpleSkuDTOMap(skuIdList);

            qualityInspectionItemDOList.forEach(x -> {
                QualityInspectionItemRespVO qualityInspectionItemRespVO = BeanUtils.toBean(x, QualityInspectionItemRespVO.class);
                if (CollUtil.isNotEmpty(simpleSkuDTOMap)) {
                    SimpleSkuDTO simpleSkuDTO = simpleSkuDTOMap.get(qualityInspectionItemRespVO.getSkuId());
                    if (Objects.nonNull(simpleSkuDTO)) {
                        qualityInspectionItemRespVO.setMainPicture(simpleSkuDTO.getMainPicture());
                        qualityInspectionItemRespVO.setThumbnail(simpleSkuDTO.getThumbnail());
                    }
                }
                qualityInspectionItemRespVOList.add(qualityInspectionItemRespVO);
            });
        }
        return new PageResult<QualityInspectionItemRespVO>().setList(qualityInspectionItemRespVOList).setTotal(qualityInspectionItemDOPageResult.getTotal());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createQualityInspectionItem(QualityInspectionItemDO qualityInspectionItem) {
        inspectionItemMapper.insert(qualityInspectionItem);
        return qualityInspectionItem.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateQualityInspectionItem(QualityInspectionItemDO qualityInspectionItem) {
        // 校验存在
        validateQualityInspectionItemExists(qualityInspectionItem.getId());
        // 更新
        inspectionItemMapper.updateById(qualityInspectionItem);
    }

    @Override
    public void deleteQualityInspectionItem(Long id) {
        // 校验存在
        validateQualityInspectionItemExists(id);
        // 删除
        inspectionItemMapper.deleteById(id);
    }

    @Override
    public QualityInspectionItemDO getQualityInspectionItem(Long id) {
        return inspectionItemMapper.selectById(id);
    }

    @Override
    public List<QualityInspectionItemDO> listItemByInspectionIdList(List<Long> inspectionIdList) {
        LambdaQueryWrapper<QualityInspectionItemDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(QualityInspectionItemDO::getInspectionId, inspectionIdList);
        return inspectionItemMapper.selectList(queryWrapper);
    }

    @Override
    public Long getQualityInspectionNumByPurchaseContractCode(String code) {
        return inspectionItemMapper.selectCount(QualityInspectionItemDO::getPurchaseContractCode, code);
    }

    @Override
    public List<QualityInspectionDO> getQualityInspectionListByIdList(List<Long> quanlityInspectionIdList) {
        return qualityInspectionMapper.selectList(QualityInspectionDO::getId, quanlityInspectionIdList);
    }

    @Override
    public Map<Long, List<QualityInspectionItemDO>> getItemListByQualityInspectionIdList(List<Long> quanlityInspectionIdList) {
        List<QualityInspectionItemDO> itemList = inspectionItemMapper.selectList(QualityInspectionItemDO::getInspectionId, quanlityInspectionIdList);
        if (CollUtil.isEmpty(itemList)) {
            return null;
        }
        return itemList.stream().collect(Collectors.groupingBy(QualityInspectionItemDO::getInspectionId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setConcessionReleaseStatus(List<Long> ids, Integer value, List<SimpleFile> annex) {
        //回写子表处理状态为让步放行
        List<QualityInspectionItemDO> itemDOList = inspectionItemMapper.selectList(QualityInspectionItemDO::getId, ids);
        if (CollUtil.isEmpty(itemDOList)){
            return;
        }
        List<Long> list = itemDOList.stream().map(QualityInspectionItemDO::getInspectionId).distinct().toList();
        List<QualityInspectionDO> inspectionDOS = qualityInspectionMapper.selectBatchIds(list);
        if (CollUtil.isEmpty(inspectionDOS)){
            return;
        }
        Map<Long, QualityInspectionDO> inspectionDOMap = inspectionDOS.stream().collect(Collectors.toMap(QualityInspectionDO::getId, s -> s));
        Map<Long,UpdateInspectionDTO> updateInspectionDTOMap = new HashMap<>();
        itemDOList.forEach(s -> {
            if (Objects.equals(value, BooleanEnum.YES.getValue())) {
                s.setHandleFlag(QmsHandleStateEnum.RELEASE.getValue());
            } else {
                s.setHandleFlag(0);
            }
            QualityInspectionDO qualityInspectionDO = inspectionDOMap.get(s.getInspectionId());
            if (Objects.isNull(qualityInspectionDO)||!InspectionNodeEnum.FINAL.getCode().equals(qualityInspectionDO.getInspectionNode())){
                return;
            }
            UpdateInspectionDTO updateInspectionDTO = new UpdateInspectionDTO();
            updateInspectionDTO.setCheckStatus(s.getInspectionStatus());
            updateInspectionDTO.setQuantity(s.getQuantity());
            updateInspectionDTO.setHandleFlag(s.getHandleFlag());
            updateInspectionDTO.setInspectionTime(qualityInspectionDO.getInspectionTime());
            updateInspectionDTOMap.put(s.getPurchaseContractItemId(), updateInspectionDTO);
        });
        inspectionItemMapper.updateBatch(itemDOList);
        if (CollUtil.isNotEmpty(updateInspectionDTOMap)){
            purchaseContractApi.updatePurchaseInspectionData(updateInspectionDTOMap);
        }
    }

    @Override
    public void setStatus(List<Long> ids, List<SimpleFile> annex, String description, List<SimpleFile> picture) {

        List<QualityInspectionItemDO> itemDOList = inspectionItemMapper.selectList(QualityInspectionItemDO::getId, ids);
        if (CollUtil.isEmpty(itemDOList)) {
            throw exception(QUALITY_INSPECTION_ITEM_NOT_EXISTS);
        }
        List<Long> idList = itemDOList.stream().map(QualityInspectionItemDO::getInspectionId).distinct().toList();
        if (CollUtil.isEmpty(idList)) {
            throw exception(QUALITY_INSPECTION_NOT_EXISTS);
        }
        if (CollUtil.isEmpty(idList)) {
            throw exception(QUALITY_CODE_NOT_SAME);
        }
        QualityInspectionDO qualityInspectionDO = qualityInspectionMapper.selectById(idList.get(0));
        if (Objects.isNull(qualityInspectionDO.getGuaranteeLetter())) {
            qualityInspectionDO.setGuaranteeLetter(annex);
        } else {
            if (annex != null) { // 检查 annex 是否为空
                qualityInspectionDO.getGuaranteeLetter().addAll(annex);
            }
        }

        if (Objects.isNull(qualityInspectionDO.getPicture())) {
            qualityInspectionDO.setPicture(picture);
        } else {
            if (picture != null) { // 检查 annex 是否为空
                qualityInspectionDO.getPicture().addAll(picture);
            }
        }

        qualityInspectionDO.setAcceptDesc(qualityInspectionDO.getAcceptDesc() + ";" + description);

        qualityInspectionDO.setQualityInspectionStatus(getInspectionStatusByDO(itemDOList));
        qualityInspectionMapper.updateById(qualityInspectionDO);
        // 同步验货单至采购合同明细
        LambdaQueryWrapper<QualityInspectionItemDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(QualityInspectionItemDO::getInspectionId, qualityInspectionDO.getId());
        queryWrapper.in(QualityInspectionItemDO::getId, ids);
        List<QualityInspectionItemDO> qualityInspectionItemDOS = inspectionItemMapper.selectList(queryWrapper);
        Map<Long, Integer> inspectionStatusMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(qualityInspectionItemDOS)) {
            qualityInspectionItemDOS.forEach(x -> {
                inspectionStatusMap.put(x.getPurchaseContractItemId(), InspectionItemStatusEnum.SUCESS.getValue());
            });
            // 更新验货单明细状态
            inspectionItemMapper.updateBatch(qualityInspectionItemDOS);
        }
        // 同步验货单至采购合同明细
        // 同步验货单至出运明细
        purchaseContractApi.modifyContractItemStatus(qualityInspectionDO.getPurchaseContractCode(), inspectionStatusMap,qualityInspectionDO.getInspectionTime());


    }

    @Override
    public List<QualityInspectionRespDTO> getDTOList(QualityInspectionReqDTO req) {
        //item全量数据筛选
        List<QualityInspectionItemDO> itemDoList = inspectionItemMapper.selectList(new LambdaQueryWrapperX<QualityInspectionItemDO>()
                .eqIfPresent(QualityInspectionItemDO::getSkuId, req.getSkuId())
                .eqIfPresent(QualityInspectionItemDO::getSkuCode, req.getSkuCode())
                .likeIfPresent(QualityInspectionItemDO::getSkuName, req.getSkuName())
        );
        if (CollUtil.isEmpty(itemDoList)) {
            return null;
        }
        //do全量数据筛选
        List<QualityInspectionDO> doList = qualityInspectionMapper.selectList(new LambdaQueryWrapperX<QualityInspectionDO>()
                .eqIfPresent(QualityInspectionDO::getVenderId, req.getVenderId())
                .eqIfPresent(QualityInspectionDO::getVenderCode, req.getVenderCode())
                .likeIfPresent(QualityInspectionDO::getVenderName, req.getVenderName())
                .eqIfPresent(QualityInspectionDO::getPurchaseContractCode, req.getPurchaseContractCode())
                .betweenIfPresent(QualityInspectionDO::getCreateTime, req.getCheckTime())
                .eqIfPresent(QualityInspectionDO::getInspectorId, req.getInspectorId())
        );
        if (CollUtil.isEmpty(doList)) {
            return null;
        }
        //获取符合条件的idList
        List<Long> itemInspectionIdlist = itemDoList.stream().map(QualityInspectionItemDO::getInspectionId).distinct().toList();
        List<Long> doIdList = doList.stream().map(QualityInspectionDO::getId).distinct().toList();
        List<Long> idList = new ArrayList<>(itemInspectionIdlist);
        idList.retainAll(itemInspectionIdlist);  //量规IdList取交集
        //组装结果数据
        List<QualityInspectionDO> resultDoList = doList.stream().filter(s -> idList.contains(s.getId())).toList();
        Map<Long, List<QualityInspectionItemDO>> itemMap = itemDoList.stream().filter(s -> idList.contains(s.getInspectionId())).collect(Collectors.groupingBy(QualityInspectionItemDO::getInspectionId));
        if (CollUtil.isEmpty(resultDoList) || CollUtil.isEmpty((itemMap))) {
            return null;
        }
        List<QualityInspectionRespDTO> dtoList = BeanUtils.toBean(resultDoList, QualityInspectionRespDTO.class);
        dtoList.forEach(s -> {
            s.setChildren(BeanUtils.toBean(itemMap.get(s.getId()), QualityInspectionItemRespDTO.class));
        });

        return dtoList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean reworkQualityInspection(QualityInspectionReworkReqVO reworkReqVO) {
        List<QualityInspectionItemDO> itemDOList = inspectionItemMapper.selectList(QualityInspectionItemDO::getId,
                reworkReqVO.getItemList().stream().map(QualityInspectionReworkItemReqVO::getItemId).toList());
        if (CollUtil.isEmpty(itemDOList)) {
            throw exception(QUALITY_INSPECTION_ITEM_NOT_EXISTS);
        }

        List<QualityInspectionItemDO> filterList = itemDOList.stream().filter(x ->
                Objects.equals(x.getInspectionStatus(), InspectionItemStatusEnum.SUCESS.getValue())
                        || x.getHandleFlag().intValue() == BooleanEnum.YES.getValue()
        ).toList();
        if (!filterList.isEmpty()) {
            throw exception(QUALITY_ITEM_SKU_SUCCESS);
        }

        List<Long> inspectionIdList = itemDOList.stream().map(QualityInspectionItemDO::getInspectionId).distinct().toList();
        if (inspectionIdList.isEmpty()) {
            throw exception(QUALITY_INSPECTION_NOT_EXISTS);
        }
        if (inspectionIdList.size() > 1) {
            throw exception(QUALITY_CODE_NOT_SAME);
        }
        QualityInspectionDO qualityInspectionDO = qualityInspectionMapper.selectById(inspectionIdList.get(0));
        if (Objects.isNull(qualityInspectionDO)) {
            throw exception(QUALITY_INSPECTION_NOT_EXISTS);
        }
        List<QualityInspectionReworkItemReqVO> itemReqVOS = reworkReqVO.getItemList();

        itemDOList.forEach(s -> {
            Optional<QualityInspectionReworkItemReqVO> first = itemReqVOS.stream().filter(i -> Objects.equals(i.getItemId(), s.getId())).findFirst();
            if (first.isPresent()) {
                s.setHandleFlag(QmsHandleStateEnum.REWORK.getValue());
                s.setReworkDesc(first.get().getReworkDesc());
//                s.setPurchaseContractItemId(first.get().getItemId());
            }
        });

        inspectionItemMapper.updateBatch(itemDOList);
        //记录日志
        AdminUserRespDTO user = adminUserApi.getUser(WebFrameworkUtils.getLoginUserId());
        OperateLogUtils.setContent(String.format("【验货单】%s 执行返工重验 %s", user.getNickname(), qualityInspectionDO.getCode()));
        OperateLogUtils.addExt(OPERATOR_EXTS_KEY, qualityInspectionDO.getCode());
        QualityInspectionSaveReqVO createReqVO = BeanUtils.toBean(qualityInspectionDO, QualityInspectionSaveReqVO.class);
        createReqVO.setId(null).setCode(null);
        createReqVO.setReworkFlag(BooleanEnum.NO.getValue()).setReinspectionFlag(BooleanEnum.YES.getValue());
        createReqVO.setPlanInspectionTime(reworkReqVO.getReworkInspectionTime());
        createReqVO.setSourceId(qualityInspectionDO.getId()).setSourceCode(qualityInspectionDO.getCode());
        createReqVO.setAnnex(null);
        createReqVO.setAmount(new JsonAmount());

        List<QualityInspectionItemRespVO> children = BeanUtils.toBean(itemDOList, QualityInspectionItemRespVO.class);
        Map<Long,UpdateInspectionDTO> updateInspectionDTOMap = new HashMap<>();
        children.forEach(s -> {
            if (InspectionNodeEnum.FINAL.getCode().equals(qualityInspectionDO.getInspectionNode())){
                UpdateInspectionDTO updateInspectionDTO = new UpdateInspectionDTO();
                Integer inspectionStatus = s.getInspectionStatus();
                Integer handleFlag = s.getHandleFlag();
                updateInspectionDTO.setCheckStatus(inspectionStatus);
                updateInspectionDTO.setHandleFlag(handleFlag);
                updateInspectionDTO.setInspectionTime(qualityInspectionDO.getInspectionTime());
                updateInspectionDTO.setQuantity(s.getQuantity());
                updateInspectionDTOMap.put(s.getPurchaseContractItemId(), updateInspectionDTO);
            }
            s.setHandleFlag(null);
            s.setReworkDesc(null);
            s.setInspectionStatus(null);
        });
        if (CollUtil.isNotEmpty(updateInspectionDTOMap)){
            purchaseContractApi.updatePurchaseInspectionData(updateInspectionDTOMap);
        }
        List<QualityInspectionItemSaveReqVO> reworkItemSaveList = BeanUtils.toBean(children, QualityInspectionItemSaveReqVO.class);
        reworkItemSaveList.forEach(x -> x.setId(null).setInspectionId(null).setLastFailDesc(x.getFailDesc()).setFailDesc(null));
        createReqVO.setItemSaveReqVOList(reworkItemSaveList);
        this.createQualityInspection(createReqVO);


        return true;
    }

    @Override
    public Long checkItemList(List<Long> qualityInspectionItemIdList) {

        List<QualityInspectionItemDO> itemDOList = inspectionItemMapper.selectList(QualityInspectionItemDO::getId, qualityInspectionItemIdList);
        if (CollUtil.isEmpty(itemDOList)) {
            throw exception(QUALITY_INSPECTION_ITEM_NOT_EXISTS);
        }
        List<Long> list = itemDOList.stream().map(QualityInspectionItemDO::getInspectionId).distinct().toList();
        if (list.isEmpty()) {
            throw exception(QUALITY_INSPECTION_NOT_EXISTS);
        }
        if (list.size() > 1) {
            throw exception(QUALITY_CODE_NOT_SAME);
        }
        return list.get(0);
    }

    @Override
    public void closeInspection(Long id) {
        QualityInspectionDO qualityInspectionDO = validateQualityInspectionExists(id);
        qualityInspectionDO.setQualityInspectionStatus(InspectionBillStatusEnum.CASE_SETTLED.getValue());
        orderLinkApi.updateOrderLinkStatus(qualityInspectionDO.getCode(), BusinessNameDict.QUALITY_INSPECTION_NAME,qualityInspectionDO.getLinkCodeList(),InspectionBillStatusEnum.CASE_SETTLED.getValue());
        qualityInspectionMapper.updateById(qualityInspectionDO);
    }

    @Override
    public Boolean updateQualityInspectionAmount(QualityInspectionSaveAmountReqVO updateReqVO) {
        QualityInspectionDO qualityInspectionDO = qualityInspectionMapper.selectById(updateReqVO.getId());
        if (Objects.isNull(qualityInspectionDO)) {
            throw exception(QUALITY_INSPECTION_NOT_EXISTS);
        }
        qualityInspectionDO.setAmount(updateReqVO.getAmount());
        return qualityInspectionMapper.updateById(qualityInspectionDO) > 0;
    }

    @Override
    public void exportQualityInspectionExcel(QualityInspectionPageReqVO pageReqVO, HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        PageResult<QualityInspectionRespVO> qualityInspectionPage = getQualityInspectionPage(pageReqVO);
        List<QualityInspectionExportRespVO> exportList = new ArrayList<>();
        List<QualityInspectionRespVO> list = qualityInspectionPage.getList();
        if (CollUtil.isNotEmpty(list)) {
            Integer sort = 1;
            for (QualityInspectionRespVO qualityInspectionRespVO : list) {
                List<QualityInspectionItemRespVO> children = qualityInspectionRespVO.getChildren();
                if (children.isEmpty()) {
                    throw exception(QUALITY_INSPECTION_ITEM_NOT_EXISTS);
                }

                QualityInspectionExportRespVO exportVO = new QualityInspectionExportRespVO();
                exportVO.setSortNumb(sort);
                sort++;
                exportVO.setInspectionTime(qualityInspectionRespVO.getInspectionTime());
                exportVO.setPurchaseContractCode(qualityInspectionRespVO.getPurchaseContractCode());
                exportVO.setVenderName(qualityInspectionRespVO.getVenderName());

                // 设置采购员部门名称
                if (Objects.nonNull(qualityInspectionRespVO.getPurchaseUser())) {
                    exportVO.setBuyerName(qualityInspectionRespVO.getPurchaseUser().getNickname());
                    exportVO.setPurchaseDeptName(qualityInspectionRespVO.getPurchaseUser().getDeptName());
                }

                // 用"/"分隔多个产品信息
                List<String> cskuCodeList = children.stream().map(QualityInspectionItemRespVO::getCskuCode).toList();
                List<String> skuNameList = children.stream().map(QualityInspectionItemRespVO::getSkuName).toList();
                List<String> quantityList = children.stream().map(item -> String.valueOf(item.getQuantity())).toList();

                // 验货结论和问题点如果是空或null就不拼接
                List<String> inspectionStatusList = children.stream()
                        .map(item -> DictFrameworkUtils.getDictDataLabel("quality_inspection_status", item.getInspectionStatus()))
                        .filter(status -> StrUtil.isNotEmpty(status))
                        .toList();
                List<String> failDescList = children.stream()
                        .map(item -> item.getFailDesc())
                        .filter(desc -> StrUtil.isNotEmpty(desc))
                        .toList();

                exportVO.setCskuCode(String.join("/", cskuCodeList));
                exportVO.setSkuName(String.join("/", skuNameList));
                exportVO.setQuantity(String.join("/", quantityList));
                exportVO.setInspectionStatus(String.join("/", inspectionStatusList));
                exportVO.setFailDesc(String.join("/", failDescList));
                exportVO.setInspectorName(qualityInspectionRespVO.getInspectorName());
                // 设置验货费用
                if (Objects.nonNull(qualityInspectionRespVO.getAmount())) {
                    exportVO.setAmount(qualityInspectionRespVO.getAmount().getAmount().toString());
                }
                exportList.add(exportVO);
            }
        }
        ExcelUtils.write(response, "验货单-单据.xls", "2025年验货报告统计表", QualityInspectionExportRespVO.class, exportList);
    }

    @Override
    public List<SimpleQulityInspectionRespVO> getSimpleList(Long saleContractItemId) {
        if (Objects.isNull(saleContractItemId)) {
            return List.of();
        }
        List<Long> itemIds = purchaseContractApi.getItemIdsBySaleItems(saleContractItemId);
        if (CollUtil.isEmpty(itemIds)) {
            return List.of();
        }
        List<QualityInspectionItemDO> qualityInspectionItemDOS = inspectionItemMapper.selectList(QualityInspectionItemDO::getPurchaseContractItemId, itemIds);
        if (CollUtil.isEmpty(qualityInspectionItemDOS)) {
            return List.of();
        }
        return BeanUtils.toBean(qualityInspectionItemDOS, SimpleQulityInspectionRespVO.class);
    }


    private void validateQualityInspectionItemExists(Long id) {
        if (inspectionItemMapper.selectById(id) == null) {
            throw exception(QUALITY_INSPECTION_ITEM_NOT_EXISTS);
        }
    }

    private void deleteQualityInspectionItemByInspectionId(Long inspectionId) {
        inspectionItemMapper.deleteByInspectionId(inspectionId);
    }

    @Override
    public void exportWord(Long id, String reportCode, Long reportId, Long companyId, HttpServletResponse response) {
        if (id == null) {
            throw exception(QUALITY_INSPECTION_NOT_EXISTS);
        }
        if (reportCode == null) {
            throw exception(REPORTCODE_NULL);
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
        QualityInspectionDO qualityInspectionDO = qualityInspectionMapper.selectById(id);
        if (qualityInspectionDO == null) {
            throw exception(QUALITY_INSPECTION_NOT_EXISTS);
        }
        List<QualityInspectionItemDO> qualityInspectionItemDOList = inspectionItemMapper.selectList(QualityInspectionItemDO::getInspectionId, id);
        if (CollUtil.isEmpty(qualityInspectionItemDOList)) {
            throw exception(QUALITY_INSPECTION_ITEM_NOT_EXISTS);
        }
        // 构造表格参数
        HashMap<String, Object> params = new HashMap<>();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String planInspectionTime = "";
        if (qualityInspectionDO.getPlanInspectionTime() != null) {
            planInspectionTime = dtf.format(qualityInspectionDO.getPlanInspectionTime());
        }
        // 构造表格头
        RowRenderData row0 = Rows.of("提交日期：                                 验          货          任         务         单", null, null, null, null, null, null,null,null,null).rowExactHeight(1).center().create();
        TableRenderData table = Tables.ofA4ExtendWidth().percentWidth("100%", new int[]{10, 10, 10, 5, 10, 10, 10 ,10 ,10 ,15}).create();
        table.addRow(row0);
        MergeCellRule.MergeCellRuleBuilder ruleBuilder = MergeCellRule.builder();
        final int[] currentRow = {Integer.parseInt(Long.toString(table.getRows().stream().count()))};
        currentRow[0] = Integer.parseInt(Long.toString(table.getRows().stream().count()));
        ruleBuilder.map(MergeCellRule.Grid.of(currentRow[0] - 1, 0), MergeCellRule.Grid.of(currentRow[0] - 1, 9));
        table.addRow(Rows.of("要求验货日期：", planInspectionTime, "订单号", qualityInspectionDO.getCode(),null, "工厂", qualityInspectionDO.getVenderName(),null,null, "联系人："+qualityInspectionDO.getFactoryContacter()).rowExactHeight(1).center().create());
        currentRow[0] = Integer.parseInt(Long.toString(table.getRows().stream().count()));
        ruleBuilder.map(MergeCellRule.Grid.of(currentRow[0] - 1, 3), MergeCellRule.Grid.of(currentRow[0] - 1, 4));
        ruleBuilder.map(MergeCellRule.Grid.of(currentRow[0] - 1, 6), MergeCellRule.Grid.of(currentRow[0] - 1, 8));
        table.addRow(Rows.of("地址（请填最新地址）：", qualityInspectionDO.getInspectionAddress(),null,null,null,"电话(请填工厂跟单员座机与手机）",null,null,null,null).rowExactHeight(1).center().create());
        currentRow[0] = Integer.parseInt(Long.toString(table.getRows().stream().count()));
        ruleBuilder.map(MergeCellRule.Grid.of(currentRow[0] - 1, 3), MergeCellRule.Grid.of(currentRow[0] - 1, 4));
        ruleBuilder.map(MergeCellRule.Grid.of(currentRow[0] - 1, 6), MergeCellRule.Grid.of(currentRow[0] - 1, 7));
        String trackUserName = "";
        String custName = "";
        if (qualityInspectionDO.getPurchaseContractCode() != null) {
            PurchaseContractAllDTO purchaseContractByCode = purchaseContractApi.getPurchaseContractByCode(qualityInspectionDO.getPurchaseContractCode());
            if(purchaseContractByCode.getManager()!=null){
                trackUserName = purchaseContractByCode.getManager().getNickname();
            }
            custName = purchaseContractByCode.getCustName();
        }
        table.addRow(Rows.of("跟单员", trackUserName,"验货员",qualityInspectionDO.getInspectorName(),null,"客户",custName,null,"电商客户",null).rowExactHeight(1).center().create());
        currentRow[0] = Integer.parseInt(Long.toString(table.getRows().stream().count()));
        ruleBuilder.map(MergeCellRule.Grid.of(currentRow[0] - 1, 3), MergeCellRule.Grid.of(currentRow[0] - 1, 4));
        ruleBuilder.map(MergeCellRule.Grid.of(currentRow[0] - 1, 6), MergeCellRule.Grid.of(currentRow[0] - 1, 7));
        table.addRow(Rows.of("是否需要样品", null,"样品数量",null,null,"是否返单",null,null,"验货性质(早期,中期,晚期)",null).rowExactHeight(1).center().create());
        currentRow[0] = Integer.parseInt(Long.toString(table.getRows().stream().count()));
        ruleBuilder.map(MergeCellRule.Grid.of(currentRow[0] - 1, 3), MergeCellRule.Grid.of(currentRow[0] - 1, 4));
        ruleBuilder.map(MergeCellRule.Grid.of(currentRow[0] - 1, 6), MergeCellRule.Grid.of(currentRow[0] - 1, 7));
        table.addRow(Rows.of("品名", "产品编号","客户货号","订单","数量","数量","总箱量","产品包装方式","产品条码",null).rowExactHeight(1).center().create());
        table.addRow(Rows.of(null, null,null,"数量","外箱","内盒",null,null,null,null).rowExactHeight(1).center().create());
        currentRow[0] = Integer.parseInt(Long.toString(table.getRows().stream().count()));
        ruleBuilder.map(MergeCellRule.Grid.of(currentRow[0] - 2, 0), MergeCellRule.Grid.of(currentRow[0] - 1, 0));
        ruleBuilder.map(MergeCellRule.Grid.of(currentRow[0] - 2, 1), MergeCellRule.Grid.of(currentRow[0] - 1, 1));
        ruleBuilder.map(MergeCellRule.Grid.of(currentRow[0] - 2, 2), MergeCellRule.Grid.of(currentRow[0] - 1, 2));
        ruleBuilder.map(MergeCellRule.Grid.of(currentRow[0] - 2, 6), MergeCellRule.Grid.of(currentRow[0] - 1, 6));
        ruleBuilder.map(MergeCellRule.Grid.of(currentRow[0] - 2, 7), MergeCellRule.Grid.of(currentRow[0] - 1, 7));
        ruleBuilder.map(MergeCellRule.Grid.of(currentRow[0] - 2, 8), MergeCellRule.Grid.of(currentRow[0] - 1, 8));
        ruleBuilder.map(MergeCellRule.Grid.of(currentRow[0] - 2, 9), MergeCellRule.Grid.of(currentRow[0] - 1, 9));
        // 构造明细行
        //        for (QualityInspectionItemDO item : qualityInspectionItemDOList) {
//            RowRenderData dataRow = Rows.of(
//                item.getSkuName(),
//                item.getSkuCode(),
//                null, // 图片
//                item.getDesc(),
//                item.getQuantity() != null ? item.getQuantity().toString() : "",
//                item.getBoxCount() != null ? item.getBoxCount().toString() : "",
//                item.getPackageType() != null ? item.getPackageType().toString() : ""
//            ).center().create();
//            // 图片处理
//            if (item.getPicture() != null && !item.getPicture().isEmpty()) {
//                String inputPath = item.getPicture().get(0).getFileUrl();
//                try {
//                    byte[] content = fileApi.getFileContent(inputPath.substring(inputPath.lastIndexOf("get/") + 4));
//                    PictureRenderData pictureRenderData = Pictures.ofBytes(content).center().size(80, 80).create();
//                    List<ParagraphRenderData> paragraphs = new ArrayList<>();
//                    ParagraphRenderData paragraphRenderData = new ParagraphRenderData();
//                    paragraphRenderData.addPicture(pictureRenderData);
//                    paragraphs.add(paragraphRenderData);
//                    dataRow.getCells().get(2).setParagraphs(paragraphs); // 第3列为图片
//                } catch (Exception e) {
//                    logger.warn("图片加载失败：" + e.getMessage());
//                }
//            }
//            table.addRow(dataRow);
//        }
        params.put("table", table);
        table.setMergeRule(ruleBuilder.build());
        String projectPath = System.getProperty("user.dir");
        reportApi.exportWord(response, reportDTO.getAnnex().get(0).getFileUrl(), projectPath + "/output.docx", params);
    }

    @Override
    public void exportExcel(Long id, String reportCode, HttpServletResponse response) {
        if (id == null) {
            throw exception(QUALITY_INSPECTION_NOT_EXISTS);
        }
        if (reportCode == null) {
            throw exception(REPORTCODE_NULL);
        }
        QualityInspectionDO qualityInspectionDO = qualityInspectionMapper.selectById(id);
        if (qualityInspectionDO == null) {
            throw exception(QUALITY_INSPECTION_NOT_EXISTS);
        }
        ReportDTO reportDTO = null;
        if (qualityInspectionDO.getCompanyId() != null) {
            reportDTO = reportApi.getCompanyReport(reportCode, qualityInspectionDO.getCompanyId());
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
        List<QualityInspectionItemDO> qualityInspectionItemDOList = inspectionItemMapper.selectList(new LambdaQueryWrapperX<QualityInspectionItemDO>().eq(QualityInspectionItemDO::getInspectionId, id));
        List<QualityInspectionItemExportVO> qualityInspectionItemExportVOList = new ArrayList<>();
        List<PackageTypeDTO> packageTypeList = packageTypeApi.getList();
        List<Long> purchaseItemIds = qualityInspectionItemDOList.stream().map(QualityInspectionItemDO::getPurchaseContractItemId).toList();
        Map<Long, PurchaseContractItemDTO> purchaseMap = purchaseContractApi.getItemMapByItemIds(purchaseItemIds);

        for (QualityInspectionItemDO item : qualityInspectionItemDOList) {
            QualityInspectionItemExportVO vo = new QualityInspectionItemExportVO();
            vo.setSkuName(item.getSkuName());
            vo.setSkuCode(item.getBasicSkuCode());  //2873打印要求编号为基础产品编号
            vo.setCskuCode(item.getCskuCode());
            vo.setQuantity(item.getQuantity());
            vo.setQtyPerOuterbox(item.getQtyPerOuterbox());
            vo.setBoxCount(item.getBoxCount());
            if (CollUtil.isNotEmpty(packageTypeList) && CollUtil.isNotEmpty(item.getPackageType())) {
                List<Long> distinctPackageType = item.getPackageType().stream().distinct().toList();
                List<PackageTypeDTO> tempPackageTypeList = packageTypeList.stream().filter(pt -> distinctPackageType.contains(pt.getId())).toList();
                if (CollUtil.isNotEmpty(tempPackageTypeList)) {
                    List<String> packageTypeNameList = tempPackageTypeList.stream().map(PackageTypeDTO::getName).distinct().toList();
                    vo.setPackageTypeName(String.join(",", packageTypeNameList));
                }
            }
            if (CollUtil.isNotEmpty(purchaseMap)){
                PurchaseContractItemDTO purchaseContractItemDTO = purchaseMap.get(item.getPurchaseContractItemId());
                if (Objects.nonNull(purchaseContractItemDTO)){
                    vo.setDescription(purchaseContractItemDTO.getDescription());
                }
            }
            if(item.getMainPicture()!=null){
                String inputPath = item.getMainPicture().getFileUrl();
                try {
                    byte[] content = fileApi.getFileContent(inputPath.substring(inputPath.lastIndexOf("get/") + 4));
                    File inputFile = FileUtils.createTempFile(content);
                    BufferedImage image = ImageIO.read(inputFile);
                    Double width = Double.valueOf(image.getWidth());
                    Double height = Double.valueOf(image.getHeight());
                    WriteCellData<Void> voidWriteCellData = ExcelUtils.imageCells(content,width,height,2.0,2.0,0,0);
                    vo.setCptp(voidWriteCellData);
                    inputFile.delete();
                } catch (Exception e) {
                    logger.info("验货单导出图片获取失败"+e.getMessage());
                }
            }
            qualityInspectionItemExportVOList.add(vo);
        }
        HashMap<String, Object> params = new HashMap();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        if (qualityInspectionDO.getPlanInspectionTime() != null) {
            String inputDate = dtf.format(qualityInspectionDO.getPlanInspectionTime());
            params.put("planInspectionTime", inputDate);
        }
        if (qualityInspectionDO.getCreateTime() != null) {
            String createTime = dtf.format(qualityInspectionDO.getCreateTime());
            params.put("createTime", createTime);
        }

        params.put("venderName", qualityInspectionDO.getVenderName());
        params.put("code", qualityInspectionDO.getPurchaseContractCode());
        params.put("factoryContacter", qualityInspectionDO.getFactoryContacter());
        params.put("inspectionAddress", qualityInspectionDO.getInspectionAddress());
        params.put("specialAttentionNotice", qualityInspectionDO.getSpecialAttentionNotice());
        if (qualityInspectionDO.getPurchaseContractCode() != null) {
            PurchaseContractAllDTO purchaseContractByCode = purchaseContractApi.getPurchaseContractByCode(qualityInspectionDO.getPurchaseContractCode());
            if(purchaseContractByCode.getManager()!=null){
                params.put("trackUserName", purchaseContractByCode.getManager().getNickname());
            }
            params.put("custName", purchaseContractByCode.getCustName());
        }
        params.put("inspectorName", qualityInspectionDO.getInspectorName());
        params.put("factoryTelephone", qualityInspectionDO.getFactoryTelephone());

        String inputPath = reportDTO.getAnnex().get(0).getFileUrl();
        byte[] content = null;
        String path = "";
        try {
            path = inputPath.substring(inputPath.lastIndexOf("get/") + 4);
            content = fileApi.getFileContent(path);
        } catch (Exception e) {
            throw exception(TEMPLETE_DOWNLOAD_FAIL, "path-" + path);
        }
        ByteArrayInputStream templateFileInputStream = new ByteArrayInputStream(content);
        try {
            // 2. 用POI打开临时文件并处理明细块
            Workbook workbook = WorkbookFactory.create(templateFileInputStream);
            Sheet sheet = workbook.getSheetAt(0);
            int blockStartRow = 8; // 第9行（下标0开始）
            int blockRowCount = 3;
            for (int i = 0; i < qualityInspectionItemExportVOList.size(); i++) {
                int targetStartRow = blockStartRow + i * blockRowCount;
                sheet.shiftRows(targetStartRow, sheet.getLastRowNum(), blockRowCount, true, false);
                // 复制缓存的模板区块到新插入的空行
                for (int j = 0; j < blockRowCount; j++) {
                    sheet.createRow(targetStartRow + j);
                }
                // 2. 合并单元格
                CellRangeAddress[] regions = {
                        new CellRangeAddress(targetStartRow, targetStartRow, 3, 4),
                        new CellRangeAddress(targetStartRow, targetStartRow, 6, 7),
                        new CellRangeAddress(targetStartRow, targetStartRow, 8, 9),
                        new CellRangeAddress(targetStartRow, targetStartRow, 10, 11),
                        new CellRangeAddress(targetStartRow, targetStartRow, 12, 14),
                        new CellRangeAddress(targetStartRow, targetStartRow, 15, 16),
                        new CellRangeAddress(targetStartRow + 1, targetStartRow + 1, 1, 9),
                        new CellRangeAddress(targetStartRow + 1, targetStartRow + 1, 10, 17),
                        new CellRangeAddress(targetStartRow + 2, targetStartRow + 2, 1, 9),
                        new CellRangeAddress(targetStartRow + 2, targetStartRow + 2, 10, 17)
                };
                for (CellRangeAddress region : regions) {
                    sheet.addMergedRegion(region);
                    setRegionBorder(sheet, workbook, region);
                }
                // 3. 填充数据（请根据实际字段和模板单元格调整）
                QualityInspectionItemExportVO qualityInspectionItemExportVO = qualityInspectionItemExportVOList.get(i);
                setCellValueSafe(sheet, targetStartRow, 1, qualityInspectionItemExportVO.getSkuName());
                setCellValueSafe(sheet, targetStartRow, 2, qualityInspectionItemExportVO.getSkuCode());
                setCellValueSafe(sheet, targetStartRow, 3, qualityInspectionItemExportVO.getCskuCode());
                setCellValueSafe(sheet, targetStartRow, 5, qualityInspectionItemExportVO.getQuantity() != null ? qualityInspectionItemExportVO.getQuantity().toString() : "0");
                setCellValueSafe(sheet, targetStartRow, 6, qualityInspectionItemExportVO.getQtyPerOuterbox() != null ? qualityInspectionItemExportVO.getQtyPerOuterbox().toString() : "0");
                setCellValueSafe(sheet, targetStartRow, 10, qualityInspectionItemExportVO.getBoxCount() != null ? qualityInspectionItemExportVO.getBoxCount().toString() : "0");
                setCellValueSafe(sheet, targetStartRow, 12, qualityInspectionItemExportVO.getPackageTypeName());
                setCellValueSafe(sheet, targetStartRow+1, 1, "产品描述：");
                setCellValueSafe(sheet, targetStartRow+1, 10, "样品或者图片");
                CellStyle centerStyle = sheet.getWorkbook().createCellStyle();
                centerStyle.setAlignment(HorizontalAlignment.CENTER);
                centerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                centerStyle.setWrapText(true);
                // 设置字体大小（可选）
                Font font = sheet.getWorkbook().createFont();
                font.setFontHeightInPoints((short) 10);
                centerStyle.setFont(font);
                String description = qualityInspectionItemExportVO.getDescription();
                setCellValueSafe(sheet, targetStartRow+2, 1, description);
                setCellValueSafe(sheet, targetStartRow+2, 10, "");
                // 设置描述和图片单元格行高、内容居中
                Row descPicRow = sheet.getRow(targetStartRow+2);
                descPicRow.setRowNum(targetStartRow+2);
                if (StrUtil.isNotEmpty(description)) {
                    // 估算需要的行数（假设每行最多50个字符）
                    int lines = (description.length() / 10) + 1;
                    // 设置行高，每行大约20点（可根据需要调整）
                    descPicRow.setHeightInPoints(lines * 20);
                } else {
                    // 默认行高
                    descPicRow.setHeightInPoints(40);
                }
                // 设置描述单元格居中
                Cell descCell = descPicRow.getCell(1);
                if (descCell != null) {
                    descCell.setCellStyle(centerStyle);
                }
                // 设置图片单元格居中
                Cell picCell = descPicRow.getCell(10);
                if (picCell != null) {
                    picCell.setCellStyle(centerStyle);
                }
                // 直接插入图片
                if (qualityInspectionItemExportVO.getCptp() != null && qualityInspectionItemExportVO.getCptp().getImageDataList() != null && !qualityInspectionItemExportVO.getCptp().getImageDataList().isEmpty()) {
                    try {
                        ImageData imageData = qualityInspectionItemExportVO.getCptp().getImageDataList().get(0);
                        byte[] imageBytes = imageData.getImage();
                        if (imageBytes != null) {
                            int pictureIdx = workbook.addPicture(imageBytes, Workbook.PICTURE_TYPE_JPEG);
                            CreationHelper helper = workbook.getCreationHelper();
                            Drawing<?> drawing = sheet.createDrawingPatriarch();
                            ClientAnchor anchor = helper.createClientAnchor();

                            // 设置锚点位置 - 图片插入到第11列，占用第11-17列
                            anchor.setCol1(10); // 第11列（起始列）
                            anchor.setCol2(16); // 第17列（结束列，减少一列避免超出边界）
                            anchor.setRow1(targetStartRow + 2); // 起始行（产品描述行）
                            anchor.setRow2(targetStartRow + 2); // 结束行（只占一行）

                            int displayWidth = 170; // 固定宽度170像素
                            int displayHeight = 170;
                            
                            int centerOffsetX = 50;
                            int centerOffsetY = 50; // 简单的垂直偏移
                            
                            // 只有当行高度小于图片高度时才调整行高
                            Row imageRow = sheet.getRow(targetStartRow + 2);
                            if (imageRow != null) {
                                int requiredHeightInPoints = Math.max(60, (int)(displayHeight * 0.75));
                                int currentRowHeightInPoints = (int) imageRow.getHeightInPoints();
                                // 只有当当前行高度小于图片所需高度时才调整
                                if (currentRowHeightInPoints < requiredHeightInPoints) {
                                    imageRow.setHeightInPoints(requiredHeightInPoints);
                                }
                            }
                            // 将像素转换为EMU（Excel度量单位）
                            int emuPerPixel = 9525;
                            anchor.setDx1(centerOffsetX * emuPerPixel); // 水平居中偏移
                            anchor.setDx2((centerOffsetX + displayWidth) * emuPerPixel); // 设置宽度
                            anchor.setDy1(centerOffsetY * emuPerPixel); // 垂直居中偏移
                            anchor.setDy2((centerOffsetY + displayHeight) * emuPerPixel); // 设置高度
                            drawing.createPicture(anchor, pictureIdx);
                        }
                    } catch (Exception e) {
                        logger.warn("插入图片失败: " + e.getMessage());
                    }
                }
                // 4. 给整个块加边框
                for (int r = targetStartRow; r < targetStartRow + blockRowCount; r++) {
                    Row row = sheet.getRow(r);
                    for (int c = 1; c < 18; c++) {
                        Cell cell = row.getCell(c);
                        if (cell != null) {
                            CellStyle style = workbook.createCellStyle();
                            style.cloneStyleFrom(cell.getCellStyle());
                            style.setBorderTop(BorderStyle.THIN);
                            style.setBorderBottom(BorderStyle.THIN);
                            style.setBorderLeft(BorderStyle.THIN);
                            style.setBorderRight(BorderStyle.THIN);
                            cell.setCellStyle(style);
                        }
                    }
                }
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            workbook.close();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(out.toByteArray());
            ExcelUtils.writeByTemplate(response, inputStream, params, "验货单.xlsx", null, null, 600);
        } catch (Exception e) {
            throw exception(EXCEL_EXPORT_FAIL, e.getMessage());
        }
    }
    private void setCellValueSafe(Sheet sheet, int rowIndex, int colIndex, String value) {
        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            row = sheet.createRow(rowIndex);
        }
        Cell cell = row.getCell(colIndex);
        if (cell == null) {
            cell = row.createCell(colIndex);
        }
        cell.setCellValue(value);
    }
    private void setRegionBorder(Sheet sheet, Workbook workbook, CellRangeAddress region) {
        for (int r = region.getFirstRow(); r <= region.getLastRow(); r++) {
            Row row = sheet.getRow(r);
            if (row == null) row = sheet.createRow(r);
            for (int c = region.getFirstColumn(); c <= region.getLastColumn(); c++) {
                Cell cell = row.getCell(c);
                if (cell == null) cell = row.createCell(c);
                CellStyle style = workbook.createCellStyle();
                style.cloneStyleFrom(cell.getCellStyle());
                style.setBorderTop(BorderStyle.THIN);
                style.setBorderBottom(BorderStyle.THIN);
                style.setBorderLeft(BorderStyle.THIN);
                style.setBorderRight(BorderStyle.THIN);
                cell.setCellStyle(style);
            }
        }
    }
}
