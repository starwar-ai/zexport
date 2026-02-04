package com.syj.eplus.module.scm.convert;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.dict.CommonDict;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.dict.core.util.DictFrameworkUtils;
import cn.iocoder.yudao.framework.ip.core.utils.AreaUtils;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.framework.common.enums.DictTypeConstants;
import com.syj.eplus.module.scm.api.vender.dto.SimpleVenderRespDTO;
import com.syj.eplus.module.scm.controller.admin.vender.vo.VenderInfoRespVO;
import com.syj.eplus.module.scm.controller.admin.vender.vo.VenderRespVO;
import com.syj.eplus.module.scm.dal.dataobject.vender.VenderDO;
import com.syj.eplus.module.scm.dal.dataobject.venderpoc.VenderPocDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Description：
 * @Author：du
 * @Date：2024/3/12 20:10
 */
@Mapper
public interface VenderConvert {
    VenderConvert INSTANCE = Mappers.getMapper(VenderConvert.class);

    /**
     * 主要为了转换业务员信息
     *
     * @param venderDO                供应商DO实体
     * @param userDeptCache 精简用户DTO缓存
     * @return
     */
    default VenderInfoRespVO convert(VenderDO venderDO, Map<Long, UserDept> userDeptCache, Map<Long, VenderPocDO> defaultVenderPocMap) {
        VenderInfoRespVO venderInfoRespVO = BeanUtils.toBean(venderDO, VenderInfoRespVO.class);
        Integer companyAreaId = venderDO.getCompanyAreaId();
        if (Objects.nonNull(companyAreaId)) {
            venderInfoRespVO.setCompanyAreaIdList(AreaUtils.formatList(companyAreaId));
            venderInfoRespVO.setCompanyAreaName(AreaUtils.format(companyAreaId, CommonDict.SLASH_CHAR));
        }
        Integer factoryAreaId = venderDO.getFactoryAreaId();
        if (Objects.nonNull(factoryAreaId)) {
            venderInfoRespVO.setFactoryAreaIdList(AreaUtils.formatList(factoryAreaId));
            venderInfoRespVO.setFactoryAreaName(AreaUtils.format(factoryAreaId, CommonDict.SLASH_CHAR));
        }
        Integer deliveryAreaId = venderDO.getDeliveryAreaId();
        if (Objects.nonNull(deliveryAreaId)) {
            venderInfoRespVO.setDeliveryAreaIdList(AreaUtils.formatList(deliveryAreaId));
            venderInfoRespVO.setDeliveryAreaName(AreaUtils.format(deliveryAreaId, CommonDict.SLASH_CHAR));
        }
        if (CollUtil.isNotEmpty(userDeptCache) && Objects.nonNull(venderDO.getCreator())) {
            UserDept creatorUser = userDeptCache.get(Long.valueOf(venderDO.getCreator()));
            venderInfoRespVO.setCreatorName(creatorUser != null ? creatorUser.getNickname() : "");
            venderInfoRespVO.setCreatorDeptName(creatorUser != null ? creatorUser.getDeptName() : "");
        }
        VenderPocDO venderPocDO = defaultVenderPocMap.get(venderDO.getId());
        if (Objects.nonNull(venderPocDO)) {
            venderInfoRespVO.setVenderPocName(venderPocDO.getName());
            venderInfoRespVO.setVenderPocPhone(venderPocDO.getMobile());
        }
        return venderInfoRespVO;
    }

    default PageResult<VenderRespVO> convertToVenderRespPageResult(PageResult<VenderDO> venderDOPageResult, Map<Long, UserDept> userDeptCache, Map<Long, VenderPocDO> defaultVenderPocMap) {
        if (Objects.isNull(venderDOPageResult) || CollUtil.isEmpty(venderDOPageResult.getList())) {
            return new PageResult<>();
        }
        List<VenderDO> venderDOList = venderDOPageResult.getList();
        List<VenderRespVO> venderRespVOList = CollectionUtils.convertList(venderDOList, venderDO -> {
            VenderInfoRespVO venderRespVO = convert(venderDO, userDeptCache, defaultVenderPocMap);
            return venderRespVO;
        });
        return new PageResult<>(venderRespVOList, venderDOPageResult.getTotal());
    }

    @Mapping(target = "venderType", ignore = true)
    SimpleVenderRespDTO convertSimpleByVenderDO(VenderDO venderDO);

    default List<SimpleVenderRespDTO> convertSimpleListByVenderDO(List<VenderDO> venderDOList) {
        return CollectionUtils.convertList(venderDOList, venderDO -> {
            SimpleVenderRespDTO simpleVenderRespVO = convertSimpleByVenderDO(venderDO);
            Integer venderType = venderDO.getVenderType();
            if (Objects.nonNull(venderType)) {
                String dictDataLabel = DictFrameworkUtils.getDictDataLabel(DictTypeConstants.VENDER_TYPE, String.valueOf(venderType));
                simpleVenderRespVO.setVenderType(dictDataLabel);
            }
            return simpleVenderRespVO;
        });
    }

    default PageResult<SimpleVenderRespDTO> convertSimpleCustRespPageResult(PageResult<VenderDO> custDOPageResult) {
        long total = custDOPageResult.getTotal();
        List<VenderDO> custDOList = custDOPageResult.getList();
        return new PageResult<SimpleVenderRespDTO>().setList(convertSimpleListByVenderDO(custDOList)).setTotal(total);
    }
}
