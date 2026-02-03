package com.syj.eplus.module.fms.controller.admin.bankregistration.vo;

import lombok.Data;

import java.util.List;

/**
 * @Description：
 * @Author：du
 * @Date：2024/7/23 21:29
 */
@Data
public class BankRegistrationCreateReq {

    private Integer submitFlag;

    private List<BankRegistrationSaveReqVO> bankRegistrationList;
}
