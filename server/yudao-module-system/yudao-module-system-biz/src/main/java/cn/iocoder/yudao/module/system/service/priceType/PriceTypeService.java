package cn.iocoder.yudao.module.system.service.priceType;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.system.controller.admin.priceType.vo.PriceTypePageReqVO;
import cn.iocoder.yudao.module.system.controller.admin.priceType.vo.PriceTypeSaveReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.priceType.PriceTypeDO;

import javax.validation.Valid;

/**
 * 价格条款 Service 接口
 *
 * @author ePlus
 */
public interface PriceTypeService {

    /**
     * 创建价格条款
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createPriceType(@Valid PriceTypeSaveReqVO createReqVO);

    /**
     * 更新价格条款
     *
     * @param updateReqVO 更新信息
     */
    void updatePriceType(@Valid PriceTypeSaveReqVO updateReqVO);

    /**
     * 删除价格条款
     *
     * @param id 编号
     */
    void deletePriceType(Long id);

    /**
     * 获得价格条款
     *
     * @param id 编号
     * @return 价格条款
     */
    PriceTypeDO getPriceType(Long id);

    /**
     * 获得价格条款分页
     *
     * @param pageReqVO 分页查询
     * @return 价格条款分页
     */
    PageResult<PriceTypeDO> getPriceTypePage(PriceTypePageReqVO pageReqVO);

}