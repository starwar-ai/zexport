package com.syj.eplus.module.infra.dal.mysql.companypath;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.infra.controller.admin.companypath.vo.CompanyPathPageReqVO;
import com.syj.eplus.module.infra.dal.dataobject.companypath.CompanyPathDO;
import com.syj.eplus.framework.common.enums.BooleanEnum;
import org.apache.ibatis.annotations.Mapper;

import java.util.Objects;

/**
 * 抛砖路径 Mapper
 *
 * @author du
 */
@Mapper
public interface CompanyPathMapper extends BaseMapperX<CompanyPathDO> {

    default PageResult<CompanyPathDO> selectPage(CompanyPathPageReqVO reqVO) {
        LambdaQueryWrapperX<CompanyPathDO> queryWrapperX = new LambdaQueryWrapperX<CompanyPathDO>()
                .eqIfPresent(CompanyPathDO::getPath, reqVO.getPath())
                .eqIfPresent(CompanyPathDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(CompanyPathDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(CompanyPathDO::getId);
        if (BooleanEnum.YES.getValue().equals(reqVO.getInternalCustFlag())&& Objects.nonNull(reqVO.getCompanyId())){
            queryWrapperX.apply("JSON_UNQUOTE(JSON_EXTRACT(path, '$.next')) = 'null' and JSON_EXTRACT(path, '$.id') != {0}",reqVO.getCompanyId());
        }
        return selectPage(reqVO,queryWrapperX);
    }

}