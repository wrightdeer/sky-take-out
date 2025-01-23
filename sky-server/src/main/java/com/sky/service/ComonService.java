package com.sky.service;

import org.springframework.web.multipart.MultipartFile;

public interface ComonService {
    String upload(MultipartFile file);
}
