package com.nowcoder.async.handler;

import com.nowcoder.async.EventHandler;
import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventType;
import com.nowcoder.model.Message;
import com.nowcoder.model.User;
import com.nowcoder.service.MessageService;
import com.nowcoder.service.UserService;
import com.nowcoder.util.WendaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class LikeHandler implements EventHandler {

    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    @Override
    public void doHandle(EventModel eventModel) {
        Message message = new Message();
        message.setFromId(WendaUtil.SYSTEM_USERID);//这个站内信是系统发给指定用户的！
        message.setToId(eventModel.getEntityOwnerId());
        message.setCreatedDate(new Date());
        User user = userService.getUser(eventModel.getActorId());
        message.setContent("用户" + user.getName() + "赞了你的评论, http://127.0.0.1:8080/question/" + eventModel.getExts("questionId"));
        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventTypes() {

        return Arrays.asList(EventType.LIKE);

    }
}
