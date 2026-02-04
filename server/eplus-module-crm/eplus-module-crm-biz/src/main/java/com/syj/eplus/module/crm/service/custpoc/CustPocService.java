package com.syj.eplus.module.crm.service.custpoc;


import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.syj.eplus.module.crm.controller.admin.custpoc.vo.CustPocPageReqVO;
import com.syj.eplus.module.crm.controller.admin.custpoc.vo.CustPocRespVO;
import com.syj.eplus.module.crm.controller.admin.custpoc.vo.CustPocSaveReqVO;
import com.syj.eplus.module.crm.dal.dataobject.custpoc.CustPocDO;

import javax.validation.Valid;
import java.util.List;

/**
 * 客户联系人 Service 接口
 *
 * @author du
 */
public interface CustPocService {

    /**
     * 创建客户联系人
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createCustPoc(@Valid CustPocSaveReqVO createReqVO);

    /**
     * 更新客户联系人
     *
     * @param updateReqVO 更新信息
     */
    void updateCustPoc(@Valid CustPocSaveReqVO updateReqVO);

    /**
     * 删除客户联系人
     *
     * @param id 编号
     */
    void deleteCustPoc(Long id);

    /**
     * 根据客户id删除客户联系人
     *
     * @param custId
     */
    void deleteCustPocByCustId(Long custId);

    /**
     * 获得客户联系人
     *
     * @param id 编号
     * @return 客户联系人
     */
    CustPocDO getCustPoc(Long id);

    /**
     * 获得客户联系人分页
     *
     * @param pageReqVO 分页查询
     * @return 客户联系人分页
     */
    PageResult<CustPocDO> getCustPocPage(CustPocPageReqVO pageReqVO);

    /**
     * 批量新增客户联系人信息
     *
     * @param custPocSaveReqVOList
     */
    void batchSavePoc(List<CustPocSaveReqVO> custPocSaveReqVOList);

    /**
     * 通过客户id获得客户联系人信息列表(此时不需要关注版本号，版本变更id必定会变更)
     *
     * @param custId
     * @return
     */
    List<CustPocRespVO> getPocListByCustId(Long custId);


    /**
     * 根据客户编号批量获取客户银行账户信息
     *
     * @param custIdList 客户id列表
     * @return 客户联系人信息
     */
    List<CustPocDO> getPocListByCustIdList(List<Long> custIdList);

    /**
     * 批量删除客户联系人信息
     *
     * @param idList
     */
    void batchDeletePocList(List<Long> idList);
}