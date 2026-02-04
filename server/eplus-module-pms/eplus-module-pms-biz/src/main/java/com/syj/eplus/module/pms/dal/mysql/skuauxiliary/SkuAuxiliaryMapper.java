package com.syj.eplus.module.pms.dal.mysql.skuauxiliary;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.pms.controller.admin.skuauxiliary.vo.SkuAuxiliaryPageReqVO;
import com.syj.eplus.module.pms.dal.dataobject.skuauxiliary.SkuAuxiliaryDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 产品辅料配比 Mapper
 *
 * @author zhangcm
 */
@Mapper
public interface SkuAuxiliaryMapper extends BaseMapperX<SkuAuxiliaryDO> {

    default PageResult<SkuAuxiliaryDO> selectPage(SkuAuxiliaryPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<SkuAuxiliaryDO>()
                .eqIfPresent(SkuAuxiliaryDO::getSkuCode, reqVO.getSkuCode())
                .likeIfPresent(SkuAuxiliaryDO::getSkuName, reqVO.getSkuName())
                .eqIfPresent(SkuAuxiliaryDO::getAuxiliarySkuCode, reqVO.getAuxiliarySkuCode())
                .likeIfPresent(SkuAuxiliaryDO::getAuxiliarySkuName, reqVO.getAuxiliarySkuName())
                .eqIfPresent(SkuAuxiliaryDO::getSkuRate, reqVO.getSkuRate())
                .eqIfPresent(SkuAuxiliaryDO::getAuxiliarySkuRate, reqVO.getAuxiliarySkuRate())
                .betweenIfPresent(SkuAuxiliaryDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(SkuAuxiliaryDO::getId));
    }

}