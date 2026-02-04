package cn.iocoder.yudao.module.infra.api.table;

import cn.iocoder.yudao.module.infra.api.table.dto.FieldDTO;

import java.util.List;

/**
 * @Description：
 * @Author：du
 * @Date：2024/1/31 18:02
 */
public interface DataBaseTableApi {

    /**
     * 获取所有字段信息
     *
     * @return 所有字段信息列表
     */
    List<FieldDTO> getTableFields();
}
