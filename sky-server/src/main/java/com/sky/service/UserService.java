package com.sky.service;

import com.sky.dto.UserLoginDTO;
import com.sky.vo.UserLoginVO;

public interface UserService {
    /**
     * 微信登录
     *
     * @param userLoginDTO 包含微信登录信息的数据传输对象
     * @return 包含用户登录信息的视图对象
     */
    UserLoginVO login(UserLoginDTO userLoginDTO);
}