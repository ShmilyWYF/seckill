package com.peak.controller;

import com.github.yulichang.toolkit.MPJWrappers;
import com.peak.accesslimit.AccessLimit;
import com.peak.dto.SysUserDTO;
import com.peak.exception.GlobalException;
import com.peak.httpUiltr.HttpEnum;
import com.peak.httpUiltr.ResponseResult;
import com.peak.pojo.SysUser;
import com.peak.pojo.SysUserinfo;
import com.peak.rabbitmq.MQsender;
import com.peak.service.SysUserService;
import com.peak.service.SysUserinfoService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author wyf
 * @since 2022-10-27
 */
@CrossOrigin
@RestController
@RequestMapping("/sysuser")
public class SysUserController {

    @Resource
    private SysUserService userService;
    @Resource
    private MQsender mQsender;
    @Resource
    private SysUserinfoService sysUserinfoService;
    @Resource
    private HttpServletRequest request;
    @Resource
    private HttpServletResponse response;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 注册用户
     * @param userDTO 接收前端注册内容
     * @return 结果
     */
    @RequestMapping(value = "/registered", method = RequestMethod.POST)
    public String registered(SysUserDTO userDTO) {
        if (userDTO == null) {
            throw new GlobalException(HttpEnum.ERROR_402, "不能为空");
        }
        SysUserDTO sysUserDTO = userService.selectJoinOne(SysUserDTO.class, MPJWrappers.lambdaJoin().selectAll(SysUser.class).leftJoin(SysUserinfo.class, SysUserinfo::getId, SysUser::getId).eq(SysUser::getUsername, userDTO.getUsername()));
        if (sysUserDTO != null) {
            throw new GlobalException(HttpEnum.ERROR_402, "用户已存在");
        }
        return mQsender.registered_queue(userDTO);
    }


    /**
     *
     * @param user
     * @param token
     * @return
     */
    @AccessLimit(needLogin = false)
    @RequestMapping(value = "/tologin",method = RequestMethod.POST)
    public ResponseResult<String> tologin(SysUser user, @CookieValue(required = false) String token) {
        SysUserinfo userinfo = (SysUserinfo) redisTemplate.opsForValue().get("sysUser:" + "token" + ":" + token);
        if (userinfo != null) {
            throw new GlobalException(HttpEnum.ERROR_401, "请不要重复登录");
        }
        return userService.toLogin(request, response, user);
    }

    @AccessLimit(needLogin = false)
    @RequestMapping(value = "/touserinfo",method = RequestMethod.GET)
    public ResponseResult<SysUserinfo> userinfo(@CookieValue String token) {
        Boolean istoken = redisTemplate.hasKey("sysUser:" + "token" + ":" + token);
        if(!istoken){
            throw new GlobalException(HttpEnum.ERROR_503,"登录信息已过期,请重新登录!");
        }
        return sysUserinfoService.toSysUserinfo(token);
    }

    @AccessLimit(needLogin = false)
    @RequestMapping(value = "/tologout",method = RequestMethod.GET)
    public ResponseResult<String> tologout(@CookieValue String token) {
        Boolean istoken = redisTemplate.hasKey("sysUser:" + "token" + ":" + token);
        if(!istoken){
            throw new GlobalException(HttpEnum.ERROR_503,"用户未登录!");
        }
        return sysUserinfoService.tologout(token);
    }

}
