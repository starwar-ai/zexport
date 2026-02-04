package cn.iocoder.yudao.module.system.dal.mysql.priceType;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.system.controller.admin.priceType.vo.PriceTypePageReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.priceType.PriceTypeDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 价格条款 Mapper
 *
 * @author ePlus
 */
@Mapper
public interface PriceTypeMapper extends BaseMapperX<PriceTypeDO> {

    default PageResult<PriceTypeDO> selectPage(PriceTypePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<PriceTypeDO>()
                .likeIfPresent(PriceTypeDO::getTypeName, reqVO.getTypeName())
                .eqIfPresent(PriceTypeDO::getTypeDesc, reqVO.getTypeDesc())
                .betweenIfPresent(PriceTypeDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(PriceTypeDO::getId));
    }

}