package com.smart.sample.service;

import com.smart.sample.entity.User;
import java.util.Map;

/**
 * User表服务接口
 */
public interface UserService {

    User login(Map<String, Object> fieldMap);
}
