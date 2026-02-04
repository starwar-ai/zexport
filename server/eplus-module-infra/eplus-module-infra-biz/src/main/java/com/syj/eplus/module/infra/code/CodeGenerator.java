package com.syj.eplus.module.infra.code;

import java.util.List;

public class CodeGenerator {


    private final List<CodeRule> rules;

    public CodeGenerator(List<CodeRule> rules) {
        this.rules = rules;
    }

    public String execute(Integer length) {
        // 优化：使用StringBuilder替代StringBuffer（单线程场景）
        StringBuilder sb = new StringBuilder();
        for (CodeRule rule : rules) {
            sb.append(rule.execute(length));
        }
        return sb.toString();
    }


}
