package com.syj.eplus.module.infra.api.sn;

import com.syj.eplus.module.infra.api.sn.dto.SnDTO;

/**
 * 序列号 API 接口
 */
public interface SnApi {

    /**
     * 获取并递增序列号
     *
     * @param type 类型
     * @param codePrefix 编码前缀
     * @return 序列号对象
     */
    SnDTO getAndIncrementSn(String type, String codePrefix);
} 