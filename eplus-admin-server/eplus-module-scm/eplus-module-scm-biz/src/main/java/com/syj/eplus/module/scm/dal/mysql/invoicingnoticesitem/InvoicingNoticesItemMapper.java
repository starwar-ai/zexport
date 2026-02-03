package com.syj.eplus.module.scm.dal.mysql.invoicingnoticesitem;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.scm.controller.admin.invoicingnotices.vo.InvoicingNoticesPageReqVO;
import com.syj.eplus.module.scm.dal.dataobject.invoicingnoticesItem.InvoicingNoticesItem;
import org.apache.ibatis.annotations.Mapper;

/**
 * 开票通知明细 Mapper
 *
 * @author du
 */
@Mapper
public interface InvoicingNoticesItemMapper extends BaseMapperX<InvoicingNoticesItem> {
    default PageResult<InvoicingNoticesItem> selectPage(InvoicingNoticesPageReqVO reqVO) {
        LambdaQueryWrapperX<InvoicingNoticesItem> queryWrapperX = new LambdaQueryWrapperX<InvoicingNoticesItem>()
                .betweenIfPresent(InvoicingNoticesItem::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(InvoicingNoticesItem::getId);
        if (StrUtil.isNotEmpty(reqVO.getManagerName())) {
            queryWrapperX.apply("JSON_EXTRACT(manager, '$.nickname') = {0}", reqVO.getManagerName());
        }
        return selectPage(reqVO, queryWrapperX);
    }
}