package com.syj.eplus.module.oa.dal.mysql.apply;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.framework.common.enums.TravelTypeEnum;
import com.syj.eplus.module.oa.controller.admin.apply.vo.ApplyPageReqVO;
import com.syj.eplus.module.oa.dal.dataobject.apply.ApplyDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Objects;

/**
 * OA申请单 Mapper
 *
 * @author ePlus
 */
@Mapper
public interface ApplyMapper extends BaseMapperX<ApplyDO> {

    default PageResult<ApplyDO> selectPage(ApplyPageReqVO reqVO) {
        LambdaQueryWrapperX<ApplyDO> applyDOLambdaQueryWrapperX = new LambdaQueryWrapperX<ApplyDO>()
                .eqIfPresent(ApplyDO::getIntendedObjectives, reqVO.getIntendedObjectives())
                .eqIfPresent(ApplyDO::getCode, reqVO.getCode())
                .eqIfPresent(ApplyDO::getApplyType, reqVO.getApplyType())
                .eqIfPresent(ApplyDO::getWecomId, reqVO.getWecomId())
                .betweenIfPresent(ApplyDO::getApplyTime, reqVO.getApplyTime())
                .likeIfPresent(ApplyDO::getPurpose, reqVO.getPurpose())
                .eqIfPresent(ApplyDO::getDest, reqVO.getDest())
                .betweenIfPresent(ApplyDO::getStartTime, reqVO.getStartTime())
                .betweenIfPresent(ApplyDO::getEndTime, reqVO.getEndTime())
                .eqIfPresent(ApplyDO::getDuration, reqVO.getDuration())
                .eqIfPresent(ApplyDO::getTransportationType, reqVO.getTransportationType())
                .eqIfPresent(ApplyDO::getEntertainEntourage, reqVO.getEntertainEntourage())
                .eqIfPresent(ApplyDO::getEntertainLevel, reqVO.getEntertainLevel())
                .eqIfPresent(ApplyDO::getEntertainNum, reqVO.getEntertainNum())
                .betweenIfPresent(ApplyDO::getEntertainTime, reqVO.getEntertainTime())
                .likeIfPresent(ApplyDO::getEntertainName, reqVO.getEntertainName())
                .eqIfPresent(ApplyDO::getEntertainType, reqVO.getEntertainType())
                .eqIfPresent(ApplyDO::getGeneralExpense, reqVO.getGeneralExpense())
                .eqIfPresent(ApplyDO::getRemarks, reqVO.getRemarks())
                .eqIfPresent(ApplyDO::getAuditStatus, reqVO.getAuditStatus())
                .eqIfPresent(ApplyDO::getAnnex, reqVO.getAnnex())
                .betweenIfPresent(ApplyDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ApplyDO::getCompanyId, reqVO.getCompanyId());
        if (Objects.nonNull(reqVO.getIsApplyExpense())){
            if (reqVO.getIsApplyExpense()==1){
                applyDOLambdaQueryWrapperX.eq(ApplyDO::getIsApplyExpense, reqVO.getIsApplyExpense());
            }else {
                applyDOLambdaQueryWrapperX.and(queryWrapper ->
                        queryWrapper.eq(ApplyDO::getIsApplyExpense, reqVO.getIsApplyExpense())
                                .or()
                                .eq(ApplyDO::getTravelType, TravelTypeEnum.INTERNATIONAL_BUSINESS_TRIP.getType()));
            }
        }
        if(Objects.nonNull(reqVO.getApplierId())){
            applyDOLambdaQueryWrapperX.apply("JSON_EXTRACT(applyer,'$.userId') = {0}", reqVO.getApplierId());
        }
        if(Objects.nonNull(reqVO.getApplierDeptId())){
            applyDOLambdaQueryWrapperX.apply("JSON_EXTRACT(applyer,'$.deptId') = {0}", reqVO.getApplierDeptId());
        }
        if (StrUtil.isNotEmpty(reqVO.getApplierDeptName())) {
            applyDOLambdaQueryWrapperX.apply("JSON_EXTRACT(applyer,'$.deptName') like CONCAT('%',{0},'%')", reqVO.getApplierDeptName());
        }
        return selectPage(reqVO,applyDOLambdaQueryWrapperX.orderByDesc(ApplyDO::getId));


    }

}