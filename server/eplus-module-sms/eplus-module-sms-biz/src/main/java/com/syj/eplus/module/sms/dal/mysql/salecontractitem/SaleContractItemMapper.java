package com.syj.eplus.module.sms.dal.mysql.salecontractitem;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.sms.dal.dataobject.salecontractitem.SaleContractItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.syj.eplus.module.sms.controller.admin.salecontract.vo.SaleContractPageReqVO;
import com.syj.eplus.module.sms.controller.admin.salecontract.vo.SaleContractProductModeRespVO;
import com.syj.eplus.module.sms.dal.dataobject.salecontract.SaleContractProductModeSummaryDO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 外销合同明细 Mapper
 *
 * @author ePlus
 */
@Mapper
public interface SaleContractItemMapper extends BaseMapperX<SaleContractItem> {
    /**
     * 使用FIND_IN_SET字符串匹配，避免foreach和IN查询
     * @param contractIdStr 合同ID字符串，如 "1,2,3,4,5"
     * @author 波波
     */
    List<SaleContractItem> selectByContractIdStr(@Param("contractIdStr") String contractIdStr);

    /**
     * 批量查询优化版本：使用IN查询
     * @param contractIds 合同ID列表
     * @return 合同明细列表
     */
    List<SaleContractItem> selectByContractIds(@Param("contractIds") List<Long> contractIds);

    /**
     * 优化版本：使用字符串拼接方式，避免foreach和过多SQL
     * @param contractIdList 合同ID列表
     * @return 按合同ID分组的合同明细Map
     * @author 波波
     */
    default Map<Long, List<SaleContractItem>> getSaleContractItemMapByContractIdList(List<Long> contractIdList) {
        if (CollUtil.isEmpty(contractIdList)) {
            return null;
        }
        
        // 将ID列表转换为逗号分隔的字符串
        String contractIdStr = contractIdList.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        
        // 使用FIND_IN_SET查询，避免foreach和IN查询
        List<SaleContractItem> saleContractItemList = this.selectByContractIdStr(contractIdStr);
        
        if (CollUtil.isEmpty(saleContractItemList)) {
            return null;
        }
        
        return saleContractItemList.stream().collect(Collectors.groupingBy(SaleContractItem::getContractId));
    }

    /**
     * 批量查询优化版本：直接使用IN查询
     * 当contractIdList不太大时(小于1000)，这种方式比FIND_IN_SET更快
     * @param contractIdList 合同ID列表
     * @return 按合同ID分组的合同明细Map
     */
    default Map<Long, List<SaleContractItem>> getSaleContractItemMapByContractIdListOptimized(List<Long> contractIdList) {
        if (CollUtil.isEmpty(contractIdList)) {
            return null;
        }
        
        List<SaleContractItem> saleContractItemList;
        
        // 如果ID数量大于1000，分批查询
        if (contractIdList.size() > 1000) {
            saleContractItemList = new ArrayList<>();
            // 分批处理，每批最套1000个
            for (int i = 0; i < contractIdList.size(); i += 1000) {
                int end = Math.min(i + 1000, contractIdList.size());
                List<Long> batch = contractIdList.subList(i, end);
                List<SaleContractItem> batchResult = this.selectByContractIds(batch);
                if (CollUtil.isNotEmpty(batchResult)) {
                    saleContractItemList.addAll(batchResult);
                }
            }
        } else {
            // 直接查询
            saleContractItemList = this.selectByContractIds(contractIdList);
        }
        
        if (CollUtil.isEmpty(saleContractItemList)) {
            return null;
        }
        
        return saleContractItemList.stream().collect(Collectors.groupingBy(SaleContractItem::getContractId));
    }

    @Select("""
              SELECT DISTINCT t.STATUS
              FROM sms_sale_contract p
              JOIN sms_sale_contract_item t ON p.id = t.contract_id
              WHERE p.id = (SELECT contract_id FROM sms_sale_contract_item WHERE id = #{itemId})
            """)
    List<Integer> getSaleContractItemStatusLink(@Param("itemId") Long itemId);

//    default List<SaleContractItem> getSaleContractItemListByCustCode(String custCode) {
//        LambdaQueryWrapperX<SaleContractItem> queryWrapperX = new LambdaQueryWrapperX<SaleContractItem>();
//        queryWrapperX.eq(SaleContractItem::getCustCode, custCode);
//        List<SaleContractItem> saleContractItemList = this.selectList(queryWrapperX);
//        if (CollUtil.isEmpty(saleContractItemList)) {
//            return null;
//        }
//        return saleContractItemList;
//    }

    default List<SaleContractItem> getSaleContractItemListBySkuCodeList(List<String> skuCodeList) {
        LambdaQueryWrapperX<SaleContractItem> queryWrapperX = new LambdaQueryWrapperX<SaleContractItem>();
        queryWrapperX.in(SaleContractItem::getSkuCode, skuCodeList);
        List<SaleContractItem> saleContractItemList = this.selectList(queryWrapperX);
        if (CollUtil.isEmpty(saleContractItemList)) {
            return null;
        }
        return saleContractItemList;
    }

    /**
     * 产品模式分页查询（按明细分页，关联主表条件）
     * 用于产品视图的扁平化展示
     *
     * @param page      分页参数
     * @param pageReqVO 查询参数
     * @return 分页结果
     * @author 波波
     */
    IPage<SaleContractProductModeRespVO> selectProductModePage(
            IPage<?> page,
            @Param("req") SaleContractPageReqVO pageReqVO
    );

    /**
     * 产品模式汇总查询（基于筛选后的明细）
     * 用于产品视图的汇总统计
     *
     * @param pageReqVO 查询参数
     * @return 汇总数据
     * @author 波波
     */
    SaleContractProductModeSummaryDO selectProductModeSummary(
            @Param("req") SaleContractPageReqVO pageReqVO
    );
}