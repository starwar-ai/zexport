package com.syj.eplus.module.pms.service.brand;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.syj.eplus.module.pms.controller.admin.brand.vo.BrandPageReqVO;
import com.syj.eplus.module.pms.controller.admin.brand.vo.BrandSaveReqVO;
import com.syj.eplus.module.pms.controller.admin.brand.vo.BrandSimpleRespVO;
import com.syj.eplus.module.pms.dal.dataobject.brand.BrandDO;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 品牌 Service 接口
 *
 * @author ePlus
 */
public interface BrandService {

    /**
     * 创建品牌
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createBrand(@Valid BrandSaveReqVO createReqVO);

    /**
     * 更新品牌
     *
     * @param updateReqVO 更新信息
     */
    void updateBrand(@Valid BrandSaveReqVO updateReqVO);

    /**
     * 删除品牌
     *
     * @param id 编号
     */
    void deleteBrand(Long id);

    /**
     * 获得品牌
     *
     * @param id 编号
     * @return 品牌
     */
    BrandDO getBrand(Long id);

    /**
     * 获得品牌分页
     *
     * @param pageReqVO 分页查询
     * @return 品牌分页
     */
    PageResult<BrandDO> getBrandPage(BrandPageReqVO pageReqVO);

    /**
     * 获取品牌精简列别
     *
     * @return
     */
    List<BrandSimpleRespVO> getBrandSimpleList();

    /**
     * 根据id列表获取品牌精简列别
     *
     * @return
     */
    Map<String, BrandSimpleRespVO> getBrandSimpleList(List<String> idList);

    /**
     * 根据id列表获取品牌精简缓存
     *
     * @return
     */
    Map<Long, BrandSimpleRespVO> getBrandSimpleMap(List<Long> idList);
}