package com.syj.eplus.module.scm.service.purchaseplanitem;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.bpm.api.task.BpmProcessInstanceApi;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import cn.iocoder.yudao.module.bpm.enums.task.BpmProcessInstanceResultEnum;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.framework.common.enums.BooleanEnum;
import com.syj.eplus.framework.common.enums.ConvertedFlagEnum;
import com.syj.eplus.framework.common.enums.PurchasePlanStatusEnum;
import com.syj.eplus.module.scm.api.vender.dto.SimpleVenderRespDTO;
import com.syj.eplus.module.scm.controller.admin.purchaseplanitem.vo.PurchasePlanItemDetailReq;
import com.syj.eplus.module.scm.controller.admin.purchaseplanitem.vo.PurchasePlanItemPageReqVO;
import com.syj.eplus.module.scm.controller.admin.purchaseplanitem.vo.PurchasePlanItemRespVO;
import com.syj.eplus.module.scm.controller.admin.purchaseplanitem.vo.PurchasePlanItemSaveReqVO;
import com.syj.eplus.module.scm.convert.PurchasePlanItemConvert;
import com.syj.eplus.module.scm.dal.dataobject.purchaseplanitem.PurchasePlanItemDO;
import com.syj.eplus.module.scm.dal.mysql.purchaseplanitem.PurchasePlanItemMapper;
import com.syj.eplus.module.scm.service.vender.VenderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.scm.enums.ErrorCodeConstants.PURCHASE_PLAN_ITEM_NOT_EXISTS;

/**
 * 采购计划明细 Service 实现类
 *
 * @author zhangcm
 */
@Service
@Validated
public class PurchasePlanItemServiceImpl extends ServiceImpl<PurchasePlanItemMapper, PurchasePlanItemDO> implements PurchasePlanItemService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private PurchasePlanItemMapper purchasePlanItemMapper;
    @Resource
    private BpmProcessInstanceApi bpmProcessInstanceApi;

    @Resource
    private VenderService venderService;

    private static final String PROCESS_DEFINITION_KEY = "${table.processKey}";


    @Override
    public Long createPurchasePlanItem(PurchasePlanItemSaveReqVO createReqVO) {

        var purchasePlanItem = BeanUtils.toBean(createReqVO, PurchasePlanItemDO.class);
        purchasePlanItemMapper.insert(purchasePlanItem);
        return purchasePlanItem.getId();
    }

    @Override
    public void updatePurchasePlanItem(PurchasePlanItemSaveReqVO updateReqVO) {
        // 校验存在
        validatePurchasePlanItemExists(updateReqVO.getId());
        // 更新
        PurchasePlanItemDO updateObj = PurchasePlanItemConvert.INSTANCE.convertPurchasePlanItemDO(updateReqVO);
        purchasePlanItemMapper.updateById(updateObj);
    }

    @Override
    public void deletePurchasePlanItem(Long id) {
        // 校验存在
        validatePurchasePlanItemExists(id);
        // 删除
        purchasePlanItemMapper.deleteById(id);
    }

    private void validatePurchasePlanItemExists(Long id) {
        if (purchasePlanItemMapper.selectById(id) == null) {
            throw exception(PURCHASE_PLAN_ITEM_NOT_EXISTS);
        }
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
    public void submitTask(Long purchasePlanItemId, Long userId) {
        bpmProcessInstanceApi.createProcessInstance(userId, PROCESS_DEFINITION_KEY, purchasePlanItemId);
        updateAuditStatus(purchasePlanItemId, BpmProcessInstanceResultEnum.PROCESS.getResult());
    }

    @Override
    public void updateAuditStatus(Long auditableId, Integer auditStatus) {
        purchasePlanItemMapper.updateById(PurchasePlanItemDO.builder().id(auditableId).build());
    }

    @Override
    public void batchSavePlanItem(List<PurchasePlanItemSaveReqVO> itemList) {
        if (CollUtil.isEmpty(itemList))
            return;
        List<String> venderCodeList = itemList.stream().map(PurchasePlanItemSaveReqVO::getVenderCode).distinct().toList();

        List<SimpleVenderRespDTO> simpleVenderRespDTOListByCodeList = venderService.getSimpleVenderRespDTOListByCodeList(venderCodeList);

        itemList.forEach(item -> {

            item.setId(null);
            if (Objects.nonNull(item.getWithTaxPrice())) {
                BigDecimal amount = item.getWithTaxPrice().getAmount();
                if(Objects.isNull(item.getNeedPurQuantity())){
                    item.setNeedPurQuantity(0);
                }
                BigDecimal total = amount.multiply(BigDecimal.valueOf(item.getNeedPurQuantity()));
                item.setAmount(new JsonAmount().setCurrency(item.getCurrency()).setAmount(total));
                if(Objects.nonNull(item.getTaxRate())){
                    BigDecimal unitAmount = amount.multiply(BigDecimal.valueOf(100)).divide(item.getTaxRate().add(BigDecimal.valueOf(100)),2, RoundingMode.HALF_UP);
                    item.setUnitPrice(new JsonAmount().setCurrency(item.getWithTaxPrice().getCurrency()).setAmount(unitAmount));
                }
            }
            if (CollUtil.isNotEmpty(simpleVenderRespDTOListByCodeList)) {
                Optional<SimpleVenderRespDTO> first = simpleVenderRespDTOListByCodeList.stream().filter(s ->
                        Objects.equals(s.getCode(), item.getVenderCode())).findFirst();
                first.ifPresent(s -> item.setVenderId(s.getId()));
            }
            var itemDO = BeanUtils.toBean(item, PurchasePlanItemDO.class);
            itemDO.setUniqueCode(IdUtil.fastSimpleUUID());
            purchasePlanItemMapper.insert(itemDO);
        });
    }

    @Override
    public List<PurchasePlanItemDO> getPurchasePlanItemListByPurchasePlanId(Long id) {
        return purchasePlanItemMapper.selectList(new LambdaQueryWrapperX<PurchasePlanItemDO>().eq(PurchasePlanItemDO::getPurchasePlanId, id).ne(PurchasePlanItemDO::getCancelFlag, BooleanEnum.YES.getValue()));
    }

    @Override
    public void deletePurchasePlanItemListByPurchasePlanId(Long id) {
        purchasePlanItemMapper.delete(PurchasePlanItemDO::getPurchasePlanId, id);
    }

    @Override
    public List<PurchasePlanItemRespVO> getPurchasePlanItemListByPurchasePlanIdList(List<Long> idList) {
        List<PurchasePlanItemDO> itemDOList = purchasePlanItemMapper.selectList(
                new LambdaQueryWrapperX<PurchasePlanItemDO>().in(PurchasePlanItemDO::getPurchasePlanId, idList));
        if (CollUtil.isEmpty(itemDOList)) {
            return null;
        }
        return PurchasePlanItemConvert.INSTANCE.convertRespVOList(itemDOList);
    }

    @Override
    public List<PurchasePlanItemRespVO> getPurchasePlanItemListByIdList(List<Long> idList) {
        List<PurchasePlanItemDO> itemDOList = purchasePlanItemMapper.selectList(
                new LambdaQueryWrapperX<PurchasePlanItemDO>().in(PurchasePlanItemDO::getId, idList).ne(PurchasePlanItemDO::getCancelFlag, BooleanEnum.YES.getValue()));
        if (CollUtil.isEmpty(itemDOList)) {
            return null;
        }
        return PurchasePlanItemConvert.INSTANCE.convertRespVOList(itemDOList);
    }

    @Override
    public List<PurchasePlanItemRespVO> getNoConvertPurchasePlanItemListByIdList(List<Long> idList) {
        List<PurchasePlanItemDO> itemDOList = purchasePlanItemMapper.selectList(
                new LambdaQueryWrapperX<PurchasePlanItemDO>()
                        .inIfPresent(PurchasePlanItemDO::getId, idList)
                        .eqIfPresent(PurchasePlanItemDO::getConvertedFlag, ConvertedFlagEnum.NOT_CONVERTED.getStatus()).ne(PurchasePlanItemDO::getCancelFlag, BooleanEnum.YES.getValue())
        );
        if (CollUtil.isEmpty(itemDOList)) {
            return null;
        }
        return PurchasePlanItemConvert.INSTANCE.convertRespVOList(itemDOList);
    }

    @Override
    public List<String> getItemCodeListByPurchasePlanId(Long id) {
        List<PurchasePlanItemDO> purchasePlanItemDOS = purchasePlanItemMapper.selectList(new LambdaQueryWrapperX<PurchasePlanItemDO>().eq(PurchasePlanItemDO::getPurchasePlanId, id).ne(PurchasePlanItemDO::getCancelFlag, BooleanEnum.YES.getValue()));
        if (CollUtil.isEmpty(purchasePlanItemDOS)){
            return List.of();
        }
        return purchasePlanItemDOS.stream().map(PurchasePlanItemDO::getSkuCode).toList();
    }

    @Override
    public Integer updatePurchasePlanItemConvertedFlag(Set<Long> itemIdSet,Long purchasePlanId) {
        List<PurchasePlanItemDO> purchasePlanItemDOS = purchasePlanItemMapper.selectBatchIds(itemIdSet);
        if (CollUtil.isEmpty(purchasePlanItemDOS)){
            return PurchasePlanStatusEnum.COMPLETED.getCode();
        }
        purchasePlanItemDOS.stream().filter(s->!ConvertedFlagEnum.CONVERTED.getStatus().equals(s.getConvertedFlag())).forEach(s->{
            Integer needPurQuantity = s.getNeedPurQuantity();
            int purchaseQuantity = Objects.isNull(s.getPurchaseQuantity()) ? 0 : s.getPurchaseQuantity();
            int convertedQuantity = Objects.isNull(s.getConvertedQuantity()) ? 0 : s.getConvertedQuantity();
            if (needPurQuantity == purchaseQuantity + convertedQuantity) {
                s.setConvertedFlag(ConvertedFlagEnum.CONVERTED.getStatus());
            }else {
                if (convertedQuantity == 0){
                    s.setConvertedFlag(ConvertedFlagEnum.NOT_CONVERTED.getStatus());
                }else {
                    s.setConvertedFlag(ConvertedFlagEnum.PART_CONVERTED.getStatus());
                }
            }
            s.setConvertedQuantity(convertedQuantity+purchaseQuantity);
        });
        purchasePlanItemMapper.updateBatch(purchasePlanItemDOS);
        List<PurchasePlanItemDO> checkItemList = purchasePlanItemMapper.selectList(new LambdaQueryWrapperX<PurchasePlanItemDO>().eq(PurchasePlanItemDO::getPurchasePlanId, purchasePlanId));
        boolean exists = checkItemList.stream().anyMatch(s -> Objects.isNull(s.getConvertedQuantity()) || s.getNeedPurQuantity() > s.getConvertedQuantity());
        if (exists){
            return PurchasePlanStatusEnum.PROCUREMENT_PENDING.getCode();
        }else {
            return PurchasePlanStatusEnum.COMPLETED.getCode();
        }
    }

    @Override
    public List<SimpleFile> getAuxiliaryAnnexByContractCode(String purchaseContractCode) {
        if (StrUtil.isEmpty(purchaseContractCode)){
            return List.of();
        }
        List<PurchasePlanItemDO> purchasePlanItemDOS = purchasePlanItemMapper.selectList(new LambdaQueryWrapperX<PurchasePlanItemDO>().select(PurchasePlanItemDO::getAuxiliaryPurchaseContractCode,PurchasePlanItemDO::getAnnex).eq(PurchasePlanItemDO::getAuxiliaryPurchaseContractCode, purchaseContractCode));
        if (CollUtil.isEmpty(purchasePlanItemDOS)){
            return List.of();
        }
        return purchasePlanItemDOS.stream().map(PurchasePlanItemDO::getAnnex).filter(CollUtil::isNotEmpty).flatMap(List::stream).toList();
    }


    @Override
    public String getProcessDefinitionKey() {
        return PROCESS_DEFINITION_KEY;
    }

    @Override
    public String getOwnBrandProcessDefinitionKey() {
        return "";
    }

    @Override
    public PurchasePlanItemRespVO getPurchasePlanItem(PurchasePlanItemDetailReq purchasePlanItemDetailReq) {
        Long purchasePlanItemId = Objects.nonNull(purchasePlanItemDetailReq.getProcessInstanceId()) ? bpmProcessInstanceApi.selectAuditAbleIdByProcessInstanceId(purchasePlanItemDetailReq.getProcessInstanceId()) : purchasePlanItemDetailReq.getPurchasePlanItemId();
        if (Objects.isNull(purchasePlanItemId)) {
            logger.error("[采购计划明细]未获取到采购计划明细id");
            return null;
        }
        PurchasePlanItemDO purchasePlanItemDO = purchasePlanItemMapper.selectById(purchasePlanItemId);
        if (purchasePlanItemDO == null||BooleanEnum.YES.getValue().equals(purchasePlanItemDO.getCancelFlag())) {
            return null;
        }
        return PurchasePlanItemConvert.INSTANCE.convert(purchasePlanItemDO);
    }

    @Override
    public PageResult<PurchasePlanItemDO> getPurchasePlanItemPage(PurchasePlanItemPageReqVO pageReqVO) {
        return purchasePlanItemMapper.selectPage(pageReqVO);
    }

}
