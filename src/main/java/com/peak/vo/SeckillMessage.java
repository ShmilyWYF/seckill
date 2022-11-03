package com.peak.vo;

import com.peak.pojo.SysUserinfo;
import com.peak.pojo.TUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeckillMessage {
        //前端业务调用
//    private TUser user;
    //前端控制台模拟调用
        private SysUserinfo sysUserinfo;

    private Long goodId;


}
