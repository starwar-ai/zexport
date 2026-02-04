package com.syj.eplus.module.oa.convert.other;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.syj.eplus.module.oa.controller.admin.otherreimb.vo.OtherReimbRespVO;
import com.syj.eplus.module.oa.controller.admin.otherreimb.vo.OtherReimbSaveReqVO;
import com.syj.eplus.module.oa.controller.admin.reimb.vo.ReimbRespVO;
import com.syj.eplus.module.oa.dal.dataobject.reimb.ReimbDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.List;

/**
 * @Author: guoliang.du
 * @CreateTime: 2025-07-30
 * @Description: 其他费用报销转换类
 */
@Mapper
public interface OtherReimbConvert {
    OtherReimbConvert INSTANCE = Mappers.getMapper(OtherReimbConvert.class);

    @Mapping(target = "invoiceList", ignore = true)
    OtherReimbRespVO convertOtherReimbRespByReimbResp(ReimbRespVO reimbResp);
    @Mapping(target = "otherExpenseResp",source = "otherExpense")
    @Mapping(target = "userFlagResp",source = "userFlag")
    default List<OtherReimbRespVO> convertOtherReimbListByReimbRespVO(List<ReimbRespVO> reimbResp){
        if (CollUtil.isEmpty(reimbResp)){
            return Collections.emptyList();
        }
        return reimbResp.stream().map(this::convertOtherReimbRespByReimbResp).toList();
    }

    ReimbDO convertReimbDO(OtherReimbSaveReqVO createReqVO);
    default PageResult<OtherReimbRespVO> convertOtherPageResult(PageResult<ReimbRespVO> travelReimbPage) {
        if (CollUtil.isEmpty(travelReimbPage.getList())) {
            return PageResult.empty();
        }
        return new PageResult<OtherReimbRespVO>().setTotal(travelReimbPage.getTotal()).setList(convertOtherReimbListByReimbRespVO(travelReimbPage.getList()));
    }

}
