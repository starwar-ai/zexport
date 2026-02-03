package com.syj.eplus.module.scm.api;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.framework.common.entity.CreatedResponse;
import com.syj.eplus.framework.common.enums.BooleanEnum;
import com.syj.eplus.module.scm.api.invoicingnotices.InvoicingNoticesApi;
import com.syj.eplus.module.scm.api.invoicingnotices.dto.InvoicingNoticesDTO;
import com.syj.eplus.module.scm.api.invoicingnotices.dto.InvoicingNoticesItemDTO;
import com.syj.eplus.module.scm.controller.admin.invoicingnotices.vo.InvoicingNoticesSaveReqVO;
import com.syj.eplus.module.scm.convert.invoicingnotices.InvoicingNoticesConvert;
import com.syj.eplus.module.scm.dal.dataobject.invoicingnoticesItem.InvoicingNoticesItem;
import com.syj.eplus.module.scm.dal.mysql.invoicingnoticesitem.InvoicingNoticesItemMapper;
import com.syj.eplus.module.scm.service.invoicingnotices.InvoicingNoticesService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class InvoicingNoticesApiImpl implements InvoicingNoticesApi {

    @Resource
    private InvoicingNoticesService invoicingNoticesService;

    @Resource
    private InvoicingNoticesItemMapper invoicingNoticesItemMapper;

    @Override
    public List<CreatedResponse> createInvoicingNotices(List<InvoicingNoticesDTO> invoicingNoticesDTOList) {
        List<CreatedResponse> responses = new ArrayList<>();
        List<InvoicingNoticesSaveReqVO> invoicingNoticesSaveReqVOList = InvoicingNoticesConvert.INSTANCE.convertInvoicingNoticesSaveReqVOList(invoicingNoticesDTOList);
        if (CollUtil.isNotEmpty(invoicingNoticesSaveReqVOList)) {
            invoicingNoticesSaveReqVOList.forEach(s -> {
                s.setManuallyFlag(BooleanEnum.NO.getValue());
                List<CreatedResponse> invoicingNotices = invoicingNoticesService.createInvoicingNotices(s);
                if(CollUtil.isNotEmpty(invoicingNotices) && invoicingNotices.size() > 0){
                    responses.add(invoicingNotices.get(0));
                }
            });
        }
        return  responses;
    }

    @Override
    public Map<String, InvoicingNoticesItemDTO> getInvoicingNoticesItemBySourceUniqueCodes(List<String> sourceUniqueCodes) {
        if (CollUtil.isEmpty(sourceUniqueCodes)) {
            return new HashMap<>();
        }
        
        List<InvoicingNoticesItem> items = invoicingNoticesItemMapper.selectList(
            new LambdaQueryWrapperX<InvoicingNoticesItem>()
                .in(InvoicingNoticesItem::getSourceUniqueCode, sourceUniqueCodes)
        );
        
        if (CollUtil.isEmpty(items)) {
            return new HashMap<>();
        }
        
        return items.stream()
            .collect(Collectors.toMap(
                InvoicingNoticesItem::getSourceUniqueCode,
                item -> BeanUtils.toBean(item, InvoicingNoticesItemDTO.class),
                (existing, replacement) -> existing
            ));
    }

    @Override
    public Map<Long, List<InvoicingNoticesItemDTO>> getInvoicingNoticesItemByPurchaseItemIds(List<Long> purchaseContractItemIds) {
        if (CollUtil.isEmpty(purchaseContractItemIds)) {
            return new HashMap<>();
        }
        
        List<InvoicingNoticesItem> items = invoicingNoticesItemMapper.selectList(
            new LambdaQueryWrapperX<InvoicingNoticesItem>()
                .in(InvoicingNoticesItem::getPurchaseContractItemId, purchaseContractItemIds)
        );
        
        if (CollUtil.isEmpty(items)) {
            return new HashMap<>();
        }
        
        return items.stream()
            .map(item -> BeanUtils.toBean(item, InvoicingNoticesItemDTO.class))
            .collect(Collectors.groupingBy(InvoicingNoticesItemDTO::getPurchaseContractItemId));
    }
}
