package cn.iocoder.yudao.module.infra.dal.mysql.file;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.infra.controller.admin.file.vo.file.FilePageReqVO;
import cn.iocoder.yudao.module.infra.dal.dataobject.file.FileDO;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Objects;

/**
 * 文件操作 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface FileMapper extends BaseMapperX<FileDO> {

    default PageResult<FileDO> selectPage(FilePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<FileDO>()
                .likeIfPresent(FileDO::getPath, reqVO.getPath())
                .likeIfPresent(FileDO::getFileType, reqVO.getType())
                .betweenIfPresent(FileDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(FileDO::getId));
    }

    default int updateFile(List<Long> idList, Long businessId, Integer businessType) {
        if (CollUtil.isEmpty(idList)) {
            return 0;
        }
        return update(new LambdaUpdateWrapper<FileDO>()
                .in(FileDO::getId, idList)
                .set(FileDO::getBusinessType, businessType)
                .set(FileDO::getBusinessId, businessId));
    }

    default List<FileDO> selectFileListByBusiness(Integer businessType, Long businessId) {
        if (Objects.isNull(businessType) || Objects.isNull(businessId)) {
            return null;
        }
        return selectList(new LambdaQueryWrapperX<FileDO>().eq(FileDO::getBusinessId, businessId)
                .eq(FileDO::getBusinessType, businessType));
    }

    default List<FileDO> selectFileListByBusinessList(Integer businessType, List<Long> businessIdList) {
        if (CollUtil.isEmpty(businessIdList)) {
            return null;
        }
        return selectList(new LambdaQueryWrapperX<FileDO>().in(FileDO::getBusinessId, businessIdList)
                .eq(FileDO::getBusinessType, businessType));
    }

}
