package top.remake.component;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import top.remake.entity.ImageFile;
import top.remake.utils.FileUtil;

/**
 * @author ZeroTwo_CHEN
 * 这段代码是在JavaFX框架下编写的，主要定义了两个类：ThumbnailPanel和AttributeAlert。  ThumbnailPanel类是一个自定义的面板，用于显示图片的缩略图和名称。它包含以下主要功能：
 * 初始化面板的大小和图片的显示方式。
 * 对图片名称进行处理，如果名称过长则进行剪切。
 * 对面板进行鼠标点击事件的监听，实现图片的选择、取消选择、多选等功能，并更新主界面的提示信息。
 * 对面板进行鼠标进入事件的监听，显示图片的信息。
 * 提供一些方法，如select、removeSelect等，用于改变面板的选中状态。
 * 提供一些getter和setter方法，用于获取和设置面板的属性。
 * AttributeAlert类是一个自定义的弹窗，用于显示图片的属性信息。它包含以下主要功能：
 * 初始化弹窗的样式和标题。
 * 创建一个垂直布局的面板，用于显示图片的属性信息，如名称、类型、大小、尺寸、位置、创建时间、修改时间和访问时间等。
 * 将创建的面板添加到弹窗的内容面板中。
 */
public class AttributeAlert extends Alert {

    private static final VBox KEY = new VBox();

    static {
        KEY.getChildren()
                .addAll(new Label("图片名称：  "),
                        new Label("图片类型：  "),
                        new Label("图片大小：  "),
                        new Label("图片尺寸：  "),
                        new Label("图片位置：  "),
                        new Label("创建时间：  "),
                        new Label("修改时间：  "),
                        new Label("访问时间：  ")
                );
        KEY.setStyle("-fx-spacing: 15px");
    }

    public AttributeAlert(ImageFile file, Image image) {
        super(Alert.AlertType.INFORMATION);
        initStyle(StageStyle.UTILITY);
        setTitle(file.getFileName() + " 属性");
        setGraphic(null);
        setHeaderText(null);

        VBox value = new VBox();
        value.getChildren()
                .addAll(new Label(file.getFileName()),
                        new Label(file.getFileType()),
                        new Label(FileUtil.fileSizeByString(file.getSizeInBytes())),
                        new Label(image.getWidth() + "x" + image.getHeight()),
                        new Label(file.getAbsolutePath()),
                        new Label(file.getCreationTime()),
                        new Label(file.getLastModifiedTime()),
                        new Label(file.getLastAccessTime())
                );
        value.setStyle("-fx-spacing: 15px");

        HBox hBox = new HBox();
        hBox.getChildren().addAll(KEY, value);

        getDialogPane().setContent(hBox);
        initModality(Modality.NONE);
    }
}
