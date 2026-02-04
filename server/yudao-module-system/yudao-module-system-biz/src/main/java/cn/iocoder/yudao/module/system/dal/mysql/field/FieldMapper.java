package cn.iocoder.yudao.module.system.dal.mysql.field;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.system.controller.admin.field.vo.FieldPageReqVO;
import cn.iocoder.yudao.module.system.controller.admin.field.vo.FieldReqVO;
import cn.iocoder.yudao.module.system.controller.admin.field.vo.TableInfoRespVO;
import cn.iocoder.yudao.module.system.dal.dataobject.field.FieldDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 系统字段 Mapper
 *
 * @author ePlus
 */
@Mapper
public interface FieldMapper extends BaseMapperX<FieldDO> {

    default PageResult<FieldDO> selectPage(FieldPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<FieldDO>()
                .likeIfPresent(FieldDO::getTableName, reqVO.getTableName())
                .eqIfPresent(FieldDO::getTableComment, reqVO.getTableComment())
                .likeIfPresent(FieldDO::getFieldName, reqVO.getFieldName())
                .eqIfPresent(FieldDO::getFieldComment, reqVO.getFieldComment())
                .eqIfPresent(FieldDO::getFieldType, reqVO.getFieldType())
                .eqIfPresent(FieldDO::getDictFlag, reqVO.getDictFlag())
                .eqIfPresent(FieldDO::getDictType, reqVO.getDictType())
                .betweenIfPresent(FieldDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(FieldDO::getId));
    }


    @Select("select distinct table_name,table_comment from system_field")
    List<TableInfoRespVO> selectTableInfo();

    default List<FieldDO> selectFieldList(FieldReqVO fieldReqVO) {
        return selectList(new LambdaQueryWrapperX<FieldDO>()
                .eqIfPresent(FieldDO::getTableName, fieldReqVO.getTableName())
                .likeIfPresent(FieldDO::getTableComment, fieldReqVO.getTableComment())
                .eqIfPresent(FieldDO::getFieldName, fieldReqVO.getFieldName())
                .likeIfPresent(FieldDO::getFieldComment, fieldReqVO.getFieldComment())
        );
    }
}