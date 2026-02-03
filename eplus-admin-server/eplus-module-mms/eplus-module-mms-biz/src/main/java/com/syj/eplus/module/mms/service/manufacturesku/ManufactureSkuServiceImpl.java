package com.syj.eplus.module.mms.service.manufacturesku;


import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import com.syj.eplus.module.mms.controller.admin.manufacturesku.vo.ManufactureSkuPageReqVO;
import com.syj.eplus.module.mms.controller.admin.manufacturesku.vo.ManufactureSkuRespVO;
import com.syj.eplus.module.mms.controller.admin.manufacturesku.vo.ManufactureSkuSaveInfoReqVO;
import com.syj.eplus.module.mms.controller.admin.manufacturesku.vo.ManufactureSkuSaveReqVO;
import com.syj.eplus.module.mms.convert.manufacturesku.ManufactureSkuConvert;
import com.syj.eplus.module.mms.dal.dataobject.manufacturesku.ManufactureSkuDO;
import com.syj.eplus.module.mms.dal.mysql.manufacturesku.ManufactureSkuMapper;
import com.syj.eplus.module.mms.service.manufactureskuitem.ManufactureSkuItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.mms.api.enums.ErrorCodeConstants.MANUFACTURE_SKU_NOT_EXISTS;

/**
 * 加工单产品 Service 实现类
 *
 * @author zhangcm
 */
@Service
@Validated
public class ManufactureSkuServiceImpl implements ManufactureSkuService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private ManufactureSkuMapper manufactureSkuMapper;

    @Resource
    private ManufactureSkuItemService manufactureSkuItemService;
    @Override
    public Long createManufactureSku(ManufactureSkuSaveReqVO createReqVO) {
        ManufactureSkuDO manufactureSku = ManufactureSkuConvert.INSTANCE.convertManufactureSkuDO(createReqVO);
        // 插入
        manufactureSkuMapper.insert(manufactureSku);
        // 返回
        return manufactureSku.getId();
    }

    @Override
    public void updateManufactureSku(ManufactureSkuSaveReqVO updateReqVO) {
        // 校验存在
        validateManufactureSkuExists(updateReqVO.getId());
        // 更新
        ManufactureSkuDO updateObj = ManufactureSkuConvert.INSTANCE.convertManufactureSkuDO(updateReqVO);
        manufactureSkuMapper.updateById(updateObj);
    }

    @Override
    public void deleteManufactureSku(Long id) {
        // 校验存在
        validateManufactureSkuExists(id);
        // 删除
        manufactureSkuMapper.deleteById(id);
    }

    private void validateManufactureSkuExists(Long id) {
        if (manufactureSkuMapper.selectById(id) == null) {
            throw exception(MANUFACTURE_SKU_NOT_EXISTS);
        }
    }
    @Override
    public ManufactureSkuRespVO getManufactureSku(Long id) {
        ManufactureSkuDO manufactureSkuDO = manufactureSkuMapper.selectById(id);
        if (manufactureSkuDO == null) {
            return null;
        }
        return ManufactureSkuConvert.INSTANCE.convertManufactureSkuRespVO(manufactureSkuDO);
    }

    @Override
    public PageResult<ManufactureSkuDO> getManufactureSkuPage(ManufactureSkuPageReqVO pageReqVO) {
        return manufactureSkuMapper.selectPage(pageReqVO);
    }

    @Override
    public void createBatch(List<ManufactureSkuSaveInfoReqVO> children, Long manufactureId)  {
        if(children == null){
            return;
        }
        List<ManufactureSkuDO> doList = BeanUtils.toBean(children, ManufactureSkuDO.class);
        if(CollUtil.isEmpty(doList)){
          return;
        }
        doList.forEach(s->{
            s.setId(null);
            s.setManufactureId(manufactureId);
        });
        manufactureSkuMapper.insertBatch(doList);
        manufactureSkuItemService.createBatch(doList,manufactureId);
    }

    @Override
    public void updateBatch(List<ManufactureSkuSaveInfoReqVO> children, Long manufactureId) {
        deleteByManufactureId(manufactureId);
        createBatch(children,manufactureId);
    }

    @Override
    public void deleteByManufactureId(Long manufactureId) {
        List<ManufactureSkuDO> doList = manufactureSkuMapper.selectList(ManufactureSkuDO::getManufactureId, manufactureId);
        if(CollUtil.isNotEmpty(doList)){
            List<Long> idList = doList.stream().map(ManufactureSkuDO::getId).distinct().toList();
            manufactureSkuMapper.deleteBatchIds(idList);
            manufactureSkuItemService.deleteByManufactureId(manufactureId);
        }
    }

    @Override
    public List<ManufactureSkuDO> selectListByManufactureId(Long manufactureId) {
        return manufactureSkuMapper.selectList(ManufactureSkuDO::getManufactureId,manufactureId).stream().toList();
    }

    @Override
    public List<ManufactureSkuDO> selectListByManufactureIds(List<Long> manufactureIdList) {
        return manufactureSkuMapper.selectList(ManufactureSkuDO::getManufactureId,manufactureIdList).stream().toList();
    }

}