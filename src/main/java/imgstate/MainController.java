package imgstate;

import com.leewyatt.rxcontrols.utils.StringUtil;
import imgmethod.*;
import imgpreview.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import javafx.util.Pair;
import org.controlsfx.control.BreadCrumbBar;
import org.controlsfx.control.Notifications;
import main.Show;

import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.text.NumberFormat;
import java.util.List;
import java.util.*;


public class MainController implements Initializable {
    @FXML
    private AnchorPane root;

    @FXML
    private AnchorPane top;

    @FXML
    private SplitPane middle;

    @FXML
    private HBox bottom;

    @FXML
    private TreeView<String> fileTreeView;

    @FXML
    private BreadCrumbBar<String> breadCrumbBar;

    @FXML
    private ScrollPane imagePreviewPane;

    private DisplayImage previewFlowPane;

    @FXML
    private Label tipsLabel;

    @FXML
    private ComboBox<String> sortOrderComboBox;

    @FXML
    private TextField searchField;

    @FXML
    private TextField pathField;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initFileTreeView();
        initPreviewFlowPane();
        initAdaptiveLayout();
        initSortPane();
        initSearch();
        initRedoUndo();
        initSizeSlider();
    }

    /**
     * 为搜索框加上监听事件，实现搜索内容的高亮显示
     * 当前为实时搜索，不用按下回车键
     */
    private void initSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {

            List<ThumbnailPanel> thumbnailPanels = previewFlowPane.getThumbnailPanels();

            //为空时显示全部
            if (searchField.getText().isEmpty()) {
                previewFlowPane.getChildren().setAll(thumbnailPanels);
                return;
            }

            //非空时开始过滤匹配
            List<ThumbnailPanel> filtered = thumbnailPanels.stream()
                    .filter(t -> {
                        final boolean[] result = {false};

                        if (t.getImageName().isMatch()) {
                            return result[0] = true;
                        }

                        ArrayList<Pair<String, Boolean>> pairs = StringUtil.parseText(t.getImageFile().getFileName(), newValue, true);
                        pairs.forEach(pair -> {
                            if (pair.getValue()) {
                                result[0] = true;
                            }
                        });

                        return result[0];
                    }).toList();

            previewFlowPane.getChildren().setAll(filtered);
        });
    }


    /**
     * 初始化树形目录
     */
    private void initFileTreeView() {
        fileTreeView.setCellFactory(LazyLoader.forTreeView());

        TreeItem<String> treeItem = new TreeItem<>();

        //读取文件系统的根并将其添加到根节点中
        File root = FileSystemView.getFileSystemView().getRoots()[0];
        File[] allFiles = root.listFiles();
        File[] directorFiles = root.listFiles(File::isDirectory);
        List<File> list = new ArrayList<>(Arrays.asList(allFiles));
        list.removeAll(Arrays.asList(directorFiles));
        for (File file : list) {
            //过滤文件及快捷方式
            if (file.isDirectory() && !file.getName().endsWith("lnk")) {
                treeItem.getChildren().add(new DirectoryTree(file, new DirectoryLoader(file)));
            }
        }

        fileTreeView.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue instanceof DirectoryTree) {
                        breadCrumbBar.selectedCrumbProperty().set(newValue);
                        previewFlowPane.update(((DirectoryTree) newValue).getDirectory());
                        pathField.setText(((DirectoryTree) newValue).getDirectory().getAbsolutePath());
                    }
                });
        fileTreeView.setRoot(treeItem);
    }

    @FXML
    private Pane pane;

    private Rectangle rectangle;

    /**
     * 初始化缩略图面板
     */
    private void initPreviewFlowPane() {
        previewFlowPane = new DisplayImage();
        rectangle = new Rectangle();
        rectangle.setFill(Color.rgb(177, 178, 170, 0.6));
        rectangle.setVisible(false);
        pane.getChildren().addAll(previewFlowPane, rectangle);
        addHandler();

        previewFlowPane.heightProperty()
                .addListener((observable, oldValue, newValue) -> pane.setMinHeight(previewFlowPane.getHeight()));

        imagePreviewPane.viewportBoundsProperty()
                .addListener((observable, oldValue, newValue) -> {
                    previewFlowPane.setPrefWidth(newValue.getWidth());
                    if (previewFlowPane.getHeight() < imagePreviewPane.getHeight()) {
                        previewFlowPane.setPrefHeight(imagePreviewPane.getHeight());
                    }
                });

        rectangle.setVisible(false);
    }

    /**
     * 树形目录导航栏
     */
    @FXML
    private void onCrumbAction(BreadCrumbBar.BreadCrumbActionEvent<String> event) {
        TreeItem<String> newValue = event.getSelectedCrumb();
        if (newValue instanceof DirectoryTree) {
            fileTreeView.getSelectionModel().select(newValue);
            previewFlowPane.update(((DirectoryTree) newValue).getDirectory());
        }
    }

    /**
     * 设置自适应布局
     */
    private void initAdaptiveLayout() {
        middle.setPrefHeight(root.getHeight() - top.getPrefHeight() - bottom.getPrefHeight());
        root.heightProperty().addListener((observable, oldValue, newValue) -> {
            double temp = top.getPrefHeight() + bottom.getPrefHeight();
            middle.setPrefHeight(newValue.doubleValue() - temp);
        });
    }

    /**
     * 初始化工具栏
     */
    private void initSortPane() {
        //排序选择框
        sortOrderComboBox.getItems()
                .addAll(SortOrder.ASC_SORT_BY_NAME, SortOrder.DESC_SORT_BY_NAME,
                        SortOrder.ASC_SORT_BY_TIME, SortOrder.DESC_SORT_BY_TIME,
                        SortOrder.ASC_SORT_BY_SIZE, SortOrder.DESC_SORT_BY_SIZE);
        sortOrderComboBox.getSelectionModel().select(0);
        sortOrderComboBox.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> previewFlowPane.update());
    }

    @FXML
    private void refresh() {
        previewFlowPane.update();
    }

    @FXML
    private void selectAll() {
        previewFlowPane.selectAll();
        updateTipsLabelText();
    }

    /**
     * 更新左下角提示栏
     */
    private void updateTipsLabelText() {
        updateTipsLabelText(previewFlowPane.getTotalCount(), previewFlowPane.getTotalSize(),
                previewFlowPane.getSelectedCount(), previewFlowPane.getSelectedSize());
    }

    /**
     * 更新左下角提示栏
     *
     * @param totalCount    当前目录的图片总数
     * @param size          总大小
     * @param selectedCount 被选中的图片的数量
     * @param selectedSize  被选中的图片的大小
     */
    public void updateTipsLabelText(int totalCount, String size, int selectedCount, String selectedSize) {
        tipsLabel.setText(String.format("共 %d 张照片(%s) - 选中 %d 张照片(%s)",
                totalCount,
                size,
                selectedCount,
                selectedSize));
    }

    /**
     * 返回comboBox中的值
     */
    public String getSortOrder() {
        return sortOrderComboBox.getSelectionModel().getSelectedItem();
    }


    /**
     * 鼠标按下时的坐标
     */
    private double x, y;
    private double width;
    private double height;
    private final Menu menu = new Menu();

    private void addHandler() {
        //对FlowPane添加监听器记录鼠标按下时的坐标，存放于x，y中。
        previewFlowPane.setOnMousePressed(event -> {
            x = event.getX();
            y = event.getY();
            menu.close();

            //处理单击空白部分时取消已选择的图片
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1 && !event.isControlDown()) {
                if (previewFlowPane.equals(event.getPickResult().getIntersectedNode())) {
                    previewFlowPane.clearSelect();
                    previewFlowPane.setShiftSign(false);
                }
                updateTipsLabelText();
            }
            //右键菜单
            if (event.getButton() == MouseButton.SECONDARY) {
                menu.show(event.getScreenX(), event.getScreenY());
            }

        });

        //通过对FlowPane添加监听器记录并计算矩形（多选框）的坐标，宽高；
        //根据得到的坐标和宽高绘制矩形并动态选择图片
        previewFlowPane.setOnMouseDragged(event -> {
            double x2 = event.getX();
            double y2 = event.getY();
            double endX = Math.min(x, x2);
            double endY = Math.min(y, y2);
            width = Math.abs(x - x2);
            height = Math.abs(y - y2);
            rectangle.setX(endX);
            rectangle.setY(endY);
            rectangle.setWidth(width);
            rectangle.setHeight(height);
            rectangle.setVisible(true);
            //选择矩形所经过的图片
            if (width >= 10 && height >= 10) {
                selectImg();
            }


            //拖拽鼠标向上超出边界时，将ScrollPane上滑
            if (event.getSceneY() < 100) {
                imagePreviewPane.setVvalue(imagePreviewPane.getVvalue() +
                        (event.getSceneY() - 100) / previewFlowPane.getHeight() / 10);
            }
            //拖拽鼠标向下超出边界时。将ScrollPane下滑
            if (event.getSceneY() > 80 + imagePreviewPane.getHeight()) {
                imagePreviewPane.setVvalue(imagePreviewPane.getVvalue() +
                        (event.getSceneY() - imagePreviewPane.getHeight() - 80) / previewFlowPane.getHeight() / 10);
            }

        });
        previewFlowPane.setOnMouseReleased(event -> {
            //选中图片
            rectangle.setVisible(false);
        });
    }

    /**
     * 根据矩形的数据选择图片
     */
    private void selectImg() {
        previewFlowPane.clearSelect();
        for (ThumbnailPanel pane : previewFlowPane.getThumbnailPanels()) {
            boolean isIntersects = rectangle.intersects(pane.getBoundsInParent());
            if (isIntersects) {
                previewFlowPane.addSelect(pane);
            }
        }
        updateTipsLabelText();
    }

    /**
     * 刷新缩略图面板
     */
    public void updateFlowPane() {
        previewFlowPane.update();
    }

    /**
     * 删除图片
     */
    @FXML
    private void deleteImage() {
        List<ThumbnailPanel> images = previewFlowPane.getNewChoices();
        if (images.size() == 0) {
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initOwner(null);
        alert.setGraphic(null);
        alert.setHeaderText(null);
        alert.getDialogPane().setPrefSize(400, 150);
        alert.setContentText("\n确定删除这 " + images.size() + " 张图片吗？\n");


        String css = getClass().getResource("/css/alert.css").toExternalForm();
        alert.getDialogPane().getStylesheets().add(css);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Platform.runLater(() -> {
                for (ThumbnailPanel image : images) {
                    FileUtil.delete(image.getImageFile().getFile());
                }
                updateFlowPane();
            });
        }


    }

    /**
     * 播放幻灯片
     */
    @FXML
    private void play() {
        int selectedCount = previewFlowPane.getSelectedCount();
        //如果未选择图片或选择了多张图片，则从第一张开始播放,否则从选择的图片开始播放
        if (previewFlowPane.getThumbnailPanels().size() == 0) {

        } else if (selectedCount == 1) {
            ThumbnailPanel thumbnailPanel = previewFlowPane.getNewChoices().get(0);
            String[] args = {thumbnailPanel.getImageFile().getAbsolutePath(), ""};
            Platform.runLater(() -> Show.main(args));
        } else {
            String[] args = {previewFlowPane.getThumbnailPanels().get(0).getImageFile().getAbsolutePath(), ""};
            Platform.runLater(() -> Show.main(args));
        }
    }

    public TextField getSearchField() {
        return searchField;
    }

    public List<ThumbnailPanel> getCopyImg() {
        return copyImg;
    }

    private List<ThumbnailPanel> copyImg = new ArrayList<>();

    /**
     * 复制图片
     */
    @FXML
    private void copyImage() {
        copyImg.clear();
        copyImg.addAll(previewFlowPane.getNewChoices());

    }

    @FXML
    private void pasteImage() {
        List<ThumbnailPanel> images = getCopyImg();
        if (images.size() == 0) {
            return;
        }
        File resource, target;
        File directory = previewFlowPane.getDirectory();
        for (ThumbnailPanel image : images) {
            resource = image.getImageFile().getFile();
            String out = directory.getAbsolutePath() + "\\" + resource.getName();
            target = new File(out);
            try {
                //目标文件已存在时，在文件名后加“-副本后缀”
                while (target.exists()) {
                    String suffix = out.substring(out.lastIndexOf("."));
                    out = out.replace(suffix, "-副本") + suffix;
                    target = new File(out);
                }
                Files.copy(resource.toPath(), target.toPath());
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        updateFlowPane();
    }

    @FXML
    private void renameImage() {
        //判断是否有图片被选中
        if (previewFlowPane.getNewChoices().isEmpty()) {

            return;
        }
        //确认已有选中图片
        if (previewFlowPane.getNewChoices().size() == 1) {
            Dialog<String> dialog = new Dialog<>();
            dialog.setTitle("重命名");
            Label label = new Label(" 新图片名称:");
            label.setStyle("-fx-spacing: 10px;-fx-font-family:'Microsoft YaHei ';-fx-font-size: 18px;");
            TextField textField = new TextField();
            textField.setMinSize(200, 30); // 设置 TextField 的最小大小
            GridPane gridPane = new GridPane();
            gridPane.add(label, 0, 0);
            gridPane.add(textField, 1, 0);
            dialog.getDialogPane().setContent(gridPane);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == ButtonType.OK) {
                    return textField.getText();
                }
                return null;
            });
            Optional<String> string1 = dialog.showAndWait();
            string1.ifPresent(e -> renameImage(string1.get(), -1, -1));
        } else {
            RenameImage renameImage = new RenameImage();
            Optional<RenameImages> data = renameImage.showAndWait();
            data.ifPresent(e -> {
                RenameImages data1 = data.get();
                renameImage(data1.getName(), data1.getStartNum(), data1.getDigit());
                refresh();
            });
        }
    }

    /**
     * 根据用户所给信息对选中文件进行批量重命名
     */
    private void renameImage(String name, int startNum, int digit) {
        File file;
        List<ThumbnailPanel> images = previewFlowPane.getNewChoices();
        List<String> pasts = new ArrayList<>();
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMinimumIntegerDigits(digit);
        nf.setGroupingUsed(false);
        if (digit == -1 && startNum == -1) {
            StringBuilder newName = new StringBuilder();
            ThumbnailPanel image = images.get(0);
            file = image.getImageFile().getFile();
            newName.append(file.getParentFile().getAbsolutePath()).append("\\");
            newName.append(name)
                    .append(".")
                    .append(image.getImageFile().getFileType().toLowerCase(Locale.ROOT));
            File dest = new File(newName.toString());
            if (dest.exists()) {
                Dialog<Boolean> dialog = new Dialog<>();
                dialog.setTitle("图片已存在");
                Label label = new Label("请选择:");
                GridPane gridPane = new GridPane();
                gridPane.add(label, 0, 0);
                gridPane.setPadding(new Insets(10, 10, 10, 10));
                gridPane.setHgap(10);
                dialog.getDialogPane().setContent(gridPane);
                ButtonType buttonType = new ButtonType("替换");
                dialog.getDialogPane().getButtonTypes().addAll(buttonType, ButtonType.CANCEL);
                dialog.setResultConverter(e -> e == buttonType);
                Optional<Boolean> result = dialog.showAndWait();
                File finalFile = file;
                result.ifPresent(e -> {
                    if (result.get()) {
                        dest.delete();
                        finalFile.renameTo(dest);
                        image.getImageFile().setFile(dest);
                        refresh();
                    }
                });

            } else {
                file.renameTo(dest);
                image.getImageFile().setFile(dest);
                refresh();
                return;
            }
        }

        for (ThumbnailPanel image : images) {
            pasts.add(image.getImageFile().getFile().getParentFile().getAbsolutePath() + "\\" + image.getImageFile().getFileName());
            String tempName = image.getImageFile().getFile().getParentFile().getAbsolutePath() + "\\" + GetTimeUtil.randomName();
            String type = image.getImageFile().getFileType();
            File newFile = new File(tempName + "." + type);
            image.getImageFile().getFile().renameTo(newFile);
            image.getImageFile().setFile(newFile);


        }

        for (ThumbnailPanel image : images) {
            StringBuilder newName = new StringBuilder();

            //生成新图片的绝对路径 eg：C:\\User\Code\abc001.png
            file = image.getImageFile().getFile();
            newName.append(file.getParentFile().getAbsolutePath()).append("\\");

            //生成图片新名称 eg：abc001.png
            newName.append(name)
                    .append(nf.format(startNum))
                    .append(".")
                    .append(image.getImageFile().getFileType().toLowerCase(Locale.ROOT));

            File dest = new File(newName.toString());
            int choice = renameCheck(dest);
            if (choice == 1) {
                //替换已存在图片
                dest.delete();
                file.renameTo(dest);
                image.getImageFile().setFile(dest);
            }

            if (choice == -1) {
                //跳过该图片的重命名
                dest = new File(pasts.get(images.indexOf(image)));
                file.renameTo(dest);
                image.getImageFile().setFile(dest);
            }

            if (choice == 0) {
                //取消所有的重命名操作
                for (ThumbnailPanel image1 : images) {
                    file = image1.getImageFile().getFile();
                    dest = new File(pasts.get(images.indexOf(image1)));
                    file.renameTo(dest);
                    image1.getImageFile().setFile(dest);
                }
                break;
            }
            if (choice == 2) {
                file.renameTo(dest);
                image.getImageFile().setFile(dest);
            }
            startNum++;
        }

    }

    /**
     * 判断该文件新名称在该路径下是否已经存在
     * 如果已存在
     */
    private int renameCheck(File file) {
        if (file.exists()) {
            Dialog<Integer> dialog = new Dialog<>();
            GridPane gridPane = new GridPane();
            dialog.setTitle(file.getName() + "已存在！");
            Label label1 = new Label("替换: 替换已存在的图片");
            Label label2 = new Label("跳过: 跳过该图片重命名");
            Label label3 = new Label("取消: 取消重命名图片  ");
            label1.setAlignment(Pos.CENTER);
            label2.setAlignment(Pos.CENTER);
            label3.setAlignment(Pos.CENTER);
            gridPane.add(label1, 0, 0);
            gridPane.add(label2, 0, 1);
            gridPane.add(label3, 0, 2);
            gridPane.setPadding(new Insets(10, 10, 10, 10));
            gridPane.setVgap(20);

            ButtonType buttonType1 = new ButtonType("替换");
            ButtonType buttonType2 = new ButtonType("跳过");
            dialog.getDialogPane().setContent(gridPane);
            dialog.getDialogPane().getButtonTypes().addAll(buttonType1, buttonType2, ButtonType.CANCEL);
            dialog.setResultConverter(e -> {
                if (e == buttonType1) {
                    //替换按钮
                    return 1;
                }
                if (e == buttonType2) {
                    //跳过按钮
                    return -1;
                }
                //取消按钮
                return 0;
            });
            Optional<Integer> data = dialog.showAndWait();
            if (data.isPresent()) {
                return data.get();
            }
        }
        return 2;
    }

    private void showImageAttribute() {
        ImageFile imageFile = previewFlowPane.getNewChoices().get(0).getImageFile();
        AttributeAlert alert = new AttributeAlert(imageFile, new Image(imageFile.getURL()));
        alert.show();
    }

    class Menu extends ContextMenu {
        MenuItem refresh = new MenuItem("✏   刷新");

        MenuItem delete = new MenuItem("✏   删除");

        MenuItem copy = new MenuItem("✏   复制");

        MenuItem paste = new MenuItem("✏   粘贴");

        MenuItem rename = new MenuItem("✏   重命名");

        MenuItem attribute = new MenuItem("✏   属性");

        //MenuItem compress = new MenuItem("压缩");

        Menu() {
            // 为菜单项设置样式
            refresh.setStyle("-fx-font-size: 18px; "); // 设置文本颜色为黑色，字体加粗
            delete.setStyle("-fx-font-size: 18px; "); // 设置文本颜色为红色，字体加粗
            copy.setStyle("-fx-font-size: 18px; "); // 设置文本颜色为蓝色，字体加粗
            paste.setStyle("-fx-font-size: 18px; "); // 设置文本颜色为绿色，字体加粗
            rename.setStyle("-fx-font-size: 18px; "); // 设置文本颜色为紫色，字体加粗
            attribute.setStyle("-fx-font-size: 18px; "); // 设置文本颜色为橙色，字体加粗
           // compress.setStyle("-fx-text-fill: #663300; -fx-font-weight: bold;"); // 设置文本颜色为棕色，字体加粗
            refresh.setOnAction(e -> refresh());
            delete.setOnAction(e -> deleteImage());
            copy.setOnAction(e -> copyImage());
            paste.setOnAction(e -> pasteImage());
            rename.setOnAction(e -> renameImage());
            attribute.setOnAction(e -> showImageAttribute());
           // compress.setOnAction(e -> compressImage());
            getItems().addAll(refresh, delete, copy, paste, rename, attribute);
        }

        void show(double x, double y) {
            if (previewFlowPane.getSelectedCount() == 0) {
                delete.setDisable(true);
                copy.setDisable(true);
                rename.setDisable(true);
                //compress.setDisable(true);
            } else {
                delete.setDisable(false);
                copy.setDisable(false);
                rename.setDisable(false);
                //compress.setDisable(false);
            }

            paste.setDisable(copyImg.size() == 0);

            attribute.setDisable(previewFlowPane.getSelectedCount() != 1);

            show(pane, x, y);
        }

        void close() {
            hide();
        }
    }

    @FXML
    private void selectParent() {
        TreeItem<String> selectedItem = fileTreeView.getSelectionModel().getSelectedItem();
        if (selectedItem instanceof DirectoryTree) {
            fileTreeView.getSelectionModel().select(selectedItem.getParent());
        }
    }

    /**
     * 目录树的redo/undo功能
     */
    private final Stack<DirectoryTree> forwardStack = new Stack<>();

    private final Stack<DirectoryTree> backwardStack = new Stack<>();

    private void initRedoUndo() {
        fileTreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (!(newValue instanceof DirectoryTree)) {
                return;
            }
            if (!forwardStack.isEmpty()) {
                if (forwardStack.peek().equals(newValue)) {
                    return;
                }
            }
            if (!backwardStack.isEmpty()) {
                if (backwardStack.peek().equals(newValue)) {
                    return;
                }
            }
            backwardStack.push((DirectoryTree) newValue);
            forwardStack.clear();
        });
    }


    @FXML
    private void forward() {
        if (!forwardStack.isEmpty()) {
            DirectoryTree pop = forwardStack.pop();
            backwardStack.push(pop);
            if (!forwardStack.isEmpty()) {
                fileTreeView.getSelectionModel().select(forwardStack.peek());
            }
        }
    }

    @FXML
    private void backward() {
        if (!backwardStack.isEmpty()) {
            DirectoryTree pop = backwardStack.pop();
            forwardStack.push(pop);
            if (!backwardStack.isEmpty()) {
                fileTreeView.getSelectionModel().select(backwardStack.peek());
            }
        }
    }

    @FXML
    private Slider thumbnailSizeSlider;

    private void initSizeSlider() {
        thumbnailSizeSlider.valueProperty().addListener(((observable, oldValue, newValue) -> {
            int i = newValue.intValue() % 20;
            if (i == 0) {
                previewFlowPane.update();
            } else {
                thumbnailSizeSlider.setValue(newValue.intValue() + 20 - i);
            }
        }));
    }

    public double getThumbnailSize() {
        return thumbnailSizeSlider.getValue();
    }

    /**
     * 导航到指定路径
     */
    @FXML
    private void goPath() {
        String path = pathField.getText();
        if (StringUtil.isEmpty(path)) {
            return;
        }
        File file = new File(path);
        previewFlowPane.update(file);
    }

    /**
     * 压缩图片
     */
    @FXML
    private void compressImage() {
        if (previewFlowPane.getSelectedCount() == 0) {
            return;
        }

        Dialog<Double> dialog = new Dialog<>();
        dialog.setTitle("提示");
        dialog.setHeaderText("请选择压缩后的质量");

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.getStylesheets()
                .add(Objects.requireNonNull(getClass().getResource("/css/base.css")).toExternalForm());

        Slider slider = new Slider(0.0, 1.0, 0.7);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(0.25);
        slider.setMinorTickCount(5);


        grid.add(new Label("压缩质量："), 0, 0);
        grid.add(slider, 1, 0);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                return slider.getValue();
            }
            return null;
        });

        dialog.showAndWait().ifPresent(result -> {
            List<ThumbnailPanel> newChoices = previewFlowPane.getNewChoices();
            Platform.runLater(() -> {
                for (ThumbnailPanel t : newChoices) {
                    try {
                        FileUtil.compressImage(t.getImageFile(), result);
                    } catch (IOException e) {
                        Notifications.create()
                                .text("图片:" + t.getImageFile().getFileName() + "压缩失败！")
                                .hideAfter(Duration.seconds(2))
                                .position(Pos.TOP_CENTER)
                                .owner(root)
                                .show();
                        throw new RuntimeException(e);
                    }
                }
                previewFlowPane.update();
            });
        });
    }

    /**
     * 关于界面
     */
    @FXML
    private void about() {
        try {
            Desktop.getDesktop().browse(new URI("https://scau.edu.cn"));
        } catch (IOException | URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
    }
}
