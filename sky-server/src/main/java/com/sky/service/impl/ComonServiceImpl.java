package com.sky.service.impl;

import com.sky.service.ComonService;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@Slf4j
public class ComonServiceImpl implements ComonService {
    @Autowired
    private AliOssUtil aliOssUtil;
    /**
     * 文件上传
     *
     * @param file
     * @return
     */
    public String upload(MultipartFile file) {
        log.info("文件上传：{}",file);
        // 获取文件原始名称
        String originalFilename = file.getOriginalFilename();
        // 获取文件类型
        String type = originalFilename.substring(originalFilename.lastIndexOf(".")+1);
        // 组成文件名称
        String fileName = UUID.randomUUID().toString()+"."+type;
        // 上传文件到阿里云
        try {
            String filePath = aliOssUtil.upload(file.getBytes(), fileName);
            return filePath;
        } catch (IOException e) {
            log.error("文件上传失败：{}", e.getMessage());
        }
        return null;
    }
}
