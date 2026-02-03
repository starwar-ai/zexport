package cn.iocoder.yudao.module.system.convert.user;

import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.framework.common.util.collection.MapUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.system.api.user.dto.UserBackAccountDTO;
import cn.iocoder.yudao.module.system.controller.admin.auth.vo.UserMobileVO;
import cn.iocoder.yudao.module.system.controller.admin.dept.vo.dept.DeptSimpleRespVO;
import cn.iocoder.yudao.module.system.controller.admin.dept.vo.post.PostSimpleRespVO;
import cn.iocoder.yudao.module.system.controller.admin.permission.vo.role.RoleSimpleRespVO;
import cn.iocoder.yudao.module.system.controller.admin.user.vo.profile.UserProfileRespVO;
import cn.iocoder.yudao.module.system.controller.admin.user.vo.user.UserBankImportExcelVO;
import cn.iocoder.yudao.module.system.controller.admin.user.vo.user.UserRespVO;
import cn.iocoder.yudao.module.system.dal.dataobject.dept.DeptDO;
import cn.iocoder.yudao.module.system.dal.dataobject.dept.PostDO;
import cn.iocoder.yudao.module.system.dal.dataobject.permission.RoleDO;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import com.syj.eplus.framework.common.entity.UserDept;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserConvert {

    UserConvert INSTANCE = Mappers.getMapper(UserConvert.class);

    default List<UserRespVO> convertList(List<AdminUserDO> list, Map<Long, DeptDO> deptMap) {
        return CollectionUtils.convertList(list, user -> convert(user, deptMap.get(user.getDeptId())));
    }

    default UserRespVO convert(AdminUserDO user, DeptDO dept) {
        UserRespVO userVO = BeanUtils.toBean(user, UserRespVO.class);
        if (dept != null) {
            userVO.setDeptName(dept.getName());
        }
        return userVO;
    }

    default List<UserDept> convertSimpleList(List<AdminUserDO> list, Map<Long, DeptDO> deptMap) {
        return CollectionUtils.convertList(list, user -> {
            UserDept userVO = BeanUtils.toBean(user, UserDept.class);
            userVO.setUserId(user.getId());
            userVO.setUserCode(user.getCode());
            MapUtils.findAndThen(deptMap, user.getDeptId(), dept -> userVO.setDeptName(dept.getName()));
            MapUtils.findAndThen(deptMap, user.getDeptId(), dept -> userVO.setDeptCode(dept.getCode()));
            return userVO;
        });
    }

    default UserProfileRespVO convert(AdminUserDO user, List<RoleDO> userRoles,
                                      DeptDO dept, List<PostDO> posts) {
        UserProfileRespVO userVO = BeanUtils.toBean(user, UserProfileRespVO.class);
        userVO.setRoles(BeanUtils.toBean(userRoles, RoleSimpleRespVO.class));
        userVO.setDept(BeanUtils.toBean(dept, DeptSimpleRespVO.class));
        userVO.setPosts(BeanUtils.toBean(posts, PostSimpleRespVO.class));
        return userVO;
    }

    default UserBankImportExcelVO convertToUserBankImportExcelVO(AdminUserDO adminUserDO, DeptDO dept) {
        UserBankImportExcelVO userBankImportExcelVO = BeanUtils.toBean(adminUserDO, UserBankImportExcelVO.class);
        if (dept != null) {
            userBankImportExcelVO.setDeptName(dept.getName());
        }
        return userBankImportExcelVO;
    }

    default List<UserBankImportExcelVO> convertToUserBankImportExcelVOList(List<AdminUserDO> list, Map<Long, DeptDO> deptMap) {
        return CollectionUtils.convertList(list, user -> convertToUserBankImportExcelVO(user, deptMap.get(user.getDeptId())));
    }

    default List<UserDept> convertToUserDept(List<AdminUserDO> list, Map<Long, DeptDO> deptMap) {
        return CollectionUtils.convertList(list, user -> {
            UserDept userDept = BeanUtils.toBean(user, UserDept.class);
            userDept.setUserCode(user.getCode());
            userDept.setUserId(user.getId());
            MapUtils.findAndThen(deptMap, user.getDeptId(), dept -> userDept.setDeptName(dept.getName()));
            return userDept;
        });
    }

    default List<UserBackAccountDTO> convertToUserBankAccountDTO(List<AdminUserDO> list) {
        return CollectionUtils.convertList(list, user -> {
            UserBackAccountDTO userDTO = BeanUtils.toBean(user, UserBackAccountDTO.class);
            return userDTO;
        });
    }

    default List<UserMobileVO> convertToUserMobileVO(List<AdminUserDO> list, Map<Long, DeptDO> deptMap) {
        return CollectionUtils.convertList(list, user -> {
            UserMobileVO userMobileVO = BeanUtils.toBean(user, UserMobileVO.class);
            userMobileVO.setUserId(user.getId());
            MapUtils.findAndThen(deptMap, user.getDeptId(), dept -> userMobileVO.setDeptName(dept.getName()));
            return userMobileVO;
        });
    }
}
