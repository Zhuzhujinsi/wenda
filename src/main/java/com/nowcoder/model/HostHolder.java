package com.nowcoder.model;

import org.springframework.stereotype.Component;

//加入Component注解，让上下文都能访问到它！
@Component
public class HostHolder {
    private static ThreadLocal<User> users = new ThreadLocal<>();//”变量副本“或叫作“线程本地变量”

    public User getUser(){
        return users.get();
    }

    public void setUsers(User user){
        users.set(user);
    }

    public void clear(){
        users.remove();
    }
}
