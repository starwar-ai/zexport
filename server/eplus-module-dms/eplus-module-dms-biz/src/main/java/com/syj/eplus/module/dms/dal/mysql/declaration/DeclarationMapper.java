package com.syj.eplus.module.dms.dal.mysql.declaration;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.MPJLambdaWrapperX;
import com.syj.eplus.module.dms.controller.admin.declaration.vo.DeclarationPageReqVO;
import com.syj.eplus.module.dms.dal.dataobject.declaration.DeclarationDO;
import com.syj.eplus.module.dms.dal.dataobject.declarationitem.DeclarationItem;
import org.apache.ibatis.annotations.Mapper;

/**
 * 报关单 Mapper
 *
 * @author du
 */
@Mapper
public interface DeclarationMapper extends BaseMapperX<DeclarationDO> {

    default PageResult<DeclarationDO> selectPage(DeclarationPageReqVO reqVO) {
        MPJLambdaWrapperX<DeclarationDO> queryWrapperX = new MPJLambdaWrapperX<DeclarationDO>()
                .selectAll(DeclarationDO.class)
                .likeIfPresent(DeclarationDO::getCode, reqVO.getCode())
                .eqIfPresent(DeclarationDO::getForeignTradeCompanyId, reqVO.getForeignTradeCompanyId())
                .likeIfPresent(DeclarationDO::getInvoiceCode, reqVO.getInvoiceCode())
                .eqIfPresent(DeclarationDO::getShipmentPlanCode, reqVO.getShipmentPlanCode())
                .betweenIfPresent(DeclarationDO::getCreateTime, reqVO.getCreateTime())
                .innerJoin(DeclarationItem.class, DeclarationItem::getDeclarationId, DeclarationDO::getId);
 
        if (StrUtil.isNotEmpty(reqVO.getDeclarationName())) {
            queryWrapperX.like(DeclarationItem::getDeclarationName, reqVO.getDeclarationName());
        }
        if (StrUtil.isNotEmpty(reqVO.getCustomsDeclarationNameEng())) {
            queryWrapperX.like(DeclarationItem::getCustomsDeclarationNameEng, reqVO.getCustomsDeclarationNameEng());
        }
        if (StrUtil.isNotEmpty(reqVO.getCustName())) {
            queryWrapperX.like(DeclarationItem::getCustName, reqVO.getCustName());
        }
        if (StrUtil.isNotEmpty(reqVO.getCustPo())) {
            queryWrapperX.like(DeclarationItem::getCustPo, reqVO.getCustPo());
        }
 
        queryWrapperX.groupBy(DeclarationDO::getId)
                .orderByDesc(DeclarationDO::getId);

        return selectJoinPage(reqVO, DeclarationDO.class, queryWrapperX);
    }

}