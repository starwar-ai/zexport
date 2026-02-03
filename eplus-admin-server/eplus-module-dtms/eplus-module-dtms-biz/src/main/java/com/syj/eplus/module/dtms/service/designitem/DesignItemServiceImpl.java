package com.syj.eplus.module.dtms.service.designitem;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.operatelog.core.util.OperateLogUtils;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.syj.eplus.framework.common.enums.BooleanEnum;
import com.syj.eplus.module.dtms.controller.admin.designitem.vo.DesignItemPageReqVO;
import com.syj.eplus.module.dtms.controller.admin.designitem.vo.DesignItemRespVO;
import com.syj.eplus.module.dtms.controller.admin.designitem.vo.DesignItemSaveReqVO;
import com.syj.eplus.module.dtms.controller.admin.designsummary.vo.DesignSummarySaveReqVO;
import com.syj.eplus.module.dtms.convert.designitem.DesignItemConvert;
import com.syj.eplus.module.dtms.dal.dataobject.design.DesignDO;
import com.syj.eplus.module.dtms.dal.dataobject.designitem.DesignItemDO;
import com.syj.eplus.module.dtms.dal.mysql.designitem.DesignItemMapper;
import com.syj.eplus.module.dtms.enums.DesignStatusEnum;
import com.syj.eplus.module.dtms.service.design.DesignService;
import com.syj.eplus.module.dtms.service.design.DesignServiceImpl;
import com.syj.eplus.module.dtms.service.designsummary.DesignSummaryService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.dtms.api.ErrorCodeConstants.DESIGN_ITEM_ALREADY_EXISTS;
import static com.syj.eplus.module.dtms.api.ErrorCodeConstants.DESIGN_ITEM_NOT_EXISTS;

/**
 * 设计-认领明细 Service 实现类
 *
 * @author ePlus
 */
@Service
@Validated
public class DesignItemServiceImpl extends ServiceImpl<DesignItemMapper, DesignItemDO> implements DesignItemService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private DesignItemMapper designItemMapper;
    @Resource
    private DesignService designService;
    @Resource
    private DesignSummaryService designSummaryService;
    @Resource
    private AdminUserApi adminUserApi;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean createDesignItem(List<DesignItemSaveReqVO> createReqVOList) {
        List<Long> designItemIdList = new ArrayList<>();
        if (CollectionUtils.isEmpty(createReqVOList)) {
            return null;
        }
        // 获取当前登录用户信息
        AdminUserRespDTO currentUser = adminUserApi.getUser(WebFrameworkUtils.getLoginUserId());
        for (DesignItemSaveReqVO createReqVO : createReqVOList) {
            // 校验是否已认领
            LambdaQueryWrapper<DesignItemDO> queryWrapper = new LambdaQueryWrapper<>();
            Long designId = createReqVO.getDesignId();
            queryWrapper.eq(DesignItemDO::getDesignId, designId);
            queryWrapper.eq(DesignItemDO::getDesignerId, createReqVO.getDesignerId());
            DesignItemDO checkItemDo = super.getOne(queryWrapper);
            if (ObjectUtil.isNotNull(checkItemDo)) {
                continue;
            }
            DesignItemDO designItem = DesignItemConvert.INSTANCE.convertDesignItemDO(createReqVO);
            designItem.setAcceptDate(LocalDateTime.now());
            // 插入
            designItemMapper.insert(designItem);
            DesignDO designDO = designService.getDOById(designId);
            String designerIds = designDO.getDesignerIds();
            if (StringUtils.isNoneBlank(designerIds)) {
                designerIds = designDO.getDesignerIds() + "," + createReqVO.getDesignerId();
            } else {
                designerIds = String.valueOf(createReqVO.getDesignerId());
            }
            designDO.setDesignerIds(designerIds);
            designService.updateDOById(designDO);
            designItemIdList.add(designItem.getId());

            // 补充操作日志明细
            OperateLogUtils.setContent(String.format("【设计任务】%s 认领了任务", currentUser.getNickname()));
            OperateLogUtils.addExt(DesignServiceImpl.OPERATOR_EXTS_KEY, designDO.getCode());
        }
        // 返回
        return designItemIdList.size() == createReqVOList.size();
    }

    @Override
    public Boolean evaluate(List<DesignItemSaveReqVO> updateReqVOList) {
        if (CollectionUtils.isEmpty(updateReqVOList)) {
            return false;
        }
        List<DesignItemDO> designItemDOList = BeanUtils.toBean(updateReqVOList, DesignItemDO.class);
        boolean itemFlag = super.updateBatchById(designItemDOList);
        Long designId = updateReqVOList.get(0).getDesignId();
        DesignDO designDO = DesignDO.builder().id(designId).designStatus(DesignStatusEnum.COMPLETED.getValue()).build();
        boolean deisgnFlag = designService.updateById(designDO);

        // 获取当前登录用户信息
        AdminUserRespDTO currentUser = adminUserApi.getUser(WebFrameworkUtils.getLoginUserId());
        // 获取任务信息
        DesignDO designInfo = designService.getById(designId);
        // 补充操作日志明细
        OperateLogUtils.setContent(String.format("【设计任务】%s 评价了任务", currentUser.getNickname()));
        OperateLogUtils.addExt(DesignServiceImpl.OPERATOR_EXTS_KEY, designInfo.getCode());

        return deisgnFlag && itemFlag;
    }

    @Override
    public void updateDesigner(DesignItemSaveReqVO updateReqVO) {
        // 校验存在
        Long designItemId = updateReqVO.getId();
        validateDesignItemExists(designItemId);
        // 校验是否已认领
        LambdaQueryWrapper<DesignItemDO> queryWrapper = new LambdaQueryWrapper<>();
        Long designId = updateReqVO.getDesignId();
        queryWrapper.eq(DesignItemDO::getDesignerId, designId);
        Long designerId = updateReqVO.getDesignerId();
        queryWrapper.eq(DesignItemDO::getDesignerId, designerId);
        DesignItemDO checkItemDo = super.getOne(queryWrapper);
        if (ObjectUtil.isNotNull(checkItemDo) && designItemId != checkItemDo.getId()) {
            throw exception(DESIGN_ITEM_ALREADY_EXISTS);
        }
        // 更新
        DesignItemDO updateObj = DesignItemConvert.INSTANCE.convertDesignItemDO(updateReqVO);
        designItemMapper.updateById(updateObj);

        DesignDO designDO = designService.getById(designId);
        String designerIds = designDO.getDesignerIds();
        if (StringUtils.isNoneBlank(designerIds)) {
            List<String> list = Arrays.asList(designerIds.split(","));
            DesignItemDO designItemDO = super.getById(designItemId);
            Collections.replaceAll(list, String.valueOf(designItemDO.getDesignerId()), String.valueOf(updateObj.getDesignerId()));
        }
        designDO.setDesignerIds(designerIds);
        designService.updateById(designDO);
    }

    @Override
    public void deleteDesignItem(Long id) {
        // 校验存在
        validateDesignItemExists(id);
        // 删除
        designItemMapper.deleteById(id);
    }

    private void validateDesignItemExists(Long id) {
        if (designItemMapper.selectById(id) == null) {
            throw exception(DESIGN_ITEM_NOT_EXISTS);
        }
    }

    @Override
    @DataPermission(enable = false)
    public DesignItemRespVO getDesignItem(Long id) {
        DesignItemDO designItemDO = designItemMapper.selectById(id);
        if (designItemDO == null) {
            return null;
        }
        return DesignItemConvert.INSTANCE.convertDesignItemRespVO(designItemDO);
    }

    @Override
    public PageResult<DesignItemDO> getDesignItemPage(DesignItemPageReqVO pageReqVO) {
        return designItemMapper.selectPage(pageReqVO);
    }


    @Override
    public Boolean complate(DesignItemSaveReqVO updateReqVO) {

        // 更新当前设计明细的完成状态
        Boolean itemFlag = false;
        Long designId = updateReqVO.getDesignId();
        DesignDO designInfo = designService.getById(designId);
        Long designerId = updateReqVO.getDesignerId();
        LambdaQueryWrapperX<DesignItemDO> queryWrapper = new LambdaQueryWrapperX<>();
        queryWrapper.eq(DesignItemDO::getDesignId, designId);
        queryWrapper.eq(DesignItemDO::getDesignerId, designerId);
        DesignItemDO designItemDO = designItemMapper.selectOne(queryWrapper);
        LocalDateTime planCompleteDate = designItemDO.getPlanCompleteDate();
        LocalDateTime completeDate = updateReqVO.getCompleteDate();
        if (completeDate.isAfter(planCompleteDate)) {
            DesignSummarySaveReqVO designSummarySaveReqVO = new DesignSummarySaveReqVO();
            designSummarySaveReqVO.setDesignId(designId);
            designSummarySaveReqVO.setDesignItemId(designItemDO.getId());
            designSummarySaveReqVO.setProgress("100%");
            Long loginUserId = WebFrameworkUtils.getLoginUserId();
            AdminUserRespDTO adminUserApiUser = adminUserApi.getUser(loginUserId);
            designSummarySaveReqVO.setDesignerId(loginUserId);
            designSummarySaveReqVO.setDesignerName(adminUserApiUser.getNickname());
            designSummarySaveReqVO.setProgressDesc(updateReqVO.getAbnormalExplain());
            designSummaryService.createDesignSummary(designSummarySaveReqVO);


        }

        // 获取当前登录用户信息
        AdminUserRespDTO currentUser = adminUserApi.getUser(WebFrameworkUtils.getLoginUserId());
        if (ObjectUtil.isNotNull(designItemDO)) {
            designItemDO.setItemType(updateReqVO.getItemType());
            designItemDO.setDesignFilePath(updateReqVO.getDesignFilePath());
            designItemDO.setCompleteDate(completeDate);
            designItemDO.setCompleteFlag(BooleanEnum.YES.getValue());

            // 补充操作日志明细
            OperateLogUtils.setContent(String.format("【设计任务】%s 完成了任务，文件位置 %s ", currentUser.getNickname(),updateReqVO.getDesignFilePath()));
            OperateLogUtils.addExt(DesignServiceImpl.OPERATOR_EXTS_KEY, designInfo.getCode());

            itemFlag = super.updateById(designItemDO);
        }
        // 查询当前设计任务的所有设计明细
        Boolean designFlag = true;
        LambdaQueryWrapperX<DesignItemDO> listQuery = new LambdaQueryWrapperX<>();
        listQuery.eq(DesignItemDO::getDesignId, designId);
        List<DesignItemDO> designItemDOList = designItemMapper.selectList(listQuery);
        DesignDO designDO = DesignDO.builder().id(designId).build();
        List<LocalDateTime> completeDateList = designItemDOList.stream().map(DesignItemDO::getCompleteDate).filter(Objects::nonNull).distinct().toList();
        if (CollUtil.isNotEmpty(completeDateList)){
            // 完成时间取认领记录最晚时间
            designDO.setCompleteDate(Collections.max(completeDateList));
        }
        List<DesignItemDO> nonComplatelist = designItemDOList.stream().filter(x -> x.getCompleteFlag() == null || x.getCompleteFlag() == BooleanEnum.NO.getValue()).toList();
        if (CollectionUtils.isEmpty(nonComplatelist)) {
            // 全部设计明细已完成，更新设计任务已完成
            designDO.setDesignStatus(DesignStatusEnum.TO_BE_ASSESS.getValue());
        }
        designFlag = designService.updateById(designDO);
        return itemFlag && designFlag;
    }

    @Override
    public List<DesignItemDO> getAllDesignItemList() {
        return designItemMapper.selectList();
    }
}
