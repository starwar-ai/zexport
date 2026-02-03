package com.syj.eplus.module.infra.service.sn;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.syj.eplus.module.infra.controller.admin.sn.vo.SnPageReqVO;
import com.syj.eplus.module.infra.controller.admin.sn.vo.SnSaveReqVO;
import com.syj.eplus.module.infra.converter.SnConverter;
import com.syj.eplus.module.infra.dal.dataobject.sn.SnDO;
import com.syj.eplus.module.infra.dal.mysql.sn.SnMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.infra.enums.ErrorCodeConstants.SN_NOT_EXISTS;

/**
 * 编号 Service 实现类
 *
 * @author 波波
 */
@Service
@Validated
public class SnServiceImpl implements SnService {

    @Resource
    private SnMapper snMapper;

    @Override
    public Long createSn(@Valid SnSaveReqVO saveReqVO) {
        // 插入
        SnDO sn = SnConverter.INSTANCE.convert(saveReqVO);
        snMapper.insert(sn);
        // 返回
        return sn.getId();
    }

    @Override
    public void updateSn(@Valid SnSaveReqVO saveReqVO) {
        // 校验存在
        validateSnExists(saveReqVO.getId());
        // 更新
        SnDO updateObj = SnConverter.INSTANCE.convert(saveReqVO);
        snMapper.updateById(updateObj);
    }

    @Override
    public void deleteSn(Long id) {
        // 校验存在
        validateSnExists(id);
        // 删除
        snMapper.deleteById(id);
    }

    private void validateSnExists(Long id) {
        if (snMapper.selectById(id) == null) {
            throw exception(SN_NOT_EXISTS);
        }
    }

    @Override
    public SnDO getSn(Long id) {
        return snMapper.selectById(id);
    }

    @Override
    public PageResult<SnDO> getSnPage(SnPageReqVO pageReqVO) {
        return snMapper.selectPage(pageReqVO);
    }

    @Override
    public SnDO getSnByType(String type, String codePrefix) {
        return snMapper.selectByTypeAndCodePrefix(type, codePrefix);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SnDO getAndIncrementSn(String type, String codePrefix) {
        // 1. 查询并锁定记录
        SnDO snDO = snMapper.selectForUpdate(type, codePrefix);
        if (snDO == null) {
            // 如果不存在，创建新记录
            snDO = new SnDO();
            snDO.setType(type);
            snDO.setCodePrefix(codePrefix);
            snDO.setSn(1);
            snMapper.insert(snDO);
            return snDO;
        }

        // 2. 递增序列号
        snMapper.incrementSn(snDO.getId());

        // 3. 优化：直接在内存中更新sn值，避免第二次数据库查询
        snDO.setSn(snDO.getSn() + 1);
        return snDO;
    }
}