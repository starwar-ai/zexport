package com.syj.eplus.module.pms.service.brand;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.syj.eplus.module.pms.controller.admin.brand.vo.BrandPageReqVO;
import com.syj.eplus.module.pms.controller.admin.brand.vo.BrandSaveReqVO;
import com.syj.eplus.module.pms.controller.admin.brand.vo.BrandSimpleRespVO;
import com.syj.eplus.module.pms.dal.dataobject.brand.BrandDO;
import com.syj.eplus.module.pms.dal.mysql.brand.BrandMapper;
import com.syj.eplus.module.pms.service.convent.BrandConvent;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.pms.enums.ErrorCodeConstants.BRAND_NOT_EXISTS;

/**
 * 品牌 Service 实现类
 *
 * @author ePlus
 */
@Service
@Validated
public class BrandServiceImpl implements BrandService {

    @Resource
    private BrandMapper brandMapper;

    @Override
    public Long createBrand(BrandSaveReqVO createReqVO) {
        // 插入
        BrandDO brand = BeanUtils.toBean(createReqVO, BrandDO.class);
        brandMapper.insert(brand);
        // 返回
        return brand.getId();
    }

    @Override
    public void updateBrand(BrandSaveReqVO updateReqVO) {
        // 校验存在
        validateBrandExists(updateReqVO.getId());
        // 更新
        BrandDO updateObj = BeanUtils.toBean(updateReqVO, BrandDO.class);
        brandMapper.updateById(updateObj);
    }

    @Override
    public void deleteBrand(Long id) {
        // 校验存在
        validateBrandExists(id);
        // 删除
        brandMapper.deleteById(id);
    }

    private void validateBrandExists(Long id) {
        if (brandMapper.selectById(id) == null) {
            throw exception(BRAND_NOT_EXISTS);
        }
    }

    @Override
    public BrandDO getBrand(Long id) {
        return brandMapper.selectById(id);
    }

    @Override
    public PageResult<BrandDO> getBrandPage(BrandPageReqVO pageReqVO) {
        return brandMapper.selectPage(pageReqVO);
    }

    @Override
    public List<BrandSimpleRespVO> getBrandSimpleList() {
        List<BrandDO> brandDOList = brandMapper.selectList(new LambdaQueryWrapperX<BrandDO>().select(BrandDO::getId, BrandDO::getOwnBrandFlag, BrandDO::getName));
        if (CollUtil.isEmpty(brandDOList)) {
            return null;
        }
        return BrandConvent.INSTANCE.convertToSimpleList(brandDOList);
    }

    @Override
    public Map<String, BrandSimpleRespVO> getBrandSimpleList(List<String> idList) {
        if (CollUtil.isEmpty(idList)) {
            return null;
        }
        List<BrandDO> brandDOList = brandMapper.selectList(new LambdaQueryWrapperX<BrandDO>().select(BrandDO::getId, BrandDO::getOwnBrandFlag, BrandDO::getName).in(BrandDO::getId, idList));
        if (CollUtil.isEmpty(brandDOList)) {
            return null;
        }
        List<BrandSimpleRespVO> brandSimpleRespVOList = BrandConvent.INSTANCE.convertToSimpleList(brandDOList);
        return brandSimpleRespVOList.stream().collect(Collectors.toMap(s -> String.valueOf(s.getId()), s -> s));
    }

    @Override
    public Map<Long, BrandSimpleRespVO> getBrandSimpleMap(List<Long> idList) {
        LambdaQueryWrapper<BrandDO> queryWrapper = new LambdaQueryWrapperX<BrandDO>().select(BrandDO::getId, BrandDO::getOwnBrandFlag, BrandDO::getName);
        if (CollUtil.isNotEmpty(idList)){
            queryWrapper.in(BrandDO::getId, idList);
        }
        List<BrandDO> brandDOList = brandMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(brandDOList)) {
            return null;
        }
        List<BrandSimpleRespVO> brandSimpleRespVOList = BrandConvent.INSTANCE.convertToSimpleList(brandDOList);
        return brandSimpleRespVOList.stream().collect(Collectors.toMap(BrandSimpleRespVO::getId, s -> s,(o,n)->n));
    }
}