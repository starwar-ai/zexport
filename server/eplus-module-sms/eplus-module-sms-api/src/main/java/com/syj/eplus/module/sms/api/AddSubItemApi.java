package com.syj.eplus.module.sms.api;

import com.syj.eplus.module.sms.api.dto.AddSubItemDTO;

import java.util.List;

/**
 * 加减项 API 接口
 *
 * @author ePlus
 */
public interface AddSubItemApi {

    /**
     * 根据合同编号列表查询加减项列表
     *
     * @param contractCodeList 合同编号列表
     * @return 加减项列表
     */
    List<AddSubItemDTO> getAddSubItemListByContractCodeList(List<String> contractCodeList);

    /**
     * 根据合同编号和状态查询加减项列表
     *
     * @param contractCodeList 合同编号列表
     * @param status 状态
     * @return 加减项列表
     */
    List<AddSubItemDTO> getAddSubItemListByContractCodeListAndStatus(List<String> contractCodeList, Integer status);

    /**
     * 批量删除加减项
     *
     * @param ids 加减项ID列表
     */
    void deleteBatchByIds(List<Long> ids);

    /**
     * 批量插入加减项
     *
     * @param addSubItemList 加减项列表
     */
    void insertBatch(List<AddSubItemDTO> addSubItemList);

    /**
     * 根据合同编号删除加减项
     *
     * @param contractCode 合同编号
     */
    void deleteByContractCode(String contractCode);
}
