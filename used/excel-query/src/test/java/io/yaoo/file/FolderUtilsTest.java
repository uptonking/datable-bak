package io.yaoo.file;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static io.yaoo.file.folder.FolderUtils.listFolderBFSNonRecursively;
import static io.yaoo.file.folder.FolderUtils.printFilesAsTree;

/**
 * FolderUtils Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>Oct 6, 2017</pre>
 */
public class FolderUtilsTest {

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: listFolderRecursively(String folderPath)
     */
    @Test
    public void testListFolderRecursively() throws Exception {

//        String folderPath = "/root/Documents/dataseed/sample";
//        String folderPath = "/root/Documents/dataseed/lhk";
        String folderPath = "/root/Documents/dataseed/bdp";
//    String folderPath = "/run/media/root/WIN/active/ebook";
//     String folderPath = "/run/media/root/WIN/active/ebook/engineering";

//     traverseFolderRecursively(folderPath);

//     List<String[]> list = listFolderDFSNonRecursively(folderPath);
        List<String[]> list = listFolderBFSNonRecursively(folderPath);
//    list.forEach(item -> System.out.println(item[0] + " - " + item[1] + " - " + item[2] + " - " + item[3]));

        printFilesAsTree(list);

        System.out.println("文件总数为：" + list.size());


    }

}
