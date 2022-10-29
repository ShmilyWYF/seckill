package com.peak.controller;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSON;
import com.peak.accesslimit.AccessLimit;
import com.peak.exception.GlobalException;
import com.peak.httpUiltr.HttpEnum;
import com.peak.httpUiltr.ResponseResult;
import com.peak.pojo.TUser;
import com.peak.service.TUserService;
import com.peak.vo.LoginVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Objects;

@CrossOrigin
@Controller
@RequestMapping("/login")
@Slf4j
@ResponseBody
public class LoginController {

    @Autowired
    private TUserService userService;
    @Resource
    private HttpServletRequest request;
    @Resource
    private HttpServletResponse response;
    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @SuppressWarnings("all")
    @AccessLimit(needLogin = false)
    @RequestMapping(value = "/toLogin",method = RequestMethod.POST)
    public ResponseResult<HttpEnum> toLogin(LoginVo loginVo,@CookieValue(required = false) String userTicket) {
        TUser bo = (TUser) redisTemplate.opsForValue().get("user:" + userTicket);
        if (bo != null) {
            throw new GlobalException(HttpEnum.ERROR_401, "请不要重复登录");
        }
        return userService.doLogin(loginVo,request,response);
    }

    @AccessLimit(maxCount = 10)
    @RequestMapping(value = "/toList",method = RequestMethod.GET)
    public String toList(HttpServletRequest request, HttpServletResponse response, @CookieValue("userTicket") String ticket){
        if(StringUtils.isEmpty(ticket)){
            //不存在cookie 回到登录页
            return JSON.toJSONString(new ResponseResult<>(HttpEnum.ERROR_600.code(),"你还没有登录，请先登录",HttpEnum.ERROR_600.type()));
        }
        //查找token
        TUser user = userService.getUserByCookie(ticket, request, response);

        //不存在此角色/没有找到此角色
        return JSON.toJSONString(Objects.requireNonNullElseGet(user, () -> new ResponseResult<>(HttpEnum.ERROR_6001)));
    }

    @RequestMapping(value = "/tologout",method = RequestMethod.GET)
    public ResponseResult<String> tologout(HttpServletRequest request, HttpServletResponse response, @CookieValue("userTicket") String ticket){
        if(StringUtils.isEmpty(ticket)){
            //不存在cookie 回到登录页
            return new ResponseResult<>(HttpEnum.ERROR_600.code(),"你还没有登录，请先登录",HttpEnum.ERROR_600.type());
        }
        return userService.tologout(ticket);
    }


    @AccessLimit(needLogin = false)
    @RequestMapping(value = "/updatepsd",method = RequestMethod.POST)
    public ResponseResult<HttpEnum> updatepsd(LoginVo loginVo,@CookieValue String userTicket) {
        TUser bo = (TUser) redisTemplate.opsForValue().get("user:" + userTicket);
        if (bo != null) {
            throw new GlobalException(HttpEnum.ERROR_401, "请不要重复登录");
        }
        return userService.updatePassword(userTicket,loginVo.getPassword(),request,response);
    }

}
