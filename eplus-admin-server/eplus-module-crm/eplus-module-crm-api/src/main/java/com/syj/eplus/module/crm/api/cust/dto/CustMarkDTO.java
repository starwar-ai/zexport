package com.syj.eplus.module.crm.api.cust.dto;

import lombok.Data;

import java.time.LocalDateTime;


@Data
public class CustMarkDTO {

    //唛头名称
    private String name;


    private Integer ver;

    //唛头英文名称
    private String engName;
    //主文字唛
    private String mainMarkText;

    //主图形唛
    private String mainMarkPic;
    //主侧文字唛
    private String mainMarkTextSide;

    //主侧图形唛
    private String mainMarkPicSide;

    //内主文字唛
    private String mainMarkTextIn;

    //内主图形唛
    private String mainMarkPicIn;

    //内侧文字唛
    private String mainMarkTextSideIn;

    //内侧图形唛
    private String mainMarkPicSideIn;

    private LocalDateTime createTime;

    private Long id;


}