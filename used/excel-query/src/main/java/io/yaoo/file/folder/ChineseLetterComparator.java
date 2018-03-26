package io.yaoo.file.folder;

import io.yaoo.file.FileBean;

import java.text.Collator;
import java.util.Comparator;

/**
 * 首字母排序
 * <p>
 * 仅英文
 */
public class ChineseLetterComparator implements Comparator {


    @Override
    public int compare(Object o1, Object o2) {
//        Comparator<Object> chsComparator = Collator.getInstance(java.util.Locale.CHINA);
        Collator chsCollator = Collator.getInstance(java.util.Locale.CHINA);

        FileBean f1 = (FileBean) o1;
        FileBean f2 = (FileBean) o2;

        return chsCollator.getCollationKey(f1.getFileName()).compareTo(chsCollator.getCollationKey(f2.getFileName()));
    }
}
