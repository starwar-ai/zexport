package cn.iocoder.yudao.module.system.service.priceType;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.system.controller.admin.priceType.vo.PriceTypePageReqVO;
import cn.iocoder.yudao.module.system.controller.admin.priceType.vo.PriceTypeSaveReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.priceType.PriceTypeDO;
import cn.iocoder.yudao.module.system.dal.mysql.priceType.PriceTypeMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.PRICE_TYPE_NOT_EXISTS;

/**
 * 价格条款 Service 实现类
 *
 * @author ePlus
 */
@Service
@Validated
public class PriceTypeServiceImpl implements PriceTypeService {

    @Resource
    private PriceTypeMapper priceTypeMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createPriceType(PriceTypeSaveReqVO createReqVO) {
        // 插入
        PriceTypeDO priceType = BeanUtils.toBean(createReqVO, PriceTypeDO.class);
        priceTypeMapper.insert(priceType);
        // 返回
        return priceType.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePriceType(PriceTypeSaveReqVO updateReqVO) {
        // 校验存在
        validatePriceTypeExists(updateReqVO.getId());
        // 更新
        PriceTypeDO updateObj = BeanUtils.toBean(updateReqVO, PriceTypeDO.class);
        priceTypeMapper.updateById(updateObj);
    }

    @Override
    public void deletePriceType(Long id) {
        // 校验存在
        validatePriceTypeExists(id);
        // 删除
        priceTypeMapper.deleteById(id);
    }

    private void validatePriceTypeExists(Long id) {
        if (priceTypeMapper.selectById(id) == null) {
            throw exception(PRICE_TYPE_NOT_EXISTS);
        }
    }

    @Override
    public PriceTypeDO getPriceType(Long id) {
        return priceTypeMapper.selectById(id);
    }

    @Override
    public PageResult<PriceTypeDO> getPriceTypePage(PriceTypePageReqVO pageReqVO) {
        return priceTypeMapper.selectPage(pageReqVO);
    }

}