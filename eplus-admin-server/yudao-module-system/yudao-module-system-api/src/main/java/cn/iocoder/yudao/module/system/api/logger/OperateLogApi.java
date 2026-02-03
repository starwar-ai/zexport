package cn.iocoder.yudao.module.system.api.logger;

import cn.iocoder.yudao.module.system.api.logger.dto.OperateLogCreateReqDTO;
import cn.iocoder.yudao.module.system.api.logger.dto.OperateLogRespDTO;

import javax.validation.Valid;
import java.util.List;

/**
 * 操作日志 API 接口
 *
 * @author 芋道源码
 */
public interface OperateLogApi {

    /**
     * 创建操作日志
     *
     * @param createReqDTO 请求
     */
    void createOperateLog(@Valid OperateLogCreateReqDTO createReqDTO);

    /**
     * 根据链路追踪获取操作日志列表
     *
     * @param key   链路追踪key
     * @param value 链路追踪value
     * @return 操作日志列表
     */
    List<OperateLogRespDTO> getOperateLogRespDTOList(String key, String value);

    /**
     * 批量插入操作日志
     *
     * @param createReqDTOList 请求
     */
    void batchCreateOperateLog(List<OperateLogCreateReqDTO> createReqDTOList);
}
