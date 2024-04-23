package top.remake.entity;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * 图片相关信息的包装类
 *
 * @author ZeroTwo_CHEN
 */
public class ImageFile {
    private static final String DEFAULT_VALUE = "NULL";

    /**
     * 图片的file对象
     */
    private File file;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 绝对路径
     */
    private String absolutePath;

    /**
     * 图片大小 单位为字节
     */
    private long sizeInBytes;

    /**
     * 时间格式
     */
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd, HH:mm:ss");

    /**
     * 文件创建时间
     */
    private String creationTime = DEFAULT_VALUE;

    /**
     * 修改时间
     */
    private String lastModifiedTime = DEFAULT_VALUE;

    /**
     * 访问时间
     */
    private String lastAccessTime = DEFAULT_VALUE;

    public ImageFile(File file) {
        this.file = file;
        this.fileName = file.getName();
        this.fileType = this.fileName.toUpperCase().substring(this.fileName.lastIndexOf(".") + 1);
        this.absolutePath = file.getAbsolutePath();
        this.sizeInBytes = file.length();

        try {
            BasicFileAttributes attrs = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
            LocalDateTime createLocalDateTime = attrs.creationTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            LocalDateTime lastModifiedLocalDateTime = attrs.lastModifiedTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            LocalDateTime lastAccessLocalDateTime = attrs.lastAccessTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            if (createLocalDateTime != null) {
                this.creationTime = DATE_TIME_FORMATTER.format(createLocalDateTime);
                this.lastModifiedTime = DATE_TIME_FORMATTER.format(lastModifiedLocalDateTime);
                this.lastAccessTime = DATE_TIME_FORMATTER.format(lastAccessLocalDateTime);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
        this.fileName = file.getName();
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public long getSizeInBytes() {
        return sizeInBytes;
    }

    public void setSizeInBytes(long sizeInBytes) {
        this.sizeInBytes = sizeInBytes;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public String getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(String lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    public String getLastAccessTime() {
        return lastAccessTime;
    }

    public void setLastAccessTime(String lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }

    public String getURL() {
        return this.file.toPath().toUri().toString();
    }
}
