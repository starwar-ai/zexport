package cn.iocoder.yudao.module.system.dal.mysql.port;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.system.controller.admin.port.vo.PortPageReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.port.PortDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 口岸 Mapper
 *
 * @author ePlus
 */
@Mapper
public interface PortMapper extends BaseMapperX<PortDO> {

    default PageResult<PortDO> selectPage(PortPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<PortDO>()
                .eqIfPresent(PortDO::getCode, reqVO.getCode())
                .likeIfPresent(PortDO::getNameEng, reqVO.getNameEng())
                .likeIfPresent(PortDO::getName, reqVO.getName())
                .likeIfPresent(PortDO::getCity, reqVO.getCity())
                .eqIfPresent(PortDO::getAddress, reqVO.getAddress())
                .eqIfPresent(PortDO::getStatus, reqVO.getStatus())
                .eqIfPresent(PortDO::getCountryId, reqVO.getCountryId())
                .betweenIfPresent(PortDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(PortDO::getTopFlag).orderByDesc(PortDO::getId));
    }

}