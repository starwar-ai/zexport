package cn.iocoder.yudao.module.system.service.rolefield;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.system.controller.admin.rolefield.vo.RoleFieldPageReqVO;
import cn.iocoder.yudao.module.system.controller.admin.rolefield.vo.RoleFieldSaveReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.field.FieldDO;
import cn.iocoder.yudao.module.system.dal.dataobject.rolefield.RoleFieldDO;
import cn.iocoder.yudao.module.system.dal.mysql.rolefield.RoleFieldMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.ROLE_FIELD_NOT_EXISTS;

/**
 * 字段角色关联 Service 实现类
 *
 * @author ePlus
 */
@Service
@Validated
public class RoleFieldServiceImpl implements RoleFieldService {

    @Resource
    private RoleFieldMapper roleFieldMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createRoleField(RoleFieldSaveReqVO createReqVO) {
        // 插入
        RoleFieldDO roleField = BeanUtils.toBean(createReqVO, RoleFieldDO.class);
        roleFieldMapper.insert(roleField);
        // 返回
        return roleField.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRoleField(RoleFieldSaveReqVO updateReqVO) {
        Long roleId = updateReqVO.getRoleId();
        if (Objects.isNull(roleId)) {
            return;
        }
        List<Long> fieldIds = updateReqVO.getFieldIds();
        roleFieldMapper.delete(new LambdaQueryWrapperX<RoleFieldDO>().eq(RoleFieldDO::getRoleId, roleId));
        if (CollUtil.isEmpty(fieldIds)) {
            return;
        }
        List<RoleFieldDO> roleFieldDOList = fieldIds.stream().map(s -> new RoleFieldDO().setFieldId(s).setRoleId(roleId)).toList();
        roleFieldMapper.insertBatch(roleFieldDOList);
    }

    @Override
    public void deleteRoleField(Long id) {
        // 校验存在
        validateRoleFieldExists(id);
        // 删除
        roleFieldMapper.deleteById(id);
    }

    private void validateRoleFieldExists(Long id) {
        if (roleFieldMapper.selectById(id) == null) {
            throw exception(ROLE_FIELD_NOT_EXISTS);
        }
    }

    @Override
    public RoleFieldDO getRoleField(Long id) {
        return roleFieldMapper.selectById(id);
    }

    @Override
    public PageResult<RoleFieldDO> getRoleFieldPage(RoleFieldPageReqVO pageReqVO) {
        return roleFieldMapper.selectPage(pageReqVO);
    }

    @Override
    public List<FieldDO> getRoleFieldListByRoleIds(List<Long> roleIds) {
        return roleFieldMapper.getFieldListByRoleIds(roleIds);
    }

}