package com.sky.acpect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 自定义切面
 */
@Aspect
@Component
@Slf4j
public class AutoFillAspect {
    /**
     * 切入点
     */
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut() {
    }

    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint) {
        log.info("开始进行公共字段自动填充...");

        // 获取被拦截的方法的数据库操作类型
        MethodSignature signature = (MethodSignature) joinPoint.getSignature(); // 方法签名对象
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);// 获取方法上的注解对象
        OperationType operationType = autoFill.value(); // 获取数据库操作类型

        // 获取被拦截方法的参数
        Object[] args = joinPoint.getArgs();
        if (args == null && args.length == 0) {
            return;
        }

        Object entity = args[0];

        // 为公共属性赋值
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();
        try {
            if (operationType == OperationType.INSERT) { // 插入操作，为createTime、createUser、updateTime、updateUser赋值


                // 获取当前类中的setCreateTime、setCreateUser、setUpdateTime、setUpdateUser方法
                Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
                // 通过反射调用setCreateTime、setCreateUser、setUpdateTime、setUpdateUser方法
                setCreateTime.invoke(entity, now);
                setCreateUser.invoke(entity, currentId);
                setUpdateTime.invoke(entity, now);
                setUpdateUser.invoke(entity, currentId);

            } else if (operationType == OperationType.UPDATE) { // 更新操作，为updateTime、updateUser赋值
                // 获取当前类中的setUpdateTime、setUpdateUser方法
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
                // 通过反射调用setUpdateTime、setUpdateUser方法
                setUpdateTime.invoke(entity, now);
                setUpdateUser.invoke(entity, currentId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
