package com.syj.eplus.framework.common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class JsonSetPreference {
    /**
     * key
     */
    private String pageKey;

    /**
     * tabIndex
     */
    private Integer tabIndex;
    /**
     * 子
     */
    private List<SetPreferenceObj> children;

    /**
     * 父
     */
    private List<SetPreferenceObj> parent;
}
