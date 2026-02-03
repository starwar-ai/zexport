package com.syj.eplus.module.scm.api;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.ConfirmSourceEntity;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;
import com.syj.eplus.module.infra.api.company.dto.SimpleCompanyDTO;
import com.syj.eplus.module.infra.api.paymentitem.dto.PaymentItemDTO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.framework.common.enums.BooleanEnum;
import com.syj.eplus.module.scm.api.vender.VenderApi;
import com.syj.eplus.module.scm.api.vender.dto.SimpleVenderResp;
import com.syj.eplus.module.scm.api.vender.dto.SimpleVenderRespDTO;
import com.syj.eplus.module.scm.api.vender.dto.VenderAllDTO;
import com.syj.eplus.module.scm.api.vender.dto.VenderPaymentDTO;
import com.syj.eplus.module.scm.dal.dataobject.vender.VenderDO;
import com.syj.eplus.module.scm.service.vender.VenderService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class VenderApiImpl implements VenderApi {
    @Resource
    @Lazy
    private VenderService venderService;

    @Override
    public List<SimpleVenderRespDTO> getSimpleVenderRespDTO(List<Long> idList) {
        return venderService.getSimpleVenderRespDTOList(idList);
    }

    @Override
    public Map<Long, SimpleVenderRespDTO> getSimpleVenderRespDTOMap(List<Long> idList) {
        List<SimpleVenderRespDTO> simpleVenderRespDTOList = venderService.getSimpleVenderRespDTOList(idList);
        return simpleVenderRespDTOList.stream().collect(Collectors.toMap(SimpleVenderRespDTO::getId, Function.identity()));
    }

    @Override
    public SimpleVenderRespDTO getSimpleVenderByInternalCompanyId(Long internalCompanyId) {
        LambdaQueryWrapper<VenderDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VenderDO::getInternalFlag, BooleanEnum.YES.getValue());
        queryWrapper.eq(VenderDO::getInternalCompanyId, internalCompanyId).last("limit 1");
        VenderDO venderDO = venderService.getOne(queryWrapper);
        if (Objects.isNull(venderDO)){
            return null;
        }
        Long id = venderDO.getId();
        List<VenderPaymentDTO> venderPaymentList = venderService.getVenderPaymentList(id);
        SimpleVenderRespDTO simpleVenderRespDTO = BeanUtils.toBean(venderDO, SimpleVenderRespDTO.class);
        simpleVenderRespDTO.setPaymentList(venderPaymentList);
        return simpleVenderRespDTO;
    }

    @Override
    public Map<String, String> getVenderNameCache(String code) {
        return venderService.getVenderNameCache(code);
    }

    @Override
    public Map<String, List<UserDept>> getVenderManagerByCodeList(List<String> codeList) {
        return venderService.getVenderManagerByCodeList(codeList);
    }

    @Override
    @DataPermission(enable = false)
    public SimpleVenderResp getSimpleVenderRespByBankPoc(String bankPoc) {
        return venderService.getSimpleVenderRespByBankPoc(bankPoc);
    }

    @Override
    public Map<String, SimpleVenderResp> getSimpleVenderMapByCodes(Collection<String> codeList) {
        if (CollUtil.isEmpty(codeList)) {
            return null;
        }
        return venderService.getSimpleVenderMapByCodes(codeList);
    }

    @Override
    public Map<String, VenderAllDTO> getVenderByCodeList(Collection<String> codeList) {
        if (CollUtil.isEmpty(codeList)){
            return Map.of();
        }
        List<VenderDO> doList = venderService.getVenderByCodeList(codeList);
        List<VenderAllDTO> dtoList =BeanUtils.toBean(doList, VenderAllDTO.class);
        if(CollUtil.isNotEmpty(dtoList)){
            return  dtoList.stream().collect(Collectors.toMap(VenderAllDTO::getCode,s->s));
        }
        return null;
    }

    @Override
    public List<ConfirmSourceEntity> getConfirmSourceList(String targetCode, Integer effectRangeType) {
        return venderService.getConfirmSourceList(targetCode, effectRangeType);
    }

    @Override
    public SimpleVenderRespDTO getSimpleVenderRespDTOById(Long venderId) {
        return venderService.getSimpleVenderRespDTOById(venderId);
    }

    @Override
    public SimpleCompanyDTO getCompanyByVenderCode(String venderCode) {
        return venderService.getCompanyByVenderCode(venderCode);
    }

    @Override
    public List<VenderPaymentDTO> getVenderPaymentList(Long venderId) {
        return venderService.getVenderPaymentList(venderId);
    }

    @Override
    public List<SimpleVenderRespDTO> getSimpleVenderRespDTOList(List<Long> codes) {
        return venderService.getSimpleVenderRespDTOList(codes);
    }

    @Override
    public Map<Long,PaymentItemDTO> getPaymentItemDTOListByVenderCode(String venderCode) {
        return venderService.getPaymentItemDTOListByVenderCode(venderCode);
    }

    @Override
    public String getBankPocByVenderCode(String venderCode) {
        return venderService.getBankPocByVenderCode(venderCode);
    }
}
