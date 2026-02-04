package com.syj.eplus.module.sms.service.otherfee;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.syj.eplus.module.sms.controller.admin.otherfee.vo.OtherFeePageReqVO;
import com.syj.eplus.module.sms.controller.admin.otherfee.vo.OtherFeeRespVO;
import com.syj.eplus.module.sms.controller.admin.otherfee.vo.OtherFeeSaveReqVO;
import com.syj.eplus.module.sms.convert.otherfee.OtherFeeConvert;
import com.syj.eplus.module.sms.dal.dataobject.otherfee.OtherFeeDO;
import com.syj.eplus.module.sms.dal.mysql.otherfee.OtherFeeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.sms.enums.ErrorCodeConstants.OTHER_FEE_NOT_EXISTS;

/**
 * 其他费用 Service 实现类
 *
 * @author ePlus
 */
@Service
@Validated
public class OtherFeeServiceImpl implements OtherFeeService {

protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private OtherFeeMapper otherFeeMapper;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createOtherFee(OtherFeeSaveReqVO createReqVO) {
        OtherFeeDO otherFee = OtherFeeConvert.INSTANCE.convertOtherFeeDO(createReqVO);
        // 插入
        otherFeeMapper.insert(otherFee);
        // 返回
        return otherFee.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOtherFee(OtherFeeSaveReqVO updateReqVO) {
        // 校验存在
        validateOtherFeeExists(updateReqVO.getId());
        // 更新
        OtherFeeDO updateObj = OtherFeeConvert.INSTANCE.convertOtherFeeDO(updateReqVO);
        otherFeeMapper.updateById(updateObj);
    }

    @Override
    public void deleteOtherFee(Long id) {
        // 校验存在
        validateOtherFeeExists(id);
        // 删除
        otherFeeMapper.deleteById(id);
    }

    private void validateOtherFeeExists(Long id) {
        if (otherFeeMapper.selectById(id) == null) {
            throw exception(OTHER_FEE_NOT_EXISTS);
        }
    }

    @Override
    public OtherFeeRespVO getOtherFee(Long id) {
        OtherFeeDO otherFeeDO = otherFeeMapper.selectById(id);
        if (otherFeeDO == null) {
             return null;
        }
        return OtherFeeConvert.INSTANCE.convertOtherFeeRespVO(otherFeeDO);
    }

    @Override
    public PageResult<OtherFeeDO> getOtherFeePage(OtherFeePageReqVO pageReqVO) {
        return otherFeeMapper.selectPage(pageReqVO);
    }

}