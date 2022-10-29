package com.peak.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import com.peak.pojo.SysUserinfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Mapper
@Repository
@Transactional
public interface SysUserinfoMapper extends MPJBaseMapper<SysUserinfo> {
}
