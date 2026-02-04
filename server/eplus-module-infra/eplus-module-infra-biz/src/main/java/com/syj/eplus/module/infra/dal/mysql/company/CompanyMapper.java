package com.syj.eplus.module.infra.dal.mysql.company;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.infra.controller.admin.company.vo.CompanyPageReqVO;
import com.syj.eplus.module.infra.dal.dataobject.company.CompanyDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 内部法人单位 Mapper
 *
 * @author du
 */
@Mapper
public interface CompanyMapper extends BaseMapperX<CompanyDO> {

    default PageResult<CompanyDO> selectPage(CompanyPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<CompanyDO>()
                .likeIfPresent(CompanyDO::getName, reqVO.getName())
                .inIfPresent(CompanyDO::getId, reqVO.getId())
                .likeIfPresent(CompanyDO::getCompanyName, reqVO.getCompanyName())
                .eqIfPresent(CompanyDO::getCompanyNature, reqVO.getCompanyNature())
                .betweenIfPresent(CompanyDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(CompanyDO::getId));
    }

    default List<CompanyDO> selectListByIdList(List<Long> idList) {
        return selectList(CompanyDO::getId, idList);
    }

}
