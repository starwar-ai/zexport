package com.syj.eplus.module.home.dal.mysql.invoiceholder;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import com.syj.eplus.module.home.controller.admin.invoiceholder.vo.InvoiceHolderPageReqVO;
import com.syj.eplus.module.home.dal.dataobject.invoiceholder.InvoiceHolderDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 发票夹 Mapper
 *
 * @author du
 */
@Mapper
public interface InvoiceHolderMapper extends BaseMapperX<InvoiceHolderDO> {

    default PageResult<InvoiceHolderDO> selectPage(InvoiceHolderPageReqVO reqVO) {
        Long loginUserId = WebFrameworkUtils.getLoginUserId();
        LambdaQueryWrapperX<InvoiceHolderDO> queryWrapperX = new LambdaQueryWrapperX<InvoiceHolderDO>()
                .eqIfPresent(InvoiceHolderDO::getReimbType, reqVO.getReimbType())
                .eqIfPresent(InvoiceHolderDO::getInvoiceAmount, reqVO.getInvoiceAmount())
                .eqIfPresent(InvoiceHolderDO::getReimbAmount, reqVO.getReimbAmount())
                .eqIfPresent(InvoiceHolderDO::getReimbItem, reqVO.getReimbItem())
                .eqIfPresent(InvoiceHolderDO::getInvoice, reqVO.getInvoice())
                .eqIfPresent(InvoiceHolderDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(InvoiceHolderDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(InvoiceHolderDO::getId);
        queryWrapperX.apply("JSON_EXTRACT(input_user, '$.userId') = {0}", loginUserId);
        if (CollUtil.isNotEmpty(reqVO.getIdList())){
            queryWrapperX.or().in(InvoiceHolderDO::getId, reqVO.getIdList());
            queryWrapperX.apply("JSON_EXTRACT(input_user, '$.userId') = {0}", loginUserId);
        }
        return selectPage(reqVO,queryWrapperX);
    }

}