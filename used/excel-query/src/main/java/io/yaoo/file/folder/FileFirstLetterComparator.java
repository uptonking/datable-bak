package io.yaoo.file.folder;

import io.yaoo.file.FileBean;

import java.util.Comparator;

/**
 * 首字母排序
 * <p>
 * 仅英文
 */
public class FileFirstLetterComparator implements Comparator {


    @Override
    public int compare(Object o1, Object o2) {
        FileBean f1 = (FileBean) o1;
        FileBean f2 = (FileBean) o2;

        return f1.getFileName().compareTo(f2.getFileName());
    }
}
