package com.howtodoinjava.ibatis.demo;

import java.io.Reader;

import com.howtodoinjava.ibatis.demo.dao.UserDao;
import com.howtodoinjava.ibatis.demo.dao.UserDaoIbatis;
import com.howtodoinjava.ibatis.demo.dto.UserDTO;
import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;

public class TestMain {

    public static void main(String[] args) throws Exception {
        UserDao manager = new UserDaoIbatis();

        Reader reader = Resources.getResourceAsReader("sql-maps-config.xml");
        SqlMapClient sqlmapClient = SqlMapClientBuilder.buildSqlMapClient(reader);

        UserDTO user = new UserDTO();
        user.setId(1);
        user.setName("Demo User1");
        user.setPassword("password");
        user.setEmail("demo-user@howtodoinjava.com");
        user.setStatus(1);

        manager.addUser(user, sqlmapClient);

        UserDTO createdUser = manager.getUserById(1, sqlmapClient);
        System.out.println(createdUser.getEmail());

        manager.deleteUserById(1, sqlmapClient);
    }
}
