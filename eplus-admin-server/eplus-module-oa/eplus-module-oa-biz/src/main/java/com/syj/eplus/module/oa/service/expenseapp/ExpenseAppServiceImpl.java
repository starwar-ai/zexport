package com.syj.eplus.module.oa.service.expenseapp;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.syj.eplus.module.oa.controller.admin.expenseapp.vo.ExpenseAppPageReqVO;
import com.syj.eplus.module.oa.dal.dataobject.expenseapp.ExpenseApp;
import com.syj.eplus.module.oa.dal.mysql.expenseapp.ExpenseAppMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ExpenseAppServiceImpl implements ExpenseAppService {

    @Resource
    private ExpenseAppMapper expenseAppMapper;
    @Override
    public boolean validateExpenseAppExistsBySpNo(String spNo) {
        return expenseAppMapper.selectCount(ExpenseApp::getSpNo, spNo)>0;
    }

    @Override
    public void createExpenseApp(ExpenseApp expenseApp) {
        expenseAppMapper.insert(expenseApp);
    }

    @Override
    public PageResult<ExpenseApp> getExpenseAppPage(ExpenseAppPageReqVO pageReqVO) {
        return expenseAppMapper.selectPage(pageReqVO);
    }


}
