package com.syj.eplus.module.service.sendproduct;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.syj.eplus.module.controller.admin.sendproduct.vo.SendProductPageReqVO;
import com.syj.eplus.module.controller.admin.sendproduct.vo.SendProductRespVO;
import com.syj.eplus.module.controller.admin.sendproduct.vo.SendProductSaveReqVO;
import com.syj.eplus.module.convert.sendproduct.SendProductConvert;
import com.syj.eplus.module.dal.dataobject.sendproduct.SendProductDO;
import com.syj.eplus.module.dal.mysql.sendproduct.SendProductMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.ems.enums.ErrorCodeConstants.SEND_PRODUCT_NOT_EXISTS;


/**
 * 寄件产品 Service 实现类
 *
 * @author zhangcm
 */
@Service
@Validated
public class SendProductServiceImpl implements SendProductService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private SendProductMapper sendProductMapper;


    @Override
    public Long createSendProduct(SendProductSaveReqVO createReqVO) {
        createReqVO.setPicture(createReqVO.getMainPicture());
        SendProductDO sendProduct = SendProductConvert.INSTANCE.convertSendProductDO(createReqVO);
        // 插入
        sendProductMapper.insert(sendProduct);
        // 返回
        return sendProduct.getId();
    }

    @Override
    public void updateSendProduct(SendProductSaveReqVO updateReqVO) {
        updateReqVO.setPicture(updateReqVO.getMainPicture());
        // 校验存在
        validateSendProductExists(updateReqVO.getId());
        // 更新
        SendProductDO updateObj = SendProductConvert.INSTANCE.convertSendProductDO(updateReqVO);
        sendProductMapper.updateById(updateObj);
    }

    @Override
    public void deleteSendProduct(Long id) {
        // 校验存在
        validateSendProductExists(id);
        // 删除
        sendProductMapper.deleteById(id);
    }

    private void validateSendProductExists(Long id) {
        if (sendProductMapper.selectById(id) == null) {
            throw exception(SEND_PRODUCT_NOT_EXISTS);
        }
    }
    @Override
    public SendProductRespVO getSendProduct(Long id) {
        SendProductDO sendProductDO = sendProductMapper.selectById(id);
        if (sendProductDO == null) {
            return null;
        }
        SendProductRespVO sendProductRespVO = SendProductConvert.INSTANCE.convertSendProductRespVO(sendProductDO);
        sendProductRespVO.setMainPicture(sendProductRespVO.getPicture());
        return sendProductRespVO;
    }

    @Override
    public PageResult<SendProductDO> getSendProductPage(SendProductPageReqVO pageReqVO) {
        return sendProductMapper.selectPage(pageReqVO);
    }

    @Override
    public List<SendProductDO> getListBysendCode(Long sendId) {
        return sendProductMapper.selectList(SendProductDO::getSendId,sendId).stream().toList();

    }

}