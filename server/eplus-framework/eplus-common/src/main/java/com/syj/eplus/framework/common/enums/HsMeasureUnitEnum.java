package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: du
 * @Date: 2024/07/30/13:39
 * @Description:
 */
@Getter
@AllArgsConstructor
public enum HsMeasureUnitEnum {
    KE("克", "克"),
    ZHU("株", "株"),
    JIA("架", "架"),
    SHENG("升", "升"),
    BA("把", "把"),
    FU("幅", "幅"),
    KUAI("块", "块"),
    ZHI("支", "支"),
    QS("千升", "千升"),
    PFM("平方米", "平方米"),
    ZHANG("张", "张"),
    SOU("艘", "艘"),
    ZUO("座", "座"),
    PAN("盘", "盘"),
    KG("千克", "千克"),
    TIAO("条", "条"),
    SHUANG("双", "双"),
    GEN("根", "根"),
    KWS("千瓦时", "千瓦时"),
    QZ("千支", "千支"),
    M("米", "米"),
    KL("克拉", "克拉"),
    BG("百个", "百个"),
    TAI("台", "台"),
    TAO("套", "套"),
    ZHI2("只", "只"),
    JIAN("件", "件"),
    FU2("副", "副"),
    DUN("吨", "吨"),
    GE("个", "个"),
    LIANNG("辆", "辆"),
    PIAN("片", "片");
    private final String value;
    private final String name;
}