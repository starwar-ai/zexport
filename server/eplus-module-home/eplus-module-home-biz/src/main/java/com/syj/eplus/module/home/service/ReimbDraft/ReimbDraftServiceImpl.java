package com.syj.eplus.module.home.service.ReimbDraft;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.syj.eplus.module.home.controller.admin.reimbDetail.vo.ReimbDraftPageReqVO;
import com.syj.eplus.module.oa.api.ReimbApi;
import com.syj.eplus.module.oa.api.dto.ReimbDetailDTO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: du
 * @Date: 2024/09/18/11:19
 * @Description:
 */
@Service
public class ReimbDraftServiceImpl implements ReimbDraftService{

    @Resource
    private ReimbApi reimbApi;
    @Override
    public void createReimbDraft(ReimbDetailDTO reimbDraftDO) {
        reimbApi.createReimbDetail(reimbDraftDO);
    }

    @Override
    public PageResult<ReimbDetailDTO> getReimbDraft(ReimbDraftPageReqVO reimbDraftPageReqVO) {
        List<ReimbDetailDTO> reimbDetail = reimbApi.getReimbDetail();
        Integer pageSize = reimbDraftPageReqVO.getPageSize();
        int skipValue = reimbDraftPageReqVO.getSkipValue();
        if (CollUtil.isEmpty(reimbDetail)){
            return PageResult.empty();
        }
        return new PageResult<ReimbDetailDTO>().setList(reimbDetail.stream().skip(skipValue).limit(pageSize).toList()).setTotal((long) reimbDetail.size());
    }
}
