package com.nowcoder.service;

import com.nowcoder.dao.LoginTicketDAO;
import com.nowcoder.dao.UserDAO;
import com.nowcoder.model.LoginTicket;
import com.nowcoder.model.User;
import com.nowcoder.util.WendaUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    @Autowired
    UserDAO userDAO;

    @Autowired
    private LoginTicketDAO loginTicketDAO;

    public User selectByName(String name) {
        return userDAO.selectByName(name);
    }

    public Map<String, String> register(String username, String password){
       Map<String, String> map = new HashMap<>();
       if(StringUtils.isBlank(username)){
           map.put("msg", "用户名不能为空");
           return map;
       }
       if(StringUtils.isBlank(password)){
           map.put("msg", "密码不能为空");
           return map;
       }

       User user = userDAO.selectByName(username);
       if(user != null){
           map.put("msg", "用户名已经被注册");
           return map;
       }

       user = new User();
       user.setName(username);
       user.setSalt(UUID.randomUUID().toString().substring(0,5));//通过UUID生成唯一的salt（每个用户的salt不同！）
       user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png",
               new Random().nextInt(1000)));
       user.setPassword(WendaUtil.MD5(password + user.getSalt()));
       userDAO.addUser(user);

       String ticket = addLoginTicket(user.getId());//注册完直接下发ticket，注册完直接登录！
       map.put("ticket", ticket);

       return map;
    }

    public Map<String, Object> login(String username, String password){
        Map<String, Object> map = new HashMap<>();
        if(StringUtils.isBlank(username)){
            map.put("msg", "用户名不能为空");
            return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("msg", "密码不能为空");
            return map;
        }

        User user = userDAO.selectByName(username);
        if(user == null){
            map.put("msg", "用户名不存在");
            return map;
        }


        if(!WendaUtil.MD5(password + user.getSalt()).equals(user.getPassword())){
            map.put("msg", "密码错误");
            return map;
        }

        String ticket = addLoginTicket(user.getId());
        map.put("ticket", ticket);
        map.put("userId", user.getId());

        return map;
    }

    public String addLoginTicket(int userId){
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(userId);
        Date now = new Date();
        now.setTime(3600 * 24 * 100 + now.getTime());
        loginTicket.setExpired(now);
        loginTicket.setStatus(0);
        loginTicket.setTicket(UUID.randomUUID().toString().replaceAll("-", ""));
        loginTicketDAO.addTicket(loginTicket);
        return loginTicket.getTicket();
    }

    public void logout(String ticket){
        loginTicketDAO.updateStatus(ticket, 1);
    }

    public User getUser(int id){
        return userDAO.selectById(id);
    }
}
