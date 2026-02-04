package com.syj.eplus.module.oa.service.expenseapp;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.syj.eplus.module.oa.controller.admin.expenseapp.vo.ExpenseAppPageReqVO;
import com.syj.eplus.module.oa.dal.dataobject.expenseapp.ExpenseApp;

/**
 * 招待费申请 service 接口
 */
public interface ExpenseAppService {

    /**
     * 通过审批编号校验招待费申请是否存在
     * @param spNo 审批编号
     * @return 招待费申请是否存在
     */
    boolean validateExpenseAppExistsBySpNo(String spNo);


    /**
     * 创建招待费申请
     * @param expenseApp 招待费申请
     */
    void createExpenseApp(ExpenseApp expenseApp);

    /**
     * 获得招待费申请分页
     * @param pageReqVO 分页查询
     * @return 招待费申请分页
     */
    PageResult<ExpenseApp> getExpenseAppPage(ExpenseAppPageReqVO pageReqVO);

}
