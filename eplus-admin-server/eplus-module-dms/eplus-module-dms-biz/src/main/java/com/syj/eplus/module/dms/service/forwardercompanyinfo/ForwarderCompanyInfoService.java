package com.syj.eplus.module.dms.service.forwardercompanyinfo;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.syj.eplus.module.dms.controller.admin.forwardercompanyinfo.vo.ForwarderCompanyInfoPageReqVO;
import com.syj.eplus.module.dms.controller.admin.forwardercompanyinfo.vo.ForwarderCompanyInfoRespVO;
import com.syj.eplus.module.dms.controller.admin.forwardercompanyinfo.vo.ForwarderCompanyInfoSaveReqVO;
import com.syj.eplus.module.dms.controller.admin.forwardercompanyinfo.vo.SimpleForwarderCompanyInfoPageReqVO;
import com.syj.eplus.module.dms.dal.dataobject.forwardercompanyinfo.ForwarderCompanyInfoDO;

import javax.validation.Valid;

/**
 * 船代公司 Service 接口
 *
 * @author du
 */
public interface ForwarderCompanyInfoService {

    /**
     * 创建船代公司
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createForwarderCompanyInfo(@Valid ForwarderCompanyInfoSaveReqVO createReqVO);

    /**
     * 更新船代公司
     *
     * @param updateReqVO 更新信息
     */
    void updateForwarderCompanyInfo(@Valid ForwarderCompanyInfoSaveReqVO updateReqVO);

    /**
     * 删除船代公司
     *
     * @param id 编号
     */
    void deleteForwarderCompanyInfo(Long id);

    /**
     * 获得船代公司
     *
     * @param id 编号
     * @return 船代公司
     */
    ForwarderCompanyInfoRespVO getForwarderCompanyInfo(Long id);

    /**
     * 获得船代公司分页
     *
     * @param pageReqVO 分页查询
     * @return 船代公司分页
     */
    PageResult<ForwarderCompanyInfoDO> getForwarderCompanyInfoPage(ForwarderCompanyInfoPageReqVO pageReqVO);

    /**
     * 获得船代公司分页精简列表
     *
     * @param pageReqVO 分页查询
     * @return 船代公司分页
     */
    PageResult<ForwarderCompanyInfoDO> getSimpleForwarderCompanyInfoPage(SimpleForwarderCompanyInfoPageReqVO pageReqVO);
}