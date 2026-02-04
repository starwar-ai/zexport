package com.syj.eplus.module.infra.code;

import cn.hutool.core.util.StrUtil;
import com.google.common.base.Strings;
import com.syj.eplus.module.infra.dal.dataobject.sn.SnDO;
import com.syj.eplus.module.infra.service.sn.SnService;

import java.util.Objects;

public class SerialNumberCodeRule extends CodeRule {

    private final SnService snService;

    public static final int MAX_LENGTH = 100;

    public static final int DEFAULT_INITIAL_VALUE = 0;
    private final String type;

    private final String codePrefix;

    public SerialNumberCodeRule(SnService snService, String type, String codePrefix,Integer length) {
        this(snService, type, 0, length, codePrefix);
    }

    public SerialNumberCodeRule(SnService snService, String type, int initialValue, String codePrefix,Integer length) {
        this(snService, type, initialValue, length, codePrefix);
    }

    public SerialNumberCodeRule(SnService snService, String type, int initialValue, Integer length, String codePrefix) {
        this.snService = snService;
        if (Strings.isNullOrEmpty(type)) throw new IllegalArgumentException("type参数不能为空");
        this.type = type;
        if (initialValue < DEFAULT_INITIAL_VALUE) throw new IllegalArgumentException("长度参数必须大于等于0");
        this.codePrefix = codePrefix;
        if (StrUtil.isEmpty(codePrefix)) throw new IllegalArgumentException("编码前缀codeprefix不能为空");
        else if (Objects.nonNull(length)&&length > MAX_LENGTH) throw new IllegalArgumentException("长度参数不能大于" + MAX_LENGTH);
    }

    @Override
    public String execute(Integer length) {
        // 1. 获取并递增序列号
        SnDO snDO = snService.getAndIncrementSn(type, codePrefix);
        if (Objects.isNull(snDO)) {
            throw new IllegalArgumentException(String.format("未查询到编号--type:%s,codePrefix:%s", type, codePrefix));
        }
        // 2. 如果length为null，直接返回序号，否则按指定长度补0
        if (length == null || length <= 0) {
            return String.valueOf(snDO.getSn());
        }
        // 3. 格式化序列号
        return String.format("%0" + length + "d", snDO.getSn());
    }
}
