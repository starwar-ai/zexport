package cn.iocoder.yudao.framework.mybatis.core.exinterface;

import cn.iocoder.yudao.framework.common.pojo.JsonEffectRange;

import java.util.List;

/**
 * @Description：
 * @Author：du
 * @Date：2024/5/22 17:46
 */
public interface ChangeExInterface {
     Long getId();

     void setId(Long id);

     void setChangeFlag(Integer changeFlag);

     Integer getChangeFlag();

     String getSourceCode();

     List<JsonEffectRange> getEffectRangeList();
}