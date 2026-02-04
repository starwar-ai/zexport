package com.syj.eplus.module.pms.service.packagetype;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.bpm.api.task.BpmProcessInstanceApi;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import cn.iocoder.yudao.module.bpm.enums.task.BpmProcessInstanceResultEnum;
import com.syj.eplus.framework.common.enums.SubmitFlagEnum;
import com.syj.eplus.module.infra.api.CodeGeneratorApi;
import com.syj.eplus.module.pms.controller.admin.packagetype.vo.*;
import com.syj.eplus.module.pms.convert.packagetype.PackageTypeConvert;
import com.syj.eplus.module.pms.dal.dataobject.packagetype.PackageTypeDO;
import com.syj.eplus.module.pms.dal.mysql.packagetype.PackageTypeMapper;
import com.syj.eplus.module.scm.api.quoteitem.QuoteitemApi;
import com.syj.eplus.module.scm.api.quoteitem.dto.QuoteitemDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.pms.enums.ErrorCodeConstants.PACKAGE_TYPE_IN_USED;
import static com.syj.eplus.module.pms.enums.ErrorCodeConstants.PACKAGE_TYPE_NOT_EXISTS;

/**
 * 包装方式 Service 实现类
 *
 * @author zhangcm
 */
@Service
@Validated
public class PackageTypeServiceImpl implements PackageTypeService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private PackageTypeMapper packageTypeMapper;
    @Resource
    private CodeGeneratorApi codeGeneratorApi;
    @Resource
    private QuoteitemApi quoteItemApi;

    @Resource
    private BpmProcessInstanceApi bpmProcessInstanceApi;
    private static final String SN_TYPE = "pmsPackageType";
    private static final String CODE_PREFIX = "PPT";
    private static final String PROCESS_DEFINITION_KEY = "pms_package_type";


    @Override
    public Long createPackageType(PackageTypeSaveReqVO createReqVO) {
        PackageTypeDO packageType = PackageTypeConvert.INSTANCE.convertPackageTypeDO(createReqVO);
// 生成 包装方式 编号
        packageType.setCode(codeGeneratorApi.getCodeGenerator(SN_TYPE, CODE_PREFIX));
        // 插入
        packageTypeMapper.insert(packageType);
        if (SubmitFlagEnum.SUBMIT.getStatus().equals(createReqVO.getSubmitFlag())) {
            submitTask(packageType.getId(), WebFrameworkUtils.getLoginUserId());
        }
        // 返回
        return packageType.getId();
    }

    @Override
    public void updatePackageType(PackageTypeSaveReqVO updateReqVO) {
        // 校验存在
        validatePackageTypeExists(updateReqVO.getId());
        // 更新
        PackageTypeDO updateObj = PackageTypeConvert.INSTANCE.convertPackageTypeDO(updateReqVO);
        packageTypeMapper.updateById(updateObj);
    }

    @Override
    public void deletePackageType(Long id) {
        // 校验存在
        validatePackageTypeExists(id);
        List<QuoteitemDTO>  QuoteitemDTOList = quoteItemApi.getQuoteitemDTOByPackageTypeId(id);
        if(QuoteitemDTOList!=null && !QuoteitemDTOList.isEmpty()){
            throw exception(PACKAGE_TYPE_IN_USED);
        }
        // 删除
        packageTypeMapper.deleteById(id);
    }

    private void validatePackageTypeExists(Long id) {
        if (packageTypeMapper.selectById(id) == null) {
            throw exception(PACKAGE_TYPE_NOT_EXISTS);
        }
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
    public void submitTask(Long packageTypeId, Long userId) {
        bpmProcessInstanceApi.createProcessInstance(userId, PROCESS_DEFINITION_KEY, packageTypeId);
        updateAuditStatus(packageTypeId, BpmProcessInstanceResultEnum.PROCESS.getResult());
    }

    @Override
    public void updateAuditStatus(Long auditableId, Integer auditStatus) {
//        packageTypeMapper.updateById(PackageTypeDO.builder().id(auditableId).auditStatus(auditStatus).build());
    }

    @Override
    public List<PackageTypeDO> getList(PackageTypeSimplePageReqVO pageReqVO) {
        if(Objects.nonNull(pageReqVO.getName())){
            return packageTypeMapper.selectList(new LambdaQueryWrapperX<PackageTypeDO>().likeIfPresent(PackageTypeDO::getName,pageReqVO.getName()));
        }
        return packageTypeMapper.selectList();
    }

    @Override
    public PackageTypeDO getById(Long id) {
        return packageTypeMapper.selectById(id);
    }

    @Override
    public List<PackageTypeDO> getByIdList(List<Long> packageIdList) {
        return packageTypeMapper.selectList(PackageTypeDO::getId,packageIdList);
    }

    @Override
    public Map<Long, String> getAllNameCache() {
        List<PackageTypeDO> packageTypeDOS = packageTypeMapper.selectList();
        if (CollUtil.isNotEmpty(packageTypeDOS)) {
            return packageTypeDOS.stream().collect(Collectors.toMap(PackageTypeDO::getId, PackageTypeDO::getName));
        }
        return Map.of();
    }

    @Override
    public String getProcessDefinitionKey() {
        return PROCESS_DEFINITION_KEY;
    }
    @Override
    public PackageTypeRespVO getPackageType( PackageTypeDetailReq packageTypeDetailReq) {
        Long packageTypeId = Objects.nonNull(packageTypeDetailReq.getProcessInstanceId()) ? bpmProcessInstanceApi.selectAuditAbleIdByProcessInstanceId(packageTypeDetailReq.getProcessInstanceId()) : packageTypeDetailReq.getPackageTypeId();
        if (Objects.isNull(packageTypeId)) {
            logger.error("[包装方式]未获取到包装方式id");
            return null;
        }
        PackageTypeDO packageTypeDO = packageTypeMapper.selectById(packageTypeId);
        if (packageTypeDO == null) {
            return null;
        }
        return PackageTypeConvert.INSTANCE.convertPackageTypeRespVO(packageTypeDO);
    }

    @Override
    public PageResult<PackageTypeDO> getPackageTypePage(PackageTypePageReqVO pageReqVO) {
        return packageTypeMapper.selectPage(pageReqVO);
    }

}