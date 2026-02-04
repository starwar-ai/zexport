package com.syj.eplus.module.scm.service.purchaseauxiliaryallocation;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.bpm.api.task.BpmProcessInstanceApi;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import cn.iocoder.yudao.module.bpm.enums.task.BpmProcessInstanceResultEnum;
import com.syj.eplus.framework.common.enums.SubmitFlagEnum;
import com.syj.eplus.module.pms.api.sku.SkuApi;
import com.syj.eplus.module.pms.api.sku.dto.SkuDTO;
import com.syj.eplus.module.scm.controller.admin.purchaseauxiliaryallocation.vo.*;
import com.syj.eplus.module.scm.convert.purchaseauxiliaryallocation.PurchaseAuxiliaryAllocationConvert;
import com.syj.eplus.module.scm.dal.dataobject.purchaseauxiliaryallocation.PurchaseAuxiliaryAllocationDO;
import com.syj.eplus.module.scm.dal.dataobject.purchasecontractitem.PurchaseContractItemDO;
import com.syj.eplus.module.scm.dal.mysql.purchaseauxiliaryallocation.PurchaseAuxiliaryAllocationMapper;
import com.syj.eplus.module.scm.service.purchasecontractitem.PurchaseContractItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.scm.enums.ErrorCodeConstants.*;

/**
 * 采购合同辅料分摊 Service 实现类
 *
 * @author zhangcm
 */
@Service
@Validated
public class PurchaseAuxiliaryAllocationServiceImpl implements PurchaseAuxiliaryAllocationService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private PurchaseAuxiliaryAllocationMapper purchaseAuxiliaryAllocationMapper;
    @Resource
    private BpmProcessInstanceApi bpmProcessInstanceApi;
    @Resource
    private SkuApi skuApi;
    @Resource
    private PurchaseContractItemService contractItemService;
    private static final String PROCESS_DEFINITION_KEY = "${table.processKey}";


    @Override
    public Long createPurchaseAuxiliaryAllocation(PurchaseAuxiliaryAllocationSaveReqVO createReqVO) {
        PurchaseAuxiliaryAllocationDO purchaseAuxiliaryAllocation = PurchaseAuxiliaryAllocationConvert.INSTANCE.convertPurchaseAuxiliaryAllocationDO(createReqVO);
        // 插入
        purchaseAuxiliaryAllocationMapper.insert(purchaseAuxiliaryAllocation);
        if (SubmitFlagEnum.SUBMIT.getStatus().equals(createReqVO.getSubmitFlag())) {
            submitTask(purchaseAuxiliaryAllocation.getId(), WebFrameworkUtils.getLoginUserId());
        }
        // 返回
        return purchaseAuxiliaryAllocation.getId();
    }

    @Override
    public void updatePurchaseAuxiliaryAllocation(PurchaseAuxiliaryAllocationSaveReqVO updateReqVO) {
        // 校验存在
        validatePurchaseAuxiliaryAllocationExists(updateReqVO.getId());
        // 更新
        PurchaseAuxiliaryAllocationDO updateObj = PurchaseAuxiliaryAllocationConvert.INSTANCE.convertPurchaseAuxiliaryAllocationDO(updateReqVO);
        purchaseAuxiliaryAllocationMapper.updateById(updateObj);
    }

    @Override
    public void deletePurchaseAuxiliaryAllocation(Long id) {
        // 校验存在
        validatePurchaseAuxiliaryAllocationExists(id);
        // 删除
        purchaseAuxiliaryAllocationMapper.deleteById(id);
    }

    private void validatePurchaseAuxiliaryAllocationExists(Long id) {
        if (purchaseAuxiliaryAllocationMapper.selectById(id) == null) {
            throw exception(PURCHASE_AUXILIARY_ALLOCATION_NOT_EXISTS);
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
    public void submitTask(Long purchaseAuxiliaryAllocationId, Long userId) {
        bpmProcessInstanceApi.createProcessInstance(userId, PROCESS_DEFINITION_KEY, purchaseAuxiliaryAllocationId);
        updateAuditStatus(purchaseAuxiliaryAllocationId, BpmProcessInstanceResultEnum.PROCESS.getResult());
    }

    @Override
    public void updateAuditStatus(Long auditableId, Integer auditStatus) {
//    purchaseAuxiliaryAllocationMapper.updateById(PurchaseAuxiliaryAllocationDO.builder().id(auditableId).auditStatus(auditStatus).build());
    }

    @Override
    public List<PurchaseAuxiliaryAllocationDO> getListByPurcahseItemIdList(List<Long> idList) {
        return purchaseAuxiliaryAllocationMapper.selectList(new LambdaQueryWrapperX<PurchaseAuxiliaryAllocationDO>()
                .eq(PurchaseAuxiliaryAllocationDO::getPurchaseContractItemId,idList));
    }

    @Override
    public void batchInsert(List<PurchaseAuxiliaryAllocationDO> allocationDoList,Long contractId) {
        purchaseAuxiliaryAllocationMapper.delete(PurchaseAuxiliaryAllocationDO::getAuxiliaryPurchaseContractId,contractId);
        allocationDoList.forEach(s->{
            s.setId(null);
        });
        purchaseAuxiliaryAllocationMapper.insertBatch(allocationDoList);
    }

    @Override
    public List<PurchaseAuxiliaryAllocationDO> getListByPurchaseContractCode(String code) {
        return purchaseAuxiliaryAllocationMapper.selectList(new LambdaQueryWrapperX<PurchaseAuxiliaryAllocationDO>()
                .eq(PurchaseAuxiliaryAllocationDO::getPurchaseContractCode,code));

    }

    @Override
    public void updatePurchaseAuxiliaryAllocationAllocation(PurchaseAuxiliaryAllocationAllocationSaveReqVO updateReqVO) {
        Long auxiliaryPurchaseContractItemId = updateReqVO.getAuxiliaryPurchaseContractItemId();
        List<AllocationItem> allocationList = updateReqVO.getAllocationList();
        if(Objects.isNull(auxiliaryPurchaseContractItemId) || CollUtil.isEmpty(allocationList)){
            throw exception(PURCHASE_UPDATE_PARA_WRONG);
        }
        purchaseAuxiliaryAllocationMapper.delete(PurchaseAuxiliaryAllocationDO::getAuxiliaryPurchaseContractItemId,auxiliaryPurchaseContractItemId);

        PurchaseContractItemDO auxiliaryDO = contractItemService.getById(auxiliaryPurchaseContractItemId);  //需要分摊的辅料明细
        if(Objects.isNull(auxiliaryDO)){
            throw exception(PURCHASE_CONTRACT_NOT_EXISTS);
        }
        SkuDTO  auxiliarySkuDTO = skuApi.getSkuDTO(auxiliaryDO.getSkuId());
        //分摊到的产品明细
        Map<Long,PurchaseContractItemDO>  itemMap = contractItemService.getByIdList(allocationList.stream().map(AllocationItem::getPurchaseContractItemId).distinct().toList());
        if(CollUtil.isEmpty(itemMap)){
            throw exception(PURCHASE_UPDATE_PARA_WRONG);
        }
        List<String> skuCodeList = itemMap.values().stream().map(PurchaseContractItemDO::getSkuCode).distinct().toList();
        Map<String, SkuDTO> skuDTOMap = skuApi.getSkuDTOMapByCodeList(skuCodeList);

        List<PurchaseAuxiliaryAllocationDO> list = new ArrayList<>();
        allocationList.forEach(s->{
            PurchaseAuxiliaryAllocationDO allocationDO = new PurchaseAuxiliaryAllocationDO();
            PurchaseContractItemDO purchaseContractItemDO = itemMap.get(s.getPurchaseContractItemId());  //产品明细
            if(Objects.isNull(purchaseContractItemDO)){
                throw exception(PURCHASE_UPDATE_PARA_WRONG);
            }
            allocationDO.setPurchaseContractId(purchaseContractItemDO.getPurchaseContractId()).setPurchaseContractCode(purchaseContractItemDO.getPurchaseContractCode());
            allocationDO.setPurchaseContractItemId(purchaseContractItemDO.getId());
            String skuCode = purchaseContractItemDO.getSkuCode();
            allocationDO.setQuantity(purchaseContractItemDO.getQuantity());
            allocationDO.setSkuCode(skuCode);
            if(CollUtil.isNotEmpty(skuDTOMap)){
                SkuDTO skuDTO = skuDTOMap.get(skuCode);
                if(Objects.nonNull(skuDTO)){
                    allocationDO.setSkuName(skuDTO.getName()).setCskuCode(skuDTO.getCskuCode());
                }
            }
            allocationDO.setAuxiliaryPurchaseContractId(auxiliaryDO.getPurchaseContractId()).setAuxiliaryPurchaseContractCode(auxiliaryDO.getPurchaseContractCode());
            allocationDO.setAuxiliaryPurchaseContractItemId(auxiliaryDO.getId());
            allocationDO.setAuxiliarySkuCode(auxiliaryDO.getSkuCode());
            if(Objects.nonNull(auxiliarySkuDTO)){
                allocationDO.setAuxiliarySkuName(auxiliarySkuDTO.getName());
            }
            allocationDO.setAllocationAmount(s.getAllocationAmount());
            list.add(allocationDO);
        });
        if(CollUtil.isEmpty(list)){
            throw exception(PURCHASE_UPDATE_PARA_WRONG);
        }
        purchaseAuxiliaryAllocationMapper.insertBatch(list);
    }

    @Override
    public List<PurchaseAuxiliaryAllocationDO> getListByPurchaseItemId(Long contractId) {
        return purchaseAuxiliaryAllocationMapper.selectList(PurchaseAuxiliaryAllocationDO::getAuxiliaryPurchaseContractItemId,contractId);

    }

    @Override
    public void deletePurchaseAuxiliaryAllocationByItemId(Long itemId) {
        purchaseAuxiliaryAllocationMapper.delete(PurchaseAuxiliaryAllocationDO::getAuxiliaryPurchaseContractItemId,itemId);
    }

    @Override
    public String getProcessDefinitionKey() {
        return PROCESS_DEFINITION_KEY;
    }
    @Override
    public PurchaseAuxiliaryAllocationRespVO getPurchaseAuxiliaryAllocation( PurchaseAuxiliaryAllocationDetailReq purchaseAuxiliaryAllocationDetailReq) {
        Long purchaseAuxiliaryAllocationId = Objects.nonNull(purchaseAuxiliaryAllocationDetailReq.getProcessInstanceId()) ? bpmProcessInstanceApi.selectAuditAbleIdByProcessInstanceId(purchaseAuxiliaryAllocationDetailReq.getProcessInstanceId()) : purchaseAuxiliaryAllocationDetailReq.getPurchaseAuxiliaryAllocationId();
        if (Objects.isNull(purchaseAuxiliaryAllocationId)) {
            logger.error("[采购合同辅料分摊]未获取到采购合同辅料分摊id");
            return null;
        }
        PurchaseAuxiliaryAllocationDO purchaseAuxiliaryAllocationDO = purchaseAuxiliaryAllocationMapper.selectById(purchaseAuxiliaryAllocationId);
        if (purchaseAuxiliaryAllocationDO == null) {
            return null;
        }
        return PurchaseAuxiliaryAllocationConvert.INSTANCE.convertPurchaseAuxiliaryAllocationRespVO(purchaseAuxiliaryAllocationDO);
    }

    @Override
    public PageResult<PurchaseAuxiliaryAllocationDO> getPurchaseAuxiliaryAllocationPage(PurchaseAuxiliaryAllocationPageReqVO pageReqVO) {
        return purchaseAuxiliaryAllocationMapper.selectPage(pageReqVO);
    }

}