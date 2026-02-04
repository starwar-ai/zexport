package com.syj.eplus.module.oa.service.subject;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.syj.eplus.module.oa.controller.admin.subject.vo.SubjectPageReqVO;
import com.syj.eplus.module.oa.controller.admin.subject.vo.SubjectRespVO;
import com.syj.eplus.module.oa.controller.admin.subject.vo.SubjectSaveReqVO;
import com.syj.eplus.module.oa.controller.admin.subject.vo.SubjectSimpleRespVO;
import com.syj.eplus.module.oa.dal.dataobject.subject.SubjectDO;

import javax.validation.Valid;
import java.util.List;

/**
 * 科目 Service 接口
 *
 * @author ePlus
 */
public interface SubjectService {

    /**
     * 创建科目
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createSubject(@Valid SubjectSaveReqVO createReqVO);

    /**
     * 更新科目
     *
     * @param updateReqVO 更新信息
     */
    void updateSubject(@Valid SubjectSaveReqVO updateReqVO);

    /**
     * 删除科目
     *
     * @param id 编号
     */
    void deleteSubject(Long id);

    /**
     * 获得科目
     *
     * @param  id 编号
     * @return 科目
     */
    SubjectRespVO getSubject(Long id);

    /**
     * 获得科目分页
     *
     * @param pageReqVO 分页查询
     * @return 科目分页
     */
    PageResult<SubjectDO> getSubjectPage(SubjectPageReqVO pageReqVO);


    /**
     * 获取科目分类属性结构
     *
     * @return
     */

    List<SubjectSimpleRespVO> getSimpleList();
}