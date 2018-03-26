package io.yaoo.file;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static java.lang.System.out;

/**
 * Created by yaoooo on 10/8/17.
 */
public class FileSerialization {

    public static void main(String[] args) throws IOException, ClassNotFoundException {

        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("filebean.txt"));

        FileBean fileBean = new FileBean("0","1234","FILE","测试文件名test");

        oos.writeObject(fileBean);

        ObjectInputStream ois = new ObjectInputStream(new FileInputStream("filebean.txt"));
        FileBean fileBean1 = (FileBean)ois.readObject();

        out.println(fileBean1);

    }

}
