package cn.iocoder.yudao.module.system.service.field;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.infra.api.table.DataBaseTableApi;
import cn.iocoder.yudao.module.infra.api.table.dto.FieldDTO;
import cn.iocoder.yudao.module.system.api.permission.PermissionApi;
import cn.iocoder.yudao.module.system.api.permission.dto.FieldPermissionDTO;
import cn.iocoder.yudao.module.system.controller.admin.field.vo.*;
import cn.iocoder.yudao.module.system.dal.dataobject.field.FieldDO;
import cn.iocoder.yudao.module.system.dal.mysql.field.FieldMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.FIELD_NOT_EXISTS;

/**
 * 系统字段 Service 实现类
 *
 * @author ePlus
 */
@Service
@Validated
public class FieldServiceImpl implements FieldService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    @Resource
    private FieldMapper fieldMapper;
    @Resource
    private PermissionApi permissionApi;
    @Resource
    private DataBaseTableApi dataBaseTableApi;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createField(FieldSaveReqVO createReqVO) {
        // 插入
        FieldDO field = BeanUtils.toBean(createReqVO, FieldDO.class);
        fieldMapper.insert(field);
        // 返回
        return field.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateField(FieldSaveReqVO updateReqVO) {
        // 校验存在
        validateFieldExists(updateReqVO.getId());
        // 更新
        FieldDO updateObj = BeanUtils.toBean(updateReqVO, FieldDO.class);
        fieldMapper.updateById(updateObj);
    }

    @Override
    public void deleteField(Long id) {
        // 校验存在
        validateFieldExists(id);
        // 删除
        fieldMapper.deleteById(id);
    }

    private void validateFieldExists(Long id) {
        if (fieldMapper.selectById(id) == null) {
            throw exception(FIELD_NOT_EXISTS);
        }
    }

    @Override
    public List<FieldRespVO> getFieldList(FieldReqVO fieldReqVO) {
        List<FieldDO> fieldDOS = fieldMapper.selectFieldList(fieldReqVO);
        if (CollUtil.isEmpty(fieldDOS)) {
            return null;
        }
        //获取角色没有权限的字段列表
        List<FieldPermissionDTO> fieldPermission = permissionApi.getNonPermissionFieldByRoleId(fieldReqVO.getRoleId());
        List<FieldRespVO> fieldRespVOList = BeanUtils.toBean(fieldDOS, FieldRespVO.class);
        if (CollUtil.isEmpty(fieldPermission)) {
            //角色与字段无绑定关系 说明有该字段权限
            return fieldRespVOList.stream().peek(s -> s.setFieldPermissionFlag(1)).collect(Collectors.toList());
        } else {
            List<Long> fieldPermissionList = fieldPermission.stream().map(FieldPermissionDTO::getId).toList();
            return fieldRespVOList.stream().peek(fieldRespVO -> {
                if (CollUtil.isEmpty(fieldPermissionList)) {
                    fieldRespVO.setFieldPermissionFlag(1);
                } else {
                    if (fieldPermissionList.contains(fieldRespVO.getId())) {
                        fieldRespVO.setFieldPermissionFlag(0);
                    } else {
                        fieldRespVO.setFieldPermissionFlag(1);
                    }
                }
            }).collect(Collectors.toList());
        }
    }

    @Override
    public PageResult<FieldDO> getFieldPage(FieldPageReqVO pageReqVO) {
        return fieldMapper.selectPage(pageReqVO);
    }

    @Override
    public void syncFieldData() {
        try {
            List<FieldDTO> tableFields = dataBaseTableApi.getTableFields();
            if (CollUtil.isNotEmpty(tableFields)) {
                //批量插入字段表
                fieldMapper.insertBatch(BeanUtils.toBean(tableFields, FieldDO.class));
            }
        } catch (Exception e) {
            logger.error("同步系统字段出错-{}", e.getMessage());
        }
    }

    @Override
    public List<TableInfoRespVO> getTableInfo() {
        return fieldMapper.selectTableInfo();
    }

}