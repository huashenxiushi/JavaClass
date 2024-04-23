package top.remake;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import top.remake.controller.DisplayWindowController;

import java.util.Objects;

/**
 * DisplayWindow类是JavaFX应用程序的一个窗口，用于展示图片
 * @author ZeroTwo_CHEN
 */
public class DisplayWindow extends Application {
    /**
     * 数组的长度用于标记启动方式
     * 长度为1则为从主窗口双击启动
     * 长度为2则为从主窗口点击播放键启动
     */
    private static String[] path = null;

    // main方法是Java程序的入口点
    public static void main(String[] args) {
        // 获取图片的路径
        DisplayWindow.path = args;

        // 检查当前线程是否是JavaFX Application Thread
        if (Platform.isFxApplicationThread()) {
            // 创建一个新的Stage
            Stage stage = new Stage();
            DisplayWindow displayWindow = new DisplayWindow();
            try {
                // 启动DisplayWindow
                displayWindow.start(stage);
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
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/display-window.fxml"));
        // 加载FXML文件并创建一个新的场景
        Scene scene = new Scene(fxmlLoader.load(), 1100, 700);
        // 获取DisplayWindowController
        DisplayWindowController controller = fxmlLoader.getController();
        // 设置舞台的图标
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResource("/image/icon.png")).toExternalForm()));
        // 将前面创建的场景设置为舞台的当前场景
        stage.setScene(scene);
        // 根据path的长度决定如何初始化controller
        if (path != null) {
            //从主界面进入展示界面
            if (path.length == 1) {
                stage.show();
                controller.init(stage, path[0]);
            } else {
                //在主界面通过点击播放界面进入 不展示stage
                controller.playByMainWindow(stage, path[0]);
            }
        } else {
            controller.init(stage, getParameters().getRaw().get(0));
        }
    }
}