package com.howtodoinjava.ibatis.demo;

import java.io.Reader;

import com.howtodoinjava.ibatis.demo.dao.UserDao;
import com.howtodoinjava.ibatis.demo.dao.UserDaoIbatis;
import com.howtodoinjava.ibatis.demo.dto.UserTEO;
import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;

public class TestMain {

    public static void main(String[] args) throws Exception {
        UserDao manager = new UserDaoIbatis();

        Reader reader = Resources.getResourceAsReader("sql-maps-config.xml");
        SqlMapClient sqlmapClient = SqlMapClientBuilder.buildSqlMapClient(reader);

        UserTEO user = new UserTEO();
        user.setId(2);
        user.setName("Demo User2");
        user.setPassword("password");
        user.setEmail("demo-user@howtodoinjava.com");
        user.setStatus(1);

        manager.addUser(user, sqlmapClient);

        UserTEO createdUser = manager.getUserById(1, sqlmapClient);
        System.out.println(createdUser.getEmail());

        manager.deleteUserById(1, sqlmapClient);
    }
}
