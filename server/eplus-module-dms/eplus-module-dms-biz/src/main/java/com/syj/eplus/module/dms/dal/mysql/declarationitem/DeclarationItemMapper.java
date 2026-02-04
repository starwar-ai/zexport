package com.syj.eplus.module.dms.dal.mysql.declarationitem;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.dms.dal.dataobject.declarationitem.DeclarationItem;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 报关单明细 Mapper
 *
 * @author du
 */
@Mapper
public interface DeclarationItemMapper extends BaseMapperX<DeclarationItem> {
    default Map<Long, List<DeclarationItem>> getDeclarationItemMapByDeclarationIdList(List<Long> declarationIdList) {
        LambdaQueryWrapperX<DeclarationItem> queryWrapperX = new LambdaQueryWrapperX<DeclarationItem>().in(DeclarationItem::getDeclarationId, declarationIdList);
        List<DeclarationItem> declarationItemList = this.selectList(queryWrapperX);
        if (CollUtil.isEmpty(declarationItemList)) {
            return null;
        }
        return declarationItemList.stream().collect(Collectors.groupingBy(DeclarationItem::getDeclarationId));
    }
}