package com.syj.eplus.module.fms.dal.mysql.payee;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import com.syj.eplus.module.fms.dal.dataobject.custclaim.PayeeEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Description：
 * @Author：du
 * @Date：2024/7/23 15:33
 */
@Mapper
public interface PayeeMapper extends BaseMapperX<PayeeEntity> {

    @Select("""
                SELECT DISTINCT CODE\s
                FROM
                  crm_cust\s
                WHERE
                  id IN (
                  SELECT DISTINCT
                    cust_id\s
                  FROM
                    crm_cust_bankaccount\s
                WHERE
                  bank_poc = #{companyTitle})
            """)
    List<String> getNoCustCodeListByCompanyTitle(@Param("companyTitle") String companyTitle);
}