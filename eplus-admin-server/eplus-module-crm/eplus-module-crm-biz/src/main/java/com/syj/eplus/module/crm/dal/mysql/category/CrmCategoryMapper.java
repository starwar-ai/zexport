package com.syj.eplus.module.crm.dal.mysql.category;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.crm.controller.admin.category.vo.CrmCategoryPageReqVO;
import com.syj.eplus.module.crm.dal.dataobject.category.CrmCategoryDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 客户分类 Mapper
 *
 * @author eplus
 */
@Mapper
public interface CrmCategoryMapper extends BaseMapperX<CrmCategoryDO> {

    default PageResult<CrmCategoryDO> selectPage(CrmCategoryPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<CrmCategoryDO>()
                .eqIfPresent(CrmCategoryDO::getCode, reqVO.getCode())
                .likeIfPresent(CrmCategoryDO::getName, reqVO.getName())
                .eqIfPresent(CrmCategoryDO::getCodeLen, reqVO.getCodeLen())
                .eqIfPresent(CrmCategoryDO::getParentId, reqVO.getParentId())
                .betweenIfPresent(CrmCategoryDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(CrmCategoryDO::getId));
    }


    @Select("WITH RECURSIVE recursion  AS\n" +
            "(\n" +
            "  SELECT t1.id,t1.parent_id,t1.code,t1.grade\n" +
            "\t  from crm_category t1\n" +
            "\t where t1.id= #{id}\n" +
            "  UNION ALL\n" +
            "  SELECT t2.id,t2.parent_id,t2.code,t2.grade\n" +
            "    from crm_category t2, recursion t3\n" +
            "\t WHERE t2.id=t3.parent_id\n" +
            ")\n" +
            "SELECT * FROM recursion t;")
    List<CrmCategoryDO> getParentCateGoryById(@Param("id") Long id);
}