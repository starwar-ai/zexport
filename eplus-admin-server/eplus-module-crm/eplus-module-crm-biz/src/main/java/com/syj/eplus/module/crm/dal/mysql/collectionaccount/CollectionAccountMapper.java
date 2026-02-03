package com.syj.eplus.module.crm.dal.mysql.collectionaccount;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.crm.controller.admin.collectionaccount.vo.CollectionAccountPageReqVO;
import com.syj.eplus.module.crm.dal.dataobject.collectionaccount.CollectionAccountDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 收款账号 Mapper
 *
 * @author zhangcm
 */
@Mapper
public interface CollectionAccountMapper extends BaseMapperX<CollectionAccountDO> {

    default PageResult<CollectionAccountDO> selectPage(CollectionAccountPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<CollectionAccountDO>()
                .eqIfPresent(CollectionAccountDO::getCustCode, reqVO.getCustCode())
                .eqIfPresent(CollectionAccountDO::getCustId, reqVO.getCustId())
                .eqIfPresent(CollectionAccountDO::getCompanyId, reqVO.getCompanyId())
                .eqIfPresent(CollectionAccountDO::getCompanyBankId, reqVO.getCompanyBankId())
                .betweenIfPresent(CollectionAccountDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(CollectionAccountDO::getId));
    }

}