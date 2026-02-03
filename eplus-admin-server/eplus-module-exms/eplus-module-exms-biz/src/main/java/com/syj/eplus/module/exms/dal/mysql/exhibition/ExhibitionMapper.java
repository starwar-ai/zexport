package com.syj.eplus.module.exms.dal.mysql.exhibition;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.exms.controller.admin.exhibition.vo.ExhibitionPageReqVO;
import com.syj.eplus.module.exms.dal.dataobject.exhibition.ExhibitionDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 展会 Mapper
 *
 * @author zhangcm
 */
@Mapper
public interface ExhibitionMapper extends BaseMapperX<ExhibitionDO> {

    default PageResult<ExhibitionDO> selectPage(ExhibitionPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ExhibitionDO>()
                .eqIfPresent(ExhibitionDO::getCode, reqVO.getCode())
                .likeIfPresent(ExhibitionDO::getName, reqVO.getName())
                .eqIfPresent(ExhibitionDO::getExpoStatus, reqVO.getExpoStatus())
                .eqIfPresent(ExhibitionDO::getExmsEventCategoryId, reqVO.getExmsEventCategoryId())
                .eqIfPresent(ExhibitionDO::getAuditStatus, reqVO.getAuditStatus())
                .eqIfPresent(ExhibitionDO::getCompanyId, reqVO.getCompanyId())
                .likeIfPresent(ExhibitionDO::getCompanyName, reqVO.getCompanyName())
                .eqIfPresent(ExhibitionDO::getDeptList, reqVO.getDeptList())
                .betweenIfPresent(ExhibitionDO::getApplyDate, reqVO.getApplyDate())
                .eqIfPresent(ExhibitionDO::getApplyUserId, reqVO.getApplyUserId())
                .likeIfPresent(ExhibitionDO::getApplyUserName, reqVO.getApplyUserName())
                .eqIfPresent(ExhibitionDO::getApplyDeptId, reqVO.getApplyDeptId())
                .likeIfPresent(ExhibitionDO::getApplyDeptName, reqVO.getApplyDeptName())
                .eqIfPresent(ExhibitionDO::getBudget, reqVO.getBudget())
                .eqIfPresent(ExhibitionDO::getTheme, reqVO.getTheme())
                .eqIfPresent(ExhibitionDO::getStallThemeList, reqVO.getStallThemeList())
                .eqIfPresent(ExhibitionDO::getCountryId, reqVO.getCountryId())
                .likeIfPresent(ExhibitionDO::getCountryName, reqVO.getCountryName())
                .eqIfPresent(ExhibitionDO::getCityId, reqVO.getCityId())
                .likeIfPresent(ExhibitionDO::getCityName, reqVO.getCityName())
                .betweenIfPresent(ExhibitionDO::getPlanStartDate, reqVO.getPlanStartDate())
                .betweenIfPresent(ExhibitionDO::getPlanEndDate, reqVO.getPlanEndDate())
                .betweenIfPresent(ExhibitionDO::getStartDate, reqVO.getStartDate())
                .betweenIfPresent(ExhibitionDO::getEndDate, reqVO.getEndDate())
                .eqIfPresent(ExhibitionDO::getStallArea, reqVO.getStallArea())
                .eqIfPresent(ExhibitionDO::getOwnerUserId, reqVO.getOwnerUserId())
                .likeIfPresent(ExhibitionDO::getOwnerUserName, reqVO.getOwnerUserName())
                .eqIfPresent(ExhibitionDO::getOwnerDeptId, reqVO.getOwnerDeptId())
                .likeIfPresent(ExhibitionDO::getOwnerDeptName, reqVO.getOwnerDeptName())
                .eqIfPresent(ExhibitionDO::getRemark, reqVO.getRemark())
                .betweenIfPresent(ExhibitionDO::getDoneTime, reqVO.getDoneTime())
                .betweenIfPresent(ExhibitionDO::getFinishTime, reqVO.getFinishTime())
                .betweenIfPresent(ExhibitionDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ExhibitionDO::getOrderNum)
                .orderByDesc(ExhibitionDO::getId));
    }

    /**
     * 查询最大排序值
     *
     * @return 最大排序值
     */
    default Integer selectMaxOrderNum() {
        ExhibitionDO exhibitionDO = selectOne(new LambdaQueryWrapperX<ExhibitionDO>()
                .select(ExhibitionDO::getOrderNum)
                .orderByDesc(ExhibitionDO::getOrderNum)
                .last("LIMIT 1"));
        return exhibitionDO != null && exhibitionDO.getOrderNum() != null ? exhibitionDO.getOrderNum() : 0;
    }

}