package com.peak.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.query.MPJQueryWrapper;
import com.peak.exception.GlobalException;
import com.peak.httpUiltr.HttpEnum;
import com.peak.httpUiltr.ResponseResult;
import com.peak.mapper.SysUserMapper;
import com.peak.pojo.SysUser;
import com.peak.pojo.SysUserinfo;
import com.peak.service.SysUserService;
import com.peak.service.SysUserinfoService;
import com.peak.utils.CookieUtil;
import com.peak.utils.MD5util;
import com.peak.utils.UUIDUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.concurrent.TimeUnit;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author wyf
 * @since 2022-10-27
 */
@Service
@Log4j2
public class SysUserServiceImpl extends MPJBaseServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Resource
    private SysUserMapper sysUserMapper;
    @Resource
    private SysUserinfoService sysUserinfoService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public String adduser(SysUser sysUser, SysUserinfo sysUserinfo) {
        try {
            sysUserMapper.insert(sysUser);
            sysUserinfo.setUserId(sysUser.getId());
            sysUserinfoService.save(sysUserinfo);
        } catch (DataIntegrityViolationException e) {
            return JSON.toJSONString(ResponseResult.failed(HttpEnum.ERROR_503, "注册异常,请刷新重试"));
        }
        return JSON.toJSONString(ResponseResult.ok(HttpEnum.OK_200, "注册成功"));
    }

    @Override
    public ResponseResult<String> toLogin(HttpServletRequest request, HttpServletResponse response, SysUser user) {
        SysUser sysUser = sysUserMapper.selectOne(new MPJQueryWrapper<SysUser>().selectAll(SysUser.class).eq("username", user.getUsername()));
        if (sysUser == null) {
            throw new GlobalException(HttpEnum.ERROR_401, "账号不存在");
        }
        if (!MD5util.inputPassToDBPass(user.getPassword(), sysUser.getSlat()).equals(sysUser.getPassword())) {
            throw new GlobalException(HttpEnum.ERROR_401, "密码错误!");
        }
        SysUserinfo userinfo = sysUserinfoService.getOne(new QueryWrapper<SysUserinfo>().eq("user_id",sysUser.getId()));
        userinfo.setLastLoginDate(new Date());
        userinfo.setLoginCount(userinfo.getLoginCount()+1);
        sysUserinfoService.updateById(userinfo);
        String token = UUIDUtil.getUUID() + sysUser.getId();
        redisTemplate.opsForValue().set("sysUser:" + "token" + ":" + token, userinfo, 8400, TimeUnit.SECONDS);
        CookieUtil.setCookie(request, response, "token", token, 8400, true);
        return ResponseResult.ok(HttpEnum.OK_200, "登录成功",token);
    }

}
