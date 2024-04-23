package top.remake.utils;

import top.remake.component.ThumbnailPanel;
import top.remake.entity.ImageFile;
import top.remake.entity.SortOrder;

import java.text.Collator;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * SortUtil类提供了对ThumbnailPanel和ImageFile列表的排序功能
 * @author ZeroTwo_CHEN
 */
public class SortUtil {
    // COLLATOR_INSTANCE用于执行区域设置敏感的String比较
    private static final Collator COLLATOR_INSTANCE = Collator.getInstance(Locale.CHINA);

    // 私有构造函数防止实例化
    private SortUtil() {
    }

    /**
     * 对ThumbnailPanel列表进行排序
     * @param thumbnailPanels 要排序的ThumbnailPanel列表
     * @param sortOrder 排序方式
     */
    public static void sortThumbnailPanel(List<ThumbnailPanel> thumbnailPanels, String sortOrder) {
        switch (sortOrder) {
            case SortOrder.ASC_SORT_BY_NAME ->
                // 按名称升序排序
                    thumbnailPanels.sort(((o1, o2) -> COLLATOR_INSTANCE.compare(o1.getImageFile().getFileName(), o2.getImageFile().getFileName())));
            case SortOrder.DESC_SORT_BY_NAME ->
                // 按名称降序排序
                    thumbnailPanels.sort(((o1, o2) -> COLLATOR_INSTANCE.compare(o2.getImageFile().getFileName(), o1.getImageFile().getFileName())));
            case SortOrder.ASC_SORT_BY_TIME ->
                // 按创建时间升序排序
                    thumbnailPanels.sort(Comparator.comparing(o -> o.getImageFile().getCreationTime()));
            case SortOrder.DESC_SORT_BY_TIME ->
                // 按创建时间降序排序
                    thumbnailPanels.sort((o1, o2) -> o2.getImageFile().getCreationTime().compareTo(o1.getImageFile().getCreationTime()));
            case SortOrder.ASC_SORT_BY_SIZE ->
                // 按大小升序排序
                    thumbnailPanels.sort(Comparator.comparingLong(o -> o.getImageFile().getSizeInBytes()));
            case SortOrder.DESC_SORT_BY_SIZE ->
                // 按大小降序排序
                    thumbnailPanels.sort((o1, o2) -> Long.compare(o2.getImageFile().getSizeInBytes(), o1.getImageFile().getSizeInBytes()));
        }
    }

    /**
     * 对ImageFile列表进行排序
     * @param imageFiles 要排序的ImageFile列表
     * @param sortOrder 排序方式
     */
    public static void sortImageFile(List<ImageFile> imageFiles, String sortOrder) {
        switch (sortOrder) {
            case SortOrder.ASC_SORT_BY_NAME ->
                // 按名称升序排序
                    imageFiles.sort((o1, o2) -> COLLATOR_INSTANCE.compare(o1.getFileName(), o2.getFileName()));
            case SortOrder.DESC_SORT_BY_NAME ->
                // 按名称降序排序
                    imageFiles.sort((o1, o2) -> COLLATOR_INSTANCE.compare(o2.getFileName(), o1.getFileName()));
            case SortOrder.ASC_SORT_BY_TIME ->
                // 按创建时间升序排序
                    imageFiles.sort(Comparator.comparing(ImageFile::getCreationTime));
            case SortOrder.DESC_SORT_BY_TIME ->
                // 按创建时间降序排序
                    imageFiles.sort((o1, o2) -> o2.getCreationTime().compareTo(o1.getCreationTime()));
            case SortOrder.ASC_SORT_BY_SIZE ->
                // 按大小升序排序
                    imageFiles.sort(Comparator.comparingLong(ImageFile::getSizeInBytes));
            case SortOrder.DESC_SORT_BY_SIZE ->
                // 按大小降序排序
                    imageFiles.sort((o1, o2) -> Long.compare(o2.getSizeInBytes(), o1.getSizeInBytes()));
        }
    }
}