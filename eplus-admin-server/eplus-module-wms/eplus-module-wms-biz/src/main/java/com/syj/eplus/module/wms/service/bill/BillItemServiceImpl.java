package com.syj.eplus.module.wms.service.bill;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.syj.eplus.module.wms.api.stock.dto.BillItemSaveReqVO;
import com.syj.eplus.module.wms.controller.admin.billitem.vo.BillItemPageReqVO;
import com.syj.eplus.module.wms.controller.admin.billitem.vo.BillItemRespVO;
import com.syj.eplus.module.wms.convert.bill.BillItemConvert;
import com.syj.eplus.module.wms.dal.dataobject.bill.BillItemDO;
import com.syj.eplus.module.wms.dal.mysql.bill.BillItemMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.wms.enums.ErrorCodeConstants.YSBILL_NOT_EXISTS;

/**
 * 仓储管理-入(出)库单-明细 Service 实现类
 *
 * @author ePlus
 */
@Service
@Validated
public class BillItemServiceImpl extends ServiceImpl<BillItemMapper,BillItemDO> implements BillItemService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private BillItemMapper billItemMapper;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createBillItem(BillItemSaveReqVO createReqVO) {
        BillItemDO billItem = BillItemConvert.INSTANCE.convertBillItemDO(createReqVO);
        // 插入
        billItemMapper.insert(billItem);
        // 返回
        return billItem.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBillItem(BillItemSaveReqVO updateReqVO) {
        // 校验存在
        validateBillItemExists(updateReqVO.getId());
        // 更新
        BillItemDO updateObj = BillItemConvert.INSTANCE.convertBillItemDO(updateReqVO);
        billItemMapper.updateById(updateObj);
    }

    @Override
    public void deleteBillItem(Long id) {
        // 校验存在
        validateBillItemExists(id);
        // 删除
        billItemMapper.deleteById(id);
    }

    private void validateBillItemExists(Long id) {
        if (billItemMapper.selectById(id) == null) {
            throw exception(YSBILL_NOT_EXISTS);
        }
    }

    @Override
    public BillItemRespVO getBillItem(Long id) {
        BillItemDO billItemDO = billItemMapper.selectById(id);
        if (billItemDO == null) {
            return null;
        }
        return BillItemConvert.INSTANCE.convertBillItemRespVO(billItemDO);
    }

    @Override
    public PageResult<BillItemDO> getBillItemPage(BillItemPageReqVO pageReqVO) {
        return billItemMapper.selectPage(pageReqVO);
    }

}
