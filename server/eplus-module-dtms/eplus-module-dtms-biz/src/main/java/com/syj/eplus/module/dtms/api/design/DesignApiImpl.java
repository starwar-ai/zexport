package com.syj.eplus.module.dtms.api.design;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import com.syj.eplus.module.dtms.api.design.dto.DesignPageReqDTO;
import com.syj.eplus.module.dtms.api.design.dto.DesignRespDTO;
import com.syj.eplus.module.dtms.controller.admin.design.vo.DesignPageReqVO;
import com.syj.eplus.module.dtms.controller.admin.design.vo.DesignRespVO;
import com.syj.eplus.module.dtms.dal.dataobject.design.DesignDO;
import com.syj.eplus.module.dtms.service.design.DesignService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 设计任务 API 实现类
 *
 * @author du
 */
@Service
public class DesignApiImpl implements DesignApi {

    @Resource
    private DesignService designService;

    @Override
    public PageResult<DesignRespDTO> getDesignPage(DesignPageReqDTO pageReqDTO) {
        // 转换请求DTO为VO
        DesignPageReqVO pageReqVO = BeanUtils.toBean(pageReqDTO, DesignPageReqVO.class);
        // 调用service
        PageResult<DesignRespVO> pageResult = designService.getDesignPage(pageReqVO);
        // 转换结果为DTO
        return BeanUtils.toBean(pageResult, DesignRespDTO.class);
    }

    @Override
    public List<DesignRespDTO> getAllDesignList() {
        List<DesignDO> list = designService.getAllList();
        return BeanUtils.toBean(list, DesignRespDTO.class);
    }
}
