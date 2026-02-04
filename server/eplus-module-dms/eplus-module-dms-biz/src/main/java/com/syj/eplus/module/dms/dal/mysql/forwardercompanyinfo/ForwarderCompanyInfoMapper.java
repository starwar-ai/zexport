package com.syj.eplus.module.dms.dal.mysql.forwardercompanyinfo;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.dms.controller.admin.forwardercompanyinfo.vo.ForwarderCompanyInfoPageReqVO;
import com.syj.eplus.module.dms.controller.admin.forwardercompanyinfo.vo.SimpleForwarderCompanyInfoPageReqVO;
import com.syj.eplus.module.dms.dal.dataobject.forwardercompanyinfo.ForwarderCompanyInfoDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 船代公司 Mapper
 *
 * @author du
 */
@Mapper
public interface ForwarderCompanyInfoMapper extends BaseMapperX<ForwarderCompanyInfoDO> {

    default PageResult<ForwarderCompanyInfoDO> selectPage(ForwarderCompanyInfoPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ForwarderCompanyInfoDO>()
                .eqIfPresent(ForwarderCompanyInfoDO::getCode, reqVO.getCode())
                .likeIfPresent(ForwarderCompanyInfoDO::getName, reqVO.getName())
                .likeIfPresent(ForwarderCompanyInfoDO::getCompanyName,reqVO.getCompanyName())
                .eqIfPresent(ForwarderCompanyInfoDO::getCompanyId,reqVO.getCompanyId())
                .likeIfPresent(ForwarderCompanyInfoDO::getContactName, reqVO.getContactName())
                .eqIfPresent(ForwarderCompanyInfoDO::getContactPhoneNumber, reqVO.getContactPhoneNumber())
                .eqIfPresent(ForwarderCompanyInfoDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(ForwarderCompanyInfoDO::getInputDate, reqVO.getInputDate())
                .betweenIfPresent(ForwarderCompanyInfoDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ForwarderCompanyInfoDO::getId));
    }

    default PageResult<ForwarderCompanyInfoDO> selectPageSimple(SimpleForwarderCompanyInfoPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ForwarderCompanyInfoDO>()
                .likeIfPresent(ForwarderCompanyInfoDO::getName, reqVO.getName())
                .orderByDesc(ForwarderCompanyInfoDO::getId));
    }

}