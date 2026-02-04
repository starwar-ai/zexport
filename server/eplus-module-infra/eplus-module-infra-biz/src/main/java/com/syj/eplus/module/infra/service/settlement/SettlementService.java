package com.syj.eplus.module.infra.service.settlement;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.syj.eplus.module.infra.api.settlement.dto.SettlementDTO;
import com.syj.eplus.module.infra.controller.admin.settlement.vo.SettlementPageReqVO;
import com.syj.eplus.module.infra.controller.admin.settlement.vo.SettlementRespVO;
import com.syj.eplus.module.infra.controller.admin.settlement.vo.SettlementSaveReqVO;
import com.syj.eplus.module.infra.dal.dataobject.settlement.SettlementDO;

import javax.validation.Valid;
import java.util.List;

/**
 * 结汇方式 Service 接口
 *
 * @author eplus
 */
public interface SettlementService {

    /**
     * 创建结汇方式
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createSettlement(@Valid SettlementSaveReqVO createReqVO);

    /**
     * 更新结汇方式
     *
     * @param updateReqVO 更新信息
     */
    void updateSettlement(@Valid SettlementSaveReqVO updateReqVO);

    /**
     * 删除结汇方式
     *
     * @param id 编号
     */
    void deleteSettlement(Long id);

    /**
     * 获得结汇方式
     *
     * @param id 编号
     * @return 结汇方式
     */
    SettlementRespVO getSettlement(Long id);

    /**
     * 获得结汇方式分页
     *
     * @param pageReqVO 分页查询
     * @return 结汇方式分页
     */
    PageResult<SettlementDO> getSettlementPage(SettlementPageReqVO pageReqVO);

    /**
     * 获取全量结汇方式
     *
     * @return
     */
    List<SettlementDO> getSettlementList();

    /**
     * 根据id获取结汇方式
     *
     * @param idList
     * @return
     */
    List<SettlementDO> getSettlementListByIdList(List<Long> idList);

    /**
     * 根据id获取结汇方式
     *
     * @param id 结汇方式id
     * @return 结汇方式
     */
    SettlementDTO getSettlementDTOById(Long id);

    /**
     * 根据id列表获取结汇方式(包含收款计划)
     *
     * @param idList 结汇方式id列表
     * @return 结汇方式列表(包含收款计划)
     */
    List<SettlementDTO> getSettlementWithCollectionPlansById(List<Long> idList);
}