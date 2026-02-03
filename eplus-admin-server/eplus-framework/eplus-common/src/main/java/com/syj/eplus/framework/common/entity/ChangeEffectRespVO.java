package com.syj.eplus.framework.common.entity;

import cn.iocoder.yudao.framework.common.pojo.JsonEffectRange;
import lombok.Data;

import java.util.List;

@Data
public class ChangeEffectRespVO {
    private Integer submitFlag;

    private List<JsonEffectRange> effectRangeList;
}
