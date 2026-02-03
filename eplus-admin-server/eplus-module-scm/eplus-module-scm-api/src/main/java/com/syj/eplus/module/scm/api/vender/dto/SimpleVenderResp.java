package com.syj.eplus.module.scm.api.vender.dto;

import com.syj.eplus.framework.common.entity.UserDept;
import lombok.Data;

import java.util.List;

/**
 * @Description：
 * @Author：du
 * @Date：2024/7/18 16:31
 */
@Data
public class SimpleVenderResp {

    private Long id;

    private String code;

    private String name;

    private List<UserDept> managerList;

    private List<Long> buyerIds;
}
