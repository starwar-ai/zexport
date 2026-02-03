package com.syj.eplus.framework.common.config.handler;

import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;

/**
 * @Description：
 * @Author：du
 * @Date：2024/6/15 14:50
 */
public class JsonLongListTypeHandler extends AbstractJsonTypeHandler<Object> {

    private static final TypeReference<List<Long>> TYPE_REFERENCE = new TypeReference<List<Long>>() {
    };

    @Override
    protected Object parse(String json) {
        List<Long> longs = JsonUtils.parseObject(json, TYPE_REFERENCE);
        return longs;
    }

    @Override
    protected String toJson(Object obj) {
        return JsonUtils.toJsonString(obj);
    }

}