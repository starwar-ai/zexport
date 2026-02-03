package com.syj.eplus.module.oa.dal.mysql.subject;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.oa.controller.admin.subject.vo.SubjectPageReqVO;
import com.syj.eplus.module.oa.dal.dataobject.subject.SubjectDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 科目 Mapper
 *
 * @author ePlus
 */
@Mapper
public interface SubjectMapper extends BaseMapperX<SubjectDO> {

    default PageResult<SubjectDO> selectPage(SubjectPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<SubjectDO>()
                .eqIfPresent(SubjectDO::getCode, reqVO.getCode())
                .likeIfPresent(SubjectDO::getName, reqVO.getName())
                .eqIfPresent(SubjectDO::getDescription, reqVO.getDescription())
                .eqIfPresent(SubjectDO::getLayer, reqVO.getLayer())
                .eqIfPresent(SubjectDO::getNature, reqVO.getNature())
                .eqIfPresent(SubjectDO::getType, reqVO.getType())
                .eqIfPresent(SubjectDO::getAuxiliaryAccounting, reqVO.getAuxiliaryAccounting())
                .eqIfPresent(SubjectDO::getAccountingNumber, reqVO.getAccountingNumber())
                .eqIfPresent(SubjectDO::getIsForeignCurrencyAccounting, reqVO.getIsForeignCurrencyAccounting())
                .eqIfPresent(SubjectDO::getCurrency, reqVO.getCurrency())
                .eqIfPresent(SubjectDO::getIsFinalExchange, reqVO.getIsFinalExchange())
                .eqIfPresent(SubjectDO::getIsBank, reqVO.getIsBank())
                .eqIfPresent(SubjectDO::getIsCash, reqVO.getIsCash())
                .eqIfPresent(SubjectDO::getIsCashBank, reqVO.getIsCashBank())
                .eqIfPresent(SubjectDO::getParentId, reqVO.getParentId())
                .likeIfPresent(SubjectDO::getParentName, reqVO.getParentName())
                .betweenIfPresent(SubjectDO::getInputDate, reqVO.getInputDate())
                .eqIfPresent(SubjectDO::getIsCashFlowRelated, reqVO.getIsCashFlowRelated())
                .eqIfPresent(SubjectDO::getIsLast, reqVO.getIsLast())
                .eqIfPresent(SubjectDO::getCashFlowCode, reqVO.getCashFlowCode())
                .likeIfPresent(SubjectDO::getCashFlowName, reqVO.getCashFlowName())
                .eqIfPresent(SubjectDO::getBankAccount, reqVO.getBankAccount())
                .eqIfPresent(SubjectDO::getBankCode, reqVO.getBankCode())
                .eqIfPresent(SubjectDO::getBalanceDirection, reqVO.getBalanceDirection())
                .eqIfPresent(SubjectDO::getBalance, reqVO.getBalance())
                .orderByDesc(SubjectDO::getId));
    }

}