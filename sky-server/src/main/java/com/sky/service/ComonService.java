package com.sky.service;

import org.springframework.web.multipart.MultipartFile;

public interface ComonService {
    /**
     * 文件上传
     * @param file 文件对象
     * @return 文件访问路径
     */
    String upload(MultipartFile file);
}
