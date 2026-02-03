package cn.iocoder.yudao.module.infra.api.table;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.module.infra.api.table.dto.FieldDTO;
import cn.iocoder.yudao.module.infra.service.db.DatabaseTableService;
import com.baomidou.mybatisplus.generator.config.po.TableField;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Description：
 * @Author：du
 * @Date：2024/1/31 18:03
 */
@Service
@Validated
public class DataBaseTableApiImpl implements DataBaseTableApi {

    @Resource
    private DatabaseTableService databaseTableService;

    //庸才去除库中基础字段的集合 后期做成可配置
    private final List<String> DEFAULT_FIELD = Arrays.asList("creator", "create_time", "updater", "update_time", "deleted");
    @Override
    public List<FieldDTO> getTableFields() {
        List<TableInfo> tableInfo = databaseTableService.getTableInfo();
        if (CollUtil.isNotEmpty(tableInfo)) {
            //处理表对象并将其中字段返回
            return tableInfo.stream().flatMap(table -> {
                List<TableField> fields = table.getFields();
                if (CollUtil.isNotEmpty(fields)) {
                    //构建字段DTO对象
                    return fields.stream().filter(s->!DEFAULT_FIELD.contains(s.getName())).map(field -> new FieldDTO().setTableName(table.getName())
                            .setTableComment(table.getComment())
                            .setFieldName(field.getColumnName())
                            .setFieldComment(field.getComment())
                            .setFieldType(field.getColumnType().getType())
                            .setDictFlag(0));
                } else {
                    return null;
                }
                //因为上面可能返回null 这里要去除null值
            }).filter(Objects::nonNull).collect(Collectors.toList());
        } else {
            return null;
        }
    }
}
