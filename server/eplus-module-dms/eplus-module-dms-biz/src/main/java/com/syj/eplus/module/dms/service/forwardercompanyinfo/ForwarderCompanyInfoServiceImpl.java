package com.syj.eplus.module.dms.service.forwardercompanyinfo;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.module.dms.controller.admin.forwardercompanyinfo.vo.ForwarderCompanyInfoPageReqVO;
import com.syj.eplus.module.dms.controller.admin.forwardercompanyinfo.vo.ForwarderCompanyInfoRespVO;
import com.syj.eplus.module.dms.controller.admin.forwardercompanyinfo.vo.ForwarderCompanyInfoSaveReqVO;
import com.syj.eplus.module.dms.controller.admin.forwardercompanyinfo.vo.SimpleForwarderCompanyInfoPageReqVO;
import com.syj.eplus.module.dms.convert.forwardercompanyinfo.ForwarderCompanyInfoConvert;
import com.syj.eplus.module.dms.dal.dataobject.forwardercompanyinfo.ForwarderCompanyInfoDO;
import com.syj.eplus.module.dms.dal.mysql.forwardercompanyinfo.ForwarderCompanyInfoMapper;
import com.syj.eplus.module.infra.api.CodeGeneratorApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.dms.enums.ErrorCodeConstants.FORWARDER_COMPANY_INFO_NOT_EXISTS;

/**
 * 船代公司 Service 实现类
 *
 * @author du
 */
@Service
@Validated
public class ForwarderCompanyInfoServiceImpl implements ForwarderCompanyInfoService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private ForwarderCompanyInfoMapper forwarderCompanyInfoMapper;
    @Resource
    private CodeGeneratorApi codeGeneratorApi;
    @Resource
    private AdminUserApi adminUserApi;
    private static final String SN_TYPE = "DMS_FCI";
    private static final String CODE_PREFIX = "DMS";


    @Override
    public Long createForwarderCompanyInfo(ForwarderCompanyInfoSaveReqVO createReqVO) {
        ForwarderCompanyInfoDO forwarderCompanyInfo = ForwarderCompanyInfoConvert.INSTANCE.convertForwarderCompanyInfoDO(createReqVO);
        // 生成 船代公司 编号
        forwarderCompanyInfo.setCode(codeGeneratorApi.getCodeGenerator(SN_TYPE, CODE_PREFIX));
        // 前端未传录入人则默认当前登录用户
        Long loginUserId = WebFrameworkUtils.getLoginUserId();
        UserDept inputUser = adminUserApi.getUserDeptByUserId(loginUserId);
        forwarderCompanyInfo.setInputUser(inputUser);
        // 插入
        forwarderCompanyInfoMapper.insert(forwarderCompanyInfo);
        // 返回
        return forwarderCompanyInfo.getId();
    }

    @Override
    public void updateForwarderCompanyInfo(ForwarderCompanyInfoSaveReqVO updateReqVO) {
        // 校验存在
        validateForwarderCompanyInfoExists(updateReqVO.getId());
        // 更新
        ForwarderCompanyInfoDO updateObj = ForwarderCompanyInfoConvert.INSTANCE.convertForwarderCompanyInfoDO(updateReqVO);
        forwarderCompanyInfoMapper.updateById(updateObj);
    }

    @Override
    public void deleteForwarderCompanyInfo(Long id) {
        // 校验存在
        validateForwarderCompanyInfoExists(id);
        // 删除
        forwarderCompanyInfoMapper.deleteById(id);
    }

    private void validateForwarderCompanyInfoExists(Long id) {
        if (forwarderCompanyInfoMapper.selectById(id) == null) {
            throw exception(FORWARDER_COMPANY_INFO_NOT_EXISTS);
        }
    }

    @Override
    public ForwarderCompanyInfoRespVO getForwarderCompanyInfo(Long id) {
        ForwarderCompanyInfoDO forwarderCompanyInfoDO = forwarderCompanyInfoMapper.selectById(id);
        if (forwarderCompanyInfoDO == null) {
            return null;
        }
        return ForwarderCompanyInfoConvert.INSTANCE.convertForwarderCompanyInfoRespVO(forwarderCompanyInfoDO);
    }

    @Override
    public PageResult<ForwarderCompanyInfoDO> getForwarderCompanyInfoPage(ForwarderCompanyInfoPageReqVO pageReqVO) {
        return forwarderCompanyInfoMapper.selectPage(pageReqVO);
    }

    @Override
    public PageResult<ForwarderCompanyInfoDO> getSimpleForwarderCompanyInfoPage(SimpleForwarderCompanyInfoPageReqVO pageReqVO) {
        return forwarderCompanyInfoMapper.selectPageSimple(pageReqVO);
    }

}