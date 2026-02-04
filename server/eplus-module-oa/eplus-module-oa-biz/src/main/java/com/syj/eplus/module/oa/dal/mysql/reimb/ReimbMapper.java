package com.syj.eplus.module.oa.dal.mysql.reimb;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.system.api.permission.dto.DeptDataPermissionRespDTO;
import com.syj.eplus.framework.common.enums.ReimbTypeEnum;
import com.syj.eplus.module.oa.controller.admin.reimb.vo.ReimbPageReqVO;
import com.syj.eplus.module.oa.dal.dataobject.reimb.ReimbDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.Objects;

/**
 * 出差报销 Mapper
 *
 * @author ePlus
 */
@Mapper
public interface ReimbMapper extends BaseMapperX<ReimbDO> {

    default PageResult<ReimbDO> selectPage(ReimbPageReqVO reqVO) {
        LambdaQueryWrapperX<ReimbDO> queryWrapperX = new LambdaQueryWrapperX<ReimbDO>()
                .likeIfPresent(ReimbDO::getCode, reqVO.getCode())
                .eqIfPresent(ReimbDO::getPrintFlag, reqVO.getPrintFlag())
                .eqIfPresent(ReimbDO::getPrintTimes, reqVO.getPrintTimes())
                .eqIfPresent(ReimbDO::getAuditStatus, reqVO.getAuditStatus())
                .eqIfPresent(ReimbDO::getRepayFlag, reqVO.getRepayFlag())
                .eqIfPresent(ReimbDO::getDescription, reqVO.getDescription())
                .eqIfPresent(ReimbDO::getCompanyId, reqVO.getCompanyId())
                .betweenIfPresent(ReimbDO::getCreateTime, reqVO.getCreateTime())
                .eq(ReimbDO::getReimbType, reqVO.getReimbType())
                .orderByDesc(ReimbDO::getId);
        DeptDataPermissionRespDTO deptDataPermission = reqVO.getDeptDataPermission();
        if (Objects.nonNull(deptDataPermission)&&!deptDataPermission.getAll()&&ReimbTypeEnum.OTHER.getValue().equals(reqVO.getReimbType())){
            queryWrapperX.apply("JSON_EXTRACT(approve_user,'$.userId') = {0}", WebFrameworkUtils.getLoginUserId());
        }else {
            if (Objects.nonNull(reqVO.getReimbUserId())){
                queryWrapperX.apply("JSON_EXTRACT(reimb_user,'$.userId') = {0}", reqVO.getReimbUserId());
            }
        }
        if (Objects.nonNull(reqVO.getReimbUserDeptId())){
            queryWrapperX.apply("JSON_EXTRACT(reimb_user,'$.deptId') = {0}", reqVO.getReimbUserDeptId());
        }
        if (Objects.nonNull(reqVO.getTotalAmountMin()) && Objects.nonNull(reqVO.getTotalAmountMax())) {
            queryWrapperX.apply("JSON_EXTRACT(total_amount_list, '$[0].amount') >= {0} AND JSON_EXTRACT(total_amount_list, '$[0].amount') <= {1}", reqVO.getTotalAmountMin(), reqVO.getTotalAmountMax());
        } else if (Objects.nonNull(reqVO.getTotalAmountMin())) {
            queryWrapperX.apply("JSON_EXTRACT(total_amount_list, '$[0].amount') >= {0}", reqVO.getTotalAmountMin());
        } else if (Objects.nonNull(reqVO.getTotalAmountMax())) {
            queryWrapperX.apply("JSON_EXTRACT(total_amount_list, '$[0].amount') <= {0}", reqVO.getTotalAmountMax());
        }
        if (Objects.nonNull(reqVO.getCurrency())) {
            queryWrapperX.apply("JSON_EXTRACT(total_amount_list, '$[0].currency') = {0}", reqVO.getCurrency());
        }
        return selectPage(reqVO,queryWrapperX);
    }

    @Update("update oa_reimb set print_times = print_times +1 where id = #{id}")
    void updatePrintTimes(@Param("id") Long id);

    @Update("update home_invoice_holder set status = #{status} where id = #{id}")
    void updateHomeInvoiceHolderStatus(@Param("id") Long id,@Param("status")Integer status);
}