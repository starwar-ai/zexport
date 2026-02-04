package com.syj.eplus.framework.common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description：
 * @Author：du
 * @Date：2024/5/22 16:20
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiffRecord<T> {
    T newItem;
    T item;
    T oldItem;
    Integer changeFlag;
}