package com.syj.eplus.module.oa.converter;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import com.syj.eplus.module.infra.api.company.dto.SimpleCompanyDTO;
import cn.iocoder.yudao.module.system.api.dept.dto.DeptRespDTO;
import com.syj.eplus.module.oa.controller.admin.travelreimb.vo.TravelReimbSaveReqVO;
import com.syj.eplus.module.oa.controller.admin.generalreimb.vo.GeneralReimbSaveReqVO;
import com.syj.eplus.module.oa.controller.admin.reimb.vo.ReimbRespVO;
import com.syj.eplus.module.oa.controller.admin.reimb.vo.ReimbSaveReqVO;
import com.syj.eplus.module.oa.dal.dataobject.dictsubject.DictSubjectDO;
import com.syj.eplus.module.oa.dal.dataobject.reimb.ReimbDO;
import com.syj.eplus.module.oa.entity.JsonReimbDetail;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Description：
 * @Author：du
 * @Date：2024/2/23 12:05
 */
@Mapper
public interface ReimbConverter {
    ReimbConverter INSTANCE = Mappers.getMapper(ReimbConverter.class);

    ReimbRespVO convertReimbRespVO(ReimbDO reimbDO);

    default ReimbRespVO convertReimbRespVO(ReimbDO reimbDO, Map<Long, String> userIdNameCache, Map<Long, DeptRespDTO> deptMap, Map<Long, SimpleCompanyDTO> simpleCompanyDTOMap,Map<Long, DictSubjectDO> dictSubjectMap) {
        ReimbRespVO reimbRespVO = convertReimbRespVO(reimbDO);
        if (Objects.isNull(reimbDO)) {
            return null;
        }
            Long companyId = reimbDO.getCompanyId();
            SimpleCompanyDTO simpleCompanyDTO = simpleCompanyDTOMap.get(companyId);
            if (Objects.nonNull(simpleCompanyDTO)) {
                reimbRespVO.setCompanyName(simpleCompanyDTO.getCompanyName());
            }
            if (CollUtil.isNotEmpty(dictSubjectMap)){
                DictSubjectDO dictSubjectDO = dictSubjectMap.get(reimbDO.getDictSubjectId());
                if (Objects.nonNull(dictSubjectDO)){
                    reimbRespVO.setFeeName(dictSubjectDO.getFeeName());
                }
                DictSubjectDO dictSubjectDO1 = dictSubjectMap.get(reimbDO.getExpenseUseToFormalId());
                if (Objects.nonNull(dictSubjectDO1)){
                    reimbRespVO.setExpenseUseToFormal(dictSubjectDO1.getFeeName());
                }
            }
        List<JsonReimbDetail> reimbDetailList = reimbDO.getReimbDetailList();
            if (CollUtil.isNotEmpty(reimbDetailList)&&CollUtil.isNotEmpty(dictSubjectMap)){
                reimbDetailList.forEach(s->{
                    DictSubjectDO dictSubjectDO = dictSubjectMap.get(s.getDictSubjectId());
                    if (Objects.nonNull(dictSubjectDO)){
                        s.setFeeName(dictSubjectDO.getFeeName());
                    }
                    DictSubjectDO dictSubjectDO1 = dictSubjectMap.get(s.getExpenseUseToFormalId());
                    if (Objects.nonNull(dictSubjectDO1)){
                        s.setExpenseUseToFormal(dictSubjectDO1.getFeeName());
                    }
                });
            }
        return reimbRespVO;
    }

    ReimbDO convertReimbDO(ReimbSaveReqVO reimbSaveReqVO);

    ReimbSaveReqVO convertReimbSaveReqVO(TravelReimbSaveReqVO createReqVO);

    ReimbSaveReqVO convertReimbSaveReqVO(GeneralReimbSaveReqVO createReqVO);

    default PageResult<ReimbRespVO> convertPageResult(PageResult<ReimbDO> reimbDOPageResult, Map<Long, String> userIdNameCache, Map<Long, DeptRespDTO> deptMap, Map<Long, SimpleCompanyDTO> simpleCompanyDTOMap,Map<Long, DictSubjectDO> dictSubjectMap) {
        Long total = reimbDOPageResult.getTotal();
        if (total == 0L) {
            return PageResult.empty();
        }
        List<ReimbDO> reimbDOList = reimbDOPageResult.getList();
        List<ReimbRespVO> reimbRespVOList = CollectionUtils.convertList(reimbDOList, travelReimbDO -> convertReimbRespVO(travelReimbDO, userIdNameCache, deptMap, simpleCompanyDTOMap,dictSubjectMap));
        return new PageResult<>(reimbRespVOList, total);
    }
}
