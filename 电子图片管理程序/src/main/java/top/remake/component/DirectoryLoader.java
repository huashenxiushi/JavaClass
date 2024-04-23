package top.remake.component;

import javafx.scene.control.TreeItem;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * @author ZeroTwo_CHEN
 * 这段代码定义了一个名为DirectoryLoader的类，它实现了Callable接口，用于异步加载目录。  DirectoryLoader类的主要功能如下：
 * 构造函数接收一个File对象，表示要加载的目录。
 * toFileTreeItem方法：将给定的文件转换为TreeItem对象。如果文件是目录，则创建一个新的FileTreeItem对象，其中包含该目录和一个新的DirectoryLoader实例。如果文件不是目录，则返回null。
 * call方法：这是实现Callable接口必须的方法。它将目录中的所有文件过滤出目录，然后将每个目录转换为TreeItem对象，最后返回一个TreeItem对象的列表。
 * 这个类的主要作用是在JavaFX的TreeView中异步加载目录，提高了用户界面的响应性。
 */
public class DirectoryLoader implements Callable<List<? extends TreeItem<String>>> {

    private final File directory;

    public DirectoryLoader(File directory) {
        this.directory = directory;
    }

    /**
     * 转换为FileTreeItem
     * @param file 待转换的文件
     * @return 结果
     */
    private TreeItem<String> toFileTreeItem(File file) {
        return file.isDirectory()
                ? new FileTreeItem(file, new DirectoryLoader(file))
                : null;
    }

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
