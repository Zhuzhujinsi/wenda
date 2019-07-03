package com.nowcoder.service;

import com.nowcoder.util.JedisAdapter;
import com.nowcoder.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class FollowService {

    @Autowired
    JedisAdapter jedisAdapter;

    /**
     * 用户关注了某个实体，可以关注问题，关注用户，关注评论等任何实体
     * @param userId
     * @param entityType
     * @param entityId
     * @return
     */

    public boolean follow(int userId, int entityType, int entityId){
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        Date date = new Date();

        //定义一个事物，将用户添加到粉丝列表，并且将关注对象加入到用户的关注列表，这两个操作要么同时执行，要么都不执行！
        Jedis jedis = jedisAdapter.getJedis();
        Transaction tx = jedisAdapter.multi(jedis);
        //实体的粉丝增加当前用户
        tx.zadd(followerKey, date.getTime(), String.valueOf(userId));
        //当前用户对这类实体的关注+1
        tx.zadd(followeeKey, date.getTime(), String.valueOf(entityId));
        List<Object> ret = jedisAdapter.exec(tx, jedis);
        return ret.size() == 2 && (long)ret.get(0) > 0 && (long)ret.get(1) > 0;
    }

    /**
     * 取消关注
     * @param userId
     * @param entityType
     * @param entityId
     * @return
     */
    public boolean unFollow(int userId, int entityType, int entityId){
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        Date date = new Date();

        //定义一个事物，将用户从粉丝列表删除，并且将关注对象从用户的关注列表移除，这两个操作要么同时执行，要么都不执行！
        Jedis jedis = jedisAdapter.getJedis();
        Transaction tx = jedisAdapter.multi(jedis);
        tx.zrem(followerKey, String.valueOf(userId));
        tx.zrem(followeeKey, String.valueOf(entityId));
        List<Object> ret = jedisAdapter.exec(tx, jedis);
        return ret.size() == 2 && (long)ret.get(0) > 0 && (long)ret.get(1) > 0;
    }

    //将set<String>转换成List<Integer>的工具函数
    private List<Integer> getIdsFromSet(Set<String> idset){
        List<Integer> ids = new ArrayList<>();
        for(String str : idset){
            ids.add(Integer.parseInt(str));
        }
        return ids;
    }

    //获取粉丝列表
    public List<Integer> getFollowers(int entityType, int entityId, int count){
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return getIdsFromSet(jedisAdapter.zrange(followerKey, 0, count));
    }

    //可以实现分页的粉丝列表
    public List<Integer> getFollowers(int entityType, int entityId,int offset, int count){
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return getIdsFromSet(jedisAdapter.zrange(followerKey, offset, offset + count));
    }

    //获取用户的关注列表
    public List<Integer> getFollowees(int userId, int entityType, int count){
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return getIdsFromSet(jedisAdapter.zrevrange(followeeKey, 0, count));
    }

    //可以实现分页的用户关注列表
    public List<Integer> getFollowees(int userId, int entityType, int offset, int count){
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return getIdsFromSet(jedisAdapter.zrange(followeeKey, offset, offset + count));
    }

    //获取实体的粉丝数目
    public long getFollowerCount(int entityType, int entityId){
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return jedisAdapter.zcard(followerKey);
    }

    //获取用户关注某类实体的数目
    public long getFolloweeCount(int userId, int entityType){
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return jedisAdapter.zcard(followeeKey);
    }

    /**
     * 判断用户是否关注了某个实体
     * @param userId
     * @param entityType
     * @param entityId
     * @return
     */
    public boolean isFollower(int userId, int entityType, int entityId){
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return jedisAdapter.zscore(followerKey, String.valueOf(userId)) != null;
    }

}
