module _365.telegram {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires java.desktop;
    requires java.sql;
    requires jdk.compiler;

    opens _365.telegram to javafx.fxml;
    exports _365.telegram;
}