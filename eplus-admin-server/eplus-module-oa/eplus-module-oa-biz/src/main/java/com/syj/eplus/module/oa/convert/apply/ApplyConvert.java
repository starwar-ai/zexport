package com.syj.eplus.module.oa.convert.apply;

import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import com.syj.eplus.module.oa.controller.admin.apply.vo.ApplyRespVO;
import com.syj.eplus.module.oa.controller.admin.apply.vo.ApplySaveReqVO;
import com.syj.eplus.module.oa.dal.dataobject.apply.ApplyDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;


@Mapper
public interface ApplyConvert {

        ApplyConvert INSTANCE = Mappers.getMapper(ApplyConvert.class);

        ApplyRespVO convert(ApplyDO applyDO);

        default ApplyRespVO convertApplyRespVO(ApplyDO applyDO){
                ApplyRespVO applyRespVO = convert(applyDO);
                return applyRespVO;
        }

    ApplyDO convertApplyDO(ApplySaveReqVO saveReqVO);

        default List<ApplyRespVO> convertList(List<ApplyDO> applyDOList){
                return CollectionUtils.convertList(applyDOList, this::convertApplyRespVO);
        }
}