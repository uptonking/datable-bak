package com.smart.sample.entity;

import com.smart.framework.base.BaseEntity;

/**
 * 数据库User表实体类
 */
public class User extends BaseEntity {

    private String username;

    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
