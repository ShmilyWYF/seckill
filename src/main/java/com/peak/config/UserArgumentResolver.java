package com.peak.config;

import com.peak.controller.SysUserController;
import com.peak.controller.UserController;
import com.peak.pojo.TUser;
import com.peak.thread.UserContext;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Component
public class UserArgumentResolver implements HandlerMethodArgumentResolver {


    /**
     * 判断是否需要处理------>
     * 该方法用于判断Controller中方法参数中是否有符合条件的参数：
     * 有则进入下一个方法resolveArgument；
     * 没有则跳过不做处理
     *
     * @param parameter
     * @return null/既定的参数类型
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        List<Method[]> list=new ArrayList<>();
        list.add(UserController.class.getDeclaredMethods());
        list.add(SysUserController.class.getDeclaredMethods());
        String name = parameter.getMethod().getName();
        for (Method[] key : list) {
            for (Method method:key) {
                if(method.getName().equals(name)){
                    return false;
                }
            }
        }
        Class<?> clazz = parameter.getParameterType();
        return clazz == TUser.class;
    }


    /**
     * 需要处理进行的操作----->
     * 该方法在上一个方法同通过之后调用：
     * 在这里可以进行处理，根据情况返回对象——返回的对象将被赋值到Controller的方法的参数中
     *
     * @param parameter
     * @param mavContainer
     * @param webRequest
     * @param binderFactory
     * @return
     * @throws Exception
     */
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        return UserContext.getUser();
    }

}
