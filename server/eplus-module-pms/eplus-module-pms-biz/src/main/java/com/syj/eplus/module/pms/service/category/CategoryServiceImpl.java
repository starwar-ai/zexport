package com.syj.eplus.module.pms.service.category;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Opt;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;
import com.syj.eplus.module.pms.controller.admin.category.vo.CategoryPageReqVO;
import com.syj.eplus.module.pms.controller.admin.category.vo.CategoryRespVO;
import com.syj.eplus.module.pms.controller.admin.category.vo.CategorySaveReqVO;
import com.syj.eplus.module.pms.controller.admin.category.vo.CategorySimpleRespVO;
import com.syj.eplus.module.pms.dal.dataobject.category.CategoryDO;
import com.syj.eplus.module.pms.dal.mysql.category.CategoryMapper;
import com.syj.eplus.module.pms.service.convent.CategoryConvent;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.pms.enums.ErrorCodeConstants.*;

/**
 * 产品分类 Service 实现类
 *
 * @author eplus
 */
@Service
@Validated
public class CategoryServiceImpl implements CategoryService {

    @Resource
    private CategoryMapper categoryMapper;

    /**
     * 最大等级数
     */
    private static final int MAX_GRADE = 3;

    @Override
    public Long createCategory(CategorySaveReqVO createReqVO) {
        // 插入
        CategoryDO category = BeanUtils.toBean(createReqVO, CategoryDO.class);
        Integer grade = category.getGrade();
        // 等级不存在则抛异常
        grade = Opt.ofNullable(grade).orElseThrow(() -> exception(CATEGORY_NOT_EMPTY));
        if (grade > MAX_GRADE) {
            throw exception(CATEGORY_NOT_EXCEED_THREE);
        }
        categoryMapper.insert(category);
        // 返回
        return category.getId();
    }

    @Override
    public void updateCategory(CategorySaveReqVO updateReqVO) {
        // 校验存在
        validateCategoryExists(updateReqVO.getId());
        // 更新
        CategoryDO updateObj = BeanUtils.toBean(updateReqVO, CategoryDO.class);
        categoryMapper.updateById(updateObj);
    }

    @Override
    public void deleteCategory(Long id) {
        // 校验存在
        validateCategoryExists(id);
        // 删除
        categoryMapper.deleteById(id);
    }

    private CategoryDO validateCategoryExists(Long id) {
        CategoryDO categoryDO = categoryMapper.selectById(id);
        if (categoryDO == null) {
            throw exception(CATEGORY_NOT_EXISTS);
        }
        return categoryDO;
    }

    @Override
    public CategoryRespVO getCategory(Long id) {
        CategoryDO categoryDO = validateCategoryExists(id);
        String profixCode = getProfixCode(id);
        return CategoryConvent.INSTANCE.convertCategoryResp(categoryDO,profixCode);
    }

    @Override
    public PageResult<CategoryRespVO> getCategoryPage(CategoryPageReqVO pageReqVO) {
        PageResult<CategoryDO> categoryDOPageResult = categoryMapper.selectPage(pageReqVO);
        Map<Long, String> nameMap = categoryMapper.selectList().stream().collect(Collectors.toMap(CategoryDO::getId, CategoryDO::getName));
        PageResult<CategoryRespVO> categoryRespVOPageResult = CategoryConvent.INSTANCE.convertCategoryPageResp(categoryDOPageResult, nameMap);
       if(Objects.isNull(categoryRespVOPageResult)) {
           return null;
       }
        // 对返回结果按 code 排序
        List<CategoryRespVO> list = categoryRespVOPageResult.getList();
        if (CollUtil.isNotEmpty(list)) {
            list.sort(Comparator.comparing(CategoryRespVO::getCode, Comparator.nullsLast(Comparator.naturalOrder())));
        }
        return  categoryRespVOPageResult;
    }

    @Override
    @DataPermission(enable = false)
    public List<CategorySimpleRespVO> getSimpleList() {
        List<CategoryDO> categoryDOS = categoryMapper.selectList();
        if (CollUtil.isEmpty(categoryDOS)) {
            return null;
        }
        Map<Long, CategoryDO> categoryDOMap = categoryDOS.stream().collect(Collectors.toMap(CategoryDO::getId, s -> s));
        List<CategorySimpleRespVO> simpleRespVOS = CategoryConvent.INSTANCE.convertChildrenList(categoryDOS, categoryDOMap);
        // 对返回结果按 code 排序
        if (CollUtil.isNotEmpty(simpleRespVOS)) {
            simpleRespVOS.sort(Comparator.comparing(CategorySimpleRespVO::getCode, Comparator.nullsLast(Comparator.naturalOrder())));
        }
        return simpleRespVOS;
    }

    @Override
    public String getProfixCode(Long id) {
        List<CategoryDO> parentCateGoryById = categoryMapper.getParentCateGoryById(id);
        if (CollUtil.isEmpty(parentCateGoryById)) {
            throw exception(CATEGORY_NOT_EXISTS);
        }
        return parentCateGoryById.stream().sorted(Comparator.comparing(CategoryDO::getGrade)).map(CategoryDO::getCode).collect(Collectors.joining());
    }

    @Override
    public List<CategoryDO> getListByIdList(List<Long> list) {
        return categoryMapper.selectList(CategoryDO::getId,list);
    }

    @Override
    public CategorySimpleRespVO getCategoryDTO(Long id) {
        if (Objects.isNull(id)){
            return null;
        }
        CategoryDO categoryDO = categoryMapper.selectOne(CategoryDO::getId, id);
        if (Objects.isNull(categoryDO)){
            return null;
        }
        return CategoryConvent.INSTANCE.convert(categoryDO);
    }

    @Override
    public String getPathCateName(Long id) {
        return categoryMapper.getPathCateName(id);
    }
}