package com.syj.eplus.module.infra.service.formchange;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.syj.eplus.module.infra.api.formchange.dto.FormChangeDTO;
import com.syj.eplus.module.infra.controller.admin.formchange.vo.FormChangePageReqVO;
import com.syj.eplus.module.infra.controller.admin.formchange.vo.FormChangeRespVO;
import com.syj.eplus.module.infra.controller.admin.formchange.vo.FormChangeSaveReqVO;
import com.syj.eplus.module.infra.dal.dataobject.formchange.FormChangeDO;
import cn.iocoder.yudao.module.system.inface.ModelKeyHolder;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 表单字段变更管理 Service 接口
 *
 * @author ePlus
 */
public interface FormChangeService {

    /**
     * 创建表单字段变更管理
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createFormChange(@Valid FormChangeSaveReqVO createReqVO);

    /**
     * 更新表单字段变更管理
     *
     * @param updateReqVO 更新信息
     */
    void updateFormChange(@Valid FormChangeSaveReqVO updateReqVO);

    /**
     * 删除表单字段变更管理
     *
     * @param id 编号
     */
    void deleteFormChange(Long id);

    /**
     * 获得表单字段变更管理
     *
     * @param id 编号
     * @return 表单字段变更管理
     */
    FormChangeRespVO getFormChange(Long id);


    /**
     * 获取类型是菜单的菜单列表
     *
     * @return 菜单List
     */
    FormChangeRespVO getFormChangeByName(String name);


    /**
     * 获取类型是菜单的菜单列表
     *
     * @return 菜单List
     */
    FormChangeDTO getFormChangeDTOByName(String name);

    /**
     * 获得表单字段变更管理分页
     *
     * @param pageReqVO 分页查询
     * @return 表单字段变更管理分页
     */
    PageResult<FormChangeDO> getFormChangePage(FormChangePageReqVO pageReqVO);

    /**
     * 同步系统字段
     */
    void syncFieldData();

    /**
     * 获取表单对应字段配置
     *
     * @param tableNames 表单名称列表
     * @return 字段配置列表
     */
    List<FormChangeDTO> getFormChangeDTOList(List<String> tableNames);

    /**
     * 处理变更表单字段
     *
     * @param changeFieldName   变更字段列表
     * @param formChangeDTOList 变更表单字段配置
     * @param modelKeyHolder    模型标识
     * @param tableName         表名
     * @param submitFlag        提交标识
     * @param mainTableFlag     主表标识
     * @return 提交标识
     */
    AtomicReference<Integer>  dealShipmentTable(Set<String> changeFieldName, Map<String, FormChangeDTO> formChangeDTOList, ModelKeyHolder modelKeyHolder, String tableName, AtomicReference<Integer> submitFlag, boolean mainTableFlag);

}