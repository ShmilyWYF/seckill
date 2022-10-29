package com.peak.config;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.peak.pojo.SysUser;
import com.peak.pojo.SysUserinfo;
import com.peak.service.SysUserinfoService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;

import javax.annotation.Resource;

public class UserRealm extends AuthorizingRealm {

    @Resource
    private SysUserinfoService sysUserinfoService;


    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        Subject subject = SecurityUtils.getSubject();
        SysUser sysUser = (SysUser) subject.getPrincipal();
        SysUserinfo userinfo = sysUserinfoService.getOne(new QueryWrapper<SysUserinfo>().eq("user_id", sysUser.getId()));

        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        simpleAuthorizationInfo.addStringPermission(userinfo.getRole());

        return simpleAuthorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) token;

        if (usernamePasswordToken.getUsername() == null) {
            return null;
        }

        return new SimpleAuthenticationInfo(usernamePasswordToken,usernamePasswordToken.getPassword(),"");
    }

}
