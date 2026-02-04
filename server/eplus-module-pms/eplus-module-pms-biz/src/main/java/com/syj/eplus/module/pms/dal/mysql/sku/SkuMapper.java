package com.syj.eplus.module.pms.dal.mysql.sku;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.MPJLambdaWrapperX;
import com.syj.eplus.framework.common.enums.ChangeFlagEnum;
import com.syj.eplus.framework.common.enums.SkuTypeEnum;
import com.syj.eplus.module.crm.enums.cust.DeletedEnum;
import com.syj.eplus.module.pms.controller.admin.sku.vo.SkuPageReqVO;
import com.syj.eplus.module.pms.dal.dataobject.hsdata.HsdataDO;
import com.syj.eplus.module.pms.dal.dataobject.sku.SimpleSkuDO;
import com.syj.eplus.module.pms.dal.dataobject.sku.SkuDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 财务收款单 Mapper
 *
 * @author du
 */
@Mapper
public interface SkuMapper extends BaseMapperX<SkuDO> {

    default PageResult<SkuDO> selectPage(SkuPageReqVO reqVO, Boolean onlySkuFlag) {
        MPJLambdaWrapperX<SkuDO> queryWrapperX = new MPJLambdaWrapperX<SkuDO>();
        queryWrapperX.selectAll(SkuDO.class);
        if (StrUtil.isNotEmpty(reqVO.getHsCode()) || Objects.nonNull(reqVO.getTaxRefundRate())){
            queryWrapperX.leftJoin(HsdataDO.class, HsdataDO::getId, SkuDO::getHsCodeId);
        }
        if(StrUtil.isNotEmpty(reqVO.getHsCode())){
            queryWrapperX.like(HsdataDO::getCode,reqVO.getHsCode());
        }
        if(Objects.nonNull(reqVO.getTaxRefundRate())){
            queryWrapperX.eq(HsdataDO::getTaxRefundRate,reqVO.getTaxRefundRate());
        }

        queryWrapperX.eqIfPresent(SkuDO::getAuditStatus, reqVO.getAuditStatus())
                .eqIfPresent(SkuDO::getSpuId, reqVO.getSpuId())
                .likeIfPresent(SkuDO::getNameEng, reqVO.getNameEng())
                .inIfPresent(SkuDO::getName, reqVO.getNameList())
                .likeIfPresent(SkuDO::getCode, reqVO.getCode())
                .eqIfPresent(SkuDO::getBarcode, reqVO.getBarcode())
                .likeIfPresent(SkuDO::getCustCode, reqVO.getCustCode())
                .likeIfPresent(SkuDO::getCskuCode, reqVO.getCskuCode())
                .eqIfPresent(SkuDO::getAgentFlag,reqVO.getAgentFlag())
                .likeIfPresent(SkuDO::getOskuCode, reqVO.getOskuCode())
                .inIfPresent(SkuDO::getId,reqVO.getIdSet())
                .likeIfPresent(SkuDO::getBasicSkuCode, reqVO.getBasicSkuCode())
                .eqIfPresent(SkuDO::getOnshelfFlag, reqVO.getOnshelfFlag())
                .eqIfPresent(SkuDO::getAdvantageFlag, reqVO.getAdvantageFlag())
                .eqIfPresent(SkuDO::getDescription, reqVO.getDescription())
                .inIfPresent(SkuDO::getCustCode,reqVO.getCustCodeList())
                .inIfPresent(SkuDO::getCode,reqVO.getCodeList())
                .inIfPresent(SkuDO::getCskuCode,reqVO.getCskuCodeList())
                .inIfPresent(SkuDO::getOskuCode,reqVO.getOskuCodeList())
                .inIfPresent(SkuDO::getBasicSkuCode,reqVO.getBasicSkuCodeList())
                .eqIfPresent(SkuDO::getDescriptionEng, reqVO.getDescriptionEng())
                .eqIfPresent(SkuDO::getOwnBrandFlag, reqVO.getOwnBrandFlag())
                .eqIfPresent(SkuDO::getBrandId, reqVO.getBrandId())
                .eqIfPresent(SkuDO::getCategoryId, reqVO.getCategoryId())
//                .eqIfPresent(SkuDO::getAuxiliarySkuFlag, reqVO.getAuxiliarySkuFlag())
                .eqIfPresent(SkuDO::getHsCodeId, reqVO.getHsCodeId())
                .inIfPresent(SkuDO::getHsCodeId, reqVO.getHsCodeIdList())
                .eqIfPresent(SkuDO::getSourceFlag, reqVO.getSourceFlag())
                .eqIfPresent(SkuDO::getSkuType, reqVO.getSkuType())
                .eqIfPresent(SkuDO::getMaterial, reqVO.getMaterial())
                .eqIfPresent(SkuDO::getMeasureUnit, reqVO.getMeasureUnit())
                .eqIfPresent(SkuDO::getSpecLength, reqVO.getSpecLength())
                .eqIfPresent(SkuDO::getSpecWidth, reqVO.getSpecWidth())
                .eqIfPresent(SkuDO::getSpecHeight, reqVO.getSpecHeight())
                .eqIfPresent(SkuDO::getSingleProcessFee, reqVO.getSingleProcessFee())
                .eqIfPresent(SkuDO::getProcessRemark, reqVO.getProcessRemark())
                .eqIfPresent(SkuDO::getCustProFlag, reqVO.getCustProFlag())
                .eqIfPresent(SkuDO::getAuxiliaryMaterial, reqVO.getAuxiliaryMaterial())
                .betweenIfPresent(SkuDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(SkuDO::getAutoCreateFlag, reqVO.getAutoCreateFlag())
                .eqIfPresent(SkuDO::getChangeFlag, ChangeFlagEnum.NO.getValue())
                .inIfPresent(SkuDO::getId,reqVO.getIdList())
                .neIfPresent(SkuDO::getCode,reqVO.getExcludeSkuCode())
                .eqIfPresent(SkuDO::getBelongingDeptId,reqVO.getBelongingDeptId())
                .orderByDesc(SkuDO::getCreateTime);
        if (Objects.nonNull(reqVO.getCreator())){
            queryWrapperX.eq(SkuDO::getCreator,String.valueOf(reqVO.getCreator()));
        }
        if (onlySkuFlag) {
            queryWrapperX.ne(SkuDO::getSkuType, SkuTypeEnum.AUXILIARY_MATERIALS.getValue());
        }
        if (CollUtil.isNotEmpty(reqVO.getSkuTypeInList())) {
            queryWrapperX.in(SkuDO::getSkuType, reqVO.getSkuTypeInList());
        }
        if(Objects.nonNull(reqVO.getName())){
            List<String> nameArr = Arrays.stream(reqVO.getName().split(" "))
                    .filter(keyword -> !keyword.isEmpty())
                    .toList();
            if(nameArr.size() > 0){
                queryWrapperX.and(wrapper -> {
                    for(int i = 0; i < nameArr.size(); i++) {
                        String name = nameArr.get(i);
                        wrapper.like(SkuDO::getName, name);
//                        if(i == 0) {
//                            wrapper.like(SkuDO::getName, name);
//                        } else {
//                            wrapper.or().like(SkuDO::getName, name);
//                        }
                    }
                });
            }
        }
        return selectPage(reqVO, queryWrapperX);
    }

    default PageResult<SkuDO> selectChangePage(SkuPageReqVO reqVO, Boolean onlySkuFlag) {
        LambdaQueryWrapperX<SkuDO> queryWrapperX = new LambdaQueryWrapperX<SkuDO>()
                .eqIfPresent(SkuDO::getAuditStatus, reqVO.getAuditStatus())
                .eqIfPresent(SkuDO::getSpuId, reqVO.getSpuId())
                .likeIfPresent(SkuDO::getName, reqVO.getName())
                .eqIfPresent(SkuDO::getNameEng, reqVO.getNameEng())
                .eqIfPresent(SkuDO::getCode, reqVO.getCode())
                .eqIfPresent(SkuDO::getBarcode, reqVO.getBarcode())
                .eqIfPresent(SkuDO::getCustCode, reqVO.getCustCode())
                .eqIfPresent(SkuDO::getCskuCode, reqVO.getCskuCode())
                .eqIfPresent(SkuDO::getOskuCode, reqVO.getOskuCode())
                .eqIfPresent(SkuDO::getOnshelfFlag, reqVO.getOnshelfFlag())
                .eqIfPresent(SkuDO::getDescription, reqVO.getDescription())
                .eqIfPresent(SkuDO::getDescriptionEng, reqVO.getDescriptionEng())
                .eqIfPresent(SkuDO::getOwnBrandFlag, reqVO.getOwnBrandFlag())
                .eqIfPresent(SkuDO::getBrandId, reqVO.getBrandId())
                .eqIfPresent(SkuDO::getCategoryId, reqVO.getCategoryId())
//                .eqIfPresent(SkuDO::getAuxiliarySkuFlag, reqVO.getAuxiliarySkuFlag())
                .eqIfPresent(SkuDO::getHsCodeId, reqVO.getHsCodeId())
                .inIfPresent(SkuDO::getHsCodeId, reqVO.getHsCodeIdList())
                .eqIfPresent(SkuDO::getSourceFlag, reqVO.getSourceFlag())
                .eqIfPresent(SkuDO::getSkuType, reqVO.getSkuType())
                .eqIfPresent(SkuDO::getMaterial, reqVO.getMaterial())
                .eqIfPresent(SkuDO::getMeasureUnit, reqVO.getMeasureUnit())
                .eqIfPresent(SkuDO::getSpecLength, reqVO.getSpecLength())
                .eqIfPresent(SkuDO::getSpecWidth, reqVO.getSpecWidth())
                .eqIfPresent(SkuDO::getSpecHeight, reqVO.getSpecHeight())
                .eqIfPresent(SkuDO::getSingleProcessFee, reqVO.getSingleProcessFee())
                .eqIfPresent(SkuDO::getProcessRemark, reqVO.getProcessRemark())
                .eqIfPresent(SkuDO::getCustProFlag, reqVO.getCustProFlag())
                .betweenIfPresent(SkuDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(SkuDO::getAutoCreateFlag, reqVO.getAutoCreateFlag())
                .eqIfPresent(SkuDO::getAgentFlag, reqVO.getAgentFlag())
                .eqIfPresent(SkuDO::getChangeDeleted, DeletedEnum.NO.getValue())
                .gtIfPresent(SkuDO::getVer, 1)
                .inIfPresent(SkuDO::getDeleted, new ArrayList<>(Arrays.asList(0,1)))
                .orderByDesc(SkuDO::getCreateTime);
        if (onlySkuFlag) {
            queryWrapperX.ne(SkuDO::getSkuType, SkuTypeEnum.AUXILIARY_MATERIALS.getValue());
        }
        if (CollUtil.isNotEmpty(reqVO.getSkuTypeInList())) {
            queryWrapperX.in(SkuDO::getSkuType, reqVO.getSkuTypeInList());
        }
        return selectPage(reqVO, queryWrapperX);
    }


    @ResultMap(value = "BaseSkuResultMap")
    @Select("""
            <script>
            SELECT
                a.id  AS id,
                a.CODE AS code,
                a.source_code AS sourceCode,
                a.price AS price,
                a.csku_code AS cskuCode,
                a.sku_type AS skuType,
                a.picture AS pictures,
                a.thumbnail AS thumbnail,
                a.company_price AS companyPrice,
                a.name AS name,
                a.name_eng AS nameEng,
                a.description AS description,
                a.description_eng AS description_eng,
                a.material AS material,
                a.cust_pro_flag  AS custProFlag,
                a.auxiliary_sku_flag  AS auxiliarySkuFlag,
                a.measure_unit  AS measureUnit,
                a.audit_status AS auditStatus,
                a.cust_code AS custCode,
                a.create_time AS createTime
            FROM
                pms_sku a
                LEFT JOIN scm_quote_item sqi ON a.id = sqi.sku_id and sqi.deleted = 0 and sqi.default_flag = 1
            WHERE
                 a.deleted =0 
                <when test='auditStatus!=null'>\s
                AND a.audit_status = #{auditStatus}\s
                </when>\s
                 <when test='advantageFlag!=null'>\s
                AND a.advantage_flag = #{advantageFlag}\s
                </when>\s
                <when test='name!="" and name!=null'>\s
                AND a.name like CONCAT('%',#{name},'%')\s
                </when>\s
                <when test='nameEng!="" and nameEng!=null'>\s
                AND a.name_eng like CONCAT('%',#{nameEng},'%')\s
                </when>\s
                <when test='code!="" and code!=null'>\s
                AND a.code like CONCAT('%',#{code},'%')\s
                </when>\s
                <when test='skuType!=null'>\s
                AND a.sku_type = #{skuType}\s
                </when>\s
                <when test='custProFlag!=null'>\s
                AND a.cust_pro_flag = #{custProFlag}\s
                </when>\s
                <when test='ownBrandFlag!=null'>\s
                AND a.own_brand_flag = #{ownBrandFlag}\s
                </when>\s
                <when test='onlySkuFlag == true'>\s
                AND a.sku_type != 4\s
                </when>\s
                <when test='codeList!=null and codeList.size()>0'>\s
                 AND a.code in
                 <foreach collection="codeList" open="(" close=")" item="code" separator="," >\s
                     #{code}\s
                 </foreach>\s
                 </when>\s
                  <when test='nameList!=null and nameList.size()>0'>\s
                 AND a.name in
                 <foreach collection="nameList" open="(" close=")" item="name" separator="," >\s
                     #{name}\s
                 </foreach>\s
                 </when>\s
                  <when test='custCodeList!=null and custCodeList.size()>0'>\s
                 AND a.cust_code in
                 <foreach collection="custCodeList" open="(" close=")" item="custCode" separator="," >\s
                     #{custCode}\s
                 </foreach>\s
                 </when>\s
                  <when test='venderIdList!=null and venderIdList.size()>0'>\s
                 AND sqi.id in
                 <foreach collection="venderIdList" open="(" close=")" item="id" separator="," >\s
                     #{id}\s
                 </foreach>\s
                 </when>\s
                 <when test='purchaseUserId!="" and purchaseUserId!=null'>\s
                AND sqi.purchase_user_id = #{purchaseUserId}\s
                </when>\s
             order by a.create_time desc
            LIMIT #{skip,jdbcType=INTEGER} , #{limit,jdbcType=INTEGER} 
            </script>
            """)
    List<SkuDO> getSkuPage( @Param("auditStatus") Integer auditStatus,
                                   @Param("advantageFlag") Integer advantageFlag,
                                   @Param("name") String name,
                                   @Param("nameEng") String nameEng,
                                   @Param("code") String code,
                                   @Param("skuType") Integer skuType,
                                   @Param("venderIdList") List<Long> venderIdList,
                                   @Param("purchaseUserId") Long purchaseUserId,
                                   @Param("onlySkuFlag") Boolean onlySkuFlag,
                                   @Param("codeList") List<String> codeList,
                                   @Param("nameList") List<String> nameList,
                                   @Param("custCodeList") List<String> custCodeList,
                                   @Param("custProFlag") Integer custProFlag,
                                   @Param("ownBrandFlag") Integer ownBrandFlag,
                                   @Param("skip") int skip,
                                   @Param("limit") int limit);


    @Select("""
            <script>
            SELECT
                count(1)
            FROM
                pms_sku a
                LEFT JOIN scm_quote_item sqi ON a.id = sqi.sku_id and sqi.deleted = 0 and sqi.default_flag = 1
                where
                 a.deleted =0
            <when test='auditStatus!=null'>\s
                 AND a.audit_status = #{auditStatus}\s
                 </when>\s
                  <when test='advantageFlag!=null'>\s
                 AND a.advantage_flag = #{advantageFlag}\s
                 </when>\s
                 <when test='name!="" and name!=null'>\s
                 AND a.name like CONCAT('%',#{name},'%')\s
                 </when>\s
                  <when test='custProFlag!=null'>\s
                AND a.cust_pro_flag = #{custProFlag}\s
                </when>\s
                <when test='ownBrandFlag!=null'>\s
                AND a.own_brand_flag = #{ownBrandFlag}\s
                </when>\s
                 <when test='nameEng!="" and nameEng!=null'>\s
                 AND a.name_eng like CONCAT('%',#{nameEng},'%')\s
                 </when>\s
                 <when test='code!="" and code!=null'>\s
                 AND a.code like CONCAT('%',#{code},'%')\s
                 </when>\s
                 <when test='skuType!=null'>\s
                 AND a.sku_type = #{skuType}\s
                 </when>\s
                 <when test='onlySkuFlag == true'>\s
                 AND a.sku_type != 4\s
                 </when>\s
                  <when test='codeList!=null and codeList.size()>0'>\s
                 AND a.code in
                 <foreach collection="codeList" open="(" close=")" item="code" separator="," >\s
                     #{code}\s
                 </foreach>\s
                 </when>\s
                  <when test='nameList!=null and nameList.size()>0'>\s
                 AND a.name in
                 <foreach collection="nameList" open="(" close=")" item="name" separator="," >\s
                     #{name}\s
                 </foreach>\s
                 </when>\s
                  <when test='custCodeList!=null and custCodeList.size()>0'>\s
                 AND a.cust_code in
                 <foreach collection="custCodeList" open="(" close=")" item="custCode" separator="," >\s
                     #{custCode}\s
                 </foreach>\s
                 </when>\s
                 <when test='venderIdList!=null and venderIdList.size()>0'>\s
                  AND sqi.id in
                  <foreach collection="venderIdList" open="(" close=")" item="id" separator="," >\s
                      #{id}\s
                  </foreach>\s
                  </when>\s
                  <when test='purchaseUserId!="" and purchaseUserId!=null'>\s
                 AND sqi.purchase_user_id = #{purchaseUserId}\s
                 </when>\s
            </script>
            """)
    Long getSkuPageCount(@Param("auditStatus") Integer auditStatus,
                         @Param("advantageFlag") Integer advantageFlag,
                         @Param("name") String name,
                         @Param("nameEng") String nameEng,
                         @Param("code") String code,
                         @Param("skuType") Integer skuType,
                         @Param("venderIdList") List<Long> venderIdList,
                         @Param("purchaseUserId") Long purchaseUserId,
                         @Param("onlySkuFlag") Boolean onlySkuFlag,
                         @Param("codeList") List<String> codeList,
                         @Param("nameList") List<String> nameList,
                         @Param("custCodeList") List<String> custCodeList,
                         @Param("custProFlag") Integer custProFlag,
                         @Param("ownBrandFlag") Integer ownBrandFlag);


    @ResultMap(value = "ResultMapWithJson")
    @Select("""
            <script>
            SELECT
                a.id  AS id,
                a.CODE AS code,
                a.barcode AS barcode,
                a.hs_code_id AS hsCodeId,
                a.source_code AS sourceCode,
                a.price AS price,
                a.csku_code AS cskuCode,
                a.sku_type AS skuType,
                a.picture AS pictures,
                a.thumbnail AS thumbnail,
                a.company_price AS companyPrice,
                a.name AS skuName,
                a.name_eng AS nameEng,
                a.description AS description,
                a.description_eng AS description_eng,
                a.material AS material,
                a.cust_pro_flag  AS custProFlag,
                a.auxiliary_sku_flag  AS auxiliarySkuFlag,
                a.measure_unit  AS measureUnit,
                b.NAME AS brandName,
                c.NAME AS categoryName,
                d.code AS hsCode,
                d.tax_refund_rate as taxRefundRate,
                a.onshelf_flag AS onshelfFlag,\s
                a.own_brand_flag AS ownBrandFlag,
                a.cust_pro_flag AS custProFlag,
                e.name AS custName,
                a.commodity_inspection_flag AS commodityInspectionFlag,
                a.create_time AS createTime,
                a.update_time AS updateTime,
                a.basic_sku_code AS basicSkuCode,
                a.osku_code AS oskuCode
            FROM
                pms_sku a
                LEFT JOIN pms_brand b ON a.brand_id = b.id
                LEFT JOIN pms_category c ON a.category_id = c.id\s
                LEFT JOIN pms_hsdata d ON a.hs_code_id = d.id
                LEFT JOIN crm_cust e ON a.cust_code = e.code AND e.deleted = 0 AND e.deleted = 0 and e.change_flag = 0
            WHERE
                 a.deleted =0 and a.audit_status=2 and a.onshelf_flag = 1 and a.change_flag = 0
                 <when test='skuCodeList!=null and skuCodeList.size()>0'>\s
                 AND a.code in
                 <foreach collection="skuCodeList" open="(" close=")" item="skuCode" separator="," >\s
                 #{skuCode}\s
                 </foreach>\s
                 </when>\s
                 <when test='sourceCodeList!=null and sourceCodeList.size()>0'>\s
                 AND a.source_code in
                 <foreach collection="sourceCodeList" open="(" close=")" item="sourceCode" separator="," >\s
                 #{sourceCode}\s
                 </foreach>\s
                 </when>\s
                 <when test='skuName!="" and skuName!=null'>\s
                AND a.name like CONCAT('%',#{skuName},'%')\s
                </when>\s
                <when test='categoryId!=null'>\s
                AND a.category_id = #{categoryId}\s
                </when>\s
                <when test='custCode!="" and custCode!=null'>\s
                AND a.cust_code = #{custCode}\s
                </when>\s
                 <when test='cskuCode!="" and cskuCode!=null'>\s
                 AND a.csku_code like CONCAT('%', #{cskuCode},'%')\s
                </when>\s
                <when test='skuType!="" and skuType!=null'>\s
                AND a.sku_type = #{skuType}\s
                </when>\s
                <when test='agentFlag!=null'>\s
                AND a.agent_flag = #{agentFlag}\s
                </when>\s
                <when test='ownBrandFlag!=null'>\s
                AND a.own_brand_flag = #{ownBrandFlag}\s
                </when>\s
                <when test='autoCreateFlag!=null'>\s
                AND a.auto_create_flag = #{autoCreateFlag}\s
                </when>\s
                <when test='custProFlag!=null'>\s
                AND a.cust_pro_flag = #{custProFlag}\s
                </when>\s
                <when test='skuCode!="" and skuCode!=null'>\s
                AND a.code like CONCAT('%',#{skuCode},'%')\s
                </when>\s
                <when test='oskuCode!="" and oskuCode!=null'>\s
                AND a.osku_code like CONCAT('%',#{oskuCode},'%')\s
                </when>\s
                  <when test='basicSkuCode!="" and basicSkuCode!=null'>\s
                AND a.basic_sku_code like CONCAT('%',#{basicSkuCode},'%')\s
                </when>\s
                 <when test='ids!=null and ids.size()>0'>\s
                 AND a.id in
             <foreach collection="ids" open="(" close=")" item="id" separator="," >\s
                 #{id}\s
             </foreach>\s
             </when>\s
                <when test='skuTypeInList!=null and skuTypeInList.size()>0'>\s
                 AND a.sku_type in
             <foreach collection="skuTypeInList" open="(" close=")" item="skuType" separator="," >\s
                 #{skuType}\s
             </foreach>\s
             </when>\s
             <when test='skuTypeOutList!=null and skuTypeOutList.size()>0'>\s
                 AND a.sku_type not in
             <foreach collection="skuTypeOutList" open="(" close=")" item="skuType1" separator="," >\s
                 #{skuType1}\s
             </foreach>\s
             </when>  
             order by a.update_time desc
            LIMIT #{skip,jdbcType=INTEGER} , #{limit,jdbcType=INTEGER} \s
            </script>
            """)
    List<SimpleSkuDO> getSimpleSku(@Param("skuCodeList") List<String> skuCodeList,
                                   @Param("sourceCodeList") List<String> sourceCodeList,
                                   @Param("categoryId") Long categoryId,
                                   @Param("skuName") String skuName,
                                   @Param("custProFlag") Integer custProFlag,
                                   @Param("custCode") String custCode,
                                   @Param("cskuCode") String cskuCode,
                                   @Param("ownBrandFlag") Integer ownBrandFlag,
                                   @Param("autoCreateFlag") Integer autoCreateFlag,
                                   @Param("ids") List<Long> ids,
                                   @Param("skuType") Integer skuType,
                                   @Param("skuTypeInList") List<Integer> skuTypeInList,
                                   @Param("skuTypeOutList") List<Integer> skuTypeOutList,
                                   @Param("skuCode") String skuCode,
                                   @Param("oskuCode") String oskuCode,
                                   @Param("basicSkuCode") String basicSkuCode,
                                   @Param("agentFlag") Integer agentFlag,
                                   @Param("skip") int skip,
                                   @Param("limit") int limit);


    @ResultMap(value = "ResultMapWithJson")
    @Select("""
            <script>
            SELECT distinct  
                a.id  AS id,
                a.CODE AS code,
                a.source_code AS sourceCode,
                a.price AS price,
                a.csku_code AS cskuCode,
                a.sku_type AS skuType,
                a.picture AS pictures,
                a.company_price AS companyPrice,
                a.name AS skuName,
                a.name_eng AS nameEng,
                a.description AS description,
                a.change_flag AS changeFlag,
                a.description_eng AS descriptionEng,
                a.material AS material,
                a.cust_pro_flag  AS custProFlag,
                a.auxiliary_sku_flag  AS auxiliarySkuFlag,
                a.measure_unit  AS measureUnit,
                b.NAME AS brandName,
                c.NAME AS categoryName,
                d.code AS hsCode,
                d.tax_refund_rate as taxRefundRate,
                a.onshelf_flag AS onshelfFlag,\s
                a.own_brand_flag AS ownBrandFlag,
                a.cust_pro_flag AS custProFlag,
                e.name AS custName,
                a.create_time AS createTime,
                a.basic_sku_code AS basicSkuCode,
                a.thumbnail as thumbnail
            FROM
                pms_sku a
                LEFT JOIN pms_brand b ON a.brand_id = b.id
                LEFT JOIN pms_category c ON a.category_id = c.id\s
                LEFT JOIN pms_hsdata d ON a.hs_code_id = d.id
                LEFT JOIN crm_cust e ON a.cust_code = e.code AND e.deleted = 0
            WHERE
                  a.audit_status=2 and a.onshelf_flag = 1 and a.change_flag = 0
                 <when test='ids!=null and ids.size()>0'>\s
                 AND a.id in
             <foreach collection="ids" open="(" close=")" item="id" separator="," >\s
                 #{id}\s
             </foreach>\s
             </when>\s
            LIMIT #{skip,jdbcType=INTEGER} , #{limit,jdbcType=INTEGER}\s
            </script>
            """)
    List<SimpleSkuDO> getSimpleSkuContainsDeleted(
                                   @Param("ids") List<Long> ids,
                                   @Param("skip") int skip,
                                   @Param("limit") int limit);

    @Select("""
            <script>
            SELECT
                count(1)
            FROM
                pms_sku a
                where
                 a.deleted =0 and a.audit_status=2 and a.onshelf_flag = 1 and a.change_flag = 0
                 <when test='skuCodeList!=null and skuCodeList.size()>0'>\s
                 AND a.code in
                 <foreach collection="skuCodeList" open="(" close=")" item="skuCode" separator="," >\s
                 #{skuCode}\s
                 </foreach>\s
                 </when>\s
                 <when test='sourceCodeList!=null and sourceCodeList.size()>0'>\s
                 AND a.source_code in
                 <foreach collection="sourceCodeList" open="(" close=")" item="sourceCode" separator="," >\s
                 #{sourceCode}\s
                 </foreach>\s
                 </when>\s
                 <when test='skuName!="" and skuName!=null'>\s
                AND a.name like CONCAT('%',#{skuName},'%')\s
                </when>\s
                <when test='categoryId!=null'>\s
                AND a.category_id = #{categoryId}\s
                </when>\s
                <when test='custCode!="" and custCode!=null'>\s
                AND a.cust_code = #{custCode}\s
                </when>\s
                 <when test='cskuCode!="" and cskuCode!=null'>\s
                 AND a.csku_code like CONCAT('%', #{cskuCode},'%')\s
                </when>\s
               <when test='basicSkuCode!="" and basicSkuCode!=null'>\s
                AND a.basic_sku_code like CONCAT('%',#{basicSkuCode},'%')\s
                </when>\s
                <when test='skuType!="" and skuType!=null'>\s
                AND a.sku_type = #{skuType}\s
                </when>\s
                <when test='agentFlag!="" and agentFlag!=null'>\s
                AND a.agent_flag = #{agentFlag}\s
                </when>\s
                 <when test='ownBrandFlag!=null'>\s
                AND a.own_brand_flag = #{ownBrandFlag}\s
                </when>\s
                 <when test='autoCreateFlag!=null'>\s
                AND a.auto_create_flag = #{autoCreateFlag}\s
                </when>\s
                <when test='custProFlag!=null'>\s
                AND a.cust_pro_flag = #{custProFlag}\s
                </when>\s
                  <when test='skuCode!="" and skuCode!=null'>\s
                AND a.code like CONCAT('%', #{skuCode},'%')\s
                </when>\s
                       <when test='oskuCode!="" and oskuCode!=null'>\s
                AND a.osku_code like CONCAT('%', #{oskuCode},'%')\s
                </when>\s
                 <when test='ids!=null and ids.size()>0'>\s
                 AND a.id in
             <foreach collection="ids" open="(" close=")" item="id" separator="," >\s
                 #{id}\s
             </foreach>\s
             </when>\s
                <when test='skuTypeInList!=null and skuTypeInList.size()>0'>\s
                 AND a.sku_type in
             <foreach collection="skuTypeInList" open="(" close=")" item="skuType" separator="," >\s
                 #{skuType}\s
             </foreach>\s
             </when>\s
                     <when test='skuTypeOutList!=null and skuTypeOutList.size()>0'>\s
                 AND a.sku_type not in
             <foreach collection="skuTypeOutList" open="(" close=")" item="skuType" separator="," >\s
                 #{skuType}\s
             </foreach>\s
             </when>   
            </script>
            """)
    Long getSkuCount(@Param("skuCodeList") List<String> skuCodeList,
                     @Param("sourceCodeList") List<String> sourceCodeList,
                     @Param("categoryId") Long categoryId,
                     @Param("skuName") String skuName,
                     @Param("custProFlag") Integer custProFlag,
                     @Param("custCode") String custCode,
                     @Param("cskuCode") String cskuCode,
                     @Param("ownBrandFlag") Integer ownBrandFlag,
                     @Param("autoCreateFlag") Integer autoCreateFlag,
                     @Param("ids") List<Long> ids,
                     @Param("skuType") Integer skuType,
                     @Param("skuTypeInList") List<Integer> skuTypeInList,
                     @Param("skuTypeOutList") List<Integer> skuTypeOutList,
                     @Param("skuCode") String skuCode,
                     @Param("oskuCode") String oskuCode,
                     @Param("basicSkuCode") String basicSkuCode,
                     @Param("agentFlag")Integer agentFlag);

    @Select("""
            select distinct sku_code from sms_sale_contract_item
                """)
    List<String> getReorderSkuCodeList();

    @Select("""
            select count(1) from pms_sku a where a.id = #{id} and a.deleted=0 and (exists(\s
                        	select 1 from sms_sale_contract_item b where a.id = b.sku_id and b.deleted = 0\s
                        )  or exists(\s
                        	select 1 from scm_purchase_plan_item c where a.id = c.sku_id and c.deleted = 0\s
                        )  or exists(
                          select 1 from scm_purchase_contract_item d where a.id = d.sku_id and d.deleted = 0
                        )  or  exists(
                        	select 1 from dms_shipment_item e where a.id = e.sku_id and e.deleted = 0
                        ) or  exists(
                        	select 1 from wms_notice_item h where a.id = h.sku_id and h.deleted = 0
                        ))
            """)
    Long validateAntiAuditStatus(@Param("id") Long id);

    @Select("""
            select a.unit from pms_hsdata a
            LEFT JOIN pms_sku b
            on a.id = b.hs_code_id
            WHERE b.code = #{skuCode}
            and b.deleted = 0
            """)
    String getHsMeasureUnitBySkuCode(@Param("skuCode")String skuCode);
}