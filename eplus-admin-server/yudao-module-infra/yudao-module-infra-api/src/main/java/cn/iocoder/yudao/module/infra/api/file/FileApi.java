package cn.iocoder.yudao.module.infra.api.file;

import java.util.List;

/**
 * 文件 API 接口
 *
 * @author 芋道源码
 */
public interface FileApi {

    /**
     * 保存文件，并返回文件的访问路径
     *
     * @param content 文件内容
     * @return 文件路径
     */
    default String createFile(byte[] content) {
        return createFile(null, null, null, content);
    }

    /**
     * 保存文件，并返回文件的访问路径
     *
     * @param path    文件路径
     * @param content 文件内容
     * @return 文件路径
     */
    default String createFile(String path, byte[] content) {
        return createFile(null, null, path, content);
    }
    default String createFile( byte[] content,String name) {
        return createFile(null, name, null, content);
    }

    /**
     * 保存文件，并返回文件的访问路径
     *
     * @param name    文件名称
     * @param path    文件路径
     * @param content 文件内容
     * @return 文件路径
     */
    String createFile(String contentType, String name, String path, byte[] content);

    /**
     * 获得主客户端文件内容
     *
     * @param path     文件路径
     * @return 文件内容
     */
    byte[] getFileContent(String path) throws Exception;

    /**
     * @param idList       文件id集合
     * @param businessId   业务id
     * @param businessType 业务类型
     */
    int updateFile(List<Long> idList, Long businessId, Integer businessType);

}
