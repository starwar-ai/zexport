package com.syj.eplus.module.crm.indexing;

import cn.hutool.core.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.crm.enums.ErrorCodeConstants.LASTID_IO_FAIL;

@Component
public class CustomerLastIdManager {
    protected final Logger logger = LoggerFactory.getLogger(getClass());


    private final Path lastIdPath;

    private static final String LASTID_PATH = "last_cust_id.txt"; // 文件路径
    private long lastId; // 上次处理的ID

    // 初始化时读取文件中的ID
    public CustomerLastIdManager(@Value("${eplus.index.path}") String indexPath) {
        this.lastIdPath = Paths.get(indexPath, LASTID_PATH);
        readLastIdFromFile();
    }

    // 读取ID
    private void readLastIdFromFile() {
        if (Files.exists(lastIdPath)) {
            try {
                String s = new String(Files.readAllBytes(lastIdPath)).trim();
                if (StrUtil.isEmpty(s)) {
                    lastId = 0;
                }else {
                    lastId = Integer.parseInt(s);
                }
            } catch (IOException e) {
                throw exception(LASTID_IO_FAIL);
            }
        } else {
            lastId = 0; // 初始值
        }
    }

    // 获取上次处理的ID
    public long getLastId() {
        return lastId;
    }

    // 保存新的ID到文件
    public void saveLastId(long newId) {
        lastId = newId;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(lastIdPath.toFile()))) {
            writer.write(String.valueOf(lastId));
        } catch (IOException e) {
            throw exception(LASTID_IO_FAIL);
        }
    }
}