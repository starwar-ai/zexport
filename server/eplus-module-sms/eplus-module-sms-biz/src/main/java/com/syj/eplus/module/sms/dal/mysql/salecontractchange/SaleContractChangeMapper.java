package com.syj.eplus.module.sms.dal.mysql.salecontractchange;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.sms.controller.admin.salecontract.vo.SaleContractChangePageReq;
import com.syj.eplus.module.sms.dal.dataobject.salecontractchange.SaleContractChange;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Description：
 * @Author：du
 * @Date：2024/7/1 17:14
 */
@Mapper
public interface SaleContractChangeMapper extends BaseMapperX<SaleContractChange> {
    default PageResult<SaleContractChange> selectPage(SaleContractChangePageReq reqVO) {
        LambdaQueryWrapperX<SaleContractChange> queryWrapperX = new LambdaQueryWrapperX<SaleContractChange>();
        
        // 基础查询条件
        queryWrapperX.likeIfPresent(SaleContractChange::getCode, reqVO.getCode());
        queryWrapperX.likeIfPresent(SaleContractChange::getSourceCode, reqVO.getContractCode());
        queryWrapperX.likeIfPresent(SaleContractChange::getSourceCode, reqVO.getSourceCode());
        queryWrapperX.eqIfPresent(SaleContractChange::getAuditStatus, reqVO.getAuditStatus());
        queryWrapperX.betweenIfPresent(SaleContractChange::getCreateTime, reqVO.getCreateTime());
        queryWrapperX.eqIfPresent(SaleContractChange::getCreator, reqVO.getCreator());
        
        // 客户相关查询条件
        queryWrapperX.likeIfPresent(SaleContractChange::getCustName, reqVO.getCustName());
        queryWrapperX.likeIfPresent(SaleContractChange::getCustCode, reqVO.getCustCode());
        
        // 下单主体查询条件
        queryWrapperX.likeIfPresent(SaleContractChange::getCompanyName, reqVO.getCompanyName());
        queryWrapperX.eqIfPresent(SaleContractChange::getCompanyId, reqVO.getCompanyId());
        
        // JSON字段查询 - 产品相关
        if (StrUtil.isNotEmpty(reqVO.getCskuCode())) {
            queryWrapperX.apply("JSON_SEARCH(children, 'one', CONCAT('%', {0}, '%'), NULL, '$[*].cskuCode') IS NOT NULL", reqVO.getCskuCode());
        }
        if (StrUtil.isNotEmpty(reqVO.getBasicSkuCode())) {
            queryWrapperX.apply("JSON_SEARCH(children, 'one', CONCAT('%', {0}, '%'), NULL, '$[*].basicSkuCode') IS NOT NULL", reqVO.getBasicSkuCode());
        }
        if (StrUtil.isNotEmpty(reqVO.getSkuCode())) {
            queryWrapperX.apply("JSON_SEARCH(children, 'one', CONCAT('%', {0}, '%'), NULL, '$[*].skuCode') IS NOT NULL", reqVO.getSkuCode());
        }
        if (StrUtil.isNotEmpty(reqVO.getName())) {
            queryWrapperX.apply("JSON_SEARCH(children, 'one', CONCAT('%', {0}, '%'), NULL, '$[*].name') IS NOT NULL", reqVO.getName());
        }
        if (StrUtil.isNotEmpty(reqVO.getNameEng())) {
            queryWrapperX.apply("JSON_SEARCH(children, 'one', CONCAT('%', {0}, '%'), NULL, '$[*].nameEng') IS NOT NULL", reqVO.getNameEng());
        }
        
        // JSON字段查询 - 业务员相关
        if (reqVO.getSalesUserId() != null) {
            queryWrapperX.apply("JSON_EXTRACT(sales,'$.userId') = {0}", reqVO.getSalesUserId());
        }
        if (StrUtil.isNotEmpty(reqVO.getSalesUserName())) {
            queryWrapperX.apply("JSON_EXTRACT(sales,'$.userName') like CONCAT('%', {0}, '%')", reqVO.getSalesUserName());
        }
        if (reqVO.getSalesDeptId() != null) {
            queryWrapperX.apply("JSON_EXTRACT(sales,'$.deptId') = {0}", reqVO.getSalesDeptId());
        }
        if (StrUtil.isNotEmpty(reqVO.getSalesDeptName())) {
            queryWrapperX.apply("JSON_EXTRACT(sales,'$.deptName') like CONCAT('%', {0}, '%')", reqVO.getSalesDeptName());
        }
        
        queryWrapperX.orderByDesc(SaleContractChange::getId);
        queryWrapperX.eqIfPresent(SaleContractChange::getSaleType, reqVO.getSaleType());
        return selectPage(reqVO, queryWrapperX);
    }

}
