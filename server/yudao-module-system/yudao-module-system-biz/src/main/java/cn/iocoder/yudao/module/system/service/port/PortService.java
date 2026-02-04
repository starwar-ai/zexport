package cn.iocoder.yudao.module.system.service.port;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.system.controller.admin.port.vo.PortPageReqVO;
import cn.iocoder.yudao.module.system.controller.admin.port.vo.PortRespVO;
import cn.iocoder.yudao.module.system.controller.admin.port.vo.PortSaveReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.port.PortDO;

import javax.validation.Valid;

/**
 * 口岸 Service 接口
 *
 * @author ePlus
 */
public interface PortService {

    /**
     * 创建口岸
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createPort(@Valid PortSaveReqVO createReqVO);

    /**
     * 更新口岸
     *
     * @param updateReqVO 更新信息
     */
    void updatePort(@Valid PortSaveReqVO updateReqVO);

    /**
     * 删除口岸
     *
     * @param id 编号
     */
    void deletePort(Long id);

    /**
     * 获得口岸
     *
     * @param id 编号
     * @return 口岸
     */
    PortDO getPort(Long id);

    /**
     * 获得口岸分页
     *
     * @param pageReqVO 分页查询
     * @return 口岸分页
     */
    PageResult<PortRespVO> getPortPage(PortPageReqVO pageReqVO);

    Boolean topPort(Long id);

    Boolean rollbackTopPort(Long id);
}