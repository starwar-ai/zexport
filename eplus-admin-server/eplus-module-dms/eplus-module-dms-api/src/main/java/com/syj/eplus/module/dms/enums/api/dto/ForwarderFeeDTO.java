package com.syj.eplus.module.dms.enums.api.dto;

import cn.iocoder.yudao.framework.common.pojo.JsonEffectRange;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.UserDept;
import lombok.Data;

import java.util.List;

@Data
public class ForwarderFeeDTO {

    /**
     * 主键
     */
    private Long id;
    /**
     * 出运费用主键
     */
    private Long shipmentId;
    /**
     * 编号
     */
    private String code;
    /**
     * 出运费用编号
     */
    private String shipmentCode;
    /**
     * 发票号
     */
    private String invoiceCode;
    /**
     * 主体主键
     */
    private Long companyId;
    /**
     * 主体名称
     */
    private String companyName;
    /**
     * 序号
     */
    private Integer sortNum;
    /**
     * 供应商主键
     */
    private Long venderId;
    /**
     * 供应商编号
     */
    private String venderCode;
    /**
     * 供应商名称
     */
    private String venderName;
    /**
     * 费用名称主键
     */
    private Long dictSubjectId;
    /**
     * 费用名称
     */
    private String dictSubjectName;
    /**
     * 费用类型
     */
    private Integer feeType;
    /**
     * 金额
     */
    private JsonAmount amount;
    /**
     * 付款状态
     */
    private Integer payStatus;
    /**
     * 申请人
     */
    private UserDept applyer;


    private String codeList;

    private String sourceCode;

    private List<JsonEffectRange> effectRangeList;

    /**
     * 变更标识
     */
    private Integer changeFlag;

    /**
     * 采购员
     */
    private List<UserDept> purchaseUserList;

    /**
     * 业务员
     */
    private List<UserDept> managerList;

    /**
     * 对公申请主键
     */
    private Long paymentAppId;

    /**
     * 对公申请编号
     */
    private String paymentAppCode;
}
