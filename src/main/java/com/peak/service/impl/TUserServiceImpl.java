package com.peak.service.impl;

import com.peak.exception.GlobalException;
import com.peak.httpUiltr.HttpEnum;
import com.peak.httpUiltr.ResponseResult;
import com.peak.mapper.TUserMapper;
import com.peak.pojo.TUser;
import com.peak.service.TUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.peak.utils.CookieUtil;
import com.peak.utils.MD5util;
import com.peak.utils.UUIDUtil;
import com.peak.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CookieValue;
import org.thymeleaf.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author wyf
 * @since 2022-10-18
 */
@Service
public class TUserServiceImpl<T> extends ServiceImpl<TUserMapper, TUser> implements TUserService {

    @Resource
    private TUserMapper userMapper;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;


    /**
     * <pre> if(StringUtils.isEmpty(mobile)||StringUtils.isEmpty(password)){
     * return ResponseResult.failed(401, "账户或密码为空", "errer");
     * }
     * //校验手机号
     * if(ValidatorUtil.isMobile(mobile)){
     * return ResponseResult.failed(401, "手机号错误或不存在","warning");
     * }
     * </pre>
     **/
    @Override
    public ResponseResult<T> doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response) {
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();

        TUser user = userMapper.selectById(mobile);
        if (null == user) {
            return ResponseResult.ok(HttpEnum.ERROR_401, "账户不存在");
        }
        if (!MD5util.inputPassToDBPass(password, user.getSlat()).equals(user.getPassword())) {
            return ResponseResult.ok(HttpEnum.ERROR_401, "密码错误");
        }
        String ticket = UUIDUtil.getUUID();
        redisTemplate.opsForValue().set("user:" + ticket, user);
        CookieUtil.setCookie(request, response, "userTicket", ticket);
        return ResponseResult.ok(HttpEnum.OK_200, "登录成功", (T) ticket);
    }

    @Override
    public TUser getUserByCookie(String ticket, HttpServletRequest request, HttpServletResponse response) {
        return (TUser) redisTemplate.opsForValue().get("user:" + ticket);
    }

    @Override
    public ResponseResult<HttpEnum> updatePassword(String ticket, String password, HttpServletRequest request, HttpServletResponse response) {
        TUser user = getUserByCookie(ticket, request, response);
        if (user == null) {
            throw new GlobalException(HttpEnum.ERROR_6001);
        }
        user.setPassword(MD5util.inputPassToDBPass(password, user.getSlat()));
        int result = userMapper.updateById(user);
        if (result > 0) {
            redisTemplate.delete("user" + ticket);
            return ResponseResult.ok();
        }
        return ResponseResult.failed(HttpEnum.ERROR_502);
    }


    @Override//添加用户
    public void adduser(TUser user) {
        String uuid = UUIDUtil.getUUID();
        String paw = MD5util.inputPassToDBPass(user.getPassword(), uuid);
        user.setSlat(uuid);
        user.setPassword(paw);
        user.setLastLoginDate(new Date());
        user.setRegisterDate(new Date());
        int insert = userMapper.insert(user);
        if (insert == 0 || insert < 0) {
            throw new GlobalException(HttpEnum.ERROR_402);
        } else {
            throw new GlobalException(HttpEnum.CREATED_201);
        }
    }

    @Override
    public ResponseResult<String> tologout(String ticket) {
        Boolean delete = redisTemplate.delete("user:" + ticket);
        if (!delete){
            return ResponseResult.ok(HttpEnum.ERROR_500,"删除用户缓存失败");
        }
        return ResponseResult.ok(HttpEnum.OK_200,"退出成功，欢迎下次使用！");
    }
}
