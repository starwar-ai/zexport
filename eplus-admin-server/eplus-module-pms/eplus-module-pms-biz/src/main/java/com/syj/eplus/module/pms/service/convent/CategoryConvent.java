package com.syj.eplus.module.pms.service.convent;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.iocoder.yudao.framework.common.dict.CommonDict;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.syj.eplus.module.pms.controller.admin.category.vo.CategoryRespVO;
import com.syj.eplus.module.pms.controller.admin.category.vo.CategorySimpleRespVO;
import com.syj.eplus.module.pms.dal.dataobject.category.CategoryDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMultiMap;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.filterList;

/**
 * @Description：
 * @Author：du
 * @Date：2024/2/6 19:21
 */
@Mapper
public interface CategoryConvent {
    CategoryConvent INSTANCE = Mappers.getMapper(CategoryConvent.class);

    CategorySimpleRespVO convert(CategoryDO bean);

    List<CategorySimpleRespVO> convert1(List<CategoryDO> beans);

    List<CategoryRespVO> convert2(List<CategoryDO> beans);

    /**
     * 构建树形产品类别
     *
     * @param sourceList 原始数据
     * @return 转换后的父子级数组
     */
    default List<CategorySimpleRespVO> convertChildrenList(List<CategoryDO> sourceList, Map<Long, CategoryDO> categoryDOMap) {
        if (CollUtil.isEmpty(sourceList)) {
            return null;
        }
        List<CategorySimpleRespVO> categorySimpleRespVOList = convert1(sourceList);
        List<CategorySimpleRespVO> childrenCategory = filterList(categorySimpleRespVOList, r -> ObjectUtil.isNotEmpty(r.getParentId()));
        if (CollUtil.isEmpty(childrenCategory)) {
            return null;
        }
        childrenCategory.forEach(s -> {
            CategoryDO categoryDO = categoryDOMap.get(s.getParentId());
            if (Objects.nonNull(categoryDO)) {
                s.setParentName(categoryDO.getName());
            }
        });
        Map<Long, List<CategorySimpleRespVO>> parentChildrenCategory = convertMultiMap(childrenCategory, CategorySimpleRespVO::getParentId);
        // 对每个父节点的子节点按 code 排序
        parentChildrenCategory.forEach((parentId, children) -> {
            if (CollUtil.isNotEmpty(children)) {
                children.sort(Comparator.comparing(CategorySimpleRespVO::getCode, Comparator.nullsLast(Comparator.naturalOrder())));
            }
        });
        for (CategorySimpleRespVO categorySimpleRespVO : categorySimpleRespVOList) {
            categorySimpleRespVO.setChildren(parentChildrenCategory.get(categorySimpleRespVO.getId()));
        }
        List<CategorySimpleRespVO> categorySimpleRespVOS = filterList(categorySimpleRespVOList, r -> ObjectUtil.isNotEmpty(r.getParentId()));
        return categorySimpleRespVOS;
    }

    default PageResult<CategoryRespVO> convertCategoryPageResp(PageResult<CategoryDO> categoryDOPageResult,Map<Long, String> nameMap ) {
        Long total = categoryDOPageResult.getTotal();
        List<CategoryDO> categoryDOList = categoryDOPageResult.getList();
        if (CollUtil.isEmpty(categoryDOList)) {
            return null;
        }
//        Map<Long, String> categoryMap = categoryDOList.stream().collect(Collectors.toMap(CategoryDO::getId, s -> s.getName()));
        List<CategoryRespVO> categoryRespVOS = convert2(categoryDOList);
        categoryRespVOS.forEach(s -> {
            if (s.getParentId() == 0L) {
                s.setParentName(CommonDict.HYPHEN);
            } else {
                s.setParentName(nameMap.get(s.getParentId()));
            }
        });
        return new PageResult<CategoryRespVO>().setTotal(total).setList(categoryRespVOS);
    }
    CategoryRespVO convertCategoryResp(CategoryDO categoryDO);
    default CategoryRespVO convertCategoryResp(CategoryDO categoryDO,String prefixCode){
        CategoryRespVO categoryRespVO = convertCategoryResp(categoryDO);
        categoryRespVO.setProfixCode(prefixCode);
        return categoryRespVO;
    }
}
