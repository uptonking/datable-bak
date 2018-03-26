package io.yaoo.file;

import java.io.Serializable;

/**
 * Created by yaoooo on 10/7/17.
 */
public class FileBean implements Serializable {
    String parentId;
    String fileId;
    String fileType;
    String fileName;

    public FileBean() {
    }

    public FileBean(String parentId, String fileId, String fileType, String fileName) {
        this.parentId = parentId;
        this.fileId = fileId;
        this.fileType = fileType;
        this.fileName = fileName;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        return "FileBean{" +
                "parentId='" + parentId + '\'' +
                ", fileId='" + fileId + '\'' +
                ", fileType='" + fileType + '\'' +
                ", fileName='" + fileName + '\'' +
                '}';
    }
}
