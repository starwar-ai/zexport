package cn.iocoder.yudao.module.infra.controller.admin.file;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.servlet.ServletUtils;
import cn.iocoder.yudao.module.infra.service.file.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.error;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 文件缓存管理")
@RestController
@RequestMapping("/admin-api/infra/file-cache")
@Validated
@Slf4j
public class FileCacheController {

    @Resource
    private FileService fileService;

    // ========== 缓存管理接口 ==========
    
    @DeleteMapping("/clear-all")
    @Operation(summary = "清除所有文件缓存")
    @PreAuthorize("@ss.hasPermission('infra:file:delete')")
    public CommonResult<Boolean> clearAllCache() {
        try {
            fileService.clearAllFileCache();
            return success(true);
        } catch (Exception e) {
            log.error("清除所有文件缓存失败", e);
            return error(500, "清除缓存失败：" + e.getMessage());
        }
    }
    
    @DeleteMapping("/clear-config/{configId}")
    @Operation(summary = "清除指定配置的文件缓存")
    @Parameter(name = "configId", description = "配置编号", required = true)
    @PreAuthorize("@ss.hasPermission('infra:file:delete')")
    public CommonResult<Boolean> clearConfigCache(@PathVariable("configId") Long configId) {
        try {
            fileService.clearConfigCache(configId);
            return success(true);
        } catch (Exception e) {
            log.error("清除配置缓存失败", e);
            return error(500, "清除缓存失败：" + e.getMessage());
        }
    }
    
    @DeleteMapping("/clear-file/{configId}")
    @Operation(summary = "清除指定文件缓存")
    @Parameter(name = "configId", description = "配置编号", required = true)
    @Parameter(name = "path", description = "文件路径", required = true)
    @PreAuthorize("@ss.hasPermission('infra:file:delete')")
    public CommonResult<Boolean> clearFileCache(@PathVariable("configId") Long configId,
                                               @RequestParam("path") String path) {
        try {
            fileService.clearFileCache(configId, path);
            return success(true);
        } catch (Exception e) {
            log.error("清除文件缓存失败", e);
            return error(500, "清除缓存失败：" + e.getMessage());
        }
    }
    
    @GetMapping("/stats/{configId}")
    @Operation(summary = "获取文件缓存统计信息")
    @Parameter(name = "configId", description = "配置编号", required = true)
    @PreAuthorize("@ss.hasPermission('infra:file:query')")
    public CommonResult<String> getCacheStats(@PathVariable("configId") Long configId) {
        try {
            String stats = fileService.getCacheStats(configId);
            return success(stats);
        } catch (Exception e) {
            log.error("获取缓存统计失败", e);
            return error(500, "获取缓存统计失败：" + e.getMessage());
        }
    }
    
    @GetMapping("/{configId}/get-force/**")
    @Operation(summary = "强制刷新并下载文件")
    @Parameter(name = "configId", description = "配置编号", required = true)
    public void getFileContentForce(HttpServletRequest request,
                                   HttpServletResponse response,
                                   @PathVariable("configId") Long configId) throws Exception {
        // 获取请求的路径
        String path = ServletUtils.getRequest().getRequestURI();
        path = path.substring(path.indexOf("/get-force/") + 11);
        if (path.startsWith("/")) {
            path = path.substring(1);
        }

        // 强制刷新并读取内容
        byte[] content = fileService.getFileContent(configId, path, true);
        if (content == null) {
            log.warn("[getFileContentForce][configId({}) path({}) 文件不存在]", configId, path);
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return;
        }
        ServletUtils.writeAttachment(response, path, content);
    }

} 