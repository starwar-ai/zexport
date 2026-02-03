package com.syj.eplus.module.infra.service.version;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import com.syj.eplus.module.infra.controller.admin.version.vo.VersionPageReqVO;
import com.syj.eplus.module.infra.controller.admin.version.vo.VersionRespVO;
import com.syj.eplus.module.infra.controller.admin.version.vo.VersionSaveReqVO;
import com.syj.eplus.module.infra.convert.version.VersionConvert;
import com.syj.eplus.module.infra.dal.dataobject.version.VersionDO;
import com.syj.eplus.module.infra.dal.mysql.version.VersionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.infra.enums.ErrorCodeConstants.VERSION_NOT_EXISTS;

/**
 * 版本记录 Service 实现类
 *
 * @author Zhangcm
 */
@Service
@Validated
public class VersionServiceImpl implements VersionService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private VersionMapper versionMapper;


    @Override
    public Long createVersion(VersionSaveReqVO createReqVO) {
        VersionDO version = VersionConvert.INSTANCE.convertVersionDO(createReqVO);
        // 插入
        versionMapper.insert(version);
        // 返回
        return version.getId();
    }

    @Override
    public void updateVersion(VersionSaveReqVO updateReqVO) {
        // 校验存在
        validateVersionExists(updateReqVO.getId());
        // 更新
        VersionDO updateObj = VersionConvert.INSTANCE.convertVersionDO(updateReqVO);
        versionMapper.updateById(updateObj);
    }

    @Override
    public void deleteVersion(Long id) {
        // 校验存在
        validateVersionExists(id);
        // 删除
        versionMapper.deleteById(id);
    }

    private void validateVersionExists(Long id) {
        if (versionMapper.selectById(id) == null) {
            throw exception(VERSION_NOT_EXISTS);
        }
    }
    @Override
    public VersionRespVO getVersion(Long id) {
        VersionDO versionDO = versionMapper.selectById(id);
        if (versionDO == null) {
            return null;
        }
        return VersionConvert.INSTANCE.convertVersionRespVO(versionDO);
    }

    @Override
    public PageResult<VersionDO> getVersionPage(VersionPageReqVO pageReqVO) {
        return versionMapper.selectPage(pageReqVO);
    }

    @Override
    public VersionRespVO getLastVersion() {
        VersionDO latestVersion = versionMapper.selectLatestEnabledVersion();
        if (latestVersion == null) {
            return null;
        }
        return BeanUtils.toBean(latestVersion,VersionRespVO.class)  ;
    }

}