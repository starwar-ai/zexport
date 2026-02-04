package com.syj.eplus.module.oa.convert.general;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.syj.eplus.module.oa.controller.admin.generalreimb.vo.GeneralReimbRespVO;
import com.syj.eplus.module.oa.controller.admin.generalreimb.vo.GeneralReimbSaveReqVO;
import com.syj.eplus.module.oa.controller.admin.reimb.vo.ReimbRespVO;
import com.syj.eplus.module.oa.dal.dataobject.reimb.ReimbDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.List;

/**
 * @Description：
 * @Author：du
 * @Date：2024/4/24 17:42
 */
@Mapper
public interface GeneralReimbConvert {
    GeneralReimbConvert INSTANCE = Mappers.getMapper(GeneralReimbConvert.class);

    @Mapping(target = "invoiceList", ignore = true)
    GeneralReimbRespVO convertGeneralReimbRespByReimbResp(ReimbRespVO reimbResp);
    @Mapping(target = "generalExpenseResp",source = "generalExpense")
    @Mapping(target = "userFlagResp",source = "userFlag")
    default List<GeneralReimbRespVO> convertGeneralReimbListByReimbRespVO(List<ReimbRespVO> reimbResp){
        if (CollUtil.isEmpty(reimbResp)){
            return Collections.emptyList();
        }
        return reimbResp.stream().map(this::convertGeneralReimbRespByReimbResp).toList();
    }

    ReimbDO convertReimbDO(GeneralReimbSaveReqVO createReqVO);
    default PageResult<GeneralReimbRespVO> convertGeneralPageResult(PageResult<ReimbRespVO> travelReimbPage) {
        if (CollUtil.isEmpty(travelReimbPage.getList())) {
            return PageResult.empty();
        }
        return new PageResult<GeneralReimbRespVO>().setTotal(travelReimbPage.getTotal()).setList(convertGeneralReimbListByReimbRespVO(travelReimbPage.getList()));
    }

}
