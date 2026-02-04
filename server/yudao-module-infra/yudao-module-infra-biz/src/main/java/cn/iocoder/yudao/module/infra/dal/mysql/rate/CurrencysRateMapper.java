package cn.iocoder.yudao.module.infra.dal.mysql.rate;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.infra.controller.admin.rate.vo.CurrencysRatePageReqVO;
import cn.iocoder.yudao.module.infra.dal.dataobject.rate.CurrencysRateDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Description：
 * @Author：du
 * @Date：2024/1/12 14:36
 */
@Mapper
public interface CurrencysRateMapper extends BaseMapperX<CurrencysRateDO> {
    @Select("SELECT *\n" +
            "FROM (\n" +
            "    SELECT *, ROW_NUMBER() OVER(PARTITION BY daily_curr_name ORDER BY create_time DESC) as row_num -- daily_curr_indate\n" +
            "    FROM daily_currencys_rate\n" +
            ") t\n" +
            "WHERE t.row_num = 1")
    List<CurrencysRateDO> getCurrencysRate();

    default PageResult<CurrencysRateDO> selectPage(CurrencysRatePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<CurrencysRateDO>()
                .betweenIfPresent(CurrencysRateDO::getDailyCurrDate, reqVO.getDailyCurrDate())
                .likeIfPresent(CurrencysRateDO::getDailyCurrName, reqVO.getDailyCurrName())
                .eqIfPresent(CurrencysRateDO::getDailyCurrRate, reqVO.getDailyCurrRate())
                .eqIfPresent(CurrencysRateDO::getDailyCurrSource, reqVO.getDailyCurrSource())
                .eqIfPresent(CurrencysRateDO::getDailyCurrMidRate, reqVO.getDailyCurrMidRate())
                .betweenIfPresent(CurrencysRateDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(CurrencysRateDO::getId));
    }
}
