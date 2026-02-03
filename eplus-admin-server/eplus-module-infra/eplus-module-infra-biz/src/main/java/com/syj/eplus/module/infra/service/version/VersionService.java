package com.syj.eplus.module.infra.service.version;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.syj.eplus.module.infra.controller.admin.version.vo.VersionPageReqVO;
import com.syj.eplus.module.infra.controller.admin.version.vo.VersionRespVO;
import com.syj.eplus.module.infra.controller.admin.version.vo.VersionSaveReqVO;
import com.syj.eplus.module.infra.dal.dataobject.version.VersionDO;

import javax.validation.Valid;

/**
 * 版本记录 Service 接口
 *
 * @author Zhangcm
 */
public interface VersionService {

    /**
     * 创建版本记录
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createVersion(@Valid VersionSaveReqVO createReqVO);

    /**
     * 更新版本记录
     *
     * @param updateReqVO 更新信息
     */
    void updateVersion(@Valid VersionSaveReqVO updateReqVO);

    /**
     * 删除版本记录
     *
     * @param id 编号
     */
    void deleteVersion(Long id);

    /**
     * 获得版本记录
     *
* @param  id 编号 
     * @return 版本记录
     */
VersionRespVO getVersion(Long id);

    /**
     * 获得版本记录分页
     *
     * @param pageReqVO 分页查询
     * @return 版本记录分页
     */
    PageResult<VersionDO> getVersionPage(VersionPageReqVO pageReqVO);

    VersionRespVO getLastVersion();

}