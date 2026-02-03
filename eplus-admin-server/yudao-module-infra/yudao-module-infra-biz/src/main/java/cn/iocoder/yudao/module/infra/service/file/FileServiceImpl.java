package cn.iocoder.yudao.module.infra.service.file;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.io.FileUtils;
import cn.iocoder.yudao.framework.file.core.client.FileClient;
import cn.iocoder.yudao.framework.file.core.client.sftp.SftpFileClient;
import cn.iocoder.yudao.framework.file.core.utils.FileTypeUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.infra.controller.admin.file.vo.file.FilePageReqVO;
import cn.iocoder.yudao.module.infra.controller.admin.file.vo.file.FileRespVO;
import cn.iocoder.yudao.module.infra.controller.admin.file.vo.file.FileUploadRespVo;
import cn.iocoder.yudao.module.infra.convert.file.FileConvert;
import cn.iocoder.yudao.module.infra.dal.dataobject.file.FileDO;
import cn.iocoder.yudao.module.infra.dal.mysql.file.FileMapper;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.infra.enums.ErrorCodeConstants.FILE_NOT_EXISTS;

/**
 * 文件 Service 实现类
 *
 * @author 芋道源码
 */
@Service
public class FileServiceImpl implements FileService {

    @Resource
    private FileConfigService fileConfigService;

    @Resource
    private FileMapper fileMapper;

    @Resource
    private AdminUserApi adminUserApi;

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public PageResult<FileRespVO> getFilePage(FilePageReqVO pageReqVO) {
        PageResult<FileDO> fileDOPageResult = fileMapper.selectPage(pageReqVO);
        return FileConvert.INSTANCE.convertFileVOPageResult(fileDOPageResult, fileConfigService.getBaseUrl());
    }

    @Override
    @SneakyThrows
    public FileUploadRespVo createFile(String contentType, String name, String path, byte[] content) {
        // 计算默认的 path 名
        String type = FileTypeUtils.getMineType(content, name);
        if (StrUtil.isEmpty(path)) {
            path = FileUtils.generatePath(content, name);
        }
        // 如果 name 为空，则使用 path 填充
        if (StrUtil.isEmpty(name)) {
            name = path;
        }
        String newName = IdUtil.fastSimpleUUID();
        // 上传到文件存储器
        FileClient client = fileConfigService.getMasterFileClient();
        Assert.notNull(client, "客户端(master) 不能为空");
        String url = client.upload(content, path, type);

        // 保存到数据库
        FileDO file = new FileDO();
        file.setConfigId(client.getId());
        file.setOldName(name);
        file.setName(newName);
        file.setPath(path);
        file.setFileUrl(url);
        file.setFileType(type);
        file.setFileSize(content.length);
        int insert = fileMapper.insert(file);
        FileUploadRespVo fileUploadRespVo = new FileUploadRespVo();
        if (insert > 0) {
            fileUploadRespVo.setId(file.getId());
            fileUploadRespVo.setFileType(contentType);
            fileUploadRespVo.setFileSize(content.length);
            //这里其实没必要 此处标记后期删除
            Map<Long, String> userIdNameCache = adminUserApi.getUserIdNameCache();
            String creator = file.getCreator();
            if (StrUtil.isNotEmpty(creator)) {
                fileUploadRespVo.setUploadUser(userIdNameCache.get(Long.valueOf(creator)));
            }
            fileUploadRespVo.setName(file.getName());
            fileUploadRespVo.setUploadTime(file.getCreateTime());
            fileUploadRespVo.setOldName(file.getOldName());
            fileUploadRespVo.setFileUrl(url);
        }
        return fileUploadRespVo;
    }

    @Override
    public void deleteFile(Long id) throws Exception {
        // 校验存在
        FileDO file = validateFileExists(id);

        // 从文件存储器中删除
        FileClient client = fileConfigService.getFileClient(file.getConfigId());
        Assert.notNull(client, "客户端({}) 不能为空", file.getConfigId());
        client.delete(file.getPath());
        // 删除记录
        fileMapper.deleteById(id);
    }

    private FileDO validateFileExists(Long id) {
        FileDO fileDO = fileMapper.selectById(id);
        if (fileDO == null) {
            throw exception(FILE_NOT_EXISTS);
        }
        return fileDO;
    }

    @Override
    public byte[] getFileContent(Long configId, String path) throws Exception {
        FileClient client = fileConfigService.getFileClient(configId);
        Assert.notNull(client, "客户端({}) 不能为空", configId);
        return client.getContent(path);
    }

    @Override
    public byte[] getFileContent(String path) throws Exception {
        FileClient client = fileConfigService.getMasterFileClient();
        Assert.notNull(client, "主客户端不能为空");
        return client.getContent(path);
    }

    @Override
    public int updateFile(List<Long> idList, Long businessId, Integer businessType) {
        return fileMapper.updateFile(idList, businessId, businessType);
    }

    @Override
    public List<FileRespVO> getFileInfo(Integer businessType, Long businessId) {
        List<FileDO> fileDOList = fileMapper.selectFileListByBusiness(businessType, businessId);
        if (CollUtil.isEmpty(fileDOList)) {
            return null;
        }

        List<FileRespVO> fileRespVOList = FileConvert.INSTANCE.convertFileRespVOList(fileDOList, fileConfigService.getBaseUrl());
        Map<Long, String> userIdNameCache = adminUserApi.getUserIdNameCache();
        fileRespVOList.forEach(fileRespVO -> {
            fileRespVO.setUploadUser(userIdNameCache.get(Long.valueOf(fileRespVO.getCreator())));
            fileRespVO.setUploadTime(fileRespVO.getCreateTime());
        });
        return fileRespVOList;
    }

    /**
     * 根据业务类型跟id批量删除文件
     *
     * @param businessType
     * @param businessId
     * @return 上一版本的文件列表
     */
    @Override
    public void deleteFileList(Integer businessType, Long businessId) {
        fileMapper.delete(new LambdaQueryWrapperX<FileDO>().eq(FileDO::getBusinessType, businessType).eq(FileDO::getBusinessId, businessId));
    }

    @Override
    public void batchDeleteFileList(Integer businessType, List<Long> businessId) {
        fileMapper.delete(new LambdaQueryWrapperX<FileDO>().eq(FileDO::getBusinessType, businessType).in(FileDO::getBusinessId, businessId));
    }

    @Override

    public List<Long> updateBusinessFile(List<Long> idList, Long businessId, Integer businessType) {
        // 查询当前业务单元所有文件列表
        List<FileDO> fileDOList = fileMapper.selectFileListByBusiness(businessType, businessId);
        if (CollUtil.isEmpty(fileDOList)) {
            return idList;
        }
        //去重
        List<Long> oldFileList = fileDOList.stream().map(FileDO::getId).toList();
        fileMapper.deleteBatchIds(oldFileList);
        //如果传入的idList为空 则清空所有附件
        if (CollUtil.isEmpty(idList)) {
            return Collections.emptyList();
        }
        List<Long> updateList = new ArrayList<>();
        // 若当前业务单元库中文件包含新传入的文件则执行先删后增返回自增id(避免主键冲突)
        fileDOList.stream().filter(fileDO -> idList.contains(fileDO.getId())).forEach(fileDO -> {
            fileDO.setId(null);
            fileMapper.insert(fileDO);
            updateList.add(fileDO.getId());
        });
        //若当前业务单元库中文件不包含新传入的文件id 则表示新增文件
        Set<Long> newIdList = idList.stream().filter(id -> !oldFileList.contains(id)).collect(Collectors.toSet());
        updateList.addAll(newIdList);
        return updateList;
    }

    @Override
    public List<FileRespVO> getFileInfoByIds(Integer businessType, List<Long> businessIdList) {
        List<FileDO> fileDOList = fileMapper.selectFileListByBusinessList(businessType, businessIdList);
        if (CollUtil.isEmpty(fileDOList)) {
            return null;
        }
        List<FileRespVO> fileRespVOList = FileConvert.INSTANCE.convertFileRespVOList(fileDOList, fileConfigService.getBaseUrl());
        Map<Long, String> userIdNameCache = adminUserApi.getUserIdNameCache();
        fileRespVOList.forEach(fileRespVO -> {
            fileRespVO.setUploadUser(userIdNameCache.get(Long.valueOf(fileRespVO.getCreator())));
            fileRespVO.setUploadTime(fileRespVO.getCreateTime());
        });
        return fileRespVOList;
    }

    @Override
    public void dealSingleTableFile(List<Long> idList, Long businessId, Integer businessType) {
        List<FileDO> fileDOList = fileMapper.selectFileListByBusiness(businessType, businessId);
        // 不存在挂载则直接更新前台传入的文件id列表
        if (CollUtil.isEmpty(fileDOList) && CollUtil.isNotEmpty(idList)) {
            updateFile(idList, businessId, businessType);
        }
        // 存在于fileDOList但不存在于idList中的id列表
        Set<Long> idsInFileDOList = fileDOList.stream()
                .map(FileDO::getId)
                .collect(Collectors.toSet());
        if (CollUtil.isNotEmpty(idsInFileDOList)) {
            List<Long> idsOnlyInFileDOList = new ArrayList<>(idsInFileDOList);
            if (CollUtil.isNotEmpty(idList)) {
                idsOnlyInFileDOList.removeAll(idList);
            }
            // 库里存在但是前端未传则删除
            if (CollUtil.isNotEmpty(idsOnlyInFileDOList)) {
                fileMapper.deleteBatchIds(idsOnlyInFileDOList);
            }
        }
        // 存在于idList但不存在于fileDOList中的id列表
        if (CollUtil.isNotEmpty(idList)) {
            Set<Long> idsInIdList = new HashSet<>(idList);
            idsInIdList.removeAll(fileDOList.stream()
                    .map(FileDO::getId)
                    .collect(Collectors.toSet()));
            List<Long> idsOnlyInIdList = new ArrayList<>(idsInIdList);

            // 库里不存在但是前端传入则进行挂载
            if (CollUtil.isNotEmpty(idsOnlyInIdList)) {
                updateFile(idsOnlyInIdList, businessId, businessType);
            }
        }
    }

    @Override
    public List<FileUploadRespVo> batchCreateFile(MultipartFile[] files) throws Exception {

        if (files.length == 0) {
            return List.of();
        }
        List<FileUploadRespVo> result = new ArrayList<>();
        for (MultipartFile file : files) {
            result.add(createFile(file.getContentType(), file.getOriginalFilename(), null, IoUtil.readBytes(file.getInputStream())));
        }
        return result;
    }
    
    // ========== 缓存管理相关方法（不在接口中定义，避免影响原框架） ==========
    
    /**
     * 清除所有文件缓存
     */
    public void clearAllFileCache() {
        logger.info("开始清除所有文件缓存");
        try {
            // 只清除主客户端的缓存（通常是SFTP）
            FileClient masterClient = fileConfigService.getMasterFileClient();
            if (masterClient instanceof SftpFileClient sftpClient) {
                sftpClient.clearAllCache();
                logger.info("清除主配置文件缓存完成");
            } else {
                logger.info("主配置不是SFTP客户端，无需清除缓存");
            }
        } catch (Exception e) {
            logger.error("清除所有文件缓存失败", e);
            throw new RuntimeException("清除所有文件缓存失败", e);
        }
    }
    
    /**
     * 清除指定配置的文件缓存
     */
    public void clearConfigCache(Long configId) {
        logger.info("开始清除配置{}的文件缓存", configId);
        try {
            FileClient client = fileConfigService.getFileClient(configId);
            if (client instanceof SftpFileClient sftpClient) {
                sftpClient.clearAllCache();
                logger.info("清除配置{}的文件缓存完成", configId);
            } else {
                logger.info("配置{}不是SFTP客户端，无需清除缓存", configId);
            }
        } catch (Exception e) {
            logger.error("清除配置{}的文件缓存失败", configId, e);
            throw new RuntimeException("清除配置缓存失败", e);
        }
    }
    
    /**
     * 清除指定文件缓存
     */
    public void clearFileCache(Long configId, String path) {
        logger.info("开始清除配置{}文件{}的缓存", configId, path);
        try {
            FileClient client = fileConfigService.getFileClient(configId);
            if (client instanceof SftpFileClient sftpClient) {
                sftpClient.clearFileCache(path);
                logger.info("清除配置{}文件{}的缓存完成", configId, path);
            } else {
                logger.info("配置{}不是SFTP客户端，无需清除缓存", configId);
            }
        } catch (Exception e) {
            logger.error("清除配置{}文件{}的缓存失败", configId, path, e);
            throw new RuntimeException("清除文件缓存失败", e);
        }
    }
    
    /**
     * 获取文件缓存统计信息
     */
    public String getCacheStats(Long configId) {
        try {
            FileClient client = fileConfigService.getFileClient(configId);
            if (client instanceof SftpFileClient sftpClient) {
                return sftpClient.getCacheStats();
            } else {
                return "配置" + configId + "不是SFTP客户端，无缓存统计";
            }
        } catch (Exception e) {
            logger.error("获取配置{}的缓存统计失败", configId, e);
            return "获取缓存统计失败：" + e.getMessage();
        }
    }
    
    /**
     * 获得文件内容（支持强制刷新）
     */
    public byte[] getFileContent(Long configId, String path, boolean forceRefresh) throws Exception {
        FileClient client = fileConfigService.getFileClient(configId);
        Assert.notNull(client, "客户端({}) 不能为空", configId);
        
        // 如果是SFTP客户端且支持强制刷新
        if (client instanceof SftpFileClient sftpClient && forceRefresh) {
            return sftpClient.getContent(path, true);
        }
        
        return client.getContent(path);
    }
}
