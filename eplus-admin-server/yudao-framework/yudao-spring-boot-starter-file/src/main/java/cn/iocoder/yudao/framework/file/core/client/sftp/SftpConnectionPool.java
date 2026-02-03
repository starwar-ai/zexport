package cn.iocoder.yudao.framework.file.core.client.sftp;

import cn.hutool.core.io.FileUtil;
import cn.hutool.extra.ssh.JschUtil;
import cn.hutool.extra.ssh.Sftp;
import cn.iocoder.yudao.framework.common.util.io.FileUtils;
import com.jcraft.jsch.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * SFTP连接池管理器
 * 解决高并发下SFTP连接失败问题
 * 
 * @author 波波
 */
public class SftpConnectionPool {
    
    private static final Logger logger = LoggerFactory.getLogger(SftpConnectionPool.class);
    
    private final SftpFileClientConfig config;
    private final BlockingQueue<Sftp> connectionPool;
    private final AtomicInteger activeConnections = new AtomicInteger(0);
    private final int maxConnections;
    private final int connectionTimeout;
    private final int readTimeout;
    private final int retryTimes;
    private final long retryInterval;
    
    // 文件缓存
    private final ConcurrentHashMap<String, CachedFile> fileCache;
    private final int maxCacheSize;
    private final long cacheExpireTime;
    private final long cleanupInterval;
    
    public SftpConnectionPool(SftpFileClientConfig config) {
        this.config = config;
        this.maxConnections = 20; // 最大连接数
        this.connectionTimeout = 30000; // 连接超时30秒
        this.readTimeout = 60000; // 读取超时60秒
        this.retryTimes = 3; // 重试3次
        this.retryInterval = 1000; // 重试间隔1秒
        
        // 缓存配置 - 暂时使用默认值，后续可以通过Spring配置注入
        this.maxCacheSize = 1000; // 最大缓存1000个文件
        this.cacheExpireTime = 3600 * 1000; // 缓存1小时
        this.cleanupInterval = 300; // 清理间隔5分钟
        
        this.connectionPool = new LinkedBlockingQueue<>(maxConnections);
        this.fileCache = new ConcurrentHashMap<>();
        
        // 启动清理任务
        startCleanupTask();
        
        logger.info("SFTP连接池初始化完成，缓存配置：最大文件数={}, 过期时间={}秒, 清理间隔={}秒", 
                   maxCacheSize, cacheExpireTime / 1000, cleanupInterval);
    }
    
    /**
     * 获取SFTP连接
     */
    public Sftp getConnection() throws Exception {
        Sftp sftp = connectionPool.poll();
        if (sftp == null) {
            if (activeConnections.get() < maxConnections) {
                sftp = createNewConnection();
                activeConnections.incrementAndGet();
            } else {
                // 等待可用连接
                sftp = connectionPool.poll(connectionTimeout, TimeUnit.MILLISECONDS);
                if (sftp == null) {
                    throw new Exception("SFTP连接池已满，无法获取连接");
                }
            }
        }
        
        // 检查连接是否有效
        if (!isConnectionValid(sftp)) {
            try {
                sftp.close();
            } catch (Exception e) {
                logger.warn("关闭无效连接时出错", e);
            }
            sftp = createNewConnection();
        }
        
        return sftp;
    }
    
    /**
     * 归还SFTP连接
     */
    public void returnConnection(Sftp sftp) {
        if (sftp != null && isConnectionValid(sftp)) {
            connectionPool.offer(sftp);
        } else {
            if (sftp != null) {
                try {
                    sftp.close();
                } catch (Exception e) {
                    logger.warn("关闭连接时出错", e);
                }
            }
            activeConnections.decrementAndGet();
        }
    }
    
    /**
     * 获取文件内容（带缓存）
     */
    public byte[] getFileContent(String path) throws Exception {
        return getFileContent(path, false);
    }
    
    /**
     * 获取文件内容（带缓存，支持强制刷新）
     */
    public byte[] getFileContent(String path, boolean forceRefresh) throws Exception {
        if (forceRefresh) {
            // 强制刷新，先清除缓存
            clearFileCache(path);
            logger.debug("强制刷新文件缓存: {}", path);
        }
        
        // 检查缓存
        CachedFile cachedFile = fileCache.get(path);
        if (cachedFile != null && !cachedFile.isExpired()) {
            logger.debug("从缓存获取文件: {}", path);
            return cachedFile.content();
        }
        
        // 从SFTP下载文件
        byte[] content = downloadFileWithRetry(path);
        
        // 缓存文件
        if (content != null) {
            fileCache.put(path, new CachedFile(content, System.currentTimeMillis()));
            logger.debug("文件已缓存: {}", path);
        }
        
        return content;
    }
    
    /**
     * 清除指定文件的缓存
     */
    public void clearFileCache(String path) {
        CachedFile removed = fileCache.remove(path);
        if (removed != null) {
            logger.info("已清除文件缓存: {}", path);
        } else {
            logger.debug("文件缓存不存在: {}", path);
        }
    }
    
    /**
     * 清除所有缓存
     */
    public void clearAllCache() {
        int size = fileCache.size();
        fileCache.clear();
        logger.info("已清除所有文件缓存，共{}个文件", size);
    }
    
    /**
     * 获取缓存统计信息
     */
    public String getCacheStats() {
        return String.format("缓存统计：当前文件数=%d, 最大文件数=%d, 过期时间=%d秒", 
                           fileCache.size(), maxCacheSize, cacheExpireTime / 1000);
    }
    
    /**
     * 带重试的文件下载
     */
    private byte[] downloadFileWithRetry(String path) throws Exception {
        Exception lastException = null;
        
        for (int i = 0; i < retryTimes; i++) {
            Sftp sftp = null;
            try {
                sftp = getConnection();
                return downloadFile(sftp, path);
            } catch (Exception e) {
                lastException = e;
                logger.warn("第{}次下载文件失败: {}, 错误: {}", i + 1, path, e.getMessage());
                
                if (i < retryTimes - 1) {
                    try {
                        Thread.sleep(retryInterval);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new Exception("下载被中断", ie);
                    }
                }
            } finally {
                if (sftp != null) {
                    returnConnection(sftp);
                }
            }
        }
        
        throw new Exception("下载文件失败，已重试" + retryTimes + "次: " + path, lastException);
    }
    
    /**
     * 下载文件 - 修复版本
     */
    private byte[] downloadFile(Sftp sftp, String path) throws Exception {
        String filePath = config.getBasePath() + "/" + path;
        File destFile = FileUtils.createTempFile();
        try {
            sftp.download(filePath, destFile);
            return FileUtil.readBytes(destFile);
        } finally {
            // 清理临时文件
            if (destFile.exists()) {
                destFile.delete();
            }
        }
    }
    
    /**
     * 创建新连接
     */
    private Sftp createNewConnection() throws Exception {
        Session session = JschUtil.openSession(
            config.getHost(), 
            config.getPort(), 
            config.getUsername(), 
            config.getPassword()
        );
        session.setTimeout(connectionTimeout);
        return new Sftp(session);
    }
    
    /**
     * 检查连接是否有效
     */
    private boolean isConnectionValid(Sftp sftp) {
        try {
            // 尝试执行一个简单的操作来检查连接
            sftp.ls("/");
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 启动清理任务
     */
    private void startCleanupTask() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            try {
                cleanupExpiredCache();
                cleanupIdleConnections();
            } catch (Exception e) {
                logger.error("清理任务执行失败", e);
            }
        }, cleanupInterval, cleanupInterval, TimeUnit.SECONDS); // 使用配置的清理间隔
    }
    
    /**
     * 清理过期缓存
     */
    private void cleanupExpiredCache() {
        long currentTime = System.currentTimeMillis();
        int beforeSize = fileCache.size();
        
        fileCache.entrySet().removeIf(entry -> {
            CachedFile cachedFile = entry.getValue();
            return cachedFile.isExpired(currentTime, cacheExpireTime);
        });
        
        // 如果缓存大小超过限制，删除最旧的缓存
        if (fileCache.size() > maxCacheSize) {
            fileCache.entrySet().stream()
                .sorted((e1, e2) -> Long.compare(e1.getValue().timestamp(), e2.getValue().timestamp()))
                .limit(fileCache.size() - maxCacheSize)
                .forEach(entry -> fileCache.remove(entry.getKey()));
        }
        
        int afterSize = fileCache.size();
        if (beforeSize != afterSize) {
            logger.debug("清理过期缓存完成，清理前：{}个文件，清理后：{}个文件", beforeSize, afterSize);
        }
    }
    
    /**
     * 清理空闲连接
     */
    private void cleanupIdleConnections() {
        // 清理超过最大连接数的连接
        while (connectionPool.size() > maxConnections / 2) {
            Sftp sftp = connectionPool.poll();
            if (sftp != null) {
                try {
                    sftp.close();
                    activeConnections.decrementAndGet();
                } catch (Exception e) {
                    logger.warn("清理空闲连接时出错", e);
                }
            }
        }
    }
    
    /**
     * 关闭连接池
     */
    public void shutdown() {
        // 关闭所有连接
        Sftp sftp;
        while ((sftp = connectionPool.poll()) != null) {
            try {
                sftp.close();
            } catch (Exception e) {
                logger.warn("关闭连接时出错", e);
            }
        }
        activeConnections.set(0);
        fileCache.clear();
        logger.info("SFTP连接池已关闭");
    }

    /**
         * 缓存文件类
         */
        private record CachedFile(byte[] content, long timestamp) {

        public boolean isExpired() {
                return isExpired(System.currentTimeMillis(), 3600 * 1000); // 默认1小时过期
            }

        public boolean isExpired(long currentTime, long expireTime) {
                return currentTime - timestamp > expireTime;
            }
        }
}
