package com.syj.eplus.module.oa.dal.mysql.feeshareitem;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.oa.controller.admin.feeshareitem.vo.FeeShareItemPageReqVO;
import com.syj.eplus.module.oa.dal.dataobject.feeshareitem.FeeShareItemDO;
import org.apache.ibatis.annotations.Mapper;
/**
 * 费用归集明细 Mapper
 *
 * @author zhangcm
 */
@Mapper
public interface FeeShareItemMapper extends BaseMapperX<FeeShareItemDO> {

    default PageResult<FeeShareItemDO> selectPage(FeeShareItemPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<FeeShareItemDO>()
                .eqIfPresent(FeeShareItemDO::getShareFeeId, reqVO.getShareFeeId())
                .eqIfPresent(FeeShareItemDO::getBusinessSubjectType, reqVO.getBusinessSubjectType())
                .eqIfPresent(FeeShareItemDO::getBusinessSubjectCode, reqVO.getBusinessSubjectCode())
                .betweenIfPresent(FeeShareItemDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(FeeShareItemDO::getId));
    }

}