package com.syj.eplus.module.scm.service.venderpoc;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.syj.eplus.module.scm.api.vender.dto.VenderPocDTO;
import com.syj.eplus.module.scm.controller.admin.venderpoc.vo.VenderPocPageReqVO;
import com.syj.eplus.module.scm.controller.admin.venderpoc.vo.VenderPocSaveReqVO;
import com.syj.eplus.module.scm.dal.dataobject.venderpoc.VenderPocDO;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 供应商联系人 Service 接口
 *
 * @author zhangcm
 */
public interface VenderPocService {

    /**
     * 创建供应商联系人
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createVenderPoc(@Valid VenderPocSaveReqVO createReqVO);

    /**
     * 更新供应商联系人
     *
     * @param updateReqVO 更新信息
     */
    void updateVenderPoc(@Valid VenderPocSaveReqVO updateReqVO);

    /**
     * 删除供应商联系人
     *
     * @param id 编号
     */
    void deleteVenderPoc(Long id);

    /**
     * 获得供应商联系人
     *
     * @param id 编号
     * @return 供应商联系人
     */
    VenderPocDO getVenderPoc(Long id);

    /**
     * 获得供应商联系人分页
     *
     * @param pageReqVO 分页查询
     * @return 供应商联系人分页
     */
    PageResult<VenderPocDO> getVenderPocPage(VenderPocPageReqVO pageReqVO);

    /**
     * 批量新增供应商联系人
     *
     * @param venderPocDOList
     */
    void batchSavePoc(List<VenderPocDO> venderPocDOList);

    /**
     * 批量新增供应商联系人
     *
     * @param venderId
     * @reeturn
     */
    List<VenderPocDO> getPocListByVenderId(Long venderId);

    /**
     * 批量删除供应商联系人信息
     *
     * @param idList
     */
    void batchDeletePocByIds(List<Long> idList);

    /**
     * 获取默认联系人缓存
     * @return
     */
    Map<Long,VenderPocDO> getDefaultVenderPocMap();

    Map<Long, List<VenderPocDTO>> getVenderPocMap(List<Long> idList);
}