package com.peak.config;


import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MyDruidConfiguration {
    private final String LOGIN_USERNAME = "admin";
    private final String LOGIN_PASSWORD = "admin";

    @Bean
    public ServletRegistrationBean druidServlet(){
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new StatViewServlet(), "/druid/*");
        Map<String,String> map=new HashMap();
        // 白名单,多个用逗号分割， 如果allow没有配置或者为空，则允许所有访问
        map.put("allow", "");
        // 黑名单,多个用逗号分割 (共同存在时，deny优先于allow)
        map.put("deny","192.168.9.110");
        // 控制台管理用户名
        map.put("loginUsername", LOGIN_USERNAME);
        // 控制台管理密码
        map.put("loginPassword", LOGIN_PASSWORD);
        // 是否可以重置数据源，禁用HTML页面上的“Reset All”功能
        map.put("resetEnable", "false");

        servletRegistrationBean.setInitParameters(map);
        return servletRegistrationBean;
    }


    //配置 Druid 监控 之  web 监控的 filter
    //WebStatFilter：用于配置Web和Druid数据源之间的管理关联监控统计
    @Bean
    public FilterRegistrationBean webStatFilter() {
        FilterRegistrationBean bean = new FilterRegistrationBean();
        bean.setFilter(new WebStatFilter());
        //exclusions：设置哪些请求进行过滤排除掉，从而不进行统计
        Map<String, String> initParams = new HashMap<>();
        initParams.put("exclusions", "*.js,*.css,/druid/*,/jdbc/*");
        bean.setInitParameters(initParams);
        //"/*" 表示过滤所有请求
        bean.setUrlPatterns(Arrays.asList("/*"));
        return bean;
    }


    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource Druiddatasource(){
        return new DruidDataSource();
    }
}
