package com.syj.eplus.module.qms.api;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.module.qms.api.dto.QualityInspectionItemRespDTO;
import com.syj.eplus.module.qms.api.dto.QualityInspectionReqDTO;
import com.syj.eplus.module.qms.api.dto.QualityInspectionRespDTO;
import com.syj.eplus.module.qms.controller.admin.qualityinspection.vo.QualityInspectionDetailReq;
import com.syj.eplus.module.qms.controller.admin.qualityinspection.vo.QualityInspectionItemRespVO;
import com.syj.eplus.module.qms.controller.admin.qualityinspection.vo.QualityInspectionRespVO;
import com.syj.eplus.module.qms.dal.dataobject.qualityinspection.QualityInspectionDO;
import com.syj.eplus.module.qms.dal.dataobject.qualityinspection.QualityInspectionItemDO;
import com.syj.eplus.module.qms.service.qualityinspection.QualityInspectionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: du
 * @Date: 2024/09/21/17:56
 * @Description:
 */
@Service
public class QualityInspectionApiImpl implements QualityInspectionApi{

    @Resource
    private QualityInspectionService qualityInspectionService;


    @Override
    public Long getQualityInspectionNumByPurchaseContractCode(String code) {
        return qualityInspectionService.getQualityInspectionNumByPurchaseContractCode(code);
    }

    @Override
    public QualityInspectionRespDTO getDetailById(Long qualityInspectionId) {
        QualityInspectionRespVO qualityInspection = qualityInspectionService.getQualityInspection(new QualityInspectionDetailReq().setQualityInspectionId(qualityInspectionId));
        QualityInspectionRespDTO dto = BeanUtils.toBean(qualityInspection, QualityInspectionRespDTO.class);
        List<QualityInspectionItemRespVO> children = qualityInspection.getChildren();
        if(CollUtil.isNotEmpty(children)){
            List<QualityInspectionItemRespDTO> dtoItemList = BeanUtils.toBean(children, QualityInspectionItemRespDTO.class);
            dto.setChildren(dtoItemList);
        }
        return dto;
    }

    @Override
    public Map<Long,QualityInspectionRespDTO> getListByIdList(List<Long> quanlityInspectionIdList) {
        List<QualityInspectionDO> doList = qualityInspectionService.getQualityInspectionListByIdList(quanlityInspectionIdList);
        if(CollUtil.isEmpty(doList)){
            return null;
        }
        Map<Long,List<QualityInspectionItemDO>> itemMap = qualityInspectionService.getItemListByQualityInspectionIdList(quanlityInspectionIdList);
        if(CollUtil.isEmpty(itemMap)){
            return null;
        }
        List<QualityInspectionRespDTO> dtoList = BeanUtils.toBean(doList, QualityInspectionRespDTO.class);
        dtoList.forEach(s->{
            List<QualityInspectionItemDO> itemDoList = itemMap.get(s.getId());
            if(CollUtil.isNotEmpty(itemDoList)){
                s.setChildren(BeanUtils.toBean(itemDoList,QualityInspectionItemRespDTO.class));
            }
        });
        return dtoList.stream().collect(Collectors.toMap(QualityInspectionRespDTO::getId,s->s,(s1,s2)->s1));
    }

    @Override
    public void setConcessionReleaseStatus( List<Long> ids,Integer value,List<SimpleFile> annex) {
        qualityInspectionService.setConcessionReleaseStatus(ids,value,annex);
    }

    @Override
    public void setStatus(List<Long> auditableId, List<SimpleFile> annex,String description, List<SimpleFile> picture) {
        qualityInspectionService.setStatus(auditableId,annex,description,picture);
    }

    @Override
    public List<QualityInspectionRespDTO> getList(QualityInspectionReqDTO req) {
        return qualityInspectionService.getDTOList(req);
    }

    @Override
    public Long checkItemList(List<Long> qualityInspectionItemIdList) {
        return qualityInspectionService.checkItemList(qualityInspectionItemIdList);

    }
}
