package com.syj.eplus.module.infra.api.settlement;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import com.syj.eplus.module.infra.api.settlement.dto.SettlementDTO;
import com.syj.eplus.module.infra.dal.dataobject.settlement.SettlementDO;
import com.syj.eplus.module.infra.service.settlement.SettlementService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * @Description：
 * @Author：du
 * @Date：2024/1/25 11:07
 */
@Service
public class SettlementApiImpl implements SettlementApi {

    @Resource
    private SettlementService paymentTermService;

    @Override
    public List<SettlementDTO> selectSettlementTermById(List<Long> idList) {
        List<SettlementDO> settlementDOList = paymentTermService.getSettlementListByIdList(idList);
        if (CollUtil.isEmpty(settlementDOList)) {
            return Collections.emptyList();
        }
        return BeanUtils.toBean(settlementDOList, SettlementDTO.class);
    }

    @Override
    public SettlementDTO getSettlementDTOById(Long id) {
        return paymentTermService.getSettlementDTOById(id);
    }

    @Override
    public List<SettlementDTO> getSettlementWithCollectionPlansById(List<Long> idList) {
        return paymentTermService.getSettlementWithCollectionPlansById(idList);
    }
}
