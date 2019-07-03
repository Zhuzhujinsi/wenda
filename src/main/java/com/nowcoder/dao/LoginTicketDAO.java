package com.nowcoder.dao;

import com.nowcoder.model.LoginTicket;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface LoginTicketDAO {
    String TABLE_NAME = "login_ticket";
    String INSERT_FIELDS = "user_id, expired, status, ticket";
    String SELECT_FIELDS = "id, " + INSERT_FIELDS;

    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS, ") values (#{userId}, #{expired}, #{status}, #{ticket})"})
    int addTicket(LoginTicket ticket);

    @Select({"select ", SELECT_FIELDS, "from ", TABLE_NAME, "where ticket = #{ticket}"})
    LoginTicket selectByTicket(String ticket);//登录时，从服务器拿出ticket，看看用户在不在

    @Update({"update ", TABLE_NAME, " set status = #{status} where ticket = #{ticket}"})
    void updateStatus(@Param("ticket") String ticket, @Param("status") int status);//登出时，将ticket对应的状态改变（改为1）
}
