package com.nowcoder.async;

import java.util.HashMap;
import java.util.Map;

public class EventModel {
    private EventType type;
    private int actorId;
    private int entityType;
    private int entityId;
    private int entityOwnerId;//方便以后给这个对象发站内信！

    public EventModel(){

    }

    public EventModel(EventType eventType){
        this.type = eventType;
    }

    private Map<String, String> exts = new HashMap<>();//扩展字段，比如加入questionId，LikeHandler中使用“eventModel.getExts("questionId")"

    public EventModel setExts(String key, String value){
        exts.put(key, value);
        return this;
    }

    public String getExts(String key){
        return exts.get(key);
    }

    public EventType getType() {
        return type;
    }

    public EventModel setType(EventType type) {
        this.type = type;
        return this;//为了xx.setType().setXX().setXX(),即链式调用方法
    }

    public int getActorId() {
        return actorId;
    }

    public EventModel setActorId(int actorId) {
        this.actorId = actorId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public EventModel setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public EventModel setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityOwnerId() {
        return entityOwnerId;
    }

    public EventModel setEntityOwnerId(int entityOwnerId) {
        this.entityOwnerId = entityOwnerId;
        return this;
    }

    public Map<String, String> getExts() {
        return exts;
    }

    public EventModel setExts(Map<String, String> exts) {
        this.exts = exts;
        return this;
    }
}
