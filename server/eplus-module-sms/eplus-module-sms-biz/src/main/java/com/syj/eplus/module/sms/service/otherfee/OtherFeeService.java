package com.syj.eplus.module.sms.service.otherfee;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.syj.eplus.module.sms.controller.admin.otherfee.vo.OtherFeePageReqVO;
import com.syj.eplus.module.sms.controller.admin.otherfee.vo.OtherFeeRespVO;
import com.syj.eplus.module.sms.controller.admin.otherfee.vo.OtherFeeSaveReqVO;
import com.syj.eplus.module.sms.dal.dataobject.otherfee.OtherFeeDO;

import javax.validation.Valid;

/**
 * 其他费用 Service 接口
 *
 * @author ePlus
 */
public interface OtherFeeService {

    /**
     * 创建其他费用
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createOtherFee(@Valid OtherFeeSaveReqVO createReqVO);

    /**
     * 更新其他费用
     *
     * @param updateReqVO 更新信息
     */
    void updateOtherFee(@Valid OtherFeeSaveReqVO updateReqVO);

    /**
     * 删除其他费用
     *
     * @param id 编号
     */
    void deleteOtherFee(Long id);

    /**
     * 获得其他费用
     *
* @param  id 编号 
     * @return 其他费用
     */
OtherFeeRespVO getOtherFee(Long id);

    /**
     * 获得其他费用分页
     *
     * @param pageReqVO 分页查询
     * @return 其他费用分页
     */
    PageResult<OtherFeeDO> getOtherFeePage(OtherFeePageReqVO pageReqVO);
}