package cn.iocoder.yudao.module.system.api.permission.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description：
 * @Author：du
 * @Date：2024/2/1 15:38
 */

@Data
@EqualsAndHashCode
public class FieldPermissionDTO {

    /**
     * 字段id
     */
    private Long id;

    /**
     * 表名称
     */
    private String tableName;

    /**
     * 字段名称
     */
    private String fieldName;

}
