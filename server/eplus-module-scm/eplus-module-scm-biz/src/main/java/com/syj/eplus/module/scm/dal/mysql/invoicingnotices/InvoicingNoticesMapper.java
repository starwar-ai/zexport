package com.syj.eplus.module.scm.dal.mysql.invoicingnotices;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.scm.controller.admin.invoicingnotices.vo.InvoicingNoticesPageReqVO;
import com.syj.eplus.module.scm.dal.dataobject.invoicingnotices.InvoicingNoticesDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Objects;

/**
 * 开票通知 Mapper
 *
 * @author du
 */
@Mapper
public interface InvoicingNoticesMapper extends BaseMapperX<InvoicingNoticesDO> {

    default PageResult<InvoicingNoticesDO> selectPage(InvoicingNoticesPageReqVO reqVO) {
        LambdaQueryWrapperX<InvoicingNoticesDO> queryWrapperX = new LambdaQueryWrapperX<InvoicingNoticesDO>()
                .likeIfPresent(InvoicingNoticesDO::getCode, reqVO.getCode())
                .eqIfPresent(InvoicingNoticesDO::getCompanyId, reqVO.getCompanyId())
                .likeIfPresent(InvoicingNoticesDO::getCompanyName, reqVO.getCompanyName())
                .likeIfPresent(InvoicingNoticesDO::getPurOrderCode, reqVO.getPurOrderCode())
                .likeIfPresent(InvoicingNoticesDO::getVenderName, reqVO.getVenderName())
                .eqIfPresent(InvoicingNoticesDO::getVenderCode, reqVO.getVenderCode())
                .eqIfPresent(InvoicingNoticesDO::getStatus, reqVO.getStatus())
                .likeIfPresent(InvoicingNoticesDO::getShipInvoiceCode, reqVO.getShipInvoiceCode())
                .betweenIfPresent(InvoicingNoticesDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(InvoicingNoticesDO::getAuditStatus, reqVO.getAuditStatus())
                .betweenIfPresent(InvoicingNoticesDO::getShipDate, reqVO.getShipDate())
                .orderByDesc(InvoicingNoticesDO::getId);
        if (CollUtil.isNotEmpty(reqVO.getExcludeAuditStatus())){
            queryWrapperX.notIn(InvoicingNoticesDO::getAuditStatus, reqVO.getExcludeAuditStatus());
        }
        if (Objects.nonNull(reqVO.getManagerId())) {
            queryWrapperX.apply("JSON_EXTRACT(manager, '$.userId') = {0}", reqVO.getManagerId());
        }
        if (Objects.nonNull(reqVO.getInputUserId())) {
            queryWrapperX.apply("JSON_EXTRACT(input_user, '$.userId') = {0}", reqVO.getInputUserId());
        }
        return selectPage(reqVO, queryWrapperX);
    }

}