package com.syj.eplus.module.wms.service.stockimport;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.syj.eplus.module.wms.controller.admin.stockimport.vo.*;
import com.syj.eplus.module.wms.dal.dataobject.stockimport.StockImportDO;

import javax.validation.Valid;
import java.util.List;

/**
 * 仓储管理-库存明细导入 Service 接口
 *
 * @author zhangcm
 */
public interface StockImportService {

    /**
     * 创建仓储管理-库存明细导入
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createStockImport(@Valid StockImportSaveReqVO createReqVO);

    /**
     * 更新仓储管理-库存明细导入
     *
     * @param updateReqVO 更新信息
     */
    void updateStockImport(@Valid StockImportSaveReqVO updateReqVO);

    /**
     * 删除仓储管理-库存明细导入
     *
     * @param id 编号
     */
    void deleteStockImport(Long id);

    /**
     * 获得仓储管理-库存明细导入
     *
     * @param  id 编号
     * @return 仓储管理-库存明细导入
     */
    StockImportRespVO getStockImport(Long id);

    /**
     * 获得仓储管理-库存明细导入分页
     *
     * @param pageReqVO 分页查询
     * @return 仓储管理-库存明细导入分页
     */
    PageResult<StockImportDO> getStockImportPage(StockImportPageReqVO pageReqVO);

    StockImportShowRespVO importExcelNotInsert(List<StockImportExcelVO> list);

    List<StockImportExcelVO> getTestImport();

    List<StockImportDO> getListByImportCode(String importCode);

    void BatchUpdate(List<StockImportDO> stockImportDOS);
}