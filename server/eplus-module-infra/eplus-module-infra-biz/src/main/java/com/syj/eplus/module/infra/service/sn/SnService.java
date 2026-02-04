package com.syj.eplus.module.infra.service.sn;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.syj.eplus.module.infra.controller.admin.sn.vo.SnPageReqVO;
import com.syj.eplus.module.infra.controller.admin.sn.vo.SnSaveReqVO;
import com.syj.eplus.module.infra.dal.dataobject.sn.SnDO;

import javax.validation.Valid;

/**
 * 编号 Service 接口
 *
 * @author 波波
 */
public interface SnService {

    /**
     * 创建编号
     *
     * @param saveReqVO 创建信息
     * @return 编号
     */
    Long createSn(@Valid SnSaveReqVO saveReqVO);

    /**
     * 更新编号
     *
     * @param saveReqVO 更新信息
     */
    void updateSn(@Valid SnSaveReqVO saveReqVO);

    /**
     * 删除编号
     *
     * @param id 编号
     */
    void deleteSn(Long id);

    /**
     * 获得编号
     *
     * @param id 编号
     * @return 编号
     */
    SnDO getSn(Long id);

    /**
     * 获得编号分页
     *
     * @param pageReqVO 分页查询
     * @return 编号分页
     */
    PageResult<SnDO> getSnPage(SnPageReqVO pageReqVO);

    /**
     * 根据类型和编码前缀获取编号
     *
     * @param type 类型
     * @param codePrefix 编码前缀
     * @return 编号
     */
    SnDO getSnByType(String type, String codePrefix);

    /**
     * 获取并递增序列号
     *
     * @param type 类型
     * @param codePrefix 编码前缀
     * @return 序列号对象
     */
    SnDO getAndIncrementSn(String type, String codePrefix);
}