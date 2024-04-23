package top.remake;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import top.remake.controller.ControllerMap;

import java.util.Objects;

// Main类是JavaFX应用程序的入口点
public class Main extends Application {

    // main方法是Java程序的入口点
    public static void main(String[] args) {
        // 调用launch方法启动JavaFX应用程序
        launch(args);
    }

    // start方法是JavaFX应用程序的主要入口点
    @Override
    public void start(Stage stage) throws Exception {
        // 创建一个FXMLLoader对象，用于加载FXML文件
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/main-window.fxml"));
        // 加载FXML文件并创建一个新的场景
        Scene scene = new Scene(fxmlLoader.load(), 1100, 700);
        // 将控制器添加到ControllerMap中
        ControllerMap.addController(fxmlLoader.getController());
        // 设置舞台的最小高度
        stage.setMinHeight(400);
        // 设置舞台的最小宽度
        stage.setMinWidth(600);
        // 设置舞台的标题
        stage.setTitle("前端之巅的JAVA课设");
        // 设置舞台的图标
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResource("/image/icon.png")).toExternalForm()));
        // 将前面创建的场景设置为舞台的当前场景
        stage.setScene(scene);
        // 显示舞台
        stage.show();
    }
}