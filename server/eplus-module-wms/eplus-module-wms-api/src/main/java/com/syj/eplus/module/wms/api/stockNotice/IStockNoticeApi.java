package com.syj.eplus.module.wms.api.stockNotice;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.syj.eplus.framework.common.entity.CreatedResponse;
import com.syj.eplus.module.wms.api.stockNotice.dto.StockNoticePageReqDTO;
import com.syj.eplus.module.wms.api.stockNotice.dto.StockNoticeReqVO;
import com.syj.eplus.module.wms.api.stockNotice.dto.StockNoticeRespDTO;
import com.syj.eplus.module.wms.api.stockNotice.dto.StockNoticeSaveReqDTO;

import java.util.List;

/**
 * 入库通知单 API
 */
public interface IStockNoticeApi {

    /**
     * 采购合同转入库通知单
     * @return
     */
    CreatedResponse toStockNotice(StockNoticeReqVO stockNoticeReqVO);

    /**
     * 根据采购合同编号查询入库通知单
     * @return List<StockNoticeReqVO>
     */
    List<StockNoticeRespDTO> GetStockNoticeListByPurchaseCode(String code);


    /**
     * 创建拉柜通知单
     * @return
     */
    CreatedResponse createNotice(StockNoticeSaveReqDTO stockNoticeSaveReqDTO);

    /**
     * 获得拉柜通知单
     *
     * @param pageReqDTO 分页查询
     * @return 仓储管理-入(出)库通知单分页
     */
    PageResult<StockNoticeRespDTO>  getNoticePage(StockNoticePageReqDTO pageReqDTO);

    /**
     * 通过采购合同主键获取入库单数量
     * @param id 采购合同主键
     * @return 入库单数量
     */
    Long getBillNumByPurchaseCode(Long id);

    /**
     * 根据销售合同查询入库通知单
     * @return List<StockNoticeReqVO>
     */
    List<StockNoticeRespDTO> getStockNoticeListBySaleContractList(List<String> saleContractList);

}
