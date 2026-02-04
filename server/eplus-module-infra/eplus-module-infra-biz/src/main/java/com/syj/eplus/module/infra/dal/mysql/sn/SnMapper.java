package com.syj.eplus.module.infra.dal.mysql.sn;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.syj.eplus.module.infra.controller.admin.sn.vo.SnPageReqVO;
import com.syj.eplus.module.infra.dal.dataobject.sn.SnDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 编号 Mapper
 *
 * @author 波波
 */
@Mapper
public interface SnMapper extends BaseMapper<SnDO> {

    default PageResult<SnDO> selectPage(SnPageReqVO reqVO) {
        LambdaQueryWrapper<SnDO> queryWrapper = new LambdaQueryWrapper<SnDO>()
                .eq(reqVO.getType() != null, SnDO::getType, reqVO.getType())
                .eq(reqVO.getCodePrefix() != null, SnDO::getCodePrefix, reqVO.getCodePrefix())
                .orderByDesc(SnDO::getId);
        IPage<SnDO> page = new Page<>(reqVO.getPageNo(), reqVO.getPageSize());
        page = selectPage(page, queryWrapper);
        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    default SnDO selectByTypeAndCodePrefix(String type, String codePrefix) {
        return selectOne(new LambdaQueryWrapper<SnDO>()
                .eq(SnDO::getType, type)
                .eq(SnDO::getCodePrefix, codePrefix));
    }

    @Select("SELECT * FROM system_sn WHERE type = #{type} AND code_prefix = #{codePrefix} FOR UPDATE")
    SnDO selectForUpdate(@Param("type") String type, @Param("codePrefix") String codePrefix);

    @Update("UPDATE system_sn SET sn = sn + 1 WHERE id = #{id}")
    void incrementSn(@Param("id") Long id);
}