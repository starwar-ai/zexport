package com.syj.eplus.module.system.config;

import com.syj.eplus.module.system.core.ReportApplicationRunner;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class ReportAutoConfiguration {
    @Bean
    public ReportApplicationRunner reportApplicationRunner() {
        return new ReportApplicationRunner();
    }
}
