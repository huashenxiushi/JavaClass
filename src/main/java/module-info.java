module EIMA {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;
    requires javafx.graphics;
    requires org.controlsfx.controls;
    requires rxcontrols;
    requires thumbnailator;

    opens main to javafx.fxml;
    opens imagestate to javafx.fxml;
    exports main;
}