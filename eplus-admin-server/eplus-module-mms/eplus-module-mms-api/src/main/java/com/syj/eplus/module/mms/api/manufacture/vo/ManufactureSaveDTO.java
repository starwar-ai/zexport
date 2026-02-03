package com.syj.eplus.module.mms.api.manufacture.vo;


import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 管理后台 - 加工单新增/修改
 */
@Data
public class ManufactureSaveDTO {

    /**
     * 主键
     */
    private Long id;

    /**
     * 编号
     */
    private String code;

    /**
     * 录入人id
     */
    private Long inputUserId;

    /**
     * 录入人姓名
     */
    private String inputUserName;

    /**
     * 录入时间
     */
    private LocalDateTime inputTime;


    /**
     * 仓库id
     */
    private Long stockId;

    /**
     * 仓库名称
     */
    private String stockName;

    /**
     * 主体id
     */
    private Long companyId;

    /**
     * 主体名称
     */
    private String companyName;

    /**
     * 客户id
     */
    private Long custId;

    /**
     * 客户编号
     */
    private String custCode;

    /**
     * 客户名称
     */
    private String custName;

    /**
     * 加工单状态
     */
    private Integer manufactureStatus;

    /**
     * 是否自动生成
     */
    private Integer autoFlag;

    /**
     * 完成加工时间
     */
    private LocalDateTime doneTime;

    /**
     * 结案时间
     */
    private LocalDateTime finishTime;

    /**
     * 结案原因
     */
    private String finishDesc;

    /**
     * 产品列表
     */
    private List<ManufactureSkuSaveDTO> children;

    /**
     * 销售合同号
     */
    private String saleContractCode;

}
