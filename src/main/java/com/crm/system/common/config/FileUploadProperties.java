package com.crm.system.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 文件上传配置
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
@Component
@ConfigurationProperties(prefix = "file.upload")
public class FileUploadProperties {

    /**
     * 上传路径
     */
    private String location = "/home/ubuntu/.openclaw/workspace/crm-system/uploads";

    /**
     * 最大文件大小（默认 100MB）
     */
    private long maxSize = 104857600;

    /**
     * 允许的文件扩展名
     */
    private String[] allowedExtensions = new String[]{
        // 图片
        "jpg", "jpeg", "png", "gif", "bmp", "webp",
        // 文档
        "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx",
        // 文本
        "txt", "csv",
        // 压缩文件
        "zip", "rar", "7z"
    };

    /**
     * 访问 URL 前缀
     */
    private String urlPrefix = "/api/files";

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public long getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(long maxSize) {
        this.maxSize = maxSize;
    }

    public String[] getAllowedExtensions() {
        return allowedExtensions;
    }

    public void setAllowedExtensions(String[] allowedExtensions) {
        this.allowedExtensions = allowedExtensions;
    }

    public String getUrlPrefix() {
        return urlPrefix;
    }

    public void setUrlPrefix(String urlPrefix) {
        this.urlPrefix = urlPrefix;
    }
}
