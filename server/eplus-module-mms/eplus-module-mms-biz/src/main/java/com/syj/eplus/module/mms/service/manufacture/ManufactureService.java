package com.syj.eplus.module.mms.service.manufacture;


import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.syj.eplus.module.mms.controller.admin.manufacture.vo.ManufacturePageReqVO;
import com.syj.eplus.module.mms.controller.admin.manufacture.vo.ManufactureRespInfoVO;
import com.syj.eplus.module.mms.controller.admin.manufacture.vo.ManufactureSaveInfoReqVO;

import javax.validation.Valid;
import java.util.List;

/**
 * 加工单 Service 接口
 *
 * @author zhangcm
 */
public interface ManufactureService {

    /**
     * 创建加工单
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createManufacture(@Valid ManufactureSaveInfoReqVO createReqVO) throws Exception;

    /**
     * 更新加工单
     *
     * @param updateReqVO 更新信息
     */
    void updateManufacture(@Valid ManufactureSaveInfoReqVO updateReqVO);

    /**
     * 删除加工单
     *
     * @param id 编号
     */
    void deleteManufacture(Long id);

    /**
     * 获得加工单
     *
* @param  id 编号 
     * @return 加工单
     */
    ManufactureRespInfoVO getManufacture(Long id);

    /**
     * 获得加工单分页
     *
     * @param pageReqVO 分页查询
     * @return 加工单分页
     */
    PageResult<ManufactureRespInfoVO> getManufacturePage(ManufacturePageReqVO pageReqVO);

    void doneManufacture(Long id);

    void finishManufacture(Long id,String desc);

    void unfinishManufacture(Long id);

    void doneBatchManufacture(List<Long> idList);
}