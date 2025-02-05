package com.sky.controller.user;

import com.sky.dto.UserLoginDTO;
import com.sky.result.Result;
import com.sky.service.UserService;
import com.sky.vo.UserLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/user")
@Api(tags = "用户端用户相关接口")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 微信登录
     * @param userLoginDTO 包含微信登录所需信息的数据传输对象
     * @return 包含登录结果信息的Result对象，内部包含UserLoginVO对象
     */
    @PostMapping("/login")
    @ApiOperation(value = "微信登录")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO) {
        log.info("微信登录：{}", userLoginDTO.getCode());
        UserLoginVO userLoginVO = userService.login(userLoginDTO);
        return Result.success(userLoginVO);
    }
}
