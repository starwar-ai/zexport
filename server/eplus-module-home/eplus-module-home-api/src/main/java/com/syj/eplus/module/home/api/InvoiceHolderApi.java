package com.syj.eplus.module.home.api;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: du
 * @Date: 2024/09/10/10:23
 * @Description:
 */
public interface InvoiceHolderApi {
    void updateInvoiceHolderStatus(List<Long> id, Integer status);
}
