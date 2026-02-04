package com.syj.eplus.framework.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * 图片工具类
 *
 * @author eplus
 */
@Slf4j
public class ImageUtils {

    /**
     * 支持的图片类型
     */
    private static final Set<String> SUPPORTED_CONTENT_TYPES = new HashSet<String>() {{
        add("image/jpeg");
        add("image/png");
        add("image/gif");
        add("image/webp");
        add("image/jpg");
    }};

    /**
     * 获取当前请求的域名
     */
    private static String getCurrentDomain() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String scheme = request.getScheme();
                String serverName = request.getServerName();
                int serverPort = request.getServerPort();
                String contextPath = request.getContextPath();

                StringBuilder url = new StringBuilder();
                url.append(scheme).append("://").append(serverName);

                if (("http".equals(scheme) && serverPort != 80) ||
                    ("https".equals(scheme) && serverPort != 443)) {
                    url.append(":").append(serverPort);
                }

                if (contextPath != null && !contextPath.isEmpty()) {
                    url.append(contextPath);
                }

                return url.toString();
            }
        } catch (Exception e) {
            log.warn("获取当前域名失败", e);
        }
        return "";
    }

    /**
     * 处理图片URL
     */
    private static String processImageUrl(String imageUrl) {
        if (imageUrl.startsWith("http://") || imageUrl.startsWith("https://")) {
            return imageUrl;
        }
        if (!imageUrl.startsWith("/")) {
            imageUrl = "/" + imageUrl;
        }
        String domain = getCurrentDomain();
        if (domain.isEmpty()) {
            return imageUrl;
        }
        return domain + imageUrl;
    }

    /**
     * 验证压缩参数
     */
    private static void validateCompressParams(int width, int height, float quality) {
        if (quality < 0 || quality > 1) {
            log.error("压缩质量参数无效: {}", quality);
            throw new IllegalArgumentException("压缩质量必须在0到1之间");
        }

        if (width <= 0 || height <= 0) {
            log.error("图片尺寸参数无效: {}x{}", width, height);
            throw new IllegalArgumentException("图片宽度和高度必须大于0");
        }
    }

    /**
     * 处理图片压缩和转换
     */
    private static byte[] processImageCompression(BufferedImage originalImage, int width, int height, float quality, String format) throws IOException {
        log.debug("原始图片尺寸: {}x{}", originalImage.getWidth(), originalImage.getHeight());

        // 调整图片大小
        BufferedImage resizedImage = resizeImage(originalImage, width, height);
        log.debug("图片尺寸调整完成");

        // 压缩图片
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        compressImage(resizedImage, outputStream, quality, format);
        log.debug("图片压缩完成");

        // 转换为Base64
        byte[] imageBytes = outputStream.toByteArray();
        log.info("图片处理完成, 压缩后大小: {} bytes", imageBytes.length);
        return imageBytes;
    }

    /**
     * 压缩图片并转换为Base64
     *
     * @param imageUrl 图片URL
     * @param width    目标宽度
     * @param height   目标高度
     * @param quality  压缩质量(0.0-1.0)
     * @return Base64编码的图片
     * @throws IOException IO异常
     */
    public static byte[] compressImageToBase64(String imageUrl, int width, int height, float quality) throws IOException {
        log.info("开始处理图片压缩, URL: {}, 目标尺寸: {}x{}, 压缩质量: {}", imageUrl, width, height, quality);
        
        // 参数校验
        validateCompressParams(width, height, quality);

        // 处理URL
        String fullUrl = processImageUrl(imageUrl);
        log.debug("处理后的完整URL: {}", fullUrl);

        // 从URL读取图片
        URL url;
        try {
            url = new URL(fullUrl);
        } catch (Exception e) {
            log.error("图片URL格式无效: {}", fullUrl, e);
            throw new IllegalArgumentException("无效的图片URL: " + fullUrl);
        }

        URLConnection conn = null;
        try {
            conn = url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            
            // 检查Content-Type
            String contentType = conn.getContentType();
            if (contentType == null || !SUPPORTED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
                log.error("不支持的图片类型: {}", contentType);
                throw new IllegalArgumentException("不支持的图片类型: " + contentType);
            }
            log.debug("图片类型: {}", contentType);
            
            String imageFormat = getImageFormat(contentType);
            
            try (InputStream inputStream = conn.getInputStream()) {
                BufferedImage originalImage = ImageIO.read(inputStream);
                if (originalImage == null) {
                    log.error("无法读取图片内容: {}", fullUrl);
                    throw new IOException("无法读取图片内容: " + fullUrl);
                }

                return processImageCompression(originalImage, width, height, quality, imageFormat);
            }
        } catch (IOException e) {
            log.error("图片处理过程中发生IO异常: {}", fullUrl, e);
            throw new IOException("图片处理失败: " + e.getMessage());
        }
    }

    /**
     * 压缩图片字节数组并转换为Base64
     *
     * @param imageBytes 图片字节数组
     * @param width     目标宽度
     * @param height    目标高度
     * @param quality   压缩质量(0.0-1.0)
     * @param format    图片格式（jpeg, png, gif, webp）
     * @return Base64编码的图片
     * @throws IOException IO异常
     */
    public static byte[] compressImageBytesToBase64(byte[] imageBytes, int width, int height, float quality, String format) throws IOException {
        log.info("开始处理图片压缩, 图片大小: {} bytes, 目标尺寸: {}x{}, 压缩质量: {}, 格式: {}", 
                imageBytes.length, width, height, quality, format);
        
        // 参数校验
        validateCompressParams(width, height, quality);

        if (imageBytes == null || imageBytes.length == 0) {
            log.error("图片数据为空");
            throw new IllegalArgumentException("图片数据不能为空");
        }

        // 验证格式
        String imageFormat = format.toLowerCase();
        if (!SUPPORTED_CONTENT_TYPES.contains("image/" + imageFormat)) {
            log.error("不支持的图片格式: {}", format);
            throw new IllegalArgumentException("不支持的图片格式: " + format);
        }

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes)) {
            BufferedImage originalImage = ImageIO.read(inputStream);
            if (originalImage == null) {
                log.error("无法读取图片内容");
                throw new IOException("无法读取图片内容");
            }

            return processImageCompression(originalImage, width, height, quality, imageFormat);
        } catch (IOException e) {
            log.error("图片处理过程中发生IO异常", e);
            throw new IOException("图片处理失败: " + e.getMessage());
        }
    }

    /**
     * 压缩图片字节数组并转换为Base64（默认JPEG格式）
     */
    public static byte[] compressImageBytesToBase64(byte[] imageBytes, int width, int height, float quality) throws IOException {
        return compressImageBytesToBase64(imageBytes, width, height, quality, "jpeg");
    }

    /**
     * 获取图片格式
     *
     * @param contentType Content-Type
     * @return 图片格式
     */
    private static String getImageFormat(String contentType) {
        switch (contentType.toLowerCase()) {
            case "image/jpeg":
            case "image/jpg":
                return "jpeg";
            case "image/png":
                return "png";
            case "image/gif":
                return "gif";
            case "image/webp":
                return "webp";
            default:
                return "jpeg";
        }
    }

    /**
     * 调整图片大小（支持等比例缩放）
     */
    private static BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        try {
            int originalWidth = originalImage.getWidth();
            int originalHeight = originalImage.getHeight();
            
            // 计算等比例缩放后的宽高
            int newWidth, newHeight;
            double aspectRatio = (double) originalWidth / originalHeight;
            
            if (originalWidth > originalHeight) {
                // 宽图，以宽度为基准进行缩放
                newWidth = targetWidth;
                newHeight = (int) (targetWidth / aspectRatio);
                if (newHeight > targetHeight) {
                    // 如果高度超出目标高度，则以高度为基准重新计算
                    newHeight = targetHeight;
                    newWidth = (int) (targetHeight * aspectRatio);
                }
            } else {
                // 长图或正方形，以高度为基准进行缩放
                newHeight = targetHeight;
                newWidth = (int) (targetHeight * aspectRatio);
                if (newWidth > targetWidth) {
                    // 如果宽度超出目标宽度，则以宽度为基准重新计算
                    newWidth = targetWidth;
                    newHeight = (int) (targetWidth / aspectRatio);
                }
            }
            
            // 确保宽高不为0
            newWidth = Math.max(1, newWidth);
            newHeight = Math.max(1, newHeight);
            
            // 创建带有正确透明度的缩放后图片
            BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, 
                originalImage.getTransparency() == Transparency.TRANSLUCENT ? 
                BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);
                
            Graphics2D graphics2D = resizedImage.createGraphics();
            
            try {
                // 设置图片缩放算法
                graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // 如果是透明背景，设置透明
                if (originalImage.getTransparency() == Transparency.TRANSLUCENT) {
                    graphics2D.setComposite(AlphaComposite.Src);
                }
                
                // 绘制缩放后的图片
                graphics2D.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
                return resizedImage;
            } finally {
                graphics2D.dispose();
            }
        } catch (Exception e) {
            log.error("调整图片大小时发生异常", e);
            throw new RuntimeException("调整图片大小失败: " + e.getMessage());
        }
    }

    /**
     * 压缩图片
     */
    private static void compressImage(BufferedImage image, ByteArrayOutputStream outputStream, float quality, String format) throws IOException {
        ImageWriter writer = null;
        try {
            // 转换颜色空间为 RGB
            BufferedImage convertedImage = image;
//            if (image.getType() != BufferedImage.TYPE_INT_RGB && !format.equalsIgnoreCase("png")) {
//                convertedImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
////                convertedImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
//                Graphics2D g2d = convertedImage.createGraphics();
//                convertedImage = g2d.getDeviceConfiguration().createCompatibleImage(image.getWidth(),image.getHeight(),Transparency.TRANSLUCENT);
//                g2d.dispose();
//                g2d = convertedImage.createGraphics();
//                try {
//                    g2d.drawImage(image, 0, 0, null);
//                } finally {
//                    g2d.dispose();
//                }
//            }
            convertedImage =createWhiteBackgroundImage(image);
            // 获取图片writer
            Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(format);
            if (!writers.hasNext()) {
                log.error("未找到{}图片写入器", format.toUpperCase());
                throw new IllegalStateException("未找到" + format.toUpperCase() + "图片写入器");
            }
            
            writer = writers.next();
            ImageWriteParam param = writer.getDefaultWriteParam();
            
            // 如果格式支持压缩，设置压缩质量
            if (param.canWriteCompressed()) {
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality(quality);
            }
            
            // 写入压缩后的图片
            writer.setOutput(ImageIO.createImageOutputStream(outputStream));
            writer.write(null, new IIOImage(convertedImage, null, null), param);
            
        } catch (IOException e) {
            log.error("压缩图片时发生IO异常", e);
            throw new IOException("压缩图片失败: " + e.getMessage());
        } finally {
            if (writer != null) {
                writer.dispose();
            }
        }
    }

    /**
     * 创建一个白色背景的BufferedImage，并将原始图片绘制到上面
     *
     * @param originalImage 原始图片
     * @return 带有白色背景的新图片
     */
    public static BufferedImage createWhiteBackgroundImage(BufferedImage originalImage) {
        // 创建一个新的BufferedImage，背景为白色
        BufferedImage newImage = new BufferedImage(
                originalImage.getWidth(),
                originalImage.getHeight(),
                BufferedImage.TYPE_INT_RGB
        );
        Graphics2D g2d = newImage.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, newImage.getWidth(), newImage.getHeight()); // 填充白色背景
        g2d.drawImage(originalImage, 0, 0, null); // 绘制原始图片
        g2d.dispose();
        return newImage;
    }
} 