package com.syj.eplus.module.crm.service.custSettlement;


import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.syj.eplus.module.crm.controller.admin.custsettlement.vo.CustSettlementPageReqVO;
import com.syj.eplus.module.crm.controller.admin.custsettlement.vo.CustSettlementSaveReqVO;
import com.syj.eplus.module.crm.dal.dataobject.custsettlement.CustSettlementDO;
import com.syj.eplus.module.crm.service.custSettlement.dto.CustSettlementDTO;

import javax.validation.Valid;
import java.util.List;

/**
 * 客户结汇方式 Service 接口
 *
 * @author eplus
 */
public interface CustSettlementService {

    /**
     * 创建客户结汇方式
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createCustSettlement(@Valid CustSettlementSaveReqVO createReqVO);

    /**
     * 更新客户结汇方式
     *
     * @param updateReqVO 更新信息
     */
    void updateCustSettlement(@Valid CustSettlementSaveReqVO updateReqVO);

    /**
     * 删除客户结汇方式
     *
     * @param id 编号
     */
    void deleteCustSettlement(Long id);

    /**
     * 获得客户结汇方式
     *
     * @param id 编号
     * @return 客户结汇方式
     */
    CustSettlementDO getCustSettlement(Long id);

    /**
     * 获得客户结汇方式分页
     *
     * @param pageReqVO 分页查询
     * @return 客户结汇方式分页
     */
    PageResult<CustSettlementDO> getCustSettlementPage(CustSettlementPageReqVO pageReqVO);

    /**
     * 批量新增客户结汇方式
     *
     * @param custSettlementDOList
     */
    void batchSaveCustSettlement(List<CustSettlementDO> custSettlementDOList);

    /**
     * 根据客户ID获取结汇方式
     *
     * @param custId 客户ID
     * @return
     */
    List<CustSettlementDTO> getSettlementByCustId(Long custId);

    /**
     * 根据客户id删除结汇方式
     *
     * @param custId
     */
    void deleteCustSettlementByCustId(Long custId);
}