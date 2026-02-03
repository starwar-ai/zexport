package com.syj.eplus.module.dal.mysql.sendbill;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.controller.admin.sendbill.vo.SendBillPageReqVO;
import com.syj.eplus.module.dal.dataobject.sendbill.SendBillDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 寄件导入单据 Mapper
 *
 * @author zhangcm
 */
@Mapper
public interface SendBillMapper extends BaseMapperX<SendBillDO> {

    default PageResult<SendBillDO> selectPage(SendBillPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<SendBillDO>()
                .betweenIfPresent(SendBillDO::getBillDate, reqVO.getBillDate())
                .eqIfPresent(SendBillDO::getBatchCode, reqVO.getBatchCode())
                .eqIfPresent(SendBillDO::getCode, reqVO.getCode())
                .eqIfPresent(SendBillDO::getCost, reqVO.getCost())
                .eqIfPresent(SendBillDO::getRemark, reqVO.getRemark())
                .eqIfPresent(SendBillDO::getVenderId, reqVO.getVenderId())
                .eqIfPresent(SendBillDO::getVenderCode, reqVO.getVenderCode())
                .eqIfPresent(SendBillDO::getVenderName, reqVO.getVenderName())
                .eqIfPresent(SendBillDO::getSortNum, reqVO.getSortNum())
                .betweenIfPresent(SendBillDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(SendBillDO::getId));
    }

}