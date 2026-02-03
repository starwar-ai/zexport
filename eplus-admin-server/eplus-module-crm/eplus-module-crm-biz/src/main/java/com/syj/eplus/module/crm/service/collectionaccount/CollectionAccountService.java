package com.syj.eplus.module.crm.service.collectionaccount;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.syj.eplus.module.crm.api.cust.dto.CollectionAccountDTO;
import com.syj.eplus.module.crm.controller.admin.collectionaccount.vo.CollectionAccountPageReqVO;
import com.syj.eplus.module.crm.controller.admin.collectionaccount.vo.CollectionAccountRespVO;
import com.syj.eplus.module.crm.controller.admin.collectionaccount.vo.CollectionAccountSaveReqVO;
import com.syj.eplus.module.crm.dal.dataobject.collectionaccount.CollectionAccountDO;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 收款账号 Service 接口
 *
 * @author zhangcm
 */
public interface CollectionAccountService {

    /**
     * 创建收款账号
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createCollectionAccount(@Valid CollectionAccountSaveReqVO createReqVO);

    /**
     * 更新收款账号
     *
     * @param updateReqVO 更新信息
     */
    void updateCollectionAccount(@Valid CollectionAccountSaveReqVO updateReqVO);

    /**
     * 删除收款账号
     *
     * @param id 编号
     */
    void deleteCollectionAccount(Long id);

    /**
     * 获得收款账号
     *
* @param  id 编号 
     * @return 收款账号
     */
CollectionAccountRespVO getCollectionAccount(Long id);

    /**
     * 获得收款账号分页
     *
     * @param pageReqVO 分页查询
     * @return 收款账号分页
     */
    PageResult<CollectionAccountDO> getCollectionAccountPage(CollectionAccountPageReqVO pageReqVO);

    void batchInsert(List<CollectionAccountDO> collectionAccountList);

    void deleteCollectionAccountByCustCode(Long custId);

    List<CollectionAccountRespVO> getCollectionAccountListByCustCode(Long custId);

    Map<String, CollectionAccountDTO> getMapByCodeList(List<String> codeList,Long companyId);

    CollectionAccountRespVO getCollectionAccountByCust(String custCode, Long companyId);
}