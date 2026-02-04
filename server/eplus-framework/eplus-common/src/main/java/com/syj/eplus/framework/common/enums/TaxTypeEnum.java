//
//package com.syj.eplus.framework.common.enums;
//
//
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//
//@Getter
//@AllArgsConstructor
//public enum TaxTypeEnum {
//    INTERNATIONAL(1, "国际发票"),
//    NORMAL(2, "普通发票"),
//    ZZS13(3, "增值税发票13%"),
//    ZZS16(4, "增值税发票16%"),
//    ZZS19(5, "增值税发票19%");
//    private Integer status;
//
//    private String name;
//
//    public static String getNameByCode(Integer code) {
//        for (TaxTypeEnum convertNoticeFlagEnum : TaxTypeEnum.values()) {
//            if (convertNoticeFlagEnum.getStatus().equals(code)) {
//                return convertNoticeFlagEnum.getName();
//            }
//        }
//        return null;
//    }
//}
