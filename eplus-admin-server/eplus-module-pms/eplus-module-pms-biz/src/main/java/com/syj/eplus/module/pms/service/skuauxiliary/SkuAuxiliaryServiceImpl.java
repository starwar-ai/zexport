package com.syj.eplus.module.pms.service.skuauxiliary;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import com.syj.eplus.module.pms.api.sku.dto.SkuAuxiliaryDTO;
import com.syj.eplus.module.pms.controller.admin.skuauxiliary.vo.SkuAuxiliaryPageReqVO;
import com.syj.eplus.module.pms.controller.admin.skuauxiliary.vo.SkuAuxiliaryRespVO;
import com.syj.eplus.module.pms.controller.admin.skuauxiliary.vo.SkuAuxiliarySaveReqVO;
import com.syj.eplus.module.pms.convert.skuauxiliary.SkuAuxiliaryConvert;
import com.syj.eplus.module.pms.dal.dataobject.skuauxiliary.SkuAuxiliaryDO;
import com.syj.eplus.module.pms.dal.mysql.skuauxiliary.SkuAuxiliaryMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.pms.enums.ErrorCodeConstants.SKU_AUXILIARY_NOT_EXISTS;

/**
 * 产品辅料配比 Service 实现类
 *
 * @author zhangcm
 */
@Service
@Validated
public class SkuAuxiliaryServiceImpl implements SkuAuxiliaryService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private SkuAuxiliaryMapper skuAuxiliaryMapper;


    @Override
    public Long createSkuAuxiliary(SkuAuxiliarySaveReqVO createReqVO) {
        SkuAuxiliaryDO skuAuxiliary = SkuAuxiliaryConvert.INSTANCE.convertSkuAuxiliaryDO(createReqVO);
        // 插入
        skuAuxiliaryMapper.insert(skuAuxiliary);
        // 返回
        return skuAuxiliary.getId();
    }

    @Override
    public void updateSkuAuxiliary(SkuAuxiliarySaveReqVO updateReqVO) {
        // 校验存在
        validateSkuAuxiliaryExists(updateReqVO.getId());
        // 更新
        SkuAuxiliaryDO updateObj = SkuAuxiliaryConvert.INSTANCE.convertSkuAuxiliaryDO(updateReqVO);
        skuAuxiliaryMapper.updateById(updateObj);
    }

    @Override
    public void deleteSkuAuxiliary(Long id) {
        // 校验存在
        validateSkuAuxiliaryExists(id);
        // 删除
        skuAuxiliaryMapper.deleteById(id);
    }

    private void validateSkuAuxiliaryExists(Long id) {
        if (skuAuxiliaryMapper.selectById(id) == null) {
            throw exception(SKU_AUXILIARY_NOT_EXISTS);
        }
    }
    @Override
    public SkuAuxiliaryRespVO getSkuAuxiliary(Long id) {
        SkuAuxiliaryDO skuAuxiliaryDO = skuAuxiliaryMapper.selectById(id);
        if (skuAuxiliaryDO == null) {
            return null;
        }
        return SkuAuxiliaryConvert.INSTANCE.convertSkuAuxiliaryRespVO(skuAuxiliaryDO);
    }

    @Override
    public PageResult<SkuAuxiliaryDO> getSkuAuxiliaryPage(SkuAuxiliaryPageReqVO pageReqVO) {
        return skuAuxiliaryMapper.selectPage(pageReqVO);
    }

    @Override
    public void batchSave(List<SkuAuxiliaryDO> skuAuxiliaryList) {
        if(CollUtil.isEmpty(skuAuxiliaryList)){
            return;
        }
        skuAuxiliaryList.forEach(s->{
            s.setId(null);
        });
        skuAuxiliaryMapper.insertBatch(skuAuxiliaryList);
    }

    @Override
    public List<SkuAuxiliaryDO> getListBySkuCode(String code) {
        return skuAuxiliaryMapper.selectList(SkuAuxiliaryDO::getSkuCode,code);
    }

    @Override
    public void batchDeleteBySkuCode(String code) {
        skuAuxiliaryMapper.delete(SkuAuxiliaryDO::getSkuCode,code);
    }

    @Override
    public Map<String, List<SkuAuxiliaryDTO>> getMapBySkuCodeList(List<String> codeList) {
        List<SkuAuxiliaryDO> auxiliaryDOList = skuAuxiliaryMapper.selectList(SkuAuxiliaryDO::getSkuCode, codeList);
        if(CollUtil.isEmpty(auxiliaryDOList)){
            return null;
        }
        List<SkuAuxiliaryDTO> dtoList = BeanUtils.toBean(auxiliaryDOList, SkuAuxiliaryDTO.class);
        return dtoList.stream().collect(Collectors.groupingBy(SkuAuxiliaryDTO::getSkuCode));

    }

    @Override
    public List<SkuAuxiliaryDO> getAuxiliaryListByCodeList(List<String> skuCodeList) {
        return skuAuxiliaryMapper.selectList(SkuAuxiliaryDO::getSkuCode,skuCodeList);
    }

}