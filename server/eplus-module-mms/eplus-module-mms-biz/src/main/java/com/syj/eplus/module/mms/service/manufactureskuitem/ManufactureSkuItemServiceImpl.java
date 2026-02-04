package com.syj.eplus.module.mms.service.manufactureskuitem;


import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.syj.eplus.module.mms.controller.admin.manufactureskuitem.vo.ManufactureSkuItemPageReqVO;
import com.syj.eplus.module.mms.controller.admin.manufactureskuitem.vo.ManufactureSkuItemRespVO;
import com.syj.eplus.module.mms.controller.admin.manufactureskuitem.vo.ManufactureSkuItemSaveReqVO;
import com.syj.eplus.module.mms.convert.manufactureskuitem.ManufactureSkuItemConvert;
import com.syj.eplus.module.mms.dal.dataobject.manufacturesku.ManufactureSkuDO;
import com.syj.eplus.module.mms.dal.dataobject.manufactureskuitem.ManufactureSkuItemDO;
import com.syj.eplus.module.mms.dal.mysql.manufactureskuitem.ManufactureSkuItemMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.mms.api.enums.ErrorCodeConstants.MANUFACTURE_SKU_ITEM_NOT_EXISTS;

/**
 * 加工单子产品 Service 实现类
 *
 * @author zhangcm
 */
@Service
@Validated
public class ManufactureSkuItemServiceImpl implements ManufactureSkuItemService {

protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private ManufactureSkuItemMapper manufactureSkuItemMapper;


    @Override
    public Long createManufactureSkuItem(ManufactureSkuItemSaveReqVO createReqVO) {
ManufactureSkuItemDO manufactureSkuItem = ManufactureSkuItemConvert.INSTANCE.convertManufactureSkuItemDO(createReqVO);
        // 插入
        manufactureSkuItemMapper.insert(manufactureSkuItem);
        // 返回
        return manufactureSkuItem.getId();
    }

    @Override
    public void updateManufactureSkuItem(ManufactureSkuItemSaveReqVO updateReqVO) {
        // 校验存在
        validateManufactureSkuItemExists(updateReqVO.getId());
        // 更新
ManufactureSkuItemDO updateObj = ManufactureSkuItemConvert.INSTANCE.convertManufactureSkuItemDO(updateReqVO);
        manufactureSkuItemMapper.updateById(updateObj);
    }

    @Override
    public void deleteManufactureSkuItem(Long id) {
        // 校验存在
        validateManufactureSkuItemExists(id);
        // 删除
        manufactureSkuItemMapper.deleteById(id);
    }

    private void validateManufactureSkuItemExists(Long id) {
        if (manufactureSkuItemMapper.selectById(id) == null) {
            throw exception(MANUFACTURE_SKU_ITEM_NOT_EXISTS);
        }
    }
    @Override
public ManufactureSkuItemRespVO getManufactureSkuItem(Long id) {
    ManufactureSkuItemDO manufactureSkuItemDO = manufactureSkuItemMapper.selectById(id);
if (manufactureSkuItemDO == null) {
return null;
}
return ManufactureSkuItemConvert.INSTANCE.convertManufactureSkuItemRespVO(manufactureSkuItemDO);
    }

    @Override
    public PageResult<ManufactureSkuItemDO> getManufactureSkuItemPage(ManufactureSkuItemPageReqVO pageReqVO) {
        return manufactureSkuItemMapper.selectPage(pageReqVO);
    }

    @Override
    public void createBatch(List<ManufactureSkuDO> children, Long manufactureId) {
        if(CollUtil.isEmpty(children)){
            return;
        }
        List<ManufactureSkuItemDO> manufactureSkuItemDOList = new ArrayList<>();
        children.forEach(s->{
            List<ManufactureSkuItemDO> skuList = s.getSkuItemList();
            if(CollUtil.isNotEmpty(skuList)){
                skuList.forEach(sku->{
                    sku.setId(null);
                    sku.setManufactureSkuId(s.getId());
                    sku.setManufactureId(manufactureId);
                });
                manufactureSkuItemDOList.addAll(skuList);
            }
        });
        if(CollUtil.isEmpty(manufactureSkuItemDOList)){
            return;
        }
        manufactureSkuItemMapper.insertBatch(manufactureSkuItemDOList);

    }

    @Override
    public void deleteByManufactureId(Long manufactureId) {
        List<ManufactureSkuItemDO> doList = manufactureSkuItemMapper.selectList(ManufactureSkuItemDO::getManufactureId, manufactureId);
        if(CollUtil.isNotEmpty(doList)){
            List<Long> idList = doList.stream().map(ManufactureSkuItemDO::getId).distinct().toList();
            manufactureSkuItemMapper.deleteBatchIds(idList);
        }
    }

    @Override
    public List<ManufactureSkuItemDO> selectListByManufactureId(Long manufactureId) {
        return manufactureSkuItemMapper.selectList(ManufactureSkuItemDO::getManufactureId,manufactureId).stream().toList();

    }

    @Override
    public List<ManufactureSkuItemDO> selectListByManufactureIds(List<Long> manufactureIdList) {
        return manufactureSkuItemMapper.selectList(ManufactureSkuItemDO::getManufactureId,manufactureIdList).stream().toList();
    }


}