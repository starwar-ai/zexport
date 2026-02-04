package com.syj.eplus.module.wms.service.bill;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.baomidou.mybatisplus.extension.service.IService;
import com.syj.eplus.framework.common.entity.CreatedResponse;
import com.syj.eplus.framework.common.entity.ShipmentPriceEntity;
import com.syj.eplus.module.wms.api.stock.dto.BillSaveReqVO;
import com.syj.eplus.module.wms.controller.admin.bill.vo.BillPageReqVO;
import com.syj.eplus.module.wms.controller.admin.bill.vo.BillRespVO;
import com.syj.eplus.module.wms.controller.admin.billitem.vo.BillItemRespVO;
import com.syj.eplus.module.wms.dal.dataobject.bill.BillDO;
import com.syj.eplus.module.wms.dal.dataobject.bill.BillItemDO;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 仓储管理-入(出)库单 Service 接口
 *
 * @author ePlus
 */
public interface BillService extends IService<BillDO> {

    /**
     * 创建仓储管理-入(出)库单
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    CreatedResponse createBill(@Valid BillSaveReqVO createReqVO);

    /**
     * 更新仓储管理-入(出)库单
     *
     * @param updateReqVO 更新信息
     */
    boolean updateBill(@Valid BillSaveReqVO updateReqVO);

    boolean addStockAndItemList(Long billId,List<BillItemDO> updateBillItemDOList);

    boolean subStockAndItemList(List<BillItemDO> billItemDOList,Boolean convertFlag,Integer stockType);

    /**
     * 删除仓储管理-入(出)库单
     *
     * @param id 编号
     */
    boolean deleteBill(Long id);

    boolean deleteBillByContract(Long purchaseContractId);

    /**
     * 获得仓储管理-入(出)库单
     *
     * @param id 编号
     * @return 仓储管理-入(出)库单
     */
    BillRespVO getBill(Long id);

    /**
     * 获得仓储管理-入(出)库单分页
     *
     * @param pageReqVO 分页查询
     * @return 仓储管理-入(出)库单分页
     */
    PageResult<BillRespVO> getBillPage(BillPageReqVO pageReqVO);
    // ==================== 子表（仓储管理-入(出)库单-明细） ====================

    /**
     * 获得仓储管理-入(出)库单-明细列表
     *
     * @param sourceId 来源单主键
     * @return 仓储管理-入(出)库单-明细列表
     */
    List<BillItemDO> getBillItemListBySourceId(List<Long> sourceId);

    /**
     * 反提交入库单
     *
     * @param id
     * @return
     */
    Boolean cancel(Long id);

    /**
     * 获得出入库记录
     *
     * @param batchCode
     * @return
     */
    List<BillItemRespVO> getRecord(String batchCode);

    /**
     * 更新出入库明细
     *
     * @param sourceId       来源单号
     * @param billtype       来源类型
     * @param billItemDOList 库存明细列表
     * @param updateStatusFlag 是否更新入库状态
     * @return
     */
    boolean updateBillItemList(Long sourceId,String sourceCode, Integer billtype, List<BillItemDO> billItemDOList,boolean updateStatusFlag);

    List<Long> getIdListByContractId(Long purchaseContractId);

    /**
     * 通过采购合同主键获取入库单数量
     * @param id 采购合同主键
     * @return 入库单数量
     */
    Long getBillNumByPurchaseId(Long id);

    /**
     * 校验通知单是否已经生成出入库单
     * @param noticeCode 通知单号
     * @return 是否已经生成出入库单
     */
    boolean checkBillByNoticeCode(String noticeCode);


    /**
     * 获取采购合同对应的入库单明细
     * @param purchaseContractCode
     * @param skuCodes
     * @return
     */
    List<BillItemDO> getBillItemByPurchaseContractCode(String purchaseContractCode, Set<String> skuCodes);

    /**
     * 批量更新入库单明细
     * @param billItemDOList 入库单明细
     */
    void updateBillItemList(List<BillItemDO> billItemDOList);

    /**
     * 获取已转入库单但还未实际数据的入库单明细
     */
    List<BillItemDO> getBillItemListByNoticeCodeList(List<String> noticeCodeList);

    /**
     * 重新生成内部合同
     * @param billId 出入库单id
     */
    void genInternalContract(Long billId);

    /**
     * 通过销售合同号获取出库对应的应收金额
     * @param saleContractCodeList 销售合同编号
     * @return 出库对应的应收金额
     */
    Map<String, List<ShipmentPriceEntity>> getStockPriceBySaleContractCode(List<String> saleContractCodeList);

    /**
     * 获取出库日期
     * @param saleItemUniqueCodeList 销售明细编号列表
     * @return 出库日期
     */
    Map<String, LocalDateTime> getOutStockTimeBySaleItems(List<String> saleItemUniqueCodeList);
}
