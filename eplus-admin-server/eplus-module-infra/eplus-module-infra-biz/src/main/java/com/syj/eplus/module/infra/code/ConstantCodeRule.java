package com.syj.eplus.module.infra.code;

public class ConstantCodeRule extends CodeRule {

    private final String code;

    public ConstantCodeRule(String code) {
        this.code = code;
    }

    @Override
    public String execute(Integer length) {
        return code;
    }
}
