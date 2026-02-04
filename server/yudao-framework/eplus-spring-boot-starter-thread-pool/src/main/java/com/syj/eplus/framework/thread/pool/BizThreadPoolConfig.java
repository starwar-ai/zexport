package com.syj.eplus.framework.thread.pool;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: du
 * @Date: 2024/09/21/17:00
 * @Description:
 */
@AutoConfiguration
@EnableAsync
public class BizThreadPoolConfig {
    // 核心线程数
    private static final int CORE_POOL_SIZE = 2;
    // 最大线程数
    private static final int MAX_POOL_SIZE = 10;
    // 缓冲队列大小
    private static final int QUEUE_CAPACITY = 50;
    // 线程池维护线程所允许的空闲时间
    private static final int KEEP_ALIVE_SECONDS = 180;
    // 线程名称前缀
    private static final String THREAD_NAME_PREFIX = "report-";

    //业务线程池 ，有需要按模块拆分
    @Bean(name = "bizThreadPool")
    public ThreadPoolTaskExecutor bizThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 设置核心线程数,它是可以同时被执行的线程数量
        executor.setCorePoolSize(CORE_POOL_SIZE);
        // 设置最大线程数,缓冲队列满了之后会申请超过核心线程数的线程
        executor.setMaxPoolSize(MAX_POOL_SIZE);
        // 设置缓冲队列容量,在执行任务之前用于保存任务
        executor.setQueueCapacity(QUEUE_CAPACITY);
        // 设置线程生存时间（秒）,当超过了核心线程出之外的线程在生存时间到达之后会被销毁
        executor.setKeepAliveSeconds(KEEP_ALIVE_SECONDS);
        // 设置线程名称前缀
        executor.setThreadNamePrefix(THREAD_NAME_PREFIX);
        // 设置拒绝策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //初始化
//        executor.initialize();
        return executor;
    }
}
