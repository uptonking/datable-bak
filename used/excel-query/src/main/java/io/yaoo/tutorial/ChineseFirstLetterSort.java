package io.yaoo.tutorial;

import java.text.Collator;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by yaoooo on 10/7/17.
 */
public class ChineseFirstLetterSort {

    public static void main(String[] args) {

        Comparator<Object> com = Collator.getInstance(java.util.Locale.CHINA);
        String[] newArray = {"中山", "汕头", "广州", "安庆", "南京", "武汉", "北京", "安阳"};
        List<String> list = Arrays.asList(newArray);

//        Collections.sort(list, com);

        Collections.sort(list, new Comparator<String>() {

            public int compare(String o1, String o2) {
                Collator collator = Collator.getInstance(java.util.Locale.CHINA);
                return collator.getCollationKey(o1).compareTo(
                        collator.getCollationKey(o2));
            }
        });



        for (String i : list) {
            System.out.println(i + "  ");
        }
    }




}
