package cn.iocoder.yudao.framework.operatelog.core.util;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.operatelog.core.aop.OperateLogAspect;
import com.syj.eplus.framework.common.entity.ChangeRecord;
import com.syj.eplus.framework.common.enums.ChangeRecordTypeEnum;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 操作日志工具类
 * 目前主要的作用，是提供给业务代码，记录操作明细和拓展字段
 *
 * @author 芋道源码
 */
public class OperateLogUtils {

    public static void addOperateLog(List<ChangeRecord> changeRecords,String operateKey,String code){
        if (CollUtil.isNotEmpty(changeRecords)) {
            OperateCompareUtil<Object> operateCompareUtil = new OperateCompareUtil<>();
            AtomicReference<String> content = new AtomicReference<>();
            changeRecords.forEach(s -> {
                content.set(operateCompareUtil.apply(content.get(), s.getFieldName(), s.getOldValue(), s.getValue(), ChangeRecordTypeEnum.GENERAL_CHANGE.getType()));
            });
            OperateLogUtils.setContent(content.get());
        }
        OperateLogUtils.addExt(operateKey, code);
    }
    public static void setContent(String content) {
        OperateLogAspect.setContent(content);
    }

    public static void addExt(String key, Object value) {
        OperateLogAspect.addExt(key, value);
    }

    public static void addOperateLog(String operatorExtsKey,String code,String content){
        OperateLogUtils.setContent(content);
        OperateLogUtils.addExt(operatorExtsKey, code);
    }

}
