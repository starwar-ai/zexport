package cn.iocoder.yudao.module.system.api.user;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.RandomUtil;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import cn.iocoder.yudao.module.system.api.user.dto.UserBackAccountDTO;
import cn.iocoder.yudao.module.system.dal.dataobject.dept.DeptDO;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import cn.iocoder.yudao.module.system.service.dept.DeptService;
import cn.iocoder.yudao.module.system.service.user.AdminUserService;
import com.syj.eplus.framework.common.entity.UserDept;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;

/**
 * Admin 用户 API 实现类
 *
 * @author 芋道源码
 */
@Service
public class AdminUserApiImpl implements AdminUserApi {

    @Resource
    private AdminUserService userService;
    @Resource
    private DeptService deptService;

    @Override
    @DataPermission(enable = false)
    public AdminUserRespDTO getUser(Long id) {
        if(id == null){
            return null;
        }

        AdminUserDO user = userService.getUser(id);
        return BeanUtils.toBean(user, AdminUserRespDTO.class);
    }

    @Override
    public Set<Long> getSubordinateIds(Long id) {
        AdminUserDO user = userService.getUser(id);
        if (user == null) {
            return null;
        }

        Set<Long> subordinateIds = null; // 下属用户编号
        DeptDO dept = deptService.getDept(user.getDeptId());
        // TODO @puhui999：需要递归查询到子部门；并且要排除到自己噢。
        // TODO @puhui999：保持 if return 原则，这里其实要判断不等于，则返回 null；最好返回 空集合，上面也是
        if (dept.getLeaderUserIds().contains(id)) { // 校验是否是该部门的负责人
            List<AdminUserDO> users = userService.getUserListByDeptIds(Collections.singletonList(dept.getId()));
            subordinateIds = convertSet(users, AdminUserDO::getId);
        }
        return subordinateIds;
    }

    @Override
    public List<AdminUserRespDTO> getUserList(Collection<Long> ids) {
        List<AdminUserDO> users = userService.getUserList(ids);
        return BeanUtils.toBean(users, AdminUserRespDTO.class);
    }

    @Override
    public List<AdminUserRespDTO> getUserListByDeptIds(Collection<Long> deptIds) {
        List<AdminUserDO> users = userService.getUserListByDeptIds(deptIds);
        return BeanUtils.toBean(users, AdminUserRespDTO.class);
    }

    @Override
    public List<AdminUserRespDTO> getUserListByPostIds(Collection<Long> postIds) {
        List<AdminUserDO> users = userService.getUserListByPostIds(postIds);
        return BeanUtils.toBean(users, AdminUserRespDTO.class);
    }

    @Override
    public void validateUserList(Collection<Long> ids) {
        userService.validateUserList(ids);
    }

    @Override
    public Map<Long, String> getUserIdNameCache() {
        return userService.getUserIdNameCache();
    }

    @Override
    public Map<Long, UserDept> getUserDeptCache(Long id) {
        return userService.getUserDeptCache(id);
    }

    @Override
    public Map<Long, UserDept> getUserDeptListCache(List<Long> idList) {
        return userService.getUserDeptListCache(idList);
    }

    @Override
    public UserDept getUserDeptByUserId(Long id) {
        return userService.getUserDeptByUserId(id);
    }

    @Override
    public Map<Long, UserBackAccountDTO> getUserBackAccountDTOMap(Long userId) {
        return userService.getUserBackAccountDTOMap(userId);
    }

    @Override
    public Long getNextDeptLeaderIdByUserId(Long userId) {
        UserDept userDept = userService.getUserDeptByUserId(userId);
        Assert.notNull(userDept, "用户({})不存在", userId);
        Long deptId = userDept.getDeptId();
        Assert.notNull(deptId, "用户({})部门为空", userId);
        Map<Long, DeptDO> deptMap = deptService.getDeptMap(List.of());
        Set<Long> leaderSet = deptService.getLeaderIdByDeptId(deptId, deptMap);
        // 如果当前申请人是部门负责人则转交上一领导审批
        if (leaderSet.contains(userId)) {
            DeptDO deptDO = deptMap.get(deptId);
            Long parentId = deptDO.getParentId();
            // 如果当前部门是根节点则返回负责人
            if (!Objects.equals(parentId, 0L)){
                leaderSet = deptService.getLeaderIdByDeptId(parentId, deptMap);
            }
        }
        Assert.notEmpty(leaderSet, "部门({})负责人为空", deptId);
        int index = RandomUtil.randomInt(leaderSet.size());
        return CollUtil.get(leaderSet, index);

    }

    @Override
    @DataPermission(enable = false)
    public Map<String, AdminUserRespDTO> getUserMapByStringIdList(List<String> userIdList) {
         List<Long> ids = new ArrayList<>();
        userIdList.forEach(s->{
            try{
                ids.add(Long.parseLong(s));
            }catch (Exception exception){

            }
        });
         if(CollUtil.isEmpty(ids)){
             return null;
         }
        Map<Long, AdminUserDO> userMap = userService.getUserMap(ids);
        Map<String, AdminUserRespDTO> result = new HashMap<>();
        userMap.forEach((k,v)->{
            result.put(k.toString(),BeanUtils.toBean(v,AdminUserRespDTO.class));
        });
        return result;
    }

    @DataPermission(enable = false)
    public Map<Long, AdminUserRespDTO> getUserMapByIdList(List<Long> userIdList) {
        Map<Long, AdminUserDO> userMap = userService.getUserMap(userIdList);
        Map<Long, AdminUserRespDTO> result = new HashMap<>();
        userMap.forEach((k,v)->{
            result.put(k,BeanUtils.toBean(v,AdminUserRespDTO.class));
        });
        return result;
    }

    @Override
    public Map<String, AdminUserRespDTO> getUserListByUserCodeList(List<String> codeList) {
        List<AdminUserDO> userDOList = userService.getUserListByUserCodeList(codeList);
        List<AdminUserRespDTO> dtoList = BeanUtils.toBean(userDOList, AdminUserRespDTO.class);
        return dtoList.stream().collect(Collectors.toMap(AdminUserRespDTO::getCode,s->s,(s1,s2)->s1));
    }

    @Override
    public Set<Long> getSuperiorLeader(Set<Long> userIds) {
        return userService.getSuperiorLeader(userIds);
    }

    @Override
    public UserDept getUserDeptByUserCode(String userCode) {
        return  userService.getUserDeptByUserCode(userCode);
    }

    @Override
    public UserDept getUserDeptByUserName(String name) {
        return userService.getUserDeptByUserName(name);
    }

    @Override
    public Map<String, UserDept> getUserDeptToUserName() {
        return  userService.getUserDeptToUserName();
    }


}
