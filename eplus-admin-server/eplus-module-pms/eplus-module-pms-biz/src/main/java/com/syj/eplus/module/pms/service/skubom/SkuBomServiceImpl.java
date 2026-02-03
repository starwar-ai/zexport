package com.syj.eplus.module.pms.service.skubom;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.syj.eplus.module.pms.api.sku.dto.SkuBomDTO;
import com.syj.eplus.module.pms.controller.admin.skubom.vo.SkuBomPageReqVO;
import com.syj.eplus.module.pms.controller.admin.skubom.vo.SkuBomSaveReqVO;
import com.syj.eplus.module.pms.dal.dataobject.skubom.SkuBomDO;
import com.syj.eplus.module.pms.dal.mysql.skubom.SkuBomMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.pms.enums.ErrorCodeConstants.SKU_BOM_NOT_EXISTS;

/**
 * 产品SKU BOM Service 实现类
 *
 * @author eplus
 */
@Service
@Validated
public class SkuBomServiceImpl extends ServiceImpl<SkuBomMapper,SkuBomDO> implements SkuBomService {

    @Resource
    private SkuBomMapper skuBomMapper;

    @Override
    public Long createSkuBom(SkuBomSaveReqVO createReqVO) {
        // 插入
        SkuBomDO skuBom = BeanUtils.toBean(createReqVO, SkuBomDO.class);
        skuBomMapper.insert(skuBom);
        // 返回
        return skuBom.getId();
    }

    @Override
    public void updateSkuBom(SkuBomSaveReqVO updateReqVO) {
        // 校验存在
        validateSkuBomExists(updateReqVO.getId());
        // 更新
        SkuBomDO updateObj = BeanUtils.toBean(updateReqVO, SkuBomDO.class);
        skuBomMapper.updateById(updateObj);
    }

    @Override
    public void deleteSkuBom(Long id) {
        // 校验存在
        validateSkuBomExists(id);
        // 删除
        skuBomMapper.deleteById(id);
    }

    private void validateSkuBomExists(Long id) {
        if (skuBomMapper.selectById(id) == null) {
            throw exception(SKU_BOM_NOT_EXISTS);
        }
    }

    @Override
    public SkuBomDO getSkuBom(Long id) {
        return skuBomMapper.selectById(id);
    }

    @Override
    public PageResult<SkuBomDO> getSkuBomPage(SkuBomPageReqVO pageReqVO) {
        return skuBomMapper.selectPage(pageReqVO);
    }

    @Override
    public void batchSaveSkuBom(List<SkuBomDO> skuBomDOList) {
        skuBomMapper.insertBatch(skuBomDOList);
    }

    @Override
    public List<SkuBomDO> getSkuBomListByParentId(Long parentId) {
        return skuBomMapper.selectList(SkuBomDO::getParentSkuId, parentId);
    }

    @Override
    public void batchDeleteSkuBomByParentId(Long parentId) {
        skuBomMapper.delete(SkuBomDO::getParentSkuId, parentId);
    }

    @Override
    public List<SkuBomDO> getSkuBomListByParentIdList(List<Long> parentIdList) {
        return skuBomMapper.selectList(new LambdaQueryWrapperX<SkuBomDO>().in(SkuBomDO::getParentSkuId, parentIdList));
    }

    @Override
    public Map<Long, List<SkuBomDO>> getSkuBomDOMapByParentId(Long parentId) {
        List<SkuBomDO> skuBomDOList = skuBomMapper.selectSkuBomChildNodeByParentId(parentId);
//        skuBomDOList = getSkuDoListByDoList(skuBomDOList);
        if (CollUtil.isEmpty(skuBomDOList)) {
            return Map.of();
        }
        return skuBomDOList.stream()
                .collect(Collectors.groupingBy(SkuBomDO::getParentSkuId));
    }

    private List<SkuBomDO> getSkuDoListByDoList(List<SkuBomDO> list){
        if(CollUtil.isEmpty(list)){
            return null;
        }

        List<SkuBomDO> result = new ArrayList<>();
        list.forEach(s->{
            List<SkuBomDO> skuBomDOList = skuBomMapper.selectSkuBomChildNodeByParentId(s.getSkuId());
            if(CollUtil.isNotEmpty(skuBomDOList)){
                result.addAll(skuBomDOList);
            }
        });
        if(CollUtil.isEmpty(result)){
            return list;
        }

        List<SkuBomDO> dos = getSkuDoListByDoList(result);
        if(CollUtil.isNotEmpty(dos)){
            result.addAll(dos);
        }
         result.addAll(list);
        return  result.stream().distinct().toList() ;
    }

    @Override
    public void insertSkuBom(SkuBomDO skuBomDO) {
        skuBomMapper.insert(skuBomDO);
    }

    @Override
    public List<SkuBomDO> getSkuBomListBYSkuId(Long skuId) {
        return  skuBomMapper.selectList(SkuBomDO::getParentSkuId,skuId);
    }

    @Override
    public Map<Long,List<SkuBomDTO>> getAllBomDTOMap() {
        List<SkuBomDO> skuBomDOS = skuBomMapper.selectList();
        if (CollUtil.isEmpty(skuBomDOS)){
            return Map.of();
        }
        List<SkuBomDTO> SkuBomDTOList = BeanUtils.toBean(skuBomDOS, SkuBomDTO.class);
        return SkuBomDTOList.stream().collect(Collectors.groupingBy(SkuBomDTO::getParentSkuId));
    }

    @Override
    public void updateSkuBom(Long sourceId, Long id) {
        List<SkuBomDO> doList = skuBomMapper.selectList(SkuBomDO::getSkuId, sourceId);
        if(CollUtil.isNotEmpty(doList)){
            doList.forEach(s->{
                s.setSkuId(id);
            });
            skuBomMapper.updateBatch(doList);
        }
        List<SkuBomDO> doList2 = skuBomMapper.selectList(SkuBomDO::getParentSkuId, sourceId);
        if(CollUtil.isNotEmpty(doList2)){
            doList2.forEach(s->{
                s.setParentSkuId(id);
            });
            skuBomMapper.updateBatch(doList2);
        }

    }

}
