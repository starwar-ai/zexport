package com.syj.eplus.module.infra.service.settlement;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.infra.api.settlement.dto.SettlementDTO;
import com.syj.eplus.module.infra.api.settlement.dto.SystemCollectionPlanDTO;
import com.syj.eplus.module.infra.controller.admin.settlement.vo.SettlementPageReqVO;
import com.syj.eplus.module.infra.controller.admin.settlement.vo.SettlementRespVO;
import com.syj.eplus.module.infra.controller.admin.settlement.vo.SettlementSaveReqVO;
import com.syj.eplus.module.infra.dal.dataobject.collectionplan.SystemCollectionPlan;
import com.syj.eplus.module.infra.dal.dataobject.settlement.SettlementDO;
import com.syj.eplus.module.infra.dal.mysql.collectionplan.SystemCollectionPlanMapper;
import com.syj.eplus.module.infra.dal.mysql.settlement.SettlementMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.infra.enums.ErrorCodeConstants.SETTLEMENT_NOT_EXISTS;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.SETTLEMENT_RATE_WRONG;

/**
 * 结汇方式 Service 实现类
 *
 * @author eplus
 */
@Service
@Validated
public class SettlementServiceImpl implements SettlementService {

    @Resource
    private SettlementMapper settlementMapper;

    @Resource
    private SystemCollectionPlanMapper systemCollectionPlanMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createSettlement(SettlementSaveReqVO createReqVO) {
        // 插入
        List<SystemCollectionPlan> collectionPlanList = BeanUtils.toBean(createReqVO.getCollectionPlanList(), SystemCollectionPlan.class);
        checkCollectionRatio(collectionPlanList);
        SettlementDO settlement = BeanUtils.toBean(createReqVO, SettlementDO.class);
        settlementMapper.insert(settlement);
        if (CollUtil.isNotEmpty(collectionPlanList)) {
            collectionPlanList.forEach(s -> s.setSettlementId(settlement.getId()));
            systemCollectionPlanMapper.insertBatch(collectionPlanList);
        }
        // 返回
        return settlement.getId();
    }

    private void checkCollectionRatio(List<SystemCollectionPlan> collectionPlanList){
        if(CollUtil.isEmpty(collectionPlanList)){
            throw exception(SETTLEMENT_RATE_WRONG);
        }
        BigDecimal sum = collectionPlanList.stream().map(SystemCollectionPlan::getCollectionRatio).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
        if(!sum.equals(BigDecimal.valueOf(100))){
            throw exception(SETTLEMENT_RATE_WRONG);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSettlement(SettlementSaveReqVO updateReqVO) {
        List<SystemCollectionPlan> collectionPlanList = BeanUtils.toBean(updateReqVO.getCollectionPlanList(), SystemCollectionPlan.class);
        checkCollectionRatio(collectionPlanList);
        // 校验存在
        validateSettlementExists(updateReqVO.getId());
        // 更新
        SettlementDO updateObj = BeanUtils.toBean(updateReqVO, SettlementDO.class);
        settlementMapper.updateById(updateObj);
        systemCollectionPlanMapper.delete(SystemCollectionPlan::getSettlementId, updateReqVO.getId());
        if (CollUtil.isNotEmpty(collectionPlanList)) {
            collectionPlanList.forEach(s->{
                s.setId(null);
                s.setSettlementId(updateObj.getId());
            });
            systemCollectionPlanMapper.insertBatch(collectionPlanList);
        }
    }

    @Override
    public void deleteSettlement(Long id) {
        // 校验存在
        validateSettlementExists(id);
        // 删除
        settlementMapper.deleteById(id);
        systemCollectionPlanMapper.delete(SystemCollectionPlan::getSettlementId, id);
    }

    private void validateSettlementExists(Long id) {
        if (settlementMapper.selectById(id) == null) {
            throw exception(SETTLEMENT_NOT_EXISTS);
        }
    }

    @Override
    public SettlementRespVO getSettlement(Long id) {
        SettlementDO settlementDO = settlementMapper.selectById(id);
        SettlementRespVO result = BeanUtils.toBean(settlementDO, SettlementRespVO.class);
        List<SystemCollectionPlan> systemCollectionPlans = systemCollectionPlanMapper.selectList(SystemCollectionPlan::getSettlementId, id);
        result.setCollectionPlanList(BeanUtils.toBean(systemCollectionPlans, SystemCollectionPlanDTO.class));
        return result;
    }

    @Override
    public PageResult<SettlementDO> getSettlementPage(SettlementPageReqVO pageReqVO) {
        PageResult<SettlementDO> settlementDOPageResult = settlementMapper.selectPage(pageReqVO);
        List<SettlementDO> settlementDOList = settlementDOPageResult.getList();
        if (CollUtil.isNotEmpty(settlementDOList)) {
            List<Long> settlementIdList = settlementDOList.stream().map(SettlementDO::getId).distinct().toList();
            List<SystemCollectionPlan> systemCollectionPlanList = systemCollectionPlanMapper.selectList(new LambdaQueryWrapperX<SystemCollectionPlan>().in(SystemCollectionPlan::getSettlementId, settlementIdList));
            if (CollUtil.isNotEmpty(systemCollectionPlanList)) {
                Map<Long, List<SystemCollectionPlan>> systemCollectionPlanMap = systemCollectionPlanList.stream().collect(Collectors.groupingBy(SystemCollectionPlan::getSettlementId));
                settlementDOList.forEach(s -> {
                    List<SystemCollectionPlan> plans = systemCollectionPlanMap.get(s.getId());
                    s.setCollectionPlanList(BeanUtils.toBean(plans, SystemCollectionPlanDTO.class));
                });
            }
        }
        settlementDOPageResult.setList(settlementDOList);
        return settlementDOPageResult;
    }

    @Override
    public List<SettlementDO> getSettlementList() {
        List<SettlementDO> settlementDOList = settlementMapper.selectList();
        if (CollUtil.isNotEmpty(settlementDOList)) {
            List<Long> settlementIdList = settlementDOList.stream().map(SettlementDO::getId).distinct().toList();
            List<SystemCollectionPlan> systemCollectionPlanList = systemCollectionPlanMapper.selectList(new LambdaQueryWrapperX<SystemCollectionPlan>().in(SystemCollectionPlan::getSettlementId, settlementIdList));
            if (CollUtil.isNotEmpty(systemCollectionPlanList)) {
                Map<Long, List<SystemCollectionPlan>> systemCollectionPlanMap = systemCollectionPlanList.stream().collect(Collectors.groupingBy(SystemCollectionPlan::getSettlementId));
                settlementDOList.forEach(s -> {
                    List<SystemCollectionPlan> plans = systemCollectionPlanMap.get(s.getId());
                    s.setCollectionPlanList(BeanUtils.toBean(plans, SystemCollectionPlanDTO.class));
                });
            }
        }
        return settlementDOList;
    }

    @Override
    public List<SettlementDO> getSettlementListByIdList(List<Long> idList) {
        return settlementMapper.selectBatchIds(idList);
    }

    @Override
    public SettlementDTO getSettlementDTOById(Long id) {
        if (Objects.isNull(id)){
            return null;
        }
        SettlementDO settlementDO = settlementMapper.selectById(id);
        if (Objects.isNull(settlementDO)){
            return null;
        }
        return BeanUtils.toBean(settlementDO, SettlementDTO.class);
    }

    @Override
    public List<SettlementDTO> getSettlementWithCollectionPlansById(List<Long> idList) {
        if (CollUtil.isEmpty(idList)) {
            return Collections.emptyList();
        }
        List<SettlementDO> settlementDOList = settlementMapper.selectBatchIds(idList);
        if (CollUtil.isEmpty(settlementDOList)) {
            return Collections.emptyList();
        }
        
        List<Long> settlementIdList = settlementDOList.stream()
                .map(SettlementDO::getId)
                .distinct()
                .toList();
        List<SystemCollectionPlan> systemCollectionPlanList = systemCollectionPlanMapper.selectList(
                new LambdaQueryWrapperX<SystemCollectionPlan>()
                        .in(SystemCollectionPlan::getSettlementId, settlementIdList));
        
        if (CollUtil.isNotEmpty(systemCollectionPlanList)) {
            Map<Long, List<SystemCollectionPlan>> systemCollectionPlanMap = systemCollectionPlanList.stream()
                    .collect(Collectors.groupingBy(SystemCollectionPlan::getSettlementId));
            settlementDOList.forEach(s -> {
                List<SystemCollectionPlan> plans = systemCollectionPlanMap.get(s.getId());
                s.setCollectionPlanList(BeanUtils.toBean(plans, SystemCollectionPlanDTO.class));
            });
        }
        
        return BeanUtils.toBean(settlementDOList, SettlementDTO.class);
    }

}