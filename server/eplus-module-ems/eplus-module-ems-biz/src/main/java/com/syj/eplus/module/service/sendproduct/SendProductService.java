package com.syj.eplus.module.service.sendproduct;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.syj.eplus.module.controller.admin.sendproduct.vo.SendProductPageReqVO;
import com.syj.eplus.module.controller.admin.sendproduct.vo.SendProductRespVO;
import com.syj.eplus.module.controller.admin.sendproduct.vo.SendProductSaveReqVO;
import com.syj.eplus.module.dal.dataobject.sendproduct.SendProductDO;

import javax.validation.Valid;
import java.util.List;

/**
 * 寄件产品 Service 接口
 *
 * @author zhangcm
 */
public interface SendProductService {

    /**
     * 创建寄件产品
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createSendProduct(@Valid SendProductSaveReqVO createReqVO);

    /**
     * 更新寄件产品
     *
     * @param updateReqVO 更新信息
     */
    void updateSendProduct(@Valid SendProductSaveReqVO updateReqVO);

    /**
     * 删除寄件产品
     *
     * @param id 编号
     */
    void deleteSendProduct(Long id);

    /**
     * 获得寄件产品
     *
* @param  id 编号 
     * @return 寄件产品
     */
SendProductRespVO getSendProduct(Long id);

    /**
     * 获得寄件产品分页
     *
     * @param pageReqVO 分页查询
     * @return 寄件产品分页
     */
    PageResult<SendProductDO> getSendProductPage(SendProductPageReqVO pageReqVO);

    List<SendProductDO> getListBysendCode(Long sendId);
}