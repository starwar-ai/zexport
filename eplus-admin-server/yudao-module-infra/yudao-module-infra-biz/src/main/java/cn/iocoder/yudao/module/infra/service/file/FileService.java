package cn.iocoder.yudao.module.infra.service.file;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.infra.controller.admin.file.vo.file.FilePageReqVO;
import cn.iocoder.yudao.module.infra.controller.admin.file.vo.file.FileRespVO;
import cn.iocoder.yudao.module.infra.controller.admin.file.vo.file.FileUploadRespVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文件 Service 接口
 *
 * @author 芋道源码
 */
public interface FileService {

    /**
     * 获得文件分页
     *
     * @param pageReqVO 分页查询
     * @return 文件分页
     */
    PageResult<FileRespVO> getFilePage(FilePageReqVO pageReqVO);

    /**
     * 保存文件，并返回文件的访问路径
     *
     * @param contentType 文件类型
     * @param name        文件名称
     * @param path        文件路径
     * @param content     文件内容
     * @return 文件路径
     */
    FileUploadRespVo createFile(String contentType, String name, String path, byte[] content);

    /**
     * 删除文件
     *
     * @param id 编号
     */
    void deleteFile(Long id) throws Exception;

    /**
     * 获得文件内容
     *
     * @param configId 配置编号
     * @param path     文件路径
     * @return 文件内容
     */
    byte[] getFileContent(Long configId, String path) throws Exception;

    /**
     * 获得文件内容（支持强制刷新）
     *
     * @param configId 配置编号
     * @param path     文件路径
     * @param forceRefresh 是否强制刷新缓存
     * @return 文件内容
     */
    byte[] getFileContent(Long configId, String path, boolean forceRefresh) throws Exception;

    /**
     * 获得主客户端文件内容
     *
     * @param path 文件路径
     * @return 文件内容
     */
    byte[] getFileContent(String path) throws Exception;

    /**
     * @param idList       文件id集合
     * @param businessId   业务id
     * @param businessType 业务类型
     */
    int updateFile(List<Long> idList, Long businessId, Integer businessType);

    /**
     * 根据业务类型跟业务id获取当前业务下附件信息
     *
     * @param businessType 业务类型
     * @param businessId   业务id
     * @return
     */
    List<FileRespVO> getFileInfo(Integer businessType, Long businessId);

    /**
     * 根据业务类型跟业务id批量删除文件
     *
     * @param businessType
     * @param businessId
     * @return 上一版本文件列表
     */
    void deleteFileList(Integer businessType, Long businessId);

    void batchDeleteFileList(Integer businessType, List<Long> businessId);

    List<Long> updateBusinessFile(List<Long> idList, Long businessId, Integer businessType);

    List<FileRespVO> getFileInfoByIds(Integer businessType, List<Long> businessIdList);

    /**
     * 处理非新山后增模块附件
     *
     * @param idList
     * @param businessId
     * @param businessType
     */
    void dealSingleTableFile(List<Long> idList, Long businessId, Integer businessType);


    /**
     * 批量保存文件，并返回文件的访问路径列表
     *
     * @param files 文件列表
     * @return 文件路径
     */
    List<FileUploadRespVo> batchCreateFile(MultipartFile[] files) throws Exception;

    // ========== 缓存管理相关方法 ==========

    /**
     * 清除所有文件缓存
     */
    void clearAllFileCache();

    /**
     * 清除指定配置的文件缓存
     *
     * @param configId 配置编号
     */
    void clearConfigCache(Long configId);

    /**
     * 清除指定文件缓存
     *
     * @param configId 配置编号
     * @param path     文件路径
     */
    void clearFileCache(Long configId, String path);

    /**
     * 获取文件缓存统计信息
     *
     * @param configId 配置编号
     * @return 缓存统计信息
     */
    String getCacheStats(Long configId);
}
