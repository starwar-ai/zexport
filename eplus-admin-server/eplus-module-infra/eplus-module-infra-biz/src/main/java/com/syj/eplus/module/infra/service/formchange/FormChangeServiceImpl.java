package com.syj.eplus.module.infra.service.formchange;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.dict.CommonDict;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.string.StrUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.infra.api.table.DataBaseTableApi;
import cn.iocoder.yudao.module.infra.api.table.dto.FieldDTO;
import com.syj.eplus.module.infra.api.formchange.dto.FormChangeDTO;
import com.syj.eplus.module.infra.api.formchange.dto.FormChangeItemDTO;
import com.syj.eplus.module.infra.controller.admin.formchange.vo.FormChangePageReqVO;
import com.syj.eplus.module.infra.controller.admin.formchange.vo.FormChangeRespVO;
import com.syj.eplus.module.infra.controller.admin.formchange.vo.FormChangeSaveReqVO;
import com.syj.eplus.module.infra.convert.formchange.FormChangeConvert;
import com.syj.eplus.module.infra.dal.dataobject.formchange.FormChangeDO;
import com.syj.eplus.module.infra.dal.dataobject.formchangeitem.FormChangeItemDO;
import com.syj.eplus.module.infra.dal.mysql.formchange.FormChangeMapper;
import com.syj.eplus.module.infra.dal.mysql.formchangeitem.FormChangeItemMapper;
import cn.iocoder.yudao.module.system.inface.ModelKeyHolder;
import com.syj.eplus.framework.common.enums.BooleanEnum;
import com.syj.eplus.framework.common.enums.ChangeLevelEnum;
import com.syj.eplus.framework.common.enums.SubmitFlagEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.*;
import static com.syj.eplus.module.infra.enums.ErrorCodeConstants.FORM_CHANGE_MUL;

/**
 * 表单字段变更管理 Service 实现类
 *
 * @author ePlus
 */
@Service
@Validated
public class FormChangeServiceImpl implements FormChangeService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private FormChangeMapper formChangeMapper;

    @Resource
    private FormChangeItemMapper formChangeItemMapper;

    @Resource
    private DataBaseTableApi dataBaseTableApi;

    private final List<String> ZIP_TABLE_PREFIX = Arrays.asList("dms_", "wms_", "scm_", "sms_", "oa_", "fms_", "crm_","pms_");

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createFormChange(FormChangeSaveReqVO createReqVO) {
        HashMap map = new HashMap<String, Object>();
        map.put("name", createReqVO.getName());
        FormChangeDO formChangeDO = formChangeMapper.selectOneByMap(map);
        if (formChangeDO != null) {
            throw exception(FORM_CHANGE_MUL);
        }
        FormChangeDO formChange = FormChangeConvert.INSTANCE.convertFormChangeDO(createReqVO);
        // 插入
        formChangeMapper.insert(formChange);
        // 返回
        return formChange.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateFormChange(FormChangeSaveReqVO updateReqVO) {
        // 校验存在
        validateFormChangeExists(updateReqVO.getId());
        // 更新
        FormChangeDO updateObj = FormChangeConvert.INSTANCE.convertFormChangeDO(updateReqVO);

        // 更新明细
        List<FormChangeItemDO> formChangeList = updateReqVO.getChildren();
        formChangeItemMapper.delete(FormChangeItemDO::getTableName, updateReqVO.getName());
        if (CollUtil.isNotEmpty(formChangeList)) {
            formChangeList.forEach(s -> {
                s.setTableName(updateReqVO.getName());
                s.setId(null);
            });
            formChangeItemMapper.insertBatch(formChangeList);
        }
        formChangeMapper.updateById(updateObj);
    }

    @Override
    public void deleteFormChange(Long id) {
        // 校验存在
        FormChangeDO formChangeDO = validateFormChangeExists(id);
        // 删除
        formChangeMapper.deleteById(id);
        // 删除明细
        formChangeItemMapper.delete(FormChangeItemDO::getTableName, formChangeDO.getName());
    }

    private FormChangeDO validateFormChangeExists(Long id) {
        FormChangeDO formChangeDO = formChangeMapper.selectById(id);
        if (formChangeDO == null) {
            throw exception(FORM_CHANGE_NOT_EXISTS);
        }
        return formChangeDO;
    }

    @Override
    public FormChangeRespVO getFormChange(Long id) {
        FormChangeDO formChangeDO = formChangeMapper.selectById(id);
        if (formChangeDO == null) {
            return null;
        }
        FormChangeRespVO formChangeRespVO = FormChangeConvert.INSTANCE.convertFormChangeRespVO(formChangeDO);
        //获取明细
        List<FormChangeItemDO> formChangeItemDOList = formChangeItemMapper.selectList(FormChangeItemDO::getTableName, formChangeDO.getName());
        formChangeRespVO.setChildren(formChangeItemDOList);
        return formChangeRespVO;
    }

    @Override
    public FormChangeRespVO getFormChangeByName(String name) {
        HashMap map = new HashMap<String, Object>();
        map.put("name", name);
        FormChangeDO formChangeDO = formChangeMapper.selectOneByMap(map);
        FormChangeRespVO formChangeRespVO = FormChangeConvert.INSTANCE.convertFormChangeRespVO(formChangeDO);
        List<FormChangeItemDO> formChangeItemDOList = formChangeItemMapper.selectList(FormChangeItemDO::getTableName, formChangeDO.getName());
        formChangeRespVO.setChildren(formChangeItemDOList);
        return formChangeRespVO;
    }

    @Override
    public FormChangeDTO getFormChangeDTOByName(String name) {
        FormChangeRespVO formChangeRespVO = getFormChangeByName(name);
        return FormChangeConvert.INSTANCE.convertFormChangeDTO(formChangeRespVO);
    }

    @Override
    public PageResult<FormChangeDO> getFormChangePage(FormChangePageReqVO pageReqVO) {
        return formChangeMapper.selectPage(pageReqVO);
    }

    @Override
    public void syncFieldData() {
        List<FieldDTO> tableFields = dataBaseTableApi.getTableFields();
        if (CollUtil.isEmpty(tableFields)) {
            return;
        }
        Map<String, List<FieldDTO>> fieldMap = tableFields.stream().filter(s -> checkTableName(s.getTableName())).collect(Collectors.groupingBy(s -> s.getTableName() + CommonDict.COMMA + s.getTableComment()));
        List<FormChangeDO> formChangeDOList = new ArrayList<>();
        List<FormChangeItemDO> formChangeItemDOList = new ArrayList<>();
        fieldMap.forEach((k, v) -> {
            if (StrUtil.isEmpty(k)) {
                throw exception(TABLE_NAME_EMPTY);
            }
            String[] split = k.split(CommonDict.COMMA);
            String tableName = split[0];
            // 表没有描述则默认为空
            String tableDesc = CommonDict.EMPTY_STR;
            if (split.length > 1) {
                tableDesc = split[1];
            }
            // 处理表信息
            FormChangeDO formChangeDO = new FormChangeDO().setName(tableName).setDescription(tableDesc)
                    .setInstanceFlag(BooleanEnum.NO.getValue())
                    .setMainInstanceFlag(BooleanEnum.NO.getValue());
            formChangeDOList.add(formChangeDO);
            // 处理字段信息
            if (CollUtil.isEmpty(v)) {
                return;
            }
            List<FormChangeItemDO> fieldList = v.stream().map(s -> new FormChangeItemDO().setTableName(tableName)
                    .setName(s.getFieldComment())
                    .setNameEng(s.getFieldName())
                    .setChangeLevel(ChangeLevelEnum.NORMAL.getValue())
                    .setEffectMainInstanceFlag(BooleanEnum.NO.getValue())).toList();
            formChangeItemDOList.addAll(fieldList);
        });
        // 已经同步过的表默认不同步
        List<FormChangeDO> changeDOList = formChangeMapper.selectList(new LambdaQueryWrapperX<FormChangeDO>().select(FormChangeDO::getName));
        if (CollUtil.isEmpty(changeDOList)) {
            formChangeMapper.insertBatch(formChangeDOList);
        } else {
            List<FormChangeDO> insertFormChangeDOList = formChangeDOList.stream().filter(s -> !changeDOList.stream().map(FormChangeDO::getName).toList().contains(s.getName())).toList();
            formChangeMapper.insertBatch(insertFormChangeDOList);
        }
        // 已经同步过的字段默认不同步
        List<FormChangeItemDO> baseFormChangeItemList = formChangeItemMapper.selectList(new LambdaQueryWrapperX<FormChangeItemDO>().select(FormChangeItemDO::getNameEng));
        if (CollUtil.isEmpty(baseFormChangeItemList)) {
            formChangeItemMapper.insertBatch(formChangeItemDOList);
        } else {
            List<FormChangeItemDO> insertFormChangeItemDOList = formChangeItemDOList.stream().filter(s -> !baseFormChangeItemList.stream().map(FormChangeItemDO::getNameEng).toList().contains(s.getNameEng())).toList();
            formChangeItemMapper.insertBatch(insertFormChangeItemDOList);
        }
    }

    @Override
    public List<FormChangeDTO> getFormChangeDTOList(List<String> tableNames) {
        List<FormChangeDO> changeDOList = formChangeMapper.selectList(FormChangeDO::getName, tableNames);
        if (CollUtil.isEmpty(changeDOList)) {
            return List.of();
        }
        List<FormChangeItemDO> formChangeItemDOList = formChangeItemMapper.selectList(FormChangeItemDO::getTableName, tableNames);
        if (CollUtil.isEmpty(formChangeItemDOList)) {
            return List.of();
        }
        Map<String, List<FormChangeItemDO>> formChangeDOMap = formChangeItemDOList.stream().collect(Collectors.groupingBy(FormChangeItemDO::getTableName));

        return FormChangeConvert.INSTANCE.convertFormChangeDTOList(changeDOList, formChangeDOMap);
    }

    @Override
    public AtomicReference<Integer> dealShipmentTable(Set<String> changeFieldName, Map<String, FormChangeDTO> formChangeDTOList, ModelKeyHolder modelKeyHolder, String tableName, AtomicReference<Integer> submitFlag, boolean mainTableFlag) {
        FormChangeDTO formChangeDTO = formChangeDTOList.get(tableName);
        if (Objects.isNull(formChangeDTO)) {
            throw exception(FIELD_CHANGE_CONFIG_NOT_EXISTS);
        }
        // 如果选择不进入流程直接更新
        if (BooleanEnum.NO.getValue().equals(formChangeDTO.getMainInstanceFlag())) {
            submitFlag.set(0);
            return submitFlag;
        }
        // 只有主表才会更新模型标识
        if (mainTableFlag) {
            modelKeyHolder.setModelKey(formChangeDTO.getModelKey());
        }
        List<FormChangeItemDTO> fieldList = formChangeDTO.getChildren();
        if (CollUtil.isEmpty(fieldList)) {
            throw exception(FIELD_CHANGE_CONFIG_NOT_EXISTS);
        }
        Map<String, FormChangeItemDTO> baseFieldMap = fieldList.stream().collect(Collectors.toMap(FormChangeItemDTO::getNameEng, s -> s, (v1, v2) -> v1));
        changeFieldName.forEach(s -> {
            String snakeFieldName = StrUtils.camelToSnake(s);
            FormChangeItemDTO formChangeItemDTO = baseFieldMap.get(snakeFieldName);
            if (Objects.isNull(formChangeItemDTO)) {
                return;
            }
            Integer changeLevel = formChangeItemDTO.getChangeLevel();
            if (ChangeLevelEnum.FORM.getValue().equals(changeLevel)) {
                submitFlag.set(SubmitFlagEnum.SUBMIT.getStatus());
            }
        });
        return submitFlag;
    }

    private boolean checkTableName(String tableName) {
        return ZIP_TABLE_PREFIX.stream().anyMatch(tableName::startsWith);
    }
}