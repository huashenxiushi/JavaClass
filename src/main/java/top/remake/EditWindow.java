package top.remake;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import top.remake.controller.EditWindowController;

import java.util.Objects;

/**
 * 这是一个编辑窗口类，用于打开和显示编辑窗口。
 * 它设置了编辑窗口的大小、图标，并加载了FXML布局文件。
 * 该类还包含一个main方法，用于启动编辑窗口。
 */
public class EditWindow extends Application {
    // 图片路径
    private static String path;

    /**
     * 主程序入口点
     * @param args 命令行参数，其中包含图片路径
     */
    public static void main(String[] args) {
        // 从命令行参数中获取图片路径
        EditWindow.path = args[0];

        // 检查当前线程是否为JavaFX应用程序线程
        if (Platform.isFxApplicationThread()) {
            // 创建新的舞台
            Stage stage = new Stage();
            // 创建编辑窗口实例
            EditWindow editWindow = new EditWindow();
            try {
                // 启动编辑窗口
                editWindow.start(stage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // 启动JavaFX应用程序
            launch(args);
        }
    }

    /**
     * JavaFX应用程序的启动方法
     * @param stage 主舞台
     * @throws Exception 如果加载FXML文件或设置舞台时出现错误
     */
    @Override
    public void start(Stage stage) throws Exception {
        // 加载FXML布局文件
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/edit-window.fxml"));
        // 创建场景
        Scene scene = new Scene(fxmlLoader.load(), 1100, 700);
        // 获取控制器
        EditWindowController controller = fxmlLoader.getController();
        // 设置窗口的图标
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResource("/image/icon.png")).toExternalForm()));
        // 将场景设置到舞台
        stage.setScene(scene);
        // 显示舞台
        stage.show();
        // 初始化控制器
        if (path != null) {
            controller.init(stage, path);
        } else {
            controller.init(stage, getParameters().getRaw().get(0));
        }
    }
}