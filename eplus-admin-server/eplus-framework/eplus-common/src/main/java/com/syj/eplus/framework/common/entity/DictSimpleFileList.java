package com.syj.eplus.framework.common.entity;

import lombok.Data;

import java.util.List;

/**
 * @Description：
 * @Author：du
 * @Date：2024/5/8 17:44
 */
@Data
public class DictSimpleFileList {
    /**
     * 字典value
     */
    private String value;
    /**
     * 字典label
     */
    private String label;
    /**
     * 文件List
     */
    private List<SimpleFile> fileList;
}
