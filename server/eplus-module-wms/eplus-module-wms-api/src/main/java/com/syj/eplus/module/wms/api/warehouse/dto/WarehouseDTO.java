package com.syj.eplus.module.wms.api.warehouse.dto;

import com.sun.xml.bind.v2.TODO;
import lombok.Data;

import java.util.List;

/**
 * Desc——
 * Create by Rangers at  2024-06-04 15:56
 */
@Data
public class WarehouseDTO {
    /**
     * 主键
     */
    private Long id;
    /**
     * 仓库编码
     */
    private String code;
    /**
     * 仓库名称
     */
    private String name;
    /**
     * 仓库地址
     */
    private String address;
    /**
     * 启用标识  0-否 1-是
     * <p>
     * 枚举 {@link TODO infra_boolean_string 对应的类}
     */
    private Integer enableFlag;
    /**
     * 仓管主键
     */
    private List<Long> managerIds;
    /**
     * 供应仓标识0-否 1-是
     * <p>
     * 枚举 {@link TODO infra_boolean_string 对应的类}
     */
    private Integer venderFlag;
    /**
     * 供应仓类型 1- 在制仓 2-虚拟仓
     * <p>
     */
    private Integer venderWmsType;
    /**
     * 供应商编码
     */
    private String venderCode;
    /**
     * 供应商名称
     */
    private String venderName;
    /**
     * 备注
     */
    private String remark;
}
