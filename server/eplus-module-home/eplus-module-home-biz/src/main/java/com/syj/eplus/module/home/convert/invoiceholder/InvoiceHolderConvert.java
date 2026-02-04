package com.syj.eplus.module.home.convert.invoiceholder;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.syj.eplus.module.home.controller.admin.invoiceholder.vo.InvoiceHolderRespVO;
import com.syj.eplus.module.home.controller.admin.invoiceholder.vo.InvoiceHolderSaveReqVO;
import com.syj.eplus.module.home.dal.dataobject.invoiceholder.InvoiceHolderDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Map;


@Mapper
public interface InvoiceHolderConvert {

        InvoiceHolderConvert INSTANCE = Mappers.getMapper(InvoiceHolderConvert.class);

        default InvoiceHolderRespVO convert(InvoiceHolderDO invoiceHolderDO,Map<Long, String> dictSubjectMap){
                InvoiceHolderRespVO respVO = BeanUtil.toBean(invoiceHolderDO, InvoiceHolderRespVO.class);
                if (CollUtil.isNotEmpty(dictSubjectMap)){
                        respVO.setInvoiceName(dictSubjectMap.get(invoiceHolderDO.getDictSubjectId()));
                }
                return respVO;
        }

    default InvoiceHolderRespVO convertInvoiceHolderRespVO(InvoiceHolderDO invoiceHolderDO,Map<Long, String> dictSubjectMap){
               return convert(invoiceHolderDO,dictSubjectMap);
        }

    InvoiceHolderDO convertInvoiceHolderDO(InvoiceHolderSaveReqVO saveReqVO);
}