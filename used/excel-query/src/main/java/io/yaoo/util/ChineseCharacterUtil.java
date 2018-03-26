package io.yaoo.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 中文字符工具类
 */
public class ChineseCharacterUtil {

    /**
     * 判断整个字符串中是否含有中文
     *
     * @param s
     * @return
     */
    public static boolean isChinese(String s) {
        for (int i = 0, len = s.length(); i < len; i++) {
            if (!Pattern.compile("[\u4e00-\u9fa5]").matcher(String.valueOf(s.charAt(i))).find()) {
                return false;
            }
        }

        return true;
    }


    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

}
