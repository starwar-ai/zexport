package cn.iocoder.yudao.module.system.convert.userprefence;

import cn.iocoder.yudao.module.system.controller.admin.userprefence.vo.UserPrefenceRespVO;
import cn.iocoder.yudao.module.system.controller.admin.userprefence.vo.UserPrefenceSaveReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.userprefence.UserPrefenceDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper
public interface UserPrefenceConvert {

        UserPrefenceConvert INSTANCE = Mappers.getMapper(UserPrefenceConvert.class);

        UserPrefenceRespVO convert(UserPrefenceDO userPrefenceDO);

        default UserPrefenceRespVO convertUserPrefenceRespVO(UserPrefenceDO userPrefenceDO){
                UserPrefenceRespVO userPrefenceRespVO = convert(userPrefenceDO);
                return userPrefenceRespVO;
        }

    UserPrefenceDO convertUserPrefenceDO(UserPrefenceSaveReqVO saveReqVO);
}