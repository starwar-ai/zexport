package com.syj.eplus.module.pms.dal.mysql.hsdata;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.pms.controller.admin.hsdata.vo.HsdataPageReqVO;
import com.syj.eplus.module.pms.dal.dataobject.hsdata.HsdataDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 海关编码 Mapper
 *
 * @author ePlus
 */
@Mapper
public interface HsdataMapper extends BaseMapperX<HsdataDO> {

    default PageResult<HsdataDO> selectPage(HsdataPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<HsdataDO>()
                .likeIfPresent(HsdataDO::getCode, reqVO.getCode())
                .likeIfPresent(HsdataDO::getUnit, reqVO.getUnit())
                .likeIfPresent(HsdataDO::getName, reqVO.getName())
                .eqIfPresent(HsdataDO::getTaxRefundRate, reqVO.getTaxRefundRate())
                .eqIfPresent(HsdataDO::getRate, reqVO.getRate())
                .eqIfPresent(HsdataDO::getRemark, reqVO.getRemark())
                .likeIfPresent(HsdataDO::getChname, reqVO.getChname())
                .eqIfPresent(HsdataDO::getAddrate, reqVO.getAddrate())
                .eqIfPresent(HsdataDO::getCode2, reqVO.getCode2())
                .betweenIfPresent(HsdataDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(HsdataDO::getId));
    }

    /**
     * 根据HS编码模糊查询获取hsCodeId列表
     * @param hsCode HS编码
     * @return hsCodeId列表
     */
    default List<Long> getHsCodeIdListByHsCode(String hsCode) {
        if (StrUtil.isEmpty(hsCode)) {
            return null;
        }
        return selectList(new LambdaQueryWrapperX<HsdataDO>()
                .select(HsdataDO::getId)
                .like(HsdataDO::getCode, hsCode))
                .stream()
                .map(HsdataDO::getId)
                .toList();
    }
}