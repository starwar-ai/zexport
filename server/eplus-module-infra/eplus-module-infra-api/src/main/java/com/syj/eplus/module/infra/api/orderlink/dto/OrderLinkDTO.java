package com.syj.eplus.module.infra.api.orderlink.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Accessors(chain = true)
public class OrderLinkDTO {
    /**
     * 主键
     */
    private Long id;

    /**
     * 业务主键
     */
    private Long businessId;

    /**
     * 业务编号
     */
    private String code;

    /**
     * 业务名称
     */
    private String name;

    /**
     * 业务类型
     */
    private Integer type;

    /**
     * 链路编号
     */
    private String linkCode;

    /**
     * 子项
     */
    private Object item;

    /**
     * 父节点编号
     */
    private String parentCode;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 主体编号
     */
    private Long companyId;

    /**
     * 主体名称
     */
    private String companyName;

    /**
     * 业务对象名称
     */
    private String businessSubjectName;

    /**
     * 新编号
     */
    private String newCode;
    /**
     * 子节点
     */
    private List<OrderLinkDTO> children;

    /**
     * 订单信息
     */
    private Object orderMsg;
    public OrderLinkDTO( String code, String name, String linkCode, Object item, String parentCode,Integer status,Object orderMsg) {
        this.code = code;
        this.name = name;
        this.linkCode = linkCode;
        this.item = item;
        this.parentCode = parentCode;
        this.status = status;
        this.orderMsg = orderMsg;
    }
}
