package imagemethod;


import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/**
 * 重命名窗口
 */

public class RenameImage extends Dialog<RenameImages> {
    private static final Label LABEL_1 = new Label("   名称前缀:");
    private static final Label LABEL_2 = new Label("   起始编号:");
    private static final Label LABEL_3 = new Label("   编号位数:");
    private final TextField textField = new TextField();
    private final Spinner<Integer> startNum = new Spinner<>(1, 10000, 1);
    private final Spinner<Integer> digit = new Spinner<>(1, 5, 1);

    public RenameImage() {

        startNum.setEditable(true);
        startNum.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);
        digit.setEditable(true);
        digit.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);
        GridPane gridPane = new GridPane();

        gridPane.setPrefSize(400, 200);
        textField.setMinSize(200, 30); // 设置 TextField 的最小大小

        startNum.setPrefSize(200, 30); // 设置 Spinner 的首选大小
        digit.setPrefSize(200, 30); // 设置 Spinner 的首选大小
        LABEL_1.setPrefSize(100, 30); // 设置 Label 的首选大小
        LABEL_1.setStyle("-fx-spacing: 10px;-fx-font-family:'Microsoft YaHei ';-fx-font-size: 18px;");
        LABEL_2.setPrefSize(100, 30); // 设置 Label 的首选大小
        LABEL_2.setStyle("-fx-spacing: 10px;-fx-font-family:'Microsoft YaHei ';-fx-font-size: 18px;");
        LABEL_3.setPrefSize(100, 30); // 设置 Label 的首选大小
        LABEL_3.setStyle("-fx-spacing: 10px;-fx-font-family:'Microsoft YaHei ';-fx-font-size: 18px;");
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setVgap(40);
        gridPane.setHgap(30);
        gridPane.add(LABEL_1, 0, 0);
        gridPane.add(LABEL_2, 0, 1);
        gridPane.add(LABEL_3, 0, 2);
        gridPane.add(textField, 1, 0);
        gridPane.add(startNum, 1, 1);
        gridPane.add(digit, 1, 2);
        this.setGraphic(null);
        this.setTitle("多张图片重命名");
        this.getDialogPane().setContent(gridPane);
        this.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return new RenameImages(textField.getText(), startNum.getValue(), digit.getValue());
            }
            return null;
        });
    }

}
