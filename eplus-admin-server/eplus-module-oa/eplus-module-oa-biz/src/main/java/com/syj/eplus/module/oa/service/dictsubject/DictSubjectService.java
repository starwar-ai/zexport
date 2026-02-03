package com.syj.eplus.module.oa.service.dictsubject;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.syj.eplus.module.oa.controller.admin.dictsubject.vo.DictFeeRespVO;
import com.syj.eplus.module.oa.controller.admin.dictsubject.vo.DictSubjectPageReqVO;
import com.syj.eplus.module.oa.controller.admin.dictsubject.vo.DictSubjectRespVO;
import com.syj.eplus.module.oa.controller.admin.dictsubject.vo.DictSubjectSaveReqVO;
import com.syj.eplus.module.oa.dal.dataobject.dictsubject.DictSubjectDO;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 类别配置 Service 接口
 *
 * @author ePlus
 */
public interface DictSubjectService {

    /**
     * 创建类别配置
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createDictSubject(@Valid DictSubjectSaveReqVO createReqVO);

    /**
     * 更新类别配置
     *
     * @param updateReqVO 更新信息
     */
    void updateDictSubject(@Valid DictSubjectSaveReqVO updateReqVO);

    /**
     * 删除类别配置
     *
     * @param id 编号
     */
    void deleteDictSubject(Long id);

    /**
     * 获得类别配置
     *
     * @param  id 编号
     * @return 类别配置
     */
     DictSubjectRespVO getDictSubject(Long id);

    /**
     * 获得科目
     *
     * @param  systemDictType 字典类型
     * @return 类别配置
     */
    List<DictSubjectDO> getSubject(String systemDictType);

    /**
     * 获得类别配置分页
     *
     * @param pageReqVO 分页查询
     * @return 类别配置分页
     */
    PageResult<DictSubjectRespVO> getDictSubjectPage(DictSubjectPageReqVO pageReqVO);

    List<DictFeeRespVO> getFeeList();

    /**
     * 获取科目缓存
     * @return 科目缓存
     */
    Map<Long,DictSubjectDO> getDictSubjectMap();

}