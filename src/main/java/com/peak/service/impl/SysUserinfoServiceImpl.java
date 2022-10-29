package com.peak.service.impl;

import com.github.yulichang.base.MPJBaseServiceImpl;
import com.peak.httpUiltr.HttpEnum;
import com.peak.httpUiltr.ResponseResult;
import com.peak.mapper.SysUserinfoMapper;
import com.peak.pojo.SysUserinfo;
import com.peak.service.SysUserinfoService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SysUserinfoServiceImpl extends MPJBaseServiceImpl<SysUserinfoMapper, SysUserinfo> implements SysUserinfoService {

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Override
    public ResponseResult<SysUserinfo> toSysUserinfo(String token) {
        SysUserinfo sysUserinfo = (SysUserinfo) redisTemplate.opsForValue().get("sysUser:" + "token" + ":" + token);
        return ResponseResult.ok(sysUserinfo);
    }

    @Override
    public ResponseResult<String> tologout(String token) {
        Boolean delete = redisTemplate.delete("sysUser:" + "token" + ":" + token);
        if (!delete){
            return ResponseResult.ok(HttpEnum.ERROR_500,"删除用户缓存失败");
        }
        return ResponseResult.ok(HttpEnum.OK_200,"退出成功，欢迎下次使用！");
    }

}
