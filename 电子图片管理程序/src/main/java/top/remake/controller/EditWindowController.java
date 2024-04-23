package top.remake.controller;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import top.remake.entity.BrushType;
import top.remake.entity.ImageFile;
import top.remake.utils.DrawShapeUtil;
import top.remake.utils.FileUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author ZeroTwo_CHEN
 */
/


/**这段代码是一个JavaFX应用程序的一部分，它定义了一个名为`EditWindowController`的控制器类，该类用于处理图像编辑窗口的用户交互。

以下是这段代码的主要功能：

1. 初始化画笔类型（线条、铅笔、矩形、圆形）并设置画笔的颜色和宽度。
2. 初始化并加载要编辑的图像。
3. 处理鼠标事件，包括按下、拖动和释放，以在画布上绘制形状。
4. 提供保存功能，将编辑后的图像保存为PNG文件。
5. 提供撤销功能，可以撤销最后一次的绘图操作。
6. 更新图像视图，包括图像的大小和窗口标题。
7. 提供色彩调整功能，包括色相、饱和度、亮度和对比度的调整。
8. 提供图像处理功能，包括灰度处理和颜色反转。

这个控制器类主要用于处理用户在图像编辑窗口中的操作，并对图像进行相应的处理。*/


public class EditWindowController implements Initializable {
    @FXML
    private ImageView imageView;

    @FXML
    private StackPane imagePane;

    /**
     * 当做画布用
     */
    @FXML
    private Group canvasGroup;

    @FXML
    private Button btnLine;

    @FXML
    private Button btnPencil;

    @FXML
    private Button btnRectangle;

    @FXML
    private Button btnCircle;

    @FXML
    private Slider brushWidth;

    @FXML
    private ColorPicker colorPicker;

    /**
     * 传入的图片文件
     */
    private File file;

    /**
     * 编辑的图片
     */
    private Image image;

    private Stage stage;

    private final DrawShapeUtil drawShapeUtil = new DrawShapeUtil();

    /**
     * 图片与窗口的间隔
     * 实际间隔为 MARGIN/2
     */
    private final static int MARGIN = 10;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initBrushType();
        drawShapeUtil.getStrokeWidthProperty().bind(brushWidth.valueProperty());
        drawShapeUtil.getBrushColorProperty().bind(colorPicker.valueProperty());
    }


    public void init(Stage stage, String path) {
        this.file = new File(path);
        this.stage = stage;
        this.image = new Image(file.toPath().toUri().toString());

        updateImageView();
        initColorAdjust();
    }

    private boolean isOutOfBounds;

    /**
     * 当鼠标按下时根据当前的画笔类型生成图形
     * 记录起始的位置
     */
    @FXML
    private void onMousePressed(MouseEvent e) {
        isOutOfBounds = false;
        Shape shape = drawShapeUtil.createShape(e.getX(), e.getY());
        shape.setMouseTransparent(true);
        canvasGroup.getChildren().add(shape);
    }

    /**
     * 鼠标拖动时实时更新
     */
    @FXML
    private void onMouseDragged(MouseEvent e) {
        if (!isOutOfBounds) {
            drawShapeUtil.updateShape(e.getX(), e.getY());
        }
    }

    /**
     * 防止画的图形超出画布
     */
    @FXML
    private void onMouseExited() {
        isOutOfBounds = true;
    }

    @FXML
    private void onMouseEntered() {
        isOutOfBounds = false;
    }

    /**
     * 保存
     */
    @FXML
    private void save() {
        WritableImage writableImage = canvasGroup.snapshot(null, null);
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(writableImage, null);

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("保存图片");
        fileChooser.setInitialFileName("Edit-" + file.getName().substring(0, file.getName().lastIndexOf(".")));
        fileChooser.setInitialDirectory(file.getParentFile());
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PNG", ".png"));

        File file = fileChooser.showSaveDialog(canvasGroup.getScene().getWindow());

        if (file != null) {
            try {
                ImageIO.write(bufferedImage, "PNG", file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void undo() {
        int size = canvasGroup.getChildren().size();
        if (size > 1) {
            canvasGroup.getChildren().remove(size - 1);
        }
    }

    private void updateTile() {
        ImageFile imageFile = new ImageFile(file);
        stage.setTitle(String.format("%s(%s,%.0fx%.0f像素)",
                imageFile.getFileName(),
                FileUtil.fileSizeByString(imageFile.getSizeInBytes()),
                image.getWidth(),
                image.getHeight()
        ));
    }

    private void updateImageView() {
        updateTile();
        imageView.setImage(image);

        imageView.setFitWidth(imagePane.getWidth() - MARGIN);
        imageView.setFitHeight(imagePane.getHeight() - MARGIN);
    }

    /**
     * 实现笔刷类型的互斥
     */
    private void initBrushType() {
        btnPencil.setStyle("-fx-background-radius: 95;-fx-border-color:#b4d1f3;-fx-background-color:#cecece; -fx-border-radius: 95;");
        btnLine.setOnAction(e -> {
            btnCircle.setStyle("-fx-background-radius: 95;-fx-border-color:#b4d1f3;-fx-background-color:white; -fx-border-radius: 95;");
            btnPencil.setStyle("-fx-background-radius: 95;-fx-border-color:#b4d1f3;-fx-background-color:white; -fx-border-radius: 95;");
            btnRectangle.setStyle("-fx-background-radius: 95;-fx-border-color:#b4d1f3;-fx-background-color:white; -fx-border-radius: 95;");
            drawShapeUtil.setBrushType(BrushType.LINE);
            btnLine.setStyle("-fx-background-radius: 95;-fx-border-color:#b4d1f3;-fx-background-color:#cecece; -fx-border-radius: 95;");
        });
        btnCircle.setOnAction(e -> {
            btnLine.setStyle("-fx-background-radius: 95;-fx-background-color:white;-fx-border-color:#b4d1f3; -fx-border-radius: 95;");
            btnPencil.setStyle("-fx-background-radius: 95;-fx-background-color:white; -fx-border-radius: 95;-fx-border-color:#b4d1f3;");
            btnRectangle.setStyle("-fx-background-radius: 95;-fx-background-color:white; -fx-border-radius: 95;-fx-border-color:#b4d1f3;");
            drawShapeUtil.setBrushType(BrushType.ELLIPSE);
            btnCircle.setStyle("-fx-background-radius: 95;-fx-background-color:#cecece; -fx-border-radius: 95;-fx-border-color:#b4d1f3;");
        });
        btnPencil.setOnAction(e -> {
            btnLine.setStyle("-fx-background-radius: 95;-fx-background-color:white; -fx-border-radius: 95;-fx-border-color:#b4d1f3;");
            btnRectangle.setStyle("-fx-background-radius: 95;-fx-background-color:white; -fx-border-radius: 95;-fx-border-color:#b4d1f3;");
            btnCircle.setStyle("-fx-background-radius: 95;-fx-background-color:white; -fx-border-radius: 95;-fx-border-color:#b4d1f3;");
            drawShapeUtil.setBrushType(BrushType.PENCIL);
            btnPencil.setStyle("-fx-background-radius: 95;-fx-background-color:#cecece; -fx-border-radius: 95;-fx-border-color:#b4d1f3;");
        });
        btnRectangle.setOnAction(e -> {
            btnLine.setStyle("-fx-background-radius: 95;-fx-background-color:white; -fx-border-radius: 95;-fx-border-color:#b4d1f3;");
            btnPencil.setStyle("-fx-background-radius: 95;-fx-background-color:white; -fx-border-radius: 95;-fx-border-color:#b4d1f3;");
            btnCircle.setStyle("-fx-background-radius: 95;-fx-background-color:white; -fx-border-radius: 95;-fx-border-color:#b4d1f3;");
            drawShapeUtil.setBrushType(BrushType.RECTANGLE);
            btnRectangle.setStyle("-fx-background-radius: 95;-fx-background-color:#cecece; -fx-border-radius: 95;-fx-border-color:#b4d1f3;");
        });
    }


    /**
     * 色相
     */
    private double hue;
    /**
     * 饱和度
     */
    private double saturation;
    /**
     * 明度
     */
    private double brightness;
    /**
     * 对比度
     */
    private double contrast;

    private final ColorAdjust colorAdjust = new ColorAdjust();


    @FXML
    private Slider hueSlider = new Slider();

    @FXML
    private Slider saturationSlider = new Slider();

    @FXML
    private Slider brightnessSlider = new Slider();

    @FXML
    private Slider contrastSlider = new Slider();


    private void initColorAdjust() {
        hueSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            hue = newValue.doubleValue();
            colorAdjust();
        });
        saturationSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            saturation = newValue.doubleValue();
            colorAdjust();
        });
        brightnessSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            brightness = newValue.doubleValue();
            colorAdjust();
        });
        contrastSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            contrast = newValue.doubleValue();
            colorAdjust();
        });
        imageView.setEffect(colorAdjust);
    }

    @FXML
    private void colorAdjust() {
        colorAdjust.setHue(hue);
        colorAdjust.setSaturation(saturation);
        colorAdjust.setBrightness(brightness);
        colorAdjust.setContrast(contrast);
    }

    @FXML
    private void resetColorAdjust() {
        hueSlider.setValue(0);
        saturationSlider.setValue(0);
        brightnessSlider.setValue(0);
        contrastSlider.setValue(0);
        imageView.setImage(image);
    }

    @FXML
    private void grayscale() {
        pixelProcess(0);
    }

    @FXML
    private void invert() {
        pixelProcess(1);
    }


    /**
     * @param type 0为灰度处理 1为颜色反转
     */
    private void pixelProcess(int type) {
        //读取原图片像素
        PixelReader pixelReader = image.getPixelReader();
        WritableImage writableImage = new WritableImage((int) image.getWidth(), (int) image.getHeight());
        PixelWriter pixelWriter = writableImage.getPixelWriter();

        for (var x = 0; x < image.getWidth(); x++) {
            for (var y = 0; y < image.getHeight(); y++) {
                Color color = pixelReader.getColor(x, y);
                if (type == 0) {
                    color = color.grayscale();
                } else {
                    color = color.invert();
                }
                pixelWriter.setColor(x, y, color);
            }
        }

        imageView.setImage(writableImage);
    }
}
