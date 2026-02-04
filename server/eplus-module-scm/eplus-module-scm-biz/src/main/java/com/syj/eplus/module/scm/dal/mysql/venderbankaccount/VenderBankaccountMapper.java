package com.syj.eplus.module.scm.dal.mysql.venderbankaccount;


import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.scm.controller.admin.venderbankaccount.vo.VenderBankaccountPageReqVO;
import com.syj.eplus.module.scm.dal.dataobject.venderbankaccount.VenderBankaccountDO;
import org.apache.ibatis.annotations.Mapper;


/**
 * 供应商银行账户 Mapper
 *
 * @author zhangcm
 */
@Mapper
public interface VenderBankaccountMapper extends BaseMapperX<VenderBankaccountDO> {

    default PageResult<VenderBankaccountDO> selectPage(VenderBankaccountPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<VenderBankaccountDO>()
                .eqIfPresent(VenderBankaccountDO::getVer, reqVO.getVer())
                .eqIfPresent(VenderBankaccountDO::getVenderId, reqVO.getVenderId())
                .eqIfPresent(VenderBankaccountDO::getVenderVer, reqVO.getVenderVer())
                .likeIfPresent(VenderBankaccountDO::getBank, reqVO.getBank())
                .likeIfPresent(VenderBankaccountDO::getBankAccount, reqVO.getBankAccount())
                .eqIfPresent(VenderBankaccountDO::getDefaultFlag, reqVO.getDefaultFlag())
                .betweenIfPresent(VenderBankaccountDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(VenderBankaccountDO::getId));
    }

}