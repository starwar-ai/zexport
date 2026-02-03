package com.syj.eplus.module.infra.api;

public interface CodeGeneratorApi {
    String getCodeGenerator(String type, String codePrefix);

    String getCodeGenerator(String type, String codePrefix,boolean timeFlag,Integer length);
    String getUserCodeGenerator();
}
