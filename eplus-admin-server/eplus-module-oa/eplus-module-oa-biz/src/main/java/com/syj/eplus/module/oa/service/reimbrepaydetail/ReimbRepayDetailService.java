package com.syj.eplus.module.oa.service.reimbrepaydetail;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.syj.eplus.module.oa.controller.admin.reimbrepaydetail.vo.ReimbRepayDetailPageReqVO;
import com.syj.eplus.module.oa.controller.admin.reimbrepaydetail.vo.ReimbRepayDetailRespVO;
import com.syj.eplus.module.oa.controller.admin.reimbrepaydetail.vo.ReimbRepayDetailSaveReqVO;
import com.syj.eplus.module.oa.dal.dataobject.reimbrepaydetail.ReimbRepayDetailDO;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 报销还款详情 Service 接口
 *
 * @author ePlus
 */
public interface ReimbRepayDetailService {

    /**
     * 创建报销还款详情
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createReimbRepayDetail(@Valid ReimbRepayDetailSaveReqVO createReqVO);

    /**
     * 批量新增报销还款详情
     *
     * @param createReqVOList 创建信息列表
     */
    void batchCreateReimbRepayDetail(List<ReimbRepayDetailSaveReqVO> createReqVOList);

    /**
     * 更新报销还款详情
     *
     * @param updateReqVO 更新信息
     */
    void updateReimbRepayDetail(@Valid ReimbRepayDetailSaveReqVO updateReqVO);

    /**
     * 删除报销还款详情
     *
     * @param id 编号
     */
    void deleteReimbRepayDetail(Long id);

    /**
     * 获得报销还款详情
     *
     * @param id 编号
     * @return 报销还款详情
     */
    ReimbRepayDetailRespVO getReimbRepayDetail(Long id);

    /**
     * 获得报销还款详情分页
     *
     * @param pageReqVO 分页查询
     * @return 报销还款详情分页
     */
    PageResult<ReimbRepayDetailDO> getReimbRepayDetailPage(ReimbRepayDetailPageReqVO pageReqVO);

    /**
     * 返回未还款及已取消的列表
     * 还款状态三种分别对应场景:
     * 1-未还款. 此时还款人已发起流程但财务还未审核，此时的还款金额应该计入报销坏款中
     * 2-已还款. 此时还款人已发起流程且财务已审核，此时的还款金额已经同步更新对应借款单的剩余还款金额
     * 3-已取消. 此时还款人已发起流程但财务已驳回，此时的还款金额不应该出现在报销计算中
     *
     * @param ids
     * @return
     */
    Map<Long, ReimbRepayDetailRespVO> getReimbRepayDetailMap(List<Long> ids, Long travelReimbId);

    /**
     * 根据借款单id获取还款详情(不包含已取消状态)
     *
     * @param id 借款单id
     * @return 还款详情list
     *
     */
    List<ReimbRepayDetailDO> getReimbRepayDetailListWithOutCancel(Long id);

    /**
     * 根据报销单id获取报销还款详情列表
     *
     * @param id 报销单id
     * @return 还款详情列表
     */
    List<ReimbRepayDetailDO> getReimbRepayDetailListByReimbId(Long id);

    /**
     * 根据报销id批量删除报销还款详情
     *
     * @param reimbId 报销id
     */
    void batchDeleteReimbRepayDetailByReimbId(Long reimbId);

    /**
     * 批量获取报销还款详情
     * @param reimbIdList 报销单id列表
     * @return 报销还款详情列表
     */
    Map<Long, List<ReimbRepayDetailDO>> batchGetReimbRepayDetailMap(Collection<Long> reimbIdList);

    void batchUpdateReimbRepayDetail(List<ReimbRepayDetailDO> reimbRepayDetailDOList);

}