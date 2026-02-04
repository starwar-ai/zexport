package com.syj.eplus.module.infra.api.code;

import cn.iocoder.yudao.framework.common.dict.CommonDict;
import com.syj.eplus.module.infra.api.CodeGeneratorApi;
import com.syj.eplus.module.infra.code.*;
import com.syj.eplus.module.infra.service.sn.SnService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Service
public class CodeGeneratorApiImpl implements CodeGeneratorApi {
    @Resource
    private SnService snService;

    private final Map<String, CodeGenerator> CODE_GENERATOR_CACHE = new ConcurrentHashMap<>();

    @Override
    public String getCodeGenerator(String type, String codePrefix) {
        String cacheKey = type + CommonDict.BASE_SNAKE + codePrefix;
        CodeGenerator codeGenerator = CODE_GENERATOR_CACHE.computeIfAbsent(cacheKey, key ->
            CodeGeneratorBuilder.create()
                    .addRule(new ConstantCodeRule(codePrefix))
                    .addRule(new DateTimeCodeRule())
                    .addRule(new SerialNumberCodeRule(snService, type, codePrefix, 4))
                    .build()
        );
        return codeGenerator.execute(4);
    }

    @Override
    public String getCodeGenerator(String type, String codePrefix, boolean timeFlag, Integer length) {
        String cacheKey = type + CommonDict.BASE_SNAKE + codePrefix + "_" + timeFlag;
        CodeGenerator codeGenerator = CODE_GENERATOR_CACHE.computeIfAbsent(cacheKey, key -> {
            CodeGeneratorBuilder codeGeneratorBuilder = CodeGeneratorBuilder.create()
                    .addRule(new ConstantCodeRule(codePrefix))
                    .addRule(new SerialNumberCodeRule(snService, type, codePrefix, length));
            if (timeFlag) {
                codeGeneratorBuilder.addRule(new DateTimeCodeRule());
            }
            return codeGeneratorBuilder.build();
        });
        return codeGenerator.execute(length);
    }

    @Override
    public String getUserCodeGenerator() {
        CodeGenerator codeGenerator = CODE_GENERATOR_CACHE.computeIfAbsent("u", key ->
            CodeGeneratorBuilder.create()
                    .addRule(new ConstantCodeRule("u"))
                    .addRule(new SerialNumberCodeRule(snService, "user", "u", 4))
                    .build()
        );
        return codeGenerator.execute(4);
    }
}
