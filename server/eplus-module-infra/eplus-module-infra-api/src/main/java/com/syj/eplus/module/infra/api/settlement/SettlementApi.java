package com.syj.eplus.module.infra.api.settlement;

import com.syj.eplus.module.infra.api.settlement.dto.SettlementDTO;

import java.util.List;

/**
 * @Description：
 * @Author：du
 * @Date：2024/1/25 11:04
 */
public interface SettlementApi {

    /**
     * 批量获取结汇方式
     *
     * @param idList
     * @return
     */
    List<SettlementDTO> selectSettlementTermById(List<Long> idList);


    /**
     * 根据id获取结汇方式
     *
     * @param id 结汇方式id
     * @return 结汇方式
     */
    SettlementDTO getSettlementDTOById(Long id);

    /**
     * 批量获取结汇方式(包含收款计划)
     *
     * @param idList 结汇方式id列表
     * @return 结汇方式列表(包含收款计划)
     */
    List<SettlementDTO> getSettlementWithCollectionPlansById(List<Long> idList);
}
