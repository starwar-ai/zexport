package com.syj.eplus.module.oa.service.feeshareitem;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.syj.eplus.module.oa.api.feeshare.dto.FeeShareItemDTO;
import com.syj.eplus.module.oa.controller.admin.feeshareitem.vo.FeeShareItemPageReqVO;
import com.syj.eplus.module.oa.controller.admin.feeshareitem.vo.FeeShareItemRespVO;
import com.syj.eplus.module.oa.controller.admin.feeshareitem.vo.FeeShareItemSaveReqVO;
import com.syj.eplus.module.oa.dal.dataobject.feeshareitem.FeeShareItemDO;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 费用归集明细 Service 接口
 *
 * @author zhangcm
 */
public interface FeeShareItemService {

    /**
     * 创建费用归集明细
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createFeeShareItem(@Valid FeeShareItemSaveReqVO createReqVO);

    /**
     * 更新费用归集明细
     *
     * @param updateReqVO 更新信息
     */
    void updateFeeShareItem(@Valid FeeShareItemSaveReqVO updateReqVO);

    /**
     * 删除费用归集明细
     *
     * @param id 编号
     */
    void deleteFeeShareItem(Long id);

    /**
     * 获得费用归集明细
     *
* @param  id 编号 
     * @return 费用归集明细
     */
FeeShareItemRespVO getFeeShareItem(Long id);

    /**
     * 获得费用归集明细分页
     *
     * @param pageReqVO 分页查询
     * @return 费用归集明细分页
     */
    PageResult<FeeShareItemDO> getFeeShareItemPage(FeeShareItemPageReqVO pageReqVO);

    void createFeeShareItemBatch(List<FeeShareItemDTO> children, Long id);

    void deleteFeeShareItemByFeeShareId(Long oldId);

    List<FeeShareItemDO> getFeeShareItemByShareId(Long shareDOId);

    Map<Long, List<FeeShareItemDO>> getListByFeeShareIdList(List<Long> feeShareIdList);

    void deleteFeeShareItemByFeeShareIdList(List<Long> idList);

    /**
     * 验证客户是否存在
     * @param custCode 客户编号
     * @return 是否存在
     */
    boolean validateCustExists(String custCode);
}