package cn.iocoder.yudao.module.system.service.rolefield;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.system.controller.admin.rolefield.vo.RoleFieldPageReqVO;
import cn.iocoder.yudao.module.system.controller.admin.rolefield.vo.RoleFieldSaveReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.field.FieldDO;
import cn.iocoder.yudao.module.system.dal.dataobject.rolefield.RoleFieldDO;

import javax.validation.Valid;
import java.util.List;

/**
 * 字段角色关联 Service 接口
 *
 * @author ePlus
 */
public interface RoleFieldService {

    /**
     * 创建字段角色关联
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createRoleField(@Valid RoleFieldSaveReqVO createReqVO);

    /**
     * 更新字段角色关联
     *
     * @param updateReqVO 更新信息
     */
    void updateRoleField(@Valid RoleFieldSaveReqVO updateReqVO);

    /**
     * 删除字段角色关联
     *
     * @param id 编号
     */
    void deleteRoleField(Long id);

    /**
     * 获得字段角色关联
     *
     * @param id 编号
     * @return 字段角色关联
     */
    RoleFieldDO getRoleField(Long id);

    /**
     * 获得字段角色关联分页
     *
     * @param pageReqVO 分页查询
     * @return 字段角色关联分页
     */
    PageResult<RoleFieldDO> getRoleFieldPage(RoleFieldPageReqVO pageReqVO);

    /**
     * 根据角色id集合获取无权限字段集
     *
     * @param roleIds
     * @return
     */
    List<FieldDO> getRoleFieldListByRoleIds(List<Long> roleIds);
}