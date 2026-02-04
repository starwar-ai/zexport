package cn.iocoder.yudao.framework.mybatis.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description：
 * @Author：du
 * @Date：2024/5/22 17:16
 */
@Getter
@AllArgsConstructor
public enum ChangeTypeEnum {
    NOT_CHANGE(1, "未变更"),
    //新增
    ADDED(2, "新增"),
    //修改
    UPDATE(3, "修改"),
    //删除
    DELETED(4, "删除");

    private Integer type;

    private String name;
}

