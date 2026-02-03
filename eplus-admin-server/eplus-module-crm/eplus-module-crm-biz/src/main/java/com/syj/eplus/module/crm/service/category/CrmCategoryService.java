package com.syj.eplus.module.crm.service.category;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.syj.eplus.module.crm.controller.admin.category.vo.CrmCategoryPageReqVO;
import com.syj.eplus.module.crm.controller.admin.category.vo.CrmCategoryRespVO;
import com.syj.eplus.module.crm.controller.admin.category.vo.CrmCategorySaveReqVO;
import com.syj.eplus.module.crm.controller.admin.category.vo.CrmCategorySimpleRespVO;
import com.syj.eplus.module.crm.dal.dataobject.category.CrmCategoryDO;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 客户分类 Service 接口
 *
 * @author eplus
 */
public interface CrmCategoryService {

    /**
     * 创建客户分类
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createCategory(@Valid CrmCategorySaveReqVO createReqVO);

    /**
     * 更新客户分类
     *
     * @param updateReqVO 更新信息
     */
    void updateCategory(@Valid CrmCategorySaveReqVO updateReqVO);

    /**
     * 删除客户分类
     *
     * @param id 编号
     */
    void deleteCategory(Long id);

    /**
     * 获得客户分类
     *
     * @param id 编号
     * @return 客户分类
     */
    CrmCategoryDO getCategory(Long id);

    /**
     * 获得客户分类分页
     *
     * @param pageReqVO 分页查询
     * @return 客户分类分页
     */
    PageResult<CrmCategoryRespVO> getCategoryPage(CrmCategoryPageReqVO pageReqVO);

    /**
     * 获取客户分类树形结构
     *
     * @return
     */

    List<CrmCategorySimpleRespVO> getSimpleList();

    /**
     * 根据id获取客户分类父节点信息
     *
     * @param id
     * @return
     */
    String getProfixCode(Long id);

    List<CrmCategoryDO> getListByIdList(List<Long> list);

    /**
     * 根据id获取客户分类名称
     * @param list id列表
     * @return 名称
     */
    String getNameByIdList(List<Long> list);

    /**
     * 根据id获取客户分类名称
     * @param list id列表
     * @return 名称
     */
    Map<Long, String> getCategroyNameMap(List<Long> list);
}