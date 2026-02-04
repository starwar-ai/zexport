package com.syj.eplus.module.infra.service.countryinfo;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.syj.eplus.module.infra.api.countryinfo.dto.CountryInfoDTO;
import com.syj.eplus.module.infra.controller.admin.countryinfo.vo.CountryInfoPageReqVO;
import com.syj.eplus.module.infra.controller.admin.countryinfo.vo.CountryInfoSaveReqVO;
import com.syj.eplus.module.infra.controller.admin.countryinfo.vo.RegionRespVO;
import com.syj.eplus.module.infra.dal.dataobject.countryinfo.CountryInfoDO;

import javax.validation.Valid;
import java.util.List;

/**
 * 国家信息 Service 接口
 *
 * @author du
 */
public interface CountryInfoService {

    /**
     * 创建国家信息
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createCountryInfo(@Valid CountryInfoSaveReqVO createReqVO);

    /**
     * 更新国家信息
     *
     * @param updateReqVO 更新信息
     */
    void updateCountryInfo(@Valid CountryInfoSaveReqVO updateReqVO);

    /**
     * 删除国家信息
     *
     * @param id 编号
     */
    void deleteCountryInfo(Long id);

    /**
     * 获得国家信息
     *
     * @return 国家信息
     */
    List<CountryInfoDO> getCountryInfo();

    /**
     * 获得国家信息分页
     *
     * @param pageReqVO 分页查询
     * @return 国家信息分页
     */
    PageResult<CountryInfoDO> getCountryInfoPage(CountryInfoPageReqVO pageReqVO);

    /**
     * 根据区域编码获取国家列表
     *
     * @param regionCode 区域编码
     * @return 国家信息列表
     */
    List<CountryInfoDO> getCountryListByRegionCode(String regionCode);

    /**
     * 获取地区列表
     *
     * @return 地区列表
     */
    List<RegionRespVO> getRegionList();

    /**
     * 通过id获取国家信息
     *
     * @param id id
     * @return 国家信息
     */
    CountryInfoDTO getCountryInfoById(Long id);

}
