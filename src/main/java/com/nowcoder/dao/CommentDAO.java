package com.nowcoder.dao;

import com.nowcoder.model.Comment;
import com.nowcoder.model.Question;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface CommentDAO {
    //注意空格
    String TABLE_NAME = "comment";
    String INSERT_FIELDS = "user_id, content, entity_id, entity_type, created_date, status";
    String SELECT_FIELDS = "id, " + INSERT_FIELDS;

    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS,
            ") values(#{userId},#{content},#{entityId},#{entityType}, #{createdDate}, #{status})"})
    int addComment(Comment comment);

    @Select({"select count(id) from ", TABLE_NAME, " where entity_id = #{entityId} and entity_type = #{entityType} order by createdDate desc"})
    int getCommentCount(@Param("entityId") int entityId, @Param("entityType") int entityType);

    @Select({"select ", INSERT_FIELDS, " from ", TABLE_NAME, " where entity_id = #{entityId} and entity_type = #{entityType}"})
    List<Comment> selectCommentByEntity(@Param("entityId") int entityId, @Param("entityType") int entityType);

    @Update({"update ", TABLE_NAME, " set status = #{status} where id = #{id}"})
    int updateStatus(@Param("id") int id, @Param("status") int status);

    @Select({"select ", SELECT_FIELDS, " from", TABLE_NAME, " where id = #{id}"})
    Comment getCommentById(int id);

    @Select({"select count(id) from ", TABLE_NAME, " where user_id=#{userId}"})
    int getUserCommentCount(int userId);


}
