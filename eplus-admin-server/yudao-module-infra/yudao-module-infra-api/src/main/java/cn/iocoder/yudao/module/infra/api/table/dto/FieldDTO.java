package cn.iocoder.yudao.module.infra.api.table.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Description：
 * @Author：du
 * @Date：2024/2/1 19:02
 */
@Data
@Accessors(chain = true)
public class FieldDTO {
    /**
     * 编号
     */
    private Long id;
    /**
     * 表名称
     */
    private String tableName;
    /**
     * 表描述
     */
    private String tableComment;
    /**
     * 字段名称
     */
    private String fieldName;
    /**
     * 字段注释
     */
    private String fieldComment;
    /**
     * 字段类型
     */
    private String fieldType;
    /**
     * 是否字典
     * <p>
     * 枚举 {@link  confirm_type 对应的类}
     */
    private Integer dictFlag;
    /**
     * 字典类型
     */
    private String dictType;
}
