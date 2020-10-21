package com.duing.file.upload;

import lombok.Data;

import java.io.File;
import java.io.Serializable;

@Data
public class FileUploadEntity implements Serializable {

    private static final long serialVersionUID = 8728125605499175055L;

    private File file;
    private String fileName;
    private int fileSize;  // 总大小

    private byte[] bytes; // 具体的字节数组
    private int dataLength; // 本次传输的数据长度

    @Override
    public String toString() {
        return "FileUploadEntity{" +
                "file=" + file +
                ", fileName='" + fileName + '\'' +
                ", fileSize=" + fileSize +
                ", dataLength=" + dataLength +
                '}';
    }
}
