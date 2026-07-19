package com.easyphoto.service;

import com.easyphoto.common.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 图片本地磁盘存储。按 年/月 目录 + UUID 文件名保存，返回可访问 URL。
 */
@Service
public class ImageStorageService {

    private static final List<String> ALLOWED_EXT = Arrays.asList("jpg", "jpeg", "png", "gif", "webp");
    private static final long MAX_SIZE = 10 * 1024 * 1024; // 10MB

    private final String uploadDir;
    private final String urlPrefix;

    public ImageStorageService(@Value("${app.upload-dir}") String uploadDir,
                               @Value("${app.upload-url-prefix}") String urlPrefix) {
        this.uploadDir = uploadDir;
        this.urlPrefix = urlPrefix;
    }

    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("文件为空");
        }
        if (file.getSize() > MAX_SIZE) {
            throw new BusinessException("文件大小超过限制（10MB）");
        }
        String original = file.getOriginalFilename();
        String ext = "";
        if (original != null && original.contains(".")) {
            ext = original.substring(original.lastIndexOf('.') + 1).toLowerCase();
        }
        if (!ALLOWED_EXT.contains(ext)) {
            throw new BusinessException("不支持的图片类型，仅允许 " + ALLOWED_EXT);
        }

        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));
        String filename = UUID.randomUUID().toString().replace("-", "") + "." + ext;
        try {
            Path dir = Paths.get(uploadDir, datePath).toAbsolutePath().normalize();
            Files.createDirectories(dir);
            Path target = dir.resolve(filename);
            file.transferTo(target.toFile());
        } catch (IOException e) {
            throw new BusinessException(500, "文件保存失败: " + e.getMessage());
        }
        // 返回访问 URL：/uploads/yyyy/MM/xxx.ext
        return urlPrefix + datePath + "/" + filename;
    }
}
