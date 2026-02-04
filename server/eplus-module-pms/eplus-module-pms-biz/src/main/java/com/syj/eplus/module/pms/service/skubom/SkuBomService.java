package com.syj.eplus.module.pms.service.skubom;


import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.baomidou.mybatisplus.extension.service.IService;
import com.syj.eplus.module.pms.api.sku.dto.SkuBomDTO;
import com.syj.eplus.module.pms.controller.admin.skubom.vo.SkuBomPageReqVO;
import com.syj.eplus.module.pms.controller.admin.skubom.vo.SkuBomSaveReqVO;
import com.syj.eplus.module.pms.dal.dataobject.skubom.SkuBomDO;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 产品SKU BOM Service 接口
 *
 * @author eplus
 */
public interface SkuBomService extends IService<SkuBomDO> {

    /**
     * 创建产品SKU BOM
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createSkuBom(@Valid SkuBomSaveReqVO createReqVO);

    /**
     * 更新产品SKU BOM
     *
     * @param updateReqVO 更新信息
     */
    void updateSkuBom(@Valid SkuBomSaveReqVO updateReqVO);

    /**
     * 删除产品SKU BOM
     *
     * @param id 编号
     */
    void deleteSkuBom(Long id);

    /**
     * 获得产品SKU BOM
     *
     * @param id 编号
     * @return 产品SKU BOM
     */
    SkuBomDO getSkuBom(Long id);

    /**
     * 获得产品SKU BOM分页
     *
     * @param pageReqVO 分页查询
     * @return 产品SKU BOM分页
     */
    PageResult<SkuBomDO> getSkuBomPage(SkuBomPageReqVO pageReqVO);


    /**
     * 批量插入skuBom
     *
     * @param skuBomDOList
     */
    void batchSaveSkuBom(List<SkuBomDO> skuBomDOList);

    /**
     * 通过父节点id获取子节点列表
     *
     * @param parentId 父节点id
     * @return 子节点列表
     */
    List<SkuBomDO> getSkuBomListByParentId(Long parentId);

    /**
     * 通过父节点id批量删除产品
     *
     * @param parentId 父节点id
     */
    void batchDeleteSkuBomByParentId(Long parentId);

    /**
     * 根据parentidList获取子产品列表
     *
     * @param parentIdList 组合产品id列表
     * @return 子产品列表
     */
    List<SkuBomDO> getSkuBomListByParentIdList(List<Long> parentIdList);

    /**
     * 根据父节点获取子节点skubom缓存
     *
     * @param parentId 父节点id
     * @return skubom缓存
     */
    Map<Long, List<SkuBomDO>> getSkuBomDOMapByParentId(Long parentId);

    /**
     * 新增skuBom
     *
     * @param skuBomDO
     */
    void insertSkuBom(SkuBomDO skuBomDO);

    List<SkuBomDO> getSkuBomListBYSkuId(Long skuId);

     Map<Long,List<SkuBomDTO>> getAllBomDTOMap();

    void updateSkuBom(Long sourceId, Long id);
}
