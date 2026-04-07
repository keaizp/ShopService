package com.yamaha.controller;

import com.yamaha.common.Result;
import com.yamaha.util.CosUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
public class UploadController {

    private final CosUtil cosUtil;

    @PostMapping("/image")
    public Result<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String imagePath = cosUtil.uploadFile(file, "goods");
            return Result.success(imagePath);
        } catch (IOException e) {
            return Result.error("上传失败: " + e.getMessage());
        }
    }
}
