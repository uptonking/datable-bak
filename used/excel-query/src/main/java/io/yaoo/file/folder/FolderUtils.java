package io.yaoo.file.folder;

import io.yaoo.file.FileBean;
import io.yaoo.util.ChineseCharacterUtil;

import java.io.File;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 遍历文件夹、树形打印
 * <p>
 * todo: 中文拼音首字母排序
 */
public class FolderUtils {

    /**
     * 广度优先非递归遍历所有子文件夹
     * 队列实现
     *
     * @param folderPath
     * @return 列表元素为数组，依次代表父文件夹id，当前id，文件类型，文件名
     */
    public static List<String[]> listFolderBFSNonRecursively(String folderPath) throws Exception {

        List<String[]> fileList = new ArrayList<>();

        final String TYPE1 = "FOLDER";
        final String TYPE2 = "FILE";

        //文件id
        int fileId = 0;

        File fileStart = new File(folderPath);
        if (fileStart == null || fileStart.isFile()) {
            return null;
        }

        Queue<Map<Integer, File>> folderList = new LinkedList<>();

        ///初始文件夹id为0
        Map<Integer, File> idFolderMap;
        idFolderMap = new HashMap<Integer, File>();
        idFolderMap.put(fileId, fileStart);
        folderList.offer(idFolderMap);

        int id;
        File folder = null;

        for (Map<Integer, File> item = folderList.poll(); item != null; item = folderList.poll()) {

            ///取出当前文件夹的id
            Set<Map.Entry<Integer, File>> mapSet = item.entrySet();
            if (mapSet.size() == 1) {
                Map.Entry<Integer, File> entry = (Map.Entry<Integer, File>) mapSet.toArray()[0];
                id = entry.getKey();
                folder = entry.getValue();
            } else {
                throw new Exception("文件信息map不唯一");
            }

            File[] filesInner = folder.listFiles();

            for (File fInner : filesInner) {

                fileId++;

                if (fInner.isDirectory()) {
                    fileList.add(new String[]{String.valueOf(id), String.valueOf(fileId), TYPE1, fInner.getName()});

                    idFolderMap = new HashMap<Integer, File>();
                    idFolderMap.put(fileId, fInner);
                    folderList.offer(idFolderMap);
                } else if (fInner.isFile()) {
                    fileList.add(new String[]{String.valueOf(id), String.valueOf(fileId), TYPE2, fInner.getName()});

                }
            }

        }

        return fileList;
    }


    /**
     * 递归遍历文件夹
     *
     * @param path
     */
    public static void traverseFolderRecursively(String path) {

        File file = new File(path);

        if (file.exists()) {

            File[] files = file.listFiles();

            if (files == null || files.length == 0) {
                System.out.println("文件夹是空的!");
                return;
            } else {
                for (File file2 : files) {
                    if (file2.isDirectory()) {
                        System.out.println("文件夹:" + file2.getAbsolutePath());
                        traverseFolderRecursively(file2.getAbsolutePath());
                    } else {
                        System.out.println("文件:" + file2.getAbsolutePath());
                    }
                }
            }
        } else {
            System.out.println("文件不存在!");
        }
    }

    /**
     * 深度优先非递归遍历所有子文件夹
     * 栈实现
     *
     * @param folderPath
     * @return
     * @throws Exception
     */
    public static List<String[]> listFolderDFSNonRecursively(String folderPath) throws Exception {

        List<String[]> fileList = new ArrayList<>();

        final String TYPE1 = "FOLDER";
        final String TYPE2 = "FILE";

        //文件id
        int fileId = 0;

        File fileStart = new File(folderPath);
        if (fileStart == null || fileStart.isFile()) {
            return null;
        }

        Deque<Map<Integer, File>> folderList = new ArrayDeque<>();

        ///初始文件夹id为0
        Map<Integer, File> idFolderMap;
        idFolderMap = new HashMap<Integer, File>();
        idFolderMap.put(fileId, fileStart);
        folderList.push(idFolderMap);

        int id;
        File folder = null;

        for (Map<Integer, File> item = folderList.pop(); item != null; item = folderList.pop()) {

            ///取出当前文件夹的id
            Set<Map.Entry<Integer, File>> mapSet = item.entrySet();
            if (mapSet.size() == 1) {
                Map.Entry<Integer, File> entry = (Map.Entry<Integer, File>) mapSet.toArray()[0];
                id = entry.getKey();
                folder = entry.getValue();
            } else {
                throw new Exception("文件信息map不唯一");
            }

            File[] filesInner = folder.listFiles();

            for (File fInner : filesInner) {

                fileId++;

                if (fInner.isDirectory()) {
                    fileList.add(new String[]{String.valueOf(id), String.valueOf(fileId), TYPE1, fInner.getName()});

                    idFolderMap = new HashMap<Integer, File>();
                    idFolderMap.put(fileId, fInner);
                    folderList.push(idFolderMap);
                } else if (fInner.isFile()) {
                    fileList.add(new String[]{String.valueOf(id), String.valueOf(fileId), TYPE2, fInner.getName()});

                }
            }

            if (folderList.size() == 0) {
                break;
            }


        }

        return fileList;


    }

    /**
     * 树形结构打印
     *
     * @param list
     */
    public static void printFilesAsTree(List<String[]> list) {

        LinkedList<FileBean> sortedList = new LinkedList<>();

        if (list == null || list.size() == 0) {
            return;
        }

        ///字符串列表转bean列表
        List<FileBean> fileBeanList = new LinkedList<>();
        FileBean fBean;
        for (String[] item : list) {
            fBean = new FileBean(item[0], item[1], item[2], item[3]);
            fileBeanList.add(fBean);
        }

        List<FileBean> tmpList = null;
        List<FileBean> tmpFileList = null;
        List<FileBean> tmpFolderList = null;
        LinkedList<FileBean> chsList = new LinkedList<>();

        FileFirstLetterComparator fComparator = new FileFirstLetterComparator();
        Comparator reverseFileLetter = Collections.reverseOrder(new FileFirstLetterComparator());

        ChineseLetterComparator chsComparator = new ChineseLetterComparator();
        Comparator reverseChsComparator = Collections.reverseOrder(new ChineseLetterComparator());

        LinkedList<FileBean> folderList = new LinkedList<>();

        FileBean item = null;

        do {

            String id = item == null ? "0" : item.getFileId();

            ///取出父目录为id的所有文件和文件夹
            tmpList = fileBeanList.stream()
                    .filter(t -> t.getParentId().equals(id))
                    .collect(Collectors.toList());
            if (tmpList == null) {
                ///空目录跳过
                continue;
            }

            ///取出父目录为id的所有文件
            tmpFileList = tmpList.stream()
                    .filter(t -> t.getFileType().equals("FILE"))
                    .collect(Collectors.toList());
            if (tmpFileList != null) {
                Collections.sort(tmpFileList, fComparator);

                ///文件按中文首字母排序
                LinkedList<Integer> toRemove = new LinkedList<>();
                chsList.clear();
                for (int i = 0, len = tmpFileList.size(); i < len; i++) {
                    if (ChineseCharacterUtil.isChinese(tmpFileList.get(i).getFileName().substring(0, 1))) {
                        chsList.add(tmpFileList.get(i));

                        toRemove.offer(i);

                    }
                }

                if (chsList.size() != 0) {
                    while (toRemove != null && toRemove.size() != 0) {
                        int removeId = toRemove.poll();
                        tmpFileList.remove(removeId);

                        if (toRemove == null || toRemove.size() == 0) {
                            break;
                        } else {
                            for (int i = 0, len = toRemove.size(); i < len; i++) {
                                toRemove.set(i, toRemove.get(i) - 1);
                            }
                        }
                    }


                    Collections.sort(chsList, chsComparator);


                    tmpFileList.addAll(chsList);
                }


                sortedList.addAll(tmpFileList);

            }

            ///取出父目录为id的所有文件夹，用栈实现深度优先
            tmpFolderList = tmpList.stream()
                    .filter(t -> t.getFileType().equals("FOLDER"))
                    .collect(Collectors.toList());
            if (tmpFolderList != null) {

//                Collections.sort(tmpFolderList, fComparator);
//                sortedList.addAll(tmpFolderList);

                Collections.sort(tmpFolderList, reverseFileLetter);

                ///文件夹按中文首字母排序
                LinkedList<Integer> toRemove = new LinkedList<>();
                chsList.clear();
                for (int i = 0, len = tmpFolderList.size(); i < len; i++) {
                    if (ChineseCharacterUtil.isChinese(tmpFolderList.get(i).getFileName().substring(0, 1))) {
                        chsList.add(tmpFolderList.get(i));

                        toRemove.offer(i);

                    }
                }

                if (chsList.size() != 0) {
                    while (toRemove != null && toRemove.size() != 0) {
                        int removeId = toRemove.poll();
                        tmpFolderList.remove(removeId);

                        if (toRemove == null || toRemove.size() == 0) {
                            break;
                        } else {
                            for (int i = 0, len = toRemove.size(); i < len; i++) {
                                toRemove.set(i, toRemove.get(i) - 1);
                            }
                        }
                    }

                    Collections.sort(chsList, reverseChsComparator);

                    chsList.addAll(tmpFolderList);
                    tmpFolderList = chsList;
                }


                for (FileBean f : tmpFolderList) {
                    folderList.push(f);
                }

                if (folderList == null || folderList.size() == 0) {
                    break;
                }


            }


            item = folderList.pop();
            sortedList.add(item);

        } while (item != null);


//        System.out.println(tmpFileList.size());
//        tmpFileList.forEach(System.out::println);
//
//        System.out.println("===============");
//        System.out.println(tmpFolderList.size());
//        tmpFolderList.forEach(System.out::println);

        final String pad3 = "   ";
        final String plus = " + ";


        FileBean lastFile = null;
        FileBean curFile = null;

        String indent = "";

        for (int i = 0, len = sortedList.size(); i < len; i++) {

            curFile = sortedList.get(i);

            if (curFile.getParentId().equals("0")) {
                indent = curFile.getFileType().equals("FILE") ? pad3 : plus;
                lastFile = curFile;
                System.out.println(indent + curFile.getFileName());
                continue;
            }


            if (!curFile.getParentId().equals(lastFile.getParentId())) {


                indent += pad3;
                int length = indent.length();
                char[] trueIndent = new char[length];
                Arrays.fill(trueIndent, ' ');

                if (curFile.getFileType().equals("FOLDER")) {
                    trueIndent[length - 2] = '\u002B';
                }


                indent = new String(trueIndent);

            } else {
                StringBuilder strBuilder = new StringBuilder(indent);
                strBuilder.setCharAt(indent.length() - 2, '\u002B');
                indent = strBuilder.toString();
                if (curFile.getFileType().equals("FILE")) {
                    char[] trueIndent2 = new char[indent.length()];
                    Arrays.fill(trueIndent2, ' ');
                    indent = new String(trueIndent2);
                }
            }

            lastFile = curFile;

            System.out.println(indent + curFile.getFileName());

        }


    }

}
