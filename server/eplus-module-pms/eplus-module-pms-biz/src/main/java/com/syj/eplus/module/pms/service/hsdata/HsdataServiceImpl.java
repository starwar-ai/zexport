package com.syj.eplus.module.pms.service.hsdata;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.syj.eplus.module.pms.controller.admin.hsdata.vo.HsdataPageReqVO;
import com.syj.eplus.module.pms.controller.admin.hsdata.vo.HsdataRespVO;
import com.syj.eplus.module.pms.controller.admin.hsdata.vo.HsdataSaveReqVO;
import com.syj.eplus.module.pms.controller.admin.hsdata.vo.HsdataSimpleRespVO;
import com.syj.eplus.module.pms.dal.dataobject.hsdata.HsdataDO;
import com.syj.eplus.module.pms.dal.mysql.hsdata.HsdataMapper;
import com.syj.eplus.module.pms.service.convent.HsdataConvent;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.pms.enums.ErrorCodeConstants.HSDATA_EXISTS;
import static com.syj.eplus.module.pms.enums.ErrorCodeConstants.HSDATA_NOT_EXISTS;

/**
 * 海关编码 Service 实现类
 *
 * @author ePlus
 */
@Service
@Validated
public class HsdataServiceImpl implements HsdataService {

    @Resource
    private HsdataMapper hsdataMapper;

    @Override
    public Long createHsdata(HsdataSaveReqVO createReqVO) {
        validateHsdataNotExists(createReqVO.getCode());
        // 插入
        HsdataDO hsdata = BeanUtils.toBean(createReqVO, HsdataDO.class);
        hsdata.setVer(1);
        hsdataMapper.insert(hsdata);
        // 返回
        return hsdata.getId();
    }



    @Override
    public void updateHsdata(HsdataSaveReqVO updateReqVO) {
        // 校验存在
        HsdataDO hsdataDO = validateHsdataExists(updateReqVO.getId());
        // 更新
        HsdataDO updateObj = BeanUtils.toBean(updateReqVO, HsdataDO.class);
        AtomicInteger verAto = new AtomicInteger(updateObj.getVer());
        updateObj.setVer(verAto.addAndGet(1));
        hsdataMapper.updateById(updateObj);
    }

    @Override
    public void deleteHsdata(Long id) {
        // 校验存在
        validateHsdataExists(id);
        // 删除
        hsdataMapper.deleteById(id);
    }

    private void validateHsdataNotExists(String code) {
        List<HsdataDO> hsdataDOS = hsdataMapper.selectList(HsdataDO::getCode, code);
        if (CollUtil.isNotEmpty(hsdataDOS)) {
            throw exception(HSDATA_EXISTS);
        }
    }


    private HsdataDO validateHsdataExists(Long id) {
        HsdataDO hsdataDO = hsdataMapper.selectById(id);
        if (hsdataDO == null) {
            throw exception(HSDATA_NOT_EXISTS);
        }
        return hsdataDO;
    }

    @Override
    public HsdataRespVO getHsdata(Long id) {
        HsdataDO hsdataDO = hsdataMapper.selectById(id);
        return BeanUtils.toBean(hsdataDO, HsdataRespVO.class);
    }

    @Override
    public PageResult<HsdataDO> getHsdataPage(HsdataPageReqVO pageReqVO) {
        return hsdataMapper.selectPage(pageReqVO);
    }

    @Override
    public List<HsdataSimpleRespVO> getHsdataSimpleList(String code) {
        LambdaQueryWrapper<HsdataDO> queryWrapper = new LambdaQueryWrapperX<HsdataDO>().select(HsdataDO::getId, HsdataDO::getCode, HsdataDO::getUnit, HsdataDO::getTaxRefundRate);
        if (StrUtil.isNotEmpty(code)) {
            queryWrapper.like(HsdataDO::getCode, code);
        }
        List<HsdataDO> hsdataDOList = hsdataMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(hsdataDOList)) {
            return null;
        }
        return HsdataConvent.INSTANCE.convertToSimpleList(hsdataDOList);
    }

    @Override
    public Map<String, HsdataSimpleRespVO> getHsdataSimpleList(List<String> idList) {
        if (CollUtil.isEmpty(idList)) {
            return null;
        }
        LambdaQueryWrapper<HsdataDO> queryWrapper = new LambdaQueryWrapperX<HsdataDO>().select(HsdataDO::getId, HsdataDO::getCode, HsdataDO::getUnit, HsdataDO::getTaxRefundRate).in(HsdataDO::getId, idList);
        List<HsdataDO> hsdataDOList = hsdataMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(hsdataDOList)) {
            return null;
        }
        List<HsdataSimpleRespVO> simpleRespVOList = HsdataConvent.INSTANCE.convertToSimpleList(hsdataDOList);
        return simpleRespVOList.stream().collect(Collectors.toMap(s -> String.valueOf(s.getId()), s -> s));
    }

    @Override
    public HsdataSimpleRespVO getHsdataByCode(String code) {
        if (StrUtil.isEmpty(code)) {
            return null;
        }
        HsdataDO hsdataDO = hsdataMapper.selectOne(HsdataDO::getCode, code);
        return HsdataConvent.INSTANCE.convertToSimple(hsdataDO);
    }

    @Override
    public List<String> getSimpleUnitHsdata() {
        List<HsdataDO> doList = hsdataMapper.selectList();
        if(CollUtil.isEmpty(doList)){
            return null;
        }
        return doList.stream().map(HsdataDO::getUnit).distinct().toList();
    }

    @Override
    public Map<Long, HsdataDO> getListByIds(List<Long> hsIdList) {

        List<HsdataDO> doList = hsdataMapper.selectList(HsdataDO::getId, hsIdList);
        if(CollUtil.isEmpty(doList)){
            return null;
        }
        return doList.stream().collect(Collectors.toMap(HsdataDO::getId, s->s));
    }


}