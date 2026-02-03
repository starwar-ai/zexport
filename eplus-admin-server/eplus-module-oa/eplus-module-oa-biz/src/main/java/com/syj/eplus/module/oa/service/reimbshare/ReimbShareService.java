package com.syj.eplus.module.oa.service.reimbshare;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.syj.eplus.framework.common.enums.FeeShareSourceTypeEnum;
import com.syj.eplus.module.oa.controller.admin.reimbshare.vo.ReimbSharePageReqVO;
import com.syj.eplus.module.oa.controller.admin.reimbshare.vo.ReimbShareRespVO;
import com.syj.eplus.module.oa.controller.admin.reimbshare.vo.ReimbShareSaveReqVO;
import com.syj.eplus.module.oa.dal.dataobject.reimbshare.ReimbShareDO;

import javax.validation.Valid;
import java.util.List;

/**
 * 报销分摊 Service 接口
 *
 * @author ePlus
 */
public interface ReimbShareService {

    /**
     * 创建报销分摊
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createReimbShare(@Valid ReimbShareSaveReqVO createReqVO);

    /**
     * 更新报销分摊
     *
     * @param updateReqVO 更新信息
     */
    void updateReimbShare(@Valid ReimbShareSaveReqVO updateReqVO);

    /**
     * 删除报销分摊
     *
     * @param id 编号
     */
    void deleteReimbShare(Long id);

    /**
     * 获得报销分摊
     *
* @param  id 编号 
     * @return 报销分摊
     */
ReimbShareRespVO getReimbShare(Long id);

    /**
     * 获得报销分摊分页
     *
     * @param pageReqVO 分页查询
     * @return 报销分摊分页
     */
    PageResult<ReimbShareDO> getReimbSharePage(ReimbSharePageReqVO pageReqVO);


    /**
     * 根据报销id获取费用归属列表
     * @param reimbId 报销id
     * @return 费用归属列表
     */
    List<ReimbShareRespVO> getReimbShareByReimbId(Long reimbId, Integer auxiliaryType);

    void batchSaveReimbShare(List<ReimbShareSaveReqVO> shareSaveReqVOList);

    /**
     * 删除报销分摊
     *
     * @param reimbId 报销编号
     */
    void deleteReimbShareByReimbId(Long reimbId, FeeShareSourceTypeEnum feeShareSourceTypeEnum);

    /**
     * 更新报销分摊
     *
     * @param updateReqVO 更新信息
     */
    void batchUpdateReimbShare( List<ReimbShareSaveReqVO> updateReqVO);

    /**
     * 处理报销分摊
     * @param auxiliaryList
     */
    void dealReimbShare(List<ReimbShareSaveReqVO> auxiliaryList,Long reimbId);
}