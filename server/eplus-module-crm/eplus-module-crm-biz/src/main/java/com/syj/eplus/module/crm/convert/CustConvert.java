package com.syj.eplus.module.crm.convert;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.dict.CommonDict;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import com.syj.eplus.module.infra.api.countryinfo.dto.CountryInfoDTO;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.module.crm.api.cust.dto.CustPocDTO;
import com.syj.eplus.module.crm.api.cust.dto.SettlementDTO;
import com.syj.eplus.module.crm.api.cust.dto.SimpleCustRespDTO;
import com.syj.eplus.module.crm.controller.admin.cust.vo.CustInfoRespVo;
import com.syj.eplus.module.crm.controller.admin.cust.vo.CustRespVO;
import com.syj.eplus.module.crm.dal.dataobject.cust.CustDO;
import com.syj.eplus.module.crm.dal.dataobject.custpoc.CustPocDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Description：
 * @Author：du
 * @Date：2024/3/5 14:39
 */
@Mapper
public interface CustConvert {

    CustConvert INSTANCE = Mappers.getMapper(CustConvert.class);

    @Mapping(target = "customerTypes", ignore = true)
    @Mapping(target = "companyPath", ignore = true)
    SimpleCustRespDTO convertSimpleByCustDO(CustDO custDO);

    default List<SimpleCustRespDTO> convertSimpleListByCustDO(List<CustDO> custDOList) {
        return CollectionUtils.convertList(custDOList, custDO -> {
            SimpleCustRespDTO simpleCustRespVO = convertSimpleByCustDO(custDO);
//            List<Integer> customerTypes = custDO.getCustomerTypes();
//            if (CollUtil.isNotEmpty(customerTypes)) {
//                String customerType = customerTypes.stream().map(s -> {
//                    return DictFrameworkUtils.getDictDataLabel(DictTypeConstants.CUSTOM_TYPE, String.valueOf(s));
//                }).collect(Collectors.joining(CommonDict.COMMA));
//                simpleCustRespVO.setCustomerTypes(customerType);
//            }
            return simpleCustRespVO;
        });
    }

    /**
     * 主要为了转换业务员信息
     *
     * @param custDO                  客户DO实体
     * @param userDeptCache 精简用户DTO缓存
     * @return
     */

    default CustInfoRespVo convert(CustDO custDO, Map<Long, UserDept> userDeptCache, Map<Long, CountryInfoDTO> countryInfoMap) {
        CustInfoRespVo custInfoRespVO = BeanUtils.toBean(custDO, CustInfoRespVo.class);
//        if (CollUtil.isNotEmpty(userDeptCache)) {
//            List<Long> managerIds = custDO.getManagerIds();
//            if (CollUtil.isNotEmpty(managerIds)) {
//                custInfoRespVO.setManagerList(managerIds.stream().map(userDeptCache::get).toList());
//            }
//        }
        Long countryId = custDO.getCountryId();
        if (Objects.nonNull(countryId)) {
            CountryInfoDTO countryInfoDTO = countryInfoMap.get(countryId);
            if (Objects.nonNull(countryInfoDTO)) {
                custInfoRespVO.setCountryName(countryInfoDTO.getName());
            }
        }
        return custInfoRespVO;
    }

    default PageResult<CustRespVO> convertToCustRespPageResult(PageResult<CustDO> custDOPageResult, Map<Long, String> categroyNameMap, Map<Long, CountryInfoDTO> countryInfoMap) {
        if (Objects.isNull(custDOPageResult) || CollUtil.isEmpty(custDOPageResult.getList())) {
            return new PageResult<>();
        }
        List<CustDO> custDOList = custDOPageResult.getList();
        List<CustRespVO> custRespVOList = CollectionUtils.convertList(custDOList, custDO -> {
            CustRespVO custRespVO = BeanUtils.toBean(custDO, CustRespVO.class);
            Long countryId = custDO.getCountryId();
            if (CollUtil.isNotEmpty(countryInfoMap)){
                CountryInfoDTO countryInfoDTO = countryInfoMap.get(countryId);
                if (Objects.nonNull(countryInfoDTO)) {
                    custRespVO.setCountryName(countryInfoDTO.getName());
                    custRespVO.setAreaName(countryInfoDTO.getRegionName());
                }
            }
            if (CollUtil.isNotEmpty(categroyNameMap)){
                List<Long> customerTypes = custDO.getCustomerTypes();
                if (CollUtil.isNotEmpty(customerTypes)){
                    String categroyName = customerTypes.stream().map(categroyNameMap::get).collect(Collectors.joining(CommonDict.COMMA));
                    custRespVO.setCustomerTypesName(categroyName);
                }
            }
            return custRespVO;
        });
        return new PageResult<>(custRespVOList, custDOPageResult.getTotal());
    }

    default PageResult<SimpleCustRespDTO> convertSimpleCustRespPageResult(PageResult<CustDO> custDOPageResult) {
        long total = custDOPageResult.getTotal();
        List<CustDO> custDOList = custDOPageResult.getList();
        return new PageResult<SimpleCustRespDTO>().setList(convertSimpleListByCustDO(custDOList)).setTotal(total);
    }

    SettlementDTO convertSettlementDTO(com.syj.eplus.module.infra.api.settlement.dto.SettlementDTO systemSettlementDTO);

    default List<SettlementDTO> convertDeclarationItemList(List<com.syj.eplus.module.infra.api.settlement.dto.SettlementDTO> settlementDTOList){
        return CollectionUtils.convertList(settlementDTOList,s->{
            return convertSettlementDTO(s);
        });
    }

    List<CustPocDTO> convertCustPocDTO(List<CustPocDO> CustPocDO);
}
