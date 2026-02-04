package com.syj.eplus.module.home.service.ReimbDraft;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.syj.eplus.module.home.controller.admin.reimbDetail.vo.ReimbDraftPageReqVO;
import com.syj.eplus.module.oa.api.dto.ReimbDetailDTO;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: du
 * @Date: 2024/09/18/11:19
 * @Description:
 */
public interface ReimbDraftService {

     void createReimbDraft(ReimbDetailDTO reimbDraftDO);

    PageResult<ReimbDetailDTO> getReimbDraft(ReimbDraftPageReqVO reimbDraftPageReqVO);
}
