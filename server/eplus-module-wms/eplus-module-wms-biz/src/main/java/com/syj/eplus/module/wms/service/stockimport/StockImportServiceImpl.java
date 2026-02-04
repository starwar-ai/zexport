package com.syj.eplus.module.wms.service.stockimport;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import com.syj.eplus.module.infra.api.company.CompanyApi;
import com.syj.eplus.module.infra.api.company.dto.SimpleCompanyDTO;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.JsonSpecificationEntity;
import com.syj.eplus.framework.common.entity.JsonWeight;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.module.crm.api.cust.CustApi;
import com.syj.eplus.module.crm.api.cust.dto.CustAllDTO;
import com.syj.eplus.module.pms.api.sku.SkuApi;
import com.syj.eplus.module.pms.api.sku.dto.SkuDTO;
import com.syj.eplus.module.scm.api.vender.VenderApi;
import com.syj.eplus.module.scm.api.vender.dto.VenderAllDTO;
import com.syj.eplus.module.wms.controller.admin.stockimport.vo.*;
import com.syj.eplus.module.wms.controller.admin.warehouse.vo.WarehouseSimpleRespVO;
import com.syj.eplus.module.wms.convert.stockimport.StockImportConvert;
import com.syj.eplus.module.wms.dal.dataobject.stockimport.StockImportDO;
import com.syj.eplus.module.wms.dal.mysql.stockimport.StockImportMapper;
import com.syj.eplus.module.wms.service.warehouse.WarehouseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.wms.enums.ErrorCodeConstants.STOCK_IMPORT_NOT_EXISTS;

/**
 * 仓储管理-库存明细导入 Service 实现类
 *
 * @author zhangcm
 */
@Service
@Validated
public class StockImportServiceImpl implements StockImportService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private AdminUserApi adminUserApi;

    @Resource
    private CompanyApi companyApi;

    @Resource
    private SkuApi skuApi;
    @Resource
    private CustApi custApi;
    @Resource
    private VenderApi venderApi;
    @Resource
    private WarehouseService warehouseService;
    @Resource
    private StockImportMapper stockImportMapper;


    @Override
    public Long createStockImport(StockImportSaveReqVO createReqVO) {
        StockImportDO stockImport = StockImportConvert.INSTANCE.convertStockImportDO(createReqVO);
        // 插入
        stockImportMapper.insert(stockImport);
        // 返回
        return stockImport.getId();
    }

    @Override
    public void updateStockImport(StockImportSaveReqVO updateReqVO) {
        // 校验存在
        validateStockImportExists(updateReqVO.getId());
        // 更新
        StockImportDO updateObj = StockImportConvert.INSTANCE.convertStockImportDO(updateReqVO);
        stockImportMapper.updateById(updateObj);
    }

    @Override
    public void deleteStockImport(Long id) {
        // 校验存在
        validateStockImportExists(id);
        // 删除
        stockImportMapper.deleteById(id);
    }

    private void validateStockImportExists(Long id) {
        if (stockImportMapper.selectById(id) == null) {
            throw exception(STOCK_IMPORT_NOT_EXISTS);
        }
    }
    @Override
    public StockImportRespVO getStockImport(Long id) {
        StockImportDO stockImportDO = stockImportMapper.selectById(id);
        if (stockImportDO == null) {
            return null;
        }
        return StockImportConvert.INSTANCE.convertStockImportRespVO(stockImportDO);
    }

    @Override
    public PageResult<StockImportDO> getStockImportPage(StockImportPageReqVO pageReqVO) {
        return stockImportMapper.selectPage(pageReqVO);
    }

    @Override
    public StockImportShowRespVO importExcelNotInsert(List<StockImportExcelVO> list) {
        StockImportShowRespVO respVO = new StockImportShowRespVO();
        List<StockImportRespVO> errorList = new ArrayList<>();
        List<StockImportRespVO> suceessList = new ArrayList<>();
        if(CollUtil.isEmpty(list)){
            return respVO;
        }
        String importCode = String.valueOf(System.currentTimeMillis());
        respVO.setImportCode(importCode);
        Map<String,UserDept> usermap = adminUserApi.getUserDeptToUserName();
        Map<String,SimpleCompanyDTO> companyMap = companyApi.getSimpleCompanyDTOToName();
        List<String> skuCodes = list.stream().map(StockImportExcelVO::getSkuCode).distinct().toList();
        Map<String, SkuDTO> skuMap = skuApi.getSkuDTOMapByCodeList(skuCodes);
        Map<String, SkuDTO> ownSkuMap = skuApi.getOwnSkuDTOMapByCodeList(skuCodes);
        List<String> custCodes = list.stream().map(StockImportExcelVO::getCustCode).distinct().toList();
        Map<String, CustAllDTO> custMap = custApi.getCustByCodeList(custCodes);
        List<WarehouseSimpleRespVO> wareList = warehouseService.getSimpleListWarehouse();
        List<String> venderCodes = list.stream().map(StockImportExcelVO::getVenderCode).distinct().toList();
        Map<String, VenderAllDTO> venderMap = venderApi.getVenderByCodeList(venderCodes);
        list.forEach(s->{
            StockImportRespVO msgByImport = getMsgByImport(s,usermap,companyMap,skuMap,custMap,wareList,venderMap,ownSkuMap);
            msgByImport.setImportCode(importCode);
            if(Objects.equals(s.getOwnBrandFlag(), "是") || Objects.equals(s.getOwnBrandFlag(), "1")){
                msgByImport.setOwnBrandFlag(1);
                msgByImport.setCustProFlag(0);
                msgByImport.setOwnBrandFlag(1);
                msgByImport.setCustId(null).setCustCode(null).setCustName(null);
            }
            if(msgByImport.getErrorFlag() == 1){
                errorList.add(msgByImport);
            }else {
                suceessList.add(msgByImport);
            }
        });
        stockImportMapper.insertBatch(BeanUtils.toBean(suceessList, StockImportDO.class));
        stockImportMapper.insertBatch(BeanUtils.toBean(errorList, StockImportDO.class));
        return respVO.setErrorList(errorList).setSuccessList(suceessList);
    }

    private StockImportRespVO getMsgByImport(StockImportExcelVO excelVO,Map<String,UserDept> usermap ,Map<String,SimpleCompanyDTO> companyMap,
                                             Map<String, SkuDTO> skuMap, Map<String, CustAllDTO> custMap , List<WarehouseSimpleRespVO> wareList ,
                                             Map<String, VenderAllDTO> venderMap, Map<String, SkuDTO> ownSkuMap  ){
        StockImportRespVO res = new StockImportRespVO();
        res.setErrorFlag(0);
        res.setDiffQuantity(0);
        res.setProducingQuantity(0);
        res.setUsedQuantity(0);
        res.setLockQuantity(0);
        res.setAvailableQuantity(0);
        res.setSplitBoxFlag(0);
        res.setCabinetQuantity(0);

        res.setPurchaseContractCode(excelVO.getPurchaseContractCode());

        UserDept purchaseUser = usermap ==null ? null : usermap.get(excelVO.getPurchaseUser());
        if(Objects.nonNull(purchaseUser)){
            res.setPurchaseUser(purchaseUser);
        }else {
            res.setPurchaseUser(new UserDept());
//            res.setErrorFlag(1);
//            res.setErrorRemark("采购员不存在;");
        }


        SimpleCompanyDTO company = companyMap == null ? null : companyMap.get(excelVO.getCompanyName());
        if(Objects.nonNull(company)){
            res.setCompanyId(company.getId());
            res.setCompanyName(company.getName());
        }else {
            res.setErrorFlag(1);
            List<SimpleCompanyDTO> companyDTOS = companyMap.values().stream().toList();
            if(CollUtil.isNotEmpty(companyDTOS)){
                res.setErrorRemark("主体信息不存在,请填写:" + String.join("/",companyDTOS.stream().map(SimpleCompanyDTO::getName).toList())   + ";");
            }else {
                res.setErrorRemark("主体信息不存在,请检查内部公司信息;");
            }
        }
        SkuDTO skuDTO = null;

        //自营产品处理
        if(Objects.equals(excelVO.getOwnBrandFlag(), "是") || Objects.equals(excelVO.getOwnBrandFlag(), "1")){
            skuDTO = skuApi.getOwnSkuByBasicCodeAndCskuCodeAndCustCode(excelVO.getBasicSkuCode(),excelVO.getCskuCode());
            if(Objects.isNull(skuDTO)){
                res.setErrorFlag(1);
                res.setErrorRemark("自营产品不存在:SKU编号或者（基础产品编号+客户货号）查询对应的自营产品不存在;");
            }
        }else {
            if(Objects.nonNull(excelVO.getSkuCode())){
                skuDTO = skuMap == null ? null :  skuMap.get(excelVO.getSkuCode());
            }
            if(Objects.isNull(skuDTO)){
                skuDTO = skuApi.getSkuByBasicCodeAndCskuCodeAndCustCode(excelVO.getBasicSkuCode(),excelVO.getCskuCode(),excelVO.getCustCode());
            }
            if(Objects.isNull(skuDTO)){
                res.setErrorFlag(1);
                res.setErrorRemark("产品不存在:SKU编号查询不存在,基础产品编号+客户货号+客户编号查询不存在;");
            }
        }
        if(Objects.nonNull(skuDTO)){
            res.setSkuId(skuDTO.getId()).setSkuCode(skuDTO.getCode()).setSkuName(skuDTO.getName()).setCskuCode(skuDTO.getCskuCode());
            res.setOwnBrandFlag(skuDTO.getOwnBrandFlag()).setCustProFlag(skuDTO.getCustProFlag()).setBasicSkuCode(skuDTO.getBasicSkuCode());
        }

        if(excelVO.getInitQuantity() > 0){
            res.setInitQuantity(excelVO.getInitQuantity());
        }else {
            res.setErrorFlag(1);
            res.setErrorRemark("入库数量错误;");
        }
        BigDecimal price = excelVO.getPrice();
        if ( price != null && price.compareTo(BigDecimal.ZERO) > 0) {
            String currency = Objects.isNull(excelVO.getCurrency() ) ? "RMB" :excelVO.getCurrency() ;
            res.setPrice(new JsonAmount().setAmount(price).setCurrency(currency));
            BigDecimal qty = new BigDecimal(excelVO.getInitQuantity());
            BigDecimal total = price.multiply(qty);
            res.setTotalAmount(new JsonAmount().setAmount(total).setCurrency(currency));
            res.setRemainTotalAmount(res.getTotalAmount());
        }else {
            JsonAmount amount = new JsonAmount().setCurrency("").setAmount(BigDecimal.ZERO);
            res.setPrice(amount).setTotalAmount(amount).setRemainTotalAmount(amount);
        }

        CustAllDTO custAllDTO = custMap == null ? null :  custMap.get(excelVO.getCustCode());
        if(Objects.nonNull(custAllDTO)){
            res.setCustCode(custAllDTO.getCode()).setCustId(custAllDTO.getId()).setCustName(custAllDTO.getName());
        }
//        else {
//            res.setErrorFlag(1);
//            res.setErrorRemark("客户编号不存在;");
//        }

        Optional<WarehouseSimpleRespVO> first = wareList.stream().filter(s -> Objects.equals(s.getName(), excelVO.getWarehouseName())).findFirst();
        if(first.isPresent()){
            WarehouseSimpleRespVO warehouseSimpleRespVO = first.get();
            res.setWarehouseId(warehouseSimpleRespVO.getId()).setWarehouseName(warehouseSimpleRespVO.getName());
        }else {
            res.setErrorFlag(1);
            res.setErrorRemark("仓库名称不存在,请输入:" + String.join("/",wareList.stream().map(WarehouseSimpleRespVO::getName).toList()) + ";");
        }

        VenderAllDTO venderAllDTO = venderMap == null ? null :  venderMap.get(excelVO.getVenderCode());
        if(Objects.nonNull(venderAllDTO)){
            res.setVenderId(venderAllDTO.getId()).setVenderCode(venderAllDTO.getCode()).setVenderName(venderAllDTO.getName());
        } else {
//            res.setErrorFlag(1);
//            res.setErrorRemark("供应商编号不存在;");
        }

        if(excelVO.getOuterboxLength() == null){
            excelVO.setOuterboxLength(BigDecimal.ZERO);
        }
        if(excelVO.getOuterboxHeight() == null){
            excelVO.setOuterboxHeight(BigDecimal.ZERO);
        }
        if(excelVO.getOuterboxWidth() == null){
            excelVO.setOuterboxWidth(BigDecimal.ZERO);
        }
        if(res.getInitQuantity() == null){
            res.setInitQuantity(0);
        }
        BigDecimal volume = excelVO.getOuterboxLength().multiply(excelVO.getOuterboxWidth()).multiply(excelVO.getOuterboxHeight());
        res.setOuterboxVolume(volume);
        res.setTotalVolume(volume.multiply(BigDecimal.valueOf(res.getInitQuantity())));

        try {
            var weight = new BigDecimal(excelVO.getOuterboxGrossweight());
            res.setOuterboxGrossweight(new JsonWeight().setWeight(weight).setUnit("g"));
            res.setTotalWeight(new JsonWeight().setWeight(weight.multiply(BigDecimal.valueOf(res.getInitQuantity()))).setUnit("g"));
        }catch (Exception ignore){
//            res.setErrorFlag(1);
//            res.setErrorRemark("单箱毛重数据异常;");
        }

        res.setRemark(excelVO.getRemark());
        res.setPosition(excelVO.getPosition());
        res.setCustPo(excelVO.getCustPo());
        res.setOuterboxLength(excelVO.getOuterboxLength());
        res.setOuterboxWidth(excelVO.getOuterboxWidth());
        res.setOuterboxHeight(excelVO.getOuterboxHeight());
        JsonWeight weight = new JsonWeight().setWeight(BigDecimal.ZERO).setUnit("kg");

        JsonSpecificationEntity specificationEntity = new JsonSpecificationEntity();
        specificationEntity.setOuterboxLength(res.getOuterboxLength()).setOuterboxHeight(res.getOuterboxHeight()).setOuterboxWidth(res.getOuterboxWidth());
        specificationEntity.setOuterboxVolume(volume).setOuterboxGrossweight(weight).setOuterboxNetweight(weight);
        res.setSpecificationList(List.of(specificationEntity));

        res.setBatchCode(String.valueOf(System.currentTimeMillis()));
        //res.setBillId();
        //res.setBatchCode();
        //res.setBillItemId();
        //res.setReceiptTime( );
        //res.setQtyPerInnerbox();
//        res.setPurchaseContractId();
//        res.setPurchaseContractCode();
//        res.setSaleContractCode();
//        res.setSaleContractId();
//        res.setQtyPerOuterbox(excelVO.ge);

        return res;
    }

    @Override
    public List<StockImportExcelVO> getTestImport() {
        List<StockImportExcelVO> testDataList = new ArrayList<>();
        // 第一条测试数据
        StockImportExcelVO data1 = new StockImportExcelVO();
        data1.setCompanyName("泛太机电");
        data1.setBasicSkuCode("BAS-2023-001");
        data1.setCustCode("CUST-001");
        data1.setCskuCode("CSKU-1001");
        data1.setSkuCode("");
        data1.setVenderCode("VEND-001");
        data1.setVenderName("诚信供应商");
        data1.setCustPo("PO-2023-0001");
        data1.setPurchaseUser("张三");
        data1.setInitQuantity(100);
        data1.setPrice(BigDecimal.valueOf(6159.00));
        data1.setCurrency("RMB");
        data1.setWarehouseName("成品仓");
        data1.setPosition("A区-1排-01架");
        data1.setOuterboxLength(BigDecimal.valueOf(50.00));
        data1.setOuterboxWidth(BigDecimal.valueOf(30.00));
        data1.setOuterboxHeight(BigDecimal.valueOf(20.00));
        data1.setOuterboxGrossweight("5000");
        data1.setRemark("常规入库");
        data1.setOwnBrandFlag("");
        testDataList.add(data1);
        // 第二条测试数据
        StockImportExcelVO data2 = new StockImportExcelVO();
        data2.setCompanyName("泛太机电");
        data2.setBasicSkuCode("BAS-2023-002");
        data2.setCustCode("CUST-002");
        data2.setCskuCode("CSKU-1002");
        data2.setSkuCode("");
        data2.setVenderCode("VEND-002");
        data2.setVenderName("优质供应商");
        data2.setCustPo("PO-2023-0002");
        data2.setPurchaseUser("李四");
        data2.setInitQuantity(50);
        data2.setPrice(BigDecimal.valueOf(3200.50));
        data2.setCurrency("RMB");
        data2.setWarehouseName("成品仓");
        data2.setPosition("B区-2排-05架");
        data2.setOuterboxLength(new BigDecimal("40.00"));
        data2.setOuterboxWidth(new BigDecimal("25.00"));
        data2.setOuterboxHeight(new BigDecimal("15.00"));
        data2.setOuterboxGrossweight("3000");
        data2.setRemark("加急入库");
        data2.setOwnBrandFlag("1");
        testDataList.add(data2);
        // 第三条测试数据
        StockImportExcelVO data3 = new StockImportExcelVO();
        data3.setCompanyName("泛太控股");
        data3.setBasicSkuCode("");
        data3.setCustCode("");
        data3.setCskuCode("");
        data3.setSkuCode("SKU-2023-0003");
        data3.setVenderCode("VEND-003");
        data3.setVenderName("环球贸易");
        data3.setCustPo("PO-2023-0003");
        data3.setPurchaseUser("王五");
        data3.setInitQuantity(200);
        data3.setPrice(BigDecimal.valueOf(1500.00));
        data3.setCurrency("USD");
        data3.setWarehouseName("成品仓");
        data3.setPosition("C区-3排-08架");
        data3.setOuterboxLength(new BigDecimal("60.00"));
        data3.setOuterboxWidth(new BigDecimal("40.00"));
        data3.setOuterboxHeight(new BigDecimal("30.00"));
        data3.setOuterboxGrossweight("10000");
        data3.setRemark("跨境采购入库");
        data3.setOwnBrandFlag("1");
        testDataList.add(data3);

        return testDataList;
    }

    @Override
    public List<StockImportDO> getListByImportCode(String importCode) {
        return stockImportMapper.selectList(StockImportDO::getImportCode,importCode);
    }

    @Override
    public void BatchUpdate(List<StockImportDO> stockImportDOS) {
        if(CollUtil.isEmpty(stockImportDOS)){
            return;
        }
        stockImportMapper.updateBatch(stockImportDOS);
    }
}