package com.syj.eplus.module.dms.service.forwarderfee;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.module.dms.controller.admin.forwarderfee.vo.ForwarderFeePageReqVO;
import com.syj.eplus.module.dms.controller.admin.forwarderfee.vo.ForwarderFeeRespVO;
import com.syj.eplus.module.dms.controller.admin.forwarderfee.vo.ForwarderFeeSaveReqVO;
import com.syj.eplus.module.dms.dal.dataobject.forwarderfee.ForwarderFeeDO;
import com.syj.eplus.module.dms.enums.api.dto.DmsForwarderFeeDTO;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 船代费用 Service 接口
 *
 * @author zhangcm
 */
public interface ForwarderFeeService {

    /**
     * 创建船代费用
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createForwarderFee(@Valid ForwarderFeeSaveReqVO createReqVO);

    /**
     * 更新船代费用
     *
     * @param updateReqVO 更新信息
     */
    void updateForwarderFee(@Valid ForwarderFeeSaveReqVO updateReqVO);

    /**
     * 删除船代费用
     *
     * @param id 编号
     */
    void deleteForwarderFee(Long id);

    /**
     * 获得船代费用
     *
     * @param  id 编号
     * @return 船代费用
     */
    ForwarderFeeRespVO getForwarderFee(Long id);

    /**
     * 获得船代费用分页
     *
     * @param pageReqVO 分页查询
     * @return 船代费用分页
     */
    PageResult<ForwarderFeeDO> getForwarderFeePage(ForwarderFeePageReqVO pageReqVO);

    List<ForwarderFeeDO> getListByShipmentId(Long shipmentId);

    void deleteByShipmentId(Long shipmentId);

    void insertBatch(List<ForwarderFeeDO> feeList);

    PageResult<ForwarderFeeDO> getForwarderFeeApplyPage(ForwarderFeePageReqVO pageReqVO);

    void updateStatusByCodeList(DmsForwarderFeeDTO dmsForwarderFeeDTO);

    List<ForwarderFeeDO> getListByCodeList(List<String> list);

    String getNewCode();

    JsonAmount getFeeShareAmountByCode(String businessCode);

    void updateBelongFlagByCode(String businessKey, Integer value);

    List<ForwarderFeeDO> getListByPaymentCode(String code);

    Map<String, String> getShipmentCodeByCodes(List<String> relationCodes);

    Map<String, List<JsonAmount>> getAmountByShipmentCodes(List<String> relationCodes);
}