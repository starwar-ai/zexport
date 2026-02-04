package com.syj.eplus.module.scm.dal.mysql.purchaseplan;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.MPJLambdaWrapperX;
import com.syj.eplus.module.scm.controller.admin.purchaseplan.vo.PurchasePlanPageReqVO;
import com.syj.eplus.module.scm.dal.dataobject.purchaseplan.PurchasePlanDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Objects;

/**
 * 采购计划 Mapper
 *
 * @author zhangcm
 */
@Mapper
public interface PurchasePlanMapper extends BaseMapperX<PurchasePlanDO> {

    default PageResult<PurchasePlanDO> selectPage(PurchasePlanPageReqVO reqVO) {
        MPJLambdaWrapperX<PurchasePlanDO> queryWrapperX = new MPJLambdaWrapperX<PurchasePlanDO>()
                .selectAll(PurchasePlanDO.class)
                .eqIfPresent(PurchasePlanDO::getVer, reqVO.getVer())
                .likeIfPresent(PurchasePlanDO::getCode, reqVO.getCode())
                .eqIfPresent(PurchasePlanDO::getAuditStatus, reqVO.getAuditStatus())
                .eqIfPresent(PurchasePlanDO::getPlanStatus, reqVO.getPlanStatus())
                .eqIfPresent(PurchasePlanDO::getCustId, reqVO.getCustId())
                .eqIfPresent(PurchasePlanDO::getCreator, reqVO.getCreator())
                .betweenIfPresent(PurchasePlanDO::getPlanDate, reqVO.getPlanDate())
                .eqIfPresent(PurchasePlanDO::getCustCode, reqVO.getCustCode())
                .likeIfPresent(PurchasePlanDO::getCustName, reqVO.getCustName())
                .likeIfPresent(PurchasePlanDO::getSaleContractCode, reqVO.getSaleContractCode())
                .betweenIfPresent(PurchasePlanDO::getEstDeliveryDate, reqVO.getEstDeliveryDate())
                .eqIfPresent(PurchasePlanDO::getSourceType, reqVO.getSourceType())
                .eqIfPresent(PurchasePlanDO::getRemark, reqVO.getRemark())
                .eqIfPresent(PurchasePlanDO::getCreatorDeptId, reqVO.getCreatorDeptId())
                .eqIfPresent(PurchasePlanDO::getAnnex, reqVO.getAnnex())
                .eqIfPresent(PurchasePlanDO::getAuxiliaryFlag, reqVO.getAuxiliaryFlag())
                .eqIfPresent(PurchasePlanDO::getCompanyId, reqVO.getCompanyId())
                .betweenIfPresent(PurchasePlanDO::getCreateTime, reqVO.getCreateTime())
                .inIfPresent(PurchasePlanDO::getCode, reqVO.getCodeList())
                .inIfPresent(PurchasePlanDO::getId, reqVO.getIdList())
                .neIfPresent(PurchasePlanDO::getPlanStatus, reqVO.getNeStatus())
                .orderByDesc(PurchasePlanDO::getId);
        String subSql = "SELECT 1 FROM scm_purchase_plan_item pi WHERE pi.purchase_plan_id = t.id";
        if (StrUtil.isNotEmpty(reqVO.getBasicSkuCode())){
            subSql+=" AND pi.basic_sku_code LIKE '%" + reqVO.getBasicSkuCode() + "%'";
        }
        if (StrUtil.isNotEmpty(reqVO.getSkuName())){
            subSql+=" AND pi.sku_name LIKE '%" + reqVO.getSkuName() + "%'";
        }
        if (StrUtil.isNotEmpty(reqVO.getCskuCode())){
            subSql+=" AND pi.csku_code LIKE '%" + reqVO.getCskuCode() + "%'";
        }
        if (StrUtil.isNotEmpty(reqVO.getSkuCode())){
            subSql+=" AND pi.sku_code LIKE '%" + reqVO.getSkuCode() + "%'";
        }
        if (StrUtil.isNotEmpty(reqVO.getVenderCode())){
            subSql+=" AND pi.vender_code LIKE '%" + reqVO.getVenderCode() + "%'";
        }
        if (StrUtil.isNotEmpty(reqVO.getAuxiliaryPurchaseContractCode())){
            subSql+=" AND pi.auxiliary_purchase_contract_code LIKE '%" + reqVO.getAuxiliaryPurchaseContractCode() + "%'";
        }
        if (StrUtil.isNotEmpty(reqVO.getAuxiliarySaleContractCode())){
            subSql+=" AND pi.auxiliary_sale_contract_code LIKE '%" + reqVO.getAuxiliarySaleContractCode() + "%'";
        }
        if (Objects.nonNull(reqVO.getPurchaseUserId())){
            subSql+=" AND pi.purchase_user_id =" + reqVO.getPurchaseUserId();
        }
        queryWrapperX.exists(subSql);
        return selectPage(reqVO, queryWrapperX);
    }

    default PageResult<PurchasePlanDO> selectPageByPlanStatus(PurchasePlanPageReqVO reqVO,List<Integer> statusList) {
        return selectPage(reqVO, new LambdaQueryWrapperX<PurchasePlanDO>()
                .eqIfPresent(PurchasePlanDO::getVer, reqVO.getVer())
                .likeIfPresent(PurchasePlanDO::getCode, reqVO.getCode())
                .eqIfPresent(PurchasePlanDO::getAuditStatus, reqVO.getAuditStatus())
                .eqIfPresent(PurchasePlanDO::getCustId, reqVO.getCustId())
                .likeIfPresent(PurchasePlanDO::getCustCode, reqVO.getCustCode())
                .likeIfPresent(PurchasePlanDO::getCustName, reqVO.getCustName())
                .eqIfPresent(PurchasePlanDO::getSourceType, reqVO.getSourceType())
                .eqIfPresent(PurchasePlanDO::getEstDeliveryDate, reqVO.getEstDeliveryDate())
                .eqIfPresent(PurchasePlanDO::getRemark, reqVO.getRemark())
                .eqIfPresent(PurchasePlanDO::getAnnex, reqVO.getAnnex())
                .eqIfPresent(PurchasePlanDO::getCompanyId, reqVO.getCompanyId())
                .eqIfPresent(PurchasePlanDO::getAuxiliaryFlag, reqVO. getAuxiliaryFlag())
                .betweenIfPresent(PurchasePlanDO::getCreateTime, reqVO.getCreateTime())
                .inIfPresent(PurchasePlanDO::getPlanStatus,statusList)
                .orderByDesc(PurchasePlanDO::getId));
    }


    @Select("""
            <script>
            SELECT p.id FROM `scm_purchase_plan` as p
            where exists(
                select 1 from scm_purchase_plan_item pi where p.id = pi.purchase_plan_id
            <when test='buyerId!="" and buyerId!=null'>
            AND pi.purchase_user_id =  #{buyerId} 
            </when>
            <when test='planStatus!=null and planStatus==5'>
            AND pi.converted_flag = 1
            </when>
             ) 
            <when test='planStatus!=null and planStatus!=5'>
            AND p.plan_status = #{planStatus,jdbcType=INTEGER} 
            </when>
            <when test='planStatus!=null and planStatus==5'>
            AND p.plan_status in (3,5) 
            </when>
             <when test='code!="" and code!=null'>
            AND p.code like CONCAT('%',#{code},'%') 
            </when>
           <when test='neStatus!="" and neStatus!=null'>
            AND p.plan_status != #{neStatus}  
            </when>
            <when test='autoFlag!="" and autoFlag!=null'>
            AND p.autoFlag = #{autoFlag}  
            </when>
            AND p.auxiliary_flag = #{auxiliaryFlag} 
            <when test='saleContractCode!="" and saleContractCode!=null'>
            AND p.sale_contract_code  like CONCAT("%",#{saleContractCode,jdbcType=VARCHAR},"%")
            </when>
            <if test= 'idList != null' >
                AND p.id in
                     <foreach collection="idList" open="(" close=")" item="id" separator="," >
                         #{id}
                     </foreach>
              </if> 
            order by p.id desc
            <when test='pageSize>0 and pageNo>=0'> 
            LIMIT #{pageSize}  OFFSET #{pageNo} 
            </when>
            </script>""")
    List<Long> getPage(@Param("pageNo") Integer pageNo,
                  @Param("pageSize") Integer pageSize,
                  @Param("buyerId") Long buyerId,
                  @Param("planStatus") Integer planStatus,
                  @Param("code") String code,
                  @Param("autoFlag") Integer autoFlag,
                  @Param("auxiliaryFlag") Integer auxiliaryFlag,
                  @Param("saleContractCode") String saleContractCode,
                  @Param("neStatus") Integer neStatus,
                  @Param("idList")Long[] idList
                  );

    @Select("""
            <script>
            SELECT count(1) FROM `scm_purchase_plan` as p
            where exists(
                select 1 from scm_purchase_plan_item pi where p.id = pi.purchase_plan_id
            <when test='buyerId!="" and buyerId!=null'>
            AND pi.purchase_user_id =  #{buyerId} 
            </when>
            <when test='planStatus!=null and planStatus==5'>
            AND pi.converted_flag = 1
            </when>
             ) 
            <when test='planStatus!=null and planStatus!=5'>
            AND p.plan_status = #{planStatus,jdbcType=INTEGER} 
            </when>
            <when test='planStatus!=null and planStatus==5'>
            AND p.plan_status in (3,5) 
            </when>
             <when test='code!="" and code!=null'>
            AND p.code like CONCAT('%',#{code},'%') 
            </when>
              <when test='neStatus!="" and neStatus!=null'>
            AND p.plan_status != #{neStatus}  
            </when>
            <when test='autoFlag!="" and autoFlag!=null'>
            AND p.autoFlag = #{autoFlag}  
            </when>
            <when test='saleContractCode!="" and saleContractCode!=null'>
            AND p.sale_contract_code  like CONCAT("%",#{saleContractCode,jdbcType=VARCHAR},"%")
            </when>
            <if test= 'idList != null' >
                AND p.id in
                     <foreach collection="idList" open="(" close=")" item="id" separator="," >
                         #{id}
                     </foreach>
              </if> 
            AND p.auxiliary_flag = #{auxiliaryFlag} 
            </script>""")
    Long  getCount(
            @Param("buyerId") Long buyerId,
            @Param("planStatus") Integer planStatus,
            @Param("code") String code,
            @Param("autoFlag") Integer autoFlag,
            @Param("auxiliaryFlag") Integer auxiliaryFlag,
            @Param("saleContractCode") String saleContractCode,
            @Param("neStatus") Integer neStatus,
            @Param("idList")Long[] idList
    );
    @Select("""
            <script>
            select count(1) from scm_purchase_plan a where a.id = #{id} and  a.deleted=0 and exists(
                                                          select contract_status from scm_purchase_contract b where b.deleted = 0 AND b.contract_status != 7 AND b.purchase_plan_id = #{id}
                                                           )
            </script>""")
    Long validateAntiAuditStatus(@Param("id")Long id);
}