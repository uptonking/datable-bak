package com.smart.sample.test;

import com.smart.framework.annotation.Order;
import com.smart.framework.base.BaseTest;
import com.smart.framework.helper.BeanHelper;
import com.smart.sample.entity.User;
import com.smart.sample.service.UserService;
import com.smart.sample.service.impl.UserServiceImpl;
import java.util.HashMap;
import java.util.Map;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class UserServiceTest extends BaseTest {

    private UserService userService = BeanHelper.getBean(UserServiceImpl.class);

    @BeforeClass
    @AfterClass
    public static void init() {
        initSQL("sql/user.sql");
    }

    @Test
    @Order(1)
    public void loginTest() {
        Map<String, Object> fieldMap = new HashMap<String, Object>();
        fieldMap.put("username", "admin");
        fieldMap.put("password", "admin");
        User user = userService.login(fieldMap);
        Assert.assertNotNull(user);
    }
}
