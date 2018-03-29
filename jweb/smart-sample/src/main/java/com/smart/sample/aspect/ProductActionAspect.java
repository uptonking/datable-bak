package com.smart.sample.aspect;

import com.smart.framework.annotation.Aspect;
import com.smart.framework.annotation.Bean;
import com.smart.framework.base.BaseAspect;

import java.lang.reflect.Method;

import org.apache.log4j.Logger;

/**
 * 产品操作类切面
 */
@Bean
@Aspect(pkg = "com.smart.sample.action", cls = "ProductAction")
public class ProductActionAspect extends BaseAspect {

    private static final Logger logger = Logger.getLogger(ProductActionAspect.class);

    private long begin;

    @Override
    public boolean filter(Class<?> cls, Method method, Object[] params) {
        String methodName = method.getName();
        return methodName.equals("index");
    }

    @Override
    public void before(Class<?> cls, Method method, Object[] params) {
        if (logger.isDebugEnabled()) {
            logger.debug("---------- Begin ----------");
        }
        begin = System.currentTimeMillis();
    }

    @Override
    public void after(Class<?> cls, Method method, Object[] params) {
        logger.info("Time: " + (System.currentTimeMillis() - begin) + "ms");
        if (logger.isDebugEnabled()) {
            logger.debug("----------- End -----------");
        }
    }

    @Override
    public void error(Class<?> cls, Method method, Object[] params, Exception e) {
        logger.info("Error: " + e);
    }
}
