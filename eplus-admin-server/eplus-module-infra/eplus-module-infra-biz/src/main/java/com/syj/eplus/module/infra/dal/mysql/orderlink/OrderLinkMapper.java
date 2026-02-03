package com.syj.eplus.module.infra.dal.mysql.orderlink;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import com.syj.eplus.module.infra.dal.dataobject.orderlink.OrderLink;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderLinkMapper extends BaseMapperX<OrderLink> {

    /**
     * 通过sourceUniqueCode列表检查是否存在订单链路
     *
     * @param uniqueCodes 唯一码列表
     * @return 是否存在
     */
    Boolean existsBySourceUniqueCodes(@Param("uniqueCodes") List<String> uniqueCodes);

}
