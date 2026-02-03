package cn.iocoder.yudao.module.system.api.port;

import cn.iocoder.yudao.module.system.service.port.PortService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Description：
 * @Author：du
 * @Date：2024/3/20 11:01
 */
@Service
public class PortApiImpl implements PortApi {

    @Resource
    private PortService portService;
//    @Override
//    public Map<Long, PortDO> getPortMap(PortDO PortDO) {
//        List<PortDO> postList = portService.getPortPage(portDO);
//        return CollectionUtils.convertMap(postList, PortDO::getId);
//    }


}
