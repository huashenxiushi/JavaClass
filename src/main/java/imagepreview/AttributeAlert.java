package imagepreview;

import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import imagemethod.ImageFile;
import imagemethod.FileUtil;


public class AttributeAlert extends Alert {

    // 创建一个VBox对象，用于存放属性的键
    private static final VBox KEY = new VBox();

    // 在静态代码块中，将属性的键添加到VBox对象中，并设置间距
    static {
        KEY.getChildren()
                .addAll(new Label("\n图片名称：  "),
                        new Label("图片类型：  "),
                        new Label("图片大小：  "),
                        new Label("图片位置：  ")
                );
        KEY.setStyle("-fx-spacing: 10px;-fx-font-family:'Microsoft YaHei ';-fx-font-size: 18px;");
    }

    /**
     * 构造方法，用于创建一个属性警告对象。
     * @param file 图像文件对象
     * @param image 图像对象
     */
    public AttributeAlert(ImageFile file, Image image) {
        // 调用父类的构造方法，设置警告类型为信息
        super(Alert.AlertType.INFORMATION);
        // 设置窗口样式为实用工具
        initStyle(StageStyle.DECORATED);

        // 设置窗口标题为图像文件的名称和"属性"
        setTitle(file.getFileName() + " 属性");
        // 设置图形为null
        setGraphic(null);
        // 设置头部文本为null

        setHeaderText(null);

        // 创建一个VBox对象，用于存放属性的值
        VBox value = new VBox();
        value.getChildren()
                .addAll(new Label("\n"+file.getFileName()),
                        new Label(file.getFileType()),
                        new Label(FileUtil.fileSizeByString(file.getSizeInBytes())),
                        new Label(file.getAbsolutePath())
                );
        // 设置VBox对象的间距
        value.setStyle("-fx-spacing: 10px;-fx-font-family:'Microsoft YaHei ';-fx-font-size: 18px;");

        // 创建一个HBox对象，用于存放属性的键和值
        HBox hBox = new HBox();
        hBox.getChildren().addAll(KEY, value);

        // 将HBox对象设置为对话框面板的内容
        getDialogPane().setContent(hBox);
        // 设置警告窗口的背景颜色和边框
        getDialogPane().setStyle("-fx-background-color: #f0f0f0; ");
        // 设置模态性为无


        initModality(Modality.NONE);
    }
}