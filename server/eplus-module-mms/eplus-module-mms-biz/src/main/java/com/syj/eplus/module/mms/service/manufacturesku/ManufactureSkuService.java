package com.syj.eplus.module.mms.service.manufacturesku;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.syj.eplus.module.mms.controller.admin.manufacturesku.vo.ManufactureSkuPageReqVO;
import com.syj.eplus.module.mms.controller.admin.manufacturesku.vo.ManufactureSkuRespVO;
import com.syj.eplus.module.mms.controller.admin.manufacturesku.vo.ManufactureSkuSaveInfoReqVO;
import com.syj.eplus.module.mms.controller.admin.manufacturesku.vo.ManufactureSkuSaveReqVO;
import com.syj.eplus.module.mms.dal.dataobject.manufacturesku.ManufactureSkuDO;

import javax.validation.Valid;
import java.util.List;

/**
 * 加工单产品 Service 接口
 *
 * @author zhangcm
 */
public interface ManufactureSkuService {

    /**
     * 创建加工单产品
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createManufactureSku(@Valid ManufactureSkuSaveReqVO createReqVO);

    /**
     * 更新加工单产品
     *
     * @param updateReqVO 更新信息
     */
    void updateManufactureSku(@Valid ManufactureSkuSaveReqVO updateReqVO);

    /**
     * 删除加工单产品
     *
     * @param id 编号
     */
    void deleteManufactureSku(Long id);

    /**
     * 获得加工单产品
     *
* @param  id 编号 
     * @return 加工单产品
     */
ManufactureSkuRespVO getManufactureSku(Long id);

    /**
     * 获得加工单产品分页
     *
     * @param pageReqVO 分页查询
     * @return 加工单产品分页
     */
    PageResult<ManufactureSkuDO> getManufactureSkuPage(ManufactureSkuPageReqVO pageReqVO);

    void createBatch(List<ManufactureSkuSaveInfoReqVO> children, Long manufactureId) throws Exception;

    void updateBatch(List<ManufactureSkuSaveInfoReqVO> children, Long manufactureId);

    void deleteByManufactureId(Long manufactureId);

    List<ManufactureSkuDO> selectListByManufactureId(Long manufactureId);

    List<ManufactureSkuDO> selectListByManufactureIds(List<Long> manufactureIdList);

}