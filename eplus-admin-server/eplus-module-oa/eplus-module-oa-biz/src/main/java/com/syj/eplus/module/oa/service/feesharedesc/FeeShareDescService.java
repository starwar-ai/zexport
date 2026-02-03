package com.syj.eplus.module.oa.service.feesharedesc;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.syj.eplus.module.oa.controller.admin.feesharedesc.vo.FeeShareDescPageReqVO;
import com.syj.eplus.module.oa.controller.admin.feesharedesc.vo.FeeShareDescRespVO;
import com.syj.eplus.module.oa.controller.admin.feesharedesc.vo.FeeShareDescSaveReqVO;
import com.syj.eplus.module.oa.dal.dataobject.feesharedesc.FeeShareDescDO;

/**
 * 费用归集具体名称 Service 接口
 *
 * @author zhangcm
 */
public interface FeeShareDescService {



    /**
     * 删除费用归集具体名称
     *
     * @param id 编号
     */
    void deleteFeeShareDesc(Long id);

    /**
     * 获得费用归集具体名称
     *
* @param  id 编号 
     * @return 费用归集具体名称
     */
FeeShareDescRespVO getFeeShareDesc(Long id);

    /**
     * 获得费用归集具体名称分页
     *
     * @param pageReqVO 分页查询
     * @return 费用归集具体名称分页
     */
    PageResult<FeeShareDescDO> getFeeShareDescPage(FeeShareDescPageReqVO pageReqVO);


    void updateAllFeeShareDesc(FeeShareDescSaveReqVO createReqVO);
}