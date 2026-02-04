package com.syj.eplus.framework.common.config.handler;

import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.Set;

/**
 * @Description：
 * @Author：du
 * @Date：2024/1/11 16:52
 */
public class JsonIntegerSetTypeHandler extends AbstractJsonTypeHandler<Object> {

    private static final TypeReference<Set<Integer>> TYPE_REFERENCE = new TypeReference<Set<Integer>>() {
    };

    @Override
    protected Object parse(String json) {
        return JsonUtils.parseObject(json, TYPE_REFERENCE);
    }

    @Override
    protected String toJson(Object obj) {
        return JsonUtils.toJsonString(obj);
    }
}
