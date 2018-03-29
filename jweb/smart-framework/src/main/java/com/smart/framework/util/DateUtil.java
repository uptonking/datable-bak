package com.smart.framework.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 时间日期工具类
 */
public class DateUtil {

    private static final SimpleDateFormat DATETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    // 格式化日期时间
    public static String formatDatetime(long timestamp) {
        return DATETIME_FORMAT.format(new Date(timestamp));
    }
}
