package com.syj.eplus.module.crm.dal.mysql.custpoc;


import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.crm.controller.admin.custpoc.vo.CustPocPageReqVO;
import com.syj.eplus.module.crm.dal.dataobject.custpoc.CustPocDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 客户联系人 Mapper
 *
 * @author du
 */
@Mapper
public interface CustPocMapper extends BaseMapperX<CustPocDO> {

    default PageResult<CustPocDO> selectPage(CustPocPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<CustPocDO>()
                .eqIfPresent(CustPocDO::getVer, reqVO.getVer())
                .eqIfPresent(CustPocDO::getCustId, reqVO.getCustId())
                .likeIfPresent(CustPocDO::getName, reqVO.getName())
                .eqIfPresent(CustPocDO::getPocPosts, reqVO.getPocPosts())
                .eqIfPresent(CustPocDO::getEmail, reqVO.getEmail())
                .eqIfPresent(CustPocDO::getMobile, reqVO.getMobile())
                .eqIfPresent(CustPocDO::getAddress, reqVO.getAddress())
                .eqIfPresent(CustPocDO::getDefaultFlag, reqVO.getDefaultFlag())
                .betweenIfPresent(CustPocDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(CustPocDO::getId));
    }

}