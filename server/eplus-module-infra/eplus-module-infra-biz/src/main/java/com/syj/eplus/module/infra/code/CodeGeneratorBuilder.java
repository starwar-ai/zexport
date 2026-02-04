package com.syj.eplus.module.infra.code;

import java.util.ArrayList;
import java.util.List;

public class CodeGeneratorBuilder {

    private final List<CodeRule> rules = new ArrayList<>();

    public static CodeGeneratorBuilder create() {
        return new CodeGeneratorBuilder();
    }

    public CodeGeneratorBuilder addRule(CodeRule rule) {
        this.rules.add(rule);
        return this;
    }

    public CodeGenerator build() {
        return new CodeGenerator(rules);
    }
}
