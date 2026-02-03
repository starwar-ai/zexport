package com.syj.eplus.module.wms.util;

import com.syj.eplus.module.infra.api.sn.SnApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class BatchCodeUtil {

    private static final String REDIS_KEY_DATE_FORMAT = "yyyyMMdd";

    @Resource
    private final SnApi snApi;

    private final SimpleDateFormat dateFormat;

    @Autowired
    public BatchCodeUtil(SnApi snApi) {
        this.snApi = snApi;
        this.dateFormat = new SimpleDateFormat(REDIS_KEY_DATE_FORMAT);
    }

    public String genBatchCode(String bizKey) {
        String currentDate = dateFormat.format(new Date());
        String type = "BATCH_CODE";
        String codePrefix = bizKey + currentDate;

        // 使用SnApi获取并递增序列号
        return bizKey + currentDate + String.format("%05d", snApi.getAndIncrementSn(type, codePrefix).getSn());
    }
}
