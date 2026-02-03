package com.syj.eplus.module.crm.dal.mysql.custbankaccount;


import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.crm.controller.admin.custbankaccount.vo.CustBankaccountPageReqVO;
import com.syj.eplus.module.crm.dal.dataobject.custbankaccount.CustBankaccountDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 银行账户信息 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface CustBankaccountMapper extends BaseMapperX<CustBankaccountDO> {

    default PageResult<CustBankaccountDO> selectPage(CustBankaccountPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<CustBankaccountDO>()
                .eqIfPresent(CustBankaccountDO::getVer, reqVO.getVer())
                .eqIfPresent(CustBankaccountDO::getCustId, reqVO.getCustId())
                .eqIfPresent(CustBankaccountDO::getBank, reqVO.getBank())
                .eqIfPresent(CustBankaccountDO::getBankAccount, reqVO.getBankAccount())
                .eqIfPresent(CustBankaccountDO::getDefaultFlag, reqVO.getDefaultFlag())
                .betweenIfPresent(CustBankaccountDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(CustBankaccountDO::getId));
    }

}