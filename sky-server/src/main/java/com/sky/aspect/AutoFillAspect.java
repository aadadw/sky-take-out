package com.sky.aspect;

import com.sky.annotation.AutoFill;
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

import java.lang.reflect.Method;
import java.time.LocalDateTime;


@Aspect
@Component
@Slf4j
public class AutoFillAspect {
    //切入点
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public  void autoFillPointCut(){}

//    前置通知，在通知中进行公共字段赋值
    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint){
        log.info("开始进行公共字段自动填充。。。。");
        //获取当前被拦截方法上数据库操作类型（就是OperationType里面的UPDATE和INSERT）
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();//方法签名对象
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);//获得方法上的注解对象
        OperationType operationType = autoFill.value();//获得数据库操作类型

        //获取当前被拦截方法的参数--实体对象（Employee或者Dish之类的）
        Object[] args = joinPoint.getArgs();
        if(args == null || args.length ==0){//保险，防止没传参数（不太可能
            return;
        }
        Object entity = args[0];
        //准备赋值的数据
        LocalDateTime now = LocalDateTime.now();
        Long currenId = BaseContext.getCurrentId();
        //根据当前不同的操作类型，为对应的属性通过反射赋值
        if(operationType == OperationType.INSERT){
            //为4个字段赋值
            try {
                Method setCreateTime = entity.getClass().getDeclaredMethod("setCreateTime",LocalDateTime.class);
                Method setCreateUser = entity.getClass().getDeclaredMethod("setCreateUser",Long.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod("setUpdateTime",LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod("setUpdateUser",Long.class);
                //通过反射为两个公共字段赋值
                setCreateTime.invoke(entity,now);
                setCreateUser.invoke(entity,currenId);
                setUpdateTime.invoke(entity,now);
                setUpdateUser.invoke(entity,currenId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (operationType == OperationType.UPDATE) {
            //为两个公共字段赋值
            try {
                Method setUpdateTime = entity.getClass().getDeclaredMethod("setUpdateTime",LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod("setUpdateUser",Long.class);
                //通过反射为两个公共字段赋值
                setUpdateTime.invoke(entity,now);
                setUpdateUser.invoke(entity,currenId);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
