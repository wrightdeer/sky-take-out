package com.sky.interceptor;

import com.sky.constant.JwtClaimsConstant;
import com.sky.context.BaseContext;
import com.sky.interceptor.utils.JwtRedisUtil;
import com.sky.properties.JwtProperties;
import com.sky.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * jwt令牌校验的拦截器
 */
@Component
@Slf4j
public class JwtTokenAdminInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private JwtRedisUtil jwtRedisUtil;


    /**
     * 校验jwt
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //判断当前拦截到的是Controller的方法还是其他资源
        if (!(handler instanceof HandlerMethod)) {
            //当前拦截到的不是动态方法，直接放行
            return true;
        }


        //1、从请求头中获取令牌
        String token = request.getHeader(jwtProperties.getAdminTokenName());

        // TODO 结合redis，实现更细的校验
        //2、校验令牌
        try {
            log.info("jwt校验:{}", token);
            Claims claims = JwtUtil.parseJWT(jwtProperties.getAdminSecretKey(), token);
            Long empId = Long.valueOf(claims.get(JwtClaimsConstant.EMP_ID).toString());
            // 在redis中进行校验
            String userId = "admin:" + empId;
            if (!jwtRedisUtil.checkJwtInRedis(userId, token)) {
                response.setStatus(401);
                return false;
            }

            // 判断拦截到的是不是退出登录接口
            if (request.getRequestURI().contains("/logout")) {
                // 如果是退出登录接口，则从redis中删除token
                jwtRedisUtil.removeJwtFromRedis(userId, token);
                return true;
            }

            // 判断拦截到的是不是修改密码接口
            if (request.getRequestURI().contains("/editPassword")) {
                // 如果是修改密码接口，则将jwt存入线程
                BaseContext.setCurrentJwt(token);
            }

            log.info("当前员工id：", empId);
            BaseContext.setCurrentId(empId);
            //3、通过，放行
            return true;
        } catch (Exception ex) {
            //4、不通过，响应401状态码
            response.setStatus(401);
            return false;
        }
    }
}
