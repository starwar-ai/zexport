package com.syj.eplus.module.scm.dal.mysql.vender;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.syj.eplus.framework.common.enums.ChangeFlagEnum;
import com.syj.eplus.module.crm.enums.cust.DeletedEnum;
import com.syj.eplus.module.scm.api.vender.dto.SimpleVenderRespDTO;
import com.syj.eplus.module.scm.controller.admin.vender.vo.VenderPageReqVO;
import com.syj.eplus.module.scm.dal.dataobject.vender.VenderDO;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * 供应商信息 Mapper
 *
 * @author zhangcm
 */
@Mapper
public interface VenderMapper extends BaseMapperX<VenderDO> {

    default PageResult<VenderDO> selectPage(VenderPageReqVO reqVO, List<String> buyerIds) {
        LambdaQueryWrapperX<VenderDO> venderDOLambdaQueryWrapperX = new LambdaQueryWrapperX<VenderDO>()
                .eqIfPresent(VenderDO::getVer, reqVO.getVer())
                .likeIfPresent(VenderDO::getCode, reqVO.getCode())
                .likeIfPresent(VenderDO::getName, reqVO.getName())
                .eqIfPresent(VenderDO::getAdministrationVenderType, reqVO.getAdministrationVenderType())
                .likeIfPresent(VenderDO::getNameShort, reqVO.getNameShort())
                .likeIfPresent(VenderDO::getLicenseNo, reqVO.getLicenseNo())
                .likeIfPresent(VenderDO::getPhone, reqVO.getPhone())
                .eqIfPresent(VenderDO::getAuditStatus, reqVO.getAuditStatus())
                .eqIfPresent(VenderDO::getEnableFlag, reqVO.getEnableFlag())
                .betweenIfPresent(VenderDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(VenderDO::getStageType, reqVO.getStageType())
                .eqIfPresent(VenderDO::getVenderType, reqVO.getVenderType())
                .eqIfPresent(VenderDO::getChangeFlag, ChangeFlagEnum.NO.getValue())
                .orderByDesc(VenderDO::getId);
        if (StrUtil.isNotEmpty(reqVO.getCurrency())){
            venderDOLambdaQueryWrapperX.apply("JSON_CONTAINS(JSON_EXTRACT(tax_msg, '$[*].currency'), CAST({0} AS JSON))", reqVO.getCurrency());
        }
        //处理多业务员过滤项逻辑
        if (CollUtil.isNotEmpty(buyerIds)) {
            String condition = buyerIds.stream()
                    .map(id -> "FIND_IN_SET(" + id + ", buyer_ids)")
                    .collect(Collectors.joining(" OR "));
            venderDOLambdaQueryWrapperX.apply("(" + condition + ")");
        }
        // 处理采购员部门过滤项逻辑
        if (Objects.nonNull(reqVO.getBuyerDeptId())) {
            venderDOLambdaQueryWrapperX.apply("JSON_CONTAINS(JSON_EXTRACT(buyer_list, '$[*].deptId'), CAST({0} AS JSON))", reqVO.getBuyerDeptId());
        }
        return selectPage(reqVO, venderDOLambdaQueryWrapperX);
    }

    default PageResult<VenderDO> selectChangePage(VenderPageReqVO reqVO, List<String> buyerIds) {
        LambdaQueryWrapperX<VenderDO> venderDOLambdaQueryWrapperX = new LambdaQueryWrapperX<VenderDO>()
                .eqIfPresent(VenderDO::getVer, reqVO.getVer())
                .likeIfPresent(VenderDO::getCode, reqVO.getCode())
                .likeIfPresent(VenderDO::getName, reqVO.getName())
                .likeIfPresent(VenderDO::getNameShort, reqVO.getNameShort())
                .likeIfPresent(VenderDO::getLicenseNo, reqVO.getLicenseNo())
                .likeIfPresent(VenderDO::getPhone, reqVO.getPhone())
                .eqIfPresent(VenderDO::getAuditStatus, reqVO.getAuditStatus())
                .eqIfPresent(VenderDO::getEnableFlag, reqVO.getEnableFlag())
                .betweenIfPresent(VenderDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(VenderDO::getStageType, reqVO.getStageType())
                .eqIfPresent(VenderDO::getVenderType, reqVO.getVenderType())
                .eqIfPresent(VenderDO::getChangeDeleted, DeletedEnum.NO.getValue())
                .gtIfPresent(VenderDO::getVer, 1)
                .inIfPresent(VenderDO::getDeleted, new ArrayList<>(Arrays.asList(0, 1)))
                .orderByDesc(VenderDO::getId);
        if (StrUtil.isNotEmpty(reqVO.getCurrency())){
            venderDOLambdaQueryWrapperX.apply("JSON_CONTAINS(JSON_EXTRACT(tax_msg, '$[*].currency'), CAST({0} AS JSON))", reqVO.getCurrency());
        }
        //处理多业务员过滤项逻辑
        if (CollUtil.isNotEmpty(buyerIds)) {
            String condition = buyerIds.stream()
                    .map(id -> "FIND_IN_SET(" + id + ", buyer_ids)")
                    .collect(Collectors.joining(" OR "));
            venderDOLambdaQueryWrapperX.apply("(" + condition + ")");
        }
        return selectPage(reqVO, venderDOLambdaQueryWrapperX);
    }

    @Select("""
            <script>SELECT t.id,t.code, t.name, t.name_short, t.name_eng FROM scm_vender t \s
             WHERE t.Deleted = '0' and audit_status=2 and enable_flag = 1 and change_flag = 0\s
            <when test='venderName!="" and venderName!=null and venderNameRegex!="" and venderName!=null'>\s
            AND t.name REGEXP #{venderNameRegex}\s
            ORDER BY length(REPLACE(t.name, #{venderName}, ''))/length(t.name)\s
            </when></script>""")
    List<SimpleVenderRespDTO> queryMatchVenderName(@Param("venderNameRegex") String venderNameRegex, @Param("venderName") String venderName);

    default PageResult<VenderDO> selectSimplePage(PageParam reqVO, LambdaQueryWrapper<VenderDO> lambdaQueryWrapper) {
        lambdaQueryWrapper.orderByDesc(VenderDO::getId);
        return selectPage(reqVO, lambdaQueryWrapper);
    }


    @Results({
            @Result(column = "buyerList", property = "buyerList", 
                    javaType = List.class, 
                    typeHandler = com.syj.eplus.framework.common.config.handler.BaseValueListTypeHandler.class),
            @Result(column = "taxMsg", property = "taxMsg",
                    javaType = List.class,
                    typeHandler = com.syj.eplus.framework.common.config.handler.JsonVenderTaxListTypeHandler.class)
    })
    @Select("""
            <script>select a.id,a.code,a.name as name,a.name_short as nameShort,a.vender_type as venderType,a.name_eng as nameEng,a.abroad_flag as abroadFlag,a.create_time as createTime,\s
            a.delivery_area_id as deliveryAreaId,a.delivery_address as deliveryAddress,a.tax_msg as taxMsg,a.buyer_list buyerList from scm_vender a \s
            where a.deleted =0 and a.enable_flag = 1 and a.change_flag = 0\s
            <when test='venderName!="" and venderName!=null and venderNameRegex!="" and venderNameRegex!=null'>\s
            AND a.name like CONCAT('%',#{venderNameRegex,jdbcType=VARCHAR},'%')\s
            </when>\s
            <when test='startTime!=null and endTime!=null'>\s
            AND a.create_time BETWEEN #{startTime,jdbcType=TIMESTAMP} AND #{endTime,jdbcType=TIMESTAMP}\s
            </when>\s
            <when test='skuQuoteFlag!=null and skuQuoteFlag==1'>\s
            AND ((a.stage_type = 1 and a.audit_status = 0) or (a.stage_type = 2 and a.audit_status = 2))\s
            </when>\s
            <when test='stageTypeFlag!=null and stageTypeFlag==1'>\s
            AND ((a.stage_type = 1 and a.audit_status = 0) or (a.stage_type = 2 and a.audit_status = 2))\s
            </when>\s
            <when test='(skuQuoteFlag==null or skuQuoteFlag==0) and (stageTypeFlag==null or stageTypeFlag==0)'>\s
            AND a.audit_status = 2\s
            </when>\s
            <when test='venderCode!="" and venderCode!=null'>
            AND a.code like CONCAT("%",#{venderCode,jdbcType=VARCHAR},"%")\s
            </when>
            <when test='rateFlag!="" and rateFlag>0'>
            AND JSON_SEARCH(a.tax_msg, 'one', NULL, '', '$[*].currency') IS NULL\s
            AND JSON_CONTAINS(JSON_EXTRACT(a.tax_msg, '$[*].currency'), CAST('' AS JSON)) = 0\s
            </when>
            <when test='venderType!=null'>
            AND a.vender_type = #{venderType} \s
            </when>
            <when test='purchaseUserId!=null'>
            AND JSON_CONTAINS(JSON_EXTRACT(buyer_list, '$[*].value'), CAST(#{purchaseUserId} AS JSON))\s
            </when>
            <when test='purchaseUserDeptId!=null'>
            AND JSON_CONTAINS(JSON_EXTRACT(buyer_list, '$[*].deptId'), CAST(#{purchaseUserDeptId} AS JSON))\s
            </when>
            <when test='administrationVenderType!=null'>
            AND a.administration_vender_type = #{administrationVenderType} \s
            </when>
            ORDER BY length(REPLACE(a.name, #{venderName ,jdbcType=VARCHAR}, ''))/length(a.name)\s
            LIMIT #{skip,jdbcType=INTEGER} , #{limit,jdbcType=INTEGER}\s
            </script>""")
    List<SimpleVenderRespDTO> selectSimpleList(@Param("startTime") LocalDateTime startTime,
                                               @Param("endTime") LocalDateTime endTime,
                                               @Param("venderCode") String venderCode,
                                               @Param("venderName") String venderName,
                                               @Param("purchaseUserId") Long purchaseUserId,
                                               @Param("purchaseUserDeptId") Long purchaseUserDeptId,
                                               @Param("venderNameRegex") String venderNameRegex,
                                               @Param("skip") int skip,
                                               @Param("limit") int limit,
                                               @Param("rateFlag") Integer rateFlag,
                                               @Param("venderType") Integer venderType,
                                               @Param("stageTypeFlag") Integer stageTypeFlag,
                                               @Param("administrationVenderType") Integer administrationVenderType,
                                               @Param("skuQuoteFlag") Integer skuQuoteFlag);

    @Select("""
            <script>select count(1) from scm_vender where deleted =0  and enable_flag = 1 and change_flag = 0\s
            <when test='venderName!="" and venderName!=null and venderNameRegex!="" and venderNameRegex!=null'>\s
            AND name like CONCAT('%',#{venderNameRegex,jdbcType=VARCHAR},'%')\s
            </when>\s
            <when test='startTime!=null and endTime!=null'>\s
            AND create_time BETWEEN #{startTime,jdbcType=TIMESTAMP} AND #{endTime,jdbcType=TIMESTAMP}\s
            </when>\s
            <when test='skuQuoteFlag!=null and skuQuoteFlag==1'>\s
            AND ((stage_type = 1 and audit_status = 0) or (stage_type = 2 and audit_status = 2))\s
            </when>\s
            <when test='stageTypeFlag!=null and stageTypeFlag==1'>\s
            AND ((stage_type = 1 and audit_status = 0) or (stage_type = 2 and audit_status = 2))\s
            </when>
            <when test='(skuQuoteFlag==null or skuQuoteFlag==0) and (stageTypeFlag==null or stageTypeFlag==0)'>\s
            AND audit_status = 2\s
            </when>\s
            <when test='venderCode!="" and venderCode!=null'>
            AND code like CONCAT("%",#{venderCode,jdbcType=VARCHAR},"%")\s
            </when>
             <when test='rateFlag!="" and rateFlag>0'>
            AND JSON_SEARCH(tax_msg, 'one', NULL, '', '$[*].currency') IS NULL\s
            AND JSON_CONTAINS(JSON_EXTRACT(tax_msg, '$[*].currency'), CAST('' AS JSON)) = 0\s
            </when>
            <when test='venderType!=null'>
            AND vender_type = #{venderType} \s
            </when>
            <when test='purchaseUserId!=null'>
            AND JSON_CONTAINS(JSON_EXTRACT(buyer_list, '$[*].value'), CAST(#{purchaseUserId} AS JSON))\s
            </when>
            <when test='purchaseUserDeptId!=null'>
            AND JSON_CONTAINS(JSON_EXTRACT(buyer_list, '$[*].deptId'), CAST(#{purchaseUserDeptId} AS JSON))\s
            </when>
            <when test='administrationVenderType!=null'>
            AND administration_vender_type = #{administrationVenderType} \s
            </when>
            </script>""")
    Long getCount(@Param("startTime") LocalDateTime startTime,
                  @Param("endTime") LocalDateTime endTime,
                  @Param("venderCode") String venderCode,
                  @Param("venderName") String venderName,
                  @Param("purchaseUserId") Long purchaseUserId,
                  @Param("purchaseUserDeptId") Long purchaseUserDeptId,
                  @Param("venderNameRegex") String venderNameRegex,
                  @Param("rateFlag") Integer rateFlag,
                  @Param("venderType") Integer venderType,
                  @Param("stageTypeFlag") Integer stageTypeFlag,
                  @Param("administrationVenderType") Integer administrationVenderType,
                  @Param("skuQuoteFlag") Integer skuQuoteFlag);

    @ResultMap(value = "ResultMapWithJson")
    @Select("""
            <script>SELECT t.id,t.code, t.name, t.tax_msg as taxMsg,t.abroad_flag,t.vender_type,t.create_time,t.internal_flag internalFlag,t.internal_company_id internalCompanyId,t.buyer_ids buyerIds  FROM scm_vender t\s
             WHERE t.enable_flag = 1 and t.change_flag = 0\s
            <when test='venderIds!=null and venderIds.size()>0'>\s
             AND t.id in\s
             <foreach collection="venderIds" open="(" close=")" item="venderId" separator="," >\s
                 #{venderId}\s
             </foreach>\s
             </when>\s
             </script>
            """)
    List<SimpleVenderRespDTO> batchSimpleVenderRespDTOList(@Param("venderIds") List<Long> venderIds);

    @Results({
            @Result(column = "taxMsg", property = "taxMsg",
                    javaType = List.class,
                    typeHandler = com.syj.eplus.framework.common.config.handler.JsonVenderTaxListTypeHandler.class)
    })
    @Select("""
            <script>SELECT t.id,t.code, t.name, t.abroad_flag,t.vender_type,t.create_time,t.tax_msg taxMsg  FROM scm_vender t\s
             WHERE t.enable_flag = 1 and t.change_flag = 0 and t.deleted = 0\s
            <when test='venderCodes!=null and venderCodes.size()>0'>\s
             AND t.code in\s
             <foreach collection="venderCodes" open="(" close=")" item="venderCode" separator="," >\s
                 #{venderCode}\s
             </foreach>\s
             </when>\s
             </script>
            """)
    List<SimpleVenderRespDTO> batchSimpleVenderRespDTOListByCodeList(@Param("venderCodes") List<String> venderCodes);


    @Select("""
                select count(1) from scm_vender t1
                where EXISTS(select 1 from scm_vender_bankaccount t2 where t1.id = t2.vender_id and t2.bank = #{bank} and t2.bank_account = #{bankAccount})
                and t1.code = #{venderCode}
            """)
    Long checkVenderBank(@Param("bank") String bank,
                         @Param("venderCode") String venderCode,
                         @Param("bankAccount") String bankAccount);

    @Insert("""
                insert into scm_vender_bankaccount (vender_id,bank,bank_account)
                VALUES(#{venderId},#{bank},#{bankAccount} )
            """)
    Long insertVenderBank(@Param("bank") String bank,
                          @Param("venderId") Long venderId,
                          @Param("bankAccount") String bankAccount);

    @Select("""
                SELECT t.name FROM scm_vender t \s
                         WHERE SOUNDEX(name) = SOUNDEX(#{name})
            """)
    List<String> checkName(@Param("name") String name);


    @Select("""
            select count(1) from scm_vender a where a.id = #{id} and a.deleted=0 and (exists( \s
                          select 1 from scm_purchase_contract b where a.id = b.vender_id and b.deleted = 0\s
                        )  or exists(\s
                        	select 1 from oa_payment_app g where a.code = g.business_subject_code and g.deleted = 0 and g.business_subject_type = 2\s
                        )  or  exists(\s
                        	select 1 from wms_notice_item h where a.id = h.vender_id and h.deleted = 0\s
                        ))
            """)
    Long validateAntiAuditStatus(@Param("id") Long id);
}
