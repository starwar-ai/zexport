package com.syj.eplus.module.wms.service.warehouse;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.framework.common.enums.BooleanEnum;
import com.syj.eplus.module.infra.api.CodeGeneratorApi;
import com.syj.eplus.module.wms.api.warehouse.dto.WarehouseDTO;
import com.syj.eplus.module.wms.controller.admin.warehouse.vo.WarehousePageReqVO;
import com.syj.eplus.module.wms.controller.admin.warehouse.vo.WarehouseRespVO;
import com.syj.eplus.module.wms.controller.admin.warehouse.vo.WarehouseSaveReqVO;
import com.syj.eplus.module.wms.controller.admin.warehouse.vo.WarehouseSimpleRespVO;
import com.syj.eplus.module.wms.convert.warehouse.WarehouseConvert;
import com.syj.eplus.module.wms.dal.dataobject.warehouse.WarehouseDO;
import com.syj.eplus.module.wms.dal.mysql.warehouse.WarehouseMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.wms.enums.ErrorCodeConstants.*;

/**
 * 仓库管理-仓库 Service 实现类
 *
 * @author Rangers
 */
@Service
@Validated
public class WarehouseServiceImpl extends ServiceImpl<WarehouseMapper,WarehouseDO> implements WarehouseService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private WarehouseMapper warehouseMapper;
    @Resource
    private CodeGeneratorApi codeGeneratorApi;
    @Resource
    private AdminUserApi adminUserApi;
    private static final String SN_TYPE = "SN_WAREHOUSE";
    private static final String CODE_PREFIX = "WH";


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createWarehouse(WarehouseSaveReqVO createReqVO) {
        WarehouseDO warehouse = WarehouseConvert.INSTANCE.convertWarehouseDO(createReqVO);
        // 生成 仓库管理-仓库 编号
        warehouse.setCode(codeGeneratorApi.getCodeGenerator(SN_TYPE, CODE_PREFIX));
        // 校验仓库名称唯一
        LambdaQueryWrapper<WarehouseDO> query = new LambdaQueryWrapper<>();
        query.eq(WarehouseDO::getName, createReqVO.getName());
        List<WarehouseDO> warehouseDOS = warehouseMapper.selectList(query);
        if (!CollectionUtils.isEmpty(warehouseDOS)) {
            throw exception(WAREHOUSE_NAME_ALREADY_EXISTS);
        }
        // 插入
        warehouseMapper.insert(warehouse);
        // 返回
        return warehouse.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer updateWarehouse(WarehouseSaveReqVO updateReqVO) {
        // 校验存在
        validateWarehouseExists(updateReqVO.getId());
        // 更新
        WarehouseDO updateObj = WarehouseConvert.INSTANCE.convertWarehouseDO(updateReqVO);
        return warehouseMapper.updateById(updateObj);
    }

    @Override
    public void enable(Long id) {
        // 校验存在
        validateWarehouseExists(id);
        WarehouseDO warehouseDO = warehouseMapper.selectById(id);
        warehouseDO.setEnableFlag(BooleanEnum.YES.getValue());
        warehouseMapper.updateById(warehouseDO);
    }

    @Override
    public void disable(Long id) {
        // 校验存在
        validateWarehouseExists(id);
        WarehouseDO warehouseDO = warehouseMapper.selectById(id);
        warehouseDO.setEnableFlag(BooleanEnum.NO.getValue());
        warehouseMapper.updateById(warehouseDO);
    }

    @Override
    public void deleteWarehouseByVenderCode(String venderCcode) {
        warehouseMapper.delete(WarehouseDO::getVenderCode, venderCcode);
    }

    @Override
    public void deleteWarehouse(Long id) {
        // 校验存在
        validateWarehouseExists(id);
        // 供应商仓库不允许删除
        WarehouseDO warehouseDO = warehouseMapper.selectById(id);
        if (warehouseDO.getVenderFlag().intValue() == BooleanEnum.YES.getValue().intValue()) {
            throw exception(WAREHOUSE_NOT_DELETE);
        }
        // 删除
        warehouseMapper.deleteById(id);
    }

    private void validateWarehouseExists(Long id) {
        if (warehouseMapper.selectById(id) == null) {
            throw exception(WAREHOUSE_NOT_EXISTS);
        }
    }

    @Override
    public WarehouseRespVO getWarehouse(Long id) {
        WarehouseDO warehouseDO = warehouseMapper.selectById(id);
        if (warehouseDO == null) {
            return null;
        }
        Map<Long, UserDept> userDeptMap = adminUserApi.getUserDeptListCache(warehouseDO.getManagerIds());
        WarehouseRespVO warehouseRespVO = WarehouseConvert.INSTANCE.convertWarehouseRespVO(warehouseDO, userDeptMap);
        long creatorId = Long.parseLong(warehouseRespVO.getCreator());
        String creatorName = adminUserApi.getUserDeptCache(creatorId).get(creatorId).getNickname();
        warehouseRespVO.setCreatorName(creatorName);
        return warehouseRespVO;
    }

    @Override
    public PageResult<WarehouseRespVO> getWarehousePage(WarehousePageReqVO pageReqVO) {
        PageResult<WarehouseDO> warehouseDOPageResult = warehouseMapper.selectPage(pageReqVO);
        List<WarehouseRespVO> warehouseRespVOList = new ArrayList<>();
        List<WarehouseDO> list = warehouseDOPageResult.getList();
        if (!CollectionUtils.isEmpty(list)) {
            Set<Long> managerIdList = list.stream()
                    .map(WarehouseDO::getManagerIds)
                    .filter(Objects::nonNull)
                    .flatMap(List::stream)
                    .collect(Collectors.toSet());
            Set<Long> creatorIdList = list.stream().map(x -> Long.valueOf(x.getCreator())).collect(Collectors.toSet());
            managerIdList.addAll(creatorIdList);
            Map<Long, AdminUserRespDTO> userMap = adminUserApi.getUserMap(managerIdList);
            list.stream().forEach(x -> {
                WarehouseRespVO warehouseRespVO = BeanUtils.toBean(x, WarehouseRespVO.class);
                // 处理多个 managerId，用逗号拼接用户名称
                if (warehouseRespVO.getManagerIds() != null && !warehouseRespVO.getManagerIds().isEmpty()) {
                    String managerNames = warehouseRespVO.getManagerIds().stream()
                            .map(userMap::get)
                            .filter(Objects::nonNull)
                            .map(AdminUserRespDTO::getNickname)
                            .collect(Collectors.joining(","));
                    warehouseRespVO.setManagerName(managerNames);
                } else {
                    warehouseRespVO.setManagerName("");
                }
                AdminUserRespDTO creatorUserRespDTO = userMap.get(Long.valueOf(warehouseRespVO.getCreator()));
                warehouseRespVO.setCreatorName(creatorUserRespDTO != null ? creatorUserRespDTO.getNickname() : "");
                warehouseRespVOList.add(warehouseRespVO);
            });
        }
        warehouseRespVOList.sort(Comparator.comparing(WarehouseRespVO::getEnableFlag).reversed());
        return new PageResult<WarehouseRespVO>().setList(warehouseRespVOList).setTotal(warehouseDOPageResult.getTotal());
    }

    @Override
    public List<WarehouseSimpleRespVO> getSimpleListWarehouse( ) {
        List<WarehouseDO> baseList = warehouseMapper.selectList(
                new LambdaQueryWrapper<WarehouseDO>()
                        .eq(WarehouseDO::getVenderFlag, 0)
                        .eq(WarehouseDO::getEnableFlag, 1)
        );
//        List<WarehouseDO> baseList = warehouseMapper.selectList(WarehouseDO::getVenderFlag,0);
        List<WarehouseSimpleRespVO> voList = BeanUtils.toBean(baseList, WarehouseSimpleRespVO.class);
        if(CollUtil.isEmpty(voList))
            return null;
        List<Long> idList = voList.stream().map(WarehouseSimpleRespVO::getManagerId).distinct().toList();
        List<AdminUserRespDTO> userList = adminUserApi.getUserList(idList);
        if(CollUtil.isEmpty(userList)){
            return  voList;
        }
        voList.forEach(s->{
            Optional<AdminUserRespDTO> first = userList.stream().filter(u -> Objects.equals(u.getId(), s.getManagerId())).findFirst();
            if(first.isPresent()){
                AdminUserRespDTO adminUserRespDTO = first.get();
                s.setManagerName(adminUserRespDTO.getNickname()).setManagerMobile(adminUserRespDTO.getMobile());
            }
        });

        return voList;

    }

    @Override
    public Map<Long,WarehouseDO> getWarehouseList(List<Long> wareHouseIdList) {
        List<WarehouseDO> warehouseDoList = warehouseMapper.selectList(WarehouseDO::getId, wareHouseIdList);
        if(CollUtil.isNotEmpty(warehouseDoList)){
            return null;
        }
        return warehouseDoList.stream().collect(Collectors.toMap(WarehouseDO::getId, s -> s));
    }

    @Override
    public Map<Long, String> getWarehoseCache() {
        List<WarehouseDO> warehouseDOS = warehouseMapper.selectList();
        if (CollUtil.isEmpty(warehouseDOS)){
            return Map.of();
        }
        return warehouseDOS.stream().collect(Collectors.toMap(WarehouseDO::getId,WarehouseDO::getName));
    }

    @Override
    public WarehouseDTO getDefaultWareMouse() {
        List<WarehouseDO> warehouseDOS = warehouseMapper.selectList(
                new LambdaQueryWrapper<WarehouseDO>().eq(WarehouseDO::getDefaultFlag,1)
                        .eq(WarehouseDO::getEnableFlag,1)
                        .eq(WarehouseDO::getVenderFlag,0)).stream().toList();
        if(CollUtil.isEmpty(warehouseDOS)){
            throw exception(WAREHOUSE_NOT_DEFAULT);
        }
        if(warehouseDOS.size() > 1){
            throw exception(WAREHOUSE_MORE_DEFAULT);
        }
        return  BeanUtils.toBean(warehouseDOS.get(0),WarehouseDTO.class);
    }

    @Override
    public void setDefault(Long id) {
        WarehouseDO warehouseDO = warehouseMapper.selectById(id);
        if(Objects.isNull(warehouseDO)){
            throw exception(WAREHOUSE_NOT_EXISTS);
        }

        List<WarehouseDO> warehouseDOS = warehouseMapper.selectList(
                new LambdaQueryWrapper<WarehouseDO>()
                        .eq(WarehouseDO::getVenderFlag, 0)
                        .eq(WarehouseDO::getDefaultFlag, 1));
        if(CollUtil.isNotEmpty(warehouseDOS)){
            warehouseDOS.forEach(s->s.setDefaultFlag(0));
            warehouseMapper.updateBatch(warehouseDOS);
        }
        warehouseDO.setDefaultFlag(1);
        warehouseMapper.updateById(warehouseDO);
    }

}
