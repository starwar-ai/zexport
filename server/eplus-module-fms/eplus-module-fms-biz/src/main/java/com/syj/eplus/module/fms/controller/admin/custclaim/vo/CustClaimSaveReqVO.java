package com.syj.eplus.module.fms.controller.admin.custclaim.vo;

import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.module.fms.dal.dataobject.custclaim.CustClaimItem;
import com.syj.eplus.module.fms.dal.dataobject.custclaim.PayeeEntity;
import lombok.Data;

import java.util.List;

/**
 * @Description：
 * @Author：du
 * @Date：2024/7/25 15:50
 */
@Data
public class CustClaimSaveReqVO {

    /**
     * 登记单id
     */
    private Long id;

    /**
     * 内部法人单位
     */
    private Long companyId;
    /**
     * 登记人
     */
    private UserDept registeredBy;
    /**
     * 公司抬
     */
    private String companyTitle;

    /**
     * 收款对象信息
     */
    private List<PayeeEntity> payeeEntityList;

    /**
     * 认领明细
     */
    private List<CustClaimItem> custClaimItemList;
}
