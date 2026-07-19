package com.easyphoto.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.nio.file.Paths;

/**
 * Web 配置：将本地图片上传目录映射为静态资源，供访问 URL 读取；开发期 CORS。
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload-dir}")
    private String uploadDir;

    @Value("${app.upload-url-prefix}")
    private String uploadUrlPrefix;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String absolutePath = Paths.get(uploadDir).toAbsolutePath().normalize().toString();
        String location = "file:" + absolutePath + File.separator;
        registry.addResourceHandler(uploadUrlPrefix + "**")
                .addResourceLocations(location);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
