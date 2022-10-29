package com.peak.config;

import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;


@Configuration
public class MyshiroConfiguration {

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(@Qualifier("dwsm") DefaultWebSecurityManager defaultWebSecurityManager) {
        ShiroFilterFactoryBean bean = new ShiroFilterFactoryBean();
        bean.setSecurityManager(defaultWebSecurityManager);
        bean.setLoginUrl("sysuser/tologin");
        Map<String,String> map = new LinkedHashMap<>();
        bean.setFilterChainDefinitionMap(map);
//        Map<String, Filter> filterMap = new HashMap<>();
//        bean.setFilters(filterMap);
        return bean;
    }

    @Bean("dwsm")
    public DefaultWebSecurityManager defaultWebSecurityManager(@Qualifier("userRealm") UserRealm realm) {
        return new DefaultWebSecurityManager(realm);
    }

    @Bean(value = "userRealm")
    public UserRealm userRealm() {
        return new UserRealm();
    }

}
