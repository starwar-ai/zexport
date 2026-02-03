package com.syj.eplus.module.scm.dal.mysql.venderpoc;


import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.scm.controller.admin.venderpoc.vo.VenderPocPageReqVO;
import com.syj.eplus.module.scm.dal.dataobject.venderpoc.VenderPocDO;
import org.apache.ibatis.annotations.Mapper;


/**
 * 供应商联系人 Mapper
 *
 * @author zhangcm
 */
@Mapper
public interface VenderPocMapper extends BaseMapperX<VenderPocDO> {

    default PageResult<VenderPocDO> selectPage(VenderPocPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<VenderPocDO>()
                .eqIfPresent(VenderPocDO::getVer, reqVO.getVer())
                .eqIfPresent(VenderPocDO::getVenderId, reqVO.getVenderId())
                .eqIfPresent(VenderPocDO::getVenderVer, reqVO.getVenderVer())
                .likeIfPresent(VenderPocDO::getName, reqVO.getName())
                .likeIfPresent(VenderPocDO::getPocTypes, reqVO.getPocTypes())
                .likeIfPresent(VenderPocDO::getEmail, reqVO.getEmail())
                .likeIfPresent(VenderPocDO::getMobile, reqVO.getMobile())
                .likeIfPresent(VenderPocDO::getAddress, reqVO.getAddress())
                .eqIfPresent(VenderPocDO::getDefaultFlag, reqVO.getDefaultFlag())
                .betweenIfPresent(VenderPocDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(VenderPocDO::getId));
    }

}