package com.howtodoinjava.ibatis.demo.dao;

import com.howtodoinjava.ibatis.demo.dto.UserTEO;
import com.ibatis.sqlmap.client.SqlMapClient;

public interface UserDao {
    public UserTEO addUser(UserTEO user, SqlMapClient sqlmapClient);

    public UserTEO getUserById(Integer id, SqlMapClient sqlmapClient);

    public void deleteUserById(Integer id, SqlMapClient sqlmapClient);
}
