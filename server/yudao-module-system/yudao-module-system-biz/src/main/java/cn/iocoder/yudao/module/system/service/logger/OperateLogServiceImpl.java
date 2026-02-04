package cn.iocoder.yudao.module.system.service.logger;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.dict.CommonDict;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.common.util.string.StrUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.system.api.logger.dto.OperateLogCreateReqDTO;
import cn.iocoder.yudao.module.system.api.logger.dto.OperateLogRespDTO;
import cn.iocoder.yudao.module.system.controller.admin.logger.vo.operatelog.OperateLogPageReqVO;
import cn.iocoder.yudao.module.system.convert.logger.OperateLogConvert;
import cn.iocoder.yudao.module.system.dal.dataobject.logger.OperateLogDO;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import cn.iocoder.yudao.module.system.dal.mysql.logger.OperateLogMapper;
import cn.iocoder.yudao.module.system.service.user.AdminUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
import static cn.iocoder.yudao.module.system.dal.dataobject.logger.OperateLogDO.JAVA_METHOD_ARGS_MAX_LENGTH;
import static cn.iocoder.yudao.module.system.dal.dataobject.logger.OperateLogDO.RESULT_MAX_LENGTH;

/**
 * 操作日志 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
@Slf4j
public class OperateLogServiceImpl implements OperateLogService {

    @Resource
    private OperateLogMapper operateLogMapper;

    @Resource
    private AdminUserService userService;

    @Override
    public void createOperateLog(OperateLogCreateReqDTO createReqDTO) {
        OperateLogDO log = BeanUtils.toBean(createReqDTO, OperateLogDO.class);
        log.setJavaMethodArgs(StrUtils.maxLength(log.getJavaMethodArgs(), JAVA_METHOD_ARGS_MAX_LENGTH));
        log.setResultData(StrUtils.maxLength(log.getResultData(), RESULT_MAX_LENGTH));
        operateLogMapper.insert(log);
    }

    @Override
    public PageResult<OperateLogDO> getOperateLogPage(OperateLogPageReqVO pageReqVO) {
        // 处理基于用户昵称的查询
        Collection<Long> userIds = null;
        if (StrUtil.isNotEmpty(pageReqVO.getUserNickname())) {
            userIds = convertSet(userService.getUserListByNickname(pageReqVO.getUserNickname()), AdminUserDO::getId);
            if (CollUtil.isEmpty(userIds)) {
                return PageResult.empty();
            }
        }
        // 查询分页
        return operateLogMapper.selectPage(pageReqVO, userIds);
    }

    @Override
    public List<OperateLogRespDTO> getOperateLogRespDTOList(String key, String value) {
        LambdaQueryWrapper<OperateLogDO> queryWrapper = new LambdaQueryWrapperX<OperateLogDO>()
                .ne(OperateLogDO::getExts, CommonDict.EMPTY_STR)
                .isNotNull(OperateLogDO::getExts)
                .apply(String.format("LOCATE('%s', JSON_EXTRACT(exts, '$.%s')) > 0",value,key))
                .orderByDesc(OperateLogDO::getStartTime);
        List<OperateLogDO> operateLogDOList = operateLogMapper.selectList(queryWrapper);
        Map<Long, AdminUserDO> userMap = userService.getUserMap(
                convertList(operateLogDOList, OperateLogDO::getUserId));
        return OperateLogConvert.INSTANCE.convertOperateLogRespDTO(operateLogDOList, userMap);
    }

    @Override
    public void batchCreateOperateLog(List<OperateLogCreateReqDTO> createReqDTOList) {
        createReqDTOList.forEach(s -> {
            s.setJavaMethodArgs(StrUtils.maxLength(s.getJavaMethodArgs(), JAVA_METHOD_ARGS_MAX_LENGTH));
            s.setResultData(StrUtils.maxLength(s.getResultData(), RESULT_MAX_LENGTH));
        });
        List<OperateLogDO> operateLogDOList = BeanUtils.toBean(createReqDTOList, OperateLogDO.class);
        operateLogMapper.insertBatch(operateLogDOList);
    }

}
