package com.syj.eplus.module.system.core;

import com.aspose.words.Document;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.util.StopWatch;

public class ReportApplicationRunner implements ApplicationRunner {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    @Override
    public void run(ApplicationArguments args) {
        // 设置压缩率阈值为0.005
        ZipSecureFile.setMinInflateRatio(0.005);
        // 加载打印打印初始化资源
        try {
            StopWatch stopWatch = new StopWatch("加载打印初始化资源");
            stopWatch.start();
            Document nodes = new Document();
            nodes.updatePageLayout();
            stopWatch.stop();
            logger.info("加载打印初始化资源成功,耗时{}ms", stopWatch.getTotalTimeMillis());
        } catch (Exception e) {
            logger.error("加载打印打印初始化资源失败");
        }
    }
}