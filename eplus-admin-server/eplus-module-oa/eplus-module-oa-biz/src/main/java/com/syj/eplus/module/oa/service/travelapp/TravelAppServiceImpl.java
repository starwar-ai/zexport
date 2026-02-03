package com.syj.eplus.module.oa.service.travelapp;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.bpm.api.task.BpmProcessInstanceApi;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import cn.iocoder.yudao.module.bpm.enums.task.BpmProcessInstanceResultEnum;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.syj.eplus.framework.common.enums.FeeShareSourceTypeEnum;
import com.syj.eplus.module.infra.api.CodeGeneratorApi;
import com.syj.eplus.module.oa.api.dto.SimpleTravelAppRespDTO;
import com.syj.eplus.module.oa.api.feeshare.FeeShareApi;
import com.syj.eplus.module.oa.controller.admin.travelapp.vo.SimpleTravelAppPageReqVO;
import com.syj.eplus.module.oa.controller.admin.travelapp.vo.TravelAppPageReqVO;
import com.syj.eplus.module.oa.controller.admin.travelapp.vo.TravelAppSaveReqVO;
import com.syj.eplus.module.oa.converter.TravelAppConvert;
import com.syj.eplus.module.oa.dal.dataobject.travelapp.TravelAppDO;
import com.syj.eplus.module.oa.dal.mysql.travelapp.TravelAppMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.oa.enums.ErrorCodeConstants.TRAVEL_APP_NOT_EXISTS;

/**
 * 出差申请 Service 实现类
 *
 * @author 管理员
 */
@Service
@Validated
public class TravelAppServiceImpl implements TravelAppService {
    public static final String PROCESS_DEFINITION_KEY = "oa_travel";
    @Resource
    private TravelAppMapper travelAppMapper;

    @Resource
    private FeeShareApi feeShareApi;
    @Resource
    private BpmProcessInstanceApi bpmProcessInstanceApi;
    private static final String CODE_PREFIX = "CC";
    private static final String SN_TYPE = "sn_travel";
    @Resource
    private CodeGeneratorApi codeGeneratorApi;

    @Override
    public Long createTravelApp(TravelAppSaveReqVO createReqVO) {
        // 插入
        TravelAppDO travelApp = BeanUtils.toBean(createReqVO, TravelAppDO.class);
        String code = codeGeneratorApi.getCodeGenerator(SN_TYPE, CODE_PREFIX);
        travelApp.setCode(code);
        travelAppMapper.insert(travelApp);
        return travelApp.getId();
    }

    @Override
    public void updateTravelApp(TravelAppSaveReqVO updateReqVO) {
        // 校验存在
        validateTravelAppExists(updateReqVO.getId());
        // 更新
        TravelAppDO updateObj = BeanUtils.toBean(updateReqVO, TravelAppDO.class);
        travelAppMapper.updateById(updateObj);
    }

    @Override
    public void updateTravelAppResult(Long id, Integer result) {
        validateTravelAppExists(id);
        travelAppMapper.updateById(new TravelAppDO().setId(id).setAuditStatus(result));
    }

    @Override
    public void approveTask(Long userId, BpmTaskApproveReqDTO reqVO) {
        bpmProcessInstanceApi.approveTask(userId, BeanUtils.toBean(reqVO, BpmTaskApproveReqDTO.class));

    }

    @Override
    public void rejectTask(Long userId, BpmTaskRejectReqDTO reqVO) {
        bpmProcessInstanceApi.rejectTask(userId, BeanUtils.toBean(reqVO, BpmTaskRejectReqDTO.class));

    }

    @Override
    public void submitTask(Long applyerId, Long userId) {
        bpmProcessInstanceApi.createProcessInstance(userId, PROCESS_DEFINITION_KEY, applyerId);
        updateTravelAppResult(applyerId, BpmProcessInstanceResultEnum.PROCESS.getResult());
    }

    @Override
    public String getProcessDefinitionKey() {
        return PROCESS_DEFINITION_KEY;
    }

    @Override
    public boolean validateTravelAppExistsByWecomId(String wecomId) {
        return travelAppMapper.selectOne(TravelAppDO::getWecomId, wecomId) != null;
    }

    @Override
    public PageResult<SimpleTravelAppRespDTO> getSimpleTravelAppPage(SimpleTravelAppPageReqVO pageReqVO) {
        PageResult<TravelAppDO> travelAppDOPageResult = travelAppMapper.selectSimplePage(pageReqVO);
        return TravelAppConvert.INSTANCE.convertTravelPageResult(travelAppDOPageResult);
    }

    @Override
    public List<SimpleTravelAppRespDTO> getSimpleTravelAppRespDTOList(List<Long> idList) {
        LambdaQueryWrapper<TravelAppDO> queryWrapper = new LambdaQueryWrapperX<TravelAppDO>().select(TravelAppDO::getId, TravelAppDO::getPurpose, TravelAppDO::getStartTime, TravelAppDO::getEndTime, TravelAppDO::getDuration, TravelAppDO::getTransportationType, TravelAppDO::getCompanions)
                .in(TravelAppDO::getId, idList);
        List<TravelAppDO> travelAppDOList = travelAppMapper.selectList(queryWrapper);
        return TravelAppConvert.INSTANCE.convertTravelRespDTOList(travelAppDOList);
    }

    @Override
    public SimpleTravelAppRespDTO getSimpleTravelAppRespDTO(String reimbCode) {
        LambdaQueryWrapper<TravelAppDO> queryWrapper = new LambdaQueryWrapperX<TravelAppDO>().select(TravelAppDO::getId, TravelAppDO::getPurpose, TravelAppDO::getStartTime, TravelAppDO::getEndTime, TravelAppDO::getDuration, TravelAppDO::getTransportationType, TravelAppDO::getCompanions,TravelAppDO::getDest)
                .eq(TravelAppDO::getCode, reimbCode);
        TravelAppDO travelAppDO = travelAppMapper.selectOne(queryWrapper);
        return TravelAppConvert.INSTANCE.convertTravelDTOFromTravelDO(travelAppDO);
    }

    @Override
    public void deleteTravelApp(Long id) {
        // 校验存在
        TravelAppDO travelAppDO = validateTravelAppExists(id);
        //删除费用归集数据
        feeShareApi.deleteByCodes(FeeShareSourceTypeEnum.TRAVEL_APPLY,List.of(travelAppDO.getCode()));
        // 删除
        travelAppMapper.deleteById(id);
    }

    private TravelAppDO validateTravelAppExists(Long id) {
        TravelAppDO travelAppDO = travelAppMapper.selectById(id);
        if (travelAppDO == null) {
            throw exception(TRAVEL_APP_NOT_EXISTS);
        }
        return travelAppDO;
    }

    @Override
    public TravelAppDO getTravelApp(Long id) {
        return travelAppMapper.selectById(id);
    }

    @Override
    public PageResult<TravelAppDO> getTravelAppPage(TravelAppPageReqVO pageReqVO) {
        return travelAppMapper.selectPage(pageReqVO);
    }

}