package com.syj.eplus.module.sms.service.saleauxiliaryallocation;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.bpm.api.task.BpmProcessInstanceApi;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import cn.iocoder.yudao.module.bpm.enums.task.BpmProcessInstanceResultEnum;
import com.syj.eplus.framework.common.enums.SaleTabEnum;
import com.syj.eplus.framework.common.enums.SubmitFlagEnum;
import com.syj.eplus.module.pms.api.sku.SkuApi;
import com.syj.eplus.module.pms.api.sku.dto.SkuNameDTO;
import com.syj.eplus.module.scm.api.purchasecontract.PurchaseContractApi;
import com.syj.eplus.module.scm.api.purchasecontract.dto.PurchaseContractItemDTO;
import com.syj.eplus.module.sms.controller.admin.saleauxiliaryallocation.vo.*;
import com.syj.eplus.module.sms.controller.admin.salecontract.vo.SaleContractDetailReq;
import com.syj.eplus.module.sms.controller.admin.salecontract.vo.SaleContractRespVO;
import com.syj.eplus.module.sms.convert.saleauxiliaryallocation.SaleAuxiliaryAllocationConvert;
import com.syj.eplus.module.sms.dal.dataobject.saleauxiliaryallocation.SaleAuxiliaryAllocationDO;
import com.syj.eplus.module.sms.dal.dataobject.salecontractitem.SaleContractItem;
import com.syj.eplus.module.sms.dal.mysql.saleauxiliaryallocation.SaleAuxiliaryAllocationMapper;
import com.syj.eplus.module.sms.service.salecontract.SaleContractService;
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
import static com.syj.eplus.module.pms.enums.ErrorCodeConstants.SKU_NOT_EXISTS;
import static com.syj.eplus.module.sms.enums.ErrorCodeConstants.*;

/**
 * 销售合同辅料分摊 Service 实现类
 *
 * @author zhangcm
 */
@Service
@Validated
public class SaleAuxiliaryAllocationServiceImpl implements SaleAuxiliaryAllocationService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private SaleAuxiliaryAllocationMapper saleAuxiliaryAllocationMapper;
    @Resource
    private BpmProcessInstanceApi bpmProcessInstanceApi;
    @Resource
    private SaleContractService saleContractService;
    @Resource
    private PurchaseContractApi purchaseContractApi;

    @Resource
    private SkuApi skuApi;
    private static final String PROCESS_DEFINITION_KEY = "${table.processKey}";


    @Override
    public Long createSaleAuxiliaryAllocation(SaleAuxiliaryAllocationSaveReqVO createReqVO) {
        SaleAuxiliaryAllocationDO saleAuxiliaryAllocation = SaleAuxiliaryAllocationConvert.INSTANCE.convertSaleAuxiliaryAllocationDO(createReqVO);
        // 插入
        saleAuxiliaryAllocationMapper.insert(saleAuxiliaryAllocation);
        if (SubmitFlagEnum.SUBMIT.getStatus().equals(createReqVO.getSubmitFlag())) {
            submitTask(saleAuxiliaryAllocation.getId(), WebFrameworkUtils.getLoginUserId());
        }
        // 返回
        return saleAuxiliaryAllocation.getId();
    }

    @Override
    public void updateSaleAuxiliaryAllocation(SaleAuxiliaryAllocationSaveReqVO updateReqVO) {
        // 校验存在
        validateSaleAuxiliaryAllocationExists(updateReqVO.getId());
        // 更新
        SaleAuxiliaryAllocationDO updateObj = SaleAuxiliaryAllocationConvert.INSTANCE.convertSaleAuxiliaryAllocationDO(updateReqVO);
        saleAuxiliaryAllocationMapper.updateById(updateObj);
    }

    @Override
    public void deleteSaleAuxiliaryAllocation(Long id) {
        // 校验存在
        validateSaleAuxiliaryAllocationExists(id);
        // 删除
        saleAuxiliaryAllocationMapper.deleteById(id);
    }

    private void validateSaleAuxiliaryAllocationExists(Long id) {
        if (saleAuxiliaryAllocationMapper.selectById(id) == null) {
            throw exception(SALE_AUXILIARY_ALLOCATION_NOT_EXISTS);
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
    public void submitTask(Long saleAuxiliaryAllocationId, Long userId) {
        bpmProcessInstanceApi.createProcessInstance(userId, PROCESS_DEFINITION_KEY, saleAuxiliaryAllocationId);
        updateAuditStatus(saleAuxiliaryAllocationId, BpmProcessInstanceResultEnum.PROCESS.getResult());
    }

    @Override
    public void updateAuditStatus(Long auditableId, Integer auditStatus) {
//    saleAuxiliaryAllocationMapper.updateById(SaleAuxiliaryAllocationDO.builder().id(auditableId).auditStatus(auditStatus).build());
    }

    @Override
    public List<SaleAuxiliaryAllocationDO> getListBySaleCode(String code) {
         return   saleAuxiliaryAllocationMapper.selectList(SaleAuxiliaryAllocationDO::getSaleContractCode,code);
    }

    @Override
    public void allocationSaleAuxiliaryAllocation(AllocationReqVO reqVO) {
        //接口不允许list为空
        List<AllocationItemReqVO> list = reqVO.getList();
        if(CollUtil.isEmpty(list)){
            return;
        }
        //接口不允许销售合同id为空
        Long saleContractId = reqVO.getSaleContractId();
        SaleContractRespVO saleContract = saleContractService.getSaleContract(new SaleContractDetailReq().setSaleContractId(saleContractId), SaleTabEnum.SALE_ONLY);
        if(Objects.isNull(saleContract)){
            throw exception(SALE_CONTRACT_NOT_EXISTS);
        }
        //接口不允许辅料合同明细id为空
        Long auxiliaryPurchaseContractItemId = reqVO.getAuxiliaryPurchaseContractItemId();
        PurchaseContractItemDTO itemDto = purchaseContractApi.getItemDetailByItemId(auxiliaryPurchaseContractItemId);
        if(Objects.isNull(itemDto)){
            throw exception(PURCHASE_ITEM_NOT_EXISTS);
        }
        List<SaleAuxiliaryAllocationDO> doList = new ArrayList<>();
        //获取销售合同明细详情
        List<Long> itemIdList = list.stream().map(AllocationItemReqVO::getSaleContractItemId).distinct().toList();
        Map<Long,SaleContractItem> itemMap = saleContractService.getItemListByItemIds(itemIdList);
        if(CollUtil.isEmpty(itemMap)){
            throw exception(CONTRACT_ITEM_NOT_EXISTS);
        }
        //获取产品详细信息
        List<String> skuCodeList = new ArrayList<>(itemMap.values().stream().map(SaleContractItem::getSkuCode).distinct().toList());
        skuCodeList.add(itemDto.getSkuCode());  //产品包含销售合同明细的产品，辅料产品
        Map<String, SkuNameDTO> skuMap = skuApi.getSkuNameCacheByCodeList(skuCodeList);
        if(CollUtil.isEmpty(skuMap)){
            throw exception(SKU_NOT_EXISTS);
        }
        //组装入库数据
        list.forEach(s->{
            SaleAuxiliaryAllocationDO saleAuxiliaryAllocationDO = new SaleAuxiliaryAllocationDO();
            SaleContractItem saleContractItem = itemMap.get(s.getSaleContractItemId());
            if(Objects.isNull(saleContractItem)){
                throw exception(CONTRACT_ITEM_NOT_EXISTS);
            }
            saleAuxiliaryAllocationDO.setSkuCode(saleContractItem.getSkuCode()).setCskuCode(saleContractItem.getCskuCode());
            SkuNameDTO skuNameDTO = skuMap.get(saleContractItem.getSkuCode());
            if (Objects.nonNull(skuNameDTO)){
                saleAuxiliaryAllocationDO.setSkuName(skuNameDTO.getName());
            }
            saleAuxiliaryAllocationDO.setAllocationAmount(s.getAllocationAmount());
            //外合同信息
            saleAuxiliaryAllocationDO.setSaleContractId(saleContract.getId()).setSaleContractCode(saleContract.getCode());
            saleAuxiliaryAllocationDO.setSaleContractItemId(s.getSaleContractItemId());
            //辅料信息
            saleAuxiliaryAllocationDO.setAuxiliaryPurchaseContractItemId(itemDto.getId());
            saleAuxiliaryAllocationDO.setAuxiliaryPurchaseContractId(itemDto.getPurchaseContractId()).setAuxiliaryPurchaseContractCode(itemDto.getPurchaseContractCode());
            saleAuxiliaryAllocationDO.setAuxiliarySkuCode(itemDto.getSkuCode());
            saleAuxiliaryAllocationDO.setAuxiliarySkuName(skuNameDTO.getName());
            saleAuxiliaryAllocationDO.setQuantity(saleContractItem.getNeedPurQuantity());

            doList.add(saleAuxiliaryAllocationDO);
        });

        saleAuxiliaryAllocationMapper.insertBatch(doList);
    }

    @Override
    public void cancelAllocationSaleAuxiliaryAllocation(Long itemId) {
        saleAuxiliaryAllocationMapper.delete(SaleAuxiliaryAllocationDO::getAuxiliaryPurchaseContractItemId,itemId);
    }

    @Override
    public List<SaleAuxiliaryAllocationRespVO> getAllocation(Long saleId, Long purchaseItemId) {
        List<SaleAuxiliaryAllocationDO> saleAuxiliaryAllocationDOList = saleAuxiliaryAllocationMapper.selectList(new LambdaQueryWrapperX<SaleAuxiliaryAllocationDO>()
                .eqIfPresent(SaleAuxiliaryAllocationDO::getSaleContractId, saleId)
                .eqIfPresent(SaleAuxiliaryAllocationDO::getAuxiliaryPurchaseContractItemId, purchaseItemId));
        if(CollUtil.isEmpty(saleAuxiliaryAllocationDOList)){
            return null;
        }
        return BeanUtils.toBean(saleAuxiliaryAllocationDOList,SaleAuxiliaryAllocationRespVO.class);
    }

    @Override
    public String getProcessDefinitionKey() {
        return PROCESS_DEFINITION_KEY;
    }
    @Override
    public SaleAuxiliaryAllocationRespVO getSaleAuxiliaryAllocation( SaleAuxiliaryAllocationDetailReq saleAuxiliaryAllocationDetailReq) {
        Long saleAuxiliaryAllocationId = Objects.nonNull(saleAuxiliaryAllocationDetailReq.getProcessInstanceId()) ? bpmProcessInstanceApi.selectAuditAbleIdByProcessInstanceId(saleAuxiliaryAllocationDetailReq.getProcessInstanceId()) : saleAuxiliaryAllocationDetailReq.getSaleAuxiliaryAllocationId();
        if (Objects.isNull(saleAuxiliaryAllocationId)) {
            logger.error("[销售合同辅料分摊]未获取到销售合同辅料分摊id");
            return null;
        }
        SaleAuxiliaryAllocationDO saleAuxiliaryAllocationDO = saleAuxiliaryAllocationMapper.selectById(saleAuxiliaryAllocationId);
        if (saleAuxiliaryAllocationDO == null) {
            return null;
        }
        return SaleAuxiliaryAllocationConvert.INSTANCE.convertSaleAuxiliaryAllocationRespVO(saleAuxiliaryAllocationDO);
    }

    @Override
    public PageResult<SaleAuxiliaryAllocationDO> getSaleAuxiliaryAllocationPage(SaleAuxiliaryAllocationPageReqVO pageReqVO) {
        return saleAuxiliaryAllocationMapper.selectPage(pageReqVO);
    }

}