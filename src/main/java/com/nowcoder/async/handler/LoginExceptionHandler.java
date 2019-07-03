package com.nowcoder.async.handler;

import com.nowcoder.async.EventHandler;
import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventType;
import com.nowcoder.util.MailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class LoginExceptionHandler implements EventHandler {

    @Autowired
    MailSender mailSender;

    @Override
    public void doHandle(EventModel eventModel) {
        //xxxx判断发现这个用户登录异常
        Map<String, Object> map = new HashMap<>();
        map.put("username", eventModel.getExts("username"));
        mailSender.sendWithHTMLTemplate(eventModel.getExts("email"), "登录IP异常", null, map);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LOGIN);
    }
}
