package io.yaoo.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import static java.lang.System.out;

/**
 * Created by yaoooo on 10/6/17.
 */
public class FileCreate {

    public static void main(String[] args) throws IOException {

        String filePath = "/root/Documents/dataseed/sample/a2.txt";
        String filePath3 = "/root/Documents/dataseed/sample/a3.txt";
        String folderPath = "/root/Documents/dataseed/sample";

        File testFile = new File(filePath);
//        File testFile = new File(folderPath);
//        File testFolder = new File(filePath);


//        out.println(testFile.getName());
//        out.println(testFile.lastModified());
//        out.println(testFile.length());


        ///字节流读取、输出
//        FileInputStream fis = new FileInputStream(filePath);
//        FileOutputStream fos = new FileOutputStream(filePath3);
//        byte[] byteIn = new byte[4];
//        int hasRead = 0;
//
//        while ((hasRead = fis.read(byteIn)) > 0) {
//            out.println(new String(byteIn, 0, hasRead));
//            fos.write(byteIn, 0, hasRead);
//        }
//
//        fis.close();
//        fos.close();

        ///字符流输出
//        FileWriter fw = new FileWriter(filePath3);
//
//        fw.write("hello ");
//        fw.write("输入输出     ");
//        fw.write("这一行有空格\n");
//        fw.write("这一行没空格 接着\r\n");
//        fw.write("再来一行");
//
//        fw.close();

        ///字符流读取
        try (
                FileReader fr = new FileReader(filePath);
                BufferedReader br = new BufferedReader(fr);

        ) {
            char[] charIn = new char[8];
            int hasRead2 = 0;

            String line=null;

//            while ((hasRead2 = fr.read(charIn)) > 0) {
            while ((line = br.readLine()) !=null) {
//                out.println(new String(charIn, 0, hasRead2));
                out.println(line);

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


//        File f1 = new File(".");
//        File f = File.createTempFile("aaa",".txt",f1);
//        out.println(f.getName());
//        out.println(f.getParent());
//        out.println(f.getAbsoluteFile());
//        out.println(f.getAbsoluteFile().getParent());


    }

}
