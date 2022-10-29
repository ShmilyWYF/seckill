package com.peak.service;

import com.github.yulichang.base.MPJBaseService;
import com.peak.httpUiltr.ResponseResult;
import com.peak.pojo.SysUser;
import com.peak.pojo.SysUserinfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wyf
 * @since 2022-10-27
 */
public interface SysUserService extends MPJBaseService<SysUser> {

    String adduser(SysUser sysUser, SysUserinfo sysUserinfo);

    ResponseResult<String> toLogin(HttpServletRequest request, HttpServletResponse response, SysUser userinfo);
}
