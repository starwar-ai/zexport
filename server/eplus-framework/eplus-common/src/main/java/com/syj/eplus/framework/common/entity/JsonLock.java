package com.syj.eplus.framework.common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class JsonLock {

    private Integer lockQuantity = 0;

    private Long stockId;

    private String batchCode;


}