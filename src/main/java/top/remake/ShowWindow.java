package top.remake;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import top.remake.controller.ShowWindowController;

import java.util.Objects;

/**
 * 这是一个显示窗口类，用于打开和显示显示窗口。
 * 它设置了显示窗口的大小、图标，并加载了FXML布局文件。
 * 该类还包含一个main方法，用于启动显示窗口。
 */
public class ShowWindow extends Application {
    /**
     * 数组的长度用于标记启动方式
     * 长度为1则为从主窗口双击启动
     * 长度为2则为从主窗口点击播放键启动
     */
    private static String[] path = null;

    /**
     * 主程序入口点
     * @param args 命令行参数，其中包含图片路径
     */
    public static void main(String[] args) {
        ShowWindow.path = args;
        // 检查当前线程是否为JavaFX应用程序线程
        if (Platform.isFxApplicationThread()) {
            // 创建新的舞台
            Stage stage = new Stage();
            // 创建显示窗口实例
            ShowWindow showWindow = new ShowWindow();
            try {
                // 启动显示窗口
                showWindow.start(stage);
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
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/display-window.fxml"));
        // 创建场景
        Scene scene = new Scene(fxmlLoader.load(), 1100, 700);
        // 获取控制器
        ShowWindowController controller = fxmlLoader.getController();
        // 设置窗口的图标
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResource("/image/icon.png")).toExternalForm()));
        // 将场景设置到舞台
        stage.setScene(scene);
        // 根据路径的长度，决定启动方式
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