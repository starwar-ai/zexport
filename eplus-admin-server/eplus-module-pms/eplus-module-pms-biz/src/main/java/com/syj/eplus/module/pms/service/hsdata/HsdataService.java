package com.syj.eplus.module.pms.service.hsdata;


import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.syj.eplus.module.pms.controller.admin.hsdata.vo.HsdataPageReqVO;
import com.syj.eplus.module.pms.controller.admin.hsdata.vo.HsdataRespVO;
import com.syj.eplus.module.pms.controller.admin.hsdata.vo.HsdataSaveReqVO;
import com.syj.eplus.module.pms.controller.admin.hsdata.vo.HsdataSimpleRespVO;
import com.syj.eplus.module.pms.dal.dataobject.hsdata.HsdataDO;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 海关编码 Service 接口
 *
 * @author ePlus
 */
public interface HsdataService {

    /**
     * 创建海关编码
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createHsdata(@Valid HsdataSaveReqVO createReqVO);

    /**
     * 更新海关编码
     *
     * @param updateReqVO 更新信息
     */
    void updateHsdata(@Valid HsdataSaveReqVO updateReqVO);

    /**
     * 删除海关编码
     *
     * @param id 编号
     */
    void deleteHsdata(Long id);

    /**
     * 获得海关编码
     *
     * @param id 编号
     * @return 海关编码
     */
    HsdataRespVO getHsdata(Long id);

    /**
     * 获得海关编码分页
     *
     * @param pageReqVO 分页查询
     * @return 海关编码分页
     */
    PageResult<HsdataDO> getHsdataPage(HsdataPageReqVO pageReqVO);

    /**
     * 获取海关精简列表
     *
     * @return
     */
    List<HsdataSimpleRespVO> getHsdataSimpleList(String code);

    /**
     * 获取海关map
     *
     * @return
     */
    Map<String, HsdataSimpleRespVO> getHsdataSimpleList(List<String> idList);


    /**
     * 通过海关编码编号获取海关编码信息
     *
     * @param code 海关编码编号
     * @return 海关编码信息
     */
    HsdataSimpleRespVO getHsdataByCode(String code);

    List<String> getSimpleUnitHsdata();

    Map<Long, HsdataDO> getListByIds(List<Long> hsIdList);
}