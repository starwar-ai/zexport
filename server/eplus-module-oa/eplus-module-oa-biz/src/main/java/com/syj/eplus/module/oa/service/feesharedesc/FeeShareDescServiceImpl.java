package com.syj.eplus.module.oa.service.feesharedesc;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.oa.controller.admin.feesharedesc.vo.FeeShareDescItemSaveReqVO;
import com.syj.eplus.module.oa.controller.admin.feesharedesc.vo.FeeShareDescPageReqVO;
import com.syj.eplus.module.oa.controller.admin.feesharedesc.vo.FeeShareDescRespVO;
import com.syj.eplus.module.oa.controller.admin.feesharedesc.vo.FeeShareDescSaveReqVO;
import com.syj.eplus.module.oa.convert.feesharedesc.FeeShareDescConvert;
import com.syj.eplus.module.oa.dal.dataobject.feesharedesc.FeeShareDescDO;
import com.syj.eplus.module.oa.dal.mysql.feesharedesc.FeeShareDescMapper;
import com.syj.eplus.module.oa.service.feeshare.FeeShareService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.FEE_SHARE_DESC_IS_USED;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.FEE_SHARE_DESC_NOT_EXISTS;

/**
 * 费用归集具体名称 Service 实现类
 *
 * @author zhangcm
 */
@Service
@Validated
public class FeeShareDescServiceImpl implements FeeShareDescService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private FeeShareDescMapper feeShareDescMapper;

    @Resource
    private FeeShareService feeShareService;


    @Override
    public void deleteFeeShareDesc(Long id) {
        // 校验存在
        validateFeeShareDescExists(id);
        FeeShareDescDO feeShareDescDO = feeShareDescMapper.selectById(id);
        //校验是否使用
        Boolean isUsed = feeShareService.isUsedByDescId(feeShareDescDO);
        if(isUsed){
            throw exception(FEE_SHARE_DESC_IS_USED);
        }

        // 删除
        feeShareDescMapper.deleteById(id);
    }

    private void validateFeeShareDescExists(Long id) {
        if (feeShareDescMapper.selectById(id) == null) {
            throw exception(FEE_SHARE_DESC_NOT_EXISTS);
        }
    }
    @Override
    public FeeShareDescRespVO getFeeShareDesc(Long id) {
        FeeShareDescDO feeShareDescDO = feeShareDescMapper.selectById(id);
        if (feeShareDescDO == null) {
            return null;
        }
        return FeeShareDescConvert.INSTANCE.convertFeeShareDescRespVO(feeShareDescDO);
    }

    @Override
    public PageResult<FeeShareDescDO> getFeeShareDescPage(FeeShareDescPageReqVO pageReqVO) {
        return feeShareDescMapper.selectPage(pageReqVO);
    }


    @Override
    public void updateAllFeeShareDesc(FeeShareDescSaveReqVO createReqVO) {
        Integer relationType = createReqVO.getRelationType();
        Integer feeShareType = createReqVO.getFeeShareType();
        List<FeeShareDescItemSaveReqVO> children = createReqVO.getChildren().stream().filter(Objects::nonNull).filter(s->!s.getName().isEmpty()).toList();
        List<FeeShareDescDO> shareDescDOList = feeShareDescMapper.selectList(new LambdaQueryWrapperX<FeeShareDescDO>()
                .eqIfPresent(FeeShareDescDO::getFeeShareType, feeShareType)
                .eqIfPresent(FeeShareDescDO::getRelationType, relationType)
        );
        List<FeeShareDescDO> addArr = new ArrayList<>();
        List<FeeShareDescDO> updateArr = new ArrayList<>();
        List<Long> deleteArr = new ArrayList<>();
        if(CollUtil.isEmpty(shareDescDOList)  && CollUtil.isNotEmpty(children)){
            //insert
            List<FeeShareDescDO> doList = BeanUtils.toBean(children, FeeShareDescDO.class);
            addArr.addAll(doList);
        } else if (CollUtil.isNotEmpty(shareDescDOList)  && CollUtil.isEmpty(children)) {
            //delete
            List<Long> idList = shareDescDOList.stream().map(FeeShareDescDO::getId).distinct().toList();
            deleteArr.addAll(idList);
        } else if (CollUtil.isNotEmpty(shareDescDOList)  && CollUtil.isNotEmpty(children)) {
            //update
            List<Long> doIdList = shareDescDOList.stream().map(FeeShareDescDO::getId).distinct().toList();
            List<Long> voIdList = children.stream().map(FeeShareDescItemSaveReqVO::getId).distinct().toList();
            Map<Long, FeeShareDescItemSaveReqVO> updateMap = children.stream().filter(s->s.getId() != null).collect(Collectors.toMap(FeeShareDescItemSaveReqVO::getId, s -> s));
            List<FeeShareDescDO> deleteList = shareDescDOList.stream().filter(s -> !voIdList.contains(s.getId())).toList();
            if(CollUtil.isNotEmpty(deleteList)){
                deleteArr.addAll(deleteList.stream().map(FeeShareDescDO::getId).distinct().toList());
            }
            List<FeeShareDescItemSaveReqVO> addList = children.stream().filter(s -> !doIdList.contains(s.getId())).toList();
            if(CollUtil.isNotEmpty(addList)){
                List<FeeShareDescDO> doList = BeanUtils.toBean(addList, FeeShareDescDO.class);
                addArr.addAll(doList);
            }
            List<FeeShareDescDO> updateList = shareDescDOList.stream().filter(s -> voIdList.contains(s.getId())).toList();
            if(CollUtil.isNotEmpty(updateList)){
                updateList.forEach(s->{
                    FeeShareDescItemSaveReqVO feeShareDescItemSaveReqVO = updateMap.get(s.getId());
                    if (Objects.isNull(feeShareDescItemSaveReqVO)){
                        return;
                    }
                    s.setName(feeShareDescItemSaveReqVO.getName());
                });
                updateArr.addAll(updateList);
            }



        }
        if(CollUtil.isNotEmpty(deleteArr)){
            feeShareDescMapper.deleteBatchIds(deleteArr);
        }
       if(CollUtil.isNotEmpty(updateArr)){
           feeShareDescMapper.updateBatch(updateArr);
       }
        Integer numb = 1;
        Optional<FeeShareDescDO> max = shareDescDOList.stream().max(Comparator.comparing(FeeShareDescDO::getSortNum));
        if(max.isPresent()){
            numb = max.get().getSortNum() + 1;
        }
        for (FeeShareDescDO feeShareDescDO : addArr) {
            feeShareDescDO.setSortNum(numb);
            feeShareDescDO.setId(null);
            feeShareDescDO.setFeeShareType(feeShareType);
            feeShareDescDO.setRelationType(relationType);
            numb ++;
        }
       if(CollUtil.isNotEmpty(addArr)){
           feeShareDescMapper.insertBatch(addArr);
       }



    }

}