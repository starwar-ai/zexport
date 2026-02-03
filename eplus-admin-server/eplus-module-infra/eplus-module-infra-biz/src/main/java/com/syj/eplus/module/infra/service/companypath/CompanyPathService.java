package com.syj.eplus.module.infra.service.companypath;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.syj.eplus.module.infra.api.companypath.dto.CompanyPathDTO;
import com.syj.eplus.module.infra.controller.admin.companypath.vo.CompanyPathPageReqVO;
import com.syj.eplus.module.infra.controller.admin.companypath.vo.CompanyPathRespVO;
import com.syj.eplus.module.infra.controller.admin.companypath.vo.CompanyPathSaveReqVO;
import com.syj.eplus.module.infra.dal.dataobject.companypath.CompanyPathDO;
import com.syj.eplus.framework.common.entity.JsonCompanyPath;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 抛砖路径 Service 接口
 *
 * @author du
 */
public interface CompanyPathService {

    /**
     * 创建抛砖路径
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createPath(@Valid CompanyPathSaveReqVO createReqVO);

    /**
     * 更新抛砖路径
     *
     * @param updateReqVO 更新信息
     */
    void updatePath(@Valid CompanyPathSaveReqVO updateReqVO);

    /**
     * 删除抛砖路径
     *
     * @param id 编号
     */
    void deletePath(Long id);

    /**
     * 获得抛砖路径
     *
     * @param id 编号
     * @return 抛砖路径
     */
    CompanyPathRespVO getPath(Long id);

    /**
     * 获得抛砖路径分页
     *
     * @param pageReqVO 分页查询
     * @return 抛砖路径分页
     */
    PageResult<CompanyPathDO> getPathPage(CompanyPathPageReqVO pageReqVO);

    /**
     * 根据路径id获取路径缓存
     *
     * @param idList 路径id列表
     * @return 路径缓存
     */
    Map<Long, CompanyPathDTO> getCompanyPathMap(List<Long> idList);
}
