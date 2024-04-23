package top.remake.utils;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.name.Rename;
import top.remake.entity.ImageFile;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * 工具类，提供了一些文件操作的静态方法
 * @author ZeroTwo_CHEN
 */
public class FileUtil {

    // 私有构造函数，防止实例化
    private FileUtil() {
    }

    /**
     * 获取文件系统中的文件名
     * @param file 文件对象
     * @return 文件名
     */
    public static String getFilename(File file) {
        return FileSystemView.getFileSystemView().getSystemDisplayName(file);
    }

    /**
     * 获取文件系统中的文件图标
     * @param file 文件对象
     * @return 文件图标的ImageView对象
     */
    public static ImageView getFileIcon(File file) {
        Image image = ((ImageIcon) FileSystemView.getFileSystemView().getSystemIcon(file)).getImage();
        BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.BITMASK);
        bufferedImage.createGraphics().drawImage(image, 0, 0, null);
        WritableImage writableImage = SwingFXUtils.toFXImage(bufferedImage, null);
        return new ImageView(writableImage);
    }

    /**
     * 判断文件是否是支持的图片格式
     * @param file 文件对象
     * @return 如果文件是支持的图片格式，返回true，否则返回false
     */
    public static boolean isSupportImageFormat(File file) {
        String fileName = file.getName().toUpperCase();
        return fileName.endsWith("JPG") ||
                fileName.endsWith("JPEG") ||
                fileName.endsWith("GIF") ||
                fileName.endsWith("PNG") ||
                fileName.endsWith("BMP");
    }

    /**
     * 删除文件
     * @param file 要删除的文件对象
     */
    public static void delete(File file) {
        Desktop.getDesktop().moveToTrash(file);
    }

    /**
     * 压缩图片
     * @param imageFile 要压缩的图片文件对象
     * @param quality 压缩质量，取值0-1
     * @throws IOException 如果压缩过程中发生错误
     */
    public static void compressImage(ImageFile imageFile, double quality) throws IOException {
        File file = imageFile.getFile();
        if ("PNG".equals(imageFile.getFileType())) {
            Thumbnails.of(file)
                    .scale(1)
                    .outputQuality(quality)
                    .outputFormat("jpg")
                    .toFiles(file.getParentFile(), Rename.PREFIX_HYPHEN_THUMBNAIL);
        }else {
            Thumbnails.of(file)
                    .scale(1)
                    .outputQuality(quality)
                    .toFiles(file.getParentFile(), Rename.PREFIX_HYPHEN_THUMBNAIL);
        }
    }

    // 1024的常量，用于文件大小的计算
    private static final double ONE_ZERO_TWO_FOUR = 1024;

    /**
     * 将文件大小从字节转换为可读的字符串
     * @param sizeInBytes 文件大小，单位为字节
     * @return 文件大小的字符串表示，单位可能为B、KB或MB
     */
    public static String fileSizeByString(long sizeInBytes) {
        String size;
        if (sizeInBytes < ONE_ZERO_TWO_FOUR) {
            size = String.format("%d B", sizeInBytes);
        } else if (sizeInBytes < ONE_ZERO_TWO_FOUR * ONE_ZERO_TWO_FOUR) {
            size = String.format("%.2f KB",  (sizeInBytes / ONE_ZERO_TWO_FOUR));
        } else {
            size = String.format("%.2f MB",  (sizeInBytes / ONE_ZERO_TWO_FOUR / ONE_ZERO_TWO_FOUR));
        }
        return size;
    }
}