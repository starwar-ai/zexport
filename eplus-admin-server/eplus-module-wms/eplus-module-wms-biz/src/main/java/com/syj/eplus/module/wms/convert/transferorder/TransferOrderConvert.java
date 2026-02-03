package com.syj.eplus.module.wms.convert.transferorder;

import cn.iocoder.yudao.framework.common.dict.CommonDict;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import com.syj.eplus.framework.common.dict.CalculationDict;
import com.syj.eplus.module.wms.controller.admin.transferorder.vo.TransferOrderRespVO;
import com.syj.eplus.module.wms.controller.admin.transferorder.vo.TransferOrderSaveReqVO;
import com.syj.eplus.module.wms.dal.dataobject.bill.BillItemDO;
import com.syj.eplus.module.wms.dal.dataobject.transferorder.TransferOrderDO;
import com.syj.eplus.module.wms.dal.dataobject.transferorderitem.TransferOrderItem;
import com.syj.eplus.module.wms.enums.StockSourceTypeEnum;
import com.syj.eplus.module.wms.enums.StockTypeEnum;
import com.syj.eplus.module.wms.util.BatchCodeUtil;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Objects;


@Mapper
public interface TransferOrderConvert {

    TransferOrderConvert INSTANCE = Mappers.getMapper(TransferOrderConvert.class);

    TransferOrderRespVO convert(TransferOrderDO transferOrderDO);

    default TransferOrderRespVO convertTransferOrderRespVO(TransferOrderDO transferOrderDO) {
        TransferOrderRespVO transferOrderRespVO = convert(transferOrderDO);
        return transferOrderRespVO;
    }

    TransferOrderDO convertTransferOrderDO(TransferOrderSaveReqVO saveReqVO);

    BillItemDO convertBillItemDO(Object transferOrderItem);

    default List<BillItemDO> convertBillItemDOList(List<TransferOrderItem> transferOrderItemList, Integer billType, BatchCodeUtil batchCodeUtil, TransferOrderDO transferOrder) {
        return CollectionUtils.convertList(transferOrderItemList, s -> {
            BillItemDO billItemDO = convertBillItemDO(s);
            billItemDO.setBillType(billType);
            billItemDO.setSourceId(s.getTransferOrderId());
            billItemDO.setSourceType(StockSourceTypeEnum.ALLOCATION.getValue());
            // 入库应收实收数量 = 调拨数量
            if (Objects.isNull(batchCodeUtil) || StockTypeEnum.IN_STOCK.getValue().compareTo(billType) == CalculationDict.ZERO) {
                billItemDO.setBatchCode(batchCodeUtil.genBatchCode(CommonDict.EMPTY_STR));
                billItemDO.setCompanyId(transferOrder.getCompanyId());
                billItemDO.setCompanyName(transferOrder.getCompanyName());
            }
            billItemDO.setOrderQuantity(s.getTransferQuantity().intValue());
            billItemDO.setOrderBoxQuantity(CalculationDict.ZERO);
            billItemDO.setActQuantity(s.getTransferQuantity().intValue());
            billItemDO.setActBoxQuantity(CalculationDict.ZERO);
            return billItemDO;
        });
    }

}
