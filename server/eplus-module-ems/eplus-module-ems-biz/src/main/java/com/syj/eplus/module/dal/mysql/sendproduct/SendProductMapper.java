package com.syj.eplus.module.dal.mysql.sendproduct;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.controller.admin.sendproduct.vo.SendProductPageReqVO;
import com.syj.eplus.module.dal.dataobject.sendproduct.SendProductDO;
import org.apache.ibatis.annotations.Mapper;
/**
 * 寄件产品 Mapper
 *
 * @author zhangcm
 */
@Mapper
public interface SendProductMapper extends BaseMapperX<SendProductDO> {

    default PageResult<SendProductDO> selectPage(SendProductPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<SendProductDO>()
                .eqIfPresent(SendProductDO::getSortNum, reqVO.getSortNum())
                .eqIfPresent(SendProductDO::getGoodsSource, reqVO.getGoodsSource())
                .eqIfPresent(SendProductDO::getSkuCode, reqVO.getSkuCode())
                .likeIfPresent(SendProductDO::getSkuName, reqVO.getSkuName())
                .eqIfPresent(SendProductDO::getPicture, reqVO.getPicture())
                .eqIfPresent(SendProductDO::getQuantity, reqVO.getQuantity())
                .betweenIfPresent(SendProductDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(SendProductDO::getId));
    }

}