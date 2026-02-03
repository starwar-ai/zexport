package com.syj.eplus.module.sms.api;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import com.syj.eplus.module.sms.api.dto.AddSubItemDTO;
import com.syj.eplus.module.sms.dal.dataobject.addsubitem.AddSubItem;
import com.syj.eplus.module.sms.dal.mysql.addsubitem.AddSubItemMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 加减项 API 实现类
 *
 * @author ePlus
 */
@Service
public class AddSubItemApiImpl implements AddSubItemApi {

    @Resource
    private AddSubItemMapper addSubItemMapper;

    @Override
    public List<AddSubItemDTO> getAddSubItemListByContractCodeList(List<String> contractCodeList) {
        if (CollUtil.isEmpty(contractCodeList)) {
            return null;
        }
        List<AddSubItem> list = addSubItemMapper.getAddSubItemListByContractCodeList(contractCodeList);
        return BeanUtils.toBean(list, AddSubItemDTO.class);
    }

    @Override
    public List<AddSubItemDTO> getAddSubItemListByContractCodeListAndStatus(List<String> contractCodeList, Integer status) {
        if (CollUtil.isEmpty(contractCodeList)) {
            return null;
        }
        List<AddSubItem> list = addSubItemMapper.selectList(AddSubItem::getContractCode, contractCodeList);
        if (CollUtil.isEmpty(list)) {
            return null;
        }
        if (status != null) {
            list = list.stream()
                    .filter(item -> item.getStatus() != null && item.getStatus().equals(status))
                    .collect(Collectors.toList());
        }
        return BeanUtils.toBean(list, AddSubItemDTO.class);
    }

    @Override
    public void deleteBatchByIds(List<Long> ids) {
        if (CollUtil.isNotEmpty(ids)) {
            addSubItemMapper.deleteBatchIds(ids);
        }
    }

    @Override
    public void insertBatch(List<AddSubItemDTO> addSubItemList) {
        if (CollUtil.isNotEmpty(addSubItemList)) {
            List<AddSubItem> list = BeanUtils.toBean(addSubItemList, AddSubItem.class);
            addSubItemMapper.insertBatch(list);
        }
    }

    @Override
    public void deleteByContractCode(String contractCode) {
        if (StrUtil.isNotBlank(contractCode)) {
            addSubItemMapper.delete(AddSubItem::getContractCode, contractCode);
        }
    }
}
