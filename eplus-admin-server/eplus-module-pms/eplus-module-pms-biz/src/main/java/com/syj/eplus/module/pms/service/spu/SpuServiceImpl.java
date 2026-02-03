package com.syj.eplus.module.pms.service.spu;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.bpm.enums.task.BpmProcessInstanceResultEnum;
import com.syj.eplus.module.infra.api.CodeGeneratorApi;
import com.syj.eplus.module.pms.controller.admin.spu.vo.SpuInfoSaveReqVO;
import com.syj.eplus.module.pms.controller.admin.spu.vo.SpuPageReqVO;
import com.syj.eplus.module.pms.dal.dataobject.spu.SpuDO;
import com.syj.eplus.module.pms.dal.mysql.spu.SpuMapper;
import com.syj.eplus.module.pms.service.category.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.pms.enums.ErrorCodeConstants.SPU_NOT_EXISTS;
import static com.syj.eplus.module.pms.enums.ErrorCodeConstants.UNDERREVIEW_EDITTING_NOT_ALLOWED;

/**
 * 商品 Service 实现类
 *
 * @author eplus
 */
@Service
@Validated
public class SpuServiceImpl implements SpuService {

    @Resource
    private SpuMapper spuMapper;


    //TODO spu的生成方式需确认

    @Resource
    private CategoryService categoryService;

    @Resource
    private CodeGeneratorApi codeGeneratorApi;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createSpu(SpuInfoSaveReqVO createReqVO) {
        // 插入
        SpuDO spu = BeanUtils.toBean(createReqVO, SpuDO.class);
        String code = getCode(spu.getCategoryId());
        spu.setCode(code);
        spuMapper.insert(spu);
        Long spuId = spu.getId();
        // 返回
        return spu.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long updateSpu(SpuInfoSaveReqVO updateReqVO) {
        Long spuId = updateReqVO.getId();
        //删除方法中已经校验审核状态 此处直接调用
        deleteSpu(spuId, false);
        //数据库自增 将id置空
        updateReqVO.setId(null);
        updateReqVO.setAuditStatus(BpmProcessInstanceResultEnum.PROCESS.getResult());
        return createSpu(updateReqVO);
    }

    @Override
    public void deleteSpu(Long id, Boolean deleted) {
        // 校验存在
        validateSpuExists(id);
        // 删除
        spuMapper.deleteById(id);
    }

    private void validateSpuExists(Long id) {
        SpuDO spuDO = spuMapper.selectById(id);
        if (Objects.isNull(spuDO)) {
            throw exception(SPU_NOT_EXISTS);
        }
        Integer auditStatus = spuDO.getAuditStatus();
        if (BpmProcessInstanceResultEnum.PROCESS.getResult().equals(auditStatus)) {
            throw exception(UNDERREVIEW_EDITTING_NOT_ALLOWED);
        }
    }

    @Override
    public SpuDO getSpu(Long id) {
        return spuMapper.selectById(id);
    }

    @Override
    public PageResult<SpuDO> getSpuPage(SpuPageReqVO pageReqVO) {
        return spuMapper.selectPage(pageReqVO);
    }

    public String getCode(Long id) {
        String profixCode = categoryService.getProfixCode(id);
        return codeGeneratorApi.getCodeGenerator(SN_TYPE, profixCode);
    }

}