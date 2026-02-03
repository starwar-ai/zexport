package com.syj.eplus.module.mms.api;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import com.syj.eplus.module.mms.api.manufacture.IManufactureApi;
import com.syj.eplus.module.mms.api.manufacture.vo.ManufactureSaveDTO;
import com.syj.eplus.module.mms.api.manufacture.vo.ManufactureSkuSaveDTO;
import com.syj.eplus.module.mms.controller.admin.manufacture.vo.ManufactureSaveInfoReqVO;
import com.syj.eplus.module.mms.controller.admin.manufacturesku.vo.ManufactureSkuSaveInfoReqVO;
import com.syj.eplus.module.mms.controller.admin.manufactureskuitem.vo.ManufactureSkuItemSaveReqVO;
import com.syj.eplus.module.mms.service.manufacture.ManufactureService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Desc——加工单 API 实现类
 * Create by Rangers at  2024-10-24 15:27
 */
@Slf4j
@Service
public class IManufactureApiImpl implements IManufactureApi {

    @Resource
    private ManufactureService manufactureService;

    @Override
    public Boolean createManufacture(ManufactureSaveDTO manufactureSaveDTO) {
        ManufactureSaveInfoReqVO createReqVO = BeanUtils.toBean(manufactureSaveDTO, ManufactureSaveInfoReqVO.class);

        List<ManufactureSkuSaveInfoReqVO> manufactureSkuSaveInfoReqVOS = new ArrayList<>();
        List<ManufactureSkuSaveDTO> children = manufactureSaveDTO.getChildren();
        if (!CollectionUtils.isEmpty(children)) {
            children.forEach(x->{
                ManufactureSkuSaveInfoReqVO skuSaveInfoReqVO = BeanUtils.toBean(x, ManufactureSkuSaveInfoReqVO.class);
                List<ManufactureSkuItemSaveReqVO> skuItemSaveReqVOList = BeanUtils.toBean(x.getSkuItemList(), ManufactureSkuItemSaveReqVO.class);
                skuSaveInfoReqVO.setSkuItemList(skuItemSaveReqVOList);
                manufactureSkuSaveInfoReqVOS.add(skuSaveInfoReqVO);
            });
        }
        createReqVO.setChildren(manufactureSkuSaveInfoReqVOS);

        Long manufactureId;
        try {
            manufactureId = manufactureService.createManufacture(createReqVO);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return manufactureId > 0;
    }
}
