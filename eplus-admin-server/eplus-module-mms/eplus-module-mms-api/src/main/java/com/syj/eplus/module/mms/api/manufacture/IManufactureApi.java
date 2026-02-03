package com.syj.eplus.module.mms.api.manufacture;

import com.syj.eplus.module.mms.api.manufacture.vo.ManufactureSaveDTO;

/**
 * 加工单-供其他模块调用api
 */
public interface IManufactureApi {

    /**
     * 创建加工单
     * @param manufactureSaveDTO
     * @return
     */
    Boolean createManufacture(ManufactureSaveDTO manufactureSaveDTO) ;

}
