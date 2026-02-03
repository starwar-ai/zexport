package com.syj.eplus.module.crm.api.cust.dto;

import com.syj.eplus.framework.common.entity.UserDept;
import lombok.Data;

import java.util.List;

/**
 * @Description：
 * @Author：du
 * @Date：2024/7/18 15:34
 */
@Data
public class SimpleCustResp {

    private Long id;

    private String code;

    private String name;

    private String shortname;

    private List<Long> managerIds;

    private List<UserDept> managerList;
}
