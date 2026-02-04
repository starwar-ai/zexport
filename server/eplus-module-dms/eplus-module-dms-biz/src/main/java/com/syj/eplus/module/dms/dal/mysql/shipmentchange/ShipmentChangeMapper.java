package com.syj.eplus.module.dms.dal.mysql.shipmentchange;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.dms.controller.admin.shipment.vo.change.ChangeShipmentPageReq;
import com.syj.eplus.module.dms.dal.dataobject.shipmentchange.ShipmentChange;
import org.apache.ibatis.annotations.Mapper;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: du
 * @Date: 2024/08/30/15:48
 * @Description:
 */
@Mapper
public interface ShipmentChangeMapper extends BaseMapperX<ShipmentChange> {

    default PageResult<ShipmentChange> selectPage(ChangeShipmentPageReq reqVO) {
        LambdaQueryWrapperX<ShipmentChange> queryWrapperX = new LambdaQueryWrapperX<>();
        
        // 原有搜索条件
        if (StrUtil.isNotEmpty(reqVO.getCskuCode())) {
            queryWrapperX.apply("JSON_EXTRACT(children,'$.cskuCode') like %{0}%", reqVO.getCskuCode());
        }
        if (StrUtil.isNotEmpty(reqVO.getBasicSkuCode())) {
            queryWrapperX.apply("JSON_EXTRACT(children,'$.basicSkuCode') like %{0}%", reqVO.getBasicSkuCode());
        }
        if (StrUtil.isNotEmpty(reqVO.getSkuCode())) {
            queryWrapperX.apply("JSON_EXTRACT(children,'$.skuCode') like %{0}%", reqVO.getSkuCode());
        }
        
        // 新增搜索条件
        if (StrUtil.isNotEmpty(reqVO.getInvoiceCode())) {
            queryWrapperX.like(ShipmentChange::getInvoiceCode, reqVO.getInvoiceCode());
        }
        if (StrUtil.isNotEmpty(reqVO.getCustName())) {
            queryWrapperX.like(ShipmentChange::getCustName, reqVO.getCustName());
        }
        if (reqVO.getOrderManagerId() != null) {
            queryWrapperX.apply("JSON_EXTRACT(order_manager,'$.userId') = {0}", reqVO.getOrderManagerId());
        }
        if (StrUtil.isNotEmpty(reqVO.getOrderManagerName())) {
            queryWrapperX.apply("JSON_EXTRACT(order_manager,'$.nickname') like %{0}%", reqVO.getOrderManagerName());
        }
        if (StrUtil.isNotEmpty(reqVO.getSkuName())) {
            queryWrapperX.apply("JSON_EXTRACT(children,'$.skuName') like %{0}%", reqVO.getSkuName());
        }
        
        queryWrapperX.orderByDesc(ShipmentChange::getId);
        return selectPage(reqVO, queryWrapperX);
    }

}