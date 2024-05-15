package imagepreview;

import javafx.scene.control.TreeItem;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * 这是一个目录加载器类，实现了Callable接口。
 * 它用于异步加载目录，并将目录转换为FileTreeItem对象。
 */
public class DirectoryLoader implements Callable<List<? extends TreeItem<String>>> {

    // 目录文件
    private final File directory;

    /**
     * 构造方法，用于创建一个目录加载器对象。
     * @param directory 目录文件
     */
    public DirectoryLoader(File directory) {
        this.directory = directory;
    }

    /**
     * 将文件转换为FileTreeItem对象。
     * 如果文件是目录，则创建一个新的FileTreeItem对象，并使用新的DirectoryLoader对象作为参数。
     * 如果文件不是目录，则返回null。
     * @param file 待转换的文件
     * @return FileTreeItem对象或null
     */
    private TreeItem<String> toFileTreeItem(File file) {
        return file.isDirectory()
                ? new DirectoryTree(file, new DirectoryLoader(file))
                : null;
    }

    /**
     * 实现Callable接口的call方法。
     * 首先，获取目录中的所有文件。
     * 然后，过滤出所有的目录文件。
     * 接着，将每个目录文件转换为FileTreeItem对象。
     * 最后，将所有的FileTreeItem对象收集到一个列表中，并返回。
     * @return FileTreeItem对象的列表
     */
    @Override
    public List<? extends TreeItem<String>> call() {
        return Arrays.stream(directory.listFiles())
                //过滤文件
                .filter(File::isDirectory)
                //转换
                .map(this::toFileTreeItem)
                .collect(Collectors.toList());
    }
}