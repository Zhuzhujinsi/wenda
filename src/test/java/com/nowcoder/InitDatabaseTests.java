package com.nowcoder;

import com.nowcoder.dao.QuestionDAO;
import com.nowcoder.dao.UserDAO;
import com.nowcoder.model.EntityType;
import com.nowcoder.model.Question;
import com.nowcoder.model.User;
import com.nowcoder.service.FollowService;
import com.nowcoder.service.SensitiveService;
import com.nowcoder.util.JedisAdapter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.Random;

@RunWith(SpringRunner.class)
@SpringBootTest
@Sql("/init-schema.sql")
public class InitDatabaseTests {
	@Autowired
	UserDAO userDAO;

	@Autowired
	QuestionDAO questionDAO;

	@Autowired
	SensitiveService sensitiveService;

	@Autowired
	FollowService followService;

	@Autowired
	JedisAdapter jedisAdapter;

	@Test
	public void initDatabase() {
		Random random = new Random();

		for(int i = 0; i < 11; ++i){
			User user = new User();
			user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png",
					random.nextInt(1000)));
			user.setName(String.format("USER%d", i));
			user.setPassword("");
			user.setSalt("");
			userDAO.addUser(user);

			for(int j = 0; j < i; ++j){
				followService.follow(j, EntityType.ENTITY_USER, i);
			}

			user.setPassword("newpassword");
			userDAO.updatePassword(user);

			Question question = new Question();
			question.setCommentCount(i);
			Date date = new Date();
			date.setTime(date.getTime() + 1000*3600*i);
			question.setCreatedDate(date);
			question.setUserId(i+1);
			question.setTitle(String.format("TITLE{%d}", i));
			question.setContent(String.format("Balalalalalalala Content %d", i));
			questionDAO.addQuestion(question);
		}

		Assert.assertEquals("xx", userDAO.selectById(1).getPassword());
		//userDAO.deleteById(1);
		//Assert.assertNull(userDAO.selectById(1));
	}

	@Test
	public void testSensitive(){
		String content = "question content <img src=\\\"https:\\\\/\\\\/baidu.com/ff.png\\\">色情赌博";
		String result = sensitiveService.filter(content);
		System.out.println(result);
	}

}
