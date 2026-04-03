package com.yamaha.util;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Component
public class CosUtil {
    @Autowired
    private COSClient cosClient;

    @Value("${cos.bucket}")
    private String bucketName;

    @Value("${cos.base-url}")
    private String baseUrl;

    public String uploadFile(MultipartFile file, String folder) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf(".")) : ".png";
        String fileName = UUID.randomUUID().toString() + fileExtension;
        String key = folder + "/" + fileName;

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());

        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, file.getInputStream(), metadata);
        cosClient.putObject(putObjectRequest);

        return key;
    }

    public String getFullImageUrl(String imagePath) {
        return baseUrl + imagePath;
    }
}