package com.syj.eplus.module.crm.service.convent;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.iocoder.yudao.framework.common.dict.CommonDict;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.syj.eplus.module.crm.controller.admin.category.vo.CrmCategoryRespVO;
import com.syj.eplus.module.crm.controller.admin.category.vo.CrmCategorySimpleRespVO;
import com.syj.eplus.module.crm.dal.dataobject.category.CrmCategoryDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

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
public interface CrmCategoryConvent {
    CrmCategoryConvent INSTANCE = Mappers.getMapper(CrmCategoryConvent.class);

    CrmCategorySimpleRespVO convert(CrmCategoryDO bean);

    List<CrmCategorySimpleRespVO> convert1(List<CrmCategoryDO> beans);

    List<CrmCategoryRespVO> convert2(List<CrmCategoryDO> beans);

    /**
     * 构建树形产品类别
     *
     * @param sourceList 原始数据
     * @return 转换后的父子级数组
     */
    default List<CrmCategorySimpleRespVO> convertChildrenList(List<CrmCategoryDO> sourceList, Map<Long, CrmCategoryDO> CrmCategoryDOMap) {
        if (CollUtil.isEmpty(sourceList)) {
            return null;
        }
        List<CrmCategorySimpleRespVO> categorySimpleRespVOList = convert1(sourceList);
        List<CrmCategorySimpleRespVO> childrenCategory = filterList(categorySimpleRespVOList, r -> ObjectUtil.isNotEmpty(r.getParentId()));
        if (CollUtil.isEmpty(childrenCategory)) {
            return null;
        }
        childrenCategory.forEach(s -> {
            CrmCategoryDO CrmCategoryDO = CrmCategoryDOMap.get(s.getParentId());
            if (Objects.nonNull(CrmCategoryDO)) {
                s.setParentName(CrmCategoryDO.getName());
            }
        });
        Map<Long, List<CrmCategorySimpleRespVO>> parentChildrenCategory = convertMultiMap(childrenCategory, CrmCategorySimpleRespVO::getParentId);
        for (CrmCategorySimpleRespVO categorySimpleRespVO : categorySimpleRespVOList) {
            categorySimpleRespVO.setChildren(parentChildrenCategory.get(categorySimpleRespVO.getId()));
        }
        List<CrmCategorySimpleRespVO> categorySimpleRespVOS = filterList(categorySimpleRespVOList, r -> ObjectUtil.isNotEmpty(r.getParentId()));
        return categorySimpleRespVOS;
    }

    default PageResult<CrmCategoryRespVO> convertCategoryPageResp(PageResult<CrmCategoryDO> CrmCategoryDOPageResult,Map<Long, String> nameMap ) {
        Long total = CrmCategoryDOPageResult.getTotal();
        List<CrmCategoryDO> CrmCategoryDOList = CrmCategoryDOPageResult.getList();
        if (CollUtil.isEmpty(CrmCategoryDOList)) {
            return null;
        }
//        Map<Long, String> categoryMap = CrmCategoryDOList.stream().collect(Collectors.toMap(CrmCategoryDO::getId, s -> s.getName()));
        List<CrmCategoryRespVO> categoryRespVOS = convert2(CrmCategoryDOList);
        categoryRespVOS.forEach(s -> {
            if (s.getParentId() == 0L) {
                s.setParentName(CommonDict.HYPHEN);
            } else {
                s.setParentName(nameMap.get(s.getParentId()));
            }
        });
        return new PageResult<CrmCategoryRespVO>().setTotal(total).setList(categoryRespVOS);
    }
}
