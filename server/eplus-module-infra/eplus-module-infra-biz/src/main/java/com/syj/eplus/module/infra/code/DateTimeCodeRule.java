package com.syj.eplus.module.infra.code;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeCodeRule extends CodeRule {

    private final DateTimeFormatter formatter;

    public DateTimeCodeRule() {
        this("yyMM");
    }

    public DateTimeCodeRule(String format) {
        formatter = DateTimeFormatter.ofPattern(format);
    }

    @Override
    public String execute(Integer length) {
        return formatter.format(LocalDateTime.now());
    }
}
