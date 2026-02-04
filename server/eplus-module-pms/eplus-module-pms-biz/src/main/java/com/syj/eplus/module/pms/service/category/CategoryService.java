package com.syj.eplus.module.pms.service.category;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.syj.eplus.module.pms.controller.admin.category.vo.CategoryPageReqVO;
import com.syj.eplus.module.pms.controller.admin.category.vo.CategoryRespVO;
import com.syj.eplus.module.pms.controller.admin.category.vo.CategorySaveReqVO;
import com.syj.eplus.module.pms.controller.admin.category.vo.CategorySimpleRespVO;
import com.syj.eplus.module.pms.dal.dataobject.category.CategoryDO;

import javax.validation.Valid;
import java.util.List;

/**
 * 产品分类 Service 接口
 *
 * @author eplus
 */
public interface CategoryService {

    /**
     * 创建产品分类
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createCategory(@Valid CategorySaveReqVO createReqVO);

    /**
     * 更新产品分类
     *
     * @param updateReqVO 更新信息
     */
    void updateCategory(@Valid CategorySaveReqVO updateReqVO);

    /**
     * 删除产品分类
     *
     * @param id 编号
     */
    void deleteCategory(Long id);

    /**
     * 获得产品分类
     *
     * @param id 编号
     * @return 产品分类
     */
    CategoryRespVO getCategory(Long id);

    /**
     * 获得产品分类分页
     *
     * @param pageReqVO 分页查询
     * @return 产品分类分页
     */
    PageResult<CategoryRespVO> getCategoryPage(CategoryPageReqVO pageReqVO);

    /**
     * 获取产品分类树形结构
     *
     * @return
     */

    List<CategorySimpleRespVO> getSimpleList();

    /**
     * 根据id获取产品分类父节点信息
     *
     * @param id
     * @return
     */
    String getProfixCode(Long id);

    List<CategoryDO> getListByIdList(List<Long> list);

    /**
     * 获得产品分类
     *
     * @param id 编号
     * @return 产品分类
     */
    CategorySimpleRespVO getCategoryDTO(Long id);

    /**
     * 通过id获取直系父节点名称
     * @param id 编号
     * @return 直系父节点名称
     */
    String getPathCateName(Long id);
}