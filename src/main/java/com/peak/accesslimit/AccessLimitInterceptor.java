package com.peak.accesslimit;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.peak.thread.UserContext;
import com.peak.httpUiltr.HttpEnum;
import com.peak.httpUiltr.ResponseResult;
import com.peak.pojo.TUser;
import com.peak.service.TUserService;
import com.peak.utils.CookieUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.thymeleaf.util.StringUtils;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

@Component
public class AccessLimitInterceptor implements HandlerInterceptor {

    @Resource
    private TUserService userService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod hm = (HandlerMethod) handler;
            AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);
            if (accessLimit == null) {
                return true;
            }
            int second = accessLimit.second();
            int maxCount = accessLimit.maxCount();
            boolean needLogin = accessLimit.needLogin();

            if (needLogin) {
                String ticket = CookieUtil.getCookieValue(request, "userTicket");
                if (StringUtils.isEmpty(ticket)) {
                    response.setHeader("Content-Type","text/html;charset=utf-8");
                    response.getWriter().append(JSON.toJSONString(ResponseResult.failed(HttpEnum.ERROR_6001,"或已失效，请重新登录")));
                    response.getWriter().flush();
                    response.getWriter().close();
                    return false;
                }
                TUser user = userService.getUserByCookie(ticket, request, response);
                UserContext.setUser(user);
                if (user == null) {
                    response.setHeader("Content-Type","text/html;charset=utf-8");
                    response.getWriter().write(new ObjectMapper().writeValueAsString(ResponseResult.failed(HttpEnum.ERROR_6001)));
                    response.getWriter().flush();
                    response.getWriter().close();
                    return false;
                }
                String key = request.getRequestURI();
                key += ":" + user.getId();
                ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
                Integer count = (Integer) valueOperations.get(key);
                if (count == null) {
                    valueOperations.set(key, 1, second, TimeUnit.SECONDS);
                } else if (count < maxCount) {
                    valueOperations.increment(key);
                } else {
                    response.setHeader("Content-Type","text/html;charset=utf-8");
                    response.getWriter().append(JSON.toJSONString(ResponseResult.failed(HttpEnum.ERROR_503,"频繁处理中")));
                    //页面被mvc拦截
                    return false;
                }
            }
        }
        //放行
        return true;
    }

}
