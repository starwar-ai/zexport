package com.syj.eplus.module.scm.api.vender;

import cn.iocoder.yudao.framework.common.pojo.ConfirmSourceEntity;
import com.syj.eplus.module.infra.api.company.dto.SimpleCompanyDTO;
import com.syj.eplus.module.infra.api.paymentitem.dto.PaymentItemDTO;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.module.scm.api.vender.dto.SimpleVenderResp;
import com.syj.eplus.module.scm.api.vender.dto.SimpleVenderRespDTO;
import com.syj.eplus.module.scm.api.vender.dto.VenderAllDTO;
import com.syj.eplus.module.scm.api.vender.dto.VenderPaymentDTO;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface VenderApi {
    /**
     * 批量获取供应商精简列表
     *
     * @param idList 供应商id列表
     * @return 供应商精简列表
     */
    List<SimpleVenderRespDTO> getSimpleVenderRespDTO(List<Long> idList);

    Map<Long, SimpleVenderRespDTO> getSimpleVenderRespDTOMap(List<Long> idList);

    /**
     * 根据内部企业编码获取供应商精简信息
     * @return
     */
    SimpleVenderRespDTO getSimpleVenderByInternalCompanyId(Long internalCompanyId);

    /**
     * 根据编号获取供应商对应名称(编号为空默认查全部)
     */
    Map<String, String> getVenderNameCache(String code);

    /**
     * 根据供应商编号列表获取供应商业务员缓存
     *
     * @param codeList 供应商编号列表
     * @return 供应商业务员缓存
     */
    Map<String, List<UserDept>> getVenderManagerByCodeList(List<String> codeList);

    /**
     * 根据银行抬头获取供应商精简信息
     *
     * @param bankPoc 银行抬头
     * @return 供应商精简信息
     */
    SimpleVenderResp getSimpleVenderRespByBankPoc(String bankPoc);

    /**
     * 根据编号批量获取供应商精简信息
     *
     * @param codeList 供应商编号列表
     * @return 供应商精简信息
     */
    Map<String, SimpleVenderResp> getSimpleVenderMapByCodes(Collection<String> codeList);

    Map<String, VenderAllDTO> getVenderByCodeList(Collection<String> codeList);

    /**
     * 获得供应商影响变更
     *
     * @param targetCode 影响范围主键
     * @return 供应商影响变更
     */
    List<ConfirmSourceEntity> getConfirmSourceList(String targetCode, Integer effectRangeType);

    SimpleVenderRespDTO getSimpleVenderRespDTOById(Long venderId);

    /**
     * 通过供应商编号获取内部主体信息
     * @param venderCode 供应商编号
     * @return 内部主体信息
     */
    SimpleCompanyDTO getCompanyByVenderCode(String venderCode);

    /**
     * 获取供应商付款方式
     * @param venderId 供应商主键
     * @return 付款方式列表
     */
    List<VenderPaymentDTO> getVenderPaymentList(Long venderId);

    /**
     * 批量获取供应商精简列表
     *
     * @param codes 供应商id列表
     * @return 供应商精简列表
     */
    List<SimpleVenderRespDTO> getSimpleVenderRespDTOList(List<Long> codes);

    /**
     * 根据供应商编号获取付款项
     * @param venderCode 供应商编号
     * @return 付款项列表
     */
    Map<Long,PaymentItemDTO> getPaymentItemDTOListByVenderCode(String venderCode);

    /**
     * 根据供应商编号获取银行抬头
     * @param venderCode 供应商编号
     * @return 银行抬头
     */
    String getBankPocByVenderCode(String venderCode);
}
