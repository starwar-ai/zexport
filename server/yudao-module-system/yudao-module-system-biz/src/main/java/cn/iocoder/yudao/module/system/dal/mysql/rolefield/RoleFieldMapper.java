package cn.iocoder.yudao.module.system.dal.mysql.rolefield;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.system.controller.admin.rolefield.vo.RoleFieldPageReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.field.FieldDO;
import cn.iocoder.yudao.module.system.dal.dataobject.rolefield.RoleFieldDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 字段角色关联 Mapper
 *
 * @author ePlus
 */
@Mapper
public interface RoleFieldMapper extends BaseMapperX<RoleFieldDO> {

    default PageResult<RoleFieldDO> selectPage(RoleFieldPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<RoleFieldDO>()
                .eqIfPresent(RoleFieldDO::getFieldId, reqVO.getFieldId())
                .eqIfPresent(RoleFieldDO::getRoleId, reqVO.getRoleId())
                .betweenIfPresent(RoleFieldDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(RoleFieldDO::getId));
    }


    @Select("<script> " +
            "select distinct t.id,t.field_name,t.table_name from system_field t\n" +
            "left join system_role_field t1 on t1.field_id = t.id\n" +
            "where t1.deleted = 0 and t1.role_id in \n" +
            "<foreach collection='roleIds' index='index' item='roleId' open='(' separator=',' close=')'>#{roleId}</foreach>" +
            "</script>")
    List<FieldDO> getFieldListByRoleIds(@Param("roleIds") List<Long> roleIds);
}