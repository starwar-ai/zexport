package com.syj.eplus.module.crm.service.custSettlement;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import com.syj.eplus.module.infra.api.settlement.SettlementApi;
import com.syj.eplus.module.infra.api.settlement.dto.SettlementDTO;
import com.syj.eplus.module.crm.controller.admin.custsettlement.vo.CustSettlementPageReqVO;
import com.syj.eplus.module.crm.controller.admin.custsettlement.vo.CustSettlementSaveReqVO;
import com.syj.eplus.module.crm.dal.dataobject.custsettlement.CustSettlementDO;
import com.syj.eplus.module.crm.dal.mysql.custsettlement.CustSettlementMapper;
import com.syj.eplus.module.crm.service.custSettlement.dto.CustSettlementDTO;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.crm.enums.ErrorCodeConstants.CUST_SETTLEMENT_NOT_EXISTS;

/**
 * 客户结汇方式 Service 实现类
 *
 * @author eplus
 */
@Service
@Validated
public class CustSettlementServiceImpl implements CustSettlementService {

    @Resource
    private CustSettlementMapper custSettlementMapper;

    @Resource
    private SettlementApi settlementApi;

    @Override
    public Long createCustSettlement(CustSettlementSaveReqVO createReqVO) {
        // 插入
        CustSettlementDO custSettlement = BeanUtils.toBean(createReqVO, CustSettlementDO.class);
        custSettlementMapper.insert(custSettlement);
        // 返回
        return custSettlement.getId();
    }

    @Override
    public void updateCustSettlement(CustSettlementSaveReqVO updateReqVO) {
        // 校验存在
//        validateCustSettlementExists(updateReqVO.getId());
        // 更新
        CustSettlementDO updateObj = BeanUtils.toBean(updateReqVO, CustSettlementDO.class);
        custSettlementMapper.updateById(updateObj);
    }

    @Override
    public void deleteCustSettlement(Long id) {
        // 校验存在
        validateCustSettlementExists(id);
        // 删除
        custSettlementMapper.deleteById(id);
    }

    private void validateCustSettlementExists(Long id) {
        if (custSettlementMapper.selectById(id) == null) {
            throw exception(CUST_SETTLEMENT_NOT_EXISTS);
        }
    }

    @Override
    public CustSettlementDO getCustSettlement(Long id) {
        return custSettlementMapper.selectById(id);
    }

    @Override
    public PageResult<CustSettlementDO> getCustSettlementPage(CustSettlementPageReqVO pageReqVO) {
        return custSettlementMapper.selectPage(pageReqVO);
    }

    @Override
    public void batchSaveCustSettlement(List<CustSettlementDO> custSettlementDOList) {
        custSettlementMapper.insertBatch(custSettlementDOList);
    }

    @Override
    public List<CustSettlementDTO> getSettlementByCustId(Long custId) {
        List<CustSettlementDO> custSettlementDOList = custSettlementMapper.selectList(CustSettlementDO::getCustId, custId);
        if (CollUtil.isEmpty(custSettlementDOList)) {
            return Collections.emptyList();
        }
        Map<Long, Integer> defaultFlagMap = custSettlementDOList.stream().filter(settle-> Objects.nonNull(settle.getSettlementId())).collect(Collectors.toMap(CustSettlementDO::getSettlementId, CustSettlementDO::getDefaultFlag));
        List<SettlementDTO> settlementDTOList = settlementApi.selectSettlementTermById(custSettlementDOList.stream().map(CustSettlementDO::getSettlementId).toList());
        if (CollUtil.isEmpty(settlementDTOList)) {
            //此时结汇方式有可能已经被删除  需考虑是否需要查询逻辑删除的历史数据
            return Collections.emptyList();
        }
        List<CustSettlementDTO> custSettlementDTOList = new LinkedList<>();
        settlementDTOList.forEach(s -> {
            CustSettlementDTO custSettlementDTO = BeanUtils.toBean(s, CustSettlementDTO.class);
            custSettlementDTO.setSettlementId(s.getId());
            custSettlementDTO.setDefaultFlag(defaultFlagMap.get(s.getId()));
            custSettlementDTOList.add(custSettlementDTO);
        });
        return custSettlementDTOList;

    }

    @Override
    public void deleteCustSettlementByCustId(Long custId) {
        custSettlementMapper.delete(CustSettlementDO::getCustId, custId);
    }

}