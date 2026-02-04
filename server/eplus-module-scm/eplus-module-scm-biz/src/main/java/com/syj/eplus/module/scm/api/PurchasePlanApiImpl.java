package com.syj.eplus.module.scm.api;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.syj.eplus.framework.common.entity.CreatedResponse;
import com.syj.eplus.module.scm.api.purchaseplan.PurchasePlanApi;
import com.syj.eplus.module.scm.api.purchaseplan.dto.PurchasePlanInfoDTO;
import com.syj.eplus.module.scm.api.purchaseplan.dto.PurchasePlanInfoSaveReqDTO;
import com.syj.eplus.module.scm.api.purchaseplan.dto.PurchasePlanItemDTO;
import com.syj.eplus.module.scm.api.purchaseplan.dto.PurchasePlanItemSaveReqDTO;
import com.syj.eplus.module.scm.controller.admin.purchaseplan.vo.PurchasePlanInfoSaveReqVO;
import com.syj.eplus.module.scm.controller.admin.purchaseplanitem.vo.PurchasePlanItemSaveReqVO;
import com.syj.eplus.module.scm.dal.dataobject.purchaseplan.PurchasePlanDO;
import com.syj.eplus.module.scm.dal.dataobject.purchaseplanitem.PurchasePlanItemDO;
import com.syj.eplus.module.scm.service.purchaseplan.PurchasePlanService;
import com.syj.eplus.module.scm.service.purchaseplanitem.PurchasePlanItemService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
public class PurchasePlanApiImpl implements PurchasePlanApi {

    @Resource
    private PurchasePlanService purchasePlanService;
    @Resource
    private PurchasePlanItemService purchasePlanItemService;


    @Override
    public List<CreatedResponse> createPurchasePlan(PurchasePlanInfoSaveReqDTO saveReqVO) {
        PurchasePlanInfoSaveReqVO saveDTO = BeanUtils.toBean(saveReqVO, PurchasePlanInfoSaveReqVO.class);
        List<PurchasePlanItemSaveReqDTO> children = saveReqVO.getChildren();
        List<PurchasePlanItemSaveReqVO> planItemSaveReqVOS = BeanUtils.toBean(children, PurchasePlanItemSaveReqVO.class);
        saveDTO.setChildren(planItemSaveReqVOS);

        return purchasePlanService.createPurchasePlan(saveDTO);
    }

    @Override
    @DataPermission(enable = false)
    public Long getOrderNumBySaleContractId(Long contractId) {
        return purchasePlanService.getOrderNumBySaleContractId(contractId);
    }

    @Override
    @DataPermission(enable = false)
    public PurchasePlanInfoDTO getPurchasePlan(Long purchasePlanId) {
        PurchasePlanDO purchasePlanDO = purchasePlanService.getById(purchasePlanId);
        if (ObjectUtil.isNull(purchasePlanDO)) {
            return null;
        }
        PurchasePlanInfoDTO purchasePlanInfoDTO = BeanUtils.toBean(purchasePlanDO, PurchasePlanInfoDTO.class);
        LambdaQueryWrapper<PurchasePlanItemDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PurchasePlanItemDO::getPurchasePlanId,purchasePlanId);
        List<PurchasePlanItemDO> purchasePlanItemDOList = purchasePlanItemService.list(queryWrapper);
        if (!CollectionUtils.isEmpty(purchasePlanItemDOList)) {
            List<PurchasePlanItemDTO> purchasePlanItemDTOList = BeanUtils.toBean(purchasePlanItemDOList, PurchasePlanItemDTO.class);
            purchasePlanInfoDTO.setPurchasePlanItemDTOList(purchasePlanItemDTOList);
        }
        return purchasePlanInfoDTO;
    }

    @Override
    public PurchasePlanItemDTO getPurchasePlanItemInfo(String saleContractCode, String skuCode,Long companyId) {
        LambdaQueryWrapper<PurchasePlanItemDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PurchasePlanItemDO::getSaleContractCode,saleContractCode);
        queryWrapper.eq(PurchasePlanItemDO::getSkuCode,skuCode);
        queryWrapper.eq(PurchasePlanItemDO::getPurchaseCompanyId,companyId);
        PurchasePlanItemDO purchasePlanItemDO = purchasePlanItemService.getOne(queryWrapper);
        return BeanUtils.toBean(purchasePlanItemDO, PurchasePlanItemDTO.class);
    }

    @Override
    @DataPermission(enable = false)
    public List <PurchasePlanItemDTO> getPurchasePlanItemListBySaleContractCodeList(List<String> codeList) {
        LambdaQueryWrapper<PurchasePlanItemDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(PurchasePlanItemDO::getSaleContractCode,codeList);
        List<PurchasePlanItemDO> doList = purchasePlanItemService.list(queryWrapper);
        if(CollUtil.isEmpty(doList)){
            return List.of();
        }
        List<PurchasePlanItemDTO> dtoList = BeanUtils.toBean(doList, PurchasePlanItemDTO.class);
        return dtoList;
    }

    @Override
    public Map<Long, List<Integer>> getPurchaseContractItemCancelFlag(List<Long> saleItemIds) {
        return purchasePlanService.getPurchaseContractItemCancelFlag(saleItemIds);
    }

    @Override
    public void updateLinkCodeList(Map<String, String> linkCodeMap) {
        purchasePlanService.updateLinkCodeList(linkCodeMap);
    }

    @Override
    public Map<Long, Integer> getPurchaseModelBySaleItemIds(Collection<Long> saleItems) {
        return purchasePlanService.getPurchaseModelBySaleItemIds(saleItems);
    }
}
