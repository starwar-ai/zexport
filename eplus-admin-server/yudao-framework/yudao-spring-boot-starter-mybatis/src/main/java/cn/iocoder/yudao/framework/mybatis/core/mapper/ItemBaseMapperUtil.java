package cn.iocoder.yudao.framework.mybatis.core.mapper;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.mybatis.core.enums.ChangeTypeEnum;
import cn.iocoder.yudao.framework.mybatis.core.exinterface.ChangeExInterface;

import java.util.Collection;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: du
 * @Date: 2024/08/05/17:43
 * @Description: 基础数据库操作公共类
 */
public class ItemBaseMapperUtil {

    /**
     * 处理子项增删改
     *
     * @param itemList        子项列表
     * @param itemBaseMapperX BaseMapper类
     */
    public static <T extends ChangeExInterface> void dealItemList(Collection<T> itemList, BaseMapperX<T> itemBaseMapperX) {
        List<T> insertList;
        List<T> updateList;
        List<Long> deleteList;
        if (CollUtil.isEmpty(itemList)) {
            return;
        }
        // 新增项
        insertList = itemList.stream().filter(s -> ChangeTypeEnum.ADDED.getType().equals(s.getChangeFlag())).toList();
        // 修改项
        updateList = itemList.stream().filter(s -> ChangeTypeEnum.UPDATE.getType().equals(s.getChangeFlag())).toList();
        // 删除项
        deleteList = itemList.stream().filter(s -> ChangeTypeEnum.DELETED.getType().equals(s.getChangeFlag())).map(T::getId).toList();
        if (CollUtil.isNotEmpty(insertList)) {
            itemBaseMapperX.insertBatch(insertList);
        }
        if (CollUtil.isNotEmpty(updateList)) {
            itemBaseMapperX.updateBatch(updateList);
        }
        if (CollUtil.isNotEmpty(deleteList)) {
            itemBaseMapperX.deleteBatchIds(deleteList);
        }
    }
}
