package com.nowcoder.dao;

import com.nowcoder.model.Comment;
import com.nowcoder.model.Feed;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface FeedDAO {
    //注意空格
    String TABLE_NAME = "feed";
    String INSERT_FIELDS = "created_date, user_id, data, type";
    String SELECT_FIELDS = "id, " + INSERT_FIELDS;

    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS,
            ") values(#{createdDate},#{userId},#{data},#{type})"})
    int addFeed(Feed feed);

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where id = #{id}"})
    Feed getFeedById(int id);

    List<Feed> selectUserFeeds(@Param("maxId") int maxId,//拉取时，最新的feed在页面顶部，最新的feed也是id最大的，每次拉取到maxId
                               @Param("userIds") List<Integer> userIds,//当未登录时，不用这个参数，登录时，用这个参数获取关注的人
                               @Param("count") int count);//count用于分页


}
