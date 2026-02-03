package com.syj.eplus.module.infra.api.sn;

import com.syj.eplus.module.infra.api.sn.dto.SnDTO;
import com.syj.eplus.module.infra.converter.SnConverter;
import com.syj.eplus.module.infra.dal.dataobject.sn.SnDO;
import com.syj.eplus.module.infra.service.sn.SnService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;

/**
 * 序列号 API 实现类
 */
@Service
@Validated
public class SnApiImpl implements SnApi {

    @Resource
    private SnService snService;

    @Override
    public SnDTO getAndIncrementSn(String type, String codePrefix) {
        SnDO snDO = snService.getAndIncrementSn(type, codePrefix);
        return SnConverter.INSTANCE.convertToDTO(snDO);
    }
} 