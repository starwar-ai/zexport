package com.syj.eplus.module.pms.service.sku;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Tuple;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.dict.CommonDict;
import cn.iocoder.yudao.framework.common.pojo.ConfirmSourceEntity;
import cn.iocoder.yudao.framework.common.pojo.JsonEffectRange;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.common.util.string.StrUtils;
import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;
import cn.iocoder.yudao.framework.dict.core.util.DictFrameworkUtils;
import cn.iocoder.yudao.framework.mybatis.core.enums.ChangeTypeEnum;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.operatelog.core.util.ChangeCompareUtil;
import cn.iocoder.yudao.framework.operatelog.core.util.DiffUtil;
import cn.iocoder.yudao.framework.operatelog.core.util.OperateCompareUtil;
import cn.iocoder.yudao.framework.operatelog.core.util.OperateLogUtils;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.bpm.api.task.BpmProcessInstanceApi;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import cn.iocoder.yudao.module.bpm.enums.task.BpmProcessInstanceResultEnum;
import cn.iocoder.yudao.module.infra.api.config.ConfigApi;
import cn.iocoder.yudao.module.infra.api.file.FileApi;
import com.syj.eplus.module.infra.api.countryinfo.CountryInfoApi;
import com.syj.eplus.module.infra.api.countryinfo.dto.CountryInfoDTO;
import cn.iocoder.yudao.module.system.api.dept.DeptApi;
import cn.iocoder.yudao.module.system.api.dept.dto.DeptRespDTO;
import com.syj.eplus.module.infra.api.formchange.FormChangeApi;
import com.syj.eplus.module.infra.api.formchange.dto.FormChangeDTO;
import com.syj.eplus.module.infra.api.formchange.dto.FormChangeItemDTO;
import cn.iocoder.yudao.module.system.api.logger.OperateLogApi;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.syj.eplus.framework.common.entity.*;
import com.syj.eplus.framework.common.enums.*;
import com.syj.eplus.framework.common.util.NumUtil;
import com.syj.eplus.framework.common.util.TransformUtil;
import com.syj.eplus.module.crm.api.cust.CustApi;
import com.syj.eplus.module.crm.api.cust.dto.CustAllDTO;
import com.syj.eplus.module.crm.enums.cust.DeletedEnum;
import com.syj.eplus.module.crm.enums.cust.EffectMainInstanceFlagEnum;
import com.syj.eplus.module.crm.enums.cust.EffectRangeEnum;
import com.syj.eplus.module.dms.enums.api.ShipmentApi;
import com.syj.eplus.module.dms.enums.api.dto.ShipmentDTO;
import com.syj.eplus.module.infra.api.CodeGeneratorApi;
import com.syj.eplus.module.pms.api.sku.dto.*;
import com.syj.eplus.module.pms.controller.admin.brand.vo.BrandSimpleRespVO;
import com.syj.eplus.module.pms.controller.admin.hsdata.vo.HsdataSimpleRespVO;
import com.syj.eplus.module.pms.controller.admin.sku.vo.*;
import com.syj.eplus.module.pms.convert.sku.SkuConvert;
import com.syj.eplus.module.pms.dal.dataobject.brand.BrandDO;
import com.syj.eplus.module.pms.dal.dataobject.hsdata.HsdataDO;
import com.syj.eplus.module.pms.dal.dataobject.sku.SimpleSkuDO;
import com.syj.eplus.module.pms.dal.dataobject.sku.SkuDO;
import com.syj.eplus.module.pms.dal.dataobject.skuauxiliary.SkuAuxiliaryDO;
import com.syj.eplus.module.pms.dal.dataobject.skubom.SkuBomDO;
import com.syj.eplus.module.pms.dal.mysql.hsdata.HsdataMapper;
import com.syj.eplus.module.pms.dal.mysql.sku.SkuMapper;
import com.syj.eplus.module.pms.service.brand.BrandService;
import com.syj.eplus.module.pms.service.category.CategoryService;
import com.syj.eplus.module.pms.service.hsdata.HsdataService;
import com.syj.eplus.module.pms.service.skuauxiliary.SkuAuxiliaryService;
import com.syj.eplus.module.pms.service.skubom.SkuBomService;
import com.syj.eplus.module.scm.api.purchasecontract.PurchaseContractApi;
import com.syj.eplus.module.scm.api.purchasecontract.dto.PurchaseContractAllDTO;
import com.syj.eplus.module.scm.api.purchasecontract.dto.PurchaseContractGetItemPageReqDTO;
import com.syj.eplus.module.scm.api.purchasecontract.dto.PurchaseContractItemDTO;
import com.syj.eplus.module.scm.api.quoteitem.QuoteitemApi;
import com.syj.eplus.module.scm.api.quoteitem.dto.QuoteitemDTO;
import com.syj.eplus.module.scm.api.vender.VenderApi;
import com.syj.eplus.module.scm.api.vender.dto.SimpleVenderResp;
import com.syj.eplus.module.sms.api.SaleContractApi;
import com.syj.eplus.module.sms.api.dto.HistoryTradePriceDTO;
import com.syj.eplus.module.sms.api.dto.SmsContractAllDTO;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.CREATE_USER_IS_NULL;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.DEPT_CODE_IS_NULL;
import static com.syj.eplus.module.crm.enums.ErrorCodeConstants.*;
import static com.syj.eplus.module.pms.enums.ErrorCodeConstants.*;
import static com.syj.eplus.module.pms.enums.ErrorCodeConstants.CHANGE_NOT_ALLOWED;
import static com.syj.eplus.module.pms.enums.ErrorCodeConstants.FIELD_CHANGE_CONFIG_NOT_EXISTS;
import static com.syj.eplus.module.pms.enums.ErrorCodeConstants.IN_CHANGE_NOT_ALLOWED;
import static com.syj.eplus.module.pms.enums.ErrorCodeConstants.NOT_UPDATE_PROCESS;

/**
 * 财务收款单 Service 实现类
 *
 * @author du
 */
@Service
@Validated
public class SkuServiceImpl implements SkuService {

    private static final String SN_TYPE = "pms_sku";

    @Resource
    private SkuMapper skuMapper;
    @Resource
    private HsdataMapper hsdataMapper;
    private static final String PROCESS_DEFINITION_KEY = "pms_sku";
    private static final String CHANGE_PROCESS_DEFINITION_KEY = "pms_change_sku";
    private static final String AUXILIARY_PROCESS_DEFINITION_KEY = "pms_auxiliary_sku";
    private static final String CHANGE_AUXILIARY_PROCESS_DEFINITION_KEY = "pms_change_auxiliary_sku";
    private static final String CSKU_PROCESS_DEFINITION_KEY = "pms_csku";
    private static final String OWN_BRAND_PROCESS_DEFINITION_KEY = "pms_own_brand";
    private static final String CHANGE_CSKU_PROCESS_DEFINITION_KEY = "pms_change_csku";
    private static final String CHANGE_OWN_BRAND_PROCESS_DEFINITION_KEY = "pms_change_own_brand";
    private static final String OPERATOR_EXTS_KEY = "skuCode";

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    @Resource
    private CodeGeneratorApi codeGeneratorApi;
    @Resource
    private DeptApi deptApi;
    @Resource
    private BpmProcessInstanceApi bpmProcessInstanceApi;
    @Resource
    @Lazy
    private CategoryService categoryService;
    @Resource
    @Lazy
    private SkuBomService skuBomService;
    @Resource
    @Lazy
    private SkuAuxiliaryService skuAuxiliaryService;
    @Resource
    private QuoteitemApi quoteitemApi;
    @Resource
    private CountryInfoApi countryInfoApi;
    @Resource
    private OperateLogApi operateLogApi;
    @Resource
    @Lazy
    private HsdataService hsdataService;
    @Resource
    @Lazy
    private BrandService brandService;
    @Resource
    private VenderApi venderApi;
    @Resource
    private PurchaseContractApi purchaseContractApi;
    @Resource
    private CustApi custApi;

    @Resource
    private SaleContractApi saleContractApi;

    @Resource
    private ShipmentApi shipmentApi;

    @Resource
    private FormChangeApi formChangeApi;
    @Resource
    private AdminUserApi adminUserApi;

    @Resource
    private FileApi fileApi;

    @Resource
    private ConfigApi configApi;

    /**
     * 处理图片压缩，生成缩略图
     *
     * @param pictureList 包含图片信息的请求对象
     */
    public String processImageCompression(List<SimpleFile> pictureList) {
        logger.info("开始图片压缩");
        String fileUrl = "";
        if (CollUtil.isNotEmpty(pictureList)) {
            try {
                for (SimpleFile picture : pictureList) {
                    // 只处理主图
                    if (StrUtil.isNotEmpty(picture.getFileUrl()) && Objects.equals(picture.getMainFlag(), 1)) {
                        byte[] content = fileApi.getFileContent(picture.getFileUrl().substring(picture.getFileUrl().lastIndexOf("get/") + 4));
                        // 压缩图片，增加缩略图尺寸为200x200，保持原始质量
                        if (Objects.isNull(content)) {
                            logger.info("未获取到图片信息url-{}", picture.getFileUrl());
                            continue;
                        }
                        // 获取原始文件扩展名
                        String originalFileName = picture.getFileUrl().substring(picture.getFileUrl().lastIndexOf("/") + 1);
                        String fileExtension = "png"; // 默认png格式
                        if (originalFileName.contains(".")) {
                            fileExtension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1).toLowerCase();
                        }

                        // 使用Thumbnailator生成缩略图
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        // 创建一个白色背景的图片作为基础
                        java.awt.image.BufferedImage background = new java.awt.image.BufferedImage(200, 200, java.awt.image.BufferedImage.TYPE_INT_RGB);
                        java.awt.Graphics2D g = background.createGraphics();
                        g.setColor(java.awt.Color.WHITE);
                        g.fillRect(0, 0, 200, 200);
                        // 加载原始图片并绘制到白色背景上
                        java.awt.image.BufferedImage originalImage = javax.imageio.ImageIO.read(new ByteArrayInputStream(content));
                        if (originalImage != null) {
                            // 计算缩放比例，保持原始图片的宽高比
                            double scale = Math.min(200.0 / originalImage.getWidth(), 200.0 / originalImage.getHeight());
                            int newWidth = (int) (originalImage.getWidth() * scale);
                            int newHeight = (int) (originalImage.getHeight() * scale);

                            // 计算居中位置
                            int x = (200 - newWidth) / 2;
                            int y = (200 - newHeight) / 2;

                            g.drawImage(originalImage, x, y, newWidth, newHeight, null);
                        }
                        g.dispose();
                        javax.imageio.ImageIO.write(background, fileExtension, outputStream);
                        byte[] byteImage = outputStream.toByteArray();
                        // 生成新的文件名，保持原始扩展名
                        String newFileName = originalFileName;
                        if (originalFileName.contains(".")) {
                            String baseName = originalFileName.substring(0, originalFileName.lastIndexOf("."));
                            newFileName = baseName + "_tb." + fileExtension;
                        } else {
                            newFileName = originalFileName + "_tb." + fileExtension;
                        }

                        fileUrl = fileApi.createFile(byteImage, newFileName);
                        // 创建新的缩略图对象
                        logger.info("图片压缩成功，文件名: {}", newFileName);
                        break;
                    }
                }
            } catch (Exception e) {
                logger.error("图片压缩失败", e);
            }
        }
        return fileUrl;
    }

    @Override
    public Map<String, SkuDTO> getOwnSkuDTOMapByCodeList(List<String> skuCodes) {
        List<SkuDO> skuDOS = skuMapper.selectList(SkuDO::getCode,skuCodes).stream().distinct().toList();
        if(CollUtil.isEmpty(skuDOS)){
            return  null;
        }
        List<String> ownSkuCodes = skuDOS.stream().map(SkuDO::getOskuCode).distinct().toList();
        if(CollUtil.isEmpty(ownSkuCodes)){
            return  null;
        }
        List<SkuDO> ownSkus = skuMapper.selectList(SkuDO::getCode,ownSkuCodes).stream().distinct().toList();
        if(CollUtil.isEmpty(ownSkus)){
            return  null;
        }
        Map<String, SkuDTO> ownSkuMap = BeanUtils.toBean(ownSkus, SkuDTO.class).stream().collect(Collectors.toMap(SkuDTO::getCode, s -> s, (s1, s2) -> s1));
        Map<String, SkuDTO> skuMap;
        skuMap = new HashMap<>();
        skuDOS.forEach(s->{
            skuMap.put(s.getCode(),ownSkuMap.get(s.getOskuCode()));
        });
        return skuMap;
    }

    @Override
    public SkuDTO getOwnSkuByBasicCodeAndCskuCodeAndCustCode(String basicSkuCode, String cskuCode) {
        List<SkuDO> skuDOS = skuMapper.selectList(new LambdaQueryWrapperX<SkuDO>()
                .eq(SkuDO::getBasicSkuCode, basicSkuCode)
                .eq(SkuDO::getOskuCode, cskuCode)
                .eq(SkuDO::getAuditStatus,BpmProcessInstanceResultEnum.APPROVE.getResult())
                .eq(SkuDO::getOwnBrandFlag,1)
        ).stream().toList();
        if(CollUtil.isEmpty(skuDOS) || (long) skuDOS.size() == 0){
            return  null;
        }
        return BeanUtils.toBean(skuDOS.get(0),SkuDTO.class);

    }

    @Override
    public Map<String, SimpleSkuDTO> getCskuAndBasicSkuCodeMapByCodes(List<String> skuCodeList) {
        if (CollUtil.isEmpty(skuCodeList)){
            return Map.of();
        }
        List<SkuDO> skuDOS = skuMapper.selectList(new LambdaQueryWrapperX<SkuDO>().select(SkuDO::getCode, SkuDO::getBasicSkuCode, SkuDO::getCskuCode).in(SkuDO::getCode, skuCodeList).eq(SkuDO::getChangeFlag, ChangeFlagEnum.NO.getValue()));
        return skuDOS.stream().collect(Collectors.toMap(SkuDO::getCode, s -> BeanUtils.toBean(s, SimpleSkuDTO.class), (s1, s2) -> BeanUtils.toBean(s1, SimpleSkuDTO.class)));
    }

    /**
     * 根据SKU编码更新客户货号
     * 仅更新客户货号为空的SKU记录
     *
     * @param code SKU编码
     * @param cskuCode 客户货号
     * @author 波波
     */
    @Override
    public void updateCSkuCodeByCode(String code, String cskuCode) {
        List<SkuDO> skuList = skuMapper.selectList(SkuDO::getCode, code).stream().distinct().toList();
        if (CollUtil.isEmpty(skuList)) {
            return;
        }
        List<SkuDO> updateList = skuList.stream()
                .filter(sku -> StrUtil.isEmpty(sku.getCskuCode()))
                .peek(sku -> sku.setCskuCode(cskuCode))
                .toList();
        if (CollUtil.isEmpty(updateList)) {
            return;
        }
        skuMapper.updateBatch(updateList);
    }

    @Override
    public void updateBarcodeByCode(String code, String barcode) {
        List<SkuDO> skuList = skuMapper.selectList(SkuDO::getCode, code).stream().distinct().toList();
        if (CollUtil.isEmpty(skuList)) {
            return;
        }
        List<SkuDO> updateList = skuList.stream()
                .filter(sku -> StrUtil.isEmpty(sku.getBarcode()))
                .peek(sku -> sku.setBarcode(barcode))
                .toList();
        if (CollUtil.isEmpty(updateList)) {
            return;
        }
        skuMapper.updateBatch(updateList);
    }

    /**
     * 提取出来的创建sku基础方法，create和change方法都会调用
     *
     * @param createReqVO
     * @param isChange
     * @return
     */
    private SimpleData createSkuDetail(SkuInfoSaveReqVO createReqVO, boolean isChange, boolean isCreateSku) {
        if (Objects.isNull(createReqVO.getAuditStatus())) {
            createReqVO.setAuditStatus(BpmProcessInstanceResultEnum.UNSUBMITTED.getResult());
        }

        // 处理图片压缩
        String thumbnail = processImageCompression(createReqVO.getPicture());
        createReqVO.setThumbnail(thumbnail);
        SkuDO sku = SkuConvert.INSTANCE.convertSkuDO(createReqVO);
        sku.setChangeStatus(ChangeStatusEnum.NOT_CHANGED.getStatus());
        if (BooleanEnum.YES.getValue().equals(createReqVO.getCustProFlag())) {
            sku.setAuditStatus(BpmProcessInstanceResultEnum.APPROVE.getResult());
        } else if (!BooleanEnum.YES.getValue().equals(createReqVO.getAutoCreateFlag()) && !isChange && !BooleanEnum.YES.getValue().equals(createReqVO.getCustProFlag())) {
            sku.setAuditStatus(BpmProcessInstanceResultEnum.UNSUBMITTED.getResult());
        }
        if (isChange) {
            sku.setModelKey(CHANGE_PROCESS_DEFINITION_KEY);
            sku.setCreateTime(createReqVO.getCreateTime());
        }
        // 基础编号赋值
        Integer custProFlag = sku.getCustProFlag();
        Integer ownBrandFlag = sku.getOwnBrandFlag();
        if ((BooleanEnum.YES.getValue().equals(custProFlag) || BooleanEnum.YES.getValue().equals(ownBrandFlag)) && !BooleanEnum.YES.getValue().equals(sku.getAgentFlag())) {
            if(Objects.isNull(createReqVO.getBasicSkuCode() )){
                throw exception(BASE_SKU_CODE_NOT_EMPTY);
            }
            sku.setBasicSkuCode(createReqVO.getBasicSkuCode());
        } else {
            sku.setBasicSkuCode(sku.getCode());
        }
        if (BooleanEnum.YES.getValue().equals(createReqVO.getAgentFlag())) {
            sku.setCommodityInspectionFlag(BooleanEnum.NO.getValue());
        }

        // 插入主信息
        skuMapper.insert(sku);
        SimpleData simpleData = createSkuDetailBiz(createReqVO, sku, isChange, isCreateSku);
        // 返回
        return simpleData;
    }

    /**
     * 提取出来的创建sku子项基础方法，create和update都会调用
     *
     * @param createReqVO
     * @param sku
     * @param isChange
     * @return
     */
    private SimpleData createSkuDetailBiz(SkuInfoSaveReqVO createReqVO, SkuDO sku, boolean isChange, boolean isCreateSku) {
        SimpleData simpleData = new SimpleData();
        Long skuId = sku.getId();
        simpleData.setId(skuId).setCode(sku.getCode()).setName(sku.getName()).setNameEng(sku.getNameEng());
        if (SubmitFlagEnum.SUBMIT.getStatus().equals(createReqVO.getSubmitFlag())) {
            if (BooleanEnum.YES.getValue().equals(createReqVO.getCustProFlag())) {
                // 自动生成组合产品子产品对应客户产品
                autoCreateCustSubSku(sku);
            } else if (SkuTypeEnum.AUXILIARY_MATERIALS.getValue().equals(createReqVO.getSkuType())) {
                if (isChange) {
                    submitChangeAuxiliarySkuTask(sku.getId(), WebFrameworkUtils.getLoginUserId());
                } else {
                    submitAuxiliarySkuTask(sku.getId(), WebFrameworkUtils.getLoginUserId());
                }
            } else {
                if (isChange) {
                    submitChangeTask(sku.getId(), WebFrameworkUtils.getLoginUserId());
                } else {
                    submitTask(sku.getId(), WebFrameworkUtils.getLoginUserId());
                }
            }
        }
        // 插入供应商报价明细
        List<QuoteitemDTO> quoteitemList = createReqVO.getQuoteitemList();
        if (CollUtil.isNotEmpty(quoteitemList)) {
            quoteitemList.forEach(s -> {
                s.setSkuCode(createReqVO.getCode());
                s.setId(null);
                s.setSkuId(sku.getId());
                s.setTotalPrice(getTotalPrice(s.getWithTaxPrice(), s.getPackagingPrice(), s.getShippingPrice()));
            });
            quoteitemApi.batchInsertQuoteitem(quoteitemList);
        }
        //批量插入辅料配比
        List<SkuAuxiliaryDO> skuAuxiliaryList = createReqVO.getSkuAuxiliaryList();
        if (CollUtil.isNotEmpty(skuAuxiliaryList)) {
            skuAuxiliaryList.forEach(s -> {
                s.setSkuCode(createReqVO.getCode()).setSkuName(createReqVO.getName());  //新建产品时前端没有编号，后台组装
            });
            skuAuxiliaryService.batchDeleteBySkuCode(sku.getCode());
            skuAuxiliaryService.batchSave(skuAuxiliaryList);
        }
        // 新增产品不在这里处理  审核通过后创建对应子产品
        if (!isCreateSku) {
            autoCreateCustSubSku(sku);
        }
        // 仅当基础组合产品创建时执行 因为客户组套已经在审核的时候处理过了
        if ((createReqVO.getSkuType().equals(SkuTypeEnum.PRODUCT_MIX.getValue()) && createReqVO.getCustProFlag().equals(BooleanEnum.NO.getValue()))||isChange) {
            List<SkuBomDO> skuBomDOList = new ArrayList<>();
            // 批量插入子产品
            List<SimpleSkuSaveReqVO> subSkuIdList = createReqVO.getSubProductList();
            if (CollUtil.isNotEmpty(subSkuIdList)) {
                skuBomDOList.addAll(subSkuIdList.stream().map(s -> new SkuBomDO().setSkuCode(s.getSkuCode()).setSkuId(s.getId()).setParentSkuId(skuId).setQty(s.getQty()).setSkuType(SkuTypeEnum.PRODUCT_MIX.getValue()).setSkuCode(s.getSkuCode())).toList());
            }
            // 批量插入配件列表
            List<SimpleSkuSaveReqVO> partsIdList = createReqVO.getAccessoriesList();
            if (CollUtil.isNotEmpty(partsIdList)) {
                skuBomDOList.addAll(partsIdList.stream().map(s -> new SkuBomDO().setSkuCode(s.getSkuCode()).setSkuId(s.getId()).setParentSkuId(skuId).setQty(s.getQty()).setSkuType(SkuTypeEnum.ACCESSORIES.getValue()).setSkuCode(s.getSkuCode())).toList());
            }
            if (CollUtil.isNotEmpty(skuBomDOList)) {
                skuBomService.batchDeleteSkuBomByParentId(skuId);
                skuBomService.batchSaveSkuBom(skuBomDOList);
            }
        }
        // 返回
        return simpleData;
    }


    private void autoCreateCustSubSku(SkuDO sku) {
        if (SkuTypeEnum.PRODUCT_MIX.getValue().equals(sku.getSkuType()) && BooleanEnum.YES.getValue().equals(sku.getCustProFlag())) {
            // 获取当前产品子节点缓存
            Map<Long, List<SkuBomDO>> skuBomDOMapByParentId = skuBomService.getSkuBomDOMapByParentId(sku.getSourceId());
            if (CollUtil.isEmpty(skuBomDOMapByParentId)) {
                return;
            }
            // 获取所有叶子节点id列表
            List<String> skuCodeList = skuBomDOMapByParentId.values().stream()
                    .flatMap(List::stream)
                    .map(SkuBomDO::getSkuCode)
                    .distinct()
                    .toList();
            List<SkuDO> skuDOList = skuMapper.selectList(new LambdaQueryWrapperX<SkuDO>().in(SkuDO::getCode, skuCodeList).eq(SkuDO::getChangeFlag, ChangeFlagEnum.NO.getValue()));
            if (CollUtil.isEmpty(skuDOList)) {
                return;
            }
            Map<String, SkuDO> custSkuMap = getCustSkuCodeList(sku.getCustCode(), skuCodeList);
            // 仅当子产品不是客户产品或子产品客户编号非当前产品客户编号时自动创建客户产品
            Map<String, SkuDO> skuDOMap = skuDOList.stream().collect(Collectors.toMap(SkuDO::getCode, s -> s));
            dealChildNodeSku(skuBomDOMapByParentId, skuDOMap, sku.getSourceId(), sku.getId(), sku.getCustCode(), custSkuMap);
        }
    }

    private Map<String, SkuDO> getCustSkuCodeList(String custCode, List<String> skuCodeList) {
        LambdaQueryWrapper<SkuDO> queryWrapper = new LambdaQueryWrapperX<SkuDO>().eq(SkuDO::getCustCode, custCode).eq(SkuDO::getAuditStatus, BpmProcessInstanceResultEnum.APPROVE.getResult()).eq(SkuDO::getChangeFlag, ChangeFlagEnum.NO.getValue()).and(x -> x.in(SkuDO::getCode, skuCodeList).or().in(SkuDO::getBasicSkuCode, skuCodeList));
        List<SkuDO> skuDOS = skuMapper.selectList(queryWrapper);
        Map<String, SkuDO> skuMap = new HashMap<>();
        if (CollUtil.isNotEmpty(skuDOS)) {
            skuDOS.forEach(s -> {
                skuMap.put(s.getCode(), s);
                skuMap.put(s.getBasicSkuCode(), s);
            });
        }
        return skuMap;
    }

    private JsonAmount getTotalPrice(JsonAmount withTaxPrice, JsonAmount packagingPrice, JsonAmount shippingPrice) {
        JsonAmount totalPrice = new JsonAmount();
        if (Objects.isNull(withTaxPrice)) {
            return totalPrice;
        }
        String currency = withTaxPrice.getCurrency();
        BigDecimal withTaxPriceAmount = withTaxPrice.getAmount();
        BigDecimal packagingAmount = BigDecimal.ZERO;
        BigDecimal shippingAmount = BigDecimal.ZERO;
        if (Objects.nonNull(packagingPrice) && Objects.nonNull(packagingPrice.getAmount())) {
            packagingAmount = packagingPrice.getAmount();
        }
        if (Objects.nonNull(shippingPrice) && Objects.nonNull(shippingPrice.getAmount())) {
            shippingAmount = shippingPrice.getAmount();
        }
        totalPrice.setCurrency(currency).setAmount(withTaxPriceAmount.add(packagingAmount).add(shippingAmount));
        return totalPrice;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SimpleData createSku(SkuInfoSaveReqVO createReqVO, boolean isCreateSku) {
        //判断相同的产品编号和相同的客户编号不能重复创建
        if (Objects.equals(createReqVO.getCustProFlag(), BooleanEnum.YES.getValue())) {
            if (!BooleanEnum.YES.getValue().equals(createReqVO.getAgentFlag()) && StrUtil.isEmpty(createReqVO.getSourceCode()) || StrUtil.isEmpty(createReqVO.getCustCode())) {
                throw exception(CUST_SKU_CREATE_MISS);
            }
            if (Objects.isNull(createReqVO.getAgentFlag()) || BooleanEnum.NO.getValue().equals(createReqVO.getAgentFlag())) {
                var query = new LambdaQueryWrapperX<SkuDO>()
                        .eq(SkuDO::getCustProFlag, BooleanEnum.YES.getValue())
                        .eq(SkuDO::getBasicSkuCode, createReqVO.getBasicSkuCode())
                        .eq(SkuDO::getChangeFlag, ChangeFlagEnum.NO.getValue())
                        .eq(SkuDO::getCustCode, createReqVO.getCustCode());
                if (Objects.nonNull(createReqVO.getCskuCode())) {
                    query.eq(SkuDO::getCskuCode, createReqVO.getCskuCode());
                }
                Optional<SkuDO> first = skuMapper.selectList(query).stream().findFirst();
                if (first.isPresent()) {
                    throw exception(CSKU_CODE_EXIST);
                }
            }
        } else if (createReqVO.getOwnBrandFlag() != null && Objects.equals(createReqVO.getOwnBrandFlag(), BooleanEnum.YES.getValue())) {
            if (StrUtil.isEmpty(createReqVO.getOskuCode())) {
                throw exception(OSKU_CODE_NULL);
            }
            var query = new LambdaQueryWrapperX<SkuDO>()
                    .eq(SkuDO::getOwnBrandFlag, BooleanEnum.YES.getValue())
                    .eq(SkuDO::getOskuCode, createReqVO.getOskuCode())
                    .eq(SkuDO::getCustProFlag, BooleanEnum.NO.getValue())
                    .eq(SkuDO::getChangeFlag, ChangeFlagEnum.NO.getValue());

            Optional<SkuDO> first = skuMapper.selectList(query).stream().findFirst();
            if (first.isPresent()) {
                throw exception(OSKU_CODE_EXIST);
            }
        } else if (Objects.equals(createReqVO.getOwnBrandFlag(), BooleanEnum.NO.getValue()) || createReqVO.getOwnBrandFlag() == null) {
            //基础产品编号重复校验
            if (!SkuTypeEnum.AUXILIARY_MATERIALS.getValue().equals(createReqVO.getSkuType()) && StrUtil.isEmpty(createReqVO.getCode())) {
                throw exception(BASE_SKU_CODE_NOT_EMPTY);
            }
            var query = new LambdaQueryWrapperX<SkuDO>().eq(SkuDO::getCustProFlag, BooleanEnum.NO.getValue())
                    .eq(SkuDO::getOwnBrandFlag, BooleanEnum.NO.getValue())
                    .ne(SkuDO::getSkuType, SkuTypeEnum.AUXILIARY_MATERIALS.getValue())
                    .eq(SkuDO::getCode, createReqVO.getCode());
            Optional<SkuDO> first = skuMapper.selectList(query).stream().findFirst();
            if (first.isPresent()) {
                throw exception(BASE_SKU_CODE_NUL);
            }
        }
        String code = createReqVO.getCode();
        if (StrUtil.isEmpty(code)) {
            if (BooleanEnum.YES.getValue().equals(createReqVO.getAgentFlag())) {
                String agentCodePrefix = configApi.getValueByConfigKey("agent_code_prefix");
                if (StrUtil.isEmpty(agentCodePrefix)) {
                    throw exception(AGENT_CODE_PREFIX_NOT_EMPTY);
                }
                code = codeGeneratorApi.getCodeGenerator("agent_sku", agentCodePrefix);
                while (skuMapper.exists(new LambdaQueryWrapperX<SkuDO>().eq(SkuDO::getCode, code))){
                    code = codeGeneratorApi.getCodeGenerator("agent_sku", agentCodePrefix);
                }
            } else {
                if (BooleanEnum.YES.getValue().equals(createReqVO.getCustProFlag())||BooleanEnum.YES.getValue().equals(createReqVO.getOwnBrandFlag())){
                    code = IdUtil.fastSimpleUUID();
                }else {
                    code = generateCode(createReqVO.getCategoryId(), createReqVO.getSkuType());
                    while (skuMapper.exists(new LambdaQueryWrapperX<SkuDO>().eq(SkuDO::getCode, code))){
                        code = generateCode(createReqVO.getCategoryId(), createReqVO.getSkuType());
                    }
                }
            }
        }else {
            if (skuMapper.exists(new LambdaQueryWrapperX<SkuDO>().eq(SkuDO::getCode, code))) {
                throw exception(SKU_CODE_EXIST);
            }
        }
        createReqVO.setCode(code);
        //新增版本默认为1
        createReqVO.setVer(1);
        createReqVO.setId(null);
        if (Objects.isNull(createReqVO.getCustProFlag())) {
            createReqVO.setCustProFlag(BooleanEnum.NO.getValue());
        }
        // 补充操作日志明细
        OperateLogUtils.setContent(String.format("【新增sku】%s", code));
        OperateLogUtils.addExt(OPERATOR_EXTS_KEY, code);
        return createSkuDetail(createReqVO, false, isCreateSku);
    }

    private void dealChildNodeSku(Map<Long, List<SkuBomDO>> skuBomDOMap, Map<String, SkuDO> skuDOMap, Long parentId, Long custSkuId, String custCode, Map<String, SkuDO> custSkuMap) {
        if (CollUtil.isEmpty(skuBomDOMap)) {
            return;
        }
        List<SkuBomDO> skuBomDOList = skuBomDOMap.get(parentId);
        if (CollUtil.isEmpty(skuBomDOList)) {
            return;
        }
        skuBomDOList.forEach(s -> {
            String skuCode = s.getSkuCode();
            Long skuId = s.getSkuId();
            // 重新插入bom时skucode会变 所以此处进行赋值
            if (CollUtil.isEmpty(skuDOMap)) {
                return;
            }
            SkuDO skuDO = skuDOMap.get(skuCode);
            if (Objects.isNull(skuDO)) {
                return;
            }
            // 仅当前客户不存在该产品的客户产品才进行创建
            if (!custSkuMap.containsKey(s.getSkuCode())) {
                //新增产品
                skuDO.setId(null);
                skuDO.setBasicSkuCode(skuCode);
                skuDO.setCode(getCode(skuDO.getCategoryId()));
                skuDO.setCustProFlag(BooleanEnum.YES.getValue());
                skuDO.setCustCode(custCode);
                skuDO.setAutoCreateFlag(BooleanEnum.YES.getValue());
                skuDO.setAuditStatus(BpmProcessInstanceResultEnum.APPROVE.getResult());
                skuDO.setSourceId(s.getSkuId());
                skuDO.setSourceCode(s.getSkuCode());
                skuMapper.insert(skuDO);
                //新增报价
                List<QuoteitemDTO> quoteitemDTOBySkuCode = quoteitemApi.getQuoteitemDTOBySkuId(skuDO.getSourceId());
                if (CollUtil.isNotEmpty(quoteitemDTOBySkuCode)) {
                    quoteitemDTOBySkuCode.forEach(quoteitemDTO -> {
                        quoteitemDTO.setId(null);
                        quoteitemDTO.setSkuId(skuDO.getId());
                        quoteitemDTO.setSkuCode(skuDO.getCode());
                        quoteitemDTO.setTotalPrice(getTotalPrice(quoteitemDTO.getWithTaxPrice(), quoteitemDTO.getPackagingPrice(), quoteitemDTO.getShippingPrice()));
                    });
                    quoteitemApi.batchInsertQuoteitem(quoteitemDTOBySkuCode);
                }
                s.setSkuId(skuDO.getId());
                s.setSkuCode(skuDO.getCode());
            } else {
                SkuDO existSkuDO = custSkuMap.get(s.getSkuCode());
                s.setSkuId(existSkuDO.getId());
                s.setSkuCode(existSkuDO.getCode());
            }
            //新增bom
            s.setId(null);
            s.setParentSkuId(custSkuId);
            skuBomService.insertSkuBom(s);
            // 递归处理节点中组合产品
            if (skuBomDOMap.containsKey(skuId)) {
                dealChildNodeSku(skuBomDOMap, skuDOMap, skuId, skuDO.getId(), custCode, custSkuMap);
            }
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSku(SkuInfoSaveReqVO updateReqVO) {
        // 已在审核中的数据不允许修改
        if (BpmProcessInstanceResultEnum.PROCESS.getResult().equals(updateReqVO.getAuditStatus())) {
            throw exception(NOT_UPDATE_PROCESS);
        }

        // 处理图片压缩
        String thumbnail = processImageCompression(updateReqVO.getPicture());
        updateReqVO.setThumbnail(thumbnail);
        // 校验存在
        validateSkuExists(updateReqVO.getId());
        SkuDO skuDO = BeanUtils.toBean(updateReqVO, SkuDO.class);
        if (BooleanEnum.YES.getValue().equals(updateReqVO.getCustProFlag()) && BooleanEnum.YES.getValue().equals(updateReqVO.getSubmitFlag())) {
            skuDO.setAuditStatus(BpmProcessInstanceResultEnum.APPROVE.getResult());
        }
        // 基础编号赋值
        Integer custProFlag = skuDO.getCustProFlag();
        Integer ownBrandFlag = skuDO.getOwnBrandFlag();
        if (!BooleanEnum.YES.getValue().equals(custProFlag) && !BooleanEnum.YES.getValue().equals(ownBrandFlag)) {
            skuDO.setBasicSkuCode(skuDO.getCode());
        }
        skuMapper.updateById(skuDO);

        SkuDO updateObj = SkuConvert.INSTANCE.convertSkuDO(updateReqVO);
        List<ChangeRecord> changeRecords = new OperateCompareUtil<SkuDO>().compare(skuDO, updateObj);
        Long oldBrandId = skuDO.getBrandId();
        Long brandId = updateObj.getBrandId();
        Long oldHsCodeId = skuDO.getHsCodeId();
        Long hsCodeId = updateObj.getHsCodeId();
        // 转换品牌名称
        TransformUtil.transformField(changeRecords, Arrays.asList(String.valueOf(oldBrandId), String.valueOf(brandId)),
                brandService::getBrandSimpleList,
                CommonDict.BRAND_FIELD_NAME::equals,
                BrandSimpleRespVO::getName);
        // 转换海关编码
        TransformUtil.transformField(changeRecords, Arrays.asList(String.valueOf(oldHsCodeId), String.valueOf(hsCodeId)),
                hsdataService::getHsdataSimpleList,
                CommonDict.HSCODE_FIELD_NAME::equals,
                HsdataSimpleRespVO::getUnit);
        // 处理供应商报价
        dealQuoteitem(skuDO.getId(), updateReqVO, changeRecords);
        // 删除供应商报价
        quoteitemApi.batchDeleteQuoteitem(null, skuDO.getId());
        // 处理子产品
        dealSkuBomOperatorLog(skuDO.getId(), changeRecords, updateReqVO, SkuTypeEnum.PRODUCT_MIX.getValue(), "子产品");
        // 处理配件
        dealSkuBomOperatorLog(skuDO.getId(), changeRecords, updateReqVO, SkuTypeEnum.ACCESSORIES.getValue(), SkuTypeEnum.ACCESSORIES.getName());
        skuBomService.batchDeleteSkuBomByParentId(skuDO.getId());
        // 处理图片
        dealPicture(skuDO.getPicture(), updateReqVO.getPicture(), changeRecords);
        // 插入操作日志
        if (CollUtil.isNotEmpty(changeRecords)) {
            OperateCompareUtil<Object> operateCompareUtil = new OperateCompareUtil<>();
            AtomicReference<String> content = new AtomicReference<>();
            changeRecords.forEach(s -> {
                content.set(operateCompareUtil.apply(content.get(), s.getFieldName(), s.getOldValue(), s.getValue(), ChangeRecordTypeEnum.GENERAL_CHANGE.getType()));
            });
            OperateLogUtils.setContent(content.get());
        }
        //删除辅料配比信息
        skuAuxiliaryService.batchDeleteBySkuCode(skuDO.getCode());

        OperateLogUtils.addExt(OPERATOR_EXTS_KEY, updateReqVO.getCode());
        //删除后创建
        createSkuDetailBiz(updateReqVO, skuDO, false, true);
    }

    /**
     * 变更sku资料
     *
     * @param changeReqVO 更新信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SimpleData changeSku(SkuInfoSaveReqVO changeReqVO) {
        //已经有高版本产品信息，无法变更
        List<SkuDO> skuDOs = skuMapper.selectList(new LambdaQueryWrapperX<SkuDO>()
                .eqIfPresent(SkuDO::getCode, changeReqVO.getCode())
                .eqIfPresent(SkuDO::getChangeFlag, ChangeFlagEnum.NO.getValue())
                .eqIfPresent(SkuDO::getVer, changeReqVO.getVer() + 1)
        );
        if (CollUtil.isNotEmpty(skuDOs)) {
            throw exception(CHANGE_NOT_ALLOWED);
        }
        SkuDO skuDO = validateSkuExists(changeReqVO.getId());
        if (Objects.equals(skuDO.getChangeStatus(), ChangeStatusEnum.IN_CHANGE.getStatus())) {
            throw exception(IN_CHANGE_NOT_ALLOWED);
        }
        Long oldId = changeReqVO.getId();
        SkuInfoRespVO oldSku = getSku(new SkuDetailReq().setSkuId(oldId));
        //基础产品海关编码发生改变，基础产品海关编码变更影响客户查产品配置为是将客户产品的海关编码更新
        if (Objects.nonNull(oldSku.getHsdata()) && !oldSku.getHsdata().getId().equals(changeReqVO.getHsCodeId()) && changeReqVO.getCustProFlag().equals(BooleanEnum.NO.getValue()) && changeReqVO.getOwnBrandFlag().equals(BooleanEnum.NO.getValue())) {
            Map<String, String> configMap = configApi.getConfigMap();
            if (configMap.get("bsku_hscode_change_effect_csku_flag").equals(BooleanEnum.YES.getValue().toString())) {
                List<SkuDO> cskuDOList = skuMapper.selectList(new LambdaQueryWrapperX<SkuDO>().eq(SkuDO::getSourceCode, changeReqVO.getCode()).eq(SkuDO::getCustProFlag, BooleanEnum.YES.getValue())).stream().toList();
                if (!CollectionUtils.isEmpty(cskuDOList)) {
                    cskuDOList.forEach(item -> {
                        item.setHsCodeId(changeReqVO.getHsCodeId());
                    });
                    skuMapper.updateBatch(cskuDOList);
                }
            }
        }
        //查询新产品与原产品
        SkuInfoRespVO sku = BeanUtils.toBean(changeReqVO, SkuInfoRespVO.class);
        sku.setQuoteitemDTOList(changeReqVO.getQuoteitemList());
        sku.setVer(sku.getVer() + 1);
        boolean effectRangeFlag = (Objects.nonNull(sku.getCustProFlag()) && BooleanEnum.YES.getValue().equals(sku.getCustProFlag())) || (Objects.nonNull(sku.getOwnBrandFlag()) && BooleanEnum.YES.getValue().equals(sku.getOwnBrandFlag()));
        if (effectRangeFlag) {
            ChangeEffectRespVO effectRange = getChangeEffect(sku);
            if (Objects.nonNull(effectRange)) {
                changeReqVO.setEffectRangeList(effectRange.getEffectRangeList());
            }
        }
        changeReqVO.setSourceId(oldId);
        if (BooleanEnum.YES.getValue().equals(changeReqVO.getCustProFlag())) {
            skuDO.setChangeStatus(ChangeStatusEnum.CHANGED.getStatus());
        } else {
            skuDO.setChangeStatus(ChangeStatusEnum.IN_CHANGE.getStatus());
        }
        skuMapper.updateById(skuDO);
        //数据库自增 将id置空
        changeReqVO.setId(null);
        //版本加1
        changeReqVO.setVer(changeReqVO.getVer() + 1);
        changeReqVO.setChangeFlag(ChangeFlagEnum.YES.getValue());
        changeReqVO.setOldData(oldSku);
        // 变更时使用oldSku的创建时间
        if (Objects.nonNull(oldSku) && Objects.nonNull(oldSku.getCreateTime())) {
            changeReqVO.setCreateTime(oldSku.getCreateTime());
        }

        // 如果都是普通级直接变更 客户产品变更不需要审批
        Boolean changeLevelFlag = getChangeLevelFlag(sku);
        if (BooleanEnum.YES.getValue().equals(changeReqVO.getCustProFlag())) {
            changeLevelFlag = false;
        }
        if (!changeLevelFlag) {
            changeReqVO.setAuditStatus(BpmProcessInstanceResultEnum.APPROVE.getResult());
            changeReqVO.setSubmitFlag(BooleanEnum.NO.getValue());
            SimpleData simpleData = createSkuDetail(changeReqVO, true, false);
            SkuInfoRespVO skuInfoRespVo = getSkuDetail(new SkuDetailReq().setSkuId(simpleData.getId()), true);
            //自动审核通过
            updateChangeAuditStatus(simpleData.getId(), BpmProcessInstanceResultEnum.APPROVE.getResult(), null);
            skuInfoRespVo.setSourceId(oldId);
            changeSuccess(skuInfoRespVo);
            return simpleData;
        } else {
            changeReqVO.setSubmitFlag(BooleanEnum.YES.getValue());
            changeReqVO.setAuditStatus(BpmProcessInstanceResultEnum.PROCESS.getResult());
            SimpleData simpleData = createSkuDetail(changeReqVO, true, true);
            return simpleData;
        }
    }

    @Override
    public void changeSuccess(SkuInfoRespVO sku) {
        // 更新原产品对应客户产品
        SkuDO updateDO = new SkuDO();
        updateDO.setSourceId(sku.getId());
        if (!BooleanEnum.YES.getValue().equals(sku.getCustProFlag())&&!BooleanEnum.YES.getValue().equals(sku.getOwnBrandFlag())){
            updateDO.setCategoryId(sku.getCategoryId());
            updateDO.setSourceFlag(sku.getSourceFlag());
            updateDO.setCommodityInspectionFlag(sku.getCommodityInspectionFlag());
            updateDO.setMeasureUnit(sku.getMeasureUnit());
            skuMapper.update(updateDO, new LambdaQueryWrapperX<SkuDO>().eq(SkuDO::getBasicSkuCode, sku.getCode()));
        }else {
            skuMapper.update(updateDO, new LambdaQueryWrapperX<SkuDO>().eq(SkuDO::getSourceId, sku.getSourceId()));
        }

        //删除原客户
        deleteOldSku(sku.getId());
        // 更改变更状态
        updateChangeStatus(sku.getSourceId(), ChangeStatusEnum.CHANGED.getStatus());
        //更新bom表
        autoCreateCustSubSku(updateDO);
//        skuBomService.updateSkuBom(sku.getSourceId(),sku.getId());
        if (CollUtil.isNotEmpty(sku.getEffectRangeList())) {
            sku.getEffectRangeList().forEach(s -> {
                if (EffectRangeEnum.SMS.getValue().equals(s.getEffectRangeType())) {
                    saleContractApi.updateConfirmFlag(ConfirmFlagEnum.NO.getValue(), s.getEffectRangeCode());
                }
                if (EffectRangeEnum.SCM.getValue().equals(s.getEffectRangeType())) {
                    purchaseContractApi.updateConfirmFlag(ConfirmFlagEnum.NO.getValue(), s.getEffectRangeCode());
                }
                if (EffectRangeEnum.DMS.getValue().equals(s.getEffectRangeType())) {
                    shipmentApi.updateConfirmFlag(ConfirmFlagEnum.NO.getValue(), s.getEffectRangeCode());
                }
            });
        }
    }

    private void updateChangeStatus(Long id, Integer status) {
        SkuDO skuDO = new SkuDO();
        skuDO.setId(id);
        skuDO.setChangeStatus(status);
        skuMapper.updateById(skuDO);
    }

    @Override
    public void deleteSku(Long id, Boolean forceDeleted) {
        // 校验存在
        SkuDO skuDO = validateSkuExists(id);
        //审核中的数据不允许修改,删除
//        if (!forceDeleted && !BpmProcessInstanceResultEnum.UNSUBMITTED.getResult().equals(skuDO.getAuditStatus())) {
//            throw exception(UNSUBMIT_EDIT_DEL_NOT_ALLOWED);
//        }
        // 删除
        skuMapper.deleteById(id);
//        quoteitemApi.batchDeleteQuoteitem(skuDO.getCode(), skuDO.getId());
    }

    @Override
    public void deleteOldSku(Long id) {
        // 校验存在
        SkuDO skuDO = validateSkuExists(id);
        // 查询该id对应编号所有的数据
        List<SkuDO> skuDOs = skuMapper.selectList(new LambdaQueryWrapperX<SkuDO>()
                .eqIfPresent(SkuDO::getCode, skuDO.getCode())
                .orderByDesc(SkuDO::getId)
        );

//        //审核中的数据不允许修改,删除
//        if (!BpmProcessInstanceResultEnum.APPROVE.getResult().equals(skuDO.getAuditStatus())) {
//            throw exception(UNAPPROVE_EDIT_DEL_NOT_ALLOWED);
//        }
        skuDOs.forEach(s -> {
            if (!s.getId().equals(id)) {
                deleteSku(s.getId(), true);
            }
        });
//        deleteSku(id, true);
    }

    private SkuDO validateSkuExists(Long id) {
        SkuDO skuDO = skuMapper.selectById(id);
        if (Objects.isNull(skuDO)) {
            throw exception(SKU_NOT_EXISTS);
        }
        return skuDO;
    }

    @Override
    public void approveTask(Long userId, BpmTaskApproveReqDTO reqVO) {
        bpmProcessInstanceApi.approveTask(userId, BeanUtils.toBean(reqVO, BpmTaskApproveReqDTO.class), OPERATOR_EXTS_KEY);
    }

    @Override
    public void rejectTask(Long userId, BpmTaskRejectReqDTO reqVO) {
        bpmProcessInstanceApi.rejectTask(userId, BeanUtils.toBean(reqVO, BpmTaskRejectReqDTO.class));
    }

    @Override
    public void submitTask(Long skuId, Long userId) {
        SkuDO skuDO = validateSkuExists(skuId);
        Integer ownBrandFlag = skuDO.getOwnBrandFlag();
        String definitionKey = PROCESS_DEFINITION_KEY;
        Map<String, Object> variables = new HashMap<>();
        if (BooleanEnum.YES.getValue().equals(ownBrandFlag)) {
            definitionKey = OWN_BRAND_PROCESS_DEFINITION_KEY;
            String creator = skuDO.getCreator();
            if (StrUtil.isEmpty(creator)) {
                throw exception(CREATE_USER_IS_NULL);
            }
            UserDept user = adminUserApi.getUserDeptByUserId(Long.parseLong(creator));
            Long deptId = user.getDeptId();
            if (Objects.isNull(deptId)) {
                throw exception(DEPT_CODE_IS_NULL);
            }
            DeptRespDTO dept = deptApi.getDept(deptId);
            if (Objects.isNull(dept) || StrUtil.isEmpty(dept.getCode())) {
                throw exception(DEPT_CODE_IS_NULL);
            }
            variables.put("deptCode", dept.getCode());
        }

        String baseSkuAuditFlag = configApi.getValueByConfigKey("base_sku_audit_flag");
        String ownSkuAuditFlag = configApi.getValueByConfigKey("own_sku_audit_flag");
        if((Objects.equals(baseSkuAuditFlag, "0") && definitionKey.equals(PROCESS_DEFINITION_KEY))
          || (Objects.equals(ownSkuAuditFlag, "0") && definitionKey.equals(OWN_BRAND_PROCESS_DEFINITION_KEY))){
            updateAuditStatus(skuId, BpmProcessInstanceResultEnum.APPROVE.getResult(), null);
        }else{
            String processInstanceId = bpmProcessInstanceApi.createProcessInstance(userId, definitionKey, skuId);
            updateAuditStatus(skuId, BpmProcessInstanceResultEnum.PROCESS.getResult(), processInstanceId);
        }
    }

    @Override
    public void updateAuditStatus(Long auditableId, Integer auditStatus, String processInstanceId) {
        SkuDO skuDO = validateSkuExists(auditableId);
        if (Objects.isNull(skuDO)) {
            throw exception(SKU_NOT_EXISTS);
        }
        SkuDO build = SkuDO.builder().id(auditableId).auditStatus(auditStatus).build();
        if (StrUtil.isNotEmpty(processInstanceId)) {
            build.setProcessInstanceId(processInstanceId);
        }
        // 自动生成组合产品子产品对应客户产品
        if (BpmProcessInstanceResultEnum.APPROVE.getResult().equals(auditStatus)) {
            autoCreateCustSubSku(skuDO);
        }
        skuMapper.updateById(build);
    }

    @Override
    public void approveChangeTask(Long userId, BpmTaskApproveReqDTO reqVO) {
        bpmProcessInstanceApi.approveTask(userId, BeanUtils.toBean(reqVO, BpmTaskApproveReqDTO.class), OPERATOR_EXTS_KEY);
    }

    @Override
    public void rejectChangeTask(Long userId, BpmTaskRejectReqDTO reqVO) {
        bpmProcessInstanceApi.rejectTask(userId, BeanUtils.toBean(reqVO, BpmTaskRejectReqDTO.class));
    }

    @Override
    public void submitChangeTask(Long skuId, Long userId) {
        String processInstanceId = bpmProcessInstanceApi.createProcessInstance(userId, CHANGE_PROCESS_DEFINITION_KEY, skuId);
        updateChangeAuditStatus(skuId, BpmProcessInstanceResultEnum.PROCESS.getResult(), processInstanceId);
    }

    @Override
    public void updateChangeAuditStatus(Long auditableId, Integer auditStatus, String processInstanceId) {
        SkuDO skuDO = validateSkuExists(auditableId);
        skuDO.setAuditStatus(auditStatus);
        if (StrUtil.isNotEmpty(processInstanceId)) {
            skuDO.setProcessInstanceId(processInstanceId);
        }
        if (auditStatus == BpmProcessInstanceResultEnum.APPROVE.getResult()) {
            skuDO.setChangeFlag(ChangeFlagEnum.NO.getValue());
            skuDO.setChangeStatus(ChangeStatusEnum.CHANGED.getStatus());
            skuDO.setAuditStatus(BpmProcessInstanceResultEnum.APPROVE.getResult());
            skuMapper.updateById(skuDO);
        } else if (auditStatus == BpmProcessInstanceResultEnum.CANCEL.getResult() || auditStatus == BpmProcessInstanceResultEnum.BACK.getResult()) {
            skuDO.setChangeFlag(ChangeFlagEnum.NO.getValue());
            skuDO.setChangeStatus(ChangeStatusEnum.NOT_CHANGED.getStatus());
            skuDO.setAuditStatus(auditStatus);
            skuMapper.updateById(skuDO);
            skuMapper.deleteById(skuDO);
            SkuDO oldSku = skuMapper.selectOne(new LambdaQueryWrapperX<SkuDO>().eq(SkuDO::getCode, skuDO.getCode()).eq(SkuDO::getVer, skuDO.getVer() - 1));
            if (Objects.nonNull(oldSku)) {
                oldSku.setChangeFlag(ChangeFlagEnum.NO.getValue());
                oldSku.setChangeStatus(ChangeStatusEnum.NOT_CHANGED.getStatus());
                skuMapper.updateById(oldSku);
            }
        } else {
            skuMapper.updateById(skuDO);
        }
    }

    @Override
    public void deleteChangeSku(Long id) {
        skuMapper.updateById(SkuDO.builder()
                .id(id)
                .changeDeleted(DeletedEnum.YES.getValue())
                .build());
        deleteSku(id, false);
    }

    @Override
    @DataPermission(enable = false)
    public List<String> getSkuNameFuzzySearchByName(String name) {
        List<String> result = new ArrayList<>();
        return result;
    }

    @Override
    @DataPermission(enable = false)
    public void updateCompanyPrice(Long id, JsonAmount price) {
        SkuDO skuDO = validateSkuExists(id);
        JsonAmount basePrice = skuDO.getCompanyPrice();
        String oldMsg = null;
        String newMsg = null;
        if (Objects.nonNull(basePrice) && Objects.nonNull(basePrice.getAmount()) && basePrice.getAmount().compareTo(BigDecimal.ZERO) != 0) {
            oldMsg = basePrice.getAmount() + CommonDict.SPACE + basePrice.getCurrency();
        }
        if (Objects.nonNull(price) && Objects.nonNull(price.getAmount()) && price.getAmount().compareTo(BigDecimal.ZERO) != 0) {
            newMsg = basePrice.getAmount() + CommonDict.SPACE + price.getCurrency();
        }
        OperateLogUtils.setContent("【修改内部核算价】" + oldMsg + "->" + newMsg);
        OperateLogUtils.addExt(OPERATOR_EXTS_KEY, skuDO.getCode());
        // 修改基础产品同步更新自营产品跟客户产品
        if (!BooleanEnum.YES.getValue().equals(skuDO.getCustProFlag())&&!BooleanEnum.YES.getValue().equals(skuDO.getOwnBrandFlag())){
            skuMapper.update(SkuDO.builder().companyPrice(price).build(),new LambdaQueryWrapperX<SkuDO>().eq(SkuDO::getBasicSkuCode, skuDO.getBasicSkuCode()));
        }else {
            skuMapper.updateById(SkuDO.builder().id(id).companyPrice(price).build());
        }
    }

    @Override
    public void updateOnshelfFlag(Long id, Integer onshelfFlag) {
        SkuDO skuDO = validateSkuExists(id);
        Integer baseFlag = skuDO.getOnshelfFlag();
        String oldLabel = DictFrameworkUtils.getDictDataLabel("onshelf_flag", baseFlag);
        String newLabel = DictFrameworkUtils.getDictDataLabel("onshelf_flag", onshelfFlag);
        OperateLogUtils.setContent("【修改在售状态】" + oldLabel + "->" + newLabel);
        OperateLogUtils.addExt(OPERATOR_EXTS_KEY, skuDO.getCode());
        if (!BooleanEnum.YES.getValue().equals(skuDO.getCustProFlag())&&!BooleanEnum.YES.getValue().equals(skuDO.getOwnBrandFlag())){
            skuMapper.update(SkuDO.builder().onshelfFlag(onshelfFlag).build(),new LambdaQueryWrapperX<SkuDO>().eq(SkuDO::getBasicSkuCode, skuDO.getBasicSkuCode()));
        }else {
            skuMapper.updateById(SkuDO.builder().id(id).onshelfFlag(onshelfFlag).build());
        }
    }

    @Override
    public void updateAdvantage(Long id, Integer advantageFlag) {
        SkuDO skuDO = validateSkuExists(id);
        Integer baseFlag = skuDO.getAdvantageFlag();
        String oldLabel = DictFrameworkUtils.getDictDataLabel("is_int", baseFlag);
        String newLabel = DictFrameworkUtils.getDictDataLabel("is_int", advantageFlag);
        OperateLogUtils.setContent("【修改优势产品】" + oldLabel + "->" + newLabel);
        OperateLogUtils.addExt(OPERATOR_EXTS_KEY, skuDO.getCode());
        if (!BooleanEnum.YES.getValue().equals(skuDO.getCustProFlag())&&!BooleanEnum.YES.getValue().equals(skuDO.getOwnBrandFlag())){
            skuMapper.update(SkuDO.builder().advantageFlag(advantageFlag).build(),new LambdaQueryWrapperX<SkuDO>().eq(SkuDO::getBasicSkuCode, skuDO.getBasicSkuCode()));
        }else {
            skuMapper.updateById(SkuDO.builder().id(id).advantageFlag(advantageFlag).build());
        }
    }

    @Override
    public PageResult<SimpleSkuDTO> getSimpleSkuDTO(SimpleSkuPageReqVO simpleSkuPageReqVO) {
        Integer pageNo = simpleSkuPageReqVO.getPageNo();
        Integer pageSize = simpleSkuPageReqVO.getPageSize();
        int skip = 0;
        if (pageSize > 0) {
            skip = (pageNo - 1) * pageSize;
        }
        List<String> skuCodeList = new ArrayList<>();
        if (CollUtil.isNotEmpty(simpleSkuPageReqVO.getSkuCodeList())) {
            skuCodeList.addAll(simpleSkuPageReqVO.getSkuCodeList());
        }
        String venderCode = simpleSkuPageReqVO.getVenderCode();
        if (StrUtil.isNotEmpty(venderCode)) {
            List<String> skuCodeListByVenderCode = quoteitemApi.getSkuCodeListByVenderCode(venderCode);
            if (CollUtil.isNotEmpty(skuCodeListByVenderCode)) {
                skuCodeList.addAll(skuCodeListByVenderCode);
            }
        }
        List<SimpleSkuDO> simpleSkuDOList = skuMapper.getSimpleSku(
                skuCodeList, null,
                simpleSkuPageReqVO.getCategoryId(),
                simpleSkuPageReqVO.getSkuName(),
                simpleSkuPageReqVO.getCustProFlag(),
                simpleSkuPageReqVO.getCustCode(),
                simpleSkuPageReqVO.getCskuCode(),
                simpleSkuPageReqVO.getOwnBrandFlag(),
                simpleSkuPageReqVO.getAutoCreateFlag(),
                null,
                simpleSkuPageReqVO.getSkuType(),
                simpleSkuPageReqVO.getSkuTypeInList(),
                simpleSkuPageReqVO.getSkuTypeOutList(),
                simpleSkuPageReqVO.getSkuCode(),
                simpleSkuPageReqVO.getOskuCode(),
                simpleSkuPageReqVO.getBasicSkuCode(),
                simpleSkuPageReqVO.getAgentFlag(),
                skip, pageSize);
        List<SimpleSkuDTO> simpleSkuDTOList = transformSkuDTO(simpleSkuDOList);
        if (CollUtil.isNotEmpty(simpleSkuDTOList)) {
            //组合产品增加子产品信息
            if (Objects.equals(simpleSkuPageReqVO.getSkuType(), SkuTypeEnum.PRODUCT_MIX.getValue())) {
                List<String> idList = simpleSkuDTOList.stream().map(SimpleSkuDTO::getCode).distinct().toList();
                Map<String, List<SimpleSkuDTO>> subSkuMap = getSubSkuMap(idList);
                if (CollUtil.isNotEmpty(subSkuMap)) {
                    simpleSkuDTOList.forEach(sku -> {
                        sku.setSonSkuList(subSkuMap.get(sku.getCode()));
                    });
                }
            }
            //设置辅料配比数据
            List<String> codeList = simpleSkuDTOList.stream().map(SimpleSkuDTO::getCode).distinct().toList();
            Map<String, List<SkuAuxiliaryDTO>> skuAuxiliaryMap = getSkuAuxiliaryMapByCodeList(codeList);
            if (CollUtil.isNotEmpty(skuAuxiliaryMap)) {
                simpleSkuDTOList.forEach(s -> {
                    s.setSkuAuxiliaryDOList(skuAuxiliaryMap.get(s.getCode()));
                });
            }
        }
        Long totalCount = skuMapper.getSkuCount(skuCodeList, null, simpleSkuPageReqVO.getCategoryId(),
                simpleSkuPageReqVO.getSkuName(), simpleSkuPageReqVO.getCustProFlag(), simpleSkuPageReqVO.getCustCode(),
                simpleSkuPageReqVO.getCskuCode(), simpleSkuPageReqVO.getOwnBrandFlag(), simpleSkuPageReqVO.getAutoCreateFlag(),
                null, simpleSkuPageReqVO.getSkuType(), simpleSkuPageReqVO.getSkuTypeInList(), simpleSkuPageReqVO.getSkuTypeOutList(),
                simpleSkuPageReqVO.getSkuCode(), simpleSkuPageReqVO.getOskuCode(), simpleSkuPageReqVO.getBasicSkuCode(), simpleSkuPageReqVO.getAgentFlag());
        if (CollUtil.isEmpty(simpleSkuDTOList)) {
            return PageResult.empty();
        }
//        //基础产品编号赋值
//        List<String> sourceCodeList = simpleSkuDTOList.stream().map(SimpleSkuDTO::getSourceCode).distinct().toList();
//        if(CollUtil.isNotEmpty(sourceCodeList)){
//            List<SkuDO> sourceSkuList =  skuMapper.selectList(new LambdaQueryWrapperX<SkuDO>().select().in(SkuDO::getCode, sourceCodeList));
//            Map<String,List<SkuDO>> sourceSkuMap = sourceSkuList.stream().collect(Collectors.groupingBy(SkuDO::getCode));
//            simpleSkuDTOList.forEach(item->{
//                String basicSkuCode = item.getCode();
//                if(CollUtil.isNotEmpty(Collections.singleton(item.getSourceCode()))){
//                    List<SkuDO> skuDos = sourceSkuMap.get(item.getSourceCode());
//                    if(CollUtil.isNotEmpty(skuDos)){
//                        if (StringUtils.isNotEmpty(skuDos.get(0).getSourceCode())){
//                            basicSkuCode = skuDos.get(0).getSourceCode();
//                        }else{
//                            basicSkuCode = skuDos.get(0).getCode();
//                        }
//                    }
//                }
//                item.setBasicSkuCode(basicSkuCode);
//            });
//        }
        return new PageResult<SimpleSkuDTO>().setList(simpleSkuDTOList).setTotal(totalCount);

    }

    @Override
    public Map<Long, SimpleSkuDTO> getSimpleSkuDTOMap(List<Long> idList) {
        List<SimpleSkuDO> simpleSkuDOList = skuMapper.getSimpleSkuContainsDeleted(idList, 0, idList.size());
        List<SimpleSkuDTO> simpleSkuDTOList = transformSkuDTO(simpleSkuDOList);
        if (CollUtil.isEmpty(simpleSkuDTOList)) {
            return null;
        }
        return simpleSkuDTOList.stream().filter(s -> ChangeFlagEnum.NO.getValue().equals(s.getChangeFlag())).collect(Collectors.toMap(SimpleSkuDTO::getId, s -> s, (o, n) -> o));
    }

    @Override
    public Map<String, SimpleSkuDTO> getSimpleSkuDTOMapByCode(List<String> codeList) {
        if (CollUtil.isEmpty(codeList)) {
            return Map.of();
        }
        List<SkuDO> skuDOS = skuMapper.selectList(new LambdaQueryWrapperX<SkuDO>().select(SkuDO::getId, SkuDO::getCode, SkuDO::getChangeFlag).in(SkuDO::getCode, codeList));
        if (CollUtil.isEmpty(skuDOS)) {
            return Map.of();
        }
        List<Long> idList = skuDOS.stream().map(SkuDO::getId).distinct().toList();
        List<SimpleSkuDO> simpleSkuDOList = skuMapper.getSimpleSkuContainsDeleted(idList, 0, idList.size());
        List<SimpleSkuDTO> simpleSkuDTOList = transformSkuDTO(simpleSkuDOList);
        if (CollUtil.isEmpty(simpleSkuDTOList)) {
            return null;
        }
        return simpleSkuDTOList.stream().filter(s -> ChangeFlagEnum.NO.getValue().equals(s.getChangeFlag())).collect(Collectors.toMap(SimpleSkuDTO::getCode, s -> s, (o, n) -> o));
    }

    @Override
    public void approveCskuTask(Long userId, BpmTaskApproveReqDTO reqVO) {
        bpmProcessInstanceApi.approveTask(userId, BeanUtils.toBean(reqVO, BpmTaskApproveReqDTO.class));
    }

    @Override
    public void rejectCskuTask(Long userId, BpmTaskRejectReqDTO reqVO) {
        bpmProcessInstanceApi.rejectTask(userId, BeanUtils.toBean(reqVO, BpmTaskRejectReqDTO.class));
    }

    @Override
    public void submitCskuTask(Long skuId, Long userId) {
        String processInstanceId = bpmProcessInstanceApi.createProcessInstance(userId, CSKU_PROCESS_DEFINITION_KEY, skuId);
        updateAuditStatus(skuId, BpmProcessInstanceResultEnum.PROCESS.getResult(), processInstanceId);
    }

    @Override
    public void approveChangeCskuTask(Long userId, BpmTaskApproveReqDTO reqVO) {
        bpmProcessInstanceApi.approveTask(userId, BeanUtils.toBean(reqVO, BpmTaskApproveReqDTO.class));
    }

    @Override
    public void rejectChangeCskuTask(Long userId, BpmTaskRejectReqDTO reqVO) {
        bpmProcessInstanceApi.rejectTask(userId, BeanUtils.toBean(reqVO, BpmTaskRejectReqDTO.class));
    }

    @Override
    public void submitChangeCskuTask(Long skuId, Long userId) {
        String processInstanceId = bpmProcessInstanceApi.createProcessInstance(userId, CHANGE_CSKU_PROCESS_DEFINITION_KEY, skuId);
        updateAuditStatus(skuId, BpmProcessInstanceResultEnum.PROCESS.getResult(), processInstanceId);
    }

    @Override
    public void approveAuxiliarySkuTask(Long userId, BpmTaskApproveReqDTO reqVO) {
        bpmProcessInstanceApi.approveTask(userId, BeanUtils.toBean(reqVO, BpmTaskApproveReqDTO.class));
    }

    @Override
    public void rejectAuxiliarySkuTask(Long userId, BpmTaskRejectReqDTO reqVO) {
        bpmProcessInstanceApi.rejectTask(userId, BeanUtils.toBean(reqVO, BpmTaskRejectReqDTO.class));
    }

    @Override
    public void submitAuxiliarySkuTask(Long skuId, Long userId) {
        String processInstanceId = bpmProcessInstanceApi.createProcessInstance(userId, AUXILIARY_PROCESS_DEFINITION_KEY, skuId);
        updateAuditStatus(skuId, BpmProcessInstanceResultEnum.PROCESS.getResult(), processInstanceId);
    }

    @Override
    public void approveChangeAuxiliarySkuTask(Long userId, BpmTaskApproveReqDTO reqVO) {
        bpmProcessInstanceApi.approveTask(userId, BeanUtils.toBean(reqVO, BpmTaskApproveReqDTO.class));
    }

    @Override
    public void rejectChangeAuxiliarySkuTask(Long userId, BpmTaskRejectReqDTO reqVO) {
        bpmProcessInstanceApi.rejectTask(userId, BeanUtils.toBean(reqVO, BpmTaskRejectReqDTO.class));
    }

    @Override
    public void submitChangeAuxiliarySkuTask(Long skuId, Long userId) {
        String processInstanceId = bpmProcessInstanceApi.createProcessInstance(userId, CHANGE_AUXILIARY_PROCESS_DEFINITION_KEY, skuId);
        updateAuditStatus(skuId, BpmProcessInstanceResultEnum.PROCESS.getResult(), processInstanceId);
    }

    @Override
    @DataPermission(enable = false)
    public Map<String, List<SimpleSkuDTO>> getSubSkuMap(List<String> codeList) {
        if (CollUtil.isEmpty(codeList)) {
            return null;
        }
        List<SkuDO> skus = skuMapper.selectList(new LambdaQueryWrapperX<SkuDO>().select(SkuDO::getCode, SkuDO::getId).in(SkuDO::getCode, codeList).eq(SkuDO::getChangeFlag, ChangeFlagEnum.NO.getValue()));
        if (CollUtil.isEmpty(skus)) {
            return null;
        }
        List<Long> idList = skus.stream().map(SkuDO::getId).distinct().toList();
        Map<Long, String> codeIdMap = skus.stream().collect(Collectors.toMap(SkuDO::getId, s -> s.getCode(), (o, n) -> o));
        List<SkuBomDO> skuBomList = skuBomService.getSkuBomListByParentIdList(idList);
        if (CollUtil.isEmpty(skuBomList)) {
            return null;
        }
        List<Long> skuIdList = skuBomList.stream().map(SkuBomDO::getSkuId).toList();
        String skuListString = "";
        if (!skuIdList.isEmpty()) {
            skuListString = skuIdList.stream().map(String::valueOf).collect(Collectors.joining(","));
        }
        List<SkuDO> skuDOList = null;
        if (skuListString.isEmpty()) {
            skuDOList = skuMapper.selectList(new LambdaQueryWrapperX<SkuDO>()
                    .in(SkuDO::getId, skuIdList)
            );
        } else {
            skuDOList = skuMapper.selectList(new LambdaQueryWrapperX<SkuDO>()
                    .in(SkuDO::getId, skuIdList)
                    .last("or (deleted = 1 and id in (" + skuListString + "))")
            );
        }
        if (CollUtil.isEmpty(skuDOList)) {
            return null;
        }
        Map<String, List<QuoteitemDTO>> quoteItemMap = quoteitemApi.getQuoteItemDTOMapBySkuIdList(skuIdList);
        Map<String, List<SimpleSkuDTO>> longListMap = SkuConvert.INSTANCE.convertSimpleSkuDTOMap(skuBomList, skuDOList, quoteItemMap, codeIdMap);
        return longListMap;
    }

    @Override
    public String getProcessDefinitionKey() {
        return PROCESS_DEFINITION_KEY;
    }

    @Override
    public String getChangeProcessDefinitionKey() {
        return CHANGE_PROCESS_DEFINITION_KEY;
    }

    @Override
    public String getCskuProcessDefinitionKey() {
        return CSKU_PROCESS_DEFINITION_KEY;
    }

    @Override
    public String getChangeCskuProcessDefinitionKey() {
        return CHANGE_CSKU_PROCESS_DEFINITION_KEY;
    }

    @Override
    public String getChangeOwnBrandProcessDefinitionKey() {
        return CHANGE_OWN_BRAND_PROCESS_DEFINITION_KEY;
    }

    @Override
    public String getAuxiliarySkuProcessDefinitionKey() {
        return AUXILIARY_PROCESS_DEFINITION_KEY;
    }

    @Override
    public String getChangeAuxiliarySkuProcessDefinitionKey() {
        return CHANGE_AUXILIARY_PROCESS_DEFINITION_KEY;
    }

    public SkuInfoRespVO getSkuDetail(SkuDetailReq skuDetailReq, Boolean isChange) {
        Long skuId = Objects.nonNull(skuDetailReq.getProcessInstanceId()) ? bpmProcessInstanceApi.selectAuditAbleIdByProcessInstanceId(skuDetailReq.getProcessInstanceId()) : skuDetailReq.getSkuId();
        if (Objects.isNull(skuId) && Objects.isNull(skuDetailReq.getSkuCode())) {
            logger.error("[sku]未获取到sku");
            return null;
        }
        LambdaQueryWrapperX<SkuDO> skuDOLambdaQueryWrapperX = new LambdaQueryWrapperX<SkuDO>();
        skuDOLambdaQueryWrapperX.eqIfPresent(SkuDO::getId, skuId);
        String Code = skuDetailReq.getSkuCode();
        if (Objects.nonNull(Code)) {
            skuDOLambdaQueryWrapperX.eqIfPresent(SkuDO::getCode, skuDetailReq.getSkuCode());
            skuDOLambdaQueryWrapperX.eq(SkuDO::getChangeFlag, 0);
        }

        if (skuId != null) {
            skuDOLambdaQueryWrapperX.inIfPresent(SkuDO::getDeleted, new ArrayList<>(Arrays.asList(0, 1)));
        }
        SkuDO skuDO = skuMapper.selectOne(skuDOLambdaQueryWrapperX);
        if (skuDO == null) {
            return null;
        }
        List<QuoteitemDTO> quoteitemDTOBySkuCode = quoteitemApi.getQuoteitemDTOBySkuId(skuDO.getId());
        QuoteitemDTO quoteitemDTO = null;
        if (CollUtil.isNotEmpty(quoteitemDTOBySkuCode)) {
            quoteitemDTO = quoteitemDTOBySkuCode.stream().filter(s -> BooleanEnum.YES.getValue().equals(s.getDefaultFlag())).findFirst().orElse(null);
        }
        Map<Long, BrandSimpleRespVO> brandSimpleRespVOMap = brandService.getBrandSimpleMap(Objects.isNull(skuDO.getBrandId()) ? new ArrayList<>() : List.of(skuDO.getBrandId()));
        SkuInfoRespVO skuRespVO = SkuConvert.INSTANCE.convertSkuRespVO(skuDO, quoteitemDTO, custApi.getCustNameCache(skuDO.getCustCode()), brandSimpleRespVOMap, adminUserApi.getUserIdNameCache());
        String categoryName = categoryService.getPathCateName(skuDO.getCategoryId());
        skuRespVO.setCategoryName(categoryName);
        BrandDO brand = brandService.getBrand(skuDO.getBrandId());
        if (Objects.nonNull(brand)) {
            skuRespVO.setBrandName(brand.getName());
        }
        // 产品名称
        skuRespVO.setSkuName(getSkuNameById(skuDO.getSourceId()));
        // 获取供应商报价列表
        skuRespVO.setQuoteitemDTOList(quoteitemApi.getQuoteitemDTOBySkuId(skuDO.getId()));
        //设置辅料配比
        skuRespVO.setSkuAuxiliaryList(getSkuAuxiliaryListByCode(skuDO.getCode()));
        // 获取报关信息
        skuRespVO.setHsdata(hsdataService.getHsdata(skuDO.getHsCodeId()));
        // 获得操作日志信息
        skuRespVO.setOperateLogRespDTOList(operateLogApi.getOperateLogRespDTOList(OPERATOR_EXTS_KEY, skuDO.getCode()));
        //获取最近成交价
        List<HistoryTradePriceDTO> hisList = saleContractApi.getHistoryTradeList(skuDO.getCode());
        if (CollUtil.isNotEmpty(hisList)) {
            JsonAmount tradePrice = hisList.get(0).getTradePrice();
            skuRespVO.setLastDealPrice(tradePrice);
            skuRespVO.setDealRecordList(hisList);
        }
        //处理客户产品的国家信息
        if (Objects.equals(skuRespVO.getCustProFlag(), BooleanEnum.YES.getValue())) {
            CustAllDTO dto = custApi.getCustByCode(skuRespVO.getCustCode());
            if (Objects.nonNull(dto)) {
                skuRespVO.setCountryId(dto.getCountryId());
                skuRespVO.setCountryName(dto.getCountryName());
                skuRespVO.setRegionName(dto.getRegionName());
            }
        }
        Long parentId = skuId;
        if (BooleanEnum.YES.getValue().equals(skuDO.getCustProFlag()) && !BpmProcessInstanceResultEnum.APPROVE.getResult().equals(skuDO.getAuditStatus())) {
            parentId = skuDO.getSourceId();
        }
        List<SkuBomDO> skuBomList = skuBomService.getSkuBomListByParentId(parentId);
        if (CollUtil.isEmpty(skuBomList)) {
            return skuRespVO;
        }
        // 获取节点sku列表及配件信息
        if (CollUtil.isNotEmpty(skuBomList)) {
            List<Long> skuIdList = skuBomList.stream().map(SkuBomDO::getSkuId).distinct().toList();
            Map<Long, Boolean> skuExitsByIds = getSkuExitsByIds(skuIdList);
            List<SkuInfoRespVO> skuRespVOList = skuBomList.stream().map(s -> {
                Long bomSkuId = s.getSkuId();
                SkuInfoRespVO sku = getSku(new SkuDetailReq().setSkuId(bomSkuId));
                if (Objects.nonNull(sku)) {
                    sku.setQty(s.getQty());
                }
                if (CollUtil.isNotEmpty(skuExitsByIds)) {
                    Boolean aBoolean = skuExitsByIds.get(bomSkuId);
                    if (Objects.isNull(aBoolean)) {
                        aBoolean = false;
                    }
                    sku.setSkuDeletedFlag(aBoolean ? 0 : 1);
                }
                return sku;
            }).filter(Objects::nonNull).collect(Collectors.toList());
            if (CollUtil.isNotEmpty(skuRespVOList)) {
                // 组合产品
                List<Long> subSkuIdList = skuBomList.stream().filter(s -> SkuTypeEnum.PRODUCT_MIX.getValue().equals(s.getSkuType())).map(SkuBomDO::getSkuId).toList();
                // 配件
                List<Long> accSkuIdList = skuBomList.stream().filter(s -> SkuTypeEnum.ACCESSORIES.getValue().equals(s.getSkuType())).map(SkuBomDO::getSkuId).toList();
                if (CollUtil.isNotEmpty(subSkuIdList)) {
                    skuRespVO.setSubProductList(skuRespVOList.stream().filter(skuInfoRespVO -> subSkuIdList.contains(skuInfoRespVO.getId())).toList());
                }
                if (CollUtil.isNotEmpty(accSkuIdList)) {
                    skuRespVO.setAccessoriesList(skuRespVOList.stream().filter(skuInfoRespVO -> accSkuIdList.contains(skuInfoRespVO.getId())).toList());
                }
            }
        }


        return skuRespVO;
    }

    private List<SkuAuxiliaryDO> getSkuAuxiliaryListByCode(String code) {
        List<SkuAuxiliaryDO> auxiliaryDOList = skuAuxiliaryService.getListBySkuCode(code);
        List<String> codeList = auxiliaryDOList.stream().map(SkuAuxiliaryDO::getAuxiliarySkuCode).distinct().toList();
        List<SkuDO> SkuDoList = skuMapper.selectList(SkuDO::getCode, codeList);
        for (SkuAuxiliaryDO s : auxiliaryDOList) {
            if (CollUtil.isEmpty(SkuDoList)) {
                throw exception(SKU_NOT_EXISTS);
            }
            Optional<SkuDO> first = SkuDoList.stream().filter(u -> Objects.equals(u.getCode(), s.getAuxiliarySkuCode())).findFirst();
            if (first.isEmpty()) {
                throw exception(SKU_NOT_EXISTS);
            }
            s.setMeasureUnit(first.get().getMeasureUnit());
            List<SimpleFile> picture = first.get().getPicture();
            if (CollUtil.isNotEmpty(picture)) {
                Optional<SimpleFile> first1 = picture.stream().filter(p -> p.getMainFlag() == 1).findFirst();
                first1.ifPresent(s::setAuxiliarySkuPicture);
            }
        }
        return auxiliaryDOList;
    }

    private Map<String, List<SkuAuxiliaryDTO>> getSkuAuxiliaryMapByCodeList(List<String> codeList) {
        Map<String, List<SkuAuxiliaryDTO>> auxiliaryMap = skuAuxiliaryService.getMapBySkuCodeList(codeList);
        if (CollUtil.isEmpty(auxiliaryMap)) {
            return null;
        }
        List<String> auxiliaryCodeList = auxiliaryMap.values().stream()
                .flatMap(List::stream)
                .map(SkuAuxiliaryDTO::getAuxiliarySkuCode)
                .toList();
        List<SkuDO> SkuDoList = skuMapper.selectList(SkuDO::getCode, auxiliaryCodeList);
        auxiliaryMap.forEach((k, v) -> {
            if (CollUtil.isEmpty(v)) {
                return;
            }
            v.forEach(s -> {
                if (CollUtil.isEmpty(SkuDoList)) {
                    throw exception(SKU_NOT_EXISTS);
                }
                Optional<SkuDO> first = SkuDoList.stream().filter(u -> Objects.equals(u.getCode(), s.getAuxiliarySkuCode())).findFirst();
                if (first.isEmpty()) {
                    throw exception(SKU_NOT_EXISTS);
                }
                s.setAuxiliarySkuUnit(first.get().getMeasureUnit());
                List<SimpleFile> picture = first.get().getPicture();
                if (CollUtil.isNotEmpty(picture)) {
                    Optional<SimpleFile> first1 = picture.stream().filter(p -> p.getMainFlag() == 1).findFirst();
                    first1.ifPresent(s::setAuxiliarySkuPicture);
                }
            });
        });
        return auxiliaryMap;
    }

    @Override
    public SkuInfoRespVO getSku(SkuDetailReq skuDetailReq) {
        return getSkuDetail(skuDetailReq, false);
    }

    @Override
    public PageResult<SkuRespVO> getSkuPage(SkuPageReqVO pageReqVO, Boolean onlySkuFlag) {
        SkuPageReqVO req = dealSkuPageReq(pageReqVO);
        if (!StrUtil.isEmpty(pageReqVO.getCurrency()) || !Objects.isNull(pageReqVO.getAmountMax()) || !Objects.isNull(pageReqVO.getAmountMin())) {
            Set<Long> skuIdSet = quoteitemApi.getSkuIdListByCurrencyAndAmount(pageReqVO.getCurrency(), pageReqVO.getAmountMax(), pageReqVO.getAmountMin());
            if (skuIdSet.isEmpty()) {
                return null;
            }
            req.setIdSet(skuIdSet);
        }
        PageResult<SkuDO> skuDOPageResult = skuMapper.selectPage(req, onlySkuFlag);
        List<SkuDO> skuDOList = skuDOPageResult.getList();
        if (CollUtil.isEmpty(skuDOList)) {
            return null;
        }
        List<Long> skuIdList = skuDOList.stream().map(SkuDO::getId).distinct().toList();
        List<Long> brandIdList = skuDOList.stream().map(SkuDO::getBrandId).distinct().toList();
        Map<Long, BrandSimpleRespVO> brandSimpleRespVOMap = brandService.getBrandSimpleMap(brandIdList);
        Map<Long, HsdataDO> hsCodeMap = getHsCodeMap(skuDOList);
        return SkuConvert.INSTANCE.convertSkuRespVOPageResult(skuDOPageResult, quoteitemApi.getQuoteItemDTOListBySkuIdList(skuIdList), custApi.getCustNameCache(null), brandSimpleRespVOMap, adminUserApi.getUserIdNameCache(), hsCodeMap);
    }

    private SkuPageReqVO dealSkuPageReq(SkuPageReqVO pageReqVO) {
        if (StrUtil.isNotEmpty(pageReqVO.getCodeListStr())) {
            List<String> codeList = new ArrayList<>(Arrays.stream(pageReqVO.getCodeListStr().split(",")).toList());
            if (CollUtil.isNotEmpty(codeList)) {
                if (CollUtil.isNotEmpty(pageReqVO.getCodeList())) {
                    codeList.addAll(pageReqVO.getCodeList());
                }
                pageReqVO.setCodeList(codeList);
            }
        }

        if (StrUtil.isNotEmpty(pageReqVO.getCskuCodeListStr())) {
            List<String> codeList = new ArrayList<>(Arrays.stream(pageReqVO.getCskuCodeListStr().split(",")).toList());
            if (CollUtil.isNotEmpty(codeList)) {
                if (CollUtil.isNotEmpty(pageReqVO.getCodeList())) {
                    codeList.addAll(pageReqVO.getCodeList());
                }
                pageReqVO.setCskuCodeList(codeList);
            }
        }

        if (StrUtil.isNotEmpty(pageReqVO.getOskuCodeListStr())) {
            List<String> codeList = new ArrayList<>(Arrays.stream(pageReqVO.getOskuCodeListStr().split(",")).toList());
            if (CollUtil.isNotEmpty(codeList)) {
                if (CollUtil.isNotEmpty(pageReqVO.getCodeList())) {
                    codeList.addAll(pageReqVO.getCodeList());
                }
                pageReqVO.setOskuCodeList(codeList);
            }
        }

        if (StrUtil.isNotEmpty(pageReqVO.getBasicSkuCodeListStr())) {
            List<String> codeList = new ArrayList<>(Arrays.stream(pageReqVO.getBasicSkuCodeListStr().split(",")).toList());
            if (CollUtil.isNotEmpty(codeList)) {
                if (CollUtil.isNotEmpty(pageReqVO.getCodeList())) {
                    codeList.addAll(pageReqVO.getCodeList());
                }
                pageReqVO.setBasicSkuCodeList(codeList);
            }
        }

        if (StrUtil.isNotEmpty(pageReqVO.getNameListStr())) {
            List<String> nameList = new ArrayList<>(Arrays.stream(pageReqVO.getNameListStr().split(",")).toList());
            if (CollUtil.isNotEmpty(nameList) && !nameList.isEmpty()) {
                pageReqVO.setNameList(nameList);
            }
        }

        if (CollUtil.isNotEmpty(pageReqVO.getVenderCodeList()) || Objects.nonNull(pageReqVO.getPurchaseUserId())) {
            List<QuoteitemDTO> quoteItemList = quoteitemApi.getQuoteitemDTOByVenderCodesOrPurchaseUserId(pageReqVO.getVenderCodeList(), pageReqVO.getPurchaseUserId());
            List<Long> idList = new ArrayList<>();
            if (CollUtil.isEmpty(quoteItemList)) {
                idList.add(0L); //无法查出有效数据
                return pageReqVO.setIdList(idList);
            }
            idList = quoteItemList.stream().map(QuoteitemDTO::getSkuId).distinct().toList();
            if (CollUtil.isEmpty(idList)) {
                idList.add(0L); //无法查出有效数据
                return pageReqVO.setOwnBrandFlag(2);
            }
            return pageReqVO.setIdList(idList);
        }

        return pageReqVO;
    }

    private SkuPageReqVO dealCustPageReq(SkuPageReqVO pageReqVO) {
        List<String> codeList = new ArrayList<>();
        //code，codeListStr,name,nameListStr  最终都是通过编号搜索
        //业务上不会出现同时搜索的情况，代码优先级为 编号精确搜索>编号模糊搜索>name精确搜索>name模糊搜索

        //1.优先级字段custCodeList
        if (CollUtil.isNotEmpty(pageReqVO.getCustCodeList())) {
            pageReqVO.setCustCode(null);
            return pageReqVO;
        }
        //2.优先级字段custCodeListStr
        if (StrUtil.isNotEmpty(pageReqVO.getCustCodeListStr())) {
            codeList = new ArrayList<>(Arrays.stream(pageReqVO.getCustCodeListStr().split(",")).toList());
            if (CollUtil.isEmpty(codeList)) {
                throw exception(SKU_CUST_CODE_ACCURATE_YES_ERROR);
            }
            pageReqVO.setCustCodeList(codeList);
            pageReqVO.setCustCode(null);
            return pageReqVO;
        }
        //3.优先级custCode
        if (StrUtil.isNotEmpty(pageReqVO.getCustCode())) {
            pageReqVO.setCustCode(null);
            return pageReqVO;
        }
        //4.优先级字段nameCodeListStr
        if (StrUtil.isNotEmpty(pageReqVO.getCustNameListStr())) {
            List<String> nameList = new ArrayList<>(Arrays.stream(pageReqVO.getCustNameListStr().split(",")).toList());
            if (CollUtil.isEmpty(nameList)) {
                throw exception(SKU_CUST_NAME_ACCURATE_YES_ERROR);
            }
            codeList = custApi.getCodeListByNameList(nameList);
            pageReqVO.setCustCodeList(codeList);
            pageReqVO.setCustCode(null);
            return pageReqVO;
        }
        //5.优先级custName
        if (StrUtil.isNotEmpty(pageReqVO.getCustName())) {
            codeList = custApi.getCodeListByName(pageReqVO.getCustName());
            pageReqVO.setCustCodeList(codeList);
            pageReqVO.setCustCode(null);
            return pageReqVO;
        }

        return pageReqVO;
    }

    @Override
    public PageResult<SkuRespVO> getSkuChangePage(SkuPageReqVO pageReqVO, Boolean onlySkuFlag) {
        SkuPageReqVO req = dealSkuPageReq(pageReqVO);
        PageResult<SkuDO> skuDOPageResult = skuMapper.selectChangePage(req, onlySkuFlag);
        List<SkuDO> skuDOList = skuDOPageResult.getList();
        if (CollUtil.isEmpty(skuDOList)) {
            return null;

        }
        List<Long> skuIdList = skuDOList.stream().map(SkuDO::getId).distinct().toList();
        List<Long> brandIdList = skuDOList.stream().map(SkuDO::getBrandId).distinct().toList();
        Map<Long, BrandSimpleRespVO> brandSimpleRespVOMap = brandService.getBrandSimpleMap(brandIdList);
        Map<Long, HsdataDO> hsCodeMap = getHsCodeMap(skuDOList);
        PageResult<SkuRespVO> skuRespVOPageResult = SkuConvert.INSTANCE.convertSkuRespVOPageResult(skuDOPageResult, quoteitemApi.getQuoteItemDTOListBySkuIdList(skuIdList), custApi.getCustNameCache(null), brandSimpleRespVOMap, adminUserApi.getUserIdNameCache(), hsCodeMap);
//        skuRespVOPageResult.getList().forEach(s -> {
//            SkuInfoRespVO oldsku = getOldSku(BeanUtils.toBean(s, SkuInfoRespVO.class));
//            s.setOldSku(oldsku);
//        });
        return skuRespVOPageResult;
    }

    @Override
    public PageResult<CskuRespVO> getCskuPage(SkuPageReqVO pageReqVO) {
        SkuPageReqVO req = dealSkuPageReq(pageReqVO);
        SkuPageReqVO custPageReq = dealCustPageReq(req);
        if (Objects.nonNull(pageReqVO.getCountryId()) || StrUtil.isNotEmpty(pageReqVO.getRegionCode())) {
            List<String> codeList = custApi.getCodeListByCountryCode(pageReqVO.getCountryId(), pageReqVO.getRegionCode());
            if (CollUtil.isEmpty(codeList) || codeList.isEmpty()) {
                return PageResult.empty();
            }
            custPageReq.setCustCodeList(codeList);
        }
        Set<Long> skuIdSet = quoteitemApi.getSkuIdListByCurrencyAndAmount(pageReqVO.getCurrency(), pageReqVO.getAmountMax(), pageReqVO.getAmountMin());
        if (CollUtil.isNotEmpty(skuIdSet)) {
            pageReqVO.setIdSet(skuIdSet);
        }
        // 根据hsCode查询hsCodeId列表
        if (StrUtil.isNotEmpty(pageReqVO.getHsCode())) {
            List<Long> hsCodeIdList = hsdataMapper.getHsCodeIdListByHsCode(pageReqVO.getHsCode());
            if (CollUtil.isEmpty(hsCodeIdList)) {
                return PageResult.empty();
            }
            custPageReq.setHsCodeIdList(hsCodeIdList);
        }
        custPageReq.setAgentFlag(pageReqVO.getAgentFlag());
        PageResult<SkuDO> skuDOPageResult = skuMapper.selectPage(custPageReq, true);
        List<SkuDO> skuDOList = skuDOPageResult.getList();
        if (CollUtil.isEmpty(skuDOList)) {
            return null;
        }
        List<Long> brandIdList = skuDOList.stream().map(SkuDO::getBrandId).distinct().toList();
        Map<Long, BrandSimpleRespVO> brandSimpleRespVOMap = brandService.getBrandSimpleMap(brandIdList);
        Map<Long, String> userDeptCache = adminUserApi.getUserIdNameCache();
//        Integer pageNo = pageReqVO.getPageNo();
//        Integer pageSize = pageReqVO.getPageSize();
//        int skip = 0;
//        if (pageSize > 0) {
//            skip = (pageNo - 1) * pageSize;
//        }
//        List<SkuDO> skuDOList = skuMapper.getSkuPage(
//                pageReqVO.getAuditStatus(),
//                pageReqVO.getAdvantageFlag(),
//                pageReqVO.getName(),
//                pageReqVO.getNameEng(),
//                pageReqVO.getCode(),
//                pageReqVO.getSkuType(),
//                pageReqVO.getVenderIdList(),
//                pageReqVO.getPurchaseUserId(),
//                null,
//                req.getCodeList(),
//                req.getNameList(),
//                pageReqVO.getCustCodeList(),
//                pageReqVO.getCustProFlag(),
//                pageReqVO.getOwnBrandFlag(),
//                skip, pageSize);
//        if (CollUtil.isEmpty(skuDOList)){
//            return PageResult.empty();
//        }
//        Long totalCount = skuMapper.getSkuPageCount(
//                pageReqVO.getAuditStatus(),
//                pageReqVO.getAdvantageFlag(),
//                pageReqVO.getName(),
//                pageReqVO.getNameEng(),
//                pageReqVO.getCode(),
//                pageReqVO.getSkuType(),
//                pageReqVO.getVenderIdList(),
//                pageReqVO.getPurchaseUserId(),
//                null,
//                req.getCodeList(),
//                req.getNameList(),
//                req.getCustCodeList(),
//                pageReqVO.getCustProFlag(),
//                pageReqVO.getOwnBrandFlag());
//
//        PageResult<SkuDO> skuDOPageResult = new PageResult<SkuDO>().setList(skuDOList).setTotal(totalCount);

        List<Long> skuIdList = skuDOList.stream().map(SkuDO::getId).distinct().toList();

        Map<Long, HsdataDO> hsCodeMap = getHsCodeMap(skuDOList);
        PageResult<SkuRespVO> skuRespVOPageResult = SkuConvert.INSTANCE.convertSkuRespVOPageResult(skuDOPageResult, quoteitemApi.getQuoteItemDTOListBySkuIdList(skuIdList), custApi.getCustNameCache(null), brandSimpleRespVOMap, userDeptCache, hsCodeMap);
        PageResult<CskuRespVO> cskuRespVOPageResult = SkuConvert.INSTANCE.convertCskuRespVOPageResult(skuRespVOPageResult);
        //列表增加客户国家和区域
        List<String> custCodeList = skuDOList.stream().map(SkuDO::getCustCode).distinct().toList();
        Map<String, CustAllDTO> custByCodeList = custApi.getCustByCodeList(custCodeList);
        if (CollUtil.isNotEmpty(custByCodeList)) {
            Map<Long, CountryInfoDTO> countryInfoMap = countryInfoApi.getCountryInfoMap();
            if (CollUtil.isEmpty(countryInfoMap)) {
                throw exception(COUNTRY_MAP_NOT_EXIT);
            }

            cskuRespVOPageResult.getList().forEach(s -> {
                CustAllDTO custAllDTO = custByCodeList.get(s.getCustCode());
                if (Objects.nonNull(custAllDTO)) {
                    CountryInfoDTO countryInfoDTO = countryInfoMap.get(custAllDTO.getCountryId());
                    if (Objects.nonNull(countryInfoDTO)) {
                        s.setCountryId(countryInfoDTO.getId());
                        s.setCountryName(countryInfoDTO.getName());
                        s.setRegionName(countryInfoDTO.getRegionName());
                    }
                }

            });
        }
        return cskuRespVOPageResult;
    }

    @Override
    public PageResult<CskuRespVO> getCskuChangePage(SkuPageReqVO pageReqVO) {
        SkuPageReqVO req = dealSkuPageReq(pageReqVO);
        PageResult<SkuDO> skuDOPageResult = skuMapper.selectChangePage(req, true);
        List<SkuDO> skuDOList = skuDOPageResult.getList();
        if (CollUtil.isEmpty(skuDOList)) {
            return null;
        }
        List<Long> skuIdList = skuDOList.stream().map(SkuDO::getId).distinct().toList();
        List<Long> brandIdList = skuDOList.stream().map(SkuDO::getBrandId).distinct().toList();
        Map<Long, BrandSimpleRespVO> brandSimpleRespVOMap = brandService.getBrandSimpleMap(brandIdList);
        Map<Long, HsdataDO> hsCodeMap = getHsCodeMap(skuDOList);
        PageResult<SkuRespVO> skuRespVOPageResult = SkuConvert.INSTANCE.convertSkuRespVOPageResult(skuDOPageResult, quoteitemApi.getQuoteItemDTOListBySkuIdList(skuIdList), custApi.getCustNameCache(null), brandSimpleRespVOMap, adminUserApi.getUserIdNameCache(), hsCodeMap);
        PageResult<CskuRespVO> cSkuRespVOPageResult = SkuConvert.INSTANCE.convertCskuRespVOPageResult(skuRespVOPageResult);
        List<CskuRespVO> list = cSkuRespVOPageResult.getList();
        if (CollUtil.isEmpty(list)) {
            return cSkuRespVOPageResult;
        }
        List<String> cskuCodeList = list.stream().map(CskuRespVO::getCode).distinct().toList();
        Map<String, Map<Integer, SkuInfoRespVO>> simpleOldCustMap = getSimpleOldCustMap(cskuCodeList);
        cSkuRespVOPageResult.getList().forEach(s -> {
            Map<Integer, SkuInfoRespVO> custInfoRespVoMap = simpleOldCustMap.get(s.getCode());
            if (CollUtil.isEmpty(custInfoRespVoMap)) {
                return;
            }
            s.setOldCsku(custInfoRespVoMap.get(s.getVer() - 1));
        });
        return cSkuRespVOPageResult;
    }

    private Map<Long, HsdataDO> getHsCodeMap(List<SkuDO> skuDOList) {
        if (CollUtil.isEmpty(skuDOList)) {
            return Map.of();
        }
        // 收集所有hsCodeId
        List<Long> hsCodeIdList = skuDOList.stream()
                .map(SkuDO::getHsCodeId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (CollUtil.isEmpty(hsCodeIdList)) {
            return Map.of();
        }
        // 批量查询HS编码
        List<HsdataDO> hsdataDOList = hsdataMapper.selectBatchIds(hsCodeIdList);
        if (CollUtil.isEmpty(hsdataDOList)) {
            return Map.of();
        }
        // 构建hsCodeId -> code的映射
        return hsdataDOList.stream()
                .collect(Collectors.toMap(HsdataDO::getId, s->s, (v1, v2) -> v1));
    }

    private Map<String, Map<Integer, SkuInfoRespVO>> getSimpleOldCustMap(List<String> cskuCodeList) {
        LambdaQueryWrapperX<SkuDO> skuDOLambdaQueryWrapperX = new LambdaQueryWrapperX<SkuDO>();
        skuDOLambdaQueryWrapperX.in(SkuDO::getCode, cskuCodeList);
        skuDOLambdaQueryWrapperX.eq(SkuDO::getAuditStatus, BpmProcessInstanceResultEnum.APPROVE.getResult());
        skuDOLambdaQueryWrapperX.eq(SkuDO::getChangeDeleted, DeletedEnum.NO.getValue());
        skuDOLambdaQueryWrapperX.in(SkuDO::getDeleted, new ArrayList<>(Arrays.asList(0, 1)));
        List<SkuDO> oldCustList = skuMapper.selectList(skuDOLambdaQueryWrapperX);
        if (CollUtil.isEmpty(oldCustList)) {
            return Map.of();
        }
        return oldCustList.stream()
                .collect(Collectors.groupingBy(
                                SkuDO::getCode, // 第一层分组：根据 code
                                Collectors.toMap(
                                        SkuDO::getVer, // 第二层分组：根据 ver
                                        custDO -> BeanUtils.toBean(custDO, SkuInfoRespVO.class)) // 转换为 CustInfoRespVo
                        )
                );
    }

    public String getCode(Long id) {
        return StrUtil.concat(false, codeGeneratorApi.getCodeGenerator(SN_TYPE, categoryService.getProfixCode(id), false, 3));
    }

    private void dealPicture(List<SimpleFile> basePicture, List<SimpleFile> reqPicture, List<ChangeRecord> changeRecords) {
        if (CollUtil.isEmpty(basePicture)) {
            // 库中不存在 前端传入为新增
            if (CollUtil.isNotEmpty(reqPicture)) {
                reqPicture.forEach(s -> {
                    if (BooleanEnum.YES.getValue().equals(s.getMainFlag())) {
                        changeRecords.add(new ChangeRecord().setFieldName("新增主图").setPictureFlag(true));
                    } else {
                        changeRecords.add(new ChangeRecord().setFieldName("新增图片").setPictureFlag(true));
                    }
                });
            }
        } else {
            // 库中存在前端未传入则删除
            if (CollUtil.isEmpty(reqPicture)) {
                basePicture.forEach(s -> {
                    if (BooleanEnum.YES.getValue().equals(s.getMainFlag())) {
                        changeRecords.add(new ChangeRecord().setFieldName("删除主图").setPictureFlag(true));
                    } else {
                        changeRecords.add(new ChangeRecord().setFieldName("删除图片").setPictureFlag(true));
                    }
                });
            } else {
                Map<String, SimpleFile> basePictureMap = basePicture.stream().collect(Collectors.toMap(SimpleFile::getFileUrl, s -> s));
                Map<String, SimpleFile> reqPictureMap = reqPicture.stream().collect(Collectors.toMap(SimpleFile::getFileUrl, s -> s));
                basePicture.forEach(s -> {
                    // 数据库中存在前端未传入则删除
                    SimpleFile simpleFile = reqPictureMap.get(s.getFileUrl());
                    if (Objects.isNull(simpleFile)) {
                        changeRecords.add(new ChangeRecord().setFieldName("删除图片").setPictureFlag(true));
                    }
                });
                reqPicture.forEach(s -> {
                    // 数据库不存在前端传入则新增
                    SimpleFile simpleFile = basePictureMap.get(s.getFileUrl());
                    if (Objects.isNull(simpleFile)) {
                        changeRecords.add(new ChangeRecord().setFieldName("新增图片").setPictureFlag(true));
                    }
                });
                String baseFileUrl = "";
                Optional<String> basefileUrlOpt = basePicture.stream().filter(s -> BooleanEnum.YES.getValue().equals(s.getMainFlag())).map(SimpleFile::getFileUrl).findFirst();
                if (basefileUrlOpt.isPresent()) {
                    baseFileUrl = basefileUrlOpt.get();
                }
                String reqFileUrl = "";
                Optional<String> reqFileUrlOpt = reqPicture.stream().filter(s -> BooleanEnum.YES.getValue().equals(s.getMainFlag())).map(SimpleFile::getFileUrl).findFirst();
                if (reqFileUrlOpt.isPresent()) {
                    reqFileUrl = reqFileUrlOpt.get();
                }
                if (!baseFileUrl.equals(reqFileUrl)) {
                    changeRecords.add(new ChangeRecord().setFieldName("更新主图").setPictureFlag(true));
                }
            }
        }
    }

    private void dealSkuBomOperatorLog(Long skuId, List<ChangeRecord> changeRecords, SkuInfoSaveReqVO updateReqVO, Integer value, String name) {
        // 获取子产品及配件信息
        List<SkuBomDO> skuBomDOList = skuBomService.getSkuBomListByParentId(skuId);
        if (CollUtil.isNotEmpty(skuBomDOList)) {
            // 子产品
            Map<Long, SkuBomDO> subProductBomMap = skuBomDOList.stream().filter(skuBomDO -> value.equals(skuBomDO.getSkuType())).collect(Collectors.toMap(SkuBomDO::getSkuId, s -> s));
            List<SimpleSkuSaveReqVO> subProductReqList = new ArrayList<>();
            if (SkuTypeEnum.PRODUCT_MIX.getValue().equals(value)) {
                subProductReqList = updateReqVO.getSubProductList();
            }
            if (SkuTypeEnum.ACCESSORIES.getValue().equals(value)) {
                subProductReqList = updateReqVO.getAccessoriesList();
            }
            if (CollUtil.isNotEmpty(subProductBomMap)) {
                // 前端传入的子产品为空则全部删除
                if (CollUtil.isEmpty(subProductReqList)) {
                    changeRecords.add(new ChangeRecord().setFieldName(String.format("清空%s", name)));
                } else {
                    Map<Long, SimpleSkuSaveReqVO> skuSaveReqVOMap = subProductReqList.stream().collect(Collectors.toMap(SimpleSkuSaveReqVO::getId, s -> s));
                    subProductReqList.forEach(subProductReq -> {
                        // bom表中存在则比较数量是否更新
                        if (subProductBomMap.containsKey(subProductReq.getId())) {
                            SkuBomDO skuBomDO = subProductBomMap.get(subProductReq.getId());
                            if (Objects.isNull(skuBomDO)) {
                                return;
                            }
                            Integer qty = skuBomDO.getQty();
                            Integer qtyReq = subProductReq.getQty();
                            if (Objects.nonNull(qty) && Objects.nonNull(qtyReq)) {
                                if (qty.intValue() != qtyReq.intValue()) {
                                }
                            }

                            // 否则为新增子产品
                        } else {
                            changeRecords.add(new ChangeRecord().setFieldName(String.format("新增%s【%s】-%s", name, subProductReq.getSkuCode(), subProductReq.getId())));
                        }
                    });
                    skuBomDOList.stream().forEach(s -> {
                        if (!skuSaveReqVOMap.containsKey(s.getSkuId())) {
                            changeRecords.add(new ChangeRecord().setFieldName(String.format("删除%s【%s】-%s", name, s.getSkuCode(), s.getId())));
                        }
                    });
                }
            } else {
                if (CollUtil.isNotEmpty(subProductReqList)) {
                    subProductReqList.forEach(s -> {
                        changeRecords.add(new ChangeRecord().setFieldName(String.format("新增%s【%s】-%s", name, s.getSkuCode(), s.getId())));
                    });
                }
            }
        }
    }

    private void dealQuoteitem(Long skuId, SkuInfoSaveReqVO updateReqVO, List<ChangeRecord> changeRecords) {
        // 查询报价信息
        List<QuoteitemDTO> baseQuoteitemList = quoteitemApi.getQuoteitemDTOBySkuId(skuId);
        List<QuoteitemDTO> reqQuoteitemList = updateReqVO.getQuoteitemList();
        // 前端传入报价为空且库中已存在报价则删除
        if (CollUtil.isEmpty(reqQuoteitemList) && CollUtil.isNotEmpty(baseQuoteitemList)) {
            changeRecords.add(new ChangeRecord().setFieldName("清空供应商报价").setOldValue(null));
        }
        // 前端传入报价不为空但库中报价为空则为新增
        if (CollUtil.isNotEmpty(reqQuoteitemList) && CollUtil.isEmpty(baseQuoteitemList)) {
            reqQuoteitemList.forEach(s -> {
                changeRecords.add(new ChangeRecord().setFieldName(String.format("新增供应商报价", s.getSequence())).setValue(s.getVenderName()));
            });
        }
        if (CollUtil.isNotEmpty(reqQuoteitemList) && CollUtil.isNotEmpty(baseQuoteitemList)) {
            Map<Long, QuoteitemDTO> quoteitemDTOMap = baseQuoteitemList.stream().collect(Collectors.toMap(QuoteitemDTO::getId, s -> s));
            List<QuoteitemDTO> updateList = reqQuoteitemList.stream().filter(s -> Objects.nonNull(s.getId())).toList();
            // 前端传入报价中包含id则为更新
            if (CollUtil.isNotEmpty(updateList)) {
                updateList.forEach(s -> {
                    QuoteitemDTO quoteitemDTO = quoteitemDTOMap.get(s.getId());
                    if (Objects.isNull(quoteitemDTO)) {
                        return;
                    }
                    List<ChangeRecord> quoteRecords = new OperateCompareUtil<QuoteitemDTO>().compare(quoteitemDTO, s);
                    if (CollUtil.isNotEmpty(quoteRecords)) {
                        quoteRecords.forEach(quoteRecord -> {
                            quoteRecord.setFieldName(String.format("修改供应商报价:%s %s -> %s", quoteRecord.getFieldName(), quoteRecord.getOldValue(), quoteRecord.getValue()));
                        });
                        changeRecords.addAll(quoteRecords);
                    }
                });
            }
            // 前端传入报价未包含id则为新增
            List<QuoteitemDTO> insertList = reqQuoteitemList.stream().filter(s -> Objects.isNull(s.getId())).toList();
            if (CollUtil.isNotEmpty(insertList)) {
                insertList.forEach(insertQuote -> {
                    changeRecords.add(new ChangeRecord().setFieldName(String.format("新增供应商报价", insertQuote.getSequence())).setValue(insertQuote.getVenderName()));
                });
            }
            // 库中报价信息前端未传入则为删除
            Set<Long> list = reqQuoteitemList.stream().map(QuoteitemDTO::getId).collect(Collectors.toSet());
            List<QuoteitemDTO> deleteList = baseQuoteitemList.stream().filter(s -> !list.contains(s.getId())).toList();
            if (CollUtil.isNotEmpty(deleteList)) {
                deleteList.forEach(deleteQuote -> {
                    changeRecords.add(new ChangeRecord().setFieldName(String.format("删除供应商报价", deleteQuote.getSequence())).setOldValue(null));
                });
            }
        }
    }

    /**
     * 处理产品中图片列表 仅返回主图方便前端展示
     *
     * @param simpleSkuDOList 精简产品DO
     * @return 产品DTO
     */

    private List<SimpleSkuDTO> transformSkuDTO(List<SimpleSkuDO> simpleSkuDOList) {
        if (CollUtil.isEmpty(simpleSkuDOList)) {
            return null;
        }
        List<Long> hsCodeIdList = simpleSkuDOList.stream().map(SimpleSkuDO::getHsCodeId).distinct().toList();
        Map<Long, HsdataDO> hsCodeMap = hsdataService.getListByIds(hsCodeIdList);
        List<Long> skuIdList = simpleSkuDOList.stream().map(SimpleSkuDO::getId).distinct().toList();
        List<QuoteitemDTO> quoteitemDTOList = quoteitemApi.getQuoteItemDTOListBySkuIdList(skuIdList);
        List<QuoteitemDTO> fliterQuoteItemList = new ArrayList<>();
        Map<String, List<QuoteitemDTO>> quoteItemMap = new HashMap<>();
        if (CollUtil.isNotEmpty(quoteitemDTOList)) {
            quoteItemMap = quoteitemDTOList.stream().collect(Collectors.groupingBy(QuoteitemDTO::getSkuCode));
            //报价信息仅包含默认报价所对应的供应商的所有报价
            Map<String, List<QuoteitemDTO>> skuCodeMap = quoteitemDTOList.stream().collect(Collectors.groupingBy(QuoteitemDTO::getSkuCode));
            if (CollUtil.isNotEmpty(skuCodeMap)) {
                skuCodeMap.forEach((skuCode, quoteList) -> {
                    Optional<QuoteitemDTO> first = quoteList.stream().filter(s -> 1 == s.getDefaultFlag()).findFirst();
                    if (first.isPresent()) {
                        List<QuoteitemDTO> qList = quoteList.stream().filter(s -> Objects.equals(s.getVenderCode(), first.get().getVenderCode())).toList();
                        fliterQuoteItemList.addAll(qList);
                    }
                });
            }
        }

        List<String> venderCodeList = fliterQuoteItemList.stream().map(QuoteitemDTO::getVenderCode).toList();
        Map<String, SimpleVenderResp> venderMap = venderApi.getSimpleVenderMapByCodes(venderCodeList);
        // 获取翻单sku编号列表
        List<String> reorderSkuCodeList = skuMapper.getReorderSkuCodeList();
        // 将json图片列表转换为主图
        List<SimpleSkuDTO> simpleSkuDTOS = SkuConvert.INSTANCE.convertSimpleSkuDTOList(simpleSkuDOList, quoteItemMap, venderMap, reorderSkuCodeList);
        if (CollUtil.isEmpty(simpleSkuDTOS)) {
            return Collections.emptyList();
        }
        List<Long> buyerIds = simpleSkuDTOS.stream().map(SimpleSkuDTO::getBuyerIds).filter(CollUtil::isNotEmpty).flatMap(List::stream).filter(Objects::nonNull).distinct().toList();
        Map<Long, UserDept> userDeptListCache = adminUserApi.getUserDeptListCache(buyerIds);
        //给采购员idList赋值
        simpleSkuDTOS.forEach(s -> {
            Long hsCodeId = s.getHsCodeId();
            if (Objects.nonNull(hsCodeId) && CollUtil.isNotEmpty(hsCodeMap)) {
                HsdataDO hsdataDO = hsCodeMap.get(hsCodeId);
                if (Objects.nonNull(hsdataDO)) {
                    s.setHsdata(BeanUtils.toBean(hsdataDO, HsDataDTO.class));
                }
            }
            //判断采购员json数据是否存在 ,不存在是旧数据，根據ids组装数据，第一个为默认
            if (CollUtil.isEmpty(s.getPurchaseUserList()) && CollUtil.isNotEmpty(buyerIds)) {
                List<Long> baseBuyerIds = s.getBuyerIds();
                if (CollUtil.isNotEmpty(baseBuyerIds)) {
                    s.setPurchaseUserList(baseBuyerIds.stream().map(userDeptListCache::get).filter(Objects::nonNull).toList());
                    //设置第一个为默认
                    s.getPurchaseUserList().stream()
                            .filter(user -> Objects.equals(buyerIds.get(0), user.getUserId()))
                            .findFirst()
                            .ifPresent(user -> user.setDefaultFlag(BooleanEnum.YES.getValue()));
                }
            }

        });
        return simpleSkuDTOS;
    }

    private String getSkuNameById(Long skuId) {
        if (Objects.isNull(skuId)) {
            return null;
        }
        SkuDO skuDO = skuMapper.selectOne(new LambdaQueryWrapperX<SkuDO>().select(SkuDO::getName).eq(SkuDO::getId, skuId));
        if (Objects.isNull(skuDO)) {
            return null;
        }
        return skuDO.getName();

    }

    @Override
    @DataPermission(enable = false)
    public Map<Long, SkuDTO> getSkuMap(List<Long> idList) {
        LambdaQueryWrapperX<SkuDO> query = new LambdaQueryWrapperX<>();
        query.in(SkuDO::getId, idList);
        query.in(SkuDO::getDeleted, List.of(BooleanEnum.NO.getValue(), BooleanEnum.YES.getValue()));
        List<SkuDO> skuDOS = skuMapper.selectList(query);
        List<SkuDTO> skuDTOList = BeanUtils.toBean(skuDOS, SkuDTO.class);
        return skuDTOList.stream().collect(Collectors.toMap(SkuDTO::getId, Function.identity()));
    }

    @Override
    @DataPermission(enable = false)
    public Map<String, SkuDTO> getSkuMapByCodeList(List<String> codeList) {
        if (CollUtil.isEmpty(codeList)) {
            return null;
        }
        LambdaQueryWrapperX<SkuDO> query = new LambdaQueryWrapperX<>();
        query.in(SkuDO::getCode, codeList).eq(SkuDO::getChangeFlag, ChangeFlagEnum.NO.getValue());
        List<SkuDO> skuDOS = skuMapper.selectList(query);
        if (CollUtil.isEmpty(skuDOS)) {
            return new HashMap<>();
        }
        List<SkuDTO> skuDTOList = BeanUtils.toBean(skuDOS, SkuDTO.class);
        return skuDTOList.stream().collect(Collectors.toMap(SkuDTO::getCode, Function.identity()));
    }

    @Override
    @DataPermission(enable = false)
    public List<SkuDTO> getSkuDTOListByCodeList(List<String> codeList) {
        if (CollUtil.isEmpty(codeList)) {
            return null;
        }
        LambdaQueryWrapperX<SkuDO> query = new LambdaQueryWrapperX<>();
        query.in(SkuDO::getCode, codeList).eq(SkuDO::getChangeFlag, ChangeFlagEnum.NO.getValue());
        List<SkuDO> skuDOS = skuMapper.selectList(query);
        if (CollUtil.isEmpty(skuDOS)) {
            return List.of();
        }
        return BeanUtils.toBean(skuDOS, SkuDTO.class);
    }


    @Override
    @DataPermission(enable = false)
    public SimpleSkuDTO getSimpleSkuDTO(Long id) {
        List<Long> idList = new ArrayList<>();
        idList.add(id);
        List<SimpleSkuDO> simpleSkuDOList = skuMapper.getSimpleSku(null, null, null, null, null, null, null, null, null, idList, null, null, null, null, null, null, null, 0, idList.size());
        List<SimpleSkuDTO> simpleSkuDTOList = transformSkuDTO(simpleSkuDOList);
        if (CollUtil.isEmpty(simpleSkuDTOList)) {
            return null;
        }
        return simpleSkuDTOList.stream().collect(Collectors.toMap(SimpleSkuDTO::getId, s -> s)).get(0);
    }

    @Override
    @DataPermission(enable = false)
    public SkuDTO getSkuDTO(Long id) {
        LambdaQueryWrapperX lambdaQueryWrapperX = new LambdaQueryWrapperX<SkuDO>()
                .eqIfPresent(SkuDO::getId, id)
                .last("or (deleted = 1 and id = " + id + ")");
        SkuDO skuDO = skuMapper.selectOne(lambdaQueryWrapperX);
        SkuDTO skuDTO = BeanUtils.toBean(skuDO, SkuDTO.class);
        return skuDTO;
    }

    @Override
    @DataPermission(enable = false)
    public Map<String, SimpleData> validateOwnbrandSku(Map<String, Tuple> skuCodeMap, String custCode) {
        Map<String, SimpleData> result = new HashMap<>();
        if (CollUtil.isEmpty(skuCodeMap)) {
            throw exception(BASE_SKU_CODE_NOT_EMPTY);
        }
        Set<String> skuCodeSet = skuCodeMap.keySet();
        if (CollUtil.isEmpty(skuCodeSet)) {
            throw exception(BASE_SKU_CODE_NOT_EMPTY);
        }
        // 非客户产品的自营产品编码列表
        List<String> containCodeList = new ArrayList<>();
        List<SkuDO> baseSkuDOList = skuMapper.selectList(new LambdaQueryWrapperX<SkuDO>().in(SkuDO::getSourceCode, skuCodeSet)
                .eq(SkuDO::getCustCode, custCode)
                .eq(SkuDO::getAuditStatus, BpmProcessInstanceResultEnum.APPROVE.getResult()));
        // 如果该自营产品存在对应的客户产品，则判断：
        // ①客户产品的客户货号是否为空，空的话“客户货号” 更新为 当前销售合同明细里填写的客户货号
        //②客户产品的参考价格是否为空，空的话“参考价格” 更新为 当前销售合同明细里填写的销售价格
        Map<Long, SkuDO> updateMap = baseSkuDOList.stream().collect(Collectors.toMap(SkuDO::getId, Function.identity()));
        Set<Long> updateCodeSet = new HashSet<>();
        if (CollUtil.isNotEmpty(baseSkuDOList)) {
            baseSkuDOList.forEach(s -> {
                SimpleData simpleData = BeanUtils.toBean(s, SimpleData.class);
                result.put(s.getSourceCode(), simpleData);
                if (StrUtil.isEmpty(s.getCskuCode())) {
                    Tuple tuple = skuCodeMap.get(s.getSourceCode());
                    if (Objects.nonNull(tuple)) {
                        s.setCskuCode(tuple.get(0));
                        updateCodeSet.add(s.getId());
                    }
                }
                if (Objects.isNull(s.getPrice()) || Objects.isNull(s.getPrice().getAmount()) || BigDecimal.ZERO.compareTo(s.getPrice().getAmount()) == 0) {
                    Tuple tuple = skuCodeMap.get(s.getSourceCode());
                    if (Objects.nonNull(tuple)) {
                        s.setPrice(tuple.get(1));
                        updateCodeSet.add(s.getId());
                    }
                }
                if (StrUtil.isEmpty(s.getBarcode())) {
                    Tuple tuple = skuCodeMap.get(s.getSourceCode());
                    if (Objects.nonNull(tuple)) {
                        s.setCskuCode(tuple.get(2));
                        updateCodeSet.add(s.getId());
                    }
                }
            });
            if (CollUtil.isNotEmpty(updateCodeSet)) {
                List<SkuDO> updateList = updateCodeSet.stream().map(updateMap::get).filter(Objects::nonNull).toList();
                skuMapper.updateBatch(updateList);
            }
            List<String> sourceCodeList = baseSkuDOList.stream().map(SkuDO::getSourceCode).distinct().toList();
            containCodeList.addAll(CollUtil.subtract(skuCodeSet, sourceCodeList));
        } else {
            containCodeList.addAll(skuCodeSet);
        }
        if (CollUtil.isEmpty(containCodeList)) {
            return result;
        }
        // 获取产品主信息
        List<SkuDO> skuDOList = skuMapper.selectList(new LambdaQueryWrapperX<SkuDO>().in(SkuDO::getCode, containCodeList).eq(SkuDO::getChangeFlag, ChangeFlagEnum.NO.getValue()));
        if (CollUtil.isEmpty(skuDOList)) {
            return result;
        }
        List<Long> skuIdList = skuDOList.stream().map(SkuDO::getId).toList();
        // 获取报价缓存
        Map<String, List<QuoteitemDTO>> quoteItemMap = quoteitemApi.getQuoteItemDTOMapBySkuIdList(skuIdList);
        // 获取子产品及配件
        List<SkuBomDO> skuBomListByParentIdList = skuBomService.getSkuBomListByParentIdList(skuIdList);
        Map<Long, Map<Integer, List<SkuBomDO>>> skuBomResultMap = new HashMap<>();
        if (CollUtil.isNotEmpty(skuBomListByParentIdList)) {
            List<String> baseSkuCodeList = skuBomListByParentIdList.stream().map(SkuBomDO::getSkuCode).distinct().toList();
            Map<String, SkuDO> custSkuCodeMap = getCustSkuCodeList(custCode, baseSkuCodeList);
            Map<Long, List<SkuBomDO>> skuBomMap = skuBomListByParentIdList.stream().collect(Collectors.groupingBy(SkuBomDO::getParentSkuId));
            if (CollUtil.isNotEmpty(skuBomMap)) {
                skuBomMap.forEach((parentSkuId, skuBomDOList) -> {
                    Map<Integer, List<SkuBomDO>> itemSkuBomMap = new HashMap<>();
                    List<SkuBomDO> generalProducts = new ArrayList<>();
                    List<SkuBomDO> accessories = new ArrayList<>();
                    if (CollUtil.isEmpty(skuBomDOList)) {
                        return;
                    }
                    skuBomDOList.forEach(s -> {
                        if (CollUtil.isNotEmpty(custSkuCodeMap) && custSkuCodeMap.containsKey(s.getSkuCode())) {
                            SkuDO existSkuDO = custSkuCodeMap.get(s.getSkuCode());
                            s.setSkuId(existSkuDO.getId());
                            s.setSkuCode(existSkuDO.getCode());
                        }
                        Integer skuType = s.getSkuType();
                        if (SkuTypeEnum.ACCESSORIES.getValue().equals(skuType)) {
                            accessories.add(s);
                        } else {
                            generalProducts.add(s);
                        }
                    });
                    itemSkuBomMap.put(SkuTypeEnum.ACCESSORIES.getValue(), accessories);
                    itemSkuBomMap.put(SkuTypeEnum.GENERAL_PRODUCTS.getValue(), generalProducts);
                    skuBomResultMap.put(parentSkuId, itemSkuBomMap);
                });
            }
        }
        skuDOList.forEach(skuDO -> {
            SkuInfoSaveReqVO skuInfoSaveReqVO = BeanUtils.toBean(skuDO, SkuInfoSaveReqVO.class);
            String code = skuDO.getCode();
            if (CollUtil.isNotEmpty(quoteItemMap)) {
                skuInfoSaveReqVO.setQuoteitemList(quoteItemMap.get(code));
            }
            Long id = skuDO.getId();
            if (CollUtil.isNotEmpty(skuBomResultMap)) {
                Map<Integer, List<SkuBomDO>> proMap = skuBomResultMap.get(id);
                if (CollUtil.isNotEmpty(proMap)) {
                    List<SkuBomDO> skuBomDOAccList = proMap.get(SkuTypeEnum.ACCESSORIES.getValue());
                    if (CollUtil.isNotEmpty(skuBomDOAccList)) {
                        List<SimpleSkuSaveReqVO> simpleSkuSaveReqVOList = new ArrayList<>();
                        SimpleSkuSaveReqVO simpleSkuSaveReqVO = new SimpleSkuSaveReqVO();
                        skuBomDOAccList.forEach(s -> {
                            simpleSkuSaveReqVO.setId(s.getSkuId());
                            simpleSkuSaveReqVO.setSkuCode(s.getSkuCode());
                            simpleSkuSaveReqVOList.add(simpleSkuSaveReqVO);
                        });
                        skuInfoSaveReqVO.setAccessoriesList(simpleSkuSaveReqVOList);
                    }
                    List<SkuBomDO> skuBomDOGenList = proMap.get(SkuTypeEnum.GENERAL_PRODUCTS.getValue());
                    if (CollUtil.isNotEmpty(skuBomDOGenList)) {
                        List<SimpleSkuSaveReqVO> simpleSkuSaveReqVOList = new ArrayList<>();
                        SimpleSkuSaveReqVO simpleSkuSaveReqVO = new SimpleSkuSaveReqVO();
                        skuBomDOGenList.forEach(s -> {
                            simpleSkuSaveReqVO.setId(s.getSkuId());
                            simpleSkuSaveReqVO.setSkuCode(s.getSkuCode());
                            simpleSkuSaveReqVO.setQty(s.getQty());
                            simpleSkuSaveReqVOList.add(simpleSkuSaveReqVO);
                        });
                        skuInfoSaveReqVO.setSubProductList(simpleSkuSaveReqVOList);
                    }
                }
            }
            skuInfoSaveReqVO.setId(null);
            skuInfoSaveReqVO.setCode(null);
            skuInfoSaveReqVO.setAutoCreateFlag(BooleanEnum.YES.getValue());
            skuInfoSaveReqVO.setSubmitFlag(BooleanEnum.NO.getValue());
            skuInfoSaveReqVO.setSourceId(skuDO.getId());
            skuInfoSaveReqVO.setSourceCode(skuDO.getCode());
            skuInfoSaveReqVO.setCustCode(custCode);
            skuInfoSaveReqVO.setCustProFlag(BooleanEnum.YES.getValue());
            Tuple tuple = skuCodeMap.get(code);
            skuInfoSaveReqVO.setCskuCode(tuple.get(0));
            skuInfoSaveReqVO.setPrice(tuple.get(1));
            skuInfoSaveReqVO.setBarcode(tuple.get(2));
            skuInfoSaveReqVO.setAuditStatus(BpmProcessInstanceResultEnum.APPROVE.getResult());
            skuInfoSaveReqVO.setOwnBrandFlag(BooleanEnum.YES.getValue());
            SimpleData simpleData = getSkuSimpleDateBySkuCodeAndCustCode(skuDO.getCode(), custCode);
            if (simpleData == null && StringUtils.isNotBlank(custCode)) {
                simpleData = createSku(skuInfoSaveReqVO, false);
            }
            result.put(code, simpleData);
        });
        return result;
    }

    private SimpleData getSkuSimpleDateBySkuCodeAndCustCode(String skuCode, String custCode) {
        var query = new LambdaQueryWrapperX<SkuDO>().eq(SkuDO::getCustProFlag, BooleanEnum.YES.getValue())
                .eq(SkuDO::getSourceCode, skuCode)
                .eq(SkuDO::getChangeFlag, BooleanEnum.NO.getValue())
                .eq(SkuDO::getCustCode, custCode);
        Optional<SkuDO> first = skuMapper.selectList(query).stream().findFirst();
        if (first.isPresent()) {
            SkuDO skuDO = first.get();
            return new SimpleData().setId(skuDO.getId()).setCode(skuDO.getCode()).setName(skuDO.getName());
        }
        return null;
    }

    @Override
    @DataPermission(enable = false)
    public Map<String, List<UserDept>> getPurchaseUserListBySkuIdList(List<Long> skuIdList) {
        if (CollUtil.isEmpty(skuIdList)) {
            logger.error("[根据产品编号获取采购员]产品id为空");
            return null;
        }
        List<QuoteitemDTO> quoteitemDTOList = quoteitemApi.getQuoteItemDTOListBySkuIdList(skuIdList);
        if (CollUtil.isEmpty(quoteitemDTOList)) {
            logger.error("[根据产品编号获取采购员]报价信息为空,skuIdList-{}", skuIdList);
            return null;
        }
        List<String> venderCodeList = quoteitemDTOList.stream().filter(s -> BooleanEnum.YES.getValue().equals(s.getDefaultFlag())).map(QuoteitemDTO::getVenderCode).distinct().toList();

        Map<String, String> skuCodeVenderCodeMap = quoteitemDTOList.stream().filter(s ->
                        BooleanEnum.YES.getValue().equals(s.getDefaultFlag()))
                .collect(Collectors.toMap(QuoteitemDTO::getSkuCode, QuoteitemDTO::getVenderCode, (o, n) -> o));


        if (CollUtil.isEmpty(skuCodeVenderCodeMap) || CollUtil.isEmpty(venderCodeList)) {
            logger.error("[根据产品编号获取采购员]默认报价为空,quoteitemDTOList-{}", quoteitemDTOList);
            return null;
        }
        Map<String, List<UserDept>> venderManagerByCodeList = venderApi.getVenderManagerByCodeList(venderCodeList);
        if (CollUtil.isEmpty(venderManagerByCodeList)) {
            logger.error("[根据产品编号获取采购员]供应商为空venderCodeList-{}", venderCodeList);
            return null;
        }
        return skuCodeVenderCodeMap.entrySet().stream()
                .filter(entry -> venderManagerByCodeList.containsKey(entry.getValue()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> venderManagerByCodeList.get(entry.getValue()),
                        (existing, replacement) -> existing));
    }

    @Override
    public PageResult<PurchaseContractItemDTO> getAuxiliarySkuPurchasePage(PurchaseContractGetItemPageReqDTO reqDTO) {
        PageResult<PurchaseContractItemDTO> result = purchaseContractApi.getAuxiliarySkuPurchasePage(reqDTO);
        return result;
    }

    @Override
    public Boolean toCheckCustChange(SkuCheckCustReqVO reqVO) {
        List<Long> skuIdList = reqVO.getSkuIdList().stream().distinct().toList();
        if (skuIdList.isEmpty()) {
            return true;
        }
        AtomicReference<Boolean> result = new AtomicReference<>(true);
        List<SkuDO> skuDOS = skuMapper.selectList(SkuDO::getId, skuIdList);
        if (CollUtil.isEmpty(skuDOS)) {
            return false;
        }
        skuDOS.forEach((sku) -> {
            if (sku.getCustProFlag() == 1 && !Objects.equals(sku.getCustCode(), reqVO.getCustCode())) {
                result.set(false);
            }
        });

        List<SkuDO> skuDOList = skuDOS.stream().filter(s -> s.getOwnBrandFlag() == 1).distinct().toList();
        if (CollUtil.isEmpty(skuDOList)) {
            return result.get();
        }

        skuDOList.forEach(s -> {
            if (Objects.nonNull(reqVO.getCustCode()) && !reqVO.getCustCode().isEmpty()
                    && Objects.nonNull(s.getCustCode()) && !s.getCustCode().isEmpty()
                    && !Objects.equals(s.getCustCode(), reqVO.getCustCode())) {
                result.set(false);
            }
        });

        return result.get();
    }

    @Override
    public PageResult<SimpleSkuDTO> getSimpleOwnBrandSkuDTO(SimpleOwnBrandSkuPageReqVO reqVO) {
        Integer pageNo = reqVO.getPageNo();
        Integer pageSize = reqVO.getPageSize();
        int currentPage = (pageNo == null || pageNo < 1) ? 1 : pageNo;
        int pageSizeVal = (pageSize == null || pageSize <= 0) ? 10 : pageSize;
        int skip = (currentPage - 1) * pageSizeVal;

        //不使用客户信息查询自营产品
        List<SimpleSkuDO> simpleSkus = skuMapper.getSimpleSku(
                reqVO.getSkuCodeList(), null, reqVO.getCategoryId(), reqVO.getSkuName(), 0,
                null, null, 1, null, null, reqVO.getSkuType(),
                null, null, reqVO.getSkuCode(), reqVO.getOskuCode(), reqVO.getBasicSkuCode(), reqVO.getAgentFlag(), skip, pageSizeVal);
        if (CollUtil.isEmpty(simpleSkus)) {
            return PageResult.empty();
        }
        List<SimpleSkuDO> resultSkus = new ArrayList<>();
        List<SimpleSkuDTO> simpleSkuDTOList = new ArrayList<>();
        if(StrUtil.isBlank(reqVO.getCustCode())){  //不传客户产品或空字符串仅查询自营产品，不替换客户产品
            simpleSkuDTOList = transformSkuDTO(simpleSkus);
        }else {
            //根据查询结果的sourceCode加上客户信息查询客户产品
            List<String> skuCodes = simpleSkus.stream().map(SimpleSkuDO::getCode).distinct().toList();
            List<SimpleSkuDO> custSimpleSkus = skuMapper.getSimpleSku(
                    null, skuCodes, null, null, 1,
                    reqVO.getCustCode(), null, 1, null, null, null,
                    null, null, null, null, null, null, 0, pageSizeVal);
            Map<String, SimpleSkuDO> custSkuMap = custSimpleSkus.stream().collect(Collectors.toMap(SimpleSkuDO::getSourceCode, Function.identity(), (v1, v2) -> v1));
            //使用客户产品替换对应的自营产品
            resultSkus = simpleSkus.stream().map(sku -> custSkuMap.getOrDefault(sku.getCode(), sku)).toList();

            simpleSkuDTOList = transformSkuDTO(resultSkus);
            //组合产品增加子产品信息
            if (Objects.equals(reqVO.getSkuType(), SkuTypeEnum.PRODUCT_MIX.getValue()) && CollUtil.isNotEmpty(simpleSkuDTOList)) {
                List<String> codeList = simpleSkuDTOList.stream().map(SimpleSkuDTO::getCode).distinct().toList();
                Map<String, List<SimpleSkuDTO>> subSkuMap = getSubSkuMap(codeList);
                if (CollUtil.isNotEmpty(subSkuMap)) {
                    simpleSkuDTOList.forEach(sku -> {
                        sku.setSonSkuList(subSkuMap.get(sku.getCode()));
                    });
                }
            }
        }

        //基础产品编号赋值
        List<String> sourceCodeList = simpleSkuDTOList.stream().map(SimpleSkuDTO::getSourceCode).distinct().toList();
        if (CollUtil.isNotEmpty(sourceCodeList)) {
            List<SkuDO> sourceSkuList = skuMapper.selectList(new LambdaQueryWrapperX<SkuDO>().select().in(SkuDO::getCode, sourceCodeList));
            Map<String, List<SkuDO>> sourceSkuMap = sourceSkuList.stream().collect(Collectors.groupingBy(SkuDO::getCode));
            simpleSkuDTOList.forEach(item -> {
                String basicSkuCode = item.getCode();
                if (CollUtil.isNotEmpty(Collections.singleton(item.getSourceCode()))) {
                    List<SkuDO> skuDos = sourceSkuMap.get(item.getSourceCode());
                    if (CollUtil.isNotEmpty(skuDos)) {
                        if (StringUtils.isNotEmpty(skuDos.get(0).getSourceCode())) {
                            basicSkuCode = skuDos.get(0).getSourceCode();
                        } else {
                            basicSkuCode = skuDos.get(0).getCode();
                        }
                    }
                }
                item.setBasicSkuCode(basicSkuCode);
            });
        }
        Long totalCount = skuMapper.getSkuCount(reqVO.getSkuCodeList(), null, reqVO.getCategoryId(), reqVO.getSkuName(),
                0, null, null, 1, null, null, reqVO.getSkuType(),
                null, null, null, reqVO.getOskuCode(), reqVO.getBasicSkuCode(), reqVO.getAgentFlag());
        return new PageResult<SimpleSkuDTO>().setList(simpleSkuDTOList).setTotal(totalCount);

    }

    @Override
    public ChangeEffectRespVO getChangeEffect(SkuInfoRespVO sku) {
        ChangeEffectRespVO changeEffectRespVO = new ChangeEffectRespVO();
        boolean effectRangeFlag = (Objects.nonNull(sku.getCustProFlag()) && BooleanEnum.YES.getValue().equals(sku.getCustProFlag())) || (Objects.nonNull(sku.getOwnBrandFlag()) && BooleanEnum.YES.getValue().equals(sku.getOwnBrandFlag()));
        if (!effectRangeFlag) {
            return changeEffectRespVO;
        }
        //查询新客户与原客户
        SkuInfoRespVO oldSku = getOldSku(sku);

        //初始化变更标记
        final boolean[] changeFlag = {false, false, false};
        Integer submitFlag = BooleanEnum.NO.getValue();
        //查询所有表的配置
        Map<String, FormChangeDTO> formChangeDTOList = formChangeApi.getFormChangeDTOList(List.of("pms_sku", "pms_sku_bom", "scm_quote_item"));
        if (CollUtil.isEmpty(formChangeDTOList)) {
            throw exception(FIELD_CHANGE_CONFIG_NOT_EXISTS);
        }
        //产品主表
        Set<String> changeFields = new ChangeCompareUtil<SkuInfoRespVO>().transformObject(oldSku, sku);
        compareTableField(changeFields, formChangeDTOList.get("pms_sku"), changeFlag, submitFlag);

        //产品子表信息
        List<SkuInfoRespVO> oldSkuBomList = oldSku.getSubProductList();
        List<SkuInfoRespVO> skuBomList = sku.getSubProductList();
        List<DiffRecord<SkuInfoRespVO>> skuBomDiffRecords = DiffUtil.compareLists(oldSkuBomList, skuBomList);
        if (CollUtil.isNotEmpty(skuBomDiffRecords)){
            long count = skuBomDiffRecords.stream().filter(s -> ChangeTypeEnum.ADDED.getType().equals(s.getChangeFlag()) || ChangeTypeEnum.DELETED.getType().equals(s.getChangeFlag())).count();
            if (count > 0){
                changeFlag[0] = true;
                changeFlag[1] = true;
                changeFlag[2] = true;
            }
        }
        Tuple skuBomTuple = new ChangeCompareUtil<SkuInfoRespVO>().transformChildList(skuBomDiffRecords, SkuInfoRespVO.class);
        compareTableField(skuBomTuple.get(1), formChangeDTOList.get("pms_sku_bom"), changeFlag, submitFlag);

        //产品配件表信息
        List<SkuInfoRespVO> oldAccessoriesList = oldSku.getAccessoriesList();
        List<SkuInfoRespVO> accessoriesList = sku.getSubProductList();
        List<DiffRecord<SkuInfoRespVO>> accessoriesDiffRecords = DiffUtil.compareLists(oldAccessoriesList, accessoriesList);
        Tuple accessoriesTuple = new ChangeCompareUtil<SkuInfoRespVO>().transformChildList(accessoriesDiffRecords, SkuInfoRespVO.class);
        compareTableField(accessoriesTuple.get(1), formChangeDTOList.get("pms_sku_bom"), changeFlag, submitFlag);

        //产品报价
        List<QuoteitemDTO> oldQuoteitemList = oldSku.getQuoteitemDTOList();
        List<QuoteitemDTO> quoteitemList = sku.getQuoteitemDTOList();
        List<DiffRecord<QuoteitemDTO>> quoteitemDiffRecords = DiffUtil.compareLists(oldQuoteitemList, quoteitemList);
        Tuple quoteitemTuple = new ChangeCompareUtil<QuoteitemDTO>().transformChildList(quoteitemDiffRecords, QuoteitemDTO.class);
        compareTableField(quoteitemTuple.get(1), formChangeDTOList.get("scm_quote_item"), changeFlag, submitFlag);

        // 处理影响范围
        List<JsonEffectRange> effectRangeList = new ArrayList<>();
        //更新该产品未完成的所有销售合同为未确认
        List<SmsContractAllDTO> smsContracts = new ArrayList<>();
        if (changeFlag[0]) {
            smsContracts = saleContractApi.getUnCompletedSaleContractBySkuCode(sku.getCode());
            if (CollUtil.isNotEmpty(smsContracts)) {
                smsContracts.forEach(s -> {
                    effectRangeList.add(new JsonEffectRange().setEffectRangeCode(s.getCode()).setEffectRangeType(EffectRangeEnum.SMS.getValue()).setConfirmFlag(ConfirmFlagEnum.NO.getValue()));
                });
            }
        }
        //更新该产品未完成的所有出运明细为未确认
        List<PurchaseContractAllDTO> purchaseContracts = new ArrayList<>();
        if (changeFlag[0]) {
            purchaseContracts = purchaseContractApi.getUnCompletedPurchaseContractBySkuCode(sku.getCode());
            if (CollUtil.isNotEmpty(purchaseContracts)) {
                purchaseContracts.forEach(s -> {
                    effectRangeList.add(new JsonEffectRange().setEffectRangeCode(s.getCode()).setEffectRangeType(EffectRangeEnum.SCM.getValue()).setConfirmFlag(ConfirmFlagEnum.NO.getValue()));
                });
            }
        }
        //更新该客户未完成的所有出运明细为未确认
        List<ShipmentDTO> shipments = new ArrayList<>();
        if (changeFlag[2]) {
            shipments = shipmentApi.getUnShippedDTOBySkuCode(sku.getCode());
            if (CollUtil.isNotEmpty(shipments)) {
                shipments.forEach(s -> {
                    effectRangeList.add(new JsonEffectRange().setEffectRangeCode(s.getCode()).setEffectRangeType(EffectRangeEnum.DMS.getValue()).setConfirmFlag(ConfirmFlagEnum.NO.getValue()));
                });
            }
        }
        changeEffectRespVO.setSubmitFlag(submitFlag);
        changeEffectRespVO.setEffectRangeList(effectRangeList);
        return changeEffectRespVO;
    }

    public Boolean getChangeLevelFlag(SkuInfoRespVO sku) {
        //查询新客户与原客户
        SkuInfoRespVO oldSku = getOldSku(sku);
        //初始化变更标记
        final boolean[] changeFlag = {false};
        //查询所有表的配置
        Map<String, FormChangeDTO> formChangeDTOList = formChangeApi.getFormChangeDTOList(List.of("pms_sku", "pms_sku_bom", "scm_quote_item"));
        if (CollUtil.isEmpty(formChangeDTOList)) {
            throw exception(FIELD_CHANGE_CONFIG_NOT_EXISTS);
        }
        //产品主表
        Set<String> changeFields = new ChangeCompareUtil<SkuInfoRespVO>().transformObject(oldSku, sku);
        compareChangeLevelTableField(changeFields, formChangeDTOList.get("pms_sku"), changeFlag);
        //产品子表信息
        List<SkuInfoRespVO> oldSkuBomList = oldSku.getSubProductList();
        List<SkuInfoRespVO> skuBomList = sku.getSubProductList();
        List<DiffRecord<SkuInfoRespVO>> skuBomDiffRecords = DiffUtil.compareLists(oldSkuBomList, skuBomList);
        Tuple skuBomTuple = new ChangeCompareUtil<SkuInfoRespVO>().transformChildList(skuBomDiffRecords, SkuInfoRespVO.class);
        compareChangeLevelTableField(skuBomTuple.get(1), formChangeDTOList.get("pms_sku_bom"), changeFlag);
        //产品配件表信息
        List<SkuInfoRespVO> oldAccessoriesList = oldSku.getAccessoriesList();
        List<SkuInfoRespVO> accessoriesList = sku.getSubProductList();
        List<DiffRecord<SkuInfoRespVO>> accessoriesDiffRecords = DiffUtil.compareLists(oldAccessoriesList, accessoriesList);
        Tuple accessoriesTuple = new ChangeCompareUtil<SkuInfoRespVO>().transformChildList(accessoriesDiffRecords, SkuInfoRespVO.class);
        compareChangeLevelTableField(accessoriesTuple.get(1), formChangeDTOList.get("pms_sku_bom"), changeFlag);
        //产品报价
        List<QuoteitemDTO> oldQuoteitemList = oldSku.getQuoteitemDTOList();
        List<QuoteitemDTO> quoteitemList = sku.getQuoteitemDTOList();
        List<DiffRecord<QuoteitemDTO>> quoteitemDiffRecords = DiffUtil.compareLists(oldQuoteitemList, quoteitemList);
        Tuple quoteitemTuple = new ChangeCompareUtil<QuoteitemDTO>().transformChildList(quoteitemDiffRecords, QuoteitemDTO.class);
        compareChangeLevelTableField(quoteitemTuple.get(1), formChangeDTOList.get("scm_quote_item"), changeFlag);
        return changeFlag[0];
    }

    private void compareTableField(Set<String> changeFieldNames, FormChangeDTO formChange, boolean[] changeFlag, Integer submitFlag) {
        if (formChange != null) {
            //影响销售的字段
            List<FormChangeItemDTO> smsItems = formChange.getChildren().stream().filter(s -> Objects.equals(s.getEffectMainInstanceFlag(), EffectMainInstanceFlagEnum.YES.getValue()) && s.getEffectRange().contains(EffectRangeEnum.SMS.getValue())).toList();
            //影响采购的字段
            List<FormChangeItemDTO> scmItems = formChange.getChildren().stream().filter(s -> Objects.equals(s.getEffectMainInstanceFlag(), EffectMainInstanceFlagEnum.YES.getValue()) && s.getEffectRange().contains(EffectRangeEnum.SCM.getValue())).toList();
            //影响出运的字段
            List<FormChangeItemDTO> dmsItems = formChange.getChildren().stream().filter(s -> Objects.equals(s.getEffectMainInstanceFlag(), EffectMainInstanceFlagEnum.YES.getValue()) && s.getEffectRange().contains(EffectRangeEnum.DMS.getValue())).toList();
            smsItems.forEach(s -> {
                if (changeFieldNames.contains(StrUtils.snakeToCamel(s.getNameEng()))) {
                    changeFlag[0] = true;
                }
            });
            scmItems.forEach(s -> {
                if (changeFieldNames.contains(StrUtils.snakeToCamel(s.getNameEng()))) {
                    changeFlag[1] = true;
                }
            });
            dmsItems.forEach(s -> {
                if (changeFieldNames.contains(StrUtils.snakeToCamel(s.getNameEng()))) {
                    changeFlag[2] = true;
                }
            });
            boolean isSubmitFlag = formChange.getChildren().stream().anyMatch(s -> ChangeLevelEnum.FORM.getValue().equals(s.getChangeLevel()));
            if (isSubmitFlag) {
                submitFlag = BooleanEnum.YES.getValue();
            }
        }
    }

    private void compareChangeLevelTableField(Set<String> changeFieldNames, FormChangeDTO formChange, boolean[] changeFlag) {
        if (formChange != null) {
            List<FormChangeItemDTO> itemDTOS = formChange.getChildren().stream().filter(s -> ChangeLevelEnum.FORM.getValue().equals(s.getChangeLevel())).toList();
            itemDTOS.forEach(s -> {
                if (changeFieldNames.contains(StrUtils.snakeToCamel(s.getNameEng()))) {
                    changeFlag[0] = true;
                }
            });
        }
    }

    /**
     * 获得产品变更资料
     *
     * @param req
     * @return 产品变更资料
     */
    @Override
    public SkuInfoRespVO getSkuChange(SkuDetailReq req) {
        return getSkuDetail(req, true);
//        if (sku != null && sku.getVer() > 1) {
//            sku.setOldSku(getOldSku(sku));
//        }
//        return sku;
    }

    /**
     * 根据sku编号获得最新的产品变更资料
     *
     * @param code
     * @return 产品变更资料
     */
    @Override
    @DataPermission(enable = false)
    public SkuInfoRespVO getSkuChangeByCode(String code) {
        if (StrUtil.isEmpty(code)) {
            return null;
        }
        LambdaQueryWrapperX<SkuDO> skuDOLambdaQueryWrapperX = new LambdaQueryWrapperX<SkuDO>();
        skuDOLambdaQueryWrapperX.eq(SkuDO::getCode, code);
        skuDOLambdaQueryWrapperX.eq(SkuDO::getAuditStatus, BpmProcessInstanceResultEnum.APPROVE.getResult());
        skuDOLambdaQueryWrapperX.eq(SkuDO::getChangeFlag, ChangeFlagEnum.NO.getValue());
        SkuDO skuDO = skuMapper.selectOne(skuDOLambdaQueryWrapperX);
        return getSkuDetail(new SkuDetailReq().setSkuId(skuDO.getId()), true);
//        if (sku != null && sku.getVer() > 1) {
//            sku.setOldSku(getOldSku(sku));
//        }
//        return sku;
    }

    /**
     * 获得旧产品资料
     *
     * @param sku
     * @return 旧产品资料
     */
    @Override
    public SkuInfoRespVO getOldSku(SkuInfoRespVO sku) {
        LambdaQueryWrapperX<SkuDO> skuDOLambdaQueryWrapperX = new LambdaQueryWrapperX<SkuDO>();
        skuDOLambdaQueryWrapperX.eq(SkuDO::getCode, sku.getCode());
        skuDOLambdaQueryWrapperX.eq(SkuDO::getVer, sku.getVer() - 1);
        skuDOLambdaQueryWrapperX.eq(SkuDO::getAuditStatus, BpmProcessInstanceResultEnum.APPROVE.getResult());
        skuDOLambdaQueryWrapperX.eq(SkuDO::getChangeDeleted, DeletedEnum.NO.getValue());
        skuDOLambdaQueryWrapperX.in(SkuDO::getDeleted, new ArrayList<>(Arrays.asList(0, 1)));
        SkuDO oldSku = skuMapper.selectOne(skuDOLambdaQueryWrapperX);
        if (oldSku == null) {
            return null;
        }
        SkuInfoRespVO skuInfoRespVO = getSkuDetail(new SkuDetailReq().setSkuId(oldSku.getId()), true);
        return skuInfoRespVO;
    }

    @Override
    @DataPermission(enable = false)
    public String getProcessDefinitionKeyByBusinessId(Long id) {
        SkuDO skuChange = skuMapper.selectById(id);
        if (Objects.isNull(skuChange)) {
            return null;
        }
        return skuChange.getModelKey();
    }

    @Override
    @DataPermission(enable = false)
    public List<SkuBomDO> getSonSkuListBySkuCode(String skuCode) {
        Optional<SkuDO> first = skuMapper.selectList(new LambdaQueryWrapperX<SkuDO>().eq(SkuDO::getCode, skuCode).eq(SkuDO::getChangeFlag, ChangeFlagEnum.NO.getValue())).stream().findFirst();
        return first.map(skuDO -> skuBomService.getSkuBomListBYSkuId(skuDO.getId())).orElse(null);
    }

    @Override
    public void approveOwnBrandTask(Long userId, BpmTaskApproveReqDTO reqVO) {
        bpmProcessInstanceApi.approveTask(userId, BeanUtils.toBean(reqVO, BpmTaskApproveReqDTO.class));
    }

    @Override
    public void rejectOwnBrandTask(Long userId, BpmTaskRejectReqDTO reqVO) {
        bpmProcessInstanceApi.rejectTask(userId, BeanUtils.toBean(reqVO, BpmTaskRejectReqDTO.class));
    }

    @Override
    public void submitOwnBrandTask(Long id, Long userId) {
        SkuDO skuDO = validateSkuExists(id);
        String creator = skuDO.getCreator();
        if (StrUtil.isEmpty(creator)) {
            throw exception(CREATE_USER_IS_NULL);
        }
        UserDept userDept = adminUserApi.getUserDeptByUserId(Long.parseLong(creator));
        Map<String, Object> variables = new HashMap<>();
        variables.put("deptCode", userDept.getDeptCode());
        String processInstanceId = bpmProcessInstanceApi.createProcessInstance(userId, OWN_BRAND_PROCESS_DEFINITION_KEY, id, variables, Map.of());
        updateAuditStatus(id, BpmProcessInstanceResultEnum.PROCESS.getResult(), processInstanceId);
    }

    @Override
    public String getOwnBrandProcessDefinitionKey() {
        return OWN_BRAND_PROCESS_DEFINITION_KEY;
    }

    @Override
    public void approveChangeOwnBrandTask(Long userId, BpmTaskApproveReqDTO reqVO) {
        bpmProcessInstanceApi.approveTask(userId, BeanUtils.toBean(reqVO, BpmTaskApproveReqDTO.class));
    }

    @Override
    public void rejectChangeOwnBrandTask(Long userId, BpmTaskRejectReqDTO reqVO) {
        bpmProcessInstanceApi.rejectTask(userId, BeanUtils.toBean(reqVO, BpmTaskRejectReqDTO.class));
    }

    @Override
    public void submitChangeOwnBrandTask(Long skuId, Long userId) {
        String processInstanceId = bpmProcessInstanceApi.createProcessInstance(userId, CHANGE_OWN_BRAND_PROCESS_DEFINITION_KEY, skuId);
        updateAuditStatus(skuId, BpmProcessInstanceResultEnum.PROCESS.getResult(), processInstanceId);
    }

    @Override
    @DataPermission(enable = false)
    public Map<String, SkuNameDTO> getSkuNameCacheByCodeList(List<String> codeList) {
        if (CollUtil.isEmpty(codeList)) {
            return Map.of();
        }
        List<SkuDO> skuDOS = skuMapper.selectList(new LambdaQueryWrapperX<SkuDO>().select(SkuDO::getName, SkuDO::getCode, SkuDO::getNameEng, SkuDO::getDeclarationName, SkuDO::getCustomsDeclarationNameEng).in(SkuDO::getCode, codeList).eq(SkuDO::getChangeFlag, BooleanEnum.NO.getValue()));
        if (CollUtil.isEmpty(skuDOS)) {
            return Map.of();
        }
        return skuDOS.stream().collect(Collectors.toMap(SkuDO::getCode, s -> BeanUtils.toBean(s, SkuNameDTO.class)));
    }

    @Override
    public String generateCode(Long categoryId, Integer skuType) {
        if (!Objects.equals(skuType, SkuTypeEnum.AUXILIARY_MATERIALS.getValue())) {
            String code = getCode(categoryId);
            while (skuMapper.exists(new LambdaQueryWrapperX<SkuDO>().eq(SkuDO::getCode, code))){
                code = getCode(categoryId);
            }
            return code;
        } else {
            String code = codeGeneratorApi.getCodeGenerator(SN_TYPE, "SK", true, 3);
            while (skuMapper.exists(new LambdaQueryWrapperX<SkuDO>().eq(SkuDO::getCode, code))){
                code = codeGeneratorApi.getCodeGenerator(SN_TYPE, "SK", true, 3);
            }
            return code;
        }
    }

    @Override
    @DataPermission(enable = false)
    public boolean antiAudit(Long id) {
        // 校验是否存在
        SkuDO skuDO = validateSkuExists(id);
        // 校验反审核状态
        validateAntiAuditStatus(id);
        // 更改单据状态
        skuDO.setAuditStatus(BpmProcessInstanceResultEnum.UNSUBMITTED.getResult());
        int i = skuMapper.updateById(skuDO);
        return i > 0;
    }

    @Override
    public Map<String, String> getHsDataUnitBySkuCodes(List<String> skuCodeList) {

        List<SkuDO> doList = skuMapper.selectList(SkuDO::getCode, skuCodeList);
        if (CollUtil.isEmpty(doList)) {
            return null;
        }
        List<Long> hsIdList = doList.stream().map(SkuDO::getHsCodeId).distinct().toList();
        Map<Long, HsdataDO> hsDataMap = hsdataService.getListByIds(hsIdList);
        if (CollUtil.isNotEmpty(hsDataMap)) {
            Map<String, String> result = new HashMap<>();
            doList.forEach(s -> {
                HsdataDO hsdataDO = hsDataMap.get(s.getHsCodeId());
                if (Objects.nonNull(hsdataDO)) {
                    result.put(s.getCode(), hsdataDO.getUnit());
                }
            });
            return result;
        }
        return null;
    }

    @Override
    public Map<String, HsDataDTO> getHsDataBySkuCodes(List<String> skuCodeList) {
        List<SkuDO> doList = skuMapper.selectList(SkuDO::getCode, skuCodeList);
        if (CollUtil.isEmpty(doList)) {
            return null;
        }
        List<Long> hsIdList = doList.stream().map(SkuDO::getHsCodeId).distinct().toList();
        Map<Long, HsdataDO> hsDataMap = hsdataService.getListByIds(hsIdList);
        if (CollUtil.isNotEmpty(hsDataMap)) {
            Map<String, HsDataDTO> result = new HashMap<>();
            doList.forEach(s -> {
                HsdataDO hsdataDO = hsDataMap.get(s.getHsCodeId());
                if (Objects.nonNull(hsdataDO)) {
                    result.put(s.getCode(), new HsDataDTO().setId(hsdataDO.getId()).setCode(hsdataDO.getCode()).setUnit(hsdataDO.getUnit()));
                }
            });
            return result;
        }
        return null;

    }

    @Override
    public Map<Long, List<SkuBomDTO>> getAllBomDTOMap() {
        return skuBomService.getAllBomDTOMap();
    }

    @Override
    public Map<String, Integer> getSkuTypeMap(List<String> skuCodeList) {
        if (CollUtil.isEmpty(skuCodeList)) {
            return Map.of();
        }
        List<SkuDO> skuDOList = skuMapper.selectList(new LambdaQueryWrapperX<SkuDO>().select(SkuDO::getCode, SkuDO::getSkuType).eq(SkuDO::getChangeFlag, ChangeFlagEnum.NO.getValue()).in(SkuDO::getCode, skuCodeList));
        if (CollUtil.isEmpty(skuDOList)) {
            return Map.of();
        }
        return skuDOList.stream().collect(Collectors.toMap(SkuDO::getCode, SkuDO::getSkuType, (o, n) -> o));
    }

    @Override
    public Map<Long, Boolean> getSkuExitsByIds(List<Long> skuIds) {
        Map<Long, Boolean> map = new HashMap<>();
        if (CollUtil.isEmpty(skuIds)) {
            return null;
        }
        List<SkuDO> skuDOS = skuMapper.selectList(SkuDO::getId, skuIds);
        skuIds.forEach(s -> {
            if (skuDOS.stream().anyMatch(sku -> Objects.equals(sku.getId(), s))) {
                map.put(s, true);
            } else {
                map.put(s, false);
            }
        });
        return map;
    }

    @Override
    public JsonAmount getCombSkuPrice(Long skuId) {
        List<SkuBomDO> skuBomList = skuBomService.getSkuBomListByParentId(skuId);
        if (CollUtil.isEmpty(skuBomList)) {
            return new JsonAmount();
        }
        List<Long> skuIdList = skuBomList.stream().map(SkuBomDO::getSkuId).toList();
        Map<String, JsonAmount> priceMap = quoteitemApi.getPriceMapBySkuIdList(skuIdList);
        if (CollUtil.isEmpty(priceMap)) {
            return new JsonAmount();
        }
        AtomicReference<String> currency = new AtomicReference<>(CommonDict.EMPTY_STR);
        AtomicReference<BigDecimal> amount = new AtomicReference<>(BigDecimal.ZERO);
        skuBomList.forEach(s -> {
            JsonAmount price = priceMap.get(s.getSkuCode());
            if (Objects.isNull(price)) {
                return;
            }
            BigDecimal quotePrice = price.getAmount();
            amount.set(NumUtil.add(NumUtil.mul(quotePrice, s.getQty()), amount.get()));
            if (StrUtil.isEmpty(currency.get())) {
                currency.set(price.getCurrency());
            }
        });
        return new JsonAmount(amount.get(), currency.get());
    }

    @Override
    public Map<String, List<String>> getSkuSpec(List<String> skuCodeList) {
        if (CollUtil.isEmpty(skuCodeList)) {
            return Map.of();
        }
        // 1. 获取skuId列表
        List<SkuDO> skuDOS = skuMapper.selectList(new LambdaQueryWrapperX<SkuDO>()
                .select(SkuDO::getId, SkuDO::getCode)
                .in(SkuDO::getCode, skuCodeList)
                .eq(SkuDO::getChangeFlag, ChangeFlagEnum.NO.getValue()));
        if (CollUtil.isEmpty(skuDOS)) {
            return Map.of();
        }

        // 2. 获取默认报价信息
        List<Long> skuIds = skuDOS.stream().map(SkuDO::getId).toList();
        List<QuoteitemDTO> quoteitemList = quoteitemApi.getQuoteItemDTOListBySkuIdList(skuIds);
        if (CollUtil.isEmpty(quoteitemList)) {
            return Map.of();
        }

        // 3. 构建skuId到默认报价的映射
        Map<Long, QuoteitemDTO> defaultQuoteMap = quoteitemList.stream()
                .filter(quote -> quote.getDefaultFlag() != null && quote.getDefaultFlag() == 1)
                .collect(Collectors.toMap(QuoteitemDTO::getSkuId, quote -> quote, (o, n) -> n));

        // 4. 构建返回结果
        return skuDOS.stream()
                .filter(sku -> defaultQuoteMap.containsKey(sku.getId()))
                .collect(Collectors.toMap(
                        SkuDO::getCode,
                        sku -> {
                            QuoteitemDTO quote = defaultQuoteMap.get(sku.getId());
                            List<JsonSpecificationEntity> specificationList = quote.getSpecificationList();
                            return specificationList.stream().map(s -> s.getOuterboxLength().setScale(2, RoundingMode.HALF_UP) + "*" +
                                    s.getOuterboxWidth().setScale(2, RoundingMode.HALF_UP) + "*" +
                                    s.getOuterboxHeight().setScale(2, RoundingMode.HALF_UP)).toList();
                        },
                        (o, n) -> n
                ));
    }

    @Override
    public Map<String, BigDecimal> getTaxRateBySkuCodeList(Collection<String> skuCodes) {
        if (CollUtil.isEmpty(skuCodes)) {
            return Map.of();
        }
        List<SkuDO> skuDOList = skuMapper.selectList(new LambdaQueryWrapperX<SkuDO>().select(SkuDO::getId).eq(SkuDO::getChangeFlag, ChangeFlagEnum.NO.getValue()).in(SkuDO::getCode, skuCodes));
        if (CollUtil.isEmpty(skuDOList)) {
            return Map.of();
        }
        return quoteitemApi.getTaxRateBySkuIdList(skuDOList.stream().map(SkuDO::getId).toList());
    }

    @Override
    public Map<String, List<SkuBomDTO>> getSonSkuMapBySkuCode(List<String> skuCodes) {
        List<SkuDO> skuDOList = skuMapper.selectList(new LambdaQueryWrapperX<SkuDO>().select(SkuDO::getId, SkuDO::getCode).eq(SkuDO::getChangeFlag, ChangeFlagEnum.NO.getValue()).in(SkuDO::getCode, skuCodes));
        if (CollUtil.isEmpty(skuDOList)) {
            return Map.of();
        }
        Map<Long, String> skuMap = skuDOList.stream().collect(Collectors.toMap(SkuDO::getId, SkuDO::getCode));
        List<SkuBomDO> skuBomList = skuBomService.getSkuBomListByParentIdList(new ArrayList<>(skuMap.keySet()));
        if (CollUtil.isEmpty(skuBomList)) {
            return Map.of();
        }
        return skuBomList.stream().collect(Collectors.groupingBy(s -> skuMap.get(s.getParentSkuId()), Collectors.mapping(s -> BeanUtils.toBean(s, SkuBomDTO.class), Collectors.toList())));
    }

    @Override
    public Map<String, String> getBasicSkuCode(List<String> skuCodes) {
        List<SkuDO> skuDOS = skuMapper.selectList(new LambdaQueryWrapperX<SkuDO>().select(SkuDO::getCode, SkuDO::getBasicSkuCode).eq(SkuDO::getChangeFlag, ChangeFlagEnum.NO.getValue()).in(SkuDO::getCode, skuCodes));
        if (CollUtil.isEmpty(skuDOS)) {
            return Map.of();
        }
        Map<String, String> result = new HashMap<>();
        skuDOS.forEach(s -> {
            if (StrUtil.isNotEmpty(s.getBasicSkuCode())) {
                result.put(s.getCode(), s.getBasicSkuCode());
            }
        });
        return result;
    }

    @Override
    public Map<String, List<String>> getOwnSkuCodeListBySkuCode(List<String> skuCodeList) {
        if (CollUtil.isEmpty(skuCodeList)) {
            return Map.of();
        }
        List<SkuDO> skuDOList = skuMapper.selectList(new LambdaQueryWrapperX<SkuDO>().select(SkuDO::getCode, SkuDO::getBasicSkuCode).in(SkuDO::getCode, skuCodeList).eq(SkuDO::getChangeFlag, ChangeFlagEnum.NO.getValue()).eq(SkuDO::getAuditStatus, BpmProcessInstanceResultEnum.APPROVE.getResult()));
        if (CollUtil.isEmpty(skuDOList)) {
            return Map.of();
        }
        // 基础产品编号为空则抛异常
        List<String> basicSkuCodeList = skuDOList.stream().map(s -> {
            String basicSkuCode = s.getBasicSkuCode();
            if (StrUtil.isEmpty(basicSkuCode)) {
                throw exception(BASIC_SKU_CODE_IS_NULL);
            }
            return basicSkuCode;
        }).distinct().toList();
        // 查出当前编号所对应所有自营产品编号
        List<SkuDO> ownSkuList = skuMapper.selectList(new LambdaQueryWrapperX<SkuDO>().select(SkuDO::getCode, SkuDO::getBasicSkuCode).in(SkuDO::getBasicSkuCode, basicSkuCodeList).eq(SkuDO::getOwnBrandFlag, BooleanEnum.YES.getValue()).eq(SkuDO::getChangeFlag, ChangeFlagEnum.NO.getValue()).eq(SkuDO::getAuditStatus, BpmProcessInstanceResultEnum.APPROVE.getResult()));
        if (CollUtil.isEmpty(ownSkuList)) {
            return skuCodeList.stream().distinct().collect(Collectors.toMap(s -> s, List::of));
        }
        // 分组
        Map<String, List<String>> ownSkuMap = ownSkuList.stream().collect(Collectors.groupingBy(SkuDO::getBasicSkuCode, Collectors.mapping(SkuDO::getCode, Collectors.toList())));
        Map<String, List<String>> result = new HashMap<>();
        skuDOList.forEach(s -> {
            List<String> ownSkuCodeList = ownSkuMap.get(s.getBasicSkuCode());
            if (CollUtil.isEmpty(ownSkuCodeList)) {
                result.put(s.getCode(), List.of(s.getCode()));
            } else {
                ownSkuCodeList.add(s.getCode());
                result.put(s.getCode(), ownSkuCodeList);
            }
        });
        return result;
    }

    @Override
    public Boolean validateSkuApprove(List<String> codeList) {
        if (CollUtil.isEmpty(codeList)) {
            return false;
        }
        List<SkuDO> skuDOS = skuMapper.selectList(new LambdaQueryWrapperX<SkuDO>().select(SkuDO::getId, SkuDO::getCode).in(SkuDO::getCode, codeList).ne(SkuDO::getSkuType, SkuTypeEnum.AUXILIARY_MATERIALS.getValue()));
        if (CollUtil.isEmpty(skuDOS)) {
            return true;
        }

        //包材产品不校验
        List<Long> idList = skuDOS.stream().map(SkuDO::getId).collect(Collectors.toList());
        codeList = skuDOS.stream().map(SkuDO::getCode).collect(Collectors.toList());
        if (CollUtil.isEmpty(idList)) {
            return true;
        }
        List<SkuBomDO> skuBomList = skuBomService.getSkuBomListByParentIdList(idList);
        if (CollUtil.isEmpty(skuBomList)) {
            Set<String> availableSkuCodes = getAvailableSkuIds(codeList);
            if (CollUtil.isEmpty(availableSkuCodes) || CollUtil.isNotEmpty(CollUtil.subtractToList(codeList, availableSkuCodes))) {
                throw exception(SKU_NOT_APPROVED);
            }
            Set<String> availableSkuCodeSet = quoteitemApi.getAvailableSkuIdSetBySkuCodeList(codeList);
            if (CollUtil.isEmpty(availableSkuCodeSet) || CollUtil.isNotEmpty(CollUtil.subtractToList(codeList, availableSkuCodeSet))) {
                throw exception(SKU_QUOTE_VENDER_NOT_APPROVED);
            }
        }
        // 需要校验的子产品id  批量校验
        Set<String> checkCodeList = skuBomList.stream().map(SkuBomDO::getSkuCode).filter(StrUtil::isNotEmpty).collect(Collectors.toSet());
        // 将当前产品id也加进去
        checkCodeList.addAll(codeList);
        Set<String> availableQuoteSkuCodeSet = quoteitemApi.getAvailableSkuIdSetBySkuCodeList(checkCodeList);
        Set<String> availableSkuCodeSet = getAvailableSkuIds(checkCodeList);
        if (CollUtil.isEmpty(availableQuoteSkuCodeSet) || CollUtil.isNotEmpty(CollUtil.subtractToList(checkCodeList, availableSkuCodeSet))) {
            throw exception(SKU_QUOTE_VENDER_NOT_APPROVED);
        }
        if (CollUtil.isEmpty(availableSkuCodeSet) || CollUtil.isNotEmpty(CollUtil.subtractToList(checkCodeList, availableSkuCodeSet))) {
            throw exception(SKU_NOT_APPROVED);
        }
        skuBomList.forEach(s -> {
            if (!availableSkuCodeSet.contains(s.getSkuCode())) {
                throw exception(SKU_SUB_NOT_APPROVED);
            }
            if (!availableQuoteSkuCodeSet.contains(s.getSkuCode())) {
                throw exception(SKU_SUB_QUOTE_VENDER_NOT_APPROVED);
            }
        });
        return true;
    }

    @Override
    public Set<String> getAvailableSkuIds(Collection<String> codeList) {
        if (CollUtil.isEmpty(codeList)) {
            return Set.of();
        }
        List<SkuDO> skuDOS = skuMapper.selectList(new LambdaQueryWrapperX<SkuDO>().select(SkuDO::getId, SkuDO::getCode).in(SkuDO::getCode, codeList).eq(SkuDO::getAuditStatus, BpmProcessInstanceResultEnum.APPROVE.getResult()));
        if (CollUtil.isEmpty(skuDOS)) {
            return Set.of();
        }
        return skuDOS.stream().map(SkuDO::getCode).collect(Collectors.toSet());
    }

    @Override
    public String getHsMeasureUnitBySkuCode(String skuCode) {
        if (StrUtil.isEmpty(skuCode)) {
            return CommonDict.EMPTY_STR;
        }
        return skuMapper.getHsMeasureUnitBySkuCode(skuCode);
    }

    @Override
    public Set<SimpleSkuDTO> transformIdByCode(Collection<String> codeList) {
        if (CollUtil.isEmpty(codeList)) {
            return Set.of();
        }
        List<SkuDO> skuDOS = skuMapper.selectList(new LambdaQueryWrapperX<SkuDO>().select(SkuDO::getId,SkuDO::getCode,SkuDO::getBasicSkuCode).in(SkuDO::getCode, codeList).eq(SkuDO::getAuditStatus, BpmProcessInstanceResultEnum.APPROVE.getResult()).eq(SkuDO::getChangeFlag, ChangeFlagEnum.NO.getValue()));
        if (CollUtil.isEmpty(skuDOS)) {
            return Set.of();
        }
        List<SimpleSkuDTO> simpleSkuDTOS = SkuConvert.INSTANCE.convertSimpleSkuDTOList(skuDOS);
        return simpleSkuDTOS.stream().collect(Collectors.toSet());
    }

    @Override
    public SkuDTO getSkuByBasicCodeAndCskuCodeAndCustCode(String basicSkuCode, String cskuCode, String custCode) {
        List<SkuDO> skuDOS = skuMapper.selectList(new LambdaQueryWrapperX<SkuDO>()
                .eq(SkuDO::getBasicSkuCode, basicSkuCode)
                .eq(SkuDO::getCskuCode, cskuCode)
                .eq(SkuDO::getCustCode, custCode)
        ).stream().toList();
        if (CollUtil.isEmpty(skuDOS))
            return null;
        return BeanUtils.toBean(skuDOS.get(0), SkuDTO.class);
    }

    @Override
    public void updateThumbnail(Long limit) {
        StopWatch queryDataSW = new StopWatch("查询数据库");
        queryDataSW.start();
        LambdaQueryWrapper<SkuDO> queryWrapper = new LambdaQueryWrapperX<SkuDO>().select(SkuDO::getThumbnail, SkuDO::getId, SkuDO::getPicture);
        queryWrapper.isNull(SkuDO::getThumbnail)
                .or()
                .eq(SkuDO::getThumbnail, "");
//        queryWrapper.apply("limit {0}",limit);
        List<SkuDO> skuDOS = skuMapper.selectList(queryWrapper);
        queryDataSW.stop();
        if (CollUtil.isEmpty(skuDOS)) {
            return;
        }
        List<SkuDO> updateList = skuDOS.stream().filter(s -> StrUtil.isEmpty(s.getThumbnail()) && CollUtil.isNotEmpty(s.getPicture())).limit(limit).collect(Collectors.toList());
        logger.info("查询待处理数据{}条,共耗时{}s", updateList.size(), queryDataSW.getTotalTimeSeconds());
        AtomicReference<Long> num = new AtomicReference<>(0L);
        List<SkuDO> batchUpdateList = new ArrayList<>();
        int batchSize = 100; // 每批处理100条数据
        StopWatch queryDataSWTotal = new StopWatch("总耗时");
        queryDataSWTotal.start();
        updateList.forEach(s -> {
                    if (CollUtil.isEmpty(s.getPicture())) {
                        return;
                    }

                    StopWatch queryDataSWSingle = new StopWatch("单条耗时");
                    queryDataSWSingle.start();

                    List<SimpleFile> picture = s.getPicture();
                    String thumbnail = processImageCompression(picture);
                    logger.info("缩略图上传成功skuId-{},url-{}", s.getId(), thumbnail);
                    s.setThumbnail(thumbnail);
                    batchUpdateList.add(s); // 添加到批次列表中

                    num.getAndSet(num.get() + 1);
                    long processedCount = num.get();

                    queryDataSWSingle.stop();
                    logger.info("处理单条耗时{}s", queryDataSWSingle.getTotalTimeSeconds());
                    logger.info("已处理{}条", processedCount);

                    // 每处理100条数据执行一次批量更新
                    if (processedCount % batchSize == 0 && !batchUpdateList.isEmpty()) {
                        StopWatch batchUpdateSW = new StopWatch("批量入库耗时");
                        batchUpdateSW.start();
                        skuMapper.updateBatch(batchUpdateList);
                        batchUpdateSW.stop();
                        logger.info("批量入库{}条数据，耗时{}s", batchUpdateList.size(), batchUpdateSW.getTotalTimeSeconds());
                        batchUpdateList.clear(); // 清空批次列表
                    }
                });

            // 处理剩余不足100条的数据
        if (!batchUpdateList.isEmpty()) {
            StopWatch batchUpdateSW = new StopWatch("最后批量入库耗时");
            batchUpdateSW.start();
            skuMapper.updateBatch(batchUpdateList);
            batchUpdateSW.stop();
            logger.info("最后批量入库{}条数据，耗时{}s", batchUpdateList.size(), batchUpdateSW.getTotalTimeSeconds());
        }
        queryDataSWTotal.stop();
        logger.info("压缩上传成功，总耗时{}s", queryDataSWTotal.getTotalTimeSeconds());
    }


    /**
     * 校验反审核状态
     *
     * @param id 主键
     */
    private void validateAntiAuditStatus(Long id) {
        Long count = skuMapper.validateAntiAuditStatus(id);
        if (count > 0) {
            throw exception(ANTI_AUDIT_EXCEPT);
        }
    }

    @Override
    public List<ConfirmSourceEntity> getConfirmSourceList(String targetCode, Integer effectRangeType) {
        LambdaQueryWrapper<SkuDO> queryWrapper = new LambdaQueryWrapper<SkuDO>().select(SkuDO::getId, SkuDO::getSourceCode)
                .apply("JSON_CONTAINS(effect_range_list,cast({0} as json))", JsonUtils.toJsonString(new JsonEffectRange().setEffectRangeType(effectRangeType).setEffectRangeCode(targetCode).setConfirmFlag(0)));
        List<SkuDO> TList = skuMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(TList)) {
            return List.of();
        }
        return TList.stream().map(s -> new ConfirmSourceEntity().setId(s.getId()).setCode(s.getSourceCode()).setConfirmSourceType(EffectRangeEnum.SKU.getValue())).toList();
    }

    /**
     * 根据优势产品标识获取skuId列表
     * @param advantageFlag 优势产品标识
     * @return skuId列表
     * @author 波波
     */
    @Override
    public List<Long> getSkuIdsByAdvantageFlag(Integer advantageFlag) {
        if (advantageFlag == null) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<SkuDO> queryWrapper = new LambdaQueryWrapper<SkuDO>()
                .select(SkuDO::getId)
                .eq(SkuDO::getAdvantageFlag, advantageFlag);
        List<Object> idObjects = skuMapper.selectObjs(queryWrapper);
        if (CollUtil.isEmpty(idObjects)) {
            return Collections.emptyList();
        }
        return idObjects.stream()
                .map(obj -> ((Number) obj).longValue())
                .collect(Collectors.toList());
    }

}
