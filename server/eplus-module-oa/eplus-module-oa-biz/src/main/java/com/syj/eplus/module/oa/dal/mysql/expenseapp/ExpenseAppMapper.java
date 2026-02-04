package com.syj.eplus.module.oa.dal.mysql.expenseapp;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.oa.controller.admin.expenseapp.vo.ExpenseAppPageReqVO;
import com.syj.eplus.module.oa.dal.dataobject.expenseapp.ExpenseApp;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ExpenseAppMapper extends BaseMapperX<ExpenseApp> {
    default PageResult<ExpenseApp> selectPage(ExpenseAppPageReqVO reqVO) {
        LambdaQueryWrapperX<ExpenseApp> queryWrapperX = new LambdaQueryWrapperX<ExpenseApp>()
                .betweenIfPresent(ExpenseApp::getExpenseDate, reqVO.getExpenseDate())
                .orderByDesc(ExpenseApp::getId);
        return selectPage(reqVO, queryWrapperX);
    }
}