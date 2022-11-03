package com.peak.config;

import com.peak.accesslimit.AccessLimitInterceptor;
import com.peak.pojo.TUser;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import java.util.List;

@Configuration
public class MyMVCConfiguration implements WebMvcConfigurer {

    @Resource
    private UserArgumentResolver userArgumentResolver;
    @Resource
    private AccessLimitInterceptor accessLimitInterceptor;

    //参数解析器
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        //对参数配置
       resolvers.add(userArgumentResolver);
    }

    //mvc拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //执行的拦截方法
        registry.addInterceptor(accessLimitInterceptor);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("http://localhost:9527").allowCredentials(true).allowedMethods("*").maxAge(8400);
    }
}
