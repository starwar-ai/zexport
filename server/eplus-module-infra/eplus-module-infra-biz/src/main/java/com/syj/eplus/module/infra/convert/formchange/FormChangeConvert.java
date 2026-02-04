package com.syj.eplus.module.infra.convert.formchange;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import com.syj.eplus.module.infra.api.formchange.dto.FormChangeDTO;
import com.syj.eplus.module.infra.api.formchange.dto.FormChangeItemDTO;
import com.syj.eplus.module.infra.controller.admin.formchange.vo.FormChangeRespVO;
import com.syj.eplus.module.infra.controller.admin.formchange.vo.FormChangeSaveReqVO;
import com.syj.eplus.module.infra.dal.dataobject.formchange.FormChangeDO;
import com.syj.eplus.module.infra.dal.dataobject.formchangeitem.FormChangeItemDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;


@Mapper
public interface FormChangeConvert {

    FormChangeConvert INSTANCE = Mappers.getMapper(FormChangeConvert.class);

    FormChangeRespVO convert(FormChangeDO formChangeDO);

    default FormChangeRespVO convertFormChangeRespVO(FormChangeDO formChangeDO) {
        FormChangeRespVO formChangeRespVO = convert(formChangeDO);
        return formChangeRespVO;
    }

    FormChangeDO convertFormChangeDO(FormChangeSaveReqVO saveReqVO);

    FormChangeDTO convertFormChangeDTO(FormChangeRespVO formChangeRespVO);

    FormChangeDTO convertFormChangeDTO(FormChangeDO formChangeDO);

    FormChangeItemDTO convertFormChangeItemDTO(FormChangeDO formChangeDO);

    List<FormChangeItemDTO> convertFormChangeItemDTOList(List<FormChangeItemDO> formChangeItemDOList);

    default List<FormChangeDTO> convertFormChangeDTOList(List<FormChangeDO> formChangeDOList, Map<String, List<FormChangeItemDO>> formChangeDOMap) {
        return CollectionUtils.convertList(formChangeDOList, formChangeDO -> {
            FormChangeDTO formChangeDTO = convertFormChangeDTO(formChangeDO);
            List<FormChangeItemDO> formChangeItemDOList = formChangeDOMap.get(formChangeDTO.getName());
            if (CollUtil.isEmpty(formChangeItemDOList)) {
                return formChangeDTO;
            }
            List<FormChangeItemDTO> formChangeItemDTOS = convertFormChangeItemDTOList(formChangeItemDOList);
            formChangeDTO.setChildren(formChangeItemDTOS);
            return formChangeDTO;
        });
    }

}