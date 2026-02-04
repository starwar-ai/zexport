package com.syj.eplus.module.dtms.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *  设计任务类型（多选）1：亚马逊，2：阿里，3：拍照抠图P图，4：包材设计，5：不干胶设计及打印，6：视频拍摄制作，7：效果图设计，8：样本宣传页
 */
@Getter
@AllArgsConstructor
public enum DesignTypeEnum {

    AMAZON(1,"亚马逊"),
    ALIBABA(2,"阿里"),
    PICTURE(3,"拍照抠图P图"),
    PACKAGING_DESIGN(4,"包材设计"),
    LABEL_PRINT_DESIGN(5,"不干胶设计及打印"),
    VIDEO(6,"视频拍摄制作"),
    RENDERING(7,"效果图设计"),
    SAMPLE(8,"样本宣传页");

    private final Integer value;

    private final String desc;
}
