package com.syj.eplus.module.sms.dal.mysql.addsubitem;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.sms.dal.dataobject.addsubitem.AddSubItem;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description：
 * @Author：du
 * @Date：2024/6/14 11:07
 */
@Mapper
public interface AddSubItemMapper extends BaseMapperX<AddSubItem> {
    /**
     * 根据合同id列表获取对应加减项map
     *
     * @param contractCodeList 合同编号列表
     * @return 加减项map
     */
    default Map<String, List<AddSubItem>> getAddSubItemMapByContractCodeList(List<String> contractCodeList) {
        LambdaQueryWrapperX<AddSubItem> queryWrapperX = new LambdaQueryWrapperX<AddSubItem>().in(AddSubItem::getContractCode, contractCodeList);
        List<AddSubItem> addSubItemList = this.selectList(queryWrapperX);
        if (CollUtil.isEmpty(addSubItemList)) {
            return null;
        }
        return addSubItemList.stream().collect(Collectors.groupingBy(AddSubItem::getContractCode));
    }

    /**
     * 根据合同id列表获取对应加减项map
     *
     * @param contractCodeList 合同编号列表
     * @return 加减项List
     */
    default List<AddSubItem> getAddSubItemListByContractCodeList(List<String> contractCodeList) {
        LambdaQueryWrapperX<AddSubItem> queryWrapperX = new LambdaQueryWrapperX<AddSubItem>().in(AddSubItem::getContractCode, contractCodeList);
        List<AddSubItem> addSubItemList = this.selectList(queryWrapperX);
        if (CollUtil.isEmpty(addSubItemList)) {
            return null;
        }
        return addSubItemList;
    }
}