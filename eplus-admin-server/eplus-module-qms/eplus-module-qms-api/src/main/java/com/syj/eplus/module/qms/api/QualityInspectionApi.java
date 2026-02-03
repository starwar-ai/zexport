package com.syj.eplus.module.qms.api;

import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.module.qms.api.dto.QualityInspectionReqDTO;
import com.syj.eplus.module.qms.api.dto.QualityInspectionRespDTO;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: du
 * @Date: 2024/09/21/17:56
 * @Description:
 */
public interface QualityInspectionApi {

    /**
     * 根据采购合同主键获取验货单数量
     * @param code 采购合同编号
     * @return 验货单数量
     */
    Long getQualityInspectionNumByPurchaseContractCode(String code);

    QualityInspectionRespDTO getDetailById(Long qualityInspectionId);

    Map<Long,QualityInspectionRespDTO> getListByIdList(List<Long> quanlityInspectionIdList);

    void setConcessionReleaseStatus(List<Long> ids,Integer value,List<SimpleFile> annex);

    void setStatus(List<Long> auditableId, List<SimpleFile> annex,String description, List<SimpleFile> picture);

    List<QualityInspectionRespDTO> getList(QualityInspectionReqDTO req);

    Long checkItemList(List<Long> qualityInspectionItemIdList);
}
