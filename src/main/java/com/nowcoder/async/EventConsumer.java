package com.nowcoder.async;

import com.alibaba.fastjson.JSON;
import com.nowcoder.util.JedisAdapter;
import com.nowcoder.util.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EventConsumer implements InitializingBean, ApplicationContextAware {
    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);
    private Map<EventType, List<EventHandler>> config = new HashMap<>();//用一个Map实现消息路由，将消息进行分发！
    private ApplicationContext applicationContext;

    @Autowired
    JedisAdapter jedisAdapter;

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, EventHandler> beans = applicationContext.getBeansOfType(EventHandler.class);//找到所有实现了EventHandler接口的类！
        if(beans != null){
            for(Map.Entry<String, EventHandler> entry : beans.entrySet()){
                List<EventType> eventTypes = entry.getValue().getSupportEventTypes();

                for(EventType type : eventTypes){
                    if(!config.containsKey(type)){
                        config.put(type, new ArrayList<EventHandler>());//如果Redis优先队列中没有这种EventType，则将它注册进去。
                    }
                    config.get(type).add(entry.getValue());//将与这种EventType相关的EventHandler添加进去。
                }
            }
        }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    String key = RedisKeyUtil.getEventQueueKey();
                    List<String> events = jedisAdapter.brpop(0, key);

                    for(String message : events){//Event Producer将事件对象序列化成了JSON（文本）
                        if(message.equals(key)){
                            continue;
                        }

                        EventModel eventModel = JSON.parseObject(message, EventModel.class);//将JSON（文本）反序列化为事件对象
                        if(!config.containsKey(eventModel.getType())){
                            logger.error("不能识别的事件");
                            continue;
                        }

                        for(EventHandler handler : config.get(eventModel.getType())){//核心操作，用实现了接口EventHandler的类去具体操作每一个EventType！
                            handler.doHandle(eventModel);
                        }
                    }
                }
            }
        });
        thread.start();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
