package com.smart.framework.util;

import org.apache.commons.lang.ArrayUtils;

/**
 * 数组工具类
 */
public class ArrayUtil {

    // 判断 Array 是否非空
    public static boolean isNotEmpty(Object[] array) {
        return ArrayUtils.isNotEmpty(array);
    }

    // 判断 Array 是否为空
    public static boolean isEmpty(Object[] array) {
        return ArrayUtils.isEmpty(array);
    }
}
