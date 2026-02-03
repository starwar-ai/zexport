package com.syj.eplus.module.scm.convert.invoicingnotices;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import com.syj.eplus.module.infra.api.company.dto.SimpleCompanyDTO;
import com.syj.eplus.module.scm.api.invoicingnotices.dto.InvoicingNoticesDTO;
import com.syj.eplus.module.scm.controller.admin.invoicingnotices.vo.InvoicingNoticesItemResp;
import com.syj.eplus.module.scm.controller.admin.invoicingnotices.vo.InvoicingNoticesRespVO;
import com.syj.eplus.module.scm.controller.admin.invoicingnotices.vo.InvoicingNoticesSaveReqVO;
import com.syj.eplus.module.scm.dal.dataobject.invoicingnotices.InvoicingNoticesDO;
import com.syj.eplus.module.scm.dal.dataobject.invoicingnoticesItem.InvoicingNoticesItem;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;
import java.util.Objects;


@Mapper
public interface InvoicingNoticesConvert {

    InvoicingNoticesConvert INSTANCE = Mappers.getMapper(InvoicingNoticesConvert.class);

    InvoicingNoticesRespVO convert(InvoicingNoticesDO invoicingNoticesDO);

    default InvoicingNoticesRespVO convertInvoicingNoticesRespVO(InvoicingNoticesDO invoicingNoticesDO) {
        InvoicingNoticesRespVO invoicingNoticesRespVO = convert(invoicingNoticesDO);
        return invoicingNoticesRespVO;
    }

    InvoicingNoticesDO convertInvoicingNoticesDO(InvoicingNoticesSaveReqVO saveReqVO);

    InvoicingNoticesSaveReqVO convertInvoicingNoticesSaveReqVO(InvoicingNoticesDTO invoicingNoticesDTO);

    default List<InvoicingNoticesSaveReqVO> convertInvoicingNoticesSaveReqVOList(List<InvoicingNoticesDTO> invoicingNoticesDTOList) {
        return CollectionUtils.convertList(invoicingNoticesDTOList, this::convertInvoicingNoticesSaveReqVO);
    }
    InvoicingNoticesItemResp convertItemResp(InvoicingNoticesItem invoicingNoticesItem);
    default List<InvoicingNoticesItemResp> convertItemRespList(List<InvoicingNoticesItem> invoicingNoticesItem, String venderName,String venderCode, SimpleCompanyDTO companyDTO){
        return CollectionUtils.convertList(invoicingNoticesItem, item -> {
            InvoicingNoticesItemResp invoicingNoticesItemResp = convertItemResp(item);
            invoicingNoticesItemResp.setVenderName(venderName);
            invoicingNoticesItemResp.setCompanyName(companyDTO.getCompanyName());
            invoicingNoticesItemResp.setVenderCode(venderCode);
            return invoicingNoticesItemResp;
        });
    }


    default PageResult<InvoicingNoticesItemResp> convertPageResult(PageResult<InvoicingNoticesItem> invoicingNoticesDOPageResult, Map<String, InvoicingNoticesDO> invoicingNoticesDOMap,Map<Long, SimpleCompanyDTO> simpleCompanyDTO,Map<String, String> venderNameCache){
        List<InvoicingNoticesItem> invoicingNoticesDOList = invoicingNoticesDOPageResult.getList();
        if (CollUtil.isEmpty(invoicingNoticesDOList)){
            return PageResult.empty();
        }
        List<InvoicingNoticesItemResp> invoicingNoticesItemResps = CollectionUtils.convertList(invoicingNoticesDOList, item -> {
            String invoicNoticesCode = item.getInvoicNoticesCode();
            InvoicingNoticesDO invoicingNoticesDO = invoicingNoticesDOMap.get(invoicNoticesCode);
            String venderCode = invoicingNoticesDO.getVenderCode();
            InvoicingNoticesItemResp invoicingNoticesItemResp = convertItemResp(item);
            invoicingNoticesItemResp.setVenderName(venderNameCache.get(venderCode));
            if (Objects.nonNull(invoicingNoticesDO.getCompanyId())){
                invoicingNoticesItemResp.setCompanyId(invoicingNoticesDO.getCompanyId());
                invoicingNoticesItemResp.setCompanyName(simpleCompanyDTO.get(invoicingNoticesDO.getCompanyId()).getCompanyName());
            }
            invoicingNoticesItemResp.setVenderCode(venderCode);
            return invoicingNoticesItemResp;
        });
        return new PageResult<InvoicingNoticesItemResp>().setList(invoicingNoticesItemResps).setTotal(invoicingNoticesDOPageResult.getTotal());
    }
}