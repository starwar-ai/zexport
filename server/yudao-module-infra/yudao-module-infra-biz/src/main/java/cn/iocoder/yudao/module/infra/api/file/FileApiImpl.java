package cn.iocoder.yudao.module.infra.api.file;

import cn.iocoder.yudao.module.infra.service.file.FileService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.List;

/**
 * 文件 API 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class FileApiImpl implements FileApi {

    @Resource
    private FileService fileService;

    @Override
    public String createFile(String contentType, String name, String path, byte[] content) {
        return fileService.createFile(contentType, name, path, content).getFileUrl();
    }

    @Override
    public byte[] getFileContent(String path) throws Exception {
        return fileService.getFileContent(path);
    }

    @Override
    public int updateFile(List<Long> idList, Long businessId, Integer businessType) {
        return fileService.updateFile(idList, businessId, businessType);
    }

}
