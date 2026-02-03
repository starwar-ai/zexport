package com.syj.eplus.module.home.api;

import com.syj.eplus.module.home.service.invoiceholder.InvoiceHolderService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: du
 * @Date: 2024/09/10/10:23
 * @Description:
 */
@Service
public class InvoiceHolderApiImpl implements InvoiceHolderApi{

    @Resource
    private InvoiceHolderService invoiceHolderService;
    @Override
    public void updateInvoiceHolderStatus(List<Long> idList, Integer status) {
        invoiceHolderService.updateInveiceHolderStatus(idList,status);
    }
}
