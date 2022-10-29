package com.peak.controller;


import com.peak.pojo.TUser;
import com.peak.service.TUserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@CrossOrigin
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private TUserService userService;


    @RequestMapping(value = "/adduser",method = RequestMethod.POST)
    public void adduser(TUser user) throws Exception {
        userService.adduser(user);
    }

}
