package com.syj.eplus.module.oa.api.feeshare.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 *
 * @author zhangcm
 */
@Data
@Accessors(chain = true)
public class FeeShareDescDTO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 排序
     */
    private Integer sortNum;
    /**
     * 相关方类别编号
     */
    private String relationCode;
    /**
     * 相关方类别
     */
    private Integer relationType;
    /**
     * 名称
     */
    private String name;

}
