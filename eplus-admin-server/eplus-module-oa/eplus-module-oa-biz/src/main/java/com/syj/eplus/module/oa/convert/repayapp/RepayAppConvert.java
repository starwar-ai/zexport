package com.syj.eplus.module.oa.convert.repayapp;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.syj.eplus.module.fms.api.payment.api.receipt.dto.ReceiptCreateDTO;
import com.syj.eplus.module.oa.api.dto.RepayAppDTO;
import com.syj.eplus.module.oa.api.dto.SimpleLoanAppRespDTO;
import com.syj.eplus.module.oa.controller.admin.repayapp.vo.RepayAppRespVO;
import com.syj.eplus.module.oa.controller.admin.repayapp.vo.RepayAppSaveReqVO;
import com.syj.eplus.module.oa.dal.dataobject.repayapp.RepayAppDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


@Mapper
public interface RepayAppConvert {

    RepayAppConvert INSTANCE = Mappers.getMapper(RepayAppConvert.class);


    RepayAppRespVO convertRepayAppRespVO(RepayAppDO repayAppDO);

    RepayAppDO convertRepayAppDO(RepayAppSaveReqVO saveReqVO);

    @Mapping(target = "code", ignore = true)
    @Mapping(target = "printFlag", ignore = true)
    @Mapping(target = "printTimes", ignore = true)
    @Mapping(target = "auditStatus", ignore = true)
    ReceiptCreateDTO convertReceiptCreateDTO(RepayAppRespVO repayAppRespVO);

    @Mapping(target = "code", ignore = true)
    @Mapping(target = "printFlag", ignore = true)
    @Mapping(target = "printTimes", ignore = true)
    @Mapping(target = "auditStatus", ignore = true)
    ReceiptCreateDTO convertReceiptCreateDTO(RepayAppDO repayAppDO);

    RepayAppSaveReqVO convertRepayAppSaveReq(RepayAppDTO repayAppDTO);

    List<RepayAppRespVO> convertRepayAppRespVOList(List<RepayAppDO> repayAppDOList);

    default PageResult<RepayAppRespVO> convertRepayRespPageResult(PageResult<RepayAppDO> repayAppDOPageResult, List<SimpleLoanAppRespDTO> simpleLoanAppRespDTOList) {
        List<RepayAppDO> repayAppDOList = repayAppDOPageResult.getList();
        if (CollUtil.isEmpty(repayAppDOList)) {
            return PageResult.empty();
        }
        List<RepayAppRespVO> repayAppRespVOList = convertRepayAppRespVOList(repayAppDOList);
        if (CollUtil.isEmpty(repayAppRespVOList)) {
            return PageResult.empty();
        }
        Map<Long, SimpleLoanAppRespDTO> simpleLoanAppRespDTOMap;
        if (CollUtil.isNotEmpty(simpleLoanAppRespDTOList)) {
            simpleLoanAppRespDTOMap = simpleLoanAppRespDTOList.stream().collect(Collectors.toMap(SimpleLoanAppRespDTO::getId, simpleLoanAppRespDTO -> simpleLoanAppRespDTO));
        } else {
            simpleLoanAppRespDTOMap = Collections.emptyMap();
        }
        if (CollUtil.isNotEmpty(simpleLoanAppRespDTOMap)) {
            repayAppRespVOList.forEach(s -> {
                SimpleLoanAppRespDTO simpleLoanAppRespDTO = simpleLoanAppRespDTOMap.get(s.getLoanAppId());
                if (Objects.nonNull(simpleLoanAppRespDTO)) {
                    s.setLoanAppCode(simpleLoanAppRespDTO.getCode());
                }
            });
        }

        return new PageResult<RepayAppRespVO>().setTotal(repayAppDOPageResult.getTotal()).setList(repayAppRespVOList);
    }

}