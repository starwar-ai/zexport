package com.syj.eplus.module.pms.dal.mysql.skubom;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.pms.controller.admin.skubom.vo.SkuBomPageReqVO;
import com.syj.eplus.module.pms.dal.dataobject.skubom.SkuBomDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 产品SKU BOM Mapper
 *
 * @author eplus
 */
@Mapper
public interface SkuBomMapper extends BaseMapperX<SkuBomDO> {

    default PageResult<SkuBomDO> selectPage(SkuBomPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<SkuBomDO>()
                .eqIfPresent(SkuBomDO::getSkuId, reqVO.getSkuId())
                .eqIfPresent(SkuBomDO::getSkuVer, reqVO.getSkuVer())
                .eqIfPresent(SkuBomDO::getQty, reqVO.getQty())
                .eqIfPresent(SkuBomDO::getParentSkuId, reqVO.getParentSkuId())
                .betweenIfPresent(SkuBomDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(SkuBomDO::getId));
    }

    @ResultMap(value = "BaseResultMap")
    @Select({
            """
                    <script>
                    WITH RECURSIVE cte AS (
                      SELECT
                        t1.id AS id,
                        t1.sku_id AS skuId,
                        t1.sku_code AS skuCode,
                        t1.sku_type AS skuType,
                        t1.qty AS qty,
                        t1.sku_ver AS skuVer,
                        t1.parent_sku_id AS parentSkuId\s
                      FROM
                        pms_sku_bom t1\s
                      WHERE
                        t1.parent_sku_id = #{parentId} and t1.deleted = 0
                      UNION ALL
                      SELECT
                        t2.id AS id,
                        t2.sku_id AS skuId,
                        t2.sku_code AS skuCode,
                        t2.sku_type AS skuType,
                        t2.qty AS qty,
                        t2.sku_ver AS skuVer,
                        t2.parent_sku_id AS parentSkuId\s
                      FROM
                        pms_sku_bom t2
                    INNER JOIN cte ON t2.parent_sku_id = cte.skuId and t2.deleted =0)
                    SELECT * FROM cte;
                    </script>
                    """
    })
    List<SkuBomDO> selectSkuBomChildNodeByParentId(Long parentId);
}