package com.syj.eplus.module.scm.service.venderpoc;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.framework.common.enums.BooleanEnum;
import com.syj.eplus.module.scm.api.vender.dto.VenderPocDTO;
import com.syj.eplus.module.scm.controller.admin.venderpoc.vo.VenderPocPageReqVO;
import com.syj.eplus.module.scm.controller.admin.venderpoc.vo.VenderPocSaveReqVO;
import com.syj.eplus.module.scm.dal.dataobject.venderpoc.VenderPocDO;
import com.syj.eplus.module.scm.dal.mysql.venderpoc.VenderPocMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.scm.enums.ErrorCodeConstants.VENDER_POC_NOT_EXISTS;


/**
 * 供应商联系人 Service 实现类
 *
 * @author zhangcm
 */
@Service
@Validated
public class VenderPocServiceImpl implements VenderPocService {

    @Resource
    private VenderPocMapper venderPocMapper;

    @Override
    public Long createVenderPoc(VenderPocSaveReqVO createReqVO) {
        // 插入
        VenderPocDO venderPoc = BeanUtils.toBean(createReqVO, VenderPocDO.class);
        venderPocMapper.insert(venderPoc);
        // 返回
        return venderPoc.getId();
    }

    @Override
    public void updateVenderPoc(VenderPocSaveReqVO updateReqVO) {
        // 校验存在
//        validateVenderPocExists(updateReqVO.getId());
        // 更新
        VenderPocDO updateObj = BeanUtils.toBean(updateReqVO, VenderPocDO.class);
        venderPocMapper.updateById(updateObj);
    }

    @Override
    public void deleteVenderPoc(Long id) {
        // 校验存在
        validateVenderPocExists(id);
        // 删除
        venderPocMapper.deleteById(id);
    }

    private void validateVenderPocExists(Long id) {
        if (venderPocMapper.selectById(id) == null) {
            throw exception(VENDER_POC_NOT_EXISTS);
        }
    }

    @Override
    public VenderPocDO getVenderPoc(Long id) {
        return venderPocMapper.selectById(id);
    }

    @Override
    public PageResult<VenderPocDO> getVenderPocPage(VenderPocPageReqVO pageReqVO) {
        return venderPocMapper.selectPage(pageReqVO);
    }

    @Override
    public void batchSavePoc(List<VenderPocDO> venderPocDOList) {
        if (CollUtil.isEmpty(venderPocDOList)) {
            return;
        }
        venderPocMapper.insertBatch(venderPocDOList);
    }

    @Override
    public List<VenderPocDO> getPocListByVenderId(Long venderId) {
        return venderPocMapper.selectList(new LambdaQueryWrapperX<VenderPocDO>().eq(VenderPocDO::getVenderId, venderId));
    }

    @Override
    public void batchDeletePocByIds(List<Long> idList) {
        venderPocMapper.deleteBatchIds(idList);
    }

    @Override
    public Map<Long, VenderPocDO> getDefaultVenderPocMap() {
        List<VenderPocDO> venderPocDOList = venderPocMapper.selectList(new LambdaQueryWrapperX<VenderPocDO>().eq(VenderPocDO::getDefaultFlag, BooleanEnum.YES.getValue()));
        if (CollUtil.isEmpty(venderPocDOList)) {
            return Collections.emptyMap();
        }
        return venderPocDOList.stream().collect(Collectors.toMap(VenderPocDO::getVenderId, venderPocDO -> venderPocDO));
    }

    @Override
    public Map<Long, List<VenderPocDTO>> getVenderPocMap(List<Long> idList) {
        List<VenderPocDO> doList = venderPocMapper.selectList(VenderPocDO::getId, idList);
        if(CollUtil.isEmpty(doList)){
            return null;
        }
        List<VenderPocDTO> dtoList = BeanUtils.toBean(doList, VenderPocDTO.class);
        return dtoList.stream().collect(Collectors.groupingBy(VenderPocDTO::getVenderId));
    }
}