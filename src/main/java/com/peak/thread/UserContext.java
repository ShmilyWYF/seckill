package com.peak.thread;

import com.peak.pojo.TUser;

public class UserContext {

    private static ThreadLocal<TUser> userThreadLocal = new ThreadLocal<>();

    public static void setUser(TUser user){
        userThreadLocal.set(user);
    }

    public static TUser getUser(){
        return userThreadLocal.get();
    }

}
