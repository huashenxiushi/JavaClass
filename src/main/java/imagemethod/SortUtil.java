package imagemethod;

import imagepreview.ThumbnailPanel;

import java.text.Collator;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * 这是一个工具类，用于对缩略图面板和图像文件进行排序。
 * 它提供了按名称、时间和大小进行升序和降序排序的功能。
 * 使用了Collator类来进行基于语言环境的字符串比较。
 */
public class SortUtil {
    // 创建一个用于中文字符串比较的Collator实例
    private static final Collator COLLATOR_INSTANCE = Collator.getInstance(Locale.CHINA);

    // 私有构造函数，防止实例化
    private SortUtil() {
    }

    /**
     * 对缩略图面板进行排序
     * @param thumbnailPanels 要排序的缩略图面板列表
     * @param sortOrder 排序方式
     */
    public static void sortThumbnailPanel(List<ThumbnailPanel> thumbnailPanels, String sortOrder) {
        switch (sortOrder) {
            case SortOrder.ASC_SORT_BY_NAME ->
                    thumbnailPanels.sort(((o1, o2) -> COLLATOR_INSTANCE.compare(o1.getImageFile().getFileName(), o2.getImageFile().getFileName())));
            case SortOrder.DESC_SORT_BY_NAME ->
                    thumbnailPanels.sort(((o1, o2) -> COLLATOR_INSTANCE.compare(o2.getImageFile().getFileName(), o1.getImageFile().getFileName())));
            case SortOrder.ASC_SORT_BY_TIME ->
                    thumbnailPanels.sort(Comparator.comparing(o -> o.getImageFile().getCreationTime()));
            case SortOrder.DESC_SORT_BY_TIME ->
                    thumbnailPanels.sort((o1, o2) -> o2.getImageFile().getCreationTime().compareTo(o1.getImageFile().getCreationTime()));
            case SortOrder.ASC_SORT_BY_SIZE ->
                    thumbnailPanels.sort(Comparator.comparingLong(o -> o.getImageFile().getSizeInBytes()));
            case SortOrder.DESC_SORT_BY_SIZE ->
                    thumbnailPanels.sort((o1, o2) -> Long.compare(o2.getImageFile().getSizeInBytes(), o1.getImageFile().getSizeInBytes()));
        }
    }

    /**
     * 对图像文件进行排序
     * @param imageFiles 要排序的图像文件列表
     * @param sortOrder 排序方式
     */
    public static void sortImageFile(List<ImageFile> imageFiles, String sortOrder) {
        switch (sortOrder) {
            case SortOrder.ASC_SORT_BY_NAME ->
                    imageFiles.sort((o1, o2) -> COLLATOR_INSTANCE.compare(o1.getFileName(), o2.getFileName()));
            case SortOrder.DESC_SORT_BY_NAME ->
                    imageFiles.sort((o1, o2) -> COLLATOR_INSTANCE.compare(o2.getFileName(), o1.getFileName()));
            case SortOrder.ASC_SORT_BY_TIME -> imageFiles.sort(Comparator.comparing(ImageFile::getCreationTime));
            case SortOrder.DESC_SORT_BY_TIME ->
                    imageFiles.sort((o1, o2) -> o2.getCreationTime().compareTo(o1.getCreationTime()));
            case SortOrder.ASC_SORT_BY_SIZE -> imageFiles.sort(Comparator.comparingLong(ImageFile::getSizeInBytes));
            case SortOrder.DESC_SORT_BY_SIZE ->
                    imageFiles.sort((o1, o2) -> Long.compare(o2.getSizeInBytes(), o1.getSizeInBytes()));
        }
    }
}