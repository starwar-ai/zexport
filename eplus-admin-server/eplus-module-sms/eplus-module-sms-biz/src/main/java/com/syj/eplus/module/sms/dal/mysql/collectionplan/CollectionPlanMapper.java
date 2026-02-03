package com.syj.eplus.module.sms.dal.mysql.collectionplan;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.sms.dal.dataobject.collectionplan.CollectionPlan;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description：
 * @Author：du
 * @Date：2024/6/14 11:28
 */
@Mapper
public interface CollectionPlanMapper extends BaseMapperX<CollectionPlan> {
    /**
     * 根据合同id列表获取对应收款计划map
     *
     * @param contractIdList 合同id列表
     * @return 收款计划map
     */
    default Map<Long, List<CollectionPlan>> getCollectionPlanMapByContractIdList(List<Long> contractIdList) {
        LambdaQueryWrapperX<CollectionPlan> queryWrapperX = new LambdaQueryWrapperX<CollectionPlan>().in(CollectionPlan::getContractId, contractIdList);
        List<CollectionPlan> collectionPlanList = this.selectList(queryWrapperX);
        if (CollUtil.isEmpty(collectionPlanList)) {
            return null;
        }
        return collectionPlanList.stream().collect(Collectors.groupingBy(CollectionPlan::getContractId));
    }
}