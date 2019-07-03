package com.nowcoder.async.handler;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.async.EventHandler;
import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventType;
import com.nowcoder.model.*;
import com.nowcoder.service.*;
import com.nowcoder.util.JedisAdapter;
import com.nowcoder.util.RedisKeyUtil;
import com.nowcoder.util.WendaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class FeedHandler implements EventHandler {

    @Autowired
    UserService userService;

    @Autowired
    QuestionService questionService;

    @Autowired
    FeedService feedService;

    @Autowired
    FollowService followService;

    @Autowired
    JedisAdapter jedisAdapter;

    private String buildFeedData(EventModel eventModel){
        Map<String, String> map = new HashMap<>();
        User actor = userService.getUser(eventModel.getActorId());
        if(actor == null){
            return null;
        }
        map.put("userId", String.valueOf(actor.getId()));
        map.put("userHead", actor.getHeadUrl());
        map.put("userName", actor.getName());

        if(eventModel.getType() == EventType.COMMENT ||
                (eventModel.getType() == EventType.FOLLOW && eventModel.getEntityType() == EntityType.ENTITY_QUESTION)){
            Question question = questionService.getById(eventModel.getEntityId());
            if(question == null){
                return null;
            }
            map.put("questionId", String.valueOf(question.getId()));
            map.put("questionTitle", question.getTitle());
            return JSONObject.toJSONString(map);
        }
        return null;
    }
    @Override
    public void doHandle(EventModel eventModel) {
        //为了测试，把model的userId随机一下
        Random r = new Random();
        eventModel.setActorId(1 + r.nextInt(10));
        //构建一个新鲜事
        Feed feed = new Feed();
        feed.setCreatedDate(new Date());
        feed.setType(eventModel.getType().getValue());
        feed.setUserId(eventModel.getActorId());
        feed.setData(buildFeedData(eventModel));
        if(feed.getData() == null){
            //不支持的feed
            return;
        }

        feedService.addFeed(feed);

        //获取所有粉丝
        List<Integer> followers = followService.getFollowers(EntityType.ENTITY_USER, eventModel.getActorId(), Integer.MAX_VALUE);
        //系统队列
        followers.add(0);
        //给所有粉丝推事件
        for (Integer follower : followers){
            String timelineKey = RedisKeyUtil.getTimelineKey(follower);
            jedisAdapter.lpush(timelineKey, String.valueOf(feed.getId()));
            //限制最大长度，如果timelineKey的长度过大，就删除后面的新鲜事
        }




    }

    @Override
    public List<EventType> getSupportEventTypes() {

        return Arrays.asList(new EventType[]{EventType.COMMENT, EventType.FOLLOW});

    }
}
