package com.syj.eplus.module.oa.convert.entertain;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.syj.eplus.module.oa.controller.admin.entertain.vo.EntertainReimbRespVO;
import com.syj.eplus.module.oa.controller.admin.entertain.vo.EntertainReimbSaveReqVO;
import com.syj.eplus.module.oa.controller.admin.reimb.vo.ReimbRespVO;
import com.syj.eplus.module.oa.controller.admin.reimb.vo.ReimbSaveReqVO;
import com.syj.eplus.module.oa.dal.dataobject.reimb.ReimbDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.List;

/**
 * @Description：
 * @Author：du
 * @Date：2024/4/24 18:08
 */
@Mapper
public interface EntertainConvert {

    EntertainConvert INSTANCE = Mappers.getMapper(EntertainConvert.class);

    ReimbSaveReqVO convertReimbSaveReq(EntertainReimbSaveReqVO createReqVO);

    EntertainReimbRespVO convertEntertainReimbResp(ReimbRespVO reimbResp);

    ReimbDO convertReimbDO(EntertainReimbSaveReqVO createReqVO);
    default PageResult<EntertainReimbRespVO> convertEntertainPageResult(PageResult<ReimbRespVO> entertainReimbPage) {
        if (CollUtil.isEmpty(entertainReimbPage.getList())) {
            return PageResult.empty();
        }
        return new PageResult<EntertainReimbRespVO>().setTotal(entertainReimbPage.getTotal()).setList(convertEntertainReimbListByReimbRespVO(entertainReimbPage.getList()));
    }

    default List<EntertainReimbRespVO> convertEntertainReimbListByReimbRespVO(List<ReimbRespVO> reimbResp){
        if (CollUtil.isEmpty(reimbResp)){
            return Collections.emptyList();
        }
        return reimbResp.stream().map(this::convertEntertainReimbResp).toList();
    }
}
