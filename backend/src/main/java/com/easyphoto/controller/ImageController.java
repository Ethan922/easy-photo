package com.easyphoto.controller;

import com.easyphoto.common.Result;
import com.easyphoto.service.ImageStorageService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * 图片上传接口。需登录（Security 默认对非公开路径要求认证）。
 */
@RestController
@RequestMapping("/api/images")
public class ImageController {

    private final ImageStorageService imageStorageService;

    public ImageController(ImageStorageService imageStorageService) {
        this.imageStorageService = imageStorageService;
    }

    @PostMapping("/upload")
    public Result<Map<String, String>> upload(@RequestParam("file") MultipartFile file) {
        String url = imageStorageService.store(file);
        Map<String, String> data = new HashMap<>();
        data.put("url", url);
        return Result.success(data);
    }
}
