package com.syj.eplus.framework.common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class SetPreferenceObj {

    private String prop;

    private String label;

    private String minWidth;

    private Boolean disable;
}
