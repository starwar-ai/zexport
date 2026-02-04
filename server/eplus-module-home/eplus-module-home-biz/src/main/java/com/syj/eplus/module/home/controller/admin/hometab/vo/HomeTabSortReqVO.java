package com.syj.eplus.module.home.controller.admin.hometab.vo;

import lombok.Data;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: du
 * @Date: 2024/08/20/15:59
 * @Description:
 */
@Data
public class HomeTabSortReqVO {

    /**
     * 页签编号列表
     */
    private List<Long> tabIds;
}
