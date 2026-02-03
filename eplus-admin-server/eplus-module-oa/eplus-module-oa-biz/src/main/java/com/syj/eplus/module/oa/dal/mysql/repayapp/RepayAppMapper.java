package com.syj.eplus.module.oa.dal.mysql.repayapp;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.FilterCondition;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.QueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.util.FilterUtil;
import cn.iocoder.yudao.framework.mybatis.core.util.MyBatisUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.syj.eplus.module.oa.controller.admin.repayapp.vo.RepayAppPageReqVO;
import com.syj.eplus.module.oa.dal.dataobject.repayapp.RepayAppDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 还款单 Mapper
 *
 * @author ePlus
 */
@Mapper
public interface RepayAppMapper extends BaseMapperX<RepayAppDO> {

    default PageResult<RepayAppDO> selectPage(RepayAppPageReqVO reqVO, List<Long> loanAppId) {
        QueryWrapperX<RepayAppDO> queryWrapperX = new QueryWrapperX<>();
        queryWrapperX.eqIfPresent(MyBatisUtils.getFieldNameByFunc(RepayAppDO::getCode), reqVO.getCode());
        queryWrapperX.betweenIfPresent(MyBatisUtils.getFieldNameByFunc(RepayAppDO::getCreateTime), reqVO.getCreateTime());
        queryWrapperX.betweenIfPresent(MyBatisUtils.getFieldNameByFunc(RepayAppDO::getRepayTime), reqVO.getRepayTime());
        queryWrapperX.eqIfPresent(MyBatisUtils.getFieldNameByFunc(getGetAuditStatus()), reqVO.getAuditStatus());
        queryWrapperX.eqIfPresent(MyBatisUtils.getFieldNameByFunc(RepayAppDO::getRepayStatus), reqVO.getRepayStatus());
        String filterCondition = reqVO.getFilterCondition();
        if (StrUtil.isNotEmpty(filterCondition)){
            List<FilterCondition> filterConditionList = JsonUtils.parseArray(filterCondition, FilterCondition.class);
            if (CollUtil.isNotEmpty(filterConditionList)){
                filterConditionList.forEach(s->{
                    FilterUtil.filterSql(queryWrapperX,s.getCond(),s.getField(),s.getValue());
                });
            }
        }
        queryWrapperX.orderByDesc("id");
        return selectPage(reqVO, queryWrapperX);
        // TODO json用户类型过滤项查询

    }

    private static SFunction<RepayAppDO, Object> getGetAuditStatus() {
        return RepayAppDO::getAuditStatus;
    }


    @Update("update oa_repay_app set repay_status = 1 where code = #{code}")
    void updateRepayStatus(@Param("code") String code);

}