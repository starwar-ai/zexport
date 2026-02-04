package com.syj.eplus.module.oa.dal.mysql.loanapp;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.oa.controller.admin.loanapp.vo.LoanAppPageReqVO;
import com.syj.eplus.module.oa.dal.dataobject.loanapp.LoanAppDO;
import com.syj.eplus.module.oa.dal.dataobject.loanapp.LoanappSumDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * 借款申请单 Mapper
 *
 * @author ePlus
 */
@Mapper
public interface LoanAppMapper extends BaseMapperX<LoanAppDO> {

    default PageResult<LoanAppDO> selectPage(LoanAppPageReqVO reqVO) {
        LambdaQueryWrapperX<LoanAppDO> loanAppDOLambdaQueryWrapperX = new LambdaQueryWrapperX<LoanAppDO>()
                .eqIfPresent(LoanAppDO::getPurpose, reqVO.getPurpose())
                .likeIfPresent(LoanAppDO::getCode, reqVO.getCode())
                .eqIfPresent(LoanAppDO::getLoanType, reqVO.getLoanType())
                .eqIfPresent(LoanAppDO::getVenderId, reqVO.getVenderId())
                .eqIfPresent(LoanAppDO::getVenderCode, reqVO.getVenderCode())
                .likeIfPresent(LoanAppDO::getVenderName, reqVO.getVenderName())
                .eqIfPresent(LoanAppDO::getLoanSource, reqVO.getLoanSource())
                .eqIfPresent(LoanAppDO::getLoanStatus, reqVO.getLoanStatus())
                .eqIfPresent(LoanAppDO::getRepayStatus, reqVO.getRepayStatus())
                .eqIfPresent(LoanAppDO::getAmount, reqVO.getAmount())
                .eqIfPresent(LoanAppDO::getPaymentStatus, reqVO.getPaymentStatus())
                .eqIfPresent(LoanAppDO::getAuditStatus, reqVO.getAuditStatus())
                .betweenIfPresent(LoanAppDO::getLoanDate, reqVO.getLoanDate())
                .eqIfPresent(LoanAppDO::getPaymentAmount, reqVO.getPaymentAmount())
                .eqIfPresent(LoanAppDO::getRepayAmount, reqVO.getRepayAmount())
                .betweenIfPresent(LoanAppDO::getRepayDate, reqVO.getRepayDate())
                .betweenIfPresent(LoanAppDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(LoanAppDO::getId);
        // 处理审批人过滤
        Long applyerId = reqVO.getApplyerId();
        if (Objects.nonNull(applyerId)) {
            loanAppDOLambdaQueryWrapperX.apply("JSON_EXTRACT(applyer, '$.userId') = {0}", applyerId);
        }
        Long loanDeptId = reqVO.getLoanDeptId();
        if (Objects.nonNull(loanDeptId)) {
            loanAppDOLambdaQueryWrapperX.apply("JSON_EXTRACT(applyer, '$.deptId') = {0}", loanDeptId);
        }
        return selectPage(reqVO, loanAppDOLambdaQueryWrapperX);
    }

    default PageResult<LoanAppDO> selectsimplePage(LoanAppPageReqVO reqVO) {
        LambdaQueryWrapperX<LoanAppDO> loanAppDOLambdaQueryWrapperX = new LambdaQueryWrapperX<LoanAppDO>()
                .betweenIfPresent(LoanAppDO::getCreateTime, reqVO.getCreateTime())
                .likeIfPresent(LoanAppDO::getCode, reqVO.getCode())
                .betweenIfPresent(LoanAppDO::getLoanDate, reqVO.getLoanDate())
                .eqIfPresent(LoanAppDO::getCompanyId, reqVO.getCompanyId())
                .eqIfPresent(LoanAppDO::getRepayStatus,reqVO.getRepayStatus())
                .eqIfPresent(LoanAppDO::getAuditStatus,reqVO.getAuditStatus())
                .eqIfPresent(LoanAppDO::getPaymentStatus,reqVO.getPaymentStatus())
                .orderByDesc(LoanAppDO::getId);
        Long applyerId = reqVO.getApplyerId();
        if (Objects.nonNull(applyerId)) {
            loanAppDOLambdaQueryWrapperX.apply("JSON_EXTRACT(applyer, '$.userId') = {0}", applyerId);
        }
        String currency = reqVO.getCurrency();
        if (StrUtil.isNotEmpty(currency)){
            loanAppDOLambdaQueryWrapperX.apply("JSON_EXTRACT(amount, '$.currency') = {0}", currency);
        }
        if( Objects.nonNull(reqVO.getOutstandingAmountNotZeroFlag() ) &&  reqVO.getOutstandingAmountNotZeroFlag() > 0){
            loanAppDOLambdaQueryWrapperX.apply("outstanding_amount is not null  and ((JSON_EXTRACT(outstanding_amount, '$.amount')  > 0  or payment_amount is null))");
        }
        return selectPage(reqVO, loanAppDOLambdaQueryWrapperX);
    }
    @Select("""
                <script>SELECT
                currency,
                IFNULL(SUM(loan_amount),0) AS amountSum,
                IFNULL(SUM(paid_amount),0) AS paymentAmountSum,
                IFNULL(IFNULL(SUM(loan_amount),0)-IFNULL(SUM(paid_amount),0),0) AS outAmountSum,
                IFNULL(SUM(repay_amount),0) AS repayAmountSum,
                IFNULL(IFNULL(SUM(paid_amount),0)-IFNULL(SUM(repay_amount),0),0) AS inRepayAmountSum
            FROM (
                SELECT
                    loan.id AS loan_id,
            
                    JSON_UNQUOTE(JSON_EXTRACT(loan.amount, '$.currency')) AS currency,
                    <!-- 提取借款金额 -->
                    CAST(JSON_EXTRACT(loan.amount, '$.amount') AS DECIMAL(19,6)) AS loan_amount,
                    CAST(JSON_EXTRACT(loan.repay_amount, '$.amount') AS DECIMAL(19,6)) AS repay_amount,
                   <!-- 计算总还款金额（来自两个还款表） -->
                    (IFNULL(SUM(CAST(JSON_EXTRACT(repay.amount, '$.amount') AS DECIMAL(19,6))), 0) +
                     IFNULL(SUM(CAST(JSON_EXTRACT(reimb.repay_amount, '$.amount') AS DECIMAL(19,6))), 0)) AS paid_amount
                FROM
                    oa_loan_app loan
                LEFT JOIN
                    oa_repay_app repay ON repay.loan_app_id = loan.id and repay.deleted = 0
                LEFT JOIN
                    oa_reimb_repay_detail reimb ON reimb.loan_id = loan.id and reimb.deleted = 0
                WHERE 1=1 and loan.deleted = 0
                <when test='applyerId!=null'>
                AND JSON_EXTRACT(loan.applyer, '$.userId') = #{applyerId}
                </when>
                <when test='deptId!=null'>
                AND JSON_EXTRACT(loan.applyer, '$.deptId') = #{deptId}
                </when>
                <when test='repayStatus!=null'>
                AND loan.repay_status = #{repayStatus}
                </when>
                <when test='paymentStatus!=null'>
                AND loan.payment_status = #{paymentStatus}
                </when>
                <when test='loanDateStart!=null and loanDateEnd!=null'>
                AND loan.loan_date BETWEEN #{loanDateStart} AND #{loanDateEnd}
                </when>
                AND loan.audit_status NOT IN (3, 4)
                GROUP BY
                    loan.id, JSON_EXTRACT(loan.amount, '$.currency')
            ) AS combined_data
            GROUP BY currency
            HAVING SUM(loan_amount) != 0 OR SUM(paid_amount) != 0
            ORDER BY currency
                        </script>
            """)
    List<LoanappSumDO> selectLoanappSum(@Param("applyerId") Long applyerId,
                                        @Param("deptId") Long deptId,
                                        @Param("repayStatus") Integer repayStatus,
                                        @Param("paymentStatus") Integer paymentStatus,
                                        @Param("loanDateStart")LocalDateTime loanDateStart,
                                        @Param("loanDateEnd")LocalDateTime loanDateEnd
                                        );
}