package com.smart.sample.aspect;

import com.smart.framework.AuthException;
import com.smart.framework.DataContext;
import com.smart.framework.annotation.Aspect;
import com.smart.framework.base.BaseAspect;
import com.smart.sample.entity.User;

import java.lang.reflect.Method;

/**
 * 身份验证类切面
 */
@Aspect(pkg = "com.smart.sample.action")
public class AuthAspect extends BaseAspect {

    @Override
    public boolean filter(Class<?> cls, Method method, Object[] params) {
        String className = cls.getSimpleName();
        String methodName = method.getName();
        return !(
                className.equals("UserAction") &&
                        (methodName.equals("login") || methodName.equals("logout")
                        )
        );
    }

    @Override
    public void before(Class<?> cls, Method method, Object[] params) {
        User user = DataContext.Session.get("user");
        if (user == null) {
            throw new AuthException();
        }
    }
}
