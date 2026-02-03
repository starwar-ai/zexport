package com.syj.eplus.module.dms.dal.mysql.settlementformitem;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.dms.dal.dataobject.settlementformitem.SettlementFormItem;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 结汇单子类 Mapper
 *
 * @author du
 */
@Mapper
public interface SettlementFormItemMapper extends BaseMapperX<SettlementFormItem> {
    default Map<Long, List<SettlementFormItem>> getSettlementFormItemMapBySettlementId(List<Long> settlementIdList) {
        LambdaQueryWrapperX<SettlementFormItem> queryWrapperX = new LambdaQueryWrapperX<SettlementFormItem>().in(SettlementFormItem::getSettlementFormId, settlementIdList);
        List<SettlementFormItem> declarationItemList = this.selectList(queryWrapperX);
        if (CollUtil.isEmpty(declarationItemList)) {
            return null;
        }
        return declarationItemList.stream().collect(Collectors.groupingBy(SettlementFormItem::getSettlementFormId));
    }

}