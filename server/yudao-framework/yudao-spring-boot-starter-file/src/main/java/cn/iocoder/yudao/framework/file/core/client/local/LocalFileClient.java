package cn.iocoder.yudao.framework.file.core.client.local;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.file.core.client.AbstractFileClient;

import java.io.File;
import java.util.List;

/**
 * 本地文件客户端
 *
 * @author 芋道源码
 */
public class LocalFileClient extends AbstractFileClient<LocalFileClientConfig> {

    public LocalFileClient(Long id, LocalFileClientConfig config) {
        super(id, config);
    }

    @Override
    protected void doInit() {
        // 补全风格。例如说 Linux 是 /，Windows 是 \
        if (!config.getBasePath().endsWith(File.separator)) {
            config.setBasePath(config.getBasePath() + File.separator);
        }
    }

    @Override
    public String upload(byte[] content, String path, String type) {
        // 执行写入
        String filePath = getFilePath(path);
        FileUtil.writeBytes(content, filePath);
        // 拼接返回路径
        return super.formatFileUrl(path);
    }

    @Override
    public void delete(String path) {
        String filePath = getFilePath(path);
        FileUtil.del(filePath);
    }

    @Override
    public byte[] getContent(String path) {
        String filePath = getFilePath(path);
        return FileUtil.readBytes(filePath);
    }

    @Override
    public void batchDelete(List<String> pathList) {
        List<String> filePathList = getFilePathList(pathList);
        if (CollUtil.isNotEmpty(filePathList)) {
            filePathList.forEach(FileUtil::del);
        }
    }

    @Override
    public String getBaseUrl() {
        return config.getDomain();
    }

    private String getFilePath(String path) {
        return config.getBasePath() + path;
    }

    /**
     * 批量转换文件存储路径
     *
     * @param pathList
     * @return
     */
    private List<String> getFilePathList(List<String> pathList) {
        if (CollUtil.isEmpty(pathList)) {
            return null;
        }
        String basePath = config.getBasePath();
        return pathList.stream().filter(StrUtil::isNotEmpty).map(path -> basePath + path).toList();
    }

}
