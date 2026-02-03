package com.syj.eplus.module.dal.mysql.send;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.controller.admin.send.vo.SendPageReqVO;
import com.syj.eplus.module.dal.dataobject.send.SendDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Objects;

/**
 * 寄件 Mapper
 *
 * @author zhangcm
 */
@Mapper
public interface SendMapper extends BaseMapperX<SendDO> {

    default PageResult<SendDO> selectPage(SendPageReqVO reqVO) {
        LambdaQueryWrapperX<SendDO> sendDOLambdaQueryWrapperX = new LambdaQueryWrapperX<SendDO>()
                .likeIfPresent(SendDO::getCode, reqVO.getCode())
                .eqIfPresent(SendDO::getInputUserId, reqVO.getInputUserId())
                .likeIfPresent(SendDO::getInputUserName, reqVO.getInputUserName())
                .eqIfPresent(SendDO::getSendRegion, reqVO.getSendRegion())
                .likeIfPresent(SendDO::getReceiveCode, reqVO.getReceiveCode())
                .likeIfPresent(SendDO::getReceiveName, reqVO.getReceiveName())
                .eqIfPresent(SendDO::getGoodsType, reqVO.getGoodsType())
                .eqIfPresent(SendDO::getPayType, reqVO.getPayType())
                .likeIfPresent(SendDO::getReceiveMsg, reqVO.getReceiveMsg())
                .eqIfPresent(SendDO::getVenderCode, reqVO.getVenderCode())
                .likeIfPresent(SendDO::getVenderName, reqVO.getVenderName())
                .eqIfPresent(SendDO::getEstCost, reqVO.getEstCost())
                .eqIfPresent(SendDO::getRemark, reqVO.getRemark())
                .eqIfPresent(SendDO::getSendStatus, reqVO.getSendStatus())
                .eqIfPresent(SendDO::getAuditStatus, reqVO.getAuditStatus())
                .likeIfPresent(SendDO::getExpressCode, reqVO.getExpressCode())
                .eqIfPresent(SendDO::getCost, reqVO.getCost())
                .eqIfPresent(SendDO::getBelongFlag, reqVO.getBelongFlag())
                .eqIfPresent(SendDO::getPayStatus, reqVO.getPayStatus())
                .betweenIfPresent(SendDO::getSubmitTime, reqVO.getSubmitTime())
                .betweenIfPresent(SendDO::getSendTime, reqVO.getSendTime())
                .betweenIfPresent(SendDO::getCostTime, reqVO.getCostTime())
                .betweenIfPresent(SendDO::getDoneTime, reqVO.getDoneTime())
                .betweenIfPresent(SendDO::getPayTime, reqVO.getPayTime())
                .betweenIfPresent(SendDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(SendDO::getId);
        if (CollUtil.isNotEmpty(reqVO.getExcludeStatus())){
            sendDOLambdaQueryWrapperX.notIn(SendDO::getSendStatus, reqVO.getExcludeStatus());
        }
        if (Objects.nonNull(reqVO.getActualUserId())) {
            sendDOLambdaQueryWrapperX.apply("actual_user ->> '$.userId' = {0}", reqVO.getActualUserId());
        }
        return selectPage(reqVO,sendDOLambdaQueryWrapperX );


    }

}