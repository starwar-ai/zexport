package cn.iocoder.yudao.module.infra.convert.file;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.infra.controller.admin.file.vo.file.FileRespVO;
import cn.iocoder.yudao.module.infra.dal.dataobject.file.FileDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @Description：
 * @Author：du
 * @Date：2024/3/16 13:25
 */
@Mapper
public interface FileConvert {
    FileConvert INSTANCE = Mappers.getMapper(FileConvert.class);

    FileRespVO convert(FileDO fileDO);

    default List<FileRespVO> convertFileRespVOList(List<FileDO> fileDOList, String baseUrl) {
        return CollectionUtils.convertList(fileDOList, fileDO -> {
            FileRespVO fileRespVO = convert(fileDO);
            if (StrUtil.isNotEmpty(baseUrl)) {
                fileRespVO.setFileUrl(baseUrl + fileRespVO.getFileUrl());
            }
            return fileRespVO;
        });
    }

    default PageResult<FileRespVO> convertFileVOPageResult(PageResult<FileDO> fileDOPageResult, String baseUrl) {
        Long total = fileDOPageResult.getTotal();
        if (total > 0) {
            List<FileDO> fileDOList = fileDOPageResult.getList();
            List<FileRespVO> fileRespVOList = convertFileRespVOList(fileDOList, baseUrl);
            return new PageResult<FileRespVO>().setList(fileRespVOList).setTotal(total);
        }
        return BeanUtils.toBean(fileDOPageResult, FileRespVO.class);
    }
}
