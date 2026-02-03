package com.syj.eplus.framework.common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class UserDept implements Serializable {
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 用户编号
     */
    private String userCode;
    /**
     * 用户昵称
     */
    private String nickname;
    /**
     * 部门id
     */
    private Long deptId;
    /**
     * 部门编号
     */
    private String deptCode;
    /**
     * 部门名称
     */
    private String deptName;
    /**
     * 默认
     */
    private Integer defaultFlag = 0;
    /**
     * 手机号码
     */
    private String mobile;
    /**
     * 状态
     */
    private Integer status;
}
