package com.syj.eplus.module.dtms.service.designsummary;

import cn.hutool.core.util.ObjectUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.syj.eplus.module.dtms.controller.admin.designsummary.vo.DesignSummaryPageReqVO;
import com.syj.eplus.module.dtms.controller.admin.designsummary.vo.DesignSummaryRespVO;
import com.syj.eplus.module.dtms.controller.admin.designsummary.vo.DesignSummarySaveReqVO;
import com.syj.eplus.module.dtms.convert.designsummary.DesignSummaryConvert;
import com.syj.eplus.module.dtms.dal.dataobject.designitem.DesignItemDO;
import com.syj.eplus.module.dtms.dal.dataobject.designsummary.DesignSummaryDO;
import com.syj.eplus.module.dtms.dal.mysql.designitem.DesignItemMapper;
import com.syj.eplus.module.dtms.dal.mysql.designsummary.DesignSummaryMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.dtms.api.ErrorCodeConstants.DESIGN_SUMMARY_NOT_EXISTS;

/**
 * 设计-工作总结 Service 实现类
 *
 * @author ePlus
 */
@Service
@Validated
public class DesignSummaryServiceImpl extends ServiceImpl<DesignSummaryMapper,DesignSummaryDO> implements DesignSummaryService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private DesignSummaryMapper designSummaryMapper;
    @Autowired
    private DesignItemMapper designItemMapper;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createDesignSummary(DesignSummarySaveReqVO createReqVO) {
        LambdaQueryWrapperX<DesignItemDO> queryWrapperItem = new LambdaQueryWrapperX<>();
        queryWrapperItem.eq(DesignItemDO::getDesignId,createReqVO.getDesignId());
        queryWrapperItem.eq(DesignItemDO::getDesignerId,createReqVO.getDesignerId());
        DesignItemDO designItemDO = designItemMapper.selectOne(queryWrapperItem);
        if (ObjectUtil.isNotNull(designItemDO)) {
            createReqVO.setDesignItemId(designItemDO.getId());
            DesignSummaryDO designSummary = DesignSummaryConvert.INSTANCE.convertDesignSummaryDO(createReqVO);
            // 插入
            super.saveOrUpdate(designSummary);
            // 返回
            return designSummary.getId();
        }
        return -1L;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDesignSummary(DesignSummarySaveReqVO updateReqVO) {
        // 校验存在
        validateDesignSummaryExists(updateReqVO.getId());
        // 更新
        DesignSummaryDO updateObj = DesignSummaryConvert.INSTANCE.convertDesignSummaryDO(updateReqVO);
        designSummaryMapper.updateById(updateObj);
    }

    @Override
    public void deleteDesignSummary(Long id) {
        // 校验存在
        validateDesignSummaryExists(id);
        // 删除
        designSummaryMapper.deleteById(id);
    }

    private void validateDesignSummaryExists(Long id) {
        if (designSummaryMapper.selectById(id) == null) {
            throw exception(DESIGN_SUMMARY_NOT_EXISTS);
        }
    }

    @Override
    public DesignSummaryRespVO getDesignSummary(Long designId,Long designerId) {
        LambdaQueryWrapperX<DesignSummaryDO> queryWrapper = new LambdaQueryWrapperX<>();
        queryWrapper.eq(DesignSummaryDO::getDesignId,designId);
        queryWrapper.eq(DesignSummaryDO::getDesignerId,designerId);
        DesignSummaryDO designSummaryDO = designSummaryMapper.selectOne(queryWrapper);
        if (ObjectUtil.isNull(designSummaryDO)) {
            LambdaQueryWrapperX<DesignItemDO> queryWrapperItem = new LambdaQueryWrapperX<>();
            queryWrapperItem.eq(DesignItemDO::getDesignId,designId);
            queryWrapperItem.eq(DesignItemDO::getDesignerId,designerId);
            DesignItemDO designItemDO = designItemMapper.selectOne(queryWrapperItem);
            if (ObjectUtil.isNotNull(designItemDO)) {
                designSummaryDO = new DesignSummaryDO();
                designSummaryDO.setDesignId(designId);
                designSummaryDO.setDesignerId(designerId);
                designSummaryDO.setDesignItemId(designItemDO.getId());
            }
        }
        return DesignSummaryConvert.INSTANCE.convertDesignSummaryRespVO(designSummaryDO);
    }

    @Override
    public PageResult<DesignSummaryDO> getDesignSummaryPage(DesignSummaryPageReqVO pageReqVO) {
        return designSummaryMapper.selectPage(pageReqVO);
    }


}
