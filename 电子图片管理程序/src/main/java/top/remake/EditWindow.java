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
 * EditWindow类是JavaFX应用程序的一个窗口，用于编辑图片
 * @author ZeroTwo_CHEN
 */
public class EditWindow extends Application {
    // 图片的路径
    private static String path;

    // main方法是Java程序的入口点
    public static void main(String[] args) {
        // 获取图片的路径
        EditWindow.path = args[0];

        // 检查当前线程是否是JavaFX Application Thread
        if (Platform.isFxApplicationThread()) {
            // 创建一个新的Stage
            Stage stage = new Stage();
            EditWindow editWindow = new EditWindow();
            try {
                // 启动EditWindow
                editWindow.start(stage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // 启动JavaFX应用程序
            launch(args);
        }
    }

    // start方法是JavaFX应用程序的主要入口点
    @Override
    public void start(Stage stage) throws Exception {
        // 创建一个FXMLLoader对象，用于加载FXML文件
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/edit-window.fxml"));
        // 加载FXML文件并创建一个新的场景
        Scene scene = new Scene(fxmlLoader.load(), 1100, 700);
        // 获取EditWindowController
        EditWindowController controller = fxmlLoader.getController();
        // 设置舞台的图标
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResource("/image/icon.png")).toExternalForm()));
        // 将前面创建的场景设置为舞台的当前场景
        stage.setScene(scene);
        // 显示舞台
        stage.show();
        // 初始化controller
        if (path != null) {
            controller.init(stage, path);
        } else {
            controller.init(stage, getParameters().getRaw().get(0));
        }
    }
}