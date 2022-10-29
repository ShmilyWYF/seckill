package com.peak.service;

import com.github.yulichang.base.MPJBaseService;
import com.peak.httpUiltr.ResponseResult;
import com.peak.pojo.SysUserinfo;

public interface SysUserinfoService extends MPJBaseService<SysUserinfo> {

     ResponseResult<SysUserinfo> toSysUserinfo(String token);

    ResponseResult<String> tologout(String token);
}
