package com.syj.eplus.module.home.service.invoiceholder;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.syj.eplus.module.home.controller.admin.invoiceholder.vo.InvoiceHolderPageReqVO;
import com.syj.eplus.module.home.controller.admin.invoiceholder.vo.InvoiceHolderRespVO;
import com.syj.eplus.module.home.controller.admin.invoiceholder.vo.InvoiceHolderSaveReqVO;

import javax.validation.Valid;
import java.util.List;

/**
 * 发票夹 Service 接口
 *
 * @author du
 */
public interface InvoiceHolderService {

    /**
     * 创建发票夹
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createInvoiceHolder(@Valid InvoiceHolderSaveReqVO createReqVO);

    /**
     * 更新发票夹
     *
     * @param updateReqVO 更新信息
     */
    void updateInvoiceHolder(@Valid InvoiceHolderSaveReqVO updateReqVO);

    /**
     * 删除发票夹
     *
     * @param id 编号
     */
    void deleteInvoiceHolder(Long id);

    /**
     * 获得发票夹
     *
* @param  id 编号 
     * @return 发票夹
     */
InvoiceHolderRespVO getInvoiceHolder(Long id);

    /**
     * 获得发票夹分页
     *
     * @param pageReqVO 分页查询
     * @return 发票夹分页
     */
    PageResult<InvoiceHolderRespVO> getInvoiceHolderPage(InvoiceHolderPageReqVO pageReqVO);

    /**
     * 更新票夹子状态
     * @param idList 主键
     * @param status 状态
     */
    void updateInveiceHolderStatus(List<Long> idList, Integer status);
}