package com.syj.eplus.module.crm.service.category;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Opt;
import cn.iocoder.yudao.framework.common.dict.CommonDict;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;
import com.syj.eplus.module.crm.controller.admin.category.vo.CrmCategoryPageReqVO;
import com.syj.eplus.module.crm.controller.admin.category.vo.CrmCategoryRespVO;
import com.syj.eplus.module.crm.controller.admin.category.vo.CrmCategorySaveReqVO;
import com.syj.eplus.module.crm.controller.admin.category.vo.CrmCategorySimpleRespVO;
import com.syj.eplus.module.crm.dal.dataobject.category.CrmCategoryDO;
import com.syj.eplus.module.crm.dal.mysql.category.CrmCategoryMapper;
import com.syj.eplus.module.crm.service.convent.CrmCategoryConvent;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.crm.enums.ErrorCodeConstants.*;

/**
 * 客户分类 Service 实现类
 *
 * @author eplus
 */
@Service
@Validated
public class CrmCategoryServiceImpl implements CrmCategoryService {

    @Resource
    private CrmCategoryMapper crmCategoryMapper;

    /**
     * 最大等级数
     */
    private static final int MAX_GRADE = 3;

    @Override
    public Long createCategory(CrmCategorySaveReqVO createReqVO) {
        // 插入
        CrmCategoryDO category = BeanUtils.toBean(createReqVO, CrmCategoryDO.class);
        Integer grade = category.getGrade();
        // 等级不存在则抛异常
        grade = Opt.ofNullable(grade).orElseThrow(() -> exception(CATEGORY_NOT_EMPTY));
        if (grade > MAX_GRADE) {
            throw exception(CATEGORY_NOT_EXCEED_THREE);
        }
        crmCategoryMapper.insert(category);
        // 返回
        return category.getId();
    }

    @Override
    public void updateCategory(CrmCategorySaveReqVO updateReqVO) {
        // 校验存在
        validateCategoryExists(updateReqVO.getId());
        // 更新
        CrmCategoryDO updateObj = BeanUtils.toBean(updateReqVO, CrmCategoryDO.class);
        crmCategoryMapper.updateById(updateObj);
    }

    @Override
    public void deleteCategory(Long id) {
        // 校验存在
        validateCategoryExists(id);
        // 删除
        crmCategoryMapper.deleteById(id);
    }

    private void validateCategoryExists(Long id) {
        if (crmCategoryMapper.selectById(id) == null) {
            throw exception(CATEGORY_NOT_EXISTS);
        }
    }

    @Override
    public CrmCategoryDO getCategory(Long id) {
        return crmCategoryMapper.selectById(id);
    }

    @Override
    public PageResult<CrmCategoryRespVO> getCategoryPage(CrmCategoryPageReqVO pageReqVO) {
        PageResult<CrmCategoryDO> CrmCategoryDOPageResult = crmCategoryMapper.selectPage(pageReqVO);
        Map<Long, String> nameMap = crmCategoryMapper.selectList().stream().collect(Collectors.toMap(CrmCategoryDO::getId, CrmCategoryDO::getName));
        return CrmCategoryConvent.INSTANCE.convertCategoryPageResp(CrmCategoryDOPageResult,nameMap);
    }

    @Override
    @DataPermission(enable = false)
    public List<CrmCategorySimpleRespVO> getSimpleList() {
        List<CrmCategoryDO> CrmCategoryDOS = crmCategoryMapper.selectList();
        if (CollUtil.isEmpty(CrmCategoryDOS)) {
            return null;
        }
        Map<Long, CrmCategoryDO> CrmCategoryDOMap = CrmCategoryDOS.stream().collect(Collectors.toMap(CrmCategoryDO::getId, s -> s));
        return CrmCategoryConvent.INSTANCE.convertChildrenList(CrmCategoryDOS, CrmCategoryDOMap);
    }

    @Override
    public String getProfixCode(Long id) {
        List<CrmCategoryDO> parentCateGoryById = crmCategoryMapper.getParentCateGoryById(id);
        if (CollUtil.isEmpty(parentCateGoryById)) {
            throw exception(CATEGORY_NOT_EXISTS);
        }
        return parentCateGoryById.stream().sorted(Comparator.comparing(CrmCategoryDO::getGrade)).map(CrmCategoryDO::getCode).collect(Collectors.joining());
    }

    @Override
    public List<CrmCategoryDO> getListByIdList(List<Long> list) {
        return crmCategoryMapper.selectList(CrmCategoryDO::getId,list);
    }

    @Override
    public String getNameByIdList(List<Long> list) {
        if (CollUtil.isEmpty(list)){
            return CommonDict.EMPTY_STR;
        }
        List<CrmCategoryDO> crmCategoryDOS = crmCategoryMapper.selectBatchIds(list);
        if (CollUtil.isEmpty(crmCategoryDOS)){
            return CommonDict.EMPTY_STR;
        }
        return crmCategoryDOS.stream().map(CrmCategoryDO::getName).collect(Collectors.joining(CommonDict.COMMA));
    }

    @Override
    public Map<Long, String> getCategroyNameMap(List<Long> list) {
        List<CrmCategoryDO> crmCategoryDOList;
        if (CollUtil.isEmpty(list)){
            crmCategoryDOList = crmCategoryMapper.selectList();
        }else {
            crmCategoryDOList = crmCategoryMapper.selectBatchIds(list);
        }
        if (CollUtil.isEmpty(crmCategoryDOList)){
            return Map.of();
        }
        return crmCategoryDOList.stream().collect(Collectors.toMap(CrmCategoryDO::getId,CrmCategoryDO::getName));
    }

}