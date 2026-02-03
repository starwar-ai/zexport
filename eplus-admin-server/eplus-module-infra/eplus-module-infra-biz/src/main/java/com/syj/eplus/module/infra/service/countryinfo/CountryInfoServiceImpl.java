package com.syj.eplus.module.infra.service.countryinfo;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import com.syj.eplus.module.infra.api.countryinfo.dto.CountryInfoDTO;
import com.syj.eplus.module.infra.controller.admin.countryinfo.vo.CountryInfoPageReqVO;
import com.syj.eplus.module.infra.controller.admin.countryinfo.vo.CountryInfoSaveReqVO;
import com.syj.eplus.module.infra.controller.admin.countryinfo.vo.RegionRespVO;
import com.syj.eplus.module.infra.convert.countryinfo.CountryInfoConvert;
import com.syj.eplus.module.infra.dal.dataobject.countryinfo.CountryInfoDO;
import com.syj.eplus.module.infra.dal.mysql.countryinfo.CountryInfoMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.COUNTRY_INFO_NOT_EXISTS;

/**
 * 国家信息 Service 实现类
 *
 * @author du
 */
@Service
@Validated
public class CountryInfoServiceImpl implements CountryInfoService {

    @Resource
    private CountryInfoMapper countryInfoMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createCountryInfo(CountryInfoSaveReqVO createReqVO) {
        // 插入
        CountryInfoDO countryInfo = BeanUtils.toBean(createReqVO, CountryInfoDO.class);
        countryInfoMapper.insert(countryInfo);
        // 返回
        return countryInfo.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCountryInfo(CountryInfoSaveReqVO updateReqVO) {
        // 校验存在
        validateCountryInfoExists(updateReqVO.getId());
        // 更新
        CountryInfoDO updateObj = BeanUtils.toBean(updateReqVO, CountryInfoDO.class);
        countryInfoMapper.updateById(updateObj);
    }

    @Override
    public void deleteCountryInfo(Long id) {
        // 校验存在
        validateCountryInfoExists(id);
        // 删除
        countryInfoMapper.deleteById(id);
    }

    private CountryInfoDO validateCountryInfoExists(Long id) {
        CountryInfoDO countryInfoDO = countryInfoMapper.selectById(id);
        if (countryInfoDO == null) {
            throw exception(COUNTRY_INFO_NOT_EXISTS);
        }
        return countryInfoDO;
    }

    @Override
    public List<CountryInfoDO> getCountryInfo() {
        return countryInfoMapper.selectList();
    }

    @Override
    public PageResult<CountryInfoDO> getCountryInfoPage(CountryInfoPageReqVO pageReqVO) {
        return countryInfoMapper.selectPage(pageReqVO);
    }

    @Override
    public List<CountryInfoDO> getCountryListByRegionCode(String regionCode) {
        return countryInfoMapper.selectList(CountryInfoDO::getRegionCode, regionCode);
    }

    @Override
    public List<RegionRespVO> getRegionList() {
        List<CountryInfoDO> doList = countryInfoMapper.selectList();
        if (CollUtil.isEmpty(doList)) {
            return null;
        }
        List<RegionRespVO> result = new ArrayList<>();
        doList.forEach(s -> {
            Optional<RegionRespVO> first = result.stream()
                    .filter(i -> Objects.equals(i.getCode(), s.getRegionCode()))
                    .findFirst();
            if (first.isEmpty()) {
                result.add(new RegionRespVO().setCode(s.getRegionCode()).setName(s.getRegionName()));
            }
        });
        List<RegionRespVO> list = result.stream()
                .filter(s -> StrUtil.isNotEmpty(s.getName()))
                .toList();
        return list;
    }

    @Override
    public CountryInfoDTO getCountryInfoById(Long id) {
        CountryInfoDO countryInfoDO = validateCountryInfoExists(id);
        return CountryInfoConvert.INSTANCE.convertDTO(countryInfoDO);
    }

}
