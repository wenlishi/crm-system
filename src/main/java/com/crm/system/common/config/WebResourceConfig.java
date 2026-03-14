package com.crm.system.common.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web 资源配置
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
@Configuration
public class WebResourceConfig implements WebMvcConfigurer {

    @Autowired
    private FileUploadProperties uploadProperties;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置静态文件访问（上传的文件）
        registry.addResourceHandler("/api/files/**")
                .addResourceLocations("file:" + uploadProperties.getLocation() + "/");
    }
}
