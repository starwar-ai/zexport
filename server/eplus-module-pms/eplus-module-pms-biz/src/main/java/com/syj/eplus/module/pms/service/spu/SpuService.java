package com.syj.eplus.module.pms.service.spu;


import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.syj.eplus.module.pms.controller.admin.spu.vo.SpuInfoSaveReqVO;
import com.syj.eplus.module.pms.controller.admin.spu.vo.SpuPageReqVO;
import com.syj.eplus.module.pms.dal.dataobject.spu.SpuDO;

import javax.validation.Valid;

/**
 * 商品 Service 接口
 *
 * @author eplus
 */
public interface SpuService {
    String SN_TYPE = "SN_SPU";

    /**
     * 创建商品
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createSpu(@Valid SpuInfoSaveReqVO createReqVO);

    /**
     * 更新商品
     *
     * @param updateReqVO 更新信息
     */
    Long updateSpu(@Valid SpuInfoSaveReqVO updateReqVO);

    /**
     * 删除商品
     *
     * @param id 编号
     */
    void deleteSpu(Long id, Boolean deleted);

    /**
     * 获得商品
     *
     * @param id 编号
     * @return 商品
     */
    SpuDO getSpu(Long id);

    /**
     * 获得商品分页
     *
     * @param pageReqVO 分页查询
     * @return 商品分页
     */
    PageResult<SpuDO> getSpuPage(SpuPageReqVO pageReqVO);

}