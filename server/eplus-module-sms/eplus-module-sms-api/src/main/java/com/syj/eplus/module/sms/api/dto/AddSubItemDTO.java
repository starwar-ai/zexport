package com.syj.eplus.module.sms.api.dto;

import cn.iocoder.yudao.framework.common.pojo.JsonEffectRange;
import cn.iocoder.yudao.framework.mybatis.core.exinterface.ChangeExInterface;
import com.syj.eplus.framework.common.entity.JsonAmount;
import lombok.Getter;
import lombok.Setter;
import lombok.AccessLevel;

import java.util.List;

/**
 * @Description：
 * @Author：du
 * @Date：2024/7/15 14:25
 */
@Getter
@Setter
public class AddSubItemDTO implements ChangeExInterface {
    /**
     * 主键
     */
    @Setter(AccessLevel.NONE)
    private Long id;
    /**
     * 合同编号
     */
    private String contractCode;
    /**
     * 加/减项
     */
    private Integer calculationType;
    /**
     * 费用名称
     */
    private String feeName;

    /**
     * 金额
     */
    private JsonAmount amount;

    /**
     * 变更标识
     */
    @Setter(AccessLevel.NONE)
    private Integer changeFlag;

    /**
     * 源编码
     */
    private String sourceCode;

    /**
     * 影响范围列表
     */
    private List<JsonEffectRange> effectRangeList;

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public void setChangeFlag(Integer changeFlag) {
        this.changeFlag = changeFlag;
    }

    @Override
    public String getSourceCode() {
        return sourceCode;
    }

    @Override
    public List<JsonEffectRange> getEffectRangeList() {
        return effectRangeList;
    }
}
