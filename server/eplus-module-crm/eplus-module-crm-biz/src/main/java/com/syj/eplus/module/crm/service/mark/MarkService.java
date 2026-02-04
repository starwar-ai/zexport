package com.syj.eplus.module.crm.service.mark;


import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.syj.eplus.module.crm.controller.admin.mark.vo.MarkPageReqVO;
import com.syj.eplus.module.crm.controller.admin.mark.vo.MarkSaveReqVO;
import com.syj.eplus.module.crm.dal.dataobject.mark.MarkDO;

import javax.validation.Valid;
import java.util.List;

/**
 * 唛头 Service 接口
 *
 * @author du
 */
public interface MarkService {

    /**
     * 创建唛头
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createMark(@Valid MarkSaveReqVO createReqVO);

    /**
     * 更新唛头
     *
     * @param updateReqVO 更新信息
     */
    void updateMark(@Valid MarkSaveReqVO updateReqVO);

    /**
     * 删除唛头
     *
     * @param id 编号
     */
    void deleteMark(Long id);

    /**
     * 根据客户id删除唛头
     *
     * @param custId
     */
    void deleteMarkByCustId(Long custId);

    /**
     * 获得唛头
     *
     * @param id 编号
     * @return 唛头
     */
    MarkDO getMark(Long id);

    /**
     * 获得唛头分页
     *
     * @param pageReqVO 分页查询
     * @return 唛头分页
     */
    PageResult<MarkDO> getMarkPage(MarkPageReqVO pageReqVO);

    /**
     * 批量新增客户唛头
     *
     * @param markSaveReqDOList
     */
    void batchSaveMark(List<MarkDO> markSaveReqDOList);

    /**
     * 通过客户id获得客户唛头信息列表(此时不需要关注版本号，版本变更id必定会变更)
     *
     * @param custId
     * @return
     */
    List<MarkDO> getMarkListByCustId(Long custId);

    /**
     * 批量删除唛头信息
     * @param idList
     */
    void batchDeleteMarkList(List<Long> idList);
}