package cn.iocoder.yudao.module.infra.controller.admin.file.vo.file;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Description：
 * @Author：du
 * @Date：2024/1/9 20:14
 */

@Data
public class FileUploadRespVo implements Serializable {

    /**
     * 文件id
     */
    private Long id;
    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 文件名称
     */
    private String name;
    /**
     * 上传用户
     */
    private String uploadUser;
    /**
     * 上传时间
     */
    private LocalDateTime uploadTime;
    /**
     * 文件大小(bit)
     */
    private Integer fileSize;
    /**
     * 下载文件url
     */
    private String fileUrl;

    /**
     * 初始文件名
     */
    private String oldName;
}
