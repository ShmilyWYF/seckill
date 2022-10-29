package com.peak.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.peak.httpUiltr.HttpEnum;
import com.peak.httpUiltr.ResponseResult;
import com.peak.pojo.TUser;
import com.peak.vo.LoginVo;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wyf
 * @since 2022-10-18
 */
public interface TUserService extends IService<TUser> {

    public ResponseResult doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response);

    public TUser getUserByCookie(String ticket,HttpServletRequest request,HttpServletResponse response);

    public ResponseResult<HttpEnum> updatePassword(String ticket, String password, HttpServletRequest request, HttpServletResponse response);

    public void adduser(TUser user);

    public ResponseResult<String> tologout(String ticket);
}
