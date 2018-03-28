package com.howtodoinjava.ibatis.demo.dao;

import com.howtodoinjava.ibatis.demo.dto.UserDTO;
import com.ibatis.sqlmap.client.SqlMapClient;

public interface UserDao {
    public UserDTO addUser(UserDTO user, SqlMapClient sqlmapClient);

    public UserDTO getUserById(Integer id, SqlMapClient sqlmapClient);

    public void deleteUserById(Integer id, SqlMapClient sqlmapClient);
}
