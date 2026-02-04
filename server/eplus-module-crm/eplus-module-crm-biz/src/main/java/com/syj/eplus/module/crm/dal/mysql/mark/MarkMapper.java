package com.syj.eplus.module.crm.dal.mysql.mark;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.crm.controller.admin.mark.vo.MarkPageReqVO;
import com.syj.eplus.module.crm.dal.dataobject.mark.MarkDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 唛头 Mapper
 *
 * @author du
 */
@Mapper
public interface MarkMapper extends BaseMapperX<MarkDO> {

    default PageResult<MarkDO> selectPage(MarkPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MarkDO>()
                .likeIfPresent(MarkDO::getName, reqVO.getName())
                .eqIfPresent(MarkDO::getVer, reqVO.getVer())
                .likeIfPresent(MarkDO::getEngName, reqVO.getEngName())
                .eqIfPresent(MarkDO::getMainMarkText, reqVO.getMainMarkText())
                .eqIfPresent(MarkDO::getMainMarkPic, reqVO.getMainMarkPic())
                .eqIfPresent(MarkDO::getMainMarkTextSide, reqVO.getMainMarkTextSide())
                .eqIfPresent(MarkDO::getMainMarkPicSide, reqVO.getMainMarkPicSide())
                .eqIfPresent(MarkDO::getMainMarkTextIn, reqVO.getMainMarkTextIn())
                .eqIfPresent(MarkDO::getMainMarkPicIn, reqVO.getMainMarkPicIn())
                .eqIfPresent(MarkDO::getMainMarkTextSideIn, reqVO.getMainMarkTextSideIn())
                .eqIfPresent(MarkDO::getMainMarkPicSideIn, reqVO.getMainMarkPicSideIn())
                .betweenIfPresent(MarkDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(MarkDO::getId));
    }

}