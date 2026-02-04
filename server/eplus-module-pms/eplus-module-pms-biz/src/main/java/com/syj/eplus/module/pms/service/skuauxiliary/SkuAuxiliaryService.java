package com.syj.eplus.module.pms.service.skuauxiliary;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.syj.eplus.module.pms.api.sku.dto.SkuAuxiliaryDTO;
import com.syj.eplus.module.pms.controller.admin.skuauxiliary.vo.SkuAuxiliaryPageReqVO;
import com.syj.eplus.module.pms.controller.admin.skuauxiliary.vo.SkuAuxiliaryRespVO;
import com.syj.eplus.module.pms.controller.admin.skuauxiliary.vo.SkuAuxiliarySaveReqVO;
import com.syj.eplus.module.pms.dal.dataobject.skuauxiliary.SkuAuxiliaryDO;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 产品辅料配比 Service 接口
 *
 * @author zhangcm
 */
public interface SkuAuxiliaryService {

    /**
     * 创建产品辅料配比
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createSkuAuxiliary(@Valid SkuAuxiliarySaveReqVO createReqVO);

    /**
     * 更新产品辅料配比
     *
     * @param updateReqVO 更新信息
     */
    void updateSkuAuxiliary(@Valid SkuAuxiliarySaveReqVO updateReqVO);

    /**
     * 删除产品辅料配比
     *
     * @param id 编号
     */
    void deleteSkuAuxiliary(Long id);

    /**
     * 获得产品辅料配比
     *
* @param  id 编号 
     * @return 产品辅料配比
     */
SkuAuxiliaryRespVO getSkuAuxiliary(Long id);

    /**
     * 获得产品辅料配比分页
     *
     * @param pageReqVO 分页查询
     * @return 产品辅料配比分页
     */
    PageResult<SkuAuxiliaryDO> getSkuAuxiliaryPage(SkuAuxiliaryPageReqVO pageReqVO);

    void batchSave(List<SkuAuxiliaryDO> skuAuxiliaryList);

    List<SkuAuxiliaryDO> getListBySkuCode(String code);

    void batchDeleteBySkuCode(String code);

    Map<String, List<SkuAuxiliaryDTO>> getMapBySkuCodeList(List<String> codeList);

    List<SkuAuxiliaryDO> getAuxiliaryListByCodeList(List<String> skuCodeList);
}