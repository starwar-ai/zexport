package cn.iocoder.yudao.module.system.service.field;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.system.controller.admin.field.vo.*;
import cn.iocoder.yudao.module.system.dal.dataobject.field.FieldDO;

import javax.validation.Valid;
import java.util.List;

/**
 * 系统字段 Service 接口
 *
 * @author ePlus
 */
public interface FieldService {

    /**
     * 创建系统字段
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createField(@Valid FieldSaveReqVO createReqVO);

    /**
     * 更新系统字段
     *
     * @param updateReqVO 更新信息
     */
    void updateField(@Valid FieldSaveReqVO updateReqVO);

    /**
     * 删除系统字段
     *
     * @param id 编号
     */
    void deleteField(Long id);

    /**
     * 获得系统字段
     *
     * @param fieldReqVO 编号
     * @return 系统字段
     */
    List<FieldRespVO> getFieldList(FieldReqVO fieldReqVO);

    /**
     * 获得系统字段分页
     *
     * @param pageReqVO 分页查询
     * @return 系统字段分页
     */
    PageResult<FieldDO> getFieldPage(FieldPageReqVO pageReqVO);

    /**
     * 同步系统字段
     */
    void syncFieldData();

    /**
     * 获取系统表集合
     *
     * @return
     */
    List<TableInfoRespVO> getTableInfo();

}