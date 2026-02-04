package com.syj.eplus.module.wechat.entity.approval;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @Description：
 * @Author：du
 * @Date：2024/3/13 19:43
 */
@Data
public class ApprovalReq {
    private String starttime;

    private String endtime;

    private String new_cursor = "";

    private Integer size;

    private List<Filters> filters;

    @Data
    @NoArgsConstructor
    @Accessors(chain = true)
    public static class Filters {
        private String key;

        private String value;
    }
}
