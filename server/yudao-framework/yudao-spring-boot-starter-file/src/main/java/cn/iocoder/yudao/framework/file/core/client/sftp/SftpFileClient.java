package cn.iocoder.yudao.framework.file.core.client.sftp;

import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.extra.ssh.JschUtil;
import cn.hutool.extra.ssh.Sftp;
import cn.iocoder.yudao.framework.common.util.io.FileUtils;
import cn.iocoder.yudao.framework.file.core.client.AbstractFileClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Sftp 文件客户端 - 优化版本
 * 使用连接池和缓存机制，解决高并发连接失败问题
 *
 * @author 芋道源码
 * @author 波波 (优化)
 */
public class SftpFileClient extends AbstractFileClient<SftpFileClientConfig> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private Sftp sftp;
    private SftpConnectionPool connectionPool;
    
    private static final String SFTP_ENABLED_KEY = "yudao.file.sftp.enabled";
    
    /** SFTP功能是否启用 */
    private boolean enabled = true;
    
    // 静态连接池缓存，避免重复创建
    private static final ConcurrentHashMap<String, SftpConnectionPool> POOL_CACHE = new ConcurrentHashMap<>();

    public SftpFileClient(Long id, SftpFileClientConfig config) {
        super(id, config);
    }

    @Override
    protected void doInit() {
        // 检查是否启用SFTP功能
        try {
            Environment env = SpringUtil.getBean(Environment.class);
            String enabledStr = env.getProperty(SFTP_ENABLED_KEY, "true");
            this.enabled = Boolean.parseBoolean(enabledStr);
        } catch (Exception e) {
            logger.warn("无法获取SFTP启用配置，默认启用SFTP功能");
            this.enabled = true;
        }
        
        if (!enabled) {
            logger.warn("SFTP功能已禁用，跳过初始化。如需启用，请设置配置项 {} = true", SFTP_ENABLED_KEY);
            return;
        }
        
        // 补全风格。例如说 Linux 是 /，Windows 是 \
        if (!config.getBasePath().endsWith(File.separator)) {
            config.setBasePath(config.getBasePath() + File.separator);
        }
        
        // 获取或创建连接池
        String poolKey = getPoolKey();
        connectionPool = POOL_CACHE.computeIfAbsent(poolKey, k -> new SftpConnectionPool(config));
        
        // 初始化单个连接（用于上传等操作）
        this.sftp = new Sftp(JschUtil.openSession(config.getHost(), config.getPort(), config.getUsername(), config.getPassword()));
    }

    @Override
    public String upload(byte[] content, String path, String type) {
        if (!enabled) {
            throw new RuntimeException("SFTP功能已禁用，无法上传文件");
        }
        try {
            // 执行写入
            String filePath = getFilePath(path);
            File file = FileUtils.createTempFile(content);
            sftp.mkdir(config.getBasePath());
            //如果目录不存在则创建新目录
            if (!sftp.isDir(config.getBasePath())) {
                sftp.mkdir(config.getBasePath());
            }
            sftp.upload(filePath, file);
            String pathlog = file.getPath();
            logger.info("文件上传成功！" + pathlog);
            
            // 上传成功后清除该文件的缓存
            if (connectionPool != null) {
                connectionPool.clearFileCache(path);
                logger.info("已清除上传文件的缓存: {}", path);
            }
            
            // 拼接返回路径
            return super.formatFileUrl(path);
        } catch (Exception e) {
            logger.error("文件上传失败: {}", path, e);
            throw new RuntimeException("文件上传失败", e);
        }
    }

    @Override
    public void delete(String path) {
        if (!enabled) {
            throw new RuntimeException("SFTP功能已禁用，无法删除文件");
        }
        try {
            String filePath = getFilePath(path);
            sftp.delFile(filePath);
            
            // 删除成功后清除该文件的缓存
            if (connectionPool != null) {
                connectionPool.clearFileCache(path);
                logger.info("已清除删除文件的缓存: {}", path);
            }
            
            logger.info("文件删除成功: {}", path);
        } catch (Exception e) {
            logger.error("文件删除失败: {}", path, e);
            throw new RuntimeException("文件删除失败", e);
        }
    }

    @Override
    public byte[] getContent(String path) {
        if (!enabled) {
            logger.warn("SFTP功能已禁用，无法获取文件内容: {}", path);
            return null;
        }
        try {
            // 使用连接池获取文件内容（带缓存和重试）
            return connectionPool.getFileContent(path);
        } catch (Exception e) {
            logger.error("文件下载失败: {}", path, e);
            return null;
        }
    }
    
    /**
     * 获取文件内容（支持强制刷新）
     */
    public byte[] getContent(String path, boolean forceRefresh) {
        if (!enabled) {
            logger.warn("SFTP功能已禁用，无法获取文件内容: {}", path);
            return null;
        }
        try {
            // 使用连接池获取文件内容（带缓存和重试，支持强制刷新）
            return connectionPool.getFileContent(path, forceRefresh);
        } catch (Exception e) {
            logger.error("文件下载失败: {}", path, e);
            return null;
        }
    }
    
    /**
     * 清除指定文件的缓存
     */
    public void clearFileCache(String path) {
        if (connectionPool != null) {
            connectionPool.clearFileCache(path);
        }
    }
    
    /**
     * 清除所有文件缓存
     */
    public void clearAllCache() {
        if (connectionPool != null) {
            connectionPool.clearAllCache();
        }
    }
    
    /**
     * 获取缓存统计信息
     */
    public String getCacheStats() {
        if (!enabled) {
            return "SFTP功能已禁用";
        }
        if (connectionPool != null) {
            return connectionPool.getCacheStats();
        }
        return "缓存统计：连接池未初始化";
    }
    
    /**
     * 检查SFTP功能是否启用
     */
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public String getBaseUrl() {
        return config.getDomain();
    }

    private String getFilePath(String path) {
        return config.getBasePath() + "\\" + path;
    }
    
    /**
     * 生成连接池的唯一键
     */
    private String getPoolKey() {
        return String.format("%s:%d:%s", 
            config.getHost(), 
            config.getPort(), 
            config.getUsername());
    }
    
    /**
     * 关闭客户端时清理资源
     */
    public void close() {
        if (sftp != null) {
            try {
                sftp.close();
            } catch (Exception e) {
                logger.warn("关闭SFTP连接时出错", e);
            }
        }
    }
}
