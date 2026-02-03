package com.syj.eplus.module.oa.service.feeshareitem;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import com.syj.eplus.module.oa.api.feeshare.dto.FeeShareItemDTO;
import com.syj.eplus.module.oa.controller.admin.feeshareitem.vo.FeeShareItemPageReqVO;
import com.syj.eplus.module.oa.controller.admin.feeshareitem.vo.FeeShareItemRespVO;
import com.syj.eplus.module.oa.controller.admin.feeshareitem.vo.FeeShareItemSaveReqVO;
import com.syj.eplus.module.oa.convert.feeshareitem.FeeShareItemConvert;
import com.syj.eplus.module.oa.dal.dataobject.feeshareitem.FeeShareItemDO;
import com.syj.eplus.module.oa.dal.mysql.feeshareitem.FeeShareItemMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.FEE_SHARE_ITEM_NOT_EXISTS;


/**
 * 费用归集明细 Service 实现类
 *
 * @author zhangcm
 */
@Service
@Validated
public class FeeShareItemServiceImpl implements FeeShareItemService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private FeeShareItemMapper feeShareItemMapper;


    @Override
    public Long createFeeShareItem(FeeShareItemSaveReqVO createReqVO) {
        FeeShareItemDO feeShareItem = FeeShareItemConvert.INSTANCE.convertFeeShareItemDO(createReqVO);
        // 插入
        feeShareItemMapper.insert(feeShareItem);
        // 返回
        return feeShareItem.getId();
    }

    @Override
    public void updateFeeShareItem(FeeShareItemSaveReqVO updateReqVO) {
        // 校验存在
        validateFeeShareItemExists(updateReqVO.getId());
        // 更新
        FeeShareItemDO updateObj = FeeShareItemConvert.INSTANCE.convertFeeShareItemDO(updateReqVO);
        feeShareItemMapper.updateById(updateObj);
    }

    @Override
    public void deleteFeeShareItem(Long id) {
        // 校验存在
        validateFeeShareItemExists(id);
        // 删除
        feeShareItemMapper.deleteById(id);
    }

    private void validateFeeShareItemExists(Long id) {
        if (feeShareItemMapper.selectById(id) == null) {
            throw exception(FEE_SHARE_ITEM_NOT_EXISTS);
        }
    }
    @Override
    public FeeShareItemRespVO getFeeShareItem(Long id) {
        FeeShareItemDO feeShareItemDO = feeShareItemMapper.selectById(id);
        if (feeShareItemDO == null) {
            return null;
        }
        return FeeShareItemConvert.INSTANCE.convertFeeShareItemRespVO(feeShareItemDO);
    }

    @Override
    public PageResult<FeeShareItemDO> getFeeShareItemPage(FeeShareItemPageReqVO pageReqVO) {
        return feeShareItemMapper.selectPage(pageReqVO);
    }

    @Override
    public void createFeeShareItemBatch(List<FeeShareItemDTO> children, Long id) {
        feeShareItemMapper.delete(FeeShareItemDO::getShareFeeId,id);
        List<FeeShareItemDO> doList = BeanUtils.toBean(children, FeeShareItemDO.class);
        if(CollUtil.isEmpty(doList)){
            return;
        }
        doList.forEach(s->{
            s.setShareFeeId(id);
            s.setId(null);
        });
        feeShareItemMapper.insertBatch(doList);
    }

    @Override
    public void deleteFeeShareItemByFeeShareId(Long id) {
        feeShareItemMapper.delete(FeeShareItemDO::getShareFeeId,id);
    }

    @Override
    public List<FeeShareItemDO> getFeeShareItemByShareId(Long shareDOId) {
        return  feeShareItemMapper.selectList(FeeShareItemDO::getShareFeeId,shareDOId);

    }

    @Override
    public Map<Long, List<FeeShareItemDO>> getListByFeeShareIdList(List<Long> feeShareIdList) {
        if(feeShareIdList == null){
            return Map.of();
        }

        List<Long> list = feeShareIdList.stream().distinct().toList();
        List<FeeShareItemDO> itemDOList = feeShareItemMapper.selectList(FeeShareItemDO::getShareFeeId, list);
        if(CollUtil.isEmpty(itemDOList)){
            return Map.of();
        }
        return itemDOList.stream().collect(Collectors.groupingBy(FeeShareItemDO::getShareFeeId));
    }

    @Override
    public void deleteFeeShareItemByFeeShareIdList(List<Long> idList) {
        if(CollUtil.isEmpty(idList)){
            return;
        }

        List<FeeShareItemDO> itemDOList = feeShareItemMapper.selectList(FeeShareItemDO::getShareFeeId, idList);
        if(CollUtil.isNotEmpty(itemDOList)){
            List<Long> doIdList = itemDOList.stream().map(FeeShareItemDO::getId).distinct().toList();
            feeShareItemMapper.deleteBatchIds(doIdList);

        }

    }

    @Override
    public boolean validateCustExists(String custCode) {
        Long count = feeShareItemMapper.selectCount(FeeShareItemDO::getBusinessSubjectCode, custCode);
        return count > 0;
    }

}