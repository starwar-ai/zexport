package com.syj.eplus.module.fms.dal.mysql.receipt;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.fms.controller.admin.receipt.vo.ReceiptPageReqVO;
import com.syj.eplus.module.fms.dal.dataobject.receipt.ReceiptDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 财务收款单 Mapper
 *
 * @author ePlus
 */
@Mapper
public interface ReceiptMapper extends BaseMapperX<ReceiptDO> {

    default PageResult<ReceiptDO> selectPage(ReceiptPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ReceiptDO>()
                .eqIfPresent(ReceiptDO::getCode, reqVO.getCode())
                .eqIfPresent(ReceiptDO::getPrintFlag, reqVO.getPrintFlag())
                .eqIfPresent(ReceiptDO::getBusinessSubjectType, reqVO.getBusinessSubjectType())
                .eqIfPresent(ReceiptDO::getPrintTimes, reqVO.getPrintTimes())
                .eqIfPresent(ReceiptDO::getAuditStatus, reqVO.getAuditStatus())
                .eqIfPresent(ReceiptDO::getCompanyId, reqVO.getCompanyId())
                .eqIfPresent(ReceiptDO::getBank, reqVO.getBank())
                .eqIfPresent(ReceiptDO::getBusinessType, reqVO.getBusinessType())
                .eqIfPresent(ReceiptDO::getBusinessCode, reqVO.getBusinessCode())
                .eqIfPresent(ReceiptDO::getBankAddress, reqVO.getBankAddress())
                .eqIfPresent(ReceiptDO::getBankAccount, reqVO.getBankAccount())
                .eqIfPresent(ReceiptDO::getBankPoc, reqVO.getBankPoc())
                .eqIfPresent(ReceiptDO::getBankCode, reqVO.getBankCode())
                .eqIfPresent(ReceiptDO::getAmount, reqVO.getAmount())
                .eqIfPresent(ReceiptDO::getRate, reqVO.getRate())
                .betweenIfPresent(ReceiptDO::getReceiptTime, reqVO.getReceiptTime())
                .eqIfPresent(ReceiptDO::getRemark, reqVO.getRemark())
                .eqIfPresent(ReceiptDO::getApprover, reqVO.getApprover())
                .betweenIfPresent(ReceiptDO::getApprovalTime, reqVO.getApprovalTime())
                .betweenIfPresent(ReceiptDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ReceiptDO::getId));
    }

}