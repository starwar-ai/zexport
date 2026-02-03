package cn.iocoder.yudao.module.system.dal.mysql.user;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.system.controller.admin.user.vo.user.UserPageReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Mapper
public interface AdminUserMapper extends BaseMapperX<AdminUserDO> {

    default AdminUserDO selectByUsername(String username) {
        return selectOne(AdminUserDO::getUsername, username);
    }

    default AdminUserDO selectByUserCode(String code) {
        return selectOne(AdminUserDO::getCode, code);
    }

    default AdminUserDO selectByEmail(String email) {
        return selectOne(AdminUserDO::getEmail, email);
    }

    default AdminUserDO selectByMobile(String mobile) {
        return selectOne(AdminUserDO::getMobile, mobile);
    }

    default List<AdminUserDO> selectByBankAccount(String bankAccount) {
        return selectList(new LambdaQueryWrapperX<AdminUserDO>().eq(AdminUserDO::getBankAccount, bankAccount));
    }

    default PageResult<AdminUserDO> selectPage(UserPageReqVO reqVO, Collection<Long> deptIds,List<Long> userIdList) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AdminUserDO>()
                .likeIfPresent(AdminUserDO::getUsername, reqVO.getUsername())
                .likeIfPresent(AdminUserDO::getMobile, reqVO.getMobile())
                .likeIfPresent(AdminUserDO::getNickname, reqVO.getNickname())
                .eqIfPresent(AdminUserDO::getStatus, reqVO.getStatus())
                .likeIfPresent(AdminUserDO::getCode, reqVO.getCode())
                .betweenIfPresent(AdminUserDO::getCreateTime, reqVO.getCreateTime())
                .inIfPresent(AdminUserDO::getDeptId, deptIds)
                .inIfPresent(AdminUserDO::getId, userIdList)
                .orderByDesc(AdminUserDO::getId));
    }

    default List<AdminUserDO> selectListByNickname(String nickname) {
        return selectList(new LambdaQueryWrapperX<AdminUserDO>().like(AdminUserDO::getNickname, nickname));
    }

    default List<AdminUserDO> selectListByStatus(Integer status, Set<Long> superUserIds) {
        return selectList(new LambdaQueryWrapperX<AdminUserDO>()
                .eq(AdminUserDO::getStatus, status)
                .notIn(AdminUserDO::getId, superUserIds));
    }
    default List<AdminUserDO> selectAllUser(Set<Long> superUserIds) {
        return selectList(new LambdaQueryWrapperX<AdminUserDO>()
                .notIn(AdminUserDO::getId, superUserIds));
    }
    default List<AdminUserDO> selectListByDeptIds(Collection<Long> deptIds) {
        return selectList(AdminUserDO::getDeptId, deptIds);
    }

    default List<AdminUserDO> selectAllAdminUser() {
        return selectList(new LambdaQueryWrapper<AdminUserDO>().select(AdminUserDO::getId, AdminUserDO::getNickname, AdminUserDO::getDeptId));
    }

    @Select("<script>" +
            "select id from system_users where dept_id in \n" +
            "<foreach collection='deptIds' index='index' item='deptId' open='(' separator=',' close=')'>#{deptId}</foreach>" +
            "</script>")
    List<Long> selectUserIdByDeptIdList(@Param("deptIds") Set<Long> deptIdList);


    @Update("update system_users set bank = #{bank},bank_address = #{bankAddress},bank_poc = #{bankPoc},bank_code = #{bank_code} where mobile = #{mobile}")
    void updateUserBankByMobile(@Param("bank") String bank,
                                @Param("bankAccount") String bankAccount,
                                @Param("bankAddress") String bankAddress,
                                @Param("bankPoc") String bankPoc,
                                @Param("bankCode") String bankCode,
                                @Param("mobile") String mobile);

}
