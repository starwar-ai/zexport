package com.syj.eplus.module.mms.service.manufacture;


import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.operatelog.core.util.OperateCompareUtil;
import cn.iocoder.yudao.framework.operatelog.core.util.OperateLogUtils;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import com.syj.eplus.framework.common.entity.ChangeRecord;
import com.syj.eplus.framework.common.enums.ChangeRecordTypeEnum;
import com.syj.eplus.framework.common.enums.MmsManufactureStatusEnum;
import com.syj.eplus.module.infra.api.CodeGeneratorApi;
import com.syj.eplus.module.mms.controller.admin.manufacture.vo.ManufacturePageReqVO;
import com.syj.eplus.module.mms.controller.admin.manufacture.vo.ManufactureRespInfoVO;
import com.syj.eplus.module.mms.controller.admin.manufacture.vo.ManufactureSaveInfoReqVO;
import com.syj.eplus.module.mms.controller.admin.manufacturesku.vo.ManufactureSkuSaveInfoReqVO;
import com.syj.eplus.module.mms.controller.admin.manufactureskuitem.vo.ManufactureSkuItemSaveReqVO;
import com.syj.eplus.module.mms.convert.manufacture.ManufactureConvert;
import com.syj.eplus.module.mms.dal.dataobject.manufacture.ManufactureDO;
import com.syj.eplus.module.mms.dal.dataobject.manufacturesku.ManufactureSkuDO;
import com.syj.eplus.module.mms.dal.dataobject.manufactureskuitem.ManufactureSkuItemDO;
import com.syj.eplus.module.mms.dal.mysql.manufacture.ManufactureMapper;
import com.syj.eplus.module.mms.service.manufacturesku.ManufactureSkuService;
import com.syj.eplus.module.mms.service.manufactureskuitem.ManufactureSkuItemService;
import com.syj.eplus.module.wms.api.stock.IStockApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.mms.api.enums.ErrorCodeConstants.MANUFACTURE_NOT_EXISTS;
import static com.syj.eplus.module.mms.api.enums.ErrorCodeConstants.MANUFACTURE_SKU_ITEM_NOT_EXISTS;

/**
 * 加工单 Service 实现类
 *
 * @author zhangcm
 */
@Service
@Validated
public class ManufactureServiceImpl implements ManufactureService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private ManufactureMapper manufactureMapper;

    @Resource
    private ManufactureSkuService manufactureSkuService;
    @Resource
    private ManufactureSkuItemService manufactureSkuItemService;
    @Resource
    private CodeGeneratorApi codeGeneratorApi;
    @Resource
    private IStockApi stockApi;

    @Resource
    private AdminUserApi adminUserApi;

    private static final String CODE_PREFIX = "MF";
    public static final String SN_TYPE = "SN_MANFACTURE";
    public static final String OPERATOR_EXTS_KEY = "mmsManufactureCode";

    @Override
    public Long createManufacture(ManufactureSaveInfoReqVO createReqVO) throws Exception {
        ManufactureDO manufacture = ManufactureConvert.INSTANCE.convertManufactureDO(createReqVO);
        String code = codeGeneratorApi.getCodeGenerator(SN_TYPE, CODE_PREFIX);
        manufacture.setCode(code);
        manufacture.setId(null);
        if (Objects.isNull(createReqVO.getManufactureStatus())) {
            manufacture.setManufactureStatus(MmsManufactureStatusEnum.PENDING_DONE.getCode());
        }
        manufactureMapper.insert(manufacture);

        Long userId = Long.parseLong(manufacture.getCreator());

        manufacture.setInputTime(LocalDateTime.now()).setInputUserId(userId);
        AdminUserRespDTO user = adminUserApi.getUser(userId);
        if(Objects.nonNull(user)){
            manufacture.setInputUserName(user.getNickname());
        }
        manufactureMapper.updateById(manufacture);

        List<ManufactureSkuSaveInfoReqVO> children = createReqVO.getChildren();
        if(CollUtil.isEmpty(children)){
            throw new Exception("子产品列表不能为空");
        }
        Long id = manufacture.getId();
        manufactureSkuService.createBatch(children,id);

        // 补充操作日志明细
        OperateLogUtils.setContent(String.format("【新增加工单】%s", code));
        OperateLogUtils.addExt(OPERATOR_EXTS_KEY, code);

        return id;
    }

    @Override
    public void updateManufacture(ManufactureSaveInfoReqVO updateReqVO) {
        // 校验存在
        ManufactureDO manufactureDO = validateManufactureExists(updateReqVO.getId());
        // 更新
        ManufactureDO updateObj = ManufactureConvert.INSTANCE.convertManufactureDO(updateReqVO);
        manufactureMapper.updateById(updateObj);
        manufactureSkuService.updateBatch(updateReqVO.getChildren(),updateObj.getId());

        //操作日志
        {
            List<ChangeRecord> changeRecords = new OperateCompareUtil<ManufactureDO>().compare(manufactureDO, updateObj);

            if (CollUtil.isNotEmpty(changeRecords)) {
                OperateCompareUtil<Object> operateCompareUtil = new OperateCompareUtil<>();
                AtomicReference<String> content = new AtomicReference<>();
                changeRecords.forEach(s -> {
                    content.set(operateCompareUtil.apply(content.get(), s.getFieldName(), s.getOldValue(), s.getValue(), ChangeRecordTypeEnum.GENERAL_CHANGE.getType()));
                });
                // 插入操作日志
                OperateLogUtils.setContent(content.get());
            }
            OperateLogUtils.addExt(OPERATOR_EXTS_KEY, updateReqVO.getCode());
        }
    }

    @Override
    public void deleteManufacture(Long id) {
        // 校验存在
        validateManufactureExists(id);
        // 删除
        manufactureMapper.deleteById(id);
        manufactureSkuService.deleteByManufactureId(id);
        manufactureSkuItemService.deleteByManufactureId(id);
    }

    private ManufactureDO validateManufactureExists(Long id) {
        ManufactureDO manufactureDO = manufactureMapper.selectById(id);
        if ( manufactureDO == null) {
            throw exception(MANUFACTURE_NOT_EXISTS);
        }
        return manufactureDO;
    }
    @Override
    public ManufactureRespInfoVO getManufacture(Long id) {
        ManufactureDO manufactureDO = manufactureMapper.selectById(id);
        if (manufactureDO == null) {
            return null;
        }
        ManufactureRespInfoVO manufactureRespInfoVO = BeanUtils.toBean(manufactureDO, ManufactureRespInfoVO.class);
        List<ManufactureSkuDO> skuList = manufactureSkuService.selectListByManufactureId(id);
        List<ManufactureSkuItemDO> skuItemList = manufactureSkuItemService.selectListByManufactureId(id);
        if(CollUtil.isNotEmpty(skuList) && CollUtil.isNotEmpty(skuItemList)){
            List<ManufactureSkuSaveInfoReqVO> skuVoList = BeanUtils.toBean(skuList, ManufactureSkuSaveInfoReqVO.class);
            Map<Long, List<ManufactureSkuItemDO>> skuItemMap = skuItemList.stream().collect(Collectors.groupingBy(ManufactureSkuItemDO::getManufactureSkuId));
            if(CollUtil.isNotEmpty(skuItemMap)){
                skuVoList.forEach(s->{
                    Optional<ManufactureSkuItemDO> first = skuItemMap.get(s.getId()).stream().findFirst();
                    if(first.isPresent()){
                        List<ManufactureSkuItemDO> doList = skuItemMap.get(s.getId());
                      if(CollUtil.isNotEmpty(doList)){
                          List<ManufactureSkuItemSaveReqVO> voList = BeanUtils.toBean(doList, ManufactureSkuItemSaveReqVO.class);
                          s.setSkuItemList(voList);
                      }
                    }
                });
            }
            manufactureRespInfoVO.setChildren(skuVoList);
        }
        return manufactureRespInfoVO;
    }

    @Override
    public PageResult<ManufactureRespInfoVO> getManufacturePage(ManufacturePageReqVO pageReqVO) {
        PageResult<ManufactureDO> doPageResult = manufactureMapper.selectPage(pageReqVO);
        List<ManufactureDO> list = doPageResult.getList();
        if(list == null){
            return new PageResult<ManufactureRespInfoVO>().setList(null).setTotal((long)0);
        }
        List<Long> idList = list.stream().map(ManufactureDO::getId).distinct().toList();
        List<ManufactureRespInfoVO> voList = BeanUtils.toBean(list, ManufactureRespInfoVO.class);
        List<ManufactureSkuDO> skuList = manufactureSkuService.selectListByManufactureIds(idList);
        List<ManufactureSkuItemDO> skuItemList = manufactureSkuItemService.selectListByManufactureIds(idList);
        if(CollUtil.isNotEmpty(skuList) &&CollUtil.isNotEmpty(skuItemList) ){
            List<ManufactureSkuSaveInfoReqVO> skuVoList = BeanUtils.toBean(skuList, ManufactureSkuSaveInfoReqVO.class);
            List<ManufactureSkuItemSaveReqVO> skuItemVoList = BeanUtils.toBean(skuItemList, ManufactureSkuItemSaveReqVO.class);


            Map<Long, List<ManufactureSkuSaveInfoReqVO>> skuMap = skuVoList.stream().collect(Collectors.groupingBy(ManufactureSkuSaveInfoReqVO::getManufactureId));
            Map<Long, Map<Long, List<ManufactureSkuItemSaveReqVO>>> skuItemMap = skuItemVoList.stream().collect(
                    Collectors.groupingBy(ManufactureSkuItemSaveReqVO::getManufactureId,
                            Collectors.groupingBy(ManufactureSkuItemSaveReqVO::getManufactureSkuId)));
            voList.forEach(s->{
                if(CollUtil.isNotEmpty(skuMap) && CollUtil.isNotEmpty(skuItemMap)){
                    Long id = s.getId();
                    List<ManufactureSkuSaveInfoReqVO> skuVos = skuMap.get(id);
                    Map<Long, List<ManufactureSkuItemSaveReqVO>> skuItemBySkuMap = skuItemMap.get(id);
                    if(CollUtil.isNotEmpty(skuVos) && CollUtil.isNotEmpty(skuItemBySkuMap)){
                        skuVos.forEach(sku->{
                            List<ManufactureSkuItemSaveReqVO> saveReqVOList = skuItemBySkuMap.get(sku.getId());
                            sku.setSkuItemList(saveReqVOList);
                        });
                    }
                    s.setChildren(skuVos);
                }
            });
        }

        return new PageResult<ManufactureRespInfoVO>().setList(voList).setTotal(doPageResult.getTotal()) ;
    }

    @Override
    public void doneManufacture(Long id) {
        ManufactureDO manufactureDO = manufactureMapper.selectById(id);
        if(manufactureDO == null){
            throw exception(MANUFACTURE_NOT_EXISTS);
        }
        List<ManufactureSkuItemDO> skuDoItemList = manufactureSkuItemService.selectListByManufactureId(id);
        if(CollUtil.isEmpty(skuDoItemList)){
            throw exception(MANUFACTURE_SKU_ITEM_NOT_EXISTS);
        }
        List<Long> skuIdList = skuDoItemList.stream().map(ManufactureSkuItemDO::getSkuId).distinct().toList();
        //查询库存
//        List<StockDTO>  stockDtoList = stockApi.getStockBySkuIdList(skuIdList);
//        if(CollUtil.isEmpty(stockDtoList)){
//            throw exception(MANUFACTURE_SKU_STOCK_NOT_EXISTS);
//        }
//        Map<Long, List<StockDTO>> skuMap = stockDtoList.stream().collect(Collectors.groupingBy(StockDTO::getSkuId));
//        skuIdList.forEach(s->{
//            List<StockDTO> stockDTOList = skuMap.get(s);
//            if(CollUtil.isEmpty(stockDTOList)){
//                throw exception(MANUFACTURE_SKU_STOCK_NOT_ENOUGH);
//            }
//            int quantity = stockDTOList.stream().mapToInt(StockDTO::getAvailableQuantity).sum();
//            if(quantity == 0){
//                return;
//            }
//            List<StockDTO> stockList = stockDTOList.stream().filter(sku -> (Objects.equals(sku.getVenderFlag(), BooleanEnum.YES.getValue())
//                                                                       && Objects.equals(sku.getVenderWmsType(), VenderWmsTypeEnum.IN_PRODUCTING_WAREHOUSE.getValue()))
//                                                                      || Objects.equals(sku.getVenderFlag(), BooleanEnum.NO.getValue())).toList();
//            if(CollUtil.isEmpty(stockList)){
//                throw exception(MANUFACTURE_SKU_STOCK_NOT_ENOUGH);
//            }
//            int stockQuantity = stockList.stream().mapToInt(StockDTO::getAvailableQuantity).sum();
//            if(stockQuantity < quantity){
//                throw exception(MANUFACTURE_SKU_STOCK_NOT_ENOUGH);
//            }
//            //做出入库单据
//
//        });
        manufactureDO.setManufactureStatus(MmsManufactureStatusEnum.DONE.getCode()).setDoneTime(LocalDateTime.now()) ;
        manufactureMapper.updateById(manufactureDO);
    }

    @Override
    public void finishManufacture(Long id,String desc) {
        ManufactureDO manufactureDO = manufactureMapper.selectById(id);
        if(manufactureDO == null){
            throw exception(MANUFACTURE_NOT_EXISTS);
        }
        manufactureDO.setManufactureStatus(MmsManufactureStatusEnum.FINISH.getCode()).setFinishTime(LocalDateTime.now()).setFinishDesc(desc);
        manufactureMapper.updateById(manufactureDO);
    }

    @Override
    public void unfinishManufacture(Long id) {
        ManufactureDO manufactureDO = manufactureMapper.selectById(id);
        if(manufactureDO == null){
            throw exception(MANUFACTURE_NOT_EXISTS);
        }
        manufactureDO.setManufactureStatus(MmsManufactureStatusEnum.PENDING_DONE.getCode()).setFinishTime(null).setFinishDesc(null);
        manufactureMapper.updateById(manufactureDO);

    }


    @Override
    public void doneBatchManufacture(List<Long> idList) {
        List<ManufactureDO> manufactureDOList = manufactureMapper.selectList(ManufactureDO::getId, idList);
        if(manufactureDOList == null || manufactureDOList.size() != idList.size()){
            throw exception(MANUFACTURE_NOT_EXISTS);
        }
        manufactureDOList.forEach(s->{
            s.setManufactureStatus(MmsManufactureStatusEnum.DONE.getCode()).setDoneTime(LocalDateTime.now()) ;
        });
        manufactureMapper.updateBatch(manufactureDOList);
    }
}
