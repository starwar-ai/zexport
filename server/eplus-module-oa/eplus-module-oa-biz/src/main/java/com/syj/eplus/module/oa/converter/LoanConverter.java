package com.syj.eplus.module.oa.converter;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import com.syj.eplus.module.infra.api.company.dto.SimpleCompanyDTO;
import cn.iocoder.yudao.module.system.api.dept.dto.DeptRespDTO;
import com.syj.eplus.framework.common.enums.RepaySourceTypeEnum;
import com.syj.eplus.module.fms.api.payment.api.payment.dto.PaymentSaveDTO;
import com.syj.eplus.module.oa.api.dto.SimpleLoanAppRespDTO;
import com.syj.eplus.module.oa.api.dto.SimpleRepayRespDTO;
import com.syj.eplus.module.oa.controller.admin.loanapp.vo.LoanAppRespVO;
import com.syj.eplus.module.oa.controller.admin.reimb.vo.ReimbRespVO;
import com.syj.eplus.module.oa.dal.dataobject.loanapp.LoanAppDO;
import com.syj.eplus.module.oa.dal.dataobject.reimbrepaydetail.ReimbRepayDetailDO;
import com.syj.eplus.module.oa.dal.dataobject.repayapp.RepayAppDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Mapper
public interface LoanConverter {


    LoanConverter INSTANCE = Mappers.getMapper(LoanConverter.class);

    LoanAppRespVO convert(LoanAppDO loanAppDO);

    List<LoanAppRespVO> convertList(List<LoanAppDO> list);

    SimpleLoanAppRespDTO convertSimpleLoanAppRespDTO(LoanAppDO loanAppDO);

    SimpleLoanAppRespDTO convertSimpleLoanAppRespDTOByVO(LoanAppRespVO loanAppVO);

    @Mapping(target = "amount", ignore = true)
    PaymentSaveDTO convertPaymentSaveDTO(LoanAppDO loanApp);
    default PageResult<LoanAppRespVO> convertPage(PageResult<LoanAppDO> pageResult) {
        return new PageResult<>(converList(pageResult.getList()), pageResult.getTotal());
    }

    default List<LoanAppRespVO> converList(List<LoanAppDO> LoanAppList) {
        List<LoanAppRespVO> result = convertList(LoanAppList);
        return result;
    }

    default PageResult<SimpleLoanAppRespDTO> convertSimpleLoanAppPageDTO(PageResult<LoanAppDO> loanAppDOPageResult, Map<Long, DeptRespDTO> deptMap, Map<Long, SimpleCompanyDTO> simpleCompanyDTOMap) {
        List<LoanAppDO> loanAppDOList = loanAppDOPageResult.getList();
        Long total = loanAppDOPageResult.getTotal();
        List<SimpleLoanAppRespDTO> simpleLoanAppRespDTOList = CollectionUtils.convertList(loanAppDOList, loanAppDO -> {
            SimpleLoanAppRespDTO simpleLoanAppRespDTO = convertSimpleLoanAppRespDTO(loanAppDO);
            if (CollUtil.isNotEmpty(simpleCompanyDTOMap)) {
                SimpleCompanyDTO simpleCompanyDTO = simpleCompanyDTOMap.get(loanAppDO.getCompanyId());
                if (Objects.nonNull(simpleCompanyDTO)) {
                    simpleLoanAppRespDTO.setCompanyName(simpleCompanyDTO.getCompanyName());
                }
            }
            return simpleLoanAppRespDTO;
        });
        return new PageResult<SimpleLoanAppRespDTO>().setList(simpleLoanAppRespDTOList).setTotal(total);
    }

    default List<SimpleLoanAppRespDTO> convertSimpleLoanAppList(List<LoanAppDO> loanAppDOList, Map<Long, DeptRespDTO> deptMap, Map<Long, SimpleCompanyDTO> simpleCompanyDTOMap) {
        return CollectionUtils.convertList(loanAppDOList, loanAppDO -> {
            SimpleLoanAppRespDTO simpleLoanAppRespDTO = convertSimpleLoanAppRespDTO(loanAppDO);
            // 转换主体名称
            if (CollUtil.isNotEmpty(simpleCompanyDTOMap)) {
                SimpleCompanyDTO simpleCompanyDTO = simpleCompanyDTOMap.get(loanAppDO.getCompanyId());
                if (Objects.nonNull(simpleCompanyDTO)) {
                    simpleLoanAppRespDTO.setCompanyName(simpleCompanyDTO.getCompanyName());
                }
            }
            return simpleLoanAppRespDTO;
        });
    }

    @Mapping(source = "amount", target = "repayAmount")
    @Mapping(source = "code",target = "repaySourceCode")
    SimpleRepayRespDTO convertByRepayAppDO(RepayAppDO repayAppDOList);

    default List<SimpleRepayRespDTO> convertByRepayAppDOList(List<RepayAppDO> repayAppDOList) {
        return repayAppDOList.stream().map(s -> convertByRepayAppDO(s)).toList();
    }

    @Mapping(source = "reimbUser", target = "repayer")
    @Mapping(source = "repayAmount", target = "repayAmount")
    List<SimpleRepayRespDTO> convertByReimbRespVO(List<ReimbRespVO> reimbRespVOList);

    SimpleRepayRespDTO convertByReimbRepayDetailList(ReimbRepayDetailDO reimbRepayDetailDOList);

    default List<SimpleRepayRespDTO> convertSimpleRepayRespDTO(List<RepayAppDO> repayAppDOList, List<ReimbRepayDetailDO> reimbRepayDetailDOList, Map<Long, ReimbRespVO> reimbRespMap ) {
        List<SimpleRepayRespDTO> result = new ArrayList<>();
        if (CollUtil.isNotEmpty(repayAppDOList)) {
            List<SimpleRepayRespDTO> simpleRepayRespDTOList = convertByRepayAppDOList(repayAppDOList);
            if (CollUtil.isNotEmpty(simpleRepayRespDTOList)) {
                simpleRepayRespDTOList.forEach(s->{
                    s.setRepaySourceType(RepaySourceTypeEnum.REPAY_APP.getType());
                    s.setReimbUser(s.getRepayer());
                });
                result.addAll(simpleRepayRespDTOList);
            }
        }
        if (CollUtil.isNotEmpty(reimbRepayDetailDOList)) {
            reimbRepayDetailDOList.forEach(reimbRepayDetailDO -> {
                SimpleRepayRespDTO simpleRepayRespDTO = convertByReimbRepayDetailList(reimbRepayDetailDO);
                if (CollUtil.isNotEmpty(reimbRespMap)) {
                    ReimbRespVO reimbRespVO = reimbRespMap.get(reimbRepayDetailDO.getReimbId());
                    if (Objects.nonNull(reimbRespVO)) {
                        simpleRepayRespDTO.setReimbUser(reimbRespVO.getReimbUser());
                    }
                }
                result.add(simpleRepayRespDTO);
            });
        }
        return result;
    }

    default PageResult<LoanAppRespVO> convertLoanAppPageResult(PageResult<LoanAppDO> loanAppDOPageResult, Map<Long, SimpleCompanyDTO> simpleCompanyDTOList) {
        List<LoanAppDO> loanAppDOList = loanAppDOPageResult.getList();
        if (CollUtil.isEmpty(loanAppDOList)) {
            return PageResult.empty();
        }
        List<LoanAppRespVO> loanAppRespVOList = convertList(loanAppDOList);
        loanAppRespVOList.forEach(s -> {
            Long companyId = s.getCompanyId();
            if (CollUtil.isNotEmpty(simpleCompanyDTOList)) {
                SimpleCompanyDTO simpleCompanyDTO = simpleCompanyDTOList.get(companyId);
                if (Objects.nonNull(simpleCompanyDTO)) {
                    s.setCompanyName(simpleCompanyDTO.getCompanyName());
                }
            }
        });
        return new PageResult<LoanAppRespVO>().setTotal(loanAppDOPageResult.getTotal()).setList(loanAppRespVOList);
    }

}
