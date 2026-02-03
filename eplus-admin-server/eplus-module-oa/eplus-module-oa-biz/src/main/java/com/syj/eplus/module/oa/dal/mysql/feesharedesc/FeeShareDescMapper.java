package com.syj.eplus.module.oa.dal.mysql.feesharedesc;


import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.oa.controller.admin.feesharedesc.vo.FeeShareDescPageReqVO;
import com.syj.eplus.module.oa.dal.dataobject.feesharedesc.FeeShareDescDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 费用归集具体名称 Mapper
 *
 * @author zhangcm
 */
@Mapper
public interface FeeShareDescMapper extends BaseMapperX<FeeShareDescDO> {

    default PageResult<FeeShareDescDO> selectPage(FeeShareDescPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<FeeShareDescDO>()
                .eqIfPresent(FeeShareDescDO::getFeeShareType, reqVO.getFeeShareType())
                .eqIfPresent(FeeShareDescDO::getRelationType, reqVO.getRelationType())
                .orderByAsc(FeeShareDescDO::getId));
    }

}