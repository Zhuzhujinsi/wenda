package com.nowcoder.dao;

import com.nowcoder.model.Message;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface MessageDAO {
    String TABLE_NAME = "message";
    String INSERT_FIELDS = "from_id, to_id, content, created_date, has_read, conversation_id";
    String SELECT_FIELDS = "id, " + INSERT_FIELDS;

    @Insert({"insert into " , TABLE_NAME, "(", INSERT_FIELDS, ") values (#{fromId}, #{toId}, #{content}, #{createdDate}, #{hasRead}, #{conversationId})"})
    int addMessage(Message message);

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where conversation_id = #{conversationId} order by created_date desc limit #{offset}, #{limit}"})
    List<Message> getConversationDetail(@Param("conversationId") String conversationId,
                                        @Param("offset") int offset,
                                        @Param("limit") int limit);

    @Select({"select from_id, to_id, content, has_read, conversation_id, b.date as created_date, b.id as id from message join (select conversation_id, count(id) as id, max(created_date) as date from message where from_id = #{userId} or to_id = #{userId} group by conversation_id) as b on message.conversation_id = b.conversation_id where b.date = message.created_date order by message.created_date desc limit #{offset}, #{limit}"})//count(id)无法映射到message model中，所以用"count(id) as id"（id在list中没有实际用处）！
    List<Message> getConversationList(@Param("userId") int userId,
                                        @Param("offset") int offset,
                                        @Param("limit") int limit);

    @Select({"select count(id) from ", TABLE_NAME, " where conversation_id = {#conversationId} and has_read = 0 and to_id = #{userId}"})
    int getConversationUnreadCount(@Param("conversationId") String conversationId, @Param("userId") int userId);
}
