package com.syj.eplus.module.dpms.service.design;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.syj.eplus.framework.common.dict.CalculationDict;
import com.syj.eplus.framework.common.enums.BooleanEnum;
import com.syj.eplus.module.dpms.controller.admin.design.vo.DesignStatisticRespVO;
import com.syj.eplus.module.dpms.util.ExecutorUtil;
import com.syj.eplus.module.dtms.api.design.DesignApi;
import com.syj.eplus.module.dtms.api.design.dto.DesignPageReqDTO;
import com.syj.eplus.module.dtms.api.design.dto.DesignRespDTO;
import com.syj.eplus.module.dtms.api.designitem.DesignItemApi;
import com.syj.eplus.module.dtms.api.designitem.dto.DesignItemRespDTO;
import com.syj.eplus.module.dtms.enums.DesignStatusEnum;
import com.syj.eplus.module.dtms.enums.EvaluateResultEnum;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.dpms.api.enums.ErrorCodeConstants.REPORT_FUTURE_EXCEPTION;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: du
 * @Date: 2024/08/27/16:17
 * @Description:
 */
@Service
@Validated
public class DesignReportServiceImpl implements DesignReportService {
    @Resource
    private DesignApi designApi;

    @Resource
    private ThreadPoolTaskExecutor bizThreadPool;

    @Resource
    private DesignItemApi designItemApi;

    @Override
    public List<DesignRespDTO> getDesignPage(DesignPageReqDTO pageReqDTO) {
        PageResult<DesignRespDTO> designPage = designApi.getDesignPage(pageReqDTO);
        if (Objects.isNull(designPage) || CollUtil.isEmpty(designPage.getList())) {
            return List.of();
        }
        return designPage.getList();
    }

    @Override
    public DesignStatisticRespVO getDesignStatisticResp() {
        // 初始化相应对象
        DesignStatisticRespVO designStatisticRespVO = new DesignStatisticRespVO().setDesignCount(CalculationDict.LONG_ZERO)
                .setFinishCount(CalculationDict.LONG_ZERO)
                .setComplaintCount(CalculationDict.LONG_ZERO)
                .setThumbUpCount(CalculationDict.LONG_ZERO)
                .setSpecialApproveCount(CalculationDict.LONG_ZERO);
        // 获取所有设计任务
        List<DesignRespDTO> allDesignList = designApi.getAllDesignList();
        if (CollUtil.isNotEmpty(allDesignList)) {
            // 设计总任务
            int size = allDesignList.size();
            designStatisticRespVO.setDesignCount((long) size);
            // 处理任务
            CompletableFuture<Long> designStatusFuture = CompletableFuture.supplyAsync(() -> allDesignList.stream().filter(s -> Objects.equals(DesignStatusEnum.COMPLETED.getValue(), s.getDesignStatus())).count(), bizThreadPool);
            // 特批任务
            CompletableFuture<Long> specialApproveFuture = CompletableFuture.supplyAsync(() -> allDesignList.stream().filter(s -> Objects.equals(BooleanEnum.YES.getValue(), s.getSpecialPermissionFlag())).count(), bizThreadPool);

            try {
                designStatisticRespVO.setFinishCount(designStatusFuture.get(ExecutorUtil.TIMEOUT, TimeUnit.SECONDS));
                designStatisticRespVO.setSpecialApproveCount(specialApproveFuture.get(ExecutorUtil.TIMEOUT, TimeUnit.SECONDS));
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                throw exception(REPORT_FUTURE_EXCEPTION);
            }
        }
        List<DesignItemRespDTO> allDesignItemList = designItemApi.getAllDesignItemList();
        if (CollUtil.isNotEmpty(allDesignItemList)) {
            // 总点赞量
            CompletableFuture<Long> thumbUpFuture = CompletableFuture.supplyAsync(() -> allDesignItemList.stream().filter(s -> Objects.equals(EvaluateResultEnum.THUMB_UP.getValue(), s.getEvaluateResult())).count(), bizThreadPool);
            // 总投诉量
            CompletableFuture<Long> complaintFuture = CompletableFuture.supplyAsync(() -> allDesignItemList.stream().filter(s -> Objects.equals(EvaluateResultEnum.COMPLAINT.getValue(), s.getEvaluateResult())).count(), bizThreadPool);

            try {
                designStatisticRespVO.setThumbUpCount(thumbUpFuture.get(ExecutorUtil.TIMEOUT, TimeUnit.SECONDS));
                designStatisticRespVO.setComplaintCount(complaintFuture.get(ExecutorUtil.TIMEOUT, TimeUnit.SECONDS));
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                throw exception(REPORT_FUTURE_EXCEPTION);
            }
        }
        return designStatisticRespVO;
    }
}
