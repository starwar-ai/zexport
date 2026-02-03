package com.syj.eplus.module.crm.api.cust;

import cn.iocoder.yudao.framework.common.pojo.ConfirmSourceEntity;
import com.syj.eplus.framework.common.entity.BaseData;
import com.syj.eplus.module.crm.api.cust.dto.*;

import java.util.List;
import java.util.Map;

/**
 * @Description：客户相关api (外部调用)
 * @Author：du
 * @Date：2024/2/22 15:39
 */
public interface CustApi {
    /**
     * 通过编号获取客户对应名称(code为空默认查全部)
     */
    Map<String, String> getCustNameCache(String code);

    /**
     * 批量获取客户精简列表
     *
     * @param idList 客户id列表
     * @return 客户精简列表
     */
    List<SimpleCustRespDTO> getSimpleCustRespDTO(List<Long> idList);

    Map<Long, SimpleCustRespDTO> getSimpleCustRespDTOMap(List<Long> idList);

    /**
     * 根据内部企业编码获取客户精简列表
     * @param internalCompanyId
     * @return
     */
    SimpleCustRespDTO getSimpleCustByInternalCompanyId(Long internalCompanyId);

    /**
     * 根据银行抬头获取客户精简信息
     *
     * @param bankPoc 银行抬头
     * @return 客户精简信息
     */
    List<SimpleCustResp> getSimpleCustRespByBankPoc(String bankPoc);

    /**
     * 回写客户抬头
     */
    void createCustTitle(Map<String,Long> notExistCustCodeMap, String custTitle);

    Map<String, CustAllDTO> getCustByCodeList(List<String> codeList);

    List<String> getCodeListByName(String custName);

    /**
     * 根据客户编号获得最新的客户变更资料
     *
     * @param code
     * @return 客户资料
     */
    CustAllDTO getCustChangeByCode(String code);

    /**
     * 获得客户影响变更
     *
     * @param targetCode 影响范围主键
     * @return 客户影响变更
     */
    List<ConfirmSourceEntity> getConfirmSourceList(String targetCode, Integer effectRangeType);

    List<String> getCodeListByCountryCode(Long countryId, String regionCode);

    CustAllDTO getCustByCode(String custCode);

    List<CustMarkDTO> getMarkListByCustCode(Long custId);

    CollectionAccountDTO getCollectionAccountByCust(String custCode, Long companyId);

    List<String> getCodeListByNameList(List<String> nameList);


    /**
     * 根据抬头模糊查询银行联系人
     * @param title 抬头
     * @return 银行联系人列表
     */
    List<CustBankAccountDTO> getBankPocListByTitle(String title);

    /**
     * 根据客户编号列表取客户精简信息
     *
     * @param cutCodeList 客户编号列表
     * @return 客户精简信息
     */
    List<SimpleCustResp> getSimpleCustRespByCode(List<String> cutCodeList);

    /**
     * 
     * 根据客户编号列表取客户精简信息
     *
     * @param custName 客户名称
     * @return 客户精简信息
     */
    List<SimpleCustResp> getSimpleCustRespByName(String custName);

     /*
      删除认领自动回写的抬头
     * @param payeeEntityIds 认领明细id
     */
    void deleteCustTitle(List<Long> payeeEntityIds);

    /**
     * 校验是否内部客户
     * @param custCode 客户编号
     */
    boolean checkIsInternalCust(String custCode);

    /**
     * 获取内部客户主体
     */
    Long getInternalCompany(String custCode);

    /**
     * 根据客户编号获取收款计划
     * @param custCode 客户编号
     * @return 收款计划
     */
    List<SystemCollectionPlanDTO> getCollectionPlanDTOByCustCode(String custCode);

    /**
     * 根据客户编号获取银行抬头
     * @param custCode 客户编号
     * @return 银行抬头
     */
    String getBankPocByCustCode(String custCode);

    /**
     * 根据客户编号获取联系人信息
     * @param custCode 客户编号
     * @return 联系人姓名
     */
    String getCustPocByCustCode(String custCode);

    Map<Long, CustDTO> getCustByIds(List<Long> custIds);

    /**
     * 获取所有内部客户
     * @return
     */
    Map<Long, BaseData> getAllInnerCustCache();
}
