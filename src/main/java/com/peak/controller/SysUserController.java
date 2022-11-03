package com.peak.controller;

import com.github.yulichang.toolkit.MPJWrappers;
import com.peak.accesslimit.AccessLimit;
import com.peak.dto.SysUserDTO;
import com.peak.exception.GlobalException;
import com.peak.httpUiltr.HttpEnum;
import com.peak.httpUiltr.ResponseResult;
import com.peak.pojo.SysUser;
import com.peak.pojo.SysUserinfo;
import com.peak.pojo.TUser;
import com.peak.rabbitmq.MQsender;
import com.peak.service.SysUserService;
import com.peak.service.SysUserinfoService;
import com.peak.service.TGoodsService;
import com.peak.service.TOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

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
    public ResponseResult<String> tologin(@Validated@RequestBody SysUser user, @CookieValue(required = false) String token) {
        SysUserinfo userinfo = (SysUserinfo) redisTemplate.opsForValue().get("sysUser:" + "token" + ":" + token);
        if (userinfo != null) {
            throw new GlobalException(HttpEnum.ERROR_401, "请不要重复登录");
        }
        return userService.toLogin(request, response, user);
    }

    @AccessLimit(needLogin = false)
    @RequestMapping(value = "/touserinfo",method = RequestMethod.GET)
    public ResponseResult<SysUserinfo> userinfo(@CookieValue String token) {
        System.out.println(token);
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

    @AccessLimit(needLogin = false)
    @RequestMapping(value = "/istoken",method = RequestMethod.GET)
    public ResponseResult<String> istoken(@CookieValue(defaultValue = "null") String token) {
        if(Objects.equals(token, "null")){
            return ResponseResult.failed(HttpEnum.ERROR_600,"token不存在");
        }
        Boolean istoken = redisTemplate.hasKey("sysUser:" + "token" + ":" + token);
        if(!istoken){
            return ResponseResult.failed(HttpEnum.OK_200,"token已过期");
        }
        return ResponseResult.ok();
    }

}
