package cn.iocoder.yudao.module.system.api.logger;

import cn.iocoder.yudao.module.system.api.logger.dto.OperateLogCreateReqDTO;
import cn.iocoder.yudao.module.system.api.logger.dto.OperateLogRespDTO;
import cn.iocoder.yudao.module.system.service.logger.OperateLogService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.List;

/**
 * 操作日志 API 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class OperateLogApiImpl implements OperateLogApi {

    @Resource
    private OperateLogService operateLogService;

    @Override
    public void createOperateLog(OperateLogCreateReqDTO createReqDTO) {
        operateLogService.createOperateLog(createReqDTO);
    }

    @Override
    public List<OperateLogRespDTO> getOperateLogRespDTOList(String key, String value) {
        return operateLogService.getOperateLogRespDTOList(key, value);
    }

    @Override
    public void batchCreateOperateLog(List<OperateLogCreateReqDTO> createReqDTOList) {
        operateLogService.batchCreateOperateLog(createReqDTOList);
    }

}
