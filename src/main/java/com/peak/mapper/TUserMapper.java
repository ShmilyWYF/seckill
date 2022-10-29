package com.peak.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.peak.httpUiltr.ResponseResult;
import com.peak.pojo.TUser;
import com.peak.vo.LoginVo;
import org.apache.ibatis.annotations.Mapper;


/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wyf
 * @since 2022-10-18
 */
@Mapper
public interface TUserMapper extends BaseMapper<TUser> {


}
