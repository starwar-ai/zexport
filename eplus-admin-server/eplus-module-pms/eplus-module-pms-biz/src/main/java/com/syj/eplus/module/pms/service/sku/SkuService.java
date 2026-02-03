package com.syj.eplus.module.pms.service.sku;

import cn.hutool.core.lang.Tuple;
import cn.iocoder.yudao.framework.common.pojo.ConfirmSourceEntity;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import com.syj.eplus.framework.common.entity.*;
import com.syj.eplus.module.pms.api.sku.dto.*;
import com.syj.eplus.module.pms.controller.admin.sku.vo.*;
import com.syj.eplus.module.pms.dal.dataobject.skubom.SkuBomDO;
import com.syj.eplus.module.scm.api.purchasecontract.dto.PurchaseContractGetItemPageReqDTO;
import com.syj.eplus.module.scm.api.purchasecontract.dto.PurchaseContractItemDTO;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 产品 Service 接口
 *
 * @author du
 */
public interface SkuService {

    /**
     * 创建产品
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    SimpleData createSku(@Valid SkuInfoSaveReqVO createReqVO,boolean isCreateSku);

    /**
     * 更新产品
     *
     * @param updateReqVO 更新信息
     */
    void updateSku(@Valid SkuInfoSaveReqVO updateReqVO);

    /**
     * 变更产品资料
     *
     * @param changeReqVO 变更信息
     */
    SimpleData changeSku(@Valid SkuInfoSaveReqVO changeReqVO);

    /**
     * 变更客户成功后
     *
     * @param sku
     */
    void changeSuccess(SkuInfoRespVO sku);

    /**
     * 删除产品
     *
     * @param id 编号
     */
    void deleteSku(Long id, Boolean deleted);

    /**
     * 删除变更产品
     *
     * @param id
     */
    void deleteChangeSku(Long id);

    /**
     * 删除该产品id对应的旧产品资料
     * * @param id 编号
     */
    void deleteOldSku(Long id);

    /**
     * 获得产品
     *
     * @param skuDetailReq 明细请求实体
     * @return 产品
     */
    SkuInfoRespVO getSku(SkuDetailReq skuDetailReq);

    /**
     * 获得变更产品
     *
     * @param skuDetailReq 明细请求实体
     * @return 变更产品
     */
    SkuInfoRespVO getSkuChange(SkuDetailReq skuDetailReq);

    /**
     * 根据sku编号获得最新的产品变更资料
     *
     * @param code
     * @return 产品资料
     */
    SkuInfoRespVO getSkuChangeByCode(String code);

    /**
     * 获得旧产品资料
     *
     * @param sku 请求实体
     * @return 旧产品资料
     */
    SkuInfoRespVO getOldSku(SkuInfoRespVO sku);

    /**
     * 获得产品分页
     *
     * @param pageReqVO 分页查询
     * @return 产品分页
     */
    PageResult<SkuRespVO> getSkuPage(SkuPageReqVO pageReqVO, Boolean onlySkuFlag);

    /**
     * 获得客户产品分页
     *
     * @param pageReqVO 分页查询
     * @return 产品分页
     */
    PageResult<CskuRespVO> getCskuPage(SkuPageReqVO pageReqVO);

    /**
     * 获得变更产品资料分页
     *
     * @param pageReqVO 分页查询
     * @return 产品资料分页
     */
    PageResult<SkuRespVO> getSkuChangePage(SkuPageReqVO pageReqVO, Boolean onlySkuFlag);

    /**
     * 获得客户产品分页
     *
     * @param pageReqVO 分页查询
     * @return 产品分页
     */
    PageResult<CskuRespVO> getCskuChangePage(SkuPageReqVO pageReqVO);

    /**
     * 获得变更产品影响的范围
     *
     * @param changeReqVO
     * @return
     */
    ChangeEffectRespVO getChangeEffect(SkuInfoRespVO changeReqVO);

    /**
     * 获取流程标识
     *
     * @return 流程标识
     */
    String getProcessDefinitionKey();

    /**
     * 获取变更流程标识
     *
     * @return 变更流程标识
     */
    String getChangeProcessDefinitionKey();

    /**
     * 获取客户产品流程标识
     *
     * @return 流程标识
     */
    String getCskuProcessDefinitionKey();

    /**
     * 获取客户产品流程标识
     *
     * @return 流程标识
     */
    String getChangeCskuProcessDefinitionKey();
    /**
     * 获取自营产品流程标识
     *
     * @return 流程标识
     */
    String getChangeOwnBrandProcessDefinitionKey();

    /**
     * 获取辅料产品流程标识
     *
     * @return 流程标识
     */
    String getAuxiliarySkuProcessDefinitionKey();

    /**
     * 获取辅料产品流程标识
     *
     * @return 流程标识
     */
    String getChangeAuxiliarySkuProcessDefinitionKey();

    /**
     * 通过任务
     *
     * @param userId 用户编号
     * @param reqDTO  通过请求
     */
    void approveTask(Long userId, @Valid BpmTaskApproveReqDTO reqDTO);

    /**
     * 不通过任务
     *
     * @param userId 用户编号
     * @param reqDTO  不通过请求
     */
    void rejectTask(Long userId, @Valid BpmTaskRejectReqDTO reqDTO);

    /**
     * 提交任务
     *
     * @param id     业务id
     * @param userId 用户id
     */
    void submitTask(Long id, Long userId);

    /**
     * 更新审核状态状态
     *
     * @param auditableId 审核业务id
     * @param auditStatus 审核状态
     */
    void updateAuditStatus(Long auditableId, Integer auditStatus, String processInstanceId);

    /**
     * 通过变更任务
     *
     * @param userId 用户编号
     * @param reqDTO  通过请求
     */
    void approveChangeTask(Long userId, @Valid BpmTaskApproveReqDTO reqDTO);

    /**
     * 不通过变更任务
     *
     * @param userId 用户编号
     * @param reqDTO  不通过请求
     */
    void rejectChangeTask(Long userId, @Valid BpmTaskRejectReqDTO reqDTO);

    /**
     * 提交变更任务
     *
     * @param skuId  产品id
     * @param userId 用户id
     */
    void submitChangeTask(Long skuId, Long userId);

    /**
     * 更新变更审核状态状态
     *
     * @param auditableId 审核业务id
     * @param auditStatus 审核状态
     */
    void updateChangeAuditStatus(Long auditableId, Integer auditStatus, String processInstanceId);

    /**
     * 根据sku名称模糊搜索最佳匹配前十条sku名称
     *
     * @param name sku名称
     * @return sku名称列表
     */
    List<String> getSkuNameFuzzySearchByName(String name);


    /**
     * 更新公司定价
     *
     * @param id    skuId
     * @param price 价格
     */
    void updateCompanyPrice(Long id, JsonAmount price);


    /**
     * 更新销售状态
     *
     * @param id          skuId
     * @param onshelfFlag 销售状态
     */
    void updateOnshelfFlag(Long id, Integer onshelfFlag);

    /**
     * 更新优势产品
     *
     * @param id            skuId
     * @param advantageFlag 优势产品标识
     */
    void updateAdvantage(Long id, Integer advantageFlag);

    /**
     * 获取sku精简列表
     *
     * @param simpleSkuPageReqVO 请求体
     * @return 精简sku响应体
     */
    PageResult<SimpleSkuDTO> getSimpleSkuDTO(SimpleSkuPageReqVO simpleSkuPageReqVO);

    /**
     * 获取产品精简信息map
     *
     * @param idList 产品id列表
     * @return 产品精简信息map
     */
    Map<Long, SimpleSkuDTO> getSimpleSkuDTOMap(List<Long> idList);

    /**
     * 获取产品精简信息map
     * @param codeList 产品id列表
     * @return 产品精简信息map
     */
    Map<String, SimpleSkuDTO>  getSimpleSkuDTOMapByCode(List<String> codeList);

    /**
     * 通过客户产品任务
     *
     * @param userId 用户编号
     * @param reqDTO  通过请求
     */
    void approveCskuTask(Long userId, @Valid BpmTaskApproveReqDTO reqDTO);

    /**
     * 不通过客户产品任务
     *
     * @param userId 用户编号
     * @param reqDTO  不通过请求
     */
    void rejectCskuTask(Long userId, @Valid BpmTaskRejectReqDTO reqDTO);

    /**
     * 提交客户产品任务
     *
     * @param id     业务id
     * @param userId 用户id
     */
    void submitCskuTask(Long id, Long userId);

    /**
     * 通过变更客户产品任务
     *
     * @param userId 用户编号
     * @param reqDTO  通过请求
     */
    void approveChangeCskuTask(Long userId, @Valid BpmTaskApproveReqDTO reqDTO);

    /**
     * 不通过变更客户产品任务
     *
     * @param userId 用户编号
     * @param reqDTO  不通过请求
     */
    void rejectChangeCskuTask(Long userId, @Valid BpmTaskRejectReqDTO reqDTO);

    /**
     * 提交变更客户产品任务
     *
     * @param skuId  产品id
     * @param userId 用户id
     */
    void submitChangeCskuTask(Long skuId, Long userId);

    /**
     * 通过辅料任务
     *
     * @param userId 用户编号
     * @param reqDTO  通过请求
     */
    void approveAuxiliarySkuTask(Long userId, @Valid BpmTaskApproveReqDTO reqDTO);

    /**
     * 不通过辅料任务
     *
     * @param userId 用户编号
     * @param reqDTO  不通过请求
     */
    void rejectAuxiliarySkuTask(Long userId, @Valid BpmTaskRejectReqDTO reqDTO);

    /**
     * 提交辅料任务
     *
     * @param id     业务id
     * @param userId 用户id
     */
    void submitAuxiliarySkuTask(Long id, Long userId);

    /**
     * 通过变更辅料任务
     *
     * @param userId 用户编号
     * @param reqDTO  通过请求
     */
    void approveChangeAuxiliarySkuTask(Long userId, @Valid BpmTaskApproveReqDTO reqDTO);

    /**
     * 不通过变更辅料任务
     *
     * @param userId 用户编号
     * @param reqDTO  不通过请求
     */
    void rejectChangeAuxiliarySkuTask(Long userId, @Valid BpmTaskRejectReqDTO reqDTO);

    /**
     * 提交变更辅料任务
     *
     * @param id     业务id
     * @param userId 用户id
     */
    void submitChangeAuxiliarySkuTask(Long id, Long userId);

    /**
     * 根据组合产品id获取子产品精简列表
     *
     * @param codeList 组合产品codeList
     * @return 子产品map
     */
    Map<String, List<SimpleSkuDTO>> getSubSkuMap(List<String> codeList);

    Map<Long, SkuDTO> getSkuMap(List<Long> idList);

    Map<String, SkuDTO> getSkuMapByCodeList(List<String> codeList);

    List<SkuDTO> getSkuDTOListByCodeList(List<String> codeList);

    SimpleSkuDTO getSimpleSkuDTO(Long id);

    SkuDTO getSkuDTO(Long id);

    /**
     * 验证自营产品是否存在当前客户对应的客户产品
     *
     * @param skuCodeMap 待校验自营产品编号map
     * @param custCode   客户编号
     * @return key-待校验自营产品编号  value-对应客户产品的精简信息
     */
    Map<String, SimpleData> validateOwnbrandSku(Map<String, Tuple> skuCodeMap, String custCode);

    /**
     * 根据sku编号列表获取对应默认报价中供应商业务员
     *
     * @param skuCodeList sku编号列表
     * @return 采购员缓存
     */
//    Map<String, List<UserDept>> getPurchaseUserListBySkuCodeList(List<String> skuCodeList);

    /**
     * 根据skuId列表获取对应默认报价中供应商业务员
     *
     * @param skuIdList skuId列表
     * @return 采购员缓存
     */
    Map<String, List<UserDept>> getPurchaseUserListBySkuIdList(List<Long> skuIdList);

    /**
     * 查询辅料相关的采购合同
     *
     * @param pageReqVO 查询条件
     * @return 采购记录列表
     */
    PageResult<PurchaseContractItemDTO> getAuxiliarySkuPurchasePage(PurchaseContractGetItemPageReqDTO pageReqVO);

    Boolean toCheckCustChange(SkuCheckCustReqVO reqDTO);

    PageResult<SimpleSkuDTO> getSimpleOwnBrandSkuDTO(SimpleOwnBrandSkuPageReqVO reqDTO);

    /**
     * 获得产品变更
     *
     * @param targetCode 影响范围主键
     * @return 客户变更
     */
    List<ConfirmSourceEntity> getConfirmSourceList(String targetCode, Integer effectRangeType);

    String getProcessDefinitionKeyByBusinessId(Long id);

    List<SkuBomDO> getSonSkuListBySkuCode(String skuCode);

    /**
     * 通过自营产品任务
     *
     * @param userId 用户编号
     * @param reqDTO  通过请求
     */
    void approveOwnBrandTask(Long userId, @Valid BpmTaskApproveReqDTO reqDTO);

    /**
     * 不通过自营产品任务
     *
     * @param userId 用户编号
     * @param reqDTO  不通过请求
     */
    void rejectOwnBrandTask(Long userId, @Valid BpmTaskRejectReqDTO reqDTO);

    /**
     * 提交自营产品任务
     *
     * @param id     业务id
     * @param userId 用户id
     */
    void submitOwnBrandTask(Long id, Long userId);

    /**
     * 获取自营产品流程标识
     *
     * @return 流程标识
     */
    String getOwnBrandProcessDefinitionKey();

    /**
     * 通过变更自营产品任务
     *
     * @param userId 用户编号
     * @param reqDTO  通过请求
     */
    void approveChangeOwnBrandTask(Long userId, @Valid BpmTaskApproveReqDTO reqDTO);

    /**
     * 不通过变更自营产品任务
     *
     * @param userId 用户编号
     * @param reqDTO  不通过请求
     */
    void rejectChangeOwnBrandTask(Long userId, @Valid BpmTaskRejectReqDTO reqDTO);

    /**
     * 提交变更自营产品任务
     *
     * @param skuId  产品id
     * @param userId 用户id
     */
    void submitChangeOwnBrandTask(Long skuId, Long userId);

    /**
     * 根据产品编号获取产品名称
     *
     * @param codeList 产品编号列表
     * @return 产品名称缓存
     */
    Map<String, SkuNameDTO> getSkuNameCacheByCodeList(List<String> codeList);

    String generateCode(Long categoryId,Integer auxiliaryFlag);

    /**
     * 反审核
     * @param id 主键
     * @return 结果
     */
    boolean antiAudit(Long id);

    Map<String, String> getHsDataUnitBySkuCodes(List<String> skuCodeList);

    Map<String, HsDataDTO> getHsDataBySkuCodes(List<String> skuCodeList);

    /**
     * 获取组合产品树
     * @return 组合产品树
     */
    Map<Long,List<SkuBomDTO>> getAllBomDTOMap();

    /**
     * 获取产品类型缓存
     * @param skuCodeList 产品编号列表
     * @return 产品类型缓存
     */
    Map<String,Integer> getSkuTypeMap(List<String> skuCodeList);

    /**
     * 根据id列表查询sku是否存在
     * @param skuIds
     * @return
     */
    Map<Long, Boolean> getSkuExitsByIds(List<Long> skuIds);

    /**
     * 获取组合产品价格
     * @param skuId 产品编号
     * @return 价格
     */
    JsonAmount getCombSkuPrice(Long skuId);

    /**
     * 获取产品规格
     * @param skuCodeList 产品编号
     * @return 产品规格
     */
    Map<String,List<String>> getSkuSpec(List<String> skuCodeList);

    /**
     * 通过产品编号批量获取报价单中税率
     * @param skuCodes 产品编号列表
     * @return 报价单中税率缓存
     */
    Map<String, BigDecimal> getTaxRateBySkuCodeList(Collection<String> skuCodes);

    Map<String,List<SkuBomDTO>> getSonSkuMapBySkuCode(List<String> skuCodes);

    /**
     * 获取基础产品编号
     * @param skuCodes sku产品编号
     * @return 基础产品编号
     */
    Map<String,String> getBasicSkuCode(List<String> skuCodes);

    /**
     * 根据产品编号获取对应自营产品编号列表
     * @param skuCodeList 产品编号列表
     */
    Map<String,List<String>> getOwnSkuCodeListBySkuCode(List<String> skuCodeList);


    /**
     * 验证产品是否有效
     * @param codeList 产品编号
     * @return 是否有效
     */
    Boolean validateSkuApprove(List<String> codeList);


    /**
     * 获取可用产品id
     * @param codeList 产品列表
     * @return 可用产品id
     */
    Set<String> getAvailableSkuIds(Collection<String> codeList);

    /**
     * 通过产品编号获取海关计量单位
     * @param skuCode 产品编号
     * @return 海关计量单位
     */
    String getHsMeasureUnitBySkuCode(String skuCode);

    /**
     * 通过产品编号转换最新id
     * @param codeList 产品编号列表
     * @return 最新id
     */
    Set<SimpleSkuDTO> transformIdByCode(Collection<String> codeList);

    SkuDTO getSkuByBasicCodeAndCskuCodeAndCustCode(String basicSkuCode, String cskuCode, String custCode);

    /**
     * 更新缩略图
     */
    void updateThumbnail(Long limit);

    /**
     * 压缩图片
     * @param pictureList 图片列表
     * @return 压缩图片
     */
     String processImageCompression(List<SimpleFile> pictureList);

    Map<String, SkuDTO> getOwnSkuDTOMapByCodeList(List<String> skuCodes);

    SkuDTO getOwnSkuByBasicCodeAndCskuCodeAndCustCode(String basicSkuCode, String cskuCode);

    Map<String,SimpleSkuDTO> getCskuAndBasicSkuCodeMapByCodes(List<String> skuCodeList);

    void updateCSkuCodeByCode(String code, String cskuCode);

    void updateBarcodeByCode(String code, String barcode);

    /**
     * 根据优势产品标识获取skuId列表
     * @param advantageFlag 优势产品标识
     * @return skuId列表
     * @author 波波
     */
    List<Long> getSkuIdsByAdvantageFlag(Integer advantageFlag);
}
