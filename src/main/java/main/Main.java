package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import imgstate.ControllerMap;

import java.util.Objects;

/**
 * 这是主程序入口，用于启动JavaFX应用程序。
 * 它设置了主窗口的大小、标题、图标，并加载了FXML布局文件。
 */
public class Main extends Application {

    /**
     * 主程序入口点
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * JavaFX应用程序的启动方法
     * @param stage 主舞台
     * @throws Exception 如果加载FXML文件或设置舞台时出现错误
     */
    @Override
    public void start(Stage stage) throws Exception {
        // 加载FXML布局文件
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/javafx/main.fxml"));
        // 创建场景
        Scene scene = new Scene(fxmlLoader.load(), 1100, 700);
        // 在FXMLoader.load调用后再使用addController
        ControllerMap.addController(fxmlLoader.getController());
        // 设置窗口的最小高度
        stage.setMinHeight(400);
        // 设置窗口的最小宽度
        stage.setMinWidth(600);
        // 设置窗口的标题
        stage.setTitle("前端之巅的JAVA课设");
        // 设置窗口的图标
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResource("/image/icon.png")).toExternalForm()));
        // 将场景设置到舞台
        stage.setScene(scene);
        // 显示舞台
        stage.show();
    }
}