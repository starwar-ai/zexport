package com.syj.eplus.module.scm.service.qualification;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.syj.eplus.module.scm.controller.admin.qualification.vo.QualificationPageReqVO;
import com.syj.eplus.module.scm.controller.admin.qualification.vo.QualificationRespVO;
import com.syj.eplus.module.scm.controller.admin.qualification.vo.QualificationSaveReqVO;
import com.syj.eplus.module.scm.dal.dataobject.qualification.QualificationDO;

import javax.validation.Valid;

/**
 * 资质 Service 接口
 *
 * @author ePlus
 */
public interface QualificationService {

    /**
     * 创建资质
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createQualification(@Valid QualificationSaveReqVO createReqVO);

    /**
     * 更新资质
     *
     * @param updateReqVO 更新信息
     */
    void updateQualification(@Valid QualificationSaveReqVO updateReqVO);

    /**
     * 删除资质
     *
     * @param id 编号
     */
    void deleteQualification(Long id);

    /**
     * 获得资质
     *
* @param  id 编号 
     * @return 资质
     */
QualificationRespVO getQualification(Long id);

    /**
     * 获得资质分页
     *
     * @param pageReqVO 分页查询
     * @return 资质分页
     */
    PageResult<QualificationDO> getQualificationPage(QualificationPageReqVO pageReqVO);
}