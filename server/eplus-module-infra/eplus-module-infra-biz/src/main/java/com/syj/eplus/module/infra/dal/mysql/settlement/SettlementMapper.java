package com.syj.eplus.module.infra.dal.mysql.settlement;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.infra.controller.admin.settlement.vo.SettlementPageReqVO;
import com.syj.eplus.module.infra.dal.dataobject.settlement.SettlementDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 结汇方式 Mapper
 *
 * @author eplus
 */
@Mapper
public interface SettlementMapper extends BaseMapperX<SettlementDO> {

    default PageResult<SettlementDO> selectPage(SettlementPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<SettlementDO>()
                .likeIfPresent(SettlementDO::getName, reqVO.getName())
                .likeIfPresent(SettlementDO::getNameEng, reqVO.getNameEng())
                .betweenIfPresent(SettlementDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(SettlementDO::getId));
    }

}