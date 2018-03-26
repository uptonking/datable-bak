package io.yaoo.excel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Date utility class
 */
public class DateUtil {
    private DateUtil() {
    }

    public static final long ONE_DAY_MILLISECONDS = 24L * 60 * 60 * 1000;

    /**
     * Format a Date object to String with specified pattern
     *
     * @param date
     * @param pattern
     * @return
     */
    public static final String formatDate(final Date date, final String pattern) {
        return new SimpleDateFormat(pattern).format(date);
    }

    public static boolean isBlank(String str) {
        return null == str ? true : "".equals(str.trim());
    }

    public static final Date parseDate(final String str, final String pattern)
            throws ParseException {
        Date date = null;
        if (!isBlank(str)) {
            date = new SimpleDateFormat(pattern).parse(str);
        }
        return date;
    }

    //判断系统时间是否在有效区间内
    public static Boolean isBetweenTime(Date date_Cur, Date startDate, Date endDate) {
        Boolean isb = false;
        if (null != date_Cur && null != endDate) {
            if (date_Cur.after(startDate) && date_Cur.before(endDate)) {
                isb = true;//区间内生效
            }
        }
        return isb;
    }
}
