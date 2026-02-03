package com.syj.eplus.module.pms.dal.mysql.category;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.pms.controller.admin.category.vo.CategoryPageReqVO;
import com.syj.eplus.module.pms.dal.dataobject.category.CategoryDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 产品分类 Mapper
 *
 * @author eplus
 */
@Mapper
public interface CategoryMapper extends BaseMapperX<CategoryDO> {

    default PageResult<CategoryDO> selectPage(CategoryPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<CategoryDO>()
                .eqIfPresent(CategoryDO::getCode, reqVO.getCode())
                .eqIfPresent(CategoryDO::getHsDataCode, reqVO.getHsDataCode())
                .likeIfPresent(CategoryDO::getName, reqVO.getName())
                .eqIfPresent(CategoryDO::getCodeLen, reqVO.getCodeLen())
                .eqIfPresent(CategoryDO::getHsCodeId, reqVO.getHsCodeId())
                .eqIfPresent(CategoryDO::getParentId, reqVO.getParentId())
                .betweenIfPresent(CategoryDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(CategoryDO::getId));
    }


    @Select("WITH RECURSIVE recursion  AS\n" +
            "(\n" +
            "  SELECT t1.id,t1.parent_id,t1.code,t1.grade\n" +
            "\t  from pms_category t1\n" +
            "\t where t1.id= #{id}\n" +
            "  UNION ALL\n" +
            "  SELECT t2.id,t2.parent_id,t2.code,t2.grade\n" +
            "    from pms_category t2, recursion t3\n" +
            "\t WHERE t2.id=t3.parent_id\n" +
            ")\n" +
            "SELECT * FROM recursion t;")
    List<CategoryDO> getParentCateGoryById(@Param("id") Long id);


    @Select("""
    WITH RECURSIVE category_path AS (
        SELECT id, parent_id, name, name AS full_path
        FROM pms_category WHERE id = #{id}
    
        UNION ALL
    
        SELECT c.id, c.parent_id, c.name, CONCAT(c.name, '->', cp.full_path)
        FROM pms_category c
        INNER JOIN category_path cp ON c.id = cp.parent_id
    )
    SELECT full_path\s
    FROM category_path\s
    ORDER BY LENGTH(full_path) DESC\s
    LIMIT 1
""")
    String getPathCateName(@Param("id")Long id);
}