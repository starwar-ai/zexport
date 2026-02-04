package com.syj.eplus.module.pms.listener.sku;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEvent;
import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEventListener;
import cn.iocoder.yudao.module.bpm.api.task.BpmProcessInstanceApi;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.module.pms.dal.dataobject.sku.SkuDO;
import com.syj.eplus.module.pms.dal.mysql.sku.SkuMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: du
 * @Date: 2024/08/16/18:09
 * @Description:
 */
@Component
public class TestListener extends BpmProcessInstanceResultEventListener {

    @Resource
    private BpmProcessInstanceApi bpmProcessInstanceApi;

    @Resource
    private SkuMapper skuMapper;

    @Override
    protected String getProcessDefinitionKey() {
        return "oa_test";
    }

    @Override
    protected void onEvent(BpmProcessInstanceResultEvent event) {
        Map<String, Object> variables = bpmProcessInstanceApi.getVariablesByProcessInstanceId(event.getId());
        if (CollUtil.isEmpty(variables)) {
            return;
        }
        Object amount = variables.get("Fi2blzwb77rfabc");
        Object currency = variables.get("Frvjlzwnts8cahc");
        String skuCode = event.getBusinessKey();
        if (Objects.isNull(amount) || Objects.isNull(currency) || Objects.isNull(skuCode)) {
            return;
        }
        JsonAmount jsonAmount = new JsonAmount().setAmount(new BigDecimal((String) amount)).setCurrency((String) currency);
        SkuDO skuDO = new SkuDO();
        skuDO.setCompanyPrice(jsonAmount);
        skuMapper.update(skuDO, new LambdaQueryWrapperX<SkuDO>().eq(SkuDO::getCode, skuCode));
    }
}
