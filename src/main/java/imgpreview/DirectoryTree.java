package imgpreview;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import imgmethod.FileUtil;

import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Supplier;

/**
 * 这是一个文件树项目类，继承自TreeItem类。
 * 它用于表示文件夹在树形结构中的节点。
 * 作者是ZeroTwo_CHEN。
 */
public class DirectoryTree extends TreeItem<String> {
    /**
     * 用于标记该item是否初始化过
     */
    private boolean isInitialized = false;

    /**
     * 该节点对应的文件夹
     */
    private final File directory;

    /**
     * 用于异步加载子文件夹
     */
    private CompletableFuture<?> future;

    /**
     * 回调
     */
    private final Callable<List<? extends TreeItem<String>>> callable;

    /**
     * 构造方法，用于创建一个文件树项目对象。
     * @param directory 文件夹
     * @param callable 回调
     */
    public DirectoryTree(File directory, Callable<List<? extends TreeItem<String>>> callable) {
        super(FileUtil.getFilename(directory), FileUtil.getFileIcon(directory));
        this.directory = directory;
        this.callable = callable;
        super.getChildren().add(new TreeItem<>());
        addExpandedListener();
    }

    /**
     * 重写getChildren方法，使得该文件夹在展开时加载其子文件夹
     */
    @Override
    public ObservableList<TreeItem<String>> getChildren() {
        if (!isInitialized) {
            isInitialized = true;
            //开始异步加载
            future = CompletableFuture.supplyAsync(new CallableToSupplierAdapter<>(callable))
                    .whenCompleteAsync(this::handleAsyncLoadComplete, Platform::runLater);
        }
        return super.getChildren();
    }

    /**
     * 重写isLeaf方法，判断该节点是否为叶子节点
     */
    @Override
    public boolean isLeaf() {
        return !directory.isDirectory();
    }

    /**
     * 添加展开监听器
     * 当关闭item时：取消异步加载 并将初始化标记位置为false
     */
    @SuppressWarnings("unchecked")
    private void addExpandedListener() {
        expandedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                isInitialized = false;
                if (future != null) {
                    future.cancel(true);
                }
                //清空其子结点
                super.getChildren().setAll(new TreeItem<>());
            }
        });
    }

    /**
     * 异步加载完成的处理
     * @param result 储存加载完成后的item
     * @param th 异常
     */
    private void handleAsyncLoadComplete(List<? extends TreeItem<String>> result, Throwable th) {
        if (th != null) {
            Thread.currentThread().getUncaughtExceptionHandler()
                    .uncaughtException(Thread.currentThread(), th);
        } else {
            super.getChildren().setAll(result);
        }
        future = null;
    }

    /**
     * 获取该节点对应的文件夹
     * @return 文件夹
     */
    public File getDirectory() {
        return directory;
    }

    /**
     * 将Callable适配为Supplier
     */
    private static class CallableToSupplierAdapter<T> implements Supplier<T> {

        private final Callable<T> callable;

        /**
         * 构造方法，用于创建一个CallableToSupplierAdapter对象。
         * @param callable Callable对象
         */
        private CallableToSupplierAdapter(Callable<T> callable) {
            this.callable = callable;
        }

        /**
         * 实现Supplier接口的get方法。
         * 将Callable接口的call方法的返回值作为get方法的返回值。
         * 如果call方法抛出异常，则将该异常包装为CompletionException并抛出。
         * @return call方法的返回值
         */
        @Override
        public T get() {
            try {
                return callable.call();
            } catch (Exception ex) {
                throw new CompletionException(ex);
            }
        }

    }
}
